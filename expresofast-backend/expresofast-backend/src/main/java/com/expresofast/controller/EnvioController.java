package com.expresofast.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.expresofast.dto.ActualizarEstadoDTO;
import com.expresofast.dto.CrearEnvioDTO;
import com.expresofast.dto.EnvioDTO;
import com.expresofast.dto.EnvioRegistroDTO;
import com.expresofast.dto.TrackingDisponibleDTO;
import com.expresofast.service.EnvioNoEncontradoException;
import com.expresofast.service.EnvioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/envios")
@CrossOrigin(origins = "http://localhost:4200")
public class EnvioController {

    private final EnvioService envioService;

    public EnvioController(EnvioService envioService) {
        this.envioService = envioService;
    }

    @GetMapping
    public List<EnvioDTO> obtenerTodos() {
        return envioService.obtenerTodos();
    }

    @GetMapping("/rastreo/{codigo}")
    public EnvioDTO obtenerPorRastreo(@PathVariable String codigo) {
        return envioService.obtenerPorRastreo(codigo);
    }

    @PostMapping
    public ResponseEntity<EnvioDTO> registrar(@Valid @RequestBody CrearEnvioDTO datos) {
        EnvioDTO creado = envioService.registrar(datos);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PostMapping("/completo")
    public ResponseEntity<EnvioDTO> registrarCompleto(@Valid @RequestBody EnvioRegistroDTO datos) {
        EnvioDTO creado = envioService.registrarCompleto(datos);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PatchMapping("/{id}/estado")
    public EnvioDTO actualizarEstado(@PathVariable Long id, @Valid @RequestBody ActualizarEstadoDTO datos) {
        return envioService.actualizarEstado(id, datos.estado());
    }

    @RestControllerAdvice
    static class ManejadorErrores {

        @ExceptionHandler(EnvioNoEncontradoException.class)
        public ResponseEntity<Map<String, String>> manejarNoEncontrado(EnvioNoEncontradoException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", ex.getMessage()));
        }

        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<Map<String, String>> manejarArgumentoInvalido(IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", ex.getMessage()));
        }
    }

    @GetMapping("/check-tracking/{trackingNumber}")
    public TrackingDisponibleDTO verificarTracking(@PathVariable String trackingNumber) {
        boolean existe = envioService.existeNumeroTracking(trackingNumber);
        return new TrackingDisponibleDTO(trackingNumber, existe);
    }
}
