package cz.cuni.amis.pogamut.ut2004.agent.navigation;

import java.util.EventListener;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import cz.cuni.amis.pogamut.base.agent.navigation.IPathExecutorState;
import cz.cuni.amis.pogamut.base.agent.navigation.PathExecutorState;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.NavigationGraphBuilder;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.astar.UT2004AStar;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.floydwarshall.FloydWarshallMap;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BotDamaged;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BotKilled;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPointNeighbourLink;
import cz.cuni.amis.pogamut.ut2004.communication.worldview.map.UT2004Map;
import cz.cuni.amis.utils.NullCheck;
import cz.cuni.amis.utils.flag.FlagListener;
import cz.cuni.amis.utils.listener.Listeners;
import cz.cuni.amis.utils.maps.CountIntMap;

/**
 * Use with care! This class automatically watching for PATH-STUCKs removing bad-edges directly from navigation graph.
 * 
 * Works ONLY IF YOU'RE using {@link FloydWarshallMap} ... does not alter map inside UT2004. 
 * 
 * The class will use {@link UT2004PathExecutor} to sense {@link PathExecutorState#STUCK}s, if the bot stucks and 
 * {@link UT2004PathExecutor#getCurrentLink()} is available, this class will assume that this link is causing problems to
 * bot's navigation and will start to count failures when navigating through this link.
 * 
 * When number of failures for some link exceeds configured number (removeBadEdgeAfterNFailures, default is: {@link UT2004PathAutoFixer#REAMOVE_EDGE_AFTER_N_FAILURES_DEFAULT}),
 * it will {@link UT2004PathAutoFixer#removeLink(NavPointNeighbourLink)} remove it from bot's internal navigation graph.
 * 
 * Note that removal of some links requires recomputation of {@link FloydWarshallMap}, so you can pass some {@link FloydWarshallMap}
 * instance into it to perform auto {@link FloydWarshallMap#refreshPathMatrix()} upon link removal.
 * 
 * @author Jimmy
 */
public class UT2004PathAutoFixer {
	
	
	
	public static final int REAMOVE_EDGE_AFTER_N_FAILURES_DEFAULT = 2;

	private IUT2004PathExecutor<? extends ILocated> pathExecutor;
	private FloydWarshallMap fwMap;
	private UT2004AStar aStar;
	private NavigationGraphBuilder navBuilder;
	
	private CountIntMap<NavPointNeighbourLink> badLinks = new CountIntMap<NavPointNeighbourLink>();
	
	private Set<NavPointNeighbourLink> removedLinks = new HashSet<NavPointNeighbourLink>();
	
	private LogCategory log;

	private int removeBadEdgeAfterNFailures;

	private boolean botHarmed;

	private IWorldEventListener<BotDamaged> botDamagedListener = new IWorldEventListener<BotDamaged>() {

		@Override
		public void notify(BotDamaged event) {
			botDamaged();			
		}
		
	};
	
	private IWorldEventListener<BotKilled> botKilledListener = new IWorldEventListener<BotKilled>() {

		@Override
		public void notify(BotKilled event) {
			botKilled();			
		}
		
	};

	/**
	 * UT2004PathAutoFixer will remove edge whenever bot fails {@link UT2004PathAutoFixer#REAMOVE_EDGE_AFTER_N_FAILURES_DEFAULT} times to walk through it.
	 * 
	 * @param bot
	 * @param pathExecutor
	 * @param fwMap can be null (won't auto-call {@link FloydWarshallMap#refreshPathMatrix()} then)
	 * @param aStar can be null (won't auto-call {@link UT2004AStar#mapChanged()} then)
	 * @param navBuilder
	 */
	public UT2004PathAutoFixer(UT2004Bot bot, IUT2004PathExecutor<? extends ILocated> pathExecutor, FloydWarshallMap fwMap, UT2004AStar aStar, NavigationGraphBuilder navBuilder) {
		this(bot, pathExecutor, fwMap, aStar, navBuilder, REAMOVE_EDGE_AFTER_N_FAILURES_DEFAULT);
	}
		
	/**
	 * UT2004PathAutoFixer will remove edge whenever bot fails 'removeBadEdgeAfterNFailures' times to walk through it.
	 * 
	 * @param bot
	 * @param pathExecutor
	 * @param fwMap can be null (won't auto-call {@link FloydWarshallMap#refreshPathMatrix()} then)
	 * @param aStar can be null (won't auto-call {@link UT2004AStar#mapChanged()} then)
	 * @param navBuilder
	 */
	public UT2004PathAutoFixer(UT2004Bot bot, IUT2004PathExecutor<? extends ILocated> pathExecutor, FloydWarshallMap fwMap, UT2004AStar aStar, NavigationGraphBuilder navBuilder, int removeBadEdgeAfterNFailures) {
		if (removeBadEdgeAfterNFailures < 1) {
			throw new IllegalArgumentException("removeBadEdgeAfterNFailures == " + removeBadEdgeAfterNFailures + " < 1 cannot be!");
		}
		this.log = bot.getLogger().getCategory(UT2004PathAutoFixer.class.getSimpleName());
		this.pathExecutor = pathExecutor;		
		this.fwMap = fwMap;
		this.aStar = aStar;
		this.navBuilder = navBuilder;		
		
		NullCheck.check(this.pathExecutor, "pathExecutor");
		NullCheck.check(this.navBuilder,   "navBuilder");
		
		this.removeBadEdgeAfterNFailures = removeBadEdgeAfterNFailures;
		
		this.pathExecutor.getState().addListener(new FlagListener<IPathExecutorState>() {

			@Override
			public void flagChanged(IPathExecutorState changedValue) {
				switch (changedValue.getState()) {
				case PATH_COMPUTED:
					pathComputed();
					return;
				case SWITCHED_TO_ANOTHER_PATH_ELEMENT:
					switchedToNewElement();
					return;
				case STUCK:
					stuck();
					return;
					
				}
			}
			
		});
		
		bot.getWorldView().addEventListener(BotDamaged.class, botDamagedListener);
		bot.getWorldView().addEventListener(BotKilled.class, botKilledListener);
	}

	protected void botDamaged() {
		// bot has been damaged, it might have moved it from the correct course
		botHarmed = true;
	}

	protected void botKilled() {
		// bot has been damaged, it has moved it from the correct course
		botHarmed = true;
	}

	protected void switchedToNewElement() {
		// we're heading to new element ...
		// => assuming we've correctly arrived to previous path point, so further navigation is OK
		botHarmed = false;
	}
	
	protected void pathComputed() {
		// we're on the new path!
		botHarmed = false;
	}

	protected void stuck() {
		if (botHarmed) {
			// we have been hit by something, it might have caused navigation error
			// => ignore
			return;
		}
		
		NavPointNeighbourLink link = pathExecutor.getCurrentLink();
		if (link == null) return;
		
		badLinks.increase(link);	
		checkRemove(link);
	}
	
	/**
	 * Bot has stuck on 'link', {@link UT2004PathAutoFixer#badLinks} count has been increased, decide whether to remove the link from the graph.
	 * @param link
	 */
	protected void checkRemove(NavPointNeighbourLink link) {
		if (log != null && log.isLoggable(Level.WARNING)) log.warning("Bot has stuck (" + badLinks.get(link) + "x) on link " + link);
		if (badLinks.get(link) >= removeBadEdgeAfterNFailures) {
			listenerLink = link;
			listenersLinkCanRemove = true;
			linkRemovalListeners.notify(canRemoveLinkNotifier);
			if (listenersLinkCanRemove) {
				removeLink(link);
			} else {
				if (log != null && log.isLoggable(Level.WARNING)) log.warning("Some listener prevented link from removal: " + link);
			}
		}
	}
	
	/**
	 * Removes link from the graph + recompute fwMap path matrix.
	 * @param link
	 */
	protected void removeLink(NavPointNeighbourLink link) {
		String fromId = link.getFromNavPoint().getId().getStringId();
		String toId   = link.getToNavPoint().getId().getStringId();		
		if (log != null && log.isLoggable(Level.WARNING)) log.warning("REMOVING EDGE FROM NAV-GRAPTH (affects fwMap): " + fromId + " -> " + toId);
		navBuilder.removeEdge(fromId, toId);
		removedLinks.add(link);
		if (fwMap != null) fwMap.refreshPathMatrix();
		if (aStar != null) aStar.mapChanged();
		
		listenerLink = link;
		linkRemovalListeners.notify(linkRemovedNotifier);
	}

	/**
	 * Returns set with all removed links.
	 * @return
	 */
	public Set<NavPointNeighbourLink> getRemovedLinks() {
		return removedLinks;
	}

	/**
	 * Return counts of navigation failures for given links, i.e., 
	 * map where key == {@link NavPointNeighbourLink}, value == how many times bot failed to navigate through the link.
	 * 
	 * @return
	 */
	public CountIntMap<NavPointNeighbourLink> getBadLinks() {
		return badLinks;
	}
	
	//
	// LISTENERS
	//

	public static interface ILinkRemovalListener extends EventListener {
		
		/**
		 * Asks whether this link can be removed from navigation graph.
		 * @param link
		 * @return
		 */
		public boolean canRemoveLink(NavPointNeighbourLink link);
		
		/**
		 * Reports that some link has been removed from navigation graph.
		 * @param link
		 */
		public void linkRemoved(NavPointNeighbourLink link);
		
	}

	private Listeners<ILinkRemovalListener> linkRemovalListeners = new Listeners<ILinkRemovalListener>();
	
	public NavPointNeighbourLink listenerLink;
	
	public boolean listenersLinkCanRemove = true;
	
	private Listeners.ListenerNotifier<ILinkRemovalListener> canRemoveLinkNotifier = new Listeners.ListenerNotifier<ILinkRemovalListener>() {		
		
		@Override
		public NavPointNeighbourLink getEvent() {
			return listenerLink;
		}

		@Override
		public void notify(ILinkRemovalListener listener) {
			listenersLinkCanRemove = listenersLinkCanRemove && listener.canRemoveLink(listenerLink);
		}
		
	};
	
	private Listeners.ListenerNotifier<ILinkRemovalListener> linkRemovedNotifier = new Listeners.ListenerNotifier<ILinkRemovalListener>() {

		@Override
		public NavPointNeighbourLink getEvent() {
			return listenerLink;
		}

		@Override
		public void notify(ILinkRemovalListener listener) {
			listener.linkRemoved(listenerLink);
		}
		
	};
	
	public void addListener(ILinkRemovalListener listener) {
		linkRemovalListeners.addStrongListener(listener);
	}
	
	public void removeListener(ILinkRemovalListener listener) {
		linkRemovalListeners.removeListener(listener);
	}
	
	public boolean isListener(ILinkRemovalListener listener) {
		return linkRemovalListeners.isListening(listener);
	}
	
}
