package cl.duoc.proyectosemestralapp

import cl.duoc.proyectosemestralapp.data.remoto.ProductoCatalogo
import cl.duoc.proyectosemestralapp.data.remoto.RespuestaCatalogo
import cl.duoc.proyectosemestralapp.data.remoto.ServicioCatalogo
import cl.duoc.proyectosemestralapp.data.repositorio.CatalogoRepositorio
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class CatalogoRepositorioTest {
    private val servicio = mockk<ServicioCatalogo>()
    private val repositorio = CatalogoRepositorio(servicio)

    @Test
    fun cargaProductosExternos() = runBlocking {
        val ejemplo = ProductoCatalogo(1, "Producto externo", "Descripción", "beauty", 9.99, 50)
        coEvery { servicio.listar(any(), any()) } returns RespuestaCatalogo(listOf(ejemplo))
        val productos = repositorio.listar()
        assertEquals(1, productos.size)
        assertEquals("Producto externo", productos.first().nombre)
        coVerify(exactly = 1) { servicio.listar(any(), any()) }
    }

    @Test
    fun devuelveListaVacia() = runBlocking {
        coEvery { servicio.listar(any(), any()) } returns RespuestaCatalogo(emptyList())
        assertTrue(repositorio.listar().isEmpty())
    }

    @Test
    fun propagaErrorDeCatalogo() = runBlocking {
        coEvery { servicio.listar(any(), any()) } throws java.io.IOException("Sin conexión")
        var huboError = false
        try {
            repositorio.listar()
        } catch (excepcion: java.io.IOException) {
            huboError = true
        }
        assertTrue(huboError)
    }
}
