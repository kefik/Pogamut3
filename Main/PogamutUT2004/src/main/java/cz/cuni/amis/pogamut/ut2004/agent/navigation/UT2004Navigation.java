package cz.cuni.amis.pogamut.ut2004.agent.navigation;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.agent.navigation.IPathExecutorState;
import cz.cuni.amis.pogamut.base.agent.navigation.IPathFuture;
import cz.cuni.amis.pogamut.base.agent.navigation.IPathPlanner;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.pogamut.base.utils.math.DistanceUtils;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.AgentInfo;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.floydwarshall.FloydWarshallMap;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.loquenavigator.LoqueNavigator;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.pathfollowing.UT2004AcceleratedPathExecutor;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.stuckdetector.AccUT2004DistanceStuckDetector;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.stuckdetector.AccUT2004PositionStuckDetector;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.stuckdetector.AccUT2004TimeStuckDetector;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.stuckdetector.UT2004DistanceStuckDetector;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.stuckdetector.UT2004PositionStuckDetector;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.stuckdetector.UT2004TimeStuckDetector;
import cz.cuni.amis.pogamut.ut2004.bot.command.AdvancedLocomotion;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Stop;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BotKilled;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.EndMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.cuni.amis.utils.flag.Flag;
import cz.cuni.amis.utils.flag.FlagListener;


/**
 * Facade for navigation in UT2004. Method navigate() can be called both synchronously and asynchronously.
 * 
 * Uses {@link IUT2004PathExecutor}, {@link FloydWarshallMap}, {@link IUT2004RunStraight} and {@link IUT2004GetBackToNavGraph}
 * to handle all possible navigation cases.
 * 
 * @author knight
 * @author jimmy
 */
public class UT2004Navigation implements IUT2004Navigation {

	public static double EXTEND_PATH_THRESHOLD = 500;
	
	/** Log used by this class. */
	protected LogCategory log;
    /** UT2004PathExecutor that is used for the navigation. */
    protected IUT2004PathExecutor<ILocated> pathExecutor;
    /** FloydWarshallMap that is used for path planning. */
    protected IPathPlanner<NavPoint> pathPlanner;    
    /** UT2004Bot reference. */
    protected UT2004Bot bot;
    /** UT2004GetBackToNavGraph for returning bot back to the navigation graph. */
    protected IUT2004GetBackToNavGraph getBackToNavGraph;
    /** UT2004RunStraight is used to run directly to player at some moment. */
    protected IUT2004RunStraight runStraight;
    /** From which distance we should use {@link IUT2004PathExecutor#extendPath(List)}. */
    protected double extendPathThreshold;
    /** Location threshold for requesting of a new path or switching a path. */
    protected static final int NEW_PATH_DISTANCE_THRESHOLD = 40;
    /** Location threshold for checking whether we have arrived on target. For XY - 2D plane distance */
    protected static final int ARRIVED_AT_LOCATION_XY_THRESHOLD = 50;
    /** Location threshold for checking whether we have arrived on target. For Z distance. */
    protected static final int ARRIVED_AT_LOCATION_Z_THRESHOLD = 100;
    /** When PLAYER is further from currentTarget than this location, recompute the path */
	protected static final double PLAYER_DISTANCE_TRASHOLD = 600;
	/** We're managed to get to player */
	public static final double AT_PLAYER = 100;
    /** State of UT2004Navigation */
    protected Flag<NavigationState> state = new Flag<NavigationState>(NavigationState.STOPPED); 

    /**
     * Listener for UT2004PathExecutor state.
     */
    FlagListener<IPathExecutorState> myUT2004PathExecutorStateListener = new FlagListener<IPathExecutorState>() {

        @Override
        public void flagChanged(IPathExecutorState changedValue) {
            switch (changedValue.getState()) {                
                case TARGET_REACHED:
                	targetReached();
                    break;
                case PATH_COMPUTATION_FAILED:
                	noPath();
                	break;
                case STUCK:
                	if (log != null && log.isLoggable(Level.WARNING)) log.warning("UT2004Navigation:stuck(). Path executor reported stuck!");
                	stuck();
                    break;
            }
        }
    };
    
    protected IWorldEventListener<EndMessage> endMessageListener = new IWorldEventListener<EndMessage>() {		
		@Override
		public void notify(EndMessage event) {
			navigate();
		}
	};
	
	protected IWorldEventListener<BotKilled> botKilledMessageListener = new IWorldEventListener<BotKilled>() {		
		@Override
		public void notify(BotKilled event) {
			 reset(true, NavigationState.STOPPED);
		}
	};	

    // ===========
    // CONSTRUCTOR
    // ===========
    
	
	/**
     * Here you may specify any custom UT2004Navigation parts.
     * 
     * @param bot
     * @param ut2004PathExecutor
     * @param pathPlanner
     * @param getBackOnPath
     * @param runStraight 
     */
    public UT2004Navigation(UT2004Bot bot, IUT2004PathExecutor ut2004PathExecutor, IPathPlanner<NavPoint> pathPlanner, IUT2004GetBackToNavGraph getBackOnPath, IUT2004RunStraight runStraight) {
    	this(bot, ut2004PathExecutor, pathPlanner, getBackOnPath, runStraight, EXTEND_PATH_THRESHOLD);
    }
    
    /**
     * Here you may specify any custom UT2004Navigation parts.
     * 
     * @param bot
     * @param ut2004PathExecutor
     * @param pathPlanner
     * @param getBackOnPath
     * @param runStraight 
     */
    public UT2004Navigation(UT2004Bot bot, IUT2004PathExecutor ut2004PathExecutor, IPathPlanner<NavPoint> pathPlanner, IUT2004GetBackToNavGraph getBackOnPath, IUT2004RunStraight runStraight, double extendPathThreshold) {
        this.log = bot.getLogger().getCategory(this.getClass().getSimpleName());
    	this.bot = bot;
    	
    	this.pathPlanner = pathPlanner;
        this.pathExecutor = ut2004PathExecutor;
        
        this.getBackToNavGraph = getBackOnPath;
        this.runStraight = runStraight;
        
        this.extendPathThreshold = extendPathThreshold;

        initListeners();
    }
    
    /**
     * Will auto-create all needed UT2004Navigation subparts.
     * @param bot
     * @param info
     * @param move
     */
	public UT2004Navigation(UT2004Bot bot, AgentInfo info, AdvancedLocomotion move) {
    	this.log = bot.getLogger().getCategory(this.getClass().getSimpleName());
     	this.bot = bot;    	
    	this.pathPlanner = new FloydWarshallMap(bot);
    	
		this.pathExecutor = 
        	new UT2004AcceleratedPathExecutor<ILocated>(
        		bot, info, move,
        		new LoqueNavigator<ILocated>(bot,info, move, bot.getLog())
        	);
		
		// add stuck detectors that watch over the path-following, if it (heuristicly) finds out that the bot has stuck somewhere,
    	// it reports an appropriate path event and the path executor will stop following the path which in turn allows 
    	// us to issue another follow-path command in the right time
        this.pathExecutor.addStuckDetector(new AccUT2004TimeStuckDetector(bot, 3000, 100000)); // if the bot does not move for 3 seconds, considered that it is stuck
        this.pathExecutor.addStuckDetector(new AccUT2004PositionStuckDetector(bot));           // watch over the position history of the bot, if the bot does not move sufficiently enough, consider that it is stuck
        this.pathExecutor.addStuckDetector(new AccUT2004DistanceStuckDetector(bot));           // watch over distances to target
        
        this.getBackToNavGraph = new UT2004GetBackToNavGraph(bot, info, move);
        this.runStraight       = new UT2004RunStraight(bot, info, move);
        
        initListeners();
	}
	
	@Override
	public Logger getLog() {
		return log;
	}
	
	@Override
	public void setLogLevel(Level level) {
		getLog().setLevel(level);
		pathExecutor.getLog().setLevel(level);
		getBackToNavGraph.getLog().setLevel(level);
		runStraight.getLog().setLevel(level);
	}

    private void initListeners() {
    	this.pathExecutor.getState().addListener(myUT2004PathExecutorStateListener);        
        bot.getWorldView().addEventListener(EndMessage.class, endMessageListener);
        bot.getWorldView().addEventListener(BotKilled.class, botKilledMessageListener);
	}
	
    // ============================
    // TWEAKING / LISTENERS
    // ============================

    @Override
    public void addStrongNavigationListener(FlagListener<NavigationState> listener) {
        state.addStrongListener(listener);
    }

    @Override
    public void removeStrongNavigationListener(FlagListener<NavigationState> listener) {
        state.removeListener(listener);
    }

    @Override
	public IUT2004PathExecutor<ILocated> getPathExecutor() {
		return pathExecutor;
	}
    
    @Override
	public IPathPlanner<ILocated> getPathPlanner() {
		return (IPathPlanner<ILocated>)(IPathPlanner)pathPlanner;
	}
    
    @Override
	public IUT2004GetBackToNavGraph getBackToNavGraph() {
		return getBackToNavGraph;
	}

    @Override
	public IUT2004RunStraight getRunStraight() {
		return runStraight;
	}
    
	// ======================
    // PUBLIC INTERFACE
    // ======================
        
    @Override
    public boolean isNavigating() {
        return navigating;
    }
    
    @Override
    public boolean isNavigatingToNavPoint() {
    	return isNavigating() && getCurrentTarget() instanceof NavPoint;
    }
    
    @Override
    public boolean isNavigatingToItem() {
    	return isNavigating() && getCurrentTarget() instanceof Item;
    }
    
    @Override
    public boolean isNavigatingToPlayer() {
    	return isNavigating() && getCurrentTarget() instanceof Player;
    }
    
    @Override
    public boolean isTryingToGetBackToNav() {
    	return getBackToNavGraph.isExecuting();
    }
    
    @Override
    public boolean isPathExecuting() {
    	return pathExecutor.isExecuting();
    }

    @Override
    public boolean isRunningStraight() {
    	return runStraight.isExecuting();
    }
    
    @Override
    public ILocated getFocus() {
        return pathExecutor.getFocus();
    }
    
    @Override
    public void setFocus(ILocated located) {
        pathExecutor.setFocus(located);
        getBackToNavGraph.setFocus(located);
        runStraight.setFocus(located);
    }

    @Override
    public void stopNavigation() {
        reset(true, NavigationState.STOPPED);
        bot.getAct().act(new Stop());
    }
    
    @Override
    public void navigate(ILocated target) {
    	if (target == null) {
    		if (log != null && log.isLoggable(Level.WARNING)) log.warning("Cannot navigate to NULL target!");
    		reset(true, NavigationState.STOPPED);
    		return;
    	}
    	
    	if (target instanceof Player) {
    		// USE DIFFERENT METHOD INSTEAD
    		navigate((Player)target);
    		return;
    	}
    	
    	if (navigating) {
    		if (currentTarget == target || currentTarget.getLocation().equals(target.getLocation())) {
    			// just continue with current execution
    			return;
    		}    		
    		// NEW TARGET!
    		// => reset - stops pathExecutor as well, BUT DO NOT STOP getBackOnPath (we will need to do that eventually if needed, or it is not running)
    		reset(false, null);
    	}
    	
    	if (log != null && log.isLoggable(Level.FINE)) log.fine("Start navigating to: " + target);
    	
    	navigating = true;
    	switchState(NavigationState.NAVIGATING);
    	
    	currentTarget = target;
    	
    	navigate();
    }
    
    @Override
    public void navigate(Player player) {
    	if (player == null) {
    		if (log != null && log.isLoggable(Level.WARNING)) log.warning("Cannot navigate to NULL player!");
    		return;
    	}
    	
    	if (navigating) {
    		if (currentTarget == player) {
    			// just continue with the execution
    			return;
    		}    		
    		// NEW TARGET!
    		// => reset - stops pathExecutor as well, BUT DO NOT STOP getBackOnPath (we will need to do that eventually if needed, or it is not running)
    		reset(false, null);
    	}
    	
    	if (log != null && log.isLoggable(Level.FINE)) log.fine("Start pursuing: " + player);
    	
    	navigating = true;
    	switchState(NavigationState.NAVIGATING);
    	
    	// Current target and currentTarget player should refer to the same object.
    	// Current target is used by navigatePlayer to compute new destination and 
    	// by this method to see if the taret has changed.
    	currentTarget = player;
    	currentTargetPlayer = player;
    	
    	navigate();
    }
    
	@Override
	public void navigate(IPathFuture<ILocated> pathHandle) {
		if (pathHandle == null) {
    		if (log != null && log.isLoggable(Level.WARNING)) log.warning("Cannot navigate to NULL pathHandle!");
    		return;
    	}
    	
    	if (navigating) {
    		if (currentPathFuture == pathHandle) {
    			// just continue with the execution
    			return;
    		}    		
    		// NEW TARGET!
    		// => reset - stops pathExecutor as well, BUT DO NOT STOP getBackOnPath (we will need to do that eventually if needed, or it is not running)
    		reset(false, null);
    	}
    	
    	if (log != null && log.isLoggable(Level.FINE)) log.fine("Start running along the path to target: " + pathHandle.getPathTo());
    	
    	navigating = true;
    	switchState(NavigationState.NAVIGATING);
    	
    	currentTarget = pathHandle.getPathTo();
    	currentPathFuture = pathHandle;
    	
    	navigate();
	}

	@Override
	public void navigate(IPathFuture<ILocated> pathHandle, boolean smooth) {
		// NO SMOOTHING
		if (smooth && log != null && log.isLoggable(Level.WARNING)) log.warning("NavGraph navigation cannot smooth paths.");
		navigate(pathHandle);
	}
	
    
    @Override
    public NavPoint getNearestNavPoint(ILocated location) {
    	if (location == null) return null;
    	if (location instanceof NavPoint) return (NavPoint)location;
    	if (location instanceof Item) {
    		if (((Item)location).getNavPoint() != null) return ((Item)location).getNavPoint();
    	}
    	return DistanceUtils.getNearest(bot.getWorldView().getAll(NavPoint.class).values(), location);        
    }
    
    public ILocated getContinueTo() {
    	return continueTo;
    }
    
    public void setContinueTo(ILocated continueTo) {
    	if (!isNavigating()) {
    		log.warning("Cannot continueTo(" + continueTo + ") as navigation is not navigating!");
    		return;
    	}
    	if (isNavigatingToPlayer()) {
    		log.warning("Cannot continueTo(" + continueTo + ") as we're navigating to player!");
    		return;
    	}
    	this.continueTo = continueTo;
    	
    	NavPoint from = getNearestNavPoint(currentTarget);
    	NavPoint to = getNearestNavPoint(continueTo);
    	
    	this.continueToPath = pathPlanner.computePath(from, to);
    	
    	checkExtendPath();
    }
        
	@Override
    public List<ILocated> getCurrentPathCopy() {
        List<ILocated> result = new ArrayList();
        if (currentPathFuture != null) {
            result.addAll(currentPathFuture.get());
        }
        return result;
    }

    @Override
    public List<ILocated> getCurrentPathDirect() {
        if (currentPathFuture != null) {
            return currentPathFuture.get();
        }
        return null;
    }
    
    @Override
    public ILocated getCurrentTarget() {
    	return currentTarget;
    }
    
    @Override
    public Player getCurrentTargetPlayer() {
    	return currentTargetPlayer;
    }
    
    @Override
    public Item getCurrentTargetItem() {
    	if (currentTarget instanceof Item) return (Item) currentTarget;
    	return null;
    }
    
    @Override
    public NavPoint getCurrentTargetNavPoint() {
    	if (currentTarget instanceof NavPoint) return (NavPoint) currentTarget;
    	return null;
    }

    
    @Override
    public ILocated getLastTarget() {
    	return lastTarget;
    }
    
    @Override
    public Player getLastTargetPlayer() {
    	return lastTargetPlayer;
    }
    
    @Override
    public Item getLastTargetItem() {
    	if (lastTarget instanceof Item) return (Item)lastTarget;
    	return null;
    }
    
    @Override
    public double getRemainingDistance() {
    	if (!isNavigating()) return 0;
    	if (isNavigatingToPlayer()) {
    		if (isPathExecuting()) {
    			return pathExecutor.getRemainingDistance() + pathExecutor.getPathTo().getLocation().getDistance(currentTargetPlayer.getLocation());
    		} else {
    			// TODO: HOW TO GET TRUE DISTANCE, IF YOU MAY HAVE ASYNC PATH-PLANNER?
    			NavPoint from = getNearestNavPoint(bot.getLocation());
    			NavPoint to = getNearestNavPoint(currentTargetPlayer.getLocation());
    			IPathFuture<NavPoint> pathFuture = pathPlanner.computePath(from, to);
    			if (pathFuture.isDone()) {
    				return bot.getLocation().getDistance(from.getLocation()) + getPathDistance(pathFuture.get()) + to.getLocation().getDistance(currentTargetPlayer.getLocation());
    			} else {
    				// CANNOT BE COMPUTED DUE TO ASYNC PATH-PLANNER
    				return -1;
    			}
    		}
    	} else {
    		if (isPathExecuting()) {
    			return pathExecutor.getRemainingDistance();
    		} else {
    			// TODO: HOW TO GET TRUE DISTANCE, IF YOU MAY HAVE ASYNC PATH-PLANNER?
    			NavPoint from = getNearestNavPoint(bot.getLocation());
    			NavPoint to = getNearestNavPoint(currentTarget.getLocation());
    			IPathFuture<NavPoint> pathFuture = pathPlanner.computePath(from, to);
    			if (pathFuture.isDone()) {    				
    				return bot.getLocation().getDistance(from.getLocation()) + getPathDistance(pathFuture.get()) + to.getLocation().getDistance(currentTarget.getLocation());
    			} else {
    				// CANNOT BE COMPUTED DUE TO ASYNC PATH-PLANNER
    				return -1;
    			}
    		}
    	}
    }

    /**
     * Careful here - although the input is List&lt;NavPoint&gt;, there may be location added to the end
     * of the list when no close navpoint to final destination exists. Just treat everything in the list
     * as ILocated and you should be fine. 
     * */
    private double getPathDistance(List list) {
		if (list == null || list.size() <= 0) return 0;
    	double distance = 0;
    	ILocated curr = (ILocated) list.get(0);
    	for (int i = 1; i < list.size(); ++i) {
    		ILocated next = (ILocated) list.get(i);
    		distance += curr.getLocation().getDistance(next.getLocation());
    		curr = next;
    	}		
		return distance;
	}    
    

	// ======================
    // VARIABLES
    // ======================
    
    /** Last location target. */
    protected ILocated lastTarget = null;
    /** Last location target. */
    protected Player   lastTargetPlayer = null;
    /** Current location target. */
    protected ILocated currentTarget = null;
    /** Current target is player (if not null) */
    protected Player   currentTargetPlayer = null;
    /** Navpoint we're running from (initial position when path executor has been triggered) */
    protected NavPoint fromNavPoint;
    /** Navpoint we're running to, nearest navpoint to currentTarget */
	protected NavPoint toNavPoint;    
    /** Current path stored in IPathFuture object. */
    protected IPathFuture currentPathFuture;
    /** Whether navigation is running. */
    protected boolean navigating = false;
    /** We're running straight to the player. */
	protected boolean runningStraightToPlayer = false;
	/** Where run-straight failed. */
	protected Location runningStraightToPlayerFailedAt = null;
	/** Whether we're using {@link UT2004Navigation#getBackToNavGraph}. */
	protected boolean usingGetBackToNavGraph = false;	
	/** Where the bot will continue to. */
	protected ILocated continueTo;
	/** Path to prolong. */
	protected IPathFuture<NavPoint> continueToPath;
	
    // ======================
    // UTILITY METHODS
    // ======================
    
    protected void navigate() {
		if (!navigating) return;
		
		if (log != null && log.isLoggable(Level.FINE)) {
			log.fine("NAVIGATING");
		}
		if (currentTargetPlayer != null) {
			if (log != null && log.isLoggable(Level.FINE)) log.fine("Pursuing " + currentTargetPlayer);
			navigatePlayer();
		} else {
			if (log != null && log.isLoggable(Level.FINE)) log.fine("Navigating to " + currentTarget);
			navigateLocation();
		}
	}
    
    private void navigateLocation() {
    	if (isPathExecuting()) {
			// Navigation is driven by Path Executor already...	
    		// => check continueTo
    		checkExtendPath();
    		if (log != null && log.isLoggable(Level.FINE)) log.fine("Path executor running");			
			return;
		}
		
		// PATH EXECUTOR IS NOT RUNNING
		// => we have not started to run along path yet

		// ARE WE ON NAV-GRAPH?
		if (!getBackToNavGraph.isOnNavGraph()) {
			// NO!
			// => get back to navigation graph
			if (log != null && log.isLoggable(Level.FINE)) log.fine("Getting back to navigation graph");
    		if (getBackToNavGraph.isExecuting()) {
    			// already running
    			return;
    		}
    		if (usingGetBackToNavGraph) {
    			// GetBackToNavGraph was already called && stopped && we're still not on nav graph
    			// => stuck
    			if (log != null && log.isLoggable(Level.WARNING)) log.warning("UT2004Navigation:stuck(). GetBackToNavGraph was already called && stopped && we're still not on nav graph.");
    			stuck();
    			return;
    		}
    		getBackToNavGraph.backToNavGraph();
    		// => mark that we're using GetBackToNavGraph
    		usingGetBackToNavGraph = true;			
    		return;
    	} else {
    		usingGetBackToNavGraph = false;
    	}
		// YES!    	
    	// ... getBackToNavGraph will auto-terminate itself when we manage to get back to graph
    	
		if (currentPathFuture == null) {
			fromNavPoint = getNearestNavPoint(bot.getLocation());
			toNavPoint   = getNearestNavPoint(currentTarget);
    	
			if (log != null && log.isLoggable(Level.FINE)) log.fine("Computing path from " + fromNavPoint.getId().getStringId() + " to " + toNavPoint.getId().getStringId());
    	
			currentPathFuture = pathPlanner.computePath(fromNavPoint, toNavPoint);
		}
		
		switch(currentPathFuture.getStatus()) {
		case FUTURE_IS_READY:
			// ALL OK!
			break;
		case FUTURE_IS_BEING_COMPUTED:
			if (log != null && log.isLoggable(Level.FINE)) log.fine("Waiting for the path to be computed...");
			return;
		case CANCELED:
			if (log != null && log.isLoggable(Level.WARNING)) log.warning("Path computation has been canceled.");
			noPath();
			return;
		case COMPUTATION_EXCEPTION:
			if (log != null && log.isLoggable(Level.WARNING)) log.warning("Path computation has failed with an exception.");
			noPath();
			return;		
		}
    	
		// PATH IS READY!
		// => tinker the path
    	if (!processPathFuture(currentPathFuture, currentTarget)) {
    		noPath();
    		return;
    	}
    	// => let's start running
    	pathExecutor.followPath(currentPathFuture);	
	}
    
    private void checkExtendPath() {
    	if (continueTo == null) return;
    	if (continueToPath == null) {
    		log.severe("continueTo specified, but continueToPath is NULL!");
    		return;
    	}
    	if (isNavigatingToPlayer()) {
    		log.warning("continueTo specified, but navigating to Player, INVALID!");
    		return;
    	}
    	if (isPathExecuting()) {
    		double remainingDistance = getRemainingDistance();
    		if (remainingDistance < extendPathThreshold) {
    			if (!continueToPath.isDone()) {
    				log.warning("Should extend path, remainingDistance = " + remainingDistance + " < " + extendPathThreshold + " = extendPathThreshold, but continueToPath.isDone() == false, cannot extend path!");
    				return;
    			}
    			log.info("Extending path to continue to " + continueTo);
    			
    			pathExecutor.extendPath(((List)continueToPath.get()));
    			
    			// ADJUST INTERNALS
    			currentPathFuture = pathExecutor.getPathFuture();
    			lastTarget = currentTarget;
    			currentTarget = continueTo;    			
    			fromNavPoint = getNearestNavPoint(((IPathFuture<ILocated>)currentPathFuture).get().get(0).getLocation());
    			toNavPoint = getNearestNavPoint(((IPathFuture<ILocated>)currentPathFuture).get().get(currentPathFuture.get().size()-1).getLocation());    			
    			
    			continueTo = null;
    			continueToPath = null;
    		}
    	}
    	
	}


	private void navigatePlayer() {
		double vDistance = bot.getLocation().getDistanceZ(currentTargetPlayer.getLocation());
		double hDistance = bot.getLocation().getDistance2D(currentTargetPlayer.getLocation());
		
		if (hDistance < AT_PLAYER && vDistance < 50) {
			// player reached
			if (log != null && log.isLoggable(Level.FINE)) log.fine("Player reached");	
			if (pathExecutor.isExecuting()) {
				pathExecutor.getPath().set(pathExecutor.getPath().size()-1, bot.getLocation());
			} else {
				targetReached();
			}
			return;
		}
		
		if (hDistance < 400 && Math.abs(vDistance) < 50) {
			// RUN STRAIGHT			
			if (runningStraightToPlayer) {
				if (runStraight.isFailed()) {
					runningStraightToPlayer = false;
					runningStraightToPlayerFailedAt = bot.getLocation();
				}
			} else {
				if (runningStraightToPlayerFailedAt == null ||                           // we have not failed previously
					bot.getLocation().getDistance(runningStraightToPlayerFailedAt) > 500 // or place where we have failed is too distant
				) {
					if (getBackToNavGraph.isExecuting()) {
						getBackToNavGraph.stop();
						usingGetBackToNavGraph = false;
					}
					if (pathExecutor.isExecuting()) {
						pathExecutor.stop();
					}
					runningStraightToPlayer = true;
					runningStraightToPlayerFailedAt = null;
					runStraight.runStraight(currentTargetPlayer);
				}				
			}
			if (runningStraightToPlayer) {
				if (log != null && log.isLoggable(Level.FINE)) log.fine("Running straight to player");
				return;
			}
		} else {
			if (runningStraightToPlayer) {
				runningStraightToPlayer = false;
				runStraight.stop(false);				
			}
		}
		
		if (pathExecutor.isExecuting()) {
			// Navigation is driven by Path Executor already...			
			if (log != null && log.isLoggable(Level.FINE)) log.fine("Path executor running");
			// check distance between point we're navigating to and current player's location
			double distance = currentTarget.getLocation().getDistance(currentTargetPlayer.getLocation());
			
			if (distance < PLAYER_DISTANCE_TRASHOLD) {
				// PLAYER DID NOT MOVED TOO MUCH FROM ITS ORIGINAL POSITION
				// => continue running using pathExecutor
				return;
			}
			
			if (log != null && log.isLoggable(Level.FINE)) log.fine("Player moved " + distance + " from its original location, checking path...");
			// WE NEED TO CHECK ON PATH!					
			NavPoint newToNavPoint = getNearestNavPoint(currentTargetPlayer);
			if (newToNavPoint != toNavPoint) {
				// WE NEED TO ALTER THE PATH!
				if (log != null && log.isLoggable(Level.FINE)) log.fine("Replanning path to get to " + currentTargetPlayer);					
				pathExecutor.stop();
				currentPathFuture = null;
			} else {
				if (log != null && log.isLoggable(Level.FINE)) log.fine("Path remains the same");
				return;
			}
		}
		
		// PATH EXECUTOR IS NOT RUNNING
		// => we have not started to run along path yet

		// ARE WE ON NAV-GRAPH?
		
		if (!getBackToNavGraph.isOnNavGraph()) {
			// NO!
			// => get back to navigation graph
			if (log != null && log.isLoggable(Level.FINE)) log.fine("Getting back to navigation graph");
			if (getBackToNavGraph.isExecuting()) {
				// nothing to see go along
				return;
			}
			if (usingGetBackToNavGraph) {
    			// GetBackToNavGraph was already called && stopped && we're still not on nav graph
    			// => stuck
				if (log != null && log.isLoggable(Level.WARNING)) log.warning("UT2004Navigation:stuck(). GetBackToNavGraph was already called && stopped && we're still not on nav graph.");
    			stuck();
    			return;
    		}
    		getBackToNavGraph.backToNavGraph();
    		// => mark that we're using GetBackToNavGraph
    		usingGetBackToNavGraph = true;			
    		return;
    	} else {
    		usingGetBackToNavGraph = false;
    	}
		// YES, WE'RE ON NAV-GRAPH!  	
    	// ... getBackToNavGraph will auto-terminate itself when we manage to get back to graph
    	
		if (currentPathFuture == null) {
			fromNavPoint = getNearestNavPoint(bot.getLocation());
			toNavPoint   = getNearestNavPoint(currentTarget);
    	
			if (log != null && log.isLoggable(Level.FINE)) log.fine("Computing path from " + fromNavPoint.getId().getStringId() + " to " + toNavPoint.getId().getStringId());
    	
			currentPathFuture = pathPlanner.computePath(fromNavPoint, toNavPoint);
		}
		
		switch(currentPathFuture.getStatus()) {
		case FUTURE_IS_READY:
			// ALL OK!
			break;
		case FUTURE_IS_BEING_COMPUTED:
			if (log != null && log.isLoggable(Level.FINE)) log.fine("Waiting for the path to be computed...");
			return;
		case CANCELED:
			if (log != null && log.isLoggable(Level.WARNING)) log.warning("Path computation has been canceled.");
			noPath();
			return;
		case COMPUTATION_EXCEPTION:
			if (log != null && log.isLoggable(Level.WARNING)) log.warning("Path computation has failed with an exception.");
			noPath();
			return;		
		}
		
		// PATH IS READY!
    	// => tinker the path
		if (!processPathFuture(currentPathFuture, currentTarget)) {
    		noPath();
    		return;
    	}
    	// => let's start running
    	pathExecutor.followPath(currentPathFuture);	
	}

	/**
     * Checks if last path element is in close distance from our desired target and if not, we
     * will add our desired target as the last path element.
     * @param futurePath
     */
    protected boolean processPathFuture(IPathFuture futurePath, ILocated currentTarget) {
        List<ILocated> pathList = futurePath.get();
        
        if (pathList == null) {
        	// we failed to compute the path, e.g., path does not exist
        	return false;
        }

        if (currentTarget == null) {
        	if (pathList.size() == 0) return false;
        	currentTarget = pathList.get(pathList.size()-1);
        } else
        if (pathList.size() == 0) {
        	currentPathFuture.get().add(currentTarget);
        } else {
            ILocated lastPathElement = pathList.get(pathList.size() - 1);
            if (lastPathElement.getLocation().getDistance(currentTarget.getLocation()) > NEW_PATH_DISTANCE_THRESHOLD) {
                currentPathFuture.get().add(currentTarget);
            }
        }
        return true;
    }
    
    protected void switchState(NavigationState newState) {
    	state.setFlag(newState);    	    	
    }
    
    protected void noPath() {
		// DAMN ...
		reset(true, NavigationState.PATH_COMPUTATION_FAILED);			
	}

        
    protected void stuck() {
    	// DAMN ...
    	reset(true, NavigationState.STUCK);
	}

	protected void targetReached() {
		// COOL !!!
		reset(true, NavigationState.TARGET_REACHED);
	}
    
    protected void reset(boolean stopGetBackToNavGraph, NavigationState resultState) {    	
    	if (currentTarget != null) {
    		lastTarget = currentTarget;
    		lastTargetPlayer = currentTargetPlayer;    			
    	}
    	
    	navigating = false;
    	
    	currentTarget = null;
    	currentTargetPlayer = null;
    	
    	fromNavPoint = null;
    	toNavPoint = null;
    	
    	currentPathFuture = null;
    	
    	runningStraightToPlayer = false;
    	runningStraightToPlayerFailedAt = null;
    	
    	continueTo = null;
    	continueToPath = null;
    	
    	pathExecutor.stop();
    	runStraight.stop(false);
    	if (stopGetBackToNavGraph) {
    		getBackToNavGraph.stop();
    		usingGetBackToNavGraph = false;
    	}
    	
    	
    	if (resultState == null) return;
    	switchState(resultState);    		
    }

	@Override
	public Flag<NavigationState> getState() {
		return state.getImmutable();
	}
    
}
