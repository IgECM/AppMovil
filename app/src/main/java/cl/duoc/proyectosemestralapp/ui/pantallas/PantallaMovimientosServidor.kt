package cl.duoc.proyectosemestralapp.ui.pantallas

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaMovimientosServidor(modeloVista: ServidorViewModel, cargando: Boolean) {
    val productos by modeloVista.productos.collectAsState()
    val movimientos by modeloVista.movimientos.collectAsState()
    var idSeleccionado by remember { mutableStateOf<Long?>(null) }
    var cantidad by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("ENTRADA") }

    Text("Registrar movimiento", style = MaterialTheme.typography.titleMedium)
    Text("Selecciona el producto")
    productos.forEach { producto ->
        Row(modifier = Modifier.fillMaxWidth()) {
            RadioButton(selected = idSeleccionado == producto.id,
                onClick = { idSeleccionado = producto.id })
            Column {
                Text(producto.nombre)
                Text("Stock disponible: ${producto.stock}")
            }
        }
    }
    Row {
        FilterChip(selected = tipo == "ENTRADA", onClick = { tipo = "ENTRADA" },
            label = { Text("Entrada") })
        Spacer(Modifier.padding(4.dp))
        FilterChip(selected = tipo == "SALIDA", onClick = { tipo = "SALIDA" },
            label = { Text("Salida") })
    }
    OutlinedTextField(value = cantidad,
        onValueChange = { cantidad = it.filter(Char::isDigit) },
        label = { Text("Cantidad") }, modifier = Modifier.fillMaxWidth())
    Spacer(Modifier.height(8.dp))
    Button(onClick = {
        val id = idSeleccionado
        val unidades = cantidad.toIntOrNull()
        if (id != null && unidades != null) {
            modeloVista.registrarMovimiento(id, unidades, tipo) { cantidad = "" }
        }
    }, enabled = !cargando && idSeleccionado != null && (cantidad.toIntOrNull() ?: 0) > 0,
        modifier = Modifier.fillMaxWidth()) {
        Text("Guardar movimiento en Spring Boot")
    }
    Spacer(Modifier.height(16.dp))
    Text("Historial en el servidor", style = MaterialTheme.typography.titleMedium)
    if (movimientos.isEmpty()) Text("Todavía no hay movimientos")
    movimientos.take(15).forEach { movimiento ->
        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
            Column(Modifier.padding(10.dp)) {
                Text("${movimiento.tipo}: ${movimiento.codigoProducto}")
                Text("Cantidad: ${movimiento.cantidad}")
                Text("Stock: ${movimiento.stockAnterior} → ${movimiento.stockPosterior}")
                Text(movimiento.fechaRegistro.take(19).replace("T", " ") + " UTC")
            }
        }
    }
}
