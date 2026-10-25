package cl.duoc.proyectosemestralapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.proyectosemestralapp.data.local.AppDatabase
import cl.duoc.proyectosemestralapp.data.local.ProductoEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProductoViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val dao = database.productoDao()

    val listaProductos: StateFlow<List<ProductoEntity>> = dao.obtenerProductos()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun insertarProducto(codigo: String, nombre: String, descripcion: String, categoria: String, precio: Int, stock: Int, stockMinimo: Int) {
        viewModelScope.launch {
            val producto = ProductoEntity(
                codigo = codigo,
                nombre = nombre,
                descripcion = descripcion,
                categoria = categoria,
                precio = precio,
                stock = stock,
                stockMinimo = stockMinimo
            )
            dao.insertarProducto(producto)
        }
    }

    fun registrarMovimiento(
        codigo: String,
        cantidad: Int,
        esEntrada: Boolean
    ) {
        viewModelScope.launch {
            // Buscar el producto por su código
            val producto = listaProductos.value.find {
                it.codigo == codigo
            } ?: return@launch

            // Validar la cantidad
            if (cantidad <= 0) {
                return@launch
            }

            // Calcular el stock actualizado
            val nuevoStock = if (esEntrada) {
                producto.stock + cantidad
            } else {
                producto.stock - cantidad
            }

            // Evitar que el stock quede negativo
            if (nuevoStock < 0) {
                return@launch
            }

            // Actualizar el producto en Room
            dao.actualizarProducto(
                producto.copy(stock = nuevoStock)
            )
        }
    }
}