/*
 * Copyright (C) 2016 AMIS research group, Faculty of Mathematics and Physics, Charles University in Prague, Czech Republic
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cz.cuni.amis.pogamut.ut2004.agent.navigation.levelGeometry;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
public class LevelGeometryCache {
	
	public static LogCategory log = new LogCategory("LevelGeometryCache");
	
	static {
		ConsolePublisher publisher = new LogPublisher.ConsolePublisher();
		publisher.setFormatter(new LogFormatter(new AgentId("Platform")));
		log.addHandler(publisher);
	}
	
	private static Map<String, SoftReference<LevelGeometry>> cache = Collections.synchronizedMap(new HashMap<String, SoftReference<LevelGeometry>>());
	
	public static LevelGeometry getLevelGeometry(String mapName) {		
		LevelGeometry result = getLevelGeometryFromCache(mapName);
		if (result != null) return result;
		return loadLevelGeometry(mapName);
	}
	
	private static LevelGeometry getLevelGeometryFromCache(String mapName) {
		SoftReference<LevelGeometry> reference = cache.get(mapName);
		if (reference == null) return null;
		
		LevelGeometry result = reference.get();
		
		if (result != null) return result;
		
		cache.remove(mapName);
		
		return null;
	}
	
	private static LevelGeometry loadLevelGeometry(String mapName) {
		LevelGeometry result = getLevelGeometryFromCache(mapName);
		if (result != null) return result;
		
		// try to read it from processed file        
        String levelGeometryFileName = LevelGeometry.processedLevelGeometryDir + File.separator + mapName + ".plg";
        File levelGeometryFile = new File(levelGeometryFileName);
        
        ObjectInputStream in = null;
        try {
        	if (!levelGeometryFile.exists()) {
        		log.warning("Processed LevelGeometry does not exist at: " + levelGeometryFile.getAbsolutePath());
        	} else {
        		log.info("Loading previously stored LevelGeometry from binary file: " + levelGeometryFile.getAbsolutePath());
	            in = new ObjectInputStream(new FileInputStream(levelGeometryFile));
	            result = (LevelGeometry) in.readObject();
	            result.setLog(log);
	            log.info("LevelGeometry LOADED SUCCESSFULLY.");
        	}
        } catch (InvalidClassException e) {
        	log.warning( "Preprocessed level geometry file was generated by a different version of software and must be regenerated." );
        } catch (ClassNotFoundException e) {
        	log.warning( "Preprocessed level geometry file was generated by a different version of software and must be regenerated." );
		} catch (IOException e) {
        	log.severe(ExceptionToString.process("Previously saved level geometry file could not have been restored, looking for original files.", e));
        }  finally {
        	if ( in != null ) {
        		try {
					in.close();
				} catch (IOException e) {
					// (╯°□°)╯︵ ┻━┻
				}
        	}
        }
        
        if (result != null) {
            cache.put(mapName, new SoftReference<LevelGeometry>(result));
        	return result;
        }
		
        try { 
        	
        	result = new LevelGeometry(log);
            result.load(mapName);
            cache.put(mapName, new SoftReference<LevelGeometry>(result));
            
        	log.info("LevelGeometry LOADED SUCCESSFULLY.");
        	
            // save processed level geometry for next time
        	ObjectOutputStream out = null;
            try {
                log.warning("Writing level geometry to a file at: " + levelGeometryFile.getAbsolutePath());
                levelGeometryFile.getParentFile().mkdirs();
                out = new ObjectOutputStream(new FileOutputStream(levelGeometryFile));
                out.writeObject(result); 
                log.info("Level geometry written ok.");
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
            
            return result;            
        } catch(Exception e) {
        	log.severe(ExceptionToString.process("Unable to load level geometry files.", e));
            return null;
        }  		
	}

}
