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

import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectFirstEncounteredEvent;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.drawing.IUT2004ServerProvider;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.drawing.LevelGeometryDraw;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo;

/**
 * LevelGeometryModule is wrapping the load/save logic for the {@link LevelGeometry}.
 * 
 * @author Jakub Tomek
 * @author Jakub Gemrot aka Jimmy
 */
public class LevelGeometryModule {
	
	private Logger log;
			
	//
	// STATE
	//
	
	private boolean loaded = false;
	private GameInfo loadedForMap = null;
	
	//
	// NAVMESH DATA STRUCTURES
	//
	
    private LevelGeometry levelGeometry;
    private LevelGeometryDraw draw;
    
	private IWorldObjectEventListener<GameInfo, IWorldObjectEvent<GameInfo>> gameInfoListener = new IWorldObjectEventListener<GameInfo, IWorldObjectEvent<GameInfo>>() {
		@Override
		public void notify(IWorldObjectEvent<GameInfo> event) {
			load(event.getObject());
		}
	};
       
    public LevelGeometryModule(IUT2004ServerProvider serverProvider, IWorldView worldView, IAgentLogger logger) {
    	if (logger == null) {
    		log = new LogCategory("LevelGeometry");
    	} else {
    		log = logger.getCategory("LevelGeometry");
    	}
    	    	
    	worldView.addObjectListener(GameInfo.class, WorldObjectFirstEncounteredEvent.class, gameInfoListener);
    	
    	GameInfo info = worldView.getSingle(GameInfo.class);
    	if (info != null) {
    		load(info);
    	}
    	
    	draw = new LevelGeometryDraw(null, log, serverProvider);
    }
    
    private void clear() {
    	log.warning("LevelGeometry has been cleared...");
    	
        levelGeometry = null;
        
        loaded = false;
        loadedForMap = null;
    }
    	
    private boolean loadLevelGeometry(String mapName) {
    	levelGeometry = LevelGeometryCache.getLevelGeometry(mapName);
    	if (levelGeometry == null) {
    		log.warning("COULD NOT INITIALIZE FOR MAP: " + mapName);
    		return false;
    	} else {
    		log.info("INITIALIZED FOR MAP: " + mapName);
    		return true;
    	}
    }
    
    private void load(GameInfo info) {
    	if (info == null) {
    		log.severe("Could not load for 'null' GameInfo!");
    		return;
    	}
        if (loaded) {
        	if (loadedForMap == null) {
        		// WTF?
        		// => clear
        		clear();
        		// => and reload
        	} else {
        		if (loadedForMap.getLevel().equals(info.getLevel())) {
        			// ALREADY INITIALIZED FOR THE SAME LEVEL
        			return;
        		}
        	}
        }
        
        // LOAD THE LEVEL GEOMETRY FOR THE MAP FROM 'info'
        String mapName = info.getLevel();        
        log.warning("Initializing LevelGeometry for: " + mapName);
    	
        if (loadLevelGeometry(mapName)) {	 
        	draw.setLevelGeometry(levelGeometry);
	        loaded = true;
	        loadedForMap = info;
	        return;
        } 
        
        loaded = false;
        loadedForMap = null;
    }
    
    /**
     * Tells, whether the module has been correctly initialized and it is possible to call {@link #getLevelGeometry()} method.  
     * @return
     */
    public boolean isInitialized() {
    	return this.levelGeometry != null && this.levelGeometry.isLoaded();
    }
    
    /**
     * Returns {@link LevelGeometry} object for currently running map.
     * 
     * Non-null iff {@link #isInitialized()}.
     * 
     * @return 
     */
    public LevelGeometry getLevelGeometry() {
        return levelGeometry;
    }
    
    /**
     * Return {@link LevelGeometryDraw} that can be used to visualize currently loaded {@link LevelGeometry} from this module.
     * @return
     */
    public LevelGeometryDraw getDraw() {
    	return this.draw;
    }
    
}
