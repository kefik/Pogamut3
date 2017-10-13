package cz.cuni.amis.pogamut.ut2004.agent.navigation.astar;

import java.util.List;

import cz.cuni.amis.pathfinding.alg.astar.AStar;
import cz.cuni.amis.pathfinding.alg.astar.AStarResult;
import cz.cuni.amis.pathfinding.map.IPFMapView;
import cz.cuni.amis.pathfinding.map.IPFMapView.DefaultView;
import cz.cuni.amis.pogamut.base.agent.navigation.IPathFuture;
import cz.cuni.amis.pogamut.base.agent.navigation.IPathPlanner;
import cz.cuni.amis.pogamut.base.agent.navigation.impl.PrecomputedPathFuture;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.NavigationGraphBuilder;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.UT2004PathAutoFixer;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.utils.collections.MyCollections;

public class UT2004AStar extends AStar<NavPoint> implements IPathPlanner<NavPoint>  {

	/**
	 * AStar configured with {@link UT2004PFMap} with no agent-specific view on the map, {@link DefaultView} is used. 
	 * @param map
	 */
	public UT2004AStar(UT2004Bot bot) {
		this(new UT2004PFMap(bot.getWorldView()));
	}
	
	/**
	 * AStar configured with {@link UT2004PFMap} with no agent-specific view on the map, {@link DefaultView} is used. 
	 * @param map
	 */
	public UT2004AStar(UT2004Bot bot, IPFMapView<NavPoint> view) {
		this(new UT2004PFMap(bot.getWorldView()), view);
	}
	
	/**
	 * AStar configured with "map" with no agent-specific view on the map, {@link DefaultView} is used. 
	 * @param map
	 */
	public UT2004AStar(UT2004PFMap map) {
		this(map, new IPFMapView.DefaultView());
	}
	
	/**
	 * AStar configured with "map" and agent-specific view on the map, if "view" is null, {@link DefaultView} is going to be used. 
	 * @param map
	 * @param view may be null
	 */
	public UT2004AStar(UT2004PFMap map, IPFMapView<NavPoint> view) {
		super(map, view);
	}	
	
	@Override
	public UT2004PFMap getMap() {
		return (UT2004PFMap) super.getMap();
	}
	
	/**
	 * Uses {@link UT2004PFGoal} to define START-NODE (from) and TARGET-NODE (to) for the A-Star, using
	 * standard 3D-Euclidian heuristic and customized 'mapView'.
	 * @param from
	 * @param to
	 * @return
	 */
	public synchronized AStarResult<NavPoint> findPath(NavPoint from, NavPoint to, IPFMapView<NavPoint> mapView) {		
		return findPath(new UT2004PFGoal(from, to), mapView);
	}
	
	/**
	 * Using {@link #findPath(NavPoint, NavPoint)} to implement {@link IPathPlanner#computePath(Object, Object)} interface.
	 */
	@Override
	public IPathFuture<NavPoint> computePath(NavPoint from, NavPoint to) {
		if (from == null || to == null) return new PrecomputedPathFuture<NavPoint>(from, to, null);
		if (from == to) return new PrecomputedPathFuture<NavPoint>(from, to, MyCollections.toList(from));
		AStarResult<NavPoint> result = findPath(from, to);
		if (result == null) return new PrecomputedPathFuture<NavPoint>(from, to, null);
		return new PrecomputedPathFuture<NavPoint>(from, to, result.getPath());		
	}
	
	@Override
	public double getDistance(NavPoint from, NavPoint to) { 
		IPathFuture<NavPoint> path = computePath(from, to);
		if (path.isDone()) {
			List<NavPoint> list = path.get();
			if (list.size() == 0) return 0;
			double result = 0;
			NavPoint np = list.get(0);
			for (int i = 1; i < list.size(); ++i) {
				NavPoint next = list.get(i);
				result += np.getLocation().getDistance(next.getLocation());
				np = next;
			}
			return result;
		} else {
			return Double.POSITIVE_INFINITY;
		}
	}
	
	/**
	 * Uses {@link UT2004PFGoal} to define START-NODE (from) and TARGET-NODE (to) for the A-Star, using
	 * standard 3D-Euclidian heuristic.
	 * @param from
	 * @param to
	 * @return
	 */
	public synchronized AStarResult<NavPoint> findPath(NavPoint from, NavPoint to) {		
		return findPath(new UT2004PFGoal(from, to));
	}
	
	/**
	 * Call to wipe cached info about the map, e.g., whenever {@link UT2004PathAutoFixer} bans some navpoint or you use {@link NavigationGraphBuilder}, etc.
	 */
	public void mapChanged() {
		getMap().mapChanged();
	}

}
