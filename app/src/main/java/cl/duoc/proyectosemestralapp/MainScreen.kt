package cl.duoc.proyectosemestralapp

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: ProductoViewModel) {
    val productos by viewModel.listaProductos.collectAsState()
    val context = LocalContext.current

    var codigo by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var stockMinimo by remember { mutableStateOf("") }

    var errorNombre by remember { mutableStateOf(false) }
    var errorPrecio by remember { mutableStateOf(false) }
    var errorStock by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kaiju - Inventario") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = codigo,
                onValueChange = { codigo = it },
                label = { Text("Codigo") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = nombre,
                onValueChange = {
                    nombre = it
                    errorNombre = false
                },
                label = { Text("Nombre *") },
                isError = errorNombre,
                modifier = Modifier.fillMaxWidth()
            )
            if (errorNombre) {
                Text(text = "El nombre no puede estar vacio", color = MaterialTheme.colorScheme.error)
            }
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripcion") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = categoria,
                onValueChange = { categoria = it },
                label = { Text("Categoria") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = precio,
                onValueChange = {
                    precio = it
                    errorPrecio = false
                },
                label = { Text("Precio *") },
                isError = errorPrecio,
                modifier = Modifier.fillMaxWidth()
            )
            if (errorPrecio) {
                Text(text = "Ingresa un precio valido", color = MaterialTheme.colorScheme.error)
            }
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = stock,
                onValueChange = {
                    stock = it
                    errorStock = false
                },
                label = { Text("Stock *") },
                isError = errorStock,
                modifier = Modifier.fillMaxWidth()
            )
            if (errorStock) {
                Text(text = "Ingresa un stock valido", color = MaterialTheme.colorScheme.error)
            }
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = stockMinimo,
                onValueChange = { stockMinimo = it },
                label = { Text("Stock Minimo") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val precioInt = precio.toIntOrNull()
                    val stockInt = stock.toIntOrNull()

                    errorNombre = nombre.isBlank()
                    errorPrecio = precioInt == null
                    errorStock = stockInt == null

                    if (!errorNombre && !errorPrecio && !errorStock) {
                        viewModel.insertarProducto(
                            codigo = codigo,
                            nombre = nombre,
                            descripcion = descripcion,
                            categoria = categoria,
                            precio = precioInt ?: 0,
                            stock = stockInt ?: 0,
                            stockMinimo = stockMinimo.toIntOrNull() ?: 0
                        )

                        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as android.os.VibratorManager
                            vibratorManager.defaultVibrator
                        } else {
                            @Suppress("DEPRECATION")
                            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                        }

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
                        } else {
                            @Suppress("DEPRECATION")
                            vibrator.vibrate(500)
                        }

                        codigo = ""
                        nombre = ""
                        descripcion = ""
                        categoria = ""
                        precio = ""
                        stock = ""
                        stockMinimo = ""
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar Producto")
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = {
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, "Total de productos en inventario Kaiju: ${productos.size}")
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
                            Text(text = "Nombre: ${producto.nombre}", style = MaterialTheme.typography.titleMedium)
                            Text(text = "Codigo: ${producto.codigo}")
                            Text(text = "Stock: ${producto.stock}")
                            Text(text = "Precio: ${producto.precio}")
                        }
                    }
                }
            }
        }
    }
}