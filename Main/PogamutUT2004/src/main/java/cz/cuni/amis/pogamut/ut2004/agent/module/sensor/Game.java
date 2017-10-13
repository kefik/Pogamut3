package cz.cuni.amis.pogamut.ut2004.agent.module.sensor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.agent.module.SensorModule;
import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.bot.IUT2004BotController;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BeginMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.FlagInfo;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Mutator;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerScore;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Self;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.TeamScore;
import cz.cuni.amis.pogamut.ut2004.communication.translator.shared.events.MutatorListObtained;
import cz.cuni.amis.utils.exception.PogamutException;

/**
 * Memory module specialized on general info about the game.
 * <p><p>
 * It is designed to be initialized inside {@link IUT2004BotController#prepareBot(UT2004Bot)} method call
 * and may be used since {@link IUT2004BotController#botInitialized(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage)}
 * is called.
 *
 * @author Juraj 'Loque' Simlovic
 * @author Jimmy
 */
public class Game extends SensorModule<UT2004Bot>
{
	/**
	 * Enums for game types that shields you from Unreal's string ids of game types.
	 * @author Jimmy
	 *
	 */
	public enum GameType
	{
		/** Classic death-match: Kill or get killed. You're on you own! */
		BotDeathMatch,
		/** Team death-match: Strategic team killing. Shoot opponents only. */
		BotTeamGame,
		/** Capture the Flag! Raid the enemy base, steal their flag. */
		BotCTFGame,
		/** Bombing run. Play soccer in UT2004, either kick ball or shoot. */
		BotBombingRun,
		/** Double domination. Take control of specific spots on the map. */
		BotDoubleDomination,
		/** This type of game is not supported. */
		Unknown;

		/**
		 * Tedious work this is.. Let's do it once, shall we?
		 *
		 * @param type Name of the type of the game type.
		 * @return Game type associated with given name.
		 */
		public static GameType getType(String type)
		{
			if (type.equalsIgnoreCase("BotDeathMatch"))       return BotDeathMatch;
			if (type.equalsIgnoreCase("BotTeamGame"))         return BotTeamGame;
			if (type.equalsIgnoreCase("BotCTFGame"))          return BotCTFGame;
			if (type.equalsIgnoreCase("BotBombingRun"))       return BotBombingRun;
			if (type.equalsIgnoreCase("BotDoubleDomination")) return BotDoubleDomination;
			return Unknown;
		}
	}
	
	/**
	 * Returns original {@link GameInfo} message.
	 * 
	 * @return
	 */
	public GameInfo getGameInfo() {
		return lastGameInfo;
	}

	/**
	 * Retreives the type of the game.
	 *
	 * @return Type of the game.
	 */
	public GameType getGameType()
	{
		// retreive from GameInfo object and translate
                if (lastGameInfo == null) return null;
		return GameType.getType(lastGameInfo.getGametype());
	}

	/*========================================================================*/

	/**
	 * Retreives the name of current map.
	 *
	 * @return Name of the current map.
	 */
	public String getMapName()
	{
		// retreive from GameInfo object
        if (lastGameInfo == null) return null;
		return lastGameInfo.getLevel();
	}
	
	/**
	 * Tells, whether the UT2004 is currently running map with name 'name'.
	 * @param name
	 * @return
	 */
	public boolean isMapName(String name) {
		if (lastGameInfo == null) return false;
		if (name == null) return false;
		return lastGameInfo.getLevel().toLowerCase().equals(name.toLowerCase());
	}
	
	/**
	 * It returns 'objectId' prefixed with "{@link Game#getMapName()}.".
	 * <p><p>
     * Note that you may pass prefixed objectId into this method, it will detect it that and auto-correct upper/lower case of this existing prefix if needed. 
     * Also you may use it as a validation feature because
     * this method will raise an exception if the prefix does not match the current map name.
	 * 
	 * @param objectId will be auto-prefixed (if enabled, which is default)
	 * @return
	 */
	public String getPrefixed(String objectId) {
		// auto prefixing is enabled
		if (getMapName() == null) {
			throw new PogamutException("GameInfo was not received yet, can't prefix '" + objectId + "'.", this);
		}
		if (objectId.toLowerCase().startsWith(mapNameLowerChar + ".")) {
			// already prefixed!
			if (!objectId.startsWith(getMapName())) {
				// but wrong upper/lower case detected, replace!
				objectId = getMapName() + objectId.substring(mapNameLowerChar.length());
			}
			// correctly prefixed, just return it
			return objectId;
		} else {
			// not correctly prefixed, check whether there is any prefix at all?
			if (objectId.contains(".")) {
				// yes there is -> map name validation fails!
				throw new PogamutException("id '" + objectId + "' is already prefixed with '" + objectId.substring(0, objectId.indexOf(".")) + "' which is different from current map name '" + getMapName() + "', map name validation fails!", this);
			}
			// no there is not ... so prefix it!
			return getMapName() + "." + objectId;
		}
	}
	
	/**
	 * It returns 'objectId' prefixed with "{@link Game#getMapName()}.".
	 * <p><p>
     * Note that you may pass prefixed objectId into this method, it will detect it that and auto-correct upper/lower case of this existing prefix if needed. 
     * Also you may use it as a validation feature because
     * this method will raise an exception if the prefix does not match the current map name.
	 * 
	 * @param objectId will be auto-prefixed (if enabled, which is default)
	 * @return objectId prefixed and converted to {@link UnrealId}
	 */
	public UnrealId getPrefixedId(String objectId) {
		// auto prefixing is enabled
		if (getMapName() == null) {
			throw new PogamutException("GameInfo was not received yet, can't prefix '" + objectId + "'.", this);
		}
		if (objectId.toLowerCase().startsWith(mapNameLowerChar + ".")) {
			// already prefixed!
			if (!objectId.startsWith(getMapName())) {
				// but wrong upper/lower case detected, replace!
				objectId = getMapName() + objectId.substring(mapNameLowerChar.length());
			}
			// correctly prefixed, just return it
			return UnrealId.get(objectId);
		} else {
			// not correctly prefixed, check whether there is any prefix at all?
			if (objectId.contains(".")) {
				// yes there is -> map name validation fails!
				throw new PogamutException("id '" + objectId + "' is already prefixed with '" + objectId.substring(0, objectId.indexOf(".")) + "' which is different from current map name '" + getMapName() + "', map name validation fails!", this);
			}
			// no there is not ... so prefix it!
			return UnrealId.get(getMapName() + "." + objectId);
		}
	}

	/*========================================================================*/

	/**
	 * Retreives current game time, since the game started.
	 *
	 * @return Current game timestamp. IN SECONDS!
	 */
	public double getTime()
	{
		// retreive from last BeginMessage object
        if (lastBeginMessage == null) return 0;
		return lastBeginMessage.getTime();
	}
	
	/**
	 * Retrieves time-delta since the last batch update.
	 * 
	 * @return time-delta IN SECONDS
	 */
	public double getTimeDelta() {
		return timeDelta;
	}
	
	/**
	 * Retreives time limit for the game.
	 *
	 * <p>Note: Then the time limit is reached and the game is tie, special
	 * game modes might be turned on, e.g. <i>sudden death overtime</i>.
	 * Depends on the game type and game settings.
	 *
	 * @return Time limit of the game.
	 *
	 * @see getRemainingTime()
	 */
	public Double getTimeLimit()
	{
		// retreive from GameInfo object
                if (lastGameInfo == null) return null;
		return lastGameInfo.getTimeLimit();
	}

	/**
	 * Retreives time remaining for the game.
	 *
	 * <p>Note: Then the time limit is reached and the game is tie, special
	 * game modes might be turned on, e.g. <i>sudden death overtime</i>.
	 * Depends on the game type and game settings.
	 *
	 * @return Time limit of the game.
	 *
	 * @see getTime()
	 * @see getTimeLimit()
	 *
	 * @todo Test, whether it is correct..
	 */
	public Double getRemainingTime()
	{
		// derive from the time limit and current time
                if (getTimeLimit() == null) return null;
		return getTimeLimit() - getTime();
	}

 	/*========================================================================*/

	/**
	 * <i>BotDeathMatch only:</i><p>
	 * Number of points (e.g. kills) needed to win the game.
	 *
	 * @return Frag limit of the game.
	 *
	 * @see getTeamScoreLimit()
	 */
	public Integer getFragLimit()
	{
		// retreive from GameInfo object
        if (lastGameInfo == null) return null;
		return lastGameInfo.getFragLimit();
	}

	/**
	 * <i>BotTeamGame, BotCTFGame, BotBombingRun, BotDoubleDomination only:</i><p>
	 * Number of points a team needs to win the game.
	 *
	 * @return Team score limit of the game.
	 *
	 * @see getFragLimit()
	 */
	public Integer getTeamScoreLimit()
	{
		// retreive from GameInfo object
		// we have to cast double to int because UT2004 exports it as double (duuno why)
        if (lastGameInfo == null) return null;
		return (int)lastGameInfo.getGoalTeamScore();
	}

	/*========================================================================*/

	/**
	 * Retrieves FlagInfo object representing the Flag for input team in BotCTFGame.
         * If current game is not BotCTFGame, returns null!
	 *
	 * @param team to get the flag for.
	 * @return FlagInfo object representing the team flag.
	 */
	public FlagInfo getCTFFlag(int team) {
                Collection<FlagInfo> flags = worldView.getAll(FlagInfo.class).values();
                for (FlagInfo flag : flags) {
                    if (flag.getTeam() == team) {
                        return flag;
                    }
                }
                return null;
	}

	/**
	 * Retrieves all FlagInfo objects in the game (by default two) in BotCTFGame.
         * If current game is not BotCTFGame, returns empty collection!
	 *
	 * @return Collection of FlagInfo objects representing the flags in the game.
	 */
	public Collection<FlagInfo> getCTFFlags() {
		return worldView.getAll(FlagInfo.class).values();
	}

	/**
	 * Retrieves all domination points in the game (by default two) in BotDoubleDomination.
         * Domination points are normal NavPoints, but with flag isDomPoint set to true.
         * Moreover, Domination points have attribute DomPointController exported, holding
         * the team that controls the point.
	 *
	 * @return Collection of NavPoint objects representing the domination points in the game.
	 */
	public Collection<NavPoint> getDominationPoints() {
                Collection<NavPoint> domPoints = new ArrayList();
                Collection<NavPoint> navPoints = worldView.getAll(NavPoint.class).values();
                for (NavPoint nav : navPoints) {
                    if (nav.isDomPoint()) {
                        domPoints.add(nav);
                    }
                }
		return domPoints;
	}

	/*========================================================================*/

	/**
	 * Returns unmodifiable map with team scores.
	 * <p><p>
	 * Map is unsynchronized! If you want to iterate it over, use synchronized statement over the map.
	 * 
	 * @return all known team scores
	 */
	public Map<Integer, TeamScore> getTeamScores() {
		return Collections.unmodifiableMap(lastTeamScore);
	}
	
	/**
	 * Retrieves teams team score.
	 * <p><p>
	 * Team score is usually rising by achieving team goals, e.g. killing opponents, capturing flags, controlling domination points, etc. Note: Team score might
	 * decrease, when opposing teams score points themselves, based on map, game type and game settings.
	 * <p><p>
	 * Note that if {@link Integer#MIN_VALUE} is returned, it means that the score is unknown.
	 * 
	 * @param team to get the team score for. 
	 * @return teams team score.
	 */
	public int getTeamScore(int team) {
		TeamScore teamScore = lastTeamScore.get(team);
		if (teamScore == null) {
			return Integer.MIN_VALUE;
		}
		// Retrieve from TeamScore object
		return teamScore.getScore();
	}
	
	/**
	 * Tells whether the team score for 'team' is known.
	 * 
	 * @param team
	 * @return
	 */
	public boolean isTeamScoreKnown(int team) {
		return lastTeamScore.containsKey(team);
	}
	
	/**
	 * Returns unmodifiable map with player scores. (Note that from {@link PlayerScore} message you can get also {@link PlayerScore#getDeaths()}.)
	 * <p><p>
	 * Map is unsynchronized! If you want to iterate it over, use synchronized statement over the map.
	 * 
	 * @return all known player scores
	 */
	public Map<UnrealId, PlayerScore> getPlayerScores() {
		return Collections.unmodifiableMap(lastPlayerScore);
	}
	
	/**
	 * Retreives agent score.
	 * <p><p>
	 * Agent score is usually rising by achieving some goals, e.g. killing opponents, capturing flags, controlling domination points, etc. Note: Agent score
	 * might decrease upon suicides, based on map, game type and game settings.
	 * <p><p>
	 * Note that if {@link Integer#MIN_VALUE} is returned, than the score is unknown.
	 * 
	 * @param id id of the player
	 * @return Current agent score.
	 */
	public int getPlayerScore(UnrealId id) {
		PlayerScore score = lastPlayerScore.get(id);
		if (score != null) {
			return score.getScore();
		}
		return Integer.MIN_VALUE;
	}
	
	/**
	 * Tells whether the player score for 'player' is known.
	 * 
	 * @param player
	 * @return
	 */
	public boolean isPlayerScoreKnown(UnrealId player) {
		return lastPlayerScore.containsKey(player);
	}

	/**
	 * Retreives number of deaths the agent took.
	 * 
	 * <p><p>
	 * A death is counted, whenever the agent dies.
	 * 
	 * <p><p>
	 * Note that if {@link Integer#MIN_VALUE} is returned, than the number of deaths is unknown.
	 * 
	 * @return Number of deaths the agent took.
	 */
	public int getPlayerDeaths(UnrealId id) {
		// retreive from PlayerScore object
		PlayerScore score = lastPlayerScore.get(id);
		if (score == null) {
			return Integer.MIN_VALUE;
		}
		return score.getDeaths();
	}
	
	/**
	 * Tells whether the number of deaths for 'player' is known.
	 * 
	 * @param player
	 * @return
	 */
	public boolean isPlayerDeathsKnown(UnrealId player) {
		return lastPlayerScore.containsKey(player);
	}
	
	/*========================================================================*/
	
	/**
	 * <i>BotTeamGame, BotCTFGame, BotDoubleDomination only:</i><p>
	 * Retrieves number of teams in the game.
	 *
	 * <p> Team numbers start from 0.  Usually, there are just two teams: 0 and 1.
	 *
	 * @return Number of teams in the game.
	 */
	public Integer getMaxTeams()
	{
		// retreive from GameInfo object
                if (lastGameInfo == null) return null;
		return lastGameInfo.getMaxTeams();
	}

	/**
	 * <i>BotTeamGame, BotCTFGame, BotDoubleDomination only:</i><p>
	 * Retreives maximum number of players per team.
	 *
	 * @return Maximum number of players per team.
	 */
	public Integer getMaxTeamSize()
	{
		// retreive from GameInfo object
                if (lastGameInfo == null) return null;
		return lastGameInfo.getMaxTeamSize();
	}

	/*========================================================================*/

	/**
	 * Retreives starting level of health. This is the level of health the
	 * players spawn with into the game.
	 *
	 * @return Starting level of health.
	 *
	 * @see getMaxHealth()
	 * @see getFullHealth()
	 */
	public Integer getStartHealth()
	{
		// retreive from InitedMessage object
                if (lastInitedMessage == null) return null;
		return lastInitedMessage.getHealthStart();
	}

	/**
	 * Retreives maximum level of <i>non-boosted</i> health. This is the level
	 * achievable by foraging standard health kits.
	 *
	 * @return Maximum level of <i>non-boosted</i> health.
	 *
	 * @see getStartHealth()
	 * @see getMaxHealth()
	 */
	public Integer getFullHealth()
	{
		// retreive from InitedMessage object
                if (lastInitedMessage == null) return null;
		return lastInitedMessage.getHealthFull();
	}

	/**
	 * Retreives maximum level of <i>boosted</i> health. This is the total
	 * maximum health achievable by any means of health kits, super health,
	 * or health vials.
	 *
	 * @return Maximum level of <i>boosted</i> health.
	 *
	 * @see getStartHealth()
	 * @see getFullHealth()
	 */
	public Integer getMaxHealth()
	{
		// retreive from InitedMessage object
                if (lastInitedMessage == null) return null;
		return lastInitedMessage.getHealthMax();
	}

	/*========================================================================*/

	/**
	 * Retreives maximum level of combined armor. The armor consist of two
	 * parts, which are summed together into combined armor value. However,
	 * each part is powered-up by different item (either by <i>small shield</i>
	 * or by <i>super-shield</i>).
	 *
	 * @return Maximum level of combined armor.
	 *
	 * @see getMaxLowArmor()
	 * @see getMaxHighArmor()
	 */
	public Integer getMaxArmor()
	{
		// retreive from InitedMessage object
                if (lastInitedMessage == null) return null;
		return lastInitedMessage.getShieldStrengthMax();
	}

	/**
	 * Retreives maximum level of low armor. The armor consist of two
	 * parts, which are summed together into combined armor value. However,
	 * each part is powered-up by different item (either by <i>small shield</i>
	 * or by <i>super-shield</i>).
	 *
	 * <p>Low armor is powered-up by <i>small shield</i>.
	 *
	 * @return Maximum level of low armor.
	 *
	 * @see getMaxArmor()
	 * @see getMaxHighArmor()
	 */
	public int getMaxLowArmor()
	{
		// FIXME[js]: Where do we retreive the max low-armor info?
		return 50;
	}

	/**
	 * Retreives maximum level of high armor. The armor consist of two
	 * parts, which are summed together into combined armor value. However,
	 * each part is powered-up by different item (either by <i>small shield</i>
	 * or by <i>super-shield</i>).
	 *
	 * <p>High armor is powered-up by <i>super-shield</i>.
	 *
	 * @return Maximum level of high armor.
	 *
	 * @see getMaxArmor()
	 * @see getMaxLowArmor()
	 */
	public int getMaxHighArmor()
	{
		// FIXME[js]: Where do we retreive the max high-armor info?
		return 100;
	}

	/*========================================================================*/

	/**
	 * Retreives starting level of adrenaline. This is the level of adrenaline
	 * the players spawn with into the game.
	 *
	 * @return Starting level of adrenaline.
	 */
	public Integer getStartAdrenaline()
	{
		// retreive from InitedMessage object
		// ut2004 exports it as double, must cast to int, ut's weirdness
                if (lastInitedMessage == null) return null;
		return (int)lastInitedMessage.getAdrenalineStart();
	}

	/**
	 * Retreives target level of adrenaline that need to be gained to start
	 * special bonus actions.
	 *
	 * <p>Once the agent's adrenaline reaches this designated level, it can be
	 * used to start special bonus booster-actions like <i>invisibility</i>,
	 * <i>speed</i>, <i>booster</i>, etc. The adrenaline is then spent on the
	 * invoked action.
	 *
	 * @return Maximum level of adrenaline that can be gained.
	 */
	public Integer getTargetAdrenaline()
	{
		// retreive from InitedMessage object
		// ut2004 exports it as double, must cast to int, ut's weirdness
                if (lastInitedMessage == null) return null;
		return (int)lastInitedMessage.getAdrenalineMax();
	}

	/**
	 * Retreives maximum level of adrenaline that can be gained.
	 *
	 * @return Maximum level of adrenaline that can be gained.
	 */
	public Integer getMaxAdrenaline()
	{
		// retreive from InitedMessage object
		// FIXME[js]: Return type!
                if (lastInitedMessage == null) return null;
		return (int)lastInitedMessage.getAdrenalineMax();
	}

	/*========================================================================*/

	/**
	 * Tells, whether the weapons stay on pick-up points, even when they are
	 * picked-up by players.
	 *
	 * <p>If so, each weapon type can be picked up from pick-up points only
	 * once. If the player already has the weapon the pick-up point offers, he
	 * can not pick it up. Also, each weapon pick-up point always contains its
	 * associated weapon.
	 *
	 * <p>If not, weapons can be picked up from pick-up points repeatedly.
	 * If the player already has the weapon the pick-up point offers, the
	 * pick-up will simply replenish ammo for that weapon. Also, upon each
	 * pick-up by a player, the offered weapon disappears (it is "taken" by
	 * that player). Weapons respawn on empty pick-up points after a while.
	 *
	 * @return True, if weapons stay on pick-up points.
	 */
	public Boolean getWeaponsStay()
	{
		// retreive from GameInfo object
                if (lastGameInfo == null) return null;
		return lastGameInfo.isWeaponStay();
	}

	/*========================================================================*/

	/**
	 * Retreives the maximum number of multi-jumping combos.
	 *
	 * <p>Note: Multi-jump combos are currently limited to double-jumps for
	 * bots.
	 *
	 * @return Maximum number of multi-jumping combos.
	 */
	public Integer getMaxMultiJump()
	{
		// retreive from InitedMessage object
                if (lastInitedMessage == null) return null;
		return lastInitedMessage.getMaxMultiJump();
	}

	/*========================================================================*/

	/**
	 * Returns list of mutators that are active in the current game.
	 * 
	 * @return Current game's mutators
	 */
	public List<Mutator> getMutators()
	{
                if (lastMutatorListObtained == null) return null;
		return lastMutatorListObtained.getMutators();
	}

	/*========================================================================*/

	/**
	 * Tells, whether the game is paused or running. When the game is paused,
	 * nobody can move or do anything (usually except posting text messages).
	 *
	 * @return True, if the game is paused. False otherwise.
	 *
	 * @see areBotsPaused()
	 */
	public Boolean isPaused()
	{
		// retreive from GameInfo object
                if (lastGameInfo == null) return null;
		return lastGameInfo.isGamePaused();
	}

	/**
	 * Tells, whether the bots are paused or running. When the bots are paused,
	 * but the game is not paused as well, human controlled players can move.
	 * The bots are standing still and can do nothing  (usually except posting
	 * text messages).
	 *
	 * @return True, if the bots are paused. False otherwise.
	 *
	 * @see isPaused()
	 */
	public Boolean isBotsPaused()
	{
		// retreive from GameInfo object
                if (lastGameInfo == null) return null;
		return lastGameInfo.isBotsPaused();
	}
	
	// ========================
	// CAPTURE THE FLAG METHODS
	// ========================

    /**
     * Returns a map indexed by team numbers, holding all flags in the game.
     * In non-Capture the Flag (CTF) gametypes the result map will be empty.
     *
     * @return Map containing all the flags in the game indexed by owner team number.
     */
	public Map<Integer, FlagInfo> getAllCTFFlags()
	{
		return allCTFFlags;
	}

        /**
         * Returns a collection of all the flags in the game.
         * In non-Capture the Flag (CTF) gametypes the result collection will be empty.
         *
         * @return Collection containing all the flags in the game.
         */
	public Collection<FlagInfo> getAllCTFFlagsCollection()
	{		
		return allCTFFlags.values();
	}
	
	/**
	 * Returns flag (if known) for the 'team'.
	 * @param team
	 * @return
	 */
	public FlagInfo getFlag(int team) {
		return allCTFFlags.get(team);
	}
	
	/**
	 * Returns {@link FlagInfo} that describes MY-team flag. Returns flag only if the info is known.
	 * @return
	 */
	public FlagInfo getMyFlag() {
		if (self == null) return null;
		return allCTFFlags.get(self.getTeam());
	}
	
	/**
	 * Returns {@link FlagInfo} that describes ENEMY-team flag. Returns flag only if the info is known.
	 * @return
	 */
	public FlagInfo getEnemyFlag() {
		if (self == null) return null;
		return getFlag(self.getTeam() == 0 ? 1 : 0);
	}
	
	/**
	 * Returns location of the base for 'red' (team == 0) or 'blue' (team == 1) team.
	 * @param team
	 * @return
	 */
	public Location getFlagBase(int team) {
		if (lastGameInfo == null) return null;
		if (team == 0) return lastGameInfo.getRedBaseLocation();
		else return lastGameInfo.getBlueBaseLocation();
	}
	
	/**
	 * Returns location of MY flag base (where I should carry enemy flag to).
	 * @return
	 */
	public Location getMyFlagBase() {
		if (self == null) return null;
		return getFlagBase(self.getTeam());
	}
	
	/**
	 * Returns location of ENEMY flag base (where the flag resides as default, until stolen).
	 * @return
	 */
	public Location getEnemyFlagBase() {
		if (self == null) return null;
		return getFlagBase(self.getTeam() == 0 ? 1 : 0);
	}

	/*========================================================================*/

	/** Most rescent message containing info about the game. */
	GameInfo lastGameInfo = null;

	/** Most rescent message containing info about the game frame. */
	InitedMessage lastInitedMessage = null;

	/** Most rescent message containing info about the game frame. */
	BeginMessage lastBeginMessage = null;
	
	/** Most recent info about game's mutators. */
	MutatorListObtained lastMutatorListObtained = null;

    /** All flags in the game - will be filled only in CTF games */
    Map<Integer, FlagInfo> allCTFFlags = new HashMap();
    
    /** Most rescent message containing info about the player's score. */
	Map<UnrealId, PlayerScore> lastPlayerScore = null;

	/** Most rescent message containing info about the player team's score. */
	Map<Integer, TeamScore> lastTeamScore = null;
	
	/** Information about self. */
	Self self;
	
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
		}
	}

	/** {@link Self} listener */
	private SelfListener selfListener;
	

	/*========================================================================*/

	/**
	 * GameInfo listener.
	 */
	private class GameInfoListener implements IWorldObjectEventListener<GameInfo, IWorldObjectEvent<GameInfo>>
	{
		@Override
		public void notify(IWorldObjectEvent<GameInfo> event)
		{
			lastGameInfo = event.getObject();
			mapNameLowerChar = lastGameInfo.getLevel().toLowerCase();
		}

		/**
		 * Constructor. Registers itself on the given WorldView object.
		 * @param worldView WorldView object to listent to.
		 */
		public GameInfoListener(IWorldView worldView)
		{
			worldView.addObjectListener(GameInfo.class, this);
		}
	}

	/** GameInfo listener */
	GameInfoListener gameInfoListener;

	String mapNameLowerChar = "";
	
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
	InitedMessageListener initedMessageListener;

	/*========================================================================*/

	private double timeDelta = -1;
	
	/**
	 * BeginMessage listener.
	 */
	private class BeginMessageListener implements IWorldEventListener<BeginMessage>
	{
		@Override
		public void notify(BeginMessage event)
		{
			if (lastBeginMessage != null) {
				timeDelta = event.getTime() - lastBeginMessage.getTime();
			}
			lastBeginMessage = event;
		}

		/**
		 * Constructor. Registers itself on the given WorldView object.
		 * @param worldView WorldView object to listent to.
		 */
		public BeginMessageListener(IWorldView worldView)
		{
			worldView.addEventListener(BeginMessage.class, this);
		}
	}

	/** BeginMessage listener */
	BeginMessageListener beginMessageListener;

	/*========================================================================*/
	
	/**
	 * MutatorListObtained listener.
	 */
	private class MutatorListObtainedListener implements IWorldEventListener<MutatorListObtained>
	{
		@Override
		public void notify(MutatorListObtained event)
		{
			lastMutatorListObtained = event;
		}

		/**
		 * Constructor. Registers itself on the given WorldView object.
		 * @param worldView WorldView object to listent to.
		 */
		public MutatorListObtainedListener(IWorldView worldView)
		{
			worldView.addEventListener(MutatorListObtained.class, this);
		}
	}

	/** MutatorListObtained listener */
	MutatorListObtainedListener mutatorListObtainedListener;

	/*========================================================================*/

	/**
	 * FlagInfo object listener.
	 */
	private class FlagInfoObjectListener implements IWorldObjectEventListener<FlagInfo,WorldObjectUpdatedEvent<FlagInfo>>
	{
		/**
         * Save flag in our HashMap.
         * 
         * @param event
         */
		public void notify(WorldObjectUpdatedEvent<FlagInfo> event)
		{
			allCTFFlags.put(event.getObject().getTeam(), event.getObject());
		}

		/**
		 * Constructor. Registers itself on the given WorldView object.
		 * @param worldView WorldView object to listent to.
		 */
		public FlagInfoObjectListener(IWorldView worldView)
		{
			worldView.addObjectListener(FlagInfo.class, WorldObjectUpdatedEvent.class, this);
		}        
	}

	/** FlagInfo object listener */
	FlagInfoObjectListener flagInfoObjectListener;

	/*========================================================================*/
	
	/**
	 * PlayerScore listener.
	 */
	private class PlayerScoreListener implements IWorldEventListener<PlayerScore>
	{
		@Override
		public void notify(PlayerScore event)
		{
			synchronized(lastPlayerScore) {
				lastPlayerScore.put(event.getId(), event);
			}
		}

		/**
		 * Constructor. Registers itself on the given WorldView object.
		 * @param worldView WorldView object to listent to.
		 */
		public PlayerScoreListener(IWorldView worldView)
		{
			worldView.addEventListener(PlayerScore.class, this);
		}
	}

	/** PlayerScore listener */
	private PlayerScoreListener playerScoreListener;

	/*========================================================================*/
	
	/**
	 * TeamScore listener.
	 */
	private class TeamScoreListener implements IWorldObjectEventListener<TeamScore, WorldObjectUpdatedEvent<TeamScore>>
	{
		/**
		 * Constructor. Registers itself on the given WorldView object.
		 * @param worldView WorldView object to listent to.
		 */
		public TeamScoreListener(IWorldView worldView)
		{
			worldView.addObjectListener(TeamScore.class, WorldObjectUpdatedEvent.class, this);
		}

		@Override
		public void notify(WorldObjectUpdatedEvent<TeamScore> event) {
			synchronized(lastTeamScore) {
				lastTeamScore.put(event.getObject().getTeam(), event.getObject());
			}
		}
	}

	/** TeamScore listener */
	private TeamScoreListener teamScoreListener;
	
	/*========================================================================*/	

	/**
	 * Constructor. Setups the memory module based on bot's world view.
	 * @param bot owner of the module that is using it
	 */
	public Game(UT2004Bot bot) {
		this(bot, null);
	}
	
	/**
	 * Constructor. Setups the memory module based on bot's world view.
	 * @param bot owner of the module that is using it
	 * @param log Logger to be used for logging runtime/debug info. If <i>null</i>, the module creates its own logger.
	 */
	public Game(UT2004Bot bot, Logger log)
	{
		super(bot, log);

		// create listeners
		gameInfoListener            = new GameInfoListener(worldView);
		beginMessageListener        = new BeginMessageListener(worldView);
		initedMessageListener       = new InitedMessageListener(worldView);
		mutatorListObtainedListener = new MutatorListObtainedListener(worldView);
        flagInfoObjectListener      = new FlagInfoObjectListener(worldView);
        playerScoreListener         = new PlayerScoreListener(worldView);
		teamScoreListener           = new TeamScoreListener(worldView);
		lastPlayerScore             = new HashMap<UnrealId, PlayerScore>();
		lastTeamScore               = new HashMap<Integer, TeamScore>();
		selfListener                = new SelfListener(worldView);
		mapNameLowerChar = "";
        
        cleanUp();
	}
	
	@Override
	protected void cleanUp() {
		super.cleanUp();
		lastGameInfo = null;
		lastInitedMessage = null;
		lastBeginMessage = null;
		lastMutatorListObtained = null;
		synchronized(lastPlayerScore) {		
			lastPlayerScore.clear();
		}
		synchronized(lastTeamScore) {
			lastTeamScore.clear();
		}
		timeDelta = -1;
	}
	
}
