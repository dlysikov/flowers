package lu.luxtrust.flowers.fsm;

import org.springframework.statemachine.StateMachineContext;

import java.io.Serializable;

public interface ContextEntity <S, E, ID extends Serializable> {

    StateMachineContext<S, E> getStateMachineContext();

    void setStateMachineContext(StateMachineContext<S, E> context);

}
