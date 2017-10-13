package cz.cuni.amis.pogamut.ut2004.agent.module.sensor;

import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;

/**
 * Interface that allows you to define "usefulness of items". Note that the "usefulness" should depend on the current
 * situation of the bot! That is - if the bot is almost dead, any health should be really useful.
 * <p><p>
 * Used by {@link AdvancedItems} to obtain current level of item's usefulness for the bot.
 * <p><p>
 * Note that it might be useful to define different item filters according to their type of usage (e.g.,
 * different filter for weapons and health).
 * 
 * @author Jimmy
 */
public interface IItemUsefulness {

	/**
	 * Tells how much the item is useful to the bot - must return values between 0 and 1 (inclusive).
	 * 
	 * @param advancedItems
	 * @param item
	 * @param usefulness degree of usefulness, 0 - return also useless, 1 - return only <b>really truly</b> useful items which are MUST HAVE!
	 * @return usefulness of 'item'
	 */
	public double getItemUsefulness(AdvancedItems advancedItems, Items items, Item item, double usefulness);

}
