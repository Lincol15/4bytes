package com.example.acopiodeleche.di

import app.cash.sqldelight.db.SqlDriver
import com.example.acopiodeleche.data.conexion.MonitorConexion
import com.example.acopiodeleche.data.conexion.MonitorConexionManual
import com.example.acopiodeleche.data.local.AlmacenSeguro
import com.example.acopiodeleche.data.local.AlmacenSeguroEnMemoria
import com.example.acopiodeleche.data.remote.ApiClient
import com.example.acopiodeleche.data.repository.RecoleccionRepository
import com.example.acopiodeleche.data.sync.Sincronizador
import com.example.acopiodeleche.data.crearDriverEnMemoria
import com.example.acopiodeleche.domain.model.Rol
import com.example.acopiodeleche.domain.model.Usuario
import com.example.acopiodeleche.ui.auth.LoginViewModel
import com.example.acopiodeleche.ui.pantallas.acopiador.RutasViewModel
import com.example.acopiodeleche.ui.pantallas.acopio.RecoleccionViewModel
import com.example.acopiodeleche.ui.pantallas.admin.AcopiadorAdminViewModel
import com.example.acopiodeleche.ui.pantallas.admin.AdminViewModel
import com.example.acopiodeleche.ui.pantallas.admin.CalidadAdminViewModel
import com.example.acopiodeleche.ui.pantallas.calidad.CalidadViewModel
import com.example.acopiodeleche.ui.pantallas.gerente.GerenteViewModel
import com.example.acopiodeleche.ui.pantallas.pagos.PagosAdminViewModel
import com.example.acopiodeleche.ui.pantallas.planta.PlantaViewModel
import com.example.acopiodeleche.ui.pantallas.planta.RecepcionPlantaViewModel
import com.example.acopiodeleche.ui.pantallas.productor.ProductorViewModel
import com.example.acopiodeleche.ui.pantallas.productor.QuejasViewModel
import com.example.acopiodeleche.ui.pantallas.productores.ProductoresViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertSame

/** Los módulos de Koin resuelven todo el grafo: repositorios, sincronizador y cada ViewModel. */
@OptIn(ExperimentalCoroutinesApi::class)
class ModulosKoinTest {

    private val moduloPrueba = module {
        single<SqlDriver> { crearDriverEnMemoria() }
        single<AlmacenSeguro> { AlmacenSeguroEnMemoria() }
        single<MonitorConexion> { MonitorConexionManual() }
    }

    @BeforeTest
    fun iniciar() {
        // Los ViewModels lanzan su carga en Dispatchers.Main; aquí no se ejecuta (no se avanza el reloj)
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @AfterTest
    fun detener() {
        stopKoin()
        Dispatchers.resetMain()
    }

    @Test
    fun todas_las_dependencias_se_resuelven() {
        val koin = startKoin { modules(moduloPrueba, moduloDatos, moduloCasosDeUso, moduloViewModels) }.koin

        assertSame(koin.get<ApiClient>(), koin.get<ApiClient>())
        assertNotNull(koin.get<RecoleccionRepository>())
        assertNotNull(koin.get<Sincronizador>())

        assertNotNull(koin.get<LoginViewModel>())
        assertNotNull(koin.get<RecoleccionViewModel>())
        assertNotNull(koin.get<RutasViewModel>())
        assertNotNull(koin.get<AdminViewModel>())
        assertNotNull(koin.get<CalidadAdminViewModel>())
        assertNotNull(koin.get<CalidadViewModel>())
        assertNotNull(koin.get<GerenteViewModel>())
        assertNotNull(koin.get<PagosAdminViewModel>())
        assertNotNull(koin.get<PlantaViewModel>())
        assertNotNull(koin.get<RecepcionPlantaViewModel>())
        assertNotNull(koin.get<ProductorViewModel>())
        assertNotNull(koin.get<QuejasViewModel>())
        assertNotNull(koin.get<ProductoresViewModel>())

        val acopiador = Usuario("3", "Carlos", "Mamani", "", "", "c@x.com", "", Rol.ACOPIADOR)
        assertNotNull(koin.get<AcopiadorAdminViewModel> { parametersOf(acopiador) })
    }
}
