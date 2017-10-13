package cz.cuni.amis.pogamut.ut2004.communication.translator.server.state;


import cz.cuni.amis.fsm.FSMState;
import cz.cuni.amis.fsm.FSMTransition;
import cz.cuni.amis.fsm.IFSMState;
import cz.cuni.amis.pogamut.base.communication.messages.InfoMessage;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.HandShakeEnd;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ItemCategoryStart;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ItemListStart;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.MapListStart;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.MutatorListStart;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPointListStart;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerListStart;
import cz.cuni.amis.pogamut.ut2004.communication.translator.TranslatorContext;
import cz.cuni.amis.pogamut.ut2004.communication.translator.TranslatorMessages;
import cz.cuni.amis.pogamut.ut2004.communication.translator.UnexpectedMessageException;
import cz.cuni.amis.pogamut.ut2004.communication.translator.server.support.AbstractServerFSMState;
import cz.cuni.amis.pogamut.ut2004.communication.translator.shared.events.MapPointListObtained;

/**
 * This class implements a batch handling logic.
 * @author Jimmy
 */
@FSMState(map = { 
					@FSMTransition(
						state=ItemCategoryState.class, 
						symbol={ItemCategoryStart.class}, 
						transition={}
					),				
					@FSMTransition(
						state=MutatorListState.class, 
						symbol={MutatorListStart.class}, 
						transition={}),
					@FSMTransition(
						state = NavPointListState.class, 
						symbol = { NavPointListStart.class }, 
						transition = {}
					),
					@FSMTransition(
						state = ItemListState.class,
						symbol = { ItemListStart.class },
						transition = {}
					),
					@FSMTransition(
							state = PlayerListState.class, 
							symbol = { PlayerListStart.class }, 
							transition = {}
					),
                    @FSMTransition(
							state = MapListState.class,
							symbol = { MapListStart.class },
							transition = {}
					)
				}
)
public class ServerRunningState extends AbstractServerFSMState<InfoMessage, TranslatorContext> {

    @Override
    public void stateEntering(TranslatorContext context, IFSMState<InfoMessage, TranslatorContext> arg1, InfoMessage arg2) {
    }

    @Override
    protected void innerStateSymbol(TranslatorContext context, InfoMessage obj) {
        if (obj instanceof IWorldChangeEvent) {
        	if (obj instanceof HandShakeEnd) {
        		long simTime = ((IWorldChangeEvent)obj).getSimTime();
        		context.getEventQueue().pushEvent(new MapPointListObtained(context.getNavPoints(), context.getItems(), simTime));
        	} else {
        		context.getEventQueue().pushEvent((IWorldChangeEvent) obj);
        	}
        } else {
        	throw new UnexpectedMessageException(TranslatorMessages.messageNotWorldEvent(this, obj), context.getLogger(), this);
        }

    }

    @Override
    public void stateLeaving(TranslatorContext context, IFSMState<InfoMessage, TranslatorContext> arg1, InfoMessage symbol) {
    }

    @Override
    public void init(TranslatorContext arg0) {        
    }

    @Override
    public void restart(TranslatorContext arg0) {
    }
    
}