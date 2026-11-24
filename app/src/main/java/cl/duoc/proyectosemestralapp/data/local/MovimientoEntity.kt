package cl.duoc.proyectosemestralapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movimientos")
data class MovimientoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productoId: Int,
    val codigoProducto: String,
    val tipo: String,
    val cantidad: Int,
    val stockAnterior: Int,
    val stockPosterior: Int,
    val fechaRegistro: Long
)
