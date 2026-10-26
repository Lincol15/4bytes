package com.example.acopiodeleche.data

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.example.acopiodeleche.db.AcopioDatabase

actual fun crearDriverEnMemoria(conEsquema: Boolean): SqlDriver =
    JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).also { if (conEsquema) AcopioDatabase.Schema.create(it) }
