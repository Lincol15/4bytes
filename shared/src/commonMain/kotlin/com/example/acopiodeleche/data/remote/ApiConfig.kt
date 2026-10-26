package com.example.acopiodeleche.data.remote

/**
 * Configuración de conexión con el backend Laravel.
 *
 * La URL base se fija al arrancar cada plataforma (Android la toma de BuildConfig,
 * definido por local.properties) para poder cambiar entre desarrollo y producción
 * sin tocar el código. Debe terminar en "/api/v1/".
 *
 * - Emulador Android:   http://10.0.2.2:8000/api/v1/
 * - Simulador iOS:      http://localhost:8000/api/v1/
 * - Celular físico:     http://<IP-LAN-del-equipo>:8000/api/v1/
 * - Producción:         https://<dominio>/api/v1/
 */
object ApiConfig {
    var baseUrl: String = "http://localhost:8000/api/v1/"
        set(value) {
            field = if (value.isBlank() || value.endsWith("/")) value else "$value/"
        }
}
