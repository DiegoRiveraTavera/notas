package com.example.notas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Construye la interfaz con Jetpack Compose.
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AccesoSeguro()
                }
            }
        }
    }
}

@Composable
fun AccesoSeguro() {

    // Estados de los campos y mensajes de la interfaz.
    var usuario by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var mostrarContrasena by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf("") }
    var accesoCorrecto by remember { mutableStateOf(false) }

    // Longitudes máximas permitidas.
    val maxUsuario = 30
    val maxContrasena = 64

    // Usuarios ficticios para probar el inicio de sesión.
    // No son credenciales adecuadas para una aplicación real.
    val usuariosValidos = mapOf(
        "alumno" to "Seguro123",
        "profesor" to "Clase2026",
        "invitado" to "Invitado123"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "AccesoSeguro",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Inicia sesión para continuar",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Campo de usuario. Rechaza caracteres que excedan el límite.
        OutlinedTextField(
            value = usuario,
            onValueChange = { nuevoUsuario ->
                if (nuevoUsuario.length <= maxUsuario) {
                    usuario = nuevoUsuario
                    mensaje = ""
                    accesoCorrecto = false
                } else {
                    mensaje =
                        "El usuario no puede superar $maxUsuario caracteres."
                    accesoCorrecto = false
                }
            },
            label = { Text("Usuario") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text
            ),
            supportingText = {
                Text("${usuario.length}/$maxUsuario caracteres")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Campo de contraseña oculta por defecto.
        OutlinedTextField(
            value = contrasena,
            onValueChange = { nuevaContrasena ->
                if (nuevaContrasena.length <= maxContrasena) {
                    contrasena = nuevaContrasena
                    mensaje = ""
                    accesoCorrecto = false
                } else {
                    mensaje =
                        "La contraseña no puede superar $maxContrasena caracteres."
                    accesoCorrecto = false
                }
            },
            label = { Text("Contraseña") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            visualTransformation = if (mostrarContrasena) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                TextButton(
                    onClick = {
                        mostrarContrasena = !mostrarContrasena
                    }
                ) {
                    Text(
                        if (mostrarContrasena) "Ocultar" else "Mostrar"
                    )
                }
            },
            supportingText = {
                Text("${contrasena.length}/$maxContrasena caracteres")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Valida las entradas y simula el inicio de sesión.
        Button(
            onClick = {
                val usuarioLimpio = usuario.trim()

                mensaje = when {
                    // Rechaza campos vacíos y usuarios con solo espacios.
                    usuarioLimpio.isEmpty() || contrasena.isEmpty() ->
                        "Completa ambos campos."

                    // La contraseña debe tener al menos ocho caracteres.
                    contrasena.length < 8 ->
                        "La contraseña debe tener al menos 8 caracteres."

                    // Comprueba si coinciden el usuario y la contraseña.
                    usuariosValidos[usuarioLimpio] == contrasena ->
                        "¡Acceso simulado correcto!"

                    // No especifica si falló el usuario o la contraseña.
                    else ->
                        "No se pudo iniciar sesión. Verifica los datos."
                }

                accesoCorrecto =
                    mensaje == "¡Acceso simulado correcto!"

                // Por seguridad, no se registran credenciales en Logcat.
                // No usar Log.d, Log.i, Log.e ni println con estos datos.
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Iniciar sesión")
        }

        // Muestra el resultado de la validación.
        if (mensaje.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = mensaje,
                color = if (accesoCorrecto) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                },
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}