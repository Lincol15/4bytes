package com.example.acopiodeleche.ui.pantallas.acopiador

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Composable multiplataforma para mostrar el mapa OpenStreetMap.
 * - Android: usa AndroidView con WebView
 * - Desktop/JVM: muestra mensaje de uso en móvil
 *
 * Los modelos PuntoRuta, TipoPunto y RutaAcopio están en RutaModelos.kt
 */
@Composable
expect fun MapaWebView(
    puntos: List<PuntoRuta>,
    rutaActiva: RutaAcopio? = null,
    modifier: Modifier = Modifier
)

/**
 * Genera el HTML con Leaflet.js + OpenStreetMap para mostrar los puntos
 * y traza la ruta usando OSRM (enrutamiento real por calles).
 * Sin API key — completamente gratuito.
 */
fun generarHtmlMapa(puntos: List<PuntoRuta>, rutaActiva: RutaAcopio? = null): String {
    val todosLosPuntos: List<PuntoRuta> = rutaActiva?.puntos ?: puntos

    // ── Pre-calcular todos los valores ANTES del ensamblado del HTML ─────

    val centroLat: Double = if (todosLosPuntos.isEmpty()) -15.840
        else { var sum = 0.0; todosLosPuntos.forEach { punto: PuntoRuta -> sum += punto.latitud }; sum / todosLosPuntos.size }
    val centroLon: Double = if (todosLosPuntos.isEmpty()) -70.021
        else { var sum = 0.0; todosLosPuntos.forEach { punto: PuntoRuta -> sum += punto.longitud }; sum / todosLosPuntos.size }

    // Marcadores Leaflet para cada punto
    val marcadoresBuilder = StringBuilder()
    todosLosPuntos.forEach { punto: PuntoRuta ->
        val color = when (punto.tipo) {
            TipoPunto.ORIGEN    -> "#2E7D32"
            TipoPunto.DESTINO   -> "#C62828"
            TipoPunto.PRODUCTOR -> "#1565C0"
            TipoPunto.PARADA    -> "#E65100"
        }
        val radio = if (punto.tipo == TipoPunto.ORIGEN || punto.tipo == TipoPunto.DESTINO) 14 else 10
        val popupExtra = if (punto.tipo == TipoPunto.ORIGEN) "<br><i>Inicio de ruta</i>" else ""
        val lat = punto.latitud
        val lon = punto.longitud
        val icono = punto.tipo.icono
        val nombre = punto.nombre
        val etiqueta = punto.tipo.etiqueta
        marcadoresBuilder.append(
            "L.circleMarker([$lat,$lon],{color:'$color',fillColor:'$color'," +
            "fillOpacity:0.85,radius:$radio,weight:2}).addTo(map)" +
            ".bindPopup('<b>$icono $nombre</b><br><span style=\"color:$color\">$etiqueta</span>$popupExtra');\n"
        )
    }
    val marcadores = marcadoresBuilder.toString()

    // Números de parada sobre productores/paradas intermedias
    val numerosBuilder = StringBuilder()
    var numIdx = 1
    todosLosPuntos.forEach { punto: PuntoRuta ->
        if (punto.tipo == TipoPunto.PRODUCTOR || punto.tipo == TipoPunto.PARADA) {
            val lat = punto.latitud
            val lon = punto.longitud
            val num = numIdx
            numerosBuilder.append(
                "L.marker([$lat,$lon],{icon:L.divIcon({html:" +
                "'<div style=\"background:#1565C0;color:white;border-radius:50%;width:22px;" +
                "height:22px;display:flex;align-items:center;justify-content:center;" +
                "font-weight:bold;font-size:12px;border:2px solid white\">" +
                "$num</div>'," +
                "iconSize:[22,22],iconAnchor:[11,11],className:''})}).addTo(map);\n"
            )
            numIdx++
        }
    }
    val numerosParada = numerosBuilder.toString()

    // Coordenadas para OSRM: lon,lat;lon,lat;...
    val osrmBuilder = StringBuilder()
    todosLosPuntos.forEachIndexed { idx: Int, punto: PuntoRuta ->
        if (idx > 0) osrmBuilder.append(";")
        osrmBuilder.append("${punto.longitud},${punto.latitud}")
    }
    val coordenadasOSRM = osrmBuilder.toString()

    // Coordenadas para línea directa fallback: [lat,lon],[lat,lon],...
    val lineaBuilder = StringBuilder()
    todosLosPuntos.forEachIndexed { idx: Int, punto: PuntoRuta ->
        if (idx > 0) lineaBuilder.append(",")
        lineaBuilder.append("[${punto.latitud},${punto.longitud}]")
    }
    val coordenadasLinea = lineaBuilder.toString()

    // Script ruta OSRM con fallback
    val scriptRuta: String = if (todosLosPuntos.size >= 2) {
        "var osrmUrl='https://router.project-osrm.org/route/v1/driving/" +
        coordenadasOSRM +
        "?overview=full&geometries=geojson';\n" +
        "fetch(osrmUrl).then(function(r){return r.json();})\n" +
        ".then(function(data){\n" +
        "  if(data.code==='Ok'&&data.routes&&data.routes[0]){\n" +
        "    L.geoJSON(data.routes[0].geometry,{style:{color:'#1565C0',weight:5,opacity:0.85}}).addTo(map);\n" +
        "    var dist=(data.routes[0].distance/1000).toFixed(1);\n" +
        "    var mins=Math.round(data.routes[0].duration/60);\n" +
        "    var d=document.getElementById('route-info');\n" +
        "    if(d)d.innerHTML='\uD83D\uDEE3\uFE0F '+dist+' km | \u23F1\uFE0F '+mins+' min';\n" +
        "  } else {\n" +
        "    L.polyline([$coordenadasLinea],{color:'#1565C0',weight:4,dashArray:'8,4'}).addTo(map);\n" +
        "  }\n" +
        "}).catch(function(){\n" +
        "  L.polyline([$coordenadasLinea],{color:'#1565C0',weight:4,dashArray:'8,4'}).addTo(map);\n" +
        "});"
    } else ""

    // Script para ajustar bounds
    val boundsScript: String = if (todosLosPuntos.size > 1) {
        "var allC='$coordenadasOSRM'.split(';').map(function(c){" +
        "var p=c.split(',');return[parseFloat(p[1]),parseFloat(p[0])];});\n" +
        "map.fitBounds(L.latLngBounds(allC),{padding:[30,30]});"
    } else ""

    val nombreRuta = rutaActiva?.nombre ?: "Ruta de acopio"
    val cantPuntos = todosLosPuntos.size

    // ── Ensamblado HTML (solo concatenación de strings pre-calculados) ────
    return "<!DOCTYPE html>\n" +
        "<html><head>\n" +
        "<meta charset=\"utf-8\"/>\n" +
        "<meta name=\"viewport\" content=\"width=device-width,initial-scale=1.0," +
        "maximum-scale=1.0,user-scalable=no\"/>\n" +
        "<link rel=\"stylesheet\" " +
        "href=\"https://unpkg.com/leaflet@1.9.4/dist/leaflet.css\"/>\n" +
        "<script src=\"https://unpkg.com/leaflet@1.9.4/dist/leaflet.js\"></script>\n" +
        "<style>\n" +
        "body{margin:0;padding:0;font-family:Arial,sans-serif;}\n" +
        "#map{width:100%;height:calc(100vh - 44px);}\n" +
        "#header{background:#1565C0;color:white;padding:6px 12px;height:44px;" +
        "display:flex;align-items:center;justify-content:space-between;" +
        "box-sizing:border-box;}\n" +
        "#header h3{margin:0;font-size:14px;font-weight:bold;}\n" +
        "#route-info{font-size:12px;opacity:0.92;}\n" +
        ".leyenda{background:white;padding:8px 10px;border-radius:8px;" +
        "font-size:11px;line-height:1.7;box-shadow:0 1px 5px rgba(0,0,0,.3);}\n" +
        "</style></head><body>\n" +
        "<div id=\"header\">\n" +
        "  <h3>\uD83D\uDDFA\uFE0F $nombreRuta &nbsp;&middot;&nbsp; $cantPuntos paradas</h3>\n" +
        "  <span id=\"route-info\">Calculando ruta...</span>\n" +
        "</div>\n" +
        "<div id=\"map\"></div>\n" +
        "<script>\n" +
        "var map=L.map('map').setView([$centroLat,$centroLon],14);\n" +
        "L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',{\n" +
        "  attribution:'\u00A9 <a href=\"https://openstreetmap.org\">OpenStreetMap</a>',\n" +
        "  maxZoom:19\n" +
        "}).addTo(map);\n" +
        marcadores + "\n" +
        numerosParada + "\n" +
        scriptRuta + "\n" +
        "var leyenda=L.control({position:'bottomleft'});\n" +
        "leyenda.onAdd=function(map){\n" +
        "  var div=L.DomUtil.create('div','leyenda');\n" +
        "  div.innerHTML='<b>Leyenda</b><br>" +
        "<span style=\"color:#2E7D32\">\u25CF</span> Planta<br>" +
        "<span style=\"color:#1565C0\">\u25CF</span> Productor<br>" +
        "<span style=\"color:#E65100\">\u25CF</span> Parada<br>" +
        "<span style=\"color:#C62828\">\u25CF</span> Destino';\n" +
        "  return div;\n" +
        "};\n" +
        "leyenda.addTo(map);\n" +
        boundsScript + "\n" +
        "</script></body></html>"
}
