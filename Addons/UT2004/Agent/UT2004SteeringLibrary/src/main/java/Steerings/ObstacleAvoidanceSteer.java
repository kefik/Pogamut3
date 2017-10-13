package Steerings;


import SocialSteeringsBeta.RefLocation;
import SteeringStuff.SteeringManager;
import SteeringStuff.RefBoolean;
import SteeringProperties.ObstacleAvoidanceProperties;
import SteeringProperties.SteeringProperties;
import SteeringStuff.IRaysFlagChanged;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.vecmath.Vector3d;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.AutoTraceRay;
import cz.cuni.amis.pogamut.ut2004.utils.UnrealUtils;
import SteeringStuff.ISteering;
import SteeringStuff.RaycastingManager;
import SteeringStuff.SteeringRay;
import SteeringStuff.SteeringTools;
import SteeringStuff.SteeringType;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.Future;
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector2d;


/**
 * A class for providing obstacle avoiding steering via raycasting.
 *
 * @author Marki
 */
public class ObstacleAvoidanceSteer implements ISteering, IRaysFlagChanged {

    /**This steering needs UT2004Bot (to get velocity and location). */
    private UT2004Bot botself;
    /**This steering needs raycasting manager (it uses 5 rays). */
    private RaycastingManager rayManager;
    
    /**Steering properties: the magnitude of the repulsive force from the obstacles.
     * Reasonable values are 0 - 1000, the default value is 240.*/
    private int repulsiveForce;
    /**Steering properties: the order of the force. Possible values are 1 - 10, the default value is 1.
     * The curve of reactions to obstacles according to the order 1 is linear, 2 quadratic, etc.
     * It means that with higher order, the bot reacts less to dsitant obstacles and more to near obstacles.
     * But the value 1 is most usefull value. Other values can cause strange behaviour alongside walls etc.*/
    private int forceOrder;
    /**Steering properties: the switch front collisions. The default value (in basic baheviour) is false.
     * Special solution of head-on collisions (front collisions). Basic behaviour leads to rebounding from the obstacles
     * (when the bot aims to the obstacle head-on, he turns nearly 180° round just in front of the obstacle).
     * When this parameter is on, bot turns and continues alongside the side of the obstacle.*/
    private boolean frontCollisions;
    /**Steering properties: the switch tree collisions. The default value (in basic baheviour) is false.
     * Special solution of collisions with trees and other narrow obstacles (so narrow, that just one of the rays will hit them).
     * In basic behaviour (when the switch is off), when the bot aims to the tree that just the front side rays hits, he avoids the tree from the worse side.
     * When the switch is on, he avoids the obstacle from the right (nearer) side.*/
    private boolean treeCollisions;


    private static double FRONT_RAY_WEIGHT = 1;
    private static double SIDE_FRONT_RAY_WEIGHT = 0.8;
    private static double SIDE_RAY_WEIGHT = 0.5;


    /** How long is the vector of walking velocity. Used for rescaling normal vector, when hitting an obstacle with a ray. */
    //protected static final double repulsiveForce=240;

    /**When rays are ready (AutoTraceRays are set), the raysReady is true.*/
    private boolean raysReady;
    /**Map with Future AutoTraceRays.*/
    private HashMap<String, Future<AutoTraceRay>> myFutureRays;
    
    // Constants for rays' ids
    protected static final String LEFTFRONT = "oleftfront";
    protected static final String LEFT = "oleft";
    protected static final String RIGHTFRONT = "orightfront";
    protected static final String RIGHT = "oright";
    protected static final String FRONT = "ofront";
    //protected static final String LEFTFRONT2 = "oleftfront2";   //delete
    //protected static final String RIGHTFRONT2 = "orightfront2";   //delete

    // Whether sensors signalize the collision.
    boolean sensorLeft = false;
    boolean sensorRight = false;
    boolean sensorLeftFront = false;
    boolean sensorRightFront = false;
    boolean sensorFront = false;
    //boolean sensorLeftFront2 = false;   //delete
    //boolean sensorRightFront2 = false;   //delete

    /** Bot's rays. */
    AutoTraceRay left, right, leftfront, rightfront, front;
    //AutoTraceRay leftfront2, rightfront2;   //delete
    
    // Lengths of bot's rays. Short rays are supposed for side rays, long rays for front rays.
    final int shortRayLength = (int) (UnrealUtils.CHARACTER_COLLISION_RADIUS * 8); //5
    final int longRayLength = (int) (UnrealUtils.CHARACTER_COLLISION_RADIUS * 25);  //15
    //final int longRayLength2 = (int) (UnrealUtils.CHARACTER_COLLISION_RADIUS * 15);  //   //delete

    /**Just random. Used in frontCollisions - whether turn left or right if it isn't clear.*/
    Random random = new Random();
    
    /**
     * 
     * @param bot - instance (UT2004Bot) of the steered bot.
     * @param rayManager - RaycastingManager of the steered bot.
     */
    public ObstacleAvoidanceSteer(UT2004Bot bot, RaycastingManager rayManager) {
        botself = bot;
        this.rayManager = rayManager;
        prepareRays();
    }

    //Prepares rays for the bot, removes any old rays and sets new ones.
    private void prepareRays() {
        //Five rays are created.
        LinkedList<SteeringRay> rayList = new LinkedList<SteeringRay>();
        rayList.add(new SteeringRay(LEFT, new Vector3d(0, -1, 0), shortRayLength));
        rayList.add(new SteeringRay(LEFTFRONT, new Vector3d(Math.sqrt(3) * 2, -1, 0), longRayLength));
        rayList.add(new SteeringRay(RIGHTFRONT, new Vector3d(Math.sqrt(3) * 2, 1, 0), longRayLength));
        rayList.add(new SteeringRay(RIGHT, new Vector3d(0, 1, 0), shortRayLength));
        rayList.add(new SteeringRay(FRONT, new Vector3d(1, 0, 0), longRayLength));
        //rayList.add(new SteeringRay(LEFTFRONT2, new Vector3d(Math.sqrt(3), -1, 0), longRayLength2));   //delete
        //rayList.add(new SteeringRay(RIGHTFRONT2, new Vector3d(Math.sqrt(3), 1, 0), longRayLength2));   //delete
        rayManager.addRays(SteeringType.OBSTACLE_AVOIDANCE, rayList, this);
        raysReady = false;
    }

    public void flagRaysChanged() {
        myFutureRays = rayManager.getMyFutureRays(SteeringType.OBSTACLE_AVOIDANCE);
        raysReady = false;
        listenToRays();
    }

    /**When the rays are ready, we can set the AutoTraceRays.*/
    private void listenToRays() {
        for(String rayId : myFutureRays.keySet()) {
            Future<AutoTraceRay> fr = myFutureRays.get(rayId);
            if (fr.isDone()) {
                try {
                    if (rayId.equals(LEFTFRONT)) {
                        leftfront = fr.get();
                    } else if (rayId.equals(RIGHTFRONT)) {
                        rightfront = fr.get();
                    } else if (rayId.equals(LEFT)) {
                        left = fr.get();
                    } else if (rayId.equals(RIGHT)) {
                        right = fr.get();
                    } else if (rayId.equals(FRONT)) {
                        front = fr.get();
                    }/* else if (rayId.equals(LEFTFRONT2)) {   //delete
                        leftfront2 = fr.get();
                    } else if (rayId.equals(RIGHTFRONT2)) {   //delete
                        rightfront2 = fr.get();
                    }*/
                    raysReady = true;
                } catch (InterruptedException ex) {
                    Logger.getLogger(ObstacleAvoidanceSteer.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ExecutionException ex) {
                    Logger.getLogger(ObstacleAvoidanceSteer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }        
    }

    
    /** When called, the bot starts steering, when possible, he walks straight, he steers away from obstacles though, when necessary. */
    @Override
    public Vector3d run(Vector3d scaledActualVelocity, RefBoolean wantsToGoFaster, RefBoolean wantsToStop, RefLocation focus) {

        wantsToGoFaster.setValue(false);
        Vector3d nextVelocity = new Vector3d(0,0,0);
        
        if (!raysReady) {
            listenToRays();
            return nextVelocity;
        }        

        //The bot's velocity is received.
        Vector3d actualVelocity = botself.getVelocity().getVector3d();
        Vector3d originalVelocity = botself.getVelocity().getVector3d();
        originalVelocity.normalize();
        originalVelocity.scale(0);
        
        //The bot checks his sensors
        if (leftfront != null)
            sensorLeftFront = leftfront.isResult();
        if (rightfront != null)
            sensorRightFront = rightfront.isResult();
        if (left != null)
            sensorLeft = left.isResult();
        if (right != null)
            sensorRight = right.isResult();
        if (front != null)
            sensorFront = front.isResult();
        /*if (leftfront2 != null)
            sensorLeftFront2 = leftfront2.isResult();   //delete
        if (rightfront2 != null)
            sensorRightFront2 = rightfront2.isResult();   //delete
        */
        /*
         * Whether any sensor is signalling or not. Used mostly when determining, whether to return to normal speed. After hitting a wall,
         * the bot's velocity can be very low, but he can speed up, obviously. When no rays are conflicting, the bot returns to his normal walking speed.
         */
        boolean sensor = sensorFront || sensorLeft || sensorLeftFront || sensorRight || sensorRightFront;
        //sensor = sensor || sensorLeftFront || sensorRightFront;   //delete
        
        /*
         * Normal vector is multiplied by this when applied to the bot. The closer the bot is to the wall (not perpendicularily, the important
         * thing is the distance from him to the point of conflict with a ray), the bigger this multiplier is, i.e. the stronger the repulsive force is.
         * It is counted as rl-bdw*2/rl, where rl is a length of ray (short or long one), bdw is bot's distance from wall. It is chosen this way
         * because when the ray is half in the wall, the normal is applied with it's "natural" force - when bot's further, the force is weaker.
         * Multiplier - because the closer we are to the wall, the stronger the repulsive power is.
         */
        double multiplier;
        
        // Normal to the point, where bot's ray intersect with wall.
        Vector3d normal;

        // A vector from the point where one of bot's rays crosses the wall to the bot.
        Vector3d botToHitLocation;
        
        /*
         *All sensors are checked respectively. When any of them signalizes a hit, a vector is added to nextvelocity.
         *The vector added is derived from normal vector of the wall, such that when the ray is half in the wall (see multiplier),
         *the steering power is as powerful as walking power at maximal strength.
         */
        if (sensorLeft) {
            botToHitLocation = new Vector3d(left.getHitLocation().x - botself.getLocation().x, left.getHitLocation().y - botself.getLocation().y, 0);
            normal = left.getHitNormal();
            multiplier = Math.pow((shortRayLength - botToHitLocation.length()) * 2f / shortRayLength, forceOrder); //How big part of the shortRay is inside the obstacle = (shortRayLength - botToHitLocation.length()) / shortRayLength.
            normal.scale(SIDE_RAY_WEIGHT * repulsiveForce * multiplier);
            nextVelocity.add((Tuple3d) normal);
        }
        if (sensorRight) {
            botToHitLocation = new Vector3d(right.getHitLocation().x - botself.getLocation().x, right.getHitLocation().y - botself.getLocation().y, 0);
            normal = right.getHitNormal();
            multiplier = Math.pow((shortRayLength - botToHitLocation.length()) * 2f / shortRayLength, forceOrder);
            normal.scale(SIDE_RAY_WEIGHT * repulsiveForce * multiplier);
            nextVelocity.add((Tuple3d) normal);
        }
        if (sensorLeftFront) {
            botToHitLocation = new Vector3d(leftfront.getHitLocation().x - botself.getLocation().x, leftfront.getHitLocation().y - botself.getLocation().y, 0);
            multiplier = Math.pow((longRayLength - botToHitLocation.length()) * 2f / longRayLength, forceOrder);
            normal = leftfront.getHitNormal();
            if (treeCollisions && (!sensorRightFront && SteeringTools.pointIsLeftFromTheVector(actualVelocity, normal) ) ) {
                normal = computeTreeCollisionVector(normal);
            }
            if (frontCollisions && !sensorFront) {
                normal = computeFrontCollisionVector(normal);
            }
            normal.scale(SIDE_FRONT_RAY_WEIGHT * repulsiveForce * multiplier);
            nextVelocity.add((Tuple3d) normal);
        }
        if (sensorRightFront) {
            botToHitLocation = new Vector3d(rightfront.getHitLocation().x - botself.getLocation().x, rightfront.getHitLocation().y - botself.getLocation().y, 0);
            multiplier = Math.pow((longRayLength - botToHitLocation.length()) * 2f / longRayLength, forceOrder);
            normal = rightfront.getHitNormal();
            if (treeCollisions && (!sensorLeftFront && !SteeringTools.pointIsLeftFromTheVector(actualVelocity, normal) ) ) {
                normal = computeTreeCollisionVector(normal);
            }
            if (frontCollisions && !sensorFront) {
                normal = computeFrontCollisionVector(normal);
            }
            normal.scale(SIDE_FRONT_RAY_WEIGHT * repulsiveForce * multiplier);
            nextVelocity.add((Tuple3d) normal);
        }
        if (sensorFront) {
            botToHitLocation = new Vector3d(front.getHitLocation().x - botself.getLocation().x, front.getHitLocation().y - botself.getLocation().y, 0);
            multiplier = Math.pow((longRayLength - botToHitLocation.length()) * 2f / longRayLength, forceOrder);
            normal = front.getHitNormal();
            if (frontCollisions) {
                normal = computeFrontCollisionVector(normal);                
            }
            normal.scale(FRONT_RAY_WEIGHT * repulsiveForce * multiplier);
            nextVelocity.add((Tuple3d) normal);
        }
        /*if (sensorLeftFront2) {
            botToHitLocation = new Vector3d(leftfront2.getHitLocation().x - botself.getLocation().x, leftfront2.getHitLocation().y - botself.getLocation().y, 0);
            multiplier = Math.pow((longRayLength2 - botToHitLocation.length()) * 2f / longRayLength2, forceOrder);
            normal = leftfront2.getHitNormal();
            if (treeCollisions && (!sensorRightFront2 && SteeringTools.pointIsLeftFromTheVector(actualVelocity, normal) ) ) {
                normal = computeTreeCollisionVector(normal);
            }
            if (frontCollisions && !sensorFront) {
                normal = computeFrontCollisionVector(normal);
            }
            normal.scale(SIDE_FRONT_RAY_WEIGHT * repulsiveForce * multiplier);
            nextVelocity.add((Tuple3d) normal);
        }
        if (sensorRightFront2) {
            botToHitLocation = new Vector3d(rightfront2.getHitLocation().x - botself.getLocation().x, rightfront2.getHitLocation().y - botself.getLocation().y, 0);
            multiplier = Math.pow((longRayLength2 - botToHitLocation.length()) * 2f / longRayLength2, forceOrder);
            normal = rightfront2.getHitNormal();
            if (treeCollisions && (!sensorLeftFront2 && !SteeringTools.pointIsLeftFromTheVector(actualVelocity, normal) ) ) {
                normal = computeTreeCollisionVector(normal);
            }
            if (frontCollisions && !sensorFront) {
                normal = computeFrontCollisionVector(normal);
            }
            normal.scale(SIDE_FRONT_RAY_WEIGHT * repulsiveForce * multiplier);
            nextVelocity.add((Tuple3d) normal);
        }*/

        //If nothing is signalling, the bot can return to its normal speed..
        if (!sensor) {
            wantsToGoFaster.setValue(true);
        } else {
            wantsToGoFaster.setValue(false);
        }
        
        return nextVelocity;
    }

    /**If it's really the front collision, the "mirror vector" is returned. Otherwise the unchanged parameter normal is returned.*/
    private Vector3d computeTreeCollisionVector(Vector3d normal) {        
        Vector3d av = botself.getVelocity().getVector3d();
        /* Jestliže signalizuje pravý přední paprsek a ne levý přední -
         * a navíc jde normálová síla v místě kolize stejným směrem jako jde paprsek, tedy doleva ==> pak by se neměla přičítat tato normála, ale spíše síla na durhou stranu.
         * Značí to situaci, kdy jsme narazili na úzkou překážku (strom) a levý přední paprsek prošel levým krajem překážky.
         * Bez tohoto ošetření by nás to stočilo doleva, což nechceme.*/
        /* Pro pravou stranu je to naopak. *//* Jestliže signalizuje levý přední paprsek a ne pravý přední -
         * a navíc jde normálová síla v místě kolize stejným směrem jako jde paprsek, tedy doleva ==> pak by se neměla přičítat tato normála, ale spíše síla na durhou stranu.
         * Značí to situaci, kdy jsme narazili na úzkou překážku (strom) a levý přední paprsek prošel levým krajem překážky.
         * Bez tohoto ošetření by nás to stočilo doleva, což nechceme.*/

        Vector2d start = new Vector2d(botself.getLocation().x, botself.getLocation().y);
        Vector2d end = new Vector2d(start.x-av.x, start.y-av.y);
        Vector2d point = new Vector2d(start.x + normal.x, start.y + normal.y);
        Vector2d pata = SteeringTools.getNearestPoint(start, end, point, false);
        Vector2d pointToPata = new Vector2d(pata.x - point.x, pata.y - point.y);
        pointToPata.scale(2);
        Vector2d mirrorPoint = new Vector2d(point.x + pointToPata.x, point.y + pointToPata.y);
        
        Vector3d result = new Vector3d(mirrorPoint.x - start.x, mirrorPoint.y - start.y, 0);
        
        if (SteeringManager.DEBUG) {
            System.out.println("Obstacle avoidance tree collision. " + result.length());
        }
        return result;
    }

    /**If it's really the front collision, the "mirror vector" is returned. Otherwise the unchanged parameter normal is returned.*/
    private Vector3d computeFrontCollisionVector(Vector3d normal) {
        Vector3d av = botself.getVelocity().getVector3d();
        Vector3d result = new Vector3d(normal.x, normal.y, 0);
        Vector3d negativeActual = new Vector3d(-av.x, -av.y, 0);

        if (SteeringManager.DEBUG) System.out.println("Angle "+SteeringTools.radiansToDegrees(normal.angle(negativeActual)));
        if (result.angle(negativeActual) <= Math.PI/2) {
            boolean turnLeft;
            if (result.angle(negativeActual) == 0) {
                turnLeft = random.nextBoolean();
            } else {
                turnLeft = SteeringTools.pointIsLeftFromTheVector(av, result);
            }
            Vector3d turn = SteeringTools.getTurningVector2(av, turnLeft);  //Tady se původně používal getTurningVector1.
            turn.normalize();
            turn.scale(0.5);    //Aby neměl rotační vektor tak velký vliv.
            result.add(turn);
            result.normalize();
            if (SteeringManager.DEBUG) System.out.println("Obstacle avoidance front collision: turn left "+turnLeft);
        }
        return result;
    }
      
    @Override
    public void setProperties(SteeringProperties newProperties) {
        this.repulsiveForce = ((ObstacleAvoidanceProperties)newProperties).getRepulsiveForce();
        this.forceOrder = ((ObstacleAvoidanceProperties)newProperties).getForceOrder();
        this.frontCollisions = ((ObstacleAvoidanceProperties)newProperties).isFrontCollisions();
        this.treeCollisions = ((ObstacleAvoidanceProperties)newProperties).isTreeCollisions();
    }
    
    public ObstacleAvoidanceProperties getProperties() {
        ObstacleAvoidanceProperties properties = new ObstacleAvoidanceProperties();
        properties.setRepulsiveForce(repulsiveForce);
        properties.setForceOrder(forceOrder);
        properties.setFrontCollisions(frontCollisions);
        properties.setTreeCollisions(treeCollisions);
        return properties;
    }
}