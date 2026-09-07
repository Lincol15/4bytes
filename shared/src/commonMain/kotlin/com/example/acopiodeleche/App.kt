package com.example.acopiodeleche

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.acopiodeleche.domain.model.Rol
import com.example.acopiodeleche.domain.model.SesionActual
import com.example.acopiodeleche.domain.model.Usuario
import com.example.acopiodeleche.domain.model.ConfiguracionPlanta
import com.example.acopiodeleche.ui.auth.LoginScreen
import com.example.acopiodeleche.ui.pantallas.acopio.AcopioScreen
import com.example.acopiodeleche.ui.pantallas.acopiador.AcopiadorHomeScreen
import com.example.acopiodeleche.ui.pantallas.admin.AdminHomeScreen
import com.example.acopiodeleche.ui.pantallas.calidad.CalidadHomeScreen
import com.example.acopiodeleche.ui.pantallas.productor.ProductorHomeScreen
import com.example.acopiodeleche.ui.pantallas.planta.TrabajadorPlantaScreen
import com.example.acopiodeleche.ui.pantallas.productores.ProductoresScreen

@Composable
fun App() {
    MaterialTheme {
        // Estado de sesión — null = no logueado
        var sesion by remember { mutableStateOf<Usuario?>(null) }

        if (sesion == null) {
            // ── Pantalla de Login ──────────────────────────────────
            LoginScreen(
                onLoginExitoso = { usuario -> sesion = usuario }
            )
        } else {
            // ── Navegar según el rol ───────────────────────────────
            val cerrarSesion: () -> Unit = {
                SesionActual.cerrar()
                sesion = null
            }

            when (sesion!!.rol) {
                Rol.ADMINISTRADOR -> AdminHomeScreen(onCerrarSesion = cerrarSesion)
                Rol.GERENTE       -> GerenteHomeScreen(onCerrarSesion = cerrarSesion)
                Rol.ACOPIADOR     -> AcopiadorHomeScreen(onCerrarSesion = cerrarSesion)
                Rol.PRODUCTOR     -> ProductorHomeScreen(onCerrarSesion = cerrarSesion)
                Rol.CONTROL_CALIDAD -> CalidadHomeScreen(onCerrarSesion = cerrarSesion)
                Rol.TRABAJADOR_PLANTA -> TrabajadorPlantaScreen(onCerrarSesion = cerrarSesion)
            }
        }
    }
}

// ── Pantalla Gerente (simplificada — se expande en Fase 10) ───────────────
private val VerdeHuata = Color(0xFF2E7D32)
private val AzulGerente = Color(0xFF1A237E)

@Composable
fun GerenteHomeScreen(
    onCerrarSesion: () -> Unit,
    modifier: Modifier = Modifier
) {
    val usuario = SesionActual.usuario
    var tabActual by remember { mutableStateOf(0) }
    val tabs = listOf("Inicio", "Product.", "Acopio", "Calidad", "Pagos", "Produc.", "Ventas")

    Column(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(AzulGerente)
                .padding(16.dp)
        ) {
            Column {
                Text("Dashboard Gerente", style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.Bold)
                Text("Ecolácteos Huata · ${usuario?.nombreCompleto ?: "Gerente"}", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.8f))
            }
            Text(
                text = "Salir",
                modifier = Modifier.align(Alignment.TopEnd).clickable { onCerrarSesion() }.padding(4.dp),
                color = Color.White.copy(alpha = 0.8f),
                style = MaterialTheme.typography.labelMedium
            )
        }

        TabRow(selectedTabIndex = tabActual) {
            tabs.forEachIndexed { i, t ->
                Tab(selected = tabActual == i, onClick = { tabActual = i }, text = { Text(t) })
            }
        }

        when (tabActual) {
            0 -> DashboardGerente()
            1 -> ProductoresScreen(soloLectura = true)
            2 -> AcopioScreen()
            3 -> CalidadGerenteTab()
            4 -> PagosGerenteTab()
            5 -> ProduccionGerenteTab()
            6 -> VentasGerenteTab()
        }
    }
}

@Composable
private fun DashboardGerente() {
    val registros  = com.example.acopiodeleche.domain.model.DatosMock.registrosAcopio
    val productores = com.example.acopiodeleche.domain.model.DatosMock.productores
    val lotes      = com.example.acopiodeleche.domain.model.DatosMock.lotesProduccion
    val ventas     = com.example.acopiodeleche.domain.model.DatosMock.ventasSalidas
    val pagos      = com.example.acopiodeleche.domain.model.DatosMock.pagos

    androidx.compose.foundation.lazy.LazyColumn(
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Text("Resumen del negocio", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TarjetaGerente("🥛", "${registros.sumOf { it.litros }} L", "Litros acopiados", AzulGerente, Modifier.weight(1f))
                TarjetaGerente("👥", "${productores.count { it.estado }}", "Productores activos", VerdeHuata, Modifier.weight(1f))
            }
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TarjetaGerente("🧀", "${lotes.size}", "Lotes producidos", Color(0xFFE65100), Modifier.weight(1f))
                TarjetaGerente("💰", "S/ ${ventas.sumOf { it.total }.let { t -> "${t.toLong()}.${((t - t.toLong()) * 100).toLong().toString().padStart(2,'0')}" }}", "Total ventas", Color(0xFF6A1B9A), Modifier.weight(1f))
            }
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TarjetaGerente("📦", "${registros.size}", "Registros acopio", Color(0xFF00838F), Modifier.weight(1f))
                TarjetaGerente("💳", "${pagos.count { it.estado == com.example.acopiodeleche.domain.model.EstadoPago.PENDIENTE }}", "Pagos pendientes", Color(0xFFE65100), Modifier.weight(1f))
            }
        }
        item {
            Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Precio vigente por litro", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    androidx.compose.foundation.layout.Spacer(Modifier.height(4.dp))
                    Text(
                        text = "S/ ${com.example.acopiodeleche.domain.model.ConfiguracionPlanta.precioPorLitro} / litro",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = VerdeHuata
                    )
                }
            }
        }
    }
}

@Composable
private fun TarjetaGerente(icono: String, valor: String, etiqueta: String, color: Color, modifier: Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)), shape = RoundedCornerShape(12.dp)) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(icono, fontSize = 26.sp)
            Text(valor, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color)
            Text(etiqueta, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

// ── Producción y Ventas para Gerente ─────────────────────────────────────

@Composable
private fun ProduccionGerenteTab() {
    val lotes = com.example.acopiodeleche.domain.model.DatosMock.lotesProduccion
    if (lotes.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🧀", fontSize = 48.sp)
                androidx.compose.foundation.layout.Spacer(Modifier.height(8.dp))
                Text("No hay lotes de producción registrados", style = MaterialTheme.typography.titleMedium)
            }
        }
        return
    }
    androidx.compose.foundation.lazy.LazyColumn(
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text("Producción registrada", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(
                "${lotes.size} lotes · ${lotes.count { it.producto == com.example.acopiodeleche.domain.model.TipoProducto.QUESO }} queso · ${lotes.count { it.producto == com.example.acopiodeleche.domain.model.TipoProducto.YOGUR }} yogur",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        items(lotes) { lote ->
            Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(lote.producto.icono, fontSize = 28.sp)
                    androidx.compose.foundation.layout.Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(lote.codigoLote, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text("${lote.fecha} · ${lote.litrosUsados} L usados", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text(lote.cantidadFormateada, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                }
            }
        }
    }
}

@Composable
private fun VentasGerenteTab() {
    val ventas = com.example.acopiodeleche.domain.model.DatosMock.ventasSalidas
    val totalVentas = ventas.sumOf { it.total }
    if (ventas.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("📦", fontSize = 48.sp)
                androidx.compose.foundation.layout.Spacer(Modifier.height(8.dp))
                Text("No hay ventas registradas", style = MaterialTheme.typography.titleMedium)
            }
        }
        return
    }
    androidx.compose.foundation.lazy.LazyColumn(
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text("Ventas / Salidas", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(
                "${ventas.size} ventas · Total: S/ ${totalVentas.let { t -> "${t.toLong()}.${((t - t.toLong()) * 100).toLong().toString().padStart(2,'0')}" }}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        items(ventas) { venta ->
            Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(venta.producto.icono, fontSize = 28.sp)
                    androidx.compose.foundation.layout.Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(venta.cliente, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text("${venta.producto.etiqueta} · ${venta.cantidadFormateada} · ${venta.fecha}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text(venta.totalFormateado, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = VerdeHuata)
                }
            }
        }
    }
}

// ── Calidad y Pagos para Gerente ──────────────────────────────────────────

@Composable
private fun CalidadGerenteTab() {
    val analisis = com.example.acopiodeleche.domain.model.DatosMock.analisisCalidad
    val productores = com.example.acopiodeleche.domain.model.DatosMock.productores

    if (analisis.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🔬", fontSize = 48.sp)
                androidx.compose.foundation.layout.Spacer(Modifier.height(8.dp))
                Text("No hay análisis de calidad registrados", style = MaterialTheme.typography.titleMedium)
            }
        }
        return
    }
    androidx.compose.foundation.lazy.LazyColumn(
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text("Análisis de calidad", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("${analisis.size} análisis · ${analisis.count { it.resultado == com.example.acopiodeleche.domain.model.ResultadoCalidad.APTO }} aptos",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        items(analisis) { cal ->
            val productor = productores.find { it.idProductor == cal.idProductor }
            Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("🔬", fontSize = 24.sp)
                    androidx.compose.foundation.layout.Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(productor?.nombreCompleto ?: "Productor", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        Text("${cal.fecha} ${cal.hora} · ${cal.codigoAnalisis}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text(
                        text = cal.resultado.etiqueta,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = when (cal.resultado) {
                            com.example.acopiodeleche.domain.model.ResultadoCalidad.APTO -> VerdeHuata
                            com.example.acopiodeleche.domain.model.ResultadoCalidad.NO_APTO -> Color.Red
                            com.example.acopiodeleche.domain.model.ResultadoCalidad.OBSERVADO -> Color(0xFFF9A825)
                            else -> Color.Gray
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun PagosGerenteTab() {
    val pagos = com.example.acopiodeleche.domain.model.DatosMock.pagos
    val productores = com.example.acopiodeleche.domain.model.DatosMock.productores

    if (pagos.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No hay pagos registrados", style = MaterialTheme.typography.titleMedium)
        }
        return
    }
    androidx.compose.foundation.lazy.LazyColumn(
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text("Gestión de pagos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(
                "${pagos.size} pagos · ${pagos.count { it.estado == com.example.acopiodeleche.domain.model.EstadoPago.PENDIENTE }} pendientes",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        items(pagos) { pago ->
            val productor = productores.find { it.idProductor == pago.idProductor }
            Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(productor?.nombreCompleto ?: "Productor", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text("${pago.periodoDesde} — ${pago.periodoHasta}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${pago.litrosTotales} L · S/ ${pago.precioPorLitro}/L", style = MaterialTheme.typography.bodySmall)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "S/ ${"%.2f".let { pago.totalCalculado.let { t -> "${t.toLong()}.${((t - t.toLong()) * 100).toLong().toString().padStart(2,'0')}" } }}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = VerdeHuata
                            )
                            Text(
                                text = pago.estado.etiqueta,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (pago.estado == com.example.acopiodeleche.domain.model.EstadoPago.PAGADO) VerdeHuata else Color(0xFFE65100)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── Pantalla Trabajador de Planta ─────────────────────────────────────────
@Composable
fun TrabajadorHomeScreen(
    onCerrarSesion: () -> Unit,
    modifier: Modifier = Modifier
) {
    val usuario = SesionActual.usuario
    Column(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF37474F))
                .padding(16.dp)
        ) {
            Column {
                Text("Trabajador de Planta", style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.Bold)
                Text("Ecolácteos Huata · ${usuario?.nombreCompleto ?: ""}", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.8f))
            }
            Text(
                text = "Salir",
                modifier = Modifier.align(Alignment.TopEnd).clickable { onCerrarSesion() }.padding(4.dp),
                color = Color.White.copy(alpha = 0.8f),
                style = MaterialTheme.typography.labelMedium
            )
        }
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🏭", fontSize = 64.sp)
                Spacer(Modifier.height(16.dp))
                Text("Módulo en desarrollo", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Recepción, Producción e Inventario\nestarán disponibles próximamente",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
