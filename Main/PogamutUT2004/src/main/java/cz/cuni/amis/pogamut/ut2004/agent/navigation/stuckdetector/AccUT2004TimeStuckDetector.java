/*
 * Copyright (C) 2014 AMIS research group, Faculty of Mathematics and Physics, Charles University in Prague, Czech Republic
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cz.cuni.amis.pogamut.ut2004.agent.navigation.stuckdetector;

import cz.cuni.amis.pogamut.base.agent.navigation.IStuckDetector;
import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.EndMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Self;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Simple {@link IStuckDetector} that watches whether the bot moves at all.
 * <p>
 * <p>
 * You may define a period of time (timeout) in which the bot should move on,
 * i.e., if the bot won't move a bit in a specified amount of time, it will
 * report a stuck.
 *
 * @author Jimmy, knight, Bogo
 */
public class AccUT2004TimeStuckDetector implements IStuckDetector {

    /**
     * Default distance that is regarded as "bot did not move a bit".
     */
    private static final double NO_MOVEMENT_SIZE = 10;

    /**
     * Default timeout after which the detector reports stuck if the bot did not
     * move. In miliseconds!
     */
    private static int DEFAULT_TIMEOUT = 3000;

    /**
     * Default timeout when the bot is waiting for something... In milliseconds!
     */
    private static int DEFAULT_WAITING_TIMEOUT = 10000;

    /**
     * Owner of the detector.
     */
    private UT2004Bot bot;

    /**
     * Timeout used by the detector, when the bot is not waiting, initialized in
     * the constructor.
     */
    private double timeout;

    /**
     * Timeout used by the detector when the bot is waiting, initialized in the
     * constructor.
     */
    private double waitingTimeout;

    private boolean botWaiting = false;

    /**
     * In reset set to false. In isStuck set to true - indicator if this
     * detector was already queried after reset about bot stucking.
     */
    private boolean bWasIsStuckCalled = false;

    /**
     * Here we store current time of the simulation.
     */
    private long currentTime;

    private String stuckDetails;
    
    /**
     * Listener watching for the {@link Self} message. Recalls
     * {@link UT2004TimeStuckDetector#eventSelf(IWorldObjectEvent)}.
     *
     * @author Jimmy
     */
    private class SelfListener implements IWorldObjectListener<Self> {

        public SelfListener(IWorldView worldView) {
            worldView.addObjectListener(Self.class, WorldObjectUpdatedEvent.class, this);
        }

        @Override
        public void notify(IWorldObjectEvent<Self> event) {
            eventSelf(event);
        }

    };

    /**
     * Listener watching for the {@link EndMessage} message. Recalls
     * {@link UT2004TimeStuckDetector#eventEndMessage(EndMessage)}.
     *
     * @author Jimmy
     */
    private class EndListener implements IWorldEventListener<EndMessage> {

        public EndListener(IWorldView worldView) {
            worldView.addEventListener(EndMessage.class, this);
        }

        @Override
        public void notify(EndMessage event) {
            eventEndMessage(event);
        }

    };

    /**
     * Listener that triggers
     * {@link UT2004TimeStuckDetector#eventSelf(IWorldObjectEvent)}.
     */
    private SelfListener selfListener;

    /**
     * Listener that triggers
     * {@link UT2004TimeStuckDetector#eventEndMessage(EndMessage)}.
     */
    private EndListener endListener;

    /**
     * Last time when the bot has moved (its velocity had been greater than
     * {@link UT2004TimeStuckDetector#NO_MOVEMENT_SIZE}.
     */
    private Double lastMovementTime = null;

    /**
     * Whether we should report that the bot has stuck.
     */
    private boolean stuck = false;

    private boolean enabled;

    private Logger log;

    public AccUT2004TimeStuckDetector(UT2004Bot bot) {
        this(bot, DEFAULT_TIMEOUT, DEFAULT_WAITING_TIMEOUT);
    }

    public AccUT2004TimeStuckDetector(UT2004Bot bot, double timeoutMillis,
            double waitingTimeoutMillis) {
        if (this.log == null) {
            this.log = bot.getLogger().getCategory(
                    this.getClass().getSimpleName());
        }
        this.bot = bot;
        this.timeout = timeoutMillis;
        this.waitingTimeout = waitingTimeoutMillis;
        selfListener = new SelfListener(bot.getWorldView());
        endListener = new EndListener(bot.getWorldView());
    }

    public void eventSelf(IWorldObjectEvent<Self> event) {
        if (!enabled) {
            return;
        }

        // we always update current time
        long simTime = event.getObject().getSimTime();
        if(simTime == 0) {
            return;
        }
        currentTime = simTime;

        // if we were not yet querried about stucking, we will simply do nothing!
        if (!bWasIsStuckCalled) {
            return;
        }

        // check whether we are moving at least a bit...
        if (event.getObject().getVelocity().size() > NO_MOVEMENT_SIZE || lastMovementTime == null) {
            lastMovementTime = (double) event.getObject().getSimTime();
        } else {
            log.info("Bot not moving. Velocity: " + event.getObject().getVelocity().size());
        }

    }

    public void eventEndMessage(EndMessage event) {
        if (!enabled || lastMovementTime == null) {
            return;
        }

        // we always update current time
        currentTime = event.getSimTime();

        if (!bWasIsStuckCalled) {
            return;
        }
        if (botWaiting) {
            if (currentTime - lastMovementTime >= waitingTimeout) {
                stuck = true;
                stuckDetails = "Bot is WAITING for more than " + waitingTimeout + " ms, considering that it has stuck.";
                if (log != null && log.isLoggable(Level.WARNING)) {
                    log.warning(stuckDetails);
                }
            }
        } else {
            if (currentTime - lastMovementTime >= timeout) {
                stuck = true;
                stuckDetails = "Bot should be moving but it is standing still for more than " + timeout + " ms, considering that it has stuck.";
                if (log != null && log.isLoggable(Level.WARNING)) {                	
                    log.warning(stuckDetails);
                }
            }
        }
    }

    @Override
    public void setEnabled(boolean state) {
        if (this.enabled == state) {
            return;
        }
        this.enabled = state;
    }

    @Override
    public void setBotWaiting(boolean state) {
        botWaiting = state;
        lastMovementTime = null;
    }

    @Override
    public boolean isStuck() {
        if (!bWasIsStuckCalled) {
			// isStuck called for the first time after reset - we return false
            // and we reset lastMovementTime
            lastMovementTime = (double) currentTime;
            bWasIsStuckCalled = true;
            return false;
        }

        return stuck;
    }

    @Override
    public void reset() {
        if (log != null && log.isLoggable(Level.FINER)) {
            log.finer("Reset.");
        }
        lastMovementTime = Double.NEGATIVE_INFINITY;
        bWasIsStuckCalled = false;
        stuck = false;
    }

    @Override
    public void setBotTarget(ILocated target) {
        // WE DO NOT CARE
    }
    
    @Override
    public String getStuckDetails() {
		return stuckDetails;
	}

}
