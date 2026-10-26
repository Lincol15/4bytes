package com.example.acopiodeleche.ui.pantallas.gerente

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
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.acopiodeleche.domain.model.ConfiguracionPlanta
import com.example.acopiodeleche.domain.model.DatosMock
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.acopiodeleche.ui.estado.CargaOError
import com.example.acopiodeleche.ui.estado.EstadoUi
import com.example.acopiodeleche.ui.estado.datosOVacio
import com.example.acopiodeleche.domain.model.EstadoPago
import com.example.acopiodeleche.domain.model.ResultadoCalidad
import com.example.acopiodeleche.domain.model.SesionActual
import com.example.acopiodeleche.domain.model.TipoProducto
import com.example.acopiodeleche.ui.pantallas.productores.ProductoresScreen
import kotlinx.coroutines.launch
import com.example.acopiodeleche.ui.tema.EsqueletoRol
import com.example.acopiodeleche.ui.tema.HuataColores
import com.example.acopiodeleche.ui.tema.OpcionMenu
import com.example.acopiodeleche.ui.tema.TarjetaEstadistica
import com.example.acopiodeleche.ui.tema.formatoLitros
import org.koin.compose.viewmodel.koinViewModel

private val AzulGerente = HuataColores.Verde
private val VerdeHuata  = HuataColores.Verde
private val AmarilloDash = HuataColores.NaranjaOscuro

/** Formatea un Double con 2 decimales — alternativa KMP a "%.2f".format(x). */
private fun Double.fmt2(): String {
    val entero = toLong()
    val decimal = ((this - entero) * 100).toLong().let { if (it < 0) -it else it }
    return "$entero.${decimal.toString().padStart(2, '0')}"
}

// ── Ítems del menú lateral ─────────────────────────────────────────────────


private val MENU_GERENTE = listOf(
    OpcionMenu("🏠", "Dashboard"),
    OpcionMenu("🌾", "Productores", "Gestión"),
    OpcionMenu("🥛", "Acopio", "Gestión"),
    OpcionMenu("🔬", "Calidad", "Gestión"),
    OpcionMenu("💳", "Pagos", "Gestión"),
    OpcionMenu("🧀", "Producción", "Gestión"),
    OpcionMenu("📦", "Ventas", "Comercial")
)

@Composable
fun GerenteHomeScreen(
    onCerrarSesion: () -> Unit,
    modifier: Modifier = Modifier
) {
    var seccionActual by remember { mutableStateOf(0) }

    EsqueletoRol(
        opciones = MENU_GERENTE,
        seleccion = seccionActual,
        onSeleccionar = { seccionActual = it },
        onCerrarSesion = onCerrarSesion,
        subtitulo = if (seccionActual == 0) "Indicadores de la planta (solo lectura)" else null,
        modifier = modifier
    ) {
        when (seccionActual) {
            0 -> DashboardGerente()
            1 -> ProductoresScreen(soloLectura = true)
            2 -> AcopioGerenteTab()
            3 -> CalidadGerenteTab()
            4 -> PagosGerenteTab()
            5 -> ProduccionGerenteTab()
            6 -> VentasGerenteTab()
        }
    }
}

@Composable
private fun DashboardGerente(viewModel: GerenteViewModel = koinViewModel()) {
    // Indicadores calculados por Laravel (mismos que el panel web)
    val estadoResumen  by viewModel.resumen.collectAsStateWithLifecycle()
    val estadoAnalisis by viewModel.analisis.collectAsStateWithLifecycle()
    val estadoPagos    by viewModel.pagos.collectAsStateWithLifecycle()
    val resumen = (estadoResumen as? EstadoUi.Exito)?.datos
    if (resumen == null) {
        CargaOError(estadoResumen, onReintentar = viewModel::cargar)
        return
    }
    val ventas      = DatosMock.ventasSalidas
    val analisis    = estadoAnalisis.datosOVacio()
    val pagosRend   = estadoPagos.datosOVacio().count { it.estado == EstadoPago.PENDIENTE }

    LazyColumn(
        contentPadding      = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // KPIs fila 1
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                TarjetaKpi("🥛", formatoLitros(resumen.litrosMes), "Litros del mes", AzulGerente, Modifier.weight(1f))
                TarjetaKpi("👥", "${resumen.productoresActivos}", "Productores activos", VerdeHuata, Modifier.weight(1f))
            }
        }
        // KPIs fila 2
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                TarjetaKpi("🧀", "${resumen.lotesActivos}", "Lotes en proceso", AmarilloDash, Modifier.weight(1f))
                TarjetaKpi("💰", "S/ ${resumen.ventasMes.toLong()}", "Ventas del mes", HuataColores.Morado, Modifier.weight(1f))
            }
        }
        // KPIs fila 3
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                TarjetaKpi("🔬", "${analisis.size}", "Análisis calidad", HuataColores.Cian, Modifier.weight(1f))
                TarjetaKpi("💳", "$pagosRend", "Pagos pendientes", if (pagosRend > 0) HuataColores.Rojo else VerdeHuata, Modifier.weight(1f))
            }
        }

        // Precio vigente
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors   = CardDefaults.cardColors(containerColor = AzulGerente.copy(0.07f)),
                shape    = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier              = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Column {
                        Text("💲 Precio vigente por litro", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        Text("Ecolácteos Huata · temporada actual", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text(
                        "S/ ${resumen.precioLitro}",
                        style      = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color      = VerdeHuata
                    )
                }
            }
        }

        // Últimas ventas
        if (ventas.isNotEmpty()) {
            item {
                HorizontalDivider()
                Spacer(Modifier.height(4.dp))
                Text("Últimas ventas", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            }
            items(ventas.takeLast(3).reversed()) { venta ->
                Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(1.dp)) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(venta.producto.icono, fontSize = 22.sp)
                        Spacer(Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(venta.cliente, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                            Text("${venta.cantidadFormateada} · ${venta.fecha}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text(venta.totalFormateado, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = VerdeHuata)
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaKpi(icono: String, valor: String, etiqueta: String, color: Color, modifier: Modifier) =
    TarjetaEstadistica(etiqueta, valor, icono, color, modifier)

// ─────────────────────────────────────────────────────────────────────────────
// CALIDAD
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun CalidadGerenteTab(viewModel: GerenteViewModel = koinViewModel()) {
    val estado by viewModel.analisis.collectAsStateWithLifecycle()
    if (estado !is EstadoUi.Exito) {
        CargaOError(estado, onReintentar = viewModel::cargar)
        return
    }
    val analisis = estado.datosOVacio()

    if (analisis.isEmpty()) {
        EmptyState("🔬", "Sin análisis de calidad", "El personal de calidad aún no ha registrado análisis")
        return
    }

    LazyColumn(
        contentPadding      = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text("Análisis de calidad", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("${analisis.size} análisis · ${analisis.count { it.resultado == ResultadoCalidad.APTO }} aptos · ${analisis.count { it.resultado == ResultadoCalidad.NO_APTO }} no aptos",
                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        items(analisis) { cal ->
            val colorRes  = when (cal.resultado) {
                ResultadoCalidad.APTO      -> VerdeHuata
                ResultadoCalidad.NO_APTO   -> HuataColores.Rojo
                ResultadoCalidad.OBSERVADO -> HuataColores.DoradoOscuro
                else                       -> Color.Gray
            }
            Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("🔬", fontSize = 22.sp)
                    Spacer(Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(cal.nombreProductor ?: "Productor", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        Text("${cal.fecha} ${cal.hora} · ${cal.codigoAnalisis}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Box(
                        modifier = Modifier
                            .background(colorRes.copy(0.12f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(cal.resultado.etiqueta, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = colorRes)
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// PAGOS
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun PagosGerenteTab(viewModel: GerenteViewModel = koinViewModel()) {
    val estado by viewModel.pagos.collectAsStateWithLifecycle()
    if (estado !is EstadoUi.Exito) {
        CargaOError(estado, onReintentar = viewModel::cargar)
        return
    }
    val pagos = estado.datosOVacio()

    if (pagos.isEmpty()) {
        EmptyState("💳", "Sin pagos registrados", "El administrador aún no ha generado pagos")
        return
    }

    val totalPagado  = pagos.filter { it.estado == EstadoPago.PAGADO }.sumOf { it.totalCalculado }
    val totalPend    = pagos.filter { it.estado == EstadoPago.PENDIENTE }.sumOf { it.totalCalculado }

    LazyColumn(
        contentPadding      = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text("Gestión de pagos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                TarjetaKpi("✅", "S/ ${totalPagado.toLong()}", "Pagado", VerdeHuata, Modifier.weight(1f))
                TarjetaKpi("⏳", "S/ ${totalPend.toLong()}", "Pendiente", HuataColores.NaranjaOscuro, Modifier.weight(1f))
            }
        }
        item { HorizontalDivider() }
        items(pagos) { pago ->
            val colorEstado = if (pago.estado == EstadoPago.PAGADO) VerdeHuata else HuataColores.NaranjaOscuro
            Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("💳", fontSize = 22.sp)
                    Spacer(Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(pago.nombreProductor ?: "Productor", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        Text("${pago.periodoDesde} – ${pago.periodoHasta} · ${pago.litrosTotales} L", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(pago.codigoPago, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("S/ ${pago.totalCalculado.fmt2()}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = VerdeHuata)
                        Text(pago.estado.name, style = MaterialTheme.typography.labelSmall, color = colorEstado, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// PRODUCCIÓN (solo lectura)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ProduccionGerenteTab() {
    val lotes = DatosMock.lotesProduccion

    if (lotes.isEmpty()) {
        EmptyState("🧀", "Sin lotes de producción", "El trabajador de planta aún no registró lotes")
        return
    }

    val totalQueso = lotes.filter { it.producto == TipoProducto.QUESO }.sumOf { it.cantidadProducida }
    val totalYogur = lotes.filter { it.producto == TipoProducto.YOGUR }.sumOf { it.cantidadProducida }

    LazyColumn(
        contentPadding      = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text("Producción registrada", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("${lotes.size} lotes en total", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                TarjetaKpi("🧀", "$totalQueso kg", "Queso total", HuataColores.NaranjaOscuro, Modifier.weight(1f))
                TarjetaKpi("🥛", "$totalYogur L", "Yogur total", AzulGerente, Modifier.weight(1f))
            }
        }
        item { HorizontalDivider() }
        items(lotes.reversed()) { lote ->
            Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(lote.producto.icono, fontSize = 26.sp)
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(lote.codigoLote, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        Text("${lote.fecha} · ${lote.litrosUsados} L usados", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        if (!lote.observacion.isNullOrBlank())
                            Text("📝 ${lote.observacion}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text(lote.cantidadFormateada, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = HuataColores.NaranjaOscuro)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// VENTAS (solo lectura)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun VentasGerenteTab() {
    val ventas      = DatosMock.ventasSalidas
    val totalVentas = ventas.sumOf { it.total }

    if (ventas.isEmpty()) {
        EmptyState("📦", "Sin ventas registradas", "El trabajador de planta aún no registró ventas")
        return
    }

    // Resumen por producto
    val resumenPorProducto = TipoProducto.entries.mapNotNull { tipo ->
        val ventasTipo = ventas.filter { it.producto == tipo }
        if (ventasTipo.isEmpty()) null
        else Triple(tipo, ventasTipo.sumOf { it.cantidad }, ventasTipo.sumOf { it.total })
    }

    LazyColumn(
        contentPadding      = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text("Ventas / Salidas", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        // Total general
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors   = CardDefaults.cardColors(containerColor = VerdeHuata.copy(0.1f)),
                shape    = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier              = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Total de ventas", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        Text("${ventas.size} ventas registradas", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text(
                        "S/ ${totalVentas.toLong()}.${"${((totalVentas - totalVentas.toLong()) * 100).toLong()}".padStart(2, '0')}",
                        style      = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color      = VerdeHuata
                    )
                }
            }
        }
        // Resumen por tipo
        if (resumenPorProducto.size > 1) {
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    resumenPorProducto.forEach { (tipo, cant, tot) ->
                        Card(
                            modifier = Modifier.weight(1f),
                            colors   = CardDefaults.cardColors(containerColor = HuataColores.NaranjaOscuro.copy(0.08f)),
                            shape    = RoundedCornerShape(10.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(tipo.icono, fontSize = 20.sp)
                                Text("S/ ${tot.toLong()}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = VerdeHuata)
                                Text("$cant ${if (tipo == TipoProducto.YOGUR) "L" else "kg"}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }

        item { HorizontalDivider() }
        items(ventas.reversed()) { venta ->
            Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(venta.producto.icono, fontSize = 26.sp)
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(venta.cliente, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        Text("${venta.producto.etiqueta} · ${venta.cantidadFormateada} · ${venta.fecha}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text(venta.totalFormateado, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = VerdeHuata)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// HELPER
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun EmptyState(icono: String, titulo: String, subtitulo: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(icono, fontSize = 52.sp)
            Spacer(Modifier.height(8.dp))
            Text(titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(subtitulo, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
