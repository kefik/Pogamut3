package cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import javax.vecmath.Vector3d;

import cz.cuni.amis.pogamut.base.agent.module.SensomotoricModule;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdateResult;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdateResult.Result;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdateResult.WorldObjectUpdateResult;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObject;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.levelGeometry.LevelGeometryModule;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.AutoTraceRay;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Self;

public class RaycastingBSP extends SensomotoricModule<UT2004Bot> {
	
	private LevelGeometryModule levelGeometryModule;
	
    Map<String, BSPRayInfoContainer> rayInfoContainers = new HashMap<String, BSPRayInfoContainer>();
    int counter = 0;
    private String idSuffix;

    private IWorldObjectEventListener<Self, WorldObjectUpdatedEvent<Self>> selfListener = new IWorldObjectEventListener<Self, WorldObjectUpdatedEvent<Self>>() {

            @Override
            public void notify(WorldObjectUpdatedEvent<Self> event) {
                    selfUpdate(event.getObject());
            }

    };

    /**
     * Alternative contructor
     * @param bot 
     */
    public RaycastingBSP(UT2004Bot bot, LevelGeometryModule levelGeometryModule) {
        this(bot, levelGeometryModule, null);        
    }
    
    /**
     * Object's contructor
     * @param bot
     * @param log 
     */
    public RaycastingBSP(UT2004Bot bot, LevelGeometryModule levelGeometryModule, Logger log) {
        super(bot, log);
        
        this.levelGeometryModule = levelGeometryModule;
        
        idSuffix = "_" + bot.getName() + UUID.randomUUID().toString();
        
        // listener for updating rays
        bot.getWorldView().addObjectListener(Self.class, WorldObjectUpdatedEvent.class, selfListener);
    }
    
    /**
     * Whether we have BSP data for raycasting.
     * @return
     */
    public boolean isUsable() {
    	return levelGeometryModule != null && levelGeometryModule.isInitialized();
    }

    /**
     * Deletes all previous rays and makes this instance ready for setting up
     * new rays.
     */
    public void clear() {
        for(BSPRayInfoContainer rayInfo : rayInfoContainers.values()) {
            if(rayInfo==null) continue;
            UnrealId unrealId = rayInfo.unrealId;
            if(unrealId==null) continue;
            final AutoTraceRay ray = agent.getWorldView().get(unrealId, AutoTraceRay.class);
            // LET'S DESTROY THE RAY INSIDE WORLDVIEW - IT WON'T BE ACCESSIBLE AGAIN!
            agent.getWorldView().notifyImmediately( 				
                    new IWorldObjectUpdatedEvent() {
                                    @Override
                                    public WorldObjectId getId() {
                                            return ray.getId();
                                    }
                                    @Override
                                    public IWorldObjectUpdateResult<IWorldObject> update(IWorldObject obj) {
                                            return new WorldObjectUpdateResult<IWorldObject>(Result.DESTROYED, obj);
                                    }
                                    @Override
                                    public long getSimTime() {
                                            return ray.getSimTime();
                                    }
                    }
            );                    
        }        
        rayInfoContainers.clear();
    }

    public Future<AutoTraceRay> createRay(Vector3d direction, int length, boolean floorCorrection) {
            String id = counter++ + idSuffix;
            return createRay(id, direction, length, floorCorrection);
    }

    /**
     * This method creates a ray and puts it into worldview
     * @param id
     * @param direction
     * @param length
     * @param floorCorrection
     * @return 
     */
    public Future<AutoTraceRay> createRay(String id, Vector3d direction, int length, boolean floorCorrection) {
            
            UnrealId unrealId = UnrealId.get(id);
            BSPRayInfoContainer rayInfo = new BSPRayInfoContainer(unrealId, direction, length, floorCorrection);
            rayInfoContainers.put(id, rayInfo);
            // that's all for now. the ray will be put in worldview in selfUpdate()
            selfUpdate(agent.getSelf());           
            return null;
    }

    /**
     * gets all the rays from the hashmap, recomputes them and sends them to world view
     * @param self 
     */
    protected void selfUpdate(Self self) {
    	if (!isUsable()) return;
        // RECOMPUTE AutoTraceRay(s)      
        for(BSPRayInfoContainer rayInfo : rayInfoContainers.values()) {
            AutoTraceRay ray = levelGeometryModule.getLevelGeometry().getAutoTraceRayMessage(self, rayInfo);
            agent.getWorldView().notifyImmediately(ray);
        }
    }

    /**
     * Returns a ray of specified id. If the ray of the specified id does not exist
     * or was not initialized yet then it returns null.
     * <p><p>
     * Note that the {@link AutoTraceRay} instance is self updating - once obtained you may use it every
     * logic cycle to obtain current readings from the ray.
     * 
     * @param rayID
     * @return
     */    
    AutoTraceRay getRay(String rayID) {
        BSPRayInfoContainer rayInfo = rayInfoContainers.get(rayID);
        if(rayInfo==null) return null;
        UnrealId unrealId = rayInfo.unrealId;
        if(unrealId==null) return null;
        AutoTraceRay ray = agent.getWorldView().get(unrealId, AutoTraceRay.class);
        return ray;
    }

}
