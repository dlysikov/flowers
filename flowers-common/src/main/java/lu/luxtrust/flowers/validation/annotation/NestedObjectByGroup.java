package lu.luxtrust.flowers.validation.annotation;

import lu.luxtrust.flowers.validation.validator.NestedObjectByGroupValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE,  ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {NestedObjectByGroupValidator.class})
@Repeatable(NestedObjectByGroupWrapper.class)
public @interface NestedObjectByGroup {

    String message() default "NestedObjectByGroup";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String[] fieldsExclusion() default {};
}
