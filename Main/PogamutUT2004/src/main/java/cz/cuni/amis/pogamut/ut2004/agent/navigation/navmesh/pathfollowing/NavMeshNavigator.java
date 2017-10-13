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

import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.base.utils.math.DistanceUtils;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.AgentInfo;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.NavPoints;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.AbstractUT2004PathNavigator;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.IUT2004PathRunner;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.NavigationState;
import cz.cuni.amis.pogamut.ut2004.bot.command.AdvancedLocomotion;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Mover;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPointNeighbourLink;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Self;
import cz.cuni.amis.pogamut.ut2004.utils.UnrealUtils;
import cz.cuni.amis.utils.NullCheck;
import cz.cuni.amis.utils.SafeEquals;
import cz.cuni.amis.utils.exception.PogamutException;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Runner for navigation with navigation mesh. Evolved from
 * {@link LoqueNavigator}. Is suited for accelerated navigation logic cycle, has
 * finer switching conditions.
 *
 * @author Bogo
 * @param <PATH_ELEMENT>
 */
public class NavMeshNavigator<PATH_ELEMENT extends ILocated> extends AbstractUT2004PathNavigator<PATH_ELEMENT> {

    /**
     * Current navigation destination.
     */
    private Location navigDestination = null;

    /**
     * Current stage of the navigation.
     */
    private Stage navigStage = Stage.COMPLETED;

    /**
     * Current focus of the bot, if null, provide default focus.
     * <p>
     * <p>
     * Filled at the beginning of the
     * {@link NavMeshNavigator#navigate(ILocated, int)}.
     */
    private ILocated focus = null;

    /**
     * {@link Self} listener.
     */
    private class SelfListener implements IWorldObjectEventListener<Self, WorldObjectUpdatedEvent<Self>> {

        private IWorldView worldView;

        /**
         * Constructor. Registers itself on the given WorldView object.
         *
         * @param worldView WorldView object to listen to.
         */
        public SelfListener(IWorldView worldView) {
            this.worldView = worldView;
            worldView.addObjectListener(Self.class, WorldObjectUpdatedEvent.class, this);
        }

        @Override
        public void notify(WorldObjectUpdatedEvent<Self> event) {
            self = event.getObject();
        }
    }

    /**
     * {@link Self} listener
     */
    private SelfListener selfListener;

    /*========================================================================*/
    /**
     * Distance, which is considered as close enough for considering the bot to
     * be AT LOCATION/NAV POINT.
     *
     * If greater than 50, navigation will failed on DM-Flux2 when navigating
     * between health vials in one of map corers.
     */
    public static final int CLOSE_ENOUGH = 40;

    /*========================================================================*/
    @Override
    protected void navigate(ILocated focus, int pathElementIndex) {
        if (log != null && log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "Navigator.navigate(): Current stage {0}", navigStage);
        }
        this.focus = focus;
        switch (navigStage = keepNavigating()) {
            case AWAITING_MOVER:
            case RIDING_MOVER:
                setBotWaiting(true);
                break;
            case TELEPORT:
            case NAVIGATING:
            case REACHING:
                setBotWaiting(false);
                break;

            case TIMEOUT:
            case CRASHED:
            case CANCELED:
                if (log != null && log.isLoggable(Level.WARNING)) {
                    log.log(Level.WARNING, "Navigation {0}", navigStage);
                }
                executor.stuck();
                return;

            case COMPLETED:
                executor.targetReached();
                break;
        }
        if (log != null && log.isLoggable(Level.FINEST)) {
            log.log(Level.FINEST, "Navigator.navigate(): Next stage {0}", navigStage);
        }
    }

    @Override
    public void reset() {
        // reinitialize the navigator's values

        navigCurrentLocation = null;
        navigCurrentNode = null;
        navigCurrentLink = null;
        navigDestination = null;
        navigIterator = null;
        navigLastLocation = null;
        navigLastNode = null;
        navigNextLocation = null;
        navigNextNode = null;
        navigNextLocationOffset = 0;
        navigStage = Stage.COMPLETED;
        setBotWaiting(false);

        resetNavigMoverVariables();
    }

    @Override
    public void newPath(List<PATH_ELEMENT> path, ILocated focus) {
        // prepare for running along new path
        reset();

        // 1) obtain the destination
        Location dest = path.get(path.size() - 1).getLocation();

        // 2) init the navigation
        initPathNavigation(dest, path, focus);
        
        // 3) start the navigation
        navigate(focus);
    }

    @Override
    public void pathExtended(List<PATH_ELEMENT> path, int currentPathIndex) {
        if (path == null || path.isEmpty()) {
            throw new RuntimeException("path is null or 0-sized!");
        }
        navigDestination = path.get(path.size() - 1).getLocation();
        navigIterator = path.iterator();

        int newOffset = -currentPathIndex;
        for (int i = 0; i < path.size() && i < currentPathIndex + navigNextLocationOffset && navigIterator.hasNext(); ++i) {
            ++newOffset;
            navigIterator.next();
        }
        log.fine("PATH EXTEND ... curr index " + currentPathIndex + ", old offset " + navigNextLocationOffset + ", new offset " + newOffset + ", path size " + path.size());
        navigNextLocationOffset = newOffset;
    }

    @Override
    public NavPointNeighbourLink getCurrentLink() {
        return navigCurrentLink;
    }

    /*========================================================================*/
    /**
     * Initializes direct navigation to the specified destination.
     *
     * @param dest Destination of the navigation.
     */
    protected void initDirectNavigation(Location dest) {
        // calculate destination distance
        int distance = (int) memory.getLocation().getDistance(dest);
        // init the navigation
        if (log != null && log.isLoggable(Level.FINE)) {
            log.log(
                    Level.FINE,"Navigator.initDirectNavigation(): initializing direct navigation, distance {0}"
            , distance);
        }
        // init direct navigation
        initDirectly(dest);
    }

    /*========================================================================*/
    /**
     * Initializes navigation to the specified destination along specified path.
     *
     * @param dest Destination of the navigation.
     * @param path Navigation path to the destination.
     */
    protected void initPathNavigation(Location dest, List<PATH_ELEMENT> path, ILocated focus) {
        // init the navigation
        if (log != null && log.isLoggable(Level.FINE)) {
            log.log(
                    Level.FINE,"Navigator.initPathNavigation(): initializing path navigation, nodes {0}"
            , path.size());
        }
        // init path navigation
        if (!initAlongPath(dest, path)) {
            // do it directly then..
            initDirectNavigation(dest);
        }
    }

    /*========================================================================*/
    /**
     * Navigates with the current navigation request.
     *
     * @return Stage of the navigation progress.
     */
    protected Stage keepNavigating() {
        // is there any point in navigating further?
        if (navigStage.terminated) {
            return navigStage;
        }

        if (log != null && log.isLoggable(Level.FINE)) {
            if (navigLastNode != null) {
                log.fine("Navigator.keepNavigating(): From " + NavPoints.describe(navigLastNode));
            } else if (navigLastLocation != null) {
                log.fine("Navigator.keepNavigating(): From " + navigLastLocation);
            }
            if (navigCurrentNode != null) {
                log.fine("Navigator.keepNavigating(): To   " + NavPoints.describe(navigCurrentNode));
            } else if (navigCurrentLocation != null) {
                log.fine("Navigator.keepNavigating(): To   " + navigCurrentLocation);
            }            
        }

        // try to navigate
        switch (navigStage) {
            case REACHING:
                navigStage = navigDirectly();
                break;
            default:
                navigStage = navigAlongPath();
                break;
        }

        // return the stage
        if (log != null && log.isLoggable(Level.FINEST)) {
            log.finest("Navigator.keepNavigating(): In stage " + navigStage);
        }
        return navigStage;
    }

    // ==============
    // ==============
    // NAVIG DIRECTLY
    // ==============
    // ==============
    
    /**
     * Initializes direct navigation to given destination.
     *
     * @param dest Destination of the navigation.
     * @return Next stage of the navigation progress.
     */
    private Stage initDirectly(Location dest) {
        // setup navigation info
        navigDestination = dest;
        // init runner
        runner.reset();
        // reset navigation stage
        return navigStage = Stage.REACHING;
    }

    /**
     * Tries to navigate the agent directly to the navig destination.
     *
     * @return Next stage of the navigation progress.
     */
    private Stage navigDirectly() {
        // get the distance from the target
        int distance = (int) memory.getLocation().getDistance(navigDestination);

        // are we there yet?
        if (distance <= CLOSE_ENOUGH) {
            if (log != null && log.isLoggable(Level.FINE)) {
                log.fine("Navigator.navigDirectly(): destination close enough: " + distance);
            }
            return Stage.COMPLETED;
        }

        // run to that location..
        if (!runner.runToLocation(navigLastLocation, navigDestination, null, (focus == null ? navigDestination : focus), null, true, false)) {
            if (log != null && log.isLoggable(Level.FINE)) {
                log.fine("Navigator.navigDirectly(): direct navigation failed");
            }
            return Stage.CRASHED;
        }

        // well, we're still running
        if (log != null && log.isLoggable(Level.FINEST)) {
            log.finer("Navigator.navigDirectly(): traveling directly, distance = " + distance);
        }
        return navigStage;
    }

    // ================
    // ================
    // NAVIG ALONG PATH 
    // ================
    // ================    
    
    /**
     * Iterator through navigation path.
     */
    private Iterator<PATH_ELEMENT> navigIterator = null;

    /**
     * How many path elements we have iterated over before selecting the current
     * {@link NavMeshNavigator#navigNextLocation}.
     */
    private int navigNextLocationOffset = 0;

    /**
     * Last location in the path (the one the agent already reached).
     */
    private Location navigLastLocation = null;

    /**
     * If {@link NavMeshNavigator#navigLastLocation} is a {@link NavPoint} or has
     * NavPoint near by, its instance is written here (null otherwise).
     */
    private NavPoint navigLastNode = null;

    /**
     * Current node in the path (the one the agent is running to).
     */
    private Location navigCurrentLocation = null;

    /**
     * If {@link NavMeshNavigator#navigCurrentLocation} is a {@link NavPoint} or
     * has NavPoint near by, its instance is written here (null otherwise).
     */
    private NavPoint navigCurrentNode = null;

    /**
     * If moving between two NavPoints {@link NavPoint} the object
     * {@link NeighbourLink} holding infomation about the link (if any) will be
     * stored here (null otherwise).
     */
    private NavPointNeighbourLink navigCurrentLink = null;

    /**
     * Next node in the path (the one being prepared).
     */
    private Location navigNextLocation = null;

    /**
     * If {@link NavMeshNavigator#navigNextLocation} is a {@link NavPoint} or has
     * NavPoint near by, its instance is written here (null otherwise).
     */
    private NavPoint navigNextNode = null;

    /**
     * Initializes navigation along path.
     *
     * @param dest Destination of the navigation.
     * @param path Path of the navigation.
     * @return True, if the navigation is successfuly initialized.
     */
    private boolean initAlongPath(Location dest, List<PATH_ELEMENT> path) {
        // setup navigation info
        navigDestination = dest;
        navigIterator = path.iterator();
        // reset current node
        navigCurrentLocation = bot.getLocation();
        navigCurrentNode = DistanceUtils.getNearest(bot.getWorldView().getAll(NavPoint.class).values(), bot.getLocation(), 40);
        // prepare next node
        prepareNextNode();
        // reset navigation stage
        navigStage = Stage.NAVIGATING;
        // reset node navigation info
        return switchToNextNode();
    }

    /**
     * Tries to navigate the agent safely along the navigation path.
     *
     * @return Next stage of the navigation progress.
     */
    private Stage navigAlongPath() {
        // get the distance from the destination
        int totalDistance = (int) memory.getLocation().getDistance(navigDestination);

        // are we there yet?
        if (totalDistance <= CLOSE_ENOUGH) {
            log.log(Level.FINEST, "Navigator.navigAlongPath(): destination close enough: {0}", totalDistance);
            return Stage.COMPLETED;
        }

        // navigate
        if (navigStage.mover) {
            log.fine("Navigator.navigAlongPath(): MOVER");
            return navigMover();
        } else if (navigStage.teleport) {
            log.fine("Navigator.navigAlongPath(): TELEPORT");
            return navigThroughTeleport();
        } else {
            log.fine("Navigator.navigAlongPath(): STANDARD");
            return navigToCurrentNode(true, false); // USE FOCUS, normal navigation
        }
    }
    
    // ==================================
    // ==================================
    // SWITCHING TO THE NEXT NODE ON PATH
    // ==================================
    // ==================================
    
    /**
     * Prepares next navigation node in path.
     * <p>
     * <p>
     * If necessary just recalls
     * {@link NavMeshNavigator#prepareNextNodeTeleporter()}.
     */
    private void prepareNextNode() {
        if (navigCurrentNode != null && navigCurrentNode.isTeleporter()) {
            // current node is a teleporter! ...
            prepareNextNodeTeleporter();
            return;
        }

        // retreive the next node, if there are any left
        // note: there might be null nodes along the path!
        ILocated located = null;
        navigNextLocation = null;
        navigNextNode = null;
        navigNextLocationOffset = 0;
        while ((located == null) && navigIterator.hasNext()) {
            // get next node in the path
            located = navigIterator.next();
            navigNextLocationOffset += 1;
        }

        // did we get the next node?
        if (located == null) {
            navigNextLocationOffset = 0;
            return;
        }

        if (executor.getPathElementIndex() + navigNextLocationOffset >= executor.getPath().size()) {
            navigNextLocationOffset = 0; // WTF?
        }

        // obtain next location
        navigNextLocation = located.getLocation();
        // obtain navpoint instance for a given location
        navigNextNode = getNavPoint(located);
    }

    /**
     * Prepares next node in the path assuming the currently pursued node is a
     * teleporter.
     */
    private void prepareNextNodeTeleporter() {
        // Retrieve the next node, if there are any left
        // note: there might be null nodes along the path!
        ILocated located = null;
        navigNextLocation = null;
        navigNextLocationOffset = 0;
        boolean nextTeleporterFound = false;
        while ((located == null) && navigIterator.hasNext()) {
            // get next node in the path
            located = navigIterator.next();
            navigNextLocationOffset += 1;
            if (located == null) {
                continue;
            }
            navigNextNode = getNavPoint(located);
            if (navigNextNode != null && navigNextNode.isTeleporter()) {
                // next node is 
                if (!nextTeleporterFound) {
                    // ignore first teleporter as it is the other end of the teleporter we're currently trying to enter
                    located = null;
                }
                nextTeleporterFound = true;
            } else {
                break;
            }
        }

        // did we get the next node?
        if (located == null) {
            navigNextLocationOffset = 0;
            return;
        }

        if (executor.getPathElementIndex() + navigNextLocationOffset >= executor.getPath().size()) {
            navigNextLocationOffset = 0; // WTF?
        }

        // obtain next location
        navigNextLocation = located.getLocation();
        // obtain navpoint instance for a given location
        navigNextNode = getNavPoint(located);
    }

    /**
     * Initializes next navigation node in path.
     *
     * @return True, if the navigation node is successfully switched.
     */
    private boolean switchToNextNode() {
        if (log != null && log.isLoggable(Level.FINER)) {
            log.finer("Navigator.switchToNextNode(): switching!");
        }

        // move the current node into last node
        navigLastLocation = navigCurrentLocation;
        navigLastNode = navigCurrentNode;

        // get the next prepared node
        if (null == (navigCurrentLocation = navigNextLocation)) {
            // no nodes left there..
            if (log != null && log.isLoggable(Level.FINER)) {
                log.finer("Navigator.switchToNextNode(): no nodes left");
            }
            navigCurrentNode = null;
            return false;
        }
        // rewrite the navpoint as well
        navigCurrentNode = navigNextNode;

        // store current NavPoint link
        navigCurrentLink = getNavPointsLink(navigLastNode, navigCurrentNode);

        if (navigCurrentLink == null) {
            getNavPointsLink(navigLastNode, navigCurrentNode);
            if (log.isLoggable(Level.INFO)) {
                log.info("No link information...");
            }
        }

        // ensure that the last node is not null
        if (navigLastLocation == null) {
            navigLastLocation = bot.getLocation();
            navigLastNode = navigCurrentNode;
        }

        // get next node distance
        int localDistance = (int) memory.getLocation().getDistance(navigCurrentLocation.getLocation());

        // is this next node a teleporter?
        if (navigCurrentNode != null && navigCurrentNode.isTeleporter()) {
            navigStage = Stage.TeleporterStage();
        } // is this next node a mover?
        else if (navigCurrentNode != null && navigCurrentNode.isLiftCenter()) {
            // setup mover sequence
            navigStage = Stage.FirstMoverStage();
            resetNavigMoverVariables();
        } // are we still moving on mover? 
        else if (navigStage.mover) {
            navigStage = navigStage.next();
            // init the runner
            runner.reset();
        } else if (navigStage.teleport) {
            navigStage = navigStage.next();
            // init the runner
            runner.reset();
        } // no movers & teleports
        else {
            // init the runner
            runner.reset();
        }

        // switch to next node
        if (log != null && log.isLoggable(Level.FINE)) {
            if (navigCurrentNode != null) {
                log.fine(
                        "Navigator.switchToNextNode(): switch to next node " + navigCurrentNode.getId().getStringId()
                        + ", distance " + localDistance
                        + ", reachable true"
                        + ", mover " + navigStage.mover
                );
                // we do not have extra information about the location we're going to reach
            } else {
                log.log(Level.FINE, "Navigator.switchToNextNode(): switch to next location {0}, distance {1}, mover {2}", new Object[]{navigCurrentLocation, localDistance, navigStage.mover});
            }
        }

        // tell the executor that we have moved in the path to the next element
        if (executor.getPathElementIndex() < 0) {
            executor.switchToAnotherPathElement(0);
        } else {
            if (navigNextLocationOffset > 0) {
                executor.switchToAnotherPathElement(executor.getPathElementIndex() + navigNextLocationOffset);
            } else {
                executor.switchToAnotherPathElement(executor.getPathElementIndex());
            }
        }
        navigNextLocationOffset = 0;

        prepareNextNode();

        if (localDistance < 20) {
        	log.log(Level.FINER, "Navigator.switchToNextNode(): next location too near, switching again!");
            return switchToNextNode();
        }

        return true;
    }

    // ================
    // ================
    // MOVER NAVIGATION
    // ================
    // ================    

    private int navigMoverRideUpCount;

    private int navigMoverRideDownCount;

    private Boolean navigMoverIsRidingUp;

    private Boolean navigMoverIsRidingDown;
    
    private Boolean navigMoverGettingBackRunnerReset;
    
    private Boolean navigMoverGettingToLiftCenterRunnerReset;
    
    private void resetNavigMoverVariables() {
        navigMoverIsRidingUp = null;
        navigMoverIsRidingDown = null;
        navigMoverRideUpCount = 0;
        navigMoverRideDownCount = 0;
        navigMoverGettingBackRunnerReset = false;
        navigMoverGettingToLiftCenterRunnerReset = false;
    }

    private void checkMoverMovement(Mover mover) {
        // ASSUMING THAT MOVER IS ALWAYS ... riding fully UP, riding fully DOWN (or vice versa) and passing all possible exits
        if (mover.getVelocity().z > 0) {
            // mover is riding UP
            if (navigMoverIsRidingUp == null) {            	
                navigMoverIsRidingUp = true;
                navigMoverIsRidingDown = false;
                navigMoverRideUpCount = 1;
                navigMoverRideDownCount = 0;
                log.fine("Navigator.checkMoverMovement(): MOVER RIDING UP (1)");
            } else if (navigMoverIsRidingDown) {
                navigMoverIsRidingUp = true;
                navigMoverIsRidingDown = false;
                ++navigMoverRideUpCount;
                log.fine("Navigator.checkMoverMovement(): MOVER RIDING UP (" + navigMoverRideUpCount + ")");
            }
        } else if (mover.getVelocity().z < 0) {
            // mover is riding DOWN
            if (navigMoverIsRidingDown == null) {
                navigMoverIsRidingUp = false;
                navigMoverIsRidingDown = true;
                navigMoverRideUpCount = 0;
                navigMoverRideDownCount = 1;
                log.fine("Navigator.checkMoverMovement(): MOVER RIDING DOWN (1)");
            } else if (navigMoverIsRidingUp) {
                navigMoverIsRidingUp = false;
                navigMoverIsRidingDown = true;
                ++navigMoverRideDownCount;
                log.fine("Navigator.checkMoverMovement(): MOVER RIDING DOWN (" + navigMoverRideDownCount + ")");
            }
        }
    }

    /**
     * Tries to navigate the agent safely along mover navigation nodes.
     *
     * <h4>Pogamut troubles</h4>
     *
     * Since the engine does not send enough reasonable info about movers and
     * their frames, the agent relies completely and only on the associated
     * navigation points. Fortunately, LiftCenter navigation points move with
     * movers.
     *
     * <p>
     * Well, do not get too excited. Pogamut seems to update the position of
     * LiftCenter navpoint from time to time, but it's not frequent enough for
     * correct and precise reactions while leaving lifts.</p>
     *
     * @return Next stage of the navigation progress.
     */
    private Stage navigMover() {
        Stage stage = navigStage;

        if (navigCurrentNode == null) {
            if (log != null && log.isLoggable(Level.WARNING)) {
                log.warning("Navigator.navigMover(" + stage + "): can't navigate through the mover without the navpoint instance (navigCurrentNode == null)");
            }
            return Stage.CRASHED;
        }

        Mover mover = (Mover) bot.getWorldView().get(navigCurrentNode.getMover());
        if (mover == null) {
            if (log != null && log.isLoggable(Level.WARNING)) {
                log.warning("Navigator.navigMover(" + stage + "): can't navigate through the mover as current node does not represent a mover (moverId == null): " + navigCurrentNode);
            }
            return Stage.CRASHED;
        }
        checkMoverMovement(mover);

        // update navigCurrentLocation as the mover might have moved
        navigCurrentLocation = navigCurrentNode.getLocation();

        if (navigNextNode != null) {
            // update navigNextLocation as the mover might have moved
            navigNextLocation = navigNextNode.getLocation();
        }

        log.fine("Navigator.navigMover(" + stage + "): SELF " + memory.getLocation());
        log.fine("Navigator.navigMover(" + stage + "): CURR " + NavPoints.describe(navigCurrentNode));
        log.fine("Navigator.navigMover(" + stage + "): NEXT " + NavPoints.describe(navigNextNode));
        log.fine("Navigator.navigMover(" + stage + "):      " + NavPoints.describe(mover));

        // get horizontal distance from the mover center node ... always POSITIVE
        int hDistance = (int) memory.getLocation().getDistance2D(navigCurrentLocation.getLocation());
        // get vertical distance from the mover center node ... +/- ... negative -> mover is below us, positive -> mover is above us
        int zDistance = (int) navigCurrentLocation.getLocation().getDistanceZ(memory.getLocation());
        // whether mover is riding UP
        boolean moverRidingUp = mover.getVelocity().z > 0;
        // whether mover is riding DOWN
        boolean moverRidingDown = mover.getVelocity().z < 0;
        // whether mover is standing still
        boolean moverStandingStill = Math.abs(mover.getVelocity().z) < Location.DISTANCE_ZERO;
        // get 2D distance to the mover
        int moverHDistance = (int) memory.getLocation().getDistance2D(mover.getLocation());
        int moverZDistance = (int) mover.getLocation().getDistanceZ(memory.getLocation());
        
        log.finer("Navigator.navigMover(" + stage + "): CURR  hDist:" + hDistance + ", zDist:" + zDistance);
        log.finer("Navigator.navigMover(" + stage + "): MOVER hDist:" + moverHDistance + ", zDist:" + moverZDistance + ", " + (moverRidingUp ? "riding UP" : (moverRidingDown ? "riding DOWN" : moverStandingStill ? "standing STILL" : " movement unknown")));

        // --------------
        // --------------
        // AWAITING MOVER
        // --------------
        // --------------
        
        if (navigStage == Stage.AWAITING_MOVER) {
        	// SHOULD WE WAIT FOR THE MOVER?
            boolean waitForTheMover = false;
            
            if (moverHDistance < 50 && moverZDistance > 100 && moverRidingUp) {
            	log.fine("Navigator.navigMover(" + stage + "): we are UNDER the mover and it is RIDING UP ... assuming waiting position");
            	waitForTheMover = true; 
            } else
            if (moverZDistance > 10) {
            	log.fine("Navigator.navigMover(" + stage + "): mover is not in correct position ... assuming waiting position");
            	waitForTheMover = true;
            } else
            if (zDistance > 20 && moverRidingUp) {
            	log.fine("Navigator.navigMover(" + stage + "): mover is riding up, we won't make it to the center ... assuming waiting position");
            	waitForTheMover = true;
            }
            
            if (waitForTheMover) {
            	// => WAIT FOR THE MOVER
            	if (memory.atLocation(navigLastLocation, 50)) {
            		// AT WAITING POSITION...
            		if (navigMoverGettingBackRunnerReset) {
                    	navigMoverGettingBackRunnerReset = false;
                    	runner.reset();
                    }
            		body.turnTo(navigCurrentLocation);
            		return navigStage;
            	}            	
            	if (!navigMoverGettingBackRunnerReset) {
                	runner.reset();
                 	navigMoverGettingBackRunnerReset = true;
                }
            	if (run(null, navigLastLocation, null, null, navigCurrentLocation, true) == NavigateResult.CRASHED) {
                     if (log != null && log.isLoggable(Level.FINE)) {
                         log.fine("Navigator.navigMover(" + stage + "): navigation to wait-for-mover node failed");
                     }
                     return Stage.CRASHED;
                }
                return navigStage;
            }
            if (navigMoverGettingBackRunnerReset) {
            	navigMoverGettingBackRunnerReset = false;
            	runner.reset();
            }

            // MOVER HAS ARRIVED (at least that what we're thinking so...)
            if (log != null && log.isLoggable(Level.FINER)) {
                log.finer("Navigator.navigMover(" + stage + "): mover arrived");
            }

            // LET'S MOVE TO THE LIFT CENTER (do not use focus)
            return navigToCurrentNode(false, true);
        }
        if (navigMoverGettingBackRunnerReset) {
        	navigMoverGettingBackRunnerReset = false;
        	runner.reset();
        }
        
        // ------------
        // ------------
        // RIDING MOVER
        // ------------
        // ------------
        
        if (navigStage == Stage.RIDING_MOVER) {
            if (navigMoverRideDownCount > 2 || navigMoverRideUpCount > 2) {
                // we're riding up & down without any effect ... failure :(
                if (log != null && log.isLoggable(Level.FINE)) {
                    log.fine("Navigator.navigThroughMover(" + stage + "): navigation to mover exit node failed, we've rided twice up & down and there was no place suitable to exit the mover in order to get to get to " + navigCurrentNode);
                }
                return Stage.CRASHED;
            }

            if (hDistance > 600) {
                if (log != null && log.isLoggable(Level.WARNING)) {
                    log.warning("Navigator.navigThroughMover(" + stage + "): navigation to mover exit node failed, the node is too far, hDistance " + hDistance + " > 600, unsupported (weird navigation graph link)");
                }
                return Stage.CRASHED;
            }

            // wait for the mover to ride us up/down
            if (Math.abs(zDistance) > 50) {
                // run to the last node, the one we're waiting on
                log.finer("Navigator.navigMover(" + stage + "): riding the mover");
                
                //WE MUST NOT USE FOCUS! We have to see the mover. TODO: provide turning behavior, i.e., turn to desired focus once in a time
                if (moverHDistance < 35) {
                	log.finer("Navigator.navigMover(" + stage + "): at lift-center, looking towards exit");
                	if (navigMoverGettingToLiftCenterRunnerReset) {
                		navigMoverGettingToLiftCenterRunnerReset = false;
                		runner.reset();
                	}
                	body.turnTo(navigCurrentLocation);
                } else {
                	log.finer("Navigator.navigMover(" + stage + "): reaching lift-center, looking towards exit");
                	if (!navigMoverGettingToLiftCenterRunnerReset) {
                		navigMoverGettingToLiftCenterRunnerReset = true;
                		runner.reset();
                	}                	
                	// RUN: get back to the mover, look towars exit, do not jump
		            if (run(null, mover.getLocation(), null, null, navigCurrentLocation, true) == NavigateResult.CRASHED) {
		                log.fine("Navigator.navigMover(" + stage + "): navigation to last node failed");
		                return Stage.CRASHED;
		            }
                }
                // and keep waiting for the mover to go to the correct position

                return navigStage;
            }
            if (navigMoverGettingToLiftCenterRunnerReset) {
        		navigMoverGettingToLiftCenterRunnerReset = false;
        		runner.reset();
        	}

            // MOVER HAS ARRIVED TO POSITION FOR EXIT (at least that what we're thinking so...)
            if (log != null && log.isLoggable(Level.FINER)) {
                log.finer("Navigator.navigMover(" + stage + "): exiting the mover");
            }

            // LET'S MOVE TO THE LIFT EXIT (do not use focus, may jump)
            return navigToCurrentNode(false, false);
        } else {
            if (log != null && log.isLoggable(Level.WARNING)) {
                log.warning("Navigator.navigThroughMover(" + stage + "): invalid stage, neither AWAITING_MOVER nor RIDING MOVER");
            }
            return Stage.CRASHED;
        }

    }

    // =====================    
    // =====================
    // TELEPORTER NAVIGATION
    // =====================
    // =====================
    
    /**
     * Tries to navigate the agent safely to the current navigation node.
     *
     * @return Next stage of the navigation progress.
     */
    private Stage navigThroughTeleport() {
        if (navigCurrentNode != null) {
            // update location of the current place we're reaching
            navigCurrentLocation = navigCurrentNode.getLocation();
        }

        if (navigNextNode != null) {
            // update location of the Next place we're reaching
            navigNextLocation = navigNextNode.getLocation();
        }

        // Now we have to check whether we have not already been teleported
        int localDistance2_1 = Integer.MAX_VALUE;
        int localDistance2_2 = Integer.MAX_VALUE;
        for (NavPointNeighbourLink link : navigCurrentNode.getOutgoingEdges().values()) {
            if (link.getToNavPoint().isTeleporter()) {
                localDistance2_1 = (int) memory.getLocation().getDistance(link.getToNavPoint().getLocation());
                localDistance2_2 = (int) memory.getLocation().getDistance(Location.add(link.getToNavPoint().getLocation(), new Location(0, 0, 100)));

                // are we close enough to switch to the OTHER END of the teleporter?
                if ((localDistance2_1 < 200) || (localDistance2_2 < 200)) {
                    // yes we are! we already passed the teleporter, so DO NOT APPEAR DUMB and DO NOT TRY TO RUN BACK 
                    if (log != null && log.isLoggable(Level.FINE)) {
                        log.fine("Navigator.navigThroughTeleport(): at the other end of teleport, switching...");
                    }
                    // ... better to switch navigation to the next node
                    if (!switchToNextNode()) {
                        // switch to the direct navigation
                        if (log != null && log.isLoggable(Level.FINE)) {
                            log.fine("Navigator.navigThroughTeleport(): switch to direct navigation");
                        }
                        initDirectly(navigDestination);
                    }
                    return keepNavigating();
                }
            }
        }
        
        // We have not reached the teleport yet, continue running to the current node (teleporter)
        return navigToCurrentNode(true, false);
    }
    
    // =======================================
    // LOWEST NAVIGATE METHOD - INVOKES RUNNER
    // =======================================
    
    /**
     * Tries to navigate the agent safely to the current navigation node.
     *
     * @return Next stage of the navigation progress.
     */
    private Stage navigToCurrentNode(boolean useFocus, boolean forceNoJump) {
    	NavigateResult result = run(navigLastLocation, navigCurrentLocation, navigCurrentLink, navigNextLocation, useFocus ? focus : navigCurrentLocation, forceNoJump); 
    	switch (result) {
    	case RUNNING:  
    		return navigStage;
    	case CRASHED:  
    		return Stage.CRASHED;
    	case REACHED: 
    		if (!switchToNextNode()) {
                initDirectly(navigDestination);
            } 
            return keepNavigating();
    	}
    	throw new PogamutException("Unhandled NavigateResult." + result, this);
    }
    
    private enum NavigateResult {
    	REACHED,
    	RUNNING,
    	CRASHED
    }
    
    /**
     * Tries to navigate the agent safely to the 'ilocFirst'.
     */
    private NavigateResult run(ILocated ilocPrevious, ILocated ilocFirst, NavPointNeighbourLink linkFirst, ILocated ilocSecond, ILocated ilocFocus, boolean forceNoJump) {
    	//NavPoint npPrevious = (ilocPrevious instanceof NavPoint ? (NavPoint)ilocPrevious : null);
    	NavPoint npFirst = getNavPoint(ilocFirst);
    	NavPoint npSecond = getNavPoint(ilocSecond);
    	
    	// 1. WRITE DOWN LOCATIONS TO TRAVEL TO    
    	Location locPrevious = ilocPrevious == null ? null : ilocPrevious.getLocation();
    	Location locCurrent = memory.getLocation();
    	Location locFirst = ilocFirst.getLocation();
    	Location locSecond = ilocSecond == null ? null : ilocSecond.getLocation();
    	
    	// 2. DECIDE ON SECOND LOCATION
    	if (npFirst != null) {
    		if (npFirst.isLiftCenter() || npFirst.isTeleporter()) {
    			// do not continue further when reaching LIFT-CENTER or TELEPORT
    			locSecond = null;
    			if (npFirst.isLiftCenter()) {
    				log.fine("Navigator.run(): Reaching LIFT-CENTER");
    			}
    			if (npFirst.isTeleporter()) {
    				log.fine("Navigator.run(): Reaching TELEPORTER");
    			}
    		}
    	}
    	if (npSecond != null) {
    		if (npSecond.isLiftCenter()) {
    			// do not continue on LIFT-CENTER, must be handled specifically with correct timing
    			locSecond = null;
    			log.fine("Navigator.run(): Will NOT continue to LIFT-CENTER");
    		}
    		if (npFirst != null && npFirst.isLiftExit() && npSecond.isLiftExit()) {
    			// do not continue immediately from LIFT-CENTER to LIFT-EXIT as we will need to wait for the mover to arrive to correct position
    			locSecond = null;
    			log.fine("Navigator.run(): Will NOT continue to LIFT-EXIT");
    		}
    	}

    	// 3. COUNT DISTANCES TO THE FIRST LOCATION
        int distToFirst   = (int) locCurrent.getDistance(locFirst);
        int dist2DToFirst = (int) locCurrent.getDistance2D(locFirst);
        int distZToFirst  = (int) Math.abs(locCurrent.getDistanceZ(locFirst));
        
        // 4. DECIDE UPON LOCATION-REACHED CONDITIONS
        double distTest = 50;
        double distTestZ = 115;
        
        if (npFirst != null) {
        	// ARE WE REACHING JUMP PAD?
        	if (npFirst.isJumpPad()) {
	        	// Enlarge the testing distance as the jump pad has a bigger radius because of jump pad area of effect 
	        	distTest = 70;
	        	// Do not care about Z for Jumppads, as agent can be already in air
	        	distZToFirst = 0;
	        	log.fine("Navigator.run(): Reaching JUMP-PAD");
        	} else
        	// ARE WE REACHING LIFT-CENTER?
        	if (npFirst.isLiftCenter()) {
        		// Use more accurate constant for LIFT-CENTER
        		distTest = 30;
        	}        	
        }
        
        // 5. CHECK WHETHER WE HAVE REACHED THE FIRST LOCATION
        if (distToFirst < distTest) { // we are precisely on the location
        	log.fine("Navigator.run(): REACHED " + locFirst + ", dist == " + distToFirst + " < " + distTest + " == test-precision");
        	// => WE HAVE REACHED THE FIRST LOCATION
        	return NavigateResult.REACHED;

        }
        if (distZToFirst < distTestZ && dist2DToFirst < distTest) { // point location is a bit off in Z but we're standing on/below the location
        	log.fine("Navigator.run(): REACHED " + locFirst + ", distZ == " + distZToFirst + " < " + distTestZ + " == test-z-precision, dist2D == " + dist2DToFirst + " < " + distTest + " == test-precision");
        	// => WE HAVE REACHED THE FIRST LOCATION
        	return NavigateResult.REACHED;
        }
        
        // 6. CHECK FOR OPPORTUNISTIC SWITCH
        if (locPrevious != null && locSecond != null) {
        	Location navigDirection = locFirst.sub(locPrevious).getNormalized();
        	Location altDirection = locSecond.sub(locCurrent).getNormalized();
        	double angle = Math.acos(navigDirection.dot(altDirection));
        	
        	double correction = 50;
            double zMin = Math.min(locFirst.z, locSecond.z) - correction;
            double zMax = Math.max(locFirst.z, locSecond.z) + correction;
            boolean isZok = zMin < locCurrent.z && locCurrent.z < zMax;
            
            double distToSecond = locCurrent.getDistance2D(locSecond);
        
            if (angle < Math.PI / 3 && isZok) {
            	if (distToSecond < distTest) {
            		log.log(Level.FINE, "Navigator.run(): REACHED next " + locSecond + " reached, dist == " + distToSecond + " < " + distTest + " == test-precision");
            		return NavigateResult.REACHED;
            	}
            }
        }
        
        // 7. RUN TO THE LOCATION
        if (!runner.runToLocation(memory.getLocation(), locFirst, locSecond, ilocFocus, linkFirst, true, forceNoJump)) {
        	// CRASH!
        	log.log(Level.INFO, "Navigator.run(): Runner CRASHED!");
        	return NavigateResult.CRASHED;
        }
        
        return NavigateResult.RUNNING;
    }
    
    // =========
    // =========
    // UTILITIES
    // =========
    // =========
    
    /**
     * Returns {@link NavPoint} instance for a given location. If there is no
     * navpoint in the vicinity of {@link NavMeshNavigator#CLOSE_ENOUGH} null is
     * returned.
     *
     * @param location
     * @return
     */
    protected NavPoint getNavPoint(ILocated location) {
    	if (location == null) return null;
        if (location instanceof NavPoint) {
            return (NavPoint) location;
        }
        NavPoint np = DistanceUtils.getNearest(main.getWorldView().getAll(NavPoint.class).values(), location);
        if (np.getLocation().getDistance(location.getLocation()) < CLOSE_ENOUGH) {
            return np;
        }
        return null;
    }
    
    /**
     * Gets the link with movement information between two navigation points.
     * Holds information about how we can traverse from the start to the end
     * navigation point.
     *
     * @return NavPointNeighbourLink or null
     */
    private NavPointNeighbourLink getNavPointsLink(NavPoint start, NavPoint end) {
        if (start == null) {
            //if start NavPoint is not provided, we try to find some
            NavPoint tmp = getNavPoint(memory.getLocation());
            if (tmp != null) {
                start = tmp;
            } else {
                return null;
            }
        }
        if (end == null) {
            return null;
        }

        if (end.getIncomingEdges().containsKey(start.getId())) {
            return end.getIncomingEdges().get(start.getId());
        }

        return null;
    }
    

    /*========================================================================*/
    /**
     * Enum of types of terminating navigation stages.
     */
    private enum TerminatingStageType {

        /**
         * Terminating with success.
         */
        SUCCESS(false),
        /**
         * Terminating with failure.
         */
        FAILURE(true);

        /**
         * Whether the terminating with failure.
         */
        public boolean failure;

        /**
         * Constructor.
         *
         * @param failure Whether the terminating with failure.
         */
        private TerminatingStageType(boolean failure) {
            this.failure = failure;
        }
    };

    /**
     * Enum of types of mover navigation stages.
     */
    private enum MoverStageType {

        /**
         * Waiting for mover.
         */
        WAITING,
        /**
         * Riding mover.
         */
        RIDING;
    };

    /**
     * Enum of types of mover navigation stages.
     */
    private enum TeleportStageType {

        /**
         * Next navpoint is a teleport
         */
        GOING_THROUGH;
    };

    /**
     * All stages the navigation can come to.
     */
    public enum Stage {

        /**
         * Running directly to the destination.
         */
        REACHING() {
                    protected Stage next() {
                        return this;
                    }
                },
        /**
         * Navigating along the path.
         */
        NAVIGATING() {
                    protected Stage next() {
                        return this;
                    }
                },
        /**
         * Waiting for a mover to arrive.
         */
        AWAITING_MOVER(MoverStageType.WAITING) {
                    protected Stage next() {
                        return RIDING_MOVER;
                    }
                },
        /**
         * Waiting for a mover to ferry.
         */
        RIDING_MOVER(MoverStageType.RIDING) {
                    protected Stage next() {
                        return NAVIGATING;
                    }
                },
        /**
         * Navigation cancelled by outer force.
         */
        CANCELED(TerminatingStageType.FAILURE) {
                    protected Stage next() {
                        return this;
                    }
                },
        /**
         * Navigation timeout reached.
         */
        TIMEOUT(TerminatingStageType.FAILURE) {
                    protected Stage next() {
                        return this;
                    }
                },
        /**
         * Navigation failed because of troublesome obstacles.
         */
        CRASHED(TerminatingStageType.FAILURE) {
                    protected Stage next() {
                        return this;
                    }
                },
        /**
         * Navigation finished successfully.
         */
        COMPLETED(TerminatingStageType.SUCCESS) {
                    protected Stage next() {
                        return this;
                    }
                },
        /**
         * We're going through the teleport.
         */
        TELEPORT(TeleportStageType.GOING_THROUGH) {
                    protected Stage next() {
                        return NAVIGATING;
                    }
                ;
        };
        

        /*====================================================================*/

        /**
         * Running through the mover.
         */
        private boolean mover;
        /**
         * Whether the nagivation is terminated.
         */
        public boolean terminated;
        /**
         * Whether the navigation has failed.
         */
        public boolean failure;
        /**
         * We're going through the teleport.
         */
        public boolean teleport;

        /*====================================================================*/
        /**
         * Constructor: Not finished, not failed
         */
        private Stage() {
            this.mover = false;
            this.teleport = false;
            this.terminated = false;
            this.failure = false;
        }

        private Stage(TeleportStageType type) {
            this.mover = false;
            this.teleport = true;
            this.failure = false;
            this.terminated = false;
        }

        /**
         * Constructor: mover.
         *
         * @param type Type of mover navigation stage.
         */
        private Stage(MoverStageType type) {
            this.mover = true;
            this.teleport = false;
            this.terminated = false;
            this.failure = false;
        }

        /**
         * Constructor: terminating.
         *
         * @param type Type of terminating navigation stage.
         */
        private Stage(TerminatingStageType type) {
            this.mover = false;
            this.teleport = false;
            this.terminated = true;
            this.failure = type.failure;
        }

        /*====================================================================*/
        /**
         * Retreives the next step of navigation sequence the stage belongs to.
         *
         * @return The next step of navigation sequence. Note: Some stages are
         * not part of any logical navigation sequence. In such cases, this
         * method simply returns the same stage.
         */
        protected abstract Stage next();

        /*====================================================================*/
        /**
         * Returns the first step of mover sequence.
         *
         * @return The first step of mover sequence.
         */
        protected static Stage FirstMoverStage() {
            return AWAITING_MOVER;
        }

        /**
         * Returns the first step of the teleporter sequence.
         *
         * @return
         */
        protected static Stage TeleporterStage() {
            return Stage.TELEPORT;
        }
    }

    /*========================================================================*/
    /**
     * Default: Loque Runner.
     */
    private IUT2004PathRunner runner;

    /*========================================================================*/
    /**
     * Agent's main.
     */
    protected UT2004Bot main;
    /**
     * Loque memory.
     */
    protected AgentInfo memory;
    /**
     * Agent's body.
     */
    protected AdvancedLocomotion body;
    /**
     * Agent's log.
     */
    protected Logger log;


    /**
     * Constructor.
     *
     * @param bot
     * @param move
     * @param info
     * @param runner
     * @param log
     */
    public NavMeshNavigator(UT2004Bot bot, AgentInfo info, AdvancedLocomotion move, IUT2004PathRunner runner, Logger log) {
        // setup reference to agent
        this.main = bot;
        this.memory = info;
        this.body = move;
        this.log = log;

        this.selfListener = new SelfListener(bot.getWorldView());

        // save runner object
        this.runner = runner;
        NullCheck.check(this.runner, "runner");
    }

    @Override
    public Logger getLog() {
        return log;
    }

}
