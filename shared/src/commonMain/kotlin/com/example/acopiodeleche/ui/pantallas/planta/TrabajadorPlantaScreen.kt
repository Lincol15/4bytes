package com.example.acopiodeleche.ui.pantallas.planta

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.acopiodeleche.domain.model.DatosMock
import com.example.acopiodeleche.domain.model.LoteProduccion
import com.example.acopiodeleche.domain.model.SesionActual
import com.example.acopiodeleche.domain.model.TipoProducto
import com.example.acopiodeleche.domain.model.VentaSalida
import com.example.acopiodeleche.ui.components.CampoFecha
import kotlin.random.Random
import kotlinx.coroutines.launch

private val AzulPlanta        = Color(0xFF37474F)
private val VerdeHuata        = Color(0xFF2E7D32)
private val NaranjaProduccion = Color(0xFFE65100)
private val RojoAlerta        = Color(0xFFC62828)

// ── Ítems del menú lateral ─────────────────────────────────────────────────

private data class ItemMenuPlanta(val icono: String, val titulo: String, val indice: Int)

private val MENU_PLANTA = listOf(
    ItemMenuPlanta("🏠", "Inicio",           0),
    ItemMenuPlanta("🧀", "Producción",       1),
    ItemMenuPlanta("📦", "Ventas / Salidas", 2)
)

// ── Función helper para calcular stock ────────────────────────────────────
private fun stockDisponible(lote: LoteProduccion): Double {
    val vendido = DatosMock.ventasSalidas
        .filter { it.idLote == lote.id }
        .sumOf { it.cantidad }
    return maxOf(0.0, lote.cantidadProducida - vendido)
}

private fun formatearMonto(t: Double): String =
    "S/ ${t.toLong()}.${((t - t.toLong()) * 100).toLong().toString().padStart(2, '0')}"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrabajadorPlantaScreen(
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
                        .background(AzulPlanta)
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .background(Color.White.copy(0.2f), RoundedCornerShape(50)),
                            contentAlignment = Alignment.Center
                        ) { Text("🏭", fontSize = 26.sp) }
                        Spacer(Modifier.height(10.dp))
                        Text(
                            usuario?.nombreCompleto ?: "Trabajador",
                            style      = MaterialTheme.typography.titleSmall,
                            color      = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Trabajador de Planta",
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
                MENU_PLANTA.forEach { item ->
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
                            selectedContainerColor = AzulPlanta.copy(0.12f),
                            selectedTextColor      = AzulPlanta,
                            selectedIconColor      = AzulPlanta
                        ),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                    )
                }

                // ── Cerrar sesión al fondo ─────────────────────────────────
                Spacer(Modifier.weight(1f))
                HorizontalDivider(modifier = Modifier.padding(horizontal = 12.dp))
                NavigationDrawerItem(
                    icon     = { Text("🚪", fontSize = 18.sp) },
                    label    = { Text("Cerrar sesión", color = RojoAlerta) },
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
                    .background(AzulPlanta)
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
                    val item = MENU_PLANTA.find { it.indice == seccionActual }
                    Text(
                        "${item?.icono ?: ""} ${item?.titulo ?: "Planta"}",
                        style      = MaterialTheme.typography.titleMedium,
                        color      = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text("Ecolácteos Huata", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(0.75f))
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                when (seccionActual) {
                    0 -> InicioTrabajador()
                    1 -> ProduccionTab()
                    2 -> VentasTab()
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// INICIO — Dashboard
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun InicioTrabajador() {
    val lotes  = DatosMock.lotesProduccion
    val ventas = DatosMock.ventasSalidas

    val stockTotalQueso  = lotes.filter { it.producto == TipoProducto.QUESO }.sumOf { stockDisponible(it) }
    val stockTotalYogur  = lotes.filter { it.producto == TipoProducto.YOGUR }.sumOf { stockDisponible(it) }
    val lotesConStock    = lotes.count { stockDisponible(it) > 0 }
    val totalVentasHoy   = ventas.sumOf { it.total }
    val lotesPorVencer   = lotes.filter { !it.fechaVencimiento.isNullOrBlank() }

    LazyColumn(
        contentPadding      = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Dashboard de planta", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("Ecolácteos Huata", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        // KPIs principales
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                KpiCard("🧀", "${stockTotalQueso.toLong()} kg", "Stock queso", NaranjaProduccion, Modifier.weight(1f))
                KpiCard("🥛", "${stockTotalYogur.toLong()} L", "Stock yogur", AzulPlanta, Modifier.weight(1f))
            }
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                KpiCard("📦", "$lotesConStock", "Lotes con stock", VerdeHuata, Modifier.weight(1f))
                KpiCard("💰", formatearMonto(totalVentasHoy), "Total ventas", Color(0xFF6A1B9A), Modifier.weight(1f))
            }
        }

        // Alertas de vencimiento
        if (lotesPorVencer.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors   = CardDefaults.cardColors(containerColor = RojoAlerta.copy(0.08f)),
                    shape    = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("⚠️", fontSize = 18.sp)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "${lotesPorVencer.size} lote(s) con fecha de vencimiento",
                                style      = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color      = RojoAlerta
                            )
                        }
                        lotesPorVencer.forEach { lote ->
                            Text(
                                "• ${lote.codigoLote} — vence: ${lote.fechaVencimiento}",
                                style = MaterialTheme.typography.labelSmall,
                                color = RojoAlerta
                            )
                        }
                    }
                }
            }
        }

        // Últimos lotes
        item {
            HorizontalDivider()
            Spacer(Modifier.height(4.dp))
            Text("Últimos lotes producidos", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        }
        if (lotes.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                    Text("Sin lotes registrados aún", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            items(lotes.takeLast(4).reversed()) { lote ->
                TarjetaLoteCompacta(lote)
            }
        }
    }
}

@Composable
private fun KpiCard(icono: String, valor: String, etiqueta: String, color: Color, modifier: Modifier) {
    Card(
        modifier = modifier,
        colors   = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        shape    = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(icono, fontSize = 22.sp)
            Text(valor, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = color)
            Text(etiqueta, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun TarjetaLoteCompacta(lote: LoteProduccion) {
    val stock = stockDisponible(lote)
    val progreso = if (lote.cantidadProducida > 0) (stock / lote.cantidadProducida).toFloat().coerceIn(0f, 1f) else 0f

    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(1.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(lote.producto.icono, fontSize = 22.sp)
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(lote.codigoLote, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    Text("${lote.fecha} · ${lote.litrosUsados} L usados", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("$stock ${lote.unidadMedida}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = if (stock > 0) VerdeHuata else Color.Gray)
                    Text("disponible", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Spacer(Modifier.height(6.dp))
            LinearProgressIndicator(
                progress    = { progreso },
                modifier    = Modifier.fillMaxWidth().height(4.dp),
                color       = if (progreso > 0.3f) VerdeHuata else NaranjaProduccion,
                trackColor  = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// PRODUCCIÓN — 3 tabs: Recetas | Nueva jornada | Historial
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProduccionTab() {
    var tabProd by remember { mutableStateOf(0) }
    var jornadas by remember { mutableStateOf(DatosMock.jornadasProduccion.toList()) }
    var mostrarNuevaJornada by remember { mutableStateOf(false) }

    if (mostrarNuevaJornada) {
        NuevaJornadaForm(
            alGuardar = { jornada, detalles ->
                DatosMock.jornadasProduccion.add(jornada)
                DatosMock.detallesProduccion.addAll(detalles)
                // también crear LoteProduccion legacy para stock
                detalles.forEach { det ->
                    val receta = DatosMock.recetas.find { it.id == det.idReceta }
                    if (receta != null) {
                        val n = Random.nextInt(100, 999)
                        val prefijo = when (receta.producto) {
                            TipoProducto.QUESO       -> "Q"
                            TipoProducto.YOGUR       -> "Y"
                            TipoProducto.MANTEQUILLA -> "M"
                            else                     -> "P"
                        }
                        val unidad = if (receta.producto == TipoProducto.YOGUR) "L" else "kg"
                        DatosMock.lotesProduccion.add(
                            LoteProduccion(
                                id                = "lote-$n",
                                codigoLote        = "$prefijo-2026-$n",
                                producto          = receta.producto,
                                litrosUsados      = det.litrosTotalesUsados,
                                cantidadProducida = det.cantidadProducida,
                                unidadMedida      = unidad,
                                fecha             = jornada.fecha,
                                idTrabajador      = jornada.idTrabajador,
                                observacion       = jornada.observacion
                            )
                        )
                    }
                }
                jornadas = DatosMock.jornadasProduccion.toList()
                mostrarNuevaJornada = false
            },
            alCancelar = { mostrarNuevaJornada = false }
        )
        return
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = tabProd) {
            Tab(selected = tabProd == 0, onClick = { tabProd = 0 }, text = { Text("📋 Recetas") })
            Tab(selected = tabProd == 1, onClick = { tabProd = 1 }, text = { Text("➕ Producir") })
            Tab(selected = tabProd == 2, onClick = { tabProd = 2 }, text = { Text("📊 Historial") })
        }
        when (tabProd) {
            0 -> RecetasTab()
            1 -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier            = Modifier.padding(32.dp)
                    ) {
                        Text("🏭", fontSize = 56.sp)
                        Text("Registrar jornada de producción", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(
                            "Selecciona las recetas que producirás hoy\ny la app calcula los insumos automáticamente",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Button(
                            onClick  = { mostrarNuevaJornada = true },
                            colors   = ButtonDefaults.buttonColors(containerColor = NaranjaProduccion),
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("+ Nueva jornada de producción", fontWeight = FontWeight.Bold) }
                    }
                }
            }
            2 -> HistorialProduccionTab(jornadas)
        }
    }
}

// ── Tab Recetas — CRUD ────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecetasTab() {
    var recetas         by remember { mutableStateOf(DatosMock.recetas.toList()) }
    var mostrarFormulario by remember { mutableStateOf(false) }
    var recetaDetalle   by remember { mutableStateOf<com.example.acopiodeleche.domain.model.Receta?>(null) }

    if (mostrarFormulario) {
        FormularioReceta(
            alGuardar = { receta, detalles ->
                DatosMock.recetas.add(receta)
                DatosMock.recetaDetalles.addAll(detalles)
                recetas = DatosMock.recetas.toList()
                mostrarFormulario = false
            },
            alCancelar = { mostrarFormulario = false }
        )
        return
    }
    if (recetaDetalle != null) {
        DetalleRecetaScreen(
            receta   = recetaDetalle!!,
            onVolver = { recetaDetalle = null }
        )
        return
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier              = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Text("${recetas.size} recetas", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Button(onClick = { mostrarFormulario = true }, colors = ButtonDefaults.buttonColors(containerColor = NaranjaProduccion)) {
                Text("+ Nueva receta")
            }
        }
        HorizontalDivider()
        if (recetas.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📋", fontSize = 48.sp); Spacer(Modifier.height(8.dp))
                    Text("Sin recetas registradas", style = MaterialTheme.typography.titleMedium)
                }
            }
        } else {
            LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(recetas) { receta ->
                    val insumos = DatosMock.recetaDetalles.filter { it.idReceta == receta.id }
                    Card(
                        modifier  = Modifier.fillMaxWidth().clickable { recetaDetalle = receta },
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(receta.icono, fontSize = 28.sp)
                                Spacer(Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(receta.nombre, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                    Text(receta.producto.etiqueta, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    if (!receta.descripcion.isNullOrBlank())
                                        Text(receta.descripcion, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Text("›", fontSize = 22.sp, color = NaranjaProduccion)
                            }
                            if (insumos.isNotEmpty()) {
                                Spacer(Modifier.height(6.dp))
                                HorizontalDivider()
                                Spacer(Modifier.height(4.dp))
                                Text("Insumos por unidad:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                insumos.forEach { ins ->
                                    Text("  • ${ins.resumen}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetalleRecetaScreen(
    receta: com.example.acopiodeleche.domain.model.Receta,
    onVolver: () -> Unit
) {
    val insumos = DatosMock.recetaDetalles.filter { it.idReceta == receta.id }
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = onVolver) { Text("← Volver") }
                Text(receta.nombre, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            }
        }
        item {
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = NaranjaProduccion.copy(0.08f))) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(receta.icono, fontSize = 36.sp)
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text(receta.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(receta.producto.etiqueta, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        if (!receta.descripcion.isNullOrBlank())
                            Text(receta.descripcion, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
        item {
            Text("Insumos por unidad producida", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        }
        if (insumos.isEmpty()) {
            item { Text("Sin insumos registrados para esta receta", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
        } else {
            items(insumos) { ins ->
                Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(1.dp)) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(36.dp).background(NaranjaProduccion.copy(0.1f), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) { Text("🧪", fontSize = 18.sp) }
                        Spacer(Modifier.width(12.dp))
                        Text(ins.insumo, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        Text("${ins.cantidad} ${ins.unidadMedida}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = NaranjaProduccion)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormularioReceta(
    alGuardar: (com.example.acopiodeleche.domain.model.Receta, List<com.example.acopiodeleche.domain.model.RecetaDetalle>) -> Unit,
    alCancelar: () -> Unit
) {
    var nombre           by remember { mutableStateOf("") }
    var descripcion      by remember { mutableStateOf("") }
    var productoSel      by remember { mutableStateOf(TipoProducto.QUESO) }
    var dropExpanded     by remember { mutableStateOf(false) }
    var insumos          by remember { mutableStateOf(listOf<com.example.acopiodeleche.domain.model.RecetaDetalle>()) }
    // campos para agregar insumo
    var insumoNombre     by remember { mutableStateOf("") }
    var insumoCantidad   by remember { mutableStateOf("") }
    var insumoUnidad     by remember { mutableStateOf("litros") }
    var dropUnidadExp    by remember { mutableStateOf(false) }
    val unidades         = listOf("litros", "gramos", "kg", "ml", "unidades")

    val formularioValido = nombre.isNotBlank()

    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = alCancelar) { Text("← Volver") }
                Text("Nueva receta", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            }
        }
        item {
            OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre de la receta *") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        }
        item {
            ExposedDropdownMenuBox(expanded = dropExpanded, onExpandedChange = { dropExpanded = it }) {
                OutlinedTextField(
                    value = "${productoSel.icono} ${productoSel.etiqueta}", onValueChange = {}, readOnly = true,
                    label = { Text("Producto final *") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(dropExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
                )
                ExposedDropdownMenu(expanded = dropExpanded, onDismissRequest = { dropExpanded = false }) {
                    TipoProducto.entries.forEach { t ->
                        DropdownMenuItem(text = { Text("${t.icono} ${t.etiqueta}") }, onClick = { productoSel = t; dropExpanded = false })
                    }
                }
            }
        }
        item {
            OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") }, minLines = 2, modifier = Modifier.fillMaxWidth())
        }
        // Agregar insumos
        item {
            HorizontalDivider()
            Text("Insumos por unidad producida", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        }
        items(insumos) { ins ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("• ${ins.resumen}", style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                TextButton(onClick = { insumos = insumos.filter { it.id != ins.id } }) { Text("✕", color = RojoAlerta) }
            }
        }
        item {
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("+ Agregar insumo", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    OutlinedTextField(value = insumoNombre, onValueChange = { insumoNombre = it }, label = { Text("Nombre del insumo") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = insumoCantidad, onValueChange = { insumoCantidad = it },
                            label = { Text("Cantidad") }, singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f)
                        )
                        ExposedDropdownMenuBox(expanded = dropUnidadExp, onExpandedChange = { dropUnidadExp = it }, modifier = Modifier.weight(1f)) {
                            OutlinedTextField(value = insumoUnidad, onValueChange = {}, readOnly = true,
                                label = { Text("Unidad") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(dropUnidadExp) },
                                modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable))
                            ExposedDropdownMenu(expanded = dropUnidadExp, onDismissRequest = { dropUnidadExp = false }) {
                                unidades.forEach { u -> DropdownMenuItem(text = { Text(u) }, onClick = { insumoUnidad = u; dropUnidadExp = false }) }
                            }
                        }
                    }
                    Button(
                        onClick = {
                            if (insumoNombre.isNotBlank() && insumoCantidad.toDoubleOrNull() != null) {
                                val nuevo = com.example.acopiodeleche.domain.model.RecetaDetalle(
                                    id = "rd-${Random.nextInt(100, 9999)}", idReceta = "",
                                    insumo = insumoNombre.trim(), cantidad = insumoCantidad.toDouble(), unidadMedida = insumoUnidad
                                )
                                insumos = insumos + nuevo
                                insumoNombre = ""; insumoCantidad = ""
                            }
                        },
                        enabled = insumoNombre.isNotBlank() && insumoCantidad.toDoubleOrNull() != null,
                        colors  = ButtonDefaults.buttonColors(containerColor = NaranjaProduccion),
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Agregar insumo") }
                }
            }
        }
        item {
            Button(
                onClick = {
                    val id = "r-${Random.nextInt(100, 9999)}"
                    val receta = com.example.acopiodeleche.domain.model.Receta(id = id, nombre = nombre.trim(), producto = productoSel, descripcion = descripcion.ifBlank { null })
                    val dets   = insumos.mapIndexed { i, ins -> ins.copy(id = "rd-${id}-$i", idReceta = id) }
                    alGuardar(receta, dets)
                },
                enabled  = formularioValido,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = NaranjaProduccion)
            ) { Text("Guardar receta", fontWeight = FontWeight.Bold) }
        }
    }
}

// ── Nueva Jornada de Producción ───────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NuevaJornadaForm(
    alGuardar: (com.example.acopiodeleche.domain.model.ProduccionJornada, List<com.example.acopiodeleche.domain.model.ProduccionDetalle>) -> Unit,
    alCancelar: () -> Unit
) {
    val recetasDisponibles = DatosMock.recetas.filter { it.activa }
    // Map recetaId -> cantidad a producir
    var cantidades   by remember { mutableStateOf(mapOf<String, String>()) }
    var fecha        by remember { mutableStateOf("") }
    var observacion  by remember { mutableStateOf("") }

    // Calcular insumos totales
    data class ResumenInsumo(val nombre: String, val total: Double, val unidad: String)
    val resumenInsumos: List<ResumenInsumo> = remember(cantidades) {
        val mapa = mutableMapOf<String, Pair<Double, String>>()
        cantidades.forEach { (idReceta, cantStr) ->
            val cant = cantStr.toDoubleOrNull() ?: 0.0
            val insumos = DatosMock.recetaDetalles.filter { it.idReceta == idReceta }
            insumos.forEach { ins ->
                val key = "${ins.insumo}|${ins.unidadMedida}"
                val prev = mapa[key] ?: (0.0 to ins.unidadMedida)
                mapa[key] = (prev.first + ins.cantidad * cant) to ins.unidadMedida
            }
        }
        mapa.map { (key, v) -> ResumenInsumo(key.substringBefore("|"), v.first, v.second) }
    }

    val hayAlgoCantidad = cantidades.any { (_, v) -> v.toDoubleOrNull()?.let { it > 0 } == true }
    val formularioValido = hayAlgoCantidad && fecha.isNotBlank()

    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = alCancelar) { Text("← Volver") }
                Text("Nueva jornada de producción", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            }
        }
        item { CampoFecha(value = fecha, onValueChange = { fecha = it }, label = "Fecha de producción *", colorPrimario = NaranjaProduccion, modifier = Modifier.fillMaxWidth()) }

        // Recetas con campo de cantidad
        item { Text("¿Qué vas a producir hoy?", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold) }
        if (recetasDisponibles.isEmpty()) {
            item {
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = RojoAlerta.copy(0.08f))) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("⚠️", fontSize = 18.sp); Spacer(Modifier.width(8.dp))
                        Text("No hay recetas registradas. Ve a la pestaña Recetas y agrega una.", style = MaterialTheme.typography.bodySmall, color = RojoAlerta)
                    }
                }
            }
        } else {
            items(recetasDisponibles) { receta ->
                val cant = cantidades[receta.id] ?: ""
                Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(receta.icono, fontSize = 28.sp)
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(receta.nombre, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            Text(receta.producto.etiqueta, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        OutlinedTextField(
                            value          = cant,
                            onValueChange  = { cantidades = cantidades + (receta.id to it) },
                            label          = { Text("Unid.") },
                            singleLine     = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier       = Modifier.width(90.dp)
                        )
                    }
                }
            }
        }

        // Resumen de insumos calculado automáticamente
        if (resumenInsumos.isNotEmpty()) {
            item {
                HorizontalDivider()
                Text("🧮 Insumos necesarios (calculado automático)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            }
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors   = CardDefaults.cardColors(containerColor = NaranjaProduccion.copy(0.07f)),
                    shape    = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        resumenInsumos.forEach { ins ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("• ${ins.nombre}", style = MaterialTheme.typography.bodySmall)
                                Text("${"%.2f".format(ins.total)} ${ins.unidad}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = NaranjaProduccion)
                            }
                        }
                    }
                }
            }
        }

        item { OutlinedTextField(value = observacion, onValueChange = { observacion = it }, label = { Text("Observación") }, minLines = 2, modifier = Modifier.fillMaxWidth()) }
        item {
            Button(
                onClick = {
                    val jornadaId = "jor-${Random.nextInt(1000, 9999)}"
                    val jornada   = com.example.acopiodeleche.domain.model.ProduccionJornada(
                        id = jornadaId, fecha = fecha.trim(),
                        idTrabajador = SesionActual.usuario?.id ?: "",
                        observacion  = observacion.ifBlank { null }
                    )
                    val detalles = cantidades.mapNotNull { (idReceta, cantStr) ->
                        val cant = cantStr.toDoubleOrNull() ?: 0.0
                        if (cant <= 0) return@mapNotNull null
                        val insumos = DatosMock.recetaDetalles.filter { it.idReceta == idReceta }
                        val litros  = insumos.filter { it.unidadMedida == "litros" }.sumOf { it.cantidad * cant }
                        com.example.acopiodeleche.domain.model.ProduccionDetalle(
                            id = "det-${Random.nextInt(1000, 9999)}", idJornada = jornadaId,
                            idReceta = idReceta, cantidadProducida = cant, litrosTotalesUsados = litros
                        )
                    }
                    alGuardar(jornada, detalles)
                },
                enabled  = formularioValido,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = NaranjaProduccion)
            ) { Text("Registrar jornada de producción", fontWeight = FontWeight.Bold) }
        }
    }
}

// ── Historial de jornadas ─────────────────────────────────────────────────

@Composable
private fun HistorialProduccionTab(jornadas: List<com.example.acopiodeleche.domain.model.ProduccionJornada>) {
    if (jornadas.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("📊", fontSize = 48.sp); Spacer(Modifier.height(8.dp))
                Text("Sin jornadas registradas", style = MaterialTheme.typography.titleMedium)
                Text("Registra tu primera jornada en la pestaña ➕ Producir", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        return
    }
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Text("${jornadas.size} jornadas registradas", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold) }
        items(jornadas.reversed()) { jornada ->
            val detalles   = DatosMock.detallesProduccion.filter { it.idJornada == jornada.id }
            val litrosTot  = detalles.sumOf { it.litrosTotalesUsados }
            Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text("🗓️ ${jornada.fecha}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            if (!jornada.observacion.isNullOrBlank())
                                Text("📝 ${jornada.observacion}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("${"%.1f".format(litrosTot)} L", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = NaranjaProduccion)
                            Text("leche usada", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    if (detalles.isNotEmpty()) {
                        Spacer(Modifier.height(6.dp))
                        HorizontalDivider()
                        Spacer(Modifier.height(4.dp))
                        detalles.forEach { det ->
                            val receta = DatosMock.recetas.find { it.id == det.idReceta }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("  ${receta?.icono ?: "📦"} ${receta?.nombre ?: "Receta"}", style = MaterialTheme.typography.labelSmall)
                                Text("${det.cantidadProducida.toLong()} unid · ${"%.1f".format(det.litrosTotalesUsados)} L", style = MaterialTheme.typography.labelSmall, color = NaranjaProduccion, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FiltroChipPlanta(label: String, seleccionado: Boolean, onClick: () -> Unit) {
    val bg  = if (seleccionado) AzulPlanta else AzulPlanta.copy(0.1f)
    val txt = if (seleccionado) Color.White else AzulPlanta
    Box(
        modifier = Modifier
            .background(bg, RoundedCornerShape(50))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = txt, fontWeight = FontWeight.Bold)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// VENTAS / SALIDAS — con 3 tabs: Stock | Nueva venta | Historial
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VentasTab() {
    var tabVentas        by remember { mutableStateOf(0) }
    var ventas           by remember { mutableStateOf(DatosMock.ventasSalidas.toList()) }
    var mostrarFormulario by remember { mutableStateOf(false) }

    if (mostrarFormulario) {
        FormularioVenta(
            alGuardar = { nueva ->
                DatosMock.ventasSalidas.add(nueva)
                ventas = DatosMock.ventasSalidas.toList()
                mostrarFormulario = false
            },
            alCancelar = { mostrarFormulario = false }
        )
        return
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = tabVentas) {
            Tab(selected = tabVentas == 0, onClick = { tabVentas = 0 }, text = { Text("📊 Stock") })
            Tab(selected = tabVentas == 1, onClick = { tabVentas = 1 }, text = { Text("+ Venta") })
            Tab(selected = tabVentas == 2, onClick = { tabVentas = 2 }, text = { Text("📋 Historial") })
        }
        when (tabVentas) {
            0 -> StockTab()
            1 -> {
                Box(
                    modifier            = Modifier.fillMaxSize(),
                    contentAlignment    = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text("📦", fontSize = 56.sp)
                        Text("Registrar nueva venta", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(
                            "Selecciona un lote con stock disponible\ny registra el despacho al cliente",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Button(
                            onClick = { mostrarFormulario = true },
                            colors  = ButtonDefaults.buttonColors(containerColor = VerdeHuata),
                            modifier = Modifier.padding(horizontal = 32.dp).fillMaxWidth()
                        ) {
                            Text("+ Nueva venta / despacho", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            2 -> HistorialVentasTab(ventas)
        }
    }
}

// ── Stock ─────────────────────────────────────────────────────────────────

@Composable
private fun StockTab() {
    val lotes = DatosMock.lotesProduccion

    if (lotes.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("📦", fontSize = 48.sp)
                Spacer(Modifier.height(8.dp))
                Text("Sin lotes producidos", style = MaterialTheme.typography.titleMedium)
                Text("Registra un lote en Producción para ver el stock", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        return
    }

    // Resumen por tipo de producto
    val resumenPorTipo = TipoProducto.entries.mapNotNull { tipo ->
        val loteTipo = lotes.filter { it.producto == tipo }
        if (loteTipo.isEmpty()) null
        else tipo to loteTipo.sumOf { stockDisponible(it) }
    }

    LazyColumn(
        contentPadding      = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text("Stock disponible", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("Calculado automáticamente (producido − vendido)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        // Resumen por producto
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                resumenPorTipo.forEach { (tipo, stock) ->
                    Card(
                        modifier = Modifier.weight(1f),
                        colors   = CardDefaults.cardColors(containerColor = NaranjaProduccion.copy(0.1f)),
                        shape    = RoundedCornerShape(10.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(tipo.icono, fontSize = 24.sp)
                            Text("${stock.toLong()} ${if (tipo == TipoProducto.YOGUR) "L" else "kg"}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = NaranjaProduccion)
                            Text(tipo.etiqueta, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }

        item {
            HorizontalDivider()
            Spacer(Modifier.height(4.dp))
            Text("Detalle por lote", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        }

        items(lotes.reversed()) { lote ->
            val stock    = stockDisponible(lote)
            val vendido  = lote.cantidadProducida - stock
            val progreso = if (lote.cantidadProducida > 0) (stock / lote.cantidadProducida).toFloat().coerceIn(0f, 1f) else 0f
            val colorStock = when {
                stock <= 0       -> Color.Gray
                progreso < 0.25f -> RojoAlerta
                else             -> VerdeHuata
            }

            Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(lote.producto.icono, fontSize = 24.sp)
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text(lote.codigoLote, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                Text(lote.fecha, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("$stock ${lote.unidadMedida}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = colorStock)
                            Text("de ${lote.cantidadProducida} producidos", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress   = { progreso },
                        modifier   = Modifier.fillMaxWidth().height(6.dp),
                        color      = colorStock,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Vendido: $vendido ${lote.unidadMedida}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        if (!lote.fechaVencimiento.isNullOrBlank())
                            Text("⚠️ Vence: ${lote.fechaVencimiento}", style = MaterialTheme.typography.labelSmall, color = RojoAlerta, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ── Historial de ventas ───────────────────────────────────────────────────

@Composable
private fun HistorialVentasTab(ventas: List<VentaSalida>) {
    val totalGeneral = ventas.sumOf { it.total }

    if (ventas.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("📋", fontSize = 48.sp)
                Spacer(Modifier.height(8.dp))
                Text("Sin ventas registradas", style = MaterialTheme.typography.titleMedium)
            }
        }
        return
    }

    LazyColumn(
        contentPadding      = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors   = CardDefaults.cardColors(containerColor = VerdeHuata.copy(0.1f)),
                shape    = RoundedCornerShape(10.dp)
            ) {
                Row(modifier = Modifier.padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text("Total acumulado", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(formatearMonto(totalGeneral), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = VerdeHuata)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("${ventas.size}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = VerdeHuata)
                        Text("ventas", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
        items(ventas.reversed()) { venta -> TarjetaVenta(venta) }
    }
}

@Composable
private fun TarjetaVenta(venta: VentaSalida) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(42.dp).background(NaranjaProduccion.copy(0.12f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) { Text(venta.producto.icono, fontSize = 22.sp) }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(venta.cliente, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        Text("${venta.producto.etiqueta} · ${venta.cantidadFormateada}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(venta.fecha, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(venta.totalFormateado, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = VerdeHuata)
                    Text("${venta.precioPorUnidad}/${venta.unidadMedida}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            if (!venta.observacion.isNullOrBlank()) {
                Spacer(Modifier.height(4.dp))
                Text("📝 ${venta.observacion}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

// ── Formulario de nueva venta ─────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormularioVenta(alGuardar: (VentaSalida) -> Unit, alCancelar: () -> Unit) {
    val lotes            = DatosMock.lotesProduccion.filter { stockDisponible(it) > 0 }
    var loteSeleccionado by remember { mutableStateOf<LoteProduccion?>(null) }
    var dropLoteExpanded by remember { mutableStateOf(false) }
    var cliente          by remember { mutableStateOf("") }
    var cantidad         by remember { mutableStateOf("") }
    var precio           by remember { mutableStateOf("") }
    var fecha            by remember { mutableStateOf("") }
    var observacion      by remember { mutableStateOf("") }

    val stockMax      = loteSeleccionado?.let { stockDisponible(it) } ?: 0.0
    val cantidadValida = cantidad.toDoubleOrNull()?.let { it > 0 && it <= stockMax } ?: false
    val formularioValido = loteSeleccionado != null && cliente.isNotBlank()
        && cantidadValida && precio.toDoubleOrNull() != null && fecha.isNotBlank()

    LazyColumn(
        contentPadding      = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = alCancelar) { Text("← Volver") }
                Text("Registrar venta / despacho", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            }
        }

        // Aviso si no hay lotes con stock
        if (lotes.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors   = CardDefaults.cardColors(containerColor = RojoAlerta.copy(0.08f))
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("⚠️", fontSize = 20.sp)
                        Spacer(Modifier.width(8.dp))
                        Text("No hay lotes con stock disponible. Registra producción primero.", style = MaterialTheme.typography.bodySmall, color = RojoAlerta)
                    }
                }
            }
            return@LazyColumn
        }

        // Selector de lote
        item {
            ExposedDropdownMenuBox(expanded = dropLoteExpanded, onExpandedChange = { dropLoteExpanded = it }) {
                OutlinedTextField(
                    value         = loteSeleccionado?.let { "${it.producto.icono} ${it.codigoLote} — stock: ${stockDisponible(it)} ${it.unidadMedida}" } ?: "",
                    onValueChange = {},
                    readOnly      = true,
                    label         = { Text("Lote con stock *") },
                    trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(dropLoteExpanded) },
                    modifier      = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
                )
                ExposedDropdownMenu(expanded = dropLoteExpanded, onDismissRequest = { dropLoteExpanded = false }) {
                    lotes.forEach { lote ->
                        DropdownMenuItem(
                            text    = { Text("${lote.producto.icono} ${lote.codigoLote} · stock: ${stockDisponible(lote)} ${lote.unidadMedida} · ${lote.fecha}") },
                            onClick = { loteSeleccionado = lote; cantidad = ""; dropLoteExpanded = false }
                        )
                    }
                }
            }
        }

        // Info del stock seleccionado
        if (loteSeleccionado != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors   = CardDefaults.cardColors(containerColor = VerdeHuata.copy(0.08f)),
                    shape    = RoundedCornerShape(8.dp)
                ) {
                    Row(modifier = Modifier.padding(10.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Stock disponible:", style = MaterialTheme.typography.bodySmall)
                        Text("$stockMax ${loteSeleccionado!!.unidadMedida}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = VerdeHuata)
                    }
                }
            }
        }

        item {
            OutlinedTextField(
                value         = cliente,
                onValueChange = { cliente = it },
                label         = { Text("Cliente / Distribuidor *") },
                singleLine    = true,
                modifier      = Modifier.fillMaxWidth()
            )
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value          = cantidad,
                    onValueChange  = { cantidad = it },
                    label          = { Text("Cantidad *") },
                    suffix         = { Text(loteSeleccionado?.unidadMedida ?: "") },
                    isError        = cantidad.isNotBlank() && !cantidadValida,
                    supportingText = if (cantidad.isNotBlank() && !cantidadValida) {
                        { Text("Máx: $stockMax ${loteSeleccionado?.unidadMedida ?: ""}", color = RojoAlerta) }
                    } else null,
                    singleLine    = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier      = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value          = precio,
                    onValueChange  = { precio = it },
                    label          = { Text("Precio *") },
                    prefix         = { Text("S/ ") },
                    suffix         = { Text("/ ${loteSeleccionado?.unidadMedida ?: "kg"}") },
                    singleLine     = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier       = Modifier.weight(1f)
                )
            }
        }

        // Vista previa del total
        if (cantidadValida && precio.toDoubleOrNull() != null) {
            val total = cantidad.toDouble() * precio.toDouble()
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors   = CardDefaults.cardColors(containerColor = VerdeHuata.copy(0.1f))
                ) {
                    Row(modifier = Modifier.padding(12.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total a cobrar:", style = MaterialTheme.typography.bodyMedium)
                        Text(formatearMonto(total), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = VerdeHuata)
                    }
                }
            }
        }

        item {
            CampoFecha(value = fecha, onValueChange = { fecha = it }, label = "Fecha de despacho *", colorPrimario = VerdeHuata, modifier = Modifier.fillMaxWidth())
        }
        item {
            OutlinedTextField(value = observacion, onValueChange = { observacion = it }, label = { Text("Observación") }, minLines = 2, modifier = Modifier.fillMaxWidth())
        }
        item {
            Button(
                onClick = {
                    alGuardar(
                        VentaSalida(
                            id              = "venta-${Random.nextInt(1000, 9999)}",
                            idLote          = loteSeleccionado!!.id,
                            producto        = loteSeleccionado!!.producto,
                            cliente         = cliente.trim(),
                            cantidad        = cantidad.toDoubleOrNull() ?: 0.0,
                            unidadMedida    = loteSeleccionado!!.unidadMedida,
                            precioPorUnidad = precio.toDoubleOrNull() ?: 0.0,
                            fecha           = fecha.trim(),
                            idTrabajador    = SesionActual.usuario?.id ?: "",
                            observacion     = observacion.ifBlank { null }
                        )
                    )
                },
                enabled  = formularioValido,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = VerdeHuata)
            ) { Text("Guardar venta / despacho", fontWeight = FontWeight.Bold) }
        }
    }
}
