package com.example.acopiodeleche.ui.estado

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.acopiodeleche.data.remote.ApiResult
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

/** Estado de una carga de datos desde el backend. */
sealed interface EstadoUi<out T> {
    data object Cargando : EstadoUi<Nothing>
    data class Exito<T>(val datos: T) : EstadoUi<T>
    data class Error(val mensaje: String) : EstadoUi<Nothing>
}

/** Estado de un envío (registro/actualización) hacia el backend. */
sealed interface EstadoEnvio {
    data object Inactivo : EstadoEnvio
    data object Enviando : EstadoEnvio
    data class Exito(val mensaje: String) : EstadoEnvio
    data class Error(val mensaje: String) : EstadoEnvio
}

fun <T> ApiResult<T>.aEstadoUi(): EstadoUi<T> = when (this) {
    is ApiResult.Exito -> EstadoUi.Exito(datos)
    is ApiResult.Error -> EstadoUi.Error(mensaje)
}

/** Datos actuales si la carga terminó bien; lista vacía en otro caso. */
fun <T> EstadoUi<List<T>>.datosOVacio(): List<T> = (this as? EstadoUi.Exito)?.datos.orEmpty()

/** Fecha de hoy del dispositivo en el formato de pantalla "dd/MM/yyyy". */
fun fechaHoyPantalla(): String {
    val hoy = kotlin.time.Clock.System.todayIn(TimeZone.currentSystemDefault())
    return "${hoy.dayOfMonth.toString().padStart(2, '0')}/${hoy.monthNumber.toString().padStart(2, '0')}/${hoy.year}"
}

/** Indicador de carga o mensaje de error con opción de reintentar. */
@Composable
fun CargaOError(estado: EstadoUi<*>, onReintentar: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        when (estado) {
            is EstadoUi.Cargando -> CircularProgressIndicator()
            is EstadoUi.Error -> Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    estado.mensaje,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
                Button(onClick = onReintentar) { Text("Reintentar") }
            }
            is EstadoUi.Exito -> Unit
        }
    }
}
