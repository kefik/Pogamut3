package cz.cuni.amis.pogamut.ut2004.agent.module.sensor.visibility.model;

import java.io.Serializable;

import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPointNeighbourLink;

//@XStreamAlias(value="VL")
public class VisibilityLocation implements Serializable, ILocated {

	/**
	 * Auto-generated.
	 */
	private static final long serialVersionUID = 1629693033838716595L;

//	@XStreamAlias(value="x")
	public double x;
	
//	@XStreamAlias(value="y")
	public double y;
	
//	@XStreamAlias(value="z")
	public double z;
	
//	@XStreamAlias(value="np1")
	public String navPoint1Id;
	
//	@XStreamAlias(value="np2")
	public String navPoint2Id;
	
	private transient Location location;
	
	public transient NavPoint navPoint;
	
	public transient NavPointNeighbourLink link;
	
	@Override
	public Location getLocation() {
		if (location == null) {
			location = new Location(x, y, z);
		}
		return location;
	}
	
}
