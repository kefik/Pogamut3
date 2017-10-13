package cz.cuni.amis.pogamut.ut2004.agent.navigation.levelGeometry;

import static org.junit.Assert.fail;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;

public class RaycastDataFileTools {
	static void save( List<PrecomputedRaycastResult> data, String fileName ) {
		try {
			ObjectOutputStream raycastDataFile = new ObjectOutputStream( new FileOutputStream("DM-Flux2_raycastDump.bin") );
			
			for (PrecomputedRaycastResult result : data ) {
				raycastDataFile.writeObject(result.request.from);
				raycastDataFile.writeObject(result.request.to);
				raycastDataFile.writeDouble(result.hitDistance);
			}
			
			raycastDataFile.close();
		} catch (IOException e) {
			fail("Failed to save data.");
		}
	}
	
	static List<PrecomputedRaycastResult> load( String fileName ) {
		List<PrecomputedRaycastResult> data = new ArrayList<PrecomputedRaycastResult>();
		
		try {
			ObjectInputStream dataFile = new ObjectInputStream( new FileInputStream(fileName) );
			
			while (true) {
				try {
					Location from = (Location) dataFile.readObject();
					Location to = (Location) dataFile.readObject();
					double hitDistance = dataFile.readDouble();
					data.add( new PrecomputedRaycastResult( from, to, hitDistance ) );
				} catch (IOException e) {
					break;
				}
			}
			
			dataFile.close();
		} catch (IOException e) {
			fail("Failed to load data.");
		} catch (ClassNotFoundException e) {
			fail("Failed to load data.");
		}
		
		return data;
	}
	
	static List<RaycastRequest> loadRequestFile( String fileName ) {
		List<RaycastRequest> data = new ArrayList<RaycastRequest>();
		
		try {
			ObjectInputStream dataFile = new ObjectInputStream( new FileInputStream(fileName) );
			
			while (true) {
				try {
					Location from = (Location) dataFile.readObject();
					Location to = (Location) dataFile.readObject();
					data.add( new RaycastRequest( from, to ) );
				} catch (IOException e) {
					break;
				}
			}
			
			dataFile.close();
		} catch (IOException e) {
			fail("Failed to load data.");
		} catch (ClassNotFoundException e) {
			fail("Failed to load data.");
		}
		
		return data;
	}
	
	static List<RaycastRequest> resultsToRequests( List<PrecomputedRaycastResult> results ) {
		return Lists.transform(
				results,
				new Function<PrecomputedRaycastResult, RaycastRequest>() {
					@Override
					public RaycastRequest apply(PrecomputedRaycastResult input) {
						return input.request;
					}
				}
			);
	}
}
