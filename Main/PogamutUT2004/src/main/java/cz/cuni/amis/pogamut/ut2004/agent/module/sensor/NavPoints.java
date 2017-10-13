package cz.cuni.amis.pogamut.ut2004.agent.module.sensor;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.agent.module.SensorModule;
import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.base.utils.math.DistanceUtils;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.base3d.worldview.object.IViewable;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.bot.IUT2004BotController;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.FlagInfo;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Mover;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Self;
import cz.cuni.amis.utils.collections.MyCollections;

/**
 * Memory module specialized on getting {@link NavPoint}s from the {@link IWorldView}.
 * 
 * <h2>Auto updating</h2>
 *
 * <p>All {@link NavPoint} objects returned by this memory module are always self-updating
 * throughout the time, until the associated navPoint leaves the game. This means
 * that once a valid NavPoint object is obtained, it is not necessary to call any
 * methods of this memory module to get the object's info updated (e.g. navpoint's
 * location, visibility, reachability, etc.). The object will autoupdate itself.
 *
 * <p>The same principle is applied to all Maps returned by this memory module.
 * Each returned Map is self-updating throughout the time. Once a specific Map
 * is obtained (e.g. a map of visible enemies) from this memory module, the Map
 * will get updated based on actions of the navPoints (e.g. joining or leaving
 * the game, changing their team, moving around the map, etc.) automatically.
 *
 * <p>Note: All Maps returned by this memory module are locked and can not be
 * modified outside this memory module. If you need to modify a Map returned by
 * this module (for your own specific purpose), create a duplicate first. Such
 * duplicates, however and of course, will not get updated.
 * 
 * <p><b>WARNING:</b>It is totally unclear what UT2004 means by reachable!!!
 * 
 * <p><p>
 * It is designed to be initialized inside {@link IUT2004BotController#prepareBot(UT2004Bot)} method call
 * and may be used since {@link IUT2004BotController#botInitialized(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage)}
 * is called.
 *  
 * @author Jimmy
 */
public class NavPoints extends SensorModule<UT2004Bot> {

	/**
	 * Retreives last known info about given navPoint.
	 *
	 * <p>Note: The returned NavPoint object is self updating throughout time.
	 * Once you have a valid NavPoint object, you do not have to call this
	 * method to get updated info about that navPoint.
	 * 
	 * <p>Note: If you have the string name only, you should use {@link #getNavPoint(String)} instead. 
	 *
	 * @param UnrealId NavPoint UnrealId to be retreived.
	 * @return Last known navPoint info; or null upon none.
	 *
	 * @see getNavPoint(nam)
	 * @see getVisibleNavPoint(UnrealId)
	 * @see getReachableNavPoint(UnrealId)
	 */
	public NavPoint getNavPoint(UnrealId UnrealId)
	{
		// retreive from map of all navPoints
		return navPoints.all.get(UnrealId);
	}
	
	/**
	 * Retreives last known info about given navPoint.
	 *
	 * <p>Note: The returned NavPoint object is self updating throughout time.
	 * Once you have a valid NavPoint object, you do not have to call this
	 * method to get updated info about that navPoint.
	 *
	 * @param String NavPoint name to be retreived, must not need to be prefixed with the map name
	 * @return Last known navPoint info; or null upon none.
	 *
	 * @see getVisibleNavPoint(UnrealId)
	 * @see getReachableNavPoint(UnrealId)
	 */
	public NavPoint getNavPoint(String name) {
		NavPoint result = navPoints.all.get(UnrealId.get(name));
		if (result != null) return result;
		GameInfo info = worldView.getSingle(GameInfo.class);
		if (info == null) return null;
		return navPoints.all.get(UnrealId.get(info.getLevel() + "." + name));
	}
	
	/**
	 * Returns {@link NavPoint} that is the nearest to the bot.
	 *
	 * @return
	 */
	public NavPoint getNearestNavPoint() {
		if (lastSelf == null) return null;
		return DistanceUtils.getNearest(navPoints.all.values(), lastSelf.getLocation());
	}
	
	
	/**
	 * Returns random navigation point.
	 * @return
	 */
	public NavPoint getRandomNavPoint() {
		return MyCollections.getRandom(navPoints.all.values());
	}
	
	/**
	 * Returns nearest {@link NavPoint} to the 'location'.
	 * @param location
	 * @return
	 */
	public NavPoint getNearestNavPoint(ILocated location) {
		if (location == null || location.getLocation() == null) return null;
		return DistanceUtils.getNearest(navPoints.all.values(), location);
	}

	/**
	 * Retrieves info about given navPoint, but only it the navPoint is visible.
	 *
	 * <p>Note: The returned NavPoint object is self updating throughout time.
	 * Once you have a valid NavPoint object, you do not have to call this
	 * method to get updated info about visibility of that navPoint.
	 *
	 * @param UnrealId NavPoint UnrealId to be retrieved.
	 * @return NavPoint info; or null upon none or not visible.
	 *
	 * @see getNavPoint(UnrealId)
	 * @see getReachableNavPoint(UnrealId)
	 */
	public NavPoint getVisibleNavPoint(UnrealId UnrealId)
	{
		// retreive from map of all visible navPoints
		return navPoints.visible.get(UnrealId);
	}

	/*========================================================================*/

	/**
	 * Retreives a Map of all navPoints.
	 *
	 * <p>Note: The returned Map is unmodifiable and self updating throughout
	 * time. Once you obtain a specific Map of navPoints from this memory module,
	 * the Map will get updated based on actions of the navPoints (e.g. joining
	 * or leaving the game, changing their status, etc.).
	 *
	 * @return Map of all navPoints, using their UnrealIds as keys.
	 *
	 * @see getEnemies()
	 * @see getFriends()
	 * @see getVisibleNavPoints()
	 * @see getReachableNavPoints()
	 */
	public Map<UnrealId, NavPoint> getNavPoints()
	{
		// publish map of all navPoints
		return Collections.unmodifiableMap(navPoints.all);
	}

	/*========================================================================*/

	/**
	 * Retrieves a Map of all visible navPoints.
	 *
	 * <p>Note: The returned Map is unmodifiable and self updating throughout
	 * time. Once you obtain a specific Map of navPoints from this memory module,
	 * the Map will get updated based on actions of the navPoints (e.g. joining
	 * or leaving the game, or changing their visibility, etc.).
	 *
	 * @return Map of all visible navPoints, using their UnrealIds as keys.
	 *
	 * @see getNavPoints()
	 * @see getVisibleEnemies()
	 * @see getVisibleFriends()
	 * @see canSeeNavPoints()
	 */
	public Map<UnrealId, NavPoint> getVisibleNavPoints()
	{
		// publish map of all visible navPoints
		return Collections.unmodifiableMap(navPoints.visible);
	}

	/*========================================================================*/

	/**
	 * Returns nearest navPoint that is visible or that was 'recently' visible. If no such navPoint exists, returns null.
	 * 
	 * @param recently how long the navPoint may be non-visible. IN MILISECONDS!
	 * @return nearest visible or 'recentlyVisibleTime' visible navPoint
	 */
	public NavPoint getRecentlyVisibleNavPoint(double recently) {	
		NavPoint nearest = null;
		double distance = Double.MAX_VALUE;
		for (NavPoint plr : navPoints.all.values()) {
			if (plr.isVisible() || lastSelf.getSimTime() - plr.getSimTime() <= recently) {
				double d = lastSelf.getLocation().getDistance(plr.getLocation());
				if (d < distance) {
					distance = d;
					nearest = plr;
				}
			}
		}
		return nearest;
	}
	
	/**
	 * Returns nearest-visible navPoint - if no if no navPoint is visible returns null.
	 * 
	 * @return nearest visible navPoint
	 */
	public NavPoint getNearestVisibleNavPoint() {		
        return DistanceUtils.getNearest(navPoints.visible.values(), lastSelf.getLocation());
	}
	
	/**
	 * Returns nearest-visible navPoint to the bot from the collection of 'navPoints' - if no navPoint
	 * is visible  returns null.
	 * 
	 * @param navPoints collection to go through
	 * @return nearest visible navPoint from the collection
	 */
	public NavPoint getNearestVisibleNavPoint(Collection<NavPoint> navPoints) {		
        return DistanceUtils.getNearestVisible(navPoints, lastSelf.getLocation());
	}
	
	/**
	 * Returns random visible navPoint - if no if no navPoint is visible returns null.
	 * 
	 * @return random visible navPoint
	 */
	public NavPoint getRandomVisibleNavPoint() {		
        return MyCollections.getRandom(navPoints.visible.values());
	}
	
	/*========================================================================*/

	/**
	 * Tells, whether the agent sees any other navPoints.
	 *
	 * @return True, if at least one other navPoint is visible; false otherwise.
	 *
	 * @see getVisibleNavPoints()
	 */
	public boolean canSeeNavPoints()
	{
		// search map of all visible navPoints
		return (navPoints.visible.size() > 0);
	}

	/*========================================================================*/

	/**
	 * Maps of navPoints of specific type.
	 */
	private class NavPointMaps
	{
		/** Map of all navPoints of the specific type. */
		private HashMap<UnrealId, NavPoint> all = new HashMap<UnrealId, NavPoint> ();
		/** Map of visible navPoints of the specific type. */
		private HashMap<UnrealId, NavPoint> visible = new HashMap<UnrealId, NavPoint> ();

		/**
		 * Processes events.
		 * @param navPoint NavPoint to process.
		 */
		private void notify(NavPoint navPoint)
		{
			UnrealId uid = navPoint.getId();

			// be sure to be within all
			if (!all.containsKey(uid))
				all.put(uid, navPoint);

			// previous visibility
			boolean wasVisible = visible.containsKey(uid);
			boolean isVisible = navPoint.isVisible();

			// refresh visible
			if (isVisible && !wasVisible)
			{
				// add to visibles
				visible.put(uid, navPoint);
			}
			else if (!isVisible && wasVisible)
			{
				// remove from visibles
				visible.remove(uid);
			}

		}

		/**
		 * Removes navPoint from all maps.
		 * @param uid UnrealId of navPoint to be removed.
		 */
		private void remove(UnrealId uid)
		{
			// remove from all maps
			all.remove(uid);
			visible.remove(uid);
		}

		private void clear() {
			all.clear();
			visible.clear();
		}
	}

	/** Maps of all navPoints. */
	private NavPointMaps navPoints = new NavPointMaps ();

	/*========================================================================*/

	/**
	 * NavPoint listener.
	 */
	private class NavPointListener implements IWorldObjectEventListener<NavPoint, WorldObjectUpdatedEvent<NavPoint>>
	{
		@Override
		public void notify(WorldObjectUpdatedEvent<NavPoint> event)
		{
            NavPoint navPoint = event.getObject();
			// do the job in map of navPoints
			navPoints.notify(navPoint);
		}

		/**
		 * Constructor. Registers itself on the given WorldView object.
		 * @param worldView WorldView object to listent to.
		 */
		public NavPointListener(IWorldView worldView)
		{
			worldView.addObjectListener(NavPoint.class, WorldObjectUpdatedEvent.class, this);
		}
	}

	/** NavPoint listener */
	NavPointListener navPointListener;

	/*========================================================================*/
	
	public static String describe(ILocated loc) {
		if (loc == null) return "null";
		StringBuffer sb = new StringBuffer();
		if (!(loc instanceof Location)) {
			if (loc instanceof Player) sb.append("Player");
			if (loc instanceof NavPoint) {
				sb.append(((NavPoint)loc).getId().getStringId());
			}
			if (loc instanceof Mover) sb.append("Mover");
			if (loc instanceof FlagInfo) sb.append("Flag");
			if (loc instanceof Item) sb.append(((Item)loc).getType().getName());
		}
		sb.append(loc.getLocation());
		if (loc instanceof NavPoint) {
			NavPoint np = (NavPoint)loc;
			if (np.isJumpPad()) sb.append("[JUMP-PAD]");
			if (np.isLiftCenter()) sb.append("[LIFT-CENTER]");
			if (np.isLiftExit()) sb.append("[LIFT-EXIT]");
			if (np.isTeleporter()) sb.append("[TELEPORT]");
		}
		if (loc instanceof IViewable) {
			sb.append("[CAN-SEE:");
			sb.append(((IViewable)loc).isVisible());
			sb.append("]");
		}
		return sb.toString();
	}
	
	/**
	 * Self listener.
	 */
	private class SelfListener implements IWorldObjectListener<Self>
	{
		@Override
		public void notify(IWorldObjectEvent<Self> event)
		{
			if (lastSelf == null || lastSelf.getTeam() != event.getObject().getTeam()) {
				lastSelf = event.getObject();				
			} else {
				lastSelf = event.getObject();
			}
		}

		/**
		 * Constructor. Registers itself on the given WorldView object.
		 * @param worldView WorldView object to listent to.
		 */
		public SelfListener(IWorldView worldView)
		{
			worldView.addObjectListener(Self.class, this);
		}
	}

	/** Self listener */
	SelfListener selfListener;
	
	Self lastSelf = null;
	
	/**
	 * Constructor. Setups the memory module based on bot's world view.
	 * @param bot owner of the module that is using it
	 */
	public NavPoints(UT2004Bot bot)
	{
		this(bot, null);
	}
	
	/**
	 * Constructor. Setups the memory module based on bot's world view.
	 * @param bot owner of the module that is using it
	 * @param log Logger to be used for logging runtime/debug info. If <i>null</i>, module creates its own logger.
	 */
	public NavPoints(UT2004Bot bot, Logger log)
	{
		super(bot, log);
		
		// create listeners
		navPointListener =     new NavPointListener(worldView);
		selfListener =       new SelfListener(worldView);
		
		cleanUp();
	}
	
	@Override
	protected void cleanUp() {
		super.cleanUp();
		lastSelf = null;
		navPoints.clear();		
	}
	
}
