package com.sandemo.hrms.exception;

/**
 * @author Sankar M <sankar.mm30@gmail.com>
 */
public class DepartmentNotFoundException extends GenericClientRuntimeException {

    public static final String MESSAGE = "Could not find the department in the database";

    public DepartmentNotFoundException() {

        super(MESSAGE);
    }
}