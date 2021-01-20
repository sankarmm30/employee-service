package com.sandemo.hrms.exception;

public class TakeAwayClientRuntimeException extends TakeAwayRuntimeException {

    public TakeAwayClientRuntimeException(String message) {
        super(message);
    }

    public TakeAwayClientRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}