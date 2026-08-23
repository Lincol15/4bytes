package com.example.acopiodeleche

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import com.example.acopiodeleche.domain.model.Categoria
import com.example.acopiodeleche.domain.model.RegistroLeche

class RegistroLecheTest {

    /**
     * Función auxiliar que crea un RegistroLeche de prueba.
     * Los parámetros con valor por defecto permiten variar solo lo que interesa en cada test.
     */
    private fun registroDemo(
        litros: Double = 120.50,
        activo: Boolean = true,
        observacion: String? = null
    ) = RegistroLeche(
        id = "r-01",
        proveedor = "Granja San Juan",
        litros = litros,
        categoria = Categoria.RECOLECCION,
        observacion = observacion,
        activo = activo
    )

    @Test
    fun sinObservacionUsaElTextoPorDefecto() {
        val registro = registroDemo()
        assertEquals("Sin observación", registro.observacionCorta)
    }

    @Test
    fun conObservacionDevuelveLaObservacionReal() {
        val registro = registroDemo(observacion = "Leche de alta calidad")
        assertEquals("Leche de alta calidad", registro.observacionCorta)
    }

    @Test
    fun unRegistroInactivoNoEsValido() {
        assertFalse(registroDemo(activo = false).esValido)
        assertTrue(registroDemo(activo = true).esValido)
    }

    @Test
    fun copyGeneraUnObjetoNuevoSinTocarElOriginal() {
        val original = registroDemo()
        val actualizado = original.copy(litros = 200.00)
        assertEquals(120.50, original.litros)
        assertEquals(200.00, actualizado.litros)
        assertEquals(original.proveedor, actualizado.proveedor)
    }
}
