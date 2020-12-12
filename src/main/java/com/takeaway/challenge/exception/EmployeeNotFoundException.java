package com.takeaway.challenge.exception;

public class EmployeeNotFoundException extends TakeAwayClientRuntimeException {

    public static final String MESSAGE = "Could not find the employee in the database";

    public EmployeeNotFoundException () {

        super(MESSAGE);
    }
}