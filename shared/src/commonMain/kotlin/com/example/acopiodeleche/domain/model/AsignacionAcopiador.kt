package com.example.acopiodeleche.domain.model

/**
 * Asignación de productores a un acopiador.
 * El admin configura qué productores atiende cada acopiador.
 */
data class AsignacionAcopiador(
    val idAcopiador: String,               // referencia a Usuario.id con rol ACOPIADOR
    val idsProductores: List<String>       // lista de Productor.idProductor asignados
)
