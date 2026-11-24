package cl.duoc.proyectosemestralapp.data.repositorio

import cl.duoc.proyectosemestralapp.data.remoto.ConfiguracionCatalogo
import cl.duoc.proyectosemestralapp.data.remoto.ProductoCatalogo
import cl.duoc.proyectosemestralapp.data.remoto.ServicioCatalogo

class CatalogoRepositorio(
    private val servicio: ServicioCatalogo = ConfiguracionCatalogo.servicio
) {
    suspend fun listar(): List<ProductoCatalogo> {
        return servicio.listar().productos
    }
}
