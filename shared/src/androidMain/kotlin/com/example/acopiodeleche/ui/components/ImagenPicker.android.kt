package com.example.acopiodeleche.ui.components

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.File

/**
 * Resultado extendido con los valores parseados del ticket LACTOMAT.
 */
data class LactomatResult(
    val uri: String,
    val esEscaneo: Boolean,
    // Valores parseados del OCR
    val grasa: String        = "",
    val sng: String          = "",
    val densidad: String     = "",
    val proteina: String     = "",
    val lactosa: String      = "",
    val sales: String        = "",
    val totalSolidos: String = "",
    val aguaAnadida: String  = "",
    val puntoCongel: String  = "",
    val ph: String           = "",
    val textoCompleto: String = ""
)

@Composable
actual fun ImagenPickerSheet(
    onImagen: (ImagenResult) -> Unit,
    onCancelar: () -> Unit,
    modifier: Modifier
) {
    ImagenPickerSheetAndroid(
        onImagen   = onImagen,
        onCancelar = onCancelar,
        modifier   = modifier
    )
}

/**
 * Versión extendida con OCR — usar en CalidadHomeScreen.
 */
@Composable
fun ImagenPickerOCR(
    onResultado: (LactomatResult) -> Unit,
    onCancelar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var cameraUri by remember { mutableStateOf<Uri?>(null) }
    var procesando by remember { mutableStateOf(false) }
    var errorMensaje by remember { mutableStateOf("") }

    fun procesarOCR(uri: Uri, esEscaneo: Boolean) {
        procesando = true
        errorMensaje = ""
        try {
            val stream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(stream)
            stream?.close()

            if (bitmap == null) {
                procesando = false
                errorMensaje = "No se pudo leer la imagen"
                return
            }

            val image = InputImage.fromBitmap(bitmap, 0)
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    procesando = false
                    val texto = visionText.text
                    val resultado = parsearTicketLactomat(uri.toString(), esEscaneo, texto)
                    onResultado(resultado)
                }
                .addOnFailureListener { e ->
                    procesando = false
                    errorMensaje = "Error OCR: ${e.message}"
                    // Retornar igualmente con URI pero sin parsear
                    onResultado(LactomatResult(uri = uri.toString(), esEscaneo = esEscaneo, textoCompleto = "Error: ${e.message}"))
                }
        } catch (e: Exception) {
            procesando = false
            errorMensaje = "Error: ${e.message}"
        }
    }

    fun crearUriCamara(): Uri {
        val dir = File(context.cacheDir, "calidad_fotos").also { it.mkdirs() }
        val archivo = File(dir, "ticket_${System.currentTimeMillis()}.jpg")
        return FileProvider.getUriForFile(context, "${context.packageName}.provider", archivo)
            .also { cameraUri = it }
    }

    val galeriaLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) procesarOCR(uri, false)
        else onCancelar()
    }

    val camaraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && cameraUri != null) procesarOCR(cameraUri!!, true)
        else onCancelar()
    }

    if (procesando) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = Color(0xFF0277BD))
                Spacer(Modifier.height(10.dp))
                Text(
                    "Leyendo ticket con OCR...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF0277BD)
                )
            }
        }
        return
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (errorMensaje.isNotBlank()) {
            Text(
                "⚠️ $errorMensaje",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Cámara
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        val uri = crearUriCamara()
                        camaraLauncher.launch(uri)
                    },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0277BD).copy(0.1f)),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("📷", fontSize = 32.sp)
                    Spacer(Modifier.height(6.dp))
                    Text("Escanear ticket", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color(0xFF0277BD))
                    Text("Foto con la cámara", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // Galería
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { galeriaLauncher.launch("image/*") },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF6A1B9A).copy(0.1f)),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🖼️", fontSize = 32.sp)
                    Spacer(Modifier.height(6.dp))
                    Text("Subir imagen", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color(0xFF6A1B9A))
                    Text("Desde la galería", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun ImagenPickerSheetAndroid(
    onImagen: (ImagenResult) -> Unit,
    onCancelar: () -> Unit,
    modifier: Modifier
) {
    val context = LocalContext.current
    var cameraUri by remember { mutableStateOf<Uri?>(null) }

    val galeriaLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) onImagen(ImagenResult(uri = uri.toString(), esEscaneo = false))
        else onCancelar()
    }

    val camaraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && cameraUri != null) onImagen(ImagenResult(uri = cameraUri.toString(), esEscaneo = true))
        else onCancelar()
    }

    fun crearUri(): Uri {
        val dir = File(context.cacheDir, "calidad_fotos").also { it.mkdirs() }
        val arch = File(dir, "ticket_${System.currentTimeMillis()}.jpg")
        return FileProvider.getUriForFile(context, "${context.packageName}.provider", arch).also { cameraUri = it }
    }

    Row(modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(modifier = Modifier.weight(1f).clickable { camaraLauncher.launch(crearUri()) },
            shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF0277BD).copy(0.1f))) {
            Column(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("📷", fontSize = 32.sp)
                Spacer(Modifier.height(6.dp))
                Text("Escanear", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color(0xFF0277BD))
            }
        }
        Card(modifier = Modifier.weight(1f).clickable { galeriaLauncher.launch("image/*") },
            shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF6A1B9A).copy(0.1f))) {
            Column(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🖼️", fontSize = 32.sp)
                Spacer(Modifier.height(6.dp))
                Text("Galería", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color(0xFF6A1B9A))
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// PARSER DEL TICKET LACTOMAT
// Busca patrones como "Grasa: 16.3" o "GRASA 16.3%" en el texto OCR
// ─────────────────────────────────────────────────────────────────────────────

fun parsearTicketLactomat(uri: String, esEscaneo: Boolean, texto: String): LactomatResult {
    val lineas = texto.lines()

    fun buscarValor(vararg claves: String): String {
        for (clave in claves) {
            for (linea in lineas) {
                val upper = linea.uppercase()
                if (upper.contains(clave.uppercase())) {
                    // Extraer número de la línea
                    val numero = Regex("""-?\d+[\.,]\d+""").find(linea)?.value?.replace(',', '.')
                    if (numero != null) return numero
                    val entero = Regex("""\d+""").find(linea.substringAfter(clave, linea))?.value
                    if (entero != null) return entero
                }
            }
        }
        return ""
    }

    return LactomatResult(
        uri           = uri,
        esEscaneo     = esEscaneo,
        grasa         = buscarValor("grasa", "fat", "FAT"),
        sng           = buscarValor("sng", "snf", "SNF", "solidos no grasos"),
        densidad      = buscarValor("densidad", "density", "dens"),
        proteina      = buscarValor("proteina", "protein", "prot"),
        lactosa       = buscarValor("lactosa", "lactose", "lact"),
        sales         = buscarValor("sales", "salt", "minerals"),
        totalSolidos  = buscarValor("total solidos", "total solids", "ts", "solidos totales"),
        aguaAnadida   = buscarValor("agua", "water", "added water", "agua añadida"),
        puntoCongel   = buscarValor("punto", "cong", "freezing", "congel"),
        ph            = buscarValor("ph", "pH"),
        textoCompleto = texto
    )
}
