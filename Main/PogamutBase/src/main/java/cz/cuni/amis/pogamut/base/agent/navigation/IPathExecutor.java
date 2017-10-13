package cz.cuni.amis.pogamut.base.agent.navigation;

import java.util.List;
import java.util.logging.Logger;

import cz.cuni.amis.utils.flag.ImmutableFlag;

/**
 * Path executor object is responsible for navigation of the agent through the environment along the 
 * list of PATH_ELEMENTs.
 * <p><p>
 * Every path executor has {@link PathExecutorState} that are represented by {@link IPathExecutorState} objects.
 * There is an exact definition how every {@link IPathExecutor} implementation must behave (i.e., there is a definition
 * of state transitions), this definition can be found in javadoc of {@link PathExecutorState} enum.
 * <p><p>
 * Implementation of path executor is allowed to fine-tune its states by defining own {@link IPathExecutorState} objects adding
 * additional sub-states (i.e., making itself an hierarchy-FSM object).
 * <p><p>
 * Every path executor may contain arbitrary number of {@link IStuckDetector}s that checks whether the agent is 
 * still able to finish the intended path (stuck may happen due to two reasons 1) the path executor fails to 
 * steer the agent safely through the environment, 2) the path no longer exist).  
 * 
 * @author Jimmy
 *
 * @param <PATH_ELEMENT>
 */
public interface IPathExecutor<PATH_ELEMENT> {
	
	/**
     * Makes the agent follow given path. Events are fired at different stages
     * of movement (see {@link IPathExecutor#getState()} and appropriate event-listener hooking method {@link ImmutableFlag#addListener(cz.cuni.amis.utils.flag.FlagListener)}).
     * 
     * @param path to follow
     */
    public void followPath(IPathFuture<? extends PATH_ELEMENT> path);
    
    /**
     * Returns a flag with the state of the executor - it is desirable you to set up listeners on this
     * flag as it publish important informations about the path execution.
     * <p><p>
     * Note that the flag contains {@link IPathExecutorState} NOT {@link PathExecutorState} itself - this way,
     * every {@link IPathExecutor} implementor can create own {@link IPathExecutorState} implementation that may carry
     * much more information about the state that just {@link PathExecutorState} extending the meanings of core 
     * {@link PathExecutorState}s.
     * <p><p>
     * It is advisable to study {@link PathExecutorState} javadoc, as it contains a description how the states can change giving
     * you a picture how {@link IPathExecutor} is working (this description is a contract for every {@link IPathExecutor}
     * implementation).
     * <p><p>
     * Listeners to the state can be attached via {@link ImmutableFlag#addListener(cz.cuni.amis.utils.flag.FlagListener)}.
     * 
     * @return flag with the state
     */
    public ImmutableFlag<IPathExecutorState> getState();
	
	/**
	 * Returns current path that the executor is following. 
	 * <p><p>
	 * Returns null if not {@link IPathExecutor#isExecuting()}.
	 * 
	 * @return current path
	 */
	public IPathFuture<PATH_ELEMENT> getPathFuture();
	
	/**
	 * If the {@link IPathExecutor#isExecuting()} and the path has been already computed, returns path the executor
	 * is currently following. Returns null otherwise.
	 * <p><p>
	 * First path element is agent's starting position and the last path element is agent's target.
	 * 
	 * @return current path or null
	 */
	public List<PATH_ELEMENT> getPath();
	
	/**
	 * Returns path origin, from where we're running.
	 * @return
	 */
	public PATH_ELEMENT getPathFrom();
	
	/**
	 * Returns target where we're running.
	 * @return
	 */
	public PATH_ELEMENT getPathTo();
	
	/**
	 * Returns an index pointing into {@link IPathExecutor#getPath()} that marks the element
	 * the path executor is currently heading to.
	 * <p><p>
	 * Returns -1 if not {@link IPathExecutor#isExecuting()}.
	 * @return
	 */
	public int getPathElementIndex();
	
	/**
	 * If the {@link IPathExecutor#isExecuting()} and the path has been already computed, returns current path element
	 * the executor is navigating to.
	 * @return path element or null
	 */
	public PATH_ELEMENT getPathElement();
	
	/**
	 * True if the path executor is in one of 'states', false otherwise.
	 * @param states
	 * @return
	 */
	public boolean inState(PathExecutorState... states);
	
	/**
	 * True if the path executor's state is not among 'states', false otherwise.
	 * @param states
	 * @return
	 */
	public boolean notInState(PathExecutorState... states);
	
	/**
	 * Determines, whether the path executor instance has been submitted with {@link IPathFuture} 
	 * and working on getting the bot to its target.
	 * <p><p>
	 * Note that <i>true</i> is also returned for the situation in which the path executor awaits the path computation to be finished.
	 * 
	 * @return	returns true, if this instance is controlling the agent and navigate
	 *			it along PATH_ELEMENTs (or at least waiting for the path). False otherwise
	 */
	public boolean isExecuting();
	
	/**
	 * Sets to true whenever the path executor reaches the end of the provided path.
	 * <p><p>
	 * False otherwise (note that {@link IPathExecutor#stop()} will switch this to 'false' again).
	 * 
	 * @return whether the target is reached
	 */
	public boolean isTargetReached();
	
	/**
	 * Sets to true whenever the path executor detect that the bot has stuck and is unable to reach the
	 * path destination.
	 * <p><p>
	 * False otherwise (note that {@link IPathExecuto#stop()} will switch this to 'false' again).
	 * 
	 * @return
	 */
	public boolean isStuck();
	
	/**
	 * True if the path does not exist (is null) or can't be computed at all (an exception has happened 
	 * or the computation has been canceled).
	 * <p><p>
	 * False otherwise (note that false does not mark the situation that the path has been computed).
	 * 
	 * @return
	 */
	public boolean isPathUnavailable();

    /**
     * Stops the path executor unconditionally.
     */
    public void stop();

     /**
     * Adds {@link IStuckDetector} into the executor to watch over the path execution.
     * @param pathListener
     */
    public void addStuckDetector(IStuckDetector stuckDetector);

    /**
     * Removes {@link IStuckDetector} from the executor (must be the same instance, equals() is <b>NOT USED</b>).
     * @param pathListener
     */
    public void removeStuckDetector(IStuckDetector stuckDetector);
    
    /**
     * Removes all stuck detectors it has.
     */
    public void removeAllStuckDetectors();
    
    /**
     * Returns log used by path executor (may be null, you should always check that).
     * 
     * @return log
     */
    public Logger getLog();
    
}
