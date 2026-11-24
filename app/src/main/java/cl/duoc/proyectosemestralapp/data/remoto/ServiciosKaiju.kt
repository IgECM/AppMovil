package cl.duoc.proyectosemestralapp.data.remoto

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ServicioProductos {
    @GET("api/productos")
    suspend fun listar(): List<ProductoRemoto>

    @POST("api/productos")
    suspend fun crear(@Body datos: ProductoPeticion): ProductoRemoto

    @PUT("api/productos/{id}")
    suspend fun actualizar(@Path("id") id: Long, @Body datos: ProductoPeticion): ProductoRemoto

    @DELETE("api/productos/{id}")
    suspend fun eliminar(@Path("id") id: Long)
}

interface ServicioMovimientos {
    @GET("api/movimientos")
    suspend fun listar(): List<MovimientoRemoto>

    @POST("api/movimientos")
    suspend fun registrar(@Body datos: MovimientoPeticion): MovimientoRemoto
}

interface ServicioVentas {
    @GET("api/ventas")
    suspend fun listar(): List<VentaRemota>

    @POST("api/ventas")
    suspend fun registrar(@Body datos: VentaPeticion): VentaRemota
}
