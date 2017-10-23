package cz.cuni.amis.pogamut.ut2004.agent.navigation.levelGeometry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

import math.geom2d.Point2D;

public class LevelGeometryTest {

	protected String map = "DM-Flux2";
	protected boolean createResultDump = false;
	
	@Test
	public void testLevelGeometry() {
		final LevelGeometry levelGeometry2 = LevelGeometryCache.getLevelGeometry( map );
		assertNotNull( levelGeometry2 );
		final LevelGeometryNaive levelGeometryNaive = LevelGeometryCacheNaive.getLevelGeometry( map );
		assertNotNull( levelGeometryNaive );
		
		if ( createResultDump ) {
			List<RaycastRequest> requests = RaycastDataFileTools.loadRequestFile( map+"_raycastRequestDump.bin");
			levelGeometryNaive.saveReferenceResults(requests, map+"_raycastDump.bin" );
		}
		
		List<PrecomputedRaycastResult> results = RaycastDataFileTools.load( map+"_raycastDump.bin" );
		List<RaycastRequest> requests = RaycastDataFileTools.resultsToRequests(results);
		
		//compareRaycast( 10, requests, results, levelGeometry2, levelGeometryNaive);
		
		System.gc();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			System.out.println("Woken up");
		}
		
		RequestSuiteRun<RayCastResult> gosuSuite = new RequestSuiteRun<RayCastResult>( "Gosu", requests ) {
			@Override
			protected RayCastResult raycast(RaycastRequest request) {
				return levelGeometry2.rayCast( request.from, request.to );
			}
		};
		
		System.out.println( gosuSuite );
		
		int goodResults = 0;
		for (int i = 0; i < requests.size(); ++i ) {
			RaycastRequest request = requests.get(i);
			double precomputedHitDistance = results.get(i).hitDistance;
			RayCastResult gosuResult = gosuSuite.requestToResultMap.get(request);
			if (
				Double.isFinite(precomputedHitDistance) == (gosuResult.isHit())
				&&
				( Double.isNaN(precomputedHitDistance) || Math.abs(precomputedHitDistance-gosuResult.hitDistance) < 0.001 )
			) {
				++goodResults;
			}
		}
		System.out.println(""+goodResults+" good results out of "+requests.size()+" requests.");
		
		for (int i = 0; i < requests.size(); ++i ) {
			RaycastRequest request = requests.get(i);
			double precomputedHitDistance = results.get(i).hitDistance;
			RayCastResult gosuResult = gosuSuite.requestToResultMap.get(request);
			boolean isGosuHit = gosuResult.hitLocation != null;
			assertEquals(
				"Request #" + i + " hit status mismatch",
				Double.isFinite(precomputedHitDistance),
				isGosuHit
			);
			if ( Double.isNaN(precomputedHitDistance) && !isGosuHit ) {
				continue;
			}
			assertEquals(
				"Request #" + i + " distance mismatch", 
				precomputedHitDistance,
				gosuResult.hitDistance,
				0.0000000001
			);
		}
	}
	
	protected void compareRaycast( int requestIndex, List<RaycastRequest> requests, List<PrecomputedRaycastResult> precomputedResults, LevelGeometry levelGeometry2, LevelGeometryNaive levelGeometryNaive ) {
		RaycastRequest request = requests.get(requestIndex);
		LevelGeometryNaive.RaycastResult naiveResult = levelGeometryNaive.raycast( request.from,  request.to );
		RayCastResult gosuResult = levelGeometry2.rayCast( request.from,  request.to );
		
		double naiveDistance = Double.NaN;
		if ( naiveResult.hit ) {
			naiveDistance = naiveResult.hitDistance;
		}
		
		double gosuSignedBoundaryDistance = Double.NaN;
		double gosuDistance = Double.NaN;
		int gosuTriangleIndex = -1;
		if ( gosuResult.isHit() ) {
			gosuDistance = gosuResult.hitDistance;
			Point2D gosuHitLocation2D = gosuResult.hitTriangle.planarPolygon.getPlane().getCoordinateSubsystem().project( gosuResult.hitLocation.asPoint3D() );
			gosuSignedBoundaryDistance = gosuResult.hitTriangle.planarPolygon.getPolygonIn2d().getBoundary().getSignedDistance(gosuHitLocation2D);
			for (int i=0; i < levelGeometry2.triangles.size(); ++i ) {
				if ( levelGeometry2.triangles.get(i) == gosuResult.hitTriangle ) {
					gosuTriangleIndex = i;
				}
			}
		}
			
		System.out.println( "Request#"+requestIndex );
		
		System.out.println( "From " + request.from + " to " + request.to + " hit " + naiveResult.hitLocation );
		
		System.out.println( "Naive   is hit = "+naiveResult.hit );
		System.out.println( "Gosu    is hit = "+(gosuResult.hitLocation != null) );
		System.out.println( "Precomp is hit = "+Double.isFinite(precomputedResults.get(requestIndex).hitDistance) );
		System.out.println( "Naive hit triangle number: "+naiveResult.hitTriangle );
		System.out.println( "Gosu hit triangle number: "+gosuTriangleIndex );
		System.out.println( "Naive   hit distance = "+naiveDistance );
		System.out.println( "Gosu    hit distance = "+gosuDistance );
		System.out.println( "Precomp hit distance = "+precomputedResults.get(requestIndex).hitDistance );
		System.out.println( "Gosu    hit distance to triangle boundary = "+gosuSignedBoundaryDistance );
	}
}
