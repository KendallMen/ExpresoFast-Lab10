package com.expresofast.service;

import java.security.SecureRandom;
import java.time.Year;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.expresofast.dto.CrearEnvioDTO;
import com.expresofast.dto.EnvioDTO;
import com.expresofast.dto.EnvioRegistroDTO;
import com.expresofast.model.Envio;
import com.expresofast.model.EstadoEnvio;
import com.expresofast.model.Paquete;
import com.expresofast.repository.EnvioRepository;

@Service
public class EnvioServiceImpl implements EnvioService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final EnvioRepository envioRepository;

    public EnvioServiceImpl(EnvioRepository envioRepository) {
        this.envioRepository = envioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnvioDTO> obtenerTodos() {
        return envioRepository.findAll().stream().map(EnvioDTO::desde).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public EnvioDTO obtenerPorRastreo(String codigoRastreo) {
        Envio envio = envioRepository.findByCodigoRastreo(codigoRastreo)
                .orElseThrow(() -> new EnvioNoEncontradoException(
                        "No existe un envío con el código de rastreo " + codigoRastreo));
        return EnvioDTO.desde(envio);
    }

    @Override
    @Transactional
    public EnvioDTO registrar(CrearEnvioDTO datos) {
        String codigo = generarCodigoRastreoUnico();
        Envio envio = new Envio(codigo, datos.destinatario(), datos.direccionDestino(),
                datos.montoFlete(), EstadoEnvio.PENDIENTE);
        return EnvioDTO.desde(envioRepository.save(envio));
    }

    @Override
    @Transactional
    public EnvioDTO actualizarEstado(Long id, String nuevoEstado) {
        Envio envio = envioRepository.findById(id)
                .orElseThrow(() -> new EnvioNoEncontradoException("No existe un envío con id " + id));

        EstadoEnvio estado;
        try {
            estado = EstadoEnvio.valueOf(nuevoEstado.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Estado inválido: " + nuevoEstado
                    + ". Valores permitidos: PENDIENTE, EN_TRANSITO, ENTREGADO, CANCELADO");
        }

        envio.setEstado(estado);
        return EnvioDTO.desde(envioRepository.save(envio));
    }

    private String generarCodigoRastreoUnico() {
        String codigo;
        do {
            int numero = 1000 + RANDOM.nextInt(9000);
            codigo = "EXP-" + Year.now().getValue() + "-" + numero;
        } while (envioRepository.existsByCodigoRastreo(codigo));
        return codigo;
    }

    @Override
    @Transactional
    public EnvioDTO registrarCompleto(EnvioRegistroDTO datos) {
        if (envioRepository.existsByCodigoRastreo(datos.numeroTracking())) {
            throw new IllegalArgumentException(
                    "El número de rastreo " + datos.numeroTracking() + " ya está en uso");
        }

        Envio envio = new Envio(datos.numeroTracking(), datos.destinatario(),
                datos.direccionDestino(), datos.montoFlete(), EstadoEnvio.PENDIENTE);
        envio.setFechaDespacho(datos.fechaDespacho());
        envio.setFechaEntregaEstimada(datos.fechaEntregaEstimada());
        datos.paquetes().forEach(p ->
                envio.agregarPaquete(new Paquete(p.descripcion(), p.pesoKg())));

        return EnvioDTO.desde(envioRepository.save(envio));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeNumeroTracking(String numeroTracking) {
        return envioRepository.existsByCodigoRastreo(numeroTracking);
    }

}
