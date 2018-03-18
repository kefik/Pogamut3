package cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh;

import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.base3d.worldview.object.Rotation;
import cz.cuni.amis.pogamut.base3d.worldview.object.Velocity;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.analysis.internal.NavMeshBoundaryInfo;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.analysis.internal.construction.NodeConstructionCoordinator;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.Identifiers.EdgeId;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.Identifiers.PolygonId;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.Identifiers.VertexId;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.NavMeshBoundary;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.NavMeshEdge;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.NavMeshPolygon;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.NavMeshVertex;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.OffMeshEdge;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.OffMeshPoint;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.internal.NavMeshNavGraphGlue;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPointMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPointNeighbourLink;

public class NavMeshMinimalTest {
	@Test
	public void testMinimalObjectModel() {
		
		try {
			final HashMap<UnrealId, NavPoint> navGraph = Maps.newHashMap();
			
			// vertices of polygon ABC, ABD and JKL
			VertexId vertexAId = new VertexId( 0 );
			VertexId vertexBId = new VertexId( 1 );
			VertexId vertexCId = new VertexId( 2 );
			VertexId vertexDId = new VertexId( 3 );
			
			PolygonId polygonABCId = new PolygonId( 1 );
			EdgeId edgeABC_ABId = new EdgeId( 10 );
			EdgeId edgeABC_BCId = new EdgeId( 11 );
			EdgeId edgeABC_CAId = new EdgeId( 12 );
						
			PolygonId polygonABDId = new PolygonId( 2 );
			EdgeId edgeABD_ABId = new EdgeId( 20 );
			EdgeId edgeABD_BDId = new EdgeId( 21 );
			EdgeId edgeABD_DAId = new EdgeId( 22 );
					
			NavMeshBoundaryInfo boundaryInfoAB = new NavMeshBoundaryInfo( vertexAId, vertexBId, polygonABCId, 0, polygonABDId, 0 );
	
			NavPoint offMeshNavPointABDSrc = new NavPointMessage(
					UnrealId.get("MinimalTest.OffMeshNavPointSrc"),
					new Location( 20, 20, 20 ),
					new Velocity( 0, 0, 0 ),
					true, null, null, false, false, null, null,
					false, false, false, false, 0, false, 0, false,
					false, false, false, false, false, false, false,
					new Rotation( 0, 0, 0), 
					false, false, null,
					new HashMap<UnrealId,NavPointNeighbourLink>(),
					new HashMap<UnrealId,NavPointNeighbourLink>(),
					null
			);
			navGraph.put( offMeshNavPointABDSrc.getId(), offMeshNavPointABDSrc );
			
			NavPoint offMeshNavPointABDDest = new NavPointMessage(
					UnrealId.get("MinimalTest.OffMeshNavPointDest"),
					new Location( 30, 30, 30 ),
					new Velocity( 0, 0, 0 ),
					true, null, null, false, false, null, null,
					false, false, false, false, 0, false, 0, false,
					false, false, false, false, false, false, false,
					new Rotation( 0, 0, 0), 
					false, false, null,
					new HashMap<UnrealId,NavPointNeighbourLink>(),
					new HashMap<UnrealId,NavPointNeighbourLink>(),
					null
			);
			navGraph.put( offMeshNavPointABDDest.getId(), offMeshNavPointABDDest );
			
			NavPointNeighbourLink offMeshNeighbourLink = new NavPointNeighbourLink(
				UnrealId.get("MinimalTest.OffMeshNeighbourLink"),
				0, 100, 200, 0.0, "", false, false, null, false, false, 1.0,
				offMeshNavPointABDSrc, offMeshNavPointABDDest
			);
			offMeshNavPointABDSrc.getOutgoingEdges().put( offMeshNavPointABDDest.getId(), offMeshNeighbourLink );
			offMeshNavPointABDDest.getIncomingEdges().put( offMeshNavPointABDSrc.getId(), offMeshNeighbourLink );
			
						
			ObjectOutputStream objectOutputStream = new ObjectOutputStream( new ByteArrayOutputStream() );
			NodeConstructionCoordinator coordinator = new NodeConstructionCoordinator();
			NavMeshNavGraphGlue navGraphGlue = new NavMeshNavGraphGlue( 
				new Function<UnrealId, NavPoint>() {
	
					@Override
					public NavPoint apply(UnrealId input) {
						return navGraph.get( input );
					}
				}
			);
			
			NavMeshVertex vertexA = new NavMeshVertex(
					vertexAId,
					new Location(  0,  0,  0 ),
					ImmutableMap.of( polygonABCId, 0, polygonABDId, 0 ),
					ImmutableList.of( edgeABC_CAId, edgeABC_ABId, edgeABD_DAId, edgeABD_ABId ),
					true, 
					coordinator
			);
			NavMeshVertex vertexB = new NavMeshVertex(
					vertexBId,
					new Location(  0,  1,  0 ),
					ImmutableMap.of( polygonABCId, 1, polygonABDId, 1 ),
					ImmutableList.of( edgeABC_ABId, edgeABC_BCId, edgeABD_ABId, edgeABD_BDId ),
					true,
					coordinator
			);
			NavMeshVertex vertexC = new NavMeshVertex(
					vertexCId,
					new Location(  0,  0,  1 ),
					ImmutableMap.of( polygonABCId, 2 ),
					ImmutableList.of( edgeABC_BCId, edgeABC_CAId ),
					true,
					coordinator
			);
			NavMeshVertex vertexD = new NavMeshVertex(
					vertexDId,
					new Location(  0,  0, -1 ),
					ImmutableMap.of( polygonABDId, 2 ),
					ImmutableList.of( edgeABD_BDId, edgeABD_DAId ),
					true,
					coordinator
			);
			
			coordinator.addVertex( vertexA );
			coordinator.addVertex( vertexB );
			coordinator.addVertex( vertexC );
			coordinator.addVertex( vertexD );
			
			NavMeshBoundary boundaryAB = new NavMeshBoundary(
					edgeABC_ABId,
					edgeABD_ABId,
					coordinator
			);
	
			coordinator.addBoundary( boundaryInfoAB, boundaryAB );
			
			NavMeshEdge edgeABC_AB = new NavMeshEdge( edgeABC_ABId, 0, polygonABCId, vertexAId, vertexBId, boundaryInfoAB, coordinator );
			NavMeshEdge edgeABC_BC = new NavMeshEdge( edgeABC_BCId, 1, polygonABCId, vertexBId, vertexCId, null, coordinator );
			NavMeshEdge edgeABC_CA = new NavMeshEdge( edgeABC_CAId, 2, polygonABCId, vertexCId, vertexAId, null, coordinator );
	
			coordinator.addPolygonEdge( edgeABC_AB );
			coordinator.addPolygonEdge( edgeABC_BC );
			coordinator.addPolygonEdge( edgeABC_CA );
			
			NavMeshPolygon polygonABC = new NavMeshPolygon(
					polygonABCId,
					ImmutableList.of( vertexAId, vertexBId, vertexCId ),
					ImmutableList.of( edgeABC_ABId, edgeABC_BCId, edgeABC_CAId ),
					ImmutableMap.of( 0, boundaryInfoAB ),
					ImmutableMap.of( polygonABDId, boundaryInfoAB ),
					new ArrayList<NavPoint>(),
					coordinator
			);
			
			coordinator.addPolygon(polygonABC);
			
			NavMeshEdge edgeABD_AB = new NavMeshEdge( edgeABD_ABId, 0, polygonABCId, vertexAId, vertexBId, boundaryInfoAB, coordinator);
			NavMeshEdge edgeABD_BD = new NavMeshEdge( edgeABD_BDId, 1, polygonABCId, vertexBId, vertexDId, null, coordinator);
			NavMeshEdge edgeABD_DA = new NavMeshEdge( edgeABD_DAId, 2, polygonABCId, vertexDId, vertexAId, null, coordinator);
			
			coordinator.addPolygonEdge( edgeABD_AB );
			coordinator.addPolygonEdge( edgeABD_BD );
			coordinator.addPolygonEdge( edgeABD_DA );
			
			OffMeshPoint offMeshPointABDSrc = new OffMeshPoint(
					navGraphGlue,
					offMeshNavPointABDSrc,
					polygonABDId,
					new ArrayList<NavPointNeighbourLink>(),
					new ArrayList<NavPointNeighbourLink>(),
					coordinator 
			);
			
			coordinator.addOffMeshPoint( offMeshPointABDSrc );
			
			NavMeshPolygon polygonABD = new NavMeshPolygon(
					polygonABDId, 
					ImmutableList.of( vertexAId, vertexBId, vertexDId ),
					ImmutableList.of( edgeABD_ABId, edgeABD_BDId, edgeABD_DAId ),
					ImmutableMap.of( 0, boundaryInfoAB ),
					ImmutableMap.of( polygonABCId, boundaryInfoAB ),
					ImmutableList.of( offMeshNavPointABDSrc ),
					coordinator
			);
			
			coordinator.addPolygon(polygonABD);
			
			
			OffMeshPoint offMeshPointABDDest = new OffMeshPoint(
					navGraphGlue,
					offMeshNavPointABDDest,
					polygonABDId,
					new ArrayList<NavPointNeighbourLink>(),
					new ArrayList<NavPointNeighbourLink>(),
					coordinator 
			);
			
			coordinator.addOffMeshPoint( offMeshPointABDDest );
			
			OffMeshEdge offMeshEdgeABD = new OffMeshEdge(
					offMeshNeighbourLink,
					coordinator
			);
			coordinator.addOffMeshEdge( offMeshNeighbourLink, offMeshEdgeABD );
					
			coordinator.runDeferredConstructors();
			
			objectOutputStream.writeObject( edgeABC_AB );
			objectOutputStream.writeObject( edgeABC_BC );
			objectOutputStream.writeObject( edgeABC_CA );
			objectOutputStream.writeObject( edgeABD_AB );
			objectOutputStream.writeObject( edgeABD_BD );
			objectOutputStream.writeObject( edgeABD_DA );
			objectOutputStream.writeObject( offMeshPointABDSrc );
			objectOutputStream.writeObject( offMeshPointABDDest );
			objectOutputStream.writeObject( offMeshEdgeABD );
			objectOutputStream.writeObject( boundaryAB );
			objectOutputStream.writeObject( polygonABC );
			objectOutputStream.writeObject( polygonABD );
			
		} catch (IOException e) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			e.printStackTrace( new PrintStream(baos) );
			String stackTrace;
			try {
				stackTrace = new String(baos.toByteArray(), "utf8" /*java.nio.charset.StandardCharsets.UTF_8*/);
			} catch (UnsupportedEncodingException e1) {
				throw new RuntimeException("Unsupported charset utf8.", e1);
			}
			fail( "Serializer setup failed.\n"+stackTrace );
		}
	}
}

