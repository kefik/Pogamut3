package cz.cuni.amis.pogamut.ut2004.communication.translator.bot.state;

import cz.cuni.amis.fsm.FSMState;
import cz.cuni.amis.fsm.FSMTransition;
import cz.cuni.amis.fsm.IFSMState;
import cz.cuni.amis.pogamut.base.communication.messages.InfoMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.HandShakeEnd;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ItemCategoryStart;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ItemListStart;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.MoverListStart;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.MutatorListStart;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPointListStart;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerListStart;
import cz.cuni.amis.pogamut.ut2004.communication.translator.TranslatorContext;
import cz.cuni.amis.pogamut.ut2004.communication.translator.bot.support.AbstractBotFSMState;
import cz.cuni.amis.pogamut.ut2004.communication.translator.bot.transition.HandshakeEndTransition;
import cz.cuni.amis.pogamut.ut2004.communication.translator.shared.events.MapPointListObtained;
import cz.cuni.amis.pogamut.ut2004.communication.translator.shared.transition.InitRequestedTransition;

/**
 * This state is introduced because start/end messages of the list of item categories, mutators, navpoints and players.
 * Without this state we would have difficulties with switching between MutatorListState -> PlayerListState -> etc...<p>
 * As there is always: ... list ... END_MSG NEW_START_MSG ... list ... the problem lies withing END_MSG and NEW_START_MSG.
 * On END_MSG we will switch to this state and on NEW_START_MSG we will switch to the state that will handle appropriate
 * incoming list.
 */
@FSMState(
			map={							
				@FSMTransition(
					state=ItemCategoryState.class, 
					symbol={ItemCategoryStart.class}, 
					transition={}
				),				
				@FSMTransition(
					state=MutatorListState.class, 
					symbol={MutatorListStart.class}, 
					transition={}
				),
				@FSMTransition(
					state=MoverListState.class,
					symbol={MoverListStart.class},
					transition={}
				),
				@FSMTransition(
					state = NavPointListState.class, 
					symbol = { NavPointListStart.class }, 
					transition = {}
				),
				@FSMTransition(
					state = ItemListState.class,
					symbol = { ItemListStart.class },
					transition = {}
				),
				@FSMTransition(
						state = PlayerListState.class, 
						symbol = { PlayerListStart.class }, 
						transition = {}
				),	
				@FSMTransition(
						state = ConfigureMessageExpectedState.class,
						symbol = { HandShakeEnd.class },
						transition = {HandshakeEndTransition.class, InitRequestedTransition.class}
				)
			}
		 )
public class HandshakeControllerState extends AbstractBotFSMState<InfoMessage, TranslatorContext>{

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
	}

	@Override
	public void stateSymbol(TranslatorContext context, InfoMessage symbol) {
		if (symbol instanceof GameInfo) {
			context.getEventQueue().pushEvent((GameInfo)symbol);
			return;
		}
	}

}
