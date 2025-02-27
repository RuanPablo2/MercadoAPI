package com.RuanPablo2.mercadoapi.exception;

public class ForbiddenException extends RuntimeException {
    private String errorCode;

    public ForbiddenException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ForbiddenException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}