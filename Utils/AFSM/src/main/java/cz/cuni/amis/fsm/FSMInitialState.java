package cz.cuni.amis.fsm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Simple annotation that tells the FSM that this state is initial.
 * <p><p>
 * Note that you may have only one initial state in the FSM.
 *  
 * @author Jimmy
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface FSMInitialState {

}
