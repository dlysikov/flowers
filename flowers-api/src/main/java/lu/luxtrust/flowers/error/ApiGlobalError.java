package lu.luxtrust.flowers.error;

import java.util.Objects;

public class ApiGlobalError<D> {

    private final String code;
    private final String defaultMessage;
    private D details;

    public ApiGlobalError(String code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    public D getDetails() {
        return details;
    }

    public void setDetails(D details) {
        this.details = details;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }

    public String getCode() {
        return code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiGlobalError that = (ApiGlobalError) o;
        return Objects.equals(code, that.code) &&
                Objects.equals(defaultMessage, that.defaultMessage) &&
                Objects.equals(details, that.details);
    }

    @Override
    public int hashCode() {

        return Objects.hash(code, defaultMessage, details);
    }

    @Override
    public String toString() {
        return "ApiGlobalError{" +
                "code='" + code + '\'' +
                ", defaultMessage='" + defaultMessage + '\'' +
                ", details=" + details +
                '}';
    }
}
