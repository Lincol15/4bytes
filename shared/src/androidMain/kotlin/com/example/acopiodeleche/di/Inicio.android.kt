package com.example.acopiodeleche.di

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.example.acopiodeleche.data.conexion.MonitorConexion
import com.example.acopiodeleche.data.conexion.MonitorConexionAndroid
import com.example.acopiodeleche.data.local.AlmacenSeguro
import com.example.acopiodeleche.data.local.AlmacenSeguroAndroid
import com.example.acopiodeleche.db.AcopioDatabase
import org.koin.dsl.module

/**
 * Arranca Koin en Android: SQLite (acopio.db), tokens en el Keystore y red con ConnectivityManager.
 * Se llama una vez desde Application.onCreate.
 */
fun iniciarApp(context: Context) {
    val app = context.applicationContext
    iniciarKoin(
        module {
            single<SqlDriver> { AndroidSqliteDriver(AcopioDatabase.Schema, app, "acopio.db") }
            single<AlmacenSeguro> { AlmacenSeguroAndroid(app) }
            single<MonitorConexion> { MonitorConexionAndroid(app) }
        }
    )
}
