package lu.luxtrust.flowers.fsm;

import lu.luxtrust.flowers.entity.enrollment.CertificateOrder;
import lu.luxtrust.flowers.enums.OrderStatus;
import lu.luxtrust.flowers.repository.CertificateOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.stereotype.Component;

@Component
public class OrderStateMachineFactory {

    private final StateMachineFactory<OrderStatus, OrderEvent> orderStatusFactory;
    private final CertificateOrderRepository orderRepository;
    private final StateMachinePersister<OrderStatus, OrderEvent, ContextEntity<OrderStatus, OrderEvent, Long>> persister;

    @Autowired
    public OrderStateMachineFactory(StateMachineFactory<OrderStatus, OrderEvent> orderStatusFactory,
                                    CertificateOrderRepository orderRepository,
                                    StateMachinePersister<OrderStatus, OrderEvent, ContextEntity<OrderStatus, OrderEvent, Long>> persister) {
        this.orderStatusFactory = orderStatusFactory;
        this.persister = persister;
        this.orderRepository = orderRepository;
    }

    public StateMachine<OrderStatus, OrderEvent> newStateMachine(Long orderId) throws Exception {
        StateMachine<OrderStatus, OrderEvent> stateMachine = orderStatusFactory.getStateMachine();
        CertificateOrder order = new CertificateOrder();
        order.setStatus(orderRepository.findOrderStatusById(orderId));
        OrderStateMachineContextEntity contextEntity = new OrderStateMachineContextEntity(order);
        persister.restore(stateMachine, contextEntity);
        return stateMachine;
    }

    public StateMachine<OrderStatus, OrderEvent> newStateMachine() {
        StateMachine<OrderStatus, OrderEvent> stateMachine = orderStatusFactory.getStateMachine();
        stateMachine.start();
        return stateMachine;
    }

    public OrderStatus getInitialState() {
        return orderStatusFactory.getStateMachine().getInitialState().getId();
    }
}
