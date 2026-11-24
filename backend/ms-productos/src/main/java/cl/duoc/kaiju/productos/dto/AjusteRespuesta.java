package cl.duoc.kaiju.productos.dto;

public record AjusteRespuesta(
    Long productoId,
    String codigoProducto,
    String tipo,
    int cantidad,
    int stockAnterior,
    int stockPosterior
) {}
