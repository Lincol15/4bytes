#  4bytes

## Sistema de Gestión para el Acopio de Leche

Sistema de software orientado a la **gestión y control del proceso de acopio de leche**, desde la recolección realizada a los productores de la zona hasta la recepción y control de la leche en la planta.

> **Proyecto académico — Ingeniería de Software 1**

---

##  1. Descripción del proyecto

**4bytes** es un proyecto de software desarrollado para apoyar la gestión de una empresa dedicada al **acopio de leche**, que trabaja directamente con productores de una zona determinada.

El proceso de acopio comprende diferentes actividades, entre ellas:

1. Registro de productores.
2. Planificación y seguimiento de la recolección.
3. Recolección de leche en los puntos establecidos.
4. Registro de cantidades recolectadas.
5. Traslado de la leche hacia la planta.
6. Recepción de la leche.
7. Control de calidad.
8. Registro de incidencias.
9. Consolidación de la información.
10. Gestión administrativa y generación de reportes.

Actualmente, parte de esta información puede encontrarse registrada de manera manual o distribuida en diferentes medios, dificultando el seguimiento completo del proceso.

Por ello, **4bytes** propone centralizar la información mediante un sistema que permita registrar, consultar, controlar y analizar los datos generados durante el proceso de acopio.

---

#  2. Problemática identificada

La empresa realiza actividades relacionadas con la recolección y recepción de leche provenientes de diferentes productores de la zona. Sin embargo, parte de la gestión de estas actividades se realiza mediante registros manuales o información dispersa.

Esta situación puede generar dificultades para:

* Mantener actualizada la información de los productores.
* Controlar las cantidades de leche recolectadas.
* Realizar un seguimiento de las rutas de recolección.
* Comparar la cantidad recolectada en campo con la cantidad recibida en planta.
* Registrar y consultar incidencias durante el proceso.
* Mantener un historial organizado de las entregas.
* Realizar un seguimiento adecuado del control de calidad.
* Obtener información consolidada para la toma de decisiones.
* Gestionar correctamente la información administrativa relacionada con el acopio.

Como consecuencia, la empresa puede presentar dificultades para conocer de manera rápida y organizada **qué productor entregó leche, cuánto se recolectó, cuándo y dónde se realizó la recolección, qué cantidad llegó a la planta y qué incidencias ocurrieron durante el proceso**.

---

#  3. Propuesta de solución

Se propone desarrollar un **Sistema de Gestión para el Acopio de Leche** que permita centralizar la información generada durante el proceso.

El sistema permitirá registrar y relacionar la información de:

* Productores.
* Puntos de recolección.
* Rutas.
* Recolecciones.
* Cantidades de leche.
* Traslados.
* Recepciones en planta.
* Controles de calidad.
* Incidencias.
* Pagos.
* Reportes administrativos.

De esta manera, se busca disponer de una fuente de información organizada que permita realizar un seguimiento desde la **recolección en campo hasta la recepción en planta**.

---

#  4. Objetivos

## 4.1 Objetivo general

Desarrollar un sistema de gestión que permita **registrar, centralizar y controlar la información relacionada con el proceso de acopio de leche**, desde la recolección a los productores hasta la recepción y control en planta.

## 4.2 Objetivos específicos

* Registrar y administrar la información de los productores.
* Registrar los puntos y rutas de recolección.
* Registrar las cantidades de leche recolectadas.
* Asociar cada recolección con su productor, ruta y responsable correspondiente.
* Registrar la información relacionada con el traslado de la leche.
* Registrar la recepción de leche en la planta.
* Comparar las cantidades recolectadas en campo con las cantidades recibidas en planta.
* Registrar los controles de calidad realizados.
* Registrar incidencias ocurridas durante la recolección y traslado.
* Mantener un historial de las operaciones realizadas.
* Facilitar la generación de reportes para la gestión administrativa.
* Facilitar la consulta de información para apoyar la toma de decisiones.

---

#  5. Usuarios del sistema

El sistema estará orientado a los diferentes actores que participan en el proceso de acopio.

###  Administrador

Responsable de la administración general del sistema.

**Funciones principales:**

* Gestionar usuarios.
* Gestionar productores.
* Consultar información general.
* Administrar parámetros del sistema.
* Consultar reportes.

###  Responsable de recolección

Encargado de realizar o registrar las actividades de recolección.

**Funciones principales:**

* Consultar rutas asignadas.
* Registrar recolecciones.
* Registrar cantidades recolectadas.
* Registrar incidencias.
* Consultar información de productores.

###  Responsable de planta

Encargado de recibir y controlar la leche que llega a la planta.

**Funciones principales:**

* Registrar recepción.
* Registrar cantidad recibida.
* Registrar controles de calidad.
* Registrar observaciones.
* Registrar incidencias detectadas en la recepción.

###  Responsable administrativo

Encargado de la información económica relacionada con los productores.

**Funciones principales:**

* Consultar entregas.
* Consultar cantidades acumuladas.
* Calcular montos correspondientes.
* Registrar pagos.
* Generar reportes administrativos.

---

# ⚙️ 6. Módulos del sistema

## 6.1 Gestión de productores

Permitirá administrar la información de las personas que proporcionan la leche.

**Funciones:**

* Registrar productor.
* Modificar productor.
* Consultar productor.
* Buscar productor.
* Activar o desactivar productor.
* Consultar historial de entregas.

---

## 6.2 Gestión de rutas

Permitirá organizar las rutas utilizadas para realizar la recolección.

**Funciones:**

* Registrar rutas.
* Modificar rutas.
* Consultar rutas.
* Asociar productores a una ruta.
* Registrar responsable de la ruta.
* Consultar historial de rutas.

---

## 6.3 Gestión de recolección

Permitirá registrar la leche recolectada directamente en campo.

**Información considerada:**

* Productor.
* Fecha.
* Hora.
* Ruta.
* Responsable.
* Cantidad recolectada.
* Observaciones.

---

## 6.4 Gestión de traslado

Permitirá registrar el traslado de la leche desde los puntos de recolección hasta la planta.

**Información considerada:**

* Ruta.
* Responsable.
* Vehículo.
* Fecha y hora de salida.
* Fecha y hora de llegada.
* Cantidad transportada.
* Observaciones.
* Incidencias.

---

## 6.5 Recepción en planta

Permitirá registrar la llegada de la leche a la planta.

El sistema permitirá relacionar la información de la recolección con la recepción para facilitar el seguimiento de las cantidades.

Por ejemplo:

```text
Productor
    ↓
Recolección en campo
    ↓
Cantidad recolectada
    ↓
Traslado
    ↓
Recepción en planta
    ↓
Cantidad recibida
    ↓
Control de calidad
```

---

## 6.6 Control de calidad

Permitirá registrar los controles realizados sobre la leche recibida.

**Información considerada:**

* Fecha y hora.
* Lote o recepción.
* Parámetros de calidad.
* Resultado.
* Observaciones.
* Estado de aceptación o rechazo.

---

## 6.7 Gestión de incidencias

Permitirá registrar situaciones que puedan ocurrir durante el proceso.

Algunos ejemplos son:

* Retraso en la recolección.
* Problemas con el vehículo.
* Derrame de leche.
* Diferencia de cantidades.
* Problemas durante el traslado.
* Problemas detectados durante la recepción.
* Rechazo de leche por control de calidad.

Cada incidencia podrá registrar:

* Fecha.
* Tipo.
* Descripción.
* Lugar.
* Responsable.
* Estado.
* Observaciones.

---

## 6.8 Gestión de pagos

Permitirá administrar la información relacionada con los pagos a los productores.

**Funciones:**

* Registrar precio por litro.
* Calcular monto por entrega.
* Consultar monto acumulado.
* Registrar pagos.
* Consultar pagos pendientes.
* Generar historial de pagos.

---

#  7. Reportes

El sistema permitirá obtener información consolidada para facilitar la gestión.

Entre los reportes previstos se encuentran:

### Reportes de producción

* Litros recolectados por día.
* Litros recolectados por semana.
* Litros recolectados por mes.
* Producción por productor.
* Producción por ruta.

### Reportes de recepción

* Cantidad recolectada.
* Cantidad recibida.
* Diferencia entre campo y planta.
* Recepciones aceptadas.
* Recepciones rechazadas.

### Reportes administrativos

* Productores activos.
* Historial de entregas.
* Montos por productor.
* Pagos realizados.
* Pagos pendientes.

### Reportes de incidencias

* Incidencias por fecha.
* Incidencias por ruta.
* Incidencias por tipo.
* Incidencias pendientes de resolver.

---

#  8. Información principal del sistema

El sistema manejará diferentes entidades relacionadas con el proceso de acopio.

```text
PRODUCTOR
    │
    ├── ENTREGAS
    │       │
    │       └── RECOLECCIÓN
    │
    └── PAGOS

RUTA
    │
    ├── PRODUCTORES
    │
    └── RECOLECCIÓN
             │
             ↓
          TRASLADO
             │
             ↓
       RECEPCIÓN PLANTA
             │
             ├── CONTROL DE CALIDAD
             │
             └── INCIDENCIAS
```

La estructura definitiva será determinada durante la etapa de análisis y diseño de la base de datos.

---

#  9. Requisitos no funcionales preliminares

Además de las funcionalidades principales, el sistema deberá considerar características de calidad.

### Seguridad

El sistema deberá controlar el acceso mediante usuarios y roles.

### Disponibilidad

La información deberá estar disponible para los usuarios autorizados cuando sea requerida.

### Integridad

El sistema deberá evitar registros inconsistentes o incompletos.

### Usabilidad

La interfaz deberá ser sencilla y permitir que los usuarios puedan registrar información de manera rápida.

### Mantenibilidad

El sistema deberá estar organizado de manera que facilite futuras modificaciones y ampliaciones.

### Trazabilidad

Las operaciones importantes deberán poder relacionarse con el usuario, fecha y proceso correspondiente.

---

#  10. Tecnologías

Las tecnologías consideradas para el desarrollo son:

| Tecnología | Utilización                |
| ---------- | -------------------------- |
| **Java**   | Desarrollo del sistema     |
| **MySQL**  | Gestión de base de datos   |
| **Git**    | Control de versiones       |
| **GitHub** | Repositorio y colaboración |
| **UML**    | Modelado del sistema       |

> Las tecnologías adicionales serán incorporadas conforme avance el desarrollo.

---

#  11. Arquitectura del sistema

La arquitectura definitiva será definida durante la etapa de diseño.

De manera preliminar, se plantea una separación por capas:

```text
┌─────────────────────────────┐
│       PRESENTACIÓN          │
│   Interfaz del sistema      │
└──────────────┬──────────────┘
               │
┌──────────────▼──────────────┐
│       LÓGICA DE NEGOCIO     │
│ Reglas y procesos del       │
│ sistema de acopio           │
└──────────────┬──────────────┘
               │
┌──────────────▼──────────────┐
│       ACCESO A DATOS        │
│   Persistencia y consultas  │
└──────────────┬──────────────┘
               │
┌──────────────▼──────────────┐
│          MySQL              │
│       Base de datos         │
└─────────────────────────────┘
```

---

# 12. Control de versiones

El proyecto utilizará **Git y GitHub** para facilitar el trabajo colaborativo.

Se propone una estructura de ramas como:

```text
main
│
└── develop
     │
     ├── feature/productores
     ├── feature/rutas
     ├── feature/recoleccion
     ├── feature/recepcion
     ├── feature/calidad
     ├── feature/incidencias
     └── feature/pagos
```

### Ejemplo

```bash
git checkout -b feature/productores
```

```bash
git add .
git commit -m "Implementar gestión de productores"
```

```bash
git push origin feature/productores
```

---

#  13. Estructura preliminar del proyecto

```text
4bytes/
│
├── src/
│   └── ...
│
├── database/
│   ├── scripts/
│   └── ...
│
├── docs/
│   ├── requisitos/
│   ├── uml/
│   ├── arquitectura/
│   └── pruebas/
│
├── README.md
└── ...
```

---

#  14. Documentación del proyecto

Durante el desarrollo se generará documentación relacionada con las diferentes etapas de Ingeniería de Software.

Entre los documentos previstos:

* Documento de visión.
* Requerimientos funcionales.
* Requerimientos no funcionales.
* Historias de usuario.
* Casos de uso.
* Diagrama de casos de uso.
* Diagrama de clases.
* Diagramas de secuencia.
* Modelo entidad-relación.
* Diseño de base de datos.
* Arquitectura del sistema.
* Plan de pruebas.
* Manual de usuario.

---

#  15. Estado del proyecto

**Estado:** 🟡 En desarrollo

### Progreso

* [x] Formación del equipo.
* [x] Definición inicial del problema.
* [x] Definición del proyecto.
* [x] Identificación preliminar de funcionalidades.
* [ ] Levantamiento de requisitos.
* [ ] Requisitos funcionales.
* [ ] Requisitos no funcionales.
* [ ] Historias de usuario.
* [ ] Casos de uso.
* [ ] Diseño de base de datos.
* [ ] Diseño UML.
* [ ] Diseño de arquitectura.
* [ ] Implementación.
* [ ] Pruebas.
* [ ] Documentación final.
* [ ] Presentación del proyecto.

---

#  16. Integrantes

| Integrante                        |
| --------------------------------- |
| **LINCOL WERNER YUJRA CALLA**     |
| **GUIDO DANIEL MAYTA SUAÑA**      |
| **RONALD CONDORI OLAZABAL**       |
| **DILWERT JHONATAN AGUILAR PAJA** |

**Equipo:** 4bytes
**Curso:** Ingeniería de Software 1
**Proyecto:** Sistema de Gestión para el Acopio de Leche

---

#  17. Propósito académico

El proyecto **4bytes** ha sido desarrollado con fines académicos para aplicar los conocimientos adquiridos en el curso de **Ingeniería de Software 1**, considerando las diferentes etapas del desarrollo de software:

```text
Problema
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

El sistema podrá evolucionar durante el desarrollo conforme se obtenga mayor información sobre los procesos reales de la empresa y se definan nuevos requisitos.

---

##  Nota

La información, funcionalidades, arquitectura, tecnologías y estructura descritas en este documento corresponden a una **propuesta inicial** y podrán modificarse durante las etapas de análisis, diseño, implementación y pruebas del proyecto.
