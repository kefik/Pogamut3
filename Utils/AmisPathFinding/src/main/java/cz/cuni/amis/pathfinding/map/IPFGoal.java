package cz.cuni.amis.pathfinding.map;

import java.util.Set;

import cz.cuni.amis.utils.heap.IHeap;

/**
 * General interface that is describing the goal for the exploratory path-finder
 * such as A-Star algorithm.
 * 
 * @author Jimmy
 */
public interface IPFGoal<NODE> {

	/**
	 * Returns start node that the algorithm will begin with.
	 * @return
	 */
	public NODE getStart();
	
	/**
	 * Goal-recognition function, i.e., it recognizes which node is actually the goal.
	 * <p><p>
	 * Returns true, if we've reached the goal ... actualNode is node we
	 * were trying to get to in the algorithm (e.g. A-Star) if this function never returns true, 
	 * path-finding algorithm will run until all nodes are evaluated.
	 * 
	 * @param actualNode
	 */
	public boolean isGoalReached(NODE actualNode);
	
	/**
	 * This is heuristic function that returns how far is "node" from your
	 * goal, i.e., estimated "cost" it will take the agent to get from
	 * "node" to the goal.
	 * <p><p>
	 * <b>WARNING:</b>
	 * <p><p>
	 * This heuristic must be correct for A* to work correctly, that means the
	 * returned distance must be smaller or equal to the real distance and.
	 * <p><p>
	 * Moreover as you will likely search "graphs" and not "trees" you will also
	 * need the heuristic to be monotonic.
	 * <p><p>
	 * In 2D, 3D an Euclidean metric will do the job.
	 * 
	 * @return how far is to the goal from the node
	 */
	public int getEstimatedCostToGoal(NODE node);

	/**
	 * This is called at the beginning of the A* algorithm to bind the open list
	 * to the goal (you may use it check which nodes we've visited, etc... for
	 * extra cost for instance).
	 * <p><p>
	 * IMMUTABLE! DON'T CHANGE IT!
	 * 
	 * @param openList
	 */
	public void setOpenList(IHeap<NODE> openList);

	/**
	 * This is called at the beginning of the A* algorithm to bind the close
	 * list to the goal (you may use it check which nodes we've visited, etc...
	 * for extra cost for instance). 
	 * <p><p>
	 * IMMUTABLE! DON'T CHANGE IT!
	 * 
	 * @param closedList
	 */
	public void setCloseList(Set<NODE> closedList);

}
