package com.example.acopiodeleche.ui.pantallas.productores

import androidx.compose.material3.AlertDialog
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
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.acopiodeleche.domain.model.Productor
import com.example.acopiodeleche.ui.estado.CargaOError
import com.example.acopiodeleche.ui.estado.EstadoEnvio
import com.example.acopiodeleche.ui.estado.EstadoUi
import com.example.acopiodeleche.ui.estado.datosOVacio
import com.example.acopiodeleche.ui.tema.HuataColores
import org.koin.compose.viewmodel.koinViewModel

private val VerdeProductor = HuataColores.VerdeOscuro
private val VerdeClaro     = HuataColores.Verde

@Composable
fun ProductoresScreen(
    soloLectura: Boolean = false,
    onVolver: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    viewModel: ProductoresViewModel = koinViewModel()
) {
    // Padrón de productores leído desde Laravel (mismos datos que la web)
    val estadoProductores by viewModel.productores.collectAsStateWithLifecycle()
    val envio by viewModel.envio.collectAsStateWithLifecycle()
    val productores = estadoProductores.datosOVacio()
    var mostrarFormulario by remember { mutableStateOf(false) }
    var productorEditando by remember { mutableStateOf<Productor?>(null) }
    var productorAEliminar by remember { mutableStateOf<Productor?>(null) }
    var busqueda          by remember { mutableStateOf("") }
    var filtroComunidad   by remember { mutableStateOf("") }
    var filtroEstado      by remember { mutableStateOf("Todos") }

    LaunchedEffect(envio) {
        if (envio is EstadoEnvio.Exito) {
            mostrarFormulario = false
            productorEditando = null
        }
    }

    if (mostrarFormulario || productorEditando != null) {
        val editando = productorEditando
        FormularioProductor(
            alGuardar = { datos, correo, contrasena ->
                if (editando != null) viewModel.actualizar(datos) else viewModel.registrar(datos, correo, contrasena)
            },
            alCancelar = { viewModel.limpiarEnvio(); mostrarFormulario = false; productorEditando = null },
            enviando = envio == EstadoEnvio.Enviando,
            mensajeError = (envio as? EstadoEnvio.Error)?.mensaje,
            productorEditar = editando
        )
        return
    }

    productorAEliminar?.let { productor ->
        AlertDialog(
            onDismissRequest = { productorAEliminar = null },
            title = { Text("¿Eliminar a ${productor.nombreCompleto}?") },
            text = {
                Text(
                    "También se elimina su cuenta de acceso. Solo es posible si no tiene entregas, " +
                        "pagos ni sanciones; si los tiene, desactívelo."
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.eliminar(productor); productorAEliminar = null },
                    colors = ButtonDefaults.buttonColors(containerColor = HuataColores.Rojo)
                ) { Text("Eliminar") }
            },
            dismissButton = { TextButton(onClick = { productorAEliminar = null }) { Text("Cancelar") } }
        )
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

        // ── Resumen y acción (el título está en la barra superior) ─────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onVolver != null) {
                TextButton(onClick = onVolver) { Text("← Volver") }
            }
            Text(
                "$totalActivos activos · $totalInactivos inactivos · ${productores.size} total",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )
            if (!soloLectura) {
                Button(
                    onClick = { viewModel.limpiarEnvio(); mostrarFormulario = true },
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text("+ Nuevo", fontWeight = FontWeight.Bold, fontSize = 13.sp)
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
            TarjetaEstadistica("🔴", "$totalInactivos", "Inactivos", HuataColores.Rojo, Modifier.weight(1f))
            TarjetaEstadistica("📍", "${comunidades.size}", "Comunidades", HuataColores.Azul, Modifier.weight(1f))
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

        when (val actual = envio) {
            is EstadoEnvio.Exito -> Text(
                actual.mensaje,
                color = VerdeClaro,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            is EstadoEnvio.Error -> Text(
                actual.mensaje,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            else -> Unit
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))

        // ── Lista ────────────────────────────────────────────────────────
        if (estadoProductores !is EstadoUi.Exito) {
            CargaOError(estadoProductores, onReintentar = viewModel::cargar)
        } else if (productorosFiltrados.isEmpty()) {
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
                            onClick = { viewModel.limpiarEnvio(); mostrarFormulario = true },
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
                            { viewModel.cambiarEstado(productor) }
                        } else null,
                        onEditar = if (!soloLectura) {
                            { viewModel.limpiarEnvio(); productorEditando = productor }
                        } else null,
                        onEliminar = if (!soloLectura) {
                            { productorAEliminar = productor }
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
    onToggleEstado: (() -> Unit)?,
    onEditar: (() -> Unit)? = null,
    onEliminar: (() -> Unit)? = null
) {
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
                    onEditar?.let {
                        TextButton(onClick = it) {
                            Text("✏️ Editar", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        }
                    }
                    onEliminar?.let {
                        TextButton(
                            onClick = it,
                            colors = ButtonDefaults.textButtonColors(contentColor = HuataColores.RojoOscuro)
                        ) {
                            Text("🗑️ Eliminar", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        }
                    }
                    TextButton(
                        onClick = onToggleEstado,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = if (productor.estado) HuataColores.Rojo else VerdeClaro
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
