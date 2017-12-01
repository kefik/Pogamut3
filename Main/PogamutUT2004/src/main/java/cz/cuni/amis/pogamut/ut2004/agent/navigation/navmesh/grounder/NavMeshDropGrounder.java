package cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.grounder;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Function;

import cz.cuni.amis.pogamut.base.utils.math.DistanceUtils;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.NavMesh;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.INavMeshAtom;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.NavMeshPolygon;
import math.bsp.BspOccupation;
import math.bsp.IConstBspTree;
import math.bsp.algorithm.BspDataSelector;
import math.geom2d.Point2D;
import math.geom2d.line.StraightLine2D;
import math.geom3d.Point3D;
import math.geom3d.Shape3D;
import math.geom3d.Vector3D;
import math.geom3d.line.StraightLine3D;
import math.geom3d.polygon.SimplePlanarPolygon3D;

/** Grounds location to navmesh by dropping down on the nearest nav mesh polygon below
 */
public class NavMeshDropGrounder implements INavMeshGrounder {
	
	/** Default ground distance
	 * 
	 * Entities (navs, players, items) above nav mesh polygon within this vertical distance are reachable by walking on said polygon.
	 */
	public static final double DEFAULT_GROUND_DISTANCE = 100.0;
	
	protected NavMesh navMesh;
	
	public NavMeshDropGrounder( NavMesh navMesh ) {
		this.navMesh = navMesh;
	}
	
	@Override
    public NavMeshPolygon tryGround(ILocated located) {
    	return getPolygonBelow( located.getLocation(), navMesh.getXyProjectionBsp(), shapeToPolygonFunction );
    }
    
	@Override
    public INavMeshAtom forceGround(ILocated located) {
    	NavMeshPolygon retval = getPolygonBelow( located.getLocation(), navMesh.getXyProjectionBsp(), shapeToPolygonFunction );
    	if ( retval != null ) {
    		return retval;
    	} else {
    		// this should be rather rare occurrence when being thrown/falling/jumping etc.
    		return DistanceUtils.getNearest( navMesh.getAtoms(), located );
    	}
    }
    
    public NavMeshPolygon getPolygonBelow(Location location) {
    	return getPolygonBelow(location, DEFAULT_GROUND_DISTANCE, navMesh.getXyProjectionBsp(), shapeToPolygonFunction);
    }
    
    public NavMeshPolygon getPolygonBelow(Location location, double maxDropDistance) {
    	return getPolygonBelow(location, maxDropDistance, navMesh.getXyProjectionBsp(), shapeToPolygonFunction);
    }
    
	protected Function<NavMeshPolygon, SimplePlanarPolygon3D> shapeToPolygonFunction = new Function<NavMeshPolygon, SimplePlanarPolygon3D>() {
		public SimplePlanarPolygon3D apply(NavMeshPolygon polygon) {
			return polygon.getShape();
		};
	};
    
    public static <PolygonElement> PolygonElement getPolygonBelow(
		Location location, 
		IConstBspTree<ArrayList<PolygonElement>, StraightLine2D> xyProjectionBsp,
		Function<PolygonElement, SimplePlanarPolygon3D> polygonToShape 
	) {
    	return getPolygonBelow( location, DEFAULT_GROUND_DISTANCE, xyProjectionBsp, polygonToShape );
    }
    
	public static <PolygonElement> PolygonElement getPolygonBelow(
		Location location, 
		double maxDropDistance,
		IConstBspTree<ArrayList<PolygonElement>, StraightLine2D> xyProjectionBsp,
		Function<PolygonElement, SimplePlanarPolygon3D> polygonToShape 
	) {
		Point3D point = location.asPoint3D();
		StraightLine3D gravityLine = new StraightLine3D( point, point.plus( new Vector3D( 0, 0, -1)) );

		
		BspDataSelector<Location, ArrayList<PolygonElement>, StraightLine2D> selectorByLocation = new BspDataSelector<Location, ArrayList<PolygonElement>, StraightLine2D>( xyProjectionBsp ) {
			@Override
			public BspOccupation determineVolumeOccupation(StraightLine2D boundary, Location volume) {
				Point2D point = new Point2D( volume.getX(), volume.getY() );
				if ( boundary.getSignedDistance( point ) >= 0 ) {
					return BspOccupation.POSITIVE;
				} else {
					return BspOccupation.NEGATIVE;
				}
			}

			@Override
			protected ArrayList<PolygonElement> filterDataByVolume(Location volume, ArrayList<PolygonElement> data) {
				return data;
			}
		};

		List<PolygonElement> candidates = selectorByLocation.select(location);

		PolygonElement bestCandidate = null;
		double bestDropDistance = Double.POSITIVE_INFINITY;
		for (PolygonElement candidate : candidates) {
			SimplePlanarPolygon3D polygon = polygonToShape.apply(candidate);
			Point3D gravityLineIntersection = gravityLine.getPlaneIntersection( polygon.getPlane() );
			
			if ( polygon.getDistance( gravityLineIntersection ) > Shape3D.ACCURACY ) {
				// polygon is not actually above/below 
				continue;
			}
			
			double dropDistance = point.getZ() - gravityLineIntersection.getZ();
			
            // TODO it should be dropDistance < 0, but navmesh is often messed up - floating above ground
			if ( dropDistance < -100 || maxDropDistance < dropDistance ) {
				// candidate is actually above or too far below
				continue;
			}

			if ( dropDistance < bestDropDistance ) {
				bestDropDistance = dropDistance;
				bestCandidate = candidate;
			}
		}

		return bestCandidate;
	}
}
