package cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.pathPlanner.AStar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import com.google.common.base.Supplier;
import com.google.common.collect.Lists;

import cz.cuni.amis.pogamut.base.agent.navigation.IPathFuture;
import cz.cuni.amis.pogamut.base.agent.navigation.IPathPlanner;
import cz.cuni.amis.pogamut.base.agent.navigation.impl.PrecomputedPathFuture;
import cz.cuni.amis.pogamut.base.utils.math.DistanceUtils;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.floydwarshall.FloydWarshallMap;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.NavMesh;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.NavMeshConstants;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.grounder.NavMeshDropGrounder;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.INavMeshAtom;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.NavMeshBoundary;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.NavMeshPolygon;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.OffMeshPoint;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.old.OldNavMeshModule;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.pathPlanner.polygonPathFunnel.PolygonPathSmoothingFunnelAlgorithm;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import math.geom2d.Point2D;
import math.geom3d.line.LineSegment3D;
import math.geom3d.plane.Plane3D;
import math.geom3d.plane.Plane3DCoordinateSubsystem;

/** Segmented A* Path Planner
 * 
 * Uses FW map to find base path and then splits it to segments without teleport nav point links. The segments are then recomputed by A*.
 * 
 * @deprecated Use {@link NavMeshAStarPathPlanner}
 */
@Deprecated
public class NavMeshSegmentedAStarPathPlanner implements IPathPlanner<ILocated>{
    
	protected Supplier<Collection<NavPoint>> navGraphProvider;
    protected NavMeshDropGrounder grounder;
    protected NavMesh navMesh;
    protected Logger log;
    
    protected FloydWarshallMap fwMap;
    protected Collection<NavPoint> allNavPoints;
    protected boolean hasTeleports;
    
    public NavMeshSegmentedAStarPathPlanner(Supplier<Collection<NavPoint>> navGraphProvider, NavMeshDropGrounder grounder, NavMesh navMesh, Logger log) {
    	this.navGraphProvider = navGraphProvider;
		this.grounder = grounder;
		this.navMesh = navMesh;
		this.log = log;
		
		this.fwMap = null;
		this.allNavPoints = null;
		this.hasTeleports = false;
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
    public IPathFuture<ILocated> computePath(ILocated from, ILocated to) {
        return new PrecomputedPathFuture<ILocated>(from, to, getPath(from, to));
    }
    
    @Override
    public double getDistance(ILocated from, ILocated to) {
    	IPathFuture<ILocated> path = computePath(from, to);
    	if (path.isDone()) {
    		List<ILocated> list = path.get();
    		if (list.size() == 0) return Double.POSITIVE_INFINITY;
    		ILocated location = list.get(0);
    		double result = 0;
    		for (int i = 1; i < list.size(); ++i) {
    			ILocated next = list.get(i);
    			result += location.getLocation().getDistance(next.getLocation());
    			location = next;
    		}
    		return result;
    	} else {
    		return Double.POSITIVE_INFINITY;
    	}
    }
    
    
    /**
     * Gets a List of polygons on which the path should go.
     *
     * @param fromAtom
     * @param toAtom
     * @return
     */
    public List<INavMeshAtom> getPolygonPath(INavMeshAtom fromAtom, INavMeshAtom toAtom) {
        // List of atoms from which we will always pick the one with shortest distance and expand ir
        List<NavMeshAStarNode> pickable = new ArrayList<NavMeshAStarNode>();
        // List of atoms, that are no longer pickable, because they have no more neighbours
        List<NavMeshAStarNode> expanded = new ArrayList<NavMeshAStarNode>();
        NavMeshAStarNode firstNode = new NavMeshAStarNode(null, fromAtom, 0, fromAtom.getLocation().getDistance(toAtom.getLocation()));
        pickable.add(firstNode);
        
        // Let's search for toAtom!
        NavMeshAStarNode targetNode = null;

        // target reach test = start and end atom are the same atom
        if (fromAtom.equals(toAtom)) {
            targetNode = firstNode;
        }

        while (targetNode == null) {

            // 1. if pickable is empty, there is no way
            if (pickable.isEmpty()) {
                return null;
            }

            // 2. find the most perspective node in pickable
            // that means that it has the shortest estimated total path length;
            NavMeshAStarNode best = pickable.get(0);
            for (NavMeshAStarNode node : pickable) {
                if (node.getEstimatedTotalCost() < best.getEstimatedTotalCost()) {
                    best = node;
                }
            }

            // 3. we expand the best node
            List<INavMeshAtom> neighbors = best.getAtom().getNeighbors();
            for (INavMeshAtom atom : neighbors) {
                boolean add = true;
                // if this atom is already in our expanded tree, we reject it?
                // TODO some optimalization for teleports
                for (NavMeshAStarNode expNode : expanded) {
                    if (expNode.getAtom().equals(atom)) {
                        add = false;
                    }
                }
                // we add new neighbour
                if (add) {
                	double costFromStart = best.getCostFromStart() + best.getAtom().getLocation().getDistance(atom.getLocation());
                	double estimatedCostToTarget = atom.getLocation().getDistance(toAtom.getLocation());
                    NavMeshAStarNode newNode = new NavMeshAStarNode(best, atom, costFromStart, estimatedCostToTarget);
                    pickable.add(newNode);
                    // target reach test
                    if (atom.equals(toAtom)) {
                        targetNode = newNode;
                    }
                }
            }
            // put expadned node into expanded
            pickable.remove(best);
            expanded.add(best);
        }

        // now we just return the path of atoms from start to end. We must build it from the end
        List<INavMeshAtom> path = new ArrayList<INavMeshAtom>();
        NavMeshAStarNode node = targetNode;
        while (node != null) {
            path.add(node.getAtom());
            node = node.getPrevious();
        }
        Collections.reverse(path);
        return path;
    }

    /**
     * Calls the method with the same name but polygons as arguments and returns
     * result
     *
     * @param from
     * @param to
     * @return
     */
    public List<INavMeshAtom> getPolygonPath(Location from, Location to) {
        return getPolygonPath( ground(from), ground(to) );
    }

    enum BoundaryCrossingStrategy {
    	CENTER,
    	SHORTEST
    };
    
    /**
     * Counts a simple path from polygonPath. Only for testing use. rules for
     * adding points: for each new atom in path:
     *
     * 1. point -> point : just add the new point! :-) 2. point -> polygon : is
     * that point inside that polygon? a) yes : add nothing b) no : this should
     * not happen. offmesh points are connected to points inside. Add the
     * center... 3. polygon -> polygon : add point that is in middle of shared
     * line 4. polygon -> point : is that point inside that polygon? a) yes :
     * add the new point b) no : this should not happen. offmesh points are
     * connected to points inside. Add the new point anyway
     *
     * @param from
     * @param to
     * @param polygonPath
     * @return
     */
    private List<ILocated> convertAtomPathToPointPath(
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
            		boolean isPreviousInside = polygonContainsPoint(polygon, new Point2D(from.getLocation().x, from.getLocation().y));
	            	if (!isPreviousInside) {
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
    
    private List<ILocated> findCrossings( 
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
    
    private List<ILocated> findCenterCrossings( 
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
    
    /**
     * Computes and returns a path between two points anywhere on the map. If no
     * such path is found, returns null;
     *
     * @param from
     * @param to
     * @return
     */
    public List<ILocated> getPath(ILocated from, ILocated to) {

        List<INavMeshAtom> polygonPath = null;

        //Look for possible teleport paths...
        if (allNavPoints == null) {
            initNavPoints();
        }
        if (fwMap != null && allNavPoints != null) {

            NavPoint fromNp = DistanceUtils.getNearest(allNavPoints, from);
            NavPoint toNp = DistanceUtils.getNearest(allNavPoints, to);

            if (hasTeleports) {
                List<NavPoint> path = fwMap.getPath(fromNp, toNp);
                if (path != null) {
                    INavMeshAtom atomFrom = ground(from.getLocation());
                    boolean skip = false;

                    for (NavPoint np : path) {
                        if (skip) {

                            atomFrom = getNearestOffmeshPoint(np.getLocation());
                            skip = false;

                        } else if (np.isTeleporter()) {

                            INavMeshAtom atomTo = getNearestOffmeshPoint(np.getLocation());
                            List<INavMeshAtom> pathPart = getPolygonPath(atomFrom, atomTo);
                            if (pathPart == null) {
                                polygonPath = null;
                                break;
                            }
                            if (polygonPath == null) {
                                polygonPath = pathPart;
                            } else {
                                polygonPath.addAll(pathPart);
                            }
                            skip = true;
                        }
                    }

                    if (polygonPath != null) {
                        INavMeshAtom atomTo = ground(to.getLocation());
                        List<INavMeshAtom> pathPart = getPolygonPath(atomFrom, atomTo);
                        if (pathPart != null) {
                            polygonPath.addAll(pathPart);
                        } else {
                            polygonPath = null;
                        }
                    }
                }
            }
        }

        // first we found a list of polygons and off-mesh connections on the path
        // using A* algorithm
        if (polygonPath == null) {
            polygonPath = getPolygonPath(from.getLocation(), to.getLocation());
        }
        if (polygonPath == null) {
            return null;
        }

        //this.drawPolygonPath(polygonPath, new Location(255,255,0));
        List<ILocated> path;

        // now we transform path made of polygons to path made of Locations        
        // path = getPolygonCentrePath(from, to, polygonPath);      
        path = convertAtomPathToPointPath(from, to, polygonPath, BoundaryCrossingStrategy.SHORTEST);

        return path;
    }
    
    /** Set Floyd-Warshal map.
     * 
     * Called from {@link OldNavMeshModule}.
     */
    public void setFwMap(FloydWarshallMap fwMap) {
        this.fwMap = fwMap;
    }
    
    public void clear() {
    	allNavPoints = null;
    	fwMap = null;
    	hasTeleports = false;
    }
    
    /** Decides whether the input point is inside of the polygon of navmesh.
     */
    private boolean polygonContainsPoint(NavMeshPolygon polygon, Point2D point2D) {
        Plane3DCoordinateSubsystem xyPlaneCoordSubsystem = Plane3D.xyPlane.getCoordinateSubsystem();
        return xyPlaneCoordSubsystem.project( polygon.getShape() ).contains( point2D );
    }
    
    private void initNavPoints() {
    	allNavPoints = navGraphProvider.get();
    	hasTeleports = false;
        for (NavPoint np : allNavPoints) {
            if (np.isTeleporter()) {
                hasTeleports = true;
                break;
            }
        }
    }
    
    /** Ground location to the navmesh
     *
     * @param location
     * @return navmesh polygon from which the location can be reached
     */
    private NavMeshPolygon ground( Location location ) {
    	return grounder.forceGround(location);
    }
    
    private INavMeshAtom getNearestOffmeshPoint(Location location) {
    	return DistanceUtils.getNearest( navMesh.getOffMeshPoints(), location );
    }
}
