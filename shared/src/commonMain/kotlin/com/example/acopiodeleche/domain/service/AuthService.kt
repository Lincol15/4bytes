package com.example.acopiodeleche.domain.service

import com.example.acopiodeleche.domain.model.Rol
import com.example.acopiodeleche.domain.model.SesionActual
import com.example.acopiodeleche.domain.model.Usuario

/**
 * Servicio de Autenticación.
 *
 * En esta fase trabaja con usuarios en memoria (DatosMock).
 * En la fase de BD se conectará con SQLDelight.
 * En la fase de backend se reemplazará por JWT.
 */
class AuthService(
    private val usuarios: MutableList<Usuario> = mutableListOf()
) {

    // ----------------------------------------------------------------
    // LOGIN
    // ----------------------------------------------------------------

    /**
     * Intenta iniciar sesión con correo y contraseña.
     * @return el Usuario si las credenciales son correctas, null si no.
     */
    fun login(correo: String, contrasena: String): Usuario? {
        val usuario = usuarios.find {
            it.correo.trim().lowercase() == correo.trim().lowercase()
            && it.contrasena == contrasena
            && it.estado
        }
        if (usuario != null) {
            SesionActual.iniciar(usuario)
        }
        return usuario
    }

    /**
     * Cierra la sesión activa.
     */
    fun logout() {
        SesionActual.cerrar()
    }

    // ----------------------------------------------------------------
    // USUARIOS (administrador)
    // ----------------------------------------------------------------

    fun registrar(usuario: Usuario): Usuario {
        require(usuario.esValido) { "El usuario no es válido." }
        require(usuarios.none { it.id == usuario.id }) {
            "Ya existe un usuario con el id '${usuario.id}'."
        }
        require(usuarios.none { it.correo.lowercase() == usuario.correo.lowercase() }) {
            "Ya existe un usuario con el correo '${usuario.correo}'."
        }
        usuarios.add(usuario)
        return usuario
    }

    fun listar(): List<Usuario> = usuarios.toList()

    fun listarPorRol(rol: Rol): List<Usuario> =
        usuarios.filter { it.rol == rol }

    fun obtenerPorId(id: String): Usuario? =
        usuarios.find { it.id == id }

    fun actualizar(usuario: Usuario): Usuario {
        require(usuario.esValido) { "El usuario no es válido." }
        val indice = usuarios.indexOfFirst { it.id == usuario.id }
        if (indice == -1) throw NoSuchElementException("No se encontró el usuario '${usuario.id}'.")
        usuarios[indice] = usuario
        return usuario
    }

    fun desactivar(id: String): Boolean {
        val indice = usuarios.indexOfFirst { it.id == id }
        if (indice == -1) return false
        usuarios[indice] = usuarios[indice].copy(estado = false)
        return true
    }

    fun reactivar(id: String): Boolean {
        val indice = usuarios.indexOfFirst { it.id == id }
        if (indice == -1) return false
        usuarios[indice] = usuarios[indice].copy(estado = true)
        return true
    }

    fun limpiar() = usuarios.clear()
    val total: Int get() = usuarios.size
}
