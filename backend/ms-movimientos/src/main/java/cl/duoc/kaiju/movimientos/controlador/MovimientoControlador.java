package cl.duoc.kaiju.movimientos.controlador;

import cl.duoc.kaiju.movimientos.dto.MovimientoPeticion;
import cl.duoc.kaiju.movimientos.modelo.Movimiento;
import cl.duoc.kaiju.movimientos.servicio.MovimientoServicio;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/movimientos")
public class MovimientoControlador {
    private final MovimientoServicio servicio;

    public MovimientoControlador(MovimientoServicio servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<Movimiento> listar(@RequestParam(required = false) Long productoId) {
        return servicio.listar(productoId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Movimiento registrar(@Valid @RequestBody MovimientoPeticion datos) {
        return servicio.registrar(datos);
    }
}
