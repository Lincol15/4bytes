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
import com.example.acopiodeleche.domain.model.DatosMock
import com.example.acopiodeleche.domain.model.LoteProduccion
import com.example.acopiodeleche.domain.model.SesionActual
import com.example.acopiodeleche.domain.model.TipoProducto
import com.example.acopiodeleche.domain.model.VentaSalida
import kotlin.random.Random

private val AzulPlanta = Color(0xFF37474F)
private val VerdeHuata = Color(0xFF2E7D32)
private val NaranjaProduccion = Color(0xFFE65100)

@Composable
fun TrabajadorPlantaScreen(
    onCerrarSesion: () -> Unit,
    modifier: Modifier = Modifier
) {
    val usuario = SesionActual.usuario
    var tabActual by remember { mutableStateOf(0) }
    val tabs = listOf("Inicio", "Producción", "Ventas/Salidas")

    Column(modifier = modifier.fillMaxSize()) {

        // Encabezado
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(AzulPlanta)
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = "Planta — Ecolácteos Huata",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = usuario?.nombreCompleto ?: "Trabajador",
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

        TabRow(selectedTabIndex = tabActual) {
            tabs.forEachIndexed { i, t ->
                Tab(selected = tabActual == i, onClick = { tabActual = i }, text = { Text(t) })
            }
        }

        when (tabActual) {
            0 -> InicioTrabajador()
            1 -> ProduccionTab()
            2 -> VentasTab()
        }
    }
}

// ── INICIO ────────────────────────────────────────────────────────────────

@Composable
private fun InicioTrabajador() {
    val lotes = DatosMock.lotesProduccion
    val ventas = DatosMock.ventasSalidas

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Resumen de producción", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TarjetaResumen("🧀", "${lotes.count { it.producto == TipoProducto.QUESO }}", "Lotes queso", NaranjaProduccion, Modifier.weight(1f))
                TarjetaResumen("🥛", "${lotes.count { it.producto == TipoProducto.YOGUR }}", "Lotes yogur", AzulPlanta, Modifier.weight(1f))
            }
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TarjetaResumen("📦", "${ventas.size}", "Ventas\nrealizadas", VerdeHuata, Modifier.weight(1f))
                TarjetaResumen(
                    "💰",
                    "S/ ${ventas.sumOf { it.total }.let { t -> "${t.toLong()}.${((t - t.toLong()) * 100).toLong().toString().padStart(2,'0')}" }}",
                    "Total ventas",
                    Color(0xFF6A1B9A),
                    Modifier.weight(1f)
                )
            }
        }
        item {
            HorizontalDivider()
            Spacer(Modifier.height(4.dp))
            Text("Últimos lotes producidos", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        }
        items(lotes.takeLast(3).reversed()) { lote ->
            TarjetaLotePequena(lote)
        }
    }
}

@Composable
private fun TarjetaResumen(icono: String, valor: String, etiqueta: String, color: Color, modifier: Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(icono, fontSize = 24.sp)
            Text(valor, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color)
            Text(etiqueta, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun TarjetaLotePequena(lote: LoteProduccion) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(1.dp)) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(lote.producto.icono, fontSize = 24.sp)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(lote.codigoLote, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                Text(lote.resumen, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(lote.cantidadFormateada, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = NaranjaProduccion)
        }
    }
}

// ── PRODUCCIÓN ────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProduccionTab() {
    var lotes by remember { mutableStateOf(DatosMock.lotesProduccion.toList()) }
    var mostrarFormulario by remember { mutableStateOf(false) }

    if (mostrarFormulario) {
        FormularioLote(
            alGuardar = { nuevo ->
                DatosMock.lotesProduccion.add(nuevo)
                lotes = DatosMock.lotesProduccion.toList()
                mostrarFormulario = false
            },
            alCancelar = { mostrarFormulario = false }
        )
    } else {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("${lotes.size} lotes registrados", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Button(
                    onClick = { mostrarFormulario = true },
                    colors = ButtonDefaults.buttonColors(containerColor = NaranjaProduccion)
                ) { Text("+ Nuevo lote") }
            }
            HorizontalDivider()
            if (lotes.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🧀", fontSize = 48.sp)
                        Spacer(Modifier.height(8.dp))
                        Text("No hay lotes registrados", style = MaterialTheme.typography.titleMedium)
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(lotes) { lote -> TarjetaLote(lote) }
                }
            }
        }
    }
}

@Composable
private fun TarjetaLote(lote: LoteProduccion) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(lote.producto.icono, fontSize = 28.sp)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(lote.codigoLote, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text(lote.producto.etiqueta, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Fecha: ${lote.fecha}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(lote.cantidadFormateada, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = NaranjaProduccion)
                    Text("de ${lote.litrosUsados} L leche", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            if (!lote.observacion.isNullOrBlank()) {
                Spacer(Modifier.height(4.dp))
                Text("Obs: ${lote.observacion}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (!lote.fechaVencimiento.isNullOrBlank()) {
                Text("Vence: ${lote.fechaVencimiento}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormularioLote(alGuardar: (LoteProduccion) -> Unit, alCancelar: () -> Unit) {
    var productoSeleccionado by remember { mutableStateOf(TipoProducto.QUESO) }
    var dropProductoExpanded by remember { mutableStateOf(false) }
    var litrosUsados by remember { mutableStateOf("") }
    var cantidadProducida by remember { mutableStateOf("") }
    var unidadMedida by remember { mutableStateOf("kg") }
    var dropUnidadExpanded by remember { mutableStateOf(false) }
    var fecha by remember { mutableStateOf("") }
    var fechaVencimiento by remember { mutableStateOf("") }
    var observacion by remember { mutableStateOf("") }

    val formularioValido = litrosUsados.toDoubleOrNull() != null
        && cantidadProducida.toDoubleOrNull() != null
        && fecha.isNotBlank()

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = alCancelar) { Text("← Volver") }
                Text("Nuevo lote de producción", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            }
        }
        item {
            ExposedDropdownMenuBox(expanded = dropProductoExpanded, onExpandedChange = { dropProductoExpanded = it }) {
                OutlinedTextField(
                    value = "${productoSeleccionado.icono} ${productoSeleccionado.etiqueta}",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Producto *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(dropProductoExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
                )
                ExposedDropdownMenu(expanded = dropProductoExpanded, onDismissRequest = { dropProductoExpanded = false }) {
                    TipoProducto.entries.forEach { tipo ->
                        DropdownMenuItem(
                            text = { Text("${tipo.icono} ${tipo.etiqueta}") },
                            onClick = {
                                productoSeleccionado = tipo
                                unidadMedida = if (tipo == TipoProducto.YOGUR) "L" else "kg"
                                dropProductoExpanded = false
                            }
                        )
                    }
                }
            }
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = litrosUsados,
                    onValueChange = { litrosUsados = it },
                    label = { Text("Litros de leche *") },
                    suffix = { Text("L") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = cantidadProducida,
                    onValueChange = { cantidadProducida = it },
                    label = { Text("Cantidad producida *") },
                    suffix = { Text(unidadMedida) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f)
                )
            }
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = fecha,
                    onValueChange = { fecha = it },
                    label = { Text("Fecha producción *") },
                    placeholder = { Text("dd/MM/yyyy") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = fechaVencimiento,
                    onValueChange = { fechaVencimiento = it },
                    label = { Text("Fecha vencimiento") },
                    placeholder = { Text("dd/MM/yyyy") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        item {
            OutlinedTextField(
                value = observacion,
                onValueChange = { observacion = it },
                label = { Text("Observación") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            Button(
                onClick = {
                    val n = Random.nextInt(100, 999)
                    val prefijo = if (productoSeleccionado == TipoProducto.QUESO) "Q" else if (productoSeleccionado == TipoProducto.YOGUR) "Y" else "P"
                    alGuardar(
                        LoteProduccion(
                            id = "lote-$n",
                            codigoLote = "$prefijo-2026-$n",
                            producto = productoSeleccionado,
                            litrosUsados = litrosUsados.toDoubleOrNull() ?: 0.0,
                            cantidadProducida = cantidadProducida.toDoubleOrNull() ?: 0.0,
                            unidadMedida = unidadMedida,
                            fecha = fecha.trim(),
                            idTrabajador = SesionActual.usuario?.id ?: "",
                            observacion = observacion.ifBlank { null },
                            fechaVencimiento = fechaVencimiento.ifBlank { null }
                        )
                    )
                },
                enabled = formularioValido,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NaranjaProduccion)
            ) { Text("Guardar lote de producción", fontWeight = FontWeight.Bold) }
        }
    }
}

// ── VENTAS / SALIDAS ──────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VentasTab() {
    var ventas by remember { mutableStateOf(DatosMock.ventasSalidas.toList()) }
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
    } else {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("${ventas.size} ventas registradas", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        "Total: S/ ${ventas.sumOf { it.total }.let { t -> "${t.toLong()}.${((t - t.toLong()) * 100).toLong().toString().padStart(2,'0')}" }}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = VerdeHuata
                    )
                }
                Button(
                    onClick = { mostrarFormulario = true },
                    colors = ButtonDefaults.buttonColors(containerColor = VerdeHuata)
                ) { Text("+ Registrar venta") }
            }
            HorizontalDivider()
            if (ventas.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📦", fontSize = 48.sp)
                        Spacer(Modifier.height(8.dp))
                        Text("No hay ventas registradas", style = MaterialTheme.typography.titleMedium)
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(ventas) { venta -> TarjetaVenta(venta) }
                }
            }
        }
    }
}

@Composable
private fun TarjetaVenta(venta: VentaSalida) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(venta.producto.icono, fontSize = 28.sp)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(venta.cliente, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text("${venta.producto.etiqueta} · ${venta.cantidadFormateada}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Fecha: ${venta.fecha}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(venta.totalFormateado, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = VerdeHuata)
                    Text("S/ ${venta.precioPorUnidad}/${venta.unidadMedida}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            if (!venta.observacion.isNullOrBlank()) {
                Spacer(Modifier.height(4.dp))
                Text("Obs: ${venta.observacion}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormularioVenta(alGuardar: (VentaSalida) -> Unit, alCancelar: () -> Unit) {
    val lotes = DatosMock.lotesProduccion
    var loteSeleccionado by remember { mutableStateOf<LoteProduccion?>(null) }
    var dropLoteExpanded by remember { mutableStateOf(false) }
    var cliente by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }
    var observacion by remember { mutableStateOf("") }

    val formularioValido = loteSeleccionado != null
        && cliente.isNotBlank()
        && cantidad.toDoubleOrNull() != null
        && precio.toDoubleOrNull() != null
        && fecha.isNotBlank()

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = alCancelar) { Text("← Volver") }
                Text("Registrar venta / salida", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            }
        }
        item {
            ExposedDropdownMenuBox(expanded = dropLoteExpanded, onExpandedChange = { dropLoteExpanded = it }) {
                OutlinedTextField(
                    value = loteSeleccionado?.let { "${it.producto.icono} ${it.codigoLote} — ${it.cantidadFormateada}" } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Lote de origen *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(dropLoteExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
                )
                ExposedDropdownMenu(expanded = dropLoteExpanded, onDismissRequest = { dropLoteExpanded = false }) {
                    if (lotes.isEmpty()) {
                        DropdownMenuItem(text = { Text("No hay lotes disponibles") }, onClick = { dropLoteExpanded = false })
                    } else {
                        lotes.forEach { lote ->
                            DropdownMenuItem(
                                text = { Text("${lote.producto.icono} ${lote.codigoLote} · ${lote.cantidadFormateada} · ${lote.fecha}") },
                                onClick = { loteSeleccionado = lote; dropLoteExpanded = false }
                            )
                        }
                    }
                }
            }
        }
        item {
            OutlinedTextField(
                value = cliente, onValueChange = { cliente = it },
                label = { Text("Cliente / Distribuidor *") },
                singleLine = true, modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = cantidad,
                    onValueChange = { cantidad = it },
                    label = { Text("Cantidad *") },
                    suffix = { Text(loteSeleccionado?.unidadMedida ?: "kg") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = precio,
                    onValueChange = { precio = it },
                    label = { Text("Precio por ${loteSeleccionado?.unidadMedida ?: "kg"} *") },
                    prefix = { Text("S/ ") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f)
                )
            }
        }
        // Vista previa del total
        if (cantidad.toDoubleOrNull() != null && precio.toDoubleOrNull() != null) {
            val total = (cantidad.toDouble() * precio.toDouble())
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = VerdeHuata.copy(alpha = 0.1f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total a cobrar:", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            "S/ ${total.let { t -> "${t.toLong()}.${((t - t.toLong()) * 100).toLong().toString().padStart(2,'0')}" }}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = VerdeHuata
                        )
                    }
                }
            }
        }
        item {
            OutlinedTextField(
                value = fecha, onValueChange = { fecha = it },
                label = { Text("Fecha de salida *") },
                placeholder = { Text("dd/MM/yyyy") },
                singleLine = true, modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            OutlinedTextField(
                value = observacion, onValueChange = { observacion = it },
                label = { Text("Observación") },
                minLines = 2, modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            Button(
                onClick = {
                    alGuardar(
                        VentaSalida(
                            id = "venta-${Random.nextInt(1000, 9999)}",
                            idLote = loteSeleccionado!!.id,
                            producto = loteSeleccionado!!.producto,
                            cliente = cliente.trim(),
                            cantidad = cantidad.toDoubleOrNull() ?: 0.0,
                            unidadMedida = loteSeleccionado!!.unidadMedida,
                            precioPorUnidad = precio.toDoubleOrNull() ?: 0.0,
                            fecha = fecha.trim(),
                            idTrabajador = SesionActual.usuario?.id ?: "",
                            observacion = observacion.ifBlank { null }
                        )
                    )
                },
                enabled = formularioValido,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VerdeHuata)
            ) { Text("Guardar venta / salida", fontWeight = FontWeight.Bold) }
        }
    }
}
