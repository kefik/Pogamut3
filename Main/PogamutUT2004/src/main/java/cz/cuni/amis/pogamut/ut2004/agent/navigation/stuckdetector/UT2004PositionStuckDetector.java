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
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Self;

public class UT2004PositionStuckDetector implements IStuckDetector {

	private static double DEFAULT_MIN_DIAMETER = 20.0;
	
	private static double DEFAULT_MIN_Z = 40.0;
	
	private static int DEFAULT_HISTORY_LENGTH = 8;
	
	private UT2004Bot bot;
	
	private double minMovementDiameter;
	
	private int historyLength;
	
	private List<Location> locationHistory;
	
	private boolean stuck = false;
	
	private boolean botWaiting = false;
	
	private boolean enabled = false;
	
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
	
	/**
	 * Listener watching for the {@link EndMessage} message. Recalls
	 * {@link #eventEndMessage(EndMessage)}.
	 * 
	 * @author Jimmy
	 */
	private class EndMessageListener implements IWorldEventListener<EndMessage> {

		public EndMessageListener(IWorldView worldView) {
			worldView.addEventListener(EndMessage.class, this);
		}

		@Override
		public void notify(EndMessage event) {
			eventEndMessage(event);
		}

	};
	
	private SelfListener selfListener;
	
	private EndMessageListener endMessageListener;
	
	private Logger log;

	private double minMovementZ;
	
	public UT2004PositionStuckDetector(UT2004Bot bot) {
		this(bot, DEFAULT_HISTORY_LENGTH, DEFAULT_MIN_DIAMETER, DEFAULT_MIN_Z);
	}
	
	public UT2004PositionStuckDetector(UT2004Bot bot, int historyLength, double minMovementDiameter, double minMovementZ) {
		if (this.log == null) {
			this.log = bot.getLogger().getCategory(this.getClass().getSimpleName());
		}
		this.bot = bot;		
		selfListener = new SelfListener(bot.getWorldView());
		endMessageListener = new EndMessageListener(bot.getWorldView());
		
		this.historyLength = historyLength;
		
		if (this.historyLength < 0) throw new IllegalArgumentException("historyLength can't be < 0");
		
		this.minMovementDiameter = minMovementDiameter;
		this.minMovementZ = minMovementZ;
		
		this.locationHistory = new ArrayList<Location>(this.historyLength);		
	}
	
	@Override
	public void setEnabled(boolean state) {
		if (this.enabled == state) return;
		this.enabled = state;		
	}
		
	@Override
	public void setBotWaiting(boolean state) {
		this.botWaiting = state;
	}
	
	@Override
	public void setBotTarget(ILocated target) {
		// WE DO NOT CARE
	}
	
	public void eventSelf(IWorldObjectEvent<Self> event) {
		if (!enabled) return;
		if (botWaiting) return; // we're just waiting...
		
		locationHistory.add(event.getObject().getLocation());
		while (locationHistory.size() > historyLength) {
			locationHistory.remove(0);
		}
	}
	
	public void eventEndMessage(EndMessage event) {
		if (!enabled) return;
		if (botWaiting) return; // we're just waiting...
		
		if (locationHistory.size() == historyLength) {
			double maxDistance = Double.NEGATIVE_INFINITY;
			double maxHeight = Double.NEGATIVE_INFINITY;
			for (Location loc1 : locationHistory){
				for (Location loc2: locationHistory) {
					if (maxDistance < loc1.getDistance2D(loc2)) {
						maxDistance = loc1.getDistance2D(loc2);
					}
					if (Math.abs(loc1.z - loc2.z) > maxHeight) {
						maxHeight = Math.abs(loc1.z - loc2.z);
					}
				}
			}
			if (maxDistance < this.minMovementDiameter && maxHeight < this.minMovementZ) {
				stuckDetails = "Bot stuck detected. Distance: " + maxDistance + " < " + this.minMovementDiameter + " && Height: " + maxHeight + " < " + minMovementZ;
				if (log != null && log.isLoggable(Level.WARNING)) log.warning(stuckDetails);
				stuck = true;
			} else {
				stuck = false;
			}
		}
	}

	
	@Override
	public boolean isStuck() {
		return stuck;
	}

	@Override
	public void reset() {
		if (log != null && log.isLoggable(Level.FINER)) log.finer("Reset.");
		this.locationHistory.clear();
		stuck = false;
	}
	
	@Override
	public String getStuckDetails() {
		return stuckDetails;
	}

}
