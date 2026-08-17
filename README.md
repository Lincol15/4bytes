# 4bytes — Sistema de Gestión para el Acopio de Leche

> Aplicación multiplataforma para gestionar y controlar el proceso de acopio de leche desde la recolección de los productores hasta su recepción en planta.

## Problema que resuelve

La empresa dedicada al acopio de leche trabaja con productores de la zona y realiza actividades de recolección, traslado y recepción de la leche. Actualmente, parte de esta información puede registrarse de forma manual o encontrarse dispersa, dificultando el control de los productores, las cantidades recolectadas, las rutas y las incidencias durante el proceso.

4bytes busca centralizar esta información y facilitar el seguimiento de la leche desde su recolección en campo hasta su recepción en la planta, permitiendo mejorar el control y reducir las diferencias de información durante el proceso.

## Público objetivo

La aplicación está dirigida principalmente a empresas o centros de acopio de leche que trabajan con productores de la zona.

Los principales usuarios serán:

- Personal encargado de la recolección.
- Personal encargado de la recepción en planta.
- Administradores del centro de acopio.
- Responsables del control de calidad.

La aplicación será utilizada principalmente durante las actividades de recolección en campo y recepción de la leche en planta.

## Funcionalidades previstas

- **F1:** Registrar, listar, editar y eliminar productores.
- **F2:** Registrar y consultar las entregas de leche realizadas por cada productor.
- **F3:** Registrar el volumen total de leche recolectado durante cada jornada.
- **F4:** Registrar y consultar las rutas de recolección.
- **F5:** Registrar la recepción de leche en la planta.
- **F6:** Comparar la cantidad de leche recolectada con la cantidad recibida en planta.
- **F7:** Registrar incidencias ocurridas durante la recolección o traslado.
- **F8:** Registrar información básica del control de calidad.
- **F9:** Permitir el inicio de sesión de los usuarios.
- **F10:** Permitir trabajar sin conexión a Internet y sincronizar la información cuando vuelva la conexión.
- **F11:** Consultar el historial de entregas y recolecciones.
- **F12:** Mostrar información básica sobre las cantidades recolectadas y recibidas.

## Entidad principal del CRUD

### Productor

La entidad principal del CRUD será **Productor**, debido a que representa a las personas que entregan leche al centro de acopio.

### Atributos tentativos

- `idProductor`
- `nombres`
- `apellidos`
- `dni`
- `telefono`
- `direccion`
- `comunidad`
- `estado`

La entidad **Productor** estará relacionada con los registros de entrega y recolección realizados durante el proceso de acopio.

## Capacidad nativa prevista

### 📍 GPS / Geolocalización

La aplicación utilizará la ubicación del dispositivo para registrar la ubicación de los puntos de recolección y apoyar el seguimiento de las rutas.

Esta capacidad permitirá relacionar una jornada de recolección con su ubicación y facilitar el control de las actividades realizadas en campo.

## Checklist de viabilidad

| Criterio | Tipo | Cumple |
|---|---|---|
| Tiene una entidad principal clara para el CRUD: Productor | Obligatorio | Sí |
| Requiere usuarios que inician sesión | Obligatorio | Sí |
| Tiene sentido utilizarla sin conexión a Internet | Obligatorio | Sí |
| El alcance es realista para un semestre | Obligatorio | Sí |
| Tiene entre 4 y 6 pantallas principales | Obligatorio | Sí |
| Cuenta con 1 o 2 entidades secundarias como máximo | Obligatorio | Sí |
| Existe un usuario real para validar la propuesta | Recomendado | Sí |
| Utiliza una capacidad nativa: GPS / ubicación | Recomendado | Sí |

## Pantallas principales previstas

La aplicación tendrá inicialmente entre 4 y 6 pantallas principales:

1. **Inicio de sesión**
2. **Panel principal**
3. **Gestión de productores**
4. **Registro de recolección**
5. **Rutas y ubicación**
6. **Recepción en planta**

Las demás funcionalidades se integrarán dentro de estas pantallas para mantener un alcance realista durante el semestre.

## Flujo general de la aplicación

El proceso principal de 4bytes seguirá el siguiente flujo:

**Productor → Recolección → Ruta → Traslado → Recepción en planta → Control de calidad → Incidencias → Historial**

El sistema permitirá registrar la información durante la recolección y posteriormente comparar la cantidad recolectada con la cantidad recibida en planta.

## Alcance inicial

El proyecto se desarrollará progresivamente durante las diferentes unidades del curso.

El alcance inicial comprende:

1. Gestión de productores.
2. Registro de entregas y recolecciones.
3. Control de cantidades recolectadas.
4. Gestión básica de rutas.
5. Registro de recepción en planta.
6. Registro de incidencias.
7. Control básico de calidad.
8. Funcionamiento sin conexión y sincronización posterior.
9. Inicio de sesión de usuarios.
10. Consulta del historial de operaciones.

El proyecto se mantendrá dentro de un alcance realista para su desarrollo durante el semestre.

## Equipo 4bytes

| Integrante | Código | Rol semana 1 |
|---|---|---|
| **LINCOL WERNER YUJRA CALLA** | Por completar | Coordinación |
| **GUIDO DANIEL MAYTA SUAÑA** | Por completar | Lógica y datos |
| **RONALD CONDORI OLAZABAL** | Por completar | UI |
| **DILWERT JHONATAN AGUILAR PAJA** | Por completar | QA y documentación |

## Tecnologías

- **Kotlin Multiplatform**
- **Compose Multiplatform**
- **Kotlin**
- **Android**
- **Desktop**
- **Git**
- **GitHub**

La aplicación será desarrollada utilizando **Kotlin Multiplatform** y **Compose Multiplatform**, permitiendo compartir código entre diferentes plataformas.

### Targets

- Android
- Desktop

> iOS queda preparado como posibilidad futura, pero su compilación requiere un entorno macOS.

## Repositorio

El proyecto será gestionado mediante Git y GitHub utilizando una estrategia de trabajo basada en ramas.

- `main`: rama principal y estable.
- `develop`: rama de desarrollo.

Los cambios realizados durante las siguientes semanas serán integrados progresivamente mediante `pull requests`.

## Objetivo del proyecto

El objetivo de 4bytes es desarrollar una aplicación multiplataforma que permita centralizar y controlar la información relacionada con el acopio de leche, facilitando el seguimiento de los productores, las recolecciones, las rutas, las cantidades transportadas y la recepción de la leche en planta.

La aplicación busca reducir el registro manual y disperso de información, mejorar el control del proceso de acopio y facilitar la consulta de los datos por parte del personal encargado.
