package com.sandemo.hrms.exception;

public class TakeAwayServerRuntimeException extends TakeAwayRuntimeException {

    public TakeAwayServerRuntimeException(String message) {
        super(message);
    }

    public TakeAwayServerRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}