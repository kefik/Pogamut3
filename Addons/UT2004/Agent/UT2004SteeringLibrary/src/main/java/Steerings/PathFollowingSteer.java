package Steerings;

import SocialSteeringsBeta.RefLocation;
import SteeringStuff.SteeringManager;
import SteeringStuff.SteeringTools;
import SteeringStuff.RefBoolean;
import SteeringStuff.ISteering;

import java.util.List;

import javax.vecmath.Tuple3d;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

import SteeringProperties.PathFollowingProperties;
import SteeringProperties.SteeringProperties;
import cz.cuni.amis.pogamut.base.agent.navigation.IPathFuture;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange;
import cz.cuni.amis.utils.future.FutureStatus;

/**
 * A class for providing pathFuture following steering to bots.
 *
 * @author Marki
 */
public class PathFollowingSteer implements ISteering {

    /** The bot moved by the PathFollowingSteer1; */
    private UT2004Bot botself;

    /**Steering properties: the magnitude of the repulsive force, to repulse agent from the side of the corridor.
     * Reasonable values are 0 - 1000, the default value is 200.*/
    private int repulsiveForce;
    /**Steering properties:  distance from the pathFuture.*/
    int distanceFromThePath;
    /**Steering properties:  pathFuture - list of pathFuture elements (ilocated).*/
    IPathFuture<ILocated> pathFuture;
    /**Steering properties:  path - list of pathFuture elements (ilocated).*/
    List<ILocated> path;
    /**Steering properties:  regulating force - helps the bot to keep the direction of the path.*/
    double regulatingForce;
    /**Steering properties: */
    private int projection;

    private static int NEARLY_THERE_DISTANCE = 150;
    
    /** previousLocation location. */
    Location previousLocation;
    /** nextLocation location. */
    Location nextLocation;
    /** Index of the actual ILocated in the pathFuture.*/
    int actualIndex;
    // Distance from the nextLocation in last logic.
    double lastDistanceFromNextLocation;
    /**When we get new pathFuture, the variable is true. When we set this pathFuture, we also set newPath false*/
    boolean newPath;

    private static boolean ZERO_DISTANCE = false;
    //private int nextLocationIndex;

    /**
     * 
     * @param bot
     */
    public PathFollowingSteer(UT2004Bot bot) {
        botself = bot;
        actualIndex = 0;
    }

    /**
     * Moves the bot around the given pathFuture.
     */
    @Override
    public Vector3d run(Vector3d scaledActualVelocity, RefBoolean wantsToGoFaster, RefBoolean wantsToStop, RefLocation focus) {
        Vector3d nextVelocity;
        Vector3d actualVelocity = botself.getVelocity().getVector3d();
        Location pointHeading;
        Vector3d pointHeadingToFoot;
        double distanceFromNextLocation;

        nextVelocity = new Vector3d(0,0,0);

        if (pathFuture == null) {
            if (SteeringManager.DEBUG) System.out.println("path null");
            return new Vector3d(0,0,0);
        } 

        if (!newPath && pathFuture.getStatus() != FutureStatus.FUTURE_IS_READY) {
            if (SteeringManager.DEBUG) System.out.println("path not ready");
            return new Vector3d(0,0,0);
        }

        if (newPath && pathFuture.getStatus() == FutureStatus.FUTURE_IS_READY) {
            if (!setComputedPath()) {
                if (SteeringManager.DEBUG) System.out.println("path null or zero length");
                return new Vector3d(0,0,0);
            }
        }

        if (path == null) {
            if (!setComputedPath()) {
                if (SteeringManager.DEBUG) System.out.println("path null or zero length");
                botself.getLog().warning("Received path is null / 0-sized");
                return new Vector3d(0,0,0);
            }
        }

        //----------------------------------------------------Path inited----------------------------------------------------//
        
        boolean shifted = shiftNextLocation();
        if (nextLocation == null) {
            if (SteeringManager.DEBUG) System.out.println("null next location");
            return new Vector3d(0, 0, 0);            
        }
        distanceFromNextLocation = botself.getLocation().getDistance(nextLocation);

        //----------------------------------------------------Zero distance (no steering, just following exactly the path)----------------------------------------------------//
        if (ZERO_DISTANCE) {
            if (shifted) {
                /* Jestliže jsme dosáhli dalšího vrcholu, musíme zkontrolovat, zda již nejsme nakonci.*/
                if (actualIndex + 1 >= path.size()) {
                    /* Jestliže jsme dosáhli posledního bodu, zastavíme se.*/
                    if (distanceFromNextLocation <= NEARLY_THERE_DISTANCE) {
                        wantsToStop.setValue(true);
                        if (SteeringManager.DEBUG) System.out.println("We reached the target point of the path.");
                    }
                    return nextVelocity;
                }
            }
            Location botsLoc = botself.getLocation();
            Vector3d toNextLoc = new Vector3d(nextLocation.x - botsLoc.x, nextLocation.y - botsLoc.y, nextLocation.z - botsLoc.z);
            toNextLoc.normalize();
            toNextLoc.scale(repulsiveForce);
            nextVelocity.add(toNextLoc);
            return nextVelocity;
        }
        //----------------------------------------------------Classical Path Following----------------------------------------------------//

        if (!shifted) {
            if (lastDistanceFromNextLocation < distanceFromNextLocation) {
                lastDistanceFromNextLocation = distanceFromNextLocation;
                if (SteeringManager.DEBUG) System.out.println("Jdeme na spatnou stranu!");
                /* Jestliže se odchýlil od správného směru, tak na něj začne působit síla ve směru aktuálního úseku cesty.
                 * Její velikost je přímo úměrná tomu, jak moc se odchýlil od správného směru.*/
                Vector3d rightDirectionForce = new Vector3d(nextLocation.x - previousLocation.x, nextLocation.y - previousLocation.y, 0);
                rightDirectionForce.normalize();
                if (SteeringManager.DEBUG) System.out.println("Od správného směru jsme se odchýlili o "+rightDirectionForce.angle(botself.getVelocity().getVector3d()));
                double scale = rightDirectionForce.angle(botself.getVelocity().getVector3d())/Math.PI;
                rightDirectionForce.scale(repulsiveForce*scale);
                nextVelocity.add((Tuple3d) rightDirectionForce);
                wantsToGoFaster.setValue(false);
                return nextVelocity;
            } else {
                lastDistanceFromNextLocation = distanceFromNextLocation;
            }
        } else {
            lastDistanceFromNextLocation = distanceFromNextLocation;
            if (actualIndex + 1 >= path.size()) {
                /* Jestliže jsme dosáhli posledního bodu, zastavíme se.*/
                if (distanceFromNextLocation <= NEARLY_THERE_DISTANCE) {
                    wantsToStop.setValue(true);
                    //focus.setTo(nextLocation);    //Natočíme se k poslednímu bodu.
                    if (SteeringManager.DEBUG) System.out.println("We reached the target point of the path.");
                } else {
                    Vector3d botToNextLocation = new Vector3d(nextLocation.x - botself.getLocation().x, nextLocation.y - botself.getLocation().y, nextLocation.z - botself.getLocation().z);
                    botToNextLocation.scale(actualVelocity.length() / botToNextLocation.length());
                    nextVelocity.add((Tuple3d) botToNextLocation);
                    if (SteeringManager.DEBUG) System.out.println("We must go to the target point.");
                }
                return nextVelocity;
            }
        }

        /* Jestliže se vůdce pohybuje, tak si vypočítáme svou pozici a na tu se budeme snažit dostat.*/
        ConfigChange cc = botself.getWorldView().getSingle(ConfigChange.class);
        double oneTick = cc.getVisionTime();    //Doba trvání jednoho tiku. Potřebuje ke spočítání lokace vůdce za jeden tik.

        double projectionTime = projection * oneTick;

        /*Vektor leadersShiftToNextTick představuje vektor posunutí vůdce do příštího tiku, avšak místo jeho aktuální rychlosti bereme v případě paměti tu průměrnou.*/
        Vector3d shift = new Vector3d(projectionTime * actualVelocity.x, projectionTime * actualVelocity.y, projectionTime * actualVelocity.z);

        //The bot's projection - where he will be in short time. It's bot's location + his velocity vector.
        pointHeading = new Location(botself.getLocation().x + shift.x, botself.getLocation().y + shift.y, botself.getLocation().z + shift.z);

        Vector2d start = new Vector2d(previousLocation.x, previousLocation.y);
        Vector2d end = new Vector2d(nextLocation.x, nextLocation.y);
        Vector2d pointP = new Vector2d(pointHeading.x, pointHeading.y);

        Vector2d foot = SteeringTools.getNearestPoint(start, end, pointP, false);
        
        //A vector from the point of heading to the crossing of -bx+ay+d=d. Its length is a distance of the bot's projection to the pathFuture.
        pointHeadingToFoot = new Vector3d(foot.x - pointHeading.getX(), foot.y - pointHeading.getY(), 0);

        //If the bot's projection wanders off the pathFuture, the vector footToPoint is added to his velocity - it should be sufficient, typically.
        if (pointHeadingToFoot.length() > distanceFromThePath) {
            double distOut = pointHeadingToFoot.length() - distanceFromThePath;
            //The further the projection is, the more powerful the steering force is.

            /* Co všechno má na sílu přitahující agenta dovnitř koridoru vliv?
             * Tak jednak to, jak moc se vzdálí od středu koridoru. Tedy pointHeadingToFoot.length().
             * Druhak šíře koridoru, tedy pak spíše pointHeadingToFoot.length() - distanceFromThePath.
             * Dále to, za jak dlouho se to stane. Tedy si nějak spočítat, kdy by měl překročit hranice koridoru.
             * Nakonec velikost síly repulsiveForce. Touto silou by to mělo působit v nějakém průměrném případě.
             */

            //Jak daleko od cesty se za danou dobu dostaneme. Pokud dvakrát vzdálenost od cesty či více, koefA=1; čím méně, tím je koefA menší.
            double koefA = distOut / distanceFromThePath;   
            if (koefA > 2) {
                koefA = 2;
            }
            nextVelocity.add(pointHeadingToFoot);
            nextVelocity.normalize();
            nextVelocity.scale(koefA * repulsiveForce + 30);    //Přičítáme 30, aby i těsně na hranici působila síla, která něco zmůže.
            if (SteeringManager.DEBUG) System.out.println("STAY IN CORRIDOR!");
            wantsToGoFaster.setValue(false);
        } else { //If the bot is on his pathFuture, he may continue at full throttle.
            wantsToGoFaster.setValue(true);
        }       

        Vector3d regulatingVector = new Vector3d(nextLocation.x - previousLocation.x,nextLocation.y - previousLocation.y,0);
        regulatingVector.normalize();
        regulatingVector.scale(regulatingForce);
        nextVelocity.add((Tuple3d) regulatingVector);

        return nextVelocity;
    }

    private boolean shiftNextLocation() {
        if (path == null) return false;
        if (SteeringManager.DEBUG) System.out.println("vzdalenost od nextLocation "+botself.getLocation().getDistance(nextLocation));
        
        if (botself.getLocation().getDistance(nextLocation) <= distanceFromThePath || getBehindBorder()) {  //Tady může být problém, že v jednom tiku se lze posunout nejvýše o jeden bod na cestě.
            if (actualIndex + 1 < path.size()) {
                if (SteeringManager.DEBUG) {
                    System.out.println("Novy navpoint; size " + path.size() + " actual index " + actualIndex);
                }
                ILocated help = path.get(actualIndex + 1);
                if (help == null) {
                    if (SteeringManager.DEBUG) {
                        System.out.println("NULL!!! " + pathFuture.getStatus() + pathFuture.isDone());
                    }
                } else {
                    actualIndex++;
                    previousLocation = nextLocation;
                    nextLocation = help.getLocation();
                }
            }
            return true;
        } else {
            return false;
        }
    }

    /**Returns true, if the agent overstepped the border of the nextLocation.*/
    private boolean getBehindBorder() {
        /* Vektory startA a endA (agent) určují úsečku "A" od previousLocation k lokaci agenta.*/
        Vector2d startA = new Vector2d(previousLocation.x, previousLocation.y);
        Vector2d endA = new Vector2d(botself.getLocation().x, botself.getLocation().y);
        Vector2d directionA = new Vector2d(endA.x - startA.x, endA.y - startA.y);
        
        /* Vektory startB a endB (border) určují přímku "B" prochízející nextLocation mající směr kolmý na spojnici nextLocation a previousLocation. Je to tedy jakási hranice
         * a my zkoumáme, zda se agent dostal za tuto hranici.*/
        Vector2d startB = new Vector2d(nextLocation.x, nextLocation.y);
        Vector2d normalDirectionB = new Vector2d(nextLocation.x - previousLocation.x, nextLocation.y - previousLocation.y);
        Vector2d directionB = new Vector2d(-normalDirectionB.y, normalDirectionB.x);
        //Vector2d endB = new Vector2d(startB.x + directionB.x, startB.y + directionB.y);

        Vector2d intersection = SteeringTools.getIntersection(startA, directionA, startB, directionB, SteeringTools.LineType.ABSCISSA, SteeringTools.LineType.STRAIGHT_LINE);
        /*Pokud úsečka "A" protíná přímku "B" (intersection není null), agent se dostal za hranici a je třeba posunout následující bod na cestě.*/
        if (SteeringManager.DEBUG) System.out.println("Jsme za hranicí "+(intersection != null)+" průsečík: "+intersection);
        return (intersection != null);
    }

    /**V tuto chvíli víme, že je pathFuture done a chceme nastavit path. Pokud se to nepovede, vrací false, jinak true.*/
    private boolean setComputedPath() {
        List<ILocated> myNewPath = this.pathFuture.get();
        if (myNewPath == null) {
            if (SteeringManager.DEBUG) System.out.println("null cesta ");
            return false;
        } else if (myNewPath.size() < 1) {
            if (SteeringManager.DEBUG) System.out.println("kratka cesta " + myNewPath.size());
            return false;
        }
        if (SteeringManager.DEBUG) System.out.println("path received " + myNewPath.size());
        if (differentNewPath(myNewPath)) {  //Jestliže se nová cesta neliší od staré, nemá smysl nic měnit. Jestliže se liší, pak si vybereme bod na této cestě nejbližší naší lokaci - a ten si nastavíme jako nextLocation.
            path = myNewPath;
            actualIndex = getIndexOfNearestILocated();
            nextLocation = path.get(actualIndex).getLocation();
            previousLocation = botself.getLocation();
            lastDistanceFromNextLocation = botself.getLocation().getDistance(nextLocation);
        }
        newPath = false;    //Nová cesta byla nastavena, tedy není třeba řešit žádnou newPath.
        return true;
    }

    private boolean differentNewPath(List<ILocated> myNewPath) {
        if (path == null) {
            return true;    //Je-li první cesta null, má smysl zkoušet druhou.
        } else if (myNewPath == null) {
            return false;   //V takovém případě chceme nechat původní cestu.
        } else if (path.size() != myNewPath.size()) {
            return true;    //Jsou-li jinak dlouhé, pak jsou různé.
        } else {
            for(int i = 0; i < path.size(); i++) {
                ILocated a = path.get(i);
                ILocated b = myNewPath.get(i);
                if (!a.equals(b)) {
                    return true;    //Jestliže se alespoň jedna dvojice bodů liší, jsou cesty různé.
                }
            }
            return false;   //Žádná dvojice se neliší.
        }
    }

    /**Vrátí index na cestě path, který je nejblíže aktuální lokaci agenta.*/
    private int getIndexOfNearestILocated() {
        int result = 0;
        Location botsLocation = botself.getLocation();
        double dist = botsLocation.getDistance(path.get(0).getLocation());
        for(int index = 0; index < path.size(); index++) {
            ILocated il = path.get(index);
            if (botsLocation.getDistance(il.getLocation()) < dist) {
                result = index;
                dist = botsLocation.getDistance(il.getLocation());
            }
        }
        return result;
    }

    public void setProperties(SteeringProperties newProperties) {
        this.repulsiveForce = ((PathFollowingProperties)newProperties).getRepulsiveForce();
        this.distanceFromThePath = ((PathFollowingProperties)newProperties).getDistanceFromThePath();
        this.pathFuture = ((PathFollowingProperties)newProperties).getPath();
        this.regulatingForce = ((PathFollowingProperties)newProperties).getRegulatingForce();
        this.projection = ((PathFollowingProperties)newProperties).getProjection();
        if (path != null) { //Jestliže jsme už v průběhu výpočtů nějaké cesty, chceme pokračovat s ní, dokud nebude hotová ta nová.
            newPath = true;
        } else {
            newPath = false;
        }
    }

    public PathFollowingProperties getProperties() {
        PathFollowingProperties properties = new PathFollowingProperties();
        properties.setRepulsiveForce(repulsiveForce);
        properties.setDistanceFromThePath(distanceFromThePath);
        properties.setPath(pathFuture);
        properties.setRegulatingForce(regulatingForce);
        properties.setProjection(projection);
        return properties;
    }
    
}
        //Výpis cesty.
        /*int i = 0;
        for (ILocated np : path) {
            if (SteeringManager.DEBUG) System.out.println("Cesta " + i + ": " + np.getLocation());
            i++;
        }*/