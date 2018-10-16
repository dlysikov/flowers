package lu.luxtrust.flowers.error.impl;

import lu.luxtrust.flowers.validation.annotation.*;
import org.hibernate.validator.constraints.Length;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.FieldError;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ValidationViolationsCodeResolverImplTest {

    @Mock
    private FieldError error;

    private ValidationViolationsCodeResolverImpl target;

    @Before
    public void init() {
        this.target = new ValidationViolationsCodeResolverImpl();
    }

    @Test
    public void gtcAcceptedCodeConvertedToRequired() {
        when(error.getCode()).thenReturn(GTCAccepted.class.getSimpleName());
        assertThat(target.resolve(error)).isEqualTo(ValidationViolationsCodeResolverImpl.Violation.REQUIRED.getCode());
    }

    @Test
    public void notNullCodeConvertedToRequired() {
        when(error.getCode()).thenReturn(NotNull.class.getSimpleName());
        assertThat(target.resolve(error)).isEqualTo(ValidationViolationsCodeResolverImpl.Violation.REQUIRED.getCode());
    }

    @Test
    public void tokenSerialNumberFilledCodeConvertedToRequired() {
        when(error.getCode()).thenReturn(TokenSerialNumberFilled.class.getSimpleName());
        assertThat(target.resolve(error)).isEqualTo(ValidationViolationsCodeResolverImpl.Violation.REQUIRED.getCode());
    }

    @Test
    public void lengthCodeConvertedToLength() {
        when(error.getCode()).thenReturn(Length.class.getSimpleName());
        assertThat(target.resolve(error)).isEqualTo(ValidationViolationsCodeResolverImpl.Violation.LENGTH.getCode());
    }

    @Test
    public void sizeCodeConvertedToLength() {
        when(error.getCode()).thenReturn(Size.class.getSimpleName());
        assertThat(target.resolve(error)).isEqualTo(ValidationViolationsCodeResolverImpl.Violation.LENGTH.getCode());
    }

    @Test
    public void emailCodeConvertedToLength() {
        when(error.getCode()).thenReturn(Email.class.getSimpleName());
        assertThat(target.resolve(error)).isEqualTo(ValidationViolationsCodeResolverImpl.Violation.INVALID.getCode());
    }

    @Test
    public void tokenSerialNumberCodeConvertedToLength() {
        when(error.getCode()).thenReturn(TokenSerialNumber.class.getSimpleName());
        assertThat(target.resolve(error)).isEqualTo(ValidationViolationsCodeResolverImpl.Violation.INVALID.getCode());
    }

    @Test
    public void phoneNumberCodeConvertedToLength() {
        when(error.getCode()).thenReturn(PhoneNumber.class.getSimpleName());
        assertThat(target.resolve(error)).isEqualTo(ValidationViolationsCodeResolverImpl.Violation.INVALID.getCode());
    }

    @Test
    public void companyIdentifier() {
        when(error.getCode()).thenReturn(CompanyIdentifierValid.class.getSimpleName());
        assertThat(target.resolve(error)).isEqualTo(ValidationViolationsCodeResolverImpl.Violation.REQUIRED.getCode());
        when(error.getRejectedValue()).thenReturn("trololo");
        assertThat(target.resolve(error)).isEqualTo(ValidationViolationsCodeResolverImpl.Violation.INVALID.getCode());
    }
}