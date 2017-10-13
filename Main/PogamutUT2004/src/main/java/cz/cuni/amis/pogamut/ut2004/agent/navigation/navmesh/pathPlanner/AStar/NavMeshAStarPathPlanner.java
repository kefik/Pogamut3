package cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.pathPlanner.AStar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cz.cuni.amis.pogamut.base.agent.navigation.IPathPlanner;
import cz.cuni.amis.pogamut.base.agent.navigation.impl.PrecomputedPathFuture;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.NavMesh;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.NavMeshConstants;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.grounder.INavMeshGrounder;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.INavMeshAtom;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.NavMeshBoundary;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.NavMeshPolygon;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.OffMeshPoint;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.pathPlanner.polygonPathFunnel.PolygonPathSmoothingFunnelAlgorithm;
import math.geom3d.line.LineSegment3D;

/** A* Path Planner
 * 
 * A* path planner with customizable heuristic / cost function.
 */
public class NavMeshAStarPathPlanner implements IPathPlanner<ILocated>{
    
    enum BoundaryCrossingStrategy {
    	CENTER,
    	SHORTEST
    };
    
    protected INavMeshGrounder grounder;
    protected NavMesh navMesh;
    protected INavMeshAStarHeuristic heuristic;
    protected Logger log;
    
    /** Constructor
     * 
     * @param grounder
     * @param navMesh
     * @param heuristic
     * @param log
     */
    public NavMeshAStarPathPlanner( INavMeshGrounder grounder, NavMesh navMesh, INavMeshAStarHeuristic heuristic, Logger log ) {
		this.grounder = grounder;
		this.navMesh = navMesh;
		this.heuristic = heuristic;
		this.log = log;
	}
    
    /**
     * Computes and returns a path between two points anywhere on the map. If no
     * such path is found, returns path of zero length;
     *
     * @param from
     * @param to
     * @return
     */
    @Override
    public PrecomputedPathFuture<ILocated> computePath(ILocated from, ILocated to) {
        return new PrecomputedPathFuture<ILocated>(from, to, getPath(from, to));
    }
    
    @Override
    public double getDistance(ILocated from, ILocated to) {
    	List<ILocated> path = computePath(from, to).get();
    	if ( path.size() == 0 ) {
    		return Double.POSITIVE_INFINITY;
    	} else {
    		double result = 0;
    		for ( int i = 1; i < path.size(); ++i ) {
    			result += path.get(i-1).getLocation().getDistance( path.get(i).getLocation() );
    		}
    		return result;
    	}
    }
    
    
    /** Gets atom path between from and to location
     *
     * @param from location
     * @param to location
     * @return path or null if none exists
     */
    public List<INavMeshAtom> getAtomPath(ILocated from, ILocated to) {
    	
    	INavMeshAtom startAtom = grounder.forceGround( from.getLocation() );
    	INavMeshAtom destinationAtom = grounder.forceGround( to.getLocation() );
    	
        if ( startAtom.equals(destinationAtom) ) {
            return Lists.newArrayList();
        }
        
        // List of nodes that are adjacent to explored part of the nav mesh but not explored yet
        Map<INavMeshAtom, NavMeshAStarNode> adjacentNodes = Maps.newHashMap();
        // List of nodes that have been explored - we have created a node for all neighbors
        Map<INavMeshAtom, NavMeshAStarNode> exploredNodes = Maps.newHashMap();
        adjacentNodes.put( startAtom, heuristic.extend( null, startAtom, destinationAtom ) );
        
        // expand the A* node graph until we find toAtom
        while ( !adjacentNodes.containsKey( destinationAtom )) {

        	if ( adjacentNodes.isEmpty() ) {
            	// ran out of atoms to explorer and did not find the destination
        		return null;
        	}
        	
            // find the adjacent node with cheapest estimated cost
            NavMeshAStarNode cheapestAdjacentNode = null;
            for (NavMeshAStarNode adjacentNode : adjacentNodes.values() ) {
                if ( cheapestAdjacentNode == null || adjacentNode.getEstimatedTotalCost() < cheapestAdjacentNode.getEstimatedTotalCost()) {
                    cheapestAdjacentNode = adjacentNode;
                }
            }

            // explore the best node
            for ( INavMeshAtom atom : cheapestAdjacentNode.getAtom().getNeighbors() ) {
                
            	if ( exploredNodes.containsKey( atom ) ) {
                    continue; // already explored
                }
                
                adjacentNodes.put( atom, heuristic.extend( cheapestAdjacentNode, atom, destinationAtom ) );
            }
            
            // put expanded node into expanded
            adjacentNodes.remove( cheapestAdjacentNode.getAtom() );
            exploredNodes.put( cheapestAdjacentNode.getAtom(), cheapestAdjacentNode );
        }
    
        return backtrack( adjacentNodes.get( destinationAtom ) );
    } 

	/**
	 * Computes and returns a path between two points anywhere on the map. If no
	 * such path is found, returns null;
	 *
	 * @param from
	 * @param to
	 * @return
	 */
	public List<ILocated> getPath(ILocated from, ILocated to) {
	
	    List<INavMeshAtom> polygonPath = getAtomPath( from, to );
	    	    
	    if (polygonPath == null) {
	        return null;
	    }
	
	    return convertAtomPathToPointPath(from, to, polygonPath, BoundaryCrossingStrategy.SHORTEST);
	}
    
	protected List<INavMeshAtom> backtrack( NavMeshAStarNode destinationNode ) {
		
        // backtrack from target node to start to extract the path
        List<INavMeshAtom> path = new ArrayList<INavMeshAtom>();
        NavMeshAStarNode node = destinationNode;
        while (node != null) {
            path.add(node.getAtom());
            node = node.getPrevious();
        }
        Collections.reverse(path); // the backtracking reversed the path
        return path;
	}
	
    /** Convert atom path to point path where polygons are replaced by crossings on boundaries between them
     * 
     * @param from from location
     * @param to to location
     * @param atomPath path between from and to
     * @return
     */
    protected List<ILocated> convertAtomPathToPointPath(
    		ILocated from,
    		ILocated to,
    		List<INavMeshAtom> atomPath,
    		BoundaryCrossingStrategy boundaryCrossingStrategy
    ) {
        List<ILocated> path = new ArrayList<ILocated>();
        path.add(from);
        
        List<NavMeshPolygon> polygonSubpath = Lists.newArrayList();
        
    	for (INavMeshAtom atom : atomPath) {
    		if (atom instanceof OffMeshPoint) {
    			OffMeshPoint point = (OffMeshPoint) atom;
    			
    			if ( polygonSubpath.size() > 0 ) { 				
    				ILocated leadIn;
    				if ( path.size()>0 ) {
    					leadIn = path.get(path.size()-1);
    				} else {
    					leadIn = from;
    				}
    					
    				path.addAll( findCrossings( leadIn, polygonSubpath, point, boundaryCrossingStrategy ) );
    				polygonSubpath.clear();
    			}
    			
                path.add(point);
            } else {
            	assert( atom instanceof NavMeshPolygon );
            	NavMeshPolygon polygon = (NavMeshPolygon) atom;
            	
            	if ( path.isEmpty() )
            	{
	            	if ( grounder.tryGround(from.getLocation()) != polygon ) {
	            		path.add( polygon.getCenter() );
	            	}
            	}
            	
            	polygonSubpath.add( polygon );
            }
    	}
    	
    	if ( polygonSubpath.size() > 0 )
    	{
    		path.addAll( findCrossings( path.get(path.size()-1), polygonSubpath, to, boundaryCrossingStrategy ) );
    	}
        
    	
        path.add(to);
        return path;
    }
    
    protected List<ILocated> findCrossings( 
    		ILocated leadIn, 
    		List<NavMeshPolygon> polygonPath,
    		ILocated leadOut, 
    		BoundaryCrossingStrategy boundaryCrossingStrategy 
    ) {
    	// find boundaries between adjacent polygons in the path
    	List<NavMeshBoundary> boundaries = Lists.newArrayList();
		for ( int i=0; i<polygonPath.size()-1; ++i)
		{
			NavMeshPolygon p1 = polygonPath.get(i);
			NavMeshPolygon p2 = polygonPath.get(i+1);
			boundaries.add( p1.getAdjPolygonToBoundaryMap().get( p2 ) );
		}
		
		switch ( boundaryCrossingStrategy ) {
		case CENTER:
			return findCenterCrossings( leadIn, boundaries, leadOut );
		case SHORTEST:
			return PolygonPathSmoothingFunnelAlgorithm.findShortestPathCrossings( leadIn, boundaries, leadOut );
		default:
			throw new AssertionError("Unrecognized strategy");
		}
    }
    
    protected List<ILocated> findCenterCrossings( 
    		ILocated leadIn, 
    		List<NavMeshBoundary> boundaries,
    		ILocated leadOut 
    ) {
    	List<ILocated> crossings = Lists.newArrayList();
    	
    	for (NavMeshBoundary boundary : boundaries ) {
        	// we must find the middle of their boundary
            LineSegment3D boundaryLs = new LineSegment3D( 
        		boundary.getSourceVertex().getLocation().asPoint3D(), 
        		boundary.getDestinationVertex().getLocation().asPoint3D()
            );
            
            crossings.add( new Location(boundaryLs.getPoint(0.5)).addZ(NavMeshConstants.liftPolygonLocation) );
    	}
    	
    	return crossings;
    }
}
