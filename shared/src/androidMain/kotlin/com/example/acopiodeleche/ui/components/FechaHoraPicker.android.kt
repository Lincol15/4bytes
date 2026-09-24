package com.example.acopiodeleche.ui.components

import kotlinx.datetime.*

actual fun obtenerFechaActual(): FechaData {
    val ahora = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    return FechaData(dia = ahora.dayOfMonth, mes = ahora.monthNumber, anio = ahora.year)
}

actual fun obtenerHoraActual(): HoraData {
    val ahora = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    return HoraData(hora = ahora.hour, minuto = ahora.minute)
}
