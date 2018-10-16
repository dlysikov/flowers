package lu.luxtrust.flowers.validation.validator;

import lu.luxtrust.flowers.entity.enrollment.CertificateOrder;
import lu.luxtrust.flowers.enums.Device;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.validation.ConstraintValidatorContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TokenSerialNumberValidatorTest {

    @Mock
    private CertificateOrder order;
    @Mock
    private ConstraintValidatorContext ctx;
    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder ctxBuilder;
    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext ctxNodeCustomizer;


    private TokenSerialNumberValidator target;

    @Before
    public void init() {
        when(ctx.buildConstraintViolationWithTemplate(anyString())).thenReturn(ctxBuilder);
        when(ctxBuilder.addPropertyNode(anyString())).thenReturn(ctxNodeCustomizer);
        this.target = new TokenSerialNumberValidator();
    }

    @Test
    public void validDeviceNotProvided() {
        assertThat(target.isValid(order, ctx)).isTrue();

        verify(ctx, never()).disableDefaultConstraintViolation();
        verify(ctxBuilder, never()).addPropertyNode(anyString());
        verify(ctxNodeCustomizer, never()).addConstraintViolation();
    }

    @Test
    public void validWhenDeviceIsMobile() {
        when(order.getDevice()).thenReturn(Device.MOBILE);
        assertThat(target.isValid(order, ctx)).isTrue();

        verify(ctx, never()).disableDefaultConstraintViolation();
        verify(ctxBuilder, never()).addPropertyNode(anyString());
        verify(ctxNodeCustomizer, never()).addConstraintViolation();
    }

    @Test
    public void validWhenDeviceIsTokenAndSerialNumberProvided() {
        when(order.getDevice()).thenReturn(Device.TOKEN);
        when(order.getTokenSerialNumber()).thenReturn("3333333");
        assertThat(target.isValid(order, ctx)).isTrue();

        verify(ctx, never()).disableDefaultConstraintViolation();
        verify(ctxBuilder, never()).addPropertyNode(anyString());
        verify(ctxNodeCustomizer, never()).addConstraintViolation();
    }

    @Test
    public void inValid() {
        when(order.getDevice()).thenReturn(Device.TOKEN);
        assertThat(target.isValid(order, ctx)).isFalse();

        verify(ctx).buildConstraintViolationWithTemplate("{org.hibernate.validator.constraints.NotEmpty.message}");
        verify(ctx).disableDefaultConstraintViolation();
        verify(ctxBuilder).addPropertyNode("tokenSerialNumber");
        verify(ctxNodeCustomizer).addConstraintViolation();
    }
}