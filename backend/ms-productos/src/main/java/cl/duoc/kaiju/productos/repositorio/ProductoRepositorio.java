package cl.duoc.kaiju.productos.repositorio;

import cl.duoc.kaiju.productos.modelo.Producto;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepositorio extends JpaRepository<Producto, Long> {
    Optional<Producto> findByCodigo(String codigo);
}
