package lu.luxtrust.flowers.validation.validator;

import lu.luxtrust.flowers.entity.enrollment.CertificateOrder;
import lu.luxtrust.flowers.entity.enrollment.ESealOrder;
import lu.luxtrust.flowers.entity.enrollment.Holder;
import lu.luxtrust.flowers.enums.CertificateLevel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.validation.ConstraintValidatorContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class GTCAcceptedValidatorTest {

    @Mock
    private CertificateOrder order;
    @Mock
    private Holder holder;
    @Mock
    private ConstraintValidatorContext ctx;
    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder ctxBuilder;
    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext ctxNodeCustomizer;

    private GTCAcceptedValidator target;

    @Before
    public void init() {
        when(ctx.buildConstraintViolationWithTemplate(anyString())).thenReturn(ctxBuilder);
        when(ctxBuilder.addPropertyNode(anyString())).thenReturn(ctxNodeCustomizer);
        when(order.getHolder()).thenReturn(holder);
        this.target = new GTCAcceptedValidator();
    }

    @Test
    public void validWhenHolderIsNotPopulated(){
        assertThat(target.isValid(new CertificateOrder(), ctx)).isTrue();
    }

    @Test
    public void validWhenCertificateLevelIsNotPopulated(){
        assertThat(target.isValid(order, ctx)).isTrue();
    }

    @Test
    public void validWhenQCPAndNoAcceptedGTC() {
        when(holder.getCertificateLevel()).thenReturn(CertificateLevel.QCP);
        when(order.getAcceptedGTC()).thenReturn(Boolean.FALSE);
        assertThat(target.isValid(order, ctx)).isTrue();
    }

    @Test
    public void validWhenNotQCPAndAcceptedGTC() {
        when(holder.getCertificateLevel()).thenReturn(CertificateLevel.LCP);
        when(order.getAcceptedGTC()).thenReturn(Boolean.TRUE);
        assertThat(target.isValid(order, ctx)).isTrue();
    }

    @Test
    public void inValidWhenQCPAndAcceptedGTC() {
        when(holder.getCertificateLevel()).thenReturn(CertificateLevel.QCP);
        when(order.getAcceptedGTC()).thenReturn(Boolean.TRUE);
        assertThat(target.isValid(order, ctx)).isFalse();

        verify(ctx).disableDefaultConstraintViolation();
        verify(ctx).buildConstraintViolationWithTemplate("{javax.validation.constraints.AssertFalse.message}");
        verify(ctxBuilder).addPropertyNode("acceptedGTC");
        verify(ctxNodeCustomizer).addConstraintViolation();
    }

    @Test
    public void inValidWhenNotQCPAndNotAcceptedGTC() {
        when(holder.getCertificateLevel()).thenReturn(CertificateLevel.LCP);
        when(order.getAcceptedGTC()).thenReturn(Boolean.FALSE);
        assertThat(target.isValid(order, ctx)).isFalse();

        verify(ctx).disableDefaultConstraintViolation();
        verify(ctx).buildConstraintViolationWithTemplate("{javax.validation.constraints.AssertTrue.message}");
        verify(ctxBuilder).addPropertyNode("acceptedGTC");
        verify(ctxNodeCustomizer).addConstraintViolation();
    }

    @Test
    public void isValidESealOrderNotAcceptedGTC() {
        ESealOrder order = new ESealOrder();
        assertThat(target.isValid(order, ctx)).isFalse();
        verify(ctx).disableDefaultConstraintViolation();
        verify(ctx).buildConstraintViolationWithTemplate("{javax.validation.constraints.AssertTrue.message}");
        verify(ctxBuilder).addPropertyNode("acceptedGTC");
        verify(ctxNodeCustomizer).addConstraintViolation();
    }

    @Test
    public void isValidESealOrderAcceptedGTC() {
        ESealOrder order = new ESealOrder();
        order.setAcceptedGTC(Boolean.TRUE);
        assertThat(target.isValid(order, ctx)).isTrue();
    }

}