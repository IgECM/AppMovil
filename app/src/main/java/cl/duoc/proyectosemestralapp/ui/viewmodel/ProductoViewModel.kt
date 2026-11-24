package cl.duoc.proyectosemestralapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.proyectosemestralapp.data.local.AppDatabase
import cl.duoc.proyectosemestralapp.data.repositorio.ProductoRepositorio
import cl.duoc.proyectosemestralapp.dominio.DatosProducto
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProductoViewModel(
    application: Application,
    private val repositorio: ProductoRepositorio
) : AndroidViewModel(application) {
    constructor(application: Application) : this(
        application,
        ProductoRepositorio(AppDatabase.getDatabase(application))
    )

    val listaProductos = repositorio.productos.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val listaMovimientos = repositorio.movimientos.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun insertarProducto(datos: DatosProducto, alFinalizar: (String?) -> Unit) {
        viewModelScope.launch {
            alFinalizar(repositorio.guardarProducto(datos))
        }
    }

    fun registrarMovimiento(
        productoId: Int,
        cantidad: Int,
        esEntrada: Boolean,
        alFinalizar: (String?) -> Unit
    ) {
        viewModelScope.launch {
            alFinalizar(repositorio.registrarMovimiento(productoId, cantidad, esEntrada))
        }
    }
}
