package cl.duoc.proyectosemestralapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [ProductoEntity::class, MovimientoEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productoDao(): ProductoDao
    abstract fun movimientoDao(): MovimientoDao

    companion object {
        @Volatile private var INSTANCIA: AppDatabase? = null

        private val MIGRACION_1_2 = object : Migration(1, 2) {
            override fun migrate(baseSql: SupportSQLiteDatabase) {
                baseSql.execSQL("""
                    CREATE TABLE IF NOT EXISTS `movimientos` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `productoId` INTEGER NOT NULL,
                        `codigoProducto` TEXT NOT NULL,
                        `tipo` TEXT NOT NULL,
                        `cantidad` INTEGER NOT NULL,
                        `stockAnterior` INTEGER NOT NULL,
                        `stockPosterior` INTEGER NOT NULL,
                        `fechaRegistro` INTEGER NOT NULL
                    )
                """.trimIndent())
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCIA ?: synchronized(this) {
                INSTANCIA ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "kaiju_database"
                ).addMigrations(MIGRACION_1_2).build().also { INSTANCIA = it }
            }
        }
    }
}
