# 4bytes — Sistema de Gestión para el Acopio de Leche

> Aplicación multiplataforma para gestionar y controlar el proceso de acopio de leche desde la recolección de los productores hasta su recepción en planta.

## Problema que resuelve

La empresa dedicada al acopio de leche trabaja con productores de la zona y realiza actividades de recolección, traslado y recepción de la leche. Parte de esta información se registra manualmente o se encuentra dispersa, dificultando el control de los productores, las cantidades recolectadas, las rutas y las incidencias durante el proceso.

4bytes busca centralizar esta información y facilitar el seguimiento de la leche recolectada en campo hasta su llegada y recepción en la planta.

## Público objetivo

La aplicación está dirigida a empresas o centros de acopio de leche que trabajan con productores de la zona.

Los principales usuarios serán:

* Personal encargado de la recolección.
* Personal encargado de la recepción en planta.
* Administradores del centro de acopio.
* Responsables del control de calidad.

La aplicación podrá utilizarse principalmente durante las actividades de recolección en campo y recepción de la leche en planta.

## Funcionalidades previstas

* **F1:** Registrar, listar, editar y eliminar productores.
* **F2:** Registrar y consultar las entregas de leche realizadas por cada productor.
* **F3:** Registrar las cantidades de leche recolectadas durante cada jornada.
* **F4:** Registrar y consultar las rutas de recolección.
* **F5:** Registrar la recepción de leche en la planta.
* **F6:** Comparar la cantidad recolectada con la cantidad recibida en planta.
* **F7:** Registrar incidencias ocurridas durante la recolección o traslado.
* **F8:** Registrar información básica del control de calidad.
* **F9:** Permitir el inicio de sesión de los usuarios.
* **F10:** Permitir trabajar sin conexión a Internet y sincronizar la información cuando vuelva la conexión.
* **F11:** Consultar el historial de entregas y recolecciones.
* **F12:** Mostrar información básica sobre las cantidades recolectadas y recibidas.

## Entidad principal del CRUD

### Productor

La entidad principal del CRUD será **Productor**, debido a que representa a las personas que entregan leche al centro de acopio.

### Atributos tentativos

* `idProductor`
* `nombres`
* `apellidos`
* `dni`
* `telefono`
* `direccion`
* `comunidad`
* `estado`

La entidad **Productor** estará relacionada posteriormente con las entregas y registros de recolección realizados durante el proceso de acopio.

## Capacidad nativa prevista

###  Ubicación

La aplicación utilizará la ubicación del dispositivo para registrar o consultar la ubicación de los puntos de recolección y apoyar el seguimiento de las rutas.

Esta capacidad permitirá relacionar una recolección con su ubicación y facilitar el control de las actividades realizadas en campo.

## Equipo 4bytes

| Integrante                    | Código        | Rol semana 1       |
| ----------------------------- | ------------- | ------------------ |
| LINCOL WERNER YUJRA CALLA     | Por completar | Coordinación       |
| GUIDO DANIEL MAYTA SUAÑA      | Por completar | Lógica y datos     |
| RONALD CONDORI OLAZABAL       | Por completar | UI                 |
| DILWER JHONATAN AGUILAR PAJA | Por completar | QA y documentación |

## Tecnologías

* **Kotlin Multiplatform**
* **Compose Multiplatform**
* **Kotlin**
* **Android**
* **Desktop**
* **Git**
* **GitHub**

La aplicación será desarrollada utilizando Kotlin Multiplatform y Compose Multiplatform, de acuerdo con las tecnologías establecidas para el proyecto.

### Targets

* Android
* Desktop

> iOS preparado: requiere macOS para compilar.
