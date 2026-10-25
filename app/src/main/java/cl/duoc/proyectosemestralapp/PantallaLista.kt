package cl.duoc.proyectosemestralapp

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun PantallaLista(viewModel: ProductoViewModel, onVolverRegistro: () -> Unit) {
    val context = LocalContext.current
    val productos by viewModel.listaProductos.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(
            onClick = { onVolverRegistro() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver al Registro")
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = {
                val detalleProductos = productos.joinToString(separator = "\n") { "- ${it.nombre} (Stock: ${it.stock}, Precio: \$${it.precio})" }
                val textoCompartir = "Inventario Kaiju:\nTotal de productos: ${productos.size}\n\n$detalleProductos"

                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, textoCompartir)
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(sendIntent, null)
                context.startActivity(shareIntent)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Compartir Inventario")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Productos Guardados:", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(productos) { producto ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(text = "Nombre: ${producto.nombre}", style = MaterialTheme.typography.bodyLarge)
                        Text(text = "Código: ${producto.codigo} | Categoría: ${producto.categoria}")
                        Text(text = "Stock: ${producto.stock} (Mín: ${producto.stockMinimo}) | Precio: \$${producto.precio}")
                    }
                }
            }
        }
    }
}