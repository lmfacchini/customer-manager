package com.client.manager.exception;

import lombok.Getter;

public class BusinessException extends RuntimeException{

    @Getter
    private final Object []args;

    public BusinessException(String message, Object...args) {
        super(message);
        this.args = args;
    }
}
