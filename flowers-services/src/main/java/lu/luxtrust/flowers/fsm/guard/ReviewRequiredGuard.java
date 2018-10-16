package lu.luxtrust.flowers.fsm.guard;

import lu.luxtrust.flowers.entity.enrollment.CertificateOrder;
import lu.luxtrust.flowers.entity.system.RequestorConfiguration;
import lu.luxtrust.flowers.enums.CertificateLevel;
import lu.luxtrust.flowers.enums.OrderStatus;
import lu.luxtrust.flowers.fsm.FsmExtendedStateVariables;
import lu.luxtrust.flowers.fsm.OrderEvent;
import lu.luxtrust.flowers.repository.RequestorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;

public class ReviewRequiredGuard implements Guard<OrderStatus, OrderEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(ReviewRequiredGuard.class);

    private final RequestorConfiguration.ValidatedBy validatedBy;
    private final RequestorRepository RequestorRepository;
    private final OrderStatus targetStatus;

    public ReviewRequiredGuard(RequestorConfiguration.ValidatedBy validatedBy, RequestorRepository RequestorRepository, OrderStatus targetStatus) {
        this.validatedBy = validatedBy;
        this.RequestorRepository = RequestorRepository;
        this.targetStatus = targetStatus;
    }

    @Override
    public boolean evaluate(StateContext<OrderStatus, OrderEvent> stateContext) {
        CertificateOrder order = stateContext.getExtendedState().get(FsmExtendedStateVariables.ORDER, CertificateOrder.class);
        Long id = order.getUnit().getRequestor().getId();
        RequestorConfiguration config = RequestorRepository.findConfiguration(id);
        config.setValidatedBy(config.getCsdEnvolvement() ? RequestorConfiguration.ValidatedBy.CSD : RequestorConfiguration.ValidatedBy.DIA);
        boolean appropriate = order.getHolder().getCertificateLevel() == CertificateLevel.LCP && config.getValidatedBy() == validatedBy;
        if (appropriate) {
            LOG.info("CertificateOrder with id {} is moving to {} status", order.getId(), targetStatus);
        }
        return appropriate;
    }
}

