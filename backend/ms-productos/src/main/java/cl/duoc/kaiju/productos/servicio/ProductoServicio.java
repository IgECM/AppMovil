package cl.duoc.kaiju.productos.servicio;

import cl.duoc.kaiju.productos.dto.AjustePeticion;
import cl.duoc.kaiju.productos.dto.AjusteRespuesta;
import cl.duoc.kaiju.productos.dto.ProductoPeticion;
import cl.duoc.kaiju.productos.modelo.Producto;
import cl.duoc.kaiju.productos.repositorio.ProductoRepositorio;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProductoServicio {
    private final ProductoRepositorio repositorio;

    public ProductoServicio(ProductoRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    public List<Producto> listar() {
        return repositorio.findAll();
    }

    public Producto buscar(Long id) {
        return repositorio.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
    }

    @Transactional
    public Producto crear(ProductoPeticion datos) {
        if (repositorio.findByCodigo(datos.codigo().trim()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El código ya existe");
        }
        Producto producto = new Producto();
        completarDatos(producto, datos);
        return repositorio.save(producto);
    }

    @Transactional
    public Producto actualizar(Long id, ProductoPeticion datos) {
        Producto producto = buscar(id);
        repositorio.findByCodigo(datos.codigo().trim()).ifPresent(existente -> {
            if (!existente.getId().equals(id)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "El código ya existe");
            }
        });
        completarDatos(producto, datos);
        return repositorio.save(producto);
    }

    @Transactional
    public AjusteRespuesta ajustarStock(Long id, AjustePeticion datos) {
        Producto producto = buscar(id);
        String tipo = datos.tipo().trim().toUpperCase();
        if (!tipo.equals("ENTRADA") && !tipo.equals("SALIDA") && !tipo.equals("VENTA")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo de movimiento inválido");
        }
        int anterior = producto.getStock();
        int cantidad = datos.cantidad();
        int posterior;
        if (tipo.equals("ENTRADA")) {
            if (cantidad > Integer.MAX_VALUE - anterior) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La cantidad supera el máximo permitido");
            }
            posterior = anterior + cantidad;
        } else {
            if (cantidad > anterior) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stock insuficiente");
            }
            posterior = anterior - cantidad;
        }
        producto.setStock(posterior);
        repositorio.save(producto);
        return new AjusteRespuesta(id, producto.getCodigo(), tipo, cantidad, anterior, posterior);
    }

    @Transactional
    public void eliminar(Long id) {
        Producto producto = buscar(id);
        repositorio.delete(producto);
    }

    private void completarDatos(Producto producto, ProductoPeticion datos) {
        producto.setCodigo(datos.codigo().trim());
        producto.setNombre(datos.nombre().trim());
        producto.setDescripcion(datos.descripcion() == null ? "" : datos.descripcion().trim());
        producto.setCategoria(datos.categoria().trim());
        producto.setPrecio(datos.precio());
        producto.setStock(datos.stock());
        producto.setStockMinimo(datos.stockMinimo());
    }
}
