package cz.cuni.amis.pogamut.ut2004.agent.navigation.levelGeometry;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;

public class LevelGeometryProfiler {

	public static void main(String[] args) {
		final LevelGeometry levelGeometry2 = LevelGeometryCache.getLevelGeometry( "DM-Flux2" );
		assertNotNull( levelGeometry2 );
		

		ObjectInputStream raycastRequestSource = null;
		try {
			raycastRequestSource = new ObjectInputStream( new FileInputStream("DM-Flux2_raycastRequestDump.bin") );
		} catch (Exception e) {
		}
		assertNotNull( "Bad raycast request source", raycastRequestSource);
		
		List<RaycastRequest> requests = new ArrayList<RaycastRequest>();
		while (true) {
			try {
				Location from = (Location) raycastRequestSource.readObject();
				Location to = (Location) raycastRequestSource.readObject();
				requests.add( new RaycastRequest( from, to ) );
			} catch (ClassNotFoundException e) {
				fail("Bad raycast request source");
			} catch (IOException e) {
				break;
			}
		}
		
		System.out.println("Start profiling");
		System.gc();
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			System.out.println("Woken up");
		}
		
		long startNanoTime = System.nanoTime();
		for (int i=0; i<200; ++i) {
			for (RaycastRequest request : requests ) {
				levelGeometry2.rayCast( request.from, request.to );
			}
		}
		long endNanoTime = System.nanoTime();
		
		System.out.println(((endNanoTime-startNanoTime)/1000)+" us");
		for (;;) {
			System.gc();
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				System.out.println("Woken up");
			}
		}
	}
}
