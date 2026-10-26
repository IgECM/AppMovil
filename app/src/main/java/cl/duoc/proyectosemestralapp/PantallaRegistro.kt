package cl.duoc.proyectosemestralapp

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun PantallaRegistro(viewModel: ProductoViewModel, onIrALista: () -> Unit, onIrAMovimientos: () -> Unit) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text(
            text = "Kaiju - Registro de Producto",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = codigo,
            onValueChange = { codigo = it },
            label = { Text("Código") },
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
        AnimatedVisibility(visible = errorNombre) {
            Text(text = "El nombre es obligatorio", color = MaterialTheme.colorScheme.error)
        }
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = categoria,
            onValueChange = { categoria = it },
            label = { Text("Categoría") },
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
        AnimatedVisibility(visible = errorPrecio) {
            Text(text = "Ingresa un precio válido", color = MaterialTheme.colorScheme.error)
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
        AnimatedVisibility(visible = errorStock) {
            Text(text = "Ingresa un stock válido", color = MaterialTheme.colorScheme.error)
        }
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = stockMinimo,
            onValueChange = { stockMinimo = it },
            label = { Text("Stock Mínimo") },
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

                    Toast.makeText(context, "Guardado con éxito", Toast.LENGTH_SHORT).show()

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

        Button(
            onClick = { onIrALista() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ver Productos Guardados")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onIrAMovimientos,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Actualizar stock")
        }
    }
}