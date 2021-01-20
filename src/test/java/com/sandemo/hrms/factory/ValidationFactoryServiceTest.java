package com.takeaway.challenge.factory;

import com.takeaway.challenge.dto.request.DepartmentRequestDto;
import com.takeaway.challenge.exception.TakeAwayClientRuntimeException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.validation.ConstraintViolationException;
import javax.validation.Validation;

@RunWith(JUnit4.class)
public class ValidationFactoryServiceTest {

    private static final String NAME = "test";

    private ValidationFactoryService validationFactoryService = new ValidationFactoryService(
            Validation.buildDefaultValidatorFactory().getValidator());

    @Test
    public void testValidateBeanValid() {

        validationFactoryService.validObject(DepartmentRequestDto.builder().name(NAME).build());

        Assert.assertTrue("Always True", true);
    }

    @Test(expected = ConstraintViolationException.class)
    public void testWhenBeanValidationFailsOne() {

        validationFactoryService.validObject(DepartmentRequestDto.builder().name(null).build());
    }

    @Test(expected = ConstraintViolationException.class)
    public void testWhenBeanValidationFailsTwo() {

        validationFactoryService.validObject(DepartmentRequestDto.builder().name(" ").build());
    }

    @Test(expected = TakeAwayClientRuntimeException.class)
    public void testWhenInputObjectIsNull() {

        validationFactoryService.validObject(null);
    }
}
