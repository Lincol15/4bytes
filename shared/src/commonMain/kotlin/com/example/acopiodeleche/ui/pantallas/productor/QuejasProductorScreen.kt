package com.example.acopiodeleche.ui.pantallas.productor

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.acopiodeleche.domain.model.DatosMock
import com.example.acopiodeleche.domain.model.EstadoQueja
import com.example.acopiodeleche.domain.model.Queja
import com.example.acopiodeleche.domain.model.SesionActual
import kotlin.random.Random

private val VerdeHuata = Color(0xFF2E7D32)

@Composable
fun QuejasProductorScreen(
    modifier: Modifier = Modifier
) {
    val idProductor = SesionActual.usuario?.idProductor ?: ""
    var quejas by remember {
        mutableStateOf(DatosMock.quejas.filter { it.idProductor == idProductor })
    }
    var mostrarFormulario by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()) {

        // Encabezado
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(VerdeHuata)
                .padding(16.dp)
        ) {
            Column {
                Text(
                    "Mis quejas y reclamos",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "El administrador responderá a tus quejas",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }

        if (mostrarFormulario) {
            FormularioQueja(
                alGuardar = { nueva ->
                    DatosMock.quejas.add(nueva)
                    quejas = DatosMock.quejas.filter { it.idProductor == idProductor }
                    mostrarFormulario = false
                },
                alCancelar = { mostrarFormulario = false },
                idProductor = idProductor
            )
        } else {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "${quejas.size} quejas enviadas",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Button(
                    onClick = { mostrarFormulario = true },
                    colors = ButtonDefaults.buttonColors(containerColor = VerdeHuata)
                ) { Text("+ Nueva queja") }
            }
            HorizontalDivider()

            if (quejas.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📝", fontSize = 48.sp)
                        Spacer(Modifier.height(8.dp))
                        Text("No has enviado ninguna queja", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Usa el botón para enviar una queja al administrador",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(quejas) { queja -> TarjetaQueja(queja) }
                }
            }
        }
    }
}

@Composable
private fun TarjetaQueja(queja: Queja) {
    val colorEstado = when (queja.estado) {
        EstadoQueja.PENDIENTE    -> Color(0xFFE65100)
        EstadoQueja.EN_REVISION  -> Color(0xFF1565C0)
        EstadoQueja.RESPONDIDA   -> VerdeHuata
        EstadoQueja.CERRADA      -> Color.Gray
    }

    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(queja.titulo, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Text(
                        "${queja.fecha} ${queja.hora}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Box(
                    modifier = Modifier
                        .background(colorEstado.copy(0.15f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        "${queja.estado.icono} ${queja.estado.etiqueta}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = colorEstado
                    )
                }
            }
            Spacer(Modifier.height(6.dp))
            Text(queja.descripcion, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

            // Respuesta del admin
            if (!queja.respuesta.isNullOrBlank()) {
                Spacer(Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(Modifier.height(8.dp))
                Card(colors = CardDefaults.cardColors(containerColor = VerdeHuata.copy(0.08f))) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            "Respuesta del administrador:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = VerdeHuata
                        )
                        Text(queja.respuesta, style = MaterialTheme.typography.bodySmall)
                        if (!queja.fechaRespuesta.isNullOrBlank()) {
                            Text(
                                queja.fechaRespuesta,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FormularioQueja(
    alGuardar: (Queja) -> Unit,
    alCancelar: () -> Unit,
    idProductor: String
) {
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }
    var hora by remember { mutableStateOf("") }

    val formularioValido = titulo.isNotBlank() && descripcion.isNotBlank() && fecha.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = alCancelar) { Text("← Volver") }
            Text("Nueva queja / reclamo", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }

        Text(
            "Tu queja será enviada directamente al administrador de Ecolácteos Huata.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        OutlinedTextField(
            value = titulo,
            onValueChange = { titulo = it },
            label = { Text("Asunto / Título *") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción detallada *") },
            placeholder = { Text("Explica tu queja con el mayor detalle posible...") },
            minLines = 4,
            modifier = Modifier.fillMaxWidth()
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = fecha,
                onValueChange = { fecha = it },
                label = { Text("Fecha *") },
                placeholder = { Text("dd/MM/yyyy") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = hora,
                onValueChange = { hora = it },
                label = { Text("Hora") },
                placeholder = { Text("HH:mm") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }

        Button(
            onClick = {
                alGuardar(
                    Queja(
                        id = "q-${Random.nextInt(1000,9999)}",
                        idProductor = idProductor,
                        titulo = titulo.trim(),
                        descripcion = descripcion.trim(),
                        fecha = fecha.trim(),
                        hora = hora.ifBlank { "00:00" },
                        estado = EstadoQueja.PENDIENTE
                    )
                )
            },
            enabled = formularioValido,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = VerdeHuata)
        ) { Text("Enviar queja", fontWeight = FontWeight.Bold, fontSize = 16.sp) }
    }
}
