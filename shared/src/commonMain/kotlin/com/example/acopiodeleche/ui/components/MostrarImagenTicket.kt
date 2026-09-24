package com.example.acopiodeleche.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun MostrarImagenTicket(
    imagenUri: String,
    modifier: Modifier = Modifier
)
