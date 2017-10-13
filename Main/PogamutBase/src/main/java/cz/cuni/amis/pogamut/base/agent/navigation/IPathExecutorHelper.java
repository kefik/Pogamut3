package cz.cuni.amis.pogamut.base.agent.navigation;

import cz.cuni.amis.pogamut.base.agent.navigation.impl.BasePathExecutor;

/**
 * If you did not read {@link IPathExecutor} javadoc - do it now! Following javadoc assumes you know what {@link IPathExecutor} is
 * and how its interface works.
 * <p><p>
 * As the {@link IPathExecutor} interface is meant to provide a gateway for the user to navigate his/her bot
 * through the environment, it can't be used (as is) as an interface for further {@link IPathExecutor} functionality
 * decomposition into (for instance) navigators (i.e., to implement actual path-following into the different
 * object that actual {@link IPathExecutor} implementation)
 * <p><p>
 * Note that example implementation is {@link BasePathExecutor} where new interface methods has better javadoc (bound to the
 * actual {@link BasePathExecutor} implementation) that can give you hints how they are usually implemented.
 * 
 * @author Jimmy
 * 
 * @param <PATH_ELEMENT>
 */
public interface IPathExecutorHelper<PATH_ELEMENT> extends IPathExecutor<PATH_ELEMENT> {
	
	/**
	 * Asks all {@link IStuckDetector} registered inside executor via {@link IPathExecutor#addStuckDetector(IStuckDetector)}
	 * whether the agent has stuck. 
	 * <p><p>
	 * This method checks (one-by-one) stuck detectors whether some of them is reporting that the agent has stuck.
	 * If the stuck is detected, particular {@link IStuckDetector} is returned. It the stuck is not detected,
	 * null is returned.
	 * 
	 * @return first detector to report that agent has stuck or null
	 */
	public IStuckDetector checkStuckDetectors();
	
	/**
	 * Switches from current path element index into the new one.
	 * <p><p>
	 * Effective only if {@link IPathExecutor#isExecuting()}.
	 * 
	 * @param index
	 */
	public void switchToAnotherPathElement(int index);
	
	/**
	 * Reports that the agent has stuck - this stuck is detected either by some registered {@link IStuckDetector}
	 * or some other part of the {@link IPathExecutor}.
	 * <p><p>
	 * Effective only if {@link IPathExecutor#isExecuting()}.
	 * 
	 * @param detector
	 */
	public void stuck();
	
	/**
	 * Reports that the agent has reached its target.
	 * <p><p>
	 * Effective only if {@link IPathExecutor#isExecuting()}.
	 */
	public void targetReached();

}