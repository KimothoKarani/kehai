package com.kehai.api.common.exception;

public abstract class KehaiException extends RuntimeException {

    protected KehaiException(String message) {
        super(message);
    }
}
