package com.example.acopiodeleche.ui.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun Saludo(nombre: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Hola, $nombre",
            style = MaterialTheme.typography.headlineSmall
        )
        Text("Bienvenido al sistema de acopio de leche")
    }
}

// ---------------------------------------------------------------
// Actividad 3: Modifiers y el orden de las capas
// ---------------------------------------------------------------

@Composable
fun PruebaModifiers() {
    Column(modifier = Modifier.padding(24.dp)) {

        Text(text = "Diferencia entre el orden de los Modifiers",
            style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(16.dp))

        // Versión A: primero padding, después background
        // El color NO cubre el margen exterior — el padding "sale" del color
        Text(
            text = "A: padding primero, luego background",
            modifier = Modifier
                .padding(16.dp)
                .background(Color(0xFFB3E5FC))  // azul claro
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Versión B: primero background, después padding
        // El color SÍ cubre el margen interior — el padding queda "dentro" del color
        Text(
            text = "B: background primero, luego padding",
            modifier = Modifier
                .background(Color(0xFFB3E5FC))  // azul claro
                .padding(16.dp)
        )
    }
}
