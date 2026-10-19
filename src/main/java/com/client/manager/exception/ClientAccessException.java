package com.client.manager.exception;

import lombok.Getter;

public class ClientAccessException extends RuntimeException{

    @Getter
    private final int status;

    public ClientAccessException(String message, int status){
        super(message);
        this.status =status;

    }
}
