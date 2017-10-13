package cz.cuni.amis.pogamut.ut2004.agent.navigation.levelGeometry;

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
 * Used to speed up bots starting time on the same JVM machine, we're caching {@link LevelGeometry} objects for given "map name".
 * 
 * @author Jimmy
 */
public class LevelGeometryCacheNaive {
	
	public static LogCategory log = new LogCategory("LevelGeometryCache");
	
	static {
		ConsolePublisher publisher = new LogPublisher.ConsolePublisher();
		publisher.setFormatter(new LogFormatter(new AgentId("Platform")));
		log.addHandler(publisher);
	}
	
	private static Map<String, SoftReference<LevelGeometryNaive>> cache = Collections.synchronizedMap(new HashMap<String, SoftReference<LevelGeometryNaive>>());
	
	public static LevelGeometryNaive getLevelGeometry(String mapName) {		
		LevelGeometryNaive result = getLevelGeometryFromCache(mapName);
		if (result != null) return result;
		return loadLevelGeometry(mapName);
	}
	
	private static LevelGeometryNaive getLevelGeometryFromCache(String mapName) {
		SoftReference<LevelGeometryNaive> reference = cache.get(mapName);
		if (reference == null) return null;
		
		LevelGeometryNaive result = reference.get();
		
		if (result != null) return result;
		
		cache.remove(mapName);
		
		return null;
	}
	
	private static LevelGeometryNaive loadLevelGeometry(String mapName) {
		LevelGeometryNaive result = getLevelGeometryFromCache(mapName);
		if (result != null) return result;
		
		// try to read it from processed file              
        try { 
        	
        	result = new LevelGeometryNaive(log);
            if (result.load(mapName)) {
            	log.info("LevelGeometry LOADED SUCCESSFULLY.");
            	cache.put(mapName, new SoftReference<LevelGeometryNaive>(result));
	            
	            return result;
            }
            
            log.severe("COULD NOT INITIALIZE FOR MAP: " + mapName);
            result = null;
            return null;
            
        } catch(Exception e) {
        	log.severe(ExceptionToString.process("Unable to load level geometry files.", e));
        	result = null;
            return null;
        }  		
	}

}
