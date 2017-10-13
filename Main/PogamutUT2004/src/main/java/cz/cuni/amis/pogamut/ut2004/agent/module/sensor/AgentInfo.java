package cz.cuni.amis.pogamut.ut2004.agent.module.sensor;

import java.util.logging.Level;
import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.agent.module.SensorModule;
import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.base.utils.math.DistanceUtils;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.base3d.worldview.object.Rotation;
import cz.cuni.amis.pogamut.base3d.worldview.object.Velocity;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.Weaponry;
import cz.cuni.amis.pogamut.ut2004.bot.IUT2004BotController;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotName;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BotKilled;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerKilled;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerScore;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Self;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Spawn;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.VolumeChanged;
import cz.cuni.amis.utils.NullCheck;

/**
 * Memory module specialized on general info about the agent whereabouts.
 * <p><p>
 * It is designed to be initialized inside {@link IUT2004BotController#prepareBot(UT2004Bot)} method call
 * and may be used since first {@link Self} message is received, i.e, since the first {@link IUT2004BotController#botFirstSpawn(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, ConfigChange, InitedMessage, Self)} 
 * is called.
 *
 * @author Juraj 'Loque' Simlovic
 * @author Jimmy
 */
public abstract class AgentInfo extends SensorModule<UT2004Bot> implements ILocated
{
	public static final String NONE_WEAPON_ID = "None";
		
	/**
	 * Retreives a unique ID of the agent in the game.
	 *
	 * <p>Note: This ID does not change and can be relied upon during entire
	 * match. However, be aware that the ID may change between different matches
	 * and/or sessions.
	 *
	 * @return ID of the agent in the game.
	 */
	public UnrealId getId()
	{
		// retreive from self object
    if (self == null) return null;
    if (self.getBotId() != null) return self.getBotId();
		return self.getId();
	}

	/**
	 * Retreives current name of the agent in the game.
	 *
	 * <p>Note: The agent may choose and change it's name during a match and it
	 * does not need to be unique among players. Even an empty string might be
	 * a valid name.
	 *
	 * @return Name of the agent in the game.
	 */
	public String getName()
	{
		// retreive from self object
        if (self == null) return null;
		return self.getName();
	}
	
	/**
	 * Returns name abstraction that allows to dynamicly append/delete tags after the name, thus easing debugging by providing simple option
	 * of bot-related-information visualization directly within the name of the bot.
	 * 
	 * @return
	 */
	public UT2004BotName getBotName() {
		return agent.getBotName(); 
	}

	/*========================================================================*/

	/** Red team number. */
	public static final int TEAM_RED = 0;
	/** Blue team number. */
	public static final int TEAM_BLUE = 1;
	/** Green team number. */
	public static final int TEAM_GREEN = 2;
	/** Gold team number. */
	public static final int TEAM_GOLD = 3;
	/** No-team number. */
	public static final int TEAM_NONE = 255;

	/**
	 * Retreives team number the agent is on.
	 *
	 * @return Team number the player is on.
	 *
	 * @see #TEAM_RED
	 * @see #TEAM_BLUE
	 * @see #TEAM_GREEN
	 * @see #TEAM_GOLD
	 * @see #TEAM_NONE
	 *
	 * @see isEnemy(int)
	 * @see isEnemy(Player)
	 * @see isFriend(int)
	 * @see isFriend(Player)
	 */
	public Integer getTeam()
	{
		// retreive from self object
                if (self == null) return null;
		return self.getTeam();
	}

	/**
	 * Tells, whether a given team is an enemy team to the agent.
	 *
	 * @param team Team number to be tested.
	 * @return True, if the given team is an enemy team.
	 *
	 * @see getTeam()
	 * @see isFriend(int)
	 */
	public boolean isEnemy(int team)
	{
		// freelancers' team or different team
		return (team == TEAM_NONE) || (team != getTeam());
	}

	/**
	 * Tells, whether a given player is an enemy to the agent.
	 *
	 * @param player Player to be tested.
	 * @return True, if the given player is an enemy.
	 *
	 * @see getTeam()
	 * @see isFriend(Player)
	 */
	public boolean isEnemy(Player player)
	{
		// test the enemy team number
		return isEnemy(player.getTeam());
	}

	/**
	 * Tells, whether a given team is a friend team to the agent.
	 *
	 * @param team Team number to be tested.
	 * @return True, if the given team is a friend team.
	 *
	 * @see getTeam()
	 * @see isEnemy(int)
	 */
	public boolean isFriend(int team)
	{
		// same team only
		return (team == getTeam());
	}

	/**
	 * Tells, whether a given player is a friend to the agent.
	 *
	 * @param player Player to be tested.
	 * @return True, if the given player is a friend.
	 *
	 * @see getTeam()
	 * @see isEnemy(Player)
	 */
	public boolean isFriend(Player player)
	{
		// test the friend team number
		return isFriend(player.getTeam());
	}

	/*========================================================================*/

	/**
	 * Which distance to a location is considered the same as specified location. Note
	 * that UT units are rather small.
	 */
	public static final double AT_LOCATION_EPSILON = 100;

    /**
     * What angle is considered to be maximum facing angle by default (in degrees).
     */
    public static final double IS_FACING_ANGLE = 6;
    
    /**
     * EXACT at location (in UT units).
     */
	public static final double CLOSE_ENOUGH_EPSILON = 50;

	/**
	 * Retreives absolute location of the agent within the map.
	 *
	 * @return Location of the agent within the map.
	 *
	 * @see getDistance(Location)
	 * @see Location#getDistance(Location)
	 * @see Location#getDistanceL1(Location)
	 * @see Location#getDistanceLinf(Location)
	 * @see Location#getDistancePlane(Location)
	 * @see Location#getDistanceSquare(Location)
	 */
	@Override
	public Location getLocation()
	{
		// retreive from self object
                if (self == null) return null;
		return self.getLocation();
	}
	
	/**
	 * Tells whether the bot is at navpoint/item/... (anything {@link ILocated}) of id 'objectId'. Note that IDs are case sensitive! UT2004 is usually using camel-case.
	 * @param objectId
	 * @return
	 */
	public boolean atLocation(String objectId) {
		Object obj = agent.getWorldView().get(UnrealId.get(objectId));
		if (obj == null) {
			GameInfo info = worldView.getSingle(GameInfo.class);
			if (info != null) {
				obj = agent.getWorldView().get(UnrealId.get(info.getLevel() + "." + objectId));
			}
			if (info == null) {
				if (log != null && log.isLoggable(Level.WARNING)) log.warning("atLocation(): Object with id '" + objectId + "' does not exist in the worldview!"); 
				return false;
			}
		}
		if (!(obj instanceof ILocated)) {
			if (log != null && log.isLoggable(Level.WARNING)) log.warning("atLocation(): Object with id '" + objectId + "' is not implementing ILocated, it is " + obj.getClass().getSimpleName() + ".");
			return false;
		}
		return atLocation((ILocated)obj);
	}
	
	/**
	 * Tells whether the bot is at navpoint/item/... (anything {@link ILocated}) of id 'objectId'. Note that IDs are case sensitive! UT2004 is usually using camel-case.
	 * @param objectId
	 * @param epsilon
	 * @return
	 */
	public boolean atLocation(String objectId, double epsilon) {
		Object obj = agent.getWorldView().get(UnrealId.get(objectId));
		if (obj == null) {
			if (log != null && log.isLoggable(Level.WARNING)) log.warning("atLocation(): Object with id '" + objectId + "' does not exist in the worldview!"); 
			return false;
		}
		if (!(obj instanceof ILocated)) {
			if (log != null && log.isLoggable(Level.WARNING)) log.warning("atLocation(): Object with id '" + objectId + "' is not implementing ILocated, it is " + obj.getClass().getSimpleName() + ".");
			return false;
		}
		return atLocation((ILocated)obj, epsilon);
	}
	
	/**
	 * Returns whether the bot is at 'location'.
	 * <p><p>
	 * Synonym for {@link AgentInfo#atLocation(Location)}.
	 * 
	 * @return bot is at lcoation
	 */
	public boolean isAtLocation(ILocated location) {
		return atLocation(location);
	}
	
	/**
	 * Returns whether the bot is at 'location' using 'epsilon'.
	 * <p><p>
	 * Synonym for {@link AgentInfo#atLocation(Location)}.
	 * 
	 * @return bot is at lcoation
	 * @param epsilon
	 */
	public boolean isAtLocation(ILocated location, double epsilon) {
		return atLocation(location, epsilon);
	}

	/**
	 * Returns whether the bot is at 'location', using {@link AgentInfo#AT_LOCATION_EPSILON}.
	 * 
	 * @return bot is at lcoation
	 */
	public boolean atLocation(ILocated location) {		
		return atLocation(location, AT_LOCATION_EPSILON);
	}

	/**
	 * Returns whether the bot is at 'location', using 'epsilon' as a distance tolerance
	 * @param location
	 * @param epsilon
	 * @return bot is at lcoation with desired epsilon tolerance
	 */
	public boolean atLocation(ILocated location, double epsilon) {
		if (location == null || getLocation() == null) return false;
		return getLocation().getPoint3d().distance(location.getLocation().getPoint3d()) < epsilon;
	}

	/**
	 * Computes crow-fly distance of the agent from given location.
	 *
	 * @param location Location within the map.
	 * @return Crow-fly distance of the agent and the location.
	 *
	 * @see getLocation()
	 */
	public Double getDistance(ILocated location)
	{
		// retreive from self object
                if (self == null) return null;
                if (location == null) return null;
		return self.getLocation().getDistance(location.getLocation());
	}

	/*========================================================================*/

	/**
	 * Retreives absolute rotation of the agent within the map.
	 *
	 * @return Rotation of the agent within the map.
	 */
	public Rotation getRotation()
	{
		// retreive from self object
                if (self == null) return null;
		return self.getRotation();
	}
	
	/**
	 * Retreives absolute rotation of the agent within the map.
	 *
	 * @return Rotation of the agent within the map.
	 */
	public Rotation getHorizontalRotation()
	{
		// retreive from self object
        if (self == null) return null;
        Rotation rot = new Rotation(0, self.getRotation().getYaw(), 0);
        return rot;
	}

	/*========================================================================*/

	/**
	 * Retreives current velocity of the agent as a vector of movement.
	 *
	 * @return Current velocity of the agent in the map.
	 *
	 * @see isMoving()
	 */
	public Velocity getVelocity()
	{
		// retreive from self object
                if (self == null) return null;
		return self.getVelocity();
	}

	/**
	 * Tells, whether the agent is moving. The agent is moving, when his
	 * actual velocity is non-zero.
	 *
	 * @return True, if the agent is moving.
	 *
	 * @see getVelocity()
	 */
	public Boolean isMoving()
	{
		// check the size of the velocity
                if (getVelocity() == null) return null;
		return !getVelocity().isZero();
	}

	/*========================================================================*/

	/**
	 * Tells, whether the agent is crouched. When crouched, the height of the
	 * agent is smaller and thus harder to spot/hit.
	 *
	 * @return True, if the agent is crouched.
	 */
	public Boolean isCrouched()
	{
		// retreive from self object
                if (self == null) return null;
		return self.isCrouched();
	}

	/**
	 * Tells, whether the agent is walking. When walking, the agent does not
	 * fall off the edges before notification about such edge can be sent to
	 * the agent. The agent's movement is, however, much slower.
	 *
	 * @return True, if the agent is walking.
	 */
	public Boolean isWalking()
	{
		// retreive from self object
                if (self == null) return null;
		return self.isWalking();
	}
        /*========================================================================*/

	/**
	 * Tells if the agent is currently facing input location.
         *
         * @param location input location.
	 * @return True, if the bot is facing input location.
	 */
	public Boolean isFacing(ILocated location)
	{
                if (location == null || getRotation() == null) return null;
                Location directionVector = location.getLocation().sub(this.getLocation()).getNormalized();
                Location agentFaceVector = this.getRotation().toLocation().getNormalized();

                if (Math.acos(directionVector.dot(agentFaceVector)) <= Math.toRadians(IS_FACING_ANGLE))
                    return true;

                return false;
	}


	/**
	 * Tells if the agent is currently facing input location.
         *
         * @param location input location.
         * @param angle specifies maximum angle (in degrees) that will be still considered as facing angle.
	 * @return True, if the angle between agent facing vector and input location is smaller or equal to input angle.
	 */
	public Boolean isFacing(ILocated location, double angle)
	{
                if (location == null || getRotation() == null) return null;
                Location directionVector = location.getLocation().sub(this.getLocation()).getNormalized();
                Location agentFaceVector = this.getRotation().toLocation().getNormalized();

                if (Math.acos(directionVector.dot(agentFaceVector)) <= Math.toRadians(angle))
                    return true;

                return false;
	}

	/*========================================================================*/

	/**
	 * Retreives location of the nearest map geometry directly beneath the
	 * agent. This can be used to determine how far the agent is above the
	 * ground, etc.
	 *
	 * @return Location of <i>the ground</i> beneath the agent.
	 */
	public Location getFloorLocation()
	{
                if (self == null) return null;
		// retreive from self object
		return self.getFloorLocation();
	}

	/**
	 * Tells, whether the agent is currently touching the groud with his feets.
	 * When not touching ground, the agent might be either jumping, or falling,
	 * or hanging from a ledge, or otherwise flying above the ground.
	 *
	 * @return True, if the agent is touching ground with his feets.
	 */
	public Boolean isTouchingGround()
	{
		// compare locations of agent and floor (beware of being crouched)
		// FIXME[jimmy]: Test the values..
                if (getLocation() == null || getFloorLocation() == null) return null;
		return (getLocation().z - getFloorLocation().z)
			   < (isCrouched() ? 50 : 80);
	}

	/*========================================================================*/

	/**
	 * Tells whether the agent has the damage multiplier (UDamage) bonus boost
	 * activated and how long will the UDamage boost remain active.
	 *
	 * <p>When UDamage is activated, the agent is  causing double (or tripple,
	 * or even more) damage to other players. The multiplying factor depends
	 * on game settings and mutators.
	 *
	 * @return Time remaining for UDamage bonus boost. When this value is
	 * positive, the agent has the UDamage bonus boost currently activated.
	 * When this value is negative, the agent does not have UDamage activated.
	 *
	 * @see hasUDamage()
	 */
	public Double getRemainingUDamageTime()
	{
		// calculate remaining time by substracting current time
                if (self == null) return null;
		return self.getUDamageTime() - getTime();
	}

	/**
	 * Tells whether the agent has the damage multiplier (UDamage) bonus boost
	 * activated.
	 *
	 * <p>When UDamage is activated, the agent is  causing double (or tripple,
	 * or even more) damage to other players. The multiplying factor depends
	 * on game settings and mutators.
	 *
	 * @return True, if the agent has damage multiplier bonus action activated.
	 *
	 * @see getRemainingUDamageTime()
	 */
	public Boolean hasUDamage()
	{
        if (getRemainingUDamageTime() == null) return null;
		// is there any remaining time?
		return getRemainingUDamageTime() > 0;
	}

	/*========================================================================*/

	/**
	 * Tells, whether the agent has special bonus action activated: the
	 * invisibility. When invisibility is activated, the agent is almost
	 * invisible to other players. When moving, outline of the agent is
	 * glittering a bit. When standing still, he is very hard to spot.
	 *
	 * <p>To learn, for how long the bonus action will remain activated, check
	 * the remaining amount of adrenaline. When level of adrenaline reaches
	 * zero, the bonus action is deactivated. See {@link getAdrenaline()}.
	 *
	 * @return True, if the agent has invisibility bonus action activated.
	 *
	 * @see getAdrenaline()
	 */
	public Boolean hasInvisibility()
	{
		// check with the self object
                if (self == null) return null;
		return self.getCombo().equals("xGame.ComboInvis");
	}

	/**
	 * Tells, whether the agent has special bonus action activated: the
	 * fast firing rate. When fast firing rate is activated, the agent is
	 * firing his weapon at a faster rate, eating more ammo, but launching
	 * more projectiles into air.
	 *
	 * <p>To learn, for how long the bonus action will remain activated, check
	 * the remaining amount of adrenaline. When level of adrenaline reaches
	 * zero, the bonus action is deactivated. See {@link getAdrenaline()}.
	 *
	 * @return True, if the agent has fast firing rate bonus action activated.
	 *
	 * @see getAdrenaline()
	 */
	public Boolean hasFastFire()
	{
		// check with the self object
                if (self == null) return null;
		return self.getCombo().equals("xGame.ComboBerserk");
	}

	/**
	 * Tells, whether the agent has special bonus action activated: the
	 * regenration, which is also called booster. When booster is activated,
	 * the agent regenerates health slowly. Note: The agent's health never
	 * rises above the maximum health level.
	 *
	 * <p>To learn, for how long the bonus action will remain activated, check
	 * the remaining amount of adrenaline. When level of adrenaline reaches
	 * zero, the bonus action is deactivated. See {@link getAdrenaline()}.
	 *
	 * @return True, if the agent has regenration bonus action activated.
	 *
	 * @see getAdrenaline()
	 */
	public Boolean hasRegeneration()
	{
		// check with the self object
                if (self == null) return null;
		return self.getCombo().equals("xGame.ComboDefensive");
	}

	/**
	 * Tells, whether the agent has special bonus action activated: the
	 * speed. When speed is activated, the agent can move much faster than
	 * other players. Note: Firing rate does not change with speed.
	 *
	 * <p>To learn, for how long the bonus action will remain activated, check
	 * the remaining amount of adrenaline. When level of adrenaline reaches
	 * zero, the bonus action is deactivated. See {@link getAdrenaline()}.
	 *
	 * @return True, if the agent has speed bonus action activated.
	 *
	 * @see getAdrenaline()
	 */
	public Boolean hasSpeed()
	{
		// check with the self object
                if (self == null) return null;
		return self.getCombo().equals("xGame.ComboSpeed");
	}

	/*========================================================================*/

	/**
	 * Tells, how much health the agent has.
	 *
	 * <p>The health usually starts at 100, and ranges from 0 to 199. These
	 * values, however, can be changed by various mutators.
	 *
	 * @return Current health status.
	 *
	 * @see isHealthy()
	 * @see isSuperHealthy()
	 */
	public Integer getHealth()
	{
		// retreive from self object
                if (self == null) return null;
		return self.getHealth();
	}

	/**
	 * Tells, whether the agent is healthy, i.e. not wounded.
	 *
	 * @return True, if the agent has at least standard amount of health.
	 *
	 * @see getHealth()
	 * @see isSuperHealthy()
	 */
	public Boolean isHealthy()
	{
		// compare self object and game info
                if (getHealth() == null || game == null) return null;
		return (getHealth() >= game.getFullHealth());
	}

	/**
	 * Tells, whether the agent is healthy to the maximum boostable extent.
	 *
	 * @return True, if the agent has maximum amount of health.
	 *
	 * @see getHealth()
	 * @see isHealthy()
	 */
	public Boolean isSuperHealthy()
	{
		// compare self object and game info
                if (getHealth() == null || game == null) return null;
		return (getHealth() >= game.getMaxHealth());
	}

	/*========================================================================*/

	/**
	 * Tells, how much of combined armor the agent is wearing.
	 *
	 * <p>The combined armor usually starts at 0, and ranges from 0 to 150.
	 * These values, however, can be changed by various mutators.
	 *
	 * <p>Note: The armor consist of two parts, which are summed together into
	 * combined armor value. However, each part is powered-up by different item
	 * (low armor by <i>small shield</i>; high armor by <i>super-shield</i>).
	 *
	 * @return Current armor status.
	 *
	 * @see hasArmor()
	 * @see getLowArmor()
	 * @see getHighArmor()
	 */
	public Integer getArmor()
	{
		// retreive from self object
                if (self == null) return null;
		return self.getArmor();
	}

	/**
	 * Tells, whether the agent is armored to the maximum extent.
	 *
	 * @return True, if the agent has maximum amount of armor.
	 *
	 * @see getArmor()
	 * @see hasLowArmor()
	 * @see hasHighArmor()
	 */
	public Boolean hasArmor()
	{
		// compare self object and game info
                if (getArmor() == null) return null;
		return (getArmor() >= game.getMaxArmor());
	}

	/**
	 * Tells, how much of low armor the agent is wearing.
	 *
	 * <p>The low armor usually starts at 0, and ranges from 0 to 50.
	 * These values, however, can be changed by various mutators.
	 *
	 * <p>Note: The armor consist of two parts, which are summed together into
	 * combined armor value. However, each part is powered-up by different item
	 * (low armor by <i>small shield</i>; high armor by <i>super-shield</i>).
	 *
	 * @return Current low armor status.
	 *
	 * @see hasLowArmor()
	 * @see getArmor()
	 * @see getHighArmor()
	 */
	public Integer getLowArmor()
	{
		// retreive from self object
                if (self == null) return null;
		return self.getSmallArmor();
	}

	/**
	 * Tells, whether the agent is armored to the maximum of low-armor extent.
	 *
	 * @return True, if the agent has maximum amount of low-armor.
	 *
	 * @see getLowArmor()
	 * @see hasArmor()
	 * @see hasHighArmor()
	 */
	public Boolean hasLowArmor()
	{
		// compare self object and game info
                if (getLowArmor() == null || game == null) return null;
		return (getLowArmor() >= game.getMaxLowArmor());
	}

	/**
	 * Tells, how much of high armor the agent is wearing.
	 *
	 * <p>The high armor usually starts at 0, and ranges from 0 to 100.
	 * These values, however, can be changed by various mutators.
	 *
	 * <p>Note: The armor consist of two parts, which are summed together into
	 * combined armor value. However, each part is powered-up by different item
	 * (low armor by <i>small shield</i>; high armor by <i>super-shield</i>).
	 *
	 * @return Current high armor status.
	 *
	 * @see hasHighArmor()
	 * @see getArmor()
	 * @see getLowArmor()
	 */
	public Integer getHighArmor()
	{
		// calculate from armor and small armor in self object
                if (self == null) return null;
		return self.getArmor() - self.getSmallArmor();
	}

	/**
	 * Tells, whether the agent is armored to the maximum of high-armor extent.
	 *
	 * @return True, if the agent has maximum amount of high-armor.
	 *
	 * @see getHighArmor()
	 * @see hasArmor()
	 * @see hasLowArmor()
	 */
	public Boolean hasHighArmor()
	{
		// compare self object and game info
                if (getHighArmor() == null) return null;
		return (getHighArmor() >= game.getMaxHighArmor());
	}

	/*========================================================================*/

	/**
	 * Tells, how much adrenaline the agent has.
	 *
	 * <p>Adrenaline can be gained through fulfilling various game tasks, such
	 * as killing opponents, capturing flags, controlling domination points,
	 * picking up adrenaline pills, etc. Note: More adrenaline is gained when
	 * the agent fulfill these tasks in combos (e.g. by scoring a double-kill
	 * the agent receives significantly more adrenaline than by scoring two
	 * single-kills).
	 *
	 * <p>Once the adrenaline reaches a designated level, it can be used to
	 * start special bonus actions like <i>booster</i>, <i>invisibility</i>,
	 * <i>speed</i>, etc. The adrenaline is then spent on the action. The more
	 * adrenaline the agent has, the longer the action lasts. Note: The agent
	 * may gain new adrenaline during the bonus action, which prolongs the
	 * action duration. See {@link isAdrenalineFull() } to determine, when the
	 * necessary adrenaline level is reached.
	 *
	 * <p>The adrenaline usually starts at 0, and ranges from 0 to 100. These
	 * values, however, can be changed by various mutators.
	 *
	 * @return Current armor status.
	 *
	 * @see isAdrenalineFull()
	 */
	public Integer getAdrenaline()
	{
		// retreive from self object
                if (self == null) return null;
		return self.getAdrenaline();
	}

	/**
	 * Tells, whether the agent gained enough adrenaline to use it for special
	 * adrenaline-based action, e.g. <i>booster</i>, <i>invisibility</i>, etc.
	 *
	 * @return True, if the adrenaline level is high enough for bonus action.
	 *
	 * @see getAdrenaline()
	 */
	public Boolean isAdrenalineSufficient()
	{
		// compare self object and game info
                if (getAdrenaline() == null) return null;
		return (getAdrenaline() >= game.getTargetAdrenaline());
	}

	/**
	 * Tells, whether the agent has full adrenaline.
	 *
	 * @return True, if the adrenaline level is at maximum.
	 *
	 * @see getAdrenaline()
	 */
	public Boolean isAdrenalineFull()
	{
		// compare self object and game info
                if (getAdrenaline() == null) return null;
		return (getAdrenaline() >= game.getMaxAdrenaline());
	}

	/*========================================================================*/

	/**
	 * Retreives UnrealId of the weapon the agent is currently holding. This
	 * UnrealId is a unique identifier of weapon from the agent's inventory.
	 * Note that this UnrealId is different from UnrealId of item the agent
	 * seen or picked up from the ground earlier.
	 *
	 * <p>The UnrealId might contains a substring, which identifies the type
	 * of the weapon. However, this is not guaranteed by definition. Therefore,
	 * you shoud use inventory to retreive the appropriate weapon object, to
	 * further retreive correct type of weapon.
	 *
	 * @return UnrealId of the weapon the agent is currently holding in hands.
	 *
	 * @see getCurrentAmmo()
	 * @see getCurrentSecondaryAmmo()
	 * @see Weaponry#getCurrentWeapon()
	 * @see Weaponry#getWeapon(UnrealId)
	 */
	public UnrealId getCurrentWeapon()
	{
		// retreive from self object
        if (self == null) return null;
		return UnrealId.get(self.getWeapon());
	}
	
	/**
	 * Returns name of the currently wielded weapon (or null if no such weapon exists). 
	 * 
	 * @see getCurrentWeapon
	 * @return
	 */
	public String getCurrentWeaponName() {
		if (getCurrentWeapon() == null) return null;
		return self.getWeapon().substring(self.getWeapon().indexOf(".")+1);
	}
	
	/**
	 * Returns type of the weapon the agent is currently holding (or null if no such weapon exists).
	 * @return
	 */
	public abstract ItemType getCurrentWeaponType() ;
	
	/**
	 * Tells whether the bot is holding some weapon or not.
	 * <p><p>
	 * Note that {@link AgentInfo#getCurrentWeapon()} always returns some id. But there is a special id that marks 'no weapon'
	 * @return
	 */
	public Boolean hasWeapon() {
                if (self == null) return null;
		return !self.getWeapon().equalsIgnoreCase(NONE_WEAPON_ID);	
	}

	/**
	 * Tells, how much ammunition the agent has left for the current weapon
	 * in its primary firing mode.
	 *
	 * @return Amount of ammunition for the primary firing mode.
	 *
	 * @see getCurrentSecondaryAmmo()
	 * @see Inventory#getCurrentPrimaryAmmo()
	 */
	public Integer getCurrentAmmo()
	{
		// retreive from self object
                if (self == null) return null;
		return self.getPrimaryAmmo();
	}

	/**
	 * Tells, how much ammunition the agent has left for the current weapon
	 * in its alternate (secondary) firing mode. Note that many weapons use primary ammo
	 * for the alternate (secondary) firing mode as well. In such cases, the amount of
	 * ammo for primary mode is returned.
	 *
	 * @return Amount of ammunition for the secondary firing mode.
	 *
	 * @see getCurrentAmmo()
	 * @see Inventory#getCurrentSecondaryAmmo()
	 */
	public Integer getCurrentSecondaryAmmo()
	{
		// retreive from self object
                if (self == null) return null;
		return self.getSecondaryAmmo();
	}

	/*========================================================================*/

	/**
	 * Tells, whether the agent is shooting or not.
	 *
	 * <p>This method reports shooting with either primary or secondary fire
	 * mode. To distinguish between the fire modes, see {@link isPriShooting()},
	 * {@link isAltShooting()}.
	 *
	 * @return Returns true, if the agent is shooting his weapon.
	 *
	 * @see isPrimaryShooting()
	 * @see isSecondaryShooting()
	 */
	public Boolean isShooting()
	{
		// retreive from self object
                if (self == null) return null;
		return self.isShooting();
	}

	/**
	 * Tells, whether the agent is shooting with primary fire mode.
	 *
	 * <p>This method reports shooting with primary fire mode only. See
	 * {@link isAltShooting()} method to determine, whether the agent shoots
	 * with alternate firing mode. See {@link isShooting()} to determine,
	 * whether the agent shoots with either primary or alternate firing mode.
	 *
	 * @return True, if the agent is shooting weapon in primary firing mode.
	 *
	 * @see isShooting()
	 * @see isSecondaryShooting()
	 */
	public Boolean isPrimaryShooting()
	{
		// shooting but not in altrenate fire mode
                if (self == null) return null;
		return isShooting() && !isSecondaryShooting();
	}

	/**
	 * Tells, whether the agent is shooting with alternate (secondary) fire mode.
	 *
	 * <p>This method reports shooting with alternate (secondary) fire mode only. See
	 * {@link isPriShooting()} method to determine, whether the agent shoots
	 * with primary firing mode. See {@link isShooting()} to determine,
	 * whether the agent shoots with either primary or alternate (secondary) firing mode.
	 *
	 * @return True, if the agent is shooting his weapon in alternate (secondary) firing
	 * mode.
	 *
	 * @see isShooting()
	 * @see isPrimaryShooting()
	 */
	public Boolean isSecondaryShooting()
	{
		// retreive from self object
        if (self == null) return null;
		return self.isAltFiring();
	}

	/*========================================================================*/

	/**
	 * Retreives number of kills the agent scored.
	 *
	 * <p>A kill is counted, whenever the agent kills an opponent.
	 *
	 * @return Number of kills the agent scored.
	 */
	public int getKills()
	{
		// returns number of kills, counted in PlayerKilledListener
		return kills;
	}

	/**
	 * Retreives number of deaths the agent took.
	 *
	 * <p>A death is counted, whenever the agent dies.
	 * 
	 * <p><p>
	 * Note that if {@link Integer#MIN_VALUE} is returned, than the number of deaths is unknown. This happens only
	 * if you call this method before the first logic iteration of the agent (i.e., before first {@link PlayerScore} messages
	 * are exported by GameBots).
	 *
	 * @return Number of deaths the agent took.
	 */
	public int getDeaths()
	{
		if (self == null) return Integer.MIN_VALUE;
		// retreive from PlayerScore object
		return game.getPlayerDeaths(getId());
	}

	/**
	 * Retreives number of suicides the agent commited.
	 *
	 * <p>A suicide is counted, whenever the agent dies by his own weapon, or
	 * by damaging himself by falling into pits, lava, acid, etc.
	 *
	 * <p>It can also be said that suicide is every agent's death, which could
	 * not be credited to any other player in the map.
	 *
	 * <p>Each suicide is also counted as death. See {@link getDeaths()}.
	 *
	 * @return Number of suicides the agent commited.
	 */
	public int getSuicides()
	{
		// returns number of suicides, counted in BotKilledListener
		return suicides;
	}

	/**
	 * Retreives current agent score.
	 *
	 * <p>Agent score is usually rising by achieving some goals, e.g. killing
	 * opponents, capturing flags, controlling domination points, etc. Note:
	 * Agent score might decrease upon suicides, based on map, game type and
	 * game settings.
	 * 
	 * <p><p>
	 * Note that if {@link Integer#MIN_VALUE} is returned, than the score is unknown. This happens only
	 * if you call this method before the first logic iteration of the agent (i.e., before first {@link PlayerScore} messages
	 * are exported by GameBots).
	 *
	 * @return Current agent score.
	 */
	public int getScore()
	{
		if (self == null) return Integer.MIN_VALUE;
		return game.getPlayerScore(getId());
	}

	/**
	 * Retreives current agent's team score.
	 *
	 * <p>Agent's team score is usually rising by achieving team goals, e.g.
	 * killing opponents, capturing flags, controlling domination points, etc.
	 * Note: Agent's team score might decrease, when oposing teams score points
	 * themselves, based on map, game type and game settings.
	 *
	 * @return Current agent's team score.
	 */
	public int getTeamScore()
	{
		// retreive from TeamScore object
		return game.getTeamScore(getTeam());
	}

	/*========================================================================*/

	/**
	 * Pulling velocity in this map zone. Such pulling velocity effectively
	 * draws the player towards a specific direction or even lifts him upwards.
	 *
	 * @return Pulling velocity in this zone.
	 */
	public Velocity getCurrentZoneVelocity()
	{
		// retreive from VolumeChanged object
                if (lastVolumeChanged == null) return null;
		return lastVolumeChanged.getZoneVelocity();
	}

	/**
	 * Gravity in this map zone. Gravity might differ throughout different
	 * parts of the map. The gravity is expressed as a velocity vector. This
	 * vector is used an acceleration. The fall speed may ramp up, to as much
	 * as {@link getFallSpeed()}.
	 *
	 * @return Gravity in this zone.
	 */
	public Velocity getCurrentZoneGravity()
	{
		// retreive from VolumeChanged object
                if (lastVolumeChanged == null) return null;
		return lastVolumeChanged.getZoneGravity();
	}

	/**
	 * Friction of the floor in this map volume. Friction of the floor works
	 * towards movement, slowing down the acceleration and speed of the agent
	 * in any direction.
	 *
	 * @return Friction of the floor.
	 */
	public Double getCurrentVolumeGroundFriction()
	{
		// retreive from VolumeChanged object
                if (lastVolumeChanged == null) return null;
		return lastVolumeChanged.getGroundFriction();
	}

	/**
	 * Friction of the fluid in this map volume. Friction of the fluid works
	 * towards movement, slowing down the acceleration and speed of the agent
	 * in any direction.
	 *
	 * @return Friction of the fluid.
	 */
	public Double getCurrentVolumeFluidFriction()
	{
		// retreive from VolumeChanged object
                if (lastVolumeChanged == null) return null;
		return lastVolumeChanged.getFluidFriction();
	}

	/**
	 * FIXME[js]: What the hell is this good for?
	 *
	 * @return TerminalVelocity of the CurrentVolume.
	 */
	public Double _getCurrentVolumeTerminalVelocity()
	{
		// retreive from VolumeChanged object
                if (lastVolumeChanged == null) return null;
		return lastVolumeChanged.getTerminalVelocity();
	}

	/**
	 * Tells, whether the current volume is water. When the agent is in water,
	 * {@link getCurrentVolumeFluidFriction()} and {@link getWaterSpeed()} can
	 * help to determine changes to movement and speed of the agent. Also note
	 * that {@link getCurrentZoneVelocity()}, {@link getCurrentZoneGravity()},
	 * and others may change (and usually does) in water.
	 *
	 * @return True, if the current volume is water.
	 */
	public Boolean isCurrentVolumeWater()
	{
		// retreive from VolumeChanged object
                if (lastVolumeChanged == null) return null;
		return lastVolumeChanged.isWaterVolume();
	}

	/**
	 * Tells, whether the current volume is causing damage. Such damage is
	 * applied to the agent's health every second. The amount of damage taken
	 * per each second spent in this volume can be determined by
	 * {@link getCurrentVolumeDamagePerSec()}. When the volume damages the
	 * agent to the death, the death is counted as a suicide.
	 *
	 * @return True, if the current volume is causing damage.
	 *
	 * @see isCurrentVolumeDestructive()
	 * @see getCurrentVolumeDamagePerSec()
	 */
	public Boolean isCurrentVolumePainCausing()
	{
		// retreive from VolumeChanged object
                if (lastVolumeChanged == null) return null;
		return lastVolumeChanged.isPainCausing();
	}

	/**
	 * Amount of damage taken for spending time in the current volume. Such
	 * damage is applied to the agent's health every second. When the volume
	 * damages the agent to the death, the death is counted as a suicide.
	 *
	 * @return Amount of damage taken for spending time in the current volume.
	 *
	 * @see isCurrentVolumePainCausing()
	 */
	public Double getCurrentVolumeDamagePerSec()
	{
		// retreive from VolumeChanged object
                if (lastVolumeChanged == null) return null;
		return lastVolumeChanged.getDamagePerSec();
	}

	/**
	 * Tells, whether the current volume kills the actors (almost) instantly.
	 * Death in such destructive volume is counted as a suicide.
	 *
	 * @return True, if the current volume kills (almost) instantly.
	 *
	 * @see isCurrentVolumePainCausing()
	 */
	public Boolean isCurrentVolumeDestructive()
	{
		// retreive from VolumeChanged object
                if (lastVolumeChanged == null) return null;
		return lastVolumeChanged.isDestructive();
	}

	/**
	 * Retreives type of damage the current volume inflicts to the agent while
	 * he spends time in this volume.
	 *
	 * <p>FIXME[js]: Is is possible to provide an enum here?
	 *
	 * @return Type of the damage the current volume inflicts to the agent.
	 */
	public String getCurrentVolumeDamageType()
	{
		// retreive from VolumeChanged object
                if (lastVolumeChanged == null) return null;
		return lastVolumeChanged.getDamageType();
	}

	/**
	 * Tells, whether the current volume (the one the agent is within) forbids
	 * usage of the inventory. If so, no weapons or items can be used, changed,
	 * or picked up.
	 *
	 * @return True, if the current volume forbids usage of the inventory.
	 */
	public Boolean isCurrentVolumeBanningInventory()
	{
		// retreive from VolumeChanged object
                if (lastVolumeChanged == null) return null;
		return lastVolumeChanged.isNoInventory();
	}

	/**
	 * Tells, whether the current volume imparts its velocity to projectiles.
	 * E.g. A volume might impart velocity to players to emulate <i>wind</i>.
	 * This settings tells, whether the same applies to projectiles. If so,
	 * Their trajectory will be affected by this volume velocity.
	 *
	 * @return True, if the current volume imparts its velocity to projectiles.
	 */
	public Boolean isCurrentVolumeAffectingProjectiles()
	{
		// retreive from VolumeChanged object
                if (lastVolumeChanged == null) return null;
		return lastVolumeChanged.isMoveProjectiles();
	}

	/**
	 * Tells, whether the current zone is a neutral zone. In neutral zone,
	 * players can't take damage.
	 *
	 * @return True, if the current zone is a neutral zone.
	 */
	public Boolean isCurrentZoneNeutral()
	{
		// retreive from VolumeChanged object
                if (lastVolumeChanged == null) return null;
		return lastVolumeChanged.isNeutralZone();
	}

	/*========================================================================*/

	/**
	 * Retreives scaling factor for damage dealt by the agent. All damage
	 * dealt by the agent is reduced (or increased) by this value.
	 *
	 * @return Scaling factor for damage dealt by the agent.
	 */
	public Double getDamageScaling()
	{
		// retreive from InitedMessage object
                if (lastInitedMessage == null) return null;
		return lastInitedMessage.getDamageScaling();
	}

	/*========================================================================*/

	/**
	 * Retreives maximum base speed of the agent.
	 *
	 * @return Maximum base speed of the agent.
	 */
	public Double getBaseSpeed()
	{
		// retreive from InitedMessage object
                if (lastInitedMessage == null) return null;
		return lastInitedMessage.getGroundSpeed();
	}

	/**
	 * Retreives maximum speed of the agent while moving in the air.
	 *
	 * @return Maximum speed of the agent while moving in the air.
	 */
	public Double getAirSpeed()
	{
		// retreive from InitedMessage object
                if (lastInitedMessage == null) return null;
		return lastInitedMessage.getAirSpeed();
	}

	/**
	 * Retreives maximum speed of the agent while moving on a ladder.
	 *
	 * @return Maximum speed of the agent while moving on a ladder.
	 */
	public Double getLadderSpeed()
	{
		// retreive from InitedMessage object
                if (lastInitedMessage == null) return null;
		return lastInitedMessage.getLadderSpeed();
	}

	/**
	 * Retreives maximum speed of the agent while moving in water.
	 *
	 * @return Maximum speed of the agent while moving in water.
	 */
	public Double getWaterSpeed()
	{
		// retreive from InitedMessage object
                if (lastInitedMessage == null) return null;
		return lastInitedMessage.getWaterSpeed();
	}

	/**
	 * Retreives maximum speed of the agent while falling.
	 *
	 * @return Maximum speed of the agent while falling.
	 */
	public Double getFallSpeed()
	{
		// retreive from InitedMessage object
                if (lastInitedMessage == null) return null;
		return lastInitedMessage.getMaxFallSpeed();
	}

	/**
	 * Retreives maximum speed of the agent while using dodge.
	 *
	 * <p>FIXME[js]: Check about the name depending on the meaning/value.
	 *
	 * @return Maximum speed of the agent while using dodge.
	 */
	public Double getDodgeSpeedFactor()
	{
		// retreive from InitedMessage object
                if (lastInitedMessage == null) return null;
		return lastInitedMessage.getDodgeSpeedFactor();
	}

	/**
	 * Retreives acceleration rate of the agent.
	 *
	 * @return Acceleration rate of the agent.
	 */
	public Double getAccelerationRate()
	{
		// retreive from InitedMessage object
                if (lastInitedMessage == null) return null;
		return lastInitedMessage.getAccelRate();
	}

	/**
	 * Retreives agent's control of movement while in the air.
	 * This value ranges from 0 (none) to 1 (full control).
	 *
	 * @return Agent's control of movement while in the air.
	 */
	public Double getAirControl()
	{
		// retreive from InitedMessage object
                if (lastInitedMessage == null) return null;
		return lastInitedMessage.getAirControl();
	}

	/**
	 * Retreives boost of the agent in the Z axis while jumping.
	 *
	 * @return Jumping boost of the agent in the Z axis.
	 */
	public Double getJumpZBoost()
	{
		// retreive from InitedMessage object
                if (lastInitedMessage == null) return null;
		return lastInitedMessage.getJumpZ();
	}

	/**
	 * Retreives boost of the agent in the Z axis while using dodge.
	 *
	 * @return Dodge boost of the agent in the Z axis.
	 */
	public Double getDodgeZBoost()
	{
		// retreive from InitedMessage object
                if (lastInitedMessage == null) return null;
		return lastInitedMessage.getDodgeSpeedZ();
	}

	/*========================================================================*/

	/**
	 * Retreives current game time, since the game started.
	 *
	 * @return Current game timestamp. IN SECONDS!
	 */
	public double getTime()
	{
        if (game == null) return 0;
		return game.getTime();
	}
	
	/**
	 * Retrieves time-delta since the last batch update.
	 * 
	 * @return time-delta IN SECONDS
	 */
	public double getTimeDelta() {
		if (game == null) return 0;
		return game.getTimeDelta();
	}

	/*========================================================================*/
	
	/**
	 * Retrieves the configuration of the bot inside UT2004.
	 * @return Configuration of the bot.
	 */
	public ConfigChange getConfig() {
		return lastConfig;
	}
	
	/*========================================================================*/
	
	
	/**
	 * Retrieves nearest known navpoint to current agent location.
	 * <p><p>
	 * WARNING: O(n) complexity.
	 * 
	 * @return nearest navpoint
	 */
	public NavPoint getNearestNavPoint() {
		if (getLocation() == null) return null;
		return DistanceUtils.getNearest(agent.getWorldView().getAll(NavPoint.class).values(), getLocation());
	}
	
	/**
	 * Retrieves nearest known navpoint to current agent location that is not further than "maxDistance".
	 * <p><p>
	 * WARNING: O(n) complexity.
	 * 
	 * @return nearest navpoint
	 */
	public NavPoint getNearestNavPoint(double maxDistance) {
		if (getLocation() == null) return null;
		return DistanceUtils.getNearest(agent.getWorldView().getAll(NavPoint.class).values(), getLocation(), maxDistance);
	}
	
	/**
	 * Retrieve nearest known navpoint to some location.
	 * @param location
	 * @return
	 */
	public NavPoint getNearestNavPoint(ILocated location) {
		if (location == null) return null;
    	if (location instanceof NavPoint) return (NavPoint)location;
    	if (location instanceof Item) {
    		if (((Item)location).getNavPoint() != null) return ((Item)location).getNavPoint();
    	}
    	return DistanceUtils.getNearest(agent.getWorldView().getAll(NavPoint.class).values(), location);        
    }
	

	/**
	 * Retrieve nearest known navpoint to some location, that is not further then "maxDistance".
	 * @param location
	 * @return
	 */
	public NavPoint getNearestNavPoint(ILocated location, double maxDistance) {
		if (location == null) return null;
    	if (location instanceof NavPoint) return (NavPoint)location;
    	if (location instanceof Item) {
    		if (((Item)location).getNavPoint() != null) return ((Item)location).getNavPoint();
    	}
    	return DistanceUtils.getNearest(agent.getWorldView().getAll(NavPoint.class).values(), location, maxDistance);        
    }
	
	/**
	 * Check whether the bot is on navigation graph, more precisly, near some navigation point.
	 * <p><p>
	 * WARNING: O(n) complexity.
	 * 
	 * @return
	 */
	public boolean isOnNavGraph() {
		if (getLocation() == null) {
			log.warning("AgentInfo.getLocation() is NULL!");
			return false;
		}
		return getLocation().getDistance(getNearestNavPoint().getLocation()) < CLOSE_ENOUGH_EPSILON;
	}
	
	/**
	 * Retrieves nearest visible navpoint to current agent location.
	 * <p><p>
	 * WARNING: O(n) complexity.
	 * 
	 * @return nearest visible navpoint
	 */
	public NavPoint getNearestVisibleNavPoint() {
                if (getLocation() == null) return null;
		return DistanceUtils.getNearestVisible(agent.getWorldView().getAll(NavPoint.class).values(), getLocation());
	}
	
	/**
	 * Retrieves nearest known item to current agent location.
	 * <p><p>
	 * WARNING: O(n) complexity.
	 * 
	 * @return nearest item
	 */
	public Item getNearestItem() {
                if (getLocation() == null) return null;
		return DistanceUtils.getNearest(agent.getWorldView().getAll(Item.class).values(), getLocation());
	}
	
	/**
	 * Retrieves nearest visible item to current agent location.
	 * <p><p>
	 * WARNING: O(n) complexity.
	 * 
	 * @return nearest visible item
	 */
	public Item getNearestVisibleItem() {
                if (getLocation() == null) return null;
		return DistanceUtils.getNearestVisible(agent.getWorldView().getAll(Item.class).values(), getLocation());
	}
	
	/**
	 * Retrieves nearest known player to current agent location.
	 * <p><p>
	 * WARNING: O(n) complexity.
	 * 
	 * @return nearest player
	 */
	public Player getNearestPlayer() {
                if (getLocation() == null) return null;
		return DistanceUtils.getNearest(agent.getWorldView().getAll(Player.class).values(), getLocation());
	}
	
	/**
	 * Retrieves nearest visible player to current agent location.
	 * <p><p>
	 * WARNING: O(n) complexity.
	 * 
	 * @return nearest visible player
	 */
	public Player getNearestVisiblePlayer() {
        if (getLocation() == null) return null;
		return DistanceUtils.getNearestVisible(agent.getWorldView().getAll(Player.class).values(), getLocation());
	}
	
	/*========================================================================*/
	
	/**
	 * Whether the bot is spawned (present within the environment).
	 */
	private boolean spawned = false;
	
	/**
	 * Whether we have already reaceived first {@link Self} message after the spawn.
	 */
	private boolean selfAfterSpawned = false;
	
	/**
	 * Whether the bot is spawned, its body is within the environment and the bot has uptodate {@link Self} information.
	 * @return
	 */
	public boolean isSpawned() {
		return spawned && selfAfterSpawned;
	}

	
	/*========================================================================*/
	
	/** Most rescent message containing info about the agent. */
	Self self = null;
	
	/** How many suicides the bot commited. */
	int suicides = 0;
	
	/** How many player we have killed so far. */
	int kills = 0;
	
	/** Most rescent message containing info about the game frame. */
	InitedMessage lastInitedMessage = null;

	/** Most rescent message containing info about the volume the player is in. */
	VolumeChanged lastVolumeChanged = null;
	
	/** Configuration of the bot inside UT2004. */
	ConfigChange lastConfig = null;

	/*========================================================================*/
	
	/**
	 * {@link BotKilled} listener counting the number of suicides.
	 */
	private class BotKilledListener implements IWorldEventListener<BotKilled> {

		public BotKilledListener(IWorldView worldView) {
			worldView.addEventListener(BotKilled.class, this);
		}
		
		@Override
		public void notify(BotKilled event) {
			if (self == null) return;
			// TODO: [jimmy] is it correct?
			if (event.getKiller() == null || event.getKiller().equals(getId())) {
				++suicides;
			}
			
			spawned = false;
			selfAfterSpawned = false;
		}
		
	}
	
	/** {@link BotKilled} listener. */
	BotKilledListener botKilledListener;
	
	/*========================================================================*/
	
	private class PlayerKilledListener implements IWorldEventListener<PlayerKilled> {

		public PlayerKilledListener(IWorldView worldView) {
			worldView.addEventListener(PlayerKilled.class, this);
		}
		
		@Override
		public void notify(PlayerKilled event) {
			if (self == null) return;
			if (event.getKiller() != null && event.getKiller().equals(getId())) {
				++kills;
			}		
		}
		
	}
	
	/** {@link PlayerKilled} listener. */
	PlayerKilledListener playerKilledListener;
	
	/*========================================================================*/

	/**
	 * InitedMessage listener.
	 */
	private class InitedMessageListener implements IWorldObjectEventListener<InitedMessage, WorldObjectUpdatedEvent<InitedMessage>>
	{
		@Override
		public void notify(WorldObjectUpdatedEvent<InitedMessage> event)
		{
			lastInitedMessage = event.getObject();
			spawned = false;
			selfAfterSpawned = false;
		}

		/**
		 * Constructor. Registers itself on the given WorldView object.
		 * @param worldView WorldView object to listent to.
		 */
		public InitedMessageListener(IWorldView worldView)
		{
			worldView.addObjectListener(InitedMessage.class, WorldObjectUpdatedEvent.class, this);
		}
	}

	/** InitedMessage listener */
	private InitedMessageListener initedMessageListener;

	/*========================================================================*/

	/**
	 * VolumeChanged listener.
	 */
	private class VolumeChangedListener implements IWorldEventListener<VolumeChanged>
	{
		@Override
		public void notify(VolumeChanged event)
		{
			lastVolumeChanged = event;
		}

		/**
		 * Constructor. Registers itself on the given WorldView object.
		 * @param worldView WorldView object to listent to.
		 */
		public VolumeChangedListener(IWorldView worldView)
		{
			worldView.addEventListener(VolumeChanged.class, this);
		}
	}

	/** VolumeChanged listener */
	private VolumeChangedListener volumeChangedListener;

	/*========================================================================*/

	/**
	 * {@link Self} listener.
	 */
	private class SelfListener implements IWorldObjectEventListener<Self, WorldObjectUpdatedEvent<Self>>
	{
		private IWorldView worldView;

		/**
		 * Constructor. Registers itself on the given WorldView object.
		 * @param worldView WorldView object to listent to.
		 */
		public SelfListener(IWorldView worldView)
		{
			this.worldView = worldView;
			worldView.addObjectListener(Self.class, WorldObjectUpdatedEvent.class, this);
		}

		@Override
		public void notify(WorldObjectUpdatedEvent<Self> event) {
			self = event.getObject();			
			spawned = true;
			selfAfterSpawned = true;
		}
	}

	/** {@link Self} listener */
	private SelfListener selfListener;


	/*========================================================================*/
	
	/**
	 * {@link ConfigChange} listener.
	 */
	private class ConfigChangeListener implements IWorldObjectEventListener<ConfigChange, IWorldObjectEvent<ConfigChange>>
	{
		private IWorldView worldView;

		/**
		 * Constructor. Registers itself on the given WorldView object.
		 * @param worldView WorldView object to listent to.
		 */
		public ConfigChangeListener(IWorldView worldView)
		{
			worldView.addObjectListener(ConfigChange.class, WorldObjectUpdatedEvent.class, this);
			this.worldView = worldView;
		}

		@Override
		public void notify(IWorldObjectEvent<ConfigChange> event) {
			lastConfig = event.getObject();			
		}
	}

	/** {@link ConfigChange} listener */
	private ConfigChangeListener configChangeListener;
	
	/*========================================================================*/
	
	/**
	 * {@link Spawn} listener.
	 */
	private class SpawnListener implements IWorldEventListener<Spawn>
	{
		private IWorldView worldView;

		/**
		 * Constructor. Registers itself on the given WorldView object.
		 * @param worldView WorldView object to listent to.
		 */
		public SpawnListener(IWorldView worldView)
		{
			
			worldView.addEventListener(Spawn.class, this);
			this.worldView = worldView;
		}

		@Override
		public void notify(Spawn event) {
			spawned = true;			
			selfAfterSpawned = false;
		}
	}

	/** {@link Spawn} listener */
	private SpawnListener spawnListener;


	/*========================================================================*/

	public Self getSelf() {
		return self;
	}

	/** Game memory module. */
	public Game game;
	
	/**
	 * Constructor. Setups the memory module based on bot's world view.
	 * @param game game info module
	 */
	public AgentInfo(UT2004Bot bot) {
		this(bot, new Game(bot), null);
	}
	
	/**
	 * Constructor. Setups the memory module based on bot's world view.
	 * @param bot owner of the module
	 * @param game game info module
	 */
	public AgentInfo(UT2004Bot bot, Game game) {
		this(bot, game, null);
	}

	/**
	 * Constructor. Setups the memory module based on bot's world view.
	 * @param bot owner of the module
	 * @param game game info module
	 * @param log Logger to be used for logging runtime/debug info. Note: If <i>null</i> is provided,
	 * this memory module creates it's own logger.
	 */
	public AgentInfo(UT2004Bot bot, Game game, Logger log)
	{
		super(bot, log);

		this.game = game;
		NullCheck.check(this.game, "game");
		
		// create listeners
		selfListener          = new SelfListener(worldView);
		initedMessageListener = new InitedMessageListener(worldView);	
		volumeChangedListener = new VolumeChangedListener(worldView);
		botKilledListener     = new BotKilledListener(worldView);
		playerKilledListener  = new PlayerKilledListener(worldView);
		configChangeListener  = new ConfigChangeListener(worldView);
		spawnListener         = new SpawnListener(worldView);
		
		cleanUp();
	}
	
	@Override
	protected void cleanUp() {
		super.cleanUp();
		self = null;
		suicides = 0;
		kills = 0;
		lastInitedMessage = null;
		lastVolumeChanged = null;
		lastConfig = null;
	}

}
