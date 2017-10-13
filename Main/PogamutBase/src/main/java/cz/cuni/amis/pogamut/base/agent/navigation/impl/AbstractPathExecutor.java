package cz.cuni.amis.pogamut.base.agent.navigation.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.agent.navigation.IPathExecutor;
import cz.cuni.amis.pogamut.base.agent.navigation.IPathExecutorState;
import cz.cuni.amis.pogamut.base.agent.navigation.IPathFuture;
import cz.cuni.amis.pogamut.base.agent.navigation.IStuckDetector;
import cz.cuni.amis.pogamut.base.agent.navigation.PathExecutorState;
import cz.cuni.amis.utils.flag.Flag;
import cz.cuni.amis.utils.flag.ImmutableFlag;
import cz.cuni.amis.utils.future.FutureStatus;

/**
 * Stub implementation of the {@link IPathExecutor} that implements several trivial methods from the
 * interface leaving the important one to be implemented by descendants.
 * <p><p>
 * Methods that need to be implemented are:
 * {@link AbstractPathExecutor#followPath(IPathFuture)}, {@link AbstractPathExecutor#getPathFuture()}, {@link AbstractPathExecutor#getPathElementIndex()}
 * and {@link AbstractPathExecutor#stop()}). Note that these methods must correctly set the path executor state
 * according to javadoc in {@link PathExecutorState}. 
 * 
 * @author Jimmy
 *
 * @param <PATH_ELEMENT>
 */
public abstract class AbstractPathExecutor<PATH_ELEMENT> implements IPathExecutor<PATH_ELEMENT> {

	protected Flag<IPathExecutorState> state = new Flag<IPathExecutorState>(new BasePathExecutorState(PathExecutorState.INSTANTIATED));
	
	protected List<IStuckDetector> stuckDetectors = new ArrayList<IStuckDetector>();
	
	protected Logger log;
	
	public AbstractPathExecutor() {
		this(null);
	}
	
	public AbstractPathExecutor(Logger log) {
		this.log = log;
	}
	
	@Override
	public Logger getLog() {
		return log;
	}

	/**
	 * Sets logger to be used by the path executor.
	 * @param log
	 */
	public void setLog(Logger log) {
		this.log = log;
	}

	/**
	 * Simple method that sets new {@link AbstractPathExecutor} state into {@link AbstractPathExecutor#state}.
	 * @param newState
	 */
	protected void switchState(IPathExecutorState newState) {
		if (log != null && log.isLoggable(Level.FINEST)) log.finest("new state " + newState.getState());
		state.setFlag(newState);
	}
	
	@Override
	public ImmutableFlag<IPathExecutorState> getState() {
		return state.getImmutable();
	}
	
	@Override
	public void addStuckDetector(IStuckDetector stuckDetector) {
		stuckDetectors.add(stuckDetector);
	}
	
	@Override
	public void removeStuckDetector(IStuckDetector stuckDetector) {
		stuckDetectors.remove(stuckDetector);
	}
	
	@Override
	public void removeAllStuckDetectors() {
		stuckDetectors.clear();
	}
	
	@Override
	public boolean inState(PathExecutorState... states) {
		IPathExecutorState current = getState().getFlag();
		for (PathExecutorState state : states) {
			if (state == current.getState()) return true;
		}
		return false;
	}

	@Override
	public boolean notInState(PathExecutorState... states) {
		IPathExecutorState current = getState().getFlag();
		for (PathExecutorState state : states) {
			if (state == current.getState()) return false;
		}
		return true;
	}
	
	@Override
	public List<PATH_ELEMENT> getPath() {
		if (!isExecuting()) return null;
		IPathFuture<PATH_ELEMENT> pathFuture = getPathFuture();
		if (pathFuture == null) return null;
		if (pathFuture.getStatus() == FutureStatus.FUTURE_IS_READY) {
			return pathFuture.get();
		} else {
			return null;
		}
	}
	
	/**
	 * Returns correct path element only if the {@link AbstractPathExecutor#getPathElementIndex()} is in the range
	 * of {@link AbstractPathExecutor#getPath()}. Otherwise, returns null.
	 * 
	 * @return current path element the executor is navigating to or null
	 */
	@Override
	public PATH_ELEMENT getPathElement() {
		int index = getPathElementIndex();
		if (index < 0) return null;
		List<PATH_ELEMENT> path = getPath();
		if (path == null) return null;
		if (index > 0 && index < path.size()) return path.get(index);
		return null;
	}
	
	@Override
	public boolean isExecuting() {
		return inState(PathExecutorState.FOLLOW_PATH_CALLED, PathExecutorState.PATH_COMPUTED, PathExecutorState.SWITCHED_TO_ANOTHER_PATH_ELEMENT);
	}

	@Override
	public boolean isTargetReached() {
		return state.getFlag().getState() == PathExecutorState.TARGET_REACHED;
	}
	
	@Override
	public boolean isStuck() {
		return state.getFlag().getState() == PathExecutorState.STUCK;
	}
	
	@Override
	public boolean isPathUnavailable() {
		return state.getFlag().getState() == PathExecutorState.PATH_COMPUTATION_FAILED;
	}
	
	@Override
	public abstract void followPath(IPathFuture<? extends PATH_ELEMENT> path);

	@Override
	public abstract IPathFuture<PATH_ELEMENT> getPathFuture();

	@Override
	public abstract int getPathElementIndex();

	@Override
	public abstract void stop();
	
}
