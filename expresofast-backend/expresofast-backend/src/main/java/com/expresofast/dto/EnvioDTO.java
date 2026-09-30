package com.expresofast.dto;

import java.time.LocalDateTime;

import com.expresofast.model.Envio;

public record EnvioDTO(
        Long id,
        String codigoRastreo,
        String destinatario,
        String direccionDestino,
        Double montoFlete,
        String estado,
        LocalDateTime fechaCreacion) {

    public static EnvioDTO desde(Envio envio) {
        return new EnvioDTO(
                envio.getId(),
                envio.getCodigoRastreo(),
                envio.getDestinatario(),
                envio.getDireccionDestino(),
                envio.getMontoFlete(),
                envio.getEstado().name(),
                envio.getFechaCreacion());
    }
}
