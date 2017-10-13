package cz.cuni.amis.pogamut.ut2004.communication.translator.shared.transition;

import cz.cuni.amis.fsm.IFSMState;
import cz.cuni.amis.fsm.IFSMTransition;
import cz.cuni.amis.pogamut.base.communication.messages.InfoMessage;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo;
import cz.cuni.amis.pogamut.ut2004.communication.translator.TranslatorContext;
import cz.cuni.amis.pogamut.ut2004.communication.translator.TranslatorMessages;
import cz.cuni.amis.pogamut.ut2004.communication.translator.UnexpectedMessageException;

/**
 * Transition that is used in GameInfoExpectedState and ReadyState when the GameInfo message is received
 * to send the GameInfo event.
 * @author Jimmy
 */
public class GameInfoTransition implements IFSMTransition<InfoMessage, TranslatorContext> {

	@Override
	public void init(TranslatorContext context) {
	}

	@Override
	public void restart(TranslatorContext context) {
	}

	@Override
	public void stepped(TranslatorContext context,
			IFSMState<InfoMessage, TranslatorContext> fromState,
			InfoMessage bySymbol,
			IFSMState<InfoMessage, TranslatorContext> toState) {
		if (!(bySymbol instanceof GameInfo)) throw new UnexpectedMessageException(TranslatorMessages.unexpectedMessage(this, bySymbol, GameInfo.class), context.getLogger(), this);
		if (!(bySymbol instanceof IWorldChangeEvent)) throw new UnexpectedMessageException(TranslatorMessages.messageNotWorldEvent(this, bySymbol), context.getLogger(), this);
		context.getEventQueue().pushEvent((GameInfo)bySymbol);		
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

}
