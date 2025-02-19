package com.RuanPablo2.mercadoapi.exception;

import lombok.Data;

import java.util.Date;

@Data
public class ErrorResponse {

    private Date timestamp;
    private int status;
    private String error;
    private String message;
    private String errorCode;
    private String path;

    public ErrorResponse(Date timestamp, int status, String error, String message, String errorCode, String path) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.errorCode = errorCode;
        this.path = path;
    }
}