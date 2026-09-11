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

@Composable
actual fun ImagenPickerConOCR(
    onDatos: (DatosOCR) -> Unit,
    onCancelar: () -> Unit,
    modifier: Modifier
) {
    val context = LocalContext.current
    var cameraUri  by remember { mutableStateOf<Uri?>(null) }
    var procesando by remember { mutableStateOf(false) }
    var error      by remember { mutableStateOf("") }

    fun procesarOCR(uri: Uri) {
        procesando = true
        error = ""
        try {
            val stream = context.contentResolver.openInputStream(uri)
            val bmp    = BitmapFactory.decodeStream(stream)
            stream?.close()
            if (bmp == null) { procesando = false; error = "No se pudo leer la imagen"; return }

            val image      = InputImage.fromBitmap(bmp, 0)
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    procesando = false
                    onDatos(parsearLactomat(uri.toString(), visionText.text))
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

    fun crearUriCamara(): Uri {
        val dir  = File(context.cacheDir, "calidad_fotos").also { it.mkdirs() }
        val arch = File(dir, "ticket_${System.currentTimeMillis()}.jpg")
        return FileProvider.getUriForFile(context, "${context.packageName}.provider", arch)
            .also { cameraUri = it }
    }

    val galeriaLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) procesarOCR(uri) else onCancelar()
    }
    val camaraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { ok ->
        if (ok && cameraUri != null) procesarOCR(cameraUri!!) else onCancelar()
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (procesando) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0277BD).copy(0.06f), RoundedCornerShape(12.dp))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = Color(0xFF0277BD), strokeWidth = 3.dp)
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "Leyendo ticket LACTOMAT...",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0277BD)
                    )
                    Text(
                        "Extrayendo valores automáticamente",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            return
        }

        if (error.isNotBlank()) {
            Text(
                "⚠️ $error",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            // Botón cámara
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { camaraLauncher.launch(crearUriCamara()) },
                shape  = RoundedCornerShape(14.dp),
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
                    Text("Auto-rellena los valores", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // Botón galería
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { galeriaLauncher.launch("image/*") },
                shape  = RoundedCornerShape(14.dp),
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
                    Text("Desde galería", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

// ── Parser LACTOMAT ───────────────────────────────────────────────────────────

private fun parsearLactomat(uri: String, texto: String): DatosOCR {
    val lineas = texto.lines()

    fun extraer(vararg claves: String): String {
        for (clave in claves) {
            for (linea in lineas) {
                if (linea.uppercase().contains(clave.uppercase())) {
                    val num = Regex("""-?\d+[.,]\d+""").find(linea)?.value?.replace(',', '.')
                        ?: Regex("""\d+""").find(linea)?.value
                    if (num != null) return num
                }
            }
        }
        return ""
    }

    return DatosOCR(
        uri          = uri,
        textoOCR     = texto,
        grasa        = extraer("grasa", "fat", "FAT"),
        sng          = extraer("sng", "snf", "SNF"),
        densidad     = extraer("densidad", "density", "dens"),
        proteina     = extraer("proteina", "protein", "prot"),
        lactosa      = extraer("lactosa", "lactose"),
        sales        = extraer("sales", "salt", "mineral"),
        totalSolidos = extraer("total solid", "ts ", "solidos totales", "t.solid"),
        aguaAnadida  = extraer("agua", "water added", "added water"),
        puntoCongel  = extraer("congel", "freezing", "punto de", "cong."),
        ph           = extraer("ph", "pH")
    )
}
