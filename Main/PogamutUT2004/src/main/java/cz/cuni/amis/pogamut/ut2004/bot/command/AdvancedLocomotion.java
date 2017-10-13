package cz.cuni.amis.pogamut.ut2004.bot.command;

import java.util.logging.Logger;

import javax.vecmath.Vector3d;

import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.base3d.worldview.object.Rotation;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Configuration;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.ContinuousMove;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Dodge;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Jump;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Move;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.TurnTo;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Self;
import javax.vecmath.Matrix3d;

/**
 * Class providing Pogamut2 UT2004 advanced locomotion commands for the bot -
 * strafing, advanced turning, dodging...
 *
 * @author Michal 'Knight' Bida
 */
public class AdvancedLocomotion extends SimpleLocomotion {

    /**
     * Self object holding information about our agent. 
	 *
     */
    Self self = null;

    /**
     * {@link Self} listener. this is needed because self can be updated during
     * the simulation and in multiPogamut we are stuck with the old version
     */
    private class SelfListener implements IWorldObjectEventListener<Self, WorldObjectUpdatedEvent<Self>> {

        private IWorldView worldView;

        /**
         * Constructor. Registers itself on the given WorldView object.
         *
         * @param worldView WorldView object to listent to.
         */
        public SelfListener(IWorldView worldView) {
            this.worldView = worldView;
            worldView.addObjectListener(Self.class, WorldObjectUpdatedEvent.class, this);
        }

        @Override
        public void notify(WorldObjectUpdatedEvent<Self> event) {
            self = event.getObject();
        }
    }
    /**
     * {@link Self} listener
     */
    private SelfListener selfListener = null;
    /**
     * Used to set focus when strafing left and right, holds the distance of
     * focus location.
     */
    private static final double FOCUS_DISTANCE = 3000;

    /**
     * Makes the bot to move through first location to second location (may be
     * specified directly or some ILocated object may be supplied - carefull
     * with objects traversability). Usage is when you want to have your bot to
     * move really smooth. Where is the problem? If you would want to achive the
     * same thing with 2 moveTo functions (first move to location1, when there
     * move to location2), there may be a little lag - you have to check if you
     * are already at first location and etc. This function can solve this
     * problem as the check is done in UnrealScript.
     *
     * (issues GB MOVE command)
     *
     * @param firstLocation First location we will go through.
     * @param secondLocation Second location we will go to (after reaching first).
     *
     * @see moveContinuous()
     */
    public void moveAlong(ILocated firstLocation, ILocated secondLocation) {
        Move moveAlong = new Move();

        moveAlong.setFirstLocation(firstLocation.getLocation());
        moveAlong.setSecondLocation(secondLocation.getLocation());

        agent.getAct().act(moveAlong);
    }

    /**
     * This makes the bot to run straight ahead continuously. Will stop when
     * other move command is issued - stopMovement, strafeTo, moveTo, moveAlong
     * even turn commands will interrupt this.
     *
     * (issues GB CMOVE command)
     *
     * @see moveAlong(ILocated, ILocated)
     *
     */
    public void moveContinuos() {
        agent.getAct().act(new ContinuousMove());
    }

    /**
     * Bot strafes right. The length of the strafe is specified by distance
     * attribute (in UT units, 1 UT unit equals roughly 1 cm). The bot will be
     * looking to object specified by the attribute focusId.
     *
     * @param distance - how far the bot strafes (in UT units, 1 UT unit equals
     * roughly 1 cm).
     * @param focusId - UnrealId of the object that should be the bot focus.
     * @see strafeLeft(double,ILocated)
     */
    public void strafeRight(double distance, UnrealId focusId) {
        if (self == null) {
            self = agent.getWorldView().getSingle(Self.class);
        }
        if (self != null) {
            Location startLoc = self.getLocation();
            Location directionVector = self.getRotation().toLocation();
            Location targetVec = directionVector.cross(new Location(0, 0, 1)).getNormalized().scale(-distance);

            agent.getAct().act(
                    new Move().setFirstLocation(startLoc.add(targetVec)).setFocusTarget(focusId));
        }
    }

    /**
     * Bot strafes right. The length of the strafe is specified by distance
     * attribute (in UT units, 1 UT unit equals roughly 1 cm). The bot will be
     * looking to location specified by the attribute focusLocation.
     *
     * @param distance - how far the bot strafes (in UT units, 1 UT unit equals
     * roughly 1 cm).
     * @param focusLocation - location where the bot should look
     * @see strafeLeft(double,ILocated)
     */
    public void strafeRight(double distance, ILocated focusLocation) {
        if (self == null) {
            self = agent.getWorldView().getSingle(Self.class);
        }
        if (self != null) {
            Location startLoc = self.getLocation();
            Location directionVector = self.getRotation().toLocation();
            Location targetVec = directionVector.cross(new Location(0, 0, 1)).getNormalized().scale(-distance);

            agent.getAct().act(
                    new Move().setFirstLocation(startLoc.add(targetVec)).setFocusLocation(focusLocation.getLocation()));
        }
    }

    /**
     * Bot strafes right. The length of the strafe is specified by distance
     * attribute (in UT units, 1 UT unit equals roughly 1 cm). Note that this
     * will reset the bot focus. The bot will be looking straight ahead (however
     * if the strafe is really long - more than 500 UT units - it will be
     * visible the bot is turning slightly performing the strafe).
     *
     * @param distance - how far the bot strafes (in UT units, 1 UT unit equals
     * roughly 1 cm).
     * @see strafeLeft(double)
     */
    public void strafeRight(double distance) {
        if (self == null) {
            self = agent.getWorldView().getSingle(Self.class);
        }
        if (self != null) {
            Location startLoc = self.getLocation();
            Location directionVector = self.getRotation().toLocation();
            Location targetVec = directionVector.cross(new Location(0, 0, 1)).getNormalized().scale(-distance);

            agent.getAct().act(
                    new Move().setFirstLocation(startLoc.add(targetVec)).setFocusLocation(
                    startLoc.add(directionVector.getNormalized().scale(
                    FOCUS_DISTANCE))));
        }
    }

    /**
     * Bot strafes left. The length of the strafe is specified by distance
     * attribute (in UT units, 1 UT unit equals roughly 1 cm). The bot will be
     * looking to object specified by the attribute focusId.
     *
     * @param distance - how far the bot strafes (in UT units, 1 UT unit equals
     * roughly 1 cm).
     * @param focusId - UnrealId of the object that should be the bot focus.
     * @see strafeRight(double,ILocated)
     */
    public void strafeLeft(double distance, UnrealId focusId) {
        if (self == null) {
            self = agent.getWorldView().getSingle(Self.class);
        }
        if (self != null) {
            Location startLoc = self.getLocation();
            Location directionVector = self.getRotation().toLocation();
            Location targetVec = directionVector.cross(new Location(0, 0, 1)).getNormalized().scale(distance);

            agent.getAct().act(
                    new Move().setFirstLocation(startLoc.add(targetVec)).setFocusTarget(focusId));
        }
    }

    /**
     * Bot strafes left. The length of the strafe is specified by distance
     * attribute (in UT units, 1 UT unit equals roughly 1 cm). The bot will be
     * looking to location specified by the attribute focusLocation.
     *
     * @param distance - how far the bot strafes (in UT units, 1 UT unit equals
     * roughly 1 cm).
     * @param focusLocation - location where the bot should look
     * @see strafeRight(double,ILocated)
     */
    public void strafeLeft(double distance, ILocated focusLocation) {
        if (self == null) {
            self = agent.getWorldView().getSingle(Self.class);
        }
        if (self != null) {
            Location startLoc = self.getLocation();
            Location directionVector = self.getRotation().toLocation();
            Location targetVec = directionVector.cross(new Location(0, 0, 1)).getNormalized().scale(distance);

            agent.getAct().act(
                    new Move().setFirstLocation(startLoc.add(targetVec)).setFocusLocation(focusLocation.getLocation()));
        }
    }

    /**
     * Bot strafes left. The length of the strafe is specified by distance
     * attribute (in UT units, 1 UT unit equals roughly 1 cm). Note that this
     * will reset the bot focus. The bot will be looking straight ahead (however
     * if the strafe is really long - more than 500 UT units - it will be
     * visible the bot is turning slightly performing the strafe).
     *
     * @param distance - how far the bot strafes (in UT units, 1 UT unit equals
     * roughly 1 cm).
     * @see strafeRight(double)
     */
    public void strafeLeft(double distance) {
        if (self == null) {
            self = agent.getWorldView().getSingle(Self.class);
        }
        if (self != null) {
            Location startLoc = self.getLocation();
            Location directionVector = self.getRotation().toLocation();
            Location targetVec = directionVector.cross(new Location(0, 0, 1)).getNormalized().scale(distance);

            agent.getAct().act(
                    new Move().setFirstLocation(startLoc.add(targetVec)).setFocusLocation(
                    startLoc.add(directionVector.getNormalized().scale(
                    FOCUS_DISTANCE))));
        }
    }

    /**
     * Makes the bot to move to location while looking at focusLocation. (issues
     * GB MOVE command)
     *
     * @param location Location we will strafe to.
     * @param focusLocation Location we will look at while strafing.
     *
     * @see strafeTo(ILocated, UnrealId)
     */
    public void strafeTo(ILocated location, ILocated focusLocation) {
        Move move = new Move().setFirstLocation(location.getLocation()).setFocusLocation(focusLocation.getLocation());
        agent.getAct().act(move);
    }

    /**
     * Makes the bot to move at location, while looking at focus object. Note
     * that when you support focus object, the bot will update his focus (place
     * he is looking at) according to focus object location (this will be
     * provided by GB UnrealScript code). Usefull when you want to track some
     * player position while moving somewhere else. (issues GB MOVE command)
     *
     * @param location Location we will strafe to.
     * @param focus Object with UrealId. We will look at this location while
     * strafing. We will update our focus location according to the current
     * position of this obejct in UT.
     *
     * @see strafeTo(ILocated, ILocated)
     *
     * @todo To check if supported object is also ILocated? see below
     */
    public void strafeTo(ILocated location, UnrealId focus) {
        Move move = new Move().setFirstLocation(location.getLocation()).setFocusTarget(focus);
        // TODO: To check if this object is also ILocated?
        // How this could be done? We need to check if supported IWorldObject
        // implements interface ILocated
		/*
         * ILocated tmpILocatedCheck; if (tmpILocatedCheck.getClass() ==
         * focus.getClass().getInterfaces()[0]) {
         *
         * }
         */
        agent.getAct().act(move);
    }
    
    /**
     * Makes the bot to move to location while looking at focusLocation. (issues
     * GB MOVE command)
     *
     * @param firstLocation First location we will strafe through.
     * @param secondLocation Second location we will strafe to (after reaching first).
     * @param focusLocation Location we will look at while strafing.
     *
     * @see strafeTo(ILocated, UnrealId)
     */
    public void strafeAlong(ILocated firstLocation, ILocated secondLocation, ILocated focusLocation) {
        Move move = new Move().setFirstLocation(firstLocation.getLocation()).setSecondLocation(secondLocation.getLocation()).setFocusLocation(focusLocation.getLocation());
        agent.getAct().act(move);
    }

    /**
     * Makes the bot to move at location, while looking at focus object. Note
     * that when you support focus object, the bot will update his focus (place
     * he is looking at) according to focus object location (this will be
     * provided by GB UnrealScript code). Usefull when you want to track some
     * player position while moving somewhere else. (issues GB MOVE command)
     * 
     * This method allows you to specify one location to strafe to in advance.
     * Usage is when you want to have your bot to
     * move really smooth. Where is the problem? If you would want to achive the
     * same thing with 2 moveTo functions (first move to location1, when there
     * move to location2), there may be a little lag - you have to check if you
     * are already at first location and etc. This function can solve this
     * problem as the check is done in UnrealScript.
     *
     * @param firstLocation First location we will strafe through.
     * @param secondLocation Second location we will strafe to (after reaching first).
     * @param focus Object with UrealId. We will look at this location while strafing. We will update our focus location according to the current position of this obejct in UT.
     * 
     * @see strafeTo(ILocated, ILocated)
     *
     * @todo To check if supported object is also ILocated? see below
     */
    public void strafeAlong(ILocated firstLocation, ILocated secondLocation, UnrealId focus) {
        Move move = new Move().setFirstLocation(firstLocation.getLocation()).setSecondLocation(secondLocation.getLocation()).setFocusTarget(focus);
        // TODO: To check if this object is also ILocated?
        // How this could be done? We need to check if supported IWorldObject
        // implements interface ILocated
		/*
         * ILocated tmpILocatedCheck; if (tmpILocatedCheck.getClass() ==
         * focus.getClass().getInterfaces()[0]) {
         *
         * }
         */
        agent.getAct().act(move);
    }
    
    

    /**
     * Makes the bot to double jump instantly (issues GB JUMP command) with
     * default settings.
     *
     * @todo How to convince javadoc see to link to method in super class
     *
     * @see jump()
     * @see dodge(Vector3d)
     */
    public void doubleJump() {
        Jump jump = new Jump();
        jump.setDoubleJump(true);

        // TODO: [Michal Bida] remove when GB is fixed
        jump.setForce((double) 680);

        agent.getAct().act(jump);
    }

    /**
     * Makes the bot to jump instantly (issues GB JUMP command) with custom
     * settings. <p><p> See also {@link SimpleLocomotion#jump()}.
     *
     * @param doubleJump whether the bot should double jump
     * @param secondJumpDelay If doubleJump, than after time specified here, the
     * bot performs second jump of a double jump (if DoubleJump is true). Time
     * is in seconds. GB2004 default is 0.5s.
     * @param jumpZ than this is a force vector specifying how big the jump
     * should be. Can't be set more than 2 * JumpZ = 680 for double jump.
     *
     * @see jump()
     * @see dodge(Vector3d)
     */
    public void generalJump(boolean doubleJump, double secondJumpDelay, double jumpZ) {
        Jump jump = new Jump();
        jump.setDoubleJump(doubleJump);
        if (doubleJump) {
            jump.setDelay(secondJumpDelay);
        }
        jump.setForce(jumpZ);
        agent.getAct().act(jump);
    }

    /**
     * Makes the bot to double jump instantly (issues GB JUMP command) with
     * custom settings. <p><p> See also {@link SimpleLocomotion#jump()}.
     *
     * @param secondJumpDelay After time specified here, the bot performs second
     * jump of a double jump (if DoubleJump is true). Time is in seconds. GB2004
     * default is 0.5s.
     * @param jumpZ Force vector specifying how big the jump should be. Can't be
     * set more than 2 * JumpZ = 680 for double jump.
     *
     * @see jump()
     * @see dodge(Vector3d)
     */
    public void doubleJump(double secondJumpDelay, double jumpZ) {
        Jump jump = new Jump();
        jump.setDoubleJump(true);
        jump.setDelay(secondJumpDelay);
        jump.setForce(jumpZ);
        agent.getAct().act(jump);
    }

    /**
     * Makes the bot to dodge in the selected direction (this is in fact single
     * jump that is executed to selected direction). Direction is absolute in
     * this method. (issues GB DODGE command)
     *
     * @param direction Absolute vector (that will be normalized) that specifies
     * direction of the jump.
     * @param bDouble Wheter we want to perform double dodge.
     * @see jump()
     * @see doubleJump()
     */
    public void dodge(Location direction, boolean bDouble) {
        direction = direction.getNormalized();
        double alpha = Math.acos(self.getRotation().toLocation().getNormalized().dot(direction.getNormalized()));
        double orientation = self.getRotation().toLocation().cross(new Location(0, 0, 1)).dot(direction);
        if (orientation > 0) {
            alpha = -alpha;
        }

        Matrix3d rot = Rotation.constructXYRot(alpha);
        direction = new Location(1, 0, 0).mul(rot);

        act.act(new Dodge().setDirection(direction).setDouble(bDouble));
    }
    
    /**
     * Dodges from specified location to the left of the bot.
     * 
     * @param inFrontOfTheBot Location to dodge from.
     * @param bDouble Whether to perform double dodge.
     */
    public void dodgeLeft(ILocated inFrontOfTheBot, boolean bDouble) {
        ILocated bot = self.getLocation();
        Location direction = new Location(inFrontOfTheBot.getLocation().x - bot.getLocation().x, inFrontOfTheBot.getLocation().y - bot.getLocation().y, 0);
        direction = direction.getNormalized();

        double x = direction.getX();
        double y = direction.getY();

        direction = new Location(-y, x, 0);

        double alpha = Math.acos(self.getRotation().toLocation().getNormalized().dot(direction.getNormalized()));
        double orientation = self.getRotation().toLocation().cross(new Location(0, 0, 1)).dot(direction);
        if (orientation > 0) {
            alpha = -alpha;
        }

        Matrix3d rot = Rotation.constructXYRot(alpha);
        direction = new Location(-1, 0, 0).mul(rot);

        this.act.act(new Dodge().setDirection(direction).setDouble(bDouble));
    }

    /**
     * Dodges from specified location to the right of the bot.
     * 
     * @param inFrontOfTheBot Location to dodge from.
     * @param bDouble Whether to perform double dodge.
     */
    public void dodgeRight(ILocated inFrontOfTheBot, boolean bDouble) {
        ILocated bot = self.getLocation();
        Location direction = new Location(inFrontOfTheBot.getLocation().x - bot.getLocation().x, inFrontOfTheBot.getLocation().y - bot.getLocation().y, 0);
        direction = direction.getNormalized();

        double x = direction.getX();
        double y = direction.getY();

        direction = new Location(-y, x, 0);

        double alpha = Math.acos(self.getRotation().toLocation().getNormalized().dot(direction.getNormalized()));

        double orientation = self.getRotation().toLocation().cross(new Location(0, 0, 1)).dot(direction);
        if (orientation > 0) {
            alpha = -alpha;
        }

        Matrix3d rot = Rotation.constructXYRot(alpha);
        direction = new Location(1, 0, 0).mul(rot);

        this.act.act(new Dodge().setDirection(direction).setDouble(bDouble));
    }

    /**
     * Dodges from the specified location.
     * 
     * @param inFrontOfTheBot Location to dodge from.
     * @param bDouble Whether to perform double dodge.
     */
    public void dodgeBack(ILocated inFrontOfTheBot, boolean bDouble) {
        ILocated bot = self.getLocation();
        Location direction = new Location(bot.getLocation().x - inFrontOfTheBot.getLocation().x, bot.getLocation().y - inFrontOfTheBot.getLocation().y, 0);
        direction = direction.getNormalized();

        double alpha = Math.acos(self.getRotation().toLocation().getNormalized().dot(direction.getNormalized()));

        double orientation = self.getRotation().toLocation().cross(new Location(0, 0, 1)).dot(direction);
        if (orientation > 0) {
            alpha = -alpha;
        }

        Matrix3d rot = Rotation.constructXYRot(alpha);
        direction = new Location(1, 0, 0).mul(rot);

        this.act.act(new Dodge().setDirection(direction).setDouble(bDouble));
    }

    /**
     * Dodges towards the specified location.
     * 
     * @param inFrontOfTheBot Location to dodge to.
     * @param bDouble Whether to perform double dodge.
     */
    public void dodgeTo(ILocated inFrontOfTheBot, boolean bDouble) {
        ILocated bot = self.getLocation();
        Location direction = new Location(inFrontOfTheBot.getLocation().x - bot.getLocation().x, inFrontOfTheBot.getLocation().y - bot.getLocation().y, 0);
        direction = direction.getNormalized();

        double alpha = Math.acos(self.getRotation().toLocation().getNormalized().dot(direction.getNormalized()));

        double orientation = self.getRotation().toLocation().cross(new Location(0, 0, 1)).dot(direction);
        if (orientation > 0) {
            alpha = -alpha;
        }

        Matrix3d rot = Rotation.constructXYRot(alpha);
        direction = new Location(1, 0, 0).mul(rot);

        this.act.act(new Dodge().setDirection(direction).setDouble(bDouble));
    }

    /**
     * Makes the bot to dodge in the selected direction (this is in fact single
     * jump that is executed to selected direction). Direction is relative to
     * bot actual rotation! (issues GB DODGE command)
     *
     * @param direction Relative vector (that will be normalized) that specifies
     * direction of the jump. Relative means that the bot current rotation will
     * be added to the this vector. So to dodge ahead issue direction (1,0,0).
     *
     * @param bDouble Wheter we want to perform double dodge.
     * @see jump()
     * @see doubleJump()
     */
    public void dodgeRelative(Location direction, boolean bDouble) {
        agent.getAct().act(new Dodge().setDirection(direction).setDouble(bDouble));
    }

    /**
     * Sets the speed multiplier for the bot. By this number the bots default
     * speed will be multiplied by. (issues GB CONF command)
     *
     * @param speedMultiplier Ranges from 0.1 to 2 (max may be set in ini in
     * [RemoteBot] MaxSpeed)
     *
     * @see setRotationSpeed(Rotation)
     */
    public void setSpeed(double speedMultiplier) {
        Configuration configure = new Configuration();
        configure.setSpeedMultiplier(speedMultiplier);
        agent.getAct().act(configure);
    }

    /**
     * Sets the rotation speed (rotation rate) for the bot. Default rotation
     * rate can be set in GameBots INI file in UT2004/System directory ( look
     * for DefaultRotationRate attribute). Default rotation rate is now
     * Pitch=3072, Yaw=60000, Roll=2048 (pitch = up/down, yaw = left/right, roll
     * = equivalent of doing a cartwheel).
     *
     * (issues GB CONF command)
     *
     * @param newRotationRate Default is Pitch=3072, Yaw=60000, Roll=2048. To
     * achieve best results we suggest to multiply the default setting.
     *
     * @see setSpeed(double)
     */
    public void setRotationSpeed(Rotation newRotationRate) {
        Configuration configure = new Configuration();
        configure.setRotationRate(newRotationRate);
        agent.getAct().act(configure);
    }

    /**
     * Constructor. Setups the command module based on given agent and logger.
     *
     * @param agent AbstractUT2004Bot we will send commands for
     * @param log Logger to be used for logging runtime/debug info.
     */
    public AdvancedLocomotion(UT2004Bot agent, Logger log) {
        super(agent, log);
        this.selfListener = new SelfListener(agent.getWorldView()); //register self listener
    }

    @Override
    public void jump() {
        super.jump();
    }

    /**
     * Makes the bot to jump instantly (issues GB JUMP command) with custom
     * settings. <p><p> See also {@link SimpleLocomotion#jump()}.
     *
     * @param jumpZ Force vector specifying how big the jump should be. Can't be
     * set more than JumpZ = 340 for single jump.
     *
     * @see jump()
     * @see dodge(Vector3d)
     */
    public void jump(double jumpZ) {
        Jump jump = new Jump();
        jump.setForce(jumpZ);
        agent.getAct().act(jump);
    }

    /**
     * Makes the bot to jump (or double jump) instantly (issues GB JUMP command)
     * with custom settings. <p><p> See also {@link SimpleLocomotion#jump()}.
     *
     * @param doubleJump whether to perform double jump
     * @param secondJumpDelay After time specified here, the bot performs second
     * jump of a double jump (if DoubleJump is true). Time is in seconds. GB2004
     * default is 0.5s.
     * @param jumpZ Force vector specifying how big the jump should be. Can't be
     * set more than 2 * JumpZ = 680 for double jump.
     *
     * @see jump()
     * @see dodge(Vector3d)
     */
    public void jump(boolean doubleJump, double secondJumpDelay, double jumpZ) {
        Jump jump = new Jump();
        jump.setDoubleJump(doubleJump);
        jump.setDelay(secondJumpDelay);
        jump.setForce(jumpZ);
        agent.getAct().act(jump);
    }

    @Override
    public void moveTo(ILocated location) {
        super.moveTo(location);
    }

    @Override
    public void setRun() {
        super.setRun();
    }

    @Override
    public void setWalk() {
        super.setWalk();
    }

    @Override
    public void stopMovement() {
        super.stopMovement();
    }

    @Override
    public void turnHorizontal(int amount) {
        super.turnHorizontal(amount);
    }

    @Override
    public void turnTo(ILocated location) {
        super.turnTo(location);
    }

    @Override
    public void turnTo(Player player) {
        super.turnTo(player);
    }

    @Override
    public void turnTo(Item item) {
        super.turnTo(item);
    }

    @Override
    public void turnVertical(int amount) {
        super.turnVertical(amount);
    }
}