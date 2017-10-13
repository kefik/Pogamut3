package cz.cuni.amis.pogamut.ut2004.communication.translator.bot.state;

import cz.cuni.amis.fsm.FSMState;
import cz.cuni.amis.fsm.FSMTransition;
import cz.cuni.amis.fsm.IFSMState;
import cz.cuni.amis.pogamut.base.communication.messages.InfoMessage;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessageMessage;
import cz.cuni.amis.pogamut.ut2004.communication.translator.TranslatorContext;
import cz.cuni.amis.pogamut.ut2004.communication.translator.TranslatorMessages;
import cz.cuni.amis.pogamut.ut2004.communication.translator.UnexpectedMessageException;
import cz.cuni.amis.pogamut.ut2004.communication.translator.bot.support.BotMessageExpectedState;

/**
 * Waits for InitedMessage message, switches to the FirstSpawnExpectedState.
 * @author Jimmy
 */
@FSMState(
			map={
				@FSMTransition(
					state=FirstSpawnExpectedState.class, 
					symbol={InitedMessageMessage.class}, 
					transition={}
				)
			}
)
public class InitedMessageExpectedState extends BotMessageExpectedState<TranslatorContext>{

	public InitedMessageExpectedState() {
		super(InitedMessage.class);
	}
	
	@Override
	public void stateEntering(TranslatorContext context,
			IFSMState<InfoMessage, TranslatorContext> fromState, InfoMessage symbol) {
		super.stateEntering(context, fromState, symbol);
	}
	
	@Override
	public void stateLeaving(TranslatorContext context, IFSMState<InfoMessage, TranslatorContext> toState, InfoMessage symbol) {
		super.stateLeaving(context, toState, symbol);
		if (!(symbol instanceof IWorldChangeEvent)) throw new UnexpectedMessageException(TranslatorMessages.messageNotWorldEvent(this, symbol), context.getLogger(), this);
		context.getEventQueue().pushEvent((IWorldChangeEvent)symbol);
	}

}
