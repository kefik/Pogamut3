package cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.bsp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

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
    
    protected HashMap<TPolygon, ArrayList<Point2D>> polygonToVertexLocationsMap = Maps.newHashMap();
    protected HashMap<TPolygon, Point2D> polygonToVertexCenterMap = Maps.newHashMap();
    
	/** Clear objects cached to speed up BSP tree construction
	 */
	public void clearCache() {
		polygonToVertexLocationsMap.clear();
		polygonToVertexCenterMap.clear();
	}
	
	@Override
	public boolean shouldSplit(IConstBspLeafNode<ArrayList<TPolygon>, StraightLine2D> leafNode) {
		return leafNode.getData().size() > STOP_SPLITTING_NUMBER_OF_POLYGONS;
	}

	@Override
	public StraightLine2D findBoundary(IConstBspLeafNode<ArrayList<TPolygon>, StraightLine2D> leafNode) {
		StraightLine2D bestBoundary = null;
		double bestBoundaryMetric = 0;
		
		for ( TPolygon splittingPolygon : leafNode.getData() ) {
			List<Point2D> vertices = getPolygonVertices(splittingPolygon);
			for (int i=0; i<vertices.size(); ++i) {
				Point2D a = vertices.get(i);
				Point2D b = vertices.get( (i+1)%vertices.size() );
				
				StraightLine2D candidateBoundary = new StraightLine2D( a, b );
				
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
		for ( Point2D vertex : getPolygonVertices(element) ) {
			BspOccupation vertexOccupation = determinePointOccupation(boundary, vertex);
			intersectsPositive = intersectsPositive || vertexOccupation.intersectsPositive();
			intersectsNegative = intersectsNegative || vertexOccupation.intersectsNegative();
		}
		
		return BspOccupation.get(intersectsNegative, intersectsPositive);
	}
	
	public BspOccupation determinePointOccupation(StraightLine2D boundary, Point2D point) {
		if ( boundary.getSignedDistance( point ) >= 0 ) {
			return BspOccupation.POSITIVE;
		} else {
			return BspOccupation.NEGATIVE;
		}
	}
	
	protected Point2D projectToXyPlane( Location point ) {
		return new Point2D( point.getX(), point.getY() );
	}
	
	protected ArrayList<Point2D> getPolygonVertices(TPolygon polygon) {
		
		if ( polygonToVertexLocationsMap.containsKey(polygon) ) {
			return polygonToVertexLocationsMap.get(polygon);
		}
		
		ArrayList<Point2D> locations = Lists.newArrayList();
		for ( Location location : getPolygonVerticesUncached( polygon ) ) {
			locations.add( projectToXyPlane( location ) );
		}

		polygonToVertexLocationsMap.put( polygon,  locations );
		
		return locations;
	}
	
	protected abstract ArrayList<Location> getPolygonVerticesUncached(TPolygon polygon);
}
