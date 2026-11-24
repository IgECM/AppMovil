package cl.duoc.proyectosemestralapp.ui.pantallas

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cl.duoc.proyectosemestralapp.data.remoto.ProductoRemoto
import cl.duoc.proyectosemestralapp.dominio.DatosProducto
import cl.duoc.proyectosemestralapp.ui.viewmodel.ServidorViewModel

@Composable
fun PantallaProductosServidor(modeloVista: ServidorViewModel, cargando: Boolean) {
    val productos by modeloVista.productos.collectAsState()
    val contexto = LocalContext.current
    var editando by remember { mutableStateOf<ProductoRemoto?>(null) }
    var porEliminar by remember { mutableStateOf<ProductoRemoto?>(null) }
    var codigo by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var stockMinimo by remember { mutableStateOf("") }

    fun limpiarFormulario() {
        editando = null
        codigo = ""
        nombre = ""
        descripcion = ""
        categoria = ""
        precio = ""
        stock = ""
        stockMinimo = ""
    }

    Text(if (editando == null) "Nuevo producto" else "Editar producto", 
        style = MaterialTheme.typography.titleMedium)
    CampoServidor(codigo, { codigo = it }, "Código")
    CampoServidor(nombre, { nombre = it }, "Nombre")
    CampoServidor(descripcion, { descripcion = it }, "Descripción")
    CampoServidor(categoria, { categoria = it }, "Categoría")
    CampoServidor(precio, { precio = it.filter(Char::isDigit) }, "Precio")
    CampoServidor(stock, { stock = it.filter(Char::isDigit) }, "Stock inicial", editando != null)
    CampoServidor(stockMinimo, { stockMinimo = it.filter(Char::isDigit) }, "Stock mínimo")
    if (editando != null) {
        Text("Para cambiar el stock utiliza Movimientos o Ventas")
    }
    Spacer(Modifier.height(8.dp))
    Button(
        onClick = {
            val datos = DatosProducto(codigo, nombre, descripcion, categoria, precio, stock, stockMinimo)
            modeloVista.guardarProducto(datos, editando?.id) {
                limpiarFormulario()
                val vibrador = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val administrador = contexto.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                    administrador.defaultVibrator
                } else {
                    contexto.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                }
                if (vibrador.hasVibrator()) {
                    vibrador.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
                }
            }
        },
        enabled = !cargando,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(if (editando == null) "Registrar en Spring Boot" else "Guardar cambios")
    }
    if (editando != null) {
        OutlinedButton(onClick = { limpiarFormulario() }, modifier = Modifier.fillMaxWidth()) {
            Text("Cancelar edición")
        }
    }

    Spacer(Modifier.height(16.dp))
    Text("Productos del servidor (${productos.size})", style = MaterialTheme.typography.titleMedium)
    if (productos.isEmpty()) Text("Todavía no hay productos en el servidor")
    productos.forEach { producto ->
        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp)) {
            Column(Modifier.padding(12.dp)) {
                Text(producto.nombre, style = MaterialTheme.typography.titleMedium)
                Text("Código: ${producto.codigo} | Categoría: ${producto.categoria}")
                Text("Precio: \$${producto.precio} | Stock: ${producto.stock}")
                if (producto.stock <= producto.stockMinimo) {
                    Text("REPONER", color = MaterialTheme.colorScheme.error)
                }
                OutlinedButton(onClick = {
                    editando = producto
                    codigo = producto.codigo
                    nombre = producto.nombre
                    descripcion = producto.descripcion
                    categoria = producto.categoria
                    precio = producto.precio.toString()
                    stock = producto.stock.toString()
                    stockMinimo = producto.stockMinimo.toString()
                }, enabled = !cargando) {
                    Text("Editar")
                }
                OutlinedButton(onClick = { porEliminar = producto }, enabled = !cargando) {
                    Text("Eliminar")
                }
            }
        }
    }
    val seleccionado = porEliminar
    if (seleccionado != null) {
        AlertDialog(
            onDismissRequest = { porEliminar = null },
            title = { Text("Eliminar producto") },
            text = { Text("¿Eliminar ${seleccionado.nombre} del servidor?") },
            confirmButton = {
                TextButton(onClick = {
                    modeloVista.eliminarProducto(seleccionado.id)
                    porEliminar = null
                    if (editando?.id == seleccionado.id) limpiarFormulario()
                }) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { porEliminar = null }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun CampoServidor(
    valor: String,
    alCambiar: (String) -> Unit,
    titulo: String,
    soloLectura: Boolean = false
) {
    OutlinedTextField(
        value = valor,
        onValueChange = alCambiar,
        label = { Text(titulo) },
        readOnly = soloLectura,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(6.dp))
}
