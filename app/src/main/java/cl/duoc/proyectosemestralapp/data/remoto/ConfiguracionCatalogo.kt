package cl.duoc.proyectosemestralapp.data.remoto

import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ConfiguracionCatalogo {
    private val cliente = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    val servicio: ServicioCatalogo = Retrofit.Builder()
        .baseUrl("https://dummyjson.com/")
        .client(cliente)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ServicioCatalogo::class.java)
}
