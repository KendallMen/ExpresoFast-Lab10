package com.expresofast.service;

import java.util.List;

import com.expresofast.dto.CrearEnvioDTO;
import com.expresofast.dto.EnvioDTO;
import com.expresofast.dto.EnvioRegistroDTO;

public interface EnvioService {

    List<EnvioDTO> obtenerTodos();

    EnvioDTO obtenerPorRastreo(String codigoRastreo);

    EnvioDTO registrar(CrearEnvioDTO datos);

    EnvioDTO actualizarEstado(Long id, String nuevoEstado);

    EnvioDTO registrarCompleto(EnvioRegistroDTO datos);

    boolean existeNumeroTracking(String numeroTracking);

}
