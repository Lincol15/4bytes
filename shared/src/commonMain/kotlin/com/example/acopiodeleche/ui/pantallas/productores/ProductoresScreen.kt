package com.example.acopiodeleche.ui.pantallas.productores

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.acopiodeleche.domain.model.DatosMock
import com.example.acopiodeleche.domain.model.Productor

@Composable
fun ProductoresScreen(modifier: Modifier = Modifier) {

    // Estado de la lista — vive aquí (estado elevado)
    var productores by remember { mutableStateOf(DatosMock.productores.toList()) }
    var mostrarFormulario by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()) {

        // Encabezado
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Text(
                text = "Productores",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = "${productores.size} registrados · ${productores.count { it.estado }} activos",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Botón registrar
        Button(
            onClick = { mostrarFormulario = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text("+ Registrar productor")
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // Formulario o lista
        if (mostrarFormulario) {
            FormularioProductor(
                alGuardar = { nuevo ->
                    productores = productores + nuevo
                    DatosMock.productores.add(nuevo)
                    mostrarFormulario = false
                },
                alCancelar = { mostrarFormulario = false }
            )
        } else {
            if (productores.isEmpty()) {
                EstadoVacioProductores()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(productores, key = { it.idProductor }) { productor ->
                        TarjetaProductor(productor = productor)
                    }
                }
            }
        }
    }
}

@Composable
private fun EstadoVacioProductores() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "No hay productores registrados",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Presiona \"+ Registrar productor\" para agregar uno",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp, start = 32.dp, end = 32.dp)
            )
        }
    }
}
