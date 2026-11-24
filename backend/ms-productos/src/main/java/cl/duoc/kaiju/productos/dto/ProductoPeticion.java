package cl.duoc.kaiju.productos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record ProductoPeticion(
    @NotBlank String codigo,
    @NotBlank String nombre,
    String descripcion,
    @NotBlank String categoria,
    @NotNull @PositiveOrZero Integer precio,
    @NotNull @PositiveOrZero Integer stock,
    @NotNull @PositiveOrZero Integer stockMinimo
) {}
