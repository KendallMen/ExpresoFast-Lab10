package com.expresofast.dto;

import jakarta.validation.constraints.NotBlank;

public record ActualizarEstadoDTO(
        @NotBlank(message = "El nuevo estado es obligatorio")
        String estado) {
}
