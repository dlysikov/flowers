package lu.luxtrust.flowers.fsm.action;

import lu.luxtrust.flowers.entity.enrollment.CertificateOrder;
import lu.luxtrust.flowers.enums.OrderStatus;
import lu.luxtrust.flowers.fsm.FsmExtendedStateVariables;
import lu.luxtrust.flowers.fsm.OrderEvent;
import lu.luxtrust.flowers.repository.CertificateOrderRepository;
import lu.luxtrust.flowers.exception.FlowersException;
import lu.luxtrust.flowers.service.LrsService;
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
public class SendOrderToLRSActionTest {

    private static final String LRS_ORDER_NUMBER = "lrs order num";

    @Mock
    private CertificateOrderRepository orderRepository;
    @Mock
    private StateContext<OrderStatus, OrderEvent> stateContext;
    @Mock
    private ExtendedState extendedState;
    @Mock
    private CertificateOrder order;
    @Mock
    private State targetState;
    @Mock
    private LrsService lrsService;
    @Mock
    private StateMachine stateMachine;
    private Map<Object, Object> variables;

    private SendOrderToLRSAction target;

    private OrderStatus orderStatus = OrderStatus.CSD_REVIEW_REQUIRED;

    @Before
    public void init() throws Exception {
        this.variables = new HashMap<>();
        when(lrsService.register(order)).thenReturn(LRS_ORDER_NUMBER);
        when(stateContext.getExtendedState()).thenReturn(extendedState);
        when(targetState.getId()).thenReturn(orderStatus);
        when(extendedState.get(FsmExtendedStateVariables.ORDER, CertificateOrder.class)).thenReturn(order);
        when(stateContext.getTarget()).thenReturn(targetState);
        when(stateContext.getStateMachine()).thenReturn(stateMachine);
        when(extendedState.getVariables()).thenReturn(variables);
        this.target = new SendOrderToLRSAction(lrsService, orderRepository);
    }

    @Test
    public void execute() {
        target.execute(stateContext);
        verify(order).setLrsOrderNumber(LRS_ORDER_NUMBER);
        verify(orderRepository).save(order);
    }

    @Test
    public void executeWithLRSError() throws Exception {
        when(lrsService.register(order)).thenThrow(new RuntimeException("SDFSDF"));
        target.execute(stateContext);

        assertThat(variables).containsKeys(FsmExtendedStateVariables.EXCEPTION);
        Object ex = variables.get(FsmExtendedStateVariables.EXCEPTION);
        assertThat(ex).isInstanceOf(FlowersException.class);

        FlowersException fe = (FlowersException) variables.get(FsmExtendedStateVariables.EXCEPTION);
        verify(stateMachine).setStateMachineError(fe);
    }
}