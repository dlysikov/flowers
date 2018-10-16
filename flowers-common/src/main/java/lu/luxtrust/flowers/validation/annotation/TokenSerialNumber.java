package lu.luxtrust.flowers.validation.annotation;

import lu.luxtrust.flowers.validation.validator.TokenSerialNumberContentValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE,  ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TokenSerialNumberContentValidator.class)
public @interface TokenSerialNumber {

    String message() default "{tsn}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
