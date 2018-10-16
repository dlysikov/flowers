package lu.luxtrust.flowers.validation.validator;

import lu.luxtrust.flowers.validation.annotation.Email;
import org.apache.commons.validator.routines.EmailValidator;

import javax.validation.ConstraintValidatorContext;

public class CommonsEmailValidator extends AbstractConstraintValidator<Email, String> {

    private EmailValidator realValidator = EmailValidator.getInstance();

    @Override
    public void initialize(Email email) {

    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return s == null || s.isEmpty() || realValidator.isValid(s);
    }
}