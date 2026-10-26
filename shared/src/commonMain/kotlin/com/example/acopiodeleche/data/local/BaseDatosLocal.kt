package com.example.acopiodeleche.data.local

import com.example.acopiodeleche.db.AcopioDatabase
import com.example.acopiodeleche.domain.model.AlmacenSesion
import com.example.acopiodeleche.domain.model.Rol
import com.example.acopiodeleche.domain.model.SesionActual
import com.example.acopiodeleche.domain.model.SesionUsuario
import com.example.acopiodeleche.domain.model.Usuario

/** Copia local de las respuestas GET de la API, separada por usuario. */
interface CacheRespuestas {
    fun leer(clave: String): String?
    fun guardar(clave: String, cuerpo: String)
}

/** Caché en SQLite; sin sesión activa no lee ni guarda nada. */
class CacheRespuestasSql(
    private val db: AcopioDatabase,
    private val ahora: () -> Long,
    private val idUsuario: () -> String? = { SesionActual.usuario?.id }
) : CacheRespuestas {

    override fun leer(clave: String): String? {
        val usuario = idUsuario() ?: return null
        return db.cacheRespuestaQueries.obtener(usuario, clave).executeAsOneOrNull()
    }

    override fun guardar(clave: String, cuerpo: String) {
        val usuario = idUsuario() ?: return
        db.cacheRespuestaQueries.guardar(usuario, clave, cuerpo, ahora())
    }
}

/**
 * Sesión guardada: usuario y vencimientos en la tabla `sesion` (una sola fila) y los tokens
 * cifrados en [seguro] (Keystore / Keychain), nunca en SQLite.
 */
class AlmacenSesionSql(
    private val db: AcopioDatabase,
    private val seguro: AlmacenSeguro
) : AlmacenSesion {

    override fun leer(): SesionUsuario? {
        val fila = db.sesionQueries.obtener().executeAsOneOrNull() ?: return null
        val rol = Rol.entries.firstOrNull { it.name == fila.rol } ?: return null
        val accessToken = seguro.leer(CLAVE_ACCESS)
        val refreshToken = seguro.leer(CLAVE_REFRESH)
        if (accessToken == null || refreshToken == null) return null
        return SesionUsuario(
            usuario = Usuario(
                id = fila.id_usuario,
                nombres = fila.nombres,
                apellidos = fila.apellidos,
                dni = fila.dni,
                telefono = fila.telefono,
                correo = fila.correo,
                contrasena = "",
                rol = rol,
                idProductor = fila.id_productor,
                comunidad = fila.comunidad,
                vehiculo = fila.vehiculo
            ),
            token = accessToken,
            refreshToken = refreshToken,
            tokenExpiraEn = fila.access_expira_en,
            refreshExpiraEn = fila.refresh_expira_en
        )
    }

    override fun guardar(sesion: SesionUsuario) {
        // Solo se persisten sesiones reales del backend (con refresh token).
        if (sesion.refreshToken.isBlank()) return
        val u = sesion.usuario
        seguro.guardar(CLAVE_ACCESS, sesion.token)
        seguro.guardar(CLAVE_REFRESH, sesion.refreshToken)
        db.sesionQueries.guardar(
            u.id, u.nombres, u.apellidos, u.dni, u.telefono, u.correo, u.rol.name,
            u.idProductor, u.comunidad, u.vehiculo,
            sesion.tokenExpiraEn, sesion.refreshExpiraEn
        )
    }

    override fun borrar() {
        seguro.borrar(CLAVE_ACCESS)
        seguro.borrar(CLAVE_REFRESH)
        db.sesionQueries.borrar()
    }

    private companion object {
        const val CLAVE_ACCESS = "access_token"
        const val CLAVE_REFRESH = "refresh_token"
    }
}
