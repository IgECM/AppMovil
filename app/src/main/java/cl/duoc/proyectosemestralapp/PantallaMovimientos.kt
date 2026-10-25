
package cl.duoc.proyectosemestralapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.ExperimentalMaterial3Api


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaMovimientos(
    viewModel: ProductoViewModel,
    onVolver: () -> Unit
) {
    val productos by viewModel.listaProductos.collectAsState()

    var codigoSeleccionado by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("Entrada") }
    var error by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Actualizar stock",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = onVolver,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver al registro")
        }

        Spacer(Modifier.height(16.dp))

        Text("Selecciona un producto")

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 180.dp)
        ) {
            items(productos) { producto ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(producto.nombre)
                        Text("Stock actual: ${producto.stock}")
                    }

                    RadioButton(
                        selected =
                            codigoSeleccionado == producto.codigo,
                        onClick = {
                            codigoSeleccionado = producto.codigo
                            error = ""
                            mensaje = ""
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Text("Tipo de movimiento")

        Row {
            FilterChip(
                selected = tipo == "Entrada",
                onClick = {
                    tipo = "Entrada"
                    mensaje = ""
                },
                label = { Text("Añadir") }
            )

            Spacer(Modifier.width(8.dp))

            FilterChip(
                selected = tipo == "Salida",
                onClick = {
                    tipo = "Salida"
                    mensaje = ""
                },
                label = { Text("Quitar") }
            )
        }

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = cantidad,
            onValueChange = {
                cantidad = it.filter { c -> c.isDigit() }
                error = ""
                mensaje = ""
            },
            label = { Text("Cantidad") },
            isError = error.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        )

        if (error.isNotEmpty()) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error
            )
        }

        if (mensaje.isNotEmpty()) {
            Text(
                text = mensaje,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                val producto = productos.find {
                    it.codigo == codigoSeleccionado
                }
                val unidades = cantidad.toIntOrNull()

                when {
                    producto == null ->
                        error = "Selecciona un producto"

                    unidades == null || unidades <= 0 ->
                        error = "Ingresa una cantidad mayor que cero"

                    tipo == "Salida" &&
                            unidades > producto.stock ->
                        error = "No hay suficiente stock disponible"

                    else -> {
                        // Requiere implementar esta función
                        // en ProductoViewModel y actualizar Room.
                        viewModel.registrarMovimiento(
                            codigo = producto.codigo,
                            cantidad = unidades,
                            esEntrada = tipo == "Entrada"
                        )

                        mensaje = "Movimiento enviado para guardar"
                        cantidad = ""
                        error = ""
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrar movimiento")
        }
    }
}
