package cz.cuni.amis.pogamut.ut2004.agent.navigation.astar;

import java.util.Set;

import cz.cuni.amis.pathfinding.alg.astar.AStar;
import cz.cuni.amis.pathfinding.alg.floydwarshall.FloydWarshall;
import cz.cuni.amis.pathfinding.map.IPFGoal;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.utils.heap.IHeap;

/**
 * Use amis-path-finding library instead, see {@link AStar} or {@link FloydWarshall}.
 * <p><p>
 * Uses standard 3D-Euclidian distance between {@link NavPoint}s as the heuristic.
 * 
 * @author Jimmy
 */
public abstract class UT2004PFTask implements IPFGoal<NavPoint> {

	private IHeap<NavPoint> openList;
	private Set<NavPoint> closedList;
	private NavPoint startNode;
	
	public UT2004PFTask(NavPoint startNode) {
		this.startNode = startNode;
	}

	@Override
	public void setOpenList(IHeap<NavPoint> openList) {
		this.openList = openList;
	}

	@Override
	public void setCloseList(Set<NavPoint> closedList) {
		this.closedList = closedList;
	}
		
	/**
	 * The open list of the path-finding algorithm. 
	 * <p><p>
	 * IMMUTABLE! DON'T CHANGE IT!
	 * 
	 * @param openList
	 */
	public IHeap<NavPoint> getOpenList() {
		return openList;
	}
	
	/**
	 * The closed list of the path-finding algorithm. 
	 * <p><p>
	 * IMMUTABLE! DON'T CHANGE IT!
	 */
	public Set<NavPoint> getClosedList() {
		return closedList;
	}

	@Override
	public NavPoint getStart() {
		return startNode;
	}
	
	@Override
	public abstract boolean isGoalReached(NavPoint actualNode);
	
	@Override
	public int getEstimatedCostToGoal(NavPoint node) {
		if (node == null) return Integer.MAX_VALUE;
		return (int)Math.round(startNode.getLocation().getDistance(node.getLocation()));
	}
	
}
