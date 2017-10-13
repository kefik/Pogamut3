package cz.cuni.amis.pogamut.ut2004.agent.module.sensor;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.agent.module.SensorModule;
import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.base.utils.math.DistanceUtils;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.bot.IUT2004BotController;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotName;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerLeft;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Self;
import cz.cuni.amis.utils.IFilter;
import cz.cuni.amis.utils.collections.MyCollections;

/**
 * Memory module specialized on whereabouts of other players.
 * 
 * <h2>Auto updating</h2>
 * 
 * <p>
 * All Player objects returned by this memory module are always self-updating
 * throughout the time, until the associated player leaves the game. This means
 * that once a valid Player object is obtained, it is not necessary to call any
 * methods of this memory module to get the object's info updated (e.g. player's
 * location, visibility, reachability, etc.). The object will autoupdate itself.
 * 
 * <p>
 * The same principle is applied to all Maps returned by this memory module.
 * Each returned Map is self-updating throughout the time. Once a specific Map
 * is obtained (e.g. a map of visible enemies) from this memory module, the Map
 * will get updated based on actions of the players (e.g. joining or leaving the
 * game, changing their team, moving around the map, etc.) automatically.
 * 
 * <p>
 * Note: All Maps returned by this memory module are locked and can not be
 * modified outside this memory module. If you need to modify a Map returned by
 * this module (for your own specific purpose), create a duplicate first. Such
 * duplicates, however and of course, will not get updated.
 * 
 * <p>
 * If you need to get info about players' deaths use {@link Senses} module.
 * 
 * <p>
 * <b>WARNING:</b>It is totally unclear what UT2004 means by reachable!!!
 * 
 * <p>
 * <p>
 * It is designed to be initialized inside
 * {@link IUT2004BotController#prepareBot(UT2004Bot)} method call and may be
 * used since
 * {@link IUT2004BotController#botInitialized(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage)}
 * is called.
 * 
 * 
 * @author Juraj 'Loque' Simlovic
 * @author Jimmy
 */
public class Players extends SensorModule<UT2004Bot> {
	/**
	 * Retreives last known info about given player.
	 * 
	 * <p>
	 * Note: The returned Player object is self updating throughout time. Once
	 * you have a valid Player object, you do not have to call this method to
	 * get updated info about that player.
	 * 
	 * @param UnrealId
	 *            Player UnrealId to be retreived.
	 * @return Last known player info; or null upon none.
	 * 
	 * @see getVisiblePlayer(UnrealId)
	 * @see getReachablePlayer(UnrealId)
	 */
	public Player getPlayer(UnrealId UnrealId) {
		// retreive from map of all players
		return players.all.get(UnrealId);
	}

	/**
	 * Returns name of the player if the unrealId is known, otherwise returns
	 * null.
	 * 
	 * @param unrealId
	 * @param stripTags
	 *            whether to cut all '[...]' tags produced by
	 *            {@link UT2004BotName}
	 * @return
	 */
	public String getPlayerName(UnrealId unrealId, boolean stripTags) {
		Player player = players.all.get(unrealId);
		if (player == null)
			return null;
		if (!stripTags)
			return player.getName();
		String name = player.getName();
		int index = name.indexOf("[");
		if (index < 0)
			return name;
		return name.substring(0, index);
	}

	/**
	 * Retreives info about given player, but only it the player is visible.
	 * 
	 * <p>
	 * Note: The returned Player object is self updating throughout time. Once
	 * you have a valid Player object, you do not have to call this method to
	 * get updated info about visibility of that player.
	 * 
	 * @param UnrealId
	 *            Player UnrealId to be retrieved.
	 * @return Player info; or null upon none or not visible.
	 * 
	 * @see getPlayer(UnrealId)
	 * @see getReachablePlayer(UnrealId)
	 */
	public Player getVisiblePlayer(UnrealId UnrealId) {
		// retreive from map of all visible players
		return players.visible.get(UnrealId);
	}

	/* ======================================================================== */

	/**
	 * Retreives a Map of all players.
	 * 
	 * <p>
	 * Note: The returned Map is unmodifiable and self updating throughout time.
	 * Once you obtain a specific Map of players from this memory module, the
	 * Map will get updated based on actions of the players (e.g. joining or
	 * leaving the game, changing their status, etc.).
	 * 
	 * @return Map of all players, using their UnrealIds as keys.
	 * 
	 * @see getEnemies()
	 * @see getFriends()
	 * @see getVisiblePlayers()
	 * @see getReachablePlayers()
	 */
	public Map<UnrealId, Player> getPlayers() {
		// publish map of all players
		return Collections.unmodifiableMap(players.all);
	}

	/**
	 * Retreives a Map of all enemies.
	 * 
	 * <p>
	 * Note: The returned Map is unmodifiable and self updating throughout time.
	 * Once you obtain a specific Map of enemies from this memory module, the
	 * Map will get updated based on actions of the players (e.g. joining or
	 * leaving the game, changing their team or status, etc.).
	 * 
	 * @return Map of all enemies, using their UnrealIds as keys.
	 * 
	 * @see getPlayers()
	 * @see getFriends()
	 * @see getVisibleEnemies()
	 * @see getReachableEnemies()
	 */
	public Map<UnrealId, Player> getEnemies() {
		// publish map of all enemies
		return Collections.unmodifiableMap(enemies.all);
	}

	/**
	 * Retreives a Map of all friends.
	 * 
	 * <p>
	 * Note: The returned Map is unmodifiable and self updating throughout time.
	 * Once you obtain a specific Map of friends from this memory module, the
	 * Map will get updated based on actions of the players (e.g. joining or
	 * leaving the game, changing their team or status, etc.).
	 * 
	 * @return Map of all friends, using their UnrealIds as keys.
	 * 
	 * @see getPlayers()
	 * @see getEnemies()
	 * @see getVisibleFriends()
	 * @see getReachableFriends()
	 */
	public Map<UnrealId, Player> getFriends() {
		// publish map of all friends
		return Collections.unmodifiableMap(friends.all);
	}

	/* ======================================================================== */

	/**
	 * Retreives a Map of all visible players.
	 * 
	 * <p>
	 * Note: The returned Map is unmodifiable and self updating throughout time.
	 * Once you obtain a specific Map of players from this memory module, the
	 * Map will get updated based on actions of the players (e.g. joining or
	 * leaving the game, or changing their visibility, etc.).
	 * 
	 * @return Map of all visible players, using their UnrealIds as keys.
	 * 
	 * @see getPlayers()
	 * @see getVisibleEnemies()
	 * @see getVisibleFriends()
	 * @see canSeePlayers()
	 */
	public Map<UnrealId, Player> getVisiblePlayers() {
		// publish map of all visible players
		return Collections.unmodifiableMap(players.visible);
	}

	/**
	 * Retreives a Map of all visible enemies.
	 * 
	 * <p>
	 * Note: The returned Map is unmodifiable and self updating throughout time.
	 * Once you obtain a specific Map of enemies from this memory module, the
	 * Map will get updated based on actions of the players (e.g. joining or
	 * leaving the game, changing their team, status or visibility, etc.).
	 * 
	 * @return Map of all visible enemies, using their UnrealIds as keys.
	 * 
	 * @see getEnemies()
	 * @see getVisiblePlayers()
	 * @see getVisibleFriends()
	 * @see canSeeEnemies()
	 */
	public Map<UnrealId, Player> getVisibleEnemies() {
		// publish map of all visible enemies
		return Collections.unmodifiableMap(enemies.visible);
	}

	/**
	 * Retreives a Map of all visible friends.
	 * 
	 * <p>
	 * Note: The returned Map is unmodifiable and self updating throughout time.
	 * Once you obtain a specific Map of friends from this memory module, the
	 * Map will get updated based on actions of the players (e.g. joining or
	 * leaving the game, changing their team, status or visibility, etc.).
	 * 
	 * @return Map of all visible friends, using their UnrealIds as keys.
	 * 
	 * @see getFriends()
	 * @see getVisiblePlayers()
	 * @see getVisibleEnemies()
	 * @see canSeeFriends()
	 */
	public Map<UnrealId, Player> getVisibleFriends() {
		// publish map of all visible friends
		return Collections.unmodifiableMap(friends.visible);
	}

	/* ======================================================================== */

	/**
	 * Returns nearest player that is visible or that was 'recently' visible. If
	 * no such player exists, returns null.
	 * 
	 * @param recently
	 *            how long the player may be non-visible. IN MILISECONDS!
	 * @return nearest visible or 'recentlyVisibleTime' visible player
	 */
	public Player getNearestPlayer(double recentlyVisibleTime) {
		return DistanceUtils.getNearestFiltered(players.all.values(), lastSelf, new LastSeen(recentlyVisibleTime));
	}

	/**
	 * Returns nearest enemy that is visible or that was 'recently' visible. If
	 * no such enemy exists, returns null.
	 * 
	 * @param recentlyVisibleTime
	 *            how long the player may be non-visible IN MILISECONDS!
	 * @return nearest visible or 'recently' visible enemy
	 */
	public Player getNearestEnemy(double recentlyVisibleTime) {
		return DistanceUtils.getNearestFiltered(enemies.all.values(), lastSelf, new LastSeen(recentlyVisibleTime));
	}

	/**
	 * Returns nearest friend that is visible or that was 'recently' visible. If
	 * no such friend exists, returns null.
	 * 
	 * @param recentlyVisibleTime
	 *            how long the player may be non-visible IN MILISECONDS!
	 * @return nearest visible or 'recently' visible friend
	 */
	public Player getNearestFriend(double recentlyVisibleTime) {
		return DistanceUtils.getNearestFiltered(friends.all.values(), lastSelf, new LastSeen(recentlyVisibleTime));
	}

	/**
	 * Returns nearest-visible player - if no if no player is visible returns
	 * null.
	 * 
	 * @return nearest visible player
	 */
	public Player getNearestVisiblePlayer() {
		return DistanceUtils.getNearest(players.visible.values(), lastSelf.getLocation());
	}

	/**
	 * Returns nearest-visible enemy - if no enemy is visible returns null.
	 * 
	 * @return nearest visible enemy
	 */
	public Player getNearestVisibleEnemy() {
		return DistanceUtils.getNearest(enemies.visible.values(), lastSelf.getLocation());
	}

	/**
	 * Returns nearest-visible friend - if no friend is visible returns null.
	 * 
	 * @return nearest visible friend
	 */
	public Player getNearestVisibleFriend() {
		return DistanceUtils.getNearest(friends.visible.values(), lastSelf.getLocation());
	}

	/**
	 * Returns nearest-visible player to the bot from the collection of
	 * 'players' - if no player is visible returns null.
	 * 
	 * @param players
	 *            collection to go through
	 * @return nearest visible player from the collection
	 */
	public Player getNearestVisiblePlayer(Collection<Player> players) {
		return DistanceUtils.getNearestVisible(players, lastSelf.getLocation());
	}

	/**
	 * Returns random visible player - if no if no player is visible returns
	 * null.
	 * 
	 * @return random visible player
	 */
	public Player getRandomVisiblePlayer() {
		return MyCollections.getRandom(players.visible.values());
	}

	/**
	 * Returns random visible enemy - if no enemy is visible returns null.
	 * 
	 * @return random visible enemy
	 */
	public Player getRandomVisibleEnemy() {
		return MyCollections.getRandom(enemies.visible.values());
	}

	/**
	 * Returns random friend - if no friend is visible returns null.
	 * 
	 * @return random visible friend
	 */
	public Player getRandomVisibleFriend() {
		return MyCollections.getRandom(friends.visible.values());
	}

	/* ======================================================================== */

	/**
	 * Tells, whether the agent sees any other players.
	 * 
	 * @return True, if at least one other player is visible; false otherwise.
	 * 
	 * @see getVisiblePlayers()
	 */
	public boolean canSeePlayers() {
		// search map of all visible players
		return (players.visible.size() > 0);
	}

	/**
	 * Tells, whether the agent sees any other enemies.
	 * 
	 * @return True, if at least one other enemy is visible; false otherwise.
	 * 
	 * @see getVisibleEnemies()
	 */
	public boolean canSeeEnemies() {
		// search map of all visible enemies
		return (enemies.visible.size() > 0);
	}

	/**
	 * Tells, whether the agent sees any other friends.
	 * 
	 * @return True, if at least one other friend is visible; false otherwise.
	 * 
	 * @see getVisibleFriends()
	 */
	public boolean canSeeFriends() {
		// search map of all visible friends
		return (friends.visible.size() > 0);
	}

	/* ======================================================================== */

	/**
	 * Tells, whether a given team is an enemy team to the agent.
	 * 
	 * @param team
	 *            Team number to be tested.
	 * @return True, if the given team is an enemy team.
	 * 
	 * @see getTeam()
	 * @see isFriend(int)
	 */
	public boolean isEnemy(int team) {
		// freelancers' team or different team
		return (team == AgentInfo.TEAM_NONE) || (team != lastSelf.getTeam());
	}

	/**
	 * Tells, whether a given player is an enemy to the agent.
	 * 
	 * @param player
	 *            Player to be tested.
	 * @return True, if the given player is an enemy.
	 * 
	 * @see getTeam()
	 * @see isFriend(Player)
	 */
	public boolean isEnemy(Player player) {
		// test the enemy team number
		return isEnemy(player.getTeam());
	}

	/**
	 * Tells, whether a given team is a friend team to the agent.
	 * 
	 * @param team
	 *            Team number to be tested.
	 * @return True, if the given team is a friend team.
	 * 
	 * @see getTeam()
	 * @see isEnemy(int)
	 */
	public boolean isFriend(int team) {
		// same team only
		return team != AgentInfo.TEAM_NONE && (team == lastSelf.getTeam());
	}

	/**
	 * Tells, whether a given player is a friend to the agent.
	 * 
	 * @param player
	 *            Player to be tested.
	 * @return True, if the given player is a friend.
	 * 
	 * @see getTeam()
	 * @see isEnemy(Player)
	 */
	public boolean isFriend(Player player) {
		// test the friend team number
		return isFriend(player.getTeam());
	}

	/* ======================================================================== */

	/**
	 * Filter that accepts players that have been seen a given time in the past 
	 */
	private class LastSeen implements IFilter<Player> {
		private double recentlyVisibleTime;

		public LastSeen(double recentlyVisibleTime) {
			this.recentlyVisibleTime = recentlyVisibleTime;
		}

		@Override
		public boolean isAccepted(Player plr) {
			return plr.isVisible() || lastSelf.getSimTime() - plr.getSimTime() <= recentlyVisibleTime;
		}

	}

	/**
	 * Maps of players of specific type.
	 */
	private class PlayerMaps {
		/** Map of all players of the specific type. */
		private HashMap<UnrealId, Player> all = new HashMap<UnrealId, Player>();
		/** Map of visible players of the specific type. */
		private HashMap<UnrealId, Player> visible = new HashMap<UnrealId, Player>();

		/**
		 * Processes events.
		 * 
		 * @param player
		 *            Player to process.
		 */
		private void notify(Player player) {
			UnrealId uid = player.getId();

			// be sure to be within all
			if (!all.containsKey(uid))
				all.put(uid, player);

			// previous visibility
			boolean wasVisible = visible.containsKey(uid);
			boolean isVisible = player.isVisible();

			// refresh visible
			if (isVisible && !wasVisible) {
				// add to visibles
				visible.put(uid, player);
			} else if (!isVisible && wasVisible) {
				// remove from visibles
				visible.remove(uid);
			}

		}

		/**
		 * Removes player from all maps.
		 * 
		 * @param uid
		 *            UnrealId of player to be removed.
		 */
		private void remove(UnrealId uid) {
			// remove from all maps
			all.remove(uid);
			visible.remove(uid);
		}

		private void clear() {
			all.clear();
			visible.clear();
		}
	}

	/** Maps of all players. */
	private PlayerMaps players = new PlayerMaps();
	/** Maps of all enemies. */
	private PlayerMaps enemies = new PlayerMaps();
	/** Maps of all friends. */
	private PlayerMaps friends = new PlayerMaps();

	/* ======================================================================== */

	/**
	 * Player listener.
	 */
	private class PlayerListener implements IWorldObjectEventListener<Player, WorldObjectUpdatedEvent<Player>> {
		@Override
		public void notify(WorldObjectUpdatedEvent<Player> event) {
			Player player = event.getObject();
			// do the job in map of players
			players.notify(player);
			if (lastSelf == null)
				return; // we do not have self yet ... we do not know which team
						// are we in
			// do the job in map of enemies
			if (isEnemy(player))
				enemies.notify(player);
			// do the job in map of friends
			if (isFriend(player))
				friends.notify(player);
		}

		/**
		 * Constructor. Registers itself on the given WorldView object.
		 * 
		 * @param worldView
		 *            WorldView object to listent to.
		 */
		public PlayerListener(IWorldView worldView) {
			worldView.addObjectListener(Player.class, WorldObjectUpdatedEvent.class, this);
		}
	}

	/** Player listener */
	PlayerListener playerListener;

	/* ======================================================================== */

	/**
	 * PlayerLeft listener.
	 */
	private class PlayerLeftListener implements IWorldEventListener<PlayerLeft> {
		@Override
		public void notify(PlayerLeft event) {
			UnrealId uid = event.getId();

			// remove from all maps
			players.remove(uid);
			enemies.remove(uid);
			friends.remove(uid);
		}

		/**
		 * Constructor. Registers itself on the given WorldView object.
		 * 
		 * @param worldView
		 *            WorldView object to listent to.
		 */
		public PlayerLeftListener(IWorldView worldView) {
			worldView.addEventListener(PlayerLeft.class, this);
		}
	}

	/** PlayerLeft listener */
	PlayerLeftListener playerLeftListener;

	/* ======================================================================== */

	/**
	 * Self listener.
	 */
	private class SelfListener implements IWorldObjectListener<Self> {
		@Override
		public void notify(IWorldObjectEvent<Self> event) {
			if (lastSelf == null || lastSelf.getTeam() != event.getObject().getTeam()) {
				lastSelf = event.getObject();
				friends.clear();
				enemies.clear();
				for (Player plr : players.all.values()) {
					if (isFriend(plr))
						friends.notify(plr);
					if (isEnemy(plr))
						enemies.notify(plr);
				}
			} else {
				lastSelf = event.getObject();
			}
		}

		/**
		 * Constructor. Registers itself on the given WorldView object.
		 * 
		 * @param worldView
		 *            WorldView object to listent to.
		 */
		public SelfListener(IWorldView worldView) {
			worldView.addObjectListener(Self.class, this);
		}
	}

	/** Self listener */
	SelfListener selfListener;

	Self lastSelf = null;

	/**
	 * Constructor. Setups the memory module based on bot's world view.
	 * 
	 * @param bot
	 *            owner of the module that is using it
	 */
	public Players(UT2004Bot bot) {
		this(bot, null);
	}

	/**
	 * Constructor. Setups the memory module based on bot's world view.
	 * 
	 * @param bot
	 *            owner of the module that is using it
	 * @param log
	 *            Logger to be used for logging runtime/debug info. If
	 *            <i>null</i>, module creates its own logger.
	 */
	public Players(UT2004Bot bot, Logger log) {
		super(bot, log);

		// create listeners
		playerListener = new PlayerListener(worldView);
		playerLeftListener = new PlayerLeftListener(worldView);
		selfListener = new SelfListener(worldView);

		cleanUp();
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();
		lastSelf = null;
		players.clear();
		friends.clear();
		enemies.clear();
	}

}
