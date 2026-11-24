package cl.duoc.proyectosemestralapp.dominio

data class DatosProducto(
    val codigo: String,
    val nombre: String,
    val descripcion: String,
    val categoria: String,
    val precio: String,
    val stock: String,
    val stockMinimo: String
)

object ReglasInventario {
    fun validarProducto(datos: DatosProducto): String? {
        if (datos.codigo.isBlank()) return "El código es obligatorio"
        if (datos.nombre.isBlank()) return "El nombre es obligatorio"
        if (datos.categoria.isBlank()) return "La categoría es obligatoria"
        val precio = datos.precio.toIntOrNull()
        if (precio == null || precio < 0) return "El precio debe ser un número válido y no negativo"
        val stock = datos.stock.toIntOrNull()
        if (stock == null || stock < 0) return "El stock debe ser un número válido y no negativo"
        val stockMinimo = datos.stockMinimo.toIntOrNull()
        if (stockMinimo == null || stockMinimo < 0) return "El stock mínimo debe ser un número válido y no negativo"
        return null
    }

    
    fun calcularStock(stockActual: Int, cantidad: Int, esEntrada: Boolean): Int? {
        if (stockActual < 0 || cantidad <= 0) return null
        if (esEntrada) {
            if (cantidad > Int.MAX_VALUE - stockActual) return null
            return stockActual + cantidad
        }
        if (cantidad > stockActual) return null
        return stockActual - cantidad
    }
}
