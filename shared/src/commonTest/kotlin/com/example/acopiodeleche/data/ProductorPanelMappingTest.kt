package com.example.acopiodeleche.data

import com.example.acopiodeleche.data.remote.dto.AnalisisCalidadDto
import com.example.acopiodeleche.data.remote.dto.PagoDto
import com.example.acopiodeleche.data.remote.dto.QuejaDto
import com.example.acopiodeleche.data.remote.dto.RegistrarAnalisisRequestDto
import com.example.acopiodeleche.data.remote.dto.toDomain
import com.example.acopiodeleche.domain.model.EstadoPago
import com.example.acopiodeleche.domain.model.EstadoQueja
import com.example.acopiodeleche.domain.model.ResultadoCalidad
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class ProductorPanelMappingTest {

    private val json = Json { ignoreUnknownKeys = true; explicitNulls = false }

    @Test
    fun analisis_rechazado_se_muestra_como_no_apto_con_fecha_y_hora_locales() {
        val dto = json.decodeFromString<AnalisisCalidadDto>(
            """{"id":4,"sample_code":"MUE-1","producer_id":8,"producer_name":"Natividad Rojas","grasa_pct":2.8,"result":"rechazado","analyzed_at":"2026-10-12T09:03:53-05:00"}"""
        )

        val analisis = dto.toDomain()

        assertEquals(ResultadoCalidad.NO_APTO, analisis.resultado)
        assertEquals("12/10/2026", analisis.fecha)
        assertEquals("09:03", analisis.hora)
        assertEquals("Natividad Rojas", analisis.nombreProductor)
    }

    @Test
    fun registro_de_analisis_no_envia_resultado_para_que_lo_decida_el_backend() {
        val cuerpo = json.encodeToString(
            RegistrarAnalisisRequestDto.serializer(),
            RegistrarAnalisisRequestDto(idProductor = 8, origenDatos = "manual", grasa = 3.5)
        )

        assertEquals("""{"producer_id":8,"origen_datos":"manual","grasa_pct":3.5}""", cuerpo)
        assertFalse(cuerpo.contains("result"))
    }

    @Test
    fun pago_usa_el_total_liquidado_por_el_backend_y_tolera_detalle_vacio_como_lista() {
        val dto = json.decodeFromString<PagoDto>(
            """{"id":1,"producer_id":8,"period_code":"SEM-2026-40","period_start":"2026-09-28","period_end":"2026-10-04","total_liters":100,"price_per_liter":1.7,"detalle_diario":[],"total_amount":185.5,"status":"pagado"}"""
        )

        val pago = dto.toDomain()

        assertEquals(185.5, pago.totalCalculado)
        assertEquals(EstadoPago.PAGADO, pago.estado)
        assertEquals("28/09/2026", pago.periodoDesde)
        assertEquals(emptyMap(), pago.detalleDiario)
    }

    @Test
    fun queja_respondida_se_mapea_a_estado_respondida() {
        val dto = json.decodeFromString<QuejaDto>(
            """{"id":2,"ticket_number":"TK-2026-00002","producer_id":8,"subject":"Pago","description":"Falta","status":"respondido","staff_response":"Corregido","created_at":"2026-10-12T08:00:00-05:00"}"""
        )

        val queja = dto.toDomain()

        assertEquals(EstadoQueja.RESPONDIDA, queja.estado)
        assertEquals("Corregido", queja.respuesta)
        assertEquals("08:00", queja.hora)
    }
}
