package lu.luxtrust.flowers.fsm;

import lu.luxtrust.flowers.entity.enrollment.CertificateOrder;
import lu.luxtrust.flowers.enums.OrderStatus;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.support.DefaultStateMachineContext;

public class OrderStateMachineContextEntity implements ContextEntity<OrderStatus, OrderEvent, Long> {

    private final CertificateOrder order;

    OrderStateMachineContextEntity(CertificateOrder order) {
        this.order = order;
    }

    @Override
    public StateMachineContext<OrderStatus, OrderEvent> getStateMachineContext() {
        return new DefaultStateMachineContext<>(order.getStatus(), null, null, null);
    }

    @Override
    public void setStateMachineContext(StateMachineContext<OrderStatus, OrderEvent> context) {
        order.setStatus(context.getState());
    }
}
