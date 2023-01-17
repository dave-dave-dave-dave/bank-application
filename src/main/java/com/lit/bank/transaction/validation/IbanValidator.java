package com.lit.bank.transaction.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.validator.routines.IBANValidator;

public class IbanValidator implements ConstraintValidator<ValidIBAN, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        return IBANValidator.getInstance().isValid(value);
    }
}
