package cl.duoc.kaiju.movimientos.repositorio;

import cl.duoc.kaiju.movimientos.modelo.Movimiento;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimientoRepositorio extends JpaRepository<Movimiento, Long> {
    List<Movimiento> findByProductoIdOrderByFechaRegistroDesc(Long productoId);
    List<Movimiento> findAllByOrderByFechaRegistroDesc();
}
