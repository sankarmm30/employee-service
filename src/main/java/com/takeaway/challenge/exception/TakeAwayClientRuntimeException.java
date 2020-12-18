package com.takeaway.challenge.exception;

public class TakeAwayClientRuntimeException extends TakeAwayRuntimeException {

    public TakeAwayClientRuntimeException(String message) {
        super(message);
    }

    public TakeAwayClientRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}