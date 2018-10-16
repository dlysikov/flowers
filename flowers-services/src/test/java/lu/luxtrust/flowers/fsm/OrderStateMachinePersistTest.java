package lu.luxtrust.flowers.fsm;

import lu.luxtrust.flowers.enums.OrderStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.statemachine.StateMachineContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OrderStateMachinePersistTest {

    @Mock
    private StateMachineContext<OrderStatus, OrderEvent> context;
    @Mock
    private ContextEntity<OrderStatus, OrderEvent, Long> order;

    @Test
    public void write() {
        OrderStateMachinePersist target = new OrderStateMachinePersist();
        target.write(context, order);

        verify(order).setStateMachineContext(context);
    }

    @Test
    public void read() {
        when(order.getStateMachineContext()).thenReturn(context);
        OrderStateMachinePersist target = new OrderStateMachinePersist();
        assertThat(target.read(order)).isSameAs(context);
        verify(order).getStateMachineContext();
    }
}