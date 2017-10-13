/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.pogamut.ut2004.communication.translator.observer.state;

import cz.cuni.amis.fsm.FSMState;
import cz.cuni.amis.fsm.FSMTransition;
import cz.cuni.amis.fsm.IFSMState;
import cz.cuni.amis.pogamut.base.communication.messages.InfoMessage;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.MapList;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.MapListEnd;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.MapListStart;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.cuni.amis.pogamut.ut2004.communication.translator.TranslatorContext;
import cz.cuni.amis.pogamut.ut2004.communication.translator.observer.support.ObserverListState;
import cz.cuni.amis.pogamut.ut2004.communication.translator.shared.events.MapListObtained;

/**
 *
 * @author ik
 */
@FSMState(map={@FSMTransition(
					state=ObserverRunningState.class,
					symbol={MapListEnd.class},
					transition={})}
)
public class MapListState extends ObserverListState<MapList, TranslatorContext> {

	public MapListState() {
		super(MapListStart.class, MapList.class, MapListEnd.class);
	}

	@Override
	public void stateLeaving(TranslatorContext context,
			IFSMState<InfoMessage, TranslatorContext> toState, InfoMessage symbol) {
		super.stateLeaving(context, toState, symbol);
		long simTime = 
			(symbol instanceof IWorldChangeEvent ? ((IWorldChangeEvent)symbol).getSimTime() : 0);
		context.getEventQueue().pushEvent(new MapListObtained(getList(), simTime));
		newList();
	}
	
	@Override
	protected void innerStateSymbol(TranslatorContext context,
			InfoMessage symbol) {
		super.innerStateSymbol(context, symbol);
		if (symbol instanceof Player) {
			context.getEventQueue().pushEvent((MapList)symbol);
		}
	}

}

