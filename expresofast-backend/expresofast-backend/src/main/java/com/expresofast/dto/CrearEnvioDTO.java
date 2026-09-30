package com.expresofast.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CrearEnvioDTO(
        @NotBlank(message = "El destinatario es obligatorio")
        String destinatario,

        @NotBlank(message = "La dirección de destino es obligatoria")
        String direccionDestino,

        @NotNull(message = "El monto de flete es obligatorio")
        @Positive(message = "El monto de flete debe ser mayor a 0")
        Double montoFlete) {
}
