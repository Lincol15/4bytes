package com.example.acopiodeleche.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Envoltura estándar de Laravel API Resources: { "data": ... }. */
@Serializable
data class DataDto<T>(val data: T)

/** Colección (paginada o no). Si es paginada, Laravel añade "meta". */
@Serializable
data class PaginaDto<T>(
    val data: List<T>,
    val meta: MetaPaginaDto? = null
)

@Serializable
data class MetaPaginaDto(
    @SerialName("current_page") val paginaActual: Int,
    @SerialName("last_page") val ultimaPagina: Int,
    val total: Int
)

/** Cuerpo de error de Laravel: { "message": "...", "errors": { "campo": ["..."] } }. */
@Serializable
data class ErrorDto(
    val message: String? = null,
    val errors: Map<String, List<String>> = emptyMap()
)
