package cl.duoc.proyectosemestralapp.data.remoto

import com.google.gson.annotations.SerializedName

data class ProductoCatalogo(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val nombre: String,
    @SerializedName("description") val descripcion: String,
    @SerializedName("category") val categoria: String,
    @SerializedName("price") val precio: Double,
    @SerializedName("stock") val stock: Int
)

data class RespuestaCatalogo(
    @SerializedName("products") val productos: List<ProductoCatalogo>
)
