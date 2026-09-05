package com.example.acopiodeleche.domain.model

/**
 * Datos de prueba para la UI mientras no existe base de datos SQLDelight.
 * Se reemplazarán por Room/SQLDelight en la sesión de persistencia.
 *
 * IMPORTANTE: Es un objeto mutable compartido — se inicializa una sola vez.
 */
object DatosMock {

    // ----------------------------------------------------------------
    // USUARIOS DE PRUEBA
    // ----------------------------------------------------------------
    val usuarios = mutableListOf(
        Usuario(
            id = "u-admin",
            nombres = "Administrador",
            apellidos = "Sistema",
            dni = "00000000",
            telefono = "999999999",
            correo = "admin@ecolacteos.com",
            contrasena = "admin123",
            rol = Rol.ADMINISTRADOR,
            estado = true
        ),
        Usuario(
            id = "u-gerente",
            nombres = "Gerente",
            apellidos = "Huata",
            dni = "11111111",
            telefono = "988888888",
            correo = "gerente@ecolacteos.com",
            contrasena = "gerente123",
            rol = Rol.GERENTE,
            estado = true
        ),
        Usuario(
            id = "u-acopiador-01",
            nombres = "Carlos",
            apellidos = "Mamani Flores",
            dni = "22222222",
            telefono = "977777777",
            correo = "carlos@ecolacteos.com",
            contrasena = "acopio123",
            rol = Rol.ACOPIADOR,
            estado = true
        ),
        Usuario(
            id = "u-productor-01",
            nombres = "Pedro",
            apellidos = "Quispe Mamani",
            dni = "72456789",
            telefono = "987654321",
            correo = "pedro@productor.com",
            contrasena = "pedro123",
            rol = Rol.PRODUCTOR,
            estado = true,
            idProductor = "p-01"
        ),
        Usuario(
            id = "u-calidad-01",
            nombres = "Ana",
            apellidos = "Torres Quispe",
            dni = "33333333",
            telefono = "966666666",
            correo = "calidad@ecolacteos.com",
            contrasena = "calidad123",
            rol = Rol.CONTROL_CALIDAD,
            estado = true
        )
    )

    // ----------------------------------------------------------------
    // PRODUCTORES
    // ----------------------------------------------------------------
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

    // ----------------------------------------------------------------
    // VEHÍCULOS
    // ----------------------------------------------------------------
    val vehiculos = listOf(
        "Motocarga 01",
        "Motocarga 02",
        "Furgón 01",
        "Furgón 02"
    )

    // ----------------------------------------------------------------
    // REGISTROS DE ACOPIO
    // ----------------------------------------------------------------
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

    // ----------------------------------------------------------------
    // PAGOS DE PRUEBA (basado en ticket real Ecolácteos Huata)
    // ----------------------------------------------------------------
    val pagos = mutableListOf(
        Pago(
            id = "pago-01",
            idProductor = "p-01",
            codigoPago = "H-11",
            periodoDesde = "27/08/2026",
            periodoHasta = "02/09/2026",
            precioPorLitro = 1.70,
            litrosTotales = 89.5,
            detalleDiario = mapOf(
                "Jueves" to 24.0,
                "Viernes" to 10.0,
                "Sábado" to 9.0,
                "Domingo" to 10.0,
                "Lunes" to 8.0,
                "Martes" to 8.5,
                "Miércoles" to 20.0
            ),
            estado = EstadoPago.PAGADO,
            fechaPago = "03/09/2026"
        )
    )

    // ----------------------------------------------------------------
    // NOTIFICACIONES DE PRUEBA
    // ----------------------------------------------------------------
    val notificaciones = mutableListOf(
        Notificacion(
            id = "n-01",
            titulo = "Bienvenidos a Ecolácteos Huata",
            mensaje = "Estimados productores, les damos la bienvenida al sistema digital de gestión.",
            tipo = TipoNotificacion.AVISO_GENERAL,
            fecha = "01/09/2026",
            hora = "08:00",
            leida = false
        ),
        Notificacion(
            id = "n-02",
            titulo = "Precio del litro actualizado",
            mensaje = "A partir del 01/09/2026 el precio por litro es S/ 1.70.",
            tipo = TipoNotificacion.CAMBIO_PRECIO,
            fecha = "01/09/2026",
            hora = "09:00",
            leida = true
        )
    )
}
