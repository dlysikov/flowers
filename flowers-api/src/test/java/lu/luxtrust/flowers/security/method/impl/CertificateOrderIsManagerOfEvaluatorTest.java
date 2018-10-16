package lu.luxtrust.flowers.security.method.impl;

import lu.luxtrust.flowers.entity.builder.CertificateOrderBuilder;
import lu.luxtrust.flowers.entity.builder.UnitBuilder;
import lu.luxtrust.flowers.entity.enrollment.CertificateOrder;
import lu.luxtrust.flowers.entity.enrollment.Unit;
import lu.luxtrust.flowers.repository.CertificateOrderRepository;
import lu.luxtrust.flowers.security.RestAuthenticationToken;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CertificateOrderIsManagerOfEvaluatorTest {

    private static final Long CERT_ID = 1L;

    @Mock
    private CertificateOrderRepository certificateOrderRepository;
    @Mock
    private RestAuthenticationToken authentication;

    private CertificateOrderIsManagerOfEvaluator target;

    @Before
    public void init() {
        this.target = new CertificateOrderIsManagerOfEvaluator(certificateOrderRepository);
    }

    @Test
    public void supportedType() {
        assertThat(target.supportedType()).isEqualTo(CertificateOrder.class);
    }

    @Test
    public void isManagerOfOrderUnitNull() {
        assertThat(target.isManagerOf(authentication, CertificateOrderBuilder.newBuilder().build())).isTrue();
    }

    @Test
    public void isManagerOfAuthUnitNull() {
        assertThat(target.isManagerOf(authentication, CertificateOrderBuilder.newBuilder().unit(UnitBuilder.newBuilder().build()).build())).isTrue();
    }

    @Test
    public void isManagerOfById() {
        Unit unit = UnitBuilder.newBuilder().id(1L).build();
        when(certificateOrderRepository.findUnitByOrderId(CERT_ID)).thenReturn(unit);
        when(authentication.getUnit()).thenReturn(unit);

        assertThat(target.isManagerOf(authentication, CERT_ID)).isTrue();
    }

    @Test
    public void isNotManagerOfById() {
        Unit unit = UnitBuilder.newBuilder().id(1L).build();
        when(certificateOrderRepository.findUnitByOrderId(CERT_ID)).thenReturn(unit);

        Unit unitAuth = UnitBuilder.newBuilder().id(2L).build();
        when(authentication.getUnit()).thenReturn(unitAuth);

        assertThat(target.isManagerOf(authentication, CERT_ID)).isFalse();
    }

    @Test
    public void isManagerOfByObject() {
        Unit unit = UnitBuilder.newBuilder().id(1L).build();
        CertificateOrder order = CertificateOrderBuilder.newBuilder().unit(unit).build();
        when(authentication.getUnit()).thenReturn(unit);

        assertThat(target.isManagerOf(authentication, order)).isTrue();
    }

    @Test
    public void isNotManagerOfByObject() {
        Unit unit = UnitBuilder.newBuilder().id(1L).build();
        CertificateOrder order = CertificateOrderBuilder.newBuilder().unit(unit).build();

        Unit unitAuth = UnitBuilder.newBuilder().id(2L).build();
        when(authentication.getUnit()).thenReturn(unitAuth);

        assertThat(target.isManagerOf(authentication, order)).isFalse();
    }
}