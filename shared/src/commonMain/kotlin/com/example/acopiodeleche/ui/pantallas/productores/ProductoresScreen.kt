package com.example.acopiodeleche.ui.pantallas.productores

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.acopiodeleche.domain.model.DatosMock
import com.example.acopiodeleche.domain.model.Productor

private val VerdeProductor = Color(0xFF1B5E20)
private val VerdeClaro     = Color(0xFF2E7D32)

@Composable
fun ProductoresScreen(
    soloLectura: Boolean = false,
    onVolver: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var productores       by remember { mutableStateOf(DatosMock.productores.toList()) }
    var mostrarFormulario by remember { mutableStateOf(false) }
    var busqueda          by remember { mutableStateOf("") }
    var filtroComunidad   by remember { mutableStateOf("") }
    var filtroEstado      by remember { mutableStateOf("Todos") }

    if (mostrarFormulario) {
        FormularioProductor(
            alGuardar = { nuevo ->
                DatosMock.productores.add(nuevo)
                productores = DatosMock.productores.toList()
                mostrarFormulario = false
            },
            alCancelar = { mostrarFormulario = false }
        )
        return
    }

    val comunidades = productores.map { it.comunidad }.distinct().sorted()

    val productorosFiltrados = remember(busqueda, filtroComunidad, filtroEstado, productores) {
        productores.filter { p ->
            val textoOk = busqueda.isBlank() ||
                p.nombreCompleto.contains(busqueda, ignoreCase = true) ||
                p.dni.contains(busqueda) ||
                p.telefono.contains(busqueda) ||
                p.comunidad.contains(busqueda, ignoreCase = true)
            val comunidadOk = filtroComunidad.isBlank() || p.comunidad == filtroComunidad
            val estadoOk = when (filtroEstado) {
                "Activos"   -> p.estado
                "Inactivos" -> !p.estado
                else        -> true
            }
            textoOk && comunidadOk && estadoOk
        }
    }

    val totalActivos  = productores.count { it.estado }
    val totalInactivos = productores.count { !it.estado }

    Column(modifier = modifier.fillMaxSize()) {

        // ── Header ──────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(VerdeProductor)
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            if (onVolver != null) {
                TextButton(
                    onClick = onVolver,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Text("←", color = Color.White, fontSize = 20.sp)
                }
            }
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "🌾 Productores",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "$totalActivos activos · $totalInactivos inactivos · ${productores.size} total",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(0.8f)
                )
            }
            if (!soloLectura) {
                Button(
                    onClick = { mostrarFormulario = true },
                    modifier = Modifier.align(Alignment.CenterEnd),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text("+ Nuevo", color = VerdeProductor, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        // ── Tarjetas resumen ─────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TarjetaEstadistica("🟢", "$totalActivos", "Activos", VerdeClaro, Modifier.weight(1f))
            TarjetaEstadistica("🔴", "$totalInactivos", "Inactivos", Color(0xFFC62828), Modifier.weight(1f))
            TarjetaEstadistica("📍", "${comunidades.size}", "Comunidades", Color(0xFF1565C0), Modifier.weight(1f))
        }

        // ── Búsqueda ─────────────────────────────────────────────────────
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            OutlinedTextField(
                value = busqueda,
                onValueChange = { busqueda = it },
                label = { Text("🔍 Buscar productor") },
                placeholder = { Text("Nombre, DNI, teléfono...") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    if (busqueda.isNotBlank()) {
                        TextButton(onClick = { busqueda = "" }) { Text("✕") }
                    }
                }
            )
            Spacer(Modifier.height(8.dp))

            // Filtros por comunidad
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                item {
                    FiltroChipProductor("Todas", filtroComunidad.isBlank()) {
                        filtroComunidad = ""
                    }
                }
                items(comunidades) { com ->
                    FiltroChipProductor(com, filtroComunidad == com) {
                        filtroComunidad = if (filtroComunidad == com) "" else com
                    }
                }
            }
            Spacer(Modifier.height(6.dp))

            // Filtro estado
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("Todos", "Activos", "Inactivos").forEach { est ->
                    FiltroChipProductor(est, filtroEstado == est) {
                        filtroEstado = est
                    }
                }
            }
            Spacer(Modifier.height(6.dp))
            Text(
                "${productorosFiltrados.size} resultados",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))

        // ── Lista ────────────────────────────────────────────────────────
        if (productorosFiltrados.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🌾", fontSize = 48.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        if (busqueda.isNotBlank()) "Sin resultados para \"$busqueda\""
                        else "No hay productores registrados",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                    if (!soloLectura && busqueda.isBlank()) {
                        Spacer(Modifier.height(8.dp))
                        Button(
                            onClick = { mostrarFormulario = true },
                            colors = ButtonDefaults.buttonColors(containerColor = VerdeClaro)
                        ) { Text("+ Registrar primer productor") }
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(productorosFiltrados, key = { it.idProductor }) { productor ->
                    TarjetaProductorMejorada(
                        productor = productor,
                        onToggleEstado = if (!soloLectura) {
                            {
                                val idx = DatosMock.productores.indexOfFirst { it.idProductor == productor.idProductor }
                                if (idx != -1) {
                                    DatosMock.productores[idx] = productor.copy(estado = !productor.estado)
                                    productores = DatosMock.productores.toList()
                                }
                            }
                        } else null
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// TARJETA MEJORADA
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun TarjetaProductorMejorada(
    productor: Productor,
    onToggleEstado: (() -> Unit)?
) {
    val litrosTotal = DatosMock.registrosAcopio
        .filter { it.idProductor == productor.idProductor }
        .sumOf { it.litros }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (productor.estado)
                MaterialTheme.colorScheme.surface
            else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            if (productor.estado) VerdeClaro.copy(0.12f) else Color.Gray.copy(0.12f),
                            RoundedCornerShape(50)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        productor.nombres.take(1).uppercase(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (productor.estado) VerdeClaro else Color.Gray
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            productor.nombreCompleto,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .background(
                                    if (productor.estado) VerdeClaro.copy(0.15f) else Color.Gray.copy(0.15f),
                                    RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                if (productor.estado) "Activo" else "Inactivo",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (productor.estado) VerdeClaro else Color.Gray,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Text(
                        "📍 ${productor.comunidad}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "📋 DNI: ${productor.dni}  ·  📞 ${productor.telefono}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                // Litros
                if (litrosTotal > 0) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            "$litrosTotal L",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = VerdeClaro
                        )
                        Text(
                            "acopiado",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Botón toggle estado
            if (onToggleEstado != null) {
                Spacer(Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onToggleEstado,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = if (productor.estado) Color(0xFFC62828) else VerdeClaro
                        )
                    ) {
                        Text(
                            if (productor.estado) "🔴 Desactivar" else "🟢 Activar",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// HELPERS
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun TarjetaEstadistica(
    icono: String,
    valor: String,
    label: String,
    color: Color,
    modifier: Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(0.08f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(icono, fontSize = 18.sp)
            Text(valor, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun FiltroChipProductor(label: String, seleccionado: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .background(
                if (seleccionado) VerdeClaro else VerdeClaro.copy(0.1f),
                RoundedCornerShape(50)
            )
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = if (seleccionado) Color.White else VerdeClaro
        )
    }
}
