package lu.luxtrust.flowers.properties.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = KeyStorePropsValidator.class)
public @interface KeyStorePropsFilled {

    String message() default "{org.hibernate.validator.constraints.NotEmpty.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
