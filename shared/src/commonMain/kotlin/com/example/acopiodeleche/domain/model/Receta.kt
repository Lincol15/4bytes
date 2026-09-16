package com.example.acopiodeleche.domain.model

/**
 * Receta — define cómo se produce un producto final.
 * Ej: "Queso 1kg" necesita 10L de leche, 0.1 unid cuajo, 200g sal
 */
data class Receta(
    val id: String,
    val nombre: String,                 // "Queso 1kg", "Queso 2kg", "Yogur 1L"
    val producto: TipoProducto,
    val descripcion: String? = null,
    val activa: Boolean = true
) {
    val icono: String get() = producto.icono
}

/**
 * RecetaDetalle — cada insumo que necesita una receta.
 * Ej: id_receta=1, insumo="Leche", cantidad=10.0, unidad="litros"
 */
data class RecetaDetalle(
    val id: String,
    val idReceta: String,
    val insumo: String,                 // "Leche", "Cuajo", "Sal", "Azúcar"...
    val cantidad: Double,               // cantidad por UNIDAD producida
    val unidadMedida: String            // "litros", "gramos", "unidades", "ml"
) {
    val resumen: String get() = "$insumo: $cantidad $unidadMedida"
}

/**
 * ProduccionJornada — una sesión/jornada de producción (fecha).
 * Equivale a la entidad "produccion" del diagrama del docente.
 */
data class ProduccionJornada(
    val id: String,
    val fecha: String,                  // "dd/MM/yyyy"
    val idTrabajador: String,
    val observacion: String? = null
)

/**
 * ProduccionDetalle — qué receta se usó en una jornada y cuántas unidades.
 * Equivale a "Produccion_detalle" del diagrama: id_produccion, id_receta, cantidad
 */
data class ProduccionDetalle(
    val id: String,
    val idJornada: String,
    val idReceta: String,
    val cantidadProducida: Double,      // unidades producidas de esa receta
    val litrosTotalesUsados: Double     // calculado: cantidadProducida * litros/unidad de la receta
)
