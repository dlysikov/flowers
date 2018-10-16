package lu.luxtrust.flowers.validation.annotation;

import lu.luxtrust.flowers.validation.validator.UserValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UserValidator.class)
public @interface UserMandatoryConditions {

    String message() default "{user}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
