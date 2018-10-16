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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

@Component
public class NotifyUserAction implements Action<OrderStatus, OrderEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(NotifyUserAction.class);

    private final OrderValidationPageService orderValidationPageService;
    private final NotificationService notificationService;
    private final CertificateOrderRepository orderRepository;
    private final Boolean sendSmsValidationCode;

    @Autowired
    public NotifyUserAction(OrderValidationPageService orderValidationPageService,
                            NotificationService notificationService,
                            @Value("${user-validation-page.mobile-code-validation.enabled}") Boolean sendSmsValidationCode,
                            CertificateOrderRepository orderRepository) {
        this.orderValidationPageService = orderValidationPageService;
        this.notificationService = notificationService;
        this.orderRepository = orderRepository;
        this.sendSmsValidationCode = sendSmsValidationCode;
    }

    @Override
    public void execute(StateContext<OrderStatus, OrderEvent> stateContext) {
        CertificateOrder order = stateContext.getExtendedState().get(FsmExtendedStateVariables.ORDER, CertificateOrder.class);
        try {
            OrderUserValidatePage page = orderValidationPageService.generateValidationPage(order);
            order.setStatus(stateContext.getTarget().getId());
            orderRepository.save(order);
            notificationService.notifyEndUserToValidateOrder(page);
            stateContext.getExtendedState().getVariables().put(FsmExtendedStateVariables.VALIDATION_PAGE, page);
            stateContext.getExtendedState().getVariables().put(FsmExtendedStateVariables.SEND_MOBILE_VALIDATION_CODE, sendSmsValidationCode);
        } catch (Exception e) {
            LOG.error("Cant move order with id {} into status {} and notify user, because of {}", order.getId(), stateContext.getTarget().getId(), e);
            FlowersException flowersException = new FlowersException(e);
            stateContext.getStateMachine().setStateMachineError(flowersException);
            stateContext.getExtendedState().getVariables().put(FsmExtendedStateVariables.EXCEPTION, flowersException);
        }
    }
}
