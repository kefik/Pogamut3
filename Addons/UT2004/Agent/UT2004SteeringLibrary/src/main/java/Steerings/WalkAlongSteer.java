package Steerings;


import SocialSteeringsBeta.RefLocation;
import SteeringStuff.SteeringManager;
import SteeringStuff.RefBoolean;
import SteeringProperties.SteeringProperties;
import SteeringProperties.WalkAlongProperties;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import javax.vecmath.Vector3d;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Self;
import SteeringStuff.ISteering;
import SteeringStuff.SteeringTools;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.PlayAnimation;
import java.util.Collection;
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector2d;


/**
 * A class for providing obstacle avoiding steering to bots via raycasting.
 *
 * @author Marki
 */
public class WalkAlongSteer implements ISteering {

    /** This steering needs botself. */
    private UT2004Bot botself;
    
    /** ISteering properties: target location - bot approaches this location. */
    private Location targetLocation;
    /** ISteering properties: target gravity - a parameter meaning how attracted the bot is to his target location. */
    private int partnerForce;
    /** ISteering properties: distance from the partner. */
    private int distanceFromThePartner;
    /** ISteering properties: name of the partner */
    private String partnerName;
    /** ISteering properties: . */
    private boolean giveWayToPartner;
    /** ISteering properties: . */
    private boolean waitForPartner;

    private boolean turn = false;
    private boolean newToPartner = false;
    private boolean newToPartner2 = true;
    private boolean newWait = true;
    private boolean truncateToPartner = false;
    private boolean truncateNextVelocity = false;

    private static int MAX_TO_PARTNER = 500;
    private static int MAX_NEXT_VELOCITY = 500;
    private static int WAIT_DISTANCE = 100;
    private static int NEARLY_THERE_DISTANCE = 200;

    private boolean waiting;

    /** Partner. */
    private Player partner;

    private Vector3d forceToTarget;
    private Vector3d forceToPartner;
    private Vector3d forceFromPartner;
    
    /**
     * @param bot Instance of the steered bot.
     */
    public WalkAlongSteer(UT2004Bot bot) {
        botself = bot;
        waiting = false;
    }

    /** When called, the bot starts steering, when possible, he get's nearer the target location. */
    @Override
    public Vector3d run(Vector3d scaledActualVelocity, RefBoolean wantsToGoFaster, RefBoolean wantsToStop, RefLocation focus) {
        //The bot's velocity is received.
        Vector3d actualVelocity = botself.getVelocity().getVector3d();
        // Supposed velocity in the next tick of logic, after applying various steering forces to the bot.
        Vector3d nextVelocity = new Vector3d(0,0,0);

        if (partner == null || partner.getLocation() == null) {
            partner = getPartner();
            if (turn) {
                Vector3d turningVector = new Vector3d(actualVelocity.y, -actualVelocity.x, 0);   //Turns 45° left.
                turningVector.scale(1/(Math.sqrt(2)));
                Vector3d negativeVector = new Vector3d(-actualVelocity.x,-actualVelocity.y,0);
                negativeVector.scale(1-1/Math.sqrt(2));
                turningVector.add((Tuple3d)negativeVector);
                return turningVector;
            } else {
                wantsToStop.setValue(true);
                if (SteeringManager.DEBUG) {
                    System.out.println("We wait for the partner.");
                }
                return nextVelocity;
            }
        }

        //<editor-fold defaultstate="collapsed" desc="Pomocné předvýpočty a výpisy">

        /*Následující blok kódu slouží pro určení pozice (target) ve vzdálenosti distanceFTP/2 od cílové pozice na mé straně.
         Díky tomu si pak každý půjde na své místo kus od cíle a nebudou se o cíl prát.*/
        Location middlePoint = new Location((botself.getLocation().x + partner.getLocation().x) / 2, (botself.getLocation().y + partner.getLocation().y) / 2, botself.getLocation().z);
        Vector2d middlePointToTarget = new Vector2d(targetLocation.x - middlePoint.x, targetLocation.y - middlePoint.y);
        Vector2d middlePointToTargetNormal = new Vector2d(-middlePointToTarget.y, middlePointToTarget.x);
        middlePointToTargetNormal.normalize();
        middlePointToTargetNormal.scale(distanceFromThePartner / 2);
        /*Místo abychom předem zjišťovali na jakou stranu od cíle přičíst tuto normálu, tak si vypočteme obě možnosti a vybereme bližší z nich.*/
        Location targetA = new Location(targetLocation.x + middlePointToTargetNormal.x, targetLocation.y + middlePointToTargetNormal.y, targetLocation.z);
        Location targetB = new Location(targetLocation.x - middlePointToTargetNormal.x, targetLocation.y - middlePointToTargetNormal.y, targetLocation.z);
        Location targetMy;
        Location targetHis;
        if (botself.getLocation().getDistance(targetA) < botself.getLocation().getDistance(targetB)) {
            targetMy = targetA;
            targetHis = targetB;
        } else {
            targetMy = targetB;
            targetHis = targetA;
        }

        /* Jestliže jsme už dostatečně blízko svého cíle, zastavíme se a natočíme na partnera.*/
        if (SteeringManager.DEBUG) System.out.println("K mému cili "+botself.getLocation().getDistance(targetMy));

        if (botself.getLocation().getDistance(targetMy) < NEARLY_THERE_DISTANCE) {
            wantsToStop.setValue(true);
            focus.data = (partner.getLocation());
            if (SteeringManager.DEBUG) System.out.println("We reached the target and we stop.");
            return nextVelocity;
        }
        
        // A vector from the bot to the target location.
        Vector3d myVectorToTarget = new Vector3d(targetMy.x - botself.getLocation().x, targetMy.y - botself.getLocation().y, targetMy.z - botself.getLocation().z);
        // A vector from the partner to the target location.
        Vector3d partnersVectorToTarget = new Vector3d(targetHis.x - partner.getLocation().x, targetHis.y - partner.getLocation().y, targetHis.z - partner.getLocation().z);

        /*** START Kontrolní výpis vzdáleností. START ***/
        if (SteeringManager.DEBUG) System.out.println("Ja k cili "+myVectorToTarget.length());
        if (SteeringManager.DEBUG) System.out.println("Partner k cili "+partnersVectorToTarget.length());

        if (partnersVectorToTarget.length() > myVectorToTarget.length()) {
            if (SteeringManager.DEBUG) System.out.println("Je dal.");
        } else {
            if (SteeringManager.DEBUG) System.out.println("Musim ho dohnat.");
        }
        /*** END Kontrolní výpis vzdáleností. END ***/

        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Deklarace sil">

        /*--------------------------1-deklarace--------------------------*/
        /*Vektor k cíli. Ten, kdo je od cíle dál, je přitahován silou attractiveForce + 30 a větší (čím je větší rozdíl mezi tím, kdo je dál, tím je tato síla větší).*/
        forceToTarget = new Vector3d(targetMy.x - botself.getLocation().x, targetMy.y - botself.getLocation().y, 0);
        forceToTarget.normalize();

        /*--------------------------2-deklarace--------------------------*/
        /*Vektor od spojnice. Pokud je agent moc blízko spojnici, je od ní odpuzován. Čím je spojnici blíže, tím je síla větší. Díky tomu se k sobě nedostanou agenti moc blízko,
          ale zároveň si automaticky dělají místo. Vzhledem k tomu, že se tato spojnice vždy přepočítá dle aktuálního stavu, funguje to plynule a spolehlivě.*/
        Vector2d startC = new Vector2d(middlePoint.x, middlePoint.y);
        Vector2d endC = new Vector2d(targetLocation.x, targetLocation.y);
        Vector2d myLoc = new Vector2d(botself.getLocation().x, botself.getLocation().y);
        Vector2d foot = SteeringTools.getNearestPoint(startC, endC, myLoc, false);
        Vector3d footToBot = new Vector3d(myLoc.x - foot.x, myLoc.y - foot.y, 0);
        double fromFoot = footToBot.length();
        Vector3d botToFoot = new Vector3d(-footToBot.x, -footToBot.y, -footToBot.z);
        footToBot.normalize();
        botToFoot.normalize();
        Vector3d footToBotCopy = new Vector3d(footToBot.x, footToBot.y, footToBot.z);
        
        Vector3d partnerToBot = new Vector3d(botself.getLocation().x - partner.getLocation().x,botself.getLocation().y - partner.getLocation().y,botself.getLocation().z - partner.getLocation().z);
        double fromPartner = partnerToBot.length();
        partnerToBot.normalize();


        /*--------------------------3-deklarace--------------------------*/
        /*Vektor k partnerovi. Jsou-li partneři od sebe příliš daleko, táhne je síla k sobě. Čím jsou od sebe dál, tím je větší.*/
        Vector3d botToPartner = new Vector3d(partner.getLocation().x - botself.getLocation().x,partner.getLocation().y - botself.getLocation().y,partner.getLocation().z - botself.getLocation().z);
        double toPartner = botToPartner.length();
        botToPartner.normalize();
        

        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Výpočet sil">

        /*--------------------------1-výpočet--------------------------*/
        double diff = (myVectorToTarget.length() - partnersVectorToTarget.length())/500;
        int add = 0;
        if (diff > 0) {
            diff = Math.min(diff, 1.5);
            add = 30;
        }
        forceToTarget.scale(partnerForce*Math.pow(2,diff) + add);
        if (SteeringManager.DEBUG) System.out.println("ToTarget "+forceToTarget.length());

        /*--------------------------2-výpočet--------------------------*/
        if (giveWayToPartner) {
            if (fromFoot < distanceFromThePartner/2) {
                double scale = (distanceFromThePartner - 2*fromFoot) / distanceFromThePartner;
                footToBot.scale(partnerForce*scale);
                if (SteeringManager.DEBUG) System.out.println("From foot "+footToBot.length());
            }else {
                footToBot.set(0,0,0);
            }
            forceFromPartner = footToBot;
        } else {
            if (fromPartner < distanceFromThePartner) {
                double scale = (distanceFromThePartner - fromPartner) / distanceFromThePartner;
                partnerToBot.scale(partnerForce*scale);
                if (SteeringManager.DEBUG) System.out.println("From partner "+partnerToBot.length());
            } else {
                partnerToBot.set(0,0,0);
            }
            forceFromPartner = partnerToBot;
        }

        /*--------------------------3-výpočet--------------------------*/        
        if (toPartner > distanceFromThePartner) {
            Vector3d myForceToPartner = new Vector3d(0,0,0);
            /* Vylepšení: pokud je o dost blíže cíli než partner, tak jde buď ke spojnici (pokud je od ní daleko), či se zastaví (pokud je u ní blízko).*/
            double diff2 = partnersVectorToTarget.length() - myVectorToTarget.length();            
            if (waitForPartner && diff2 > 3 * distanceFromThePartner) {
                /* Jestliže je agent daleko od partnera, vhodně blízko u spojnice
                 * a o dost blíže cíli než partner, zastaví se (aby se nevracel k němu).*/
                if (fromFoot <= distanceFromThePartner/2) {
                    waiting = true;
                    wantsToStop.setValue(true);
                    focus.data = (partner.getLocation());
                    if (SteeringManager.DEBUG) {
                        System.out.println("We reached the target and we stop.");
                        if (SteeringManager.Thomas) botself.getAct().act(new PlayAnimation().setName("social_wavefar").setLoop(true));  //social_wavefar Throw
                    }
                    return nextVelocity;                    
                } else {
                    double scale = 2 * fromFoot / distanceFromThePartner;
                    botToFoot.scale(partnerForce * scale);
                    botToPartner.scale(partnerForce * scale);
                    myForceToPartner.add(botToFoot);
                    myForceToPartner.add(botToPartner);
                }
            } else {
                if (newWait && waiting) {
                    double distancesDifference = (partnersVectorToTarget.length() - myVectorToTarget.length());
                    System.out.println("Diff copy: "+distancesDifference);
                    if (distancesDifference > WAIT_DISTANCE) {
                        waiting = true;
                        wantsToStop.setValue(true);
                        focus.data = (partner.getLocation());
                        if (SteeringManager.DEBUG) {
                            System.out.println("We are waiting, but partner is already near to us.");                            
                        }
                        return nextVelocity;
                    }
                }
                /* Jsme-li od sebe příliš daleko, působí na nás přitažlivá síla. Čím jsme od sebe dále, tím je větší.*/
                double angleToTarget = forceToTarget.angle(botself.getVelocity().asVector3d());
                /* Když se agent vrací k partnerovi (jde zhruba od cíle), tak na něj nepůsobí síla přímo k partnerovi, ale mírně vedle něj.
                 * To znamená, že se k lokaci partnera přičte vektor od axis o velikost distanceFromPartner/2 --> a vezme se síla k této nové lokaci kousek vedle partnera.
                 * Díky tomu agent rovnou běží na správnou stranu od partnera a nemusí na něj působit síla fromAxis (ta je totiž původcem otáčení na špatnou stranu).
                 * Díky totmuto chování se zároveň nedostane příliš blízko k partnerovi - a ve chvíli, kdy se má otáčet, na něj můžeme zapůsobit silou toPointBetween,
                 * která ho přitáhne trochu k partnerovi (a trochu k cíli) - a nemusíme se bát, že do partnera vrazí.
                 * Tedy ne, že by do partnera nemohl vrazit, ale pokud nebude ve velké rychlosti/nebudou mít nastavenou příliš malou vzdálenost od sebe, tak by se to stát nemělo.
                 */
                if (newToPartner && (angleToTarget > Math.PI/2)) {
                    footToBotCopy.normalize();
                    footToBotCopy.scale(distanceFromThePartner / 2);
                    Location nextToPartner = new Location(partner.getLocation().x + footToBotCopy.x, partner.getLocation().y + footToBotCopy.y, partner.getLocation().z);
                    myForceToPartner = new Vector3d(nextToPartner.x - botself.getLocation().x, nextToPartner.y - botself.getLocation().y, nextToPartner.z - botself.getLocation().z);
                } else {
                    myForceToPartner.add(botToPartner);
                }
                double scale = (toPartner - distanceFromThePartner) / distanceFromThePartner;
                myForceToPartner.normalize();
                myForceToPartner.scale(partnerForce * scale);
                if (SteeringManager.DEBUG) {
                    System.out.println("To partner " + myForceToPartner.length());
                }
                /* Když se agent vrací k partnerovi (jde zhruba od cíle) a když už na něj působí malá přitažlivá síla k partnerovi
                 * (tedy je už blízko němu a brzo se začne otáčet), tak na něj začne působit ještě síla toPointBetween: k místu mezi partnerem a společným cílem.
                 * Čím je síla k partnerovi menší (což znamená, že je partnerovi blíže a bude se brzy otáčet), tím je síla toPointBetween větší.
                 * Díky síle toPointBetween se agent otočí na správnou stranu.
                 * Pozor, toto chování má jednu nevýhodu - agent se otočí trochu dříve, než dojde na úroveň partnera.
                 * Ale zhodnotila jsem to jako minoritní nevýhodu, neboť se tento rozdíl poměrně brzy srovná.
                 */
                if (newToPartner2 && (angleToTarget > Math.PI / 2)) {   
                    if (myForceToPartner.length() < partnerForce) {
                        Location betweenPaT = new Location((partner.getLocation().x + targetLocation.x) / 2, (partner.getLocation().y + targetLocation.y) / 2, (partner.getLocation().z + targetLocation.z) / 2);
                        Vector3d toPointBetween = new Vector3d(betweenPaT.x - botself.getLocation().x, betweenPaT.y - botself.getLocation().y, betweenPaT.z - botself.getLocation().z);
                        toPointBetween.normalize();
                        double scaleBetween = partnerForce - myForceToPartner.length();
                        toPointBetween.scale(scaleBetween);
                        myForceToPartner.add(toPointBetween);
                        forceFromPartner.scale(1 / 2);  //Navíc zmenšíme napolovic sílu od partnera (pouze síla toPointBetween někdy dle pozorování nestačila).
                    }
                }
            }
            forceToPartner = myForceToPartner;
        } else {
            forceToPartner = new Vector3d(0,0,0);
        }
        
        if (truncateToPartner) {
            if (forceToPartner.length() > MAX_TO_PARTNER) {
                forceToPartner.normalize();
                forceToPartner.scale(MAX_TO_PARTNER);
            }
        }
        //Toto bylo super: vectorToPartner.scale(attractiveForce * ((botself.getLocation().getDistance(partner.getLocation()) - distanceFromThePartner) * 2 / distanceFromThePartner));

        //</editor-fold>
        waiting = false;
        nextVelocity.add((Tuple3d)forceToTarget);
        nextVelocity.add((Tuple3d)forceFromPartner);
        nextVelocity.add((Tuple3d)forceToPartner);
        if (SteeringManager.DEBUG) System.out.println("Vektor vysledny "+nextVelocity.length());

        if (truncateNextVelocity) {
            if (nextVelocity.length() > MAX_NEXT_VELOCITY) {
                nextVelocity.normalize();
                nextVelocity.scale(MAX_NEXT_VELOCITY);
            }
        }

        wantsToGoFaster.setValue(false);
        return nextVelocity;
    }

    //A method which gets the identity of the leader.
    private Player getPartner() {
        UnrealId myId = botself.getWorldView().getSingle(Self.class).getId();
        Collection<Player> col = botself.getWorldView().getAll(Player.class).values();
        for (Player p : col) {
            if (SteeringManager.DEBUG) System.out.println("Player "+p.getName());
            if (p.getName().equals(partnerName) && p.getId() != myId) {
                if (SteeringManager.DEBUG) System.out.println("Got the partner "+p.toString());
                return p;
            }
        }
        return null;
    }
    @Override
    public void setProperties(SteeringProperties newProperties) {
        this.partnerForce = ((WalkAlongProperties)newProperties).getPartnerForce();
        this.partnerName = ((WalkAlongProperties)newProperties).getPartnerName();
        this.targetLocation = ((WalkAlongProperties)newProperties).getTargetLocation();
        this.distanceFromThePartner = ((WalkAlongProperties)newProperties).getDistanceFromThePartner();
        this.giveWayToPartner = ((WalkAlongProperties)newProperties).isGiveWayToPartner();
        this.waitForPartner = ((WalkAlongProperties)newProperties).isWaitForPartner();
    }

    public WalkAlongProperties getProperties() {
        WalkAlongProperties properties = new WalkAlongProperties();
        properties.setPartnerForce(partnerForce);
        properties.setPartnerName(partnerName);
        properties.setTargetLocation(targetLocation);
        properties.setDistanceFromThePartner(distanceFromThePartner);
        properties.setGiveWayToPartner(giveWayToPartner);
        properties.setWaitForPartner(waitForPartner);
        return properties;
    }

    public Vector3d getForceToPartner() {
        /*if (forceToPartner == null || forceToPartner.length() == 0) {
            return forceFromPartner;
        } else if (forceToPartner != null && forceFromPartner != null && forceFromPartner.length() > 0) {
            Vector3d sum = new Vector3d(forceFromPartner);
            sum.add(forceToPartner);
            return sum;
            //System.out.println("POZOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOR");
            //System.out.println(forceFromPartner.length());
            //System.out.println(forceToPartner.length());
        } else {
            return forceToPartner;
        }*/
        return forceFromPartner;
    }

    public Vector3d getForceToTarget() {
        //return forceToTarget;
        return forceToPartner;
    }

}
