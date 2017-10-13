package cz.cuni.amis.pogamut.ut2004.communication.translator.bot.state;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.cuni.amis.fsm.FSMState;
import cz.cuni.amis.fsm.FSMTransition;
import cz.cuni.amis.fsm.IFSMState;
import cz.cuni.amis.pogamut.base.communication.messages.InfoMessage;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ItemListEnd;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ItemListStart;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.translator.TranslatorContext;
import cz.cuni.amis.pogamut.ut2004.communication.translator.bot.support.BotListState;

@FSMState(
			map={
				@FSMTransition(
					state=HandshakeControllerState.class, 
					symbol={ItemListEnd.class}, 
					transition={}
				)
			}
)
public class ItemListState extends BotListState<Item, TranslatorContext> {

	public ItemListState() {
		super(ItemListStart.class, Item.class, ItemListEnd.class);
	}
	
	@Override
	public void stateLeaving(TranslatorContext translatorContext,
			IFSMState<InfoMessage, TranslatorContext> toState, InfoMessage symbol) {
		processNavPointsAndItems(translatorContext.getNavPoints(), getList(), translatorContext);
		newList();
	}

	private void processNavPointsAndItems(Map<UnrealId, NavPoint> origNavPoints, List<Item> list, TranslatorContext context) {
		Map<UnrealId, Item> items = new HashMap<UnrealId, Item>();
		
		for (Item item : list) { 
			items.put(item.getId(), item);
		}
		
		context.setItems(items);				
	}
	
	@Override
	public void stateSymbol(TranslatorContext context, InfoMessage symbol) {
		super.stateSymbol(context, symbol);
		// DO NOT PUSH - LEAVE IT TO {@link HandshakeEndTransition} that will process it all in one step and pushes all in one step...
		//context.getEventQueue().pushEvent((Item)symbol);
	}
}