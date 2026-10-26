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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.acopiodeleche.domain.model.PagoAcopiador
import com.example.acopiodeleche.domain.model.VistaPreviaPago
import com.example.acopiodeleche.ui.estado.CargaOError
import com.example.acopiodeleche.ui.estado.EstadoEnvio
import com.example.acopiodeleche.ui.estado.EstadoUi
import com.example.acopiodeleche.ui.estado.datosOVacio
import com.example.acopiodeleche.domain.model.EstadoPago
import com.example.acopiodeleche.domain.model.Pago
import com.example.acopiodeleche.domain.model.Productor
import com.example.acopiodeleche.domain.model.RegistroAcopio
import com.example.acopiodeleche.ui.tema.HuataColores
import org.koin.compose.viewmodel.koinViewModel

private val VerdeHuata = HuataColores.Verde
private val AzulPago   = HuataColores.Azul

/**
 * Pantalla de Pagos para el Administrador.
 *
 * FLUJO:
 * 1. El acopiador registra litros diariamente → se guardan en Laravel
 * 2. El admin ve la liquidación de la semana calculada por el backend (PaymentService)
 * 3. Genera el pago con un clic; Laravel lo guarda con bonos y descuentos
 * 4. El productor ve el pago en su app y en la web
 */
@Composable
fun PagosAdminScreen(
    onVolver: () -> Unit,
    modifier: Modifier = Modifier
) {
    var tabActual by remember { mutableStateOf(0) }
    val tabs = listOf("Resumen litros", "Pagos generados", "Acopiadores")
    val viewModel: PagosAdminViewModel = koinViewModel()
    val envio by viewModel.envio.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize()) {

        // El título está en la barra superior; aquí solo el resumen
        Text(
            "Precio: S/ ${ConfiguracionPlanta.precioPorLitro}/L · Pago cada viernes",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
        )

        TabRow(selectedTabIndex = tabActual) {
            tabs.forEachIndexed { i, t ->
                Tab(selected = tabActual == i, onClick = { tabActual = i }, text = { Text(t) })
            }
        }

        when (val actual = envio) {
            is EstadoEnvio.Exito -> MensajePago(actual.mensaje, VerdeHuata, viewModel::limpiarEnvio)
            is EstadoEnvio.Error -> MensajePago(actual.mensaje, HuataColores.Rojo, viewModel::limpiarEnvio)
            else -> Unit
        }

        when (tabActual) {
            0 -> ResumenLitrosTab(viewModel)
            1 -> PagosGeneradosTab(viewModel)
            2 -> PagosAcopiadoresTab(viewModel)
        }
    }
}

// ── TAB 1: RESUMEN DE LITROS (liquidación de la semana calculada por Laravel) ──

@Composable
private fun ResumenLitrosTab(viewModel: PagosAdminViewModel) {
    val estado by viewModel.vistaPrevia.collectAsStateWithLifecycle()
    val envio by viewModel.envio.collectAsStateWithLifecycle()
    if (estado !is EstadoUi.Exito) {
        CargaOError(estado, onReintentar = viewModel::cargar)
        return
    }
    val filas = estado.datosOVacio()
    val conEntregas = filas.filter { it.puedeGenerar }
    val totalLitros = conEntregas.sumOf { it.litros }
    val totalPagar = conEntregas.sumOf { it.total }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = VerdeHuata.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("📊 Semana ${fechaCorta(viewModel.inicioSemana)} — ${fechaCorta(viewModel.finSemana)}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total litros recolectados:", style = MaterialTheme.typography.bodyMedium)
                        Text("$totalLitros L", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total a pagar (${conEntregas.size} productores):", style = MaterialTheme.typography.bodyMedium)
                        Text(soles(totalPagar), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = VerdeHuata)
                    }
                    Text("Incluye bonos y descuentos calculados por el servidor", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        item {
            Text("Liquidación por productor", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        }

        if (conEntregas.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().height(120.dp), contentAlignment = Alignment.Center) {
                    Text("No hay entregas registradas esta semana", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            items(conEntregas, key = { it.idProductor }) { fila ->
                TarjetaVistaPrevia(fila, enviando = envio == EstadoEnvio.Enviando) { viewModel.generarPago(fila) }
            }
        }
    }
}

@Composable
private fun TarjetaVistaPrevia(fila: VistaPreviaPago, enviando: Boolean, onGenerar: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(fila.nombreProductor, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Text(fila.comunidad, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Precio: S/ ${fila.precioPorLitro}/L", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("${fila.litros} L", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = VerdeHuata)
                    Text(soles(fila.total), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = AzulPago)
                }
            }
            Spacer(Modifier.height(8.dp))
            if (fila.yaGenerado) {
                Text("✅ Pago ya generado para esta semana", style = MaterialTheme.typography.bodySmall, color = VerdeHuata)
            } else {
                Button(
                    onClick = onGenerar,
                    enabled = !enviando,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = VerdeHuata)
                ) { Text("Generar pago: ${soles(fila.total)}", fontWeight = FontWeight.Bold) }
            }
        }
    }
}

// ── TAB 2: PAGOS GENERADOS ────────────────────────────────────────────────

@Composable
private fun PagosGeneradosTab(viewModel: PagosAdminViewModel) {
    val estado by viewModel.pagos.collectAsStateWithLifecycle()
    val envio by viewModel.envio.collectAsStateWithLifecycle()
    if (estado !is EstadoUi.Exito) {
        CargaOError(estado, onReintentar = viewModel::cargar)
        return
    }
    val pagos = estado.datosOVacio()
    val pendientes = pagos.count { it.estado == EstadoPago.PENDIENTE }
    val totalPendiente = pagos.filter { it.estado == EstadoPago.PENDIENTE }.sumOf { it.totalCalculado }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                TarjetaResumenPago("⏳", "$pendientes", "Pendientes", HuataColores.NaranjaOscuro, Modifier.weight(1f))
                TarjetaResumenPago("💰", soles(totalPendiente), "Por pagar", VerdeHuata, Modifier.weight(1f))
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
            items(pagos, key = { it.id }) { pago ->
                TarjetaPagoProductor(
                    pago = pago,
                    enviando = envio == EstadoEnvio.Enviando,
                    onMarcarPagado = { viewModel.marcarPagado(pago) }
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
private fun TarjetaPagoProductor(pago: Pago, enviando: Boolean, onMarcarPagado: () -> Unit) {
    val colorEstado = when (pago.estado) {
        EstadoPago.PAGADO    -> VerdeHuata
        EstadoPago.PENDIENTE -> HuataColores.NaranjaOscuro
        EstadoPago.PARCIAL   -> HuataColores.DoradoOscuro
    }

    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(pago.nombreProductor ?: "Productor", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
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

            if (pago.detalleDiario.isNotEmpty()) {
                Text("Detalle de entregas:", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                pago.detalleDiario.entries.take(5).forEach { (dia, litrosDia) ->
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 1.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(dia, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                Text(soles(pago.totalCalculado), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = VerdeHuata)
            }

            if (pago.estado == EstadoPago.PENDIENTE) {
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = onMarcarPagado,
                    enabled = !enviando,
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

// ── TAB 3: SUELDOS DE ACOPIADORES (planilla mensual del backend) ──────────

@Composable
private fun PagosAcopiadoresTab(viewModel: PagosAdminViewModel) {
    val estado by viewModel.pagosAcopiadores.collectAsStateWithLifecycle()
    val envio by viewModel.envio.collectAsStateWithLifecycle()
    if (estado !is EstadoUi.Exito) {
        CargaOError(estado, onReintentar = viewModel::cargar)
        return
    }
    val pagos = estado.datosOVacio()

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
                    Text("ℹ️ Planilla de acopiadores · ${viewModel.mesActual}", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Text("Cada acopiador cobra el sueldo mensual definido por el administrador.", style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = viewModel::generarPlanilla,
                        enabled = envio != EstadoEnvio.Enviando,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = AzulPago)
                    ) { Text("Generar planilla del mes") }
                }
            }
        }

        if (pagos.isEmpty()) {
            item {
                Box(Modifier.fillMaxWidth().height(120.dp), contentAlignment = Alignment.Center) {
                    Text("Aún no se generó la planilla de este mes", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        items(pagos, key = { it.id }) { pago ->
            TarjetaPagoAcopiador(pago, enviando = envio == EstadoEnvio.Enviando) { viewModel.marcarPagadoAcopiador(pago) }
        }
    }
}

@Composable
private fun TarjetaPagoAcopiador(pago: PagoAcopiador, enviando: Boolean, onMarcarPagado: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(pago.nombreAcopiador, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("${pago.entregas} entregas · ${pago.litrosRecolectados} L recolectados", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Periodo: ${pago.periodo}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(soles(pago.total), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = AzulPago)
                    Text(pago.estado.etiqueta, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Spacer(Modifier.height(8.dp))
            if (pago.estado == EstadoPago.PAGADO) {
                Text("Pagado el: ${pago.fechaPago.orEmpty()}", style = MaterialTheme.typography.bodySmall, color = VerdeHuata)
            } else {
                Button(
                    onClick = onMarcarPagado,
                    enabled = !enviando,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = AzulPago)
                ) { Text("Registrar pago al acopiador") }
            }
        }
    }
}

@Composable
private fun MensajePago(mensaje: String, color: Color, onCerrar: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(mensaje, color = color, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
        TextButton(onClick = onCerrar) { Text("✕") }
    }
}

private fun soles(monto: Double): String {
    val centimos = kotlin.math.round(monto * 100).toLong()
    return "S/ ${centimos / 100}.${(centimos % 100).toString().padStart(2, '0')}"
}

/** "2026-10-12" -> "12/10". */
private fun fechaCorta(fechaIso: String): String = fechaIso.split("-").let { if (it.size == 3) "${it[2]}/${it[1]}" else fechaIso }
