package cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Function;

import cz.cuni.amis.pogamut.base.agent.impl.AgentId;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.pogamut.base.utils.logging.LogFormatter;
import cz.cuni.amis.pogamut.base.utils.logging.LogPublisher;
import cz.cuni.amis.pogamut.base.utils.logging.LogPublisher.ConsolePublisher;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;

/**
 * Used to speed up bots starting time on the same JVM machine, we're caching {@link NavMesh} objects for given "map name".
 * 
 * @author Jimmy
 */
public class NavMeshCache {
	
	public static LogCategory log = new LogCategory("AnalysedNavMeshCache");
	
	static {
		ConsolePublisher publisher = new LogPublisher.ConsolePublisher();
		publisher.setFormatter(new LogFormatter(new AgentId("Platform")));
		log.addHandler(publisher);
	}
	
	private static Map<String, SoftReference<NavMesh>> cache = Collections.synchronizedMap(new HashMap<String, SoftReference<NavMesh>>());
	
	public static void loadNavMesh( NavMesh navMesh, final Map<UnrealId, NavPoint> navGraph, String mapName) {		
		NavMesh cachedNavMesh = getNavMeshFromCache(mapName);
		if (cachedNavMesh != null) {
			navMesh.copyFrom( cachedNavMesh);
			navMesh.setNavGraph(
				new Function<UnrealId, NavPoint>(){
	
					@Override
					public NavPoint apply(UnrealId input) {
						return navGraph.get(input);
					}
				}
			);
		} else {
			reloadNavMesh( navMesh, navGraph, mapName);
		}
	}
	
	private static NavMesh getNavMeshFromCache(String mapName) {
		SoftReference<NavMesh> reference = cache.get(mapName);
		if (reference == null) return null;
		
		NavMesh result = reference.get();
		
		if (result != null) return result;
		
		cache.remove(mapName);
		
		return null;
	}
	
	public static void reloadNavMesh( NavMesh navMesh, final Map<UnrealId, NavPoint> navGraph, String mapName) {
		NavMesh cachedNavmesh = null;
		
		String navMeshFileName = NavMesh.processedMeshDir + "/" + mapName + ".anm";
		File navMeshFile = new File(navMeshFileName);
		
		ObjectInputStream in = null;
		try {
			log.warning("Loading previously stored NavMesh from binary file: " + navMeshFile.getAbsolutePath());
			in = new ObjectInputStream(new FileInputStream(navMeshFile));
			cachedNavmesh = (NavMesh) in.readObject();
			cachedNavmesh.setNavGraph(
				new Function<UnrealId, NavPoint>(){
	
					@Override
					public NavPoint apply(UnrealId input) {
						return navGraph.get(input);
					}
				}
			);
		} catch (InvalidClassException e) {
	    	log.warning( "Analysed navmesh file was generated by a different version of the software and must be regenerated." );
	    } catch (ClassNotFoundException e) {
	    	log.warning( "Analysed navmesh file was generated by a different version of the software and must be regenerated." );
		} catch (IOException e) {
	    	log.warning( "Previously saved analysed navmesh file could not have been restored, looking for original files." );
	    }  finally {
	    	if ( in != null ) {
	    		try {
					in.close();
				} catch (IOException e) {
					// (╯°□°)╯︵ ┻━┻
				}
	    	}
	    }
		
        if (cachedNavmesh != null) {
			cache.put(mapName, new SoftReference<NavMesh>(cachedNavmesh));
			navMesh.copyFrom( cachedNavmesh );
        	return;
        }
        
		try {
			cachedNavmesh = new NavMesh(log);
			cachedNavmesh.load(navGraph, mapName);
            cache.put(mapName, new SoftReference<NavMesh>(cachedNavmesh));
            
        	log.info("NavMesh LOADED SUCCESSFULLY.");
        	
/*            // save analyzed navmesh for next time
        	ObjectOutputStream out = null;
            try {
                log.warning("Writing navmesh to a file at: " + navMeshFile.getAbsolutePath());
                navMeshFile.getParentFile().mkdirs();
                out = new ObjectOutputStream(new FileOutputStream(navMeshFile));
                out.writeObject(cachedNavmesh); 
                log.info("Navmesh written ok.");
            } catch (Exception e) {
            	log.severe(ExceptionToString.process("Exception during writing level geom to a file.", e));
            } finally {
            	if ( out != null ) {
            		try {
            			out.close();
    				} catch (IOException e) {
    					// (╯°□°)╯︵ ┻━┻
    				}
            	}
            }
*/            
            navMesh.copyFrom( cachedNavmesh );
            return;
		} catch (Exception e) {
			throw new RuntimeException( "Unable to load navmesh file; the navmesh is expected to be inside 'navmesh' directory; you can download some from: https://github.com/kefik/Pogamut3/tree/master/Addons/UT2004/Map/UT2004NavMeshTools/04-NavMeshes", e );
		}
	}
}
