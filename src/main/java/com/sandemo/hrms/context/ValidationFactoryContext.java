package com.sandemo.hrms.context;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.validation.Validation;
import javax.validation.Validator;

/**
 * @author Sankar M <sankar.mm30@gmail.com>
 *
 * Validation configuration for validating the bean in programmatically
 */
@Configuration
public class ValidationFactoryContext {

    @Bean("validator")
    public Validator validator() {

        return Validation.buildDefaultValidatorFactory().getValidator();
    }
}
