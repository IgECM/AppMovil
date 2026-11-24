package cl.duoc.proyectosemestralapp.ui.pantallas

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cl.duoc.proyectosemestralapp.dominio.DatosProducto
import cl.duoc.proyectosemestralapp.ui.viewmodel.ProductoViewModel

@Composable
fun PantallaRegistro(modeloVista: ProductoViewModel, onIrALista: () -> Unit, onIrAMovimientos: () -> Unit, onIrAServidor: () -> Unit) {
    val contexto = LocalContext.current
    var codigo by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var stockMinimo by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var guardando by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Kaiju - Registro de Producto", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = codigo,
            onValueChange = { codigo = it; error = "" },
            label = { Text("Código *") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it; error = "" },
            label = { Text("Nombre *") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = categoria,
            onValueChange = { categoria = it; error = "" },
            label = { Text("Categoría *") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = precio,
            onValueChange = { precio = it.filter(Char::isDigit); error = "" },
            label = { Text("Precio *") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = stock,
            onValueChange = { stock = it.filter(Char::isDigit); error = "" },
            label = { Text("Stock *") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = stockMinimo,
            onValueChange = { stockMinimo = it.filter(Char::isDigit); error = "" },
            label = { Text("Stock mínimo *") },
            modifier = Modifier.fillMaxWidth()
        )

        if (error.isNotBlank()) {
            Spacer(Modifier.height(8.dp))
            Text(error, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(16.dp))
        Button(
            enabled = !guardando,
            onClick = {
                guardando = true
                val datos = DatosProducto(
                    codigo = codigo,
                    nombre = nombre,
                    descripcion = descripcion,
                    categoria = categoria,
                    precio = precio,
                    stock = stock,
                    stockMinimo = stockMinimo
                )

                modeloVista.insertarProducto(datos) { errorGuardado ->
                    guardando = false
                    if (errorGuardado != null) {
                        error = errorGuardado
                    } else {
                        error = ""
                        Toast.makeText(contexto, "Producto guardado", Toast.LENGTH_SHORT).show()
                        val vibrador = contexto.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                        vibrador?.vibrate(
                            VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE)
                        )
                        codigo = ""
                        nombre = ""
                        descripcion = ""
                        categoria = ""
                        precio = ""
                        stock = ""
                        stockMinimo = ""
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (guardando) "Guardando..." else "Guardar producto")
        }

        Spacer(Modifier.height(8.dp))
        Button(onClick = onIrALista, modifier = Modifier.fillMaxWidth()) {
            Text("Ver productos guardados")
        }

        Spacer(Modifier.height(8.dp))
        Button(onClick = onIrAMovimientos, modifier = Modifier.fillMaxWidth()) {
            Text("Actualizar stock")
        }

        Spacer(Modifier.height(12.dp))
        Button(onClick = onIrAServidor, modifier = Modifier.fillMaxWidth()) {
            Text("Inventario en servidor (Spring Boot)")
        }
    }
}
