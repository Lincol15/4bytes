package com.example.acopiodeleche.ui.components

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

private val AzulOCR  = Color(0xFF0277BD)
private val VerdeOCR = Color(0xFF2E7D32)

@Composable
actual fun ImagenPickerConOCR(
    onDatos: (DatosOCR) -> Unit,
    onCancelar: () -> Unit,
    modifier: Modifier
) {
    val context    = LocalContext.current
    var procesando by remember { mutableStateOf(false) }
    var error      by remember { mutableStateOf("") }

    fun procesarOCR(uri: Uri) {
        procesando = true
        error = ""
        try {
            val stream = context.contentResolver.openInputStream(uri)
            val bmp    = BitmapFactory.decodeStream(stream)
            stream?.close()
            if (bmp == null) {
                procesando = false
                error = "No se pudo leer la imagen"
                return
            }
            val image      = InputImage.fromBitmap(bmp, 0)
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    procesando = false
                    onDatos(parsearTicketLactomat(uri.toString(), visionText.text))
                }
                .addOnFailureListener { e ->
                    procesando = false
                    error = "Error OCR: ${e.message}"
                    onDatos(DatosOCR(uri = uri.toString(), textoOCR = "Error: ${e.message}"))
                }
        } catch (e: Exception) {
            procesando = false
            error = "Error: ${e.message}"
        }
    }

    val galeriaLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) procesarOCR(uri) else onCancelar()
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // ── Procesando ────────────────────────────────────────────────────
        if (procesando) {
            Card(
                modifier  = Modifier.fillMaxWidth(),
                shape     = RoundedCornerShape(16.dp),
                colors    = CardDefaults.cardColors(containerColor = AzulOCR.copy(0.07f)),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = AzulOCR, strokeWidth = 3.dp, modifier = Modifier.size(52.dp))
                    Spacer(Modifier.height(16.dp))
                    Text("Leyendo ticket LACTOMAT...", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = AzulOCR)
                    Spacer(Modifier.height(4.dp))
                    Text("Extrayendo valores automáticamente", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
                }
            }
            return
        }

        // ── Error ─────────────────────────────────────────────────────────
        if (error.isNotBlank()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(10.dp),
                colors   = CardDefaults.cardColors(containerColor = Color.Red.copy(0.08f))
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("⚠️", fontSize = 20.sp)
                    Spacer(Modifier.width(8.dp))
                    Text(error, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                }
            }
        }

        // ── Botón único: Subir ticket ─────────────────────────────────────
        Card(
            modifier  = Modifier.fillMaxWidth().clickable { galeriaLauncher.launch("image/*") },
            shape     = RoundedCornerShape(18.dp),
            colors    = CardDefaults.cardColors(containerColor = AzulOCR.copy(0.07f)),
            elevation = CardDefaults.cardElevation(3.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 28.dp, horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(AzulOCR.copy(0.13f), CircleShape)
                        .border(2.dp, AzulOCR.copy(0.35f), CircleShape),
                    contentAlignment = Alignment.Center
                ) { Text("🖼️", fontSize = 34.sp) }
                Text("Subir ticket", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = AzulOCR)
                Text(
                    "Selecciona la foto del ticket LACTOMAT desde tu galería",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Box(
                    modifier = Modifier
                        .background(AzulOCR, RoundedCornerShape(8.dp))
                        .padding(horizontal = 14.dp, vertical = 5.dp)
                ) {
                    Text("AUTO-RELLENA LOS VALORES", style = MaterialTheme.typography.labelSmall, color = Color.White, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                }
            }
        }

        // ── Tip ───────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(VerdeOCR.copy(0.06f), RoundedCornerShape(10.dp))
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("💡", fontSize = 16.sp)
            Spacer(Modifier.width(8.dp))
            Text(
                "Los valores se llenan automáticamente desde la foto. Verifica antes de guardar.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        HorizontalDivider()
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// PARSER LACTOMAT v7 — regex sobre texto completo + fallback por bloques
//
// Estrategia robusta en 3 capas:
//
//  CAPA 1 — Regex inline: busca "Etiqueta <ruido> VALOR" en una sola línea
//           Ej: "Grasa.........16.3%"  → captura "16.3"
//               "Densidad ,.18.2"      → captura "18.2"
//               "Punto cong.....-0.501 C" → captura "-0.501"
//
//  CAPA 2 — Valor en línea siguiente: si la etiqueta está sola en su línea,
//           el valor puede estar en la próxima línea no vacía.
//           Ej:  "Grasa."   ← etiqueta sola
//                "16.3"     ← valor en siguiente línea
//
//  CAPA 3 — Regex tolerante: busca el número más cercano a la etiqueta
//           en una ventana de ±3 líneas, ignorando fechas, SN, Temp.
//
// Maneja artefactos OCR conocidos:
//   "......" → ignorados (puntos de relleno del ticket)
//   "3:9"   → "3.9"  (OCR confunde punto con dos puntos)
//   ",."    → "0."   (densidad ",.18.2" → "0.18" → se trata con regex especial)
//   ".718X" → "7.18" (OCR pierde primer dígito)
//   "%" "°" "C" → eliminados
// ─────────────────────────────────────────────────────────────────────────────

private fun parsearTicketLactomat(uri: String, texto: String): DatosOCR {

    // ── Preprocesar texto completo ────────────────────────────────────────
    // Normalizar para que regex sea más confiable
    val textoPrep = texto
        .replace(Regex("\\.{2,}"), " ")           // puntos relleno → espacio
        .replace(Regex("[%°]"), " ")               // quitar % y °
        .replace(Regex("\\s*[Cc]\\s*(?=\\s|\$)"), " ") // " C" unidad → espacio
        .replace(Regex(",\\."), "0.")              // ",." → "0."
        .replace(Regex("(\\d+):(?=[0-9])"), "$1.") // "3:9" → "3.9" (solo cuando hay dígito después)

    val lineas = textoPrep.lines().map { it.trim() }.filter { it.isNotBlank() }

    // ── Función para detectar ruido ───────────────────────────────────────
    fun esRuido(s: String): Boolean {
        val l = s.lowercase()
        return l.contains("analizador") || l.contains("lactomat") ||
               l.contains(" mode") || l.contains("mode:") ||
               l.contains("sn:") || l.contains("sn ") ||
               l.contains("temp") ||
               Regex("""\d{2}[./]\d{2}[./]\d{2}""").containsMatchIn(s) || // fecha
               Regex("""^\d{2}[.:]\d{2}\s*$""").containsMatchIn(s)         // hora sola
    }

    // ── Extraer número de texto libre ─────────────────────────────────────
    fun extraerNumero(s: String): String {
        // Artefacto ".NNNx" → "N.NN" (OCR pierde primer dígito del decimal)
        Regex("""^\s*\.(\d{2,3})[Xx]?\s*$""").find(s)?.let { m ->
            val d = m.groupValues[1]
            return "${d[0]}.${d.substring(1)}"
        }
        // Número negativo decimal
        Regex("""-\d+\.\d+""").find(s)?.let { return it.value }
        // Número positivo decimal
        Regex("""\b\d+\.\d+""").find(s)?.let { return it.value }
        // Entero razonable (1-3 dígitos, excluye series largas)
        Regex("""\b(\d{1,3})\b""").findAll(s).toList()
            .filter { m ->
                val v = m.value.toIntOrNull() ?: return@filter false
                v in 0..999
            }
            .maxByOrNull { it.value.length }
            ?.let { return it.value }
        return ""
    }

    // ── CAPA 1: Regex sobre texto preprocesado — busca etiqueta+valor en texto completo ──
    // Pattern: etiqueta (espacios/puntos opcionales) [-]NÚMERO
    fun buscarRegexGlobal(vararg patrones: String): String {
        for (patron in patrones) {
            // Intenta capturar decimal negativo, decimal positivo, entero
            val regex = Regex(
                """(?i)$patron[\s.,:]*(-?\d+\.\d+)""",
                RegexOption.IGNORE_CASE
            )
            regex.find(textoPrep)?.groupValues?.get(1)?.let { return it }
        }
        return ""
    }

    // ── CAPA 2: busca el valor en la línea de la etiqueta o la siguiente ──
    fun buscarEnLineas(vararg claves: String): String {
        for (idx in lineas.indices) {
            val linea = lineas[idx]
            if (claves.none { linea.lowercase().contains(it.lowercase()) }) continue
            if (esRuido(linea)) continue

            // Parte derecha de la misma línea (tras la clave)
            for (clave in claves) {
                val pos = linea.lowercase().indexOf(clave.lowercase())
                if (pos >= 0) {
                    val derecha = linea.substring(pos + clave.length)
                    val n = extraerNumero(derecha)
                    if (n.isNotBlank()) return n
                }
            }

            // Líneas siguientes (hasta 3), parando si es otra etiqueta o ruido
            val etiquetasConocidas = listOf("grasa","sng","densidad","proteina","proteína",
                "lactosa","sales","total","agua","punto","ph")
            for (i in (idx + 1)..minOf(idx + 3, lineas.size - 1)) {
                val sig = lineas[i]
                if (esRuido(sig)) continue
                if (etiquetasConocidas.any { sig.lowercase().contains(it) }) break
                val n = extraerNumero(sig)
                if (n.isNotBlank()) return n
            }
        }
        return ""
    }

    // ── Combinar capas: primero regex global, luego línea a línea ─────────
    fun campo(regexPatrones: Array<out String>, clavesLinea: Array<out String>): String {
        val r1 = buscarRegexGlobal(*regexPatrones)
        if (r1.isNotBlank()) return r1
        return buscarEnLineas(*clavesLinea)
    }

    // ── Caso especial: pH ─────────────────────────────────────────────────
    // Puede aparecer como "pH...11.5" o la línea "pH" sola seguida de
    // "08:40 01/09/26" (hora/fecha) y luego "11.5"
    fun buscarPh(): String {
        // Intentar en texto con regex
        Regex("""(?i)\bph[\s.,:]*(\d+\.\d+)""").find(textoPrep)?.groupValues?.get(1)?.let { return it }
        // Buscar el índice de "ph" en las líneas
        val idx = lineas.indexOfFirst { it.lowercase().contains("ph") && !esRuido(it) }
        if (idx < 0) return ""
        // Buscar el primer número que NO sea una fecha/hora en las líneas siguientes
        for (i in idx..minOf(idx + 5, lineas.size - 1)) {
            val s = lineas[i]
            if (esRuido(s)) continue
            // Saltear si parece hora (08.40) o tiene "/"
            if (s.contains("/")) continue
            if (Regex("""^\d{2}\.\d{2}$""").containsMatchIn(s)) continue
            // Extraer el número de la parte derecha si tiene "ph"
            val pos = s.lowercase().indexOf("ph")
            val buscar = if (pos >= 0) s.substring(pos + 2) else s
            val n = extraerNumero(buscar)
            if (n.isNotBlank()) return n
        }
        return ""
    }

    // ── Caso especial: Grasa ──────────────────────────────────────────────
    // En el ticket real: "Grasa.........16.3%"
    // El OCR a veces lee "Grasa." separado de "16.3" en distintas líneas
    // O puede leer todo junto. Usamos regex más amplio.
    fun buscarGrasa(): String {
        // Regex en texto completo: "Grasa" seguido de cualquier cosa hasta un número
        Regex("""(?i)grasa[\s.,.:*x%\d]*?(\d+\.?\d*)""").find(textoPrep)?.let { m ->
            val v = m.groupValues[1]
            // Validar que sea razonable para grasa (1.0 – 30.0 %)
            val d = v.toDoubleOrNull()
            if (d != null && d >= 1.0 && d <= 30.0) return v
        }
        return buscarEnLineas("grasa")
    }

    // ── Caso especial: Densidad ───────────────────────────────────────────
    // El OCR puede leer "Densidad ,.18.2" → necesitamos "18.2"
    fun buscarDensidad(): String {
        Regex("""(?i)densidad[\s.,: *]*(\d+\.?\d*)""").find(textoPrep)?.let { m ->
            val v = m.groupValues[1]
            val d = v.toDoubleOrNull()
            if (d != null && d > 0) return v
        }
        return buscarEnLineas("densidad")
    }

    // ── Caso especial: SNG ────────────────────────────────────────────────
    // OCR puede leer ".718X" = 7.18
    fun buscarSng(): String {
        val v1 = buscarRegexGlobal("sng")
        if (v1.isNotBlank()) return v1
        // Intentar artefacto ".NNNx"
        val idxSng = lineas.indexOfFirst { it.lowercase().contains("sng") }
        if (idxSng >= 0) {
            for (i in idxSng..(minOf(idxSng + 3, lineas.size - 1))) {
                val s = lineas[i]
                // Buscar artefacto punto-inicial
                Regex("""\.(\d{2,3})[Xx]?""").find(s)?.let { m ->
                    val d = m.groupValues[1]
                    val reconstruido = "${d[0]}.${d.substring(1)}"
                    if (reconstruido.toDoubleOrNull() != null) return reconstruido
                }
                val n = extraerNumero(lineas[i].lowercase().substringAfter("sng"))
                if (n.isNotBlank()) return n
            }
        }
        return ""
    }

    // ── Caso especial: Punto cong — necesita capturar negativo ────────────
    fun buscarPuntoCong(): String {
        // Buscar "-0.5xx" o "-0,5xx" cerca de "punto" o "cong"
        Regex("""(?i)(?:punto|cong)[\s.,: *]*(-\d+\.\d+)""").find(textoPrep)?.groupValues?.get(1)?.let { return it }
        Regex("""(?i)(?:punto|cong)[\s.,: *]*(\d+\.\d+)""").find(textoPrep)?.groupValues?.get(1)?.let { return it }
        return buscarEnLineas("punto cong", "punto")
    }

    // ── Construir resultado ───────────────────────────────────────────────
    return DatosOCR(
        uri          = uri,
        textoOCR     = lineas.joinToString("\n"),
        grasa        = buscarGrasa(),
        sng          = buscarSng(),
        densidad     = buscarDensidad(),
        proteina     = campo(arrayOf("proteina", "proteína"), arrayOf("proteina", "proteína")),
        lactosa      = campo(arrayOf("lactosa"),              arrayOf("lactosa")),
        sales        = campo(arrayOf("sales"),                arrayOf("sales")),
        totalSolidos = campo(arrayOf("total sol", "total s"), arrayOf("total sol", "total")),
        aguaAnadida  = campo(arrayOf("agua"),                 arrayOf("agua")),
        puntoCongel  = buscarPuntoCong(),
        ph           = buscarPh()
    )
}
