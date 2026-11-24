package cl.duoc.proyectosemestralapp

import cl.duoc.proyectosemestralapp.dominio.DatosProducto
import cl.duoc.proyectosemestralapp.dominio.ReglasInventario
import org.junit.Assert.*
import org.junit.Test

class ReglasInventarioTest {
    private fun formulario() = DatosProducto("KA-001", "Producto prueba", "", "Insumos", "100", "10", "2")

    @Test fun formularioCorrecto() = assertNull(ReglasInventario.validarProducto(formulario()))
    @Test fun codigoObligatorio() = assertNotNull(ReglasInventario.validarProducto(formulario().copy(codigo = " ")))
    @Test fun nombreObligatorio() = assertNotNull(ReglasInventario.validarProducto(formulario().copy(nombre = "")))
    @Test fun categoriaObligatoria() = assertNotNull(ReglasInventario.validarProducto(formulario().copy(categoria = "")))
    @Test fun precioNegativo() = assertNotNull(ReglasInventario.validarProducto(formulario().copy(precio = "-1")))
    @Test fun precioNoNumerico() = assertNotNull(ReglasInventario.validarProducto(formulario().copy(precio = "abc")))
    @Test fun stockMinimoInvalido() = assertNotNull(ReglasInventario.validarProducto(formulario().copy(stockMinimo = "abc")))
    @Test fun entradaCorrecta() = assertEquals(15, ReglasInventario.calcularStock(10, 5, true))
    @Test fun salidaCorrecta() = assertEquals(5, ReglasInventario.calcularStock(10, 5, false))
    @Test fun salidaSinStock() = assertNull(ReglasInventario.calcularStock(3, 5, false))
    @Test fun cantidadCero() = assertNull(ReglasInventario.calcularStock(10, 0, true))
    @Test fun stockNegativo() = assertNotNull(ReglasInventario.validarProducto(formulario().copy(stock = "-1")))
    @Test fun stockNoNumerico() = assertNotNull(ReglasInventario.validarProducto(formulario().copy(stock = "texto")))
    @Test fun stockMinimoNegativo() = assertNotNull(ReglasInventario.validarProducto(formulario().copy(stockMinimo = "-1")))
    @Test fun precioVacio() = assertNotNull(ReglasInventario.validarProducto(formulario().copy(precio = "")))
    @Test fun stockActualNegativo() = assertNull(ReglasInventario.calcularStock(-1, 1, true))
    @Test fun salidaExacta() = assertEquals(0, ReglasInventario.calcularStock(3, 3, false))
    @Test fun cantidadNegativa() = assertNull(ReglasInventario.calcularStock(10, -1, true))
    @Test fun entradaDesbordada() = assertNull(ReglasInventario.calcularStock(Int.MAX_VALUE, 1, true))
}
