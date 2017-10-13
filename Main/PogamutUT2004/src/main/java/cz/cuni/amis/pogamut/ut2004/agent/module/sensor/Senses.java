package cz.cuni.amis.pogamut.ut2004.agent.module.sensor;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.vecmath.Vector3d;

import cz.cuni.amis.pogamut.base.agent.module.SensorModule;
import cz.cuni.amis.pogamut.base.communication.messages.InfoMessage;
import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.base3d.worldview.object.Rotation;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.bot.IUT2004BotController;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.AdrenalineGained;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BeginMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BotDamaged;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BotKilled;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Bumped;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.FallEdge;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.HearNoise;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.HearPickup;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.IncomingProjectile;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ItemPickedUp;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerDamaged;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerKilled;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.WallCollision;
import cz.cuni.amis.utils.NullCheck;

/**
 * Memory module specialized on agent's senses.
 * <p><p>
 * <b>THIS MODULE IS BUGGY AS HELL! You can expect its source code to see what classes / functions it uses, BUT PLEASE, DO NOT USE IT!</b>
 * <p><p>
 * This module hooks up a LOT OF LISTENERS and provide you with lot of methods to query current state of bot's sensors.
 * <p><p>
 * There are two types of methods:
 * <ol>
 * <li>general - providing sensors 500ms back to the history</li>
 * <li>once - providing sensors 500ms back to the history + queriable only once (first call may return true, next will report false until another sense arrive),
 *     this allows you to create simple if-then rules that won't fire twice.</li>
 * </ol>
 * <p><p>
 * If you are missing some methods that you think should be incorporated to the module, 
 * post to <a href=http://diana.ms.mff.cuni.cz/main/tiki-forums.php">Pogamut 3 forum</a> and
 * we may then discuss the implementation.
 * 
 * <p><p>
 * It is designed to be initialized inside {@link IUT2004BotController#prepareBot(UT2004Bot)} method call
 * and may be used since {@link IUT2004BotController#botInitialized(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage)}
 * is called.
 *
 * @author Jimmy
 */
public class Senses extends SensorModule<UT2004Bot>
{
	
	/**
	 * Specifies amount of time after which the sense (event) is considered to be invalid/old/discarded.
	 */
	public static final double SENSE_THRESHOLD = 0.5;
	
	/**
	 * Tells, whether the agent is colliding with map geometry.
	 *
	 * <p><b>Note: This method clears the collision flag upon invocation.</b>
	 * This is to prevent taking more action because of one collision.
	 *
	 * @return True, if the agent is colliding with map geometry.
	 */
	public boolean isCollidingOnce()
	{
		if (!lastWallCollisionFlag) return false;
		// TODO: [jimmy] what about the constant? is it OK?
		boolean col = agentInfo.getLocation().getDistance(lastWallCollision.getLocation()) < 100;
		lastWallCollisionFlag = false;
		return col;
	}
	
	/**
	 * Tells, whether the agent is colliding with map geometry.
	 *
	 * @return True, if the agent is colliding with map geometry.
	 */
	public boolean isColliding()
	{
		if (lastWallCollision == null) return false;
		// TODO: [jimmy] what about the constant? is it OK?
		return agentInfo.getTime() - lastWallCollisionTime < SENSE_THRESHOLD && 
		       agentInfo.getLocation().getDistance(lastWallCollision.getLocation()) < 100;
	}
	
	/**
	 * Tells where the agent has collided.
	 * @return location of the last collision
	 */
	public Location getCollisionLocation() {
		if (lastWallCollision == null) return null;
		return lastWallCollision.getLocation();
	}

	/**
	 * Tells the normal of last agent's collision.
	 * @return normal vector of the triangle the bot has collided with
	 */
	public Vector3d getCollisionNormal() {
		if (lastWallCollision == null) return null;
		return lastWallCollision.getNormal();
	}

	/*========================================================================*/

	/**
	 * Tells whether the bot is bumping another player/other map geometry.
	 * @return True, if the bot bumped another player/other map geometry.
	 */
	public boolean isBumping ()
	{
		if (lastBumped == null) return false;
		return agentInfo.getTime() - lastBumpedTime < SENSE_THRESHOLD && agentInfo.getLocation().getDistance(lastBumped.getLocation()) < 100;
	}
	
	/**
	 * Tells, whether the agent is bumping with another player/other map geometry.
	 *
	 * <p><b>Note: This method clears the bumping flag upon invocation.</b>
	 * This is to prevent taking more action because of one bumping.
	 *
	 * @return True, if the agent is bumping another player/other map geometry.
	 */
	public boolean isBumpingOnce() {
		if (!lastBumpedFlag) return false;
		boolean result = isBumping();
		lastBumpedFlag = false;
		return result;
	}
	
	/**
	 * Tells whether the bot is bumping another player (bot or human).
	 */
	public boolean isBumpingPlayer() {
		if (!isBumping()) return false;
		return players.getPlayer(lastBumped.getId()) != null;
	}
	
	/**
	 * Tells whether the bot is bumping another player (bot or human).
	 * 
	 * <p><b>Note: This method clears the bumping flag upon invocation.</b>
	 * This is to prevent taking more action because of one collision.
	 * 
	 * @return True, if the agent is bumping to a player
	 */
	public boolean isBumpingPlayerOnce() {
		if (!lastBumpedFlag) return false;
		boolean result = isBumpingPlayer();
		lastBumpedFlag = false;
		return result;
	}
	
	/**
	 * Returns tha {@link Player} object of the player the bot has bumped into (if it was a bot).
	 * @return player the bot has bumped into (if it bumped the player)
	 */
	public Player getBumpingPlayer() {
		if (lastBumped == null) return null;
		return players.getPlayer(lastBumped.getId());
	}
	
	/**
	 * Returns location where bumping occurred.
	 * @return location where the bumping has occurred
	 */
	public Location getBumpLocation() {
		if (lastBumped == null) return null;
		return lastBumped.getLocation();
	}

	/*========================================================================*/

	/**
	 * Tells whether the bot has just fall of the ledge
	 * @return whether the bot has just fall of the ledge
	 */
	public boolean isFallEdge()
	{
		if (lastFallEdge == null) return false;
		// TODO: [jimmy] is the constant 100 ok?
		return agentInfo.getTime() - lastFallEdgeTime < SENSE_THRESHOLD && agentInfo.getLocation().getDistance(lastFallEdge.getLocation()) < 100;
	}
	
	/**
	 * Tells whether the bot has just fall of the ledge
	 * 
	 * <p><b>Note: This method clears the fall-edge flag upon invocation.</b>
	 * This is to prevent taking more action because of one fall.
	 * 
	 * @return whether the bot has just fall of the ledge
	 */
	public boolean isFallEdgeOnce()
	{
		if (!lastFallEdgeFlag) return false;
		boolean result = isFallEdge();
		lastFallEdgeFlag = false;
		return result;
	}

	/*========================================================================*/

	/**
	 * Tells whether the bot is hearing noise.
	 * @return True, if the bot is hearing noise.
	 */
	public boolean isHearingNoise()
	{
		if (lastHearNoise == null) return false;
		return agentInfo.getTime() - lastHearNoiseTime < SENSE_THRESHOLD;
	}
	
	/**
	 * Tells whether the bot is hearing noise.
	 * 
	 * <p><b>Note: This method clears the hearing-noise flag upon invocation.</b>
	 * This is to prevent taking more action because of hearing the noise.
	 * 
	 * @return True, if the bot is hearing noise.
	 */
	public boolean isHearingNoiseOnce() {
		if (!lastHearNoiseFlag) return false;
		boolean result = isHearingNoise();
		lastHearNoiseFlag = false;
		return result;
	}
	
	/**
	 * Tells where the noise is coming from.
	 * @return way where the noise has happened  
	 */
	public Rotation getNoiseRotation() {
		if (lastHearNoise == null) return null;
		return lastHearNoise.getRotation();
	}
	
	/**
	 * Tells what has caused a noise (may be null).
	 * @return what has caused a noise
	 */
	public UnrealId getNoiseSource() {
		if (lastHearNoise == null) return null;
		return lastHearNoise.getSource();
	}
	
	/**
	 * Tells what type the noise is.
	 * @return noise type
	 */
	public String getNoiseType() {
		// TODO: is possible enum?
		if (lastHearNoise == null) return null;
		return lastHearNoise.getType();
	}

	/*========================================================================*/

	/**
	 * Tells whether the bot is hearing pickup.
	 * @return True, if the bot is hearing pickup.
	 */
	public boolean isHearingPickup()
	{
		if (lastHearPickup == null) return false;
		return agentInfo.getTime() - lastHearPickupTime < SENSE_THRESHOLD;
	}
	
	/**
	 * Tells whether the bot is hearing pickup.
	 * 
	 * <p><b>Note: This method clears the hearing-pickup flag upon invocation.</b>
	 * This is to prevent taking more action because of hearing the pickup.
	 * 
	 * @return True, if the bot is hearing pickup.
	 */
	public boolean isHearingPickupOnce() {
		if (!lastHearPickupFlag) return false;
		boolean result = isHearingPickup();
		lastHearPickupFlag = false;
		return result;
	}
	
	/**
	 * Tells where the pickup noise is coming from.
	 * @return way where the noise has happened  
	 */
	public Rotation getPickupNoiseRotation() {
		if (lastHearPickup == null) return null;
		return lastHearPickup.getRotation();
	}
	
	/**
	 * Tells what has caused the pickup noise (may be null).
	 * @return what has caused a noise
	 */
	public UnrealId getPickupNoiseSource() {
		if (lastHearPickup == null) return null;
		return lastHearPickup.getSource();
	}
	
	/**
	 * Tells what type the pickup noise is.
	 * @return noise type
	 */
	public String getPickupNoiseType() {
		// TODO: is possible enum?
		if (lastHearPickup == null) return null;
		return lastHearPickup.getType();
	}

	/*========================================================================*/

	/**
	 * Tells, whether the agent is being damaged.
	 *
	 * @return True, if the agent is being damaged.
	 */
	public boolean isBeingDamaged ()
	{
		if (lastBotDamaged == null) return false;
		return agentInfo.getTime() - lastBotDamagedTime < SENSE_THRESHOLD; 
	}
	
	/**
	 * Tells, whether the agent is being damaged.
	 * 
	 * <p><b>Note: This method clears the being-damaged flag upon invocation.</b>
	 * This is to prevent taking more action because of taking the damage.
	 *
	 * @return True, if the agent is being damaged.
	 */
	public boolean isBeingDamagedOnce ()
	{
		if (!isBeingDamaged()) return false;
		if (!lastBotDamagedFlag) return false;
		lastBotDamagedFlag = false;
		return true; 
	}
	
	/**
	 * Returns the description of the last damage done to the bot.
	 * @return
	 */
	public BotDamaged getLastDamage() {
		return lastBotDamaged;
	}
	
	/*========================================================================*/
	
	/**
	 * Tells, whether the agent is being damaged by another player (i.e. was shot).
	 *
	 * @return True, if the agent is being damaged.
	 */
	public boolean isShot() {
		if (lastBotShot == null) return false;
		return agentInfo.getTime() - lastBotShotTime < SENSE_THRESHOLD;
	}
	
	/**
	 * Tells, whether the agent is being damaged by another player (i.e. was shot).
	 * 
	 * <p><b>Note: This method clears the shot flag upon invocation.</b>
	 * This is to prevent taking more action because of taking the damage.
	 *
	 * @return True, if the agent is being damaged.
	 */
	public boolean isShotOnce() {
		if (!isShot()) return false;
		if (!lastBotShotFlag) return false;
		lastBotShotFlag = false;
		return true; 
	}
	
	/**
	 * Returns the description of the last shot that has hit the bot.
	 * @return last shot the bot has taken
	 */
	public BotDamaged getLastShot() {
		return lastBotShot;
	}
	
	// TODO: we probably need to provide better handling of different BotDamaged events (i.e., when you are hurt
	//       by acid + being shot by two different bots you see)

	/*========================================================================*/

	/**
	 * Tells whether the bot see any incoming projectiles.
	 * @return whether the bot see any incoming projectile
	 */
	public boolean seeIncomingProjectile ()
	{
		if (lastIncomingProjectile == null) return false;
		return agentInfo.getTime() - lastIncomingProjectileTime < SENSE_THRESHOLD;
	}
	
	/**
	 * Tells whether the bot see any incoming projectiles.
	 * @return whether the bot see any incoming projectile
	 */
	public boolean seeIncomingProjectileOnce() {
		if (!seeIncomingProjectile()) return false;
		if (!lastIncomingProjectileFlag) return false;
		lastIncomingProjectileFlag = false;
		return true;
	}
	
	/**
	 * Provides access to the last incoming-projectile object.
	 * @return incoming projectile object
	 */
	public IncomingProjectile getLastIncomingProjectile() {
		return lastIncomingProjectile;
	}
	
	//TODO: more advanced methods such as "give me a direction to run to to avoid the missile"
	//      or "time to impact"
	//TODO: proper handling of multiple incoming projectiles is needed

	/*========================================================================*/

	/**
	 * Tells, whether the agent is causing any damage.
	 *
	 * @return True, if the agent is causing any damage.
	 */
	public boolean isCausingDamage ()
	{
		if (lastPlayerDamaged == null) return false;
		return agentInfo.getTime() - lastPlayerDamagedTime < SENSE_THRESHOLD; 
	}
	
	/**
	 * Tells, whether the agent is causing any damage.
	 * 
	 * <p><b>Note: This method clears the causing-damage flag upon invocation.</b>
	 * This is to prevent taking more action because of causing the damage.
	 *
	 * @return True, if the agent is being damaged.
	 */
	public boolean isCausingDamageOnce ()
	{
		if (!isCausingDamage()) return false;
		if (!lastPlayerDamagedFlag) return false;
		lastPlayerDamagedFlag = false;
		return true; 
	}
	
	/**
	 * Returns the description of the last damage caused by the bot.
	 * @return
	 */
	public PlayerDamaged getLastCausedDamage() {
		return lastPlayerDamaged;
	}
	
	/*========================================================================*/
	
	/**
	 * Tells, whether the agent hit another player (i.e.. shot it).
	 *
	 * @return True, if the agent shot another player.
	 */
	public boolean isHitPlayer() {
		if (lastPlayerShot == null) return false;
		return agentInfo.getTime() - lastPlayerShotTime < SENSE_THRESHOLD;
	}
	
	/**
	 * Tells, whether the agent hit another player (i.e.. shot it).
	 * 
	 * <p><b>Note: This method clears the player-hit flag upon invocation.</b>
	 * This is to prevent taking more action because of hitting a player.
	 *
	 * @return True, if the agent hit another player.
	 */
	public boolean isHitPlayerOnce() {
		if (!isHitPlayer()) return false;
		if (!lastPlayerShotFlag) return false;
		lastPlayerShotFlag = false;
		return true; 
	}
	
	/**
	 * Returns the description of the last hit of another player.
	 * @return last hit the bot has dealt
	 */
	public PlayerDamaged getLastHitPlayer() {
		return lastPlayerShot;
	}
	
	// TODO: we probably need to provide better handling of different PlayerDamaged events 
	//       preferably categorizing them according to Ids of different players

	/*========================================================================*/
	
	/**
	 * Tells whether some other player has just died (we do not care which one).
	 * @return True, if some other player has just died.
	 */
	public boolean isPlayerKilled() {
		for (UnrealId id : playerKilled.keySet()) {
			if (isPlayerKilled(id)) return true;
		}
		return false;
	}
	
	/**
	 * Tells whether some other player has just died (we do not care which one).
	 * 
	 * <p><b>Note: This method clears the arbitrary-player-killed flag upon invocation.</b>
	 * This is to prevent taking more action because of killing a player.
	 * 
	 * @return True, if some other player has just died.
	 */
	public boolean isPlayerKilledOnce() {
		if (!playerKilledGlobalFlag) return false;
		if (!isPlayerKilled()) return false;
		playerKilledGlobalFlag = false;
		return true;
	}
	
	/**
	 * Tells whether some other player of id 'playerId' has just died.
	 * @return True, the player of id 'playerId' has just died.
	 */
	public boolean isPlayerKilled(UnrealId playerId) {
		return playerKilled.get(playerId) != null && agentInfo.getTime() - playerKilled.get(playerId).time < SENSE_THRESHOLD;
	}
	
	/**
	 * Tells whether some other player has just died.
	 * @return True, the player  has just died.
	 */
	public boolean isPlayerKilled(Player player) {
		return isPlayerKilled(player.getId());
	}
	
	/**
	 * Tells whether some other player of id 'playerId' has just died.
	 * 
	 * <p><b>Note: This method clears the specific-player-killed flag upon invocation.</b>
	 * This is to prevent taking more action because of killing a player.
	 * 
	 * @param playerId
	 * @return True, the player of id 'playerId' has just died.
	 */
	public boolean isPlayerKilledOnce(UnrealId playerId) {
		if (!isPlayerKilled(playerId)) return false;
		if (playerKilled.get(playerId).queried) return false;
		playerKilled.get(playerId).queried = true;
		return true;
	}
	
	/**
	 * Tells whether some other player has just died.
	 * 
	 * <p><b>Note: This method clears the specific-player-killed flag upon invocation.</b>
	 * This is to prevent taking more action because of killing a player.
	 * 
	 * @param player
	 * @return True, the player  has just died.
	 */
	public boolean isPlayerKilledOnce(Player player) {
		return isPlayerKilledOnce(player.getId());
	}
	
	/**
	 * Returns detail information about the way the player of id 'playerId' has died.
	 * @param playerId
	 * @return detail info about player's death
	 */
	public PlayerKilled getPlayerKilled(UnrealId playerId) {
		if (playerKilled.get(playerId) == null) return null;
		return playerKilled.get(playerId).event;
	}
	
	/**
	 * Returns detail information about the way the player has died.
	 * @param player
	 * @return detail info about player's death
	 */
	public PlayerKilled getPlayerKilled(Player player) {
		return getPlayerKilled(player.getId());
	}
	
	/*========================================================================*/
	
	/**
	 * Tells whether the bot has recently got an adrenaline.
	 * @return whether the bot has recently got any adrenaline
	 */
	public boolean isAdrenalineGained() {
		return lastAdrenalineGained != null && agentInfo.getTime() - lastAdrenalineGainedTime < SENSE_THRESHOLD;
	}
	
	/**
	 * Tells whether the bot has recently got an adrenaline.
	 * 
	 * <p><b>Note: This method clears the adrenaline-gained flag upon invocation.</b>
	 * This is to prevent taking more action because of adrenaline gain.
	 * 
	 * @return whether the bot has recently got any adrenaline
	 */
	public boolean isAdrenalineGainedOnce() {
		if (!lastAdrenalineGainedFlag) return false;
		if (!isAdrenalineGained()) return false;
		lastAdrenalineGainedFlag = false;
		return true;
	}
	
	/*========================================================================*/
	
	/**
	 * Tells whether this bot has recently died.
	 * @return whether the bot has recently died.
	 */
	public boolean hasDied() {
		return lastBotKilled != null && agentInfo.getTime() - lastBotKilledTime < SENSE_THRESHOLD;
	}
	
	/**
	 * Tells whether this bot has recently died.
	 * 
	 * <p><b>Note: This method clears the bot-died flag upon invocation.</b>
	 * This is to prevent taking more action because of bot death.
	 * 
	 * @return whether the bot has recently got any adrenaline
	 */
	public boolean hasDiedOnce() {
		if (!lastBotKilledFlag) return false;
		if (!hasDied()) return false;
		lastBotKilledFlag = false;
		return true;
	}
	
	/**
	 * Provides information about the way this bot has died.
	 * @return info about last bot death
	 */
	public BotKilled getBotDeath() {
		return lastBotKilled;
	}
	
	/*========================================================================*/
	
	/**
	 * Tells whether this bot has picked up some item recently.
	 * @return whether the has picked up an item recently
	 */
	public boolean isItemPickedUp() {
		return lastItemPickedUp != null && agentInfo.getTime() - lastItemPickedUpTime < SENSE_THRESHOLD;
	}
	
	/**
	 * Tells whether this bot has picked up some item recently.
	 * 
	 * <p><b>Note: This method clears the pick-up flag upon invocation.</b>
	 * This is to prevent taking more action because of item pickup.
	 * 
	 * @return whether the bot has recently got any adrenaline
	 */
	public boolean isItemPickedUpOnce() {
		if (!lastItemPickedUpFlag) return false;
		if (!isItemPickedUp()) return false;
		lastItemPickedUpFlag = false;
		return true;
	}
	
	/**
	 * Provides information about the last item the bot has picked up.
	 * @return last picked item
	 */
	public ItemPickedUp getItemPickedUp() {
		return lastItemPickedUp;
	}
	
	/**
	 * Returns UT2004 time delta. Note that first logic tick it returns NULL as it does not have enough info to work with.
	 * @return
	 */
	public Double getTimeDelta() {
		if (previousBeginMessage == null) return null;
		return lastBeginMessage.getTime() - previousBeginMessage.getTime();
	}
	
	/*========================================================================*/
	
	Bumped lastBumped = null;
	double lastBumpedTime = -1;
	boolean lastBumpedFlag = false;
	
	WallCollision lastWallCollision = null;
	double lastWallCollisionTime = -1;
	boolean lastWallCollisionFlag = false;
	
	FallEdge lastFallEdge = null;
	double lastFallEdgeTime = -1;
	boolean lastFallEdgeFlag = false;
	
	HearNoise lastHearNoise = null;
	double lastHearNoiseTime = -1;
	boolean lastHearNoiseFlag = false;

	HearPickup lastHearPickup = null;
	double lastHearPickupTime = -1;
	boolean lastHearPickupFlag = false;

	BotDamaged lastBotDamaged = null;
	double lastBotDamagedTime = -1;
	boolean lastBotDamagedFlag = false;
	
	BotDamaged lastBotShot = null;
	double lastBotShotTime = -1;
	boolean lastBotShotFlag = false;
	
	IncomingProjectile lastIncomingProjectile = null;
	double lastIncomingProjectileTime = -1;
	boolean lastIncomingProjectileFlag = false;
	
	PlayerDamaged lastPlayerDamaged = null;
	double lastPlayerDamagedTime = -1;
	boolean lastPlayerDamagedFlag = false;
	
	PlayerDamaged lastPlayerShot = null;
	double lastPlayerShotTime = -1;
	boolean lastPlayerShotFlag = false;
	
	ItemPickedUp lastItemPickedUp = null;
	double lastItemPickedUpTime = -1;
	boolean lastItemPickedUpFlag = false;	
	
	private BeginMessage previousBeginMessage = null;
	private BeginMessage lastBeginMessage = null;
	
	private class Entry<EVENT extends InfoMessage> {
		
		private EVENT event;
		private boolean queried;
		private double time;

		public Entry(EVENT event) {
			this.event = event;
			this.queried = false;
			this.time = agentInfo.getTime();
		}
		
	}
	
	private Map<UnrealId, Entry<PlayerKilled>> playerKilled = new HashMap<UnrealId, Entry<PlayerKilled>>();
	private boolean playerKilledGlobalFlag = false;
	
	AdrenalineGained lastAdrenalineGained = null;
	double lastAdrenalineGainedTime = -1;
	boolean lastAdrenalineGainedFlag = false;
	
	BotKilled lastBotKilled = null;
	double lastBotKilledTime = -1;
	boolean lastBotKilledFlag = false;
	
	
	/*========================================================================*/

	/**
	 * Bumped listener.
	 */
	private class BumpedListener implements IWorldEventListener<Bumped>
	{
		@Override
		public void notify(Bumped event)
		{
			lastBumped = event;
			lastBumpedTime = agentInfo.getTime();
			lastBumpedFlag = true;
		}

		/**
		 * Constructor. Registers itself on the given WorldView object.
		 * @param worldView WorldView object to listent to.
		 */
		public BumpedListener(IWorldView worldView)
		{
			worldView.addEventListener(Bumped.class, this);
		}
	}

	/** Bumped listener */
	BumpedListener bumpedListener;
	
	/*========================================================================*/

	/**
	 * {@link WallCollision} listener.
	 */
	private class WallCollisionListener implements IWorldEventListener<WallCollision>
	{
		@Override
		public void notify(WallCollision event)
		{
			lastWallCollision = event;
			lastWallCollisionTime = agentInfo.getTime();
			lastWallCollisionFlag = true;
		}

		/**
		 * Constructor. Registers itself on the given WorldView object.
		 * @param worldView WorldView object to listen to.
		 */
		public WallCollisionListener(IWorldView worldView)
		{
			worldView.addEventListener(WallCollision.class, this);
		}
	}

	/** {@link WallCollision} listener */
	WallCollisionListener wallCollisitonListener;

	/*========================================================================*/
	
	/**
	 * {@link FallEdge} listener.
	 */
	private class FallEdgeListener implements IWorldEventListener<FallEdge>
	{
		@Override
		public void notify(FallEdge event)
		{
			lastFallEdge = event;
			lastFallEdgeTime = agentInfo.getTime();
			lastFallEdgeFlag = true;
		}

		/**
		 * Constructor. Registers itself on the given WorldView object.
		 * @param worldView WorldView object to listen to.
		 */
		public FallEdgeListener(IWorldView worldView)
		{
			worldView.addEventListener(FallEdge.class, this);
		}
	}

	/** {@link FallEdge} listener */
	FallEdgeListener fallEdgeListener;

	/*========================================================================*/
	
	/**
	 * {@link HearNoise} listener.
	 */
	private class HearNoiseListener implements IWorldEventListener<HearNoise>
	{
		@Override
		public void notify(HearNoise event)
		{
			lastHearNoise = event;
			lastHearNoiseTime = agentInfo.getTime();
			lastHearNoiseFlag = true;
		}

		/**
		 * Constructor. Registers itself on the given WorldView object.
		 * @param worldView WorldView object to listen to.
		 */
		public HearNoiseListener(IWorldView worldView)
		{
			worldView.addEventListener(HearNoise.class, this);
		}
	}

	/** {@link HearNoise} listener */
	HearNoiseListener hearNoiseListener;

	/*========================================================================*/
	
	/**
	 * {@link HearPickup} listener.
	 */
	private class HearPickupListener implements IWorldEventListener<HearPickup>
	{
		@Override
		public void notify(HearPickup event)
		{
			lastHearPickup = event;
			lastHearPickupTime = agentInfo.getTime();
			lastHearPickupFlag = true;
		}

		/**
		 * Constructor. Registers itself on the given WorldView object.
		 * @param worldView WorldView object to listen to.
		 */
		public HearPickupListener(IWorldView worldView)
		{
			worldView.addEventListener(HearPickup.class, this);
		}
	}

	/** {@link HearPickup} listener */
	HearPickupListener hearPickupListener;

	/*========================================================================*/
	
	/**
	 * {@link BotDamaged} listener.
	 */
	private class BotDamagedListener implements IWorldEventListener<BotDamaged>
	{
		@Override
		public void notify(BotDamaged event)
		{
			lastBotDamaged = event;
			lastBotDamagedTime = agentInfo.getTime();
			lastBotDamagedFlag = true;
			
			if (lastBotDamaged.isBulletHit()) {
				lastBotShot = event;
				lastBotShotTime = agentInfo.getTime();
				lastBotShotFlag = true;
			}
		}

		/**
		 * Constructor. Registers itself on the given WorldView object.
		 * @param worldView WorldView object to listen to.
		 */
		public BotDamagedListener(IWorldView worldView)
		{
			worldView.addEventListener(BotDamaged.class, this);
		}
	}

	/** {@link BotDamaged} listener */
	BotDamagedListener botDamagedListener;

	/*========================================================================*/
	
	/**
	 * {@link IncomingProjectile} listener.
	 */
	private class IncomingProjectileListener implements IWorldObjectEventListener<IncomingProjectile, IWorldObjectEvent<IncomingProjectile>>
	{
		@Override
		public void notify(IWorldObjectEvent<IncomingProjectile> event)
		{
			lastIncomingProjectile = event.getObject();
			lastIncomingProjectileTime = agentInfo.getTime();
			lastIncomingProjectileFlag = true;			
		}

		/**
		 * Constructor. Registers itself on the given WorldView object.
		 * @param worldView WorldView object to listen to.
		 */
		public IncomingProjectileListener(IWorldView worldView)
		{
			worldView.addObjectListener(IncomingProjectile.class, WorldObjectUpdatedEvent.class, this);
		}
	}

	/** {@link IncomingProjectile} listener */
	IncomingProjectileListener incomingProjectileListener;

	/*========================================================================*/
	
	/**
	 * {@link PlayerDamaged} listener.
	 */
	private class PlayerDamagedListener implements IWorldEventListener<PlayerDamaged>
	{
		@Override
		public void notify(PlayerDamaged event)
		{
			lastPlayerDamaged = event;
			lastPlayerDamagedTime = agentInfo.getTime();
			lastPlayerDamagedFlag = true;
			
			if (lastPlayerDamaged.isBulletHit()) {
				lastPlayerShot = event;
				lastPlayerShotTime = agentInfo.getTime();
				lastPlayerShotFlag = true;
			}
		}

		/**
		 * Constructor. Registers itself on the given WorldView object.
		 * @param worldView WorldView object to listen to.
		 */
		public PlayerDamagedListener(IWorldView worldView)
		{
			worldView.addEventListener(PlayerDamaged.class, this);
		}
	}

	/** {@link PlayerDamaged} listener */
	PlayerDamagedListener playerDamagedListener;
	
	/*========================================================================*/

	/**
	 * {@link PlayerKilled} listener.
	 */
	private class PlayerKilledListener implements IWorldEventListener<PlayerKilled>
	{
		@Override
		public void notify(PlayerKilled event)
		{
			if (event.getId() == null) return;
			playerKilled.put(event.getId(), new Entry<PlayerKilled>(event));
			playerKilledGlobalFlag = true;
		}

		/**
		 * Constructor. Registers itself on the given WorldView object.
		 * @param worldView WorldView object to listen to.
		 */
		public PlayerKilledListener(IWorldView worldView)
		{
			worldView.addEventListener(PlayerKilled.class, this);
		}
	}

	/** {@link PlayerKilled} listener */
	PlayerKilledListener playerKilledListener;
	
	/*========================================================================*/
	
	/**
	 * {@link AdrenalineGained} listener.
	 */
	private class AdrenalineGainedListener implements IWorldEventListener<AdrenalineGained>
	{
		@Override
		public void notify(AdrenalineGained event)
		{
			lastAdrenalineGained = event;
			lastAdrenalineGainedFlag = true;
			lastAdrenalineGainedTime = agentInfo.getTime();
		}

		/**
		 * Constructor. Registers itself on the given WorldView object.
		 * @param worldView WorldView object to listen to.
		 */
		public AdrenalineGainedListener(IWorldView worldView)
		{
			worldView.addEventListener(AdrenalineGained.class, this);
		}
	}

	/** {@link AdrenalineGained} listener */
	AdrenalineGainedListener adrenalineGainedListener;
	
	/*========================================================================*/
	
	/**
	 * {@link BotKilled} listener.
	 */
	private class BotKilledListener implements IWorldEventListener<BotKilled>
	{
		@Override
		public void notify(BotKilled event)
		{
			lastBotKilled = event;
			lastBotKilledFlag = true;
			lastBotKilledTime = agentInfo.getTime();
		}

		/**
		 * Constructor. Registers itself on the given WorldView object.
		 * @param worldView WorldView object to listen to.
		 */
		public BotKilledListener(IWorldView worldView)
		{
			worldView.addEventListener(BotKilled.class, this);
		}
	}

	/** {@link BotKilled} listener */
	BotKilledListener botKilledListener;
	
	/*========================================================================*/
	
	/**
	 * {@link BeginMessage} listener.
	 */
	private class BeginMessageListener implements IWorldEventListener<BeginMessage>
	{
		@Override
		public void notify(BeginMessage event)
		{
			previousBeginMessage = lastBeginMessage;
			lastBeginMessage = event;
		}

		/**
		 * Constructor. Registers itself on the given WorldView object.
		 * @param worldView WorldView object to listen to.
		 */
		public BeginMessageListener(IWorldView worldView)
		{
			worldView.addEventListener(BeginMessage.class, this);
		}
	}
	
	/** {@link BeginMessage} listener */
	BeginMessageListener beginMessageListener;
	
	/*========================================================================*/

	
	/**
	 * {@link ItemPickedUp} listener.
	 */
	private class ItemPickedUpListener implements IWorldEventListener<ItemPickedUp>
	{
		@Override
		public void notify(ItemPickedUp event)
		{
			lastItemPickedUp = event;
			lastItemPickedUpFlag = true;
			lastItemPickedUpTime = agentInfo.getTime();
		}

		/**
		 * Constructor. Registers itself on the given WorldView object.
		 * @param worldView WorldView object to listen to.
		 */
		public ItemPickedUpListener(IWorldView worldView)
		{
			worldView.addEventListener(ItemPickedUp.class, this);
		}
	}

	/** {@link ItemPickedUp} listener */
	ItemPickedUpListener itemPickedUpListener;
	
	/*========================================================================*/
	
	/** AgentInfo memory module. */
	protected AgentInfo agentInfo;
	/** Players memory module. */
	private Players players;
	
	/**
	 * Constructor. Setups the memory module based on bot's world view.
	 * @param bot owner of the module that is using it
	 * @param agentInfo AgentInfo memory module
	 * @param players Players memory module
	 */
	public Senses(UT2004Bot bot, AgentInfo agentInfo, Players players)
	{
		this(bot, agentInfo, players, null);
	}
	
	/**
	 * Constructor. Setups the memory module based on bot's world view.
	 * @param bot owner of the module that is using it
	 * @param agentInfo AgentInfo memory module.
	 * @param log Logger to be used for logging runtime/debug info. If <i>null</i>, module creates its own logger.
	 */
	public Senses(UT2004Bot bot, AgentInfo agentInfo, Players players, Logger log)
	{
		super(bot, log);

		// set AgentInfo memory module
		this.agentInfo = agentInfo;
		NullCheck.check(this.agentInfo, "agentInfo");
		
		// set Players memory module
		this.players = players;
		NullCheck.check(this.players, "players");

		// create listeners
		bumpedListener =             new BumpedListener(worldView);
		wallCollisitonListener =     new WallCollisionListener(worldView);
		fallEdgeListener =           new FallEdgeListener(worldView);
		hearNoiseListener =          new HearNoiseListener(worldView);
		hearPickupListener =         new HearPickupListener(worldView);
		botDamagedListener =         new BotDamagedListener(worldView);
		incomingProjectileListener = new IncomingProjectileListener(worldView);
		playerDamagedListener =      new PlayerDamagedListener(worldView);
		playerKilledListener =       new PlayerKilledListener(worldView);
		adrenalineGainedListener =   new AdrenalineGainedListener(worldView);
		botKilledListener =          new BotKilledListener(worldView);
		itemPickedUpListener =       new ItemPickedUpListener(worldView);
		beginMessageListener =       new BeginMessageListener(worldView);
		
		cleanUp();
	}
	
	@Override
	protected void cleanUp() {
		super.cleanUp();
		lastAdrenalineGained = null;
		lastAdrenalineGainedFlag = false;
		lastAdrenalineGainedTime = -1;
		lastBotDamaged = null;
		lastBotDamagedFlag = false;
		lastBotDamagedTime = -1;
		lastBotKilled = null;
		lastBotKilledFlag = false;
		lastBotKilledTime = -1;
		lastBotShot = null;
		lastBotShotFlag = false;
		lastBotShotTime = -1;
		lastBumped = null;
		lastBumpedFlag = false;
		lastBumpedTime = -1;
		lastFallEdge = null;
		lastFallEdgeFlag = false;
		lastFallEdgeTime = -1;
		lastHearNoise = null;
		lastHearNoiseFlag = false;
		lastHearNoiseTime = -1;
		lastHearPickup = null;
		lastHearPickupFlag = false;
		lastHearPickupTime = -1;
		lastIncomingProjectile = null;
		lastIncomingProjectileFlag = false;
		lastIncomingProjectileTime = -1;
		lastPlayerDamaged = null;
		lastPlayerDamagedFlag = false;
		lastPlayerDamagedTime = -1;
		lastPlayerShot = null;
		lastPlayerShotFlag = false;
		lastPlayerShotTime = -1;
		lastWallCollision = null;
		lastWallCollisionFlag = false;
		lastWallCollisionTime = -1;
	}
	
}