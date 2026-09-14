package com.example.acopiodeleche.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import java.io.File

/**
 * Resultado extendido con los valores parseados del ticket LACTOMAT.
 */
data class LactomatResult(
    val uri: String,
    val esEscaneo: Boolean,
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
        val dir  = File(context.cacheDir, "calidad_fotos").also { it.mkdirs() }
        val arch = File(dir, "ticket_${System.currentTimeMillis()}.jpg")
        return FileProvider.getUriForFile(context, "${context.packageName}.provider", arch).also { cameraUri = it }
    }

    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier.weight(1f).clickable { camaraLauncher.launch(crearUri()) },
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0277BD).copy(0.1f))
        ) {
            Column(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("📷", fontSize = 32.sp)
                Spacer(Modifier.height(6.dp))
                Text("Escanear", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color(0xFF0277BD))
            }
        }
        Card(
            modifier = Modifier.weight(1f).clickable { galeriaLauncher.launch("image/*") },
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF6A1B9A).copy(0.1f))
        ) {
            Column(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🖼️", fontSize = 32.sp)
                Spacer(Modifier.height(6.dp))
                Text("Galería", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color(0xFF6A1B9A))
            }
        }
    }
}
