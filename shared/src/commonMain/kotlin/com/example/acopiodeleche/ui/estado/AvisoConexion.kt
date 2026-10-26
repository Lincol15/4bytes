package com.example.acopiodeleche.ui.estado

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.acopiodeleche.ui.tema.Tono

/** Texto del aviso superior, o null si no hay nada que avisar. */
fun textoAvisoConexion(
    enLinea: Boolean,
    pendientes: Int,
    rechazadas: Int,
    sesionExpirada: Boolean
): String? {
    if (sesionExpirada) return "Su sesión expiró. Inicie sesión nuevamente."
    val partes = buildList {
        if (!enLinea) add("Sin conexión · mostrando datos guardados")
        if (pendientes > 0) add("$pendientes ${if (pendientes == 1) "recolección" else "recolecciones"} por enviar")
        if (rechazadas > 0) add("$rechazadas sin sincronizar (revise Acopio)")
    }
    return partes.takeIf { it.isNotEmpty() }?.joinToString(" · ")
}

/** Franja superior con el estado de conexión y sincronización. */
@Composable
fun AvisoConexion(texto: String, esAlerta: Boolean, modifier: Modifier = Modifier) {
    val tono = if (esAlerta) Tono.ROJO else Tono.DORADO
    val fondo = tono.fondo
    val color = tono.texto
    Text(
        text = texto,
        color = color,
        style = MaterialTheme.typography.labelMedium,
        textAlign = TextAlign.Center,
        modifier = modifier
            .fillMaxWidth()
            .background(fondo)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    )
}
