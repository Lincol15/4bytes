package com.example.acopiodeleche.ui.pantallas.acopio

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.acopiodeleche.domain.model.EstadoSync
import com.example.acopiodeleche.ui.tema.tarjetaHuata
import com.example.acopiodeleche.domain.model.RegistroAcopio

@Composable
fun TarjetaAcopio(
    registro: RegistroAcopio,
    modifier: Modifier = Modifier,
    onReintentar: ((RegistroAcopio) -> Unit)? = null,
    onDescartar: ((RegistroAcopio) -> Unit)? = null
) {
    // El backend devuelve el nombre del productor junto con el registro
    val nombreProductor = registro.nombreProductor ?: "Productor desconocido"

    Box(modifier.fillMaxWidth().tarjetaHuata(RoundedCornerShape(16.dp))) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Fila superior: productor + litros
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = nombreProductor,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = registro.zona,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = registro.litrosFormateados,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))

            // Datos del registro
            FilaDatoAcopio(etiqueta = "Vehículo", valor = registro.vehiculo)
            FilaDatoAcopio(etiqueta = "Acopiador", valor = registro.acopiador)
            FilaDatoAcopio(etiqueta = "Fecha", valor = "${registro.fecha}  ${registro.hora}")
            FilaDatoAcopio(etiqueta = "Observación", valor = registro.observacionCorta)

            Spacer(modifier = Modifier.height(8.dp))

            // Chip de estado: recepción en planta o, si solo está en el teléfono, sincronización
            SuggestionChip(
                onClick = {},
                label = { Text(registro.estadoRecepcion) },
                colors = SuggestionChipDefaults.suggestionChipColors(
                    containerColor = when {
                        registro.estadoSync == EstadoSync.ERROR -> MaterialTheme.colorScheme.errorContainer
                        registro.estadoSync == EstadoSync.PENDIENTE -> MaterialTheme.colorScheme.tertiaryContainer
                        registro.recibido -> MaterialTheme.colorScheme.primaryContainer
                        else -> MaterialTheme.colorScheme.secondaryContainer
                    }
                )
            )

            // Rechazado al sincronizar: se muestra el motivo y se puede reintentar o descartar
            if (registro.estadoSync == EstadoSync.ERROR) {
                Text(
                    text = registro.errorSync ?: "El servidor no aceptó el registro.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    onReintentar?.let { TextButton(onClick = { it(registro) }) { Text("Reintentar") } }
                    onDescartar?.let { TextButton(onClick = { it(registro) }) { Text("Descartar") } }
                }
            }
        }
    }
}

@Composable
private fun FilaDatoAcopio(etiqueta: String, valor: String) {
    Row(modifier = Modifier.padding(vertical = 2.dp)) {
        Text(
            text = "$etiqueta: ",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = valor,
            style = MaterialTheme.typography.bodySmall
        )
    }
}
