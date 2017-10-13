package cz.cuni.amis.fsm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Simple annotation that tells the FSM that this state is terminal one.
 * @author Jimmy
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface FSMTerminalState {

}
