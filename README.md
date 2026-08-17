# LactoRuta

> Registra la recolección de leche en campo, incluso sin señal, y llévala sincronizada hasta la planta.

*(Nombre provisional: reemplácenlo por el que elija el equipo — debe ser corto y aparecer así en repo y sustentaciones)*

## Problema que resuelve
La empresa recolecta leche de productores de la zona a través de rutas fijas, la traslada a la planta y luego la recibe y controla. Gran parte de este proceso se lleva de forma manual o con información dispersa (cuadernos, hojas sueltas, mensajes), lo que dificulta controlar las entregas, las cantidades exactas recolectadas, el cumplimiento de rutas, las incidencias durante la recolección y los datos de cada productor. Además, no hay una forma clara de comparar lo que se recogió en campo contra lo que finalmente llega y se registra en planta.

## Público objetivo
- **Recolectores/choferes de ruta**: registran cada entrega directamente en el punto de recolección, muchas veces sin conexión a internet.
- **Personal de planta**: recibe la leche, la contrasta contra lo recolectado en campo y controla incidencias.
- **Administración**: consulta el estado de productores, rutas e histórico de entregas.

## Funcionalidades previstas
- Iniciar sesión según el rol (recolector, planta, administración).
- Registrar una recolección: productor, cantidad, ruta, fecha/hora.
- Registrar incidencias durante la recolección (leche rechazada, faltante, retraso, etc.).
- Listar y filtrar recolecciones por ruta, productor o fecha.
- Registrar la recepción en planta y compararla contra lo recolectado en campo.
- Gestionar el catálogo de productores (alta, edición, datos de contacto y ruta asignada).
- Trabajar sin conexión en campo y sincronizar automáticamente al recuperar señal.

## Entidad principal del CRUD
**Recolección**
- id
- productor (relación con Productor)
- ruta
- cantidad (litros)
- fecha y hora
- estado (recolectado / en tránsito / recibido en planta)
- incidencia (opcional: tipo y descripción)

## Capacidad nativa prevista
Geolocalización (ubicar puntos de recolección y rutas), cámara/QR (identificar rápidamente al productor) y notificaciones (avisos de incidencias o confirmación de recepción en planta).

## Equipo [nombre del equipo]
| Integrante | Código | Rol semana 1 |
|---|---|---|
| LINCOL WERNER YUJRA CALLA | Por completar | Coordinación |
| GUIDO DANIEL MAYTA SUAÑA | Por completar | Lógica y datos |
| RONALD CONDORI OLAZABAL | Por completar | UI |
| DILWERT JHONATAN AGUILAR PAJA | Por completar | QA y documentación |

## Tecnologías
Kotlin Multiplatform · Compose Multiplatform · targets Android y Desktop
(iOS preparado: requiere macOS para compilar)
