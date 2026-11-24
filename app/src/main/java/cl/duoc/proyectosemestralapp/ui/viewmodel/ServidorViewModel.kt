package cl.duoc.proyectosemestralapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.proyectosemestralapp.data.remoto.MovimientoRemoto
import cl.duoc.proyectosemestralapp.data.remoto.ProductoRemoto
import cl.duoc.proyectosemestralapp.data.remoto.VentaRemota
import cl.duoc.proyectosemestralapp.data.repositorio.ServidorRepositorio
import cl.duoc.proyectosemestralapp.dominio.DatosProducto
import java.io.IOException
import java.net.SocketTimeoutException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class ServidorViewModel(
    private val repositorio: ServidorRepositorio = ServidorRepositorio()
) : ViewModel() {

    private val _productos = MutableStateFlow<List<ProductoRemoto>>(emptyList())
    val productos = _productos.asStateFlow()

    private val _movimientos = MutableStateFlow<List<MovimientoRemoto>>(emptyList())
    val movimientos = _movimientos.asStateFlow()

    private val _ventas = MutableStateFlow<List<VentaRemota>>(emptyList())
    val ventas = _ventas.asStateFlow()

    private val _cargando = MutableStateFlow(false)
    val cargando = _cargando.asStateFlow()

    private val _mensaje = MutableStateFlow("")
    val mensaje = _mensaje.asStateFlow()

    fun cargarDatos() {
        if (_cargando.value) return
        viewModelScope.launch {
            _cargando.value = true
            _mensaje.value = ""
            try {
                actualizarListas()
                _mensaje.value = "Datos actualizados"
            } catch (excepcion: Exception) {
                _mensaje.value = mensajeError(excepcion)
            } finally {
                _cargando.value = false
            }
        }
    }

    fun guardarProducto(datos: DatosProducto, id: Long?, alGuardar: () -> Unit) {
        val error = repositorio.validarDatos(datos)
        if (error != null) {
            _mensaje.value = error
            return
        }
        ejecutarCambio("Producto guardado en el servidor", alGuardar) {
            repositorio.guardarProducto(datos, id)
        }
    }

    fun eliminarProducto(id: Long) {
        ejecutarCambio("Producto eliminado del servidor") {
            repositorio.eliminarProducto(id)
        }
    }

    fun registrarMovimiento(id: Long, cantidad: Int, tipo: String, alGuardar: () -> Unit) {
        if (cantidad <= 0) {
            _mensaje.value = "La cantidad debe ser mayor que cero"
            return
        }
        ejecutarCambio("Movimiento registrado en el servidor", alGuardar) {
            repositorio.registrarMovimiento(id, cantidad, tipo)
        }
    }

    fun registrarVenta(id: Long, cantidad: Int, alGuardar: () -> Unit) {
        if (cantidad <= 0) {
            _mensaje.value = "La cantidad debe ser mayor que cero"
            return
        }
        ejecutarCambio("Venta registrada en el servidor", alGuardar) {
            repositorio.registrarVenta(id, cantidad)
        }
    }

    private fun ejecutarCambio(
        textoExito: String,
        alGuardar: () -> Unit = {},
        cambio: suspend () -> Unit
    ) {
        if (_cargando.value) return
        viewModelScope.launch {
            _cargando.value = true
            _mensaje.value = ""
            try {
                cambio()
                alGuardar()
                _mensaje.value = textoExito
                try {
                    actualizarListas()
                } catch (excepcion: Exception) {
                    _mensaje.value = "$textoExito. No se pudieron actualizar las listas; pulsa Actualizar datos."
                }
            } catch (excepcion: SocketTimeoutException) {
                _mensaje.value = "La respuesta tardó demasiado. Comprueba si se guardó antes de repetir la operación."
            } catch (excepcion: Exception) {
                _mensaje.value = mensajeError(excepcion)
            } finally {
                _cargando.value = false
            }
        }
    }

    private suspend fun actualizarListas() {
        _productos.value = repositorio.listarProductos()
        _movimientos.value = repositorio.listarMovimientos()
        _ventas.value = repositorio.listarVentas()
    }

    private fun mensajeError(excepcion: Exception): String {
        return when (excepcion) {
            is HttpException -> when (excepcion.code()) {
                400 -> "Datos inválidos o stock insuficiente"
                404 -> "Producto no encontrado"
                409 -> "El código del producto ya está registrado"
                else -> "Error del servidor: ${excepcion.code()}"
            }
            is SocketTimeoutException -> "Tiempo de espera agotado. Comprueba la conexión e inténtalo de nuevo."
            is IOException -> "Sin conexión. Comprueba que el PC esté en la misma red y Spring Boot esté encendido."
            else -> "No se pudo completar la operación"
        }
    }
}
