package com.example.notas

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

internal class SupabaseNotasRepository(
    private val projectUrl: String,
    private val anonKey: String
) {
    val isConfigured: Boolean
        get() = projectUrl.isNotBlank() && anonKey.isNotBlank()

    suspend fun getNotas(): List<Nota> = withContext(Dispatchers.IO) {
        val response = request(
            method = "GET",
            path = "/rest/v1/notas?select=id,titulo,descripcion,creada_en&order=creada_en.desc"
        )
        try {
            val jsonArray = JSONArray(response)
            List(jsonArray.length()) { index ->
                val item = jsonArray.getJSONObject(index)
                Nota(
                    id = item.getString("id"),
                    titulo = item.getString("titulo"),
                    descripcion = item.optString("descripcion"),
                    creadaEn = item.optString("creada_en")
                )
            }
        } catch (exception: JSONException) {
            throw IOException("Supabase devolvió una respuesta de notas no válida.", exception)
        }
    }

    suspend fun crearNota(titulo: String, descripcion: String) {
        withContext(Dispatchers.IO) {
            val payload = JSONObject()
                .put("titulo", titulo.trim())
                .put("descripcion", descripcion.trim())
                .toString()
            request(
                method = "POST",
                path = "/rest/v1/notas",
                body = payload
            )
        }
    }

    private fun request(method: String, path: String, body: String? = null): String {
        val baseUrl = projectUrl.trim().trimEnd('/')
        val connection = (URL("$baseUrl$path").openConnection() as HttpURLConnection).apply {
            requestMethod = method
            connectTimeout = 15_000
            readTimeout = 15_000
            setRequestProperty("apikey", anonKey)
            setRequestProperty("Authorization", "Bearer $anonKey")
            setRequestProperty("Accept", "application/json")
            if (body != null) {
                doOutput = true
                setRequestProperty("Content-Type", "application/json; charset=utf-8")
                setRequestProperty("Prefer", "return=minimal")
            }
        }

        try {
            if (body != null) {
                connection.outputStream.bufferedWriter(Charsets.UTF_8).use { it.write(body) }
            }

            val statusCode = connection.responseCode
            val responseStream = if (statusCode in 200..299) {
                connection.inputStream
            } else {
                connection.errorStream
            }
            val responseBody = responseStream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }.orEmpty()
            if (statusCode !in 200..299) {
                throw IOException("Error de Supabase ($statusCode): ${responseBody.ifBlank { "sin detalles" }}")
            }
            responseBody
        } finally {
            connection.disconnect()
        }
    }
}
