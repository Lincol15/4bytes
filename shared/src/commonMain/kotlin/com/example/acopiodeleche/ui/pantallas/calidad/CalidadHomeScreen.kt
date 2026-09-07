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
import kotlin.random.Random

private val AzulCalidad = Color(0xFF0277BD)
private val VerdeHuata = Color(0xFF2E7D32)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalidadHomeScreen(
    onCerrarSesion: () -> Unit,
    modifier: Modifier = Modifier
) {
    val usuario = SesionActual.usuario
    var tabActual by remember { mutableStateOf(0) }
    val tabs = listOf("Nuevo análisis", "Historial")
    val analisis = remember { mutableStateOf(DatosMock.analisisCalidad) }

    Column(modifier = modifier.fillMaxSize()) {

        // Encabezado
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(AzulCalidad)
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = "Control de Calidad",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Ecolácteos Huata · ${usuario?.nombreCompleto ?: ""}",
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
            0 -> FormularioAnalisis(
                alGuardar = { nuevo ->
                    DatosMock.analisisCalidad.add(nuevo)
                    analisis.value = DatosMock.analisisCalidad
                    tabActual = 1
                }
            )
            1 -> HistorialCalidad(analisis = analisis.value)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormularioAnalisis(alGuardar: (ControlCalidad) -> Unit) {
    val productores = DatosMock.productores.filter { it.estado }

    var productorSeleccionado by remember { mutableStateOf<Productor?>(null) }
    var dropProductorExpanded by remember { mutableStateOf(false) }

    var fecha by remember { mutableStateOf("") }
    var hora by remember { mutableStateOf("") }
    var grasa by remember { mutableStateOf("") }
    var sng by remember { mutableStateOf("") }
    var densidad by remember { mutableStateOf("") }
    var proteina by remember { mutableStateOf("") }
    var lactosa by remember { mutableStateOf("") }
    var sales by remember { mutableStateOf("") }
    var totalSolidos by remember { mutableStateOf("") }
    var aguaAnadida by remember { mutableStateOf("") }
    var puntoCongel by remember { mutableStateOf("") }
    var ph by remember { mutableStateOf("") }
    var observacion by remember { mutableStateOf("") }

    val formularioValido = productorSeleccionado != null && fecha.isNotBlank() && hora.isNotBlank()

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("🔬 Nuevo análisis", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(
                text = "Basado en Analizador LACTOMAT — Ecolácteos Huata",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Productor
        item {
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
                            onClick = { productorSeleccionado = p; dropProductorExpanded = false }
                        )
                    }
                }
            }
        }

        // Fecha y hora
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = fecha, onValueChange = { fecha = it },
                    label = { Text("Fecha *") }, placeholder = { Text("dd/MM/yyyy") },
                    singleLine = true, modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = hora, onValueChange = { hora = it },
                    label = { Text("Hora *") }, placeholder = { Text("HH:mm") },
                    singleLine = true, modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            HorizontalDivider()
            Text("Parámetros del análisis", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Text("(Según ticket LACTOMAT)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        // Parámetros — dos columnas
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CampoNumerico("Grasa (%)", grasa, { grasa = it }, Modifier.weight(1f))
                CampoNumerico("SNG (%)", sng, { sng = it }, Modifier.weight(1f))
            }
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CampoNumerico("Densidad", densidad, { densidad = it }, Modifier.weight(1f))
                CampoNumerico("Proteína (%)", proteina, { proteina = it }, Modifier.weight(1f))
            }
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CampoNumerico("Lactosa (%)", lactosa, { lactosa = it }, Modifier.weight(1f))
                CampoNumerico("Sales (%)", sales, { sales = it }, Modifier.weight(1f))
            }
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CampoNumerico("Total sólidos (%)", totalSolidos, { totalSolidos = it }, Modifier.weight(1f))
                CampoNumerico("Agua añadida (%)", aguaAnadida, { aguaAnadida = it }, Modifier.weight(1f))
            }
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CampoNumerico("Punto cong. (°C)", puntoCongel, { puntoCongel = it }, Modifier.weight(1f))
                CampoNumerico("pH", ph, { ph = it }, Modifier.weight(1f))
            }
        }
        item {
            OutlinedTextField(
                value = observacion, onValueChange = { observacion = it },
                label = { Text("Observación") }, minLines = 2, modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            Button(
                onClick = {
                    alGuardar(
                        ControlCalidad(
                            id = "cal-${Random.nextInt(1000, 9999)}",
                            codigoAnalisis = "LAC-${Random.nextInt(100, 999)}",
                            idProductor = productorSeleccionado!!.idProductor,
                            fecha = fecha.trim(),
                            hora = hora.trim(),
                            grasa = grasa.toDoubleOrNull(),
                            sng = sng.toDoubleOrNull(),
                            densidad = densidad.toDoubleOrNull(),
                            proteina = proteina.toDoubleOrNull(),
                            lactosa = lactosa.toDoubleOrNull(),
                            sales = sales.toDoubleOrNull(),
                            totalSolidos = totalSolidos.toDoubleOrNull(),
                            aguaAnadida = aguaAnadida.toDoubleOrNull(),
                            puntoCongel = puntoCongel.toDoubleOrNull(),
                            ph = ph.toDoubleOrNull(),
                            observacion = observacion.ifBlank { null },
                            resultado = ResultadoCalidad.PENDIENTE,
                            origenDatos = OrigenDatos.MANUAL,
                            idUsuarioAnalisis = SesionActual.usuario?.id ?: ""
                        )
                    )
                },
                enabled = formularioValido,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AzulCalidad)
            ) { Text("Guardar análisis", fontWeight = FontWeight.Bold) }
        }
    }
}

@Composable
private fun CampoNumerico(label: String, value: String, onValueChange: (String) -> Unit, modifier: Modifier) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = modifier
    )
}

@Composable
private fun HistorialCalidad(analisis: List<ControlCalidad>) {
    if (analisis.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🔬", fontSize = 48.sp)
                Spacer(Modifier.height(8.dp))
                Text("No hay análisis registrados", style = MaterialTheme.typography.titleMedium)
                Text("Registra el primer análisis en la pestaña anterior",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        return
    }
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(analisis) { cal ->
            val productor = DatosMock.productores.find { it.idProductor == cal.idProductor }
            Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(productor?.nombreCompleto ?: "Productor", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text("${cal.fecha} ${cal.hora} · ${cal.codigoAnalisis}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Box(
                            modifier = Modifier
                                .background(
                                    when (cal.resultado) {
                                        ResultadoCalidad.APTO -> VerdeHuata.copy(alpha = 0.15f)
                                        ResultadoCalidad.NO_APTO -> Color.Red.copy(alpha = 0.15f)
                                        ResultadoCalidad.OBSERVADO -> Color(0xFFF9A825).copy(alpha = 0.3f)
                                        ResultadoCalidad.PENDIENTE -> Color.Gray.copy(alpha = 0.15f)
                                    },
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = cal.resultado.etiqueta,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    if (cal.grasa != null || cal.ph != null) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            if (cal.grasa != null) FilaParametro("Grasa", "${cal.grasa}%")
                            if (cal.ph != null) FilaParametro("pH", "${cal.ph}")
                            if (cal.densidad != null) FilaParametro("Densidad", "${cal.densidad}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FilaParametro(label: String, valor: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(valor, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
