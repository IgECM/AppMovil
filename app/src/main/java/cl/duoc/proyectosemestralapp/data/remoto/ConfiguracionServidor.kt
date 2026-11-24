package cl.duoc.proyectosemestralapp.data.remoto

import java.io.IOException
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ConfiguracionServidor {
    private val cliente = OkHttpClient.Builder()
        .connectTimeout(6, TimeUnit.SECONDS)
        .readTimeout(45, TimeUnit.SECONDS)
        .writeTimeout(45, TimeUnit.SECONDS)
        .callTimeout(60, TimeUnit.SECONDS)
        .addInterceptor { cadena ->
            val direccion = DescubrimientoServidor.obtenerDireccion()
            val original = cadena.request()
            val nuevaUrl = original.url().newBuilder().host(direccion).build()
            val peticion = original.newBuilder().url(nuevaUrl).build()
            try {
                cadena.proceed(peticion)
            } catch (excepcion: IOException) {
                DescubrimientoServidor.olvidarDireccion(direccion)
                if (original.method() != "GET") throw excepcion
                val otraDireccion = DescubrimientoServidor.obtenerDireccion()
                val otraUrl = original.url().newBuilder().host(otraDireccion).build()
                val otraPeticion = original.newBuilder().url(otraUrl).build()
                try {
                    cadena.proceed(otraPeticion)
                } catch (segundaExcepcion: IOException) {
                    DescubrimientoServidor.olvidarDireccion(otraDireccion)
                    throw segundaExcepcion
                }
            }
        }
        .build()

    private fun crearCliente(puerto: Int): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://127.0.0.1:$puerto/")
            .client(cliente)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val productos: ServicioProductos = crearCliente(8081).create(ServicioProductos::class.java)
    val movimientos: ServicioMovimientos = crearCliente(8082).create(ServicioMovimientos::class.java)
    val ventas: ServicioVentas = crearCliente(8083).create(ServicioVentas::class.java)
}
