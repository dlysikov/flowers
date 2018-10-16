package lu.luxtrust.flowers.error;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ApiErrors {
    private final Set<ApiFieldError> fieldErrors;
    private final Set<ApiGlobalError> globalErrors;

    public ApiErrors(Set<ApiFieldError> fieldErrors, Set<ApiGlobalError> globalErrors) {
        this.fieldErrors = new HashSet<>(fieldErrors);
        this.globalErrors = new HashSet<>(globalErrors);
    }

    public Set<ApiFieldError> getFieldErrors() {
        return fieldErrors;
    }

    public Set<ApiGlobalError> getGlobalErrors() {
        return globalErrors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiErrors apiErrors = (ApiErrors) o;
        return Objects.equals(fieldErrors, apiErrors.fieldErrors) &&
                Objects.equals(globalErrors, apiErrors.globalErrors);
    }

    @Override
    public int hashCode() {

        return Objects.hash(fieldErrors, globalErrors);
    }

    @Override
    public String toString() {
        return "ApiErrors{" +
                "fieldErrors=" + fieldErrors +
                ", globalErrors=" + globalErrors +
                '}';
    }
}
