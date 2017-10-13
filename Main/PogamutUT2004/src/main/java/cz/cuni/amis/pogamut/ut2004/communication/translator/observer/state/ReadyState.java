package cz.cuni.amis.pogamut.ut2004.communication.translator.observer.state;

import cz.cuni.amis.fsm.FSMState;
import cz.cuni.amis.fsm.FSMTransition;
import cz.cuni.amis.fsm.IFSMState;
import cz.cuni.amis.pogamut.base.communication.messages.InfoMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfoMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.HandShakeStart;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Password;
import cz.cuni.amis.pogamut.ut2004.communication.translator.TranslatorContext;
import cz.cuni.amis.pogamut.ut2004.communication.translator.TranslatorMessages;
import cz.cuni.amis.pogamut.ut2004.communication.translator.UnexpectedMessageException;
import cz.cuni.amis.pogamut.ut2004.communication.translator.observer.support.AbstractObserverFSMState;
import cz.cuni.amis.pogamut.ut2004.communication.translator.shared.transition.GameInfoTransition;

/**
 * When the HELLO_CONTROL_SERVER message comes we switch into this state that sends the ReadyCommandRequest event to the world view
 * and waits for next symbol, it may be:
 * <ul>
 * <li>Password message - switches to the PasswordState</li>
 * <li>GameInfo message - switches to the HandshakeControllerState</li>
 * </ul>
 * 
 * @author Jimmy
 */
@FSMState(map = { @FSMTransition(
						state = PasswordState.class, 
						symbol = { Password.class }, 
						transition = {}),
				  @FSMTransition(
						state = ObserverRunningState.class, 
						symbol = { GameInfoMessage.class }, 
						transition = {GameInfoTransition.class})	
				}
)
public class ReadyState extends AbstractObserverFSMState<InfoMessage, TranslatorContext> {

	@Override
	public void init(TranslatorContext context) {
	}

	@Override
	public void restart(TranslatorContext context) {
	}

	@Override
	public void stateEntering(TranslatorContext context,
			IFSMState<InfoMessage, TranslatorContext> fromState,
			InfoMessage symbol) {
	}

	@Override
	public void stateLeaving(TranslatorContext context,
			IFSMState<InfoMessage, TranslatorContext> toState, InfoMessage symbol) {
	}

	@Override
	protected void innerStateSymbol(TranslatorContext context, InfoMessage symbol) {
		if (symbol instanceof HandShakeStart) return;
		throw new UnexpectedMessageException(TranslatorMessages.unexpectedMessage(this, symbol), context.getLogger(), this);
	}

}