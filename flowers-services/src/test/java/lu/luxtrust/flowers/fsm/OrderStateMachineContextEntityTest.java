package lu.luxtrust.flowers.fsm;

import lu.luxtrust.flowers.entity.enrollment.CertificateOrder;
import lu.luxtrust.flowers.enums.OrderStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.statemachine.StateMachineContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OrderStateMachineContextEntityTest {

    @Mock
    private CertificateOrder order;
    @Mock
    private StateMachineContext<OrderStatus, OrderEvent> context;
    private OrderStatus orderStatus = OrderStatus.DRAFT;
    private OrderStateMachineContextEntity target;

    @Before
    public void init() {
        when(context.getState()).thenReturn(orderStatus);
        when(order.getStatus()).thenReturn(orderStatus);
        target = new OrderStateMachineContextEntity(order);
    }

    @Test
    public void getStateMachineContext() {
        StateMachineContext<OrderStatus, OrderEvent> result = target.getStateMachineContext();
        assertThat(result).isNotNull();
        assertThat(result.getState()).isEqualTo(orderStatus);
    }

    @Test
    public void setStateMachineContext() {
        target.setStateMachineContext(context);
        verify(order).setStatus(orderStatus);
    }
}