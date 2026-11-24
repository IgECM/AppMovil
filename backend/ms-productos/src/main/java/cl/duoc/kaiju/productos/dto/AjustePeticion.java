package cl.duoc.kaiju.productos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AjustePeticion(
    @NotBlank String tipo,
    @NotNull @Positive Integer cantidad
) {}
