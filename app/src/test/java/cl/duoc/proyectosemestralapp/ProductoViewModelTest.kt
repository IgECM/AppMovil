package cl.duoc.proyectosemestralapp

import android.app.Application
import cl.duoc.proyectosemestralapp.data.local.MovimientoEntity
import cl.duoc.proyectosemestralapp.data.local.ProductoEntity
import cl.duoc.proyectosemestralapp.data.repositorio.ProductoRepositorio
import cl.duoc.proyectosemestralapp.dominio.DatosProducto
import cl.duoc.proyectosemestralapp.ui.viewmodel.ProductoViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class ProductoViewModelTest {
    @get:Rule val regla = ReglaDespachador()

    private class Escenario(
        val repositorio: ProductoRepositorio,
        val modeloVista: ProductoViewModel
    )

    private fun crearEscenario(): Escenario {
        val repositorio = mockk<ProductoRepositorio>()
        every { repositorio.productos } returns MutableStateFlow<List<ProductoEntity>>(emptyList())
        every { repositorio.movimientos } returns MutableStateFlow<List<MovimientoEntity>>(emptyList())
        val aplicacion = RuntimeEnvironment.getApplication() as Application
        val modeloVista = ProductoViewModel(aplicacion, repositorio)
        return Escenario(repositorio, modeloVista)
    }

    private fun datos() = DatosProducto(
        codigo = "KA-101",
        nombre = "Polera",
        descripcion = "Prueba",
        categoria = "Ropa",
        precio = "5000",
        stock = "20",
        stockMinimo = "2"
    )

    @Test
    fun guardaProductoCorrectamente() = runTest {
        val escenario = crearEscenario()
        coEvery { escenario.repositorio.guardarProducto(any()) } returns null
        var resultado: String? = "Sin ejecutar"
        escenario.modeloVista.insertarProducto(datos()) { resultado = it }
        advanceUntilIdle()
        assertNull(resultado)
        coVerify(exactly = 1) { escenario.repositorio.guardarProducto(any()) }
    }

    @Test
    fun entregaErrorAlGuardarProducto() = runTest {
        val escenario = crearEscenario()
        coEvery { escenario.repositorio.guardarProducto(any()) } returns "Código duplicado"
        var resultado: String? = null
        escenario.modeloVista.insertarProducto(datos()) { resultado = it }
        advanceUntilIdle()
        assertEquals("Código duplicado", resultado)
    }

    @Test
    fun registraEntradaCorrectamente() = runTest {
        val escenario = crearEscenario()
        coEvery { escenario.repositorio.registrarMovimiento(1, 5, true) } returns null
        var termino = false
        escenario.modeloVista.registrarMovimiento(1, 5, true) { termino = it == null }
        advanceUntilIdle()
        assertTrue(termino)
        coVerify(exactly = 1) { escenario.repositorio.registrarMovimiento(1, 5, true) }
    }

    @Test
    fun informaErrorAlRegistrarSalida() = runTest {
        val escenario = crearEscenario()
        coEvery { escenario.repositorio.registrarMovimiento(1, 100, false) } returns "Stock insuficiente"
        var resultado: String? = null
        escenario.modeloVista.registrarMovimiento(1, 100, false) { resultado = it }
        advanceUntilIdle()
        assertEquals("Stock insuficiente", resultado)
        coVerify(exactly = 1) { escenario.repositorio.registrarMovimiento(1, 100, false) }
    }
}
