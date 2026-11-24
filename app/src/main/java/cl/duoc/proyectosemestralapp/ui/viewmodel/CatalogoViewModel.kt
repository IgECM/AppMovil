package cl.duoc.proyectosemestralapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.proyectosemestralapp.data.remoto.ProductoCatalogo
import cl.duoc.proyectosemestralapp.data.repositorio.CatalogoRepositorio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CatalogoViewModel(
    private val repositorio: CatalogoRepositorio = CatalogoRepositorio()
) : ViewModel() {

    private val _productos = MutableStateFlow<List<ProductoCatalogo>>(emptyList())
    val productos = _productos.asStateFlow()

    private val _cargando = MutableStateFlow(false)
    val cargando = _cargando.asStateFlow()

    private val _mensaje = MutableStateFlow("")
    val mensaje = _mensaje.asStateFlow()

    fun cargarProductos() {
        if (_cargando.value) return
        viewModelScope.launch {
            _cargando.value = true
            _mensaje.value = ""
            try {
                _productos.value = repositorio.listar()
                _mensaje.value = "Catálogo externo actualizado"
            } catch (excepcion: Exception) {
                _mensaje.value = "No se pudo cargar el catálogo externo. Comprueba Internet."
            } finally {
                _cargando.value = false
            }
        }
    }
}
