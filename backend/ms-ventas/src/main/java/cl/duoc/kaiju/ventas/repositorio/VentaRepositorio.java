package cl.duoc.kaiju.ventas.repositorio;

import cl.duoc.kaiju.ventas.modelo.Venta;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VentaRepositorio extends JpaRepository<Venta, Long> {
    List<Venta> findAllByOrderByFechaRegistroDesc();
}
