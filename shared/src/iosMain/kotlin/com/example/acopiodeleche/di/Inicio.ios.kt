package com.example.acopiodeleche.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.example.acopiodeleche.data.conexion.MonitorConexion
import com.example.acopiodeleche.data.conexion.MonitorConexionIos
import com.example.acopiodeleche.data.local.AlmacenSeguro
import com.example.acopiodeleche.data.local.AlmacenSeguroIos
import com.example.acopiodeleche.db.AcopioDatabase
import org.koin.dsl.module

/** Arranca Koin en iOS: SQLite (acopio.db), tokens en el Keychain y red con NWPathMonitor. */
fun iniciarApp() {
    iniciarKoin(
        module {
            single<SqlDriver> { NativeSqliteDriver(AcopioDatabase.Schema, "acopio.db") }
            single<AlmacenSeguro> { AlmacenSeguroIos() }
            single<MonitorConexion> { MonitorConexionIos() }
        }
    )
}
