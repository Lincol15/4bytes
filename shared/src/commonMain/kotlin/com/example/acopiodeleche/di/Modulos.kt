package com.example.acopiodeleche.di

import app.cash.sqldelight.db.SqlDriver
import com.example.acopiodeleche.data.conexion.MonitorConexion
import com.example.acopiodeleche.data.local.AlmacenSesionSql
import com.example.acopiodeleche.data.local.CacheRespuestas
import com.example.acopiodeleche.data.local.CacheRespuestasSql
import com.example.acopiodeleche.data.remote.AdminApi
import com.example.acopiodeleche.data.remote.ApiClient
import com.example.acopiodeleche.data.remote.AuthApi
import com.example.acopiodeleche.data.remote.CalidadApi
import com.example.acopiodeleche.data.remote.EstadoConexion
import com.example.acopiodeleche.data.remote.NotificacionApi
import com.example.acopiodeleche.data.remote.PagoApi
import com.example.acopiodeleche.data.remote.PlantaApi
import com.example.acopiodeleche.data.remote.ProductorApi
import com.example.acopiodeleche.data.remote.QuejaApi
import com.example.acopiodeleche.data.remote.RecoleccionApi
import com.example.acopiodeleche.data.remote.RutaApi
import com.example.acopiodeleche.data.remote.SancionApi
import com.example.acopiodeleche.data.repository.AdminRepository
import com.example.acopiodeleche.data.repository.AuthRepository
import com.example.acopiodeleche.data.repository.CalidadRepository
import com.example.acopiodeleche.data.repository.NotificacionRepository
import com.example.acopiodeleche.data.repository.PagoRepository
import com.example.acopiodeleche.data.repository.PlantaRepository
import com.example.acopiodeleche.data.repository.ProductorRepository
import com.example.acopiodeleche.data.repository.QuejaRepository
import com.example.acopiodeleche.data.repository.RecoleccionRepository
import com.example.acopiodeleche.data.repository.RutaRepository
import com.example.acopiodeleche.data.repository.SancionRepository
import com.example.acopiodeleche.data.sync.Precarga
import com.example.acopiodeleche.data.sync.Sincronizador
import com.example.acopiodeleche.db.AcopioDatabase
import com.example.acopiodeleche.domain.model.Rol
import com.example.acopiodeleche.domain.model.SesionActual
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
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.scope.Scope
import org.koin.dsl.module
import org.koin.mp.KoinPlatform
import org.koin.core.context.startKoin
import kotlin.time.Clock

internal fun ahoraMs(): Long = Clock.System.now().toEpochMilliseconds()

/**
 * Capa de datos: base local, cliente HTTP, repositorios y sincronizador (un solo ejemplar de cada uno).
 * Necesita del módulo de la plataforma: [SqlDriver], AlmacenSeguro y [MonitorConexion].
 */
val moduloDatos = module {
    single { AcopioDatabase(get<SqlDriver>()) }
    single { AlmacenSesionSql(get(), get()) }
    single<CacheRespuestas> { CacheRespuestasSql(get(), ::ahoraMs) }
    single { EstadoConexion(get<MonitorConexion>().hayRed) }
    single { ApiClient(cache = get(), conexion = get()) }

    single { AuthRepository(AuthApi(get())) }
    single { ProductorRepository(ProductorApi(get())) }
    single { RecoleccionRepository(RecoleccionApi(get()), get()) }
    single { RutaRepository(RutaApi(get())) }
    single { CalidadRepository(CalidadApi(get())) }
    single { PagoRepository(PagoApi(get())) }
    single { NotificacionRepository(NotificacionApi(get())) }
    single { QuejaRepository(QuejaApi(get())) }
    single { AdminRepository(AdminApi(get())) }
    single { SancionRepository(SancionApi(get())) }
    single { PlantaRepository(PlantaApi(get())) }

    single {
        val alcance = this
        Sincronizador(get(), get()) { rol -> precargas(rol, alcance) }
    }
}

/** ViewModels de cada pantalla; Koin les inyecta los repositorios. */
val moduloViewModels = module {
    viewModelOf(::LoginViewModel)
    viewModelOf(::RecoleccionViewModel)
    viewModelOf(::RutasViewModel)
    viewModelOf(::AdminViewModel)
    viewModelOf(::CalidadAdminViewModel)
    viewModelOf(::CalidadViewModel)
    viewModelOf(::GerenteViewModel)
    viewModelOf(::PagosAdminViewModel)
    viewModelOf(::PlantaViewModel)
    viewModelOf(::RecepcionPlantaViewModel)
    viewModelOf(::ProductorViewModel)
    viewModelOf(::QuejasViewModel)
    viewModelOf(::ProductoresViewModel)
    // Detalle de un acopiador: recibe el acopiador elegido como parámetro
    viewModel { (acopiador: Usuario) -> AcopiadorAdminViewModel(acopiador, get(), get()) }
}

/**
 * Arranca Koin con el módulo de la plataforma y restaura la sesión guardada.
 * Cada plataforma lo llama una vez al iniciar (Application en Android, MainViewController en iOS).
 */
fun iniciarKoin(moduloPlataforma: Module) {
    if (KoinPlatform.getKoinOrNull() != null) return
    val koin = startKoin { modules(moduloPlataforma, moduloDatos, moduloViewModels) }.koin
    SesionActual.almacen = koin.get<AlmacenSesionSql>()
    SesionActual.restaurar(ahoraMs())
}

/** Las mismas consultas (y parámetros) que hacen las pantallas de cada rol al abrirse. */
private fun precargas(rol: Rol, alcance: Scope): List<Precarga> = with(alcance) {
    when (rol) {
        Rol.ACOPIADOR -> listOf(
            { get<ProductorRepository>().listar() },
            { get<RecoleccionRepository>().listar() },
            { get<RutaRepository>().listar() }
        )
        Rol.PRODUCTOR -> listOf(
            { get<RecoleccionRepository>().listar() },
            { get<PagoRepository>().listar() },
            { get<CalidadRepository>().listar() },
            { get<NotificacionRepository>().listar() },
            { get<QuejaRepository>().listar() }
        )
        Rol.CONTROL_CALIDAD -> listOf(
            { get<CalidadRepository>().listar() },
            { get<ProductorRepository>().listar() }
        )
        Rol.TRABAJADOR_PLANTA -> listOf(
            { get<RecoleccionRepository>().listar(recibido = false) },
            { get<PlantaRepository>().productos() },
            { get<PlantaRepository>().insumos() },
            { get<PlantaRepository>().recetas() },
            { get<PlantaRepository>().lotes() },
            { get<PlantaRepository>().ventas() }
        )
        Rol.GERENTE -> listOf(
            { get<AdminRepository>().resumen() },
            { get<CalidadRepository>().listar() },
            { get<PagoRepository>().listar() }
        )
        Rol.ADMINISTRADOR -> listOf(
            { get<AdminRepository>().resumen() },
            { get<AdminRepository>().usuarios() },
            { get<AdminRepository>().avisosPublicados() },
            { get<AdminRepository>().configuracion() },
            { get<ProductorRepository>().listar() },
            { get<CalidadRepository>().listar() },
            { get<SancionRepository>().listar() }
        )
    }
}
