package cz.cuni.amis.pogamut.ut2004.agent.navigation;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.agent.navigation.IStuckDetector;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.AgentInfo;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.Senses;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.loquenavigator.KefikRunner;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.stuckdetector.UT2004DistanceStuckDetector;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.stuckdetector.UT2004PositionStuckDetector;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.stuckdetector.UT2004TimeStuckDetector;
import cz.cuni.amis.pogamut.ut2004.bot.command.AdvancedLocomotion;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Stop;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.EndMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;

/**
 * Run straight class is meant to provide "straight running" combined with "stuck detection".
 * 
 * Automatically uses {@link UT2004TimeStuckDetector}, {@link UT2004PositionStuckDetector} and {@link UT2004DistanceStuckDetector}.
 * 
 * @author Jimmy
 */
public class UT2004RunStraight implements IUT2004RunStraight {

	public static final int CLOSE_ENOUGH = 50;

	public static final double AT_PLAYER = 100;
	
	public static final double AT_NAVPOINT = 50;

	public static final double MAX_ANGLE = 45 * Math.PI / 180;
	
	protected UT2004Bot bot;
	
	protected AgentInfo info;
	
	protected IUT2004PathRunner runner;
	
	protected boolean executing;
	
	protected LogCategory log;

	protected IWorldEventListener<EndMessage> endListener = new IWorldEventListener<EndMessage>() {
		
		@Override
		public void notify(EndMessage event) {
			runStraight();
		}
		
	};
	
	protected List<IStuckDetector> stuckDetectors = new ArrayList<IStuckDetector>();

	public UT2004RunStraight(UT2004Bot bot, AgentInfo info, AdvancedLocomotion move) {
		this.log = bot.getLogger().getCategory(this.getClass().getSimpleName());
		this.bot = bot;
		this.info = info;		
		this.runner = new KefikRunner(bot, info, move, log);
		
		stuckDetectors.add(new UT2004TimeStuckDetector(bot, 3000, 10000));
		stuckDetectors.add(new UT2004PositionStuckDetector(bot));
		stuckDetectors.add(new UT2004DistanceStuckDetector(bot));
		
		bot.getWorldView().addEventListener(EndMessage.class, endListener);
	}
	
	@Override
	public Logger getLog() {
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
	public boolean isExecuting() {
		return executing;
	}
	
	@Override
	public boolean isSuccess() {
		return success;
	}
	
	@Override
	public boolean isFailed() {
		return failed;
	}
	
	@Override
	public ILocated getLastTarget() {
		return lastTarget;
	}
	
	@Override
	public ILocated getCurrentTarget() {
		return currentTarget;
	}
	
	@Override
	public void setFocus(ILocated focus) {
		this.focus = focus;
	}
	
	@Override
	public void runStraight(ILocated target) {
		if (executing) {
			if (currentTarget != null && currentTarget.getLocation().equals(target.getLocation())) {
				// same target
				return;
			}
			// different target!
		}
		
		if (log != null && log.isLoggable(Level.INFO)) log.info("Run straight to: " + target);
		
		reset();
				
		initialLocation = info.getLocation();
		currentTarget = target;
		
		for (IStuckDetector stuckDetector : stuckDetectors) {
			stuckDetector.reset();
			stuckDetector.setEnabled(true);
			stuckDetector.setBotTarget(target);
		}
			
		executing = true;
		
		runStraight();
	}
	
	@Override
	public void stop(boolean stopMovement) {		
		if (!executing) return;
		if (log != null && log.isLoggable(Level.INFO)) log.info("STOPPED");
		
		reset();
		if (stopMovement) {
			bot.getAct().act(new Stop());
		}
		
		for (IStuckDetector stuckDetector : stuckDetectors) {
			stuckDetector.setEnabled(false);
		}
	}
	
	//
	// VARIABLES
	//
		
	protected Location initialLocation;
	
	protected ILocated currentTarget;

	protected boolean success;

	protected boolean failed;

	protected ILocated lastTarget;

	protected ILocated focus;
	
	//
	// RESET
	// 
	
	protected void reset() {
		if (log != null && log.isLoggable(Level.FINER)) log.finer("Reset");
		
		if (currentTarget != null) {
			lastTarget = currentTarget;
		}
				
		initialLocation = null;
		currentTarget = null;
		
		success = false;
		failed = false;
		executing = false;
		
		runner.reset();
	}
	
	//
	// EXECUTION
	//

	protected void runStraight() {
		if (!executing) return;
		
		for (IStuckDetector stuckDetector : stuckDetectors) {
			if (stuckDetector.isStuck()) {
				stuck();
				return;
			}
		}
		
		double distance = bot.getLocation().getDistance(currentTarget.getLocation());
		if (currentTarget instanceof Player) {
			if (distance < AT_PLAYER) {
				success();
				return;
			}
		} else
		if (distance < AT_NAVPOINT) {
			success();
			return;
		}
		
		double hDistance = bot.getLocation().getDistance2D(currentTarget.getLocation());
		double vDistance = bot.getLocation().getDistanceZ(currentTarget.getLocation());
		
		double angle = Math.atan(Math.abs(vDistance) / hDistance);
		
		if (!runner.runToLocation(initialLocation, currentTarget.getLocation(), null, focus == null ? currentTarget : focus, null, angle < MAX_ANGLE, false)) {
			stuck();
			return;
		}
		
	}

	protected void success() {
		if (log != null && log.isLoggable(Level.FINE)) log.fine("Target reached.");
		stop(false);
		success = true;		
	}

	protected void stuck() {
		if (log != null && log.isLoggable(Level.INFO)) log.info("Running failed.");
		stop(true);
		failed = true;
	}

}
