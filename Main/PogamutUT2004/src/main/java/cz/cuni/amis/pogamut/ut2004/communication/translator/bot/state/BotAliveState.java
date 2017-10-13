package cz.cuni.amis.pogamut.ut2004.communication.translator.bot.state;

import java.util.HashSet;
import java.util.Set;

import cz.cuni.amis.fsm.FSMState;
import cz.cuni.amis.fsm.FSMTransition;
import cz.cuni.amis.fsm.IFSMState;
import cz.cuni.amis.pogamut.base.communication.messages.InfoMessage;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BotKilled;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PathListStart;
import cz.cuni.amis.pogamut.ut2004.communication.translator.TranslatorContext;
import cz.cuni.amis.pogamut.ut2004.communication.translator.TranslatorMessages;
import cz.cuni.amis.pogamut.ut2004.communication.translator.UnexpectedMessageException;
import cz.cuni.amis.pogamut.ut2004.communication.translator.bot.support.AbstractBotFSMState;
import cz.cuni.amis.pogamut.ut2004.communication.worldview.objects.IGBViewable;

/**
 * This class implements a batch handling logic.
 * @author Jimmy
 */
@FSMState(map = { 
					@FSMTransition(
							state = BotDeadState.class, 
							symbol = {BotKilled.class }, 
							transition = {}
					),
                    @FSMTransition(
                        state = PathAcceptState.class,
						symbol = {PathListStart.class },
						transition = {}
                    )
				}
)
public class BotAliveState extends AbstractBotFSMState<InfoMessage, TranslatorContext> {

	/**
	 * Last batch of the objects we have seen in previous moment.
	 */
	private Set<IGBViewable> lastBatch = new HashSet<IGBViewable>();
	
	/**
	 * Current objects we have in the field of view.
	 */
    private Set<IGBViewable> currentBatch = new HashSet<IGBViewable>();

    public void stateEntering(TranslatorContext context, IFSMState<InfoMessage, TranslatorContext> arg1, InfoMessage arg2) {
    }

    public void stateSymbol(TranslatorContext context, InfoMessage obj) {
/*        if (obj instanceof IGBViewable) {
            currentBatch.add((IGBViewable) obj);
            if (!lastBatch.contains(obj) || obj instanceof Player) {
            	if (!(obj instanceof IWorldChangeEvent)) throw new UnexpectedMessageException(TranslatorMessages.messageNotWorldEvent(this, obj), context.getLogger(), this);
            	context.getEventQueue().pushEvent((IWorldChangeEvent)obj);
            }
        } else if (obj instanceof EndMessage) {
            // we've got end of the batch message!

            // remove all message from last batch that is in current batch (we
            // can still see them)
            lastBatch.removeAll(currentBatch);

            // for each object that disappeared from the field of view
            for (IGBViewable object : lastBatch) {
                // generate disappear event
                context.getEventQueue().pushEvent(object.createDisappearEvent());
            }

            // write current batch as the last one
            lastBatch = currentBatch;

            // create new batch
            currentBatch = new HashSet<IGBViewable>(lastBatch.size() + 10);

            // push EndMessage event
            if (!(obj instanceof IWorldChangeEvent)) throw new UnexpectedMessageException(TranslatorMessages.messageNotWorldEvent(this, obj), context.getLogger(), this);
            context.getEventQueue().pushEvent((IWorldChangeEvent) obj);
        } else if (obj instanceof IWorldChangeEvent) {
            context.getEventQueue().pushEvent((IWorldChangeEvent) obj);
        } else {
        	throw new UnexpectedMessageException(TranslatorMessages.messageNotWorldEvent(this, obj), context.getLogger(), this);
        }
*/
    	
        context.getEventQueue().pushEvent((IWorldChangeEvent) obj);
    }

    public void stateLeaving(TranslatorContext context, IFSMState<InfoMessage, TranslatorContext> arg1, InfoMessage symbol) {
    	if (symbol instanceof BotKilled) {
            // the bot has been killed! 
            // for each object that disappeared from the field of view
    		/*lastBatch.removeAll(currentBatch);
            for (IGBViewable object : lastBatch) {
                // generate disappear event
                context.getEventQueue().pushEvent(object.createDisappearEvent());
            }
            lastBatch.clear();
            for (IGBViewable object : currentBatch) {
                // generate disappear event
                context.getEventQueue().pushEvent(object.createDisappearEvent());
            }
            currentBatch.clear();
            */
             // push BotKilled event
            if (!(symbol instanceof IWorldChangeEvent)) throw new UnexpectedMessageException(TranslatorMessages.messageNotWorldEvent(this, symbol), context.getLogger(), this);
            context.getEventQueue().pushEvent((IWorldChangeEvent) symbol);
    	}
    }

    public void init(TranslatorContext arg0) {        
    }

    public void restart(TranslatorContext arg0) {
    	lastBatch.clear();
    	currentBatch.clear();
    }
    
}