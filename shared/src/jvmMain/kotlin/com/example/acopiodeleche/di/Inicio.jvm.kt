package com.example.acopiodeleche.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.example.acopiodeleche.data.conexion.MonitorConexion
import com.example.acopiodeleche.data.conexion.MonitorConexionManual
import com.example.acopiodeleche.data.local.AlmacenSeguro
import com.example.acopiodeleche.data.local.AlmacenSeguroEnMemoria
import com.example.acopiodeleche.db.AcopioDatabase
import org.koin.dsl.module
import java.util.Properties

/**
 * Arranca Koin en Desktop (desarrollo): SQLite en [rutaArchivo]. Los tokens quedan solo en memoria
 * (no hay almacén seguro del sistema) y la red se considera siempre disponible.
 */
fun iniciarApp(rutaArchivo: String = "acopio.db") {
    iniciarKoin(
        module {
            single<SqlDriver> { JdbcSqliteDriver("jdbc:sqlite:$rutaArchivo", Properties(), AcopioDatabase.Schema) }
            single<AlmacenSeguro> { AlmacenSeguroEnMemoria() }
            single<MonitorConexion> { MonitorConexionManual() }
        }
    )
}
