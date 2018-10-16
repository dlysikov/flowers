package lu.luxtrust.flowers.properties.validation;

import lu.luxtrust.flowers.properties.KeyStoreProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

@Component
public class KeyStorePropsValidator  implements ConstraintValidator<KeyStorePropsFilled, KeyStoreProperties> {

    private Validator validator;

    @Override
    public void initialize(KeyStorePropsFilled constraintAnnotation) {
    }

    @Override
    public boolean isValid(KeyStoreProperties value, ConstraintValidatorContext context) {
        if (value.getType() == null) {
            addConstraintViolation("type", context, "{org.hibernate.validator.constraints.NotEmpty.message}");
            return Boolean.FALSE;
        }
        if (value.getType() == KeyStoreProperties.KeyStoreSpiType.HSM) {
            return hasViolations("hsm", validator.validate(value.getHsm()), context);
        }
        if (value.getType() == KeyStoreProperties.KeyStoreSpiType.FILE) {
            return hasViolations("file", validator.validate(value.getFile()), context);
        }
        return Boolean.TRUE;
    }

    private <T> Boolean hasViolations(String parent, Set<ConstraintViolation<T>> violations, ConstraintValidatorContext context) {
        if (!violations.isEmpty()) {
            violations.forEach((v) -> addConstraintViolation(parent +"." + v.getPropertyPath().toString(), context, v.getMessageTemplate()));
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    private void addConstraintViolation(String property, ConstraintValidatorContext ctx, String msg) {
        ctx.disableDefaultConstraintViolation();
        ctx.buildConstraintViolationWithTemplate(msg).addPropertyNode(property).addConstraintViolation();
    }

    @Autowired
    public void setValidator(Validator validator) {
        this.validator = validator;
    }
}
