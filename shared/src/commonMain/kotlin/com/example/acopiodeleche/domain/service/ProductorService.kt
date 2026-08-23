package com.example.acopiodeleche.domain.service

import com.example.acopiodeleche.domain.model.Productor

/**
 * Servicio de Productores.
 *
 * Gestiona el CRUD completo de productores usando una lista en memoria.
 * En fases posteriores este repositorio interno se reemplazará por una
 * base de datos (Room / SQLDelight) sin cambiar la firma pública del servicio.
 */
class ProductorService {

    // Repositorio en memoria — visible internamente para los tests
    private val repositorio: MutableList<Productor> = mutableListOf()

    // ----------------------------------------------------------------
    // REGISTRAR
    // ----------------------------------------------------------------

    /**
     * Registra un nuevo productor.
     * @throws IllegalArgumentException si el productor no es válido o el DNI ya existe.
     */
    fun registrar(productor: Productor): Productor {
        require(productor.esValido) {
            "El productor no es válido: revisa que id, nombres, apellidos y dni no estén en blanco."
        }
        require(repositorio.none { it.idProductor == productor.idProductor }) {
            "Ya existe un productor con el id '${productor.idProductor}'."
        }
        require(repositorio.none { it.dni == productor.dni }) {
            "Ya existe un productor con el DNI '${productor.dni}'."
        }
        repositorio.add(productor)
        return productor
    }

    // ----------------------------------------------------------------
    // OBTENER POR ID
    // ----------------------------------------------------------------

    /**
     * Devuelve el productor con el [id] indicado, o null si no existe.
     */
    fun obtenerPorId(id: String): Productor? =
        repositorio.find { it.idProductor == id }

    // ----------------------------------------------------------------
    // LISTAR
    // ----------------------------------------------------------------

    /**
     * Devuelve todos los productores registrados (copia inmutable).
     */
    fun listar(): List<Productor> = repositorio.toList()

    /**
     * Devuelve solo los productores activos.
     */
    fun listarActivos(): List<Productor> =
        repositorio.filter { it.estado }

    // ----------------------------------------------------------------
    // BUSCAR
    // ----------------------------------------------------------------

    /**
     * Busca productores cuyo nombre completo o DNI contenga el [texto] (sin distinción de mayúsculas).
     */
    fun buscar(texto: String): List<Productor> {
        val query = texto.trim().lowercase()
        return repositorio.filter { p ->
            p.nombreCompleto.lowercase().contains(query) ||
            p.dni.contains(query) ||
            p.comunidad.lowercase().contains(query)
        }
    }

    // ----------------------------------------------------------------
    // ACTUALIZAR
    // ----------------------------------------------------------------

    /**
     * Reemplaza los datos del productor que tenga el mismo [idProductor].
     * @throws NoSuchElementException si el productor no existe.
     * @throws IllegalArgumentException si los nuevos datos no son válidos.
     */
    fun actualizar(productor: Productor): Productor {
        require(productor.esValido) {
            "El productor no es válido: revisa que id, nombres, apellidos y dni no estén en blanco."
        }
        val indice = repositorio.indexOfFirst { it.idProductor == productor.idProductor }
        if (indice == -1) throw NoSuchElementException(
            "No se encontró un productor con id '${productor.idProductor}'."
        )
        repositorio[indice] = productor
        return productor
    }

    // ----------------------------------------------------------------
    // ELIMINAR
    // ----------------------------------------------------------------

    /**
     * Elimina el productor con el [id] indicado.
     * @return true si fue eliminado, false si no existía.
     */
    fun eliminar(id: String): Boolean =
        repositorio.removeAll { it.idProductor == id }

    // ----------------------------------------------------------------
    // UTILIDAD INTERNA (para tests)
    // ----------------------------------------------------------------

    /** Vacía el repositorio. Útil para limpiar el estado entre tests. */
    fun limpiar() = repositorio.clear()

    /** Cantidad de productores registrados. */
    val total: Int get() = repositorio.size
}
