package cl.duoc.proyectosemestralapp.data.repositorio

import cl.duoc.proyectosemestralapp.data.remoto.ConfiguracionServidor
import cl.duoc.proyectosemestralapp.data.remoto.ServicioProductos
import cl.duoc.proyectosemestralapp.data.remoto.ServicioMovimientos
import cl.duoc.proyectosemestralapp.data.remoto.ServicioVentas
import cl.duoc.proyectosemestralapp.data.remoto.MovimientoPeticion
import cl.duoc.proyectosemestralapp.data.remoto.MovimientoRemoto
import cl.duoc.proyectosemestralapp.data.remoto.ProductoPeticion
import cl.duoc.proyectosemestralapp.data.remoto.ProductoRemoto
import cl.duoc.proyectosemestralapp.data.remoto.VentaPeticion
import cl.duoc.proyectosemestralapp.data.remoto.VentaRemota
import cl.duoc.proyectosemestralapp.dominio.DatosProducto
import cl.duoc.proyectosemestralapp.dominio.ReglasInventario

class ServidorRepositorio(
    private val servicioProductos: ServicioProductos = ConfiguracionServidor.productos,
    private val servicioMovimientos: ServicioMovimientos = ConfiguracionServidor.movimientos,
    private val servicioVentas: ServicioVentas = ConfiguracionServidor.ventas
) {
    suspend fun listarProductos(): List<ProductoRemoto> {
        return servicioProductos.listar()
    }

    suspend fun listarMovimientos(): List<MovimientoRemoto> {
        return servicioMovimientos.listar()
    }

    suspend fun listarVentas(): List<VentaRemota> {
        return servicioVentas.listar()
    }

    fun validarDatos(datos: DatosProducto): String? {
        return ReglasInventario.validarProducto(datos)
    }

    suspend fun guardarProducto(datos: DatosProducto, id: Long?) {
        val peticion = ProductoPeticion(
            codigo = datos.codigo.trim(),
            nombre = datos.nombre.trim(),
            descripcion = datos.descripcion.trim(),
            categoria = datos.categoria.trim(),
            precio = datos.precio.toInt(),
            stock = datos.stock.toInt(),
            stockMinimo = datos.stockMinimo.toInt()
        )
        if (id == null) {
            servicioProductos.crear(peticion)
        } else {
            servicioProductos.actualizar(id, peticion)
        }
    }

    suspend fun eliminarProducto(id: Long) {
        servicioProductos.eliminar(id)
    }

    suspend fun registrarMovimiento(id: Long, cantidad: Int, tipo: String) {
        servicioMovimientos.registrar(MovimientoPeticion(id, tipo, cantidad))
    }

    suspend fun registrarVenta(id: Long, cantidad: Int) {
        servicioVentas.registrar(VentaPeticion(id, cantidad))
    }
}
