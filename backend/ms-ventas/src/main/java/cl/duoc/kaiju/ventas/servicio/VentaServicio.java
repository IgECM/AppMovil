package cl.duoc.kaiju.ventas.servicio;

import cl.duoc.kaiju.ventas.dto.MovimientoPeticion;
import cl.duoc.kaiju.ventas.dto.ProductoRespuesta;
import cl.duoc.kaiju.ventas.dto.VentaPeticion;
import cl.duoc.kaiju.ventas.modelo.Venta;
import cl.duoc.kaiju.ventas.repositorio.VentaRepositorio;
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
public class VentaServicio {
    private final VentaRepositorio repositorio;
    private final RestTemplate clienteHttp = new RestTemplate();
    private final String urlProductos;
    private final String urlMovimientos;

    public VentaServicio(VentaRepositorio repositorio,
                         @Value("${kaiju.productos.url}") String urlProductos,
                         @Value("${kaiju.movimientos.url}") String urlMovimientos) {
        this.repositorio = repositorio;
        this.urlProductos = urlProductos;
        this.urlMovimientos = urlMovimientos;
    }

    public List<Venta> listar() {
        return repositorio.findAllByOrderByFechaRegistroDesc();
    }

    public Venta registrar(VentaPeticion datos) {
        ProductoRespuesta producto;
        try {
            producto = clienteHttp.getForObject(
                urlProductos + "/api/productos/" + datos.productoId(), ProductoRespuesta.class
            );
        } catch (HttpClientErrorException.NotFound excepcion) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado");
        } catch (ResourceAccessException excepcion) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Microservicio de productos no disponible");
        }
        if (producto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "No hubo respuesta de productos");
        }
        try {
            clienteHttp.postForObject(
                urlMovimientos + "/api/movimientos",
                new MovimientoPeticion(datos.productoId(), "VENTA", datos.cantidad()),
                String.class
            );
        } catch (HttpClientErrorException excepcion) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se pudo descontar el stock");
        } catch (ResourceAccessException excepcion) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Microservicio de movimientos no disponible");
        }
        Venta venta = new Venta();
        venta.setProductoId(producto.id());
        venta.setCodigoProducto(producto.codigo());
        venta.setCantidad(datos.cantidad());
        venta.setPrecioUnitario(producto.precio());
        venta.setTotal((long) producto.precio() * datos.cantidad());
        venta.setFechaRegistro(Instant.now());
        return repositorio.save(venta);
    }
}
