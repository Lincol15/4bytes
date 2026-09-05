package com.example.acopiodeleche.domain.model

/**
 * Estado de la sesión activa.
 * Se guarda en memoria durante la ejecución de la app.
 */
data class SesionUsuario(
    val usuario: Usuario,
    val token: String = ""          // para uso futuro con JWT/backend
)

/**
 * Singleton que mantiene la sesión activa en memoria.
 * En fase de BD se reemplazará por DataStore/SharedPreferences.
 */
object SesionActual {
    var sesion: SesionUsuario? = null

    val usuario: Usuario? get() = sesion?.usuario
    val rol: Rol? get() = sesion?.usuario?.rol
    val estaLogueado: Boolean get() = sesion != null

    fun iniciar(usuario: Usuario) {
        sesion = SesionUsuario(usuario)
    }

    fun cerrar() {
        sesion = null
    }
}
