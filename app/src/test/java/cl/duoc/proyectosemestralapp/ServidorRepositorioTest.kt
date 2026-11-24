package cl.duoc.proyectosemestralapp

import cl.duoc.proyectosemestralapp.data.remoto.MovimientoPeticion
import cl.duoc.proyectosemestralapp.data.remoto.MovimientoRemoto
import cl.duoc.proyectosemestralapp.data.remoto.ProductoPeticion
import cl.duoc.proyectosemestralapp.data.remoto.ProductoRemoto
import cl.duoc.proyectosemestralapp.data.remoto.ServicioMovimientos
import cl.duoc.proyectosemestralapp.data.remoto.ServicioProductos
import cl.duoc.proyectosemestralapp.data.remoto.ServicioVentas
import cl.duoc.proyectosemestralapp.data.remoto.VentaPeticion
import cl.duoc.proyectosemestralapp.data.remoto.VentaRemota
import cl.duoc.proyectosemestralapp.data.repositorio.ServidorRepositorio
import cl.duoc.proyectosemestralapp.dominio.DatosProducto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class ServidorRepositorioTest {
    private val servicioProductos = mockk<ServicioProductos>()
    private val servicioMovimientos = mockk<ServicioMovimientos>()
    private val servicioVentas = mockk<ServicioVentas>()
    private val repositorio = ServidorRepositorio(servicioProductos, servicioMovimientos, servicioVentas)

    private fun datos() = DatosProducto(" K-001 ", " Camisa ", " Blanca ", " Ropa ", "5000", "750", "100")
    private fun producto() = ProductoRemoto(1, "K-001", "Camisa", "Blanca", "Ropa", 5000, 750, 100)
    private fun movimiento() = MovimientoRemoto(1, 1, "K-001", "ENTRADA", 5, 750, 755, "2026-11-23")
    private fun venta() = VentaRemota(1, 1, "K-001", 3, 5000, 15000, "2026-11-23")

    @Test
    fun consultaProductos() = runBlocking {
        coEvery { servicioProductos.listar() } returns listOf(producto())
        assertEquals("Camisa", repositorio.listarProductos().first().nombre)
        coVerify(exactly = 1) { servicioProductos.listar() }
    }

    @Test
    fun consultaMovimientos() = runBlocking {
        coEvery { servicioMovimientos.listar() } returns listOf(movimiento())
        assertEquals(755, repositorio.listarMovimientos().first().stockPosterior)
    }

    @Test
    fun consultaVentas() = runBlocking {
        coEvery { servicioVentas.listar() } returns listOf(venta())
        assertEquals(15000L, repositorio.listarVentas().first().total)
    }

    @Test
    fun validaFormularioCorrecto() {
        assertNull(repositorio.validarDatos(datos()))
    }

    @Test
    fun validaFormularioIncompleto() {
        assertNotNull(repositorio.validarDatos(datos().copy(codigo = "")))
    }

    @Test
    fun creaProductoConDatosLimpios() = runBlocking {
        coEvery { servicioProductos.crear(any()) } returns producto()
        repositorio.guardarProducto(datos(), null)
        coVerify(exactly = 1) {
            servicioProductos.crear(match {
                it.codigo == "K-001" && it.nombre == "Camisa" &&
                    it.descripcion == "Blanca" && it.categoria == "Ropa" &&
                    it.precio == 5000 && it.stock == 750 && it.stockMinimo == 100
            })
        }
    }

    @Test
    fun actualizaProductoPorId() = runBlocking {
        coEvery { servicioProductos.actualizar(any(), any()) } returns producto()
        repositorio.guardarProducto(datos(), 7)
        coVerify(exactly = 1) { servicioProductos.actualizar(7, any()) }
    }

    @Test
    fun eliminaProductoPorId() = runBlocking {
        coEvery { servicioProductos.eliminar(7) } returns Unit
        repositorio.eliminarProducto(7)
        coVerify(exactly = 1) { servicioProductos.eliminar(7) }
    }

    @Test
    fun registraEntrada() = runBlocking {
        coEvery { servicioMovimientos.registrar(any()) } returns movimiento()
        repositorio.registrarMovimiento(1, 5, "ENTRADA")
        coVerify(exactly = 1) {
            servicioMovimientos.registrar(match {
                it.productoId == 1L && it.cantidad == 5 && it.tipo == "ENTRADA"
            })
        }
    }

    @Test
    fun registraVenta() = runBlocking {
        coEvery { servicioVentas.registrar(any()) } returns venta()
        repositorio.registrarVenta(1, 3)
        coVerify(exactly = 1) {
            servicioVentas.registrar(match { it.productoId == 1L && it.cantidad == 3 })
        }
    }

    @Test
    fun propagaErrorAlConsultarProductos() = runBlocking {
        coEvery { servicioProductos.listar() } throws java.io.IOException("Sin conexión")
        var huboError = false
        try {
            repositorio.listarProductos()
        } catch (excepcion: java.io.IOException) {
            huboError = true
        }
        assertTrue(huboError)
    }

    @Test
    fun propagaErrorAlCrearProducto() = runBlocking {
        coEvery { servicioProductos.crear(any()) } throws java.io.IOException("Sin conexión")
        var huboError = false
        try {
            repositorio.guardarProducto(datos(), null)
        } catch (excepcion: java.io.IOException) {
            huboError = true
        }
        assertTrue(huboError)
    }
}
