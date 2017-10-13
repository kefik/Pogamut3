package SocialSteeringsBeta;

import SteeringStuff.ISteering;
import SteeringStuff.RefBoolean;
import SteeringStuff.SteeringManager;
import SteeringStuff.SteeringTools;
import SteeringStuff.SteeringType;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.Raycasting;
import cz.cuni.amis.pogamut.ut2004.bot.command.AdvancedLocomotion;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Configuration;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Move;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.SetWalk;
import javax.vecmath.Vector3d;

/**
 *
 * @author Petr
 */
public class SocialSteeringManager extends SteeringManager {

    protected IAnimationEngine animationEngine;
    private int lastWS = 0;

    public SocialSteeringManager(UT2004Bot bot, Raycasting raycasting, AdvancedLocomotion locomotion, IAnimationEngine engine, double multiplier) {
        super(bot, raycasting, locomotion, multiplier);
        animationEngine = engine;
    }

    public SocialSteeringManager(UT2004Bot bot, Raycasting raycasting, AdvancedLocomotion locomotion, IAnimationEngine engine) {
        this(bot, raycasting, locomotion, engine, 1);

    }

    /**
     * DOCASNY komentar v CZ Pridana funkcionalita by mela byt schopna <br/>
     * a)zastavit pohyb pokud bude animationEngine prehravat neprerusitelnou
     * animaci<br/> b)pouzivat 3 rychlosti pohybu misto puvodnich 2<br/> c)treti
     * rychlost pohybu nechava postavu natocenou do focus celou dobu pohybu,
     * tedy je vhodne pouzit "animaci slapani zeli" <br/>
     *
     * @param nextVelocity
     * @param everyoneWantsToGoFaster
     * @param focusLocation
     */
    @Override
    public void moveTheBot(Vector3d nextVelocity, boolean everyoneWantsToGoFaster, Location focusLocation) {
        
        double precision;
        if (nextVelocity instanceof SteeringResult && nextVelocity.length() > 0.001) { //also indicates social steering

            precision = ((SteeringResult) nextVelocity).getAccurancyMultiplier();
           
        } else if(nextVelocity.length() > 0.001){ //basic steerings
            if (animationEngine.canBeInterupt(botself)) {
                //<editor-fold defaultstate="collapsed" desc="debug">
                if (SOC_STEER_LOG.DEBUG) {
                    SOC_STEER_LOG.AddLogLine("basic steering manager", SOC_STEER_LOG.KSync + botself.getName());
                }
                //</editor-fold>
                 animationEngine.playAnimation(2, botself, true,0);
               // botself.getAct().act(new SetWalk().setWalk(true).setWalkAnim("walk_loop"));
                super.moveTheBot(nextVelocity, everyoneWantsToGoFaster, focusLocation);
                return;
            }else
            {
                //<editor-fold defaultstate="collapsed" desc="debug">
                if (SOC_STEER_LOG.DEBUG) {
                    SOC_STEER_LOG.AddLogLine("basic steering manager, wait for anim", SOC_STEER_LOG.KSync + botself.getName());
                }
                //</editor-fold>
                stopMovement(focusLocation);
                return;
            }
        }else //
        {
            precision = 0;
        }
        
        if (SteeringManager.DEBUG) System.out.println("Precision: "+precision);

        //determinates how we will move(run = 3, walk = 2, pace = 1)
        int walkType;

        double nextVelocityLength = nextVelocity.length() * multiplier; //The multiplier enables to enlarge or decrease the velocity. E.g. to make the bot to run.

        if (precision == 0) { //bot stops
            walkType = 0;
        } else if (precision < 3) { //bot 
            walkType = 1;
        } else if (nextVelocityLength < WALK_VELOCITY_LENGTH * 100)
        {
            walkType = 2;
        } else //running
        {
            walkType = 3;
        }



        //<editor-fold defaultstate="collapsed" desc="wants to go faster">
//        if (nextVelocityLength < 0.8*WALK_VELOCITY_LENGTH && everyoneWantsToGoFaster) {
//            if (SteeringManager.DEBUG) System.out.println("we enlarge the velocity");
//            nextVelocityLength = 0.8 * WALK_VELOCITY_LENGTH;
//        }

        //</editor-fold>

        double nextVelMult = nextVelocityLength / WALK_VELOCITY_LENGTH;
        nextVelocityLength = nextVelMult * WALK_VELOCITY_LENGTH;
        nextVelocity.normalize();
        nextVelocity.scale(nextVelocityLength);


        myNextVelocity = new Vector3d(nextVelocity.x, nextVelocity.y, nextVelocity.z);
        botself.getAct().act(new Configuration().setSpeedMultiplier(nextVelocityLength / WALK_VELOCITY_LENGTH).setAutoTrace(true).setDrawTraceLines(drawRaycasting));

        if (SteeringManager.DEBUG) System.out.println("animationEngine.canBeInterupt "+animationEngine.canBeInterupt(botself));

        if (animationEngine.canBeInterupt(botself)) {
            switch (walkType) {
                case 0:
                    //<editor-fold defaultstate="collapsed" desc="debug">
                    if (SOC_STEER_LOG.DEBUG) {
                        SOC_STEER_LOG.AddLogLine("correct place, idling", SOC_STEER_LOG.KSync + botself.getName());
                    }
                    //</editor-fold>
                    stopMovement( focusLocation);
                    if (SteeringManager.DEBUG) System.out.println("stopMovement1");
                    break;

                case 1:
                   
                    Location tLoc = new Location(botself.getLocation().x + nextVelocity.x, botself.getLocation().y + nextVelocity.y, botself.getLocation().z);
                    
                    Move m = new Move();
                    // todo: pomoci sinove vety spocitat uhel ve kterem se koukame a nastavit straffle left resp straffle right pro pohyb k cily
                    int direction;
                    double angle = SteeringTools.getAngle(botself.getLocation(), botself.getRotation(), tLoc);
                    if(angle < Math.PI / 4)
                    {
                        direction = 0;
                    }else if (angle > ( Math.PI / 4) * 3 )
                    {
                        direction = 2;
                    }else 
                    {
                        if(SteeringTools.pointIsLeftFromTheVector(focusLocation.sub(botself.getLocation()).asVector3d(), tLoc.asVector3d()))
                        {
                            direction = 3;
                        }else
                        {
                            direction = 1;
                        }
                    }
                    //0.. rovne 1..prava 2..couva 3..leva
                    m.setFirstLocation(tLoc);
                    m.setFocusLocation(focusLocation);
                    Configuration c = new Configuration();
                    
                    c.setSpeedMultiplier(0.4);
                    botself.getAct().act(c);
                    
                    botself.getAct().act(m);
                    
                    if (lastWS == walkType) {
                        break;
                    }
                    String s = "";
                    if (myNextVelocity.length() > 20) {
                        s = animationEngine.playAnimation(1, botself, true, direction);
                        if (SteeringManager.DEBUG) System.out.println("Play animation strafe.");
                    } else {
                        //<editor-fold defaultstate="collapsed" desc="debug">
                        if (SOC_STEER_LOG.DEBUG) {
                            SOC_STEER_LOG.AddLogLine("slowMove not anim", SOC_STEER_LOG.KSync + botself.getName());
                        }
                        //</editor-fold>
                    }//<editor-fold defaultstate="collapsed" desc="debug">
                    if (SOC_STEER_LOG.DEBUG) {
                        SOC_STEER_LOG.AddLogLine("slowMove " + angle + " angle " + s, SOC_STEER_LOG.KSync + botself.getName());
                    }
                    //</editor-fold>

                    //botself.getAct().act(new SetWalk().setWalk(true).setWalkAnim(s));

                    break;
                
                case 2:
                     
                    tLoc = new Location(botself.getLocation().x + nextVelocity.x, botself.getLocation().y + nextVelocity.y, botself.getLocation().z);

                    Move mm = new Move();
                    mm.setFirstLocation(tLoc);
                    mm.setFocusLocation(tLoc);

                    Configuration cc = new Configuration();
                    
                    cc.setSpeedMultiplier(0.8);
                    botself.getAct().act(cc);
                    
                    botself.getAct().act(mm);
                    if (lastWS == walkType) {
                        break;
                    }
                 
                    String s2 = animationEngine.playAnimation(2, botself, true,0);
                    //<editor-fold defaultstate="collapsed" desc="debug">
                    if (SOC_STEER_LOG.DEBUG) {
                        SOC_STEER_LOG.AddLogLine("Walk" + s2, SOC_STEER_LOG.KSync + botself.getName());
                    }
                    //</editor-fold>
                    botself.getAct().act(new SetWalk().setWalk(true).setWalkAnim(s2));
                    break;
                case 3: break;
            }
        } else {
            //animation that can not be interrupted is played on botself, so we do not move
            //<editor-fold defaultstate="collapsed" desc="debug">
            if (SOC_STEER_LOG.DEBUG) {
                SOC_STEER_LOG.AddLogLine("Wait for animation unineruptible animation", SOC_STEER_LOG.KSync + botself.getName());
            }
            //</editor-fold>
         
             stopMovement(focusLocation);
            if (SteeringManager.DEBUG) System.out.println("stopMovement2");
            
        }
        //this.myNextVelocity.x = 0;
        //this.myNextVelocity.y = 0;
        //this.myNextVelocity.z = 0;
        lastWS = walkType; 
    }

    private void stopMovement(Location focusLocation) {
        
        myNextVelocity = new Vector3d(0, 0, 0);
        locomotion.setSpeed(0.0);//just for sure...
        locomotion.stopMovement();
        if (!focusLocation.equals(new Location(0,0,0))) {
            locomotion.turnTo(focusLocation);
        }
        if (SteeringManager.DEBUG) System.out.println(botself.getName()+" We turn to "+focusLocation);
        if(animationEngine.canBeInterupt(botself)){
            animationEngine.playAnimation("ambi_stand_normal01", botself, false);
        }
    }

    @Override
    protected Location setFocusSpecific(SteeringType steeringType, boolean wantsToStop, Location newFocus, Location focusLoc) {
        if (steeringType == SteeringType.TRIANGLE) {
            return newFocus;
        }
        return super.setFocusSpecific(steeringType, wantsToStop, newFocus, focusLoc);
    }

    @Override
    protected Vector3d setVelocitySpecific(ISteering steering, RefBoolean wantsToGoFaster, RefBoolean wantsToStop, RefLocation newFocus) {
        if (steering instanceof ISocialSteering) {
            return ((ISocialSteering) steering).runSocial(myNextVelocity, wantsToGoFaster, wantsToStop, newFocus);
        } else {
            return super.setVelocitySpecific(steering, wantsToGoFaster, wantsToStop, newFocus);
        }
    }
}
