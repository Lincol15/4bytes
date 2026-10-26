package com.example.acopiodeleche.domain.model

/**
 * Estado de la sesión activa.
 * Se guarda en memoria durante la ejecución de la app.
 */
data class SesionUsuario(
    val usuario: Usuario,
    val token: String = ""          // token Bearer de Laravel Sanctum
)

/**
 * Singleton que mantiene la sesión activa en memoria.
 * El token no se persiste: al cerrar la app se vuelve a iniciar sesión.
 */
object SesionActual {
    var sesion: SesionUsuario? = null

    val usuario: Usuario? get() = sesion?.usuario
    val rol: Rol? get() = sesion?.usuario?.rol
    val estaLogueado: Boolean get() = sesion != null

    /** Inicia o actualiza la sesión; si no se indica token, conserva el actual. */
    fun iniciar(usuario: Usuario, token: String = sesion?.token.orEmpty()) {
        sesion = SesionUsuario(usuario, token)
    }

    fun cerrar() {
        sesion = null
    }
}
