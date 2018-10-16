package lu.luxtrust.flowers.fsm.action;

import lu.luxtrust.flowers.entity.enrollment.CertificateOrder;
import lu.luxtrust.flowers.enums.OrderStatus;
import lu.luxtrust.flowers.fsm.FsmExtendedStateVariables;
import lu.luxtrust.flowers.fsm.OrderEvent;
import lu.luxtrust.flowers.repository.CertificateOrderRepository;
import lu.luxtrust.flowers.repository.OrderUserValidatePageRepository;
import lu.luxtrust.flowers.exception.FlowersException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

@Component
public class UserValidatesOrderAction implements Action<OrderStatus, OrderEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(UserValidatesOrderAction.class);

    private final CertificateOrderRepository orderRepository;
    private final OrderUserValidatePageRepository orderUserValidatePageRepository;

    @Autowired
    public UserValidatesOrderAction(CertificateOrderRepository orderRepository,
                                    OrderUserValidatePageRepository orderUserValidatePageRepository) {
        this.orderRepository = orderRepository;
        this.orderUserValidatePageRepository = orderUserValidatePageRepository;
    }

    @Override
    public void execute(StateContext<OrderStatus, OrderEvent> stateContext) {
        CertificateOrder order = stateContext.getExtendedState().get(FsmExtendedStateVariables.ORDER, CertificateOrder.class);
        try {
            order.setStatus(stateContext.getTarget().getId());
            orderRepository.save(order);
            orderUserValidatePageRepository.removeByOrderId(order.getId());
            LOG.info("User validated order with id {}, and validation page was deactivated", order.getId());
        } catch (Exception e) {
            LOG.error("Cant move order with id {} into status {}, because of {}", order.getId(), stateContext.getTarget().getId(), e);
            FlowersException flowersException = new FlowersException(e);
            stateContext.getStateMachine().setStateMachineError(flowersException);
            stateContext.getExtendedState().getVariables().put(FsmExtendedStateVariables.EXCEPTION, flowersException);
        }
    }
}
