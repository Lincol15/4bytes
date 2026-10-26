package com.example.acopiodeleche.domain.model

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Estado de la sesión activa.
 *
 * [token] es el access token de Laravel Sanctum (dura poco) y [refreshToken] sirve solo
 * para pedir un par nuevo cuando el access token vence. Los vencimientos son epoch en ms.
 */
data class SesionUsuario(
    val usuario: Usuario,
    val token: String = "",
    val refreshToken: String = "",
    val tokenExpiraEn: Long = 0,
    val refreshExpiraEn: Long = 0
)

/** Guarda la sesión en el dispositivo para abrir la app sin conexión. */
interface AlmacenSesion {
    fun leer(): SesionUsuario?
    fun guardar(sesion: SesionUsuario)
    fun borrar()
}

/**
 * Singleton con la sesión activa. Si hay [almacen], la sesión se persiste y se restaura
 * al abrir la app; así el acopiador puede trabajar sin conexión aunque reinicie el celular.
 */
object SesionActual {
    private val _estado = MutableStateFlow<SesionUsuario?>(null)
    val estado: StateFlow<SesionUsuario?> = _estado.asStateFlow()

    private val _sesionExpirada = MutableStateFlow(false)
    /** true si la sesión se cerró porque el refresh token venció o fue revocado. */
    val sesionExpirada: StateFlow<Boolean> = _sesionExpirada.asStateFlow()

    var almacen: AlmacenSesion? = null

    val sesion: SesionUsuario? get() = _estado.value
    val usuario: Usuario? get() = sesion?.usuario
    val rol: Rol? get() = sesion?.usuario?.rol
    val estaLogueado: Boolean get() = sesion != null

    /** Inicia o actualiza la sesión; si no se indica token, conserva el actual. */
    fun iniciar(usuario: Usuario, token: String = sesion?.token.orEmpty()) {
        abrir((sesion ?: SesionUsuario(usuario)).copy(usuario = usuario, token = token))
    }

    /** Abre la sesión (o la reemplaza tras renovar los tokens) y la guarda en el dispositivo. */
    fun abrir(nueva: SesionUsuario) {
        _estado.value = nueva
        _sesionExpirada.value = false
        almacen?.guardar(nueva)
    }

    /** Recupera la sesión guardada si el refresh token sigue vigente. */
    fun restaurar(ahora: Long) {
        val guardada = almacen?.leer() ?: return
        if (guardada.refreshExpiraEn > ahora) {
            _estado.value = guardada
        } else {
            almacen?.borrar()
        }
    }

    fun cerrar() {
        _estado.value = null
        almacen?.borrar()
    }

    /** Cierra la sesión porque el servidor ya no acepta el refresh token. */
    fun expirar() {
        cerrar()
        _sesionExpirada.value = true
    }
}
