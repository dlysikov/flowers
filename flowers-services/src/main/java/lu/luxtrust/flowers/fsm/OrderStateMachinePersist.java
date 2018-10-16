package lu.luxtrust.flowers.fsm;

import lu.luxtrust.flowers.enums.OrderStatus;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.StateMachinePersist;

public class OrderStateMachinePersist implements StateMachinePersist<OrderStatus, OrderEvent, ContextEntity<OrderStatus, OrderEvent, Long>> {
    @Override
    public void write(StateMachineContext<OrderStatus, OrderEvent> context, ContextEntity<OrderStatus, OrderEvent, Long> order) {
        order.setStateMachineContext(context);
    }

    @Override
    public StateMachineContext<OrderStatus, OrderEvent> read(ContextEntity<OrderStatus, OrderEvent, Long> order) {
        return order.getStateMachineContext();
    }
}
