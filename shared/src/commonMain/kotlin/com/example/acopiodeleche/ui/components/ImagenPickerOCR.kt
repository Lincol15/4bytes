package com.example.acopiodeleche.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Datos parseados del ticket LACTOMAT por OCR.
 * En commonMain todos los campos son String vacío por defecto.
 */
data class DatosOCR(
    val grasa:        String = "",
    val sng:          String = "",
    val densidad:     String = "",
    val proteina:     String = "",
    val lactosa:      String = "",
    val sales:        String = "",
    val totalSolidos: String = "",
    val aguaAnadida:  String = "",
    val puntoCongel:  String = "",
    val ph:           String = "",
    val uri:          String = "",
    val textoOCR:     String = ""
)

/**
 * Composable expect: picker de imagen con OCR integrado.
 * - Android: cámara + galería + ML Kit Text Recognition
 * - JVM: stub que muestra mensaje
 *
 * @param onDatos   Se llama con los datos parseados del ticket
 * @param onCancelar Se llama al cancelar
 */
@Composable
expect fun ImagenPickerConOCR(
    onDatos: (DatosOCR) -> Unit,
    onCancelar: () -> Unit,
    modifier: Modifier = Modifier
)
