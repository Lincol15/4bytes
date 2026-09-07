package com.example.acopiodeleche.ui.pantallas.acopiador

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
actual fun MapaWebView(
    puntos: List<PuntoRuta>,
    modifier: Modifier
) {
    Box(
        modifier = modifier.background(Color(0xFFE8F5E9)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🗺", fontSize = 48.sp)
            Spacer(Modifier.height(8.dp))
            Text(
                "Mapa disponible en Android",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                "${puntos.size} puntos en la ruta",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}
