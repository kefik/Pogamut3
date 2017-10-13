package cz.cuni.amis.pogamut.ut2004.communication.translator.server.state;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.cuni.amis.fsm.FSMState;
import cz.cuni.amis.fsm.FSMTransition;
import cz.cuni.amis.fsm.IFSMState;
import cz.cuni.amis.pogamut.base.communication.messages.InfoMessage;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ItemListEnd;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ItemListStart;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.translator.TranslatorContext;
import cz.cuni.amis.pogamut.ut2004.communication.translator.server.support.ServerListState;
import cz.cuni.amis.pogamut.ut2004.communication.translator.shared.events.MapPointListObtained;
import java.util.logging.Level;

@FSMState(
			map={
				@FSMTransition(
					state=ServerRunningState.class, 
					symbol={ItemListEnd.class}, 
					transition={}
				)
			}
)
public class ItemListState extends ServerListState<Item, TranslatorContext> {

	public ItemListState() {
		super(ItemListStart.class, Item.class, ItemListEnd.class);
	}
	
	@Override
	public void stateLeaving(TranslatorContext translatorContext,
			IFSMState<InfoMessage, TranslatorContext> toState, InfoMessage symbol) {
		long simTime = 
			(symbol instanceof IWorldChangeEvent ? ((IWorldChangeEvent)symbol).getSimTime() : 0);
		processItems(translatorContext.getNavPoints(), getList(), translatorContext, simTime);
		newList();
	}

	private void processItems(Map<UnrealId, NavPoint> origNavPoints, List<Item> list, TranslatorContext context, long simTime) {
		Map<UnrealId, Item> items = new HashMap<UnrealId, Item>();
		
		for (Item item : list) { 
			items.put(item.getId(), item);
		}
		
		context.setItems(items);		
		
		if (context.getNavPoints() != null && context.getNavPoints().size() != 0) {
			context.processNavPointsAndItems();
			if (context.getLogger().isLoggable(Level.FINE)) context.getLogger().fine("Pushing NavPoint events.");
			context.getEventQueue().pushEvent(context.getNavPoints().values().toArray(new IWorldChangeEvent[0]));
			if (context.getLogger().isLoggable(Level.FINE)) context.getLogger().fine("Pushing Item events.");
			context.getEventQueue().pushEvent(context.getItems().values().toArray(new IWorldChangeEvent[0]));
			context.getEventQueue().pushEvent(new MapPointListObtained(context.getNavPoints(), context.getItems(), simTime));
		} else {
			if (context.getLogger().isLoggable(Level.FINE)) context.getLogger().fine("Pushing Item events.");
			context.getEventQueue().pushEvent(context.getItems().values().toArray(new IWorldChangeEvent[0]));
		}
		
	}
	
}