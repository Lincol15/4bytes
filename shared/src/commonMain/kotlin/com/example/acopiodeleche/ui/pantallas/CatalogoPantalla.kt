package com.example.acopiodeleche.ui.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// ---------------------------------------------------------------
// Actividad 2: Primer composable
// ---------------------------------------------------------------

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

        Text(
            text = "Diferencia entre el orden de los Modifiers",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Versión A: primero padding, después background
        // El color NO cubre el margen exterior
        Text(
            text = "A: padding primero, luego background",
            modifier = Modifier
                .padding(16.dp)
                .background(Color(0xFFB3E5FC))
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Versión B: primero background, después padding
        // El color SÍ cubre el margen interior
        Text(
            text = "B: background primero, luego padding",
            modifier = Modifier
                .background(Color(0xFFB3E5FC))
                .padding(16.dp)
        )
    }
}

// ---------------------------------------------------------------
// Actividad 4: Estado con remember y mutableStateOf
// ---------------------------------------------------------------

/**
 * Versión ROTA: clicks se reinicia en cada recomposición.
 * Demuestra por qué remember es necesario.
 */
@Composable
fun ContadorRoto() {
    var clicks = 0  // ← se reinicia en cada recomposición
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Contador ROTO: $clicks",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { clicks++ }) {
            Text("Pulsar (no funciona)")
        }
        Text(
            text = "Sin remember, clicks vuelve a 0 en cada recomposición.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

/**
 * Versión CORRECTA: remember conserva el valor entre recomposiciones.
 */
@Composable
fun Contador() {
    var clicks by remember { mutableStateOf(0) }
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Contador: $clicks veces",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { clicks++ }) {
            Text("Sumar uno")
        }
        Text(
            text = "Con remember, el valor se conserva entre recomposiciones.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

/**
 * Pantalla de demostración completa de la Actividad 4.
 * Muestra ContadorRoto y Contador juntos para comparar.
 */
@Composable
fun DemoEstado(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Actividad 4: Estado con remember",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Pulsa los botones y observa la diferencia:",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        ContadorRoto()
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        Contador()
    }
}
