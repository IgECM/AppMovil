package cl.duoc.proyectosemestralapp.data.remoto

data class ProductoRemoto(
    val id: Long,
    val codigo: String,
    val nombre: String,
    val descripcion: String,
    val categoria: String,
    val precio: Int,
    val stock: Int,
    val stockMinimo: Int
)

data class ProductoPeticion(
    val codigo: String,
    val nombre: String,
    val descripcion: String,
    val categoria: String,
    val precio: Int,
    val stock: Int,
    val stockMinimo: Int
)

data class MovimientoRemoto(
    val id: Long,
    val productoId: Long,
    val codigoProducto: String,
    val tipo: String,
    val cantidad: Int,
    val stockAnterior: Int,
    val stockPosterior: Int,
    val fechaRegistro: String
)

data class MovimientoPeticion(
    val productoId: Long,
    val tipo: String,
    val cantidad: Int
)

data class VentaRemota(
    val id: Long,
    val productoId: Long,
    val codigoProducto: String,
    val cantidad: Int,
    val precioUnitario: Int,
    val total: Long,
    val fechaRegistro: String
)

data class VentaPeticion(
    val productoId: Long,
    val cantidad: Int
)
