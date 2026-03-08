package com.exemplo.moneytransferapi.exception;

public class SaldoInsuficienteExeception extends RuntimeException{
    public SaldoInsuficienteExeception(String message) {
        super(message);
    }
}
