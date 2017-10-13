package cz.cuni.amis.pogamut.ut2004.communication.translator.observer.state;

import cz.cuni.amis.fsm.FSMState;
import cz.cuni.amis.fsm.FSMTransition;
import cz.cuni.amis.fsm.IFSMState;
import cz.cuni.amis.pogamut.base.communication.messages.InfoMessage;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PasswdOk;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PasswdWrong;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Password;
import cz.cuni.amis.pogamut.ut2004.communication.translator.TranslatorContext;
import cz.cuni.amis.pogamut.ut2004.communication.translator.TranslatorMessages;
import cz.cuni.amis.pogamut.ut2004.communication.translator.UnexpectedMessageException;
import cz.cuni.amis.pogamut.ut2004.communication.translator.observer.support.AbstractObserverFSMState;

/**
 * Entered by the Password message that is sent to the world view, the state then wait for PasswdOk or PasswdWrong
 * message to came.
 * 
 * @author Jimmy
 */
@FSMState(map={@FSMTransition(
                state=ReadyState.class, 
                symbol={PasswdOk.class}, 
                transition={}),
               @FSMTransition(
                state=CommunicationTerminatedState.class, 
                symbol={PasswdWrong.class},
                transition={})}
)
public class PasswordState extends AbstractObserverFSMState<InfoMessage, TranslatorContext> {
	
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
		if (!(symbol instanceof Password)) throw new UnexpectedMessageException(TranslatorMessages.unexpectedMessage(this, symbol, Password.class), context.getLogger(), this);
		if (!(symbol instanceof IWorldChangeEvent)) throw new UnexpectedMessageException(TranslatorMessages.messageNotWorldEvent(this, symbol), context.getLogger(), this);
		context.getEventQueue().pushEvent((IWorldChangeEvent)symbol);
	}

	@Override
	public void stateLeaving(TranslatorContext context,
			IFSMState<InfoMessage, TranslatorContext> toState, InfoMessage symbol) {
	}

	@Override
	protected void innerStateSymbol(TranslatorContext context, InfoMessage symbol) {
		if (!(symbol instanceof Password)) throw new UnexpectedMessageException(TranslatorMessages.unexpectedMessage(this, symbol), context.getLogger(), this);
	}
	
}
