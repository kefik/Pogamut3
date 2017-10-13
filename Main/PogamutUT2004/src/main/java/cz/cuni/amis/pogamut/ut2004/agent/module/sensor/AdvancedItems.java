package cz.cuni.amis.pogamut.ut2004.agent.module.sensor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import cz.cuni.amis.pogamut.ut2004.bot.IUT2004BotController;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import cz.cuni.amis.utils.NullCheck;

/**
 * Module specialized on items on the map. This module extends the {@link Items}
 * module by adding filters for usability and/or "takeability". These filters may
 * help the agent to decide, whether it is useful for him, to forage specific
 * items from the map.
 * <p><p>
 * You have to provide the implementation of {@link IItemUsefulness} interface 
 * where you have to specify <i>what usefulness is</i>. 
 * <p><p>
 * Note that it might be useful to instantiate this class multiple times with different item filters to achieve
 * various specialized filters (e.g., different useful filter for weapons and health).
 * <p><p>
 * It is designed to be initialized inside {@link IUT2004BotController#initializeController(UT2004Bot)} method call
 * and may be used since {@link IUT2004BotController#botInitialized(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage)}
 * is called.
 *
 * @author Juraj 'Loque' Simlovic
 * @author Jimmy
 */
public class AdvancedItems
{
	
	/**
	 * Returns a filtered list of items that are guessed to be currently spawned in the map.
	 *
	 * <p><p>WARNING: O(n) complexity!
	 * 
	 * <p><p>This implementation is guessing (optimistically) whether the item is spawned based on
	 * the last time we have seen it missing (saw its navpoint but the item was not laying there).
	 * 
	 * <p><p>Note that the guessing is not perfect, experiment with it or check the source code
	 * and possibly reimplement to suit your needs.
	 * 
	 * <p><p>Note that this method is working only if items are respawning.
	 *
	 * @param  usefulness degree of usefulness, 0 - return also useless, 1 - return only <b>really truly</b> useful items which are MUST HAVE!
	 * @return True, if the item is spawned; false if the pickup is empty.
	 */
	public Map<UnrealId, Item> getSpawnedItems(double usefulness) {
		return filterUsefulItems(items.getSpawnedItems().values(), usefulness);
	}
	
	/**
	 * Returns a filtered list of items of a <b>specific type</b> that are guessed to be currently spawned in the map.
	 *
	 * <p><p>WARNING: O(n) complexity!
	 * 
	 * <p><p>This implementation is guessing (optimistically) whether the item is spawned based on
	 * the last time we have seen it missing (saw its navpoint but the item was not laying there).
	 * 
	 * <p><p>Note that the guessing is not perfect, experiment with it or check the source code
	 * and possibly reimplement to suit your needs.
	 * 
	 * <p><p>Note that this method is working only if items are respawning.
	 *
	 * @param  usefulness degree of usefulness, 0 - return also useless, 1 - return only <b>really truly</b> useful items which are MUST HAVE!
	 * @return Map of spawned items.
	 */
	public Map<UnrealId, Item> getSpawnedItems(ItemType type, double usefulness) {
		return filterUsefulItems(items.getSpawnedItems(type).values(), usefulness);
	}
	
	/**
	 * Returns a filtered list of items of a <b>specific category</b> that are guessed to be currently spawned in the map.
	 *
	 * <p><p>WARNING: O(n) complexity!
	 * 
	 * <p><p>This implementation is guessing (optimistically) whether the item is spawned based on
	 * the last time we have seen it missing (saw its navpoint but the item was not laying there).
	 * 
	 * <p><p>Note that the guessing is not perfect, experiment with it or check the source code
	 * and possibly reimplement to suit your needs.
	 * 
	 * <p><p>Note that this method is working only if items are respawning.
	 *
	 * @param  usefulness degree of usefulness, 0 - return also useless, 1 - return only <b>really truly</b> useful items which are MUST HAVE!
	 * @return Map of spawned items.
	 */
	public Map<UnrealId, Item> getSpawnedItems(ItemType.Category category, double usefulness) {
		return filterUsefulItems(items.getSpawnedItems(category).values(), usefulness);
	}
	
	/**
	 * Returns a filtered list of items of a <b>specific group</b> that are guessed to be currently spawned in the map.
	 *
	 * <p><p>WARNING: O(n) complexity!
	 * 
	 * <p><p>This implementation is guessing (optimistically) whether the item is spawned based on
	 * the last time we have seen it missing (saw its navpoint but the item was not laying there).
	 * 
	 * <p><p>Note that the guessing is not perfect, experiment with it or check the source code
	 * and possibly reimplement to suit your needs.
	 * 
	 * <p><p>Note that this method is working only if items are respawning.
	 *
	 * @param  usefulness degree of usefulness, 0 - return also useless, 1 - return only <b>really truly</b> useful items which are MUST HAVE!
	 * @return Map of spawned items.
	 */
	public Map<UnrealId, Item> getSpawnedItems(ItemType.Group group, double usefulness) {
		return filterUsefulItems(items.getSpawnedItems(group).values(), usefulness);
	}
	
	/*========================================================================*/

	/**
	 * Retrieves map of all items (both known and thrown).
	 * 
	 * <p><p>WARNING: O(n) complexity!
	 *
	 * @param usefulness degree of usefulness, 0 - return also useless, 1 - return only <b>really truly</b> useful items which are MUST HAVE!
	 * @return Map of all items. Note: non-spawned items are included too.
	 */
	public Map<UnrealId, Item> getAllItems(double usefulness)
	{		
		return filterUsefulItems (
			items.getAllItems().values(), usefulness
		);
	}
	
	/**
	 * Retrieves map of all items of <b>specific type</b> (both known and thrown).
	 * 
	 * <p><p>WARNING: O(n) complexity!
	 *
	 * @param usefulness degree of usefulness, 0 - return also useless, 1 - return only <b>really truly</b> useful items which are MUST HAVE!
	 * @return Map of all items. Note: non-spawned items are included too.
	 */
	public Map<UnrealId, Item> getAllItems(ItemType type, double usefulness)
	{		
		return filterUsefulItems (items.getAllItems(type).values(), usefulness);
	}
	
	/**
	 * Retrieves map of all items of <b>specific category</b> (both known and thrown).
	 * 
	 * <p><p>WARNING: O(n) complexity!
	 *
	 * @param usefulness degree of usefulness, 0 - return also useless, 1 - return only <b>really truly</b> useful items which are MUST HAVE!
	 * @return Map of all items. Note: non-spawned items are included too.
	 */
	public Map<UnrealId, Item> getAllItems(ItemType.Category category, double usefulness)
	{		
		return filterUsefulItems (items.getAllItems(category).values(), usefulness);
	}
	
	/**
	 * Retrieves map of all items of <b>specific group</b> (both known and thrown).
	 * 
	 * <p><p>WARNING: O(n) complexity!
	 *
	 * @param usefulness degree of usefulness, 0 - return also useless, 1 - return only <b>really truly</b> useful items which are MUST HAVE!
	 * @return Map of all items. Note: non-spawned items are included too.
	 */
	public Map<UnrealId, Item> getAllItems(ItemType.Group group, double usefulness)
	{		
		return filterUsefulItems (items.getAllItems(group).values(), usefulness);
	}

	/*========================================================================*/

	/**
	 * Retrieves map of all visible items.
	 * 
	 * <p><p>WARNING: O(n) complexity!
	 *
	 * @param usefulness degree of usefulness, 0 - return also useless, 1 - return only <b>really truly</b> useful items which are MUST HAVE!
	 * @return Map of all visible items. Note: Spawned items are included only.
	 */
	public Map<UnrealId, Item> getVisibleItems(double usefulness)
	{
		return filterUsefulItems(items.getVisibleItems().values(), usefulness);
	}
	
	/**
	 * Retrieves map of all visible items of <b>specific type</b>.
	 * 
	 * <p><p>WARNING: O(n) complexity!
	 *
	 * @param usefulness degree of usefulness, 0 - return also useless, 1 - return only <b>really truly</b> useful items which are MUST HAVE!
	 * @return Map of all visible items. Note: Spawned items are included only.
	 */
	public Map<UnrealId, Item> getVisibleItems(ItemType type, double usefulness)
	{
		return filterUsefulItems(items.getVisibleItems(type).values(), usefulness);
	}
	
	/**
	 * Retrieves map of all visible items of <b>specific category</b>.
	 * 
	 * <p><p>WARNING: O(n) complexity!
	 *
	 * @param usefulness degree of usefulness, 0 - return also useless, 1 - return only <b>really truly</b> useful items which are MUST HAVE!
	 * @return Map of all visible items. Note: Spawned items are included only.
	 */
	public Map<UnrealId, Item> getVisibleItems(ItemType.Category category, double usefulness)
	{
		return filterUsefulItems(items.getVisibleItems(category).values(), usefulness);
	}
	
	/**
	 * Retrieves map of all visible items of <b>specific group</b>.
	 * 
	 * <p><p>WARNING: O(n) complexity!
	 *
	 * @param usefulness degree of usefulness, 0 - return also useless, 1 - return only <b>really truly</b> useful items which are MUST HAVE!
	 * @return Map of all visible items. Note: Spawned items are included only.
	 */
	public Map<UnrealId, Item> getVisibleItems(ItemType.Group group, double usefulness)
	{
		return filterUsefulItems(items.getVisibleItems(group).values(), usefulness);
	}

	/*========================================================================*/

	/**
	 * Retrieves map of all item pickup points.
	 *
	 * <p><p>WARNING: O(n) complexity!
	 *
	 * @param usefulness degree of usefulness, 0 - return also useless, 1 - return only <b>really truly</b> useful items which are MUST HAVE!
	 * @return Map of all items. Note: Empty pickups are included as well.
	 *
	 * @see isPickupSpawned(Item)
	 */
	public Map<UnrealId, Item> getKnownPickups(double usefulness)
	{
		return filterUsefulItems(items.getKnownPickups().values(), usefulness);
	}
	
	/**
	 * Retrieves map of all item pickup points of items of a <b>specific type</b>.
	 *
	 * <p><p>WARNING: O(n) complexity!
	 *
	 * @param usefulness degree of usefulness, 0 - return also useless, 1 - return only <b>really truly</b> useful items which are MUST HAVE!
	 * @return Map of all items. Note: Empty pickups are included as well.
	 *
	 * @see isPickupSpawned(Item)
	 */
	public Map<UnrealId, Item> getKnownPickups(ItemType type, double usefulness)
	{
		return filterUsefulItems(items.getKnownPickups(type).values(), usefulness);
	}
	
	/**
	 * Retrieves map of all item pickup points of items of a <b>specific category</b>.
	 *
	 * <p><p>WARNING: O(n) complexity!
	 *
	 * @param usefulness degree of usefulness, 0 - return also useless, 1 - return only <b>really truly</b> useful items which are MUST HAVE!
	 * @return Map of all items. Note: Empty pickups are included as well.
	 *
	 * @see isPickupSpawned(Item)
	 */
	public Map<UnrealId, Item> getKnownPickups(ItemType.Category category, double usefulness)
	{
		return filterUsefulItems(items.getKnownPickups(category).values(), usefulness);
	}
	
	/**
	 * Retrieves map of all item pickup points of items of a <b>specific group</b>.
	 *
	 * <p><p>WARNING: O(n) complexity!
	 *
	 * @param usefulness degree of usefulness, 0 - return also useless, 1 - return only <b>really truly</b> useful items which are MUST HAVE!
	 * @return Map of all items. Note: Empty pickups are included as well.
	 *
	 * @see isPickupSpawned(Item)
	 */
	public Map<UnrealId, Item> getKnownPickups(ItemType.Group group, double usefulness)
	{
		return filterUsefulItems(items.getKnownPickups(group).values(), usefulness);
	}


	/*========================================================================*/

	/**
	 * Determines, whether an item can be useful for the agent.
	 *
	 * @param item Given item to be checked.
	 * @param usefulness degree of usefulness, 0 - return also useless, 1 - return only <b>really truly</b> useful items which are MUST HAVE!
	 * @return True, if the item can be useful.
	 */
	public boolean isItemUseful(Item item, double usefulness) {
		if (usefulness < 0) usefulness = 0;
		else if (usefulness > 1) usefulness = 1;
		return filter.getItemUsefulness(this, items, item, usefulness) >= usefulness;
	}

	/**
	 * Determines, whether an item can be useful for the agent.
	 *
	 * <p><p>WARNING: O(n) complexity!
	 * 
	 * @param items Items to be filtered on.
	 * @param usefulness degree of usefulness, 0 - return also useless, 1 - return only <b>really truly</b> useful items which are MUST HAVE!
	 * @return Map of all useful items.
	 */
	public Map<UnrealId, Item> filterUsefulItems(Collection<Item> items, double usefulness)
	{
		// new empty list
		Map<UnrealId, Item> map = new HashMap<UnrealId, Item> ();

		// run through items and filter them out
		for (Item i: items) {
			if (isItemUseful(i, usefulness)) {
				// add only useful to the results
				map.put(i.getId(), i);
			}
		}

		return map;
	}

	/*========================================================================*/

	/**
	 * Returns underlying {@link Items} module.
	 */
	public Items getItems() {
		return items;
	}	

	/**
	 * Filter of useful items.
	 */
	private IItemUsefulness filter;
	

	/**
	 * Items memory module.
	 */
	private Items items;
	
	/**
	 * Constructor. Setups the memory module based on bot's world view.
	 * @param items items memory module
	 * @param filter the filter of the usefulness 
	 */
	public AdvancedItems(Items items, IItemUsefulness filter)
	{
		this.items = items;
		NullCheck.check(this.items, "items");
		this.filter = filter;
	}

}
