package com.takeaway.challenge.exception;

public class InvalidEmailIdException extends TakeAwayClientRuntimeException {

    public static final String MESSAGE = "Email id is invalid";

    public InvalidEmailIdException() {

        super(MESSAGE);
    }
}