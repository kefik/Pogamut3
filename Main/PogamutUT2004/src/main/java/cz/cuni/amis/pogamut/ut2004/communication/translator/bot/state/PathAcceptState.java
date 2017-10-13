/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.pogamut.ut2004.communication.translator.bot.state;

import cz.cuni.amis.fsm.FSMState;
import cz.cuni.amis.fsm.FSMTransition;
import cz.cuni.amis.fsm.IFSMState;
import cz.cuni.amis.pogamut.base.communication.messages.InfoMessage;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PathList;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PathListEnd;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PathListStart;
import cz.cuni.amis.pogamut.ut2004.communication.translator.TranslatorContext;
import cz.cuni.amis.pogamut.ut2004.communication.translator.bot.support.BotListState;
import cz.cuni.amis.pogamut.ut2004.communication.translator.shared.events.Path;

/**
 * Takes care of the path list. It stores them inside a List object and when END message comes it sends
 * them to the world view via Path event.
 * @author Jimmy
 */
@FSMState(map = {
					@FSMTransition(
							state = BotAliveState.class,
							symbol = {PathListEnd.class },
							transition = {}
					)
				}
)
public class PathAcceptState extends BotListState<PathList, TranslatorContext> {

    private String pathId = null;

	public PathAcceptState() {
		super(PathListStart.class, PathList.class, PathListEnd.class);
	}

    @Override
	public void stateEntering(TranslatorContext context, IFSMState<InfoMessage, TranslatorContext> fromState, InfoMessage symbol) {
        super.stateEntering(context, fromState, symbol);
        pathId = ((PathListStart)symbol).getMessageId();
    }

	@Override
	public void stateLeaving(TranslatorContext context, IFSMState<InfoMessage, TranslatorContext> toState, InfoMessage symbol) {
		super.stateLeaving(context, toState, symbol);
		long simTime = 
			(symbol instanceof IWorldChangeEvent ? ((IWorldChangeEvent)symbol).getSimTime() : 0);
		context.getEventQueue().pushEvent(new Path(pathId, getList(), simTime));
		getList().clear();
	}
}
