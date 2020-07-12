package cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.pathPlanner.AStar;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.Sets;
import com.google.inject.internal.Maps;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.NavMesh;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.INavMeshAtom;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.NavMeshPolygon;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.OffMeshPoint;

/** A* heuristic based on path distance and estimate of distance via straight line
 *
 * Time complexity of distance estimation increases linearly with the count of teleports in the map.
 */
public class NavMeshAStarDistanceHeuristic implements INavMeshAStarHeuristic {

	protected NavMesh navMesh;
	// teleporter -> distance from teleporter exit to the cached destination
	protected HashMap<OffMeshPoint, Double> teleporterToDistanceToCachedDestinationMap = null; 
	protected Location cachedDestination = null; // cached destination for which teleporter exit distances were computed
	
	public NavMeshAStarDistanceHeuristic( NavMesh navMesh ) {
		this.navMesh = navMesh;
	}
	
	/** Compute cost of traveling an existing A* node to an adjacent atom
	 * 
	 * @param current current node
	 * @param adjacentAtom atom, which is adjacent to the atom represented by the current node	 * @return cost
	 */
	public double computeCost(NavMeshAStarNode from, INavMeshAtom adjacentAtom) {
		INavMeshAtom fromAtom = from.getAtom();
		if ( fromAtom instanceof NavMeshPolygon && adjacentAtom instanceof NavMeshPolygon ) {
			return fromAtom.getLocation().getDistance( adjacentAtom.getLocation() );
		} else if ( fromAtom instanceof OffMeshPoint && adjacentAtom instanceof OffMeshPoint ) {
			OffMeshPoint fromOffMeshPoint = (OffMeshPoint) fromAtom;
			if ( fromOffMeshPoint.getNavPoint().isTeleporter() ) {
				return 0;
			} else {
				return fromAtom.getLocation().getDistance( adjacentAtom.getLocation() );
			}
		} else {
			// polygon and off mesh point in the polygon
			// consider this distance zero, since we don't know how much we have to deviate from the polygon's center
			return 0;
		}
	}

	/** Estimate cost of traveling between two arbitrary atoms
	 * 
	 * @return estimated cost
	 */
	public double estimateCost(INavMeshAtom from, INavMeshAtom to) {
		ensureTeleporterDistancesUpToDate( to.getLocation() );
		
		double retval = from.getLocation().getDistance( to.getLocation() );	
		for ( Entry<OffMeshPoint, Double> entry : teleporterToDistanceToCachedDestinationMap.entrySet() ) {
			double distanceViaTeleporter = (
				from.getLocation().getDistance( entry.getKey().getLocation() )
				+
				entry.getValue()
			);
			retval = Math.min( retval, distanceViaTeleporter );
		}
		
		return retval;
	}

	@Override
	public NavMeshAStarNode extend( NavMeshAStarNode node, INavMeshAtom adjacentAtom, INavMeshAtom destinationAtom) {
		
		double costFromStartToAdjacent = 0;
		if ( node != null ) {
			costFromStartToAdjacent = node.getCostFromStart() + computeCost( node, adjacentAtom );
		}
		double estimatedCostFromAdjacentToDestination = estimateCost( adjacentAtom, destinationAtom );
		
		return new NavMeshAStarNode( node, adjacentAtom, costFromStartToAdjacent, estimatedCostFromAdjacentToDestination );
	}

	protected void ensureTeleporterDistancesUpToDate( Location destination ) {
		if ( cachedDestination != null && cachedDestination.equals( destination ) ) {
			return;
		}
		
		cachedDestination = destination;
		
		// ensure teleporter locations have been initialized
		if ( teleporterToDistanceToCachedDestinationMap == null ) {
			teleporterToDistanceToCachedDestinationMap = Maps.newHashMap();
			for ( OffMeshPoint point : navMesh.getOffMeshPoints() ) {
				if ( point.getNavPoint().isTeleporter() ) {
					teleporterToDistanceToCachedDestinationMap.put( point, null );
				}
			}
		}
		
		// the complication here is that the agent may travel through multiple teleporters
		
		HashMap<Location, Double> sinkToDistanceMap = Maps.newHashMap();
		sinkToDistanceMap.put( destination, 0.0 );
		Set<OffMeshPoint> unusedTeleporters = Sets.newHashSet( teleporterToDistanceToCachedDestinationMap.keySet() );
		while ( unusedTeleporters.size() > 0 ) {
			// breadth search from the destination for the next closest teleport
			
			OffMeshPoint teleporterWithClosestExit = null;
			Double teleporterWithClosestExitCostEstimate = Double.POSITIVE_INFINITY;
			for ( OffMeshPoint candidateTeleporter : unusedTeleporters ) {
				assert( candidateTeleporter.getOutgoingEdges().size() == 1 ); // teleporter should have single exit
				Location candidateTeleporterExit = candidateTeleporter.getOutgoingEdges().iterator().next().getTo().getLocation();
				double candidateCostEstimate = Double.POSITIVE_INFINITY;
				for ( Entry<Location, Double> sinkEntry : sinkToDistanceMap.entrySet() ) {
					double sinkCostEstimate = candidateTeleporterExit.getDistance( sinkEntry.getKey() ) + sinkEntry.getValue();
					candidateCostEstimate = Math.min( candidateCostEstimate, sinkCostEstimate );
				}
				
				if ( candidateCostEstimate < teleporterWithClosestExitCostEstimate ) {
					teleporterWithClosestExit = candidateTeleporter;
					teleporterWithClosestExitCostEstimate = candidateCostEstimate;
				}
			}
			
			sinkToDistanceMap.put( teleporterWithClosestExit.getLocation(), teleporterWithClosestExitCostEstimate );
			teleporterToDistanceToCachedDestinationMap.put( teleporterWithClosestExit, teleporterWithClosestExitCostEstimate );
			unusedTeleporters.remove( teleporterWithClosestExit );
		}
	}
}
