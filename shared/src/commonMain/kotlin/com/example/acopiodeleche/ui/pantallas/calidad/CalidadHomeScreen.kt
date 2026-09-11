package com.example.acopiodeleche.ui.pantallas.calidad

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
import com.example.acopiodeleche.domain.model.ControlCalidad
import com.example.acopiodeleche.domain.model.DatosMock
import com.example.acopiodeleche.domain.model.OrigenDatos
import com.example.acopiodeleche.domain.model.Productor
import com.example.acopiodeleche.domain.model.ResultadoCalidad
import com.example.acopiodeleche.domain.model.SesionActual
import com.example.acopiodeleche.ui.components.DatosOCR
import com.example.acopiodeleche.ui.components.FechaData
import com.example.acopiodeleche.ui.components.HoraData
import com.example.acopiodeleche.ui.components.ImagenPickerConOCR
import kotlin.random.Random

private val AzulCalidad = Color(0xFF0277BD)
private val VerdeHuata  = Color(0xFF2E7D32)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalidadHomeScreen(
    onCerrarSesion: () -> Unit,
    modifier: Modifier = Modifier
) {
    val usuario   = SesionActual.usuario
    var tabActual by remember { mutableStateOf(0) }
    val tabs      = listOf("Nuevo análisis", "Historial")
    val analisis  = remember { mutableStateOf(DatosMock.analisisCalidad) }

    Column(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(AzulCalidad)
                .padding(16.dp)
        ) {
            Column {
                Text("Control de Calidad", style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.Bold)
                Text("Ecolácteos Huata · ${usuario?.nombreCompleto ?: ""}", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(0.8f))
            }
            Text(
                "Salir",
                modifier = Modifier.align(Alignment.TopEnd).clickable { onCerrarSesion() }.padding(4.dp),
                color = Color.White.copy(0.8f),
                style = MaterialTheme.typography.labelMedium
            )
        }
        TabRow(selectedTabIndex = tabActual) {
            tabs.forEachIndexed { i, t ->
                Tab(selected = tabActual == i, onClick = { tabActual = i }, text = { Text(t) })
            }
        }
        when (tabActual) {
            0 -> FormularioAnalisis(alGuardar = { nuevo ->
                DatosMock.analisisCalidad.add(nuevo)
                analisis.value = DatosMock.analisisCalidad
                tabActual = 1
            })
            1 -> HistorialCalidad(analisis = analisis.value)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// FORMULARIO
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormularioAnalisis(alGuardar: (ControlCalidad) -> Unit) {
    val productores = DatosMock.productores.filter { it.estado }

    var productorSeleccionado by remember { mutableStateOf<Productor?>(null) }
    var dropExpanded          by remember { mutableStateOf(false) }

    var grasa        by remember { mutableStateOf("") }
    var sng          by remember { mutableStateOf("") }
    var densidad     by remember { mutableStateOf("") }
    var proteina     by remember { mutableStateOf("") }
    var lactosa      by remember { mutableStateOf("") }
    var sales        by remember { mutableStateOf("") }
    var totalSolidos by remember { mutableStateOf("") }
    var aguaAnadida  by remember { mutableStateOf("") }
    var puntoCongel  by remember { mutableStateOf("") }
    var ph           by remember { mutableStateOf("") }
    var observacion  by remember { mutableStateOf("") }

    var mostrarPicker  by remember { mutableStateOf(false) }
    var imagenUri      by remember { mutableStateOf("") }
    var textoOCR       by remember { mutableStateOf("") }
    var ocrListo       by remember { mutableStateOf(false) }
    var origenDatos    by remember { mutableStateOf(OrigenDatos.MANUAL) }

    val formularioValido = productorSeleccionado != null

    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {

        item {
            Text("🔬 Nuevo análisis", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("Analizador LACTOMAT — Ecolácteos Huata", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        // Fecha/hora automática
        item {
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = AzulCalidad.copy(0.07f)), shape = RoundedCornerShape(10.dp)) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("🕐", fontSize = 22.sp)
                    Spacer(Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Fecha y hora automáticas", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        Text("${FechaData.hoy().formateada()}  ·  ${HoraData.ahora().formateada()}", style = MaterialTheme.typography.bodySmall, color = AzulCalidad)
                    }
                    Text("Auto", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = AzulCalidad,
                        modifier = Modifier.background(AzulCalidad.copy(0.12f), RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp))
                }
            }
        }

        // Productor
        item {
            ExposedDropdownMenuBox(expanded = dropExpanded, onExpandedChange = { dropExpanded = it }) {
                OutlinedTextField(
                    value = productorSeleccionado?.nombreCompleto ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Productor *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(dropExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
                )
                ExposedDropdownMenu(expanded = dropExpanded, onDismissRequest = { dropExpanded = false }) {
                    productores.forEach { p ->
                        DropdownMenuItem(
                            text = { Text("${p.nombreCompleto} — ${p.comunidad}") },
                            onClick = { productorSeleccionado = p; dropExpanded = false }
                        )
                    }
                }
            }
        }

        // Sección OCR
        item {
            HorizontalDivider()
            Spacer(Modifier.height(4.dp))
            Text("📷 Escanear ticket LACTOMAT", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Text("Toma foto o sube imagen del ticket — los valores se llenan automáticamente.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        // Resultado OCR
        if (ocrListo && imagenUri.isNotBlank()) {
            item {
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = AzulCalidad.copy(0.08f)), shape = RoundedCornerShape(12.dp)) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("✅", fontSize = 24.sp)
                        Spacer(Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Ticket leído correctamente", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = AzulCalidad)
                            Text("Valores auto-rellenados. Verifica y corrige si es necesario.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            if (textoOCR.isNotBlank()) {
                                Spacer(Modifier.height(4.dp))
                                Text("OCR: ${textoOCR.take(80)}…", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        TextButton(onClick = { imagenUri = ""; ocrListo = false; origenDatos = OrigenDatos.MANUAL }) {
                            Text("✕", color = Color.Red, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Botón / picker
        item {
            if (!mostrarPicker) {
                Button(
                    onClick = { mostrarPicker = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = AzulCalidad)
                ) {
                    Text(if (!ocrListo) "📷 Escanear / Subir ticket" else "📷 Volver a escanear", fontWeight = FontWeight.Bold)
                }
            } else {
                ImagenPickerConOCR(
                    onDatos = { datos: DatosOCR ->
                        if (datos.grasa.isNotBlank())        grasa        = datos.grasa
                        if (datos.sng.isNotBlank())          sng          = datos.sng
                        if (datos.densidad.isNotBlank())     densidad     = datos.densidad
                        if (datos.proteina.isNotBlank())     proteina     = datos.proteina
                        if (datos.lactosa.isNotBlank())      lactosa      = datos.lactosa
                        if (datos.sales.isNotBlank())        sales        = datos.sales
                        if (datos.totalSolidos.isNotBlank()) totalSolidos = datos.totalSolidos
                        if (datos.aguaAnadida.isNotBlank())  aguaAnadida  = datos.aguaAnadida
                        if (datos.puntoCongel.isNotBlank())  puntoCongel  = datos.puntoCongel
                        if (datos.ph.isNotBlank())           ph           = datos.ph
                        imagenUri   = datos.uri
                        textoOCR    = datos.textoOCR
                        ocrListo    = true
                        origenDatos = OrigenDatos.OCR
                        mostrarPicker = false
                    },
                    onCancelar = { mostrarPicker = false }
                )
            }
        }

        // Parámetros
        item {
            HorizontalDivider()
            Text("Parámetros del análisis", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Text(
                if (ocrListo) "✅ Valores cargados desde ticket — verifica" else "Completa los valores del ticket LACTOMAT",
                style = MaterialTheme.typography.bodySmall,
                color = if (ocrListo) AzulCalidad else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        item { Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp)) { CampoNum("Grasa (%)", grasa, { grasa = it }, Modifier.weight(1f)); CampoNum("SNG (%)", sng, { sng = it }, Modifier.weight(1f)) } }
        item { Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp)) { CampoNum("Densidad", densidad, { densidad = it }, Modifier.weight(1f)); CampoNum("Proteína (%)", proteina, { proteina = it }, Modifier.weight(1f)) } }
        item { Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp)) { CampoNum("Lactosa (%)", lactosa, { lactosa = it }, Modifier.weight(1f)); CampoNum("Sales (%)", sales, { sales = it }, Modifier.weight(1f)) } }
        item { Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp)) { CampoNum("Total sólidos (%)", totalSolidos, { totalSolidos = it }, Modifier.weight(1f)); CampoNum("Agua añadida (%)", aguaAnadida, { aguaAnadida = it }, Modifier.weight(1f)) } }
        item { Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp)) { CampoNum("Punto cong. (°C)", puntoCongel, { puntoCongel = it }, Modifier.weight(1f)); CampoNum("pH", ph, { ph = it }, Modifier.weight(1f)) } }
        item { OutlinedTextField(value = observacion, onValueChange = { observacion = it }, label = { Text("Observación") }, minLines = 2, modifier = Modifier.fillMaxWidth()) }

        // Guardar
        item {
            Button(
                onClick = {
                    alGuardar(ControlCalidad(
                        id                = "cal-${Random.nextInt(1000, 9999)}",
                        codigoAnalisis    = "LAC-${Random.nextInt(100, 999)}",
                        idProductor       = productorSeleccionado!!.idProductor,
                        fecha             = FechaData.hoy().formateada(),
                        hora              = HoraData.ahora().formateada(),
                        grasa             = grasa.toDoubleOrNull(),
                        sng               = sng.toDoubleOrNull(),
                        densidad          = densidad.toDoubleOrNull(),
                        proteina          = proteina.toDoubleOrNull(),
                        lactosa           = lactosa.toDoubleOrNull(),
                        sales             = sales.toDoubleOrNull(),
                        totalSolidos      = totalSolidos.toDoubleOrNull(),
                        aguaAnadida       = aguaAnadida.toDoubleOrNull(),
                        puntoCongel       = puntoCongel.toDoubleOrNull(),
                        ph                = ph.toDoubleOrNull(),
                        observacion       = observacion.ifBlank { null },
                        imagenTicket      = imagenUri.ifBlank { null },
                        resultado         = ResultadoCalidad.PENDIENTE,
                        origenDatos       = origenDatos,
                        idUsuarioAnalisis = SesionActual.usuario?.id ?: ""
                    ))
                },
                enabled  = formularioValido,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = AzulCalidad)
            ) { Text("Guardar análisis", fontWeight = FontWeight.Bold) }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// HISTORIAL
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun HistorialCalidad(analisis: List<ControlCalidad>) {
    if (analisis.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🔬", fontSize = 48.sp)
                Spacer(Modifier.height(8.dp))
                Text("No hay análisis registrados", style = MaterialTheme.typography.titleMedium)
                Text("Registra el primer análisis en la pestaña anterior", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        return
    }
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Text("${analisis.size} análisis registrados", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        }
        items(analisis.reversed()) { cal ->
            val productor = DatosMock.productores.find { it.idProductor == cal.idProductor }
            Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(productor?.nombreCompleto ?: "Productor", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text("${cal.fecha}  ${cal.hora}  ·  ${cal.codigoAnalisis}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            val origenLabel = when (cal.origenDatos) {
                                OrigenDatos.OCR           -> "📷 Desde ticket"
                                OrigenDatos.OCR_CORREGIDO -> "📷 Ticket corregido"
                                OrigenDatos.MANUAL        -> "✍️ Manual"
                                else                      -> ""
                            }
                            if (origenLabel.isNotBlank()) Text(origenLabel, style = MaterialTheme.typography.labelSmall, color = AzulCalidad)
                        }
                        Box(
                            modifier = Modifier
                                .background(
                                    when (cal.resultado) {
                                        ResultadoCalidad.APTO      -> VerdeHuata.copy(0.15f)
                                        ResultadoCalidad.NO_APTO   -> Color.Red.copy(0.15f)
                                        ResultadoCalidad.OBSERVADO -> Color(0xFFF9A825).copy(0.3f)
                                        ResultadoCalidad.PENDIENTE -> Color.Gray.copy(0.15f)
                                    },
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) { Text(cal.resultado.etiqueta, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold) }
                    }
                    if (cal.grasa != null || cal.ph != null || cal.densidad != null) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            if (cal.grasa    != null) Param("Grasa",    "${cal.grasa}%")
                            if (cal.ph       != null) Param("pH",       "${cal.ph}")
                            if (cal.densidad != null) Param("Densidad", "${cal.densidad}")
                            if (cal.sng      != null) Param("SNG",      "${cal.sng}%")
                        }
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
private fun CampoNum(label: String, value: String, onChange: (String) -> Unit, modifier: Modifier) {
    OutlinedTextField(
        value = value, onValueChange = onChange,
        label = { Text(label) }, singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = modifier
    )
}

@Composable
private fun Param(label: String, valor: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(valor, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
