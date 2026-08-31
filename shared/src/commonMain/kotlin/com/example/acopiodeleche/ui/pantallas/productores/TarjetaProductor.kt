package com.example.acopiodeleche.ui.pantallas.productores

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.acopiodeleche.domain.model.Productor

@Composable
fun TarjetaProductor(
    productor: Productor,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Fila: nombre + chip de estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = productor.nombreCompleto,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                SuggestionChip(
                    onClick = {},
                    label = { Text(productor.estadoTexto) },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = if (productor.estado)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.errorContainer
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Datos del productor
            FilaDato(etiqueta = "DNI", valor = productor.dni)
            FilaDato(etiqueta = "Zona", valor = productor.comunidad)
            FilaDato(etiqueta = "Teléfono", valor = productor.telefono)
        }
    }
}

@Composable
private fun FilaDato(etiqueta: String, valor: String) {
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
