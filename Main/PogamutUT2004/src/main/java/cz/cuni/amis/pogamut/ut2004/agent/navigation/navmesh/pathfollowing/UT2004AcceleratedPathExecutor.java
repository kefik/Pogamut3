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
package cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.pathfollowing;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.agent.navigation.IPathExecutionEstimator;
import cz.cuni.amis.pogamut.base.agent.navigation.IPathExecutorState;
import cz.cuni.amis.pogamut.base.agent.navigation.IStuckDetector;
import cz.cuni.amis.pogamut.base.agent.navigation.PathExecutorState;
import cz.cuni.amis.pogamut.base.agent.navigation.impl.BasePathExecutor;
import cz.cuni.amis.pogamut.base.agent.navigation.impl.PrecomputedPathFuture;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.base.utils.Pogamut;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.AgentInfo;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.IUT2004PathExecutor;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.IUT2004PathExecutorHelper;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.IUT2004PathNavigator;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.UT2004PathExecutor;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.UT2004PathExecutorStuckState;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.loquenavigator.LoqueNavigator;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.timeoutestimator.UT2004BasicTimeoutEstimator;
import cz.cuni.amis.pogamut.ut2004.bot.command.AdvancedLocomotion;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.SetRoute;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BotKilled;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.LocationUpdate;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPointNeighbourLink;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Self;
import cz.cuni.amis.pogamut.ut2004.utils.PogamutUT2004Property;
import cz.cuni.amis.utils.NullCheck;
import cz.cuni.amis.utils.Tuple2;

/**
 * Accelerated version of {@link UT2004Pathexecutor}. Uses
 * {@link LocationUpdate} as impulse for navigation logic cycle. Should be used
 * with accelerated versions of the stuck detectors.
 *
 * @author Bogo
 * @param <PATH_ELEMENT>
 */
public class UT2004AcceleratedPathExecutor<PATH_ELEMENT extends ILocated> extends BasePathExecutor<PATH_ELEMENT> implements IUT2004PathExecutor<PATH_ELEMENT>, IUT2004PathExecutorHelper<PATH_ELEMENT> {

    /**
     * When doing {@link UT2004PathExecutor#extendPath(List)}, how many OLD
     * (already passed by elements) should be left in the merged path.
     *
     * Some nodes are needed due to lift/teleport navigation!
     */
    public static final int PATH_MERGE_CUTOFF = 3;

    private IUT2004PathNavigator<PATH_ELEMENT> navigator;

    private UT2004Bot bot;

    private Self self;
    
    private LocationUpdate lastLocationUpdate;

    private long pathExecutionStart = Long.MIN_VALUE;

    private double pathExecutionTimeout = Double.POSITIVE_INFINITY;

    private IWorldObjectEventListener<Self, WorldObjectUpdatedEvent<Self>> selfListener = new IWorldObjectEventListener<Self, WorldObjectUpdatedEvent<Self>>() {
        @Override
        public void notify(WorldObjectUpdatedEvent<Self> event) {
            self = event.getObject();    
            navigate();
        }
    };

    private final IWorldEventListener<LocationUpdate> locationUpdateMessageListener = new IWorldEventListener<LocationUpdate>() {
        @Override
        public void notify(LocationUpdate event) {
        	if (self == null) return;
        	checkStuck();
        	if (navigator.isBotWaiting()) {
        		log.fine("NAVIGATE EVEN IF THE BOT IS WAITING");
        		navigate();
        	}
        }
    };
    
    private final IWorldEventListener<BotKilled> botKilledListener = new IWorldEventListener<BotKilled>() {
        @Override
        public void notify(BotKilled event) {
            stop();
        }
    };

    private IPathExecutionEstimator<PATH_ELEMENT> timeoutEstimator;

    /**
     * Current focus of the bot.
     */
    private ILocated focus;

    private Boolean sendingSetRoute = Pogamut.getPlatform().getBooleanProperty(PogamutUT2004Property.POGAMUT_UT2004_PATH_EXECUTOR_SEND_SET_ROUTE.getKey());

    public UT2004AcceleratedPathExecutor(UT2004Bot bot, AgentInfo info, AdvancedLocomotion body, IUT2004PathNavigator<PATH_ELEMENT> navigator) {
        this(bot, info, body, navigator, null);
    }

    public UT2004AcceleratedPathExecutor(UT2004Bot bot, AgentInfo info, AdvancedLocomotion move, IUT2004PathNavigator<PATH_ELEMENT> navigator, Logger log) {
        super(log);
        if (getLog() == null) {
            setLog(bot.getLogger().getCategory(getClass().getSimpleName()));
        }
        NullCheck.check(bot, "bot");
        if (sendingSetRoute == null) {
            sendingSetRoute = false;
        }
        this.bot = bot;
        this.navigator = navigator;
        if (this.navigator == null) {
            this.navigator = new LoqueNavigator<PATH_ELEMENT>(bot, info, move, getLog());
        }
        this.navigator.setBot(bot);
        this.navigator.setExecutor(this);
        bot.getWorldView().addObjectListener(Self.class, WorldObjectUpdatedEvent.class, selfListener);
        bot.getWorldView().addEventListener(LocationUpdate.class, locationUpdateMessageListener);
        bot.getWorldView().addEventListener(BotKilled.class, botKilledListener);
        this.timeoutEstimator = new UT2004BasicTimeoutEstimator<PATH_ELEMENT>();
    }

    public UT2004AcceleratedPathExecutor<PATH_ELEMENT> setTimeoutEstimator(IPathExecutionEstimator<PATH_ELEMENT> timeoutEstimator) {
        this.timeoutEstimator = timeoutEstimator;
        return this;
    }

    @Override
    protected IPathExecutorState createState(PathExecutorState state) {
        switch (state) {
            case STUCK:
                UT2004PathExecutorStuckState newState = new UT2004PathExecutorStuckState();

                IStuckDetector detector = checkStuckDetectors();
                if (detector == null) {
                    // GLOBAL TIMEOUT
                    newState.setGlobalTimeout(true);
                } else {
                    // STUCK DETECTED
                    newState.setStuckDetector(detector);
                }

                newState.setLink(navigator.getCurrentLink());

                return newState;
            default:
                return super.createState(state);
        }
    }

    @Override
    public void extendPath(List<PATH_ELEMENT> morePath) {
        synchronized (mutex) {
            if (morePath == null) {
                log.warning("Cannot extendPath() with NULL path.");
                return;
            }
            if (morePath.isEmpty()) {
                log.warning("Cannot extendPath() with 0-sized path.");
                return;
            }
            List<PATH_ELEMENT> currPath = getPath();
            if (currPath == null) {
                log.warning("Does not follow any path, cannot extendPath() now!");
                return;
            }
            int currIndex = getPathElementIndex();

            Tuple2<List<PATH_ELEMENT>, Integer> mergedPathAndIndex = mergePath(currPath, currIndex, morePath);
            List<PATH_ELEMENT> newPath = mergedPathAndIndex.getFirst();
            int newPathIndex = mergedPathAndIndex.getSecond();

            this.pathFuture = new PrecomputedPathFuture<PATH_ELEMENT>(newPath.get(0), newPath.get(newPath.size() - 1), newPath);

            int previousPathIndexDelta = this.pathElementIndex - this.previousPathElementIndex;

            this.pathElementIndex = newPathIndex;
            this.previousPathElementIndex = newPathIndex - previousPathIndexDelta;
            if (this.previousPathElementIndex < 0) {
                this.previousPathElementIndex = 0;
            }

            navigator.pathExtended(newPath, pathElementIndex);
        }
    }

    /**
     * Merges path together.
     *
     * @param currPath
     * @param currIndex
     * @param morePath
     * @return
     */
    protected Tuple2<List<PATH_ELEMENT>, Integer> mergePath(List<PATH_ELEMENT> currPath, int currIndex, List<PATH_ELEMENT> morePath) {
        PATH_ELEMENT currPathElement = (currIndex >= 0 && currIndex < currPath.size() ? currPath.get(currIndex) : null);
        PATH_ELEMENT lastCurrPathElement = currPath.get(currPath.size() - 1);
        PATH_ELEMENT firstMorePathElement = morePath.get(0);
        boolean mergeFirst = lastCurrPathElement.getLocation().getDistance(firstMorePathElement.getLocation()) < 50;
        int cutOffIndex = (currIndex > PATH_MERGE_CUTOFF ? currIndex - PATH_MERGE_CUTOFF : 0);
        int newPathSize = currPath.size() - cutOffIndex + morePath.size() + (mergeFirst ? -1 : 0);
        List<PATH_ELEMENT> mergedPath = new ArrayList<PATH_ELEMENT>(newPathSize);
        int mergedIndex = currIndex - cutOffIndex;

        for (int i = cutOffIndex; i < currPath.size(); ++i) {
            PATH_ELEMENT element = currPath.get(i);
            mergedPath.add(element);
//			if (mergedIndex < 0 && element == currPathElement) mergedIndex = mergedPath.size()-1;
        }
        for (int i = (mergeFirst ? 1 : 0); i < morePath.size(); ++i) {
            PATH_ELEMENT element = morePath.get(i);
            mergedPath.add(element);
//			if (mergedIndex < 0 && element == currPathElement) mergedIndex = mergedPath.size()-1;
        }

        return new Tuple2<List<PATH_ELEMENT>, Integer>(mergedPath, mergedIndex);
    }

    @Override
    public NavPointNeighbourLink getCurrentLink() {
        return navigator.getCurrentLink();
    }

    @Override
    protected void stopped() {
    }

    @Override
    protected void followPathImpl() {
    }

    /**
     * If the path is not zero-length, recalls
     * {@link IUT2004PathNavigator#newPath(List)} and set the path into the
     * GB2004 via {@link SetRoute}.
     */
    @Override
    protected void pathComputedImpl() {
        if (getPath().isEmpty()) {
            targetReached();
        } else {
            if (sendingSetRoute) {
                bot.getAct().act(new SetRoute().setRoute(getPath()));
            }
            navigator.newPath(getPath(), focus);
            pathExecutionStart = System.currentTimeMillis();
            calculateTimeout();
        }
    }

    @Override
    protected void pathComputationFailedImpl() {
    }

    /**
     * Sets the path into the GB2004 via {@link SetRoute} whenever switch occurs
     * and the rest of the path is greater than 32 path elements.
     */
    @Override
    protected void switchToAnotherPathElementImpl() {
        List<PATH_ELEMENT> path = getPath();
        if (path == null) {
            return;
        }
        if (path.size() > 31 + getPathElementIndex()) {
            List<PATH_ELEMENT> pathPart = new ArrayList<PATH_ELEMENT>(32);
            for (int i = getPathElementIndex(); i < path.size() && i < getPathElementIndex() + 31; ++i) {
                pathPart.add(path.get(i));
            }
            bot.getAct().act(new SetRoute().setRoute(pathPart));
        }

        PATH_ELEMENT pathElement = getPathElement();
        for (IStuckDetector detector : getStuckDetectors()) {
            detector.setBotTarget(pathElement);
        }
    }

    protected void calculateTimeout() {
        IPathExecutionEstimator<PATH_ELEMENT> estimator = timeoutEstimator;
        if (estimator != null) {
            pathExecutionTimeout = estimator.getTimeout(getPath());
        } else {
            pathExecutionTimeout = Long.MAX_VALUE;
        }

    }

    protected void eventLocationUpdateMessage(LocationUpdate event) {
        lastLocationUpdate = event;
    }
    
    protected void checkStuck() {
    	if (!inState(PathExecutorState.PATH_COMPUTED, PathExecutorState.SWITCHED_TO_ANOTHER_PATH_ELEMENT)) return;
    	
    	double timeDelta = System.currentTimeMillis() - pathExecutionStart;
        if (timeDelta > pathExecutionTimeout) {
            if (log != null && log.isLoggable(Level.FINER)) {
                log.log(Level.FINER, "TIMEOUT! ({0}ms)", pathExecutionTimeout);
            }
            stuck();
            return;
        }
        IStuckDetector detector = checkStuckDetectors();
        if (detector != null) {
            if (log != null && log.isLoggable(Level.WARNING)) {
                log.log(Level.WARNING, "{0} has reported stuck", detector.getClass().getSimpleName());
            }
            stuck();
        }
    }

    protected void navigate() {
    	if (!inState(PathExecutorState.PATH_COMPUTED, PathExecutorState.SWITCHED_TO_ANOTHER_PATH_ELEMENT)) return;
        log.fine("NAVIGATING");
        navigator.navigate(focus);
    }

    @Override
    public double getRemainingDistance() {
        double result = 0;

        List<PATH_ELEMENT> path = getPath();

        if (path == null) {
            return 0;
        }

        int currPathIndex = getPathElementIndex();

        if (currPathIndex >= path.size()) {
            return 0;
        }
        if (currPathIndex < 0) {
            currPathIndex = 0;
        }

        result += self.getLocation().getDistance(path.get(currPathIndex).getLocation());
        ++currPathIndex;

        for (int i = currPathIndex; i < path.size(); ++i) {
            result += path.get(i - 1).getLocation().getDistance(path.get(i).getLocation());
        }

        return result;
    }

    @Override
    public ILocated getFocus() {
        return this.focus;
    }

    @Override
    public void setFocus(ILocated located) {
        this.focus = located;
    }

    @Override
    public List<IStuckDetector> getStuckDetectors() {
        return stuckDetectors;
    }

    @Override
    protected void preStuckImpl() {
        super.preStuckImpl();
    }

    @Override
    protected void stuckImpl() {
    }

    @Override
    protected void stopImpl() {
        super.stopImpl();
    }

    @Override
    protected void preTargetReachedImpl() {
        super.preTargetReachedImpl();
    }

    @Override
    protected void targetReachedImpl() {
    }

    public IUT2004PathNavigator<PATH_ELEMENT> getNavigator() {
        return navigator;
    }
}
