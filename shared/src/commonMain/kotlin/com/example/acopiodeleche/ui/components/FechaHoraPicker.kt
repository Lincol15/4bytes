package com.example.acopiodeleche.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

// ── Utilidades de fecha/hora (sin kotlinx-datetime para mantener compatibilidad) ──

/**
 * Datos de fecha estructurados para el picker.
 */
data class FechaData(val dia: Int, val mes: Int, val anio: Int) {
    fun formateada(): String =
        "${dia.toString().padStart(2, '0')}/${mes.toString().padStart(2, '0')}/$anio"

    companion object {
        /** Fecha hardcodeada como "hoy" en el proyecto (contexto: sep 2026). */
        fun hoy(): FechaData = FechaData(dia = 10, mes = 9, anio = 2026)

        fun desdeTexto(texto: String): FechaData? {
            val partes = texto.split("/")
            if (partes.size != 3) return null
            return try {
                FechaData(
                    dia  = partes[0].trim().toInt(),
                    mes  = partes[1].trim().toInt(),
                    anio = partes[2].trim().toInt()
                )
            } catch (_: NumberFormatException) { null }
        }
    }
}

data class HoraData(val hora: Int, val minuto: Int) {
    fun formateada(): String =
        "${hora.toString().padStart(2, '0')}:${minuto.toString().padStart(2, '0')}"

    companion object {
        /** Hora default al abrir el picker. */
        fun ahora(): HoraData = HoraData(hora = 7, minuto = 0)

        fun desdeTexto(texto: String): HoraData? {
            val partes = texto.split(":")
            if (partes.size != 2) return null
            return try {
                HoraData(hora = partes[0].trim().toInt(), minuto = partes[1].trim().toInt())
            } catch (_: NumberFormatException) { null }
        }
    }
}

private val MESES = listOf(
    "Ene", "Feb", "Mar", "Abr", "May", "Jun",
    "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"
)

private fun diasEnMes(mes: Int, anio: Int): Int = when (mes) {
    2 -> if (anio % 4 == 0 && (anio % 100 != 0 || anio % 400 == 0)) 29 else 28
    4, 6, 9, 11 -> 30
    else -> 31
}

// ─────────────────────────────────────────────────────────────────────────────
// COMPONENTE PRINCIPAL: Campo de fecha con picker en modal
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Campo de fecha con calendario rápido integrado.
 *
 * Muestra un OutlinedTextField con un botón 📅 que abre un mini-calendario.
 * Al abrir, pre-rellena con la fecha actual automáticamente.
 *
 * @param value          Texto actual en formato dd/MM/yyyy
 * @param onValueChange  Callback cuando cambia la fecha
 * @param label          Etiqueta del campo
 * @param obligatorio    Si es true, el label muestra *
 * @param colorPrimario  Color de acento (botón, selección, borde)
 * @param modifier       Modifier del campo
 */
@Composable
fun CampoFecha(
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "Fecha",
    obligatorio: Boolean = true,
    colorPrimario: Color = Color(0xFF2E7D32),
    modifier: Modifier = Modifier
) {
    var mostrarPicker by remember { mutableStateOf(false) }

    // Parsear la fecha actual para el picker (si ya hay texto válido)
    val fechaInicial = remember(value) {
        FechaData.desdeTexto(value) ?: FechaData.hoy()
    }

    Box(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = { nuevo ->
                // Permitir edición manual también
                if (nuevo.length <= 10) onValueChange(nuevo)
            },
            label = { Text(if (obligatorio) "$label *" else label) },
            placeholder = { Text("dd/MM/yyyy") },
            singleLine = true,
            trailingIcon = {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clickable { mostrarPicker = true }
                        .background(colorPrimario.copy(alpha = 0.08f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("📅", fontSize = 20.sp)
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorPrimario,
                focusedLabelColor  = colorPrimario
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }

    if (mostrarPicker) {
        CalendarioDialog(
            fechaInicial = fechaInicial,
            colorPrimario = colorPrimario,
            onConfirmar = { fecha ->
                onValueChange(fecha.formateada())
                mostrarPicker = false
            },
            onDismiss = { mostrarPicker = false }
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// COMPONENTE PRINCIPAL: Campo de hora con picker en modal
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Campo de hora con picker rápido integrado.
 *
 * Muestra un OutlinedTextField con un botón 🕐 que abre un selector de hora/minuto.
 * Al abrir, pre-rellena con la hora actual automáticamente.
 */
@Composable
fun CampoHora(
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "Hora",
    obligatorio: Boolean = true,
    colorPrimario: Color = Color(0xFF2E7D32),
    modifier: Modifier = Modifier
) {
    var mostrarPicker by remember { mutableStateOf(false) }

    val horaInicial = remember(value) {
        HoraData.desdeTexto(value) ?: HoraData.ahora()
    }

    Box(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = { nuevo ->
                if (nuevo.length <= 5) onValueChange(nuevo)
            },
            label = { Text(if (obligatorio) "$label *" else label) },
            placeholder = { Text("HH:mm") },
            singleLine = true,
            trailingIcon = {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clickable { mostrarPicker = true }
                        .background(colorPrimario.copy(alpha = 0.08f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🕐", fontSize = 20.sp)
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorPrimario,
                focusedLabelColor  = colorPrimario
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }

    if (mostrarPicker) {
        HoraDialog(
            horaInicial = horaInicial,
            colorPrimario = colorPrimario,
            onConfirmar = { hora ->
                onValueChange(hora.formateada())
                mostrarPicker = false
            },
            onDismiss = { mostrarPicker = false }
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// COMPOSABLE: Fila combinada Fecha + Hora (uso frecuente en formularios)
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Fila con campo de fecha y campo de hora lado a lado.
 * Ambos con auto-relleno al abrirse.
 *
 * Al crear el composable, si fecha u hora están vacíos se inicializan
 * automáticamente con valores por defecto.
 */
@Composable
fun FilaFechaHora(
    fecha: String,
    hora: String,
    onFechaChange: (String) -> Unit,
    onHoraChange: (String) -> Unit,
    colorPrimario: Color = Color(0xFF2E7D32),
    labelFecha: String = "Fecha",
    labelHora: String = "Hora"
) {
    // Auto-rellenar si están vacíos
    val fechaEfectiva = if (fecha.isBlank()) FechaData.hoy().formateada() else fecha
    val horaEfectiva  = if (hora.isBlank()) HoraData.ahora().formateada() else hora

    // Notificar al padre si se auto-rellenó
    if (fecha.isBlank()) onFechaChange(fechaEfectiva)
    if (hora.isBlank()) onHoraChange(horaEfectiva)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CampoFecha(
            value = fechaEfectiva,
            onValueChange = onFechaChange,
            label = labelFecha,
            colorPrimario = colorPrimario,
            modifier = Modifier.weight(1f)
        )
        CampoHora(
            value = horaEfectiva,
            onValueChange = onHoraChange,
            label = labelHora,
            colorPrimario = colorPrimario,
            modifier = Modifier.weight(1f)
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// DIÁLOGO: Calendario mensual
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun CalendarioDialog(
    fechaInicial: FechaData,
    colorPrimario: Color,
    onConfirmar: (FechaData) -> Unit,
    onDismiss: () -> Unit
) {
    var anio  by remember { mutableStateOf(fechaInicial.anio) }
    var mes   by remember { mutableStateOf(fechaInicial.mes) }
    var dia   by remember { mutableStateOf(fechaInicial.dia) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {

                // Título
                Text(
                    "📅 Seleccionar fecha",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Navegación mes/año
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Año
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TextButton(onClick = { anio-- }) { Text("◀", fontSize = 14.sp) }
                        Text(
                            "$anio",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(48.dp),
                            textAlign = TextAlign.Center
                        )
                        TextButton(onClick = { anio++ }) { Text("▶", fontSize = 14.sp) }
                    }
                    // Mes
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TextButton(onClick = {
                            if (mes == 1) { mes = 12; anio-- } else mes--
                        }) { Text("◀", fontSize = 14.sp) }
                        Text(
                            MESES[mes - 1],
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(36.dp),
                            textAlign = TextAlign.Center
                        )
                        TextButton(onClick = {
                            if (mes == 12) { mes = 1; anio++ } else mes++
                        }) { Text("▶", fontSize = 14.sp) }
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Cabecera días de la semana
                Row(modifier = Modifier.fillMaxWidth()) {
                    listOf("L", "M", "X", "J", "V", "S", "D").forEach { d ->
                        Text(
                            d,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(Modifier.height(4.dp))

                // Celdas del calendario
                val diasMes    = diasEnMes(mes, anio)
                // 1=Lun, 7=Dom → calcular el offset del primer día
                val primerDia  = calcularDiaSemana(1, mes, anio) // 0=Lun…6=Dom

                val celdas = primerDia + diasMes
                val semanas = (celdas + 6) / 7

                for (semana in 0 until semanas) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        for (col in 0 until 7) {
                            val celda = semana * 7 + col
                            val numDia = celda - primerDia + 1
                            val valido = numDia in 1..diasMes

                            if (valido) {
                                val seleccionado = numDia == dia
                                val esHoy = numDia == FechaData.hoy().dia
                                    && mes == FechaData.hoy().mes
                                    && anio == FechaData.hoy().anio

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(2.dp)
                                        .background(
                                            when {
                                                seleccionado -> colorPrimario
                                                esHoy        -> colorPrimario.copy(alpha = 0.15f)
                                                else         -> Color.Transparent
                                            },
                                            RoundedCornerShape(50)
                                        )
                                        .then(
                                            if (esHoy && !seleccionado)
                                                Modifier.border(1.dp, colorPrimario, RoundedCornerShape(50))
                                            else Modifier
                                        )
                                        .clickable { dia = numDia },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "$numDia",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal,
                                        color = if (seleccionado) Color.White else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(vertical = 6.dp)
                                    )
                                }
                            } else {
                                Box(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }

                // Corrección de dia si el mes cambió y el día ya no existe
                val diasActuales = diasEnMes(mes, anio)
                if (dia > diasActuales) dia = diasActuales

                Spacer(Modifier.height(12.dp))

                // Botones
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancelar")
                    }
                    Button(
                        onClick = { onConfirmar(FechaData(dia, mes, anio)) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = colorPrimario)
                    ) {
                        Text("Confirmar", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// DIÁLOGO: Selector de hora
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun HoraDialog(
    horaInicial: HoraData,
    colorPrimario: Color,
    onConfirmar: (HoraData) -> Unit,
    onDismiss: () -> Unit
) {
    var hora    by remember { mutableStateOf(horaInicial.hora) }
    var minuto  by remember { mutableStateOf(horaInicial.minuto) }
    var editHora   by remember { mutableStateOf(horaInicial.hora.toString().padStart(2, '0')) }
    var editMinuto by remember { mutableStateOf(horaInicial.minuto.toString().padStart(2, '0')) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "🕐 Seleccionar hora",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(20.dp))

                // Rueda de horas y minutos
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Columna HORA
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Hora", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(4.dp))
                        TextButton(onClick = { hora = (hora + 1) % 24; editHora = hora.toString().padStart(2,'0') }) {
                            Text("▲", fontSize = 20.sp, color = colorPrimario)
                        }
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(colorPrimario, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                hora.toString().padStart(2, '0'),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        TextButton(onClick = { hora = if (hora == 0) 23 else hora - 1; editHora = hora.toString().padStart(2,'0') }) {
                            Text("▼", fontSize = 20.sp, color = colorPrimario)
                        }
                    }

                    Text(
                        ":",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    // Columna MINUTO
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Min", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(4.dp))
                        TextButton(onClick = { minuto = (minuto + 5) % 60; editMinuto = minuto.toString().padStart(2,'0') }) {
                            Text("▲", fontSize = 20.sp, color = colorPrimario)
                        }
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(colorPrimario, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                minuto.toString().padStart(2, '0'),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        TextButton(onClick = { minuto = if (minuto < 5) 55 else minuto - 5; editMinuto = minuto.toString().padStart(2,'0') }) {
                            Text("▼", fontSize = 20.sp, color = colorPrimario)
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Accesos rápidos
                Text("Accesos rápidos", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(6 to 0, 7 to 0, 8 to 0, 12 to 0, 14 to 0, 17 to 0).forEach { (h, m) ->
                        val seleccionado = hora == h && minuto == m
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    if (seleccionado) colorPrimario else colorPrimario.copy(0.1f),
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { hora = h; minuto = m }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "${h.toString().padStart(2,'0')}:00",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (seleccionado) Color.White else colorPrimario
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Botones
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancelar")
                    }
                    Button(
                        onClick = { onConfirmar(HoraData(hora, minuto)) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = colorPrimario)
                    ) {
                        Text("Confirmar", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// UTILIDAD: Calcular día de la semana (Tomohiko Sakamoto simplificado)
// Retorna 0=Lunes, 1=Martes, …, 6=Domingo
// ─────────────────────────────────────────────────────────────────────────────

private fun calcularDiaSemana(dia: Int, mes: Int, anio: Int): Int {
    val t = intArrayOf(0, 3, 2, 5, 0, 3, 5, 1, 4, 6, 2, 4)
    val a = if (mes < 3) anio - 1 else anio
    val dow = (a + a / 4 - a / 100 + a / 400 + t[mes - 1] + dia) % 7
    // dow: 0=Dom, 1=Lun, …, 6=Sab  →  convertir a 0=Lun…6=Dom
    return (dow + 6) % 7
}
