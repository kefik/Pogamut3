package cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric;

import java.util.concurrent.Future;
import java.util.logging.Logger;

import javax.vecmath.Vector3d;

import cz.cuni.amis.pogamut.base.agent.module.SensomotoricModule;
import cz.cuni.amis.pogamut.base.communication.exception.CommunicationException;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.levelGeometry.LevelGeometryModule;
import cz.cuni.amis.pogamut.ut2004.bot.IUT2004BotController;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.AutoTraceRay;
import cz.cuni.amis.utils.flag.Flag;

/**
 * Support for creating rays used for raycasting (see {@link AutoTraceRay} that is being utilized).
 * <p><p>
 * It is designed to be initialized inside {@link IUT2004BotController#initializeController(UT2004Bot)} method call
 * and may be used since {@link IUT2004BotController#botInitialized(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage)}
 * is called.
 * @author ik
 */
public class Raycasting extends SensomotoricModule<UT2004Bot> {

	RaycastingUT2004 rayUT2004;	
	RaycastingBSP rayBSP;
	
    public Flag<Boolean> getAllRaysInitialized() {
        return rayUT2004.allRaysInitialized;
    }
    
    public Raycasting(UT2004Bot bot) {
    	this(bot, null);
    }

    public Raycasting(UT2004Bot bot, LevelGeometryModule levelGeometryModule) {
        this(bot, levelGeometryModule, null);
    }
    
    public Raycasting(UT2004Bot bot, LevelGeometryModule levelGeometryModule, Logger log) {
        super(bot, log);
        
        rayUT2004 = new RaycastingUT2004(bot, log);
        rayBSP = new RaycastingBSP(bot, levelGeometryModule, log);
        
        cleanUp();
    }
    
    @Override
    protected void cleanUp() {
    	super.cleanUp();
    }

    /**
     * Deletes all previous rays and makes this instance ready for setting up
     * new rays.
     */
    public void clear() throws CommunicationException {
    	rayBSP.clear();
    	rayUT2004.clear();        
    }

    /**
     * Once all rays were initialized using createRay(...) methods, call this
     * method to start listening for response from UT.
     */
    public void endRayInitSequence() {
        rayUT2004.endRayInitSequence();
    }

    /**
     * Initializes ray usind AddRay command and returns future that waits for
     * the first AutoTraceRay message corresponding to this ray.
     * @param Id
     * User set Id of the ray, so the ray can be identified.
     * 
     * @param Direction
     * Vector direction of the ray (it will be relative - added to
     * the vector, where the bot is looking, also takes into
     * account angle of the floor the bot is standing on).
     * 
     * @param Length
     * Specifies the length of the ray (in UT units).
     * 
     * @param FastTrace
     * True if we want to use FastTrace function instead of Trace
     * function (a bit faster but less information provided - just
     * information if we hit something or not).
     * 
     * @param FloorCorrection
     * If we should correct ray directions accoring floor normal. Note: Has issue - we can't set set rays up or down when correction is active.
	 * 
     * @param TraceActors
     * If we want to trace also actors – bots, monsters, players,
     * items. False if we want to trace just level geometry.
	 * 
     * @return
     */
    public Future<AutoTraceRay> createRay(String Id, Vector3d Direction, int Length, boolean FastTrace, boolean FloorCorrection, boolean TraceActors) throws CommunicationException {
       if (!TraceActors && rayBSP.isUsable()) {
   		   return rayBSP.createRay(Id, Direction, Length, FloorCorrection);
       } else {
    	   return rayUT2004.createRay(Id, Direction, Length, FastTrace, FloorCorrection, TraceActors);
       }
    }

    /**
     * Creates ray with system generated id. Note that the ray is not initialized immediately - we have to wait for GB2004 to 
     * confirm us. Therefore you will not receive actual instance of {@link AutoTraceRay} but its {@link Future}.
     * Use method {@link Future#isDone()} to check whether the ray was initialized and method {@link Future#get()} to obtain the ray instance.
     * 
     * @param Direction
     * Vector direction of the ray (it will be relative - added to
     * the vector, where the bot is looking, also takes into
     * account angle of the floor the bot is standing on).
	 * 
     * @param Length
     * Specifies the length of the ray (in UT units).
     * 
     * @param FastTrace
     * True if we want to use FastTrace function instead of Trace
     * function (a bit faster but less information provided - just
     * information if we hit something or not).
     * 
     * @param FloorCorrection
     * If we should correct ray directions according floor normal. Note: Has issue - we can't set set rays up or down when correction is active.
     * 
     * @param TraceActors
     * If we want to trace also actors, bots, monsters, players,
     * items. False if we want to trace just level geometry.
	 * 
     * @return ray's future - use method {@link Future#isDone()} to check whether the ray was initialized and method {@link Future#get()} to obtain the ray instance
     * @throws cz.cuni.amis.pogamut.base.communication.exceptions.CommunicationException
     */
    public Future<AutoTraceRay> createRay(Vector3d Direction, int Length, boolean FastTrace, boolean FloorCorrection, boolean TraceActors) throws CommunicationException {
    	 if (!TraceActors && rayBSP.isUsable()) {
   		   return rayBSP.createRay(Direction, Length, FloorCorrection);
         } else {
        	 return rayUT2004.createRay(Direction, Length, FastTrace, FloorCorrection, TraceActors);
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
    public AutoTraceRay getRay(String rayID) {
        AutoTraceRay r1 = rayUT2004.getRay(rayID);
        if(r1 != null) return r1;
        else return rayBSP.getRay(rayID);      
    }

    /**
     * Sets {@link Raycasting#allRaysInitialized} flag to true if all rays has been initialized.
     */
    protected void checkIfAllInited() {
        rayUT2004.checkIfAllInited();
    }
    
}

