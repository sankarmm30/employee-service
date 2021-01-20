package com.sandemo.hrms.factory;

import com.sandemo.hrms.dto.request.DepartmentRequestDto;
import com.sandemo.hrms.exception.GenericClientRuntimeException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.validation.ConstraintViolationException;
import javax.validation.Validation;

/**
 * @author Sankar M <sankar.mm30@gmail.com>
 */
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

    @Test(expected = GenericClientRuntimeException.class)
    public void testWhenInputObjectIsNull() {

        validationFactoryService.validObject(null);
    }
}
