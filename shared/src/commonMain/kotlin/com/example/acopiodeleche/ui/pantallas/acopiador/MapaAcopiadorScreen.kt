package com.example.acopiodeleche.ui.pantallas.acopiador

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.acopiodeleche.domain.model.DatosMock

private val VerdeHuata = Color(0xFF2E7D32)
private val AzulMapa   = Color(0xFF0277BD)

/**
 * Punto de ruta del acopiador.
 * Puede ser el origen (planta), destino (productor) o parada.
 */
data class PuntoRuta(
    val id: String,
    val nombre: String,
    val tipo: TipoPunto,
    val latitud: Double,
    val longitud: Double
)

enum class TipoPunto(val etiqueta: String, val icono: String) {
    ORIGEN("Punto de inicio", "🏭"),
    DESTINO("Punto final", "🏁"),
    PRODUCTOR("Productor", "🌾"),
    PARADA("Parada", "📍")
}

@Composable
fun MapaAcopiadorScreen(
    onVolver: () -> Unit,
    modifier: Modifier = Modifier
) {
    var puntos by remember {
        mutableStateOf(
            mutableListOf(
                PuntoRuta("p0", "Planta Ecolácteos Huata", TipoPunto.ORIGEN, -15.840, -70.021),
                PuntoRuta("p1", "Pedro Quispe — Comunidad A", TipoPunto.PRODUCTOR, -15.845, -70.018),
                PuntoRuta("p2", "María Condori — Comunidad B", TipoPunto.PRODUCTOR, -15.850, -70.015),
                PuntoRuta("p3", "Rosa Mamani — Comunidad C", TipoPunto.PRODUCTOR, -15.855, -70.012)
            )
        )
    }

    var mostrarAgregarPunto by remember { mutableStateOf(false) }
    var nombreNuevoPunto    by remember { mutableStateOf("") }
    var latitudTexto        by remember { mutableStateOf("") }
    var longitudTexto       by remember { mutableStateOf("") }
    var tipoSeleccionado    by remember { mutableStateOf(TipoPunto.PRODUCTOR) }

    Column(modifier = modifier.fillMaxSize()) {

        // Encabezado
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(AzulMapa)
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = onVolver) {
                    Text("←", color = Color.White, fontSize = 20.sp)
                }
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(
                        "Mapa de ruta",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "${puntos.size} puntos en la ruta",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }

        // Vista del mapa (WebView con OpenStreetMap)
        MapaWebView(
            puntos = puntos,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )

        HorizontalDivider()

        // Lista de puntos + agregar
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Puntos de la ruta",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Button(
                        onClick = { mostrarAgregarPunto = !mostrarAgregarPunto },
                        colors = ButtonDefaults.buttonColors(containerColor = AzulMapa)
                    ) { Text(if (mostrarAgregarPunto) "Cancelar" else "+ Agregar punto") }
                }
            }

            // Formulario para agregar punto
            if (mostrarAgregarPunto) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("Nuevo punto", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

                            // Selector de tipo
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                TipoPunto.entries.forEach { tipo ->
                                    val seleccionado = tipoSeleccionado == tipo
                                    OutlinedButton(
                                        onClick = { tipoSeleccionado = tipo },
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            containerColor = if (seleccionado) AzulMapa.copy(0.1f) else Color.Transparent
                                        )
                                    ) {
                                        Text(
                                            "${tipo.icono} ${tipo.etiqueta.take(6)}",
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = nombreNuevoPunto,
                                onValueChange = { nombreNuevoPunto = it },
                                label = { Text("Nombre del punto") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = latitudTexto,
                                    onValueChange = { latitudTexto = it },
                                    label = { Text("Latitud") },
                                    placeholder = { Text("-15.840") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = longitudTexto,
                                    onValueChange = { longitudTexto = it },
                                    label = { Text("Longitud") },
                                    placeholder = { Text("-70.021") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Button(
                                onClick = {
                                    val lat = latitudTexto.toDoubleOrNull()
                                    val lon = longitudTexto.toDoubleOrNull()
                                    if (nombreNuevoPunto.isNotBlank() && lat != null && lon != null) {
                                        val nuevaLista = puntos.toMutableList()
                                        nuevaLista.add(
                                            PuntoRuta(
                                                id = "p${puntos.size}",
                                                nombre = nombreNuevoPunto.trim(),
                                                tipo = tipoSeleccionado,
                                                latitud = lat,
                                                longitud = lon
                                            )
                                        )
                                        puntos = nuevaLista
                                        nombreNuevoPunto = ""
                                        latitudTexto = ""
                                        longitudTexto = ""
                                        mostrarAgregarPunto = false
                                    }
                                },
                                enabled = nombreNuevoPunto.isNotBlank()
                                    && latitudTexto.toDoubleOrNull() != null
                                    && longitudTexto.toDoubleOrNull() != null,
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = AzulMapa)
                            ) { Text("Agregar a la ruta") }
                        }
                    }
                }
            }

            // Lista de puntos existentes
            items(puntos) { punto ->
                TarjetaPuntoRuta(
                    punto = punto,
                    onEliminar = {
                        if (punto.tipo != TipoPunto.ORIGEN) {
                            puntos = puntos.filter { it.id != punto.id }.toMutableList()
                        }
                    }
                )
            }

            item {
                Spacer(Modifier.height(8.dp))
                // Instrucciones
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = AzulMapa.copy(alpha = 0.08f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("ℹ️ Cómo usar el mapa", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(4.dp))
                        Text("• El mapa muestra todos los puntos de la ruta", style = MaterialTheme.typography.bodySmall)
                        Text("• Agrega puntos con latitud y longitud del productor", style = MaterialTheme.typography.bodySmall)
                        Text("• 🏭 = Planta (inicio) · 🌾 = Productor · 🏁 = Final", style = MaterialTheme.typography.bodySmall)
                        Text("• Toca el mapa para hacer zoom e interactuar", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaPuntoRuta(punto: PuntoRuta, onEliminar: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        when (punto.tipo) {
                            TipoPunto.ORIGEN    -> VerdeHuata.copy(0.15f)
                            TipoPunto.DESTINO   -> Color.Red.copy(0.15f)
                            TipoPunto.PRODUCTOR -> AzulMapa.copy(0.15f)
                            TipoPunto.PARADA    -> Color.Gray.copy(0.15f)
                        },
                        RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) { Text(punto.tipo.icono, fontSize = 20.sp) }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(punto.nombre, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                Text(
                    "Lat: ${punto.latitud} · Lon: ${punto.longitud}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(punto.tipo.etiqueta, style = MaterialTheme.typography.labelSmall, color = AzulMapa)
            }

            if (punto.tipo != TipoPunto.ORIGEN) {
                TextButton(onClick = onEliminar) {
                    Text("✕", color = Color.Red)
                }
            }
        }
    }
}
