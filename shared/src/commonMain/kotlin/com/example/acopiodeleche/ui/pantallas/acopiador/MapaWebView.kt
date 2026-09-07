package com.example.acopiodeleche.ui.pantallas.acopiador

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Composable multiplataforma para mostrar el mapa OpenStreetMap.
 * - Android: usa AndroidView con WebView
 * - Desktop/JVM: muestra mensaje de uso en móvil
 */
@Composable
expect fun MapaWebView(
    puntos: List<PuntoRuta>,
    modifier: Modifier = Modifier
)

/**
 * Genera el HTML con Leaflet.js + OpenStreetMap para mostrar los puntos.
 * Sin API key — completamente gratuito.
 */
fun generarHtmlMapa(puntos: List<PuntoRuta>): String {
    val centroLat = if (puntos.isEmpty()) -15.840 else puntos.map { it.latitud }.average()
    val centroLon = if (puntos.isEmpty()) -70.021 else puntos.map { it.longitud }.average()

    val marcadores = puntos.joinToString("\n") { punto ->
        val color = when (punto.tipo) {
            TipoPunto.ORIGEN    -> "green"
            TipoPunto.DESTINO   -> "red"
            TipoPunto.PRODUCTOR -> "blue"
            TipoPunto.PARADA    -> "orange"
        }
        """
        L.circleMarker([${punto.latitud}, ${punto.longitud}], {
            color: '$color', fillColor: '$color', fillOpacity: 0.8, radius: 10
        }).addTo(map).bindPopup('<b>${punto.tipo.icono} ${punto.nombre}</b><br>${punto.tipo.etiqueta}');
        """.trimIndent()
    }

    // Línea conectando los puntos en orden
    val coordenadas = puntos.joinToString(", ") { "[${it.latitud}, ${it.longitud}]" }
    val linea = if (puntos.size >= 2) """
        L.polyline([$coordenadas], {color: '#1565C0', weight: 3, dashArray: '8,4'}).addTo(map);
    """.trimIndent() else ""

    return """
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8"/>
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no"/>
<link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"/>
<script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
<style>
  body { margin: 0; padding: 0; }
  #map { width: 100%; height: 100vh; }
</style>
</head>
<body>
<div id="map"></div>
<script>
  var map = L.map('map').setView([$centroLat, $centroLon], 13);
  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '© OpenStreetMap contributors',
    maxZoom: 19
  }).addTo(map);
  $marcadores
  $linea
</script>
</body>
</html>
    """.trimIndent()
}
