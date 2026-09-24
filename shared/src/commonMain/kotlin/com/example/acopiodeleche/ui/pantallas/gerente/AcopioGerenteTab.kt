package com.example.acopiodeleche.ui.pantallas.gerente

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.acopiodeleche.domain.model.DatosMock
import com.example.acopiodeleche.domain.model.RegistroAcopio

private val VerdeHuata = Color(0xFF2E7D32)
private val AzulGerente = Color(0xFF1A237E)

@Composable
fun AcopioGerenteTab(modifier: Modifier = Modifier) {
    val registros = remember { DatosMock.registrosAcopio.toList() }
    
    // Agrupar por acopiador
    val acopiadoresConRegistros = registros.groupBy { it.acopiador }
    
    Column(modifier = modifier.fillMaxSize()) {
        // ── Encabezado con estadísticas ──────────────────────────────────
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = AzulGerente),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🥛", fontSize = 32.sp)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            "Registro de Acopios",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            "Vista de gerencia - Solo lectura",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(0.8f)
                        )
                    }
                }
                
                Spacer(Modifier.height(16.dp))
                HorizontalDivider(color = Color.White.copy(0.3f))
                Spacer(Modifier.height(16.dp))
                
                // Estadísticas
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    EstadisticaCard(
                        titulo = "Total Registros",
                        valor = "${registros.size}",
                        icono = "📋"
                    )
                    EstadisticaCard(
                        titulo = "Acopiadores",
                        valor = "${acopiadoresConRegistros.size}",
                        icono = "👥"
                    )
                    EstadisticaCard(
                        titulo = "Total Litros",
                        valor = String.format("%.1f L", registros.sumOf { it.litros }),
                        icono = "🥛"
                    )
                }
            }
        }

        // ── Lista de acopios agrupados ───────────────────────────────────
        if (registros.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📊", fontSize = 64.sp)
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "No hay registros de acopio",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Los acopios registrados por el personal aparecerán aquí",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Agrupar por acopiador
                acopiadoresConRegistros.forEach { (nombreAcopiador, registrosAcopiador) ->
                    item {
                        AcopiadorGrupoCard(
                            nombreAcopiador = nombreAcopiador,
                            registros = registrosAcopiador
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EstadisticaCard(titulo: String, valor: String, icono: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp)
    ) {
        Text(icono, fontSize = 24.sp)
        Spacer(Modifier.height(4.dp))
        Text(
            valor,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            titulo,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(0.8f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun AcopiadorGrupoCard(
    nombreAcopiador: String,
    registros: List<RegistroAcopio>
) {
    val totalLitros = registros.sumOf { it.litros }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(3.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header del acopiador
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(VerdeHuata.copy(0.15f), RoundedCornerShape(50)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👤", fontSize = 24.sp)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            nombreAcopiador,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "${registros.size} registro${if(registros.size != 1) "s" else ""}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                // Total de litros
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        String.format("%.1f", totalLitros),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = VerdeHuata
                    )
                    Text(
                        "Litros",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(Modifier.height(8.dp))
            
            // Lista de registros
            registros.forEach { registro ->
                RegistroAcopioItem(registro)
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun RegistroAcopioItem(registro: RegistroAcopio) {
    val productor = DatosMock.productores
        .find { it.idProductor == registro.idProductor }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(0.5f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        productor?.nombreCompleto ?: "Productor desconocido",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    
                    // Comunidad/Zona
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("📍", fontSize = 14.sp)
                        Spacer(Modifier.width(4.dp))
                        Text(
                            registro.zona,
                            style = MaterialTheme.typography.bodySmall,
                            color = VerdeHuata,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    Spacer(Modifier.height(2.dp))
                    
                    // Fecha y hora
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("📅", fontSize = 14.sp)
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "${registro.fecha} · ${registro.hora}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    // Observación si existe
                    if (registro.observacion?.isNotBlank() == true) {
                        Spacer(Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("📝", fontSize = 14.sp)
                            Spacer(Modifier.width(4.dp))
                            Text(
                                registro.observacion ?: "",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
                
                Spacer(Modifier.width(12.dp))
                
                // Litros destacados
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier
                        .background(VerdeHuata.copy(0.1f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        String.format("%.1f", registro.litros),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = VerdeHuata
                    )
                    Text(
                        "Litros",
                        style = MaterialTheme.typography.labelSmall,
                        color = VerdeHuata
                    )
                }
            }
        }
    }
}
