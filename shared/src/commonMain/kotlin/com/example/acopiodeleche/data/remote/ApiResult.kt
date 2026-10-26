package com.example.acopiodeleche.data.remote

/**
 * Resultado de una llamada a la API: éxito con datos o un error ya clasificado
 * con un mensaje listo para mostrar en la UI.
 */
sealed interface ApiResult<out T> {

    data class Exito<T>(val datos: T) : ApiResult<T>

    sealed interface Error : ApiResult<Nothing> {
        val mensaje: String
    }

    /** 422: datos rechazados por las validaciones de Laravel (campo -> mensajes). */
    data class ErrorValidacion(
        override val mensaje: String,
        val errores: Map<String, List<String>> = emptyMap()
    ) : Error

    /** 401: sin token o token vencido/revocado. */
    data class NoAutenticado(override val mensaje: String) : Error

    /** 403: el rol del usuario no tiene permiso para la operación. */
    data class SinPermiso(override val mensaje: String) : Error

    /** Otros códigos HTTP (404, 429, 5xx) o respuesta inesperada. */
    data class ErrorServidor(val codigo: Int, override val mensaje: String) : Error

    /** No se pudo contactar al servidor (sin red, URL incorrecta, tiempo agotado). */
    data class ErrorConexion(override val mensaje: String) : Error
}

inline fun <T, R> ApiResult<T>.map(transformar: (T) -> R): ApiResult<R> = when (this) {
    is ApiResult.Exito -> ApiResult.Exito(transformar(datos))
    is ApiResult.Error -> this
}
