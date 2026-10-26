package com.example.acopiodeleche.data.local

/**
 * Guarda secretos (access y refresh token) fuera de la base SQLite, en el almacén seguro de
 * cada plataforma: Android Keystore (AES-GCM) y Keychain en iOS.
 */
interface AlmacenSeguro {
    fun guardar(clave: String, valor: String)
    fun leer(clave: String): String?
    fun borrar(clave: String)
}

/** Sin cifrado: solo para Desktop (desarrollo) y pruebas. */
class AlmacenSeguroEnMemoria : AlmacenSeguro {
    private val valores = mutableMapOf<String, String>()

    override fun guardar(clave: String, valor: String) {
        valores[clave] = valor
    }

    override fun leer(clave: String): String? = valores[clave]

    override fun borrar(clave: String) {
        valores.remove(clave)
    }
}
