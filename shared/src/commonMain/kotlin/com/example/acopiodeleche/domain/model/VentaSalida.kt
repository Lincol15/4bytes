package com.example.acopiodeleche.domain.model

/**
 * Entidad VentaSalida — registra la salida/venta de productos de la planta.
 *
 * El Trabajador de Planta registra cada despacho.
 * El Gerente consulta el resumen.
 */
data class VentaSalida(
    val id: String,
    val idLote: String,                 // lote del que sale
    val producto: TipoProducto,
    val cliente: String,                // nombre del cliente o distribuidor
    val cantidad: Double,
    val unidadMedida: String,           // "kg", "L", "unidades"
    val precioPorUnidad: Double,        // precio por kg/L/unidad
    val fecha: String,                  // "dd/MM/yyyy"
    val idTrabajador: String,
    val observacion: String? = null
) {
    val total: Double
        get() = cantidad * precioPorUnidad

    val totalFormateado: String
        get() = "S/ ${"%.2f".let { total.let { t ->
            "${t.toLong()}.${((t - t.toLong()) * 100).toLong().toString().padStart(2,'0')}"
        }}}"

    val cantidadFormateada: String
        get() = "$cantidad $unidadMedida"

    val resumen: String
        get() = "${producto.etiqueta} · $cantidadFormateada → $cliente · $totalFormateado"
}
