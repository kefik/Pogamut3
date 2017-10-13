package Steerings;

import SocialSteeringsBeta.RefLocation;
import SteeringStuff.SteeringManager;
import SteeringStuff.SteeringTools;
import SteeringStuff.RefBoolean;
import SteeringProperties.SteeringProperties;
import SteeringProperties.WallFollowingProperties;
import SteeringStuff.IRaysFlagChanged;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import javax.vecmath.Vector3d;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.AutoTraceRay;
import SteeringStuff.ISteering;
import SteeringStuff.RaycastingManager;
import SteeringStuff.SteeringRay;
import SteeringStuff.SteeringType;
import cz.cuni.amis.pogamut.ut2004.utils.UnrealUtils;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.vecmath.Tuple3d;


/**
 * A class for providing wall following steering to bots via raycasting.
 *
 * @author Marki
 */
public class WallFollowingSteer implements ISteering, IRaysFlagChanged {
    /** This steering needs botself, raycasting manager. */
    private UT2004Bot botself;
    private RaycastingManager rayManager;

    private int wallForce;
    /** Řád síly - 1-10. */
    private int orderOfTheForce;
    /**Steering properties: */
    private double attractiveForceWeight;
    /**Steering properties: */
    private double repulsiveForceWeight;
    /**Steering properties: */
    private double convexEdgesForceWeight;
    /**Steering properties: */
    private double concaveEdgesForceWeight;
    /**Steering properties: */
    private boolean justMySide;
    /**Steering properties: */
    private boolean specialDetection;
    /**Steering properties: */
    private boolean frontCollisions;
    

    /** ISteering properties:  distance from the wall - how far from the wall should the bot go.*/
    int DISTANCE_FROM_THE_WALL = 166;
    //How big is the value of the bot's counter when leaving a wall. (For so many ticks he is turning.)
    private static final int DEFAULTCOUNTER = 10;
    //Tento parametr je asi na pytel, páč to s ním stejně moc dobře nefunguje. Takže je jako konstanta a asi ho smažu.
    private boolean goFast = false;

    //Possible states of the bot - whether he's following nothing, the left hand, or the right hand.
    enum State { NOTHING, LEFT, RIGHT };
    
    
    //The bot's state.
    State state;

    //Counter of bot's memory, when leaving a wall (90° turns etc.).
    int counter;

    //Help variable to remember, when we were turning beacause of the convex edge --> we will diminish the force concave edges.
    boolean convexTurning;
    
    private boolean raysReady;
    private HashMap<String, Future<AutoTraceRay>> myFutureRays;

    // Constants for rays' ids
    protected static final String NLEFTFRONT = "wleftfront";
    protected static final String NLEFT = "wleft";
    protected static final String NRIGHTFRONT = "wrightfront";
    protected static final String NRIGHT = "wright";
    protected static final String NFRONT = "wfront";

    Random random = new Random();
    
    /**
     * Bot's rays.
     */
    AutoTraceRay nleft, nright, nleftfront, nrightfront, nfront;

    /**
     * Lengths of bot's rays. Short rays are supposed for side rays, long rays for nfront rays.
     */
    int shortSideRayLength;
    int shortSideFrontRayLength;
    int longSideRayLength;
    int longSideFrontRayLength;
    int shortFrontRayLength;
    int longFrontRayLength;


    /**
     *
     * @param bot Instance of the steered bot.
     * @param rayManager Raycasting manager of the steered bot.
     * @param newProperties Sets the steering properties.
     */
    public WallFollowingSteer(UT2004Bot bot, RaycastingManager rayManager, SteeringProperties newProperties) {
        botself = bot;
        this.rayManager = rayManager;
        setProperties(newProperties);   //This is not necessary.
        state = State.NOTHING;
        counter = 0;
        convexTurning = false;
        prepareRays();
    }

    /**
     *
     * @param bot Instance of the steered bot.
     * @param rayManager Raycasting manager of the steered bot.
     */
    public WallFollowingSteer(UT2004Bot bot, RaycastingManager rayManager) {
        botself = bot;
        this.rayManager = rayManager;
        setProperties(new WallFollowingProperties());   //This is not necessary.
        state = State.NOTHING;
        counter = 0;
        convexTurning = false;
        prepareRays();
    }

    //<editor-fold defaultstate="collapsed" desc="Rays">

    //Prepares rays for the bot, removes any old rays and sets new ones.
    private void prepareRays() {

        /*Délky postranních paprsků jsou 8 a 12. Délky šikmých se vynásobí 2 a předního se vynásobí odmocninou(3).*/
        int shortLength = 8;
        int longLength = 12;

        shortSideRayLength = (int) (UnrealUtils.CHARACTER_COLLISION_RADIUS * shortLength * DISTANCE_FROM_THE_WALL / 166f);        //8
        longSideRayLength = (int) (UnrealUtils.CHARACTER_COLLISION_RADIUS * longLength * DISTANCE_FROM_THE_WALL / 166f);        //12
        shortSideFrontRayLength = (int) (UnrealUtils.CHARACTER_COLLISION_RADIUS * shortLength * 2 * DISTANCE_FROM_THE_WALL / 166f);  //20
        longSideFrontRayLength = (int) (UnrealUtils.CHARACTER_COLLISION_RADIUS * longLength * 2 * DISTANCE_FROM_THE_WALL / 166f);   //30
        shortFrontRayLength = (int) (UnrealUtils.CHARACTER_COLLISION_RADIUS * shortLength * Math.sqrt(3) * DISTANCE_FROM_THE_WALL / 166f);      //18
        longFrontRayLength = (int) (UnrealUtils.CHARACTER_COLLISION_RADIUS * longLength * Math.sqrt(3) * DISTANCE_FROM_THE_WALL / 166f);       //27
        
        //Five rays are created.
        LinkedList<SteeringRay> rayList = new LinkedList<SteeringRay>();
        rayList.add(new SteeringRay(NLEFT, new Vector3d(0, -1, 0), longSideRayLength));
        rayList.add(new SteeringRay(NLEFTFRONT, new Vector3d(Math.sqrt(3), -1, 0), longSideFrontRayLength));
        rayList.add(new SteeringRay(NRIGHTFRONT, new Vector3d(Math.sqrt(3), 1, 0), longSideFrontRayLength));
        rayList.add(new SteeringRay(NRIGHT, new Vector3d(0, 1, 0), longSideRayLength));
        rayList.add(new SteeringRay(NFRONT, new Vector3d(1, 0, 0), longFrontRayLength));
        rayManager.addRays(SteeringType.WALL_FOLLOWING, rayList, this);
        raysReady = false;
        //System.out.println("Rays wall preparation end.");
    }


    public void flagRaysChanged() {
        myFutureRays = rayManager.getMyFutureRays(SteeringType.WALL_FOLLOWING);
        raysReady = false;
        listenToRays();
    }

    private void listenToRays() {
        for(String rayId : myFutureRays.keySet()) {
            Future<AutoTraceRay> fr = myFutureRays.get(rayId);
            if (fr.isDone()) {
                //System.out.println("Ray done."+rayId);
                try {
                    if (rayId.equals(NLEFTFRONT)) {
                        nleftfront = fr.get();
                    } else if (rayId.equals(NRIGHTFRONT)) {
                        nrightfront = fr.get();
                    } else if (rayId.equals(NLEFT)) {
                        nleft = fr.get();
                    } else if (rayId.equals(NRIGHT)) {
                        nright = fr.get();
                    } else if (rayId.equals(NFRONT)) {
                        nfront = fr.get();
                    }
                    raysReady = true;
                } catch (InterruptedException ex) {
                    Logger.getLogger(ObstacleAvoidanceSteer.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ExecutionException ex) {
                    Logger.getLogger(ObstacleAvoidanceSteer.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                //System.out.println("Ray isn't done."+rayId);
            }
        }
    }
    //</editor-fold>
    
    /**
     * When called, the bot starts steering, when possible, he walks straight, he steers away from obstacles though, when necessary.
     * If the isn't a wall, he goes straight forward.
     */
    @Override
    public Vector3d run(Vector3d scaledActualVelocity, RefBoolean wantsToGoFaster, RefBoolean wantsToStop, RefLocation focus) {

        //<editor-fold defaultstate="collapsed" desc="Set and init variables">

        wantsToGoFaster.setValue(false);
        //Supposed velocity in the next tick of logic, after applying various steering forces to the bot. If no sensor is active, it will stay as the current velocity.
        Vector3d nextVelocity = new Vector3d(0,0,0);

        if (!raysReady) {
            listenToRays();
            return nextVelocity;
        }
        
        //A vector from the point where one of bot's rays crosses the wall to the bot.
        Vector3d botToHitLocation;

        //Normal to the point, where bot's ray intersect with wall.
        Vector3d normal;
        
        //Normal vector is multiplied by this when applied to the bot.
        double multiplier;

        //The bot's velocity is received.
        Vector3d actualVelocity = botself.getVelocity().getVector3d();

        //Whether any of the former short rays is triggered - i.e. whether the bot is close to any wall. If not, he may walk at normal speed.
        boolean shortrays = false;

        //Whether sensors signalize the collision. (Computed in the run())
        boolean sensornLeft = false;
        boolean sensornRight = false;
        boolean sensornLeftFront = false;
        boolean sensornRightFront = false;
        boolean sensornFront = false;
        
        //The bot checks his sensors
        if (nleftfront != null)
            sensornLeftFront = nleftfront.isResult();
        if (nrightfront != null)
            sensornRightFront = nrightfront.isResult();
        if (nleft != null)
            sensornLeft = nleft.isResult();
        if (nright != null)
            sensornRight = nright.isResult();
        if (nfront != null)
            sensornFront = nfront.isResult();

        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Set state">

        //The bot may change his state.
        if (state == State.NOTHING) {
            //If the bot is in NOTHING state and he touches the wall with both of his negative side rays
            //(pleft and pleftront or pright and prightfront), he switches to the appropriate state.
            if (sensornLeft && sensornLeftFront) {
                state = State.LEFT;
                counter = DEFAULTCOUNTER;
            } else if (sensornRight && sensornRightFront) {
                state = State.RIGHT;
                counter = DEFAULTCOUNTER;
            } else if (sensornFront) {
                if (frontCollisions) {
                    botToHitLocation = new Vector3d(nfront.getHitLocation().x - botself.getLocation().x, nfront.getHitLocation().y - botself.getLocation().y, 0);
                    normal = nfront.getHitNormal();
                    if (Math.PI - normal.angle(actualVelocity) < Math.PI/5) {   //Nějak téměř kolmo na zeď.
                        boolean turnLeft;
                        if (normal.angle(actualVelocity) == 180) {
                            turnLeft = random.nextBoolean();
                            if (SteeringManager.DEBUG) {
                                System.out.println("Wall exactly front collision.");
                            }
                        } else {
                            turnLeft = SteeringTools.pointIsLeftFromTheVector(actualVelocity, normal);
                            if (SteeringManager.DEBUG) {
                                System.out.println("Wall nearly front collision.");
                            }
                        }
                        if (SteeringManager.DEBUG) {
                            System.out.println("We turn left " + turnLeft);
                        }
                        Vector3d turn = SteeringTools.getTurningVector(actualVelocity, turnLeft);
                        turn.normalize();
                        turn.scale(0.5);
                        normal.add(turn);
                        normal.normalize();
                        multiplier = Math.pow(((longFrontRayLength - botToHitLocation.length()) * 2f / longFrontRayLength), orderOfTheForce);
                        normal.scale(multiplier * wallForce);
                        nextVelocity.add((Tuple3d) normal);
                        return nextVelocity;
                    }
                }
            }
        } else if (state == State.LEFT) {
            if (sensornLeft && sensornLeftFront) {//The bot walks alongside the wall, he replenishes his counter.
                counter = DEFAULTCOUNTER;
            }
        } else if (state == State.RIGHT) {
            if (sensornRight && sensornRightFront) {//The bot walks alongside the wall, he replenishes his counter.
                counter = DEFAULTCOUNTER;
            }
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Just my side parameter">

        if (justMySide) {
            if (state.equals(State.LEFT)) {
                sensornRight = false;
                sensornRightFront = false;
            } else if (state.equals(State.RIGHT)) {
                sensornLeft = false;
                sensornLeftFront = false;
            }
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Setting the wantsToGoFaster value.">

        if (goFast) {
            if (sensornFront) { //If wall is in front of the bot, he shouldn't go faster.
                wantsToGoFaster.setValue(false);
            } else {
                wantsToGoFaster.setValue(true);
            }
        } else {
            if (!shortrays) {   //If nothing is signalling repulsively, the bot can return to its normal speed.
                wantsToGoFaster.setValue(true);
            } else {
                wantsToGoFaster.setValue(false);
            }
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Main ray forces of the wall">

        /*
         *All sensors are checked respectively. When any of them signalizes a hit, a vector is added to nextvelocity.
         *The vector added is derived from normal vector of the wall, such that when the ray is half in the wall (see multiplier),
         *the steering power is as powerful as walking power at maximal velocity.
         */
        if (sensornLeft) {
            //The force, which hauls the bot towards to the wall.
            botToHitLocation = new Vector3d(nleft.getHitLocation().x - botself.getLocation().x, nleft.getHitLocation().y - botself.getLocation().y, 0);
            normal = nleft.getHitNormal();
            multiplier = Math.pow(1 - (longSideRayLength - botToHitLocation.length()) / longSideRayLength, orderOfTheForce);
            normal.scale(attractiveForceWeight * multiplier * wallForce);
            nextVelocity.sub((Tuple3d) normal);
            //if (SteeringManager.DEBUG) System.out.println("left to "+normal.length());

            //When closer, the bot is pushed away from the wall by another force.
            if (botToHitLocation.length() < shortSideRayLength) {
                shortrays = true;
                multiplier = Math.pow(((shortSideRayLength - botToHitLocation.length()) * 2f / shortSideRayLength), orderOfTheForce);
                normal.normalize();
                normal.scale(repulsiveForceWeight * multiplier * wallForce);
                nextVelocity.add((Tuple3d) normal);
                //if (SteeringManager.DEBUG) System.out.println("left from "+normal.length());
            }
        }

        if (sensornRight) {
            //The force, which hauls the bot towards to the wall.
            botToHitLocation = new Vector3d(nright.getHitLocation().x - botself.getLocation().x, nright.getHitLocation().y - botself.getLocation().y, 0);
            normal = nright.getHitNormal();
            multiplier = Math.pow(1 - (longSideRayLength - botToHitLocation.length()) / longSideRayLength, orderOfTheForce);
            normal.scale(attractiveForceWeight * multiplier * wallForce);
            nextVelocity.sub((Tuple3d) normal);
            //if (SteeringManager.DEBUG) System.out.println("right to "+normal.length());

            //When closer, the bot is pushed away from the wall by another force.
            if (botToHitLocation.length() < shortSideRayLength) {
                shortrays = true;
                multiplier = Math.pow(((shortSideRayLength - botToHitLocation.length()) * 2f / shortSideRayLength), orderOfTheForce);
                normal.normalize();
                normal.scale(repulsiveForceWeight * multiplier * wallForce);
                nextVelocity.add((Tuple3d) normal);
                //if (SteeringManager.DEBUG) System.out.println("right from "+normal.length());
            }
        }
        if (sensornLeftFront) {
            //The force, which hauls the bot towards to the wall.
            botToHitLocation = new Vector3d(nleftfront.getHitLocation().x - botself.getLocation().x, nleftfront.getHitLocation().y - botself.getLocation().y, 0);
            normal = nleftfront.getHitNormal();
            multiplier = Math.pow(1 - (longSideFrontRayLength - botToHitLocation.length()) / longSideFrontRayLength, orderOfTheForce);
            normal.scale(attractiveForceWeight * multiplier * wallForce);
            nextVelocity.sub((Tuple3d) normal);
            //if (SteeringManager.DEBUG) System.out.println("left-front to "+normal.length());

            //When closer, the bot is pushed away from the wall by another force.
            if (botToHitLocation.length() < shortSideFrontRayLength) {
                shortrays = true;
                multiplier = Math.pow(((shortSideFrontRayLength - botToHitLocation.length()) * 2f / shortSideFrontRayLength), orderOfTheForce);
                normal.normalize();
                normal.scale(repulsiveForceWeight * multiplier * wallForce);
                nextVelocity.add((Tuple3d) normal);
                //if (SteeringManager.DEBUG) System.out.println("left-front from "+normal.length());
            }

        }
        if (sensornRightFront) {
            //The force, which hauls the bot towards to the wall.
            botToHitLocation = new Vector3d(nrightfront.getHitLocation().x - botself.getLocation().x, nrightfront.getHitLocation().y - botself.getLocation().y, 0);
            normal = nrightfront.getHitNormal();
            multiplier = Math.pow(1 - (longSideFrontRayLength - botToHitLocation.length()) / longSideFrontRayLength, orderOfTheForce);
            normal.scale(attractiveForceWeight * multiplier * wallForce);
            nextVelocity.sub((Tuple3d) normal);
            //if (SteeringManager.DEBUG) System.out.println("right-front to "+normal.length());

            //When closer, the bot is pushed away from the wall by another force.
            if (botToHitLocation.length() < shortSideFrontRayLength) {
                shortrays = true;
                multiplier = Math.pow(((shortSideFrontRayLength - botToHitLocation.length()) * 2f / shortSideFrontRayLength), orderOfTheForce);
                normal.normalize();
                normal.scale(repulsiveForceWeight * multiplier * wallForce);
                nextVelocity.add((Tuple3d) normal);
                //if (SteeringManager.DEBUG) System.out.println("right-front from "+normal.length());
            }
        }
        if (sensornFront) {
            //No attractive force in this case.
            botToHitLocation = new Vector3d(nfront.getHitLocation().x - botself.getLocation().x, nfront.getHitLocation().y - botself.getLocation().y, 0);
            normal = nfront.getHitNormal();
            //multiplier = Math.pow(1 - (longSideFrontRayLength - botToHitLocation.length()) / longSideFrontRayLength, orderOfTheForce);
            //normal.scale(multiplier * MAGNITUDE_OF_THE_FORCE);
            //nextVelocity.sub((Tuple3d) normal);

            //When closer, the bot is pushed away from the wall by another force.
            if (botToHitLocation.length() < shortFrontRayLength) {
                shortrays = true;
                multiplier = Math.pow(((shortFrontRayLength - botToHitLocation.length()) * 2f / shortFrontRayLength), orderOfTheForce);
                normal.normalize();
                normal.scale(repulsiveForceWeight * 3 * multiplier * wallForce);    //3 krát, aby to mělo větší vliv.
                //if (SteeringManager.DEBUG) System.out.println("Hey front "+normal.length());
                nextVelocity.add((Tuple3d) normal);
            }
        }
        //</editor-fold>
        
        boolean nextTurning = false;
        
        //<editor-fold defaultstate="collapsed" desc="Convex edges">
        //System.out.println("Convex turning. "+convexTurning);
        if (specialDetection) {
            if (state.equals(State.LEFT)) {
                if ( (sensornFront && (convexTurning || (Math.abs(nfront.getHitNormal().angle(actualVelocity) - Math.PI) < Math.PI/4)) ) ||
                   ( (sensornLeftFront && (Math.abs(nleftfront.getHitNormal().angle(actualVelocity) - Math.PI) < Math.PI/2) && convexTurning) ||  //Pokud jsme se začali točit, tak se točíme tak dlouho, až jdeme rovnoběžně se zdí.
                     (sensornLeftFront && (Math.abs(nleftfront.getHitNormal().angle(actualVelocity) - Math.PI) < Math.PI/4)) || //Může se stát, že přední paprsek ten roh mine, ale přesto jdeme kolmo na zeď.
                     (sensornLeft      && (Math.abs(nleft.getHitNormal().angle(actualVelocity)      - Math.PI) < Math.PI/2) && convexTurning) ) ) {
                    if (SteeringManager.DEBUG) System.out.println("Left convex edge. " + SteeringTools.radiansToDegrees(nleftfront.getHitNormal().angle(actualVelocity)));
                    if (SteeringManager.DEBUG) {
                        if (sensornFront) System.out.println("Left convex edge front collision. " + SteeringTools.radiansToDegrees(nfront.getHitNormal().angle(actualVelocity)));
                        else if (sensornLeftFront) System.out.println("Left convex edge side front. " + SteeringTools.radiansToDegrees(nleftfront.getHitNormal().angle(actualVelocity)));
                        else System.out.println("Left convex edge side. " + SteeringTools.radiansToDegrees(nleft.getHitNormal().angle(actualVelocity)));
                    }
                    //Kdykoliv když naráží přední paprsek - či pokud jsme se začali točit a zeď levého paprsku je na nás buď kolmá, či její normála svírá s naší velocity 135° - 225°.
                    nextTurning = true;
                    double k = 1;
                    if (sensornFront) {
                        k = nfront.getHitNormal().angle(actualVelocity) / Math.PI; //Jestliže jsou vektory přímo opačné, bude úhel PI a k=1. Čím je úhel menší, tím bude k menší.
                    }
                    else if (sensornLeftFront) {
                        k = 0.6*nleftfront.getHitNormal().angle(actualVelocity) / Math.PI;
                    }
                    else {
                        k = 0.3*nleft.getHitNormal().angle(actualVelocity) / Math.PI;
                    }
                    if (SteeringManager.DEBUG) {
                        System.out.println("Turning vector. " + k*SteeringTools.getTurningVector2(actualVelocity, false).length());
                    }
                    Vector3d turningVector = SteeringTools.getTurningVector2(actualVelocity, false);
                    turningVector.scale(k*convexEdgesForceWeight);
                    //nextVelocity.add(turningVector);
                    //nextVelocity = turningVector;
                    counter = DEFAULTCOUNTER;
                    convexTurning = nextTurning;
                    return turningVector;
                    //return nextVelocity;
                }
            } else if (state.equals(State.RIGHT)) {
                if ( (sensornFront && (convexTurning || (Math.abs(nfront.getHitNormal().angle(actualVelocity) - Math.PI) < Math.PI/4)) ) ||
                   ( (sensornRightFront && (Math.abs(nrightfront.getHitNormal().angle(actualVelocity) - Math.PI) < Math.PI/2) && convexTurning) ||
                     (sensornRightFront && (Math.abs(nrightfront.getHitNormal().angle(actualVelocity) - Math.PI) < Math.PI/4)) || //Může se stát, že přední paprsek ten roh mine, ale přesto jdeme kolmo na zeď.
                     (sensornRight      && (Math.abs(nright.getHitNormal().angle(actualVelocity)      - Math.PI) < Math.PI/2) && convexTurning) ) ) {
                    if (SteeringManager.DEBUG) {
                        if (sensornFront) System.out.println("Right convex edge front collision. " + SteeringTools.radiansToDegrees(nfront.getHitNormal().angle(actualVelocity)));
                        else if (sensornRightFront) System.out.println("Right convex edge side front. " + SteeringTools.radiansToDegrees(nrightfront.getHitNormal().angle(actualVelocity)));
                        else System.out.println("Right convex edge side. " + SteeringTools.radiansToDegrees(nright.getHitNormal().angle(actualVelocity)));
                    }
                    nextTurning = true;
                    double k = 1;
                    if (sensornFront) {
                        k = nfront.getHitNormal().angle(actualVelocity) / Math.PI; //Jestliže jsou vektory přímo opačné, bude úhel PI a k=1. Čím je úhel menší, tím bude k menší.
                    }
                    else if (sensornRightFront) {
                        k = 0.6*nrightfront.getHitNormal().angle(actualVelocity) / Math.PI;
                    }
                    else {
                        k = 0.3*nright.getHitNormal().angle(actualVelocity) / Math.PI;
                    }
                    if (SteeringManager.DEBUG) {
                        System.out.println("Turning vector. " + k*SteeringTools.getTurningVector2(actualVelocity, true).length());
                    }
                    Vector3d turningVector = SteeringTools.getTurningVector2(actualVelocity, true);
                    turningVector.scale(k*convexEdgesForceWeight);
                    //nextVelocity.add(turningVector);
                    //nextVelocity = turningVector;
                    counter = DEFAULTCOUNTER;
                    convexTurning = nextTurning;
                    return turningVector;
                    //return nextVelocity;
                }
            }
        } else {    //Basic solution of the convex edges. Convex edge <==> the front and sideFront ray hits.
                    //If the convex edge is detected, the exactly turning vector is returned.
            if (state == State.LEFT) {
                if (sensornLeftFront && sensornFront) {    //This means the concave edge. We must turn right.
                    //if (counter > 0) {
                    //counter--;
                    Vector3d turningVector = SteeringTools.getTurningVector2(actualVelocity, false);
                    turningVector.scale(convexEdgesForceWeight*0.8);
                    return turningVector;
                    /*} else {
                        state = State.NOTHING;
                    }*/
                }
            } else if (state == State.RIGHT) {
                if (sensornRightFront && sensornFront) {    //This means the concave edge. We must turn left.
                    //if (counter > 0) {
                    //counter--;
                    Vector3d turningVector = SteeringTools.getTurningVector2(actualVelocity, true);
                    turningVector.scale(convexEdgesForceWeight*0.8);
                    return turningVector;
                    /*} else {
                        state = State.NOTHING;
                    }*/
                } 
            }
        }
       

        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Concave edges">

        //If the concave edge is detected, the exactly turning vector is returned.

        if (state == State.LEFT) {
            if (!sensornLeft && !sensornLeftFront) {//He left the wall completely, no sensor on the left side works. That is for turning around >=90°(& <=270° :-)) angles when he needs to "remember" that he should turn.
                if (SteeringManager.DEBUG) System.out.println("counter "+counter);
                if (counter > 0) {
                    counter--;
                    if (SteeringManager.DEBUG) System.out.println("Left concave edge. ");
                    Vector3d turningVector = SteeringTools.getTurningVector2(actualVelocity, true);
                    if (convexTurning) turningVector.scale(0.5);
                    turningVector.scale(concaveEdgesForceWeight*0.8);
                    return turningVector;
                } else {
                    state = State.NOTHING;
                }
            }
        } else if (state == State.RIGHT) {
            if (!sensornRight && !sensornRightFront) {//He left the wall completely, no sensor on the left side works. That is for turning around >=90°(& <=270° :-)) angles when he needs to "remember" that he should turn.            {
                if (SteeringManager.DEBUG) System.out.println("counter "+counter);
                if (counter > 0) {
                    counter--;
                    if (SteeringManager.DEBUG) System.out.println("Right concave edge. ");
                    Vector3d turningVector = SteeringTools.getTurningVector2(actualVelocity, false);
                    if (convexTurning) turningVector.scale(0.5);
                    turningVector.scale(concaveEdgesForceWeight*0.8);
                    return turningVector;
                    //nextVelocity.add(SteeringTools.getTurningVector(actualVelocity, false));
                    //nextVelocity = SteeringTools.getTurningVector(actualVelocity, false);
                } else {
                    state = State.NOTHING;
                }
            }
        }
        //</editor-fold>

        convexTurning = nextTurning;
        return nextVelocity;
    }

    @Override
    public void setProperties(SteeringProperties newProperties) {
        this.wallForce = ((WallFollowingProperties)newProperties).getWallForce();
        this.orderOfTheForce = ((WallFollowingProperties)newProperties).getOrderOfTheForce();
        this.attractiveForceWeight = ((WallFollowingProperties)newProperties).getAttractiveForceWeight();
        this.repulsiveForceWeight = ((WallFollowingProperties)newProperties).getRepulsiveForceWeight();
        this.concaveEdgesForceWeight = ((WallFollowingProperties)newProperties).getConcaveEdgesForceWeight();
        this.convexEdgesForceWeight = ((WallFollowingProperties)newProperties).getConvexEdgesForceWeight();
        this.justMySide = ((WallFollowingProperties)newProperties).isJustMySide();
        this.specialDetection = ((WallFollowingProperties)newProperties).isSpecialDetection();
        this.frontCollisions = ((WallFollowingProperties)newProperties).isFrontCollisions();
    }

    public WallFollowingProperties getProperties() {
        WallFollowingProperties properties = new WallFollowingProperties();
        properties.setWallForce(wallForce);
        properties.setOrderOfTheForce(orderOfTheForce);
        properties.setAttractiveForceWeight(attractiveForceWeight);
        properties.setRepulsiveForceWeight(repulsiveForceWeight);
        properties.setConcaveEdgesForceWeight(concaveEdgesForceWeight);
        properties.setConvexEdgesForceWeight(convexEdgesForceWeight);
        properties.setJustMySide(justMySide);
        properties.setSpecialDetection(specialDetection);
        properties.setFrontCollisions(frontCollisions);
        return properties;
    }
}
