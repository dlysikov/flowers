package lu.luxtrust.flowers.fsm.action;

import lu.luxtrust.flowers.entity.enrollment.CertificateOrder;
import lu.luxtrust.flowers.enums.OrderStatus;
import lu.luxtrust.flowers.fsm.FsmExtendedStateVariables;
import lu.luxtrust.flowers.fsm.OrderEvent;
import lu.luxtrust.flowers.repository.CertificateOrderRepository;
import lu.luxtrust.flowers.exception.FlowersException;
import lu.luxtrust.flowers.service.LrsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

@Component
public class SendOrderToLRSAction implements Action<OrderStatus, OrderEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(NotifyUserAction.class);

    private final LrsService lrsService;
    private final CertificateOrderRepository orderRepository;

    @Autowired
    public SendOrderToLRSAction(LrsService lrsService, CertificateOrderRepository orderRepository) {
        this.lrsService = lrsService;
        this.orderRepository = orderRepository;
    }

    @Override
    public void execute(StateContext<OrderStatus, OrderEvent> stateContext) {
        CertificateOrder order = stateContext.getExtendedState().get(FsmExtendedStateVariables.ORDER, CertificateOrder.class);
        LOG.info("Sending order with id={} to lrs", order.getId());

        try {
            order.setLrsOrderNumber(lrsService.register(order));
            orderRepository.save(order);
            LOG.info("CertificateOrder with flowers id={} successfully registered in LRS with identifier={}", order.getId(), order.getLrsOrderNumber());
        } catch(FlowersException e) {
            addError(stateContext, e, order);
        } catch (Exception e) {
            addError(stateContext, new FlowersException(e), order);
        }
    }

    private void addError(StateContext<OrderStatus, OrderEvent> stateContext, Exception e, CertificateOrder order){
        LOG.error("Cant send order with id={} to lrs for processing", order.getId());
        stateContext.getStateMachine().setStateMachineError(e);
        stateContext.getExtendedState().getVariables().put(FsmExtendedStateVariables.EXCEPTION, e);
    }
}
