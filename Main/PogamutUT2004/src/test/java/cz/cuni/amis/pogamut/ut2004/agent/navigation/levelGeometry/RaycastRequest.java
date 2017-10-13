package cz.cuni.amis.pogamut.ut2004.agent.navigation.levelGeometry;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;

public class RaycastRequest {
	final Location from;
	final Location to;
	
	public RaycastRequest(Location from, Location to) {
		super();
		this.from = from;
		this.to = to;
	}
	
	@Override
	public int hashCode() {
		return from.hashCode()+to.hashCode();
	}
}	
	
	