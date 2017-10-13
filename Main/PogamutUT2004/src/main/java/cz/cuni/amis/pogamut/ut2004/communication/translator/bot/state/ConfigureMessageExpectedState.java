package cz.cuni.amis.pogamut.ut2004.communication.translator.bot.state;

import cz.cuni.amis.fsm.FSMState;
import cz.cuni.amis.fsm.FSMTransition;
import cz.cuni.amis.fsm.IFSMState;
import cz.cuni.amis.pogamut.base.communication.messages.InfoMessage;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChangeMessage;
import cz.cuni.amis.pogamut.ut2004.communication.translator.TranslatorContext;
import cz.cuni.amis.pogamut.ut2004.communication.translator.TranslatorMessages;
import cz.cuni.amis.pogamut.ut2004.communication.translator.UnexpectedMessageException;
import cz.cuni.amis.pogamut.ut2004.communication.translator.bot.support.BotMessageExpectedState;

/**
 * Waits for ConfigChange message, switches to the InitedMessageExpectedState.
 * @author Jimmy
 */
@FSMState(map={
				@FSMTransition(
						state=InitedMessageExpectedState.class, 
						symbol={ConfigChangeMessage.class}, 
						transition={}
					)
				}
)
public class ConfigureMessageExpectedState extends BotMessageExpectedState<TranslatorContext> {
	
	public ConfigureMessageExpectedState() {
		super(ConfigChange.class);
	}
	
	@Override
	public void stateLeaving(TranslatorContext context, IFSMState<InfoMessage, TranslatorContext> toState, InfoMessage symbol) {
		super.stateLeaving(context, toState, symbol);
		if (!(symbol instanceof IWorldChangeEvent)) throw new UnexpectedMessageException(TranslatorMessages.messageNotWorldEvent(this, symbol), context.getLogger(), this);
		context.getEventQueue().pushEvent((IWorldChangeEvent)symbol);
	}
	
}
