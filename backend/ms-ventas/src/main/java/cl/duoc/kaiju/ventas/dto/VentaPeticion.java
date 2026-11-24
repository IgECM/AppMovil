package cl.duoc.kaiju.ventas.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record VentaPeticion(
    @NotNull @Positive Long productoId,
    @NotNull @Positive Integer cantidad
) {}
