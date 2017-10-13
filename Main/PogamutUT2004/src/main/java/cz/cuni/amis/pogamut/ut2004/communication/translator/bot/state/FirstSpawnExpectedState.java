package cz.cuni.amis.pogamut.ut2004.communication.translator.bot.state;

import cz.cuni.amis.fsm.FSMState;
import cz.cuni.amis.fsm.FSMTransition;
import cz.cuni.amis.fsm.IFSMState;
import cz.cuni.amis.pogamut.base.communication.messages.InfoMessage;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Spawn;
import cz.cuni.amis.pogamut.ut2004.communication.translator.TranslatorContext;
import cz.cuni.amis.pogamut.ut2004.communication.translator.bot.support.AbstractBotFSMState;
import cz.cuni.amis.pogamut.ut2004.communication.translator.shared.events.BotFirstSpawned;

/**
 * FSM switches into this state when PlayerList is transmitted during the handshake. It sends InitCommandRequest event
 * and waits for Spawn message and then it sends BotFirstSpawned event. 
 * @author Jimmy
 */
@FSMState(map = { 
					@FSMTransition(
							state = BotAliveState.class, 
							symbol = { Spawn.class }, 
							transition = {}
					)
				}
)
public class FirstSpawnExpectedState extends AbstractBotFSMState<InfoMessage, TranslatorContext> {
    
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
	}

	@Override
	public void stateLeaving(TranslatorContext context,
			IFSMState<InfoMessage, TranslatorContext> toState, InfoMessage symbol) {		
		context.getEventQueue().pushEvent(new BotFirstSpawned());
		context.getEventQueue().pushEvent((Spawn)symbol);
	}

	@Override
	public void stateSymbol(TranslatorContext context, InfoMessage symbol) {
		context.getEventQueue().pushEvent((IWorldChangeEvent)symbol);		
	}

}