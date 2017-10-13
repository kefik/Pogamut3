package cz.cuni.amis.utils.astar;

import java.util.Collection;

/**
 * This class defines the goal of A* algorithm, it allows you to provide complex implementation
 * of the {@link AStarGoal#isGoalReached(Object)} method.
 * 
 * <p><p>
 * Use amis-path-finding library instead, see svn://artemis.ms.mff.cuni.cz/pogamut/trunk/project/Utils/AmisPathFinding
 */
@Deprecated
public interface AStarGoal<NODE> extends AStarEvaluator<NODE> {
	
	/**
	 * This is called at the beginning of the A* algorithm to bind the open list
	 * to the goal (you may use it check which nodes we've visited, etc... for
	 * extra cost for instance). DON'T CHANGE IT!
	 */
	public void setOpenList(Collection<NODE> openList);
	
	/**
	 * This is called at the beginning of the A* algorithm to bind the close list
	 * to the goal (you may use it check which nodes we've visited, etc... for
	 * extra cost for instance). DON'T CHANGE IT!
	 */
	public void setCloseList(Collection<NODE> closeList);
	
	 /**
	  * Returns true, if we've reached the goal ... e.g. actualNode
      * is node we were trying to get to
      * if this function never returns true, A* will run until
      * all nodes are evaluated
	  * @param actualNode
	  */
	 public boolean isGoalReached(NODE actualNode);
}
