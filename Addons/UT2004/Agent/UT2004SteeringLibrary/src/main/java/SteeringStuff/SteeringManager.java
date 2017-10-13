package SteeringStuff;

import SocialSteeringsBeta.RefLocation;
import SocialSteeringsBeta.TriangleSteer;
import java.util.HashMap;
import java.util.Random;

import javax.vecmath.Vector3d;

import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.base3d.worldview.object.Rotation;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.Raycasting;
import cz.cuni.amis.pogamut.ut2004.bot.command.AdvancedLocomotion;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Configuration;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.PlayAnimation;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.EndMessage;
import javax.vecmath.Tuple3d;

import SteeringProperties.*;
import Steerings.*;
import java.util.LinkedList;
/**
 * This class manages the whole navigation layer of the steered bot. The manager calls steerings to compute the force, combines all forces and sends the computed next velocity to the locomotion layer (modul locomotion).
 * @author Marki
 */
public class SteeringManager {

    /** Enables/disables all debugging println.*/
    public static final boolean DEBUG = false;

    /** This manager needs botself, raycasting and AdvancedLocomotion. */
    protected UT2004Bot botself;
    //private Raycasting raycasting;
    protected AdvancedLocomotion locomotion;
    protected RaycastingManager rayManager;
    private HashMap<SteeringType,ISteering> mySteerings;
    public HashMap<SteeringType,Double> steeringWeights;
    private HashMap<SteeringType,Vector3d> steeringForces;
    private Vector3d myActualVelocity;
    protected Vector3d myNextVelocity;
    protected double multiplier;
    private double lastVeloWeight = 2;
    private boolean useLastVeloWeight = false;

    public static final double MAX_FORCE = 2000;
    /** How long is the vector of walking velocity. Used for rescaling normal vector, when hitting an obstacle with a ray. */
    protected static final double WALK_VELOCITY_LENGTH=220;
    /** How long is the vector of running velocity. Used for rescaling normal vector, when hitting an obstacle with a ray. */
    //protected static final double RUN_VELOCITY_LENGTH=440;
    private static final double MIN_VALUE_TO_SUM = 0.3*WALK_VELOCITY_LENGTH;
    public static final Location BASIC_LOCATION=new Location(800,-1500,-3446.65);//Mezi stromy (vice v ulici).
    //public static final Location BASIC_LOCATION=new Location(9440,-9500,-3446.65); //Na rohu více na ulici.
    //public static final Location BASIC_LOCATION=new Location(15000,-9500,-3446.65); //Na rohu - dále.
    //public static final Location BASIC_LOCATION=new Location(5200,-2000,-3446.65); //Take nejak u koule.
    //public static final Location BASIC_LOCATION=new Location(4500,-850,-3446.65); //U koule ve stredu mesta.

    /**Whether the rays should be drawned (this value will be the parameter of setDrawTraceLines()).*/
    protected boolean drawRaycasting;
    
    /**If true, the velocity can be enlarged when every active steering agrees.*/
    private boolean canEnlargeVelocity;

    private boolean WAPath = false;
    public static boolean Thomas = false;
    private boolean turning = true;
    private boolean WA_debugg = false;

    /**
     * When we register this listener, we activate our {@link SteeringManager } to steer the bot.
     * This listener will call {@link SteeringManager#run() } on this object.
     */
    private IWorldEventListener<EndMessage> endMessageListener = new IWorldEventListener<EndMessage>() {
        @Override
        public void notify(EndMessage event) {
            //Each time we receive end message run steering method will be called.
            run();
        }
    };

    
    /**
     * Creates the new SteeringManager. This class manages the whole navigation layer of the steered bot. The manager calls steerings to compute the force, combines all forces and sends the computed next velocity to the locomotion layer (modul locomotion).
     * @param bot The bot, who should be steered be these steerings.
     * @param raycasting The instance of the class Raycasting.
     * @param locomotion The AdvancedLocomotion of the bot.
     */
    public SteeringManager(UT2004Bot bot, Raycasting raycasting, AdvancedLocomotion locomotion) {
        this.botself = bot;
        //this.raycasting = raycasting;
        this.locomotion = locomotion;
        this.multiplier = 1;
        rayManager = new RaycastingManager(botself, raycasting);
        mySteerings = new HashMap<SteeringType,ISteering>();
        steeringWeights = new HashMap<SteeringType, Double>();
        steeringForces = new HashMap<SteeringType, Vector3d>();
        steeringManagerInitialized();
        myActualVelocity = new Vector3d();
        myNextVelocity = new Vector3d();
        drawRaycasting = false;
        canEnlargeVelocity = true;
    }

    /**
     *
     * @param bot The bot, who should be steered be these steerings. This class manages the whole navigation layer of the steered bot. The manager calls steerings to compute the force, combines all forces and sends the computed next velocity to the locomotion layer (modul locomotion).
     * @param raycasting The instance of the class Raycasting.
     * @param locomotion The AdvancedLocomotion of the bot.
     * @param multiplier Default value is 1. The multiplier of the velocity. E.g, if you want to make this bot run nearly all the time, set the multiplier 2. But remember, that steerings could work worse.
     */
    public SteeringManager(UT2004Bot bot, Raycasting raycasting, AdvancedLocomotion locomotion, double multiplier) {
        this.botself = bot;
        //this.raycasting = raycasting;
        this.locomotion = locomotion;
        this.multiplier = multiplier;
        rayManager = new RaycastingManager(botself, raycasting);
        mySteerings = new HashMap<SteeringType,ISteering>();
        steeringWeights = new HashMap<SteeringType, Double>();
        steeringForces = new HashMap<SteeringType, Vector3d>();
        steeringManagerInitialized();
        myActualVelocity = new Vector3d();
        myNextVelocity = new Vector3d();
        drawRaycasting = false;
        canEnlargeVelocity = true;
    }

    private void steeringManagerInitialized() {
        locomotion.setWalk();   //The bot is set to walk at normal speed.
        if (Thomas) {
            botself.getAct().act(new PlayAnimation().setName("walk_loop").setLoop(true));
        }
        botself.getAct().act(new Configuration().setDrawTraceLines(drawRaycasting).setAutoTrace(true).setSpeedMultiplier((double)1));
    }

    /**Adds the steering of the steering type from the argument. The weight of this steering wil be 1.*/
    public void addSteering(SteeringType type) {
        addSteering(type, 1);
    }

    /**Adds the steering of the steering type from the argument with the weight from the argument.*/
    public void addSteering(SteeringType type, double weight) {
        if (SteeringManager.DEBUG) System.out.println("WE ADD BEHAVIOR "+type);
        switch (type) {
            case OBSTACLE_AVOIDANCE:
                mySteerings.put(type, new ObstacleAvoidanceSteer(botself, rayManager));
                break;
            case TARGET_APPROACHING:
                mySteerings.put(type, new TargetApproachingSteer(botself));
                break;
            case LEADER_FOLLOWING:
                mySteerings.put(type, new LeaderFollowingSteer(botself));
                break;
            case PATH_FOLLOWING:
                if (WAPath && mySteerings.containsKey(SteeringType.WALK_ALONG)) {   //Když by měl mít WA i PF, tak se budou brát vrcholy z PF a sázet se mu jako cíle WA.
                    
                } else {
                    mySteerings.put(type, new PathFollowingSteer(botself));
                }
                break;
            case PEOPLE_AVOIDANCE:
                mySteerings.put(type, new PeopleAvoidanceSteer(botself));
                break;
            case WALK_ALONG:
                mySteerings.put(type, new WalkAlongSteer(botself));
                break;
            case WALL_FOLLOWING:
                mySteerings.put(type, new WallFollowingSteer(botself, rayManager));
                break;
            case TRIANGLE:
               mySteerings.put(type, new TriangleSteer(botself));
                break;
            case STICK_TO_PATH:
            	mySteerings.put(type, new StickToPathSteer(botself));
                break;
        }
        steeringWeights.put(type, new Double(weight));
    }

    /**Returns true, if the manager has this steering type in the list of used steerings.*/
    public boolean hasSteering(SteeringType type) {
        return mySteerings.containsKey(type);
    }

    /**Removes the steering of the steering type from the argument.*/
    public void removeSteering(SteeringType type) {
        if (SteeringManager.DEBUG) System.out.println("WE REMOVE BEHAVIOR "+type);
        if ((type == SteeringType.OBSTACLE_AVOIDANCE) || (type == SteeringType.WALL_FOLLOWING)) {
            rayManager.removeRays(type);
        }
        mySteerings.remove(type);
        steeringWeights.remove(type);
    }

    /**Sets the steering properties of the steering type from the argument.*/
    public void setSteeringProperties(SteeringType type, SteeringProperties newProperties) {
        if (SteeringManager.DEBUG) System.out.println("WE SET PROPERTIES "+type+" NEW PROPERTIES "+newProperties);
        ISteering steer = mySteerings.get(type);
        if (steer!=null) steer.setProperties(newProperties);
    }
    
    /**
     * The main method. This method must be called in each tick (logic), if we want the navigation layer to compute the next velocity and send it to the locomotion layer.
     * Note: Should not be called anymore. Use start() and stop() methods.
     */
    public void run() {
        steeringForces.clear();

        Vector3d velocity = botself.getVelocity().getVector3d();

        if (SteeringManager.DEBUG) System.out.println("Velocity "+velocity+" length "+velocity.length());
                
        // Supposed velocity in the next tick of logic, after applying various steering forces to the bot.
        Vector3d nextVelocity = new Vector3d(velocity.x, velocity.y, velocity.z);

        double actualWeight;

        if (useLastVeloWeight) {
            actualWeight = lastVeloWeight;
        } else {
            actualWeight = 3 - velocity.length()/WALK_VELOCITY_LENGTH;  //This causes that <= WALK_VEOCITY_LENGTH will have actualWeight 2, sth. >= 2*WALK_VELOCITY_LENGTH 1, and other values wil be between 1 and 2.
            if (actualWeight <1)
                actualWeight = 1;
            else if (actualWeight > 2)
                actualWeight = 2;
            if (velocity.length() == 0)
                actualWeight = 0;
        }

        //The actual velocity has bigger weigh ==> the behavior will be smoother.   //5389.0,-6203.0,-3446.65
        nextVelocity.scale(actualWeight);

        myActualVelocity = new Vector3d(nextVelocity.x, nextVelocity.y, nextVelocity.z);
        Vector3d myStopVelocity = new Vector3d(nextVelocity.x, nextVelocity.y, nextVelocity.z);
        
        double totalWeight = actualWeight;
        
        boolean everyoneWantsToGoFaster = canEnlargeVelocity;
        RefBoolean wantsToGoFaster = new RefBoolean(false);
        RefBoolean wantsToStop = new RefBoolean(false);
        Location focusLoc = new Location(0,0,0);
        
        for(SteeringType stType : mySteerings.keySet()) {
            ISteering steering = mySteerings.get(stType);
            RefLocation newFocus = new RefLocation(); 
            newFocus.data = new Location(0, 0, 0);
            Vector3d newVelocity = setVelocitySpecific(steering, wantsToGoFaster, wantsToStop, newFocus);
            focusLoc = setFocusSpecific(stType,wantsToStop.getValue(),newFocus.data,focusLoc);
            if (wantsToStop.getValue()) {   //Wants to stop causes, tak bot stops, if this steering is the only one. Otherwise the other steerings can cause that bot will again move.
                newVelocity.x = -myStopVelocity.x;
                newVelocity.y = -myStopVelocity.y;
                newVelocity.z = -myStopVelocity.z;
                myStopVelocity.sub(newVelocity);
                everyoneWantsToGoFaster = false;
                if (SteeringManager.DEBUG) System.out.println("We stop.");
                wantsToStop.setValue(false);
            } else {
                if (newVelocity.length() > MAX_FORCE) newVelocity.scale(MAX_FORCE/newVelocity.length());
                newVelocity.scale(steeringWeights.get(stType)); //Each steering has its own weight.
                everyoneWantsToGoFaster = everyoneWantsToGoFaster && wantsToGoFaster.getValue();
            }
            if (newVelocity.length()>0) {
                //TODO: WARNING hack to use different type of steering return values
                //it should be redone, more cleaner and robust way... Petr B.
                newVelocity.add((Tuple3d)nextVelocity);
                nextVelocity = newVelocity;
                if (newVelocity.length() > MIN_VALUE_TO_SUM)    //Only significant steerings are counted into totalWeight.
                    totalWeight += steeringWeights.get(stType);
            }
            if (SteeringManager.DEBUG) System.out.println(steering.toString()+"| length "+newVelocity.length()+" | weight: "+steeringWeights.get(stType));
            steeringForces.put(stType, newVelocity);
        }
        if (SteeringManager.DEBUG) System.out.print("Sum "+nextVelocity.length()+" TotalWeight: "+totalWeight);
        if (totalWeight > 0) {
            nextVelocity.scale(1/totalWeight);
        }
        if (SteeringManager.DEBUG) System.out.println(" Result "+nextVelocity.length());

        moveTheBot(nextVelocity, everyoneWantsToGoFaster, focusLoc);
    }

    /**This method is used mainly in SteeringManager.run(). 
     * But if we want to stop the bot - and after a while make him again to walk,
     * we can remember his velocity befor stopping and use this methode.*/
    public void moveTheBot(Vector3d nextVelocity, boolean everyoneWantsToGoFaster, Location focusLocation) {

        double nextVelocityLength = nextVelocity.length() * multiplier; //The multiplier enables to enlarge or decrease the velocity. E.g. to make the bot to run.

        if (SteeringManager.DEBUG) System.out.println("next velocity before scaling "+nextVelocityLength+" : "+(nextVelocityLength/WALK_VELOCITY_LENGTH));

        if (nextVelocityLength == 0) {     //If the velocity is too small, we could turn round. Maybe the better solution would be to count the next location just more far away from the actual location.
            if (!focusLocation.equals(new Location(0,0,0))) {
                if (turning) {
                    locomotion.turnTo(focusLocation);
                    if (SteeringManager.DEBUG) System.out.println("We stop and turn to the location "+focusLocation);
                }
            } else {
                if (SteeringManager.DEBUG) System.out.println("We stop but don't turn to the location "+focusLocation);
            }
            locomotion.stopMovement();
            if (Thomas) botself.getAct().act(new PlayAnimation().setName("idleanim").setLoop(true));
            
            myNextVelocity = new Vector3d(0,0,0);
            return;
        }

        if (nextVelocityLength < 0.8*WALK_VELOCITY_LENGTH && everyoneWantsToGoFaster) {
            if (SteeringManager.DEBUG) System.out.println("we enlarge the velocity");
            nextVelocityLength = 0.8 * WALK_VELOCITY_LENGTH;
        }

        double nextVelMult = nextVelocityLength / WALK_VELOCITY_LENGTH;

        /* According to the velocity magnitude we decide, if the bot wil run or walk. The treshold is 2.5*WALK_VELOCITY_LENGTH.
         * Ideal are the values between 0.8 and 1.2. The bigger values (up to 2.5) can cause, that the bot looks like "skying".
         * 0.8*WALK_VELOCITY_LENGTH means the velocity magnitude 176, 1.2*WALK_VELOCITY_LENGTH --> 264, 2.5*WALK_VELOCITY_LENGTH --> 550, 20.5*WALK_VELOCITY_LENGTH --> 4510.*/
        if (nextVelMult > 2.5) {
            locomotion.setRun();
            if (Thomas) botself.getAct().act(new PlayAnimation().setName("run_normal01").setLoop(true));
            if (SteeringManager.DEBUG) System.out.println("run");
            if (nextVelMult > 20.5) {
                nextVelMult = 20.5;
            }
            nextVelMult -= 2.5;
            nextVelMult = nextVelMult/18;
            nextVelMult = (0.8+nextVelMult);
        } else {
            locomotion.setWalk();
            if (Thomas) botself.getAct().act(new PlayAnimation().setName("walk_loop").setLoop(true));
            if (SteeringManager.DEBUG) System.out.println("walk");
            if (nextVelMult > 0.8) {
                nextVelMult = 0.85 + 0.1*Math.sqrt(10*(nextVelMult - 0.8));
            } else {
                nextVelMult = nextVelMult*0.75 + 0.25;
            }
        }
        nextVelocityLength = nextVelMult * WALK_VELOCITY_LENGTH;
        nextVelocity.normalize();
        nextVelocity.scale(nextVelocityLength);

        myNextVelocity = new Vector3d(nextVelocity.x, nextVelocity.y, nextVelocity.z);
        
        if (SteeringManager.DEBUG) System.out.println("next velocity "+nextVelocity.length()+" : "+(nextVelocity.length()/WALK_VELOCITY_LENGTH));
        
        //we change the bot's speed and turn him.
        botself.getAct().act(new Configuration().setSpeedMultiplier(nextVelocityLength / WALK_VELOCITY_LENGTH).setAutoTrace(true).setDrawTraceLines(drawRaycasting));

        locomotion.moveTo(new Location(botself.getLocation().x + nextVelocity.x, botself.getLocation().y + nextVelocity.y, botself.getLocation().z));

        if (WA_debugg && mySteerings.containsKey(SteeringType.WALK_ALONG)) {
            WalkAlongSteer WAsteering = (WalkAlongSteer)mySteerings.get(SteeringType.WALK_ALONG);
            myActualVelocity = WAsteering.getForceToPartner();
            if (myActualVelocity == null) {
                myActualVelocity = new Vector3d(0,0,0);
            }
            myNextVelocity = WAsteering.getForceToTarget();
            if (myNextVelocity == null) {
                myNextVelocity = new Vector3d(0,0,0);
            }
        }
    }

    /**Returns the sum of two loactions.*/
    private Location addLocations(Location focusLoc, Location newFocus) {
        if (focusLoc.equals(new Location(0,0,0))) return newFocus;
        if (newFocus.equals(new Location(0,0,0))) return focusLoc;
        Location result = new Location((focusLoc.x + newFocus.x) / 2, (focusLoc.y + newFocus.y) / 2, (focusLoc.z + newFocus.z) / 2);
        return result;
    }

    /**Returns the random location near to the BASIC_LOCATION.*/
    public static Location getRandomStartLocation() {
        Random random = new Random();
        int znam = 1;
        if (random.nextBoolean()) znam = -1;
        return new Location(BASIC_LOCATION.x - random.nextInt(500)*znam, BASIC_LOCATION.y - random.nextInt(500)*znam, BASIC_LOCATION.z);
    }

    /**Returns the random rotation.*/
    public static Rotation getRandomStartRotation() {
        Random random = new Random();
        return new Rotation(0,angleToUTUnits(random.nextInt(360)-180),0);
    }

    /**
     * convert angle to UT rotation units
     * @param angle
     * @return
     */
    private static int angleToUTUnits(double angle){
        return (int) Math.round((angle*65535)/360);
    }

    /**Returns the hasp map of used steering forces.*/
    public HashMap<SteeringType, Vector3d> getSteeringForces() {
        return steeringForces;
    }

    /**Returns the scaled actual velocity.*/
    public Vector3d getMyActualVelocity() {
        return myActualVelocity;
    }

    /**Returns the computed next velocity.*/
    public Vector3d getMyNextVelocity() {
        return myNextVelocity;
    }

    /**Returns whether the rays are drawn in the UE2.*/
    public boolean isDrawRaycasting() {
        return drawRaycasting;
    }

    /**Sets whether the rays in UE2 should be drawned.*/
    public void setDrawRaycasting(boolean drawRaycasting) {
        this.drawRaycasting = drawRaycasting;
    }

    /**If true, the velocity can be enlarged when every active steering agrees.*/
    public void setCanEnlargeVelocity(boolean canEnlargeVelocity) {
        this.canEnlargeVelocity = canEnlargeVelocity;
    }

    /**Returns whether the velocity can be enlarged when every active steering agrees.*/
    public boolean isCanEnlargeVelocity() {
        return canEnlargeVelocity;
    }
    
    /**Sets the multiplier of the velocity. The computed velocity vektor will be multiplied by this value.
     * E.g, if you want to make this bot run nearly all the time, set the multiplier 2 (or 3).
     * But remember, that steerings could work worse.*/
    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }

    public void setLastVeloWeight(double lastVeloWeight) {
        this.lastVeloWeight = lastVeloWeight;
    }

    public void setUseLastVeloWeight(boolean useLastVeloWeight) {
        this.useLastVeloWeight = useLastVeloWeight;
    }

    /**
     * Registers {@link SteeringManager#endMessageListener } and by this activates
     * steering navigation.
     */
    public void start() {
        if (!botself.getWorldView().isListening(EndMessage.class, endMessageListener))
            botself.getWorldView().addEventListener(EndMessage.class, endMessageListener);
    }

    /**
     * Unregisters {@link SteeringManager#endMessageListener } - steering manager
     * will cease to navigate the bot (the bot will stop, but steering settings
     * will be preserved).
     */
    public void stop() {
        if (botself.getWorldView().isListening(EndMessage.class, endMessageListener))
            botself.getWorldView().removeEventListener(EndMessage.class, endMessageListener);
    }

    /**
     * Returns whether we are currently using {@link SteeringManager }
     * for navigation.
     * @return
     */
    public boolean isNavigating() {
        return botself.getWorldView().isListening(EndMessage.class, endMessageListener);
    }

    /**
     * Removes all currently active steerings.
     */
    public void clearSteerings() {
        LinkedList<SteeringType> helpsteeringList = new LinkedList<SteeringType>(mySteerings.keySet());
        for (SteeringType type : helpsteeringList) {
            removeSteering(type);
        }
    }
    
    /**
     * Returns path following properites if this steering is set or null otherwise.
     * @return
     */
    public LeaderFollowingProperties getLeaderFollowingProperties() {
        ISteering steering = mySteerings.get(SteeringType.LEADER_FOLLOWING);
        if (steering != null)
            return ((LeaderFollowingSteer) steering).getProperties();

        return null;
    }

    /**
     * Returns path following properites if this steering is set or null otherwise.
     * @return
     */
    public ObstacleAvoidanceProperties getObstacleAvoidanceProperties() {
        ISteering steering = mySteerings.get(SteeringType.OBSTACLE_AVOIDANCE);
        if (steering != null)
            return ((ObstacleAvoidanceSteer) steering).getProperties();

        return null;
    }

    /**
     * Returns path following properites if this steering is set or null otherwise.
     * @return
     */
    public PathFollowingProperties getPathFollowingProperties() {
        ISteering steering = mySteerings.get(SteeringType.PATH_FOLLOWING);
        if (steering != null)
            return ((PathFollowingSteer) steering).getProperties();

        return null;
    }

    /**
     * Returns people avoidance properites if this steering is set or null otherwise.
     * @return
     */
    public PeopleAvoidanceProperties getPeopleAvoidanceProperties() {
        ISteering steering = mySteerings.get(SteeringType.PEOPLE_AVOIDANCE);
        if (steering != null)
            return ((PeopleAvoidanceSteer) steering).getProperties();

        return null;
    }

    /**
     * Returns target approaching properites if this steering is set or null otherwise.
     * @return
     */
    public TargetApproachingProperties getTargetApproachingProperties() {
        ISteering steering = mySteerings.get(SteeringType.TARGET_APPROACHING);
        if (steering != null)
            return ((TargetApproachingSteer) steering).getProperties();

        return null;
    }

    /**
     * Returns walk along properites if this steering is set or null otherwise.
     * @return
     */
    public WalkAlongProperties getWalkAlongProperties() {
        ISteering steering = mySteerings.get(SteeringType.WALK_ALONG);
        if (steering != null)
            return ((WalkAlongSteer) steering).getProperties();

        return null;
    }

    /**
     * Returns wall following properites if this steering is set or null otherwise.
     * @return
     */
    public WallFollowingProperties getWallFollowingProperties() {
        ISteering steering = mySteerings.get(SteeringType.WALL_FOLLOWING);
        if (steering != null)
            return ((WallFollowingSteer) steering).getProperties();
        
        return null;
    }

    public void addLeaderFollowingSteering(LeaderFollowingProperties properties) {
        addSteering(SteeringType.LEADER_FOLLOWING);
        setSteeringProperties(SteeringType.LEADER_FOLLOWING, properties);
    }

    public void removeLeaderFollowingSteering() {
        removeSteering(SteeringType.LEADER_FOLLOWING);
    }

    public void setLeaderFollowingSteering(LeaderFollowingProperties properties) {
        setSteeringProperties(SteeringType.LEADER_FOLLOWING, properties);
    }

    public boolean isLeaderFollowingActive() {
        return hasSteering(SteeringType.LEADER_FOLLOWING);
    }

    public void addObstacleAvoidanceSteering(ObstacleAvoidanceProperties properties) {
        addSteering(SteeringType.OBSTACLE_AVOIDANCE);
        setSteeringProperties(SteeringType.OBSTACLE_AVOIDANCE, properties);
    }

    public void removeObstacleAvoidanceSteering() {
        removeSteering(SteeringType.OBSTACLE_AVOIDANCE);
    }

    public void setObstacleAvoidanceSteering(ObstacleAvoidanceProperties properties) {
        setSteeringProperties(SteeringType.OBSTACLE_AVOIDANCE, properties);
    }

    public boolean isObstacleAvoidanceActive() {
        return hasSteering(SteeringType.OBSTACLE_AVOIDANCE);
    }

    public void addPathFollowingSteering(PathFollowingProperties properties) {
        addSteering(SteeringType.PATH_FOLLOWING);
        setSteeringProperties(SteeringType.PATH_FOLLOWING, properties);
    }

    public void removePathFollowingSteering() {
        removeSteering(SteeringType.PATH_FOLLOWING);
    }

    public void setPathFollowingSteering(PathFollowingProperties properties) {
        setSteeringProperties(SteeringType.PATH_FOLLOWING, properties);
    }

    public boolean isPathFollowingActive() {
        return hasSteering(SteeringType.PATH_FOLLOWING);
    }

    public void addPeopleAvoidanceSteering(PeopleAvoidanceProperties properties) {
        addSteering(SteeringType.PEOPLE_AVOIDANCE);
        setSteeringProperties(SteeringType.PEOPLE_AVOIDANCE, properties);
    }

    public void removePeopleAvoidanceSteering() {
        removeSteering(SteeringType.PEOPLE_AVOIDANCE);
    }

    public void setPeopleAvoidanceSteering(PeopleAvoidanceProperties properties) {
        setSteeringProperties(SteeringType.PEOPLE_AVOIDANCE, properties);
    }

    public boolean isPeopleAvoidanceActive() {
        return hasSteering(SteeringType.PEOPLE_AVOIDANCE);
    }

    public void addTargetApproachingSteering(TargetApproachingProperties properties) {
        addSteering(SteeringType.TARGET_APPROACHING);
        setSteeringProperties(SteeringType.TARGET_APPROACHING, properties);
    }

    public void removeTargetApproachingSteering() {
        removeSteering(SteeringType.TARGET_APPROACHING);
    }

    public void setTargetApproachingSteering(TargetApproachingProperties properties) {
        setSteeringProperties(SteeringType.TARGET_APPROACHING, properties);
    }

    public boolean isTargetApproachingActive() {
        return hasSteering(SteeringType.TARGET_APPROACHING);
    }

    public void addWalkAlongSteering(WalkAlongProperties properties) {
        addSteering(SteeringType.WALK_ALONG);
        setSteeringProperties(SteeringType.WALK_ALONG, properties);
    }

    public void removeWalkAlongSteering() {
        removeSteering(SteeringType.WALK_ALONG);
    }

    public void setWalkAlongSteering(WalkAlongProperties properties) {
        setSteeringProperties(SteeringType.WALK_ALONG, properties);
    }

    public boolean isWalkAlongActive() {
        return hasSteering(SteeringType.WALK_ALONG);
    }

    public void addWallFollowingSteering(WallFollowingProperties properties) {
        addSteering(SteeringType.WALL_FOLLOWING);        
        setSteeringProperties(SteeringType.WALL_FOLLOWING, properties);
    }

    public void removeWallFollowingSteering() {
        removeSteering(SteeringType.WALL_FOLLOWING);
    }

    public void setWallFollowingSteering(WallFollowingProperties properties) {
        setSteeringProperties(SteeringType.WALL_FOLLOWING, properties);
    }

    public boolean isWallFollowingActive() {
        return hasSteering(SteeringType.WALL_FOLLOWING);
    }
    
	public void addStickToPathSteering(StickToPathProperties properties) {
		addSteering(SteeringType.STICK_TO_PATH);        
        setSteeringProperties(SteeringType.STICK_TO_PATH, properties);
	}

	public void removeStickToPathSteering() {
        removeSteering(SteeringType.STICK_TO_PATH);
    }

    public void setStickToPathSteering(StickToPathProperties properties) {
        setSteeringProperties(SteeringType.STICK_TO_PATH, properties);
    }

    public boolean isStickToPathSteeringActive() {
        return hasSteering(SteeringType.STICK_TO_PATH);
    }

    // <editor-fold defaultstate="collapsed" desc="new methods">
    protected Location setFocusSpecific(SteeringType steeringType, boolean wantsToStop, Location newFocus, Location focusLoc) {
        if(wantsToStop)
        {
            return addLocations(focusLoc, newFocus);
        }
        else
        {
            return focusLoc;
        }
        
    }
    
    /**
     When owerriden can provide different behaviour of steering computation 
     * i.e. can tunnel some other information like true distance to target place
     * it is used for the social steerings...
     */
    protected Vector3d setVelocitySpecific(ISteering steering, RefBoolean wantsToGoFaster, RefBoolean wantsToStop, RefLocation newFocus) {
        Vector3d newVelocity = steering.run(myActualVelocity, wantsToGoFaster, wantsToStop, newFocus);
        return newVelocity;
    }
    
    // </editor-fold>
    
}
