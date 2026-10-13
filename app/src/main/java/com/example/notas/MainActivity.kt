package com.example.notas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.notas.ui.theme.NotasTheme
import kotlinx.coroutines.launch
import java.io.IOException

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NotasTheme {
                NotasScreen(
                    repository = remember {
                        SupabaseNotasRepository(
                            projectUrl = BuildConfig.SUPABASE_URL,
                            anonKey = BuildConfig.SUPABASE_ANON_KEY
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun NotasScreen(repository: SupabaseNotasRepository) {
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var notas by remember { mutableStateOf<List<Nota>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    var isError by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    suspend fun cargarNotas() {
        if (!repository.isConfigured) {
            message = "Configura supabase.url y supabase.anonKey en local.properties."
            isError = true
            return
        }
        isLoading = true
        try {
            notas = repository.getNotas()
            message = null
            isError = false
        } catch (exception: IOException) {
            message = exception.message ?: "No se pudieron consultar las notas."
            isError = true
        } finally {
            isLoading = false
        }
    }

    LaunchedEffect(repository) {
        cargarNotas()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Notas en la nube",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Guarda tus ideas y consúltalas desde cualquier lugar.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Nueva nota", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                    OutlinedTextField(
                        value = titulo,
                        onValueChange = { titulo = it.take(80) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Título") },
                        supportingText = { Text("${titulo.length}/80 (mínimo 3)") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = descripcion,
                        onValueChange = { descripcion = it.take(300) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Descripción") },
                        supportingText = { Text("${descripcion.length}/300") },
                        minLines = 3
                    )

                    val validationError = NotaValidator.validate(titulo, descripcion)
                    Button(
                        onClick = {
                            scope.launch {
                                isSaving = true
                                try {
                                    repository.crearNota(titulo, descripcion)
                                    titulo = ""
                                    descripcion = ""
                                    message = "Nota guardada correctamente."
                                    isError = false
                                    cargarNotas()
                                } catch (exception: IOException) {
                                    message = exception.message ?: "No se pudo guardar la nota."
                                    isError = true
                                } finally {
                                    isSaving = false
                                }
                            }
                        },
                        enabled = validationError == null && !isSaving && repository.isConfigured,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.height(18.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(Modifier.width(10.dp))
                            Text("Enviando…")
                        } else {
                            Text("Guardar nota")
                        }
                    }
                    if (validationError != null) {
                        Text(
                            text = validationError,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Notas guardadas", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                TextButton(
                    onClick = { scope.launch { cargarNotas() } },
                    enabled = !isLoading && repository.isConfigured
                ) {
                    Text("Actualizar")
                }
            }

            if (message != null) {
                Text(
                    text = message.orEmpty(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            }

            when {
                isLoading && notas.isEmpty() -> CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                notas.isEmpty() && !isError -> Text(
                    text = "Todavía no hay notas. ¡Crea la primera!",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                else -> Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    notas.forEach { nota -> NotaCard(nota) }
                }
            }
        }
    }
}

@Composable
private fun NotaCard(nota: Nota) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(nota.titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            if (nota.descripcion.isNotBlank()) {
                Text(nota.descripcion, style = MaterialTheme.typography.bodyMedium)
            }
            HorizontalDivider()
            Text(
                text = "Creada: ${nota.creadaEn.substringBefore('T').ifBlank { "fecha no disponible" }}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NotaCardPreview() {
    NotasTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            NotaCard(Nota("preview", "Mi primera nota", "Esta nota es una vista previa.", "2026-10-12T12:00:00Z"))
        }
    }
}
