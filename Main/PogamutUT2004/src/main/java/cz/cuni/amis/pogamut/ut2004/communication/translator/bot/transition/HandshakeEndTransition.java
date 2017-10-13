package cz.cuni.amis.pogamut.ut2004.communication.translator.bot.transition;

import cz.cuni.amis.fsm.IFSMState;
import cz.cuni.amis.fsm.IFSMTransition;
import cz.cuni.amis.pogamut.base.communication.messages.InfoMessage;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ItemListEnd;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ItemListStart;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPointListEnd;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPointListStart;
import cz.cuni.amis.pogamut.ut2004.communication.translator.TranslatorContext;
import cz.cuni.amis.pogamut.ut2004.communication.translator.shared.events.MapPointListObtained;

public class HandshakeEndTransition implements IFSMTransition<InfoMessage, TranslatorContext> {

	@Override
	public void init(TranslatorContext context) {
	}

	@Override
	public void restart(TranslatorContext context) {
	}

	@Override
	public void stepped(TranslatorContext context,
			IFSMState<InfoMessage, TranslatorContext> fromState,
			InfoMessage bySymbol,
			IFSMState<InfoMessage, TranslatorContext> toState) {
		
		long simTime = (bySymbol instanceof IWorldChangeEvent ? ((IWorldChangeEvent)bySymbol).getSimTime() : 0);
		
		// PROCESS NAVIGATION GRAPHS
		context.processNavPointLinks();
		// PROCESS NAVPOINT<->ITEM BINDING
		context.processNavPointsAndItems();
		
		// EXPORT NAVIGATION GRAPH
		if (context.getNavPoints() != null) {
			context.getEventQueue().pushEvent(new NavPointListStart());
			for (NavPoint np : context.getNavPoints().values()) {
				context.getEventQueue().pushEvent(np);
			}
			context.getEventQueue().pushEvent(new NavPointListEnd());
		}
		
		// EXPORT ITEMS
		if (context.getItems() != null) {
			context.getEventQueue().pushEvent(new ItemListStart());
			for (Item item : context.getItems().values()) {
				context.getEventQueue().pushEvent(item);
			}
			context.getEventQueue().pushEvent(new ItemListEnd());
		}
		
		// EXPORT GENERAL EVENT
		context.getEventQueue().pushEvent(new MapPointListObtained(context.getNavPoints(), context.getItems(), simTime));
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

}
