package lu.luxtrust.flowers.fsm;

import lu.luxtrust.flowers.enums.OrderStatus;
import lu.luxtrust.flowers.repository.CertificateOrderRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.persist.StateMachinePersister;

import static lu.luxtrust.flowers.enums.OrderStatus.DRAFT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OrderStateMachineFactoryTest {

    private static final Long ORDER_ID = 1L;

    @Mock
    private StateMachineFactory<OrderStatus, OrderEvent> orderStatusFactory;
    @Mock
    private CertificateOrderRepository orderRepository;
    @Mock
    private StateMachinePersister<OrderStatus, OrderEvent, ContextEntity<OrderStatus, OrderEvent, Long>> persister;
    @Mock
    private StateMachine<OrderStatus, OrderEvent> stateMachine;
    private OrderStatus orderStatus = DRAFT;

    private OrderStateMachineFactory target;

    @Before
    public void init() {
        when(orderRepository.findOrderStatusById(ORDER_ID)).thenReturn(orderStatus);
        when(orderStatusFactory.getStateMachine()).thenReturn(stateMachine);
        target = new OrderStateMachineFactory(orderStatusFactory, orderRepository, persister);
    }

    @Test
    public void newStateMachineForOrderId() throws Exception {
        StateMachine<OrderStatus, OrderEvent> result = target.newStateMachine(ORDER_ID);
        assertThat(result).isSameAs(stateMachine);

        verify(orderStatusFactory).getStateMachine();
        verify(orderRepository).findOrderStatusById(ORDER_ID);

        ArgumentCaptor<OrderStateMachineContextEntity> contextCaptor = ArgumentCaptor.forClass(OrderStateMachineContextEntity.class);
        ArgumentCaptor<StateMachine> stateMachineCaptor = ArgumentCaptor.forClass(StateMachine.class);
        verify(persister).restore(stateMachineCaptor.capture(), contextCaptor.capture());

        assertThat(stateMachineCaptor.getValue()).isSameAs(result);

        OrderStateMachineContextEntity context = contextCaptor.getValue();
        assertThat(context).isNotNull();
        assertThat(context.getStateMachineContext().getState()).isEqualTo(orderStatus);
    }

    @Test
    public void newStateMachine() {
        StateMachine<OrderStatus, OrderEvent> result = target.newStateMachine();
        assertThat(result).isSameAs(stateMachine);
        verify(orderStatusFactory).getStateMachine();
        verify(result).start();
    }
}