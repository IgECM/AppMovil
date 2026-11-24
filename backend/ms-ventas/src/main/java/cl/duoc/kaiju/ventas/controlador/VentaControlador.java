package cl.duoc.kaiju.ventas.controlador;

import cl.duoc.kaiju.ventas.dto.VentaPeticion;
import cl.duoc.kaiju.ventas.modelo.Venta;
import cl.duoc.kaiju.ventas.servicio.VentaServicio;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ventas")
public class VentaControlador {
    private final VentaServicio servicio;

    public VentaControlador(VentaServicio servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<Venta> listar() {
        return servicio.listar();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Venta registrar(@Valid @RequestBody VentaPeticion datos) {
        return servicio.registrar(datos);
    }
}
