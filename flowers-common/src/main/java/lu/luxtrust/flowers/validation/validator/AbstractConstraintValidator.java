package lu.luxtrust.flowers.validation.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.annotation.Annotation;

public abstract class AbstractConstraintValidator<A extends Annotation, T> implements ConstraintValidator<A,T> {

    protected boolean valid = true;

    protected void addConstraintViolation(String property, ConstraintValidatorContext ctx, String msgTemplate) {
        ctx.disableDefaultConstraintViolation();
        ctx.buildConstraintViolationWithTemplate("{"+ msgTemplate + "}")
                .addPropertyNode(property)
                .addConstraintViolation();
        valid = false;
    }
}
