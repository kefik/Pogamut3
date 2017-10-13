package SteeringStuff;

import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.Raycasting;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.AutoTraceRay;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.Future;

/**
 * This class provides the rays of the bot in the navigation layer. The rays could be changed more times (actually two steerings use rays).
 * @author Marki
 */
public class RaycastingManager {
    
    private UT2004Bot botself;
    public Raycasting raycasting;

    private static boolean fastTrace = false;
    private static boolean floorCorrection = false;
    private static boolean traceActor = false;

    private HashMap<SteeringType, LinkedList<SteeringRay>> raysMap = new HashMap<SteeringType, LinkedList<SteeringRay>>();
    private HashMap<SteeringType, IRaysFlagChanged> rayFlagChangedListeners = new HashMap<SteeringType, IRaysFlagChanged>();
    private HashMap<SteeringType, HashMap<String, Future<AutoTraceRay>>> rayFutures = new HashMap<SteeringType, HashMap<String, Future<AutoTraceRay>>>();

    /** Creates the new RaycastingManager of the botself with the raycasting module.*/
    public RaycastingManager(UT2004Bot botself, Raycasting raycasting) {
        this.botself = botself;
        this.raycasting = raycasting;
    }

    /** Adds new ISteeringPropertiesChangedListener - he will listen for changes in some steering properties.*/
    public void addRayFlagChangedListener(SteeringType type, IRaysFlagChanged listener) {
        rayFlagChangedListeners.put(type, listener);
    }

    /** Notify the listneres, that the steering properties were changed.*/
    public void notifyRayFlagChangedListeners() {
        for (IRaysFlagChanged listener : rayFlagChangedListeners.values()) {
            if (SteeringManager.DEBUG) System.out.println("Flag ray changed. We tell to "+listener);
            listener.flagRaysChanged();
        }
    }

    /**Adds rays of the type. The rays are in the rayList. The listener will be added.*/
    public void addRays(SteeringType type, LinkedList<SteeringRay> rayList, IRaysFlagChanged listener) {
        raysMap.put(type, rayList);
        addRayFlagChangedListener(type, listener);
        prepareRays();        
    }

    /**The rays of the type will be removed.*/
    public void removeRays(SteeringType type) {
        if (SteeringManager.DEBUG) System.out.println("We remove rays for "+type);
        raysMap.remove(type);
        rayFlagChangedListeners.remove(type);
        prepareRays();
    }

    /**Returns the futureRays of the type.*/
    public HashMap<String, Future<AutoTraceRay>> getMyFutureRays(SteeringType type) {
        return rayFutures.get(type);
    }

    /**Returns whether the rays are ready (the flag of getAllRaysInitialized).*/
    public boolean raysAreReady() {
        return raycasting.getAllRaysInitialized().getFlag();
    }

    //Prepares rays for the bot, removes any old rays and sets new ones.
    private void prepareRays() {
        raycasting.clear();
        rayFutures.clear();
        
        //botself.getAct().act(new RemoveRay("All"));
        
        for(SteeringType type : raysMap.keySet()) {
            LinkedList<SteeringRay> rayList = raysMap.get(type);
            if (SteeringManager.DEBUG) System.out.println("We prepare rays for "+type);
            HashMap<String, Future<AutoTraceRay>> fr = new HashMap<String, Future<AutoTraceRay>>();
            for(SteeringRay ray : rayList) {
                Future<AutoTraceRay> future = raycasting.createRay(ray.id, ray.direction, ray.length, fastTrace, floorCorrection, traceActor);
                //System.out.println("Ray "+ray.id);
                fr.put(ray.id, future);
            }
            rayFutures.put(type, fr);
        }
        
        raycasting.endRayInitSequence();
        notifyRayFlagChangedListeners();
    }
}
