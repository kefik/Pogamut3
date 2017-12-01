package cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.old;
//old navmesh

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import cz.cuni.amis.pogamut.base.agent.impl.AgentId;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.pogamut.base.utils.logging.LogFormatter;
import cz.cuni.amis.pogamut.base.utils.logging.LogPublisher;
import cz.cuni.amis.pogamut.base.utils.logging.LogPublisher.ConsolePublisher;
import cz.cuni.amis.utils.ExceptionToString;

/**
 * Used to speed up bots starting time on the same JVM machine, we're caching {@link OldNavMeshCore} objects for given "map name".
 * 
 * @author Jimmy
 */
@Deprecated
public class OldNavMeshCoreCache {
	
	public static LogCategory log = new LogCategory("NavMeshCoreCache");
	
	static {
		ConsolePublisher publisher = new LogPublisher.ConsolePublisher();
		publisher.setFormatter(new LogFormatter(new AgentId("Platform")));
		log.addHandler(publisher);
	}
	
	private static Map<String, SoftReference<OldNavMeshCore>> cache = Collections.synchronizedMap(new HashMap<String, SoftReference<OldNavMeshCore>>());
	
	public static OldNavMeshCore getNavMeshCore(String mapName) {		
		OldNavMeshCore result = getNavMeshCoreFromCache(mapName);
		if (result != null) return result;
		return loadNavMeshCore(mapName);
	}
	
	private static OldNavMeshCore getNavMeshCoreFromCache(String mapName) {
		SoftReference<OldNavMeshCore> reference = cache.get(mapName);
		if (reference == null) return null;
		
		OldNavMeshCore result = reference.get();
		
		if (result != null) return result;
		
		cache.remove(mapName);
		
		return null;
	}
	
	private static OldNavMeshCore loadNavMeshCore(String mapName) {
		OldNavMeshCore result = getNavMeshCoreFromCache(mapName);
		if (result != null) return result;
		
		String processedNavMeshFileName = OldNavMeshConstants.processedMeshDir + "/" + mapName + ".navmesh.processed";
        File processedNavMeshFile = new File(processedNavMeshFileName);
		
		ObjectInputStream in;
		try {
			log.warning("Loading previously stored NavMesh from binary file: " + processedNavMeshFile.getAbsolutePath());
			in = new ObjectInputStream(new FileInputStream(processedNavMeshFile));
			result = (OldNavMeshCore) in.readObject();
	        in.close();
		} catch (Exception e) {
			if (result == null) {
				log.warning(ExceptionToString.process("Failed to load processed navmesh from: " + processedNavMeshFile.getAbsolutePath(), e));
				return null;
			}
		}
		
        cache.put(mapName, new SoftReference<OldNavMeshCore>(result));
        
        return result;
	}
	
	public static void setNavMeshCore(String mapName, OldNavMeshCore core) {
		cache.put(mapName, new SoftReference<OldNavMeshCore>(core));
	}

}
