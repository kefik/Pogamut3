package cz.cuni.amis.pogamut.ut2004.agent.module.sensor.visibilitynavmesh.model;

import java.io.Serializable;

import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPointNeighbourLink;

public class VisibilityNavMeshLocation implements Serializable, ILocated {

	/**
	 * Auto-generated 
	 */
	private static final long serialVersionUID = -9034254663185373879L;

	public int navmeshPolygonId;
	
//	@XStreamAlias(value="x")
	public double x;
	
//	@XStreamAlias(value="y")
	public double y;
	
//	@XStreamAlias(value="z")
	public double z;
		
	private transient Location location;
		
	@Override
	public Location getLocation() {
		if (location == null) {
			location = new Location(x, y, z);
		}
		return location;
	}
	
}
