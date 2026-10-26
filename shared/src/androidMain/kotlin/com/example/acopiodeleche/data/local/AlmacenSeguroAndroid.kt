package com.example.acopiodeleche.data.local

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Tokens cifrados con AES-256-GCM. La clave vive en el Android Keystore (no se puede extraer
 * del dispositivo); en SharedPreferences solo se guarda el texto cifrado.
 */
class AlmacenSeguroAndroid(context: Context) : AlmacenSeguro {

    private val preferencias = context.applicationContext.getSharedPreferences(ARCHIVO, Context.MODE_PRIVATE)

    override fun guardar(clave: String, valor: String) {
        val cifrador = Cipher.getInstance(TRANSFORMACION).apply { init(Cipher.ENCRYPT_MODE, claveSecreta()) }
        val cifrado = cifrador.doFinal(valor.encodeToByteArray())
        // Se guarda IV + texto cifrado; el IV es único por escritura
        val datos = cifrador.iv + cifrado
        preferencias.edit().putString(clave, Base64.encodeToString(datos, Base64.NO_WRAP)).apply()
    }

    override fun leer(clave: String): String? {
        val guardado = preferencias.getString(clave, null) ?: return null
        return try {
            val datos = Base64.decode(guardado, Base64.NO_WRAP)
            val iv = datos.copyOfRange(0, LARGO_IV)
            val cifrador = Cipher.getInstance(TRANSFORMACION).apply {
                init(Cipher.DECRYPT_MODE, claveSecreta(), GCMParameterSpec(BITS_ETIQUETA, iv))
            }
            cifrador.doFinal(datos.copyOfRange(LARGO_IV, datos.size)).decodeToString()
        } catch (e: Exception) {
            // Clave del Keystore regenerada o dato corrupto: se descarta y habrá que iniciar sesión
            borrar(clave)
            null
        }
    }

    override fun borrar(clave: String) {
        preferencias.edit().remove(clave).apply()
    }

    private fun claveSecreta(): SecretKey {
        val keyStore = KeyStore.getInstance(KEYSTORE).apply { load(null) }
        (keyStore.getEntry(ALIAS, null) as? KeyStore.SecretKeyEntry)?.let { return it.secretKey }
        val generador = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEYSTORE)
        generador.init(
            KeyGenParameterSpec.Builder(ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build()
        )
        return generador.generateKey()
    }

    private companion object {
        const val KEYSTORE = "AndroidKeyStore"
        const val ALIAS = "acopio_tokens"
        const val ARCHIVO = "acopio_seguro"
        const val TRANSFORMACION = "AES/GCM/NoPadding"
        const val LARGO_IV = 12
        const val BITS_ETIQUETA = 128
    }
}
