package cl.duoc.proyectosemestralapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import cl.duoc.proyectosemestralapp.ui.pantallas.PantallaServidor
import cl.duoc.proyectosemestralapp.ui.theme.ProyectoSemestralAppTheme
import cl.duoc.proyectosemestralapp.ui.viewmodel.CatalogoViewModel
import cl.duoc.proyectosemestralapp.ui.viewmodel.ServidorViewModel

class MainActivity : ComponentActivity() {
    private val modeloServidor: ServidorViewModel by viewModels()
    private val modeloCatalogo: CatalogoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProyectoSemestralAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PantallaServidor(
                        modeloVista = modeloServidor,
                        modeloCatalogo = modeloCatalogo
                    )
                }
            }
        }
    }
}
