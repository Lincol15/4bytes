# Checklist de viabilidad — Idea: Sistema de gestión de acopio de leche

| Criterio | Tipo | ¿Cumple? | Justificación |
|---|---|---|---|
| Entidad principal clara para el CRUD | Obligatorio | **Sí** | La entidad **Recolección** (o "Entrega de leche"): productor, ruta, cantidad, fecha/hora, estado, incidencias. Se crea, lista, edita y elimina de forma natural. |
| Requiere usuarios que inician sesión | Obligatorio | **Sí** | Al menos dos perfiles: recolector/chofer en campo y personal de planta/administración. Cada uno ve y registra información distinta. |
| Tiene sentido usarla sin conexión | Obligatorio | **Sí** | La recolección ocurre en campo, muchas veces en zonas rurales sin buena señal. El recolector debe poder registrar la entrega offline y sincronizar al llegar a la planta o donde haya cobertura. |
| Alcance realista (4-6 pantallas, 1-2 entidades secundarias) | Obligatorio | **Sí** | Pantallas estimadas: login, lista de rutas del día, registro de recolección, detalle de productor, recepción en planta, historial/reportes básicos. Entidades secundarias: Productor y Ruta. |
| Existe un usuario real para validar | Recomendado | **Sí** | La empresa de acopio de leche y sus productores son el caso real que motivó el proyecto. |
| Alguna capacidad nativa aporta valor | Recomendado | **Sí** | Geolocalización (rutas y puntos de recolección), cámara/QR (identificar rápidamente al productor), notificaciones (incidencias, confirmación de recepción). |

**Conclusión:** la idea cumple los 4 criterios obligatorios y los 2 recomendados. No cae en las categorías de alto riesgo (no es red social, no es marketplace multi-tienda con pagos, no depende de IA). Alcance viable para un semestre.

---

## Otras ideas consideradas y descartadas (para completar el acta)

Antes de confirmar esta idea, es buena práctica que el equipo mencione 2-3 alternativas que evaluaron y por qué las descartaron (el checklist pide "ideas descartadas"). Ejemplos de motivo típico: alcance muy amplio, no había offline claro, no había usuario real para validar, entidad poco definida. Complétenlo en el acta con las ideas que realmente discutieron en su lluvia de ideas.
