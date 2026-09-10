package com.example.acopiodeleche.ui.pantallas.pagos

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.acopiodeleche.domain.model.ConfiguracionPlanta
import com.example.acopiodeleche.domain.model.DatosMock
import com.example.acopiodeleche.domain.model.EstadoPago
import com.example.acopiodeleche.domain.model.Pago
import com.example.acopiodeleche.domain.model.Productor
import com.example.acopiodeleche.domain.model.RegistroAcopio
import kotlin.random.Random

private val VerdeHuata = Color(0xFF2E7D32)
private val AzulPago   = Color(0xFF1565C0)

/**
 * Pantalla de Pagos para el Administrador.
 *
 * FLUJO CORRECTO:
 * 1. El acopiador registra litros diariamente → se guarda en DatosMock.registrosAcopio
 * 2. El admin ve en tiempo real los litros acumulados por productor
 * 3. Al fin de semana (viernes), el admin genera el pago con un solo clic
 *    → el sistema calcula: litrosTotales × precioPorLitro
 * 4. El productor ve el pago en su app
 */
@Composable
fun PagosAdminScreen(
    onVolver: () -> Unit,
    modifier: Modifier = Modifier
) {
    var tabActual by remember { mutableStateOf(0) }
    val tabs = listOf("Resumen litros", "Pagos generados", "Acopiadores")

    Column(modifier = modifier.fillMaxSize()) {

        // Encabezado
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(VerdeHuata)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onVolver) {
                Text("←", color = Color.White, fontSize = 20.sp)
            }
            Spacer(Modifier.width(4.dp))
            Column {
                Text(
                    "Gestión de Pagos",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Precio: S/ ${ConfiguracionPlanta.precioPorLitro}/L · Pago cada viernes",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }

        TabRow(selectedTabIndex = tabActual) {
            tabs.forEachIndexed { i, t ->
                Tab(selected = tabActual == i, onClick = { tabActual = i }, text = { Text(t) })
            }
        }

        when (tabActual) {
            0 -> ResumenLitrosTab()
            1 -> PagosGeneradosTab()
            2 -> PagosAcopiadoresTab()
        }
    }
}

// ── TAB 1: RESUMEN DE LITROS (auto-calculado desde registros del acopiador) ──

@Composable
private fun ResumenLitrosTab() {
    val registros = DatosMock.registrosAcopio
    val productores = DatosMock.productores.filter { it.estado }
    val precio = ConfiguracionPlanta.precioPorLitro

    // Agrupar registros por productor
    val litrosPorProductor = registros
        .groupBy { it.idProductor }
        .mapValues { (_, regs) -> regs.sumOf { it.litros } }

    val totalGeneral = litrosPorProductor.values.sumOf { it }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            // Resumen general
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = VerdeHuata.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("📊 Resumen acumulado", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total litros recolectados:", style = MaterialTheme.typography.bodyMedium)
                        Text("$totalGeneral L", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total a pagar (${productores.size} productores):", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            "S/ ${(totalGeneral * precio).let { t -> "${t.toLong()}.${((t - t.toLong()) * 100).toLong().toString().padStart(2,'0')}" }}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = VerdeHuata
                        )
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Registros de acopio:", style = MaterialTheme.typography.bodySmall)
                        Text("${registros.size}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Text(
                "Litros por productor (desde registros del acopiador)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }

        if (productores.isEmpty() || litrosPorProductor.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().height(120.dp), contentAlignment = Alignment.Center) {
                    Text("No hay registros de acopio aún", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            items(productores) { productor ->
                val litros = litrosPorProductor[productor.idProductor] ?: 0.0
                val totalProductor = litros * precio
                val regsProductor = registros.filter { it.idProductor == productor.idProductor }
                var yaGenerado by remember {
                    mutableStateOf(DatosMock.pagos.any { it.idProductor == productor.idProductor })
                }

                Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Encabezado productor
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(productor.nombreCompleto, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                Text(productor.comunidad, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${regsProductor.size} entregas registradas", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    "$litros L",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = VerdeHuata
                                )
                                Text(
                                    "S/ ${totalProductor.let { t -> "${t.toLong()}.${((t - t.toLong()) * 100).toLong().toString().padStart(2,'0')}" }}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = AzulPago
                                )
                            }
                        }

                        // Detalle por entrega
                        if (regsProductor.isNotEmpty()) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
                            regsProductor.forEach { reg ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 1.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        "${reg.fecha} ${reg.hora}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        reg.litrosFormateados,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        // Botón generar pago
                        Spacer(Modifier.height(8.dp))
                        if (litros > 0) {
                            if (yaGenerado) {
                                Text(
                                    "✅ Pago ya generado para este productor",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = VerdeHuata
                                )
                            } else {
                                Button(
                                    onClick = {
                                        // Generar pago automáticamente desde los registros
                                        val detalle = regsProductor.associate { reg ->
                                            "${reg.fecha} ${reg.hora}" to reg.litros
                                        }
                                        DatosMock.pagos.add(
                                            Pago(
                                                id = "pago-${Random.nextInt(1000, 9999)}",
                                                idProductor = productor.idProductor,
                                                codigoPago = "H-${Random.nextInt(10, 99)}",
                                                periodoDesde = regsProductor.minByOrNull { it.fecha }?.fecha ?: "",
                                                periodoHasta = regsProductor.maxByOrNull { it.fecha }?.fecha ?: "",
                                                precioPorLitro = precio,
                                                litrosTotales = litros,
                                                detalleDiario = detalle,
                                                estado = EstadoPago.PENDIENTE
                                            )
                                        )
                                        yaGenerado = true
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = VerdeHuata)
                                ) {
                                    Text(
                                        "Generar pago: S/ ${totalProductor.let { t -> "${t.toLong()}.${((t - t.toLong()) * 100).toLong().toString().padStart(2,'0')}" }}",
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        } else {
                            Text(
                                "Sin entregas registradas esta semana",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── TAB 2: PAGOS GENERADOS ────────────────────────────────────────────────

@Composable
private fun PagosGeneradosTab() {
    var pagos by remember { mutableStateOf(DatosMock.pagos.toList()) }
    val precio = ConfiguracionPlanta.precioPorLitro

    val pendientes = pagos.count { it.estado == EstadoPago.PENDIENTE }
    val totalPendiente = pagos.filter { it.estado == EstadoPago.PENDIENTE }.sumOf { it.totalCalculado }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                TarjetaResumenPago("⏳", "$pendientes", "Pendientes", Color(0xFFE65100), Modifier.weight(1f))
                TarjetaResumenPago(
                    "💰",
                    "S/ ${totalPendiente.let { t -> "${t.toLong()}.${((t - t.toLong()) * 100).toLong().toString().padStart(2,'0')}" }}",
                    "Por pagar",
                    VerdeHuata,
                    Modifier.weight(1f)
                )
            }
        }

        if (pagos.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("💳", fontSize = 40.sp)
                        Spacer(Modifier.height(8.dp))
                        Text("No hay pagos generados aún", style = MaterialTheme.typography.bodyLarge)
                        Text("Ve a \"Resumen litros\" y genera los pagos", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        } else {
            items(pagos) { pago ->
                TarjetaPagoProductor(
                    pago = pago,
                    onMarcarPagado = {
                        val idx = DatosMock.pagos.indexOfFirst { it.id == pago.id }
                        if (idx != -1) {
                            DatosMock.pagos[idx] = pago.copy(estado = EstadoPago.PAGADO, fechaPago = "Hoy")
                            pagos = DatosMock.pagos.toList()
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun TarjetaResumenPago(icono: String, valor: String, etiqueta: String, color: Color, modifier: Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(icono, fontSize = 20.sp)
            Text(valor, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = color)
            Text(etiqueta, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun TarjetaPagoProductor(pago: Pago, onMarcarPagado: () -> Unit) {
    val productor = DatosMock.productores.find { it.idProductor == pago.idProductor }
    val colorEstado = when (pago.estado) {
        EstadoPago.PAGADO    -> VerdeHuata
        EstadoPago.PENDIENTE -> Color(0xFFE65100)
        EstadoPago.PARCIAL   -> Color(0xFFF9A825)
    }

    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(productor?.nombreCompleto ?: "Productor", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Código: ${pago.codigoPago}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    if (pago.periodoDesde.isNotBlank()) {
                        Text("Periodo: ${pago.periodoDesde} — ${pago.periodoHasta}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Box(
                    modifier = Modifier
                        .background(colorEstado.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(pago.estado.etiqueta, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = colorEstado)
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Detalle de entregas
            if (pago.detalleDiario.isNotEmpty()) {
                Text("Detalle de entregas:", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                pago.detalleDiario.entries.take(5).forEach { (fecha, litrosDia) ->
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 1.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(fecha, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("$litrosDia L", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                    }
                }
                if (pago.detalleDiario.size > 5) {
                    Text("... y ${pago.detalleDiario.size - 5} más", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total litros:", style = MaterialTheme.typography.bodySmall)
                Text(pago.litrosFormateados, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Precio por litro:", style = MaterialTheme.typography.bodySmall)
                Text(pago.precioPorLitroFormateado, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("TOTAL A PAGAR:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(
                    "S/ ${pago.totalCalculado.let { t -> "${t.toLong()}.${((t - t.toLong()) * 100).toLong().toString().padStart(2,'0')}" }}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = VerdeHuata
                )
            }

            if (pago.estado == EstadoPago.PENDIENTE) {
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = onMarcarPagado,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = VerdeHuata)
                ) { Text("✅ Marcar como PAGADO") }
            } else if (pago.fechaPago != null) {
                Spacer(Modifier.height(4.dp))
                Text("Pagado el: ${pago.fechaPago}", style = MaterialTheme.typography.bodySmall, color = VerdeHuata)
            }
        }
    }
}

// ── TAB 3: PAGOS A ACOPIADORES ────────────────────────────────────────────

@Composable
private fun PagosAcopiadoresTab() {
    val registros = DatosMock.registrosAcopio
    val tarifaAcopiador = 0.20

    val acopiadoresConRegistros = registros
        .groupBy { it.acopiador }
        .map { (nombre, regs) -> Triple(nombre, regs.sumOf { it.litros }, regs.size) }
        .sortedByDescending { it.second }

    if (acopiadoresConRegistros.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No hay registros de acopio", style = MaterialTheme.typography.bodyLarge)
        }
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = AzulPago.copy(alpha = 0.08f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("ℹ️ Pago al acopiador", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Text("Tarifa: S/ $tarifaAcopiador por litro recolectado", style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        items(acopiadoresConRegistros) { (nombre, litros, cantRegs) ->
            val total = litros * tarifaAcopiador
            Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("$cantRegs entregas · $litros L recolectados", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                "S/ ${total.let { t -> "${t.toLong()}.${((t - t.toLong()) * 100).toLong().toString().padStart(2,'0')}" }}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = AzulPago
                            )
                            Text("${litros}L × S/$tarifaAcopiador", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = { },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = AzulPago)
                    ) { Text("Registrar pago al acopiador") }
                }
            }
        }
    }
}
