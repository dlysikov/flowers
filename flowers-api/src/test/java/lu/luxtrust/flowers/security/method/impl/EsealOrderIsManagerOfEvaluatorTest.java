package lu.luxtrust.flowers.security.method.impl;

import lu.luxtrust.flowers.entity.builder.ESealOrderBuilder;
import lu.luxtrust.flowers.entity.builder.UnitBuilder;
import lu.luxtrust.flowers.entity.enrollment.ESealOrder;
import lu.luxtrust.flowers.entity.enrollment.Unit;
import lu.luxtrust.flowers.repository.ESealOrderRepository;
import lu.luxtrust.flowers.security.RestAuthenticationToken;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EsealOrderIsManagerOfEvaluatorTest {

    private static final Long ESEAL_ID = 1L;

    @Mock
    private ESealOrderRepository orderRepository;
    @Mock
    private RestAuthenticationToken authentication;
    private EsealOrderIsManagerOfEvaluator target;


    @Before
    public void init() {
        this.target = new EsealOrderIsManagerOfEvaluator(orderRepository);
    }

    @Test
    public void supportedType() {
        assertThat(target.supportedType()).isEqualTo(ESealOrder.class);
    }

    @Test
    public void isManagerOfOrderUnitNull() {
        assertThat(target.isManagerOf(authentication, ESealOrderBuilder.newBuilder().build())).isTrue();
    }

    @Test
    public void isManagerOfAuthUnitNull() {
        assertThat(target.isManagerOf(authentication, ESealOrderBuilder.newBuilder().unit(UnitBuilder.newBuilder().build()).build())).isTrue();
    }

    @Test
    public void isManagerOfById() {
        Unit unit = UnitBuilder.newBuilder().id(1L).build();
        when(orderRepository.findUnitByOrderId(ESEAL_ID)).thenReturn(unit);
        when(authentication.getUnit()).thenReturn(unit);

        assertThat(target.isManagerOf(authentication, ESEAL_ID)).isTrue();
    }

    @Test
    public void isNotManagerOfById() {
        Unit unit = UnitBuilder.newBuilder().id(1L).build();
        when(orderRepository.findUnitByOrderId(ESEAL_ID)).thenReturn(unit);

        Unit unitAuth = UnitBuilder.newBuilder().id(2L).build();
        when(authentication.getUnit()).thenReturn(unitAuth);

        assertThat(target.isManagerOf(authentication, ESEAL_ID)).isFalse();
    }

    @Test
    public void isManagerOfByObject() {
        Unit unit = UnitBuilder.newBuilder().id(1L).build();
        ESealOrder order = ESealOrderBuilder.newBuilder().unit(unit).build();
        when(authentication.getUnit()).thenReturn(unit);

        assertThat(target.isManagerOf(authentication, order)).isTrue();
    }

    @Test
    public void isNotManagerOfByObject() {
        Unit unit = UnitBuilder.newBuilder().id(1L).build();
        ESealOrder order = ESealOrderBuilder.newBuilder().unit(unit).build();

        Unit unitAuth = UnitBuilder.newBuilder().id(2L).build();
        when(authentication.getUnit()).thenReturn(unitAuth);

        assertThat(target.isManagerOf(authentication, order)).isFalse();
    }
}