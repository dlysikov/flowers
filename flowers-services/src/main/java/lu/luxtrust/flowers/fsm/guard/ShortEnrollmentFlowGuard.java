package lu.luxtrust.flowers.fsm.guard;

import lu.luxtrust.flowers.entity.enrollment.CertificateOrder;
import lu.luxtrust.flowers.entity.system.RequestorConfiguration;
import lu.luxtrust.flowers.enums.OrderStatus;
import lu.luxtrust.flowers.fsm.FsmExtendedStateVariables;
import lu.luxtrust.flowers.fsm.OrderEvent;
import lu.luxtrust.flowers.repository.RequestorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;
import org.springframework.stereotype.Component;

@Component
public class ShortEnrollmentFlowGuard implements Guard<OrderStatus, OrderEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(ShortEnrollmentFlowGuard.class);

    private final RequestorRepository RequestorRepository;

    @Autowired
    public ShortEnrollmentFlowGuard(RequestorRepository RequestorRepository) {
        this.RequestorRepository = RequestorRepository;
    }

    @Override
    public boolean evaluate(StateContext<OrderStatus, OrderEvent> stateContext) {
        CertificateOrder order = stateContext.getExtendedState().get(FsmExtendedStateVariables.ORDER, CertificateOrder.class);
        LOG.info("Evaluating short enrollment flow for service {}", order.getUnit().getRequestor());

        RequestorConfiguration config = RequestorRepository.findConfiguration(order.getUnit().getRequestor().getId());
        LOG.info("Short enrollment flow for {} is {}", order.getUnit().getRequestor(), config.getShortFlow() ? "enabled" : "disabled");
        return config.getShortFlow();
    }
}
