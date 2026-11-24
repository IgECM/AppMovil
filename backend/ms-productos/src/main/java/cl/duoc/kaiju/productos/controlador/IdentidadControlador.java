package cl.duoc.kaiju.productos.controlador;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IdentidadControlador {
    @GetMapping(value = "/api/identidad", produces = "text/plain")
    public String identificar() {
        return "KAIJU_SERVIDOR";
    }
}
