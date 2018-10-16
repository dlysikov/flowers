package lu.luxtrust.flowers.entity;

import org.junit.Before;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractEntityValidationTest<T> {

    protected Validator validator;

    @Before
    public void init() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    protected void assertValidationResult(T entity, String path, Class<?> group) {
        Set<ConstraintViolation<T>> validate = validator.validate(entity, group);
        assertThat(validate).hasSize(1);

        ArrayList<ConstraintViolation<T>> validateList = new ArrayList<>(validate);
        assertThat(validateList.get(0).getPropertyPath().toString()).isEqualTo(path);
    }

}
