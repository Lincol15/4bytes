package com.example.acopiodeleche.data.local

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import platform.CoreFoundation.CFDictionaryAddValue
import platform.CoreFoundation.CFDictionaryCreateMutable
import platform.CoreFoundation.CFDictionaryRef
import platform.CoreFoundation.CFRelease
import platform.CoreFoundation.CFStringRef
import platform.CoreFoundation.CFTypeRef
import platform.CoreFoundation.CFTypeRefVar
import platform.CoreFoundation.kCFBooleanTrue
import platform.CoreFoundation.kCFTypeDictionaryKeyCallBacks
import platform.CoreFoundation.kCFTypeDictionaryValueCallBacks
import platform.Foundation.CFBridgingRelease
import platform.Foundation.CFBridgingRetain
import platform.Foundation.NSData
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.Foundation.dataUsingEncoding
import platform.Security.SecItemAdd
import platform.Security.SecItemCopyMatching
import platform.Security.SecItemDelete
import platform.Security.errSecSuccess
import platform.Security.kSecAttrAccessible
import platform.Security.kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly
import platform.Security.kSecAttrAccount
import platform.Security.kSecAttrService
import platform.Security.kSecClass
import platform.Security.kSecClassGenericPassword
import platform.Security.kSecMatchLimit
import platform.Security.kSecMatchLimitOne
import platform.Security.kSecReturnData
import platform.Security.kSecValueData

/**
 * Tokens en el Keychain de iOS (contraseña genérica por clave), accesibles solo en este
 * dispositivo y después del primer desbloqueo.
 */
@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
class AlmacenSeguroIos(private val servicio: String = "com.example.acopiodeleche") : AlmacenSeguro {

    override fun guardar(clave: String, valor: String) {
        borrar(clave)
        val datos = NSString.create(string = valor).dataUsingEncoding(NSUTF8StringEncoding) ?: return
        val datosCf = CFBridgingRetain(datos)
        try {
            conConsulta(
                clave,
                kSecValueData to datosCf,
                kSecAttrAccessible to kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly
            ) { consulta -> SecItemAdd(consulta, null) }
        } finally {
            CFRelease(datosCf)
        }
    }

    override fun leer(clave: String): String? = memScoped {
        val resultado = alloc<CFTypeRefVar>()
        val estado = conConsulta(
            clave,
            kSecReturnData to kCFBooleanTrue,
            kSecMatchLimit to kSecMatchLimitOne
        ) { consulta -> SecItemCopyMatching(consulta, resultado.ptr) }
        if (estado != errSecSuccess) return@memScoped null
        val datos = CFBridgingRelease(resultado.value) as? NSData ?: return@memScoped null
        NSString.create(data = datos, encoding = NSUTF8StringEncoding) as String?
    }

    override fun borrar(clave: String) {
        conConsulta(clave) { consulta -> SecItemDelete(consulta) }
    }

    /** Arma el diccionario de consulta del Keychain (clase, servicio y cuenta + extras) y lo libera al terminar. */
    private fun <T> conConsulta(
        clave: String,
        vararg extras: Pair<CFStringRef?, CFTypeRef?>,
        accion: (CFDictionaryRef?) -> T
    ): T {
        val servicioCf = CFBridgingRetain(servicio)
        val claveCf = CFBridgingRetain(clave)
        val consulta = CFDictionaryCreateMutable(null, 0, kCFTypeDictionaryKeyCallBacks.ptr, kCFTypeDictionaryValueCallBacks.ptr)
        CFDictionaryAddValue(consulta, kSecClass, kSecClassGenericPassword)
        CFDictionaryAddValue(consulta, kSecAttrService, servicioCf)
        CFDictionaryAddValue(consulta, kSecAttrAccount, claveCf)
        extras.forEach { (k, v) -> CFDictionaryAddValue(consulta, k, v) }
        return try {
            accion(consulta)
        } finally {
            CFRelease(consulta)
            CFRelease(servicioCf)
            CFRelease(claveCf)
        }
    }
}
