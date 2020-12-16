package com.takeaway.challenge.dto.request;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.time.LocalDate;

@RunWith(JUnit4.class)
public class PutEmployeeRequestDtoTest {

    private static final String NAME = "test";
    private static final String EMAIL = "test@test.com";
    private static final Long DEPART_ID = 1L;

    @Test
    public void testIsValid() {

        // Valid Tests
        Assert.assertTrue(getPutEmployeeRequestDto(null, null, null, DEPART_ID).isValid());
        Assert.assertTrue(getPutEmployeeRequestDto(null, null, LocalDate.now(), DEPART_ID).isValid());
        Assert.assertTrue(getPutEmployeeRequestDto(null, EMAIL, LocalDate.now(), DEPART_ID).isValid());
        Assert.assertTrue(getPutEmployeeRequestDto(NAME, EMAIL, LocalDate.now(), DEPART_ID).isValid());

        //Invalid Test

        Assert.assertFalse(getPutEmployeeRequestDto(null, null, null, null).isValid());
        Assert.assertFalse(getPutEmployeeRequestDto(" ", null, null, null).isValid());
        Assert.assertFalse(getPutEmployeeRequestDto(null, " ", null, null).isValid());
    }

    @Test
    public void testIsValidNameWhenNotNull() {

        // Valid Tests
        Assert.assertTrue(getPutEmployeeRequestDto(NAME, null, null, null).isValidNameWhenNotNull());
        Assert.assertTrue(getPutEmployeeRequestDto(null, null, null, null).isValidNameWhenNotNull());

        //Invalid Test

        Assert.assertFalse(getPutEmployeeRequestDto("", null, null, null).isValidNameWhenNotNull());
        Assert.assertFalse(getPutEmployeeRequestDto(" ", null, null, null).isValidNameWhenNotNull());
    }

    @Test
    public void testIsValidEmailWhenNotNull() {

        // Valid Tests
        Assert.assertTrue(getPutEmployeeRequestDto(null, NAME, null, null).isValidEmailWhenNotNull());
        Assert.assertTrue(getPutEmployeeRequestDto(null, null, null, null).isValidEmailWhenNotNull());

        //Invalid Test

        Assert.assertFalse(getPutEmployeeRequestDto(null, "", null, null).isValidEmailWhenNotNull());
        Assert.assertFalse(getPutEmployeeRequestDto(null, " ", null, null).isValidEmailWhenNotNull());
    }

    private PutEmployeeRequestDto getPutEmployeeRequestDto(final String name, final String email, final LocalDate dateOfBirth,
                                                           final Long departmentId) {

        return PutEmployeeRequestDto.builder()
                .name(name)
                .email(email)
                .dateOfBirth(dateOfBirth)
                .departmentId(departmentId)
                .build();
    }
}
