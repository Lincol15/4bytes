package com.example.acopiodeleche.domain.model

/**
 * Datos de prueba para la UI mientras no existe base de datos.
 * Se reemplazarán por Room/SQLDelight en la sesión de persistencia.
 */
object DatosMock {

    val productores = mutableListOf(
        Productor(
            idProductor = "p-01",
            nombres = "Pedro",
            apellidos = "Quispe Mamani",
            dni = "72456789",
            telefono = "987654321",
            direccion = "Jr. Los Álamos 123",
            comunidad = "Comunidad A",
            estado = true
        ),
        Productor(
            idProductor = "p-02",
            nombres = "María",
            apellidos = "Condori Flores",
            dni = "65432198",
            telefono = "976543210",
            direccion = "Av. Principal 456",
            comunidad = "Comunidad B",
            estado = true
        ),
        Productor(
            idProductor = "p-03",
            nombres = "Juan",
            apellidos = "Huanca Ticona",
            dni = "54321987",
            telefono = "965432109",
            direccion = "Calle Nueva 789",
            comunidad = "Comunidad A",
            estado = false
        ),
        Productor(
            idProductor = "p-04",
            nombres = "Rosa",
            apellidos = "Mamani Apaza",
            dni = "43219876",
            telefono = "954321098",
            direccion = "Pasaje Los Pinos 12",
            comunidad = "Comunidad C",
            estado = true
        )
    )

    val vehiculos = listOf(
        "Motocarga 01",
        "Motocarga 02",
        "Furgón 01",
        "Furgón 02"
    )

    val registrosAcopio = mutableListOf(
        RegistroAcopio(
            id = "a-01",
            idProductor = "p-01",
            acopiador = "Carlos Mamani",
            vehiculo = "Motocarga 01",
            zona = "Comunidad A",
            fecha = "30/08/2026",
            hora = "07:30",
            litros = 35.0,
            observacion = "Leche en buenas condiciones",
            recibido = true
        ),
        RegistroAcopio(
            id = "a-02",
            idProductor = "p-02",
            acopiador = "Carlos Mamani",
            vehiculo = "Motocarga 01",
            zona = "Comunidad B",
            fecha = "30/08/2026",
            hora = "08:15",
            litros = 42.5,
            recibido = false
        ),
        RegistroAcopio(
            id = "a-03",
            idProductor = "p-04",
            acopiador = "Luis Quispe",
            vehiculo = "Furgón 01",
            zona = "Comunidad C",
            fecha = "30/08/2026",
            hora = "06:45",
            litros = 28.0,
            observacion = "Leche fría",
            recibido = true
        )
    )
}
