package lu.luxtrust.flowers.error;

import java.util.Objects;

public class ApiFieldError {
    private final String field;
    private final String code;
    private final Object rejectedValue;
    private final String defaultMessage;

    public ApiFieldError(String field, String code, Object rejectedValue, String defaultMessage) {
        this.field = field;
        this.code = code;
        this.rejectedValue = rejectedValue;
        this.defaultMessage = defaultMessage;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }

    public String getField() {
        return field;
    }

    public String getCode() {
        return code;
    }

    public Object getRejectedValue() {
        return rejectedValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiFieldError that = (ApiFieldError) o;
        return Objects.equals(field, that.field) &&
                Objects.equals(code, that.code) &&
                Objects.equals(rejectedValue, that.rejectedValue) &&
                Objects.equals(defaultMessage, that.defaultMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(field, code, rejectedValue, defaultMessage);
    }

    @Override
    public String toString() {
        return "ApiFieldError{" +
                "field='" + field + '\'' +
                ", code='" + code + '\'' +
                ", rejectedValue=" + rejectedValue +
                ", defaultMessage='" + defaultMessage + '\'' +
                '}';
    }
}
