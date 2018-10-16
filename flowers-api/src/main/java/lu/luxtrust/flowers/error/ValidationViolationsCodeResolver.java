package lu.luxtrust.flowers.error;

import org.springframework.validation.FieldError;

public interface ValidationViolationsCodeResolver {

    String resolve(FieldError error);
}
