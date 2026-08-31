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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.acopiodeleche.domain.model.DatosMock
import com.example.acopiodeleche.domain.model.RegistroAcopio

@Composable
fun TarjetaAcopio(
    registro: RegistroAcopio,
    modifier: Modifier = Modifier
) {
    // Resuelve el nombre del productor por ID
    val nombreProductor = DatosMock.productores
        .find { it.idProductor == registro.idProductor }
        ?.nombreCompleto ?: "Productor desconocido"

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
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

            // Chip de estado de recepción
            SuggestionChip(
                onClick = {},
                label = { Text(registro.estadoRecepcion) },
                colors = SuggestionChipDefaults.suggestionChipColors(
                    containerColor = if (registro.recibido)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.secondaryContainer
                )
            )
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
