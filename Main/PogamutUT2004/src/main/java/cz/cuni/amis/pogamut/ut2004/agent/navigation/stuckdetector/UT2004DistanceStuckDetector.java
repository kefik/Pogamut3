package cz.cuni.amis.pogamut.ut2004.agent.navigation.stuckdetector;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.agent.navigation.IStuckDetector;
import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectListener;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.EndMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Self;

public class UT2004DistanceStuckDetector implements IStuckDetector {

	private UT2004Bot bot;
	
	private Location botTarget;
	
	private boolean botWaiting = false;
	
	private int historyLength;
	
	private List<Location> positionHistory;
	
	private List<Double> distanceHistory;
	
	private Boolean closing;
	
	private Boolean distancing;
	
	private int closingCount = 0;
	
	private int distancingCount = 0;
	
	private boolean stuck = false;
	
	private boolean enabled = false;
	
	private int totalClosingCountMoreThanOne = 0;
	private int totalDistancingCountMoreThanOne = 0;
		
	private String stuckDetails; 
	
	private class SelfListener implements IWorldObjectListener<Self> {

		public SelfListener(IWorldView worldView) {
			worldView.addObjectListener(Self.class, this);
		}
		
		@Override
		public void notify(IWorldObjectEvent<Self> event) {
			eventSelf(event);
		}
		
	};
	
	private SelfListener selfListener;
	
	private Logger log;

	private double minMovementZ;

	public UT2004DistanceStuckDetector(UT2004Bot bot) {
		if (this.log == null) {
			this.log = bot.getLogger().getCategory(this.getClass().getSimpleName());
		}
		this.bot = bot;		
		selfListener = new SelfListener(bot.getWorldView());
		
		this.historyLength = 2;
		
		if (this.historyLength < 0) throw new IllegalArgumentException("historyLength can't be < 0");
		
		this.distanceHistory = new ArrayList<Double>(this.historyLength);
		this.positionHistory = new ArrayList<Location>(this.historyLength);
	}
	
	@Override
	public void setEnabled(boolean state) {
		if (this.enabled == state) return;
		this.enabled = state;		
	}
		
	@Override
	public void setBotWaiting(boolean state) {
		this.botWaiting = state;
		if (this.botWaiting) {
			Location botTarget = this.botTarget;
			reset();
			this.botTarget = botTarget;
		}
	}
	
	@Override
	public void setBotTarget(ILocated target) {
		if (this.botTarget != null) {
			if (log != null && log.isLoggable(Level.FINER)) log.finer("TARGET APPROACHING STUCK DETECTOR - previous stats:");
			if (log != null && log.isLoggable(Level.FINER)) log.finer("  Closing count:              " + closingCount);
			if (log != null && log.isLoggable(Level.FINER)) log.finer("  Distancing count:           " + distancingCount);
			
			if (closingCount > 1) ++totalClosingCountMoreThanOne;
			if (distancingCount > 1) ++totalDistancingCountMoreThanOne;
			
			if (log != null && log.isLoggable(Level.FINER)) log.finer("  Total closing count > 1:    " + totalClosingCountMoreThanOne);
			if (log != null && log.isLoggable(Level.FINER)) log.finer("  Total distancing count > 1: " + totalDistancingCountMoreThanOne);
		}
		
		boolean botWaiting = this.botWaiting;
		reset();
		this.botWaiting = botWaiting;
		
		if (target == null || target instanceof Player) {
			this.botTarget = null;
		} else {
			this.botTarget = target.getLocation();
		}
		if (this.botTarget == null) {
			stuck = false;
		}
	}
	
	public void eventSelf(IWorldObjectEvent<Self> event) {
		if (!enabled) return;   // not enabled
		if (botWaiting) return; // we're just waiting...
		
		if (botTarget == null) return; // nothing to watch over
		
		Location currentLocation = event.getObject().getLocation();
		double currentDistance = currentLocation.getDistance(botTarget);
		
		positionHistory.add(currentLocation);
		distanceHistory.add(currentDistance);
		
		if (positionHistory.size() == 1) return; // not enought info yet
		
		Location previousLocation = positionHistory.get(positionHistory.size()-2);
		double previousDistance = distanceHistory.get(distanceHistory.size()-2);
		
		boolean currentClosing = currentDistance < previousDistance;
		boolean currentDistancing = !currentClosing;
		
		if (closing == null) {
			// positionHistory.size() == 2
			closing = currentClosing;
			distancing = currentDistancing;
			closingCount = closing ? 1 : 0;
			distancingCount = distancing ? 1 : 0;
			return;
		}
		
		// WE HAVE ALL SET UP TO CHECK TARGET-APPROACHING-STUCK
		
		boolean previousClosing = closing;
		boolean previousDistancing = distancing;
		
		this.closing = currentClosing;
		this.distancing = currentDistancing;
		
		if (currentClosing) {
			// WE'RE CURRENTLY CLOSING TO TARGET
			if (previousClosing) {
				// AND WE'VE BEEN CLOSING PREVIOUSLY AS WELL
				// => all proceeds ok so far...				
			} else {
				++closingCount;
			}			
		} else {
			// WE'RE DISTANCING
			if (previousDistancing) {
				// AND WE'VE BEEN DISTANCING PREVIOUSLY AS WELL
				// ... hmmmmm
			} else {
				++distancingCount;
				if (distancingCount > 1) {
					stuckDetails = "Bot stuck detected, #closing " + closingCount + ", #distancing " + distancingCount;
					if (log != null && log.isLoggable(Level.WARNING)) log.warning(stuckDetails); 
					stuck = true;
				}
			}
		}
		
		if (log != null && log.isLoggable(Level.FINER)) log.finer("TARGET APPROACHING STUCK DETECTOR");
		if (log != null && log.isLoggable(Level.FINER)) log.finer("  Current distance:    " + currentDistance);
		if (log != null && log.isLoggable(Level.FINER)) log.finer("  Current closing:     " + currentClosing);
		if (log != null && log.isLoggable(Level.FINER)) log.finer("  Current distancing:  " + currentDistancing);
		if (log != null && log.isLoggable(Level.FINER)) log.finer("  Previous closing:    " + previousClosing);
		if (log != null && log.isLoggable(Level.FINER)) log.finer("  Previous distancing: " + previousDistancing);
		if (log != null && log.isLoggable(Level.FINER)) log.finer("  Closing count:       " + closingCount);
		if (log != null && log.isLoggable(Level.FINER)) log.finer("  Distancing count:    " + distancingCount);
	}
	
	@Override
	public boolean isStuck() {
		return stuck;
	}

	@Override
	public void reset() {
		if (log != null && log.isLoggable(Level.FINER)) log.finer("Reset.");
		this.distanceHistory.clear();
		this.positionHistory.clear();
		closing = null;
		distancing = null;
		closingCount = 0;
		distancingCount = 0;
		stuck = false;
		botTarget = null;
		botWaiting = false;
	}

	@Override
	public String getStuckDetails() {
		return stuckDetails;
	}

}
