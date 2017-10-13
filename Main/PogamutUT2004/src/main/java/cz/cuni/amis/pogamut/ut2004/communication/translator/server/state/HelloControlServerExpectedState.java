package cz.cuni.amis.pogamut.ut2004.communication.translator.server.state;

import cz.cuni.amis.fsm.FSMInitialState;
import cz.cuni.amis.fsm.FSMState;
import cz.cuni.amis.fsm.FSMTransition;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.HelloControlServerHandshake;
import cz.cuni.amis.pogamut.ut2004.communication.translator.TranslatorContext;
import cz.cuni.amis.pogamut.ut2004.communication.translator.server.support.ServerMessageExpectedState;
import cz.cuni.amis.pogamut.ut2004.communication.translator.shared.transition.ReadyRequestedTransition;

/**
 * First and initial state of the ServerFSM handler. It expects HELLO_CONTROL_SERVER msg and throws exception if it doesn't come.
 * @author Jimmy
 */
@FSMState(map={
				@FSMTransition(
						state=ReadyState.class, 
						symbol={HelloControlServerHandshake.class}, 
						transition={ReadyRequestedTransition.class}
					)
				}
)
@FSMInitialState
public class HelloControlServerExpectedState extends ServerMessageExpectedState<TranslatorContext> {

	public HelloControlServerExpectedState() {
		super(HelloControlServerHandshake.class);
	}

}