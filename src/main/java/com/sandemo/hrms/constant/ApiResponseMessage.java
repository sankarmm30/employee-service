package com.sandemo.hrms.constant;

/**
 * @author Sankar M <sankar.mm30@gmail.com>
 *
 * This enum holds the messages that are send over api response for both success or error cases
 */
public enum ApiResponseMessage {

    EMP_CREATE_MESSAGE("Employee has been created"),
    EMP_UPDATE_MESSAGE("Employee details have been updated"),
    EMP_DELETE_MESSAGE("Employee has been deleted"),

    DEP_CREATE_MESSAGE("Department has been created");

    private final String value;

    ApiResponseMessage(String value) {
        this.value = value;
    }

    /**
     * Returns the value
     *
     * @return
     */
    public String getValue() {

        return this.value;
    }
}
