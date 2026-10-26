package com.example.acopiodeleche.domain.usecase

import com.example.acopiodeleche.domain.ApiResult
import com.example.acopiodeleche.domain.model.EstadoSync
import com.example.acopiodeleche.domain.model.Productor
import com.example.acopiodeleche.domain.model.RegistroAcopio
import com.example.acopiodeleche.domain.repository.ProductorRepositorio
import com.example.acopiodeleche.domain.repository.RecoleccionRepositorio
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Reglas de negocio de los casos de uso, con repositorios falsos (sin red ni base de datos). */
class CasosDeUsoTest {

    private class ProductoresFalsos(var lista: List<Productor> = emptyList()) : ProductorRepositorio {
        val llamadas = mutableListOf<String>()
        var activoPedido: Boolean? = null

        override suspend fun listar() = ApiResult.Exito(lista).also { llamadas += "listar" }
        override suspend fun registrar(productor: Productor, correo: String, contrasena: String) =
            ApiResult.Exito(productor.copy(idProductor = "10")).also { llamadas += "registrar:$correo" }
        override suspend fun actualizar(productor: Productor) = ApiResult.Exito(productor).also { llamadas += "actualizar" }
        override suspend fun eliminar(productor: Productor) = ApiResult.Exito(Unit).also { llamadas += "eliminar" }
        override suspend fun cambiarEstado(productor: Productor, activo: Boolean): ApiResult<Productor> {
            activoPedido = activo
            return ApiResult.Exito(productor.copy(estado = activo))
        }
    }

    private class RecoleccionesFalsas : RecoleccionRepositorio {
        val registradas = mutableListOf<Pair<Double, String?>>()
        val reintentadas = mutableListOf<RegistroAcopio>()
        val descartadas = mutableListOf<RegistroAcopio>()

        override suspend fun listar(recibido: Boolean?, desde: String?) = ApiResult.Exito(emptyList<RegistroAcopio>())
        override fun pendientes(): Flow<List<RegistroAcopio>> = flowOf(emptyList())
        override suspend fun registrar(
            idProductor: String,
            litros: Double,
            observacion: String?,
            idParada: String?,
            nombreProductor: String?,
            zona: String?
        ): ApiResult<RegistroAcopio> {
            registradas += litros to observacion
            return ApiResult.Exito(registro("1", litros))
        }
        override suspend fun enviarPendientes() = 0
        override fun reintentar(registro: RegistroAcopio) { reintentadas += registro }
        override fun descartar(registro: RegistroAcopio) { descartadas += registro }
        override suspend fun registrarRecepcion(registro: RegistroAcopio) = ApiResult.Exito(registro)
    }

    private val valido = Productor("5", "Juana", "Mamani", "12345678", "912345678", "Jr. Puno", "Huata", estado = true)

    @Test
    fun registrar_productor_con_dni_invalido_no_llega_al_repositorio() = runTest {
        val repo = ProductoresFalsos()

        val resultado = RegistrarProductor(repo)(valido.copy(dni = "1234"), "juana@x.com", "secreto1")

        assertEquals("El DNI debe tener 8 dígitos.", assertIs<ApiResult.ErrorValidacion>(resultado).mensaje)
        assertTrue(repo.llamadas.isEmpty())
    }

    @Test
    fun registrar_productor_exige_correo_valido_y_contrasena_de_6() = runTest {
        val repo = ProductoresFalsos()

        assertIs<ApiResult.ErrorValidacion>(RegistrarProductor(repo)(valido, "juana-sin-arroba", "secreto1"))
        assertIs<ApiResult.ErrorValidacion>(RegistrarProductor(repo)(valido, "juana@x.com", "123"))
        assertIs<ApiResult.Exito<Productor>>(RegistrarProductor(repo)(valido, "  juana@x.com ", "secreto1"))
        assertEquals(listOf("registrar:juana@x.com"), repo.llamadas)
    }

    @Test
    fun actualizar_productor_exige_telefono_de_9_digitos() = runTest {
        val repo = ProductoresFalsos()

        val resultado = ActualizarProductor(repo)(valido.copy(telefono = "98700010"))

        assertEquals("El teléfono debe tener 9 dígitos.", assertIs<ApiResult.ErrorValidacion>(resultado).mensaje)
        assertTrue(repo.llamadas.isEmpty())
    }

    @Test
    fun actualizar_productor_sin_id_no_es_posible() = runTest {
        assertIs<ApiResult.ErrorValidacion>(ActualizarProductor(ProductoresFalsos())(valido.copy(idProductor = "")))
    }

    @Test
    fun obtener_productores_los_ordena_por_nombre() = runTest {
        val repo = ProductoresFalsos(listOf(valido.copy(nombres = "Zoila"), valido.copy(nombres = "ana")))

        val nombres = assertIs<ApiResult.Exito<List<Productor>>>(ObtenerProductores(repo)()).datos.map { it.nombres }

        assertEquals(listOf("ana", "Zoila"), nombres)
    }

    @Test
    fun cambiar_estado_invierte_el_estado_actual() = runTest {
        val repo = ProductoresFalsos()

        CambiarEstadoProductor(repo)(valido.copy(estado = true))
        assertEquals(false, repo.activoPedido)
        CambiarEstadoProductor(repo)(valido.copy(estado = false))
        assertEquals(true, repo.activoPedido)
    }

    @Test
    fun registrar_recoleccion_valida_los_litros_y_el_productor() = runTest {
        val repo = RecoleccionesFalsas()
        val registrar = RegistrarRecoleccion(repo)

        assertIs<ApiResult.ErrorValidacion>(registrar("3", 0.0, null))
        assertIs<ApiResult.ErrorValidacion>(registrar("3", -5.0, null))
        assertIs<ApiResult.ErrorValidacion>(registrar("3", 10_001.0, null))
        assertIs<ApiResult.ErrorValidacion>(registrar("", 20.0, null))
        assertTrue(repo.registradas.isEmpty())

        assertIs<ApiResult.Exito<RegistroAcopio>>(registrar("3", 40.5, "   "))
        assertEquals(listOf<Pair<Double, String?>>(40.5 to null), repo.registradas)
    }

    @Test
    fun combinar_no_repite_pendientes_que_el_servidor_ya_tiene() {
        val servidor = listOf(registro("10", 20.0, uuid = "a"), registro("11", 30.0))
        val locales = listOf(
            registro("a", 20.0, uuid = "a", sync = EstadoSync.PENDIENTE),
            registro("b", 15.0, uuid = "b", sync = EstadoSync.PENDIENTE)
        )

        val combinadas = CombinarRecolecciones()(servidor, locales)

        assertEquals(listOf("b", "10", "11"), combinadas.map { it.id })
    }

    @Test
    fun solo_se_reintentan_las_rechazadas_y_solo_se_descartan_las_locales() {
        val repo = RecoleccionesFalsas()
        val rechazada = registro("x", 10.0, uuid = "x", sync = EstadoSync.ERROR)
        val pendiente = registro("y", 10.0, uuid = "y", sync = EstadoSync.PENDIENTE)
        val sincronizada = registro("7", 10.0)

        ReintentarRecoleccion(repo)(rechazada)
        ReintentarRecoleccion(repo)(pendiente)
        DescartarRecoleccion(repo)(pendiente)
        DescartarRecoleccion(repo)(sincronizada)

        assertEquals(listOf(rechazada), repo.reintentadas)
        assertEquals(listOf(pendiente), repo.descartadas)
        assertNull(repo.descartadas.firstOrNull { it.id == "7" })
    }
}

private fun registro(id: String, litros: Double, uuid: String? = null, sync: EstadoSync = EstadoSync.SINCRONIZADO) =
    RegistroAcopio(
        id = id, idProductor = "3", acopiador = "Carlos", vehiculo = "ABC-123", zona = "Huata",
        fecha = "26/10/2026", hora = "08:00", litros = litros, estadoSync = sync, clientUuid = uuid
    )
