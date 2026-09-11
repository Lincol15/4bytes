package com.example.acopiodeleche.ui.pantallas.acopiador

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val VerdeHuata = Color(0xFF2E7D32)
private val AzulMapa   = Color(0xFF1565C0)

// ─────────────────────────────────────────────────────────────────────────────
// RUTAS PREDEFINIDAS — Ecolácteos Huata (Puno, Perú)
// Coordenadas reales de la zona lacustre del Titicaca / Huata
// ─────────────────────────────────────────────────────────────────────────────

private val PLANTA_HUATA = PuntoRuta(
    id       = "planta",
    nombre   = "Planta Ecolácteos Huata",
    tipo     = TipoPunto.ORIGEN,
    latitud  = -15.8240,
    longitud = -69.9980
)

/**
 * Rutas de acopio fijas del acopiador Carlos Mamani.
 * Cada ruta cubre una comunidad diferente.
 */
private val RUTAS_PREDEFINIDAS: List<RutaAcopio> = listOf(

    // ── RUTA A: Comunidad A (Norte) ──────────────────────────────────────
    RutaAcopio(
        nombre      = "🌾 Ruta A — Comunidad A",
        descripcion = "3 productores · ~12 km · Norte de Huata",
        puntos      = listOf(
            PLANTA_HUATA,
            PuntoRuta("a1", "Pedro Quispe Mamani", TipoPunto.PRODUCTOR, -15.8190, -69.9950),
            PuntoRuta("a2", "Juan Huanca Ticona",  TipoPunto.PRODUCTOR, -15.8150, -69.9920),
            PuntoRuta("a3", "Comunidad A — Parada central", TipoPunto.PARADA, -15.8100, -69.9890),
            PuntoRuta("a4", "Retorno Planta Huata",TipoPunto.DESTINO,  -15.8240, -69.9980)
        )
    ),

    // ── RUTA B: Comunidad B (Este) ──────────────────────────────────────
    RutaAcopio(
        nombre      = "🌾 Ruta B — Comunidad B",
        descripcion = "2 productores · ~8 km · Este de Huata",
        puntos      = listOf(
            PLANTA_HUATA,
            PuntoRuta("b1", "María Condori Flores",  TipoPunto.PRODUCTOR, -15.8260, -69.9900),
            PuntoRuta("b2", "Comunidad B — Punto carga", TipoPunto.PARADA, -15.8280, -69.9840),
            PuntoRuta("b3", "Rosa Mamani Apaza",     TipoPunto.PRODUCTOR, -15.8310, -69.9800),
            PuntoRuta("b4", "Retorno Planta Huata",  TipoPunto.DESTINO,  -15.8240, -69.9980)
        )
    ),

    // ── RUTA C: Comunidad C (Sur) ──────────────────────────────────────
    RutaAcopio(
        nombre      = "🌾 Ruta C — Comunidad C",
        descripcion = "3 productores · ~15 km · Sur de Huata",
        puntos      = listOf(
            PLANTA_HUATA,
            PuntoRuta("c1", "Luis Quispe Chura",    TipoPunto.PRODUCTOR, -15.8300, -69.9960),
            PuntoRuta("c2", "Elena Apaza Mamani",   TipoPunto.PRODUCTOR, -15.8360, -69.9950),
            PuntoRuta("c3", "Comunidad C — Parada", TipoPunto.PARADA,   -15.8410, -69.9930),
            PuntoRuta("c4", "Feliciano Ticona",     TipoPunto.PRODUCTOR, -15.8450, -69.9910),
            PuntoRuta("c5", "Retorno Planta Huata", TipoPunto.DESTINO,  -15.8240, -69.9980)
        )
    ),

    // ── RUTA COMPLETA: Todas las comunidades ─────────────────────────────
    RutaAcopio(
        nombre      = "🗺️ Ruta completa (todas)",
        descripcion = "6 productores · ~35 km · Ruta jornada completa",
        puntos      = listOf(
            PLANTA_HUATA,
            PuntoRuta("x1", "Pedro Quispe — Com. A",    TipoPunto.PRODUCTOR, -15.8190, -69.9950),
            PuntoRuta("x2", "Juan Huanca — Com. A",     TipoPunto.PRODUCTOR, -15.8150, -69.9920),
            PuntoRuta("x3", "María Condori — Com. B",   TipoPunto.PRODUCTOR, -15.8280, -69.9840),
            PuntoRuta("x4", "Rosa Mamani — Com. B",     TipoPunto.PRODUCTOR, -15.8310, -69.9800),
            PuntoRuta("x5", "Luis Quispe — Com. C",     TipoPunto.PRODUCTOR, -15.8360, -69.9950),
            PuntoRuta("x6", "Elena Apaza — Com. C",     TipoPunto.PRODUCTOR, -15.8450, -69.9910),
            PuntoRuta("xf", "Retorno Planta Huata",     TipoPunto.DESTINO,  -15.8240, -69.9980)
        )
    )
)

// ─────────────────────────────────────────────────────────────────────────────
// PANTALLA PRINCIPAL
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun MapaAcopiadorScreen(
    onVolver: () -> Unit,
    modifier: Modifier = Modifier
) {
    var rutaSeleccionada by remember { mutableStateOf<RutaAcopio>(RUTAS_PREDEFINIDAS[0]) }
    var mostrarAgregarPunto by remember { mutableStateOf(false) }
    var puntosExtra by remember { mutableStateOf(listOf<PuntoRuta>()) }

    // Puntos efectivos = ruta base + cualquier punto extra agregado
    val puntosEfectivos = remember(rutaSeleccionada, puntosExtra) {
        if (puntosExtra.isEmpty()) {
            rutaSeleccionada.puntos
        } else {
            // Insertar puntos extra antes del destino final
            val sinFinal = rutaSeleccionada.puntos.dropLast(1)
            val final    = rutaSeleccionada.puntos.last()
            sinFinal + puntosExtra + listOf(final)
        }
    }

    val rutaEfectiva = rutaSeleccionada.copy(puntos = puntosEfectivos)

    Column(modifier = modifier.fillMaxSize()) {

        // ── Encabezado ────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(AzulMapa)
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = onVolver) {
                    Text("←", color = Color.White, fontSize = 20.sp)
                }
                Spacer(Modifier.width(4.dp))
                Column {
                    Text(
                        "Mapa de ruta",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "${puntosEfectivos.size} paradas · ${rutaSeleccionada.descripcion}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }
        }

        // ── Mapa WebView ──────────────────────────────────────────────────
        MapaWebView(
            puntos     = puntosEfectivos,
            rutaActiva = rutaEfectiva,
            modifier   = Modifier
                .fillMaxWidth()
                .height(320.dp)
        )

        HorizontalDivider()

        // ── Lista de contenido ────────────────────────────────────────────
        LazyColumn(
            contentPadding    = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier          = Modifier.fillMaxSize()
        ) {

            // ── Selector de ruta predefinida ──────────────────────────────
            item {
                Text(
                    "Seleccionar ruta",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))
            }

            items(RUTAS_PREDEFINIDAS) { ruta ->
                val activa = ruta.nombre == rutaSeleccionada.nombre
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            rutaSeleccionada  = ruta
                            puntosExtra       = listOf()
                            mostrarAgregarPunto = false
                        }
                        .then(
                            if (activa)
                                Modifier.border(2.dp, AzulMapa, RoundedCornerShape(12.dp))
                            else Modifier
                        ),
                    shape  = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (activa) AzulMapa.copy(alpha = 0.1f)
                                         else MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(if (activa) 4.dp else 1.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(
                                    if (activa) AzulMapa else AzulMapa.copy(alpha = 0.12f),
                                    RoundedCornerShape(10.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                if (activa) "✓" else "${RUTAS_PREDEFINIDAS.indexOf(ruta) + 1}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (activa) Color.White else AzulMapa
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                ruta.nombre,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (activa) AzulMapa else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                ruta.descripcion,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                "${ruta.puntos.count { it.tipo == TipoPunto.PRODUCTOR }} productores",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (activa) AzulMapa else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // ── Paradas de la ruta activa ─────────────────────────────────
            item {
                Spacer(Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Paradas de la ruta",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Button(
                        onClick = { mostrarAgregarPunto = !mostrarAgregarPunto },
                        colors = ButtonDefaults.buttonColors(containerColor = AzulMapa),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            if (mostrarAgregarPunto) "✕ Cancelar" else "+ Parada extra",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }

            // ── Formulario agregar punto extra ────────────────────────────
            if (mostrarAgregarPunto) {
                item {
                    FormularioNuevoPunto(
                        colorPrimario = AzulMapa,
                        onAgregar = { nuevo ->
                            puntosExtra = puntosExtra + nuevo
                            mostrarAgregarPunto = false
                        }
                    )
                }
            }

            // ── Lista de paradas ──────────────────────────────────────────
            items(puntosEfectivos) { punto ->
                val orden = puntosEfectivos.indexOf(punto) + 1
                val esExtra = puntosExtra.any { it.id == punto.id }
                TarjetaPuntoRuta(
                    punto    = punto,
                    orden    = orden,
                    esExtra  = esExtra,
                    onEliminar = {
                        puntosExtra = puntosExtra.filter { it.id != punto.id }
                    }
                )
            }

            // ── Info ──────────────────────────────────────────────────────
            item {
                Spacer(Modifier.height(4.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors   = CardDefaults.cardColors(
                        containerColor = AzulMapa.copy(alpha = 0.07f)
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            "ℹ️ Acerca del mapa",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(4.dp))
                        Text("• La ruta real se traza con enrutamiento por calles (OSRM)", style = MaterialTheme.typography.bodySmall)
                        Text("• Selecciona una ruta predefinida para cambiar el recorrido", style = MaterialTheme.typography.bodySmall)
                        Text("• Agrega paradas extra con el botón «+ Parada extra»", style = MaterialTheme.typography.bodySmall)
                        Text("• 🏭 Inicio · 🌾 Productor · 📍 Parada · 🏁 Final", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// FORMULARIO: Agregar punto extra
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun FormularioNuevoPunto(
    colorPrimario: Color,
    onAgregar: (PuntoRuta) -> Unit
) {
    var nombre          by remember { mutableStateOf("") }
    var latitudTexto    by remember { mutableStateOf("") }
    var longitudTexto   by remember { mutableStateOf("") }
    var tipoSeleccionado by remember { mutableStateOf(TipoPunto.PRODUCTOR) }

    Card(
        modifier  = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("Nueva parada", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

            // Tipo de punto
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(TipoPunto.PRODUCTOR, TipoPunto.PARADA).forEach { tipo ->
                    val sel = tipoSeleccionado == tipo
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                if (sel) colorPrimario else colorPrimario.copy(0.1f),
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { tipoSeleccionado = tipo }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "${tipo.icono} ${tipo.etiqueta}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (sel) Color.White else colorPrimario
                        )
                    }
                }
            }

            OutlinedTextField(
                value         = nombre,
                onValueChange = { nombre = it },
                label         = { Text("Nombre del productor / parada") },
                singleLine    = true,
                modifier      = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value         = latitudTexto,
                    onValueChange = { latitudTexto = it },
                    label         = { Text("Latitud") },
                    placeholder   = { Text("-15.840") },
                    singleLine    = true,
                    modifier      = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value         = longitudTexto,
                    onValueChange = { longitudTexto = it },
                    label         = { Text("Longitud") },
                    placeholder   = { Text("-70.021") },
                    singleLine    = true,
                    modifier      = Modifier.weight(1f)
                )
            }

            Button(
                onClick = {
                    val lat = latitudTexto.toDoubleOrNull()
                    val lon = longitudTexto.toDoubleOrNull()
                    if (nombre.isNotBlank() && lat != null && lon != null) {
                        onAgregar(
                            PuntoRuta(
                                id       = "extra-${nombre.hashCode()}",
                                nombre   = nombre.trim(),
                                tipo     = tipoSeleccionado,
                                latitud  = lat,
                                longitud = lon
                            )
                        )
                    }
                },
                enabled = nombre.isNotBlank()
                    && latitudTexto.toDoubleOrNull() != null
                    && longitudTexto.toDoubleOrNull() != null,
                modifier = Modifier.fillMaxWidth(),
                colors   = ButtonDefaults.buttonColors(containerColor = colorPrimario)
            ) {
                Text("Agregar a la ruta", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// TARJETA: Un punto de la ruta
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun TarjetaPuntoRuta(
    punto: PuntoRuta,
    orden: Int,
    esExtra: Boolean,
    onEliminar: () -> Unit
) {
    val colorTipo = when (punto.tipo) {
        TipoPunto.ORIGEN    -> VerdeHuata
        TipoPunto.DESTINO   -> Color(0xFFC62828)
        TipoPunto.PRODUCTOR -> AzulMapa
        TipoPunto.PARADA    -> Color(0xFFE65100)
    }

    Card(
        modifier  = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(if (esExtra) 3.dp else 1.dp),
        colors    = CardDefaults.cardColors(
            containerColor = if (esExtra)
                colorTipo.copy(alpha = 0.06f)
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Número de orden
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(colorTipo, RoundedCornerShape(50)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    if (punto.tipo == TipoPunto.ORIGEN) "🏭"
                    else if (punto.tipo == TipoPunto.DESTINO) "🏁"
                    else "$orden",
                    fontSize    = if (punto.tipo == TipoPunto.ORIGEN || punto.tipo == TipoPunto.DESTINO) 16.sp else 14.sp,
                    fontWeight  = FontWeight.Bold,
                    color       = Color.White
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        punto.nombre,
                        style      = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (esExtra) {
                        Spacer(Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .background(colorTipo.copy(0.15f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text("nuevo", style = MaterialTheme.typography.labelSmall, color = colorTipo)
                        }
                    }
                }
                Text(
                    "${punto.tipo.icono} ${punto.tipo.etiqueta}",
                    style = MaterialTheme.typography.bodySmall,
                    color = colorTipo
                )
                Text(
                    "Lat: ${truncar4(punto.latitud)} · Lon: ${truncar4(punto.longitud)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Botón eliminar (solo para puntos extra)
            if (esExtra) {
                TextButton(onClick = onEliminar) {
                    Text("✕", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// Formatea un Double con 4 decimales — compatible con commonMain (sin String.format JVM)
private fun truncar4(d: Double): String {
    val factor = 10000.0
    val truncado = kotlin.math.round(d * factor) / factor
    val entero = truncado.toLong()
    val decimal = ((truncado - entero) * factor).toLong()
    val decStr = decimal.toString().trimStart('-').padStart(4, '0')
    return if (d < 0 && entero == 0L) "-0.$decStr" else "$entero.$decStr"
}
