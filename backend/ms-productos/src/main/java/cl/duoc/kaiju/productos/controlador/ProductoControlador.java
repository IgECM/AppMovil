package cl.duoc.kaiju.productos.controlador;

import cl.duoc.kaiju.productos.dto.AjustePeticion;
import cl.duoc.kaiju.productos.dto.AjusteRespuesta;
import cl.duoc.kaiju.productos.dto.ProductoPeticion;
import cl.duoc.kaiju.productos.modelo.Producto;
import cl.duoc.kaiju.productos.servicio.ProductoServicio;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/productos")
public class ProductoControlador {
    private final ProductoServicio servicio;

    public ProductoControlador(ProductoServicio servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<Producto> listar() {
        return servicio.listar();
    }

    @GetMapping("/{id}")
    public Producto buscar(@PathVariable Long id) {
        return servicio.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Producto crear(@Valid @RequestBody ProductoPeticion datos) {
        return servicio.crear(datos);
    }

    @PutMapping("/{id}")
    public Producto actualizar(@PathVariable Long id, @Valid @RequestBody ProductoPeticion datos) {
        return servicio.actualizar(id, datos);
    }

    @PostMapping("/{id}/ajustes")
    public AjusteRespuesta ajustarStock(@PathVariable Long id, @Valid @RequestBody AjustePeticion datos) {
        return servicio.ajustarStock(id, datos);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        servicio.eliminar(id);
    }
}
