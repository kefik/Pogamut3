package cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.pathTracer;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import math.geom2d.Point2D;

/** Ray traced path
 *
 * Immutable once computed.
 * 
 * @param <TPolygon>
 * @param <TEdge>
 */
public class RayPath<TPolygon,TEdge>{
	
	protected TPolygon start;
	protected List<PathStep> steps = Lists.newArrayList();
	
	public RayPath( TPolygon start ) {
		this.start = start;
	}
	
	public List<PathStep> getSteps() {
		return Collections.unmodifiableList( steps );
	}
	
	public List<TPolygon> asPolygons() {
		List<TPolygon> retval = Lists.newArrayList();
		retval.add( start );
		for ( PathStep step : steps ) {
			retval.add( step.polygon );
		}
		return retval;
	}

	
	public class PathStep
	{
		protected Location intersection;
		protected TEdge edge;
		protected TPolygon polygon;
		
		public PathStep(Location intersection, TEdge edge, TPolygon polygon) {
			super();
			this.intersection = intersection;
			this.edge = edge;
			this.polygon = polygon;
		}

		public Location getIntersection() {
			return intersection;
		}

		public TEdge getEdge() {
			return edge;
		}

		public TPolygon getPolygon() {
			return polygon;
		}
	}

	protected void addStep(Location intersection, TEdge edge, TPolygon polygon) {
		steps.add( new PathStep( intersection, edge, polygon ) );
	}
}