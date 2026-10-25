package cl.duoc.proyectosemestralapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import cl.duoc.proyectosemestralapp.ui.theme.ProyectoSemestralAppTheme

class MainActivity : ComponentActivity() {
    private val viewModel: ProductoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProyectoSemestralAppTheme {

                var pantalla by remember { mutableStateOf("registro") }

                when (pantalla) {
                    "registro" -> {
                        PantallaRegistro(
                            viewModel = viewModel,
                            onIrALista = { pantalla = "lista" },
                            onIrAMovimientos = { pantalla = "movimientos" }
                        )
                    }

                    "lista" -> {
                        PantallaLista(
                            viewModel = viewModel,
                            onVolverRegistro = { pantalla = "registro" }
                        )
                    }

                    "movimientos" -> {
                        PantallaMovimientos(
                            viewModel = viewModel,
                            onVolver = { pantalla = "registro" }
                        )
                    }
                }

            }
        }
    }
}