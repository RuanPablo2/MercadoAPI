package com.RuanPablo2.mercadoapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class OrderStatusException extends RuntimeException {
    private final String errorCode;

    public OrderStatusException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public OrderStatusException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}