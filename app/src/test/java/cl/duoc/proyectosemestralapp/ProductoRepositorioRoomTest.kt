package cl.duoc.proyectosemestralapp

import androidx.room.Room
import cl.duoc.proyectosemestralapp.data.local.AppDatabase
import cl.duoc.proyectosemestralapp.data.repositorio.ProductoRepositorio
import cl.duoc.proyectosemestralapp.dominio.DatosProducto
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class ProductoRepositorioRoomTest {
    private lateinit var baseDatos: AppDatabase
    private lateinit var repositorio: ProductoRepositorio

    @Before
    fun iniciar() {
        baseDatos = Room.inMemoryDatabaseBuilder(
            RuntimeEnvironment.getApplication(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        repositorio = ProductoRepositorio(baseDatos)
    }

    @After
    fun cerrar() {
        baseDatos.close()
    }

    private fun datos() = DatosProducto(
        codigo = " K-001 ",
        nombre = " Camisa ",
        descripcion = " Blanca ",
        categoria = " Ropa ",
        precio = "5000",
        stock = "750",
        stockMinimo = "100"
    )

    @Test
    fun guardaUnProductoEnRoom() = runBlocking {
        assertNull(repositorio.guardarProducto(datos()))
        val producto = baseDatos.productoDao().buscarPorCodigo("K-001")
        assertNotNull(producto)
        assertEquals("Camisa", producto!!.nombre)
        assertEquals(750, producto.stock)
        assertEquals(1, repositorio.productos.first().size)
    }

    @Test
    fun noGuardaUnCodigoDuplicado() = runBlocking {
        assertNull(repositorio.guardarProducto(datos()))
        assertEquals("El código ya está registrado", repositorio.guardarProducto(datos()))
        assertEquals(1, repositorio.productos.first().size)
    }

    @Test
    fun rechazaUnFormularioInvalido() = runBlocking {
        assertEquals("El nombre es obligatorio", repositorio.guardarProducto(datos().copy(nombre = "")))
        assertTrue(repositorio.productos.first().isEmpty())
    }

    @Test
    fun registraEntradaConAuditoria() = runBlocking {
        repositorio.guardarProducto(datos())
        val id = baseDatos.productoDao().buscarPorCodigo("K-001")!!.id
        assertNull(repositorio.registrarMovimiento(id, 5, true))
        assertEquals(755, baseDatos.productoDao().buscarPorId(id)!!.stock)
        val movimiento = repositorio.movimientos.first().first()
        assertEquals("ENTRADA", movimiento.tipo)
        assertEquals(750, movimiento.stockAnterior)
        assertEquals(755, movimiento.stockPosterior)
        assertEquals(5, movimiento.cantidad)
    }

    @Test
    fun registraSalidaConAuditoria() = runBlocking {
        repositorio.guardarProducto(datos())
        val id = baseDatos.productoDao().buscarPorCodigo("K-001")!!.id
        assertNull(repositorio.registrarMovimiento(id, 3, false))
        assertEquals(747, baseDatos.productoDao().buscarPorId(id)!!.stock)
        assertEquals("SALIDA", repositorio.movimientos.first().first().tipo)
    }

    @Test
    fun rechazaUnaSalidaMayorQueElStock() = runBlocking {
        repositorio.guardarProducto(datos())
        val id = baseDatos.productoDao().buscarPorCodigo("K-001")!!.id
        assertEquals("Cantidad inválida o stock insuficiente", repositorio.registrarMovimiento(id, 751, false))
        assertEquals(750, baseDatos.productoDao().buscarPorId(id)!!.stock)
        assertTrue(repositorio.movimientos.first().isEmpty())
    }

    @Test
    fun noRegistraMovimientosParaProductosInexistentes() = runBlocking {
        assertEquals("El producto ya no existe", repositorio.registrarMovimiento(999, 5, true))
        assertTrue(repositorio.movimientos.first().isEmpty())
    }

    @Test
    fun rechazaMovimientoDeCantidadCero() = runBlocking {
        repositorio.guardarProducto(datos())
        val id = baseDatos.productoDao().buscarPorCodigo("K-001")!!.id
        assertEquals("Cantidad inválida o stock insuficiente", repositorio.registrarMovimiento(id, 0, true))
        assertEquals(750, baseDatos.productoDao().buscarPorId(id)!!.stock)
    }

    @Test
    fun noRegistraSalidaNegativa() = runBlocking {
        repositorio.guardarProducto(datos())
        val producto = baseDatos.productoDao().buscarPorCodigo("K-001")!!
        val resultado = repositorio.registrarMovimiento(producto.id, -1, false)
        assertEquals("Cantidad inválida o stock insuficiente", resultado)
        assertEquals(750, baseDatos.productoDao().buscarPorId(producto.id)!!.stock)
        assertTrue(repositorio.movimientos.first().isEmpty())
    }

    @Test
    fun noRegistraEntradaQueDesbordaElMaximo() = runBlocking {
        repositorio.guardarProducto(datos())
        val producto = baseDatos.productoDao().buscarPorCodigo("K-001")!!
        baseDatos.productoDao().actualizarProducto(producto.copy(stock = Int.MAX_VALUE))
        val resultado = repositorio.registrarMovimiento(producto.id, 1, true)
        assertEquals("Cantidad inválida o stock insuficiente", resultado)
        assertEquals(Int.MAX_VALUE, baseDatos.productoDao().buscarPorId(producto.id)!!.stock)
        assertTrue(repositorio.movimientos.first().isEmpty())
    }
}
