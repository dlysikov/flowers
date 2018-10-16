package lu.luxtrust.flowers.validation.validator;

import lu.luxtrust.flowers.validation.annotation.NestedObjectByGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.*;
import java.util.HashSet;
import java.util.Set;
@Component
public class NestedObjectByGroupValidator extends AbstractConstraintValidator<NestedObjectByGroup, Object>  {

    @Autowired
    private Validator validator;
    private NestedObjectByGroup nestedObjectByGroup;

    @Override
    public void initialize(NestedObjectByGroup nestedObjectByGroup) {
        this.nestedObjectByGroup = nestedObjectByGroup;
        if (validator == null) {
            ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            this.validator = factory.getValidator();
        }
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext ctx) {
        if (o != null) {
            Set<ConstraintViolation<Object>> result = filter(this.validator.validate(o, nestedObjectByGroup.groups()));
            boolean valid = result.isEmpty();
            for (ConstraintViolation<Object> violation : result) {
                ctx.disableDefaultConstraintViolation();
                ctx.buildConstraintViolationWithTemplate(violation.getMessageTemplate())
                        .addPropertyNode(violation.getPropertyPath().toString())
                        .addConstraintViolation();
            }
            return valid;
        }
        return Boolean.TRUE;
    }

    private Set filter(Set<ConstraintViolation<Object>> set) {
        Set resultSet = new HashSet();
        resultSet.addAll(set);
        for (ConstraintViolation<Object> violation: set) {
            for(String field: nestedObjectByGroup.fieldsExclusion()) {
                if(violation.getPropertyPath().toString().equalsIgnoreCase(field)) {
                    resultSet.remove(violation);
                }
            }
        }
        return resultSet;
    }
}
