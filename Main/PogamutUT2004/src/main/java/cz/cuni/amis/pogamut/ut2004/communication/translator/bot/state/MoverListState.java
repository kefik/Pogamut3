package cz.cuni.amis.pogamut.ut2004.communication.translator.bot.state;

import cz.cuni.amis.fsm.FSMState;
import cz.cuni.amis.fsm.FSMTransition;
import cz.cuni.amis.fsm.IFSMState;
import cz.cuni.amis.pogamut.base.communication.messages.InfoMessage;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Mover;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.MoverListEnd;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.MoverListStart;
import cz.cuni.amis.pogamut.ut2004.communication.translator.TranslatorContext;
import cz.cuni.amis.pogamut.ut2004.communication.translator.bot.support.BotListState;
import cz.cuni.amis.pogamut.ut2004.communication.translator.shared.events.MoverListObtained;
import cz.cuni.amis.pogamut.ut2004.communication.translator.shared.events.MutatorListObtained;


/**
 * Takes care of the mover list. It stores them inside a List object and when END message comes it sends
 * them to the world view via MoverListObtained event.
 * @author Knight
 */
@FSMState(map={
				@FSMTransition(
					state=HandshakeControllerState.class,
					symbol={MoverListEnd.class},
					transition={})
				}
)
public class MoverListState extends BotListState<Mover, TranslatorContext>{

	public MoverListState() {
		super(MoverListStart.class, Mover.class, MoverListEnd.class);
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
		context.getEventQueue().pushEvent(new MoverListObtained(getList(), simTime));
		newList();
	}

	@Override
	public void stateSymbol(TranslatorContext context, InfoMessage symbol) {
		super.stateSymbol(context, symbol);
		context.getEventQueue().pushEvent((Mover)symbol);
	}

}
