package cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.pathPlanner.polygonPathFunnel;

import java.util.List;

import com.google.common.collect.Lists;

import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.NavMeshBoundary;
import math.geom3d.plane.Plane3D;
import math.geom3d.plane.Plane3DCoordinateSubsystem;

/** An implementation of the "Simple stupid funnel algorithm" for polygon path smoothing
 * <p>
 * This implementation compensates for a quirk of path executor that could cause 
 * the agent to fall into a pit or bump into a wall when a waypoint is placed on the edge of walkable area.
 */
public class PolygonPathSmoothingFunnelAlgorithm {
	public static final Plane3DCoordinateSubsystem xyPlaneSubsystem = Plane3D.xyPlane.getCoordinateSubsystem();
	    
	/** Find shortest path through boundaries
	 * 
	 * @param leadIn starting point
	 * @param boundaries boundaries to go through (boundaries between polygons in a polygon path)
	 * @param leadOut ending point
	 * @return crossings through boundaries, some boundary crossings may be skipped 
	 *         if they are implied by the previous and following crossing (on a line between them)  
	 */
    public static List<ILocated> findShortestPathCrossings(
    		ILocated leadIn,
    		List<NavMeshBoundary> boundaries,
    		ILocated leadOut
    ) {
      	List<ILocated> crossings = Lists.newArrayList();
    	
    	if ( boundaries.isEmpty() ) {
    		return crossings;
    	}
    	
    	{
    		// DEBUG
    		FunnelDebug.debugFunnel_DrawInit(leadIn, boundaries, leadOut);
    	}
    	
    	Funnel gatewayFunnel = Funnel.createFromBoundary(
    		leadIn,
    		boundaries.get(0),
    		0
    	);
    	
    	{
        	// DEBUG
        	FunnelDebug.debugFunnel_drawGateway(gatewayFunnel);
        }
	    
        // now we will go further over the boundaries until the boundary falls outside the funnel or until we find target point inside the funnel
        
        for ( int index = 1; index < boundaries.size(); ++index ) {          
        	
            Funnel newGatewayFunnel = Funnel.createFromBoundary(
        		gatewayFunnel.getVantagePoint(),
        		boundaries.get(index),
        		index
        	);
            FunnelRay newLeftRay = newGatewayFunnel.getLeftRay();
            FunnelRay newRightRay = newGatewayFunnel.getRightRay();
            
            {
            	// DEBUG
            	FunnelDebug.debugFunnel_NextStep(gatewayFunnel, newGatewayFunnel, leadIn, boundaries, leadOut, crossings);
            }
           
            FunnelZone newLeftRayZone = gatewayFunnel.determineZone( newLeftRay.getCrossing().getLocation().asPoint3D() );
            switch ( newLeftRayZone ) {
            case INSIDE:
            	// update the funnel            	
            	gatewayFunnel = new Funnel( newLeftRay, gatewayFunnel.getRightRay() );
            	break;
            case OUTSIDE_LEFT:
                // do nothing
            	break;
            case OUTSIDE_RIGHT:
            	// the path bends to the right, add right ray's crossing to the path and restart from it
            	Location crossing =  gatewayFunnel.getRightRay().getCrossing();
            	crossings.add( crossing );
            	
            	{
            		// DEBUG
            		FunnelDebug.debugFunnel_NewCrossing(crossing);
            	}
            	
            	index = gatewayFunnel.getRightRay().getIndex() + 1;
            	
            	while ( index < boundaries.size()
            			&&
            			(crossing.equals( boundaries.get(index).getSourceVertex() )
            			||
            			crossing.equals( boundaries.get(index).getDestinationVertex()) )
            	) {
            		// the next boundary shares the vertex we picked as a crossing so skip it            		
            		++index;
            	}
            	
            	if (index >= boundaries.size()) {
            		// WE'RE BENDING ON THE LAST BOUNDARY...
            		break;
            	}
            	
                gatewayFunnel = Funnel.createFromBoundary( 
            		crossing,
            		boundaries.get( index ),
            		index
                );
            	continue; // restart from it
            default:
            	throw new AssertionError("Unrecognized FunnelZone.");
            }
                       
            FunnelZone newRightRayZone = gatewayFunnel.determineZone( newRightRay.getCrossing().getLocation().asPoint3D() );
            switch ( newRightRayZone ) {
            case INSIDE:
            	// update the funnel
            	gatewayFunnel = new Funnel( gatewayFunnel.getLeftRay(), newRightRay );
            	break;
            case OUTSIDE_LEFT:
            	// the path bends to the left, add left ray's crossing to the path and restart from it
            	Location crossing = gatewayFunnel.getLeftRay().getCrossing();
            	
            	{
            		// DEBUG
            		FunnelDebug.debugFunnel_NewCrossing(crossing);
            	}
            	
            	crossings.add( crossing );
            	
            	index = gatewayFunnel.getLeftRay().getIndex() + 1;
            	
            	while ( index < boundaries.size()
            			&&
            			(crossing.equals( boundaries.get(index).getSourceVertex() )
            			||
            			crossing.equals( boundaries.get(index).getDestinationVertex()) )
            	) {
            		// the next boundary shares the vertex we picked as a crossing so skip it
            		++index;
            	}
            	
            	if (index >= boundaries.size()) {
            		// WE'RE BENDING ON THE LAST BOUNDARY...
            		break;
            	}
            	
                gatewayFunnel = Funnel.createFromBoundary( 
            		crossing,
            		boundaries.get( index ),
            		index
                );
                continue; // restart from it
            case OUTSIDE_RIGHT:
            	// do nothing
            	break;
            default:
            	throw new AssertionError("Unrecognized FunnelZone.");
            }
        }
        
        // potentially add a crossing for the lead out point
        switch ( gatewayFunnel.determineZone(leadOut.getLocation().asPoint3D()) ) {
        case OUTSIDE_LEFT:
        	// the path bends to the left
	        {
	    		// DEBUG
	    		FunnelDebug.debugFunnel_NewCrossing(gatewayFunnel.getLeftRay().getCrossing());
	    	}
            crossings.add( gatewayFunnel.getLeftRay().getCrossing());
        	break;
        case OUTSIDE_RIGHT:
        	// the path bends to the right
	        {
	    		// DEBUG
	    		FunnelDebug.debugFunnel_NewCrossing(gatewayFunnel.getRightRay().getCrossing());
	    	}
        	crossings.add( gatewayFunnel.getRightRay().getCrossing());
        	break;
        case INSIDE:
        	// do nothing
        	break;
        default:
        	throw new AssertionError("Unrecognized FunnelZone.");
        }
        
        return crossings;
    }
}
