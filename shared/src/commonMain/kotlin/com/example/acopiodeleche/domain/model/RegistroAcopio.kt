package com.example.acopiodeleche.domain.model

/**
 * Entidad RegistroAcopio — modelo real del flujo de acopio de leche.
 *
 * Representa un registro de recolección: un acopiador visita a un productor,
 * recoge leche con un vehículo y registra la cantidad.
 *
 * NOTA: RegistroLeche se mantiene separado porque los tests de la sesión 2
 * dependen de él. En la sesión de base de datos se unificarán ambos modelos.
 *
 * Relación con Productor: via [idProductor] (referencia por ID).
 * El nombre completo se resuelve en la UI consultando ProductorService.
 */
data class RegistroAcopio(
    val id: String,
    val idProductor: String,        // referencia a Productor.idProductor
    val acopiador: String,          // nombre del acopiador que recoge la leche
    val vehiculo: String,           // ej: "Motocarga 01", "Furgón 02"
    val zona: String,               // comunidad o zona de recolección
    val fecha: String,              // formato "dd/MM/yyyy"
    val hora: String,               // formato "HH:mm"
    val litros: Double,
    val observacion: String? = null,
    val recibido: Boolean = false   // false = en tránsito, true = recibido en planta
) {
    /** Litros con unidad para mostrar en UI. */
    val litrosFormateados: String
        get() = "$litros L"

    /** Observación con valor por defecto. */
    val observacionCorta: String
        get() = observacion ?: "Sin observación"

    /** Estado de recepción legible. */
    val estadoRecepcion: String
        get() = if (recibido) "Recibido en planta" else "En tránsito"

    /** Validación básica: litros positivos y campos obligatorios no vacíos. */
    val esValido: Boolean
        get() = litros > 0
                && idProductor.isNotBlank()
                && acopiador.isNotBlank()
                && vehiculo.isNotBlank()
                && fecha.isNotBlank()
                && hora.isNotBlank()
}
