/*
 * Copyright (C) 2013 AMIS research group, Faculty of Mathematics and Physics, Charles University in Prague, Czech Republic
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
package cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh;
//old navmesh

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.google.common.collect.Maps;

import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.NavigationGraphBuilder;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.UT2004MapTweaks;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.drawing.IUT2004ServerProvider;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.drawing.NavMeshDraw;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.grounder.NavMeshDropGrounder;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.pathPlanner.AStar.NavMeshAStarDistanceHeuristic;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.pathPlanner.AStar.NavMeshAStarPathPlanner;
import cz.cuni.amis.pogamut.ut2004.bot.IUT2004BotController;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.translator.shared.events.MapPointListObtained;
import cz.cuni.amis.utils.NullCheck;

/**
 * Main class of Navigation Mesh module.
 * <p><p>
 * It expects .navmesh files to be present within ./navmesh directory (at project root).
 * NavMesh files can be obtained from: svn://artemis.ms.mff.cuni.cz/pogamut/trunk/project/Addons/UT2004NavMeshTools/04-NavMeshes
 * <p><p>
 * .navmesh file contains pure navmesh for the map. When loaded it gets combined with current navigation graph (extracting off-mesh links that leads between various polygons).
 * <p><p>
 * Currently there is no way to regenerate the navmesh during runtime (i.e. if you would like to manipulate with navigation graph / off-mesh links during runtime, its impossible,
 * you will have to copy-paste the implementation of this class and hack it manually), but there is at least a way to regenerate .processed file by setting {@link #setReloadNavMesh(boolean)} 
 * to true during {@link IUT2004BotController#mapInfoObtained()}. E.g., you might first use {@link NavigationGraphBuilder} via {@link UT2004BotModuleController#getNavBuilder()}
 * or tweak the map via {@link UT2004MapTweaks}, resp. {@link UT2004BotModuleController#getMapTweaks()}, then {@link #setReloadNavMesh(boolean)} set to TRUE in order to regenerate the off-mesh links.  
 *
 * @author Jakub Gemrot aka Jimmy
 * @author Jakub Tomek
 */
public class NavMeshModule {
    
    private IWorldView worldView;
    private Logger log;
    
    //
    // STATE
    //
    private boolean shouldReloadNavMesh = false;
    private GameInfo loadedForMap = null;

    // 
    // COMPONENTS
    //
    private NavMesh navMesh;
    private NavMeshDraw navMeshDraw;
    private NavMeshDropGrounder dropGrounder;
    private NavMeshAStarDistanceHeuristic aStarDistanceHeuristic;
    private NavMeshAStarPathPlanner aStarPathPlanner;
    private NavMeshClearanceComputer clearanceComputer;
    
    //private NavMeshDraw navMeshDraw;
    
    //
    // LISTENER
    //
    private IWorldEventListener<MapPointListObtained> mapListEndListener = new IWorldEventListener<MapPointListObtained>() {
        
        @Override
        public void notify(MapPointListObtained event) {
            GameInfo info = worldView.getSingle(GameInfo.class);
            if (info == null) {
                throw new RuntimeException( "Null game info after MapPointLIstObtained event." );
            }
            load(info);
        }
    };
	
    
    public NavMeshModule(IUT2004ServerProvider serverProvider, IWorldView worldView, IAgentLogger logger) {
        if (logger == null) {
            log = new LogCategory("NavMesh");
        } else {
            log = logger.getCategory("NavMesh");
        }
        
        this.worldView = worldView;
        NullCheck.check(this.worldView, "worldView");
        
        navMesh = new NavMesh(log);
        dropGrounder = new NavMeshDropGrounder(navMesh);
        aStarDistanceHeuristic = new NavMeshAStarDistanceHeuristic(navMesh);
        aStarPathPlanner = new NavMeshAStarPathPlanner( dropGrounder, navMesh, aStarDistanceHeuristic, log);
        clearanceComputer = new NavMeshClearanceComputer( dropGrounder );
        navMeshDraw = new NavMeshDraw(navMesh, log, serverProvider);
        
        GameInfo info = worldView.getSingle(GameInfo.class);
        if (info != null) {
            load(info);
        } else {
        	worldView.addEventListener(MapPointListObtained.class, mapListEndListener);        	
        }
    }
    
    private void load(GameInfo info) {
    	try {
            
    	
	    	if (info == null) {
	            log.severe("Could not load for 'null' GameInfo!");
	            return;
	        }
	        if ( loadedForMap != null &&  loadedForMap.getLevel().equals(info.getLevel()) ) {
	            return;
	        }
	        
	        Map<WorldObjectId, NavPoint> worldViewOfNavPoints = worldView.getAll( NavPoint.class );
	        HashMap<UnrealId, NavPoint> navGraph = Maps.newHashMap();
	        synchronized ( worldViewOfNavPoints ) {
	        	for ( NavPoint navPoint : worldViewOfNavPoints.values() ) {
	        		navGraph.put( navPoint.getId(), navPoint );
	        	}
	        }
	        
	        if ( shouldReloadNavMesh ) {
	        	NavMeshCache.reloadNavMesh( navMesh, navGraph, info.getLevel() );
	        } else {
	        	NavMeshCache.loadNavMesh( navMesh, navGraph, info.getLevel() );
	        }
	        
	        loadedForMap = info;
	        
    	} catch (Exception e) {
    		log.warning("Failed to load NavMesh...");
    		loadedForMap = null;
    	}
    }

    // ================
    // PUBLIC INTERFACE
    // ================
    /**
     * Tells whether NavMesh has been initialized and {@link #getNavMesh()} is
     * usable.
     *
     * @return
     */
    public boolean isInitialized() {
        return loadedForMap != null;
    }
    
    /**
     * Whether {@link NavMeshModule} should ignored ".processed" navmesh and reload it from ".navmesh".
     * <p><p>
     * Note that reloading can happen only during {@link #load(GameInfo)} that means you have to set this flag
     * before {@link GameInfo} came from GB2004, i.e., during {@link IUT2004BotController#prepareBot(cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot)}.
     *  
     * @return
     */
    public boolean shouldReloadNavMesh() { 
    	return shouldReloadNavMesh;
    }
    
    /**
     * Whether {@link NavMeshModule} should ignored ".processed" navmesh and reload it from ".navmesh".
     * <p><p>
     * Note that reloading can happen only during {@link #load(GameInfo)} that means you have to set this flag
     * before {@link GameInfo} came from GB2004, i.e., during {@link IUT2004BotController#prepareBot(cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot)}.
     *  
     * @return
     */
    public void setReloadNavMesh(boolean value) {
    	this.shouldReloadNavMesh = value;
    }

    /**
     * Always non-null, always returns {@link NavMesh}. However, you should
     * check {@link #isInitialized()} whether the {@link NavMesh} is usable.
     *
     * @return
     */
    public NavMesh getNavMesh() {
        return navMesh;
    }
    
    public NavMeshDropGrounder getDropGrounder() {
    	return dropGrounder;
    }

	public NavMeshAStarDistanceHeuristic getAStarDistanceHeuristic() {
		return aStarDistanceHeuristic;
	}

	public NavMeshAStarPathPlanner getAStarPathPlanner() {
		return aStarPathPlanner;
	}
    
	public NavMeshClearanceComputer getClearanceComputer() {
		return clearanceComputer;
	}
	
/*    public NavMeshDraw getNavMeshDraw() {
        return navMeshDraw;
    }*/    
}
