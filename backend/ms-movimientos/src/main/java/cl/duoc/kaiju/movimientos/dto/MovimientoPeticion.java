package cl.duoc.kaiju.movimientos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record MovimientoPeticion(
    @NotNull @Positive Long productoId,
    @NotBlank String tipo,
    @NotNull @Positive Integer cantidad
) {}
