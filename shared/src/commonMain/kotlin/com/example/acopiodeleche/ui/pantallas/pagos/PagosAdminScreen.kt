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
import kotlin.random.Random

private val VerdeHuata = Color(0xFF2E7D32)
private val AzulPago = Color(0xFF1565C0)

// ── DÍAS DE LA SEMANA (el pago es semanal, pago cada viernes) ─────────────
private val DIAS_SEMANA = listOf("Jueves", "Viernes", "Sábado", "Domingo", "Lunes", "Martes", "Miércoles")

/**
 * Pantalla de Pagos para el Administrador.
 * El admin genera los pagos semanales de los productores y acopiadores.
 */
@Composable
fun PagosAdminScreen(
    onVolver: () -> Unit,
    modifier: Modifier = Modifier
) {
    var tabActual by remember { mutableStateOf(0) }
    val tabs = listOf("Productores", "Acopiadores")

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
                    "Precio vigente: S/ ${ConfiguracionPlanta.precioPorLitro}/L · Pago cada viernes",
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
            0 -> PagosProductoresTab()
            1 -> PagosAcopiadoresTab()
        }
    }
}

// ── PAGOS A PRODUCTORES ───────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PagosProductoresTab() {
    var pagos by remember { mutableStateOf(DatosMock.pagos.toList()) }
    var mostrarFormulario by remember { mutableStateOf(false) }

    if (mostrarFormulario) {
        FormularioPagoProductor(
            alGuardar = { nuevo ->
                DatosMock.pagos.add(nuevo)
                pagos = DatosMock.pagos.toList()
                mostrarFormulario = false
            },
            alCancelar = { mostrarFormulario = false }
        )
    } else {
        Column {
            // Resumen
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TarjetaResumenPago(
                    icono = "💰",
                    valor = "${pagos.count { it.estado == EstadoPago.PENDIENTE }}",
                    etiqueta = "Pendientes",
                    color = Color(0xFFE65100),
                    modifier = Modifier.weight(1f)
                )
                TarjetaResumenPago(
                    icono = "✅",
                    valor = "${pagos.count { it.estado == EstadoPago.PAGADO }}",
                    etiqueta = "Pagados",
                    color = VerdeHuata,
                    modifier = Modifier.weight(1f)
                )
                TarjetaResumenPago(
                    icono = "📊",
                    valor = "S/ ${pagos.sumOf { it.totalCalculado }.let { t ->
                        "${t.toLong()}.${((t - t.toLong()) * 100).toLong().toString().padStart(2,'0')}"
                    }}",
                    etiqueta = "Total",
                    color = AzulPago,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "${pagos.size} pagos registrados",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Button(
                    onClick = { mostrarFormulario = true },
                    colors = ButtonDefaults.buttonColors(containerColor = VerdeHuata)
                ) { Text("+ Nuevo pago semanal") }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            if (pagos.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay pagos registrados", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(pagos) { pago ->
                        TarjetaPagoProductor(
                            pago = pago,
                            onMarcarPagado = {
                                val idx = DatosMock.pagos.indexOfFirst { it.id == pago.id }
                                if (idx != -1) {
                                    DatosMock.pagos[idx] = pago.copy(
                                        estado = EstadoPago.PAGADO,
                                        fechaPago = "Hoy"
                                    )
                                    pagos = DatosMock.pagos.toList()
                                }
                            }
                        )
                    }
                }
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

            // Encabezado productor
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        productor?.nombreCompleto ?: "Productor",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Código: ${pago.codigoPago}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "Periodo: ${pago.periodoDesde} — ${pago.periodoHasta}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Box(
                    modifier = Modifier
                        .background(colorEstado.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        pago.estado.etiqueta,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = colorEstado
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Detalle diario
            if (pago.detalleDiario.isNotEmpty()) {
                Text(
                    "Detalle semanal:",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
                pago.detalleDiario.forEach { (dia, litrosDia) ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 1.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(dia, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("$litrosDia L", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
            }

            // Cálculo
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total litros:", style = MaterialTheme.typography.bodySmall)
                Text(pago.litrosFormateados, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Precio por litro:", style = MaterialTheme.typography.bodySmall)
                Text(pago.precioPorLitroFormateado, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
            }
            if (pago.descuento > 0) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Descuento:", style = MaterialTheme.typography.bodySmall)
                    Text("- S/ ${pago.descuento}", style = MaterialTheme.typography.bodySmall, color = Color.Red)
                }
            }

            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("TOTAL A PAGAR:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(
                    "S/ ${pago.totalCalculado.let { t -> "${t.toLong()}.${((t - t.toLong()) * 100).toLong().toString().padStart(2,'0')}" }}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = VerdeHuata
                )
            }

            // Botón marcar como pagado
            if (pago.estado == EstadoPago.PENDIENTE) {
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = onMarcarPagado,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = VerdeHuata)
                ) { Text("✅ Marcar como PAGADO") }
            } else if (pago.estado == EstadoPago.PAGADO && pago.fechaPago != null) {
                Spacer(Modifier.height(4.dp))
                Text(
                    "Pagado el: ${pago.fechaPago}",
                    style = MaterialTheme.typography.bodySmall,
                    color = VerdeHuata
                )
            }
        }
    }
}

// ── FORMULARIO NUEVO PAGO SEMANAL ─────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormularioPagoProductor(
    alGuardar: (Pago) -> Unit,
    alCancelar: () -> Unit
) {
    val productores = DatosMock.productores.filter { it.estado }
    var productorSeleccionado by remember { mutableStateOf<Productor?>(null) }
    var dropProductorExpanded by remember { mutableStateOf(false) }

    var periodoDesde by remember { mutableStateOf("") }
    var periodoHasta by remember { mutableStateOf("") }
    var descuento     by remember { mutableStateOf("0") }

    // Litros por día (según días de la semana del ticket)
    val litrosPorDia = remember {
        DIAS_SEMANA.associateWith { mutableStateOf("") }.toMutableMap()
    }

    // Calcular total automáticamente
    val totalLitros = litrosPorDia.values.sumOf { it.value.toDoubleOrNull() ?: 0.0 }
    val precio = ConfiguracionPlanta.precioPorLitro
    val desc = descuento.toDoubleOrNull() ?: 0.0
    val totalAPagar = (totalLitros * precio) - desc

    // Obtener registros reales del productor seleccionado para auto-rellenar
    val registrosProductor = productorSeleccionado?.let { p ->
        DatosMock.registrosAcopio.filter { it.idProductor == p.idProductor }
    } ?: emptyList()

    val formularioValido = productorSeleccionado != null
        && periodoDesde.isNotBlank()
        && periodoHasta.isNotBlank()
        && totalLitros > 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = alCancelar) { Text("← Volver") }
            Text(
                "Nuevo pago semanal",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            "💡 Precio vigente: S/ ${ConfiguracionPlanta.precioPorLitro}/L — configurable desde Administración",
            style = MaterialTheme.typography.bodySmall,
            color = VerdeHuata
        )

        // Selector de productor
        ExposedDropdownMenuBox(expanded = dropProductorExpanded, onExpandedChange = { dropProductorExpanded = it }) {
            OutlinedTextField(
                value = productorSeleccionado?.nombreCompleto ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Productor *") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(dropProductorExpanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
            )
            ExposedDropdownMenu(expanded = dropProductorExpanded, onDismissRequest = { dropProductorExpanded = false }) {
                productores.forEach { p ->
                    DropdownMenuItem(
                        text = { Text("${p.nombreCompleto} — ${p.comunidad}") },
                        onClick = {
                            productorSeleccionado = p
                            dropProductorExpanded = false
                            // Auto-rellenar con registros existentes
                            val regs = DatosMock.registrosAcopio.filter { it.idProductor == p.idProductor }
                            regs.forEach { reg ->
                                // Mapear fecha a día de la semana si coincide
                                DIAS_SEMANA.forEach { dia ->
                                    if (reg.fecha.contains(dia.take(3), ignoreCase = true)) {
                                        litrosPorDia[dia]?.value = reg.litros.toString()
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }

        // Periodo
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = periodoDesde,
                onValueChange = { periodoDesde = it },
                label = { Text("Desde *") },
                placeholder = { Text("dd/MM/yyyy") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = periodoHasta,
                onValueChange = { periodoHasta = it },
                label = { Text("Hasta * (viernes)") },
                placeholder = { Text("dd/MM/yyyy") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }

        // Litros por día
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = VerdeHuata.copy(alpha = 0.05f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "Litros entregados por día",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Ingresa los litros de cada día de la semana",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                DIAS_SEMANA.forEach { dia ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            dia,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.width(90.dp)
                        )
                        OutlinedTextField(
                            value = litrosPorDia[dia]?.value ?: "",
                            onValueChange = { litrosPorDia[dia]?.value = it },
                            label = { Text("Litros") },
                            suffix = { Text("L") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Descuento opcional
        OutlinedTextField(
            value = descuento,
            onValueChange = { descuento = it },
            label = { Text("Descuento (S/)") },
            prefix = { Text("S/ ") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        // Vista previa del cálculo
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = VerdeHuata.copy(alpha = 0.1f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("📊 Resumen del pago", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                HorizontalDivider()
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total litros:", style = MaterialTheme.typography.bodyMedium)
                    Text("$totalLitros L", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Precio × litro:", style = MaterialTheme.typography.bodyMedium)
                    Text("S/ $precio", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("$totalLitros × $precio =", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "S/ ${(totalLitros * precio).let { t -> "${t.toLong()}.${((t - t.toLong()) * 100).toLong().toString().padStart(2,'0')}" }}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                if (desc > 0) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Descuento:", style = MaterialTheme.typography.bodyMedium)
                        Text("- S/ $desc", style = MaterialTheme.typography.bodyMedium, color = Color.Red)
                    }
                }
                HorizontalDivider()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("TOTAL A PAGAR:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Text(
                        "S/ ${totalAPagar.let { t -> "${t.toLong()}.${((t - t.toLong()) * 100).toLong().toString().padStart(2,'0')}" }}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = VerdeHuata
                    )
                }
            }
        }

        // Botón guardar
        Button(
            onClick = {
                val detalle = DIAS_SEMANA
                    .filter { (litrosPorDia[it]?.value?.toDoubleOrNull() ?: 0.0) > 0 }
                    .associate { dia -> dia to (litrosPorDia[dia]?.value?.toDoubleOrNull() ?: 0.0) }

                alGuardar(
                    Pago(
                        id = "pago-${Random.nextInt(1000, 9999)}",
                        idProductor = productorSeleccionado!!.idProductor,
                        codigoPago = "H-${Random.nextInt(10, 99)}",
                        periodoDesde = periodoDesde.trim(),
                        periodoHasta = periodoHasta.trim(),
                        precioPorLitro = ConfiguracionPlanta.precioPorLitro,
                        litrosTotales = totalLitros,
                        detalleDiario = detalle,
                        descuento = desc,
                        estado = EstadoPago.PENDIENTE
                    )
                )
            },
            enabled = formularioValido,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = VerdeHuata)
        ) {
            Text("Guardar pago semanal", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

// ── PAGOS A ACOPIADORES ───────────────────────────────────────────────────

@Composable
private fun PagosAcopiadoresTab() {
    // Agrupar registros de acopio por acopiador
    val registros = DatosMock.registrosAcopio
    val acopiadoresConRegistros = registros
        .groupBy { it.acopiador }
        .map { (nombre, regs) ->
            Triple(nombre, regs.sumOf { it.litros }, regs.size)
        }
        .sortedByDescending { it.second }

    // Tarifa del acopiador — por defecto S/ 0.20 por litro recolectado
    val tarifaAcopiador = 0.20

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
                    Text(
                        "Tarifa: S/ $tarifaAcopiador por litro recolectado",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        "El administrador puede ajustar esta tarifa en Configuración",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        items(acopiadoresConRegistros) { (nombre, litros, cantRegs) ->
            val totalAcopiador = litros * tarifaAcopiador
            Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(
                                "$cantRegs entregas · $litros L recolectados",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                "S/ ${totalAcopiador.let { t -> "${t.toLong()}.${((t - t.toLong()) * 100).toLong().toString().padStart(2,'0')}" }}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = AzulPago
                            )
                            Text(
                                "${litros}L × S/$tarifaAcopiador",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = { /* marcar pagado al acopiador */ },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = AzulPago)
                    ) { Text("Registrar pago al acopiador") }
                }
            }
        }
    }
}
