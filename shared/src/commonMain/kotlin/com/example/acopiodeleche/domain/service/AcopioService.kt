package com.example.acopiodeleche.domain.service

import com.example.acopiodeleche.domain.model.Categoria
import com.example.acopiodeleche.domain.model.RegistroLeche

/**
 * Servicio de Acopio / Entrega de Leche.
 *
 * Gestiona el CRUD completo de registros de entrega usando una lista en memoria.
 * Trabaja sobre la clase [RegistroLeche] que ya existe en el proyecto.
 */
class AcopioService {

    // Repositorio en memoria
    private val repositorio: MutableList<RegistroLeche> = mutableListOf()

    // Límite de litros por entrega para la validación de negocio
    companion object {
        const val LITROS_MINIMOS = 0.0
        const val LITROS_MAXIMOS = 5000.0
    }

    // ----------------------------------------------------------------
    // VALIDACIÓN
    // ----------------------------------------------------------------

    /**
     * Valida que la cantidad de litros sea positiva y no supere el máximo permitido.
     * @throws IllegalArgumentException si los litros no son válidos.
     */
    fun validarLitros(litros: Double) {
        require(litros > LITROS_MINIMOS) {
            "La cantidad de litros debe ser mayor a $LITROS_MINIMOS. Recibido: $litros"
        }
        require(litros <= LITROS_MAXIMOS) {
            "La cantidad de litros no puede superar $LITROS_MAXIMOS. Recibido: $litros"
        }
    }

    // ----------------------------------------------------------------
    // REGISTRAR
    // ----------------------------------------------------------------

    /**
     * Registra una nueva entrega de leche.
     * @throws IllegalArgumentException si el id ya existe o los litros no son válidos.
     */
    fun registrar(registro: RegistroLeche): RegistroLeche {
        validarLitros(registro.litros)
        require(repositorio.none { it.id == registro.id }) {
            "Ya existe una entrega con el id '${registro.id}'."
        }
        repositorio.add(registro)
        return registro
    }

    // ----------------------------------------------------------------
    // OBTENER POR ID
    // ----------------------------------------------------------------

    /**
     * Devuelve la entrega con el [id] indicado, o null si no existe.
     */
    fun obtenerPorId(id: String): RegistroLeche? =
        repositorio.find { it.id == id }

    // ----------------------------------------------------------------
    // LISTAR
    // ----------------------------------------------------------------

    /**
     * Devuelve todas las entregas registradas (copia inmutable).
     */
    fun listar(): List<RegistroLeche> = repositorio.toList()

    /**
     * Devuelve solo las entregas activas y válidas.
     */
    fun listarValidas(): List<RegistroLeche> =
        repositorio.filter { it.esValido }

    // ----------------------------------------------------------------
    // BUSCAR POR PROVEEDOR
    // ----------------------------------------------------------------

    /**
     * Devuelve todas las entregas del proveedor cuyo nombre contiene [nombreProveedor]
     * (sin distinción de mayúsculas).
     */
    fun buscarPorProveedor(nombreProveedor: String): List<RegistroLeche> {
        val query = nombreProveedor.trim().lowercase()
        return repositorio.filter { it.proveedor.lowercase().contains(query) }
    }

    /**
     * Devuelve todas las entregas de una [categoria] específica.
     */
    fun buscarPorCategoria(categoria: Categoria): List<RegistroLeche> =
        repositorio.filter { it.categoria == categoria }

    // ----------------------------------------------------------------
    // ACTUALIZAR
    // ----------------------------------------------------------------

    /**
     * Reemplaza los datos de la entrega que tenga el mismo [id].
     * @throws NoSuchElementException si la entrega no existe.
     * @throws IllegalArgumentException si los litros no son válidos.
     */
    fun actualizar(registro: RegistroLeche): RegistroLeche {
        validarLitros(registro.litros)
        val indice = repositorio.indexOfFirst { it.id == registro.id }
        if (indice == -1) throw NoSuchElementException(
            "No se encontró una entrega con id '${registro.id}'."
        )
        repositorio[indice] = registro
        return registro
    }

    // ----------------------------------------------------------------
    // ELIMINAR
    // ----------------------------------------------------------------

    /**
     * Elimina la entrega con el [id] indicado.
     * @return true si fue eliminada, false si no existía.
     */
    fun eliminar(id: String): Boolean =
        repositorio.removeAll { it.id == id }

    // ----------------------------------------------------------------
    // UTILIDAD INTERNA (para tests)
    // ----------------------------------------------------------------

    /** Vacía el repositorio. Útil para limpiar el estado entre tests. */
    fun limpiar() = repositorio.clear()

    /** Cantidad de entregas registradas. */
    val total: Int get() = repositorio.size

    /** Suma total de litros registrados. */
    val totalLitros: Double get() = repositorio.sumOf { it.litros }
}
