package lu.luxtrust.flowers.error;

import com.google.common.collect.Sets;
import lu.luxtrust.flowers.exception.FlowersException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ApiExceptionHandler.class);

    private final ValidationViolationsCodeResolver codeResolver;
    private final String maxFileSize;
    private final String maxFilesSize;

    public ApiExceptionHandler(ValidationViolationsCodeResolver codeResolver,
                               @Value("${spring.http.multipart.max-file-size}") String maxFileSize,
                               @Value("${spring.http.multipart.max-request-size}") String maxFilesSize) {
        this.maxFileSize = maxFileSize;
        this.maxFilesSize = maxFilesSize;
        this.codeResolver = codeResolver;
    }

    @Override
    protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return handle(ex.getBindingResult(), ex, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {

        return handle(ex.getBindingResult(), ex, headers, status, request);
    }

    private ResponseEntity<Object> handle(BindingResult bindingResult, Exception ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        if (bindingResult.hasErrors()) {
            Set<ApiFieldError> apiFieldErrors = bindingResult.getFieldErrors().stream()
                    .map(fieldError -> new ApiFieldError(fieldError.getField(), codeResolver.resolve(fieldError), fieldError.getRejectedValue(), fieldError.getDefaultMessage()))
                    .collect(Collectors.toSet());

            Set<ApiGlobalError> apiGlobalErrors = bindingResult.getGlobalErrors().stream()
                    .map(globalError -> new ApiGlobalError(globalError.getCode(), globalError.getDefaultMessage()))
                    .collect(Collectors.toSet());

            ApiErrors apiErrorsView = new ApiErrors(apiFieldErrors, apiGlobalErrors);

            LOG.warn("Request for URI {} has the following validation errors {}, {}", request.getContextPath(), apiErrorsView, ex);

            return new ResponseEntity<>(apiErrorsView, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        return handleExceptionInternal(ex, null, headers, status, request);
    }

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<Object> handleFileSizeExceed(HttpServletRequest request, Exception ex) {

        LOG.warn("To big file. Request URI {}, {}",request.getContextPath(), ex);

        ApiGlobalError<List<String>> toBigFileError = new ApiGlobalError<>(GlobalErrorCodes.TO_BIG_FILE, "Max file size exceeded!");
        toBigFileError.setDetails(Arrays.asList(maxFileSize, maxFilesSize));
        return new ResponseEntity<>(new ApiErrors(Sets.newHashSet(), Sets.newHashSet(toBigFileError)), HttpStatus.PAYLOAD_TOO_LARGE);
    }

    @ExceptionHandler(FlowersException.class)
    public ResponseEntity<Object> handleApplicationUnhandledException(HttpServletRequest request, FlowersException ex) {
        ApiGlobalError<Object> unexpectedException = new ApiGlobalError<>(StringUtils.isEmpty(ex.getErrorCode()) ? GlobalErrorCodes.UNEXPECTED_ERROR : ex.getErrorCode(), ex.getMessage());
        unexpectedException.setDetails(ex.getDetails());
        LOG.error("Server error. Details {}, exception {}", ex.getDetails(), ex);
        return new ResponseEntity<>(new ApiErrors(Sets.newHashSet(), Sets.newHashSet(unexpectedException)), ex.getHttpStatus() == null ? HttpStatus.INTERNAL_SERVER_ERROR : ex.getHttpStatus());
    }
}
