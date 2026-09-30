package com.expresofast.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.expresofast.model.EstadoEnvio;
import com.expresofast.model.Envio;
import com.expresofast.repository.EnvioRepository;

@Component
public class DataSeeder implements CommandLineRunner {

    private final EnvioRepository envioRepository;

    public DataSeeder(EnvioRepository envioRepository) {
        this.envioRepository = envioRepository;
    }

    @Override
    public void run(String... args) {
        if (envioRepository.count() > 0) {
            return;
        }

        envioRepository.save(new Envio("EXP-2026-1001", "Luis Fernandez", "Cartago, Paraiso", 4500.0, EstadoEnvio.PENDIENTE));
        envioRepository.save(new Envio("EXP-2026-1002", "Sofia Jimenez", "San Jose, Curridabat", 7500.0, EstadoEnvio.EN_TRANSITO));
        envioRepository.save(new Envio("EXP-2026-1003", "Ana Mora", "Heredia, Barva", 3200.0, EstadoEnvio.PENDIENTE));
        envioRepository.save(new Envio("EXP-2026-1004", "Diego Castro", "Alajuela, Centro", 6100.0, EstadoEnvio.ENTREGADO));
        envioRepository.save(new Envio("EXP-2026-1005", "Laura Rojas", "Cartago, Tres Rios", 2800.0, EstadoEnvio.CANCELADO));
    }
}
