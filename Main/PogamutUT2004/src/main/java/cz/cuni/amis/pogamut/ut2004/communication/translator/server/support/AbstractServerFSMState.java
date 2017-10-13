package cz.cuni.amis.pogamut.ut2004.communication.translator.server.support;

import cz.cuni.amis.fsm.IFSMState;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.AliveMessage;
import cz.cuni.amis.pogamut.ut2004.communication.translator.TranslatorContext;
import cz.cuni.amis.pogamut.ut2004.communication.translator.TranslatorMessages;
import cz.cuni.amis.pogamut.ut2004.communication.translator.UnexpectedMessageException;

/**
 * This abstract server states is handling ALIVE message, descendants does not need to care about those messages.
 * @author Jimmy
 *
 * @param <SYMBOL>
 * @param <CONTEXT>
 */
public abstract class AbstractServerFSMState<SYMBOL, CONTEXT extends TranslatorContext> implements IFSMState<SYMBOL, CONTEXT> {
	
	protected abstract void innerStateSymbol(CONTEXT context, SYMBOL symbol);

	@Override
	public final void stateSymbol(CONTEXT context, SYMBOL symbol) {
		if (symbol instanceof AliveMessage) {
			if (!(symbol instanceof IWorldChangeEvent)) throw new UnexpectedMessageException(TranslatorMessages.messageNotWorldEvent(this, symbol), this);
			context.getEventQueue().pushEvent((IWorldChangeEvent) symbol);
		} else {
			innerStateSymbol(context, symbol);
		}
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

}
