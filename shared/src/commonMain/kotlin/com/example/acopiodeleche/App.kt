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
import com.example.acopiodeleche.ui.pantallas.DemoEstado
import com.example.acopiodeleche.ui.pantallas.acopio.AcopioScreen
import com.example.acopiodeleche.ui.pantallas.productores.ProductoresScreen

@Composable
@Preview
fun App() {
    MaterialTheme {
        // Estado de la tab activa — elevado aquí (Actividad 8: elevación del estado)
        var tabSeleccionado by remember { mutableStateOf(0) }

        // Tab 0: Productores  |  Tab 1: Acopio  |  Tab 2: Demo Compose (Actividades 2-4)
        val tabs = listOf("Productores", "Acopio", "Demo")

        Column(modifier = Modifier.fillMaxSize()) {

            TabRow(selectedTabIndex = tabSeleccionado) {
                tabs.forEachIndexed { indice, titulo ->
                    Tab(
                        selected = tabSeleccionado == indice,
                        onClick  = { tabSeleccionado = indice },
                        text     = { Text(titulo) }
                    )
                }
            }

            when (tabSeleccionado) {
                0 -> ProductoresScreen()
                1 -> AcopioScreen()
                2 -> DemoEstado()   // Actividades 2, 3 y 4 de la guía
            }
        }
    }
}
