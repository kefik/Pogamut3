package cz.cuni.amis.fsm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation marking the class as FSM state containing map() attribute that specifies transitions
 * leading from this state.
 * @author Jimmy
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface FSMState {
	
	FSMTransition[] map();
	
}
