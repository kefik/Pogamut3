package cz.cuni.amis.pogamut.base.agent.navigation;

/**
 * First - read javadoc for {@link IPathPlanner} and {@link IPathExecutor}. 
 * <p><p>
 * Done? Ok, continue...
 * <p><p>
 * So, the {@link IPathPlanner} should get us the path and {@link IPathExecutor} should follow it.
 * This is nice isn't it? It allows you to configure different path planners (for instance one for
 * planning retreat paths, other one planning attack paths) and you may use a single executor
 * or instantiate different one and pick one that suits your needs. Ok, nice, but such scenario
 * requires you to mediate communication bqetween planner and executor and handle executor failures.
 * Sometimes it might be nice to have executor stuffed with planner that it can use to replan the
 * path in the case of failures. Voila - this interface pops out.
 * <p><p> 
 * This interface serves for executors that contains planners they can use in the case of path failure.
 * Whenever the executor fails to reach the path, it should use the injected path planner
 * (via {@link IPathExecutorWithPlanner#setPathPlanner(IPathPlanner)}) to obtain the new path and try that.
 * <p><p>
 * EXPERIMENTAL INTERFACE - might be revisited in future releases (I'm aware that the interface
 * is faulty due to ignore other path-planning/execution concerns such as "how to determine that
 * the executre really really stuck").
 * <p><p>
 * Note that implementation of this interface will the most likely be so specific, that it is 
 * always a good idea to setup own timeout counter and halt the path executor manually if
 * the counter reaches zero. I mean, it probably won't be a flawless implementation that
 * can solve all encountered situations. (Or just use {@link IPathExecutorWithPlanner#runTo(Object, double)}.)
 * 
 * @author Jimmy
 *
 * @param <PATH_ELEMENT>
 */
public interface IPathExecutorWithPlanner<PATH_ELEMENT> extends IPathExecutor<PATH_ELEMENT> {

	public void runTo(PATH_ELEMENT to);
	
	public void runTo(PATH_ELEMENT to, double globalTimeout);
	
	public void setPathPlanner(IPathPlanner<PATH_ELEMENT> pathPlanner);
	
}
