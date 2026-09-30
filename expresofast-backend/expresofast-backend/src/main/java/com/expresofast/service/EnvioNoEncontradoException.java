package com.expresofast.service;

public class EnvioNoEncontradoException extends RuntimeException {
    public EnvioNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
