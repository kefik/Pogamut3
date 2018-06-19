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
	
	static ArrayList<Point2D> trivialBoundaryOffsets = Lists.newArrayList();
	static {
		trivialBoundaryOffsets.add( new Point2D(  1, 0 ) );
		trivialBoundaryOffsets.add( new Point2D(  1, 1 ) );
		trivialBoundaryOffsets.add( new Point2D(  0, 1 ) );
		trivialBoundaryOffsets.add( new Point2D( -1, 1 ) );
	}

	@Override
	public StraightLine2D findBoundary(IConstBspLeafNode<ArrayList<TPolygon>, StraightLine2D> leafNode) {
		StraightLine2D bestBoundary = null;
		double bestBoundaryEliminationCount = 0;
		double sufficientEliminationFraction = getSufficientEliminationFraction();
		double totalPolygonCount = leafNode.getData().size();
		
		Point2D nodeDataCenter = getDataCenter( leafNode.getData() );
		for ( Point2D trivialBoundaryOffset : trivialBoundaryOffsets ) {
			StraightLine2D candidateBoundary = new StraightLine2D( nodeDataCenter, nodeDataCenter.plus(trivialBoundaryOffset) );
			
			double candidateBoundaryEliminationCount = computeEliminationCount( candidateBoundary, leafNode.getData() );
	        
	        if (bestBoundaryEliminationCount < candidateBoundaryEliminationCount) {
	        	bestBoundary = candidateBoundary;
	        	bestBoundaryEliminationCount = candidateBoundaryEliminationCount;
	        }
	        
	        if ( bestBoundaryEliminationCount > totalPolygonCount*sufficientEliminationFraction )
	        {
	        	return bestBoundary;
	        }
		}
		
		for ( TPolygon splittingPolygon : leafNode.getData() ) {
			List<Point2D> vertices = getPolygonVertices(splittingPolygon);
			for (int i=0; i<vertices.size(); ++i) {
				Point2D a = vertices.get(i);
				Point2D b = vertices.get( (i+1)%vertices.size() );
				
				StraightLine2D candidateBoundary = new StraightLine2D( a, b );
				
		        double candidateBoundaryEliminationCount = computeEliminationCount( candidateBoundary, leafNode.getData() );
		        
		        if (bestBoundaryEliminationCount < candidateBoundaryEliminationCount) {
		        	bestBoundary = candidateBoundary;
		        	bestBoundaryEliminationCount = candidateBoundaryEliminationCount;
		        }
		        
		        if ( bestBoundaryEliminationCount > totalPolygonCount*sufficientEliminationFraction )
		        {
		        	return bestBoundary;
		        }
			}
		}
		
		return bestBoundary;
	}
	
	public double computeEliminationCount( StraightLine2D boundary, List<TPolygon> polygons ) {
		double totalPolygonCount = polygons.size();
    	double negativeSidePolygonCount = 0;
    	double positiveSidePolygonCount = 0;
    	
        for (TPolygon polygon : polygons) {
        	BspOccupation occupation = determineElementOccupation( boundary, polygon );
            if (occupation.intersectsNegative()) {
            	++negativeSidePolygonCount;
            }
            if (occupation.intersectsPositive()) {
            	++positiveSidePolygonCount;
            }
        }
        
        return Math.min( totalPolygonCount-negativeSidePolygonCount, totalPolygonCount-positiveSidePolygonCount);
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
	
	protected Point2D getDataCenter( ArrayList<TPolygon> polygons ) {
		double x = 0;
		double y = 0;
		for ( TPolygon polygon : polygons ) {
			Point2D polygonCenter = getPolygonVertexCenter(polygon);
			x += polygonCenter.getX();
			y += polygonCenter.getY();
		}
		return new Point2D( x / polygons.size(), y / polygons.size() );
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
	
	/** Elimination fraction that is sufficient for a candidate boundary.
	 * <br>
	 * If candidate boundary achieves this elimination fraction - splits polygons such that at least this fraction of them is eliminated on both sides,
	 * then stop looking for a better candidate.
	 */
	protected double getSufficientEliminationFraction() {
		return 0.49;
	}
	
	protected Point2D getPolygonVertexCenter(TPolygon polygon) {
		if ( polygonToVertexCenterMap.containsKey(polygon) ) {
			return polygonToVertexCenterMap.get(polygon);
		}
		
		double x = 0;
		double y = 0;
		
		ArrayList<Point2D> vertices = getPolygonVertices(polygon);
		for ( Point2D vertex : vertices ) {
			x += vertex.x;
			y += vertex.y;
		}
		
		Point2D center = new Point2D( x / vertices.size(), y / vertices.size() );
		
		polygonToVertexCenterMap.put( polygon, center );
		
		return center;
		
	}
	
	protected abstract ArrayList<Location> getPolygonVerticesUncached(TPolygon polygon);
}
