package com.sandemo.hrms.exception;

/**
 * @author Sankar M <sankar.mm30@gmail.com>
 */
public class EmailIdAlreadyExistsException extends GenericClientRuntimeException {

    public static final String MESSAGE = "Email id is already exists";

    public EmailIdAlreadyExistsException() {

        super(MESSAGE);
    }
}