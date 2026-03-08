package com.exemplo.moneytransferapi.exception;

public class UsuarioNotFoundException  extends RuntimeException {
    public UsuarioNotFoundException(String message) {
        super(message);
    }
}
