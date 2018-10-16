package lu.luxtrust.flowers.fsm.action;

import lu.luxtrust.flowers.entity.builder.CertificateOrderBuilder;
import lu.luxtrust.flowers.entity.enrollment.CertificateOrder;
import lu.luxtrust.flowers.enums.OrderStatus;
import lu.luxtrust.flowers.enums.RoleType;
import lu.luxtrust.flowers.exception.FlowersException;
import lu.luxtrust.flowers.fsm.FsmExtendedStateVariables;
import lu.luxtrust.flowers.fsm.OrderEvent;
import lu.luxtrust.flowers.service.NotificationService;
import lu.luxtrust.flowers.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.statemachine.ExtendedState;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static lu.luxtrust.flowers.properties.NotificationProperties.NotificationConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NotifyCsdReviewActionTest {

    private static final List<String> CSD_EMAILS = Collections.singletonList("trololo@gmail.com");
    private static final List<String> ADMIN_EMAILS = Collections.singletonList("s2000@gmail.com");

    @Mock private NotificationService notificationService;
    @Mock private NotificationConfig csdNotificationConfig;
    @Mock private NotificationConfig adminNotificationConfig;
    @Mock private UserService userService;
    @Mock private StateContext<OrderStatus, OrderEvent> stateContext;
    @Mock private ExtendedState extendedState;
    @Mock private StateMachine stateMachine;

    private NotifyCsdReviewAction action;
    private Map<Object, Object> variables;
    private CertificateOrder order = CertificateOrderBuilder.newBuilder().build();
    private Map<String, Object> data = Collections.singletonMap("order", order);

    @Before
    public void init() {
        this.variables = new HashMap<>();
        when(stateContext.getExtendedState()).thenReturn(extendedState);
        when(stateContext.getStateMachine()).thenReturn(stateMachine);
        when(extendedState.get(FsmExtendedStateVariables.ORDER, CertificateOrder.class)).thenReturn(order);
        when(extendedState.getVariables()).thenReturn(variables);
        action = new NotifyCsdReviewAction(notificationService, csdNotificationConfig, adminNotificationConfig, userService);
    }

    @Test
    public void notifyCsd() {
        when(userService.findEmailsByRole(RoleType.CSD_AGENT)).thenReturn(CSD_EMAILS);
        action.execute(stateContext);
        verify(notificationService).notify(data, CSD_EMAILS, csdNotificationConfig);
    }

    @Test
    public void notifyAdminIfNoDiaPresent() {
        when(userService.findEmailsByRole(RoleType.FLOWERS_ADMIN)).thenReturn(ADMIN_EMAILS);
        action.execute(stateContext);
        verify(notificationService).notify(data, ADMIN_EMAILS, adminNotificationConfig);
    }

    @Test
    public void exceptionDuringCsdNotification() {
        when(userService.findEmailsByRole(RoleType.CSD_AGENT)).thenReturn(CSD_EMAILS);
        doThrow(new FlowersException("TEST")).when(notificationService).notify(data, CSD_EMAILS, csdNotificationConfig);
        action.execute(stateContext);

        checkException();
    }

    @Test
    public void exceptionDuringAdminNotification() {
        when(userService.findEmailsByRole(RoleType.FLOWERS_ADMIN)).thenReturn(ADMIN_EMAILS);
        doThrow(new FlowersException("TEST")).when(notificationService).notify(data, ADMIN_EMAILS, adminNotificationConfig);
        action.execute(stateContext);

        checkException();
    }

    @Test
    public void exceptionWhenSearchingCsdMails() {
        doThrow(new RuntimeException()).when(userService).findEmailsByRole(RoleType.CSD_AGENT);
        action.execute(stateContext);

        checkException();
    }

    @Test
    public void exceptionWhenSearchingAdminMails() {
        doThrow(new RuntimeException()).when(userService).findEmailsByRole(RoleType.FLOWERS_ADMIN);
        action.execute(stateContext);

        checkException();
    }

    private void checkException() {
        assertThat(variables).containsKeys(FsmExtendedStateVariables.EXCEPTION);
        Object ex = variables.get(FsmExtendedStateVariables.EXCEPTION);
        assertThat(ex).isInstanceOf(FlowersException.class);

        FlowersException fe = (FlowersException) variables.get(FsmExtendedStateVariables.EXCEPTION);
        verify(stateMachine).setStateMachineError(fe);
    }
}