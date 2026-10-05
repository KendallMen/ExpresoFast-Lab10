package com.expresofast.dto;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import com.expresofast.dto.PaqueteDTO;

public record EnvioRegistroDTO(
        @NotBlank(message = "El número de rastreo es obligatorio")
        String numeroTracking,

        @NotBlank(message = "El destinatario es obligatorio")
        String destinatario,

        @NotBlank(message = "La dirección de destino es obligatoria")
        String direccionDestino,

        @NotNull(message = "El monto de flete es obligatorio")
        @Positive(message = "El monto de flete debe ser mayor a 0")
        Double montoFlete,

        @NotNull(message = "La fecha de despacho es obligatoria")
        LocalDate fechaDespacho,

        @NotNull(message = "La fecha de entrega estimada es obligatoria")
        LocalDate fechaEntregaEstimada,

        @NotEmpty(message = "Debe incluir al menos un paquete")
        @Valid
        List<PaqueteDTO> paquetes) {
}
