package cz.cuni.amis.pogamut.ut2004.communication.translator.observer.state;

import cz.cuni.amis.fsm.FSMState;
import cz.cuni.amis.fsm.FSMTransition;
import cz.cuni.amis.fsm.IFSMState;
import cz.cuni.amis.pogamut.base.communication.messages.InfoMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPointNeighbourLink;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPointNeighbourLinkEnd;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPointNeighbourLinkStart;
import cz.cuni.amis.pogamut.ut2004.communication.translator.TranslatorContext;
import cz.cuni.amis.pogamut.ut2004.communication.translator.observer.support.ObserverListState;

/**
 * Takes care of the navpoint neighbour list. It stores them inside a List object and when END message comes it 
 * stores them inside the context via context.setNeighbours() for the NavPointListState that will use
 * context.getNeighbours() to obtain them.
 * 
 * @author Jimmy
 */
@FSMState(map={
				@FSMTransition(
					state=NavPointListState.class, 
					symbol={NavPointNeighbourLinkEnd.class}, 
					transition={}
				)
			}
)
public class NavPointNeighboursState extends ObserverListState<NavPointNeighbourLink, TranslatorContext> {

	public NavPointNeighboursState() {
		super(NavPointNeighbourLinkStart.class, NavPointNeighbourLink.class, NavPointNeighbourLinkEnd.class);
	}
	
	@Override
	public void stateLeaving(TranslatorContext translatorContext,
			IFSMState<InfoMessage, TranslatorContext> toState, InfoMessage symbol) {
		translatorContext.setNeighbours(getList());
		newList();
	}

}