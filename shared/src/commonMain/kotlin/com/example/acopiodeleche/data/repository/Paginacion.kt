package com.example.acopiodeleche.data.repository

import com.example.acopiodeleche.domain.ApiResult
import com.example.acopiodeleche.data.remote.dto.PaginaDto

/** Límite de seguridad para no recorrer listados enormes desde el móvil. */
private const val MAX_PAGINAS = 20

/** Recorre las páginas de un listado de Laravel y devuelve todos los elementos. */
internal suspend fun <T> obtenerTodasLasPaginas(
    pedirPagina: suspend (Int) -> ApiResult<PaginaDto<T>>
): ApiResult<List<T>> {
    val elementos = mutableListOf<T>()
    var pagina = 1
    while (true) {
        when (val resultado = pedirPagina(pagina)) {
            is ApiResult.Error -> return resultado
            is ApiResult.Exito -> {
                elementos += resultado.datos.data
                val ultima = resultado.datos.meta?.ultimaPagina ?: pagina
                if (pagina >= ultima || pagina >= MAX_PAGINAS) return ApiResult.Exito(elementos)
                pagina++
            }
        }
    }
}

internal fun idNumerico(id: String): Int? = id.toIntOrNull()

internal val ERROR_ID_INVALIDO = ApiResult.ErrorValidacion("El registro no proviene del servidor.")
