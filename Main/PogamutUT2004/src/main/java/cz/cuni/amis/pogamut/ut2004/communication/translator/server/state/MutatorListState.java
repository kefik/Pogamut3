package cz.cuni.amis.pogamut.ut2004.communication.translator.server.state;

import cz.cuni.amis.fsm.FSMState;
import cz.cuni.amis.fsm.FSMTransition;
import cz.cuni.amis.fsm.IFSMState;
import cz.cuni.amis.pogamut.base.communication.messages.InfoMessage;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Mutator;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.MutatorListEnd;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.MutatorListStart;
import cz.cuni.amis.pogamut.ut2004.communication.translator.TranslatorContext;
import cz.cuni.amis.pogamut.ut2004.communication.translator.server.support.ServerListState;
import cz.cuni.amis.pogamut.ut2004.communication.translator.shared.events.MutatorListObtained;


/**
 * Takes care of the mutator list. It stores them inside a List object and when END message comes it sends
 * them to the world view via MutatorListObtained event.
 * @author Jimmy
 */
@FSMState(map={
				@FSMTransition(
					state=ServerRunningState.class, 
					symbol={MutatorListEnd.class}, 
					transition={})
				}
)
public class MutatorListState extends ServerListState<Mutator, TranslatorContext>{

	public MutatorListState() {
		super(MutatorListStart.class, Mutator.class, MutatorListEnd.class);
	}
	
	@Override
	public void stateEntering(TranslatorContext context, IFSMState<InfoMessage, TranslatorContext> fromState, InfoMessage symbol) {
		super.restart(context);
		super.stateEntering(context, fromState, symbol);
	}
	
	@Override
	public void stateLeaving(TranslatorContext context, IFSMState<InfoMessage, TranslatorContext> toState, InfoMessage symbol) {
		super.stateLeaving(context, toState, symbol);
		long simTime = 
			(symbol instanceof IWorldChangeEvent ? ((IWorldChangeEvent)symbol).getSimTime() : 0);
		context.getEventQueue().pushEvent(new MutatorListObtained(getList(), simTime));
		newList();
	}
	
	@Override
	protected void innerStateSymbol(TranslatorContext context,
			InfoMessage symbol) {
		super.innerStateSymbol(context, symbol);
		if (symbol instanceof Mutator) {
			context.getEventQueue().pushEvent((Mutator)symbol);
		}
	}
	

}
