package cz.cuni.amis.pogamut.ut2004.communication.translator.observer.support;

import cz.cuni.amis.fsm.IFSMState;
import cz.cuni.amis.pogamut.base.communication.messages.InfoMessage;
import cz.cuni.amis.pogamut.ut2004.communication.translator.TranslatorContext;
import cz.cuni.amis.pogamut.ut2004.communication.translator.TranslatorMessages;
import cz.cuni.amis.pogamut.ut2004.communication.translator.UnexpectedMessageException;

/**
 * Used to check the message that is used to leave the state.
 * <p><p>
 * All other messages are considered as the violation of the protocol -> throws UnexpectedMessageException.
 * @author Jimmy
 * 
 * @param <CONTEXT>
 */
public abstract class ObserverMessageExpectedState<CONTEXT extends TranslatorContext> extends AbstractObserverFSMState<InfoMessage, CONTEXT> {

	@SuppressWarnings("unchecked")
	private Class message;
	
	/**
	 * @param expectedMessage message that triggers the switch to another state
	 */
	@SuppressWarnings("unchecked")
	public ObserverMessageExpectedState(Class expectedMessage) {
		message = expectedMessage;
	}

	@Override
	public void init(CONTEXT context) {
	}

	@Override
	public void restart(CONTEXT context) {
	}

	@Override
	public void stateEntering(CONTEXT context,
			IFSMState<InfoMessage, CONTEXT> fromState, InfoMessage symbol) {
	}

	@Override
	public void stateLeaving(CONTEXT context, IFSMState<InfoMessage, CONTEXT> toState, InfoMessage symbol) {
		if (!symbol.getClass().equals(message)) {
			if (!message.isAssignableFrom(symbol.getClass())) {
				throw new UnexpectedMessageException(TranslatorMessages.unexpectedMessage(this, symbol), context.getLogger(), this);
			}
		}
	}

	@Override
	protected void innerStateSymbol(CONTEXT context, InfoMessage symbol) {
		throw new UnexpectedMessageException(TranslatorMessages.unexpectedMessage(this, symbol), context.getLogger(), this);
	}

}

