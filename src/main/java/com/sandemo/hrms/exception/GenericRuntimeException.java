package com.sandemo.hrms.exception;

public class TakeAwayRuntimeException extends RuntimeException {

    public TakeAwayRuntimeException(String message) {
        super(message);
    }

    public TakeAwayRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}