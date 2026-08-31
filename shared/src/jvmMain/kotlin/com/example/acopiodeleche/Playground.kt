package com.example.acopiodeleche
import com.example.acopiodeleche.domain.model.Categoria
import com.example.acopiodeleche.domain.model.RegistroLeche

fun main() {
    // =========================================================
    // ACTIVIDAD 2.2 — Tipo explícito e inferido
    // =========================================================

    // --- Tipo explícito: yo declaro el tipo ---
    val nombreProveedor: String = "Granja San Juan"
    val litrosEntregados: Double = 120.50
    val diasActivo: Int = 15
    val estaActivo: Boolean = true
    val inicial: Char = 'G'

    // --- Tipo inferido: Kotlin lo deduce del valor ---
    val categoria = "Recolección"   // String
    val descuento = 0.05             // Double
    val registrosDia = 3             // Int

    println("Proveedor: $nombreProveedor")
    println("Litros: $litrosEntregados L")
    println("Días activo: $diasActivo")
    println("Activo: $estaActivo | Inicial: $inicial")
    println("Categoría: $categoria | Descuento: $descuento")
    println("Total registros del día: $registrosDia")

    // =========================================================
    // ACTIVIDAD 2.3 — val vs var
    // =========================================================
    val precioPorLitro = 2.50
    // precioPorLitro = 3.00  // <- descomente esta línea y observe el error: Val cannot be reassigned
    var contador = 0
    contador = contador + 1
    contador += 1
    println("Contador: $contador")

    // =========================================================
    // ACTIVIDAD 2.4 — Null-safety
    // =========================================================
    val observacion: String? = null
    val notaProveedor: String? = "Entregar antes de las 6am"

    // ?. llamada segura: si es null, toda la expresión es null
    println(observacion?.length)      // imprime null
    println(notaProveedor?.length)    // imprime el largo

    // ?: operador Elvis: valor por defecto cuando hay null
    val textoMostrado = observacion ?: "Sin observación"
    println(textoMostrado)

    // Combinación frecuente en la UI
    val largo = observacion?.length ?: 0
    println("Largo seguro: $largo")

    // =========================================================
    // ACTIVIDAD 3 — Listas y operaciones sobre colecciones
    // =========================================================

    // 3.1 Lista inmutable y recorridos
    val categorias = listOf("Proveedores", "Recolección", "Procesamiento", "Distribución")
    println("\nCantidad de categorías: ${categorias.size}")
    println("La primera es: ${categorias[0]}")
    println("La última es: ${categorias[categorias.size - 1]}")

    for (cat in categorias) {
        println("- $cat")
    }

    categorias.forEachIndexed { indice, valor ->
        println("$indice -> $valor")
    }

    // 3.2 Índice seguro (evita IndexOutOfBoundsException)
    println(categorias.getOrNull(10))   // imprime null en lugar de fallar
    println(categorias.firstOrNull())   // el primero, o null si la lista está vacía

    // 3.3 Lista mutable
    val registros = mutableListOf<String>()
    registros.add("Granja San Juan")
    registros.add("Granja Los Pinos")
    registros.add("Granja El Valle")
    println("Registros: $registros")
    registros.remove("Granja Los Pinos")
    println("Después de quitar: $registros")
    println("Está vacío: ${registros.isEmpty()}")

    // 3.4 Las seis operaciones esenciales
    val litros = listOf(120.50, 45.00, 80.00, 200.50, 30.00, 60.00)
    val proveedores = listOf("Granja San Juan", "Granja Los Pinos", "Granja El Valle", "Hacienda Norte", "Rancho Sur", "Finca Blanca")

    println(litros.filter { it > 50.0 })
    println(proveedores.map { it.uppercase() })
    println("Total litros: ${litros.sumOf { it }}")
    println(proveedores.find { it.startsWith("G") })
    println(litros.sortedDescending())
    println("Grandes entregas: ${litros.count { it > 100 }}")
    println("Promedio: ${litros.average()}")

    // =========================================================
    // ACTIVIDAD 7 — Instanciar RegistroLeche (las 4 formas)
    // =========================================================

    // FORMA 1 — Posicional: el orden debe coincidir con el constructor
    val registro1 = RegistroLeche("r-01", "Granja San Juan", 120.50, Categoria.RECOLECCION)

    // FORMA 2 — Con nombres: el orden no importa y se lee solo  <- RECOMENDADA
    val registro2 = RegistroLeche(
        proveedor = "Granja Los Pinos",
        id = "r-02",
        litros = 45.00,
        categoria = Categoria.PROVEEDOR
    )

    // FORMA 3 — Aprovechando todos los parámetros
    val registro3 = RegistroLeche(
        id = "r-03",
        proveedor = "Hacienda Norte",
        litros = 200.50,
        categoria = Categoria.PROCESAMIENTO,
        observacion = "Leche de alta calidad, bajo en grasa",
        activo = true
    )

    // FORMA 4 — Un registro inactivo, para probar las reglas
    val registro4 = RegistroLeche(
        id = "r-04",
        proveedor = "Rancho Sur",
        litros = 0.0,
        categoria = Categoria.DISTRIBUCION,
        activo = false
    )

    val catalogo = listOf(registro1, registro2, registro3, registro4)
    println("\n=== REGISTROS (${catalogo.size} entradas) ===")
    catalogo.forEach { r ->
        println(r)
    }

    // 7.3 Reporte con propiedades calculadas y operaciones de lista
    println()
    println("=== REPORTE ===")
    catalogo.forEach { r ->
        val estado = if (r.esValido) "VÁLIDO" else "INVÁLIDO"
        println("[$estado] ${r.proveedor} - ${r.litrosFormateados}")
        println("  ${r.observacionCorta}")
    }

    println()
    println("Válidos: ${catalogo.count { it.esValido }}")
    println("Total litros: ${catalogo.sumOf { it.litros }}")
    println("Mayor entrega: ${catalogo.maxByOrNull { it.litros }?.proveedor}")
    println("Solo recolección: ${catalogo.filter { it.categoria == Categoria.RECOLECCION }.map { it.proveedor }}")
    println("Por categoría: ${catalogo.groupBy { it.categoria }.mapValues { it.value.size }}")

    // =========================================================
    // ACTIVIDAD 8 — copy(), igualdad y desestructuración
    // =========================================================
    println()
    println("=== COPY E IGUALDAD ===")

    // copy(): crea un objeto NUEVO cambiando solo lo indicado
    val registroActualizado = registro1.copy(litros = 150.00)
    println("Original: ${registro1.litros}")               // 120.5, intacto
    println("Actualizado: ${registroActualizado.litros}")  // 150.0

    // Igualdad por CONTENIDO, no por dirección de memoria
    val copiaExacta = registro1.copy()
    println("Son iguales: ${registro1 == copiaExacta}") // true

    // Desestructuración: extraer varias propiedades a la vez
    val (identificador, nombreProv, litrosProv) = registro1
    println("$identificador | $nombreProv | $litrosProv L")
}

