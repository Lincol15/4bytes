package com.example.acopiodeleche.data

import com.example.acopiodeleche.data.remote.AdminApi
import com.example.acopiodeleche.data.remote.ApiClient
import com.example.acopiodeleche.data.repository.AdminRepository
import com.example.acopiodeleche.data.remote.AuthApi
import com.example.acopiodeleche.data.remote.CalidadApi
import com.example.acopiodeleche.data.remote.NotificacionApi
import com.example.acopiodeleche.data.remote.PagoApi
import com.example.acopiodeleche.data.remote.QuejaApi
import com.example.acopiodeleche.data.remote.SancionApi
import com.example.acopiodeleche.data.remote.PlantaApi
import com.example.acopiodeleche.data.repository.PlantaRepository
import com.example.acopiodeleche.data.repository.SancionRepository
import com.example.acopiodeleche.data.repository.CalidadRepository
import com.example.acopiodeleche.data.repository.NotificacionRepository
import com.example.acopiodeleche.data.repository.PagoRepository
import com.example.acopiodeleche.data.repository.QuejaRepository
import com.example.acopiodeleche.data.remote.ProductorApi
import com.example.acopiodeleche.data.remote.RecoleccionApi
import com.example.acopiodeleche.data.remote.RutaApi
import com.example.acopiodeleche.data.repository.AuthRepository
import com.example.acopiodeleche.data.repository.ProductorRepository
import com.example.acopiodeleche.data.repository.RecoleccionRepository
import com.example.acopiodeleche.data.repository.RutaRepository

/**
 * Contenedor de dependencias de la capa de datos: un único ApiClient compartido
 * por todos los repositorios.
 */
object AppContainer {
    private val apiClient by lazy { ApiClient() }

    val authRepository by lazy { AuthRepository(AuthApi(apiClient)) }
    val productorRepository by lazy { ProductorRepository(ProductorApi(apiClient)) }
    val recoleccionRepository by lazy { RecoleccionRepository(RecoleccionApi(apiClient)) }
    val rutaRepository by lazy { RutaRepository(RutaApi(apiClient)) }
    val calidadRepository by lazy { CalidadRepository(CalidadApi(apiClient)) }
    val pagoRepository by lazy { PagoRepository(PagoApi(apiClient)) }
    val notificacionRepository by lazy { NotificacionRepository(NotificacionApi(apiClient)) }
    val quejaRepository by lazy { QuejaRepository(QuejaApi(apiClient)) }
    val adminRepository by lazy { AdminRepository(AdminApi(apiClient)) }
    val sancionRepository by lazy { SancionRepository(SancionApi(apiClient)) }
    val plantaRepository by lazy { PlantaRepository(PlantaApi(apiClient)) }
}
