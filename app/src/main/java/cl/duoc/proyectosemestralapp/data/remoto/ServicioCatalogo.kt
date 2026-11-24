package cl.duoc.proyectosemestralapp.data.remoto

import retrofit2.http.GET
import retrofit2.http.Query

interface ServicioCatalogo {
    @GET("products")
    suspend fun listar(
        @Query("limit") limite: Int = 10,
        @Query("select") campos: String = "title,description,category,price,stock"
    ): RespuestaCatalogo
}
