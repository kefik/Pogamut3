package cz.cuni.amis.pogamut.ut2004.agent.navigation.levelGeometry;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;

public class PrecomputedRaycastResult {
	public final RaycastRequest request;
	public final double hitDistance;
	
	public PrecomputedRaycastResult(Location from, Location to, double hitDistance) {
		this.request = new RaycastRequest( from, to );
		this.hitDistance = hitDistance;
	}
	
	@Override
	public int hashCode() {
		return request.from.hashCode()+request.to.hashCode();
	}
}
