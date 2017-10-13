package cz.cuni.amis.utils.astar;

/**
 * Evaluator is extending a functionality of {@link AStarHeuristic} allowing 
 * you to additionally specified which NODEs can't be visited at all or assign
 * extra cost to edges between nodes which is added to {@link AStarMap#getEdgeCost(Object, Object)}
 * when computing distances between them.
 * 
 * @author Jimmy
 *
 * @param <NODE>
 * 
 * <p><p>
 * Use amis-path-finding library instead, see svn://artemis.ms.mff.cuni.cz/pogamut/trunk/project/Utils/AmisPathFinding
 */
@Deprecated
public interface AStarEvaluator<NODE> extends AStarHeuristic<NODE> {

	 /**
	  * Returns true if A* can use this node (e.g. to step on this type of floor)
      * You can use it to forbid some specific nodes	  
	  */
	 public boolean isNodeOpened(NODE node); 

	 /**
	  * Returns extra cost to add to value when trying to go
      * nodeFrom to nodeTo ... of course it can depends only on nodeTo 
      * (some special kind of a floor for instance)
      * 
      * Don't worry about the edge cost to become negative, A* ensures that
      * that the least cost is 0 (Algorithm can't work over graphs with negative
      * costs.)
      * 
	  * @return extra cost of edge for nodeFrom -> nodeTo
	  */
	 public int getExtraCost(NODE nodeFrom, NODE nodeTo);
	
}
