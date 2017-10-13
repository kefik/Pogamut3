package cz.cuni.amis.pogamut.ut2004.agent.navigation.timeoutestimator;

import java.util.List;

import cz.cuni.amis.pogamut.base.agent.navigation.IPathExecutionEstimator;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;

public class UT2004BasicTimeoutEstimator<PATH_ELEMENT extends ILocated> implements IPathExecutionEstimator<PATH_ELEMENT> {

	@Override
	public double getTimeout(List<PATH_ELEMENT> path) {
		if (path == null) return 0;
		if (path.size() <= 1) return 5000;
		Location loc = path.get(0).getLocation();
		double totalDistance = 0;
		
		for (int i = 1; i < path.size(); ++i) {
			totalDistance += loc.getDistance(path.get(i).getLocation());
			loc = path.get(i).getLocation();
		}
		
		return 5000 + totalDistance * 5;
	}
	

}
