package lu.luxtrust.flowers.error.impl;

import lu.luxtrust.flowers.error.ValidationViolationsCodeResolver;
import lu.luxtrust.flowers.validation.annotation.*;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.FieldError;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashMap;
import java.util.Map;

@Component
public class ValidationViolationsCodeResolverImpl implements ValidationViolationsCodeResolver {

    enum Violation {
        REQUIRED("required"),
        INVALID("invalid"),
        LENGTH("length");
        private String code;

        Violation(String code) {
            this.code = code;
        }

        public String getCode() {
            return this.code;
        }
    }

    private final Map<String, ValidationViolationsCodeResolver> annotationClassName2Code;

    public ValidationViolationsCodeResolverImpl() {
        this.annotationClassName2Code = new HashMap<>();
        this.annotationClassName2Code.put(GTCAccepted.class.getSimpleName(), new SingleValueResolver(Violation.REQUIRED));
        this.annotationClassName2Code.put(TokenSerialNumberFilled.class.getSimpleName(), new SingleValueResolver(Violation.REQUIRED));
        this.annotationClassName2Code.put(NotNull.class.getSimpleName(), new SingleValueResolver(Violation.REQUIRED));
        this.annotationClassName2Code.put(NotEmpty.class.getSimpleName(), new SingleValueResolver(Violation.REQUIRED));
        this.annotationClassName2Code.put(Length.class.getSimpleName(), new SingleValueResolver(Violation.LENGTH));
        this.annotationClassName2Code.put(Size.class.getSimpleName(), new SingleValueResolver(Violation.LENGTH));
        this.annotationClassName2Code.put(Email.class.getSimpleName(), new SingleValueResolver(Violation.INVALID));
        this.annotationClassName2Code.put(TokenSerialNumber.class.getSimpleName(), new SingleValueResolver(Violation.INVALID));
        this.annotationClassName2Code.put(PhoneNumber.class.getSimpleName(), new SingleValueResolver(Violation.INVALID));
        this.annotationClassName2Code.put(CompanyIdentifierValid.class.getSimpleName(), new ByContentResolver());
        this.annotationClassName2Code.put(DeliveryAddressFilled.class.getSimpleName(), new ByContentResolver());
        this.annotationClassName2Code.put(Adult.class.getSimpleName(), new SingleValueResolver(Violation.INVALID));
        this.annotationClassName2Code.put(UserMandatoryConditions.class.getSimpleName(), new SingleValueResolver(Violation.REQUIRED));

    }

    @Override
    public String resolve(FieldError error) {
        if (!this.annotationClassName2Code.containsKey(error.getCode())) {
            return error.getCode();
        }
        return this.annotationClassName2Code.get(error.getCode()).resolve(error);
    }

    private static class SingleValueResolver implements ValidationViolationsCodeResolver {

        private Violation value;

        private SingleValueResolver(Violation value) {
            this.value = value;
        }

        @Override
        public String resolve(FieldError error) {
            return value.getCode();
        }
    }

    private static class ByContentResolver implements ValidationViolationsCodeResolver {

        @Override
        public String resolve(FieldError error) {
            if (error.getRejectedValue() == null || (error.getRejectedValue() instanceof String && StringUtils.isEmpty(error.getRejectedValue().toString()))) {
                return Violation.REQUIRED.getCode();
            } else if (!StringUtils.isEmpty(error.getDefaultMessage()) && error.getDefaultMessage().matches("^(.+)\\{min}(.+)\\{max}(.*)$")) {
                return Violation.LENGTH.getCode();
            } else {
                return Violation.INVALID.getCode();
            }
        }
    }
}
