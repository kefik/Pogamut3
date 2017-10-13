package cz.cuni.amis.pogamut.base.agent.navigation;

/**
 * Interface for generic path planner. PathPlanner is responsible for finding 
 * paths between two points in the map. 
 * <p><p>
 * There are several ways how to implement this interface, eg. Dijkstra, A*, 
 * external A* from the environment, Floyd-Warshall, etc. ...
 * <p><p>
 * Bot programmers are able to supply their own implementation of PathPlanner,
 * eg. for cars or flying vehicles, or path planners that aren't computing the 
 * shortest path but paths with some other property (eg. paths without 
 * jumps, movers etc.)
 * <p><p>
 * {@link IPathPlanner} should be independent of the bot.
 * 
 * @author Ik
 * @author Jimmy
 */
public interface IPathPlanner<PATH_ELEMENT> {

	/**
	 * Returns a future where the path planner will set the result of its computation.
	 * <p><p>
	 * Note that the {@link IPathFuture} might already contain the path (i.e., the returned path was
	 * computed inside this method or it was a precomputed result). Always examine status of the future before
	 * attaching listeners to it.
	 * 
	 * @param from
	 * @param to
	 * @return
	 */
	public IPathFuture<PATH_ELEMENT> computePath(PATH_ELEMENT from, PATH_ELEMENT to);
	
	/**
	 * Returns nearest-path-distance between two elements. 
	 * @param from
	 * @param to
	 * @return
	 */
	public double getDistance(PATH_ELEMENT from, PATH_ELEMENT to);
	
}
