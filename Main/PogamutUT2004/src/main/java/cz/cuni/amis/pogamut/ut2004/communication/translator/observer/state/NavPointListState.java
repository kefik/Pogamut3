package cz.cuni.amis.pogamut.ut2004.communication.translator.observer.state;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.cuni.amis.fsm.FSMState;
import cz.cuni.amis.fsm.FSMTransition;
import cz.cuni.amis.fsm.IFSMState;
import cz.cuni.amis.pogamut.base.communication.messages.InfoMessage;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPointListEnd;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPointListStart;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPointNeighbourLink;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPointNeighbourLinkEnd;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPointNeighbourLinkStart;
import cz.cuni.amis.pogamut.ut2004.communication.translator.TranslatorContext;
import cz.cuni.amis.pogamut.ut2004.communication.translator.TranslatorMessages;
import cz.cuni.amis.pogamut.ut2004.communication.translator.UnexpectedMessageException;
import cz.cuni.amis.pogamut.ut2004.communication.translator.observer.support.AbstractObserverFSMState;
import cz.cuni.amis.pogamut.ut2004.communication.translator.shared.events.MapPointListObtained;
import java.util.logging.Level;

/**
 * Takes care of the navpoint list. It stores them inside a List object and when END message comes it sends
 * them to the world view via <b>NavPointListObtained</b> event, note that before we send the nav points we're doing
 * the preprocessing of those navpoints to interconnect them with links (NavPointNeighbourLink) filling
 * respective fileds of incomingEdges and outgoingEdges.
 * <p><p>
 * The processing of navpoints and it's links is done via TranslatorContext.processNavPointLinks().
 * 
 * @author Jimmy
 */
@FSMState(map={@FSMTransition(
					state=ObserverRunningState.class, 
					symbol={NavPointListEnd.class}, 
					transition={}),
			   @FSMTransition(
					state = NavPointNeighboursState.class, 
					symbol = { NavPointNeighbourLinkStart.class }, 
					transition = { }
			   )	
		  }
)
public class NavPointListState extends AbstractObserverFSMState<InfoMessage, TranslatorContext> {
	
	private boolean begun = false;
	
	private Map<UnrealId, NavPoint> navPoints = new HashMap<UnrealId, NavPoint>();
	
	private Map<UnrealId, List<NavPointNeighbourLink>> neighbours = new HashMap<UnrealId, List<NavPointNeighbourLink>>();
	
	private NavPoint currentNavPoint = null;

	public NavPointListState() {
	}
	
	@Override
	public void stateEntering(TranslatorContext context, IFSMState<InfoMessage, TranslatorContext> fromState, InfoMessage symbol) {
		if (!begun) {
			if (!symbol.getClass().equals(NavPointListStart.class)) throw new UnexpectedMessageException(TranslatorMessages.unexpectedMessage(this, symbol, NavPointListStart.class), context.getLogger(), this);
			begun = true;
			return;
		}		
		if (!symbol.getClass().equals(NavPointNeighbourLinkEnd.class)) throw new UnexpectedMessageException(TranslatorMessages.unexpectedMessage(this, symbol, NavPointNeighbourLinkEnd.class), context.getLogger(), this);
		navPoints.put(currentNavPoint.getId(), currentNavPoint);
		neighbours.put(currentNavPoint.getId(), context.getNeighbours());
	}
	
	@Override
	public void stateLeaving(TranslatorContext context,
			IFSMState<InfoMessage, TranslatorContext> toState, InfoMessage symbol) {
		if (symbol.getClass().equals(NavPointNeighbourLinkStart.class)) return;	
		if (!symbol.getClass().equals(NavPointListEnd.class)) throw new UnexpectedMessageException(TranslatorMessages.unexpectedMessage(this, symbol, NavPointListEnd.class), context.getLogger(), this);
		
		long simTime = 
			(symbol instanceof IWorldChangeEvent ? ((IWorldChangeEvent)symbol).getSimTime() : 0);
		
		// we've received all the navpoints we could!		
		context.setNavPoints(navPoints);
		context.setNavPointLinks(neighbours);
		context.processNavPointLinks();

		if (context.getItems() != null && context.getItems().size() > 0) {
			context.processNavPointsAndItems();
			if (context.getLogger().isLoggable(Level.FINE)) context.getLogger().fine("Pushing NavPoint events.");
			context.getEventQueue().pushEvent(context.getNavPoints().values().toArray(new IWorldChangeEvent[0]));
			if (context.getLogger().isLoggable(Level.FINE)) context.getLogger().fine("Pushing Item events.");
			context.getEventQueue().pushEvent(context.getItems().values().toArray(new IWorldChangeEvent[0]));
			context.getEventQueue().pushEvent(new MapPointListObtained(context.getNavPoints(), context.getItems(), simTime));
		} else {
			if (context.getLogger().isLoggable(Level.FINE)) context.getLogger().fine("Pushing NavPoint events.");
			context.getEventQueue().pushEvent(context.getNavPoints().values().toArray(new IWorldChangeEvent[0]));
		}
		
		// leaving the state, mark we haven't ever begun
		navPoints = new HashMap<UnrealId, NavPoint>(navPoints.size() + 20);
		neighbours = new HashMap<UnrealId, List<NavPointNeighbourLink>>(neighbours.size() + 20);
		begun = false;
	}

	@Override
	protected void innerStateSymbol(TranslatorContext context, InfoMessage symbol) {
		if (!symbol.getClass().equals(NavPoint.class)) {
			if (!NavPoint.class.isAssignableFrom(symbol.getClass())) {
				throw new UnexpectedMessageException(TranslatorMessages.unexpectedMessage(this, symbol, NavPoint.class), context.getLogger(), this);
			}
		}
		currentNavPoint = (NavPoint)symbol;	
	}

	@Override
	public void init(TranslatorContext context) {
	}

	@Override
	public void restart(TranslatorContext context) {
		currentNavPoint = null;
		begun = false;
		navPoints = new HashMap<UnrealId, NavPoint>(navPoints.size() + 20);
		neighbours = new HashMap<UnrealId, List<NavPointNeighbourLink>>(neighbours.size() + 20);
	}
	
}