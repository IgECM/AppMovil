package cl.duoc.proyectosemestralapp.data.remoto

import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ConfiguracionServidor {
    const val direccionComputador = "192.168.1.19"

    private val cliente = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(45, TimeUnit.SECONDS)
        .writeTimeout(45, TimeUnit.SECONDS)
        .callTimeout(60, TimeUnit.SECONDS)
        .build()

    private fun crearCliente(puerto: Int): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://$direccionComputador:$puerto/")
            .client(cliente)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val productos: ServicioProductos = crearCliente(8081).create(ServicioProductos::class.java)
    val movimientos: ServicioMovimientos = crearCliente(8082).create(ServicioMovimientos::class.java)
    val ventas: ServicioVentas = crearCliente(8083).create(ServicioVentas::class.java)
}
