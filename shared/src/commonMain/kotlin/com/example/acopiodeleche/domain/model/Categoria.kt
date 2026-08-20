package com.example.acopiodeleche.domain.model

/**
 * Categorías del acopio de leche.
 * Un enum sirve cuando las opciones son fijas y ninguna lleva datos propios.
 */
enum class Categoria(val etiqueta: String) {
    PROVEEDOR("Proveedores"),
    RECOLECCION("Recolección"),
    PROCESAMIENTO("Procesamiento"),
    DISTRIBUCION("Distribución")
}
