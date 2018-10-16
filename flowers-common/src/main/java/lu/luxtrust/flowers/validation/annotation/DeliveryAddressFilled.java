package lu.luxtrust.flowers.validation.annotation;

import lu.luxtrust.flowers.validation.validator.DeliveryAddressValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DeliveryAddressValidator.class)
public @interface DeliveryAddressFilled {

    String message() default "{org.hibernate.validator.constraints.NotEmpty.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String fieldName() default "address";

    boolean nullValid() default true;
}
