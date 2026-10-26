package com.example.acopiodeleche.data

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.example.acopiodeleche.db.AcopioDatabase

/** Las pruebas de Android corren en la JVM del equipo (host): se usa el driver JDBC. */
actual fun crearDriverEnMemoria(conEsquema: Boolean): SqlDriver =
    JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).also { if (conEsquema) AcopioDatabase.Schema.create(it) }
