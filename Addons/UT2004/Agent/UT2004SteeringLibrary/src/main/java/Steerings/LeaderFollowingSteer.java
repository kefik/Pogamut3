package Steerings;

import SocialSteeringsBeta.RefLocation;
import SteeringStuff.SteeringManager;
import SteeringStuff.SteeringTools;
import SteeringStuff.RefBoolean;
import SteeringProperties.LeaderFollowingProperties;
import SteeringProperties.LeaderFollowingProperties.LFtype;
import SteeringProperties.SteeringProperties;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import SteeringStuff.ISteering;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Random;
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;


/**
 * Provides following steering to bots. They will follow the player named "Leader" if they see him. If not, they will walk in small circle,
 * so they can see the leader if he comes into their field of vision.
 *
 * @author Marki
 */
public class LeaderFollowingSteer implements ISteering {

    /** This steering needs botself. */
    private UT2004Bot botself;

    private LeaderFollowingProperties p;

    /** ISteering properties: name of the leader.*/
    private int leaderForce;
    /** ISteering properties: name of the leader.*/
    private String leaderName;
    /** ISteering properties:  distance from the leader. What it is to be too close to the leader. When less than this, the bot is too close.*/
    private int distanceFromTheLeader;
    /** ISteering properties: the distance from ideal position, in which the force has the length leaderForce.*/
    private int forceDistance;
    /** ISteering properties: the type 1 means the basic type, the 2 is the formation type with angles.*/
    private LFtype myLFtype;
    /** ISteering properties: whether the formation following has the memory for the velocities (leadersVelocities).*/
    private boolean deceleration;
    /** ISteering properties: angle from the leader.*/
    private double angleFromTheLeader;
    /** ISteering properties: whether the formation following has the memory for the velocities (leadersVelocities).*/
    private boolean velocityMemory;
    /** ISteering properties: How big is the memory for velocities (leadersVelocities).*/
    private int sizeOfMemory;
    /** ISteering properties: whether the formation following has the memory for the velocities (leadersVelocities).*/
    private boolean circumvention;

    private static int NEARLY_THERE_DISTANCE = 80;
    private double innerDistanceFromTheLeader = Math.max(150,distanceFromTheLeader/2);
    //private static int NOT_FAR_FROM_POSITION = 100;
    
    private LinkedList<Vector3d> leadersVelocities;

    private Player leader;
    private Vector3d leadersVelocity;
    private Location leadersLocation;

    private Location lastLeadersLoc;
    private Vector3d lastLeadersVelocity;
    private Vector3d lastLeadersNonZeroVelocity;

    Random random = new Random();

    /**
     *
     * @param bot
     */
    public LeaderFollowingSteer(UT2004Bot bot)
    {
        botself=bot;
        leadersVelocities = new LinkedList<Vector3d>();
    }

    /**
     * Steers the bot.
     */
    @Override
    public Vector3d run(Vector3d scaledActualVelocity, RefBoolean wantsToGoFaster, RefBoolean wantsToStop, RefLocation focus) {
        // Supposed velocity in the next tick of logic, after applying various steering forces to the bot.
        Vector3d nextVelocity;
        //The bot's velocity is received.
        Vector3d actualVelocity = botself.getVelocity().getVector3d();
        //The velocity of the bot is got and it's put to x,y,z for future use.
        nextVelocity = new Vector3d(0,0,0);
        
        // If leader is null or if the leader can't be seen, the bot spins around and tries to get the leader.
        if (leader == null ) {
            leader = getLeader();
            if (leader == null) {
                Vector3d turningVector = new Vector3d(actualVelocity.y, -actualVelocity.x, 0);   //Turns 45° left.
                turningVector.scale(1/(Math.sqrt(2)));
                Vector3d negativeVector = new Vector3d(-actualVelocity.x,-actualVelocity.y,0);
                negativeVector.scale(1-1/Math.sqrt(2));
                turningVector.add((Tuple3d)negativeVector);
                return turningVector;
            } else {
                lastLeadersLoc = leader.getLocation();
                lastLeadersVelocity = leader.getVelocity().getVector3d();
                lastLeadersNonZeroVelocity = lastLeadersVelocity;
            }
        }
        
        if (!leader.isVisible()) {
            if (botself.getLocation().getDistance(lastLeadersLoc) < NEARLY_THERE_DISTANCE) {
                Vector3d turningVector = new Vector3d(actualVelocity.y, -actualVelocity.x, 0);   //Turns 45° left.
                turningVector.scale(1 / (Math.sqrt(2)));
                Vector3d negativeVector = new Vector3d(-actualVelocity.x, -actualVelocity.y, 0);
                negativeVector.scale(1 - 1 / Math.sqrt(2));
                turningVector.add((Tuple3d) negativeVector);
                return turningVector;
            }
            leadersLocation = lastLeadersLoc;
            leadersVelocity = lastLeadersVelocity;
        } else {
            leadersLocation = leader.getLocation();
            leadersVelocity = leader.getVelocity().getVector3d();
        }
        
        Vector3d botToLeader = new Vector3d(leadersLocation.getX() - botself.getLocation().getX(), leadersLocation.getY() - botself.getLocation().getY(), 0);
        lastLeadersLoc = leader.getLocation();
        lastLeadersVelocity = leader.getVelocity().getVector3d();
        if (lastLeadersVelocity.length() != 0) lastLeadersNonZeroVelocity = lastLeadersVelocity;
        
        if (velocityMemory) {
            if (leadersVelocities.size() > sizeOfMemory)
                leadersVelocities.removeFirst();
            leadersVelocities.addLast(leadersVelocity);
        }

        if (SteeringManager.DEBUG) System.out.println("leaders velocity " + leadersVelocity.length());

        if (myLFtype.equals(LFtype.BASIC)) {  //Základní typ leader followingu.            
            if (SteeringManager.DEBUG) System.out.println("Distance should be "+distanceFromTheLeader+" and is "+botToLeader.length());
            if (leadersVelocity.length() == 0 && Math.abs(botToLeader.length() - distanceFromTheLeader) <= NEARLY_THERE_DISTANCE ) {
            /* První možnost - vůdce stojí a my jsme ve vhodné vzdálenosti od něj. Pak má smysl, abychom se také zastavili.
             * Tedy vracíme vektor opačný k předpokládané následující velocity nebýt našeho steeringu. Nebude-li jiných steeringů, bot se zastaví.*/                
                wantsToStop.setValue(true);
                if (SteeringManager.DEBUG) System.out.println("Leader stopped. We stop also.");
                focus.data  = leadersLocation;
                if (SteeringManager.DEBUG) System.out.println("The focus should be "+focus);
                return nextVelocity;
            } else {
                if (botToLeader.length() <= distanceFromTheLeader) {
                    /* Druhá možnost znamená, že jsme příliš blízko vůdci. Pak je potřeba zpomalit. */
                    if (deceleration) {
                        /*Typicky jdeme-li za vůdcem, potřebujeme zpomalit, ale jít stále stejným směrem.
                         * Proto musíme navrátit vektor, který když se sečte s předpokládanou budoucí velocity (nebýt našeho steeringu), výsledný vektor bude směřovat k vůdci,
                         * ale bude menší než minulá velocity. Podle toho počítáme vektor, který vracíme.*/
                        if (leadersVelocity.length() <= actualVelocity.length()) {
                            /* V případě, že se vůdce zastavil a my jsme mu příliš blízko, je třeba od něj odejít.
                             * Tedy si přičteme sílu, která nás od něj oddálí.
                             */
                            Vector3d fromTheLeader = new Vector3d(botToLeader.x, botToLeader.y, botToLeader.z);
                            fromTheLeader.negate();
                            /* Čím je vůdcova rychlost menší než naše, tím větší bude síla, která na nás od něj zapůsobí.
                             * Tato síla může mít velikost např. 100 - 300, ale ještě se to upraví. */
                            fromTheLeader.normalize();
                            if (leadersVelocity.length() == 0) {
                                if (scaledActualVelocity.length() == 0)
                                    fromTheLeader.scale(leaderForce); //Zde by bylo hezčí, kdyby bot odcouval. Ale to neumíme.
                                else
                                    fromTheLeader.scale(2*scaledActualVelocity.length()); //Zde by bylo hezčí, kdyby bot odcouval. Ale to neumíme.
                                if (SteeringManager.DEBUG) System.out.println("Leader stopped. But we are too near. => From the leader "+fromTheLeader.length());
                            } else {
                                fromTheLeader.scale(50*actualVelocity.length() / leadersVelocity.length());
                            }
                            nextVelocity.add((Tuple3d) fromTheLeader);
                            if (SteeringManager.DEBUG) System.out.println("Leader is slower => From the leader "+fromTheLeader.length());
                        }
                        /* Vypočteme míru, jak moc jsme od vůdce vzdálení. Čím jsme blíže, tím více by nás měl vektor zbrzdit.
                         * Hodnota nextLength je procentuální část ze scaledActualVelocity.length(). Čím jsme vůdci blíže, tím je tato část menší.
                         * To bude totiž výsledná velocity směrem k vůdci, nebude-li jiných steeringů.*/
                        double nextLength = scaledActualVelocity.length() * (botToLeader.length()/distanceFromTheLeader);
                        botToLeader.scale(nextLength/botToLeader.length());
                        nextVelocity.add((Tuple3d) botToLeader);
                        nextVelocity.sub(scaledActualVelocity); //Toto celé je kvůli tomu, aby když se tento vektor složí se scaleActualVelocity, což je předpokládaná budoucí velocity, nebude-li ostatních steeirngů, tak výsledný vektor bude stále k vůdci, ale malý, takže následovník zpomalí.
                        if (SteeringManager.DEBUG) System.out.println("We are too near => From the leader "+nextVelocity.length());
                        /* Nechceme, aby se zrychlovalo.*/
                    } else {    //no deceleration
                        //Pokud není zapnutý parametr deceleration, prostě na nás působí síla od vůdce.
                        nextVelocity.sub(botToLeader);
                        double multiplier = botToLeader.length()/distanceFromTheLeader;
                        /*Čím jsme vůdci blíže, tím je multiplier menší. Výsledek je v rozsahu 0 (jsme těsně skoro na správném místě) až 1 (jsem úplně u vůdce). */
                        nextVelocity.normalize();
                        nextVelocity.scale(leaderForce*multiplier*multiplier);  //multiplier^2 je kvůli tomu, aby malé hodnoty byly opravdu malé.
                    }
                    wantsToGoFaster.setValue(false);
                } else {
                    /* Třetí případ znamená, že jsme od vůdce daleko. Pak je třeba ho dohnat.
                     * Přítáhne nás tedy síla k němu, která bude mít velikost: 0.3*(vzdálenost - ideální/2)*log(vzdálenost - ideální).
                     * Hodnoty se tedy pohybují okolo */
                    if (SteeringManager.DEBUG) System.out.println("Bot to leader "+botToLeader.length());
                    nextVelocity.add((Tuple3d) botToLeader);
                    nextVelocity.normalize();
                    double distanceFromIdealPosition = botToLeader.length() - distanceFromTheLeader;
                    double scale = getForceOfTheDistance(distanceFromIdealPosition);
                    nextVelocity.scale(scale);
                    if (SteeringManager.DEBUG) System.out.println("Bot to leader after scaling " + nextVelocity.length());    //If the leader is in distanceFromTheLeader, this force will be 200. If more, it will be about 300 to 400. If less, it will be from 113 to 200.
                    wantsToGoFaster.setValue(true);
                }                
            }
        } else {  //Formační typ leader followingu.
            Vector3d averageLeadersVelocity;
            if (velocityMemory) {   //Pokud používáme paměť, tak si vypočítáme průměrnou velocity vůdce za posledních několik tiků.
                    averageLeadersVelocity = getAverageLeadersVelocity();
                } else {    //Pokud paměť nepoužíváme, tak počítáme s aktuální velocity vůdce.
                    averageLeadersVelocity = leadersVelocity;
                }
            if (leadersVelocity.length() == 0 || averageLeadersVelocity.length() == 0) {    //Vůdce se právě zastavil nebo průměrná rychlost vyjde nulová.
                /* Jestliže vůdce stojí, tak my se musíme dostat na svou pozici a pak se také zastavit.*/
                Vector3d specialSituation = new Vector3d(lastLeadersNonZeroVelocity.x * Math.cos(angleFromTheLeader) - lastLeadersNonZeroVelocity.y * Math.sin(angleFromTheLeader), lastLeadersNonZeroVelocity.x * Math.sin(angleFromTheLeader) + lastLeadersNonZeroVelocity.y * Math.cos(angleFromTheLeader), 0);
                specialSituation.scale(distanceFromTheLeader / specialSituation.length());
                Vector3d botToSpSituation = new Vector3d(leadersLocation.x + specialSituation.x - botself.getLocation().x, leadersLocation.y + specialSituation.y - botself.getLocation().y, 0);
                if (botToSpSituation.length() <= NEARLY_THERE_DISTANCE) {
                    wantsToStop.setValue(true);
                    if (SteeringManager.DEBUG) System.out.println("Leader stopped. We stop also. "+botToSpSituation.length());
                    focus.data = leadersLocation;
                    if (SteeringManager.DEBUG) System.out.println("The focus should be "+focus);
                    return nextVelocity;
                } else {
                    if (SteeringManager.DEBUG) System.out.println("Leader stopped. But we must go to our position. "+botToSpSituation.length());
                    nextVelocity.add((Tuple3d)toTheSituation(botToSpSituation, wantsToGoFaster, leadersLocation));
                    wantsToGoFaster.setValue(false);
                    return nextVelocity;
                }
            } else {    //Vůdce se pohybuje.
                /* Jestliže se vůdce pohybuje, tak si vypočítáme svou pozici a na tu se budeme snažit dostat.*/   
                ConfigChange cc = botself.getWorldView().getSingle(ConfigChange.class);
                double oneTick = cc.getVisionTime();    //Doba trvání jednoho tiku. Potřebuje ke spočítání lokace vůdce za jeden tik.
                /*Vektor vectorSituationToTheLeader je vektor, který svírá s vektorem velocity vůdce úhel angleFromTheLeader, který je v radiánech*/
                Vector3d vectorSituationToTheLeader = new Vector3d(averageLeadersVelocity.x * Math.cos(angleFromTheLeader) - averageLeadersVelocity.y * Math.sin(angleFromTheLeader), averageLeadersVelocity.x * Math.sin(angleFromTheLeader) + averageLeadersVelocity.y * Math.cos(angleFromTheLeader), 0);
                /*Tento vektor zkrátíme na vzdálenost distanceFromTheLeader.*/
                vectorSituationToTheLeader.scale(distanceFromTheLeader / vectorSituationToTheLeader.length());
                /*Vektor leadersShiftToNextTick představuje vektor posunutí vůdce do příštího tiku, avšak místo jeho aktuální rychlosti bereme v případě paměti tu průměrnou.*/
                Vector3d leadersShiftToNextTick = new Vector3d(oneTick*averageLeadersVelocity.x, oneTick*averageLeadersVelocity.y, oneTick*averageLeadersVelocity.z);
                /* Vektor l je to samé s opravdovou aktuální rychlostí a slouží pouze pro kontrolní výpis.*/
                Vector3d l = new Vector3d(oneTick*leadersVelocity.x, oneTick*leadersVelocity.y, oneTick*leadersVelocity.z);
                Location leadersNextLoc = new Location(leadersLocation.x + leadersShiftToNextTick.x, leadersLocation.y + leadersShiftToNextTick.y, leadersLocation.z + leadersShiftToNextTick.z);
                if (SteeringManager.DEBUG) System.out.println("shift "+l.length()+" time "+oneTick+" 0.8*av "+(0.8*averageLeadersVelocity.length()));
                /*Vektor botToSituation je vektor od lokace následovníka k pozici, ke které má směřovat.
                 Ta se spočítá jako pozice vůdce v příštím tiku (leadersLocation + leadersShiftToNextTick) sečtená s vektorem určující pozici vůči vůdci (vectorSituationToTheLeader).*/
                Vector3d botToSituation = new Vector3d(leadersNextLoc.x + vectorSituationToTheLeader.x - botself.getLocation().x, leadersNextLoc.y + vectorSituationToTheLeader.y - botself.getLocation().y, 0);
                /* Vektor botToSituation přeškálujeme na vhodnou velikost a vrátíme jako výsledek steeirngu.*/
                nextVelocity.add((Tuple3d)toTheSituation(botToSituation, wantsToGoFaster, leadersNextLoc));
            }
        }

        return nextVelocity;
    }

    /**
     * Obecně by se měl vracet vektor botToSituation (vektor od bota k pozici vůči vůdci) - vhodně přeškálovaný.
     * Nicméně pokud je pozice jakoby za námi, ale vůdce jde zhruba ve směru LocToFollower, chtělo by to spíše zpomalit. To ještě není hotové a dala bych to jako volitelný parametr.
     * Dále se může stát, že mezi naší vysněnou lokací a námi je právě leader a navíc že jde stejným směrem. Pak je třeba se od tohoto směru vychýlit - a vůdce obejít.
     * K výpočtům tedy budeme potřebovat naší lokaci i aktuální velocity, to samé od leadera. K tomu vektor k naší pozici.
     */
    private Vector3d toTheSituation(Vector3d botToSituation, RefBoolean wantsToGoFaster, Location leadersNextLoc) {
        Vector3d result = new Vector3d(0,0,0);
        Vector2d leadersLoc = new Vector2d((leadersLocation.x + leadersNextLoc.x)/2, (leadersLocation.y + leadersNextLoc.y)/2);   //Dala by se použít lokace leadera v příštím tiku.
        Vector3d botToLeader = new Vector3d(leadersLocation.x - botself.getLocation().x,leadersLocation.y - botself.getLocation().y,0);
        Vector3d botToNextLeader = new Vector3d(leadersLoc.x - botself.getLocation().x,leadersLoc.y - botself.getLocation().y,0);

        wantsToGoFaster.setValue(false);
        
        if (circumvention && botToLeader.length() < botToSituation.length()) {
            Vector3d turningVector;
            double koef;
            boolean turnLeft;
            if (botToSituation.angle(botToNextLeader) == 0) {   // Vůdce je přímo před námi. Zatočíme náhodně nalevo či napravo.                
                turnLeft = random.nextBoolean();
                if (SteeringManager.DEBUG) { System.out.println("Leader exactly front collision."); }
                koef = 1;
            } else {
                Vector2d startVelocity = new Vector2d(botself.getLocation().x,botself.getLocation().y);
                Vector2d endVelocity = new Vector2d(botself.getLocation().x + botToSituation.x,botself.getLocation().y + botToSituation.y);
                Vector2d nearestPoint = SteeringTools.getNearestPoint(startVelocity, endVelocity, leadersLoc, true);
                Vector2d nearestPointToLeadersLoc = new Vector2d(nearestPoint.x - leadersLoc.x, nearestPoint.y - leadersLoc.y);
                koef = Math.max(0, (innerDistanceFromTheLeader - nearestPointToLeadersLoc.length())/innerDistanceFromTheLeader);
                turnLeft = SteeringTools.pointIsLeftFromTheVector(botToLeader, botToSituation);
                if (SteeringManager.DEBUG) { System.out.println("Leader nearly front collision."+nearestPointToLeadersLoc.length()); }
                if (SteeringManager.DEBUG) { System.out.println("Distance "+nearestPointToLeadersLoc.length()+" koef "+koef); }
            }
            turningVector = SteeringTools.getTurningVector(botToSituation, turnLeft);
            turningVector.scale(koef);
            if (SteeringManager.DEBUG) { System.out.println("Turning vector "+turningVector.length()+" botToSit "+botToSituation.length()+" turn left "+turnLeft); }
            result.add(turningVector);
        }
        result.add((Tuple3d) botToSituation);
        double scale = getForceOfTheDistance(botToSituation.length());
        result.normalize();
        result.scale(scale);        
        return result;
    }

    /**Ve vzdálenosti NOT_FAR_FROM_POSITION je to leaderForce a za každých dalších NOT_FAR_FROM_POSITION jednotek se síla zvětší o leaderForce.*/
    private double getForceOfTheDistance(double distance) {
        double scale = leaderForce * distance / forceDistance;
        if (scale > SteeringManager.MAX_FORCE) {
            scale = SteeringManager.MAX_FORCE;
        }
        return scale;
    }

    //A method which gets the identity of the leader.
    private Player getLeader()
    {
        Collection<Player> col=botself.getWorldView().getAll(Player.class).values();
        for (Player pl : col) {
            if (pl.getName().equals(leaderName)) {
                if (SteeringManager.DEBUG) System.out.println("Got the leader");
                leadersVelocities.addLast(pl.getVelocity().getVector3d());
                return pl;
            }
        }
        return null;
    }

    private Vector3d getAverageLeadersVelocity() {
        double x = 0;
        double y = 0;
        for(Vector3d velo : leadersVelocities) {
            x += velo.x;
            y += velo.y;
        }
        x = x/leadersVelocities.size();
        y = y/leadersVelocities.size();
        Vector3d averageVelo = new Vector3d(x,y,0);
        return averageVelo;
    }

    @Override
    public void setProperties(SteeringProperties newProperties) {
        this.leaderForce = ((LeaderFollowingProperties)newProperties).getLeaderForce();
        this.leaderName = ((LeaderFollowingProperties)newProperties).getLeaderName();
        this.distanceFromTheLeader = ((LeaderFollowingProperties)newProperties).getDistanceFromTheLeader();
        this.forceDistance = ((LeaderFollowingProperties)newProperties).getForceDistance();
        this.myLFtype = ((LeaderFollowingProperties)newProperties).getMyLFtype();
        this.deceleration = ((LeaderFollowingProperties)newProperties).isDeceleration();
        this.angleFromTheLeader = ((LeaderFollowingProperties)newProperties).getAngleFromTheLeader();
        this.velocityMemory = ((LeaderFollowingProperties)newProperties).isVelocityMemory();
        this.sizeOfMemory = ((LeaderFollowingProperties)newProperties).getSizeOfMemory();
        this.circumvention = ((LeaderFollowingProperties)newProperties).isCircumvention();
        if (forceDistance == 0) {
            forceDistance = 1;  //Jinak bychom dělili nulou.
        }
    }

    public LeaderFollowingProperties getProperties() {
        LeaderFollowingProperties properties = new LeaderFollowingProperties();

        properties.setLeaderForce(leaderForce);
        properties.setLeaderName(leaderName);
        properties.setDistanceFromTheLeader(distanceFromTheLeader);
        properties.setForceDistance(forceDistance);
        properties.setMyLFtype(myLFtype);
        properties.setDeceleration(deceleration);
        properties.setAngleFromTheLeader(angleFromTheLeader);
        properties.setVelocityMemory(velocityMemory);
        properties.setSizeOfMemory(sizeOfMemory);
        properties.setCircumvention(circumvention);

        return properties;
    }
}