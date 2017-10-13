package cz.cuni.amis.fsm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used inside the map() of the FSMState transition to specify
 * the FSM transition from a certain state.
 * 
 * @author Jimmy
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface FSMTransition {

	/**
	 * Classes of the symbols that triggers the transitions (or logic).
	 * @return
	 */
	Class[] symbol();
	
	/**
	 * Classes of the IFSMTransition classes that should be stepped before switching to the next state.
	 * <p><p>
	 * Transitions are executed in provided order between oldState.stateLeaving() and newState.stateEntering()
	 * @return
	 */
	Class<? extends IFSMTransition>[] transition();
	
	/**
	 * Target of the transition. What state to switch to when this transition is triggered.
	 * @return
	 */
	Class<? extends IFSMState> state();

}
