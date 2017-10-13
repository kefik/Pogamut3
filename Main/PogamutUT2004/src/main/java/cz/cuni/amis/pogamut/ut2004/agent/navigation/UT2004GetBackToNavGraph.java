package cz.cuni.amis.pogamut.ut2004.agent.navigation;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import cz.cuni.amis.pogamut.base.agent.navigation.IStuckDetector;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.pogamut.base.utils.math.DistanceUtils;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.base3d.worldview.object.Rotation;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.AgentInfo;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.Senses;
import cz.cuni.amis.pogamut.ut2004.agent.module.utils.TabooSet;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.IUT2004GetBackToNavGraph;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.IUT2004PathRunner;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.UT2004RunStraight;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.loquenavigator.KefikRunner;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.stuckdetector.UT2004DistanceStuckDetector;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.stuckdetector.UT2004PositionStuckDetector;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.stuckdetector.UT2004TimeStuckDetector;
import cz.cuni.amis.pogamut.ut2004.bot.command.AdvancedLocomotion;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Move;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.EndMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;

/**
 * This class is meant to provide easy "get-back-to-navigation-graph-in-order-I-can-safely-navigate-through-map"
 * implementation. 
 * 
 * Automatically uses {@link UT2004TimeStuckDetector}, {@link UT2004PositionStuckDetector} and {@link UT2004DistanceStuckDetector}.
 * 
 * @author Jimmy
 */
public class UT2004GetBackToNavGraph implements IUT2004GetBackToNavGraph {

	public static final int CLOSE_ENOUGH = 50;

	public static final double MAX_ANGLE = UT2004RunStraight.MAX_ANGLE;
	
	protected UT2004Bot bot;
	
	protected AgentInfo info;
	
	protected IUT2004PathRunner runner;
	
	protected boolean executing;
	
	protected LogCategory log;
	
	protected int randomMoveDirection;

	protected IWorldEventListener<EndMessage> endListener = new IWorldEventListener<EndMessage>() {
		
		@Override
		public void notify(EndMessage event) {
			getBackOnNavGraph();
		}
		
	};
	
	protected List<IStuckDetector> stuckDetectors = new ArrayList<IStuckDetector>();

	protected ILocated focus;
	
	public UT2004GetBackToNavGraph(UT2004Bot bot, AgentInfo info, AdvancedLocomotion move) {
		this.log = bot.getLogger().getCategory(this.getClass().getSimpleName());
		this.bot = bot;
		this.info = info;		
		this.runner = new KefikRunner(bot, info, move, log);
		
		stuckDetectors.add(new UT2004TimeStuckDetector(bot, 3000, 5000));
		stuckDetectors.add(new UT2004PositionStuckDetector(bot));
		stuckDetectors.add(new UT2004DistanceStuckDetector(bot));
		
		bot.getWorldView().addEventListener(EndMessage.class, endListener);
	}
	
	@Override
	public LogCategory getLog() {
		return log;
	}
	
	@Override
	public void addStuckDetector(IStuckDetector stuckDetector) {
		stuckDetectors.add(stuckDetector);
	}
	
	@Override
	public void removeStuckDetector(IStuckDetector stuckDetector) {
		stuckDetectors.remove(stuckDetector);
	}
	
	@Override
	public void clearStuckDetectors() {
		stuckDetectors.clear();
	}
		
	@Override
	public NavPoint getNearestNavPoint() {
		return info.getNearestNavPoint();
	}
	
	@Override
	public boolean isOnNavGraph() {
		return info.isOnNavGraph();
	}
	
	@Override
	public boolean isExecuting() {
		return executing;
	}
	
	@Override
	public void setFocus(ILocated located) {
		this.focus = located;
	}
	
	@Override
	public void backToNavGraph() {
		if (executing) return;
		
		if (log != null && log.isLoggable(Level.INFO)) log.info("STARTED");
		
		reset();
		
		initialLocation = info.getLocation();
		
		for (IStuckDetector stuckDetector : stuckDetectors) {			
			stuckDetector.reset();
			stuckDetector.setEnabled(true);
		}
			
		executing = true;
	}
	
	@Override
	public void stop() {
		if (!executing) return;
		if (log != null && log.isLoggable(Level.INFO)) log.info("STOPPED");
				
		executing = false;
		reset();
		
		for (IStuckDetector stuckDetector : stuckDetectors) {
			stuckDetector.setEnabled(false);
		}
	}
	
	//
	// VARIABLES
	//
	
	protected TabooSet<NavPoint> tried;

	protected NavPoint tryingNav;
	
	protected Location initialLocation;
	
	//
	// RESET
	// 
	
	protected void reset() {
		if (log != null && log.isLoggable(Level.FINER)) log.finer("Reset");
		
		if (tried == null) {
			tried = new TabooSet<NavPoint>(bot);
		} else {
			tried.clear();
		}
		
		tryingNav = null;		
	}
	
	//
	// EXECUTION
	//

	protected void getBackOnNavGraph() {
		if (!executing) return;
	
		if (isOnNavGraph()) {
			// WE'VE MANAGED TO GET BACK ON NAVIGATION GRAPH
			if (log != null && log.isLoggable(Level.INFO)) log.info("Got back to Navigation Graph.");
			stop();
			return;
		}
		
		while (true) {
			if (runToNavPoint()) {
				// WE'RE ON THE WAY...
				return;
			}
			// RUNNING FAILED
			// => select new navpoint
			while (tryingNav == null) {
				tryingNav = DistanceUtils.getNearest(tried.filter(bot.getWorldView().getAll(NavPoint.class).values()), info.getLocation());
			
				// CHECK SUITABILITY
				if (tryingNav == null || info.getLocation().getDistance(tryingNav.getLocation()) > 2000) {
					// FAILURE, NO MORE SUITABLE NAV POINTS TO TRY
					// => clear taboo set and start retrying...
					if (tried.size() == 0) {
						// TOTAL FAILURE!!! No suitable navpoints :-( lest try to move the bot a bit.
						runSomewhere();
						//stop();
						return;
					}
					
					tried.clear();
					tryingNav = null;	
					
					continue;
				}
			}
			
			if (log != null && log.isLoggable(Level.FINE)) log.fine("Trying to get to: " + tryingNav);
			
			// NAV POINT CHOSEN
			initialLocation = info.getLocation();
			for (IStuckDetector stuckDetector : stuckDetectors) {
				stuckDetector.reset();
				stuckDetector.setBotTarget(tryingNav);
			}
			runner.reset();
			
			// CONTINUE WITH NEXT CYCLE THAT WILL TRY TO RUN TO tryingNav
		}
	}
	
	/**
	 * Last resort - keep trying random points 200 UT units from bot location - 90 degrees left, right and backwards. :-)
	 */
	protected void runSomewhere() {
		randomMoveDirection++;
		if (randomMoveDirection >= 2)
			randomMoveDirection = -1;
					
		Location backwardsLoc = bot.getLocation();
		
		Rotation rot = bot.getRotation();
		rot.setYaw(rot.getYaw() + randomMoveDirection * 16000);
		backwardsLoc = backwardsLoc.sub(rot.toLocation().getNormalized().scale(200));					
		
		double hDistance = bot.getLocation().getDistance2D(backwardsLoc);
		double vDistance = bot.getLocation().getDistanceZ(backwardsLoc);
		
		double angle = Math.atan(Math.abs(vDistance) / hDistance);		
		runner.runToLocation(initialLocation, backwardsLoc, null, focus == null ? backwardsLoc : focus, null, angle < MAX_ANGLE, false);				
	}

	protected boolean runToNavPoint() {
		if (tryingNav == null) return false;
		// WE'RE TRYING TO RUN TO SOME NAVPOINT
		
		if (log != null && log.isLoggable(Level.FINE)) log.fine("Running to: " + tryingNav);
		
		double hDistance = bot.getLocation().getDistance2D(tryingNav.getLocation());
		double vDistance = bot.getLocation().getDistanceZ(tryingNav.getLocation());
		
		double angle = Math.atan(Math.abs(vDistance) / hDistance);
		
		if (runner.runToLocation(initialLocation, tryingNav.getLocation(), null, focus == null ? tryingNav.getLocation() : focus, null, angle < MAX_ANGLE, false)) {
			// RUNNING SEEMS OK
			// => check stuck detectors
			for (IStuckDetector stuckDetector : stuckDetectors) {
				if (stuckDetector.isStuck()) {
					// WE'RE STUCK!
					runFailed();
					return false;
				}
			}
			
			return true;
		} else {
			// RUNNER FAILED...
			runFailed();			
			return false;
		}
	}

	protected void runFailed() {
		// RUNNING FAILED 
		// => ban nav point
		tried.add(tryingNav);
		tryingNav = null;
	}

}
