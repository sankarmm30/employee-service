package com.takeaway.challenge.exception;

public class DepartmentNotFoundException extends TakeAwayClientRuntimeException {

    public static final String MESSAGE = "Could not find the department in the database";

    public DepartmentNotFoundException() {

        super(MESSAGE);
    }
}