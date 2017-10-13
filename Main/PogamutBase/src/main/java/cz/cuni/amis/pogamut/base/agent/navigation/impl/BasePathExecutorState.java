package cz.cuni.amis.pogamut.base.agent.navigation.impl;

import cz.cuni.amis.pogamut.base.agent.navigation.IPathExecutorState;
import cz.cuni.amis.pogamut.base.agent.navigation.PathExecutorState;
import cz.cuni.amis.utils.flag.Flag;

/**
 * Represents simple implementation of the {@link IPathExecutorState} containing just the state.
 * <p><p>
 * Do not instantiated your own objects - use preinstantiated objects (constants), e.g., {@link BasePathExecutor#INSTANTIATED}, etc. 
 * (The only exception is the {@link PathExecutorState#SWITCHED_TO_ANOTHER_PATH_ELEMENT} state where you always have to instantiate
 * this class to reraise the same event again.)
 * <p><p>
 * Use {@link BasePathExecutorState#getState(PathExecutorState)} to translate {@link PathExecutorState} into {@link BasePathExecutorState}.
 * @author Jimmy
 */
public class BasePathExecutorState implements IPathExecutorState {

	/**
	 * Corresponds to the {@link PathExecutorState#INSTANTIATED} state.
	 */
	public static final BasePathExecutorState INSTANTIATED = new BasePathExecutorState(PathExecutorState.INSTANTIATED);
	
	/**
	 * Corresponds to the {@link PathExecutorState#FOLLOW_PATH_CALLED} state.
	 */
	public static final BasePathExecutorState FOLLOW_PATH_CALLED = new BasePathExecutorState(PathExecutorState.FOLLOW_PATH_CALLED);
	
	/**
	 * Corresponds to the {@link PathExecutorState#PATH_COMPUTED} state.
	 */
	public static final BasePathExecutorState PATH_COMPUTED = new BasePathExecutorState(PathExecutorState.PATH_COMPUTED);
	
	/**
	 * Corresponds to the {@link PathExecutorState#PATH_COMPUTATION_FAILED} state.
	 */
	public static final BasePathExecutorState PATH_COMPUTATION_FAILED = new BasePathExecutorState(PathExecutorState.PATH_COMPUTATION_FAILED);
	
	/**
	 * Corresponds to the {@link PathExecutorState#SWITCHED_TO_ANOTHER_PATH_ELEMENT} state.
	 */
	public static final BasePathExecutorState SWITCHED_TO_ANOTHER_PATH_ELEMENT = new BasePathExecutorState(PathExecutorState.SWITCHED_TO_ANOTHER_PATH_ELEMENT);
	
	/**
	 * Corresponds to the {@link PathExecutorState#TARGET_REACHED} state.
	 */
	public static final BasePathExecutorState TARGET_REACHED = new BasePathExecutorState(PathExecutorState.TARGET_REACHED);
	
	/**
	 * Corresponds to the {@link PathExecutorState#STUCK} state.
	 */
	public static final BasePathExecutorState STUCK = new BasePathExecutorState(PathExecutorState.STUCK);
	
	/**
	 * Corresponds to the {@link PathExecutorState#STOPPED} state.
	 */
	public static final BasePathExecutorState STOPPED = new BasePathExecutorState(PathExecutorState.STOPPED);
	
	public static BasePathExecutorState getState(PathExecutorState state) {
		switch(state) {
		case INSTANTIATED:                     return BasePathExecutorState.INSTANTIATED;
		case FOLLOW_PATH_CALLED:               return BasePathExecutorState.FOLLOW_PATH_CALLED;
		case PATH_COMPUTED:                    return BasePathExecutorState.PATH_COMPUTED;
		case PATH_COMPUTATION_FAILED:          return BasePathExecutorState.PATH_COMPUTATION_FAILED;
		case SWITCHED_TO_ANOTHER_PATH_ELEMENT: return BasePathExecutorState.SWITCHED_TO_ANOTHER_PATH_ELEMENT;
		case TARGET_REACHED:                   return BasePathExecutorState.TARGET_REACHED;
		case STUCK:                            return BasePathExecutorState.STUCK;
		case STOPPED:                          return BasePathExecutorState.STOPPED;
		default:
			throw new RuntimeException("Unhandled state '" + state + "', can't create corresponding BasePathExecutorState.");
		}
	}
	
	/**
	 * State of the path executor.
	 */
	private PathExecutorState state;
	
	/**
	 * Instantiated the path executor with the given state.
	 * <p><p>
	 * This constructor is meant to be used only by {@link BasePathExecutor} descendants! If you want to use it,
	 * it is advised to use predefined constants. (The only exception is the {@link PathExecutorState#SWITCHED_TO_ANOTHER_PATH_ELEMENT} state.)
	 * @param state
	 */
	public BasePathExecutorState(PathExecutorState state) {
		this.state = state;
	}
	
	@Override
	public PathExecutorState getState() {
		return state;
	}

}
