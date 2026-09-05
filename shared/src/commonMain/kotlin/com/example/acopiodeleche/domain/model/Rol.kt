package com.example.acopiodeleche.domain.model

/**
 * Roles del sistema 4bytes — Ecolácteos Huata.
 * Cada rol tiene acceso a módulos específicos.
 */
enum class Rol(val etiqueta: String, val descripcion: String) {
    ADMINISTRADOR("Administrador", "Gestiona usuarios, roles y configuración del sistema"),
    GERENTE("Gerente", "Supervisa el negocio: acopio, calidad, producción, pagos y reportes"),
    ACOPIADOR("Acopiador", "Registra entregas de leche en campo"),
    PRODUCTOR("Productor", "Consulta sus entregas, pagos y notificaciones"),
    CONTROL_CALIDAD("Control de Calidad", "Registra análisis de calidad de la leche"),
    TRABAJADOR_PLANTA("Trabajador de Planta", "Recepción, producción e inventario")
}
