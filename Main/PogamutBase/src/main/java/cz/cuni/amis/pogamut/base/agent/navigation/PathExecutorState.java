package cz.cuni.amis.pogamut.base.agent.navigation;

/**
 * High-level description of the {@link IPathExecutor} state.
 * <p><p>
 * {@link IPathExecutor} initial state is INSTANTIATED.
 * <p><p>
 * Read javadocs for respective states to get the picture how the {@link IPathExecutor} works.
 * <p><p>
 * Note that the most important outcome of state & transition definitions is that the {@link IPathExecutor} must
 * switch itself to state STOPPED before it may switch into FOLLOW_PATH_CALLED again.
 * 
 * @author Jimmy
 */
public enum PathExecutorState {
	
	/**
	 * The {@link IPathExecutor} has been just instantiated and neither {@link IPathExecutor#followPath(IPathFuture)} nor
	 * {@link IPathExecutor#stop()} methods has been called since than.
	 * <p><p>
	 * Initial state of the {@link IPathExecutor}.
	 * <p><p>
	 * Next state may (only) be: 
	 * <ul>
	 * <li>FOLLOW_PATH_CALLED - if {@link IPathExecutor#followPath(IPathFuture)} is called from the outside</li>
	 * <li>STOPPED - if {@link IPathExecutor#stop() is called from the outside</li>
	 * </ul>
	 */
	INSTANTIATED,

	/**
	 * {@link IPathExecutor#followPath(IPathFuture)} has been just called.
	 * <p><p>
	 * Next state may (only) be:
	 * <ul>
	 * <li>PATH_COMPUTED - whenever the {@link IPathFuture} provides a path (== the path computation finishes and the path exists)</li>
	 * <li>PATH_COMPUTATION_FAILED - whenever the {@link IPathFuture} fails to provide the path (computation does not finish, exception is returned, null path is returned)</li>
	 * <li>STOPPED - if {@link IPathExecutor#stop() is called</li>
	 * </ul>
	 */
	FOLLOW_PATH_CALLED,
	
	/**
	 * {@link IPathFuture} has returned a path. This state marks the beginning of the agent's navigation through
	 * the environment according to the path.
	 * <p><p>
	 * Next state may (only) be: 
	 * <ul>
	 * <li>TARGET_REACHED - if provided path is zero-length (i.e. path start == path end)</li>
	 * <li>SWITCHED_TO_ANOTHER_PATH_ELEMENT - whenever the path executor starts to follow obtained path</li>
	 * <li>STUCK - if one of the executor's {@link IStuckDetector} reports that the bot has stuck</li> 
	 * <li>STOPPED - if {@link IPathExecutor#stop() is called</li>
	 * </ul>
	 */
	PATH_COMPUTED,
	
	/**
	 * {@link IPathFuture} has failed to provide a path.
	 * <p><p>
	 * Next state may (only) be:
	 * <ul>
	 * <li>STOPPED - if {@link IPathExecutor#stop() is called</li>
	 * </ul>
	 */
	PATH_COMPUTATION_FAILED,
	
	/**
	 * {@link IPathExecutor} has switched to another path element (begun to navigate to another path element)
	 * <p><p>
	 * Next state may (only) be: 
	 * <ul>
	 * <li>SWITCHED_TO_ANOTHER_PATH_ELEMENT - whenever the path executor starts to navigate the agent towards different path element</li>
	 * <li>TARGET_REACHED - whenever the agent gets to desired destination</li>
	 * <li>STUCK - if one of the executor's {@link IStuckDetector} reports that the bot has stuck</li>
	 * <li>STOPPED - if {@link IPathExecutor#stop() is called</li>
	 * </ul>
	 */
	SWITCHED_TO_ANOTHER_PATH_ELEMENT,
	
	/**
	 * {@link IPathExecutor} has successfully navigated the agent along the path. Path target has been reached.
	 * <p><p>
	 * Next state may (only) be: 
	 * <ul>
	 * <li>STOPPED - if {@link IPathExecutor#stop() is called</li>
	 * </ul>
	 */
	TARGET_REACHED,
	
	/**
	 * One of the {@link IStuckDetector} has signalized that the agent is unable to reach its destination (navigate along
	 * the path for whatever reason). The {@link IPathExecutor} halted.
	 * <p><p>
	 * Next state may (only) be:
	 * <ul>
	 * <li>STOPPED - if {@link IPathExecutor#stop() is called</li>
	 * </ul>
	 * <p><p>
	 * Note that this is GREAT candidate for broadcasting custom {@link IPathExecutorState} that contains more information about the stuck, i.e.,
	 * PogamutUT2004 is using UT2004PathExecutorStuckState.
	 */
	STUCK,
	
	/**
	 * The {@link IPathExecutor#stop()} has been called and no {@link IPathExecutor#followPath(IPathFuture)} has been called
	 * since that.
	 * <p><p>
	 * Next state may (only) be:
	 * <ul>
	 * <li>FOLLOW_PATH_CALLED - if {@link IPathExecutor#followPath(IPathFuture)} is called from the outside</li>
	 * </ul>
	 */
	STOPPED
	
}
