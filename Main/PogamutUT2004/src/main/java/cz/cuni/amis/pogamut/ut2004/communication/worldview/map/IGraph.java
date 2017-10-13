package cz.cuni.amis.pogamut.ut2004.communication.worldview.map;

import java.util.Collection;

public interface IGraph<Node> {
	
	public Collection<Node> getNeighbours(Node node);
	
	public double getEdgeCost(Node from, Node to);

}
