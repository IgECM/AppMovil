package cl.duoc.proyectosemestralapp

import cl.duoc.proyectosemestralapp.data.remoto.ProductoCatalogo
import cl.duoc.proyectosemestralapp.data.repositorio.CatalogoRepositorio
import cl.duoc.proyectosemestralapp.ui.viewmodel.CatalogoViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.io.IOException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CatalogoViewModelTest {
    @get:Rule val regla = ReglaDespachador()
    private val repositorio = mockk<CatalogoRepositorio>()
    private val modeloVista = CatalogoViewModel(repositorio)

    @Test
    fun cargaCatalogoCorrectamente() = runTest {
        val ejemplo = ProductoCatalogo(1, "Producto", "Descripción", "beauty", 9.99, 12)
        coEvery { repositorio.listar() } returns listOf(ejemplo)
        modeloVista.cargarProductos()
        advanceUntilIdle()
        assertEquals("Producto", modeloVista.productos.value.first().nombre)
        assertEquals("Catálogo externo actualizado", modeloVista.mensaje.value)
        assertFalse(modeloVista.cargando.value)
        coVerify(exactly = 1) { repositorio.listar() }
    }

    @Test
    fun muestraErrorCuandoFallaInternet() = runTest {
        coEvery { repositorio.listar() } throws IOException("Red desconectada")
        modeloVista.cargarProductos()
        advanceUntilIdle()
        assertTrue(modeloVista.productos.value.isEmpty())
        assertTrue(modeloVista.mensaje.value.contains("No se pudo cargar"))
        assertFalse(modeloVista.cargando.value)
    }
}
