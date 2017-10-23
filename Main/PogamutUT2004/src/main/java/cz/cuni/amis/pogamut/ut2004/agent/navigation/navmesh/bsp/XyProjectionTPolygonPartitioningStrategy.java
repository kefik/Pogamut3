package cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.bsp;

import java.util.ArrayList;
import java.util.List;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import math.bsp.BspOccupation;
import math.bsp.IBspStrategy;
import math.bsp.node.IConstBspLeafNode;
import math.bsp.strat.BspListDataStrategy;
import math.geom2d.Point2D;
import math.geom2d.line.StraightLine2D;

public abstract class XyProjectionTPolygonPartitioningStrategy<TPolygon>
	extends BspListDataStrategy<TPolygon, StraightLine2D>
	implements IBspStrategy<ArrayList<TPolygon>, StraightLine2D> {
	
    public static int STOP_SPLITTING_NUMBER_OF_POLYGONS = 1;
    
	@Override
	public boolean shouldSplit(IConstBspLeafNode<ArrayList<TPolygon>, StraightLine2D> leafNode) {
		return leafNode.getData().size() > STOP_SPLITTING_NUMBER_OF_POLYGONS;
	}

	@Override
	public StraightLine2D findBoundary(IConstBspLeafNode<ArrayList<TPolygon>, StraightLine2D> leafNode) {
		StraightLine2D bestBoundary = null;
		double bestBoundaryMetric = 0;
		
		for ( TPolygon splittingPolygon : leafNode.getData() ) {
			List<Location> vertexLocations = getPolygonVertexLocations(splittingPolygon);
			for (int i=0; i<vertexLocations.size(); ++i) {
				Location a = vertexLocations.get(i);
				Location b = vertexLocations.get( (i+1)%vertexLocations.size() );
				
				StraightLine2D candidateBoundary = new StraightLine2D( projectToXyPlane(a), projectToXyPlane(b) );
				
				double totalPolygonCount = leafNode.getData().size();
	        	double negativeSidePolygonCount = 0;
	        	double positiveSidePolygonCount = 0;
	        	
		        for (TPolygon polygon : leafNode.getData()) {
		        	BspOccupation occupation = determineElementOccupation( candidateBoundary, polygon );
		            if (occupation.intersectsNegative()) {
		            	++negativeSidePolygonCount;
		            }
		            if (occupation.intersectsPositive()) {
		            	++positiveSidePolygonCount;
		            }
		        }
		        
		        double candidateBoundaryMetric = Math.min( totalPolygonCount-negativeSidePolygonCount, totalPolygonCount-positiveSidePolygonCount);
		        
		        if (bestBoundaryMetric < candidateBoundaryMetric) {
		        	bestBoundary = candidateBoundary;
		        	bestBoundaryMetric = candidateBoundaryMetric;
		        }        	        
			}
		}
		
		return bestBoundary;
	}
	
	@Override
	public BspOccupation determineElementOccupation(StraightLine2D boundary, TPolygon element) {
		boolean intersectsPositive = false;
		boolean intersectsNegative = false;
		for ( Location vertexLocation : getPolygonVertexLocations(element) ) {
			BspOccupation vertexOccupation = determinePointOccupation(boundary, vertexLocation);
			intersectsPositive = intersectsPositive || vertexOccupation.intersectsPositive();
			intersectsNegative = intersectsNegative || vertexOccupation.intersectsNegative();
		}
		
		return BspOccupation.get(intersectsNegative, intersectsPositive);
	}
	
	public BspOccupation determinePointOccupation(StraightLine2D boundary, Location point) {
		if ( boundary.getSignedDistance( projectToXyPlane(point) ) >= 0 ) {
			return BspOccupation.POSITIVE;
		} else {
			return BspOccupation.NEGATIVE;
		}
	}
	
	protected Point2D projectToXyPlane( Location point ) {
		return new Point2D( point.getX(), point.getY() );
	}
	
	protected abstract List<Location> getPolygonVertexLocations(TPolygon polygon);
}
