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
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.acopiodeleche.domain.model.InsumoPlanta
import com.example.acopiodeleche.domain.model.ProductoPlanta
import com.example.acopiodeleche.ui.estado.CargaOError
import com.example.acopiodeleche.ui.estado.EstadoEnvio
import com.example.acopiodeleche.ui.estado.EstadoUi
import com.example.acopiodeleche.ui.estado.datosOVacio
import com.example.acopiodeleche.ui.estado.fechaHoyPantalla
import com.example.acopiodeleche.domain.model.LoteProduccion
import com.example.acopiodeleche.domain.model.SesionActual
import com.example.acopiodeleche.domain.model.TipoProducto
import com.example.acopiodeleche.domain.model.VentaSalida
import com.example.acopiodeleche.ui.components.CampoFecha
import kotlinx.coroutines.launch
import com.example.acopiodeleche.ui.tema.EsqueletoRol
import com.example.acopiodeleche.ui.tema.HuataColores
import com.example.acopiodeleche.ui.tema.OpcionMenu
import com.example.acopiodeleche.ui.tema.TarjetaEstadistica
import org.koin.compose.viewmodel.koinViewModel

private val AzulPlanta        = HuataColores.Verde
private val VerdeHuata        = HuataColores.Verde
private val NaranjaProduccion = HuataColores.NaranjaOscuro
private val RojoAlerta        = HuataColores.Rojo

// ── Ítems del menú lateral ─────────────────────────────────────────────────


private val MENU_PLANTA = listOf(
    OpcionMenu("🏠", "Inicio"),
    OpcionMenu("🧀", "Producción", "Planta"),
    OpcionMenu("📦", "Ventas / Salidas", "Planta"),
    OpcionMenu("📥", "Recepción de leche", "Planta")
)

// ── Stock del lote: lo calcula Laravel a partir del inventario (entradas − salidas) ──
private fun stockDisponible(lote: LoteProduccion): Double = maxOf(0.0, lote.stock ?: lote.cantidadProducida)

@Composable
private fun MensajePlanta(envio: EstadoEnvio, onCerrar: () -> Unit) {
    val (texto, color) = when (envio) {
        is EstadoEnvio.Exito -> envio.mensaje to VerdeHuata
        is EstadoEnvio.Error -> envio.mensaje to RojoAlerta
        else -> return
    }
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(texto, color = color, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
        TextButton(onClick = onCerrar) { Text("✕") }
    }
}

private fun formatearMonto(t: Double): String =
    "S/ ${t.toLong()}.${((t - t.toLong()) * 100).toLong().toString().padStart(2, '0')}"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrabajadorPlantaScreen(
    onCerrarSesion: () -> Unit,
    modifier: Modifier = Modifier
) {
    var seccionActual by remember { mutableStateOf(0) }

    EsqueletoRol(
        opciones = MENU_PLANTA,
        seleccion = seccionActual,
        onSeleccionar = { seccionActual = it },
        onCerrarSesion = onCerrarSesion,
        subtitulo = if (seccionActual == 0) "Producción, stock y recepción de leche" else null,
        modifier = modifier
    ) {
        when (seccionActual) {
            0 -> InicioTrabajador()
            1 -> ProduccionTab()
            2 -> VentasTab()
            3 -> RecepcionPlantaScreen()
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// INICIO — Dashboard
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun InicioTrabajador(viewModel: PlantaViewModel = koinViewModel()) {
    val estadoLotes  by viewModel.lotes.collectAsStateWithLifecycle()
    val estadoVentas by viewModel.ventas.collectAsStateWithLifecycle()
    if (estadoLotes !is EstadoUi.Exito) {
        CargaOError(estadoLotes, onReintentar = viewModel::cargar)
        return
    }
    val lotes  = estadoLotes.datosOVacio()
    val ventas = estadoVentas.datosOVacio()

    val stockTotalQueso  = lotes.filter { it.producto == TipoProducto.QUESO }.sumOf { stockDisponible(it) }
    val stockTotalYogur  = lotes.filter { it.producto == TipoProducto.YOGUR }.sumOf { stockDisponible(it) }
    val lotesConStock    = lotes.count { stockDisponible(it) > 0 }
    val totalVentasHoy   = ventas.sumOf { it.total }
    val lotesPorVencer   = lotes.filter { !it.fechaVencimiento.isNullOrBlank() && stockDisponible(it) > 0 }

    LazyColumn(
        contentPadding      = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
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
                KpiCard("💰", formatearMonto(totalVentasHoy), "Total ventas", HuataColores.Morado, Modifier.weight(1f))
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
            items(lotes.take(4)) { lote ->
                TarjetaLoteCompacta(lote)
            }
        }
    }
}

@Composable
private fun KpiCard(icono: String, valor: String, etiqueta: String, color: Color, modifier: Modifier) =
    TarjetaEstadistica(etiqueta, valor, icono, color, modifier)

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
private fun ProduccionTab(viewModel: PlantaViewModel = koinViewModel()) {
    var tabProd by remember { mutableStateOf(0) }
    var mostrarNuevaJornada by remember { mutableStateOf(false) }
    val envio by viewModel.envio.collectAsStateWithLifecycle()
    val estadoLotes by viewModel.lotes.collectAsStateWithLifecycle()

    LaunchedEffect(envio) {
        if (envio is EstadoEnvio.Exito && mostrarNuevaJornada) {
            mostrarNuevaJornada = false
            tabProd = 2
        }
    }

    if (mostrarNuevaJornada) {
        NuevaJornadaForm(
            viewModel = viewModel,
            alGuardar = { fecha, cantidades, observacion -> viewModel.registrarProduccion(fecha, cantidades, observacion) },
            alCancelar = { viewModel.limpiarEnvio(); mostrarNuevaJornada = false }
        )
        return
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = tabProd) {
            Tab(selected = tabProd == 0, onClick = { tabProd = 0 }, text = { Text("📋 Recetas") })
            Tab(selected = tabProd == 1, onClick = { tabProd = 1 }, text = { Text("➕ Producir") })
            Tab(selected = tabProd == 2, onClick = { tabProd = 2 }, text = { Text("📊 Historial") })
        }
        MensajePlanta(envio, viewModel::limpiarEnvio)
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
                            onClick  = { viewModel.limpiarEnvio(); mostrarNuevaJornada = true },
                            colors   = ButtonDefaults.buttonColors(containerColor = NaranjaProduccion),
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("+ Nueva jornada de producción", fontWeight = FontWeight.Bold) }
                    }
                }
            }
            2 -> if (estadoLotes is EstadoUi.Exito) HistorialProduccionTab(estadoLotes.datosOVacio())
                 else CargaOError(estadoLotes, onReintentar = viewModel::cargar)
        }
    }
}

// ── Tab Recetas — CRUD ────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecetasTab(viewModel: PlantaViewModel = koinViewModel()) {
    // Recetas del backend (las mismas que en el panel web de planta)
    val estadoRecetas by viewModel.recetas.collectAsStateWithLifecycle()
    val estadoProductos by viewModel.productos.collectAsStateWithLifecycle()
    val estadoInsumos by viewModel.insumos.collectAsStateWithLifecycle()
    val envio by viewModel.envio.collectAsStateWithLifecycle()
    val recetasConDetalle = estadoRecetas.datosOVacio()
    val recetas = recetasConDetalle.map { it.first }
    var mostrarFormulario by remember { mutableStateOf(false) }
    var recetaDetalle   by remember { mutableStateOf<com.example.acopiodeleche.domain.model.Receta?>(null) }

    LaunchedEffect(envio) {
        if (envio is EstadoEnvio.Exito) mostrarFormulario = false
    }

    if (mostrarFormulario) {
        FormularioReceta(
            productos = estadoProductos.datosOVacio(),
            catalogoInsumos = estadoInsumos.datosOVacio(),
            enviando = envio == EstadoEnvio.Enviando,
            mensajeError = (envio as? EstadoEnvio.Error)?.mensaje,
            alGuardar = { nombre, idProducto, descripcion, insumos ->
                viewModel.registrarReceta(nombre, idProducto, descripcion, insumos)
            },
            alCancelar = { viewModel.limpiarEnvio(); mostrarFormulario = false }
        )
        return
    }
    if (recetaDetalle != null) {
        DetalleRecetaScreen(
            receta   = recetaDetalle!!,
            insumos  = recetasConDetalle.firstOrNull { it.first.id == recetaDetalle!!.id }?.second.orEmpty(),
            onVolver = { recetaDetalle = null }
        )
        return
    }
    if (estadoRecetas !is EstadoUi.Exito) {
        CargaOError(estadoRecetas, onReintentar = viewModel::cargar)
        return
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier              = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Text("${recetas.size} recetas", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Button(onClick = { viewModel.limpiarEnvio(); mostrarFormulario = true }, colors = ButtonDefaults.buttonColors(containerColor = NaranjaProduccion)) {
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
                items(recetasConDetalle) { (receta, insumos) ->
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
                                    Text(receta.nombreProducto ?: receta.producto.etiqueta, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
    insumos: List<com.example.acopiodeleche.domain.model.RecetaDetalle>,
    onVolver: () -> Unit
) {
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
    productos: List<ProductoPlanta>,
    catalogoInsumos: List<InsumoPlanta>,
    enviando: Boolean,
    mensajeError: String?,
    alGuardar: (nombre: String, idProducto: String, descripcion: String?, insumos: Map<String, Double>) -> Unit,
    alCancelar: () -> Unit
) {
    var nombre           by remember { mutableStateOf("") }
    var descripcion      by remember { mutableStateOf("") }
    var productoSel      by remember { mutableStateOf<ProductoPlanta?>(null) }
    var dropExpanded     by remember { mutableStateOf(false) }
    // insumo del catálogo -> cantidad por unidad producida
    var insumos          by remember { mutableStateOf(listOf<Pair<InsumoPlanta, Double>>()) }
    var insumoSel        by remember { mutableStateOf<InsumoPlanta?>(null) }
    var insumoCantidad   by remember { mutableStateOf("") }
    var dropInsumoExp    by remember { mutableStateOf(false) }

    // Laravel exige producto del catálogo y al menos un insumo (la leche también es un insumo)
    val formularioValido = nombre.isNotBlank() && productoSel != null && insumos.isNotEmpty()

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
                    value = productoSel?.let { "${it.tipo.icono} ${it.nombre}" } ?: "", onValueChange = {}, readOnly = true,
                    label = { Text("Producto final *") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(dropExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
                )
                ExposedDropdownMenu(expanded = dropExpanded, onDismissRequest = { dropExpanded = false }) {
                    productos.forEach { p ->
                        DropdownMenuItem(text = { Text("${p.tipo.icono} ${p.nombre} (${p.unidad})") }, onClick = { productoSel = p; dropExpanded = false })
                    }
                }
            }
        }
        item {
            OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Instrucciones") }, minLines = 2, modifier = Modifier.fillMaxWidth())
        }
        item {
            HorizontalDivider()
            Text("Insumos por unidad producida", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        }
        items(insumos) { (ins, cantidad) ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("• ${ins.nombre}: $cantidad ${ins.unidad}", style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                TextButton(onClick = { insumos = insumos.filter { it.first.id != ins.id } }) { Text("✕", color = RojoAlerta) }
            }
        }
        item {
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("+ Agregar insumo del almacén", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    ExposedDropdownMenuBox(expanded = dropInsumoExp, onExpandedChange = { dropInsumoExp = it }) {
                        OutlinedTextField(
                            value = insumoSel?.let { "${it.nombre} (${it.unidad})" } ?: "", onValueChange = {}, readOnly = true,
                            label = { Text("Insumo") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(dropInsumoExp) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        )
                        ExposedDropdownMenu(expanded = dropInsumoExp, onDismissRequest = { dropInsumoExp = false }) {
                            catalogoInsumos.filter { c -> insumos.none { it.first.id == c.id } }.forEach { c ->
                                DropdownMenuItem(
                                    text = { Text("${if (c.esLeche) "🥛 " else ""}${c.nombre} (${c.unidad}) · stock ${c.stock}") },
                                    onClick = { insumoSel = c; dropInsumoExp = false }
                                )
                            }
                        }
                    }
                    OutlinedTextField(
                        value = insumoCantidad, onValueChange = { insumoCantidad = it },
                        label = { Text("Cantidad por unidad") }, suffix = { Text(insumoSel?.unidad.orEmpty()) }, singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = {
                            val ins = insumoSel
                            val cant = insumoCantidad.toDoubleOrNull()
                            if (ins != null && cant != null && cant > 0) {
                                insumos = insumos + (ins to cant)
                                insumoSel = null; insumoCantidad = ""
                            }
                        },
                        enabled = insumoSel != null && (insumoCantidad.toDoubleOrNull() ?: 0.0) > 0,
                        colors  = ButtonDefaults.buttonColors(containerColor = NaranjaProduccion),
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Agregar insumo") }
                }
            }
        }
        if (mensajeError != null) {
            item { Text(mensajeError, color = RojoAlerta, style = MaterialTheme.typography.bodySmall) }
        }
        item {
            Button(
                onClick = {
                    alGuardar(nombre.trim(), productoSel!!.id, descripcion.ifBlank { null }, insumos.associate { it.first.id to it.second })
                },
                enabled  = formularioValido && !enviando,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = NaranjaProduccion)
            ) { Text(if (enviando) "Guardando..." else "Guardar receta", fontWeight = FontWeight.Bold) }
        }
    }
}

// ── Nueva Jornada de Producción ───────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NuevaJornadaForm(
    viewModel: PlantaViewModel,
    alGuardar: (fecha: String, cantidadesPorReceta: Map<String, Double>, observacion: String?) -> Unit,
    alCancelar: () -> Unit
) {
    val estadoRecetas by viewModel.recetas.collectAsStateWithLifecycle()
    val envio by viewModel.envio.collectAsStateWithLifecycle()
    val recetasConDetalle = estadoRecetas.datosOVacio().filter { it.first.activa }
    val recetasDisponibles = recetasConDetalle.map { it.first }
    val detallesPorReceta = recetasConDetalle.associate { it.first.id to it.second }
    // Map recetaId -> cantidad a producir
    var cantidades   by remember { mutableStateOf(mapOf<String, String>()) }
    var fecha        by remember { mutableStateOf(fechaHoyPantalla()) }
    var observacion  by remember { mutableStateOf("") }

    // Calcular insumos totales
    data class ResumenInsumo(val nombre: String, val total: Double, val unidad: String)
    val resumenInsumos: List<ResumenInsumo> = remember(cantidades) {
        val mapa = mutableMapOf<String, Pair<Double, String>>()
        cantidades.forEach { (idReceta, cantStr) ->
            val cant = cantStr.toDoubleOrNull() ?: 0.0
            val insumos = detallesPorReceta[idReceta].orEmpty()
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
        item {
            Text("¿Qué vas a producir hoy?", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Text("La leche se toma de las entregas recibidas más antiguas y los insumos se descuentan del almacén.",
                style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
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
                            Text(receta.nombreProducto ?: receta.producto.etiqueta, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                                Text("${(kotlin.math.round(ins.total * 100) / 100)} ${ins.unidad}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = NaranjaProduccion)
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
                    val unidades = cantidades.mapNotNull { (idReceta, cantStr) ->
                        cantStr.toDoubleOrNull()?.takeIf { it > 0 }?.let { idReceta to it }
                    }.toMap()
                    alGuardar(fecha.trim(), unidades, observacion)
                },
                enabled  = formularioValido && envio != EstadoEnvio.Enviando,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = NaranjaProduccion)
            ) { Text(if (envio == EstadoEnvio.Enviando) "Registrando..." else "Registrar jornada de producción", fontWeight = FontWeight.Bold) }
        }
        (envio as? EstadoEnvio.Error)?.let { error ->
            item { Text(error.mensaje, color = RojoAlerta, style = MaterialTheme.typography.bodySmall) }
        }
    }
}

// ── Historial de jornadas ─────────────────────────────────────────────────

@Composable
private fun HistorialProduccionTab(lotes: List<LoteProduccion>) {
    if (lotes.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("📊", fontSize = 48.sp); Spacer(Modifier.height(8.dp))
                Text("Sin jornadas registradas", style = MaterialTheme.typography.titleMedium)
                Text("Registra tu primera jornada en la pestaña ➕ Producir", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        return
    }
    // Cada jornada = lotes producidos en la misma fecha
    val jornadas = lotes.groupBy { it.fecha }.toList()
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Text("${jornadas.size} jornadas registradas", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold) }
        items(jornadas) { (fecha, lotesDia) ->
            val litrosTot = lotesDia.sumOf { it.litrosUsados }
            Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text("🗓️ $fecha", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            lotesDia.firstNotNullOfOrNull { it.observacion?.takeIf { o -> o.isNotBlank() } }?.let {
                                Text("📝 $it", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("$litrosTot L", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = NaranjaProduccion)
                            Text("leche usada", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                    HorizontalDivider()
                    Spacer(Modifier.height(4.dp))
                    lotesDia.forEach { lote ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("  ${lote.producto.icono} ${lote.nombreProducto ?: lote.producto.etiqueta} · ${lote.codigoLote}", style = MaterialTheme.typography.labelSmall)
                            Text("${lote.cantidadFormateada} · ${lote.litrosUsados} L", style = MaterialTheme.typography.labelSmall, color = NaranjaProduccion, fontWeight = FontWeight.Bold)
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
private fun VentasTab(viewModel: PlantaViewModel = koinViewModel()) {
    var tabVentas        by remember { mutableStateOf(0) }
    val estadoVentas     by viewModel.ventas.collectAsStateWithLifecycle()
    val estadoLotes      by viewModel.lotes.collectAsStateWithLifecycle()
    val envio            by viewModel.envio.collectAsStateWithLifecycle()
    var mostrarFormulario by remember { mutableStateOf(false) }

    LaunchedEffect(envio) {
        if (envio is EstadoEnvio.Exito && mostrarFormulario) {
            mostrarFormulario = false
            tabVentas = 2
        }
    }

    if (mostrarFormulario) {
        FormularioVenta(
            lotesDisponibles = estadoLotes.datosOVacio(),
            enviando = envio == EstadoEnvio.Enviando,
            mensajeError = (envio as? EstadoEnvio.Error)?.mensaje,
            alGuardar = { lote, cliente, cantidad, precio, fecha, observacion ->
                viewModel.registrarVenta(lote, cliente, cantidad, precio, fecha, observacion)
            },
            alCancelar = { viewModel.limpiarEnvio(); mostrarFormulario = false }
        )
        return
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = tabVentas) {
            Tab(selected = tabVentas == 0, onClick = { tabVentas = 0 }, text = { Text("📊 Stock") })
            Tab(selected = tabVentas == 1, onClick = { tabVentas = 1 }, text = { Text("+ Venta") })
            Tab(selected = tabVentas == 2, onClick = { tabVentas = 2 }, text = { Text("📋 Historial") })
        }
        MensajePlanta(envio, viewModel::limpiarEnvio)
        when (tabVentas) {
            0 -> if (estadoLotes is EstadoUi.Exito) StockTab(estadoLotes.datosOVacio())
                 else CargaOError(estadoLotes, onReintentar = viewModel::cargar)
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
                            onClick = { viewModel.limpiarEnvio(); mostrarFormulario = true },
                            colors  = ButtonDefaults.buttonColors(containerColor = VerdeHuata),
                            modifier = Modifier.padding(horizontal = 32.dp).fillMaxWidth()
                        ) {
                            Text("+ Nueva venta / despacho", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            2 -> if (estadoVentas is EstadoUi.Exito) HistorialVentasTab(estadoVentas.datosOVacio())
                 else CargaOError(estadoVentas, onReintentar = viewModel::cargar)
        }
    }
}

// ── Stock ─────────────────────────────────────────────────────────────────

@Composable
private fun StockTab(lotes: List<LoteProduccion>) {
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
            Text("Según el inventario del servidor (producido − vendido)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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

        items(lotes) { lote ->
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
                                Text("${lote.nombreProducto ?: lote.producto.etiqueta} · ${lote.fecha}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                        Text("Total acumulado (sin IGV)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(formatearMonto(totalGeneral), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = VerdeHuata)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("${ventas.size}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = VerdeHuata)
                        Text("ventas", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
        items(ventas) { venta -> TarjetaVenta(venta) }
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
                        Text("${venta.nombreProducto ?: venta.producto.etiqueta} · ${venta.cantidadFormateada}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${venta.fecha}${venta.codigoFactura?.let { " · $it" }.orEmpty()}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
private fun FormularioVenta(
    lotesDisponibles: List<LoteProduccion>,
    enviando: Boolean,
    mensajeError: String?,
    alGuardar: (lote: LoteProduccion, cliente: String, cantidad: Double, precio: Double, fecha: String, observacion: String?) -> Unit,
    alCancelar: () -> Unit
) {
    val lotes            = lotesDisponibles.filter { stockDisponible(it) > 0 }
    var loteSeleccionado by remember { mutableStateOf<LoteProduccion?>(null) }
    var dropLoteExpanded by remember { mutableStateOf(false) }
    var cliente          by remember { mutableStateOf("") }
    var cantidad         by remember { mutableStateOf("") }
    var precio           by remember { mutableStateOf("") }
    var fecha            by remember { mutableStateOf(fechaHoyPantalla()) }
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
                        Text("Subtotal (+ IGV en la factura):", style = MaterialTheme.typography.bodyMedium)
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
                        loteSeleccionado!!, cliente.trim(), cantidad.toDoubleOrNull() ?: 0.0,
                        precio.toDoubleOrNull() ?: 0.0, fecha.trim(), observacion.ifBlank { null }
                    )
                },
                enabled  = formularioValido && !enviando,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = VerdeHuata)
            ) { Text(if (enviando) "Guardando..." else "Guardar venta / despacho", fontWeight = FontWeight.Bold) }
        }
        if (mensajeError != null) {
            item { Text(mensajeError, color = RojoAlerta, style = MaterialTheme.typography.bodySmall) }
        }
    }
}
