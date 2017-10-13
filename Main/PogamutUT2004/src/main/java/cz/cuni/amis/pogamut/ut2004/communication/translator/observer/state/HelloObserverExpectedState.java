package cz.cuni.amis.pogamut.ut2004.communication.translator.observer.state;

import cz.cuni.amis.fsm.FSMInitialState;
import cz.cuni.amis.fsm.FSMState;
import cz.cuni.amis.fsm.FSMTransition;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.HelloObserverHandshake;
import cz.cuni.amis.pogamut.ut2004.communication.translator.TranslatorContext;
import cz.cuni.amis.pogamut.ut2004.communication.translator.observer.support.ObserverMessageExpectedState;
import cz.cuni.amis.pogamut.ut2004.communication.translator.shared.transition.ReadyRequestedTransition;

/**
 * First and initial state of the ObserverFSM handler. It expects HELLO_CONTROL_SERVER msg and throws exception if it doesn't come.
 * @author Jimmy
 */
@FSMState(map={
				@FSMTransition(
						state=ReadyState.class, 
						symbol={HelloObserverHandshake.class}, 
						transition={ReadyRequestedTransition.class}
					)
				}
)
@FSMInitialState
public class HelloObserverExpectedState extends ObserverMessageExpectedState<TranslatorContext> {

	public HelloObserverExpectedState() {
		super(HelloObserverHandshake.class);
	}

}