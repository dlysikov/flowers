package lu.luxtrust.flowers.error;

import lu.luxtrust.flowers.exception.FlowersException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ApiExceptionHandlerTest {

    private static final String FIELD = "field";
    private static final String CODE = "code";
    private static final Object REJECTED_VALUE = "SSSSS";
    private static final String DEFAULT_MESSAGE = "DEFAULT_MESSAGE";
    private static final String MAX_FILE_SIZE = "10KB";
    private static final String MAX_FILES_SIZE = "30KB";

    @Mock
    private ValidationViolationsCodeResolver resolver;
    @Mock
    private MethodArgumentNotValidException ex;
    private BindException bex;
    @Mock
    private HttpHeaders headers;
    @Mock
    private WebRequest request;
    @Mock
    private BindingResult bindingResult;
    @Mock
    private FieldError fieldError;
    @Mock
    private ObjectError globalError;
    @Mock
    private HttpServletRequest httpRequest;
    @Mock
    private Exception exception;
    @Mock
    private FlowersException flowersException;

    private HttpStatus status = HttpStatus.OK;

    private ApiExceptionHandler target;

    @Before
    public void init() {
        this.target = new ApiExceptionHandler(resolver, MAX_FILE_SIZE, MAX_FILES_SIZE);
        bex = new BindException(bindingResult);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        when(fieldError.getField()).thenReturn(FIELD);
        when(fieldError.getCode()).thenReturn(CODE);
        when(fieldError.getRejectedValue()).thenReturn(REJECTED_VALUE);
        when(fieldError.getDefaultMessage()).thenReturn(DEFAULT_MESSAGE);

        when(globalError.getCode()).thenReturn(CODE);
        when(globalError.getDefaultMessage()).thenReturn(DEFAULT_MESSAGE);
        when(resolver.resolve(any(FieldError.class))).thenReturn(CODE);
    }

    @Test
    public void handleMethodArgumentNotValid() {
        when(bindingResult.hasErrors()).thenReturn(Boolean.TRUE);
        when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList(fieldError));
        when(bindingResult.getGlobalErrors()).thenReturn(Arrays.asList(globalError));

        ResponseEntity<Object> result = target.handleMethodArgumentNotValid(ex, headers, status, request);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(result.getBody()).isInstanceOf(ApiErrors.class);

        ApiErrors body = (ApiErrors) result.getBody();
        assertThat(body.getGlobalErrors()).containsOnly(new ApiGlobalError(CODE, DEFAULT_MESSAGE));
        assertThat(body.getFieldErrors()).containsOnly(new ApiFieldError(FIELD, CODE, REJECTED_VALUE, DEFAULT_MESSAGE));
    }

    @Test
    public void handleBindExceptionNotValid() {
        when(bindingResult.hasErrors()).thenReturn(Boolean.TRUE);
        when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList(fieldError));
        when(bindingResult.getGlobalErrors()).thenReturn(Arrays.asList(globalError));

        ResponseEntity<Object> result = target.handleBindException(bex, headers, status, request);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(result.getBody()).isInstanceOf(ApiErrors.class);

        ApiErrors body = (ApiErrors) result.getBody();
        assertThat(body.getGlobalErrors()).containsOnly(new ApiGlobalError(CODE, DEFAULT_MESSAGE));
        assertThat(body.getFieldErrors()).containsOnly(new ApiFieldError(FIELD, CODE, REJECTED_VALUE, DEFAULT_MESSAGE));
    }

    @Test
    public void handleFileSizeExceed() {
        ResponseEntity<Object> result = target.handleFileSizeExceed(httpRequest, exception);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.PAYLOAD_TOO_LARGE);
        assertThat(result.getBody()).isInstanceOf(ApiErrors.class);

        ApiErrors body = (ApiErrors) result.getBody();
        ApiGlobalError<List> expectedError = new ApiGlobalError<>(GlobalErrorCodes.TO_BIG_FILE, "Max file size exceeded!");
        expectedError.setDetails(Arrays.asList(MAX_FILE_SIZE, MAX_FILES_SIZE));

        assertThat(body.getGlobalErrors()).containsOnly(expectedError);
        assertThat(body.getFieldErrors()).isEmpty();
    }

    @Test
    public void handleApplicationUnhandledException() {
        when(flowersException.getMessage()).thenReturn(DEFAULT_MESSAGE);
        when(flowersException.getDetails()).thenReturn(FIELD);

        ResponseEntity<Object> result = target.handleApplicationUnhandledException(httpRequest, flowersException);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(result.getBody()).isInstanceOf(ApiErrors.class);

        ApiErrors body = (ApiErrors) result.getBody();
        ApiGlobalError<Object> expectedError = new ApiGlobalError<>(GlobalErrorCodes.UNEXPECTED_ERROR, DEFAULT_MESSAGE);
        expectedError.setDetails(FIELD);

        assertThat(body.getGlobalErrors()).containsOnly(expectedError);
        assertThat(body.getFieldErrors()).isEmpty();
    }
}