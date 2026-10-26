package com.example.acopiodeleche.data.conexion

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Red del sistema con ConnectivityManager (requiere ACCESS_NETWORK_STATE). Se registra una vez
 * y vive mientras viva la app.
 */
class MonitorConexionAndroid(context: Context) : MonitorConexion {

    private val gestor = context.applicationContext.getSystemService(ConnectivityManager::class.java)
    private val estado = MutableStateFlow(tieneInternet(gestor.activeNetwork))
    override val hayRed: StateFlow<Boolean> = estado.asStateFlow()

    init {
        gestor.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
            override fun onCapabilitiesChanged(network: Network, capacidades: NetworkCapabilities) {
                estado.value = capacidades.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            }

            override fun onLost(network: Network) {
                estado.value = false
            }

            override fun onUnavailable() {
                estado.value = false
            }
        })
    }

    private fun tieneInternet(red: Network?): Boolean =
        red != null && gestor.getNetworkCapabilities(red)?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
}
