package lu.luxtrust.flowers.fsm.action;

import lu.luxtrust.flowers.entity.enrollment.CertificateOrder;
import lu.luxtrust.flowers.entity.enrollment.OrderUserValidatePage;
import lu.luxtrust.flowers.enums.OrderStatus;
import lu.luxtrust.flowers.exception.FlowersException;
import lu.luxtrust.flowers.fsm.FsmExtendedStateVariables;
import lu.luxtrust.flowers.fsm.OrderEvent;
import lu.luxtrust.flowers.repository.CertificateOrderRepository;
import lu.luxtrust.flowers.service.NotificationService;
import lu.luxtrust.flowers.service.OrderValidationPageService;
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
public class NotifyUserActionTest {

    private static final Long ORDER_ID = 1L;

    @Mock
    private OrderValidationPageService orderValidationPageService;
    @Mock
    private NotificationService notificationService;
    @Mock
    private CertificateOrderRepository orderRepository;
    @Mock
    private StateContext<OrderStatus, OrderEvent> stateContext;
    @Mock
    private ExtendedState extendedState;
    @Mock
    private CertificateOrder order;
    @Mock
    private OrderUserValidatePage page;
    @Mock
    private State targetState;
    @Mock
    private StateMachine stateMachine;
    private Map<Object, Object> variables;
    private OrderStatus orderStatus = OrderStatus.USER_DRAFT;

    private NotifyUserAction target;

    @Before
    public void init() {
        this.variables = new HashMap<>();
        when(order.getId()).thenReturn(ORDER_ID);
        when(stateContext.getExtendedState()).thenReturn(extendedState);
        when(targetState.getId()).thenReturn(orderStatus);
        when(extendedState.get(FsmExtendedStateVariables.ORDER, CertificateOrder.class)).thenReturn(order);
        when(stateContext.getTarget()).thenReturn(targetState);
        when(orderValidationPageService.generateValidationPage(order)).thenReturn(page);
        when(stateContext.getStateMachine()).thenReturn(stateMachine);
        when(extendedState.getVariables()).thenReturn(variables);
        target = new NotifyUserAction(orderValidationPageService, notificationService, Boolean.FALSE, orderRepository);
    }

    @Test
    public void exceptionDuringDBStore() {
        when(orderRepository.save(order)).thenThrow(new RuntimeException());
        target.execute(stateContext);

        checkException();
    }

    @Test
    public void exceptionDuringValidationPageGeneration() {
        when(orderValidationPageService.generateValidationPage(order)).thenThrow(new RuntimeException());
        target.execute(stateContext);
        checkException();
    }

    @Test
    public void exceptionDuringNotification() {
        doThrow(new FlowersException("TSET")).when(notificationService).notifyEndUserToValidateOrder(page);
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
        verify(orderValidationPageService).generateValidationPage(order);
        verify(orderRepository).save(order);
        verify(order).setStatus(orderStatus);
        verify(notificationService).notifyEndUserToValidateOrder(page);
    }
}