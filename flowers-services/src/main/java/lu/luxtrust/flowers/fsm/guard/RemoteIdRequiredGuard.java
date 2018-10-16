package lu.luxtrust.flowers.fsm.guard;

import lu.luxtrust.flowers.entity.enrollment.CertificateOrder;
import lu.luxtrust.flowers.enums.CertificateLevel;
import lu.luxtrust.flowers.enums.OrderStatus;
import lu.luxtrust.flowers.fsm.FsmExtendedStateVariables;
import lu.luxtrust.flowers.fsm.OrderEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;
import org.springframework.stereotype.Component;

@Component
public class RemoteIdRequiredGuard implements Guard<OrderStatus, OrderEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(RemoteIdRequiredGuard.class);

    @Override
    public boolean evaluate(StateContext<OrderStatus, OrderEvent> stateContext) {
        CertificateOrder order = stateContext.getExtendedState().get(FsmExtendedStateVariables.ORDER, CertificateOrder.class);
        boolean appropriate = order.getRemoteId() && order.getHolder().getCertificateLevel() != CertificateLevel.LCP;
        if (appropriate) {
            LOG.info("CertificateOrder with id {} is moving to {} status", order.getId(), OrderStatus.REMOTE_IDENTIFICATION_REQUIRED);
        }
        return appropriate;
    }
}
