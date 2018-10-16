package lu.luxtrust.flowers.validation.annotation;

import lu.luxtrust.flowers.validation.validator.CompanyIdentifierValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CompanyIdentifierValidator.class)
public @interface CompanyIdentifierValid {

    String message() default "{companyIdentifier}";

    boolean fullyEmptyAccepted() default true;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
