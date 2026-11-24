package cl.duoc.proyectosemestralapp

import cl.duoc.proyectosemestralapp.data.remoto.MovimientoRemoto
import cl.duoc.proyectosemestralapp.data.remoto.ProductoRemoto
import cl.duoc.proyectosemestralapp.data.remoto.VentaRemota
import cl.duoc.proyectosemestralapp.data.repositorio.ServidorRepositorio
import cl.duoc.proyectosemestralapp.dominio.DatosProducto
import cl.duoc.proyectosemestralapp.ui.viewmodel.ServidorViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import java.io.IOException
import java.net.SocketTimeoutException
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ServidorViewModelTest {
    @get:Rule val regla = ReglaDespachador()
    private val repositorio = mockk<ServidorRepositorio>()
    private val modeloVista = ServidorViewModel(repositorio)
    private fun datos() = DatosProducto("K-001", "Camisa", "Blanca", "Ropa", "5000", "750", "100")
    private fun producto() = ProductoRemoto(1, "K-001", "Camisa", "Blanca", "Ropa", 5000, 750, 100)
    private fun prepararConsultas() {
        coEvery { repositorio.listarProductos() } returns listOf(producto())
        coEvery { repositorio.listarMovimientos() } returns emptyList()
        coEvery { repositorio.listarVentas() } returns emptyList()
    }

    @Test
    fun actualizaListasCorrectamente() = runTest {
        prepararConsultas()
        modeloVista.cargarDatos()
        advanceUntilIdle()
        assertEquals(1, modeloVista.productos.value.size)
        assertEquals("Datos actualizados", modeloVista.mensaje.value)
        assertFalse(modeloVista.cargando.value)
    }

    @Test
    fun informaErrorDeConexion() = runTest {
        coEvery { repositorio.listarProductos() } throws IOException("Sin red")
        modeloVista.cargarDatos()
        advanceUntilIdle()
        assertTrue(modeloVista.mensaje.value.contains("Sin conexión"))
        assertFalse(modeloVista.cargando.value)
    }

    @Test
    fun informaTiempoAgotado() = runTest {
        coEvery { repositorio.listarProductos() } throws SocketTimeoutException("Tiempo")
        modeloVista.cargarDatos()
        advanceUntilIdle()
        assertTrue(modeloVista.mensaje.value.contains("Tiempo de espera"))
    }

    @Test
    fun rechazaProductoInvalido() = runTest {
        every { repositorio.validarDatos(any()) } returns "El nombre es obligatorio"
        modeloVista.guardarProducto(datos(), null) {}
        advanceUntilIdle()
        assertEquals("El nombre es obligatorio", modeloVista.mensaje.value)
        coVerify(exactly = 0) { repositorio.guardarProducto(any(), any()) }
    }

    @Test
    fun guardaProductoYActualizaListas() = runTest {
        every { repositorio.validarDatos(any()) } returns null
        coEvery { repositorio.guardarProducto(any(), any()) } returns Unit
        prepararConsultas()
        var termino = false
        modeloVista.guardarProducto(datos(), null) { termino = true }
        advanceUntilIdle()
        assertTrue(termino)
        assertEquals("Producto guardado en el servidor", modeloVista.mensaje.value)
        assertEquals("Camisa", modeloVista.productos.value.first().nombre)
    }

    @Test
    fun confirmaGuardadoAunqueFalleActualizacion() = runTest {
        every { repositorio.validarDatos(any()) } returns null
        coEvery { repositorio.guardarProducto(any(), any()) } returns Unit
        coEvery { repositorio.listarProductos() } throws IOException("Sin red")
        modeloVista.guardarProducto(datos(), null) {}
        advanceUntilIdle()
        assertTrue(modeloVista.mensaje.value.contains("Producto guardado"))
        assertTrue(modeloVista.mensaje.value.contains("No se pudieron actualizar"))
    }

    @Test
    fun advierteSiGuardarAgotaTiempo() = runTest {
        every { repositorio.validarDatos(any()) } returns null
        coEvery { repositorio.guardarProducto(any(), any()) } throws SocketTimeoutException("Lento")
        modeloVista.guardarProducto(datos(), null) {}
        advanceUntilIdle()
        assertTrue(modeloVista.mensaje.value.contains("Comprueba si se guardó"))
    }

    @Test
    fun eliminaProducto() = runTest {
        coEvery { repositorio.eliminarProducto(1) } returns Unit
        prepararConsultas()
        modeloVista.eliminarProducto(1)
        advanceUntilIdle()
        coVerify(exactly = 1) { repositorio.eliminarProducto(1) }
        assertEquals("Producto eliminado del servidor", modeloVista.mensaje.value)
    }

    @Test
    fun rechazaMovimientoCero() = runTest {
        modeloVista.registrarMovimiento(1, 0, "ENTRADA") {}
        advanceUntilIdle()
        assertEquals("La cantidad debe ser mayor que cero", modeloVista.mensaje.value)
        coVerify(exactly = 0) { repositorio.registrarMovimiento(any(), any(), any()) }
    }

    @Test
    fun registraMovimientoValido() = runTest {
        coEvery { repositorio.registrarMovimiento(1, 5, "ENTRADA") } returns Unit
        prepararConsultas()
        var termino = false
        modeloVista.registrarMovimiento(1, 5, "ENTRADA") { termino = true }
        advanceUntilIdle()
        assertTrue(termino)
        assertEquals("Movimiento registrado en el servidor", modeloVista.mensaje.value)
    }

    @Test
    fun rechazaVentaNegativa() = runTest {
        modeloVista.registrarVenta(1, -1) {}
        advanceUntilIdle()
        assertEquals("La cantidad debe ser mayor que cero", modeloVista.mensaje.value)
        coVerify(exactly = 0) { repositorio.registrarVenta(any(), any()) }
    }

    @Test
    fun registraVentaValida() = runTest {
        coEvery { repositorio.registrarVenta(1, 3) } returns Unit
        prepararConsultas()
        modeloVista.registrarVenta(1, 3) {}
        advanceUntilIdle()
        coVerify(exactly = 1) { repositorio.registrarVenta(1, 3) }
        assertEquals("Venta registrada en el servidor", modeloVista.mensaje.value)
    }

    @Test
    fun muestraErroresHttpConMensajesClaros() = runTest {
        val mensajes = mapOf(
            400 to "Datos inválidos",
            404 to "Producto no encontrado",
            409 to "El código del producto ya está registrado",
            500 to "Error del servidor: 500"
        )
        for (elemento in mensajes) {
            val codigo = elemento.key
            val mensaje = elemento.value
            coEvery { repositorio.listarProductos() } throws HttpException(
                Response.error<Any>(codigo, ResponseBody.create(null, "Error"))
            )
            modeloVista.cargarDatos()
            advanceUntilIdle()
            assertTrue(modeloVista.mensaje.value.contains(mensaje))
        }
    }

    @Test
    fun manejaErroresInesperados() = runTest {
        coEvery { repositorio.listarProductos() } throws IllegalStateException("Error")
        modeloVista.cargarDatos()
        advanceUntilIdle()
        assertEquals("No se pudo completar la operación", modeloVista.mensaje.value)
    }

    @Test
    fun rechazaVentaConCantidadCero() = runTest {
        modeloVista.registrarVenta(1, 0) {}
        advanceUntilIdle()
        assertEquals("La cantidad debe ser mayor que cero", modeloVista.mensaje.value)
    }
}
