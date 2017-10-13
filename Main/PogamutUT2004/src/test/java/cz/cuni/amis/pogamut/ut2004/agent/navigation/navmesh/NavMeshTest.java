package cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.google.common.base.Suppliers;
import com.google.common.collect.Lists;

import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navgraph.NavGraph;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.grounder.NavMeshDropGrounder;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.INavMeshAtom;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.NavMeshPolygon;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.NavMeshVertex;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.OffMeshPoint;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.old.OldINavMeshAtom;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.old.OldNavMesh;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.old.OldNavMeshPolygon;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.old.OldOffMeshPoint;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.pathPlanner.AStar.NavMeshAStarDistanceHeuristic;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.pathPlanner.AStar.NavMeshAStarPathPlanner;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.pathPlanner.AStar.NavMeshSegmentedAStarPathPlanner;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfoMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;

public class NavMeshTest {

	protected String map = "DM-Flux2";
	
	protected OldNavMesh vanillaNavMesh = null;
	protected NavMesh navMesh = null;
	protected NavMeshDropGrounder dropGrounder = null;
	@SuppressWarnings("deprecation")
	protected NavMeshSegmentedAStarPathPlanner segmentedAStarPathPlanner = null;
	protected NavMeshAStarPathPlanner aStarPathPlanner = null;
	
	@SuppressWarnings("deprecation")
	@Test
	public void testMap() {
		
		final NavGraph navGraph = new NavGraph( new File( map + ".navgraph" ) );
		
		vanillaNavMesh = new OldNavMesh( 
			new OldNavMesh.INavPointWorldView() {
				@Override
				public Map<UnrealId, NavPoint> get() {
					return navGraph.navPointsById;
				}
			},
			null
		);
		GameInfo gameInfo = new GameInfoMessage( "DM", map, false, 0, 0, 0, 0, 0, null, null, null, null, false, false, null, 0, null, 0, 0, "" );
		vanillaNavMesh.load( gameInfo, false);
		
		navMesh = new NavMesh( null );
		NavMeshCache.loadNavMesh( navMesh, navGraph.navPointsById, map );
		assertNotNull( navMesh );
		
		dropGrounder = new NavMeshDropGrounder(navMesh);
		segmentedAStarPathPlanner = new NavMeshSegmentedAStarPathPlanner(
			Suppliers.ofInstance(navGraph.navPointsById.values()),
			dropGrounder,
			navMesh,
			new LogCategory("NavMeshSegmentedAStarPathPlanner")
		);
		aStarPathPlanner = new NavMeshAStarPathPlanner(
				dropGrounder,
				navMesh,
				new NavMeshAStarDistanceHeuristic(navMesh),
				new LogCategory("NavMeshAStarPathPlanner")
		);
				
		
		List<Query> queries = Lists.newArrayList();
		queries.add( new Query( new Location( 1855, 295, -325 ), new Location( -747, 305, -317 ) ) );
		queries.add( new Query( new Location( 1855, 295, -325 ), new Location( -165, 1152, 81 ) ) );
		StringBuilder fails = new StringBuilder();
		for ( Query query : queries ) {
			Location from = query.src;
			Location to = query.dst;
			
			if ( !compareGrounding( from ) ) {
				fails.append( "Grounding does not match "+from+"\n" );
			}
			
			if ( !compareGrounding( to ) ) {
				fails.append( "Grounding does not match "+to+"\n" );
			}
			
			if ( !comparePolygonPathing( from, to ) ) {
				fails.append( "Polygon pathing does not match "+from+" -> "+to+"\n" );
			}
			
			if ( !comparePathing( from, to ) ) {
				fails.append( "Pathing does not match "+from+" -> "+to+"\n" );
			}
		}
		
		assertTrue( fails.toString(), fails.toString().isEmpty() );
	}
	
	static class Query {
		Location src;
		Location dst;
		
		public Query( Location src, Location dst  ) {
			this.src = src;
			this.dst = dst;
		}
	}
	
	@SuppressWarnings("deprecation")
	boolean comparePolygonPathing( Location src, Location dst ) {
		try {
			List<OldINavMeshAtom> vanillaPath = vanillaNavMesh.getPolygonPath( src,  dst );
			List<INavMeshAtom> segmentedPath = segmentedAStarPathPlanner.getPolygonPath( src, dst );
			List<INavMeshAtom> path = aStarPathPlanner.getAtomPath( src,  dst );

			
			if ( vanillaPath.size() != segmentedPath.size() 
				 ||
				 segmentedPath.size() != path.size() 
			) {
				return false;
			}
			
			for ( int i = 0; i < segmentedPath.size(); ++i ) {
				if ( vanillaPath.get(i) instanceof OldNavMeshPolygon != ( segmentedPath.get(i) instanceof NavMeshPolygon )
					 ||
					 ( segmentedPath.get(i) instanceof NavMeshPolygon ) != ( path.get(i) instanceof NavMeshPolygon )
				) {
					return false;
				}
				if ( vanillaPath.get(i) instanceof OldNavMeshPolygon ) {
					OldNavMeshPolygon vanillaPolygon = (OldNavMeshPolygon) vanillaPath.get(i);
					NavMeshPolygon segmentedPolygon = (NavMeshPolygon) segmentedPath.get(i);
					NavMeshPolygon polygon = (NavMeshPolygon) path.get(i);
					
					if ( !comparePolygons( vanillaPolygon, segmentedPolygon )
						 ||
						 segmentedPolygon != polygon
					) {
						return false;
					}
				} else {
					OldOffMeshPoint vanillaOffMeshPoint = (OldOffMeshPoint) vanillaPath.get(i);
					OffMeshPoint segmentedOffMeshPoint = (OffMeshPoint) segmentedPath.get(i);
					OffMeshPoint offMeshPoint = (OffMeshPoint) path.get(i);
										
					if ( !vanillaOffMeshPoint.getNavPointId().equals( segmentedOffMeshPoint.getNavPoint().getId() )
						 ||
						 segmentedOffMeshPoint != offMeshPoint
					) {
						return false;
					}
				}
			}
			
			return true;
		} catch (Exception e) {	
			e.printStackTrace();
			return false;
		}
	}
	
	
	@SuppressWarnings("deprecation")
	boolean comparePathing( Location from, Location to ) {
		
		try {
			List<ILocated> vanillaPath = vanillaNavMesh.getPath(from, to );
			List<ILocated> segmentedPath = segmentedAStarPathPlanner.getPath( from,  to );
			List<ILocated> path = aStarPathPlanner.getPath( from,  to );
			
			if ( vanillaPath.size() != segmentedPath.size() || segmentedPath.size() != path.size() ) {
				return false;
			}
			
			for ( int i = 0; i < segmentedPath.size(); ++i ) {
				if ( !vanillaPath.get(i).equals( segmentedPath.get(i) ) ||
				     !segmentedPath.get(i).equals( path.get(i) )	
				) {
					return false;
				}
			}
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	boolean compareGrounding( Location location ) {
		try {
			OldNavMeshPolygon vanillaPolygon = vanillaNavMesh.getNearestPolygon( location );
			NavMeshPolygon polygon = dropGrounder.forceGround( location );
			return comparePolygons( vanillaPolygon, polygon );
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	boolean comparePolygons( OldNavMeshPolygon vanillaPolygon, NavMeshPolygon polygon ) {
		List<Location> vanillaVertices = getVanillaPolygonVertices(vanillaPolygon);
		List<NavMeshVertex> vertices = polygon.getVertices();
		
		if ( vanillaVertices.size() != vertices.size() ) {
			return false;
		}
		
		for ( int i=0; i < vertices.size(); ++i ) {
			if ( !vanillaVertices.get(i).equals( vertices.get(i).getLocation() ) ) {
				return false;
			}
		}
		
		return true;
	}
	
	List<Location> getVanillaPolygonVertices( OldNavMeshPolygon vanillaPolygon ) {
		List<Location> retval = Lists.newArrayList();
		for ( int vertexId : vanillaNavMesh.getPolygon( vanillaPolygon.getPolygonId() ) ) {
			retval.add( new Location( vanillaNavMesh.getVertex( vertexId ) ) );
		}
		return retval;
	}
}
