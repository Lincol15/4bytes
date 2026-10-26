package com.example.acopiodeleche

import android.app.Application
import com.example.acopiodeleche.data.remote.ApiConfig
import com.example.acopiodeleche.di.iniciarApp

/** Arranca Koin una sola vez por proceso (no en cada recreación de la Activity). */
class AcopioApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // URL del backend Laravel según el tipo de build (ver api.baseUrl.* en local.properties)
        ApiConfig.baseUrl = BuildConfig.API_BASE_URL
        iniciarApp(this)
    }
}
