package lu.luxtrust.flowers.exception;

import org.springframework.http.HttpStatus;

public class FlowersException extends RuntimeException {

    private Object details;
    private String errorCode;
    private HttpStatus httpStatus;

    public FlowersException(String message) {
        super(message);
    }

    public FlowersException(String message, Object details) {
        super(message);
        this.details = details;
    }

    public FlowersException(String message, Object details, String errorCode, HttpStatus httpStatus) {
        super(message);
        this.details = details;
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public FlowersException(String message, Object details, String errorCode) {
        super(message);
        this.details = details;
        this.errorCode = errorCode;
    }
    public FlowersException(Exception e) {
        super(e);
    }

    public FlowersException(Exception e, String message) {
        super(message, e);
    }

    public FlowersException(Exception e, Object details) {
        super(e);
        this.details = details;
    }

    public Object getDetails() {
        return details;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
