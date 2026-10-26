package com.example.acopiodeleche.ui.pantallas.admin

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.acopiodeleche.domain.model.ControlCalidad
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.acopiodeleche.data.remote.dto.MOTIVOS_RECHAZO
import com.example.acopiodeleche.domain.model.Productor
import com.example.acopiodeleche.ui.estado.CargaOError
import com.example.acopiodeleche.ui.estado.EstadoEnvio
import com.example.acopiodeleche.ui.estado.EstadoUi
import com.example.acopiodeleche.ui.estado.datosOVacio
import com.example.acopiodeleche.ui.estado.fechaHoyPantalla
import com.example.acopiodeleche.domain.model.ResultadoCalidad
import com.example.acopiodeleche.domain.model.Sancion
import com.example.acopiodeleche.domain.model.SesionActual
import com.example.acopiodeleche.domain.model.TipoSancion
import com.example.acopiodeleche.ui.tema.HuataColores
import org.koin.compose.viewmodel.koinViewModel

private val VerdeHuata  = HuataColores.Verde
private val RojoAdmin   = HuataColores.Rojo
private val AmarilloAdv = HuataColores.DoradoOscuro

@Composable
fun CalidadAdminScreen(
    onVolver: () -> Unit,
    modifier: Modifier = Modifier
) {
    var tabActual by remember { mutableStateOf(0) }
    val tabs = listOf("Análisis", "Sanciones")

    Column(modifier = modifier.fillMaxSize()) {

        // El título está en la barra superior; aquí solo el resumen
        Text(
            "Evaluación de análisis y sanciones a productores",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
        )

        TabRow(selectedTabIndex = tabActual) {
            tabs.forEachIndexed { i, t ->
                Tab(selected = tabActual == i, onClick = { tabActual = i }, text = { Text(t) })
            }
        }

        when (tabActual) {
            0 -> AnalisisAdminTab(onIrSanciones = { tabActual = 1 })
            1 -> SancionesTab()
        }
    }
}

// ── ANÁLISIS — ADMIN EVALÚA ───────────────────────────────────────────────

@Composable
private fun AnalisisAdminTab(
    onIrSanciones: () -> Unit,
    viewModel: CalidadAdminViewModel = koinViewModel()
) {
    // Análisis registrados por control de calidad (app o web), desde Laravel
    val estadoAnalisis by viewModel.analisis.collectAsStateWithLifecycle()
    val envio by viewModel.envio.collectAsStateWithLifecycle()
    if (estadoAnalisis !is EstadoUi.Exito) {
        CargaOError(estadoAnalisis, onReintentar = viewModel::cargar)
        return
    }
    val analisis = estadoAnalisis.datosOVacio()

    val pendientes  = analisis.count { it.resultado == ResultadoCalidad.PENDIENTE }
    val observados  = analisis.count { it.resultado == ResultadoCalidad.OBSERVADO }
    val aptos       = analisis.count { it.resultado == ResultadoCalidad.APTO }
    val noAptos     = analisis.count { it.resultado == ResultadoCalidad.NO_APTO }

    if (analisis.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🔬", fontSize = 48.sp)
                Spacer(Modifier.height(8.dp))
                Text("No hay análisis registrados", style = MaterialTheme.typography.titleMedium)
                Text(
                    "El personal de calidad debe registrar los análisis primero",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        (envio as? EstadoEnvio.Error)?.let { error ->
            item { Text(error.mensaje, color = RojoAdmin, style = MaterialTheme.typography.bodySmall) }
        }
        // Resumen
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ChipResumen("⏳ $pendientes", "Pendientes", AmarilloAdv, Modifier.weight(1f))
                ChipResumen("✅ $aptos", "Aptos", VerdeHuata, Modifier.weight(1f))
                ChipResumen("👁 $observados", "Observados", HuataColores.NaranjaOscuro, Modifier.weight(1f))
                ChipResumen("❌ $noAptos", "No aptos", RojoAdmin, Modifier.weight(1f))
            }
        }

        // Advertencia si hay múltiples observaciones por productor
        val productoresConProblemas = analisis
            .filter { it.resultado == ResultadoCalidad.OBSERVADO || it.resultado == ResultadoCalidad.NO_APTO }
            .groupBy { it.idProductor }
            .filter { it.value.size >= 2 }

        if (productoresConProblemas.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = RojoAdmin.copy(alpha = 0.08f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            "⚠️ Productores con problemas repetidos",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = RojoAdmin
                        )
                        Spacer(Modifier.height(4.dp))
                        productoresConProblemas.forEach { (idProd, regs) ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    regs.first().nombreProductor ?: idProd,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "${regs.size} análisis con problemas",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = RojoAdmin
                                )
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        Button(
                            onClick = onIrSanciones,
                            colors = ButtonDefaults.buttonColors(containerColor = RojoAdmin),
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Ir a Sanciones →") }
                    }
                }
            }
        }

        // Lista de análisis
        item {
            Text(
                "Todos los análisis",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }

        items(analisis) { cal ->
            TarjetaAnalisisAdmin(
                analisis = cal,
                onEvaluar = { nuevoResultado, motivo -> viewModel.evaluar(cal, nuevoResultado, motivo) }
            )
        }
    }
}

@Composable
private fun ChipResumen(valor: String, etiqueta: String, color: Color, modifier: Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(valor, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = color)
            Text(etiqueta, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun TarjetaAnalisisAdmin(
    analisis: ControlCalidad,
    onEvaluar: (ResultadoCalidad, String?) -> Unit
) {
    val colorResultado = when (analisis.resultado) {
        ResultadoCalidad.APTO      -> VerdeHuata
        ResultadoCalidad.NO_APTO   -> RojoAdmin
        ResultadoCalidad.OBSERVADO -> HuataColores.NaranjaOscuro
        ResultadoCalidad.PENDIENTE -> AmarilloAdv
    }

    var mostrarEvaluacion by remember { mutableStateOf(false) }
    var eligiendoMotivo by remember { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Encabezado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        analisis.nombreProductor ?: "Productor",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "${analisis.fecha} ${analisis.hora} · ${analisis.codigoAnalisis}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Box(
                    modifier = Modifier
                        .background(colorResultado.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        analisis.resultado.etiqueta,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = colorResultado
                    )
                }
            }

            // Parámetros clave
            val params = buildList {
                analisis.grasa?.let { add("Grasa" to "$it%") }
                analisis.aguaAnadida?.let { add("Agua añadida" to "$it%") }
                analisis.ph?.let { add("pH" to "$it") }
                analisis.densidad?.let { add("Densidad" to "$it") }
            }
            if (params.isNotEmpty()) {
                Spacer(Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    params.forEach { (label, valor) ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(valor, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            // Advertencia de agua añadida alta
            if ((analisis.aguaAnadida ?: 0.0) > 5.0) {
                Spacer(Modifier.height(6.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = RojoAdmin.copy(alpha = 0.1f))
                ) {
                    Text(
                        "⚠️ Agua añadida: ${analisis.aguaAnadida}% — Posible adulteración. Requiere evaluación.",
                        style = MaterialTheme.typography.bodySmall,
                        color = RojoAdmin,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            // Botones de evaluación (solo si está pendiente)
            if (analisis.resultado == ResultadoCalidad.PENDIENTE || mostrarEvaluacion) {
                Spacer(Modifier.height(8.dp))
                Text("Evaluar resultado:", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(
                        onClick = { onEvaluar(ResultadoCalidad.APTO, null); mostrarEvaluacion = false },
                        colors = ButtonDefaults.buttonColors(containerColor = VerdeHuata),
                        modifier = Modifier.weight(1f)
                    ) { Text("✅ Apto", style = MaterialTheme.typography.labelSmall) }
                    Button(
                        onClick = { onEvaluar(ResultadoCalidad.OBSERVADO, null); mostrarEvaluacion = false },
                        colors = ButtonDefaults.buttonColors(containerColor = HuataColores.NaranjaOscuro),
                        modifier = Modifier.weight(1f)
                    ) { Text("👁 Observado", style = MaterialTheme.typography.labelSmall) }
                    Button(
                        // Laravel exige el motivo del rechazo
                        onClick = { eligiendoMotivo = true },
                        colors = ButtonDefaults.buttonColors(containerColor = RojoAdmin),
                        modifier = Modifier.weight(1f)
                    ) { Text("❌ No apto", style = MaterialTheme.typography.labelSmall) }
                }
                if (eligiendoMotivo) {
                    Spacer(Modifier.height(6.dp))
                    Text("Motivo del rechazo:", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                    MOTIVOS_RECHAZO.forEach { (clave, etiqueta) ->
                        TextButton(onClick = {
                            onEvaluar(ResultadoCalidad.NO_APTO, clave)
                            eligiendoMotivo = false
                            mostrarEvaluacion = false
                        }) { Text("• $etiqueta", color = RojoAdmin, style = MaterialTheme.typography.labelMedium) }
                    }
                }
            } else {
                Spacer(Modifier.height(4.dp))
                TextButton(onClick = { mostrarEvaluacion = true }) {
                    Text("Cambiar evaluación", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

// ── SANCIONES ─────────────────────────────────────────────────────────────

@Composable
private fun SancionesTab(viewModel: CalidadAdminViewModel = koinViewModel()) {
    // Sanciones registradas en Laravel (las mismas que el módulo de sanciones de la web)
    val estadoSanciones by viewModel.sanciones.collectAsStateWithLifecycle()
    val estadoProductores by viewModel.productores.collectAsStateWithLifecycle()
    val estadoAnalisis by viewModel.analisis.collectAsStateWithLifecycle()
    val envio by viewModel.envio.collectAsStateWithLifecycle()
    val sanciones = estadoSanciones.datosOVacio()
    var mostrarFormulario by remember { mutableStateOf(false) }

    LaunchedEffect(envio) {
        if (envio is EstadoEnvio.Exito) mostrarFormulario = false
    }

    if (mostrarFormulario) {
        FormularioSancion(
            productores = estadoProductores.datosOVacio(),
            analisis = estadoAnalisis.datosOVacio(),
            alGuardar = { nueva -> viewModel.registrarSancion(nueva) },
            alCancelar = { viewModel.limpiarEnvio(); mostrarFormulario = false },
            enviando = envio == EstadoEnvio.Enviando,
            mensajeError = (envio as? EstadoEnvio.Error)?.mensaje
        )
    } else if (estadoSanciones !is EstadoUi.Exito) {
        CargaOError(estadoSanciones, onReintentar = viewModel::cargar)
    } else {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "${sanciones.size} sanciones registradas",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Button(
                    onClick = { viewModel.limpiarEnvio(); mostrarFormulario = true },
                    colors = ButtonDefaults.buttonColors(containerColor = RojoAdmin)
                ) { Text("+ Nueva sanción") }
            }
            HorizontalDivider()

            if (sanciones.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("✅", fontSize = 48.sp)
                        Spacer(Modifier.height(8.dp))
                        Text("No hay sanciones registradas", style = MaterialTheme.typography.titleMedium)
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(sanciones) { sancion ->
                        TarjetaSancion(sancion)
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaSancion(sancion: Sancion) {
    val color = when (sancion.tipo) {
        TipoSancion.ADVERTENCIA         -> AmarilloAdv
        TipoSancion.SUSPENSION_TEMPORAL -> HuataColores.NaranjaOscuro
        TipoSancion.RETIRO              -> RojoAdmin
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.05f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "${sancion.tipo.icono} ${sancion.tipo.etiqueta}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                    Text(
                        sancion.nombreProductor ?: sancion.idProductor,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        sancion.fecha,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Box(
                    modifier = Modifier
                        .background(
                            if (sancion.activa) color.copy(0.15f) else Color.Gray.copy(0.15f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        if (sancion.activa) "Activa" else "Inactiva",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (sancion.activa) color else Color.Gray
                    )
                }
            }
            Spacer(Modifier.height(6.dp))
            Text("Motivo: ${sancion.motivo}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
            Text(sancion.descripcion, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormularioSancion(
    productores: List<Productor>,
    analisis: List<ControlCalidad>,
    alGuardar: (Sancion) -> Unit,
    alCancelar: () -> Unit,
    enviando: Boolean = false,
    mensajeError: String? = null
) {
    var productorSeleccionado by remember { mutableStateOf("") }
    var dropExpanded by remember { mutableStateOf(false) }
    var tipoSeleccionado by remember { mutableStateOf(TipoSancion.ADVERTENCIA) }
    var dropTipoExpanded by remember { mutableStateOf(false) }
    var motivo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf(fechaHoyPantalla()) }

    val analisisProblemas = if (productorSeleccionado.isNotBlank())
        analisis.count {
            it.idProductor == productorSeleccionado &&
            (it.resultado == ResultadoCalidad.OBSERVADO || it.resultado == ResultadoCalidad.NO_APTO)
        } else 0

    val formularioValido = productorSeleccionado.isNotBlank()
        && motivo.isNotBlank()
        && descripcion.isNotBlank()
        && fecha.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = alCancelar) { Text("← Volver") }
            Text("Nueva sanción", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }

        // Selector productor
        ExposedDropdownMenuBox(expanded = dropExpanded, onExpandedChange = { dropExpanded = it }) {
            OutlinedTextField(
                value = productores.find { it.idProductor == productorSeleccionado }?.nombreCompleto ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Productor *") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(dropExpanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
            )
            ExposedDropdownMenu(expanded = dropExpanded, onDismissRequest = { dropExpanded = false }) {
                productores.forEach { p ->
                    val prob = analisis.count {
                        it.idProductor == p.idProductor &&
                        (it.resultado == ResultadoCalidad.OBSERVADO || it.resultado == ResultadoCalidad.NO_APTO)
                    }
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(p.nombreCompleto)
                                if (prob > 0) Text(
                                    "⚠️ $prob análisis con problemas",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = RojoAdmin
                                )
                            }
                        },
                        onClick = { productorSeleccionado = p.idProductor; dropExpanded = false }
                    )
                }
            }
        }

        if (analisisProblemas >= 2) {
            Card(colors = CardDefaults.cardColors(containerColor = RojoAdmin.copy(alpha = 0.1f))) {
                Text(
                    "⚠️ Este productor tiene $analisisProblemas análisis con problemas. Se recomienda sanción.",
                    style = MaterialTheme.typography.bodySmall,
                    color = RojoAdmin,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        // Tipo sanción
        ExposedDropdownMenuBox(expanded = dropTipoExpanded, onExpandedChange = { dropTipoExpanded = it }) {
            OutlinedTextField(
                value = "${tipoSeleccionado.icono} ${tipoSeleccionado.etiqueta}",
                onValueChange = {},
                readOnly = true,
                label = { Text("Tipo de sanción *") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(dropTipoExpanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
            )
            ExposedDropdownMenu(expanded = dropTipoExpanded, onDismissRequest = { dropTipoExpanded = false }) {
                TipoSancion.entries.forEach { tipo ->
                    DropdownMenuItem(
                        text = { Text("${tipo.icono} ${tipo.etiqueta}") },
                        onClick = { tipoSeleccionado = tipo; dropTipoExpanded = false }
                    )
                }
            }
        }

        OutlinedTextField(
            value = motivo,
            onValueChange = { motivo = it },
            label = { Text("Motivo *") },
            placeholder = { Text("ej: Adulteración repetida — agua añadida > 5%") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción detallada *") },
            minLines = 3,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = fecha,
            onValueChange = { fecha = it },
            label = { Text("Fecha *") },
            placeholder = { Text("dd/MM/yyyy") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                alGuardar(
                    Sancion(
                        id = "",   // lo asigna el backend
                        idProductor = productorSeleccionado,
                        tipo = tipoSeleccionado,
                        motivo = motivo.trim(),
                        descripcion = descripcion.trim(),
                        fecha = fecha.trim(),
                        idAdministrador = SesionActual.usuario?.id ?: "",
                        activa = true
                    )
                )
            },
            enabled = formularioValido && !enviando,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = RojoAdmin)
        ) {
            Text(if (enviando) "Registrando..." else "Emitir ${tipoSeleccionado.etiqueta}", fontWeight = FontWeight.Bold)
        }
        if (mensajeError != null) {
            Text(mensajeError, color = RojoAdmin, style = MaterialTheme.typography.bodySmall)
        }
    }
}
