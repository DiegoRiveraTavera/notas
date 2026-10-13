package com.example.notas

internal data class Nota(
    val id: String,
    val titulo: String,
    val descripcion: String,
    val creadaEn: String
)

internal object NotaValidator {
    fun validate(titulo: String, descripcion: String): String? {
        val normalizedTitle = titulo.trim()
        return when {
            normalizedTitle.length !in 3..80 -> "El título debe tener entre 3 y 80 caracteres."
            descripcion.length > 300 -> "La descripción no puede superar 300 caracteres."
            else -> null
        }
    }
}
