package com.expresofast.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.expresofast.model.EstadoEnvio;
import com.expresofast.model.Envio;

public interface EnvioRepository extends JpaRepository<Envio, Long> {

    Optional<Envio> findByCodigoRastreo(String codigoRastreo);

    List<Envio> findByEstado(EstadoEnvio estado);

    boolean existsByCodigoRastreo(String codigoRastreo);
}
