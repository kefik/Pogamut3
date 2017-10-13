package cz.cuni.amis.pogamut.ut2004.communication.translator.bot.state;

import cz.cuni.amis.fsm.FSMState;
import cz.cuni.amis.fsm.FSMTransition;
import cz.cuni.amis.fsm.IFSMState;
import cz.cuni.amis.pogamut.base.communication.messages.InfoMessage;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Spawn;
import cz.cuni.amis.pogamut.ut2004.communication.translator.TranslatorContext;
import cz.cuni.amis.pogamut.ut2004.communication.translator.TranslatorMessages;
import cz.cuni.amis.pogamut.ut2004.communication.translator.UnexpectedMessageException;
import cz.cuni.amis.pogamut.ut2004.communication.translator.bot.support.AbstractBotFSMState;
import java.util.logging.Level;

/**
 * Represent state where the bot is dead and can't accept any messages except Spawn!
 * @author Jimmy
 */
@FSMState(map = { 
				@FSMTransition(
						state = BotAliveState.class, 
						symbol = {Spawn.class }, 
						transition = {}
				)
			}
)
public class BotDeadState extends AbstractBotFSMState<InfoMessage, TranslatorContext> {

    public void stateEntering(TranslatorContext arg0, IFSMState<InfoMessage, TranslatorContext> arg1, InfoMessage arg2) {
    	if (arg0.getLogger().isLoggable(Level.WARNING)) arg0.getLogger().warning("Bot switched to DEAD STATE.");
    }

    public void stateSymbol(TranslatorContext context, InfoMessage symbol) {
	    //if (context.getLogger().isLoggable(Level.WARNING)) context.getLogger().warning(TranslatorMessages.unprocessedMessage(this, symbol));
    	// REQUIRED BY EMOHAWK-UDK, for transmitting object attribs changes even if the bot dies
    	context.getEventQueue().pushEvent((IWorldChangeEvent) symbol);
    }

    public void stateLeaving(TranslatorContext arg0, IFSMState<InfoMessage, TranslatorContext> arg1, InfoMessage arg2) {
    	if (!(arg2 instanceof Spawn)) throw new UnexpectedMessageException(TranslatorMessages.unexpectedMessage(this, arg2, Spawn.class), arg0.getLogger(), this);
    	if (!(arg2 instanceof IWorldChangeEvent)) throw new UnexpectedMessageException(TranslatorMessages.messageNotWorldEvent(this, arg2), arg0.getLogger(), this);
    	arg0.getEventQueue().pushEvent((IWorldChangeEvent) arg2);
    }

    public void init(TranslatorContext arg0) {

    }

    public void restart(TranslatorContext arg0) {

    }
    
}
