package Steerings;

import SocialSteeringsBeta.RefLocation;
import SteeringStuff.SteeringManager;
import SteeringStuff.SteeringTools;
import SteeringStuff.RefBoolean;
import SteeringProperties.PeopleAvoidanceProperties;
import SteeringProperties.SteeringProperties;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import javax.vecmath.Vector3d;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Self;
import SteeringStuff.ISteering;
import java.util.Collection;
import java.util.Random;
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector2d;

/**
 * A class for providing people avoiding steering.
 *
 * @author Marki
 */
public class PeopleAvoidanceSteer implements ISteering {

    /**This steering needs UT2004Bot (to get velocity, location, bot's id and config change). */
    private UT2004Bot botself;
    
    /**Steering properties: force from other people. Default value is 200. */
    private int repulsiveForce;
    /**Steering properties: the distance from other people. Default value is 300. */
    private int distanceFromOtherPeople;
    /**Steering properties: go round partner. Default value is false. */
    private boolean circumvention;
    /** ISteering properties: length of the vision - in number of ticks. Default value is 16. */
    private double projection;
    private boolean deceleration = true;
    private boolean acceleration = false;

    private static int TICK_PARTS = 5;

    Random random = new Random();
    
    /**
     * @param bot Instance of the steered bot.
     */
    public PeopleAvoidanceSteer(UT2004Bot bot) {
        botself = bot;
    }
    
    /** When called, the bot starts steering, when possible, he walks straight, otherwise he steers away from othe people to avoid collision with them. */
    public Vector3d run(Vector3d scaledActualVelocity, RefBoolean wantsToGoFaster, RefBoolean wantsToStop, RefLocation focus)
    {
        //The bot's velocity is received.
        Vector3d actualVelocity = botself.getVelocity().getVector3d();
        //Next velocity. If no sensor is active, it will stay as the current velocity.
        Vector3d nextVelocity = new Vector3d(0,0,0);
        
        // A vector from other bot to our bot.
        Vector3d otherBotToFollower;

        //My UnrealId.
        UnrealId myId = botself.getWorldView().getSingle(Self.class).getId();

        wantsToGoFaster.setValue(true);

        //Let's deal with other bots too. If they're too close to our bot, they repulse him with their forces.
        Collection<Player> col = botself.getWorldView().getAllVisible(Player.class).values();

        for (Player p : col) {
            if (p.getId() != myId) {               
                if (deceleration || acceleration) {
                    Vector3d notPushVector = notPushPartner(scaledActualVelocity, p, wantsToGoFaster);
                    nextVelocity.add((Tuple3d) notPushVector);
                } else if (circumvention) {
                    Vector3d goRoundVector = goRoundPartner(p);
                    nextVelocity.add((Tuple3d) goRoundVector);
                }
                if(p.getLocation().getDistance(botself.getLocation()) < distanceFromOtherPeople) {
                    Vector3d botToPlayer = new Vector3d(p.getLocation().x - botself.getLocation().x, p.getLocation().y - botself.getLocation().y, 0);
                    if (actualVelocity.angle(botToPlayer) < Math.PI/2) {    //Zajímá nás teď jen tehdy je-li vedle nás a více před námi.
                        otherBotToFollower = new Vector3d(botself.getLocation().x - p.getLocation().x, botself.getLocation().y - p.getLocation().y, botself.getLocation().z - p.getLocation().z);
                        otherBotToFollower.normalize();
                        otherBotToFollower.scale(repulsiveForce * (distanceFromOtherPeople - botself.getLocation().getDistance(p.getLocation())) / distanceFromOtherPeople);
                        if (SteeringManager.DEBUG) System.out.println("Bot "+p.getName()+" je moc blizko ==> odpudivá síla "+otherBotToFollower.length());
                        wantsToGoFaster.setValue(false);
                        nextVelocity.add((Tuple3d) otherBotToFollower);
                    }
                }
            }
        }
        return nextVelocity;
    }

    private Vector3d goRoundPartner(Player player) {        
        Vector3d result = new Vector3d(0,0,0);
        Location myActualLocation = botself.getLocation();
        Vector3d myVelocity = botself.getVelocity().getVector3d();
        Location hisActualLocation = player.getLocation();
        Vector3d hisVelocity = player.getVelocity().getVector3d();
        Location myNextLocation = null;
        Location hisNextLocation = null;
        double collisionTime = -1;
        for(int t=0;t <= projection*TICK_PARTS;t++){
            double time = ((double)t)/TICK_PARTS;
            myNextLocation = getLocationAfterTime(myActualLocation, myVelocity, time);
            hisNextLocation = getLocationAfterTime(hisActualLocation, hisVelocity, time);
            if (myNextLocation.getDistance(hisNextLocation) <= distanceFromOtherPeople) {
                collisionTime = time;
                break;
            }
        }
        if (collisionTime != -1) {  //Za dobu collisionTime bychom se přiblížili příliš blízko.
            double ourNextDistance = myNextLocation.getDistance(hisNextLocation);
            Vector3d myNextLocationToHis = new Vector3d(hisNextLocation.x - myNextLocation.x, hisNextLocation.y - myNextLocation.y, hisNextLocation.z - myNextLocation.z);
            double ourNextAngle = myNextLocationToHis.angle(myVelocity);

            Vector3d turningVector;
            double koefA, koefB;
            boolean turnLeft;
            
            /*Teď podle toho, zda bude v danou chvíli druhý bot od nás napravo či nalevo, zatočíme na danou stranu.
             A podle toho, jak dalekood sebe budeme a za jak dlouho to je, bude síla velká.*/
            if (ourNextAngle == 0) {
                turnLeft = random.nextBoolean();
                if (SteeringManager.DEBUG) {
                    System.out.println("Partner exactly front collision. "+turnLeft);
                }
                koefA = 1;
                koefB = getKoefB(collisionTime);
            } else {
                koefA = getKoefA(ourNextAngle, ourNextDistance);
                koefB = getKoefB(collisionTime);
                turnLeft = !SteeringTools.pointIsLeftFromTheVector(myVelocity, myNextLocationToHis);
                if (SteeringManager.DEBUG) System.out.println("Partner nearly front collision. " + turnLeft);
                if (SteeringManager.DEBUG) System.out.println("Distance " + ourNextDistance + " koefA " + koefA + " koefB " + koefB);
            }
            turningVector = SteeringTools.getTurningVector2(botself.getVelocity().getVector3d(), turnLeft);
            turningVector.normalize();
            turningVector.scale(2*repulsiveForce * koefA * koefB);
            if (SteeringManager.DEBUG) System.out.println("Turning vector " + turningVector.length());
            result.add(turningVector);
        }        
        return result;
    }

    /**Vrací maximum z koefA1 a koefA2, které zkoumají, jak moc agent bude mířit na toho druhého a jak moc budou od sebe v danou chvíli daleko.*/
    private double getKoefA(double angle, double distance) {
        return Math.max(getKoefA1(angle), getKoefA2(distance));
    }

    /*Pokud je vzdálenost právě distanceFromOthers či více, měl by být koefA 0.
     * Jinak čím je vzdálenost menší, tím je síla větší.*/
    private double getKoefA1(double angle) {
        return Math.max(0, (Math.PI/2 - angle) / Math.PI/2);
    }

    /*Pokud je vzdálenost právě distanceFromOthers či více, měl by být koefA 0.
     * Jinak čím je vzdálenost menší, tím je síla větší.*/
    private double getKoefA2(double distance) {
        return Math.max(0, (distanceFromOtherPeople - distance) / distanceFromOtherPeople);
    }

    /*Pokud je konfliktní pozice vzdálená jeden tik, měl by být koeficient 1.
    Pokud je vzdálená méně než jeden tik, výsledek by měl být vyšší. Pokud více, výsledek by měl být něco mezi 0 a 1.*/
    private double getKoefB(double ticks) {
        double koef;
        if (projection > 0) {
            koef = (projection - ticks)/(projection - 1);
        } else {
            koef = 1;
        }
        return koef;
    }
    
    private Location getLocationAfterTime(Location start, Vector3d velocity, double time) {
        return new Location(start.x + time*velocity.x, start.y + time*velocity.y, start.z);
    }

    /**
     */
    private Vector3d notPushPartner(Vector3d botsVelocity, Player player, RefBoolean wantsToGoFaster) {
        Vector3d myVelo = botself.getVelocity().getVector3d();
        Vector3d hisVelo = player.getVelocity().getVector3d();
        Location myLoc = botself.getLocation();
        Location hisLoc = player.getLocation();
        Vector2d vInterSec = SteeringTools.getIntersectionOld(new Vector2d(myLoc.x, myLoc.y), new Vector2d(myVelo.x, myVelo.y), new Vector2d(hisLoc.x, hisLoc.y), new Vector2d(hisVelo.x, hisVelo.y));
        Vector3d result = new Vector3d();
        boolean noForce = true;
        if (vInterSec != null) {    //Zajímají nás jen ty případy, kdy se mají naše budoucí dráhy křížit.
            Location locInterSec = new Location(vInterSec.x, vInterSec.y, myLoc.z);
            double myDist = locInterSec.getDistance(myLoc);
            double hisDist = locInterSec.getDistance(hisLoc);
            double myTime = myDist / myVelo.length();
            double hisTime = hisDist / hisVelo.length();
            double minTime = Math.min(myTime, hisTime);
            Location myNewLoc = new Location(myLoc.x + myVelo.x * minTime, myLoc.y + myVelo.y * minTime, myLoc.z);
            Location hisNewLoc = new Location(hisLoc.x + hisVelo.x * minTime, hisLoc.y + hisVelo.y * minTime, hisLoc.z);
            double newLocsDiff = myNewLoc.getDistance(hisNewLoc);

            //Podle visionInTicks spočítáme okruh, který nás zajímá - a vše co bude dál (než far_distance), budeme igonorvat.
            double far_distance = projection*myVelo.length();
            double far_distance2 = Math.max(far_distance,distanceFromOtherPeople+1); //Aby far_distance2 bylo vyšší než distanceFromOtherPeople.
            if (myDist <= far_distance2 && newLocsDiff < 2*distanceFromOtherPeople) { //Zajímá nás jen to, kdy není průsečík moc daleko a když bychom se na něm měli setkat "společně", tedy že lokace, na které dorazíme za minTime, budou méně vzdálené než je povolená vzdálenost.
                double koefA = (far_distance2 - myDist) / (far_distance2 - distanceFromOtherPeople);
                koefA = Math.min(koefA, 1);
                double koefB = ( 2*distanceFromOtherPeople - newLocsDiff) / (2*distanceFromOtherPeople);
                if (myTime < hisTime && acceleration) {
                    if (SteeringManager.DEBUG) System.out.println("We speed up: koefA "+koefA+" koefB "+koefB);
                    noForce = false;
                    result = getBiggerVelocity(botsVelocity, 3*koefA*koefB, false, wantsToGoFaster);
                } else if (myTime > hisTime && deceleration) {
                    if (SteeringManager.DEBUG) System.out.println("We slow down: koefA "+koefA+" koefB "+koefB);
                    noForce = false;
                    result = getBiggerVelocity(botsVelocity, koefA*koefB, true, wantsToGoFaster);
                } else if (myTime == hisTime) {
                    boolean slowDown = random.nextBoolean();
                    if (SteeringManager.DEBUG) System.out.println("Random --> We slow down "+slowDown+" koefA "+koefA+" koefB "+koefB);
                    noForce = false;
                    result = getBiggerVelocity(botsVelocity, 5, slowDown, wantsToGoFaster);
                }
            }
        }
        if (noForce && circumvention) {   //Pokud žádný z nich nezpomaluje ani nezrychluje, může mít smysl, aby se obešli.
            result = goRoundPartner(player);
        }
        if (SteeringManager.DEBUG) System.out.println("pushing force: " + result.length());
        return result;
    }

    private Vector3d getBiggerVelocity(Vector3d velocity, double scale, boolean negate, RefBoolean wantsToGoFaster) {
        Vector3d result = new Vector3d(velocity.x, velocity.y, velocity.z);
        if (negate) {
            result.negate();
            wantsToGoFaster.setValue(false);
        }
        result.scale(scale);
        return result;
    }

    public void setProperties(SteeringProperties newProperties) {
        this.repulsiveForce = ((PeopleAvoidanceProperties)newProperties).getRepulsiveForce();
        this.distanceFromOtherPeople = ((PeopleAvoidanceProperties)newProperties).getDistanceFromOtherPeople();
        this.circumvention = ((PeopleAvoidanceProperties)newProperties).isCircumvention();
        this.deceleration = ((PeopleAvoidanceProperties)newProperties).isDeceleration();
        this.acceleration = ((PeopleAvoidanceProperties)newProperties).isAcceleration();
        this.projection = ((PeopleAvoidanceProperties)newProperties).getProjection();
    }

    public PeopleAvoidanceProperties getProperties() {
        PeopleAvoidanceProperties properties = new PeopleAvoidanceProperties();
        properties.setRepulsiveForce(repulsiveForce);
        properties.setDistanceFromOtherPeople(distanceFromOtherPeople);
        properties.setCircumvention(circumvention);
        properties.setDeceleration(deceleration);
        properties.setAcceleration(acceleration);
        properties.setProjection(projection);
        return properties;
    }
}