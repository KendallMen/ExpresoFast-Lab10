package com.expresofast.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import com.expresofast.model.Paquete;

public record PaqueteDTO(
        Long id,

        @NotBlank(message = "La descripción del paquete es obligatoria")
        String descripcion,

        @NotNull(message = "El peso del paquete es obligatorio")
        @Positive(message = "El peso debe ser mayor a 0")
        BigDecimal pesoKg) {

    public static PaqueteDTO desde(Paquete p) {
        return new PaqueteDTO(p.getId(), p.getDescripcion(), p.getPesoKg());
    }
}
