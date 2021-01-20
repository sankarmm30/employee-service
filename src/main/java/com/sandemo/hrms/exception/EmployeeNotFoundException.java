package com.sandemo.hrms.exception;

/**
 * @author Sankar M <sankar.mm30@gmail.com>
 */
public class EmployeeNotFoundException extends GenericClientRuntimeException {

    public static final String MESSAGE = "Could not find the employee in the database";

    public EmployeeNotFoundException () {

        super(MESSAGE);
    }
}