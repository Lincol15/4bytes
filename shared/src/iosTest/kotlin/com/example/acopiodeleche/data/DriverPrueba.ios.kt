package com.example.acopiodeleche.data

import app.cash.sqldelight.db.AfterVersion
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.driver.native.inMemoryDriver
import com.example.acopiodeleche.db.AcopioDatabase

/** Esquema vacío (versión 1) para crear una base sin tablas y probar migraciones. */
private object EsquemaVacio : SqlSchema<QueryResult.Value<Unit>> {
    override val version: Long = 1
    override fun create(driver: SqlDriver) = QueryResult.Unit
    override fun migrate(driver: SqlDriver, oldVersion: Long, newVersion: Long, vararg callbacks: AfterVersion) =
        QueryResult.Unit
}

actual fun crearDriverEnMemoria(conEsquema: Boolean): SqlDriver =
    if (conEsquema) inMemoryDriver(AcopioDatabase.Schema) else inMemoryDriver(EsquemaVacio)
