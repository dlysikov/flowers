package lu.luxtrust.flowers.controller;

import lu.luxtrust.flowers.error.ApiErrors;
import lu.luxtrust.flowers.error.ApiFieldError;
import lu.luxtrust.flowers.error.ValidationViolationsCodeResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.SmartValidator;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class RequiredFieldsResolver {

    private final SmartValidator validator;
    private final ValidationViolationsCodeResolver codeResolver;

    @Autowired
    public RequiredFieldsResolver(SmartValidator validator, ValidationViolationsCodeResolver codeResolver) {
        this.validator = validator;
        this.codeResolver = codeResolver;
    }

    public ResponseEntity<ApiErrors> getRequiredFields(Object entity, BindingResult result, Object validationGroup, String...alwaysMandatory) {
        validator.validate(entity, result, validationGroup);
        Set<ApiFieldError> apiFieldErrors = result.getFieldErrors().stream()
                .map(fieldError -> new ApiFieldError(fieldError.getField(), codeResolver.resolve(fieldError), fieldError.getRejectedValue(), fieldError.getDefaultMessage()))
                .collect(Collectors.toSet());
        for (String field : alwaysMandatory) {
            apiFieldErrors.add(new ApiFieldError(field, "required", null, null));
        }
        return ResponseEntity.ok(new ApiErrors(apiFieldErrors, Collections.emptySet()));
    }
}
