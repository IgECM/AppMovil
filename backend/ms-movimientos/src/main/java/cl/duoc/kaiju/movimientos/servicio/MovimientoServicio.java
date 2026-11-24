package cl.duoc.kaiju.movimientos.servicio;

import cl.duoc.kaiju.movimientos.dto.AjustePeticion;
import cl.duoc.kaiju.movimientos.dto.AjusteRespuesta;
import cl.duoc.kaiju.movimientos.dto.MovimientoPeticion;
import cl.duoc.kaiju.movimientos.modelo.Movimiento;
import cl.duoc.kaiju.movimientos.repositorio.MovimientoRepositorio;
import java.time.Instant;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MovimientoServicio {
    private final MovimientoRepositorio repositorio;
    private final RestTemplate clienteHttp;
    private final String urlProductos;

    public MovimientoServicio(MovimientoRepositorio repositorio,
                              @Value("${kaiju.productos.url}") String urlProductos) {
        this.repositorio = repositorio;
        this.clienteHttp = new RestTemplate();
        this.urlProductos = urlProductos;
    }

    public List<Movimiento> listar(Long productoId) {
        if (productoId == null) {
            return repositorio.findAllByOrderByFechaRegistroDesc();
        }
        return repositorio.findByProductoIdOrderByFechaRegistroDesc(productoId);
    }

    public Movimiento registrar(MovimientoPeticion datos) {
        String tipo = datos.tipo().trim().toUpperCase();
        if (!tipo.equals("ENTRADA") && !tipo.equals("SALIDA") && !tipo.equals("VENTA")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo inválido");
        }
        String url = urlProductos + "/api/productos/" + datos.productoId() + "/ajustes";
        AjusteRespuesta respuesta;
        try {
            respuesta = clienteHttp.postForObject(
                url, new AjustePeticion(tipo, datos.cantidad()), AjusteRespuesta.class
            );
        } catch (HttpClientErrorException excepcion) {
            if (excepcion.getStatusCode().value() == 404) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado");
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se pudo actualizar el stock");
        } catch (ResourceAccessException excepcion) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Microservicio de productos no disponible");
        }
        if (respuesta == null) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "No hubo respuesta de productos");
        }
        Movimiento movimiento = new Movimiento();
        movimiento.setProductoId(respuesta.productoId());
        movimiento.setCodigoProducto(respuesta.codigoProducto());
        movimiento.setTipo(respuesta.tipo());
        movimiento.setCantidad(respuesta.cantidad());
        movimiento.setStockAnterior(respuesta.stockAnterior());
        movimiento.setStockPosterior(respuesta.stockPosterior());
        movimiento.setFechaRegistro(Instant.now());
        return repositorio.save(movimiento);
    }
}
