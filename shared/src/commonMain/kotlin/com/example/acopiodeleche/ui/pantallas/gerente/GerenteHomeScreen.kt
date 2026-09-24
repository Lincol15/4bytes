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
import com.example.acopiodeleche.domain.model.EstadoPago
import com.example.acopiodeleche.domain.model.ResultadoCalidad
import com.example.acopiodeleche.domain.model.SesionActual
import com.example.acopiodeleche.domain.model.TipoProducto
import com.example.acopiodeleche.ui.pantallas.productores.ProductoresScreen
import kotlinx.coroutines.launch

private val AzulGerente = Color(0xFF1A237E)
private val VerdeHuata  = Color(0xFF2E7D32)
private val AmarilloDash = Color(0xFFE65100)

// ── Ítems del menú lateral ─────────────────────────────────────────────────

private data class ItemMenuGerente(val icono: String, val titulo: String, val indice: Int)

private val MENU_GERENTE = listOf(
    ItemMenuGerente("🏠", "Dashboard",    0),
    ItemMenuGerente("🌾", "Productores",  1),
    ItemMenuGerente("🥛", "Acopio",       2),
    ItemMenuGerente("🔬", "Calidad",      3),
    ItemMenuGerente("💳", "Pagos",        4),
    ItemMenuGerente("🧀", "Producción",   5),
    ItemMenuGerente("📦", "Ventas",       6)
)

@Composable
fun GerenteHomeScreen(
    onCerrarSesion: () -> Unit,
    modifier: Modifier = Modifier
) {
    val usuario       = SesionActual.usuario
    var seccionActual by remember { mutableStateOf(0) }
    val drawerState   = rememberDrawerState(DrawerValue.Closed)
    val scope         = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier             = Modifier.width(260.dp),
                drawerContainerColor = MaterialTheme.colorScheme.surface
            ) {
                // ── Cabecera con perfil ────────────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AzulGerente)
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .background(Color.White.copy(0.2f), RoundedCornerShape(50)),
                            contentAlignment = Alignment.Center
                        ) { Text("📊", fontSize = 26.sp) }
                        Spacer(Modifier.height(10.dp))
                        Text(
                            usuario?.nombreCompleto ?: "Gerente",
                            style      = MaterialTheme.typography.titleSmall,
                            color      = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Gerente General",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(0.75f)
                        )
                        Text(
                            "Ecolácteos Huata",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(0.6f)
                        )
                        if (!usuario?.correo.isNullOrBlank()) {
                            Spacer(Modifier.height(2.dp))
                            Text(
                                usuario!!.correo,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(0.5f)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                // ── Ítems de navegación ────────────────────────────────────
                MENU_GERENTE.forEach { item ->
                    val seleccionado = seccionActual == item.indice
                    NavigationDrawerItem(
                        icon  = { Text(item.icono, fontSize = 18.sp) },
                        label = {
                            Text(
                                item.titulo,
                                fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        selected = seleccionado,
                        onClick  = {
                            seccionActual = item.indice
                            scope.launch { drawerState.close() }
                        },
                        colors   = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = AzulGerente.copy(0.12f),
                            selectedTextColor      = AzulGerente,
                            selectedIconColor      = AzulGerente
                        ),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                    )
                }

                // ── Cerrar sesión al fondo ─────────────────────────────────
                Spacer(Modifier.weight(1f))
                HorizontalDivider(modifier = Modifier.padding(horizontal = 12.dp))
                NavigationDrawerItem(
                    icon     = { Text("🚪", fontSize = 18.sp) },
                    label    = { Text("Cerrar sesión", color = Color(0xFFB71C1C)) },
                    selected = false,
                    onClick  = {
                        scope.launch { drawerState.close() }
                        onCerrarSesion()
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
                Spacer(Modifier.height(12.dp))
            }
        },
        modifier = modifier
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── TopBar ────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AzulGerente)
                    .padding(horizontal = 8.dp, vertical = 12.dp)
            ) {
                // Botón hamburguesa
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .size(44.dp)
                        .clickable { scope.launch { drawerState.open() } }
                        .background(Color.White.copy(0.15f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier            = Modifier.padding(8.dp)
                    ) {
                        repeat(3) {
                            Box(
                                modifier = Modifier
                                    .width(20.dp)
                                    .height(2.dp)
                                    .background(Color.White, RoundedCornerShape(1.dp))
                            )
                        }
                    }
                }

                Column(
                    modifier            = Modifier.align(Alignment.Center).padding(horizontal = 56.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val item = MENU_GERENTE.find { it.indice == seccionActual }
                    Text(
                        "${item?.icono ?: ""} ${item?.titulo ?: "Gerencia"}",
                        style      = MaterialTheme.typography.titleMedium,
                        color      = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text("Ecolácteos Huata", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(0.75f))
                }
            }

            // ── Contenido ─────────────────────────────────────────────────
            Box(modifier = Modifier.fillMaxSize()) {
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
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// DASHBOARD
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun DashboardGerente() {
    val registros   = DatosMock.registrosAcopio
    val productores = DatosMock.productores
    val lotes       = DatosMock.lotesProduccion
    val ventas      = DatosMock.ventasSalidas
    val pagos       = DatosMock.pagos
    val analisis    = DatosMock.analisisCalidad

    val totalVentas   = ventas.sumOf { it.total }
    val litrosAcopio  = registros.sumOf { it.litros }
    val pagosRend     = pagos.count { it.estado == EstadoPago.PENDIENTE }

    LazyColumn(
        contentPadding      = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Resumen ejecutivo", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("Vista gerencial — Ecolácteos Huata", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        // KPIs fila 1
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                TarjetaKpi("🥛", "%.1f L".format(litrosAcopio), "Litros acopiados", AzulGerente, Modifier.weight(1f))
                TarjetaKpi("👥", "${productores.count { it.estado }}", "Productores activos", VerdeHuata, Modifier.weight(1f))
            }
        }
        // KPIs fila 2
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                TarjetaKpi("🧀", "${lotes.size}", "Lotes producidos", AmarilloDash, Modifier.weight(1f))
                TarjetaKpi("💰", "S/ ${totalVentas.toLong()}", "Total ventas", Color(0xFF6A1B9A), Modifier.weight(1f))
            }
        }
        // KPIs fila 3
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                TarjetaKpi("🔬", "${analisis.size}", "Análisis calidad", Color(0xFF00838F), Modifier.weight(1f))
                TarjetaKpi("💳", "$pagosRend", "Pagos pendientes", if (pagosRend > 0) Color(0xFFC62828) else VerdeHuata, Modifier.weight(1f))
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
                        "S/ ${ConfiguracionPlanta.precioPorLitro}",
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
private fun TarjetaKpi(icono: String, valor: String, etiqueta: String, color: Color, modifier: Modifier) {
    Card(
        modifier = modifier,
        colors   = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        shape    = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(icono, fontSize = 26.sp)
            Text(valor, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color)
            Text(etiqueta, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// CALIDAD
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun CalidadGerenteTab() {
    val analisis    = DatosMock.analisisCalidad
    val productores = DatosMock.productores

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
        items(analisis.reversed()) { cal ->
            val productor = productores.find { it.idProductor == cal.idProductor }
            val colorRes  = when (cal.resultado) {
                ResultadoCalidad.APTO      -> VerdeHuata
                ResultadoCalidad.NO_APTO   -> Color(0xFFC62828)
                ResultadoCalidad.OBSERVADO -> Color(0xFFF9A825)
                else                       -> Color.Gray
            }
            Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("🔬", fontSize = 22.sp)
                    Spacer(Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(productor?.nombreCompleto ?: "Productor", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
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
private fun PagosGerenteTab() {
    val pagos       = DatosMock.pagos
    val productores = DatosMock.productores

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
                TarjetaKpi("⏳", "S/ ${totalPend.toLong()}", "Pendiente", Color(0xFFE65100), Modifier.weight(1f))
            }
        }
        item { HorizontalDivider() }
        items(pagos.reversed()) { pago ->
            val productor  = productores.find { it.idProductor == pago.idProductor }
            val colorEstado = if (pago.estado == EstadoPago.PAGADO) VerdeHuata else Color(0xFFE65100)
            Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("💳", fontSize = 22.sp)
                    Spacer(Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(productor?.nombreCompleto ?: "Productor", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        Text("${pago.periodoDesde} – ${pago.periodoHasta} · ${pago.litrosTotales} L", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(pago.codigoPago, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("S/ ${"%.2f".format(pago.totalCalculado)}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = VerdeHuata)
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
                TarjetaKpi("🧀", "$totalQueso kg", "Queso total", Color(0xFFE65100), Modifier.weight(1f))
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
                    Text(lote.cantidadFormateada, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
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
                            colors   = CardDefaults.cardColors(containerColor = Color(0xFFE65100).copy(0.08f)),
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
