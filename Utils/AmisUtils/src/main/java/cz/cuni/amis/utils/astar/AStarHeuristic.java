package cz.cuni.amis.utils.astar;

/**
 * This is an interface containing a method for computing the {@link AStar} heuristic. That means the estimation
 * how far the goal is from some node that is currently being visited by {@link AStar}.
 * 
 * @author Jimmy
 * 
 * <p><p>
 * Use amis-path-finding library instead, see svn://artemis.ms.mff.cuni.cz/pogamut/trunk/project/Utils/AmisPathFinding
 */
@Deprecated
public interface AStarHeuristic<NODE> {

	/**
	 * This is heuristic function.
	 * <p><p>
	 * <b>WARNING:</b>
	 * <p><p>
	 * This heuristic must be correct for A* to work correctly, that means
	 * the returned distance must be smaller or equal to the real distance
	 * and must be monotonic. (In 2D, 3D an euclidean metric will do the job).
	 * 
	 * @return how far is to the goal from the node
	 */
	 public int getEstimatedDistanceToGoal(NODE node);
	
}
