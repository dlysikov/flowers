package lu.luxtrust.flowers.fsm.action;

import lu.luxtrust.flowers.entity.enrollment.OrderUserValidatePage;
import lu.luxtrust.flowers.enums.OrderStatus;
import lu.luxtrust.flowers.exception.FlowersException;
import lu.luxtrust.flowers.fsm.FsmExtendedStateVariables;
import lu.luxtrust.flowers.fsm.OrderEvent;
import lu.luxtrust.flowers.model.SMS;
import lu.luxtrust.flowers.service.OrderValidationPageService;
import lu.luxtrust.flowers.service.SmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

@Component
public class SendMobileValidationCodeAction implements Action<OrderStatus, OrderEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(SendMobileValidationCodeAction.class);

    private final SmsService smsService;
    private final OrderValidationPageService orderValidationPageService;

    @Autowired
    public SendMobileValidationCodeAction(SmsService smsService, OrderValidationPageService orderValidationPageService) {
        this.smsService = smsService;
        this.orderValidationPageService = orderValidationPageService;
    }

    @Override
    public void execute(StateContext<OrderStatus, OrderEvent> stateContext) {
        Boolean send = stateContext.getExtendedState().get(FsmExtendedStateVariables.SEND_MOBILE_VALIDATION_CODE, Boolean.class);
        OrderUserValidatePage page = stateContext.getExtendedState().get(FsmExtendedStateVariables.VALIDATION_PAGE, OrderUserValidatePage.class);
        if (send) {
            SMS sms = smsService.send(orderValidationPageService.generateMobileValidationSMS(page));
            if (sms.getStatus() != SMS.Status.SENT) {
                FlowersException ex = new FlowersException(String.format("Cant send mobile validation code for order with id %d", page.getOrder().getId()));
                stateContext.getExtendedState().getVariables().put(FsmExtendedStateVariables.EXCEPTION, ex);
                stateContext.getStateMachine().setStateMachineError(ex);
            }
        } else {
            if (stateContext.getStateMachine().hasStateMachineError()) {
                Exception e = stateContext.getExtendedState().get(FsmExtendedStateVariables.EXCEPTION, Exception.class);
                LOG.info("Skipping mobile validation code sending for order with id={}. Reason: exception", page.getOrder().getId(), e);
            } else {
                LOG.info("Skipping mobile validation code sending for order with id={}", page.getOrder().getId());
            }
        }
    }
}
