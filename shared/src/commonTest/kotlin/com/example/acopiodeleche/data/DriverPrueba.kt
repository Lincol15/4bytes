package com.example.acopiodeleche.data

import app.cash.sqldelight.db.SqlDriver

/**
 * Base SQLite en memoria para las pruebas comunes: JDBC en JVM y Android (host),
 * driver nativo en iOS. Con [conEsquema] = false queda vacía (para probar migraciones).
 */
expect fun crearDriverEnMemoria(conEsquema: Boolean = true): SqlDriver
