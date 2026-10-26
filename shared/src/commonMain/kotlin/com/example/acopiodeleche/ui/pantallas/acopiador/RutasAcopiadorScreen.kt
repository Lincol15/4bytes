package com.example.acopiodeleche.ui.pantallas.acopiador

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.acopiodeleche.domain.model.ParadaRuta
import com.example.acopiodeleche.domain.model.RutaRecoleccion
import com.example.acopiodeleche.ui.estado.CargaOError
import com.example.acopiodeleche.ui.estado.EstadoUi

private val NaranjaRuta = Color(0xFFE65100)

/** Rutas de recolección asignadas al acopiador (desde Laravel) y estado de cada parada. */
@Composable
fun RutasAcopiadorScreen(
    onVolver: () -> Unit,
    viewModel: RutasViewModel = viewModel { RutasViewModel() }
) {
    val estado by viewModel.rutas.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(NaranjaRuta)
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = onVolver) {
                    Text("←", color = Color.White, fontSize = 20.sp)
                }
                Spacer(Modifier.width(4.dp))
                Text(
                    "Mis rutas",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (error != null) {
            Text(
                error.orEmpty(),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        when (val actual = estado) {
            is EstadoUi.Exito -> if (actual.datos.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No tienes rutas asignadas", style = MaterialTheme.typography.titleMedium)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(actual.datos, key = { it.id }) { ruta ->
                        TarjetaRuta(ruta) { parada, nuevoEstado ->
                            viewModel.actualizarParada(parada, nuevoEstado)
                        }
                    }
                }
            }
            else -> CargaOError(actual, onReintentar = viewModel::cargar)
        }
    }
}

@Composable
private fun TarjetaRuta(ruta: RutaRecoleccion, onCambiarEstado: (ParadaRuta, String) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text("${ruta.codigo} · ${ruta.nombre}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(
                listOfNotNull(ruta.dia, ruta.horaInicio, ruta.vehiculo, ruta.estado).joinToString(" · "),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                "${ruta.paradasVisitadas}/${ruta.paradas.size} paradas visitadas",
                style = MaterialTheme.typography.labelMedium,
                color = NaranjaRuta
            )
            ruta.paradas.forEach { parada ->
                Spacer(Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(Modifier.height(6.dp))
                Text(
                    "${parada.orden}. ${parada.productor?.nombreCompleto ?: "Productor"} · ${parada.litrosEstimados} L est.",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                parada.instrucciones?.takeIf { it.isNotBlank() }?.let {
                    Text("📝 $it", style = MaterialTheme.typography.bodySmall)
                }
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(ParadaRuta.ESTADOS_PARADA.entries.toList()) { (clave, etiqueta) ->
                        FilterChip(
                            selected = parada.estado == clave,
                            onClick = { if (parada.estado != clave) onCambiarEstado(parada, clave) },
                            label = { Text(etiqueta, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }
            }
        }
    }
}
