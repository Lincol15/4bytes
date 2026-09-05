package com.example.acopiodeleche.ui.pantallas.acopiador

import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.acopiodeleche.domain.model.DatosMock
import com.example.acopiodeleche.domain.model.SesionActual
import com.example.acopiodeleche.ui.pantallas.acopio.AcopioScreen
import com.example.acopiodeleche.ui.pantallas.productores.ProductoresScreen

private val VerdeHuata = Color(0xFF2E7D32)
private val AzulHuata = Color(0xFF1565C0)

@Composable
fun AcopiadorHomeScreen(
    onCerrarSesion: () -> Unit,
    modifier: Modifier = Modifier
) {
    val usuario = SesionActual.usuario
    var pantallaActual by remember { mutableStateOf(0) }
    // 0=inicio, 1=registrar acopio, 2=productores, 3=historial

    when (pantallaActual) {
        1 -> AcopioScreen(
            onVolver = { pantallaActual = 0 }
        )
        2 -> ProductoresScreen(
            soloLectura = true,
            onVolver = { pantallaActual = 0 }
        )
        else -> {
            Column(modifier = modifier.fillMaxSize()) {

                // Encabezado verde
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(VerdeHuata)
                        .padding(16.dp)
                ) {
                    Column {
                        Text(
                            text = "Bienvenido, ${usuario?.nombres ?: "Acopiador"}",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Ecolácteos Huata · Acopiador",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                    Text(
                        text = "Salir",
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .clickable { onCerrarSesion() }
                            .padding(4.dp),
                        color = Color.White.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                // Resumen jornada
                val registrosHoy = DatosMock.registrosAcopio
                val litrosHoy = registrosHoy.sumOf { it.litros }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TarjetaResumen(
                        icono = "🥛",
                        valor = "${registrosHoy.size}",
                        etiqueta = "Entregas hoy",
                        color = VerdeHuata,
                        modifier = Modifier.weight(1f)
                    )
                    TarjetaResumen(
                        icono = "📦",
                        valor = "$litrosHoy L",
                        etiqueta = "Litros hoy",
                        color = AzulHuata,
                        modifier = Modifier.weight(1f)
                    )
                }

                HorizontalDivider()

                // Menú principal
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = "Acciones",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                    item {
                        MenuItemAcopiador(
                            icono = "➕",
                            titulo = "Registrar acopio",
                            descripcion = "Registrar nueva entrega de leche",
                            color = VerdeHuata
                        ) { pantallaActual = 1 }
                    }
                    item {
                        MenuItemAcopiador(
                            icono = "👥",
                            titulo = "Ver productores",
                            descripcion = "Lista de productores de la zona",
                            color = AzulHuata
                        ) { pantallaActual = 2 }
                    }
                    item {
                        MenuItemAcopiador(
                            icono = "📋",
                            titulo = "Historial de jornada",
                            descripcion = "Entregas registradas hoy",
                            color = Color(0xFF6A1B9A)
                        ) { pantallaActual = 3 }
                    }
                    item {
                        MenuItemAcopiador(
                            icono = "🗺",
                            titulo = "Mapa / Mi ubicación",
                            descripcion = "Próximamente disponible",
                            color = Color(0xFF00838F)
                        ) { /* fase 3 */ }
                    }

                    // Historial reciente
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Últimas entregas",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    items(DatosMock.registrosAcopio.takeLast(3).reversed()) { registro ->
                        val productor = DatosMock.productores
                            .find { it.idProductor == registro.idProductor }
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("🥛", fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = productor?.nombreCompleto ?: "Productor",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${registro.fecha} ${registro.hora} · ${registro.vehiculo}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(
                                    text = registro.litrosFormateados,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = VerdeHuata
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaResumen(
    icono: String,
    valor: String,
    etiqueta: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(icono, fontSize = 28.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = valor,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = etiqueta,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun MenuItemAcopiador(
    icono: String,
    titulo: String,
    descripcion: String,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(color.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(icono, fontSize = 24.sp)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = descripcion,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text("›", fontSize = 24.sp, color = color)
        }
    }
}
