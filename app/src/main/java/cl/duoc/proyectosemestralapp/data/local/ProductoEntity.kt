package cl.duoc.proyectosemestralapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "productos")
data class ProductoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, val codigo: String,
    val nombre: String, val descripcion: String,
    val categoria: String, val precio: Int,
    val stock: Int, val stockMinimo: Int
)