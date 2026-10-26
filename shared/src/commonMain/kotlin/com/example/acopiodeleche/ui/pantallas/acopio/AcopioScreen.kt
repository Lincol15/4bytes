package com.example.acopiodeleche.ui.pantallas.acopio

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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.acopiodeleche.ui.estado.CargaOError
import com.example.acopiodeleche.ui.estado.EstadoEnvio
import com.example.acopiodeleche.ui.estado.EstadoUi
import com.example.acopiodeleche.ui.estado.datosOVacio

@Composable
fun AcopioScreen(
    onVolver: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    viewModel: RecoleccionViewModel = viewModel { RecoleccionViewModel() }
) {
    val estadoRegistros by viewModel.registros.collectAsStateWithLifecycle()
    val estadoProductores by viewModel.productores.collectAsStateWithLifecycle()
    val envio by viewModel.envio.collectAsStateWithLifecycle()
    val registros = estadoRegistros.datosOVacio()
    var mostrarFormulario by remember { mutableStateOf(false) }

    // Al confirmar el backend el registro, se cierra el formulario y se muestra la confirmación
    LaunchedEffect(envio) {
        if (envio is EstadoEnvio.Exito) mostrarFormulario = false
    }

    Column(modifier = modifier.fillMaxSize()) {

        // Encabezado
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            if (onVolver != null) {
                TextButton(onClick = onVolver) { Text("← Volver") }
            }
            Text(
                text = "Acopio de leche",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = "${registros.size} registros · ${
                    registros.sumOf { it.litros }.let {
                        val entero = it.toLong()
                        val decimal = ((it - entero) * 10).toLong()
                        "$entero.${decimal} L"
                    }
                } en total",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Button(
            onClick = { viewModel.limpiarEnvio(); mostrarFormulario = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) { Text("+ Registrar acopio") }

        when (val actual = envio) {
            is EstadoEnvio.Exito -> MensajeEnvio(actual.mensaje, Color(0xFF2E7D32), viewModel::limpiarEnvio)
            is EstadoEnvio.Error -> MensajeEnvio(actual.mensaje, MaterialTheme.colorScheme.error, viewModel::limpiarEnvio)
            else -> Unit
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        if (mostrarFormulario) {
            if (estadoProductores is EstadoUi.Exito) {
                FormularioAcopio(
                    productores = estadoProductores.datosOVacio(),
                    enviando = envio == EstadoEnvio.Enviando,
                    alGuardar = { nuevo -> viewModel.registrar(nuevo) },
                    alCancelar = { mostrarFormulario = false }
                )
            } else {
                CargaOError(estadoProductores, onReintentar = viewModel::cargar)
            }
        } else if (estadoRegistros !is EstadoUi.Exito) {
            CargaOError(estadoRegistros, onReintentar = viewModel::cargar)
        } else {
            if (registros.isEmpty()) {
                EstadoVacioAcopio()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(registros, key = { it.id }) { registro ->
                        TarjetaAcopio(registro = registro)
                    }
                }
            }
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

@Composable
private fun EstadoVacioAcopio() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "No hay registros de acopio",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Presiona \"+ Registrar acopio\" para agregar uno",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp, start = 32.dp, end = 32.dp)
            )
        }
    }
}
