package com.example.acopiodeleche

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.acopiodeleche.ui.pantallas.acopio.AcopioScreen
import com.example.acopiodeleche.ui.pantallas.productores.ProductoresScreen

@Composable
@Preview
fun App() {
    MaterialTheme {
        var tabSeleccionado by remember { mutableStateOf(0) }
        val tabs = listOf("Productores", "Acopio")

        Column(modifier = Modifier.fillMaxSize()) {

            // Navegación por tabs
            TabRow(selectedTabIndex = tabSeleccionado) {
                tabs.forEachIndexed { indice, titulo ->
                    Tab(
                        selected = tabSeleccionado == indice,
                        onClick = { tabSeleccionado = indice },
                        text = { Text(titulo) }
                    )
                }
            }

            // Contenido según tab seleccionado
            when (tabSeleccionado) {
                0 -> ProductoresScreen()
                1 -> AcopioScreen()
            }
        }
    }
}
