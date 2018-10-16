package lu.luxtrust.flowers.fsm.action;

import lu.luxtrust.flowers.entity.enrollment.CertificateOrder;
import lu.luxtrust.flowers.entity.system.User;
import lu.luxtrust.flowers.enums.OrderStatus;
import lu.luxtrust.flowers.fsm.FsmExtendedStateVariables;
import lu.luxtrust.flowers.fsm.OrderEvent;
import lu.luxtrust.flowers.repository.CertificateOrderRepository;
import lu.luxtrust.flowers.repository.OrderUserValidatePageRepository;
import lu.luxtrust.flowers.exception.FlowersException;
import lu.luxtrust.flowers.service.NotificationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.statemachine.ExtendedState;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserValidatesOrderActionTest {

    private static final Long ORDER_ID = 1L;

    @Mock
    private CertificateOrderRepository orderRepository;
    @Mock
    private OrderUserValidatePageRepository orderUserValidatePageRepository;
    @Mock
    private NotificationService notificationService;
    @Mock
    private StateContext<OrderStatus, OrderEvent> stateContext;
    @Mock
    private ExtendedState extendedState;
    @Mock
    private CertificateOrder order;
    @Mock
    private State targetState;
    @Mock
    private User issuer;
    private OrderStatus orderStatus = OrderStatus.USER_DRAFT;
    @Mock
    private StateMachine stateMachine;
    private Map<Object, Object> variables;

    private UserValidatesOrderAction target;

    @Before
    public void init() {
        this.variables = new HashMap<>();
        when(orderRepository.findIssuer(ORDER_ID)).thenReturn(issuer);
        when(order.getId()).thenReturn(ORDER_ID);
        when(stateContext.getExtendedState()).thenReturn(extendedState);
        when(targetState.getId()).thenReturn(orderStatus);
        when(extendedState.get(FsmExtendedStateVariables.ORDER, CertificateOrder.class)).thenReturn(order);
        when(stateContext.getTarget()).thenReturn(targetState);
        when(stateContext.getStateMachine()).thenReturn(stateMachine);
        when(extendedState.getVariables()).thenReturn(variables);
        this.target = new UserValidatesOrderAction(orderRepository, orderUserValidatePageRepository);
    }

    @Test
    public void exceptionDuringDBSaving() {
        when(orderRepository.save(order)).thenThrow(new RuntimeException());
        target.execute(stateContext);

        checkException();
    }

    private void checkException() {
        assertThat(variables).containsKeys(FsmExtendedStateVariables.EXCEPTION);
        Object ex = variables.get(FsmExtendedStateVariables.EXCEPTION);
        assertThat(ex).isInstanceOf(FlowersException.class);

        FlowersException fe = (FlowersException) variables.get(FsmExtendedStateVariables.EXCEPTION);
        verify(stateMachine).setStateMachineError(fe);
    }

    @Test
    public void executeSuccessfully() {
        target.execute(stateContext);
        verify(orderRepository).save(order);
        verify(order).setStatus(orderStatus);
        verify(orderUserValidatePageRepository).removeByOrderId(ORDER_ID);
    }
}