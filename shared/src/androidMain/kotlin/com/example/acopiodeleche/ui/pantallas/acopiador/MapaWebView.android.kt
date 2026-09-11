package com.example.acopiodeleche.ui.pantallas.acopiador

import android.content.Intent
import android.net.Uri
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView

private val AzulGM = Color(0xFF1A73E8)  // azul Google Maps

@Composable
actual fun MapaWebView(
    puntos: List<PuntoRuta>,
    rutaActiva: RutaAcopio?,
    modifier: Modifier
) {
    val context = LocalContext.current
    val todosLosPuntos = rutaActiva?.puntos ?: puntos

    // Construir URL de Google Maps con los waypoints
    // Formato: https://www.google.com/maps/dir/lat1,lon1/lat2,lon2/...
    val gmUrl = buildString {
        append("https://www.google.com/maps/dir/")
        todosLosPuntos.forEachIndexed { idx, punto ->
            if (idx > 0) append("/")
            append("${punto.latitud},${punto.longitud}")
        }
    }

    // WebView que carga Google Maps directamente
    val html = buildGoogleMapsHtml(todosLosPuntos, rutaActiva?.nombre ?: "Ruta de acopio")

    Column(modifier = modifier) {
        // Botón abrir en Google Maps app
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(AzulGM)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    rutaActiva?.nombre ?: "Ruta de acopio",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "${todosLosPuntos.size} paradas",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(0.8f)
                )
            }
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(gmUrl))
                    intent.setPackage("com.google.android.apps.maps")
                    try {
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        // Si no tiene Google Maps, abre en navegador
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(gmUrl))
                        context.startActivity(browserIntent)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    "🗺️ Abrir Maps",
                    color = AzulGM,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }

        // WebView con mini mapa OpenStreetMap embebido (preview)
        AndroidView(
            factory = { ctx ->
                WebView(ctx).apply {
                    webViewClient = WebViewClient()
                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        loadWithOverviewMode = true
                        useWideViewPort = true
                        setSupportZoom(true)
                        builtInZoomControls = true
                        displayZoomControls = false
                        cacheMode = WebSettings.LOAD_DEFAULT
                    }
                    loadDataWithBaseURL(
                        "https://openstreetmap.org",
                        html,
                        "text/html",
                        "UTF-8",
                        null
                    )
                }
            },
            update = { webView ->
                webView.loadDataWithBaseURL(
                    "https://openstreetmap.org",
                    buildGoogleMapsHtml(todosLosPuntos, rutaActiva?.nombre ?: "Ruta"),
                    "text/html",
                    "UTF-8",
                    null
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
    }
}

/**
 * Genera HTML con el mapa de preview usando OpenStreetMap + Leaflet.
 * El botón principal abre Google Maps app.
 */
private fun buildGoogleMapsHtml(puntos: List<PuntoRuta>, nombreRuta: String): String {
    if (puntos.isEmpty()) return "<html><body><p>Sin puntos</p></body></html>"

    val centroLat = puntos.map { it.latitud }.average()
    val centroLon = puntos.map { it.longitud }.average()

    val marcadoresJs = buildString {
        puntos.forEach { punto ->
            val color = when (punto.tipo) {
                TipoPunto.ORIGEN    -> "#1A73E8"
                TipoPunto.DESTINO   -> "#C62828"
                TipoPunto.PRODUCTOR -> "#2E7D32"
                TipoPunto.PARADA    -> "#E65100"
            }
            val lat = punto.latitud
            val lon = punto.longitud
            val icono = punto.tipo.icono
            val nombre = punto.nombre
            val etiqueta = punto.tipo.etiqueta
            appendLine(
                "L.circleMarker([$lat,$lon],{color:'$color',fillColor:'$color'," +
                "fillOpacity:0.9,radius:12,weight:2}).addTo(map)" +
                ".bindPopup('<b>$icono $nombre</b><br>$etiqueta');"
            )
        }
    }

    val osrmCoords = puntos.joinToString(";") { "${it.longitud},${it.latitud}" }
    val lineaFallback = puntos.joinToString(",") { "[${it.latitud},${it.longitud}]" }

    val scriptRuta = if (puntos.size >= 2) {
        "fetch('https://router.project-osrm.org/route/v1/driving/$osrmCoords?overview=full&geometries=geojson')" +
        ".then(function(r){return r.json();})" +
        ".then(function(d){" +
        "  if(d.code==='Ok'&&d.routes&&d.routes[0]){" +
        "    L.geoJSON(d.routes[0].geometry,{style:{color:'#1A73E8',weight:5,opacity:0.85}}).addTo(map);" +
        "  } else {" +
        "    L.polyline([$lineaFallback],{color:'#1A73E8',weight:4,dashArray:'8,4'}).addTo(map);" +
        "  }" +
        "}).catch(function(){" +
        "  L.polyline([$lineaFallback],{color:'#1A73E8',weight:4,dashArray:'8,4'}).addTo(map);" +
        "});"
    } else ""

    val boundsJs = if (puntos.size > 1) {
        "var bnds='$osrmCoords'.split(';').map(function(c){var p=c.split(',');return[parseFloat(p[1]),parseFloat(p[0])];});" +
        "map.fitBounds(L.latLngBounds(bnds),{padding:[20,20]});"
    } else ""

    return """<!DOCTYPE html>
<html><head>
<meta charset="utf-8"/>
<meta name="viewport" content="width=device-width,initial-scale=1.0,maximum-scale=1.0,user-scalable=no"/>
<link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"/>
<script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
<style>
body{margin:0;padding:0;}
#map{width:100%;height:100vh;}
</style>
</head><body>
<div id="map"></div>
<script>
var map=L.map('map',{zoomControl:true}).setView([$centroLat,$centroLon],14);
L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',{
  attribution:'© OpenStreetMap',maxZoom:19
}).addTo(map);
$marcadoresJs
$scriptRuta
$boundsJs
</script></body></html>"""
}
