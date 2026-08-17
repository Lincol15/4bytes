# 🥛 4bytes — Sistema de Gestión para el Acopio de Leche

![Estado](https://img.shields.io/badge/Estado-En%20desarrollo-yellow)
![Proyecto](https://img.shields.io/badge/Proyecto-Acad%C3%A9mico-blue)
![Curso](https://img.shields.io/badge/Curso-Ingenier%C3%ADa%20de%20Software%201-green)

##  1. Información general

**4bytes** es un proyecto integrador desarrollado para el curso de **Ingeniería de Software 1**.

El proyecto consiste en el diseño y desarrollo de una **aplicación móvil para la gestión del proceso de acopio de leche**, orientada a una empresa que trabaja con productores de la zona.

La aplicación busca digitalizar y centralizar la información generada durante el proceso de:

**Productor → Recolección → Traslado → Recepción en planta → Control de calidad → Incidencias → Pagos → Reportes**

---

# 2. Integrantes del grupo

| N.º | Integrante                        |
| --: | --------------------------------- |
|   1 | **LINCOL WERNER YUJRA CALLA**     |
|   2 | **GUIDO DANIEL MAYTA SUAÑA**      |
|   3 | **RONALD CONDORI OLAZABAL**       |
|   4 | **DILWERT JHONATAN AGUILAR PAJA** |

**Nombre del grupo:** 4bytes
**Curso:** Ingeniería de Software 1
**Tipo de proyecto:** Proyecto integrador académico

---

#  3. Propuesta de la aplicación

Se propone desarrollar una **aplicación móvil para gestionar y controlar el proceso de acopio de leche**, permitiendo registrar la información desde el momento en que la leche es recolectada de los productores hasta su llegada y recepción en la planta.

La aplicación permitirá centralizar información que actualmente puede encontrarse registrada de manera manual o dispersa en diferentes medios.

El sistema permitirá conocer:

* Qué productor realizó una entrega.
* Cuántos litros fueron recolectados.
* Cuándo se realizó la recolección.
* Qué ruta fue utilizada.
* Quién realizó la recolección.
* Cuándo se realizó el traslado.
* Cuánto llegó finalmente a la planta.
* Si existieron diferencias entre la cantidad recolectada y recibida.
* Qué controles de calidad se realizaron.
* Qué incidencias ocurrieron.
* Cuánto corresponde pagar a cada productor.
* Cuáles son los resultados y estadísticas del proceso.

---

#  4. Problema identificado

La empresa objeto del proyecto se dedica al **acopio de leche**, trabajando directamente con productores de la zona.

El proceso comprende diferentes actividades, como la recolección de leche, el traslado hacia la planta y posteriormente la recepción y control de la materia prima.

Durante este proceso, parte de la información puede ser registrada de manera manual o encontrarse distribuida en diferentes medios, generando dificultades para mantener un control completo de las operaciones.

Entre los principales problemas identificados se encuentran:

* Información dispersa de los productores.
* Registro manual de las entregas.
* Dificultad para controlar las cantidades recolectadas.
* Dificultad para realizar seguimiento de las rutas.
* Falta de un historial centralizado de las recolecciones.
* Dificultad para registrar y consultar incidencias.
* Problemas para comparar la cantidad recolectada en campo con la cantidad recibida en planta.
* Dificultad para consultar rápidamente la información de los productores.
* Mayor posibilidad de errores durante el registro y consolidación de información.
* Dificultad para obtener reportes para la toma de decisiones.

##  Problema principal

> **La empresa presenta dificultades para centralizar, controlar y realizar seguimiento de la información generada durante el proceso de acopio de leche debido al uso de registros manuales o información dispersa.**

---

#  5. Solución propuesta

Para solucionar esta problemática se propone desarrollar una **aplicación móvil de gestión del acopio de leche**.

La aplicación permitirá centralizar la información en un sistema organizado, facilitando el registro, consulta y seguimiento de las diferentes actividades.

La solución permitirá realizar una trazabilidad del proceso:

```text
PRODUCTOR
    │
    ▼
RECOLECCIÓN
    │
    ▼
TRASLADO
    │
    ▼
RECEPCIÓN EN PLANTA
    │
    ▼
CONTROL DE CALIDAD
    │
    ├──────────────► INCIDENCIAS
    │
    ▼
REGISTRO Y CONSOLIDACIÓN
    │
    ▼
PAGOS Y REPORTES
```

De esta manera, la empresa podrá disponer de información centralizada y organizada para realizar un mejor seguimiento del proceso de acopio.

---

#  6. Objetivo general

Desarrollar una aplicación móvil que permita **gestionar, centralizar y controlar la información relacionada con el proceso de acopio de leche**, desde la recolección realizada a los productores hasta la recepción y control en la planta.

---

#  7. Objetivos específicos

* Registrar y administrar la información de los productores.
* Registrar las entregas realizadas por cada productor.
* Registrar la cantidad de leche recolectada.
* Registrar las rutas utilizadas durante la recolección.
* Registrar la información del responsable de la recolección.
* Registrar el traslado de la leche hacia la planta.
* Registrar la cantidad de leche recibida en la planta.
* Comparar la cantidad recolectada con la cantidad recibida.
* Registrar controles de calidad.
* Registrar incidencias ocurridas durante el proceso.
* Mantener un historial de las operaciones realizadas.
* Facilitar la gestión de los pagos correspondientes a los productores.
* Generar reportes para apoyar la toma de decisiones.

---

# 8. Usuarios de la aplicación

La aplicación estará orientada a los diferentes usuarios que participan en el proceso de acopio.

##  Administrador

Podrá:

* Gestionar usuarios.
* Registrar y modificar productores.
* Gestionar rutas.
* Consultar información.
* Consultar reportes.
* Administrar parámetros del sistema.

##  Responsable de recolección

Podrá:

* Consultar productores asignados.
* Consultar rutas.
* Registrar recolecciones.
* Registrar cantidades recolectadas.
* Registrar fecha y hora.
* Registrar incidencias.
* Consultar el historial de recolecciones.

##  Responsable de planta

Podrá:

* Registrar la recepción de leche.
* Registrar cantidades recibidas.
* Realizar controles de calidad.
* Registrar observaciones.
* Registrar incidencias.
* Consultar información de las recolecciones.

##  Responsable administrativo

Podrá:

* Consultar entregas.
* Consultar cantidades acumuladas.
* Gestionar precios.
* Calcular montos correspondientes.
* Registrar pagos.
* Consultar pagos pendientes.
* Generar reportes administrativos.

---

#  9. Funcionalidades principales

## 9.1 Gestión de productores

Permitirá:

* Registrar productores.
* Modificar información.
* Consultar productores.
* Buscar productores.
* Activar o desactivar productores.
* Consultar historial de entregas.

### Datos principales

* Código del productor.
* Nombres y apellidos.
* Documento de identidad.
* Teléfono.
* Dirección.
* Comunidad o zona.
* Estado.

---

## 9.2 Gestión de rutas

Permitirá:

* Registrar rutas.
* Modificar rutas.
* Consultar rutas.
* Asociar productores a una ruta.
* Registrar responsable.
* Consultar historial.

---

## 9.3 Registro de recolección

Permitirá registrar las actividades realizadas en campo.

### Información

* Productor.
* Ruta.
* Responsable.
* Fecha.
* Hora.
* Cantidad de litros.
* Observaciones.

---

## 9.4 Gestión del traslado

Permitirá registrar el traslado desde los puntos de recolección hasta la planta.

### Información

* Ruta.
* Responsable.
* Vehículo.
* Fecha y hora de salida.
* Fecha y hora de llegada.
* Cantidad transportada.
* Observaciones.
* Incidencias.

---

## 9.5 Recepción en planta

Permitirá registrar la cantidad de leche que llega a la planta.

El sistema podrá relacionar:

**Cantidad recolectada → Cantidad transportada → Cantidad recibida**

Esto permitirá detectar diferencias durante el proceso.

### Ejemplo

```text
Cantidad recolectada: 500 litros
Cantidad recibida:    490 litros
Diferencia:            10 litros
```

La diferencia podrá ser registrada y posteriormente analizada.

---

## 9.6 Control de calidad

Permitirá registrar los resultados de los controles realizados sobre la leche recibida.

### Información

* Fecha.
* Hora.
* Recepción.
* Parámetros evaluados.
* Resultado.
* Estado.
* Observaciones.

El resultado podrá clasificarse, según los criterios definidos por la empresa, como:

* Aprobado.
* Observado.
* Rechazado.

---

## 9.7 Gestión de incidencias

Permitirá registrar problemas ocurridos durante las diferentes etapas.

### Ejemplos

* Retraso en la recolección.
* Problemas con el vehículo.
* Derrame de leche.
* Diferencia de cantidades.
* Problemas durante el traslado.
* Problemas durante la recepción.
* Problemas de calidad.

Cada incidencia podrá registrar:

* Tipo.
* Fecha.
* Lugar.
* Descripción.
* Responsable.
* Estado.
* Observaciones.

---

## 9.8 Gestión de pagos

Permitirá gestionar la información económica relacionada con los productores.

### Funcionalidades

* Registrar precio por litro.
* Calcular monto de una entrega.
* Consultar monto acumulado.
* Registrar pagos.
* Consultar pagos pendientes.
* Consultar historial de pagos.

---

#  10. Reportes

La aplicación permitirá generar información para facilitar el análisis y la toma de decisiones.

## Reportes de producción

* Producción diaria.
* Producción semanal.
* Producción mensual.
* Producción por productor.
* Producción por ruta.

## Reportes de recepción

* Cantidad recolectada.
* Cantidad recibida.
* Diferencias detectadas.
* Recepciones aceptadas.
* Recepciones rechazadas.

## Reportes administrativos

* Productores registrados.
* Productores activos.
* Historial de entregas.
* Montos por productor.
* Pagos realizados.
* Pagos pendientes.

## Reportes de incidencias

* Incidencias por fecha.
* Incidencias por ruta.
* Incidencias por tipo.
* Incidencias pendientes.

---

# 11. Flujo principal de la aplicación

El funcionamiento general será:

```text
1. Registrar productor
        ↓
2. Asignar ruta
        ↓
3. Realizar recolección
        ↓
4. Registrar cantidad recolectada
        ↓
5. Realizar traslado
        ↓
6. Registrar llegada a planta
        ↓
7. Registrar cantidad recibida
        ↓
8. Realizar control de calidad
        ↓
9. Registrar incidencias
        ↓
10. Consolidar información
        ↓
11. Gestionar pago
        ↓
12. Generar reportes
```

---

#  12. Plataforma tecnológica

Para la aplicación móvil se analizaron tres alternativas de desarrollo multiplataforma:

* Kotlin Multiplatform (KMP)
* Flutter
* React Native

La comparación permitirá seleccionar la tecnología más adecuada para las necesidades del proyecto.

---

#  13. Cuadro comparativo: KMP vs Flutter vs React Native

| Criterio                     | Kotlin Multiplatform (KMP)                              | Flutter                                         | React Native                               |
| ---------------------------- | ------------------------------------------------------- | ----------------------------------------------- | ------------------------------------------ |
| Desarrollador / organización | JetBrains                                               | Google                                          | Meta / comunidad                           |
| Lenguaje principal           | Kotlin                                                  | Dart                                            | JavaScript / TypeScript                    |
| Android                      | Sí                                                      | Sí                                              | Sí                                         |
| iOS                          | Sí                                                      | Sí                                              | Sí                                         |
| Código compartido            | Flexible, según arquitectura                            | Alto                                            | Alto                                       |
| Interfaz de usuario          | Puede ser nativa o compartida con Compose Multiplatform | Widgets de Flutter                              | Componentes nativos                        |
| Rendimiento                  | Cercano al nativo                                       | Alto                                            | Alto                                       |
| Integración con APIs nativas | Muy buena                                               | Buena                                           | Buena                                      |
| Curva de aprendizaje         | Media                                                   | Media                                           | Media                                      |
| Ecosistema                   | En crecimiento                                          | Amplio                                          | Amplio                                     |
| Enfoque principal            | Compartir lógica y/o UI manteniendo flexibilidad nativa | Desarrollo multiplataforma desde un código base | Desarrollo de interfaces nativas con React |
| Adecuado para 4bytes         | Sí                                                      | **Sí**                                          | Sí                                         |

Kotlin Multiplatform permite compartir código entre diferentes plataformas y ofrece la posibilidad de compartir lógica de negocio manteniendo interfaces nativas o compartir también la interfaz mediante Compose Multiplatform.

Flutter está orientado al desarrollo de aplicaciones multiplataforma desde un único código base y proporciona un conjunto de widgets y herramientas para construir interfaces.

React Native permite desarrollar aplicaciones para Android e iOS utilizando React, JavaScript o TypeScript, y sus componentes se representan mediante elementos nativos de las plataformas.

---

#  14. Tecnología propuesta

Después de realizar la comparación inicial, se propone utilizar **Flutter** para el desarrollo de la aplicación móvil de 4bytes.

## ¿Por qué Flutter?

Flutter resulta adecuado para el proyecto debido a que:

* Permite desarrollar aplicaciones multiplataforma desde un código base.
* Cuenta con una amplia documentación.
* Proporciona componentes de interfaz mediante widgets.
* Facilita la creación de interfaces personalizadas.
* Permite desarrollar para Android e iOS.
* Es apropiado para proyectos que necesitan avanzar rápidamente con una aplicación móvil.
* Cuenta con recursos de aprendizaje oficiales y una comunidad amplia.

La documentación oficial de Flutter dispone de guías de inicio, catálogo de widgets, documentación de API y recursos de aprendizaje para desarrollar aplicaciones multiplataforma.

> **Nota:** La selección de Flutter corresponde a una decisión preliminar y podrá ser revisada por el equipo durante la etapa de análisis técnico.

---

#  15. Tecnologías previstas

### Aplicación móvil

**Flutter**

### Lenguaje

**Dart**

### Backend

Por definir durante la etapa de diseño.

### Base de datos

**MySQL**

### Control de versiones

**Git**

### Repositorio

**GitHub**

### Modelado

**UML**

---

#  16. Arquitectura preliminar

La arquitectura definitiva será determinada durante la etapa de diseño.

De manera preliminar se plantea:

```text
┌─────────────────────────────┐
│       APLICACIÓN MÓVIL      │
│           Flutter           │
└──────────────┬──────────────┘
               │
               │ HTTP / API REST
               ▼
┌─────────────────────────────┐
│           BACKEND           │
│      Lógica de negocio      │
└──────────────┬──────────────┘
               │
               ▼
┌─────────────────────────────┐
│          MySQL              │
│       Base de datos         │
└─────────────────────────────┘
```

---

# 17. Entidades preliminares

Durante el análisis se consideran inicialmente las siguientes entidades:

```text
Usuario
Productor
Ruta
Recolección
Traslado
Recepción
ControlCalidad
Incidencia
Pago
```

Las entidades y relaciones definitivas serán establecidas durante el diseño de la base de datos.

---

#  18. Estructura del repositorio

```text
4bytes/
│
├── README.md
│
├── docs/
│   ├── acta/
│   │   └── acta-conformacion.md
│   │
│   ├── requisitos/
│   │   ├── requisitos-funcionales.md
│   │   └── requisitos-no-funcionales.md
│   │
│   ├── uml/
│   │   ├── casos-uso/
│   │   ├── clases/
│   │   └── secuencia/
│   │
│   ├── arquitectura/
│   │   └── arquitectura.md
│   │
│   └── pruebas/
│
├── mobile/
│   └── ...
│
├── backend/
│   └── ...
│
└── database/
    ├── scripts/
    └── ...
```

---

#  19. Control de versiones

El proyecto utilizará Git y GitHub para administrar el código fuente y facilitar el trabajo colaborativo.

Se propone utilizar la siguiente estrategia:

```text
main
│
└── develop
     │
     ├── feature/productores
     ├── feature/rutas
     ├── feature/recoleccion
     ├── feature/traslado
     ├── feature/recepcion
     ├── feature/calidad
     ├── feature/incidencias
     └── feature/pagos
```

### Crear una rama

```bash
git checkout -b feature/productores
```

### Registrar cambios

```bash
git add .
git commit -m "Agregar gestión de productores"
```

### Subir cambios

```bash
git push origin feature/productores
```

---

#  20. Acta de conformación

El equipo **4bytes** está conformado por cuatro integrantes para el desarrollo del proyecto integrador del curso de Ingeniería de Software 1.

Los integrantes se comprometen a participar en las actividades de análisis, diseño, desarrollo, pruebas y documentación del proyecto.

### Integrantes

1. LINCOL WERNER YUJRA CALLA
2. GUIDO DANIEL MAYTA SUAÑA
3. RONALD CONDORI OLAZABAL
4. DILWERT JHONATAN AGUILAR PAJA

**Nombre del proyecto:** Sistema de Gestión para el Acopio de Leche

**Equipo:** 4bytes

**Curso:** Ingeniería de Software 1

---

# 21. Estado del proyecto

**Estado actual:** 🟡 En desarrollo

### Progreso

* [x] Conformación del grupo.
* [x] Definición del nombre del proyecto.
* [x] Identificación del problema.
* [x] Propuesta de solución.
* [x] Definición preliminar de funcionalidades.
* [x] Comparación inicial de tecnologías móviles.
* [x] Selección preliminar de Flutter.
* [ ] Levantamiento detallado de requisitos.
* [ ] Requisitos funcionales.
* [ ] Requisitos no funcionales.
* [ ] Historias de usuario.
* [ ] Casos de uso.
* [ ] Diagrama de clases.
* [ ] Diagramas de secuencia.
* [ ] Diseño de base de datos.
* [ ] Diseño de arquitectura.
* [ ] Desarrollo de la aplicación.
* [ ] Desarrollo del backend.
* [ ] Integración con base de datos.
* [ ] Pruebas.
* [ ] Documentación final.
* [ ] Presentación del proyecto.

---

#  22. Documentación y fuentes

La documentación del proyecto se desarrollará progresivamente durante las diferentes etapas de Ingeniería de Software.

Las principales fuentes técnicas utilizadas para la comparación de tecnologías son:

### Kotlin Multiplatform

JetBrains. (2026). *What is Kotlin Multiplatform*. Kotlin Documentation.

[Kotlin Multiplatform Documentation](https://kotlinlang.org/docs/multiplatform/kmp-overview.html?utm_source=chatgpt.com)

### Flutter

Google. (2026). *Flutter documentation*. Flutter.

[Flutter Documentation](https://docs.flutter.dev/?utm_source=chatgpt.com)

### React Native

Meta Open Source. (2026). *React Native*. React Native.

[React Native Documentation](https://reactnative.dev/?utm_source=chatgpt.com)

---

# 23. Propósito académico

El proyecto **4bytes** se desarrolla con fines académicos para aplicar los conocimientos adquiridos en el curso de **Ingeniería de Software 1**.

El proyecto contempla las principales etapas del desarrollo de software:

```text
Identificación del problema
          ↓
Análisis
          ↓
Requisitos
          ↓
Diseño
          ↓
Implementación
          ↓
Pruebas
          ↓
Documentación
          ↓
Producto final
```

El sistema podrá evolucionar conforme avance el proyecto y se obtenga información más detallada sobre los procesos reales de la empresa.

---

#  24. Resumen del proyecto

| Aspecto                        | Descripción                                                        |
| ------------------------------ | ------------------------------------------------------------------ |
| **Proyecto**                   | 4bytes                                                             |
| **Aplicación**                 | Sistema de Gestión para el Acopio de Leche                         |
| **Problema**                   | Información manual y dispersa durante el proceso de acopio         |
| **Solución**                   | Aplicación móvil para centralizar y controlar la información       |
| **Usuarios**                   | Administrador, recolector, responsable de planta y administrativo  |
| **Proceso**                    | Recolección → Traslado → Recepción → Calidad → Incidencias → Pagos |
| **Tecnología móvil propuesta** | Flutter                                                            |
| **Lenguaje**                   | Dart                                                               |
| **Base de datos**              | MySQL                                                              |
| **Repositorio**                | GitHub                                                             |
| **Control de versiones**       | Git                                                                |
| **Curso**                      | Ingeniería de Software 1                                           |
| **Estado**                     | En desarrollo                                                      |

---

##  25. Conclusión

**4bytes** propone una solución tecnológica para mejorar la gestión del proceso de acopio de leche mediante la digitalización y centralización de la información.

La aplicación permitirá realizar un seguimiento de las operaciones desde el productor y la recolección en campo hasta la recepción en planta, incluyendo el traslado, control de calidad, registro de incidencias, pagos y generación de reportes.

Con esta solución se busca reducir la dependencia de registros manuales, mejorar la organización de la información, facilitar la trazabilidad del proceso y proporcionar información útil para la gestión y toma de decisiones.

---

**© 2026 — Equipo 4bytes**
**Proyecto académico — Ingeniería de Software 1**
