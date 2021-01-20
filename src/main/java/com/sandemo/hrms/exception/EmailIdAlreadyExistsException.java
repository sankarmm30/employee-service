package com.takeaway.challenge.exception;

public class EmailIdAlreadyExistsException extends TakeAwayClientRuntimeException {

    public static final String MESSAGE = "Email id is already exists";

    public EmailIdAlreadyExistsException() {

        super(MESSAGE);
    }
}