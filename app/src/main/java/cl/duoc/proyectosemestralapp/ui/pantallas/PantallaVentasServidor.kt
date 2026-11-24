package cl.duoc.proyectosemestralapp.ui.pantallas

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.proyectosemestralapp.ui.viewmodel.ServidorViewModel

@Composable
fun PantallaVentasServidor(modeloVista: ServidorViewModel, cargando: Boolean) {
    val productos by modeloVista.productos.collectAsState()
    val ventas by modeloVista.ventas.collectAsState()
    var idSeleccionado by remember { mutableStateOf<Long?>(null) }
    var cantidad by remember { mutableStateOf("") }

    Text("Registrar venta", style = MaterialTheme.typography.titleMedium)
    Text("Selecciona el producto")
    productos.forEach { producto ->
        Row(modifier = Modifier.fillMaxWidth()) {
            RadioButton(selected = idSeleccionado == producto.id,
                onClick = { idSeleccionado = producto.id })
            Column {
                Text(producto.nombre)
                Text("Stock: ${producto.stock} | Precio: \$${producto.precio}")
            }
        }
    }
    OutlinedTextField(value = cantidad,
        onValueChange = { cantidad = it.filter(Char::isDigit) },
        label = { Text("Unidades vendidas") }, modifier = Modifier.fillMaxWidth())
    Spacer(Modifier.height(8.dp))
    Button(onClick = {
        val id = idSeleccionado
        val unidades = cantidad.toIntOrNull()
        if (id != null && unidades != null) {
            modeloVista.registrarVenta(id, unidades) { cantidad = "" }
        }
    }, enabled = !cargando && idSeleccionado != null && (cantidad.toIntOrNull() ?: 0) > 0,
        modifier = Modifier.fillMaxWidth()) {
        Text("Registrar venta en Spring Boot")
    }
    Spacer(Modifier.height(16.dp))
    Text("Ventas del servidor", style = MaterialTheme.typography.titleMedium)
    if (ventas.isEmpty()) Text("Todavía no hay ventas")
    ventas.take(15).forEach { venta ->
        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
            Column(Modifier.padding(10.dp)) {
                Text("Producto: ${venta.codigoProducto}")
                Text("Cantidad: ${venta.cantidad}")
                Text("Total: \$${venta.total}")
                Text(venta.fechaRegistro.take(19).replace("T", " ") + " UTC")
            }
        }
    }
}
