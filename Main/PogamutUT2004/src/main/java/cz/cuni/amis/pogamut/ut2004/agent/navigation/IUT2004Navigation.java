package cz.cuni.amis.pogamut.ut2004.agent.navigation;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.agent.navigation.IPathFuture;
import cz.cuni.amis.pogamut.base.agent.navigation.IPathPlanner;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.floydwarshall.FloydWarshallMap;
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
 * @author Jimmy
 */
public interface IUT2004Navigation {
	
    /**
     * Use this to register listeners to various states the navigation - stuck, target reached, etc.
     * See {@link NavigationState}.
     * 
     * @param listener
     */
    public void addStrongNavigationListener(FlagListener<NavigationState> listener);

    /**
     * Removes path state listener.
     * @param listener
     */
    public void removeStrongNavigationListener(FlagListener<NavigationState> listener);

    /**
     * Returns underlying {@link IUT2004PathExecutor} object that is being used by this {@link IUT2004Navigation}.
     * @return
     */
	public IUT2004PathExecutor<ILocated> getPathExecutor();
	
	/**
	 * Returns underlaying {@link IPathPlanner} object that is being used by this {@link IUT2004Navigation}.
	 * @return
	 */
	public IPathPlanner<ILocated> getPathPlanner();

	/**
     * Returns underlying {@link IUT2004GetBackToNavGraph} object that is being used by this {@link IUT2004Navigation}.
     * @return
     */
	public IUT2004GetBackToNavGraph getBackToNavGraph();

	/**
     * Returns underlying {@link IUT2004RunStraight} object that is being used by this {@link IUT2004Navigation}.
     * @return
     */
	public IUT2004RunStraight getRunStraight();
    
	/**
     * True if navigating, e.g., trying to get somewhere using either {@link IUT2004PathExecutor}, {@link IUT2004GetBackToNavGraph} or {@link IUT2004RunStraight}.
     * @return
     */
    public boolean isNavigating();
    
    /**
     * Whether we're currently navigating to navpoint (final target).
     * @return
     */
    public boolean isNavigatingToNavPoint();
    
    /**
     * Whether we're currently navigating to item (final target).
     * @return
     */
    public boolean isNavigatingToItem();
    
    /**
     * Whether we're currently navigating to player (final target). 
     * @return
     */
    public boolean isNavigatingToPlayer();
    
    /**
     * Whether {@link UT2004Navigation} is currently trying to get back to nav using {@link IUT2004GetBackToNavGraph}.
     * @return
     */
    public boolean isTryingToGetBackToNav();
    
    /**
     * Whether {@link UT2004Navigation} is currently using {@link IUT2004PathExecutor} to follow the path.
     * @return
     */
    public boolean isPathExecuting();

    /**
     * Whether {@link UT2004Navigation} is currently using {@link UT2004Navigation#runStraight} to get to player by running straight to it/him/her.
     * @return
     */
    public boolean isRunningStraight();
    
    /**
     * Returns current focus of the bots, may be null (== no focus).
     * @param located
     */
    public ILocated getFocus();
    
    /**
     * Sets focus of the bot when navigating (when using this object to run to some location target)!
     * To reset focus call this method with null parameter.
     * @param located
     */
    public void setFocus(ILocated located);

    /**
     * Stops navigation and resets the class.
     * 
     * Does NOT reset focus!
     */
    public void stopNavigation();
    
    /**
     * This method can be called periodically or asynchronously. Will move bot to 'target'.
     *
     * The bot will stop on bad input (location == null).
     * 
     * @param target target location
     */
    public void navigate(ILocated target);
    
    /**
     * This method can be called periodically or asynchronously. Will move bot to input location.
     * Uses UT2004PathExecutor and FloydWarshallMap.
     * The bot will stop on bad input (location null).
     * @param location target location
     */
    public void navigate(Player player);
    
    /**
     * Let the bot to follow this path.
     * @param pathHandle
     */
    public void navigate(IPathFuture<ILocated> pathHandle);
    
    /**
     * When the bot is about to reach its target, it will prolong his path to continue to 'target'.
     * 
     * DOES NOT WORK WITH {@link IUT2004Navigation#navigate(Player)}.
     * 
     * WARNING: continueTo is reset when bot stop navigating / stuck, etc.
     * 
     * WARNING: continueTo is also "nullified" when the bot actually prolongs its path to reach the 'target'.
     * 
     * @param target cannot be {@link Player} 
     */
    public void setContinueTo(ILocated target);
    
    /**
     * Returns where the bot will continue to, for more info see {@link #setContinueTo(ILocated)}.
     * 
     * WARNING: continueTo is reset when bot stop navigating / stuck, etc.
     * 
     * WARNING: continueTo is also "nullified" when the bot actually prolongs its path to reach the 'target'.
     * 
     * @return
     */
    public ILocated getContinueTo();
    
    /**
     * Returns nearest navigation point to input location. FloydWarshallMap works only on NavPoints.
     * @param location
     * @return
     */
    public NavPoint getNearestNavPoint(ILocated location);
    
    /**
     * Returns COPY of current path in list. May take some time to fill up. Returns
     * empty list if path not computed.
     * @return
     */
    public List<ILocated> getCurrentPathCopy();

    /**
     * Returns current path as in IPathFuture object that is used by ut2004pathExecutor
     * to navigate. Can be altered. May return null if path not computed!
     * Be carefull when altering this during UT2004PathExecutor run - it may cause
     * undesirable behavior.
     * @return
     */
    public List<ILocated> getCurrentPathDirect();
    
    /**
     * Current POINT where the navigation is trying to get to.
     * @return
     */
    public ILocated getCurrentTarget();
    
    /**
     * If navigation is trying to get to some player, otherwise returns null.
     * @return
     */
    public Player getCurrentTargetPlayer();
    
    /**
     * If navigation is trying to get to some item, otherwise returns null.
     * @return
     */
    public Item getCurrentTargetItem();
    
    /**
     * If navigation is trying to get to some navpoint, otherwise returns null.
     * @return
     */
    public NavPoint getCurrentTargetNavPoint();

    /**
     * Returns previous location we tried to get to (i.e., what was {@link UT2004Navigation#getCurrentTarget()} before
     * another {@link UT2004Navigation#navigate(ILocated)} or {@link UT2004Navigation#navigate(Player)} was called.
     * @return
     */
    public ILocated getLastTarget();
    
    /**
     * If previous target was a player, returns non-null player we previously tried to get to 
     * (i.e., what was {@link UT2004Navigation#getCurrentTargetPlayer()} before
     * another {@link UT2004Navigation#navigate(ILocated)} or {@link UT2004Navigation#navigate(Player)} was called.
     * @return
     */
    public Player getLastTargetPlayer();
    
    /**
     * If previous target was an item, returns non-null {@link Item} we previously tried to get to.
     * (i.e., what was {@link UT2004Navigation#getCurrentTargetItem()} before
     * another {@link UT2004Navigation#navigate(ILocated)} or {@link UT2004Navigation#navigate(Player)} was called.
     * @return
     */
    public Item getLastTargetItem();

    /**
     * Returns an immutable flag with the current state of the navigation.
     * 
     * @return an immutable flag with the current state of the navigation.
     */
	public Flag<NavigationState> getState();
	
	/**
	 * Returns how far is our target (path-distance == real-distance).
	 * 
	 * May return -1 if it cannot be computed.
	 * 
	 * @return
	 */
	public double getRemainingDistance();
	
	/**
	 * Returns logger used by the object.
	 * @return
	 */
	public Logger getLog();
	
	/**
	 * Ensures logging level for the object and all composites. 
	 * @param level
	 */
	public void setLogLevel(Level level);

}
