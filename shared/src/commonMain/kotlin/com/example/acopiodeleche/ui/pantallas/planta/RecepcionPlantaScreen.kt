package com.example.acopiodeleche.ui.pantallas.planta

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.acopiodeleche.domain.model.RegistroAcopio
import com.example.acopiodeleche.ui.estado.CargaOError
import com.example.acopiodeleche.ui.estado.EstadoEnvio
import com.example.acopiodeleche.ui.estado.EstadoUi
import com.example.acopiodeleche.ui.tema.HuataColores
import org.koin.compose.viewmodel.koinViewModel

private val AzulRecepcion = HuataColores.Azul

/** Recepción en planta: lista las entregas en tránsito y registra su llegada en el backend. */
@Composable
fun RecepcionPlantaScreen(
    viewModel: RecepcionPlantaViewModel = koinViewModel()
) {
    val pendientes by viewModel.pendientes.collectAsStateWithLifecycle()
    val envio by viewModel.envio.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Entregas en tránsito",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = viewModel::cargar) { Text("Actualizar") }
        }

        when (val actual = envio) {
            is EstadoEnvio.Exito -> MensajeEnvio(actual.mensaje, AzulRecepcion, viewModel::limpiarEnvio)
            is EstadoEnvio.Error -> MensajeEnvio(actual.mensaje, MaterialTheme.colorScheme.error, viewModel::limpiarEnvio)
            else -> Unit
        }

        when (val actual = pendientes) {
            is EstadoUi.Exito -> if (actual.datos.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay entregas pendientes de recepción", style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(actual.datos, key = { it.id }) { registro ->
                        TarjetaPendiente(
                            registro = registro,
                            enviando = envio == EstadoEnvio.Enviando,
                            onRecibir = { viewModel.registrarRecepcion(registro) }
                        )
                    }
                }
            }
            else -> CargaOError(actual, onReintentar = viewModel::cargar)
        }
    }
}

@Composable
private fun TarjetaPendiente(registro: RegistroAcopio, enviando: Boolean, onRecibir: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    registro.nombreProductor ?: "Productor",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "${registro.fecha} ${registro.hora} · ${registro.zona}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "🚛 ${registro.acopiador} · ${registro.vehiculo}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    registro.litrosFormateados,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AzulRecepcion
                )
            }
            Button(
                onClick = onRecibir,
                enabled = !enviando,
                colors = ButtonDefaults.buttonColors(containerColor = AzulRecepcion)
            ) { Text("Recibir") }
        }
    }
}

@Composable
private fun MensajeEnvio(mensaje: String, color: Color, onCerrar: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(mensaje, color = color, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
        TextButton(onClick = onCerrar) { Text("✕") }
    }
}
