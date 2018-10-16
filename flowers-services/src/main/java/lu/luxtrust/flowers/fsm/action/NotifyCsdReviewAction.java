package lu.luxtrust.flowers.fsm.action;

import lu.luxtrust.flowers.entity.enrollment.CertificateOrder;
import lu.luxtrust.flowers.enums.OrderStatus;
import lu.luxtrust.flowers.enums.RoleType;
import lu.luxtrust.flowers.exception.FlowersException;
import lu.luxtrust.flowers.fsm.FsmExtendedStateVariables;
import lu.luxtrust.flowers.fsm.OrderEvent;
import lu.luxtrust.flowers.service.NotificationService;
import lu.luxtrust.flowers.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;

import static lu.luxtrust.flowers.properties.NotificationProperties.NotificationConfig;

public class NotifyCsdReviewAction implements Action<OrderStatus, OrderEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(NotifyCsdReviewAction.class);

    private final NotificationService notificationService;
    private final NotificationConfig csdNotificationConfig;
    private final NotificationConfig adminNotificationConfig;
    private final UserService userService;

    public NotifyCsdReviewAction(NotificationService notificationService, NotificationConfig csdNotificationConfig, NotificationConfig adminNotificationConfig, UserService userService) {
        this.notificationService = notificationService;
        this.csdNotificationConfig = csdNotificationConfig;
        this.adminNotificationConfig = adminNotificationConfig;
        this.userService = userService;
    }

    @Transactional
    @Override
    public void execute(StateContext<OrderStatus, OrderEvent> stateContext) {
        CertificateOrder order = stateContext.getExtendedState().get(FsmExtendedStateVariables.ORDER, CertificateOrder.class);
        try {
            List<String> emails = userService.findEmailsByRole(RoleType.CSD_AGENT);
            if (emails == null || emails.isEmpty()) {
                LOG.warn("cannot find CSD emails, order id {}", order.getId());
                emails = userService.findEmailsByRole(RoleType.FLOWERS_ADMIN);
                notificationService.notify(Collections.singletonMap("order", order), emails, adminNotificationConfig);
            } else {
                LOG.info("sending notifications to CSDs: {}", emails);
                notificationService.notify(Collections.singletonMap("order", order), emails, csdNotificationConfig);
            }
        } catch (Exception e) {
            LOG.error("Failed to send notification for order with id {} because of {}", order.getId(), e);
            FlowersException flowersException = new FlowersException(e);
            stateContext.getStateMachine().setStateMachineError(flowersException);
            stateContext.getExtendedState().getVariables().put(FsmExtendedStateVariables.EXCEPTION, flowersException);
        }
    }
}
