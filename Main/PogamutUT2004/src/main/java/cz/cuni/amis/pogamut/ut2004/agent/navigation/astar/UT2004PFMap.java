package cz.cuni.amis.pogamut.ut2004.agent.navigation.astar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import cz.cuni.amis.pathfinding.map.IPFKnownMap;
import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPointNeighbourLink;
import cz.cuni.amis.utils.maps.LazyMap;

public class UT2004PFMap implements IPFKnownMap<NavPoint> {

	private LazyMap<NavPoint, List<NavPoint>> neighbours = new LazyMap<NavPoint, List<NavPoint>>() {

		@Override
		protected List<NavPoint> create(NavPoint key) {
			List<NavPoint> list = new ArrayList<NavPoint>(key.getOutgoingEdges().size());
			for (NavPointNeighbourLink link : key.getOutgoingEdges().values()) {
				list.add(link.getToNavPoint());
			}
			return list;
		}
		
	};
	
	private IWorldView worldView;
	
	public UT2004PFMap(IWorldView worldView) {
		this.worldView = worldView;
	}
	
	@Override
	public int getNodeCost(NavPoint node) {
		return 0;
	}

	@Override
	public Collection<NavPoint> getNeighbors(NavPoint node) {
		return neighbours.get(node);
	}

	@Override
	public int getArcCost(NavPoint nodeFrom, NavPoint nodeTo) {
		NavPointNeighbourLink link = nodeFrom.getOutgoingEdges().get(nodeTo.getId());
		if (link == null) return Integer.MAX_VALUE;
		return (int)Math.round(nodeTo.getLocation().getDistance(nodeFrom.getLocation()));
	}

	@Override
	public Collection<NavPoint> getNodes() {
		return worldView.getAll(NavPoint.class).values();
	}

	public void mapChanged() {
		neighbours.clear();
	}

}
