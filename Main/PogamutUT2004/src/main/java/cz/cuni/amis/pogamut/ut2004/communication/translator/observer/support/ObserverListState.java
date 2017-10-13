package cz.cuni.amis.pogamut.ut2004.communication.translator.observer.support;

import java.util.ArrayList;
import java.util.List;

import cz.cuni.amis.fsm.IFSMState;
import cz.cuni.amis.pogamut.base.communication.messages.InfoMessage;
import cz.cuni.amis.pogamut.ut2004.communication.translator.TranslatorContext;
import cz.cuni.amis.pogamut.ut2004.communication.translator.TranslatorMessages;
import cz.cuni.amis.pogamut.ut2004.communication.translator.UnexpectedMessageException;

/**
 * Support class that takes care of batch of messages in the form of:<p>
 * START_MSG MSG MSG .... MSG END_MSG
 * <p><p>
 * Performs class checks over the symbols that are used to enter / leave the state.
 * <p><p>
 * Stores all the MSGs inside a list.
 * <p><p>
 * To use this state you have to subclass this abstract class, use correct super() inside the constructor of your class
 * and override stateLeaving() method (first by calling super.stateLeaving()) and use getList() to obtain the list
 * of all messages that came between START and END message. 
 * 
 * @author Jimmy
 *
 * @param <MESSAGE>
 * @param <CONTEXT>
 */
public abstract class ObserverListState<MESSAGE, CONTEXT extends TranslatorContext> extends AbstractObserverFSMState<InfoMessage, CONTEXT> {
	
	private List<MESSAGE> messages = null;
	@SuppressWarnings("unchecked")
	private Class beginMessage;
	private Class<MESSAGE> message;
	@SuppressWarnings("unchecked")
	private Class endMessage;
	
	/**
	 * @param beginMessage message class that should be used to enter this state
	 * @param message class of messages we should store inside the list (must be the same as generic type MESSAGE!)
	 * @param endMessage message class that should be used to leave this state
	 */
	@SuppressWarnings("unchecked")
	public ObserverListState(Class beginMessage, Class<MESSAGE> message, Class endMessage) {
		this.beginMessage = beginMessage;
		this.message = message;
		this.endMessage = endMessage;
	}
	
	protected List<MESSAGE> getList() {
		return messages;
	}
	
	protected void newList() {
		messages = new ArrayList<MESSAGE>();
	}

	@Override
	public void init(CONTEXT context) {
		messages = new ArrayList<MESSAGE>();
	}

	@Override
	public void restart(CONTEXT context) {
		messages = new ArrayList<MESSAGE>();
	}

	@Override
	public void stateEntering(CONTEXT context, IFSMState<InfoMessage, CONTEXT> fromState, InfoMessage symbol) {
		if (!symbol.getClass().equals(beginMessage)) throw new UnexpectedMessageException(TranslatorMessages.unexpectedMessage(this, symbol, beginMessage), context.getLogger(), this);
	}

	@Override
	public void stateLeaving(CONTEXT context,
			IFSMState<InfoMessage, CONTEXT> toState, InfoMessage symbol) {
		if (!symbol.getClass().equals(endMessage)) throw new UnexpectedMessageException(TranslatorMessages.unexpectedMessage(this, symbol, endMessage), context.getLogger(), this);		
	}

	@Override
	protected void innerStateSymbol(CONTEXT context, InfoMessage symbol) {
		if (!symbol.getClass().equals(message)) {
			if (!message.isAssignableFrom(symbol.getClass())) {
				throw new UnexpectedMessageException(TranslatorMessages.unexpectedMessage(this, symbol, message), context.getLogger(), this);
			}
		}
		messages.add(message.cast(symbol));
	}

}
