package cz.cuni.amis.pogamut.base.agent.navigation;

import java.util.List;

/**
 * Simple interface that defines a method for estimating timeout for traveling along the path.
 * <p><p>
 * Implementors can be used to provide maximum time (timeout) that is needed to reach the end of the path.
 * It is useful for {@link IPathExecutor} so it won't stuck whenever the execution fails unexpectedly.  
 * 
 * @author Jimmy
 *
 * @param <PATH_ELEMENT>
 */
public interface IPathExecutionEstimator<PATH_ELEMENT> {
	
	/**
	 * Returns maximum amount of time (in ms) that is needed to follow the path and reach its end.
	 * <p><p>
	 * Usually used to determine timeout for path execution by {@link IPathExecutor}.
	 * 
	 * @param path
	 * @return time in ms
	 */
	public double getTimeout(List<PATH_ELEMENT> path);

}
