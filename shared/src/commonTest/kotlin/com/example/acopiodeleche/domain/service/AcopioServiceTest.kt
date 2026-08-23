package com.example.acopiodeleche.domain.service

import com.example.acopiodeleche.domain.model.Categoria
import com.example.acopiodeleche.domain.model.RegistroLeche
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.assertFailsWith

class AcopioServiceTest {

    // Se recrea antes de CADA test gracias a @BeforeTest — garantiza aislamiento
    private lateinit var servicio: AcopioService

    @BeforeTest
    fun setUp() {
        servicio = AcopioService()
    }

    // ----------------------------------------------------------------
    // Datos de prueba
    // ----------------------------------------------------------------

    private fun entregaDemo(
        id: String = "e-01",
        proveedor: String = "Granja San Juan",
        litros: Double = 120.50,
        categoria: Categoria = Categoria.RECOLECCION,
        observacion: String? = null,
        activo: Boolean = true
    ) = RegistroLeche(
        id = id,
        proveedor = proveedor,
        litros = litros,
        categoria = categoria,
        observacion = observacion,
        activo = activo
    )

    // ================================================================
    // VALIDACIÓN DE LITROS
    // ================================================================

    @Test
    fun validarLitrosPositivosEsCorrecto() {
        // No debe lanzar excepción
        servicio.validarLitros(100.0)
        servicio.validarLitros(0.001)
        servicio.validarLitros(AcopioService.LITROS_MAXIMOS)
    }

    @Test
    fun validarLitrosCeroLanzaExcepcion() {
        assertFailsWith<IllegalArgumentException> {
            servicio.validarLitros(0.0)
        }
    }

    @Test
    fun validarLitrosNegativosLanzaExcepcion() {
        assertFailsWith<IllegalArgumentException> {
            servicio.validarLitros(-10.0)
        }
    }

    @Test
    fun validarLitrosMayoresAlMaximoLanzaExcepcion() {
        assertFailsWith<IllegalArgumentException> {
            servicio.validarLitros(AcopioService.LITROS_MAXIMOS + 0.01)
        }
    }

    // ================================================================
    // REGISTRAR
    // ================================================================

    @Test
    fun registrarEntregaCorrectamente() {
        val entrega = entregaDemo()
        val resultado = servicio.registrar(entrega)

        assertEquals(entrega, resultado)
        assertEquals(1, servicio.total)
    }

    @Test
    fun registrarVariasEntregas() {
        servicio.registrar(entregaDemo(id = "e-01"))
        servicio.registrar(entregaDemo(id = "e-02"))
        servicio.registrar(entregaDemo(id = "e-03"))

        assertEquals(3, servicio.total)
    }

    @Test
    fun registrarEntregaConIdDuplicadoLanzaExcepcion() {
        servicio.registrar(entregaDemo(id = "e-01"))

        assertFailsWith<IllegalArgumentException> {
            servicio.registrar(entregaDemo(id = "e-01"))
        }
    }

    @Test
    fun registrarEntregaConLitrosCeroLanzaExcepcion() {
        assertFailsWith<IllegalArgumentException> {
            servicio.registrar(entregaDemo(litros = 0.0))
        }
    }

    @Test
    fun registrarEntregaConLitrosNegativosLanzaExcepcion() {
        assertFailsWith<IllegalArgumentException> {
            servicio.registrar(entregaDemo(litros = -5.0))
        }
    }

    @Test
    fun registrarEntregaActualizaTotalLitros() {
        servicio.registrar(entregaDemo(id = "e-01", litros = 100.0))
        servicio.registrar(entregaDemo(id = "e-02", litros = 50.0))

        assertEquals(150.0, servicio.totalLitros)
    }

    // ================================================================
    // OBTENER POR ID
    // ================================================================

    @Test
    fun obtenerEntregaPorIdExistente() {
        val entrega = entregaDemo()
        servicio.registrar(entrega)

        val encontrada = servicio.obtenerPorId("e-01")

        assertNotNull(encontrada)
        assertEquals("e-01", encontrada.id)
        assertEquals("Granja San Juan", encontrada.proveedor)
    }

    @Test
    fun obtenerEntregaInexistenteDevuelveNull() {
        val resultado = servicio.obtenerPorId("id-que-no-existe")

        assertNull(resultado)
    }

    // ================================================================
    // LISTAR
    // ================================================================

    @Test
    fun listarDevuelveTodas() {
        servicio.registrar(entregaDemo(id = "e-01"))
        servicio.registrar(entregaDemo(id = "e-02"))

        val lista = servicio.listar()

        assertEquals(2, lista.size)
    }

    @Test
    fun listarConRepositorioVacioDevuelveListaVacia() {
        val lista = servicio.listar()

        assertTrue(lista.isEmpty())
    }

    @Test
    fun listarValidasDevuelveSoloEntregasActivas() {
        servicio.registrar(entregaDemo(id = "e-01", litros = 100.0, activo = true))
        servicio.registrar(entregaDemo(id = "e-02", litros = 50.0,  activo = false))
        servicio.registrar(entregaDemo(id = "e-03", litros = 80.0,  activo = true))

        val validas = servicio.listarValidas()

        assertEquals(2, validas.size)
        assertTrue(validas.all { it.esValido })
    }

    // ================================================================
    // BUSCAR POR PROVEEDOR
    // ================================================================

    @Test
    fun buscarPorProveedorDevuelveCoincidencias() {
        servicio.registrar(entregaDemo(id = "e-01", proveedor = "Granja San Juan"))
        servicio.registrar(entregaDemo(id = "e-02", proveedor = "Granja Los Pinos"))
        servicio.registrar(entregaDemo(id = "e-03", proveedor = "Hacienda Norte"))

        val resultado = servicio.buscarPorProveedor("granja")

        assertEquals(2, resultado.size)
    }

    @Test
    fun buscarPorProveedorExactoDevuelveUno() {
        servicio.registrar(entregaDemo(id = "e-01", proveedor = "Granja San Juan"))
        servicio.registrar(entregaDemo(id = "e-02", proveedor = "Hacienda Norte"))

        val resultado = servicio.buscarPorProveedor("Hacienda Norte")

        assertEquals(1, resultado.size)
        assertEquals("Hacienda Norte", resultado.first().proveedor)
    }

    @Test
    fun buscarPorProveedorInexistenteDevuelveListaVacia() {
        servicio.registrar(entregaDemo())

        val resultado = servicio.buscarPorProveedor("PROVEEDOR_QUE_NO_EXISTE")

        assertTrue(resultado.isEmpty())
    }

    @Test
    fun buscarPorCategoriaDevuelveCoincidencias() {
        servicio.registrar(entregaDemo(id = "e-01", categoria = Categoria.RECOLECCION))
        servicio.registrar(entregaDemo(id = "e-02", categoria = Categoria.PROCESAMIENTO))
        servicio.registrar(entregaDemo(id = "e-03", categoria = Categoria.RECOLECCION))

        val resultado = servicio.buscarPorCategoria(Categoria.RECOLECCION)

        assertEquals(2, resultado.size)
        assertTrue(resultado.all { it.categoria == Categoria.RECOLECCION })
    }

    // ================================================================
    // ACTUALIZAR
    // ================================================================

    @Test
    fun actualizarEntregaCorrectamente() {
        val original = entregaDemo(litros = 100.0)
        servicio.registrar(original)

        val actualizada = original.copy(litros = 150.0)
        val resultado = servicio.actualizar(actualizada)

        assertEquals(150.0, resultado.litros)
        assertEquals(150.0, servicio.obtenerPorId("e-01")?.litros)
    }

    @Test
    fun actualizarEntregaInexistenteLanzaExcepcion() {
        assertFailsWith<NoSuchElementException> {
            servicio.actualizar(entregaDemo(id = "no-existe"))
        }
    }

    @Test
    fun actualizarEntregaConLitrosCeroLanzaExcepcion() {
        servicio.registrar(entregaDemo())

        assertFailsWith<IllegalArgumentException> {
            servicio.actualizar(entregaDemo(litros = 0.0))
        }
    }

    @Test
    fun actualizarNoModificaOtrasEntregas() {
        servicio.registrar(entregaDemo(id = "e-01", litros = 100.0))
        servicio.registrar(entregaDemo(id = "e-02", litros = 200.0))

        servicio.actualizar(entregaDemo(id = "e-01", litros = 999.0))

        assertEquals(200.0, servicio.obtenerPorId("e-02")?.litros)
    }

    // ================================================================
    // ELIMINAR
    // ================================================================

    @Test
    fun eliminarEntregaExistente() {
        servicio.registrar(entregaDemo())

        val eliminada = servicio.eliminar("e-01")

        assertTrue(eliminada)
        assertEquals(0, servicio.total)
        assertNull(servicio.obtenerPorId("e-01"))
    }

    @Test
    fun eliminarEntregaInexistenteDevuelveFalse() {
        val resultado = servicio.eliminar("id-que-no-existe")

        assertFalse(resultado)
    }

    @Test
    fun eliminarNoAfectaOtrasEntregas() {
        servicio.registrar(entregaDemo(id = "e-01"))
        servicio.registrar(entregaDemo(id = "e-02"))

        servicio.eliminar("e-01")

        assertEquals(1, servicio.total)
        assertNotNull(servicio.obtenerPorId("e-02"))
    }

    @Test
    fun eliminarActualizaTotalLitros() {
        servicio.registrar(entregaDemo(id = "e-01", litros = 100.0))
        servicio.registrar(entregaDemo(id = "e-02", litros = 50.0))

        servicio.eliminar("e-01")

        assertEquals(50.0, servicio.totalLitros)
    }
}
