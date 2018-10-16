package lu.luxtrust.flowers.fsm.guard;

import lu.luxtrust.flowers.entity.enrollment.CertificateOrder;
import lu.luxtrust.flowers.entity.enrollment.Unit;
import lu.luxtrust.flowers.entity.system.Requestor;
import lu.luxtrust.flowers.entity.system.RequestorConfiguration;
import lu.luxtrust.flowers.enums.OrderStatus;
import lu.luxtrust.flowers.fsm.FsmExtendedStateVariables;
import lu.luxtrust.flowers.fsm.OrderEvent;
import lu.luxtrust.flowers.repository.RequestorRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.statemachine.ExtendedState;
import org.springframework.statemachine.StateContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ShortEnrollmentFlowGuardTest {

    private static final Long REQUESTOR_ID = 1L;

    @Mock
    private RequestorRepository RequestorRepository;
    @Mock
    private StateContext<OrderStatus, OrderEvent> stateContext;
    @Mock
    private ExtendedState extendedState;
    @Mock
    private CertificateOrder order;
    @Mock
    private Requestor requestor;
    @Mock
    private RequestorConfiguration config;
    @Mock
    private Unit unit;

    private ShortEnrollmentFlowGuard target;

    @Before
    public void init() {
        when(stateContext.getExtendedState()).thenReturn(extendedState);
        when(extendedState.get(FsmExtendedStateVariables.ORDER, CertificateOrder.class)).thenReturn(order);
        when(unit.getRequestor()).thenReturn(requestor);
        when(order.getUnit()).thenReturn(unit);
        when(requestor.getId()).thenReturn(REQUESTOR_ID);
        when(RequestorRepository.findConfiguration(REQUESTOR_ID)).thenReturn(config);
        this.target = new ShortEnrollmentFlowGuard(RequestorRepository);
    }

    @Test
    public void evaluateTrue() {
        when(config.getShortFlow()).thenReturn(Boolean.TRUE);

        assertThat(target.evaluate(stateContext)).isTrue();
        verify(RequestorRepository).findConfiguration(REQUESTOR_ID);
    }

    @Test
    public void evaluateFalse() {
        when(config.getShortFlow()).thenReturn(Boolean.FALSE);

        assertThat(target.evaluate(stateContext)).isFalse();
        verify(RequestorRepository).findConfiguration(REQUESTOR_ID);
    }

}