package com.example.acopiodeleche.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Resultado de captura/selección de imagen.
 * En commonMain solo guardamos la ruta/URI como String.
 * En Android será el URI, en JVM será la ruta del archivo.
 */
data class ImagenResult(
    val uri: String,          // URI o ruta del archivo
    val esEscaneo: Boolean    // true = desde cámara, false = desde galería
)

/**
 * Composable expect para lanzar cámara o galería.
 * El actual en Android usa ActivityResultLauncher.
 * El actual en JVM muestra un stub.
 *
 * @param onImagen  Se llama cuando el usuario selecciona/captura imagen
 * @param onCancelar Se llama cuando cancela
 * @param content   Contenido que muestra los botones de acción
 */
@Composable
expect fun ImagenPickerSheet(
    onImagen: (ImagenResult) -> Unit,
    onCancelar: () -> Unit,
    modifier: Modifier = Modifier
)
