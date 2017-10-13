package cz.cuni.amis.pogamut.ut2004.communication.translator.shared.transition;

import cz.cuni.amis.fsm.IFSMState;
import cz.cuni.amis.fsm.IFSMTransition;
import cz.cuni.amis.pogamut.base.communication.messages.InfoMessage;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.pogamut.ut2004.communication.translator.TranslatorContext;
import cz.cuni.amis.pogamut.ut2004.communication.translator.shared.events.ReadyCommandRequest;

public class ReadyRequestedTransition implements IFSMTransition<InfoMessage, TranslatorContext> {

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
		if (bySymbol instanceof IWorldChangeEvent) {
			context.getEventQueue().pushEvent(new ReadyCommandRequest(((IWorldChangeEvent)bySymbol).getSimTime()));
		} else {
			context.getEventQueue().pushEvent(new ReadyCommandRequest(0));
		}
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

}
