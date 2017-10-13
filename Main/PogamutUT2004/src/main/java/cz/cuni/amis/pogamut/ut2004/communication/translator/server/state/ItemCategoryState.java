package cz.cuni.amis.pogamut.ut2004.communication.translator.server.state;

import cz.cuni.amis.fsm.FSMState;
import cz.cuni.amis.fsm.FSMTransition;
import cz.cuni.amis.fsm.IFSMState;
import cz.cuni.amis.pogamut.base.communication.messages.InfoMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ItemCategory;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ItemCategoryEnd;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ItemCategoryStart;
import cz.cuni.amis.pogamut.ut2004.communication.translator.TranslatorContext;
import cz.cuni.amis.pogamut.ut2004.communication.translator.TranslatorMessages;
import cz.cuni.amis.pogamut.ut2004.communication.translator.UnexpectedMessageException;
import cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor.ItemDescriptor;
import cz.cuni.amis.pogamut.ut2004.communication.translator.server.support.AbstractServerFSMState;
import cz.cuni.amis.pogamut.ut2004.communication.translator.shared.events.ItemDescriptorObtained;

/**
 * Takes care of the ITC messages creating new categories inside the ItemTranslator class. Every ITC message that
 * comes is sent to the ItemTranslator object from the TranslatorContext.
 * @author Jimmy
 */
@FSMState(map={
			@FSMTransition(
					state=ServerRunningState.class, 
					symbol={ItemCategoryEnd.class}, 
					transition={})
		}
)
public class ItemCategoryState extends AbstractServerFSMState<InfoMessage, TranslatorContext>{

	@Override
	public void init(TranslatorContext context) {
	}

	@Override
	public void restart(TranslatorContext context) {
	}

	@Override
	public void stateEntering(TranslatorContext context,
							  IFSMState<InfoMessage, TranslatorContext> fromState,
			                  InfoMessage symbol) {
		if (!(symbol instanceof ItemCategoryStart)) throw new UnexpectedMessageException(TranslatorMessages.unexpectedMessage(this, symbol, ItemCategoryStart.class), context.getLogger(), this);
	}

	@Override
	public void stateLeaving(TranslatorContext context,
			IFSMState<InfoMessage, TranslatorContext> toState, InfoMessage symbol) {
		if (!(symbol instanceof ItemCategoryEnd)) throw new UnexpectedMessageException(TranslatorMessages.unexpectedMessage(this, symbol, ItemCategoryEnd.class), context.getLogger(), this);
	}

	@Override
	protected void innerStateSymbol(TranslatorContext context, InfoMessage symbol) {
		if (!(symbol instanceof ItemCategory)) throw new UnexpectedMessageException(TranslatorMessages.unexpectedMessage(this, symbol, ItemCategory.class), context.getLogger(), this);
		long simTime = ((ItemCategory)symbol).getSimTime();
		ItemDescriptor desc = context.getItemTranslator().createDescriptor((ItemCategory)symbol);
		if (desc == null) {
			context.getLogger().warning("ItemCategory was not translated into ItemDescriptor: " + symbol);
			return;
		}
		context.getEventQueue().pushEvent(new ItemDescriptorObtained(desc, simTime));
	}

}