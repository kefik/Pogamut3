package cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.vecmath.Vector3d;

import cz.cuni.amis.pogamut.base.agent.module.SensomotoricModule;
import cz.cuni.amis.pogamut.base.communication.exception.CommunicationException;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdateResult;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdateResult.Result;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdateResult.WorldObjectUpdateResult;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObject;
import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectFuture;
import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.pogamut.ut2004.bot.IUT2004BotController;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.AddRay;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.RemoveRay;
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
public class RaycastingUT2004 extends SensomotoricModule<UT2004Bot> {

    Map<String, WorldObjectFuture<AutoTraceRay>> rayFutures = new HashMap<String, WorldObjectFuture<AutoTraceRay>>();
    String idSuffix = null;
    int counter = 0;
    int alreadyInitialized = 0;
    Flag<Boolean> allRaysInitialized = new Flag<Boolean>(false);
    boolean listening = false;

    public Flag<Boolean> getAllRaysInitialized() {
        return allRaysInitialized.getImmutable();
    }

    public RaycastingUT2004(UT2004Bot bot) {
        this(bot, null);
    }
    
    public RaycastingUT2004(UT2004Bot bot, Logger log) {
        super(bot, log);
        idSuffix = "_" + bot.getName() + UUID.randomUUID().toString();
        
        cleanUp();
    }
    
    @Override
    protected void cleanUp() {
    	super.cleanUp();
    	for (Entry<String, WorldObjectFuture<AutoTraceRay>> entry : rayFutures.entrySet()) {
    		final AutoTraceRay ray = entry.getValue().get(0, TimeUnit.MILLISECONDS); 
    		if (ray != null) {
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
    		entry.getValue().cancel(false);
    	}
    	rayFutures.clear();
        allRaysInitialized.setFlag(false);
        alreadyInitialized = 0;
        listening = false;
    }

    /**
     * Deletes all previous rays and makes this instance ready for setting up
     * new rays.
     */
    public void clear() throws CommunicationException {
        act.act(new RemoveRay("All"));
        cleanUp();
    }

    /**
     * Once all rays were initialized using createRay(...) methods, call this
     * method to start listening for response from UT.
     */
    public void endRayInitSequence() {
        listening = true;
        checkIfAllInited();
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
    public Future<AutoTraceRay> createRay(
            String Id, Vector3d Direction, int Length, boolean FastTrace, boolean FloorCorrection, boolean TraceActors) throws CommunicationException {
        AddRay addRay = new AddRay(Id, Direction, Length, FastTrace, FloorCorrection, TraceActors);

        // create pointer to object that will be created in the future
        WorldObjectFuture future = new WorldObjectFuture<AutoTraceRay>(worldView, Id, AutoTraceRay.class) {

            @Override
            protected void customObjectEncounteredHook(AutoTraceRay obj) {
                alreadyInitialized++;
                checkIfAllInited();
            }
        };
        rayFutures.put(Id, future);

        // send ray configuration command
        act.act(addRay);
        return future;
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
    public Future<AutoTraceRay> createRay(
            Vector3d Direction, int Length, boolean FastTrace, boolean FloorCorrection, boolean TraceActors) throws CommunicationException {
        String id = counter++ + idSuffix;
        return createRay(id, Direction, Length, FastTrace, FloorCorrection, TraceActors);
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
        try {
        	Future<AutoTraceRay> ray = rayFutures.get(rayID);
        	if (ray == null) return null;
        	if (ray.isDone()) return ray.get();
        	else return null;
        } catch (Exception ex) {
            if (log.isLoggable(Level.SEVERE)) log.severe(ex.getMessage());
            return null;
        }
    }

    /**
     * Sets {@link RaycastingUT2004#allRaysInitialized} flag to true if all rays has been initialized.
     */
    protected void checkIfAllInited() {
        if (listening && rayFutures.size() == alreadyInitialized) {
            allRaysInitialized.setFlag(true);
        }
    }
    
}

