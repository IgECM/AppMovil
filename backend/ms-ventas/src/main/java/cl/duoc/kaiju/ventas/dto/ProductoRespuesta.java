package cl.duoc.kaiju.ventas.dto;

public record ProductoRespuesta(
    Long id,
    String codigo,
    String nombre,
    String descripcion,
    String categoria,
    int precio,
    int stock,
    int stockMinimo
) {}
