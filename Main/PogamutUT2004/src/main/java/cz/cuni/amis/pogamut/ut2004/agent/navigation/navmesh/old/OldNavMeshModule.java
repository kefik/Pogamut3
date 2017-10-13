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
package cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.old;
//old navmesh

import java.util.Map;
import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.NavigationGraphBuilder;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.UT2004MapTweaks;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.floydwarshall.FloydWarshallMap;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.drawing.IUT2004ServerProvider;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.drawing.NavMeshDraw;
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
 * .navmesh file contains pure navmesh for the map. When loaded it gets combined with current navigation graph (extracting off-mesh links that leads between various polygons)
 * and this structure is then stored within ./navmesh as %MAP-NAME%.processed file.
 * <p><p>
 * Currently there is no way to regenerate the navmesh during runtime (i.e. if you would like to manipulate with navigation graph / off-mesh links during runtime, its impossible,
 * you will have to copy-paste the implementation of this class and hack it manually), but there is at least a wayy to regenerate .processed file by setting {@link #setReloadNavMesh(boolean)} 
 * to true during {@link IUT2004BotController#mapInfoObtained()}. E.g., you might first use {@link NavigationGraphBuilder} via {@link UT2004BotModuleController#getNavBuilder()}
 * or tweak the map via {@link UT2004MapTweaks}, resp. {@link UT2004BotModuleController#getMapTweaks()}, then {@link #setReloadNavMesh(boolean)} set to TRUE in order to regenerate the off-mesh links.  
 *
 * @author Jakub Gemrot aka Jimmy
 * @author Jakub Tomek
 */
@Deprecated
public class OldNavMeshModule {
    
    private IWorldView worldView;
    private Logger log;
    
    private NavMeshDraw navMeshDraw;

    //
    // STATE
    //
    private boolean shouldReloadNavMesh = false;
    private boolean loaded = false;
    private GameInfo loadedForMap = null;

    //
    // NAVMESH DATA STRUCTURES
    //
    private OldNavMesh navMesh;

    //
    // LISTENER
    //
    private IWorldEventListener<MapPointListObtained> mapListEndListener = new IWorldEventListener<MapPointListObtained>() {
        
        @Override
        public void notify(MapPointListObtained event) {
            GameInfo info = worldView.getSingle(GameInfo.class);
            if (info != null) {
                load(info);
            }
        }
    };
    
    public OldNavMeshModule(IUT2004ServerProvider serverProvider, IWorldView worldView, IAgentLogger logger) {
        if (logger == null) {
            log = new LogCategory("NavMesh");
        } else {
            log = logger.getCategory("NavMesh");
        }
        
        this.worldView = worldView;
        NullCheck.check(this.worldView, "worldView");
        
        navMesh = new OldNavMesh( 
        	new OldNavMesh.INavPointWorldView() {			
				@SuppressWarnings("unchecked")
				@Override
				public Map<UnrealId, NavPoint> get() {
					return (Map<UnrealId, NavPoint>)(Map)OldNavMeshModule.this.worldView.getAll( NavPoint.class );
				}
        	},
        	log
        );
        
        worldView.addEventListener(MapPointListObtained.class, mapListEndListener);
        
        GameInfo info = worldView.getSingle(GameInfo.class);
        if (info != null) {
            load(info);
        }
        
        navMeshDraw = new NavMeshDraw(navMesh, log, serverProvider);
    }
    
    private void clear() {
        log.warning("NavMesh has been cleared...");
        
        navMesh.clear();
        
        loaded = false;
        loadedForMap = null;
    }
    
    private void load(GameInfo info) {
        if (info == null) {
            log.severe("Could not load for 'null' GameInfo!");
            return;
        }
        if (loaded) {
            if (loadedForMap == null) {
                // WTF?
                clear();
            } else {
                if (loadedForMap.getLevel().equals(info.getLevel())) {
                    // ALREADY INITIALIZED FOR THE SAME LEVEL
                    return;
                }
            }
        }
        
        if (navMesh.load(info, shouldReloadNavMesh)) {
            loaded = true;
            loadedForMap = info;
        } else {
            loaded = false;
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
        return loaded;
    }
    
    /**
     * Whether {@link OldNavMeshModule} should ignored ".processed" navmesh and reload it from ".navmesh".
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
     * Whether {@link OldNavMeshModule} should ignored ".processed" navmesh and reload it from ".navmesh".
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
     * Always non-null, always returns {@link OldNavMesh}. However, you should
     * check {@link #isInitialized()} whether the {@link OldNavMesh} is usable.
     *
     * @return
     */
    public OldNavMesh getNavMesh() {
        return navMesh;
    }
    
    public NavMeshDraw getNavMeshDraw() {
        return navMeshDraw;
    }
    
    public void setFwMap(FloydWarshallMap fwMap) {
        this.navMesh.setFwMap(fwMap);
    }
    
}
