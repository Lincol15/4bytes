package com.example.acopiodeleche.ui.pantallas.productor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
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
import com.example.acopiodeleche.domain.model.EstadoPago
import com.example.acopiodeleche.domain.model.SesionActual

private val VerdeHuata = Color(0xFF2E7D32)
private val AzulHuata = Color(0xFF1565C0)

@Composable
fun ProductorHomeScreen(
    onCerrarSesion: () -> Unit,
    modifier: Modifier = Modifier
) {
    val usuario = SesionActual.usuario
    val idProductor = usuario?.idProductor ?: ""
    val productor = DatosMock.productores.find { it.idProductor == idProductor }

    // Filtrar datos del productor actual
    val misEntregas = DatosMock.registrosAcopio.filter { it.idProductor == idProductor }
    val misPagos = DatosMock.pagos.filter { it.idProductor == idProductor }
    val misNotificaciones = DatosMock.notificaciones
    val noLeidas = misNotificaciones.count { !it.leida }

    var tabActual by remember { mutableStateOf(0) }
    val tabs = listOf("Inicio", "Entregas", "Pagos", "Avisos")

    Column(modifier = modifier.fillMaxSize()) {

        // Encabezado verde
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(VerdeHuata)
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = productor?.nombreCompleto ?: usuario?.nombreCompleto ?: "Productor",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "DNI: ${productor?.dni ?: ""} · ${productor?.comunidad ?: ""}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
            Text(
                text = "Salir",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .clickable { onCerrarSesion() }
                    .padding(4.dp),
                color = Color.White.copy(alpha = 0.8f),
                style = MaterialTheme.typography.labelMedium
            )
        }

        // Tabs
        TabRow(selectedTabIndex = tabActual) {
            tabs.forEachIndexed { i, titulo ->
                Tab(
                    selected = tabActual == i,
                    onClick = { tabActual = i },
                    text = {
                        if (i == 3 && noLeidas > 0) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(titulo)
                                Spacer(Modifier.width(4.dp))
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .background(Color.Red, RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$noLeidas",
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        } else {
                            Text(titulo)
                        }
                    }
                )
            }
        }

        when (tabActual) {
            0 -> ResumenProductor(
                idProductor = idProductor,
                totalEntregas = misEntregas.size,
                totalLitros = misEntregas.sumOf { it.litros },
                pagosPendientes = misPagos.count { it.estado == EstadoPago.PENDIENTE }
            )
            1 -> EntregasProductorTab(entregas = misEntregas)
            2 -> PagosProductorTab(pagos = misPagos)
            3 -> NotificacionesTab(notificaciones = misNotificaciones)
        }
    }
}

@Composable
private fun ResumenProductor(
    idProductor: String,
    totalEntregas: Int,
    totalLitros: Double,
    pagosPendientes: Int
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TarjetaResumenProductor("🥛", "$totalEntregas", "Entregas", VerdeHuata, Modifier.weight(1f))
                TarjetaResumenProductor("📦", "$totalLitros L", "Litros", AzulHuata, Modifier.weight(1f))
                TarjetaResumenProductor("💳", "$pagosPendientes", "Pagos pend.", Color(0xFFE65100), Modifier.weight(1f))
            }
        }
        item {
            Text(
                "Precio actual por litro",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = VerdeHuata.copy(alpha = 0.1f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("💰", fontSize = 32.sp)
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "S/ 1.70 por litro",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = VerdeHuata
                        )
                        Text(
                            text = "Precio vigente — Ecolácteos Huata",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaResumenProductor(icono: String, valor: String, etiqueta: String, color: Color, modifier: Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(icono, fontSize = 22.sp)
            Text(valor, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color)
            Text(etiqueta, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun EntregasProductorTab(entregas: List<com.example.acopiodeleche.domain.model.RegistroAcopio>) {
    if (entregas.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No hay entregas registradas", style = MaterialTheme.typography.bodyLarge)
        }
        return
    }
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(entregas) { entrega ->
            Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "${entrega.fecha} — ${entrega.hora}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Acopiador: ${entrega.acopiador}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Vehículo: ${entrega.vehiculo}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = entrega.litrosFormateados,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = VerdeHuata
                            )
                            Text(
                                text = "S/ ${"%.2f".let { (entrega.litros * 1.70).let { t -> "${t.toLong()}.${((t - t.toLong()) * 100).toLong()}" } }}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    if (!entrega.observacion.isNullOrBlank()) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Obs: ${entrega.observacion}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PagosProductorTab(pagos: List<com.example.acopiodeleche.domain.model.Pago>) {
    if (pagos.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No hay pagos registrados", style = MaterialTheme.typography.bodyLarge)
        }
        return
    }
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(pagos) { pago ->
            Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Código: ${pago.codigoPago}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Del ${pago.periodoDesde} al ${pago.periodoHasta}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Box(
                            modifier = Modifier
                                .background(
                                    when (pago.estado) {
                                        EstadoPago.PAGADO -> VerdeHuata.copy(alpha = 0.15f)
                                        EstadoPago.PENDIENTE -> Color(0xFFE65100).copy(alpha = 0.15f)
                                        else -> Color.Gray.copy(alpha = 0.15f)
                                    },
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = pago.estado.etiqueta,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = when (pago.estado) {
                                    EstadoPago.PAGADO -> VerdeHuata
                                    EstadoPago.PENDIENTE -> Color(0xFFE65100)
                                    else -> Color.Gray
                                }
                            )
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Precio por litro:", style = MaterialTheme.typography.bodySmall)
                        Text(pago.precioPorLitroFormateado, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Litros entregados:", style = MaterialTheme.typography.bodySmall)
                        Text(pago.litrosFormateados, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                    }

                    // Detalle diario
                    if (pago.detalleDiario.isNotEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        Text("Detalle:", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                        pago.detalleDiario.forEach { (dia, litrosDia) ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(dia, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("$litrosDia L", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("TOTAL A PAGAR:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text(
                            text = "S/ ${"%.2f".let { pago.totalCalculado.let { t -> "${t.toLong()}.${((t - t.toLong()) * 100).toLong().toString().padStart(2, '0')}" } }}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = VerdeHuata
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificacionesTab(notificaciones: List<com.example.acopiodeleche.domain.model.Notificacion>) {
    if (notificaciones.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No hay notificaciones", style = MaterialTheme.typography.bodyLarge)
        }
        return
    }
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(notificaciones) { notif ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(2.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (!notif.leida)
                        VerdeHuata.copy(alpha = 0.05f)
                    else
                        MaterialTheme.colorScheme.surface
                )
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                    Text(notif.tipo.icono, fontSize = 28.sp)
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = notif.titulo,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = if (!notif.leida) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.weight(1f)
                            )
                            if (!notif.leida) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(VerdeHuata, RoundedCornerShape(4.dp))
                                )
                            }
                        }
                        Text(
                            text = notif.mensaje,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${notif.fecha} ${notif.hora}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
