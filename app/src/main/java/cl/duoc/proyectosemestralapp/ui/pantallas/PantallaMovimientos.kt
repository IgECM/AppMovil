package cl.duoc.proyectosemestralapp.ui.pantallas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import cl.duoc.proyectosemestralapp.ui.viewmodel.ProductoViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaMovimientos(
    modeloVista: ProductoViewModel,
    onVolver: () -> Unit
) {
    val productos by modeloVista.listaProductos.collectAsState()
    val movimientos by modeloVista.listaMovimientos.collectAsState()

    var idSeleccionado by remember { mutableStateOf<Int?>(null) }
    var cantidad by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("Entrada") }
    var error by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
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
                            idSeleccionado == producto.id,
                        onClick = {
                            idSeleccionado = producto.id
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
                label = { Text("Ingresar") }
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
                    it.id == idSeleccionado
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
                        modeloVista.registrarMovimiento(
                            productoId = producto.id,
                            cantidad = unidades,
                            esEntrada = tipo == "Entrada"
                        ) { errorGuardado ->
                            if (errorGuardado == null) {
                                mensaje = "Movimiento registrado correctamente"
                                cantidad = ""
                                error = ""
                            } else {
                                error = errorGuardado
                                mensaje = ""
                            }
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrar movimiento")
        }

        Spacer(Modifier.height(24.dp))
        Text("Historial de movimientos", style = MaterialTheme.typography.titleMedium)
        if (movimientos.isEmpty()) {
            Text("Todavía no se han registrado movimientos")
        } else {
            val formatoFecha = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "CL")) }
            movimientos.take(10).forEach { movimiento ->
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                Text("${movimiento.tipo}: ${movimiento.codigoProducto} - ${movimiento.cantidad} unidades")
                Text("Stock: ${movimiento.stockAnterior} → ${movimiento.stockPosterior}")
                Text(formatoFecha.format(Date(movimiento.fechaRegistro)), style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
