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
// PARSER LACTOMAT v5 — asignación por ventana exclusiva
//
// OCR real observado (línea por línea):
//   0: Analizador de LACTOMAT
//   1: SN 49731 Mode 1
//   2: Temp. 14.8
//   3: Grasa.
//   4: SNG
//   5: Proteína.
//   6: 16.3          ← Grasa
//   7: Densidad ,.18.2
//   8: Lactosa.
//   9: .718X         ← SNG: 7.18 (OCR lo leyó con punto inicial y X)
//  10: 2.7           ← Proteína
//  11: 3:9           ← Lactosa: 3.9 (OCR usó ":" en vez de ".")
//  12: Sales
//  13: Total sol idos 23.4
//  14: Agua anadida 3.5
//  15: 0.4           ← Sales
//  16: Punto cong -0.501
//  17: ph
//  18: 08:40 01/09/26
//  19: 11.5          ← pH
//
// Regla: cada campo toma el PRIMER número válido entre su línea (inclusive)
// y la línea donde empieza el SIGUIENTE campo (exclusive).
// Así "Grasa" solo mira líneas 3-6, no puede contaminar a Densidad.
// ─────────────────────────────────────────────────────────────────────────────

private fun parsearTicketLactomat(uri: String, texto: String): DatosOCR {

    val lineasRaw = texto.lines().map { it.trim() }.filter { it.isNotBlank() }

    // ── Normalizar artefactos OCR conocidos ───────────────────────────────
    fun norm(s: String): String = s
        .replace(Regex("\\.{2,}"), " ")          // "......" → espacio
        .replace(Regex("(\\d+):(\\d+)"), "$1.$2") // "3:9" → "3.9"
        .replace(Regex(",\\."), "0.")             // ",." → "0."
        .replace(Regex("[%°]"), "")
        .replace(Regex("\\s*[Cc]\\s*$"), "")     // " C" al final
        .trim()

    val lineas = lineasRaw.map { norm(it) }.filter { it.isNotBlank() }

    // ── Extraer número de una cadena (decimal preferido, entero como fallback) ──
    // ".718X" → strip non-numeric prefix/suffix → "7.18" con heurística de punto inicial
    fun extraerNum(s: String): String {
        // Caso especial: ".NNNx" → el OCR leyó un decimal sin el dígito inicial
        // Ejemplo: ".718X" corresponde a "7.18" — recomponemos como "N.NN" si hay 3 decimales
        val puntoInicial = Regex("""^\s*\.(\d{2,3})[Xx]?\s*$""").find(s)
        if (puntoInicial != null) {
            val decimales = puntoInicial.groupValues[1]
            // "718" → primer dígito es parte entera: "7.18"
            return "${decimales[0]}.${decimales.substring(1)}"
        }
        // Decimal normal (incluyendo negativos)
        Regex("""-?\d+\.\d+""").find(s)?.let { return it.value }
        // Entero corto (evitar serie SN:49731 y año 26 de fecha)
        Regex("""-?\d+""").findAll(s).toList()
            .filter { m -> m.value.length <= 4 && (m.value.startsWith("-") || m.value.toIntOrNull()?.let { it < 1000 } == true) }
            .lastOrNull()?.let { return it.value }
        return ""
    }

    // ── Detectar si una línea es una "clave" del ticket ───────────────────
    val CLAVES = listOf("grasa","sng","densidad","proteina","proteína",
                        "lactosa","sales","total","agua","punto","ph")
    fun esClave(s: String) = CLAVES.any { s.lowercase().contains(it) }

    // ── Detectar ruido a ignorar ──────────────────────────────────────────
    fun esRuido(s: String): Boolean {
        val l = s.lowercase()
        return l.contains("/") || l.contains("analizador") || l.contains("lactomat") ||
               l.contains("mode") || l.contains("sn") || l.contains("temp") ||
               Regex("""^\d{2}\.\d{2}$""").containsMatchIn(s)  // hora "08.40"
    }

    // ── Localizar el índice de una clave ──────────────────────────────────
    fun idxDe(vararg claves: String): Int {
        for (clave in claves)
            lineas.indexOfFirst { it.lowercase().contains(clave.lowercase()) }
                .takeIf { it >= 0 }?.let { return it }
        return -1
    }

    // ── Extraer valor dentro de la ventana [desde, hasta) ─────────────────
    // Recorre líneas desde `desde` hasta `hasta` (exclusive) buscando el primer número.
    fun valorEnVentana(desde: Int, hasta: Int): String {
        if (desde < 0) return ""
        val fin = minOf(hasta, lineas.size)
        for (i in desde until fin) {
            val linea = lineas[i]
            if (esRuido(linea)) continue
            val n = extraerNum(linea)
            if (n.isNotBlank()) return n
        }
        return ""
    }

    // ── Localizar todos los campos ────────────────────────────────────────
    val idxGrasa  = idxDe("Grasa")
    val idxSng    = idxDe("SNG", "Sng")
    val idxDens   = idxDe("Densidad")
    val idxProt   = idxDe("Proteina", "Proteína")
    val idxLact   = idxDe("Lactosa")
    val idxSales  = idxDe("Sales")
    val idxTotal  = idxDe("Total sol", "Total")
    val idxAgua   = idxDe("Agua")
    val idxPunto  = idxDe("Punto")
    val idxPh     = idxDe("ph")

    // Orden de aparición en el ticket → define las ventanas
    // ventana(A) = [idxA .. idxSiguiente)
    // Para el último campo (pH) la ventana es [idxPh .. fin]
    // pero saltando líneas con "/" (fecha)
    fun ventanaHasta(desde: Int, vararg siguientes: Int): Int {
        val proxima = siguientes.filter { it > desde }.minOrNull() ?: lineas.size
        return proxima
    }

    // Punto cong: asegurar captura del signo negativo
    fun valorPuntoCong(): String {
        if (idxPunto < 0) return ""
        val hasta = ventanaHasta(idxPunto, idxPh)
        for (i in idxPunto until minOf(hasta + 2, lineas.size)) {
            val linea = lineas[i]
            if (esRuido(linea)) continue
            Regex("""-\d+\.\d+""").find(linea)?.let { return it.value }
            Regex("""\d+\.\d+""").find(linea)?.let { return it.value }
        }
        return ""
    }

    // pH: saltar líneas con "/" o ":" que sean hora/fecha
    fun valorPh(): String {
        if (idxPh < 0) return ""
        for (i in idxPh until lineas.size) {
            val linea = lineas[i]
            if (linea.contains("/")) continue
            if (Regex("""^\d{2}\.\d{2}$""").containsMatchIn(linea)) continue // hora
            val n = extraerNum(linea)
            if (n.isNotBlank()) return n
        }
        return ""
    }

    val textoNorm = lineas.joinToString("\n")

    return DatosOCR(
        uri          = uri,
        textoOCR     = textoNorm,
        grasa        = valorEnVentana(idxGrasa,  ventanaHasta(idxGrasa,  idxSng, idxDens, idxProt, idxLact)),
        sng          = valorEnVentana(idxSng,    ventanaHasta(idxSng,    idxDens, idxProt, idxLact)),
        densidad     = valorEnVentana(idxDens,   ventanaHasta(idxDens,   idxProt, idxLact, idxSales)),
        proteina     = valorEnVentana(idxProt,   ventanaHasta(idxProt,   idxLact, idxSales, idxTotal)),
        lactosa      = valorEnVentana(idxLact,   ventanaHasta(idxLact,   idxSales, idxTotal, idxAgua)),
        sales        = valorEnVentana(idxSales,  ventanaHasta(idxSales,  idxTotal, idxAgua, idxPunto)),
        totalSolidos = valorEnVentana(idxTotal,  ventanaHasta(idxTotal,  idxAgua, idxPunto, idxPh)),
        aguaAnadida  = valorEnVentana(idxAgua,   ventanaHasta(idxAgua,   idxPunto, idxPh)),
        puntoCongel  = valorPuntoCong(),
        ph           = valorPh()
    )
}
