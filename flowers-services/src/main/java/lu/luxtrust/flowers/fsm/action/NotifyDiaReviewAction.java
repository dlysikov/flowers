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

public class NotifyDiaReviewAction implements Action<OrderStatus, OrderEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(NotifyDiaReviewAction.class);

    private final NotificationService notificationService;
    private final NotificationConfig diaNotificationConfig;
    private final NotificationConfig adminNotificationConfig;
    private final UserService userService;

    public NotifyDiaReviewAction(NotificationService notificationService, NotificationConfig diaNotificationConfig, NotificationConfig adminNotificationConfig, UserService userService) {
        this.notificationService = notificationService;
        this.diaNotificationConfig = diaNotificationConfig;
        this.adminNotificationConfig = adminNotificationConfig;
        this.userService = userService;
    }

    @Transactional
    @Override
    public void execute(StateContext<OrderStatus, OrderEvent> stateContext) {
        CertificateOrder order = stateContext.getExtendedState().get(FsmExtendedStateVariables.ORDER, CertificateOrder.class);
        try {
            List<String> emails = userService.findDiaEmailsByUnit(order.getUnit());
            if (emails == null || emails.isEmpty()) {
                LOG.warn("cannot find dia emails for unit {}, order id {}", order.getUnit().getId(), order.getId());
                emails = userService.findEmailsByRole(RoleType.FLOWERS_ADMIN);
                notificationService.notify(Collections.singletonMap("order", order), emails, adminNotificationConfig);
            } else {
                LOG.info("sending notifications to DIAs: {}", emails);
                notificationService.notify(Collections.singletonMap("order", order), emails, diaNotificationConfig);
            }
        } catch (Exception e) {
            LOG.error("Failed to send notification for order with id {} because of {}", order.getId(), e);
            FlowersException flowersException = new FlowersException(e);
            stateContext.getStateMachine().setStateMachineError(flowersException);
            stateContext.getExtendedState().getVariables().put(FsmExtendedStateVariables.EXCEPTION, flowersException);
        }
    }
}
