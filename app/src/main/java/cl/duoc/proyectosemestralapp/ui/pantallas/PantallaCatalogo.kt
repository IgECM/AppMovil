package cl.duoc.proyectosemestralapp.ui.pantallas

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.proyectosemestralapp.ui.viewmodel.CatalogoViewModel

@Composable
fun PantallaCatalogo(modeloVista: CatalogoViewModel) {
    val productos by modeloVista.productos.collectAsState()
    val cargando by modeloVista.cargando.collectAsState()
    val mensaje by modeloVista.mensaje.collectAsState()

    LaunchedEffect(Unit) {
        if (productos.isEmpty()) modeloVista.cargarProductos()
    }

    Text("Catálogo externo de referencia", style = MaterialTheme.typography.titleMedium)
    Text("Datos de ejemplo de DummyJSON. No modifican el inventario ni las ventas de Kaiju.")
    Spacer(Modifier.height(8.dp))
    Button(onClick = { modeloVista.cargarProductos() }, enabled = !cargando,
        modifier = Modifier.fillMaxWidth()) {
        Text("Actualizar catálogo externo")
    }
    if (cargando) CircularProgressIndicator()
    if (mensaje.isNotEmpty()) Text(mensaje)
    Spacer(Modifier.height(8.dp))
    productos.forEach { producto ->
        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
            Column(Modifier.padding(12.dp)) {
                Text(producto.nombre, style = MaterialTheme.typography.titleMedium)
                Text(producto.descripcion)
                Text("Categoría: ${producto.categoria}")
                Text("Precio de referencia: ${producto.precio} | Stock de ejemplo: ${producto.stock}")
            }
        }
    }
}
