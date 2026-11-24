package cl.duoc.proyectosemestralapp.ui.pantallas

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import android.content.Intent
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cl.duoc.proyectosemestralapp.ui.viewmodel.ServidorViewModel
import cl.duoc.proyectosemestralapp.ui.viewmodel.CatalogoViewModel

@Composable
fun PantallaServidor(modeloVista: ServidorViewModel, modeloCatalogo: CatalogoViewModel) {
    val cargando by modeloVista.cargando.collectAsState()
    val productos by modeloVista.productos.collectAsState()
    val contexto = LocalContext.current
    val mensaje by modeloVista.mensaje.collectAsState()
    var seccion by remember { mutableStateOf("productos") }

    LaunchedEffect(Unit) {
        modeloVista.cargarDatos()
    }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)
    ) {
        Text("Kaiju - Gestión de inventario", style = MaterialTheme.typography.titleLarge)
        Text("Inventario principal conectado a Spring Boot")
        Spacer(Modifier.height(8.dp))
        OutlinedButton(onClick = { modeloVista.cargarDatos() }, enabled = !cargando,
            modifier = Modifier.fillMaxWidth()) {
            Text("Actualizar inventario")
        }
        OutlinedButton(
            onClick = {
                val detalle = productos.joinToString("\n") { producto ->
                    "${producto.nombre} - Código: ${producto.codigo}, Stock: ${producto.stock}, Precio: \$${producto.precio}"
                }
                val mensajeCompartir = "Inventario Kaiju\nProductos: ${productos.size}\n\n$detalle"
                val intento = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, mensajeCompartir)
                }
                contexto.startActivity(Intent.createChooser(intento, "Compartir inventario"))
            },
            enabled = productos.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Compartir inventario")
        }
        if (cargando) CircularProgressIndicator()
        if (mensaje.isNotEmpty()) {
            Text(mensaje, color = MaterialTheme.colorScheme.primary)
        }
        Spacer(Modifier.height(12.dp))
        Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
            Button(onClick = { seccion = "productos" }) { Text("Productos") }
            Button(onClick = { seccion = "movimientos" }) { Text("Movimientos") }
            Button(onClick = { seccion = "ventas" }) { Text("Ventas") }
            Button(onClick = { seccion = "catalogo" }) { Text("Catálogo externo") }
        }
        Spacer(Modifier.height(12.dp))
        when (seccion) {
            "productos" -> PantallaProductosServidor(modeloVista, cargando)
            "movimientos" -> PantallaMovimientosServidor(modeloVista, cargando)
            "ventas" -> PantallaVentasServidor(modeloVista, cargando)
            "catalogo" -> PantallaCatalogo(modeloCatalogo)
        }
    }
}
