package cz.cuni.amis.pogamut.ut2004.agent.module.sensor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.agent.module.SensorModule;
import cz.cuni.amis.pogamut.base.agent.navigation.IPathPlanner;
import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.base.utils.math.DistanceUtils;
import cz.cuni.amis.pogamut.base.utils.math.DistanceUtils.IGetDistance;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.Weaponry;
import cz.cuni.amis.pogamut.ut2004.agent.module.utils.TabooSet;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.floydwarshall.FloydWarshallMap;
import cz.cuni.amis.pogamut.ut2004.bot.IUT2004BotController;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.EndMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ItemPickedUp;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.cuni.amis.pogamut.ut2004.communication.translator.shared.events.MapPointListObtained;
import cz.cuni.amis.pogamut.ut2004.utils.UnrealUtils;
import cz.cuni.amis.utils.NullCheck;
import cz.cuni.amis.utils.collections.MyCollections;
import cz.cuni.amis.utils.maps.HashMapMap;

/**
 * Memory module specialized on items on the map.
 * <p><p>
 * Apart from providing useful getters based on {@link ItemType}, {@link ItemType.Group} and {@link ItemType.Category} it also
 * provides an optimistic approach for guessing whether some item is spawned inside the map via getSpawnedXYZ methods
 * such as {@link Items#getSpawnedItems()}.
 * <p><p>
 * <b>WARNING:</b>There are methods that contains "reachable" in its name but it is totally unclear what UT2004 means by reachable!!!
 * These methods are for experimenting purposes only.
 * <p><p>
 * It is designed to be initialized inside {@link IUT2004BotController#prepareBot(UT2004Bot)} method call
 * and may be used since {@link IUT2004BotController#botInitialized(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage)}
 * is called.
 *
 * @author Juraj 'Loque' Simlovic
 * @author Jimmy
 */
public abstract class Items extends SensorModule<UT2004Bot> {	
	
	private Random random = new Random(System.currentTimeMillis());
	
	private IPathPlanner<NavPoint> pathPlanner = null;
	
	private NavPoint obtainNavPoint(ILocated obj) {
		if (obj instanceof NavPoint) return (NavPoint)obj;
		if (obj instanceof Item) return ((Item)obj).getNavPoint();
		return null;
	}
	
	private IGetDistance<ILocated> pathPlannerGetDistance = new IGetDistance<ILocated>() {

		@Override
		public double getDistance(ILocated object, ILocated target) {
			if (object == null || target == null) return Double.POSITIVE_INFINITY;
			if (pathPlanner == null) return target.getLocation().getDistance(object.getLocation());
			NavPoint objectNP = obtainNavPoint(object);
			NavPoint targetNP = obtainNavPoint(target);
			if (objectNP == null || targetNP == null) return Double.POSITIVE_INFINITY;
			return pathPlanner.getDistance(objectNP, targetNP);
		}
		
	};
	
	/**
	 * Sets {@link IPathPlanner} (typically {@link FloydWarshallMap}) to be used by the module in order to
	 * determine path-distance to items. 
	 * @param pathPlanner
	 */
	public void setPathPlanner(IPathPlanner<NavPoint> pathPlanner) {
		this.pathPlanner = pathPlanner;
	}

	/**
	 * Method that determines whether 'item' is pickable in the current state of the bot.
	 * E.g., it asseses health for health items, ammo for weapons & ammo, etc.
	 * <p><p>
	 * Contributed by: David Holan
	 * 
	 * @param item
	 * @return
	 */
	public abstract boolean isPickable(Item item);
	
	/*========================================================================*/
	
	/**
	 * Retrieves list of all items, which includes all known pickups and all
	 * visible thrown items.
	 *
	 * <p>Note: The returned Map is unmodifiable and self updating throughout
	 * time. Once you obtain a specific Map of items from this module, the Map
	 * will get updated based on what happens within the map.
	 *
	 * @return List of all items. Note: Spawned items are included only.
	 */
	public Map<UnrealId, Item> getAllItems()
	{
		return Collections.unmodifiableMap(items.all);
	}

	/**
	 * Retrieves list of all items <b>of specific type</b>.
	 *
	 * <p>Note: The returned Map is unmodifiable and self updating throughout
	 * time. Once you obtain a specific Map of items from this module, the Map
	 * will get updated based on what happens within the map.
	 *
	 * @return List of all items of specific type. Note: Spawned items are included only.
	 */
	public Map<UnrealId, Item> getAllItems(ItemType type)
	{
		return Collections.unmodifiableMap(items.allCategories.get(type));
	}
	
	/**
	 * Retrieves map of all items belonging to a <b>specific 'category'</b> of items, which
	 * includes all known pickups.
	 * 
	 * <p>Note: The returned Map is modifiable and is always constructed upon every
	 * invocation of this method.
	 * 
	 * <p><p>WARNING: O(n) complexity!
	 * 
	 * @param category
	 * @return Map of all items of a specific category.
	 */
	public Map<UnrealId, Item> getAllItems(ItemType.Category category) {
		Map<UnrealId, Item> result = new HashMap<UnrealId, Item>();
		for (ItemType type : category.getTypes()) {
			result.putAll(getAllItems(type));
		}
		return result;
	}
	
	/**
	 * Retrieves map of all items belonging to a <b>specific 'group'</b> of items, which
	 * includes all known pickups.
	 * 
	 * <p>Note: The returned Map is modifiable and is always constructed upon every
	 * invocation of this method.
	 * 
	 * <p><p>WARNING: O(n) complexity!
	 * 
	 * @param group
	 * @return Map of all items of a specific group.
	 */
	public Map<UnrealId, Item> getAllItems(ItemType.Group group) {
		Map<UnrealId, Item> result = new HashMap<UnrealId, Item>();
		for (ItemType type : group.getTypes()) {
			result.putAll(getAllItems(type));
		}
		return result;
	}

	/**
	 * Retrieves a specific item from the all items in the map.
	 * <p><p>
	 * Once obtained it is self-updating based on what happens in the game.
	 *
	 * @param id
	 * @return A specific Item be it Spawned or Dropped (Dropped item must be visible though!).
	 */
	public Item getItem(UnrealId id) {
		Item item = items.all.get(id);
		if (item == null) item = items.visible.get(id);
		return item;
	}

	/**
	 * Retrieves a specific item from the all items in the map.
	 * <p><p>
	 * Once obtained it is self-updating based on what happens in the game.
	 *
	 * @param stringUnrealId
	 * @return A specific Item be it Spawned or Dropped (Dropped item must be visible though!).
	 */
	public Item getItem(String stringUnrealId) {
		return getItem(UnrealId.get(stringUnrealId));
	}
	
	/**
	 * Returns random item from 'all' items.
	 * <p><p>
	 * Note that there is no need to provide all "getRandomXYZ(xyz)", just
	 * use {@link MyCollections#getRandom(java.util.Collection)}. 
	 * @return A specific Item be it Spawned or Dropped (Dropped item must be visible though!).
	 */
	public Item getRandomItem() {
		if (getAllItems().size() == 0) return null;
		int num = random.nextInt(getAllItems().size());
		Iterator<Item> iter = getAllItems().values().iterator();
		for (int i = 0; i < num-1; ++i) iter.next();
		return iter.next();
	}
	
	/**
	 * Returns nearest-by-air (Euclidean norm) item spawning point from 'all' items.
	 * Does not need to be necessarily spawned right now. 
	 * @return A nearest Item be it Spawned or Dropped (Dropped item must be visible though!).
	 */
	public Item getNearestItem() {
		return DistanceUtils.getNearest(getAllItems().values(), agentInfo.getLocation());
	}
	
	/**
	 * Returns nearest-by-path (using {@link #pathPlanner} previously injected via {@link #setPathPlanner(IPathPlanner)}) item spawning point from 'all' items EXCEPT DROPPED ONES!
	 * Does not need to be necessarily spawned right now. 
	 * @return A nearest Item be it Spawned.
	 */
	public Item getPathNearestItem() {
		return DistanceUtils.getNearest(getAllItems().values(), agentInfo.getNearestNavPoint(), pathPlannerGetDistance);
	}
	
	/**
	 * Returns nearest-by-air (Euclidean norm) item spawning point from 'all' items of specific category.
	 * Does not need to be necessarily spawned right now. 
	 * @param category 
	 * @return A nearest Item be it Spawned or Dropped (Dropped item must be visible though!) of specific category.
	 */
	public Item getNearestItem(ItemType.Category category) {
		return DistanceUtils.getNearest(getAllItems(category).values(), agentInfo.getLocation());
	}
	
	/**
	 * Returns nearest-by-path (using {@link #pathPlanner} previously injected via {@link #setPathPlanner(IPathPlanner)}) item spawning point from 'all' items EXCEPT DROPPED ONES!
	 * Does not need to be necessarily spawned right now. 
	 * @param category
	 * @return A nearest Item be it Spawned of specific category.
	 */
	public Item getPathNearestItem(ItemType.Category category) {
		return DistanceUtils.getNearest(getAllItems(category).values(), agentInfo.getNearestNavPoint(), pathPlannerGetDistance);
	}
	
	/**
	 * Returns nearest-by-air (Euclidean norm) item spawning point from 'all' items of specific group.
	 * Does not need to be necessarily spawned right now. 
	 * @param group 
	 * @return A nearest Item be it Spawned or Dropped (Dropped item must be visible though!) of specific group.
	 */
	public Item getNearestItem(ItemType.Group group) {
		return DistanceUtils.getNearest(getAllItems(group).values(), agentInfo.getLocation());
	}
	
	/**
	 * Returns nearest-by-path (using {@link #pathPlanner} previously injected via {@link #setPathPlanner(IPathPlanner)}) item spawning point from 'all' items EXCEPT DROPPED ONES!
	 * Does not need to be necessarily spawned right now. 
	 * @param group 
	 * @return A nearest Item be it Spawned of specific group.
	 */
	public Item getPathNearestItem(ItemType.Group group) {
		return DistanceUtils.getNearest(getAllItems(group).values(), agentInfo.getNearestNavPoint(), pathPlannerGetDistance);
	}
	
	/**
	 * Returns nearest-by-air (Euclidean norm) item spawning point from 'all' items of specific type.
	 * Does not need to be necessarily spawned right now. 
	 * @param group 
	 * @return A nearest Item be it Spawned or Dropped (Dropped item must be visible though!) of specific category.
	 */
	public Item getNearestItem(ItemType type) {
		return DistanceUtils.getNearest(getAllItems(type).values(), agentInfo.getLocation());
	}
	
	/**
	 * Returns nearest-by-path (using {@link #pathPlanner} previously injected via {@link #setPathPlanner(IPathPlanner)}) item spawning point from 'all' items EXCEPT DROPPED ONES!
	 * Does not need to be necessarily spawned right now. 
	 * @param type
	 * @return A nearest Item be it Spawned of specific type.
	 */
	public Item getPathNearestItem(ItemType type) {
		return DistanceUtils.getNearest(getAllItems(type).values(), agentInfo.getNearestNavPoint(), pathPlannerGetDistance);
	}

	/*========================================================================*/

	/**
	 * Retreives list of all visible items, which includes all visible known
	 * pickups and all visible thrown items.
	 *
	 * <p>Note: The returned Map is unmodifiable and self updating throughout
	 * time. Once you obtain a specific Map of items from this module, the Map
	 * will get updated based on what happens within the map.
	 *
	 * @return List of all visible items. Note: Spawned items are included only.
	 */
	public Map<UnrealId, Item> getVisibleItems()
	{
		return Collections.unmodifiableMap(items.visible);
	}

	/**
	 * Retreives list of all visible items <b> of specific type</b>, which includes all visible known
	 * pickups and all visible thrown items.
	 *
	 * <p>Note: The returned Map is unmodifiable and self updating throughout
	 * time. Once you obtain a specific Map of items from this module, the Map
	 * will get updated based on what happens within the map.
	 *
	 * @return List of all visible items of specific type. Note: Spawned items are included only.
	 */
	public Map<UnrealId, Item> getVisibleItems(ItemType type)
	{
		return Collections.unmodifiableMap(items.visibleCategories.get(type));
	}
	
	/**
	 * Retrieves map of visible items belonging to a <b>specific 'category'</b> of items, which
	 * includes all known pickups.
	 * 
	 * <p>Note: The returned Map is modifiable and is always constructed upon every
	 * invocation of this method.
	 * 
	 * <p><p>WARNING: O(n) complexity!
	 * 
	 * @param category
	 * @return Map of visible items of a specific category.
	 */
	public Map<UnrealId, Item> getVisibleItems(ItemType.Category category) {
		Map<UnrealId, Item> result = new HashMap<UnrealId, Item>();
		for (ItemType type : category.getTypes()) {
			result.putAll(getVisibleItems(type));
		}
		return result;
	}
	
	/**
	 * Retrieves map of visible items belonging to a <b>specific 'group'</b> of items, which
	 * includes all known pickups.
	 * 
	 * <p>Note: The returned Map is modifiable and is always constructed upon every
	 * invocation of this method.
	 * 
	 * <p><p>WARNING: O(n) complexity!
	 * 
	 * @param group
	 * @return Map of visible items of a specific group.
	 */
	public Map<UnrealId, Item> getVisibleItems(ItemType.Group group) {
		Map<UnrealId, Item> result = new HashMap<UnrealId, Item>();
		for (ItemType type : group.getTypes()) {
			result.putAll(getVisibleItems(type));
		}
		return result;
	}

	/**
	 * Retrieves a specific item from the visible items in the map. If item of specified
	 * id is not visible returns null.
	 * <p><p>
	 * Once obtained it is self-updating based on what happens in the game.
	 *
	 * @param id
	 * @return A specific Item be it Spawned or Dropped.
	 */
	public Item getVisibleItem(UnrealId id) {
		Item item = items.visible.get(id);
		return item;
	}

	/**
	 * Retrieves a specific item from the visible items in the map. If item of specified
	 * id is not visible returns null.
	 * <p><p>
	 * Once obtained it is self-updating based on what happens in the game.
	 *
	 * @param stringUnrealId
	 * @return A specific Item be it Spawned or Dropped.
	 */
	public Item getVisibleItem(String stringUnrealId) {
		return getVisibleItem(UnrealId.get(stringUnrealId));
	}
	
	/**
	 * Returns nearest-by-air (Euclidean norm) item spawning point from visible items.
	 * Does not need to be necessarily spawned right now. 
	 * @return A nearest visible Item be it Spawned or Dropped (Dropped item must be visible though!).
	 */
	public Item getNearestVisibleItem() {
		return DistanceUtils.getNearest(getVisibleItems().values(), agentInfo.getLocation());
	}
	
	/**
	 * Returns nearest-by-path (using {@link #pathPlanner} previously injected via {@link #setPathPlanner(IPathPlanner)}) item spawning point from visible items EXCEPT DROPPED ONES!
	 * Does not need to be necessarily spawned right now. 
	 * @return A nearest visible Item be it Spawned.
	 */
	public Item getPathNearestVisibleItem() {
		return DistanceUtils.getNearest(getVisibleItems().values(), agentInfo.getNearestNavPoint(), pathPlannerGetDistance);
	}
	
	/**
	 * Returns nearest-by-air (Euclidean norm) item spawning point from visible items of specific category.
	 * Does not need to be necessarily spawned right now. 
	 * @param category 
	 * @return A nearest visible Item be it Spawned or Dropped (Dropped item must be visible though!) of specific category.
	 */
	public Item getNearestVisibleItem(ItemType.Category category) {
		return DistanceUtils.getNearest(getVisibleItems(category).values(), agentInfo.getLocation());
	}
	
	/**
	 * Returns nearest-by-path (using {@link #pathPlanner} previously injected via {@link #setPathPlanner(IPathPlanner)}) item spawning point from visible items EXCEPT DROPPED ONES!
	 * Does not need to be necessarily spawned right now. 
	 * @param category
	 * @return A nearest visible Item be it Spawned of specific category.
	 */
	public Item getPathNearestVisibleItem(ItemType.Category category) {
		return DistanceUtils.getNearest(getVisibleItems(category).values(), agentInfo.getNearestNavPoint(), pathPlannerGetDistance);
	}
	
	/**
	 * Returns nearest-by-air (Euclidean norm) item spawning point from visible items of specific group.
	 * Does not need to be necessarily spawned right now. 
	 * @param group 
	 * @return A nearest visible Item be it Spawned or Dropped (Dropped item must be visible though!) of specific group.
	 */
	public Item getNearestVisibleItem(ItemType.Group group) {
		return DistanceUtils.getNearest(getVisibleItems(group).values(), agentInfo.getLocation());
	}
	
	/**
	 * Returns nearest-by-path (using {@link #pathPlanner} previously injected via {@link #setPathPlanner(IPathPlanner)}) item spawning point from visible items EXCEPT DROPPED ONES!
	 * Does not need to be necessarily spawned right now. 
	 * @param group 
	 * @return A nearest visible Item be it Spawned of specific group.
	 */
	public Item getPathNearestVisibleItem(ItemType.Group group) {
		return DistanceUtils.getNearest(getVisibleItems(group).values(), agentInfo.getNearestNavPoint(), pathPlannerGetDistance);
	}
	
	/**
	 * Returns nearest-by-air (Euclidean norm) item spawning point from visible items of specific type.
	 * Does not need to be necessarily spawned right now. 
	 * @param group 
	 * @return A nearest visible Item be it Spawned or Dropped (Dropped item must be visible though!) of specific category.
	 */
	public Item getNearestVisibleItem(ItemType type) {
		return DistanceUtils.getNearest(getVisibleItems(type).values(), agentInfo.getLocation());
	}
	
	/**
	 * Returns nearest-by-path (using {@link #pathPlanner} previously injected via {@link #setPathPlanner(IPathPlanner)}) item spawning point from visible items EXCEPT DROPPED ONES!
	 * Does not need to be necessarily spawned right now. 
	 * @param type
	 * @return A nearest visible Item be it Spawned of specific type.
	 */
	public Item getPathNearestVisibleItem(ItemType type) {
		return DistanceUtils.getNearest(getVisibleItems(type).values(), agentInfo.getNearestNavPoint(), pathPlannerGetDistance);
	}
	
	/*========================================================================*/

	/**
	 * Retrieves list of all known item pickup points.
	 *
	 * <p>Note: The returned Map is unmodifiable and self updating throughout
	 * time. Once you obtain a specific Map of items from this module, the Map
	 * will get updated based on what happens within the map.
	 *
	 * @return List of all items. Note: Empty pickups are included as well.
	 *
	 * @see isPickupSpawned(Item)
	 */
	public Map<UnrealId, Item> getKnownPickups()
	{
		return Collections.unmodifiableMap(items.known);
	}

	/**
	 * Retrieves list of all known item pickup points <b>of specific type</b>.
	 *
	 * <p>Note: The returned Map is unmodifiable and self updating throughout
	 * time. Once you obtain a specific Map of items from this module, the Map
	 * will get updated based on what happens within the map.
	 *
	 * @return List of all items of specific type. Note: Empty pickups are included as well.
	 *
	 * @see isPickupSpawned(Item)
	 */
	public Map<UnrealId, Item> getKnownPickups(ItemType type)
	{
		return Collections.unmodifiableMap(items.knownCategories.get(type));
	}
	
	/**
	 * Retrieves map of all known pickups belonging to a <b>specific 'category'</b> of items, which
	 * includes all known pickups.
	 * 
	 * <p>Note: The returned Map is modifiable and is always constructed upon every
	 * invocation of this method.
	 * 
	 * <p><p>WARNING: O(n) complexity!
	 * 
	 * @param category
	 * @return Map of known pickups of a specific category.
	 */
	public Map<UnrealId, Item> getKnownPickups(ItemType.Category category) {
		Map<UnrealId, Item> result = new HashMap<UnrealId, Item>();
		for (ItemType type : category.getTypes()) {
			result.putAll(getKnownPickups(type));
		}
		return result;
	}
	
	/**
	 * Retrieves map of all known pickups belonging to a <b>specific 'group'</b> of items, which
	 * includes all known pickups.
	 * 
	 * <p>Note: The returned Map is modifiable and is always constructed upon every
	 * invocation of this method.
	 * 
	 * <p><p>WARNING: O(n) complexity!
	 * 
	 * @param group
	 * @return Map of known pickups of a specific group.
	 */
	public Map<UnrealId, Item> getKnownPickups(ItemType.Group group) {
		Map<UnrealId, Item> result = new HashMap<UnrealId, Item>();
		for (ItemType type : group.getTypes()) {
			result.putAll(getKnownPickups(type));
		}
		return result;
	}

	/**
	 * Retrieves a specific pickup point.
	 * <p><p>
	 * Once obtained it is self-updating based on what happens in the game.
	 *
	 * @param id
	 * @return A specific Item be it Spawned or Dropped (Dropped item must be visible though!).
	 */
	public Item getKnownPickup(UnrealId id) {
		return items.known.get(id);
	}

	/**
	 * Retrieves a specific pickup point.
	 * <p><p>
	 * Once obtained it is self-updating based on what happens in the game.
	 *
	 * @param stringUnrealId
	 * @return A specific Item be it Spawned or Dropped (Dropped item must be visible though!).
	 */
	public Item getKnownPickup(String stringUnrealId) {
		return getKnownPickup(UnrealId.get(stringUnrealId));
	}

	/*========================================================================*/
	
	/**
	 * Returns an underlaying data structure, the {@link TabooSet} that contains
	 * items, which are thought "not to be spawned now". You can alter "taboo times in there"
	 * if you need to fine-tune the {@link #isPickupSpawned(Item)} believes.
	 * @return
	 */
	public TabooSet<Item> getSpawnedTaboos() {
		return items.itemMissing;
	}
	
	/**
	 * Uses {@link Items#isPickupSpawned(Item)} to return all items that are believed to 
	 * be currently spawned.
	 * 
	 * <p><p>WARNING: O(n) complexity!
	 * 
	 * @return collection of spawned items
	 */
	public Map<UnrealId, Item> getSpawnedItems() {
		Map<UnrealId, Item> result = new HashMap<UnrealId, Item>();
		for (Item item : getAllItems().values()) {
			if (isPickupSpawned(item)) result.put(item.getId(), item);
		}
		return result;
	}
	
	/**
	 * Uses {@link Items#isPickupSpawned(Item)} to return all items of 'type' that are believed to 
	 * be currently spawned.
	 * 
	 * <p><p>WARNING: O(n) complexity!
	 * 
	 * @return Map of spawned items of a specific type.
	 */
	public Map<UnrealId, Item> getSpawnedItems(ItemType type) {
		Map<UnrealId, Item> result = new HashMap<UnrealId, Item>();
		for (Item item : getAllItems(type).values()) {
			if (isPickupSpawned(item)) {
				result.put(item.getId(), item);
			}
		}
		return result;
	}
	
	/**
	 * Uses {@link Items#isPickupSpawned(Item)} to return all items belonging to a specific 'category' that are believed to 
	 * be currently spawned.
	 * 
	 * <p>Note: The returned Map is modifiable and is always constructed upon every
	 * invocation of this method.
	 * 
	 * <p><p>WARNING: O(n) complexity!
	 * 
	 * @param category
	 * @return Map of spawned items of a specific category.
	 */
	public Map<UnrealId, Item> getSpawnedItems(ItemType.Category category) {
		Map<UnrealId, Item> result = new HashMap<UnrealId, Item>();
		for (ItemType type : category.getTypes()) {
			result.putAll(getSpawnedItems(type));
		}
		return result;
	}
	
	/**
	 * Uses {@link Items#isPickupSpawned(Item)} to return all items belonging to a specific 'group' that are believed to 
	 * be currently spawned.
	 * 
	 * <p>Note: The returned Map is modifiable and is always constructed upon every
	 * invocation of this method.
	 * 
	 * <p><p>WARNING: O(n) complexity!
	 * 
	 * @param group
	 * @return Map of spawned items of a specific group.
	 */
	public Map<UnrealId, Item> getSpawnedItems(ItemType.Group group) {
		Map<UnrealId, Item> result = new HashMap<UnrealId, Item>();
		for (ItemType type : group.getTypes()) {
			result.putAll(getSpawnedItems(type));
		}
		return result;
	}
	
	/**
	 * Returns nearest-by-air (Euclidean norm) item spawning point from spawned items.
	 * Does not need to be necessarily spawned right now. 
	 * @return A nearest spawned Item be it Spawned or Dropped (Dropped item must be visible though!).
	 */
	public Item getNearestSpawnedItem() {
		return DistanceUtils.getNearest(getSpawnedItems().values(), agentInfo.getLocation());
	}
	
	/**
	 * Returns nearest-by-path (using {@link #pathPlanner} previously injected via {@link #setPathPlanner(IPathPlanner)}) item spawning point from spawned items EXCEPT DROPPED ONES!
	 * Does not need to be necessarily spawned right now. 
	 * @return A nearest spawned Item be it Spawned.
	 */
	public Item getPathNearestSpawnedItem() {
		return DistanceUtils.getNearest(getSpawnedItems().values(), agentInfo.getNearestNavPoint(), pathPlannerGetDistance);
	}
	
	/**
	 * Returns nearest-by-air (Euclidean norm) item spawning point from spawned items of specific category.
	 * Does not need to be necessarily spawned right now. 
	 * @param category 
	 * @return A nearest spawned Item be it Spawned or Dropped (Dropped item must be visible though!) of specific category.
	 */
	public Item getNearestSpawnedItem(ItemType.Category category) {
		return DistanceUtils.getNearest(getSpawnedItems(category).values(), agentInfo.getLocation());
	}
	
	/**
	 * Returns nearest-by-path (using {@link #pathPlanner} previously injected via {@link #setPathPlanner(IPathPlanner)}) item spawning point from spawned items EXCEPT DROPPED ONES!
	 * Does not need to be necessarily spawned right now. 
	 * @param category
	 * @return A nearest spawned Item be it Spawned of specific category.
	 */
	public Item getPathNearestSpawnedItem(ItemType.Category category) {
		return DistanceUtils.getNearest(getSpawnedItems(category).values(), agentInfo.getNearestNavPoint(), pathPlannerGetDistance);
	}
	
	/**
	 * Returns nearest-by-air (Euclidean norm) item spawning point from spawned items of specific group.
	 * Does not need to be necessarily spawned right now. 
	 * @param group 
	 * @return A nearest spawned Item be it Spawned or Dropped (Dropped item must be visible though!) of specific group.
	 */
	public Item getNearestSpawnedItem(ItemType.Group group) {
		return DistanceUtils.getNearest(getSpawnedItems(group).values(), agentInfo.getLocation());
	}
	
	/**
	 * Returns nearest-by-path (using {@link #pathPlanner} previously injected via {@link #setPathPlanner(IPathPlanner)}) item spawning point from spawned items EXCEPT DROPPED ONES!
	 * Does not need to be necessarily spawned right now. 
	 * @param group 
	 * @return A nearest spawned Item be it Spawned of specific group.
	 */
	public Item getPathNearestSpawnedItem(ItemType.Group group) {
		return DistanceUtils.getNearest(getSpawnedItems(group).values(), agentInfo.getNearestNavPoint(), pathPlannerGetDistance);
	}
	
	/**
	 * Returns nearest-by-air (Euclidean norm) item spawning point from spawned items of specific type.
	 * Does not need to be necessarily spawned right now. 
	 * @param group 
	 * @return A nearest spawned Item be it Spawned or Dropped (Dropped item must be visible though!) of specific category.
	 */
	public Item getNearestSpawnedItem(ItemType type) {
		return DistanceUtils.getNearest(getSpawnedItems(type).values(), agentInfo.getLocation());
	}
	
	/**
	 * Returns nearest-by-path (using {@link #pathPlanner} previously injected via {@link #setPathPlanner(IPathPlanner)}) item spawning point from spawned items EXCEPT DROPPED ONES!
	 * Does not need to be necessarily spawned right now. 
	 * @param type
	 * @return A nearest spawned Item be it Spawned of specific type.
	 */
	public Item getPathNearestSpawnedItem(ItemType type) {
		return DistanceUtils.getNearest(getSpawnedItems(type).values(), agentInfo.getNearestNavPoint(), pathPlannerGetDistance);
	}
	
	/**
	 * Returns how fast are the items respawning based on their item type (in UT Time == UT seconds == {@link UnrealUtils#UT2004_TIME_SPEED} * 1 seconds).
	 * @param item
	 * @return
	 */
	public double getItemRespawnUT2004Time(Item item) {
		return getItemRespawnTime(item.getType()) * UnrealUtils.UT2004_TIME_SPEED;
	}
	
	/**
	 * Returns how fast are the items respawning based on their item type (in UT Time == UT seconds == {@link UnrealUtils#UT2004_TIME_SPEED} * 1 seconds).
	 * @param itemType
	 * @return
	 */
	public double getItemRespawnUT2004Time(ItemType itemType) {
		return getItemRespawnTime(itemType) * UnrealUtils.UT2004_TIME_SPEED;
	}
	
	/**
	 * Returns how fast are the items respawning based on their item type (in real seconds according to {@link System#currentTimeMillis()}.
	 * @param item
	 * @return
	 */
	public double getItemRespawnTime(Item item) {
		return getItemRespawnTime(item.getType());
	}
	
	/**
	 * Returns how fast are the items respawning based on their item type (in real seconds according to {@link System#currentTimeMillis()}.
	 * @param itemType
	 * @return
	 */
	public abstract double getItemRespawnTime(ItemType itemType);
	
	/**
	 * Tells, whether the given pickup point contains a spawned item.
	 * 
	 * <p><p>This implementation is guessing (optimistically) whether the item is spawned based on
	 * the last time we have seen it missing (bot has seen its spawning-navpoint but the item was not laying there).
	 * 
	 * <p><p>Note that the guessing is not perfect, experiment with it or check the source code
	 * and possibly reimplement (probably copy-paste) to suit your needs.
	 * 
	 * <p><p>Note that this method is working correctly only if items are respawning.
	 *
	 * @param item Item, for which its pickup point is to be examined.
	 * @return True, if the item is spawned; false if the pickup is empty.
	 *
	 * @see getKnownPickups(boolean,boolean)
	 */
	public boolean isPickupSpawned(Item item) {
		if (item == null) return false;
		if (item.isVisible()) {
			// if the item is visible it is truly spawned
			return true;
		}
		NavPoint np = item.getNavPoint();
		if (np == null) {
			np = navPoints.get(item.getNavPointId());
		}		
		if (np != null) { 
			if (np.isVisible()) {
				// we can see the spawning-navpoint but the item is not visible!
				return np.isItemSpawned();
			} else {
				return !items.itemMissing.isTaboo(item);
			}			
		} else {
			// we do not have item's navpoint, just check times
			return !items.itemMissing.isTaboo(item);			
		}
	}
	
	/**
	 * Tells, whether the given pickup will be thought to be spawned in "millis"
	 * 
	 * <p><p>This implementation is guessing (optimistically) whether the item is spawned based on
	 * the last time we have seen it missing (bot has seen its spawning-navpoint but the item was not laying there).
	 * 
	 * <p><p>Note that the guessing is not perfect, experiment with it or check the source code
	 * and possibly reimplement (probably copy-paste) to suit your needs.
	 * 
	 * <p><p>Note that this method is working correctly only if items are respawning.
	 *
	 * @param item Item, for which its pickup point is to be examined.
	 * @param long seconds, lookahead time, does not count with "picking up"
	 * @return True, if the item is spawned; false if the pickup is empty.
	 *
	 * @see getKnownPickups(boolean,boolean)
	 */
	public boolean willPickupBeSpawnedIn(Item item, double seconds) {		
		if (isPickupSpawned(item)) return true;
		return !items.itemMissing.willBeTaboo(item, seconds);
	}
	
	/**
	 * Returns the number of seconds you believe the pickup will be spawned in.
	 * @param item
	 * @return
	 */
	public double getItemTimeToSpawn(Item item) {
		if (isPickupSpawned(item)) return 0;
		return items.itemMissing.getTabooTime(item);
	}
	
	/**
	 * Tells, whether the given pickup point contains a spawned item.
	 * 
	 * <p><p>This implementation is guessing (optimistically) whether the item is spawned based on
	 * the last time we have seen it missing (saw its navpoint but the item was not laying there).
	 * 
	 * <p><p>Note that the guessing is not perfect, experiment with it or check the source code
	 * and possibly reimplement to suit your needs.
	 * 
	 * <p><p>Note that this method is working only if items are respawning.
	 *
	 * @param itemId Id of the item, for which its pickup point is to be examined.
	 * @return True, if the item is spawned; false if the pickup is empty.
	 *
	 * @see getKnownPickups(boolean,boolean)
	 */
	public boolean isPickupSpawned(UnrealId itemId) {	
		return isPickupSpawned(items.all.get(itemId));
	}

	/*========================================================================*/

	/**
	 * Maps of items of specific type.
	 */
	private class ItemMaps
	{
		/** Map of all items (known and thrown). */
		private HashMap<UnrealId, Item> all = new HashMap<UnrealId, Item> ();
		/** Map of visible items of the specific type. */
		private HashMap<UnrealId, Item> visible = new HashMap<UnrealId, Item> ();
		/** Map of visible items of the specific type. */
		private HashMap<UnrealId, Item> reachable = new HashMap<UnrealId, Item> ();
		/** Map of all known items of the specific type. */
		private HashMap<UnrealId, Item> known = new HashMap<UnrealId, Item> ();
		/** Map of all items (known and thrown) of specific categories. */
		private HashMapMap<ItemType, UnrealId, Item> allCategories = new HashMapMap<ItemType, UnrealId, Item>();
		/** Map of visible items of the specific type. */
		private HashMapMap<ItemType, UnrealId, Item> visibleCategories = new HashMapMap<ItemType, UnrealId, Item> ();
		/** Map of visible items of the specific type. */
		private HashMapMap<ItemType, UnrealId, Item> reachableCategories = new HashMapMap<ItemType, UnrealId, Item> ();
		/** Map of all known items of the specific type. */
		private HashMapMap<ItemType, UnrealId, Item> knownCategories = new HashMapMap<ItemType, UnrealId, Item> ();
		/**Map of all items and the time when they were last seen as 'missing' (== picked up == they were not on their navpoint). */		
		private TabooSet<Item> itemMissing;	
		/** Set of items that were picked up in this sync-batch. */
		private Set<Item> justPickedUp = new HashSet<Item>();
		
		private HashMap<UnrealId, Boolean> itemSpawned = new HashMap<UnrealId, Boolean>();
		
		public ItemMaps(UT2004Bot bot) {
			itemMissing = new TabooSet<Item>(bot);		
		}

		private void notify(NavPoint navPoint) {
			if (navPoint.getItem() == null) return; // NOT AN INVENTORY SPOT
			Item item = getItem(navPoint.getItem());
			if (item == null) return; // MISSING ITEM? ... should not happen...
			// we have an inventory spot
			if (navPoint.isItemSpawned()) {
				// item is spawned...
				itemMissing.remove(item);
			} else {
				if (itemMissing.isTaboo(item)) {
					// item is already a taboo
					return;
				}
				itemMissing.add(item, getItemRespawnUT2004Time(item));
			}
		}
		
		/**
		 * Processes events.
		 * @param item Item to process.
		 */
		private void notify(Item item)
		{
			UnrealId uid = item.getId();

			// be sure to be within all
			if (!all.containsKey(uid)) {
				all.put(uid, item);
				allCategories.put(item.getType(), item.getId(), item);
			}

			// previous visibility
			boolean wasVisible = visible.containsKey(uid);
			boolean isVisible = item.isVisible();

			// refresh visible
			if (isVisible && !wasVisible)
			{
				// add to visible(s)
				visible.put(uid, item);
				visibleCategories.put(item.getType(), item.getId(), item);
			}
			else if (!isVisible && wasVisible)
			{
				// remove from visible(s)
				visible.remove(uid);
				visibleCategories.remove2(item.getType(), item.getId());
			}

			// remove non-visible thrown items
			if (!isVisible && item.isDropped()) {
				all.remove(uid);
				allCategories.remove2(item.getType(), item.getId());
			}
	
		}
		
		/**
		 * Processes events.
		 * @param items Map of known items to process.
		 */
		private void notify(Map<UnrealId, Item> items)
		{
			// register all known items
			known.putAll(items);
			for (Item item : items.values()) {
				knownCategories.put(item.getType(), item.getId(), item);
				notify(item);
			}
		}
		
		/**
		 * Handles 'itemMissingTimes', called from the {@link EndMessageListener}.
		 * @param navPoints
		 */
		private void notifyBatchEnd(List<NavPoint> navPoints) {
			justPickedUp.clear();
		}

		/**
		 * Handles 'itemMissingTimes' for picked up item.
		 * @param event
		 */
		private void notify(ItemPickedUp event) {
			Item item = all.get(event.getId());
			if (item == null) return;
			justPickedUp.add(item);
			itemMissing.add(item, getItemRespawnUT2004Time(item));
		}

		private void clear() {
			all.clear();
			allCategories.clear();
			itemMissing.clear();
			justPickedUp.clear();
			known.clear();
			knownCategories.clear();
			reachable.clear();
			reachableCategories.clear();
			visible.clear();
			visibleCategories.clear();			
		}
	}

	/** Maps of all items. */
	private ItemMaps items;

	/*========================================================================*/

	protected class ItemsListener implements IWorldObjectEventListener<Item, IWorldObjectEvent<Item>> {

		public ItemsListener(IWorldView worldView) {
			worldView.addObjectListener(Item.class, IWorldObjectEvent.class, this);
		}

        public void notify(IWorldObjectEvent<Item> event) {
            items.notify(event.getObject());
        }

    }

	protected ItemsListener itemsListener;

	/**
	 *  This method is called from the constructor to hook a listener that updated the items field.
	 *  <p><p>
	 *  It must:
	 *  <ol>
	 *  <li>initialize itemsListener field</li>
	 *  <li>hook the listener to the world view</li>
	 *  </ol>
	 *  <p><p>
	 *  By overriding this method you may provide your own listener that may wrap Items with your class
	 *  adding new fields into them.
	 *
	 *  @param worldView
	 **/
	protected ItemsListener createItemsListener(IWorldView worldView) {
		return new ItemsListener(worldView);
	}

	/*========================================================================*/

	/**
	 * MapPointsListObtained listener.
	 */
	protected class MapPointsListener implements IWorldEventListener<MapPointListObtained>
	{
		/**
		 * Constructor. Registers itself on the given WorldView object.
		 * @param worldView WorldView object to listen to.
		 */
		public MapPointsListener(IWorldView worldView)
		{
			worldView.addEventListener(MapPointListObtained.class, this);
		}

		@Override
		public void notify(MapPointListObtained event)
		{
			navPoints = event.getNavPoints();
			items.notify(event.getItems());			
		}

	}

	/** MapPointsListObtained listener */
	protected MapPointsListener mapPointsListener;
	protected Map<UnrealId, NavPoint> navPoints = new HashMap<UnrealId, NavPoint>();

	/**
	 *  This method is called from the constructor to create a listener that initialize items field from
	 *  the MapPointListObtained event.
	 *  <p><p>
	 *  By overriding this method you may provide your own listener that may wrap Items with your class
	 *  adding new fields into them.
	 *
	 *  @param worldView
	 * @return
	 **/
	protected MapPointsListener createMapPointsListener(IWorldView worldView) {
		return new MapPointsListener(worldView);
	}

	/*========================================================================*/
	
	/**
	 * MapPointsListObtained listener.
	 */
	protected class NavPointListener implements IWorldObjectEventListener<NavPoint, WorldObjectUpdatedEvent<NavPoint>>
	{
		/**
		 * Constructor. Registers itself on the given WorldView object.
		 * @param worldView WorldView object to listen to.
		 */
		public NavPointListener(IWorldView worldView) {
			worldView.addObjectListener(NavPoint.class, WorldObjectUpdatedEvent.class, this);
		}

		@Override
		public void notify(WorldObjectUpdatedEvent<NavPoint> event) {
			items.notify(event.getObject());
			if (event.getObject().isVisible() || event.getObject().isItemSpawned()) {
				navPointsToProcess.add(event.getObject());
			} else {
				navPointsToProcess.remove(event.getObject());
			}
		}

	}
	
	/**
	 * Contains only navpoints that are visible so we can check whether they are spawning-points,
	 * if so - we may check whether the item is laying there or not to handle spawning times.
	 */
	protected List<NavPoint> navPointsToProcess = new ArrayList<NavPoint>();
	
	protected NavPointListener navPointListener;
	
	/*========================================================================*/
	
	/**
	 * {@link EndMessage} listener.
	 */
	protected class EndMessageListener implements IWorldEventListener<EndMessage>
	{
		/**
		 * Constructor. Registers itself on the given WorldView object.
		 * @param worldView WorldView object to listen to.
		 */
		public EndMessageListener(IWorldView worldView)
		{
			worldView.addEventListener(EndMessage.class, this);
		}

		@Override
		public void notify(EndMessage event)
		{
			items.notifyBatchEnd(navPointsToProcess);
		}

	}
	
	protected EndMessageListener endMessageListener;
	
	/*========================================================================*/
	
	/**
	 * {@link ItemPickedUp} listener.
	 */
	protected class ItemPickedUpListener implements IWorldEventListener<ItemPickedUp>
	{
		/**
		 * Constructor. Registers itself on the given WorldView object.
		 * @param worldView WorldView object to listen to.
		 */
		public ItemPickedUpListener(IWorldView worldView)
		{
			worldView.addEventListener(ItemPickedUp.class, this);
		}

		@Override
		public void notify(ItemPickedUp event)
		{
			items.notify(event);
		}

	}
	
	protected ItemPickedUpListener itemPickedUpListener;

	/*========================================================================*/

	/** AgentInfo memory module. */
	protected AgentInfo agentInfo;
	
	/** Weaponry memory module. */
	protected Weaponry weaponry;
	
	/** Game memory module. */
	protected Game game;

	
	/**
	 * Constructor. Setups the memory module based on bot's world view.
	 * @param bot owner of the module that is using it
	 * @param agentInfo AgentInfo memory module
	 * @param log Logger to be used for logging runtime/debug info, if null is provided the module creates its own logger
	 */
	public Items(UT2004Bot bot, AgentInfo agentInfo, Game game, Weaponry weaponry, Logger log)
	{
		super(bot, log);

		// save reference
		this.agentInfo = agentInfo;
		NullCheck.check(this.agentInfo, "agentInfo");
		
		this.weaponry = weaponry;
		NullCheck.check(this.weaponry, "weaponry");
		
		this.game = game;
		NullCheck.check(this.game, "game");

		items = new ItemMaps(bot);
		
		// create listeners
		itemsListener =        createItemsListener(worldView);
		mapPointsListener =    createMapPointsListener(worldView);
		navPointListener =     new NavPointListener(worldView);
		endMessageListener =   new EndMessageListener(worldView);
		itemPickedUpListener = new ItemPickedUpListener(worldView);
		
		cleanUp();
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();
		navPoints.clear();
		items.clear();
	}
	
}