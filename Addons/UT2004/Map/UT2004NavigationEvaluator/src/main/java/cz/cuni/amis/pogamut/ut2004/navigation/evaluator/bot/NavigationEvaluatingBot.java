/*
 * Copyright (C) 2013 AMIS research group, Faculty of Mathematics and Physics, Charles University in Prague, Czech Republic
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
package cz.cuni.amis.pogamut.ut2004.navigation.evaluator.bot;

import java.util.Date;
import java.util.logging.Level;

import cz.cuni.amis.pogamut.base.agent.navigation.IPathExecutorState;
import cz.cuni.amis.pogamut.base.agent.navigation.IPathFuture;
import cz.cuni.amis.pogamut.base.agent.navigation.IPathPlanner;
import cz.cuni.amis.pogamut.base.agent.navigation.PathExecutorState;
import cz.cuni.amis.pogamut.base.utils.math.DistanceUtils;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.IUT2004Navigation;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Initialize;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BotKilled;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPointNeighbourLink;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Self;
import cz.cuni.amis.pogamut.ut2004.navigation.evaluator.ServerRunner;
import cz.cuni.amis.pogamut.ut2004.navigation.evaluator.data.EvaluationResult;
import cz.cuni.amis.pogamut.ut2004.navigation.evaluator.data.PathResult;
import cz.cuni.amis.pogamut.ut2004.navigation.evaluator.data.PathResult.ResultType;
import cz.cuni.amis.pogamut.ut2004.utils.UT2004BotRunner;
import cz.cuni.amis.utils.exception.PogamutException;
import cz.cuni.amis.utils.flag.FlagListener;
import java.util.HashSet;

/**
 * Bot for evaluating navigations. Initialized with navigation given by
 * parameters and performs evaluation on given map. Extended from original
 * example archetype from Pogamut.
 *
 * @author Bogo
 */
public class NavigationEvaluatingBot extends EvaluatingBot {

	private IPathPlanner<ILocated> myPathPlanner;
	private IUT2004Navigation myNavigation;
	
    private PathContainer pathContainer;
    private Path currentPath;
    private NavigationState state;
    private Date startDate;
    private EvaluationResult result;

    private boolean waitForRecordStart = false;
    private IPathFuture<ILocated> waitingPath = null;

    private boolean failedToBuildPath = false;
    private boolean failedInNavigate = false;
    private boolean isRespawning = false;

    private ILocated respawnLocation = null;

    private long lastRespawn = Long.MAX_VALUE;

    private boolean teleportFailed = false;

    public NavigationEvaluatingBot() {
    }

    public BotNavigationParameters getParams() {
        return (BotNavigationParameters) bot.getParams();
    }

    public ExtendedBotNavigationParameters getExtendedParams() {
        if (!hasExtendedParams()) {
            return null;
        }
        Object params = getParams();
        if (params.getClass() == ExtendedBotNavigationParameters.class) {
            return (ExtendedBotNavigationParameters) params;
        } else {
            return null;
        }
    }

    public boolean hasExtendedParams() {
        return getParams().isPathRecord();
    }

    public ExtendedBotNavigationParameters getNewExtendedParams() {
        ExtendedBotNavigationParameters params = getExtendedParams();
        if (params == null) {
            params = new ExtendedBotNavigationParameters(getParams(), pathContainer, result);
        } else {
            params.setPathContainer(pathContainer);
            params.setEvaluationResult(result);
        }
        params.getEvaluationResult().setLog(null);
        params.getPathContainer().setWorld(null);
        return params;
    }

    /**
     * Initializes the path finding from parameters with use of
     * {@link NavigationFactory}.
     *
     * @param bot
     *
     */
    @Override
    protected void initializePathFinding(UT2004Bot bot) {
    	super.initializePathFinding(bot);
        myPathPlanner = NavigationFactory.getPathPlanner(this, bot, getParams().getPathPlanner());
        myNavigation = NavigationFactory.getNavigation(this, bot, getParams().getNavigation());
    }

    /**
     * Here we can modify initializing command for our bot.
     *
     * @return
     */
    @Override
    public Initialize getInitializeCommand() {
        return new Initialize().setName("NavigationEvaluatingBot").setManualSpawn(false);
    }

    /**
     * The bot is initialized in the environment - a physical representation of
     * the bot is present in the game.
     *
     * @param gameInfo
     * @param config information about configuration
     * @param init information about configuration
     */
    @SuppressWarnings("unchecked")
    @Override
    public void botInitialized(GameInfo gameInfo, ConfigChange config, InitedMessage init) {

        ExtendedBotNavigationParameters extendedParams = getExtendedParams();
        if (extendedParams == null) {

            pathContainer = new PathContainer(world);
            NavigationFactory.initializePathContainer(pathContainer, this);

            result = new EvaluationResult(pathContainer.size(), info.game.getMapName(), log, getParams().getResultPath());
        } else {
            pathContainer = extendedParams.getPathContainer();
            pathContainer.setWorld(world);

            result = extendedParams.getEvaluationResult();
            result.setLog(log);
        }
        currentPath = getNextPath(null);

        //initRespawn(currentPath.getStart());
        state = NavigationState.NotMoving;

        // IMPORTANT
        // adds a listener to the path executor for its state changes, it will
        // allow you to
        // react on stuff like "PATH TARGET REACHED" or "BOT STUCK"
        myNavigation.getPathExecutor().getState().addStrongListener(new FlagListener<IPathExecutorState>() {
            @Override
            public void flagChanged(IPathExecutorState changedValue) {
                pathExecutorStateChange(changedValue.getState());
            }
        });

    }

    /**
     * The bot is initialized in the environment - a physical representation of
     * the bot is present in the game.
     *
     * @param gameInfo
     * @param config information about configuration
     * @param init information about configuration
     * @param self
     */
    @Override
    public void botFirstSpawn(GameInfo gameInfo, ConfigChange config, InitedMessage init, Self self) {
        // receive logs from the navigation so you can get a grasp on how it is
        // working
        myNavigation.getPathExecutor().getLog().setLevel(getParams().getLogLevel());
    }

    /**
     * This method is called only once right before actual logic() method is
     * called for the first time.
     */
    @Override
    public void beforeFirstLogic() {
        if (getParams().isFullRecord()) {
            result.startRecording(act);
        }
    }

    /**
     * Main method that controls the bot - makes decisions what to do next. It
     * is called iteratively by Pogamut engine every time a synchronous batch
     * from the environment is received. This is usually 4 times per second - it
     * is affected by visionTime variable, that can be adjusted in GameBots ini
     * file in UT2004/System folder.
     */
    @Override
    public void logic() {
        // mark that another logic iteration has began
        log.info("--- Logic iteration ---");

        isRespawning = false;
        // maybe we should end? WE should not, we can have valid path in
        // currentPath
        // if (pathContainer.isEmpty()) {
        // wrapUpEvaluation();
        // }
        // get next request
        //TODO: Should not happen
        if (currentPath == null) {
            log.warning("Current path was NULL, which was unexpected!");
            currentPath = getNextPath(null);
        }

        navigatePath();
    }

    private void navigatePath() {
        log.info(String.format("Navigating path ID = %s", currentPath.getId()));

        if (state == NavigationState.Navigating) {
            boolean reachedTarget = info.atLocation(currentPath.getEnd().getLocation());
            if (reachedTarget) {
                state = NavigationState.AtTheTarget;
                log.info("Reached end of path...");
            } else {
                // Do nothing
                if (!myNavigation.isNavigating()) {
                    state = NavigationState.Failed;
                    failedInNavigate = true;
                }
                log.info("Navigation in progress...");
                return;
            }
        }
        if (state == NavigationState.OnWayToStart) {
            boolean reachedStart = info.atLocation(currentPath.getStart().getLocation());
            if (reachedStart) {
                state = NavigationState.NotMoving;
                log.info("Reached start of path...");
            } else {
                if (teleportFailed) {
                    log.info("Navigating to start from nearest NavPoint.");
                    myNavigation.navigate(currentPath.getStart());
                    if (new Date().getTime() - lastRespawn > 15000) {
                        state = NavigationState.FailedOnWayToStart;
                        myNavigation.stopNavigation();
                    }
                } else {

                    log.info("Respawn to start in progress...");
                    if (new Date().getTime() - lastRespawn > 1000) {
                        //For NavPoint with too low Z.
                        teleportFailed = true;
                        NavPoint nearest = getNearestLinkedNavPoint(currentPath.getStart());
                        initRespawn(nearest);
                        myNavigation.navigate(currentPath.getStart());
                    }
                }
                return;
            }
        }

        if (state == NavigationState.AtTheTarget) {
            // Write result and find new path -> through NotMoving
            log.info("Successfuly reached end. Hooray!");
            if (getParams().isPathRecord()) {
                result.stopRecording(act, currentPath, getParams().keepOnlyFailedRecords());
            }
            sendMessageToGame("Successfuly completed path!");
            result.addResult(currentPath, PathResult.ResultType.Completed, (new Date()).getTime() - startDate.getTime());
            startDate = null;
            log.info(String.format("Completed %d/%d paths...", result.getProcessedCount(), result.getTotalPaths()));
            currentPath = getNextPath(currentPath.getEnd());
            state = NavigationState.NotMoving;
        }
        if (state == NavigationState.Failed || state == NavigationState.FailedOnWayToStart) {
            // Write failed report and find new path -> through NotMoving
            log.info("Navigation failed!");

            NavPoint nearestNavPoint = info.getNearestNavPoint();
            Location botLocation = info.getLocation();

            if (state == NavigationState.Failed) {
                if (getParams().isPathRecord()) {
                    result.stopRecording(act, currentPath, false);
                }
                sendMessageToGame("Path navigating failed!");
                ResultType type = failedToBuildPath ? ResultType.NotBuilt : failedInNavigate ? ResultType.FailedInNavigate : ResultType.Failed;
                result.addResult(currentPath, type, startDate == null ? 0
                        : (new Date()).getTime() - startDate.getTime(), botLocation, nearestNavPoint);
            } else {
                if (!pathContainer.addTabooPath(currentPath)) {
                    sendMessageToGame("Path navigating failed!");
                    ResultType type = failedInNavigate ? ResultType.FailedToStartInNavigate : ResultType.FailedToStart;
                    result.addResult(currentPath, type, 0, botLocation, nearestNavPoint);
                }
            }
            failedToBuildPath = false;
            failedInNavigate = false;
            startDate = null;
            currentPath = getNextPath(nearestNavPoint);
            state = NavigationState.NotMoving;
        }

        if (state != NavigationState.NotMoving) {
            // Should not happen
            log.severe("Invalid state!");
            return;
        }

        // Determine where are you - Start(navigate currentPath) X
        // Elsewhere(navigate to start)
        boolean atStart = info.atLocation(currentPath.getStart());
        if (atStart) {
            IPathFuture<ILocated> path;
            // We were waiting for recording to start...
            if (waitForRecordStart) {
                path = waitingPath;
            } else {
                // Standard path computing
                path = myPathPlanner.computePath(currentPath.getStart(), currentPath.getEnd());
            }
            while (path == null || path.get() == null) {
                // Failed to build path
                sendMessageToGame("Path building failed!");
                result.addResult(currentPath, PathResult.ResultType.NotBuilt, 0);
                NavPoint origStart = currentPath.getStart();
                currentPath = getNextPath(origStart);
                if (origStart.equals(currentPath.getStart())) {
                    path = myPathPlanner.computePath(currentPath.getStart(), currentPath.getEnd());
                } else {
                    initRespawn(currentPath.getStart());
                    state = NavigationState.NotMoving;
                    return;
                }
            }
            currentPath.computeMetrics(path);
            if (getParams().isPathRecord() && !waitForRecordStart) {
                result.startRecording(act, currentPath);
                waitForRecordStart = true;
                waitingPath = path;
                return;
            } else {
                waitForRecordStart = false;
                waitingPath = null;
            }
            startDate = new Date();
            log.info("Starting navigation on the path.");
            myNavigation.navigate(path);
            state = NavigationState.Navigating;
        } else {
            log.info("Starting respawn to the start.");
            initRespawn(currentPath.getStart());
            state = NavigationState.OnWayToStart;
            teleportFailed = false;
        }
    }

    private NavPoint getNearestLinkedNavPoint(NavPoint start) {
        HashSet<NavPoint> neigbours = new HashSet<NavPoint>();
        for (NavPointNeighbourLink edge : start.getIncomingEdges().values()) {
            neigbours.add(edge.getFromNavPoint());
        }
        return DistanceUtils.getNearest(neigbours, start);
    }

    /**
     * Called each time our bot die. Good for reseting all bot state dependent
     * variables.
     *
     * @param event
     */
    @Override
    public void botKilled(BotKilled event) {
        if (!isRespawning) {
            if (respawnLocation == null) {
                log.info("Bot died!");
                this.state = this.state == NavigationState.OnWayToStart ? NavigationState.FailedOnWayToStart : NavigationState.Failed;
                myNavigation.stopNavigation();
            }
            //respawn();
        } else {
            isRespawning = false;
        }
    }

    /**
     * Path executor has changed its state (note that
     * {@link UT2004BotModuleController#getPathExecutor()} is internally used by
     * {@link UT2004BotModuleController#getNavigation()} as well!).
     *
     * @param state
     */
    protected void pathExecutorStateChange(PathExecutorState state) {
        switch (state) {
            case PATH_COMPUTATION_FAILED:
                failedToBuildPath = true;
                this.state = this.state == NavigationState.OnWayToStart ? NavigationState.FailedOnWayToStart : NavigationState.Failed;
                break;

            case TARGET_REACHED:
                break;

            case STUCK:
                this.state = this.state == NavigationState.OnWayToStart ? NavigationState.FailedOnWayToStart : NavigationState.Failed;

                break;
            case STOPPED:
            default:
                break;
        }
    }

    public static void main(String args[]) throws PogamutException {
        new UT2004BotRunner(NavigationEvaluatingBot.class, "NavigationEvaluatingBot").setMain(true).setLogLevel(Level.INFO).startAgent();
    }

    private Path getNextPath(NavPoint start) {
        int processedCount = result.getProcessedCount();
        if (getParams().isPathRecord()) {
            ExtendedBotNavigationParameters params = getExtendedParams();
            int iteration = params == null ? 1 : params.getIteration();
            if (processedCount >= iteration * ServerRunner.getPathRecordsLimit()) {
                // Shutdown bot and restart ucc.
                wrapUpEvaluation();
                return null;
            }
        }
        log.log(Level.WARNING, "Requested next path, from {0}", start == null ? "NULL" : start);
        Path path = start == null ? pathContainer.getPath() : pathContainer.getPath(start);
        if (path == null) {
            log.info("No path from requested node found, looking for another");
            path = pathContainer.getPath();
        }
        if (path == null || processedCount >= getParams().getLimitForCompare()) {
            wrapUpEvaluation();
        }
        if (processedCount > 0 && processedCount % 10 == 0) {
            // Inform about progress every 10 completed paths
            result.exportAggregate();
        }
        return path;
    }

    private void wrapUpEvaluation() {
        log.info("No more paths to navigate, we are finished...");
        isCompleted = true;
        new Runnable() {
            public void run() {
                bot.stop();
            }
        }.run();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void sendMessageToGame(String message) {
        body.getCommunication().sendGlobalTextMessage(message);
        //act.act(new ShowText(bot.getName(), message, Color.yellow, 10d, true));
    }

    // private void sendMessageToGame(String template, Object... obj) {
    // String message = String.format(template, obj);
    // sendMessageToGame(message);
    // }
    @Override
    public void botShutdown() {
        if (getParams().isFullRecord()) {
            result.stopRecording(act, getParams().keepOnlyFailedRecords() && !result.hasFailedResult());
        }
        result.exportAggregate();
        result.export(true);

        super.botShutdown();
    }

    private void initRespawn(ILocated location) {
        respawnLocation = location;
        log.warning("Killing bot to init respawning of bot.");
        isRespawning = true;
        lastRespawn = new Date().getTime();
        respawn();
    }

    private void respawn() {
        if (respawnLocation == null) {
            log.warning("Respawning bot.");
            body.getAction().respawn();
        } else {
            log.log(Level.WARNING, "Respawning bot to location {0}.", respawnLocation);
            body.getAction().respawn(respawnLocation);
            respawnLocation = null;
        }
    }
}
