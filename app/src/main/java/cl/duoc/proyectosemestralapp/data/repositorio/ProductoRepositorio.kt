package cl.duoc.proyectosemestralapp.data.repositorio

import androidx.room.withTransaction
import cl.duoc.proyectosemestralapp.data.local.AppDatabase
import cl.duoc.proyectosemestralapp.data.local.MovimientoEntity
import cl.duoc.proyectosemestralapp.data.local.ProductoEntity
import cl.duoc.proyectosemestralapp.dominio.DatosProducto
import cl.duoc.proyectosemestralapp.dominio.ReglasInventario

class ProductoRepositorio(private val baseDatos: AppDatabase) {
    private val productosDao = baseDatos.productoDao()
    private val movimientosDao = baseDatos.movimientoDao()

    val productos = productosDao.obtenerProductos()
    val movimientos = movimientosDao.obtenerMovimientos()

    suspend fun guardarProducto(datos: DatosProducto): String? {
        ReglasInventario.validarProducto(datos)?.let { return it }
        return try {
            baseDatos.withTransaction {
                val codigo = datos.codigo.trim()
                if (productosDao.buscarPorCodigo(codigo) != null) {
                    return@withTransaction "El código ya está registrado"
                }
                productosDao.insertarProducto(
                    ProductoEntity(
                        codigo = codigo,
                        nombre = datos.nombre.trim(),
                        descripcion = datos.descripcion.trim(),
                        categoria = datos.categoria.trim(),
                        precio = datos.precio.toInt(),
                        stock = datos.stock.toInt(),
                        stockMinimo = datos.stockMinimo.toInt()
                    )
                )
                null
            }
        } catch (excepcion: Exception) {
            "No se pudo guardar el producto: ${excepcion.localizedMessage ?: "error desconocido"}"
        }
    }

    suspend fun registrarMovimiento(productoId: Int, cantidad: Int, esEntrada: Boolean): String? {
        return try {
            baseDatos.withTransaction {
                val producto = productosDao.buscarPorId(productoId)
                    ?: return@withTransaction "El producto ya no existe"
                val stockPosterior = ReglasInventario.calcularStock(producto.stock, cantidad, esEntrada)
                    ?: return@withTransaction "Cantidad inválida o stock insuficiente"
                productosDao.actualizarProducto(producto.copy(stock = stockPosterior))
                movimientosDao.insertarMovimiento(
                    MovimientoEntity(
                        productoId = producto.id,
                        codigoProducto = producto.codigo,
                        tipo = if (esEntrada) "ENTRADA" else "SALIDA",
                        cantidad = cantidad,
                        stockAnterior = producto.stock,
                        stockPosterior = stockPosterior,
                        fechaRegistro = System.currentTimeMillis()
                    )
                )
                null
            }
        } catch (excepcion: Exception) {
            "No se pudo registrar el movimiento: ${excepcion.localizedMessage ?: "error desconocido"}"
        }
    }
}
