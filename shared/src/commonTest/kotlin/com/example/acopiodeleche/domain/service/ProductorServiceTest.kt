package com.example.acopiodeleche.domain.service

import com.example.acopiodeleche.domain.model.Productor
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.assertFailsWith

class ProductorServiceTest {

    // Se recrea antes de CADA test gracias a @BeforeTest — garantiza aislamiento
    private lateinit var servicio: ProductorService

    @BeforeTest
    fun setUp() {
        servicio = ProductorService()
    }

    // ----------------------------------------------------------------
    // Datos de prueba
    // ----------------------------------------------------------------

    private fun productorDemo(
        id: String = "p-01",
        nombres: String = "Juan",
        apellidos: String = "Quispe Mamani",
        dni: String = "12345678",
        telefono: String = "987654321",
        direccion: String = "Av. Los Álamos 123",
        comunidad: String = "San Juan",
        estado: Boolean = true
    ) = Productor(
        idProductor = id,
        nombres = nombres,
        apellidos = apellidos,
        dni = dni,
        telefono = telefono,
        direccion = direccion,
        comunidad = comunidad,
        estado = estado
    )

    // ================================================================
    // REGISTRAR
    // ================================================================

    @Test
    fun registrarProductorCorrectamente() {
        val productor = productorDemo()
        val resultado = servicio.registrar(productor)

        assertEquals(productor, resultado)
        assertEquals(1, servicio.total)
    }

    @Test
    fun registrarVariosProductores() {
        servicio.registrar(productorDemo(id = "p-01", dni = "11111111"))
        servicio.registrar(productorDemo(id = "p-02", dni = "22222222"))
        servicio.registrar(productorDemo(id = "p-03", dni = "33333333"))

        assertEquals(3, servicio.total)
    }

    @Test
    fun registrarProductorConIdDuplicadoLanzaExcepcion() {
        servicio.registrar(productorDemo(id = "p-01", dni = "11111111"))

        assertFailsWith<IllegalArgumentException> {
            servicio.registrar(productorDemo(id = "p-01", dni = "99999999"))
        }
    }

    @Test
    fun registrarProductorConDniDuplicadoLanzaExcepcion() {
        servicio.registrar(productorDemo(id = "p-01", dni = "12345678"))

        assertFailsWith<IllegalArgumentException> {
            servicio.registrar(productorDemo(id = "p-02", dni = "12345678"))
        }
    }

    @Test
    fun registrarProductorConNombresBlancosLanzaExcepcion() {
        assertFailsWith<IllegalArgumentException> {
            servicio.registrar(productorDemo(nombres = ""))
        }
    }

    @Test
    fun registrarProductorConDniBlancosLanzaExcepcion() {
        assertFailsWith<IllegalArgumentException> {
            servicio.registrar(productorDemo(dni = ""))
        }
    }

    // ================================================================
    // OBTENER POR ID
    // ================================================================

    @Test
    fun obtenerProductorPorIdExistente() {
        val productor = productorDemo()
        servicio.registrar(productor)

        val encontrado = servicio.obtenerPorId("p-01")

        assertNotNull(encontrado)
        assertEquals("p-01", encontrado.idProductor)
        assertEquals("Juan", encontrado.nombres)
    }

    @Test
    fun obtenerProductorInexistenteDevuelveNull() {
        val resultado = servicio.obtenerPorId("id-que-no-existe")

        assertNull(resultado)
    }

    // ================================================================
    // LISTAR
    // ================================================================

    @Test
    fun listarDevuelveTodosLosProductores() {
        servicio.registrar(productorDemo(id = "p-01", dni = "11111111"))
        servicio.registrar(productorDemo(id = "p-02", dni = "22222222"))

        val lista = servicio.listar()

        assertEquals(2, lista.size)
    }

    @Test
    fun listarConRepositorioVacioDevuelveListaVacia() {
        val lista = servicio.listar()

        assertTrue(lista.isEmpty())
    }

    @Test
    fun listarActivosDevuelveSoloProductoresActivos() {
        servicio.registrar(productorDemo(id = "p-01", dni = "11111111", estado = true))
        servicio.registrar(productorDemo(id = "p-02", dni = "22222222", estado = false))
        servicio.registrar(productorDemo(id = "p-03", dni = "33333333", estado = true))

        val activos = servicio.listarActivos()

        assertEquals(2, activos.size)
        assertTrue(activos.all { it.estado })
    }

    // ================================================================
    // BUSCAR
    // ================================================================

    @Test
    fun buscarPorNombreDevuelveCoincidencias() {
        servicio.registrar(productorDemo(id = "p-01", dni = "11111111", nombres = "Juan",  apellidos = "Pérez",  comunidad = "Zona A"))
        servicio.registrar(productorDemo(id = "p-02", dni = "22222222", nombres = "María", apellidos = "López",  comunidad = "Zona B"))
        servicio.registrar(productorDemo(id = "p-03", dni = "33333333", nombres = "Juana", apellidos = "Gómez",  comunidad = "Zona C"))

        val resultado = servicio.buscar("juan")

        assertEquals(2, resultado.size)
    }

    @Test
    fun buscarPorDniDevuelveCoincidencia() {
        servicio.registrar(productorDemo(id = "p-01", dni = "12345678"))
        servicio.registrar(productorDemo(id = "p-02", dni = "87654321"))

        val resultado = servicio.buscar("12345678")

        assertEquals(1, resultado.size)
        assertEquals("12345678", resultado.first().dni)
    }

    @Test
    fun buscarSinCoincidenciasDevuelveListaVacia() {
        servicio.registrar(productorDemo())

        val resultado = servicio.buscar("TEXTO_QUE_NO_EXISTE")

        assertTrue(resultado.isEmpty())
    }

    // ================================================================
    // ACTUALIZAR
    // ================================================================

    @Test
    fun actualizarProductorCorrectamente() {
        val original = productorDemo(telefono = "111111111")
        servicio.registrar(original)

        val actualizado = original.copy(telefono = "999999999")
        val resultado = servicio.actualizar(actualizado)

        assertEquals("999999999", resultado.telefono)
        assertEquals("999999999", servicio.obtenerPorId("p-01")?.telefono)
    }

    @Test
    fun actualizarProductorInexistenteLanzaExcepcion() {
        assertFailsWith<NoSuchElementException> {
            servicio.actualizar(productorDemo(id = "no-existe"))
        }
    }

    @Test
    fun actualizarNoModificaOtrosProductores() {
        servicio.registrar(productorDemo(id = "p-01", dni = "11111111", nombres = "Juan"))
        servicio.registrar(productorDemo(id = "p-02", dni = "22222222", nombres = "María"))

        servicio.actualizar(productorDemo(id = "p-01", dni = "11111111", nombres = "Juan Modificado"))

        assertEquals("María", servicio.obtenerPorId("p-02")?.nombres)
    }

    // ================================================================
    // ELIMINAR
    // ================================================================

    @Test
    fun eliminarProductorExistente() {
        servicio.registrar(productorDemo())

        val eliminado = servicio.eliminar("p-01")

        assertTrue(eliminado)
        assertEquals(0, servicio.total)
        assertNull(servicio.obtenerPorId("p-01"))
    }

    @Test
    fun eliminarProductorInexistenteDevuelveFalse() {
        val resultado = servicio.eliminar("id-que-no-existe")

        assertFalse(resultado)
    }

    @Test
    fun eliminarNoAfectaOtrosProductores() {
        servicio.registrar(productorDemo(id = "p-01", dni = "11111111"))
        servicio.registrar(productorDemo(id = "p-02", dni = "22222222"))

        servicio.eliminar("p-01")

        assertEquals(1, servicio.total)
        assertNotNull(servicio.obtenerPorId("p-02"))
    }
}
