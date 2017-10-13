package cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric;

import java.util.logging.Level;

import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.ItemDescriptors;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.UT2004ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType.Category;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ItemPickedUp;
import cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor.ItemDescriptor;
import cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor.WeaponDescriptor;

/**
 * Handles specific needs of UT2004 with regards to weaponry. Currently fixes
 * broken item descriptors.
 * 
 * @author mpkorstanje
 * 
 */
public class UT2004Weaponry extends Weaponry {

	private class RedeemerPickedUpListener implements
			IWorldEventListener<ItemPickedUp> {

		/**
		 * Constructor. Registers itself on the given WorldView object.
		 * 
		 * @param worldView
		 *            WorldView object to listent to.
		 */
		public RedeemerPickedUpListener(IWorldView worldView) {
			worldView.addEventListener(ItemPickedUp.class, this);
		}

		@Override
		public void notify(ItemPickedUp event) {
			if (event == null)
				return;
			if (event.getType() == null)
				return;
			if (event.getType().getCategory() != Category.AMMO
					&& event.getType().getCategory() != Category.WEAPON) {
				return;
			}

			if (event.getDescriptor().getPickupType() == UT2004ItemType.REDEEMER
					|| event.getDescriptor().getPickupType() == UT2004ItemType.REDEEMER_AMMO
					|| event.getDescriptor().getPickupType() == UT2004ItemType.ION_PAINTER
					|| event.getDescriptor().getPickupType() == UT2004ItemType.ION_PAINTER_AMMO)

			{

				ItemDescriptor descriptor = itemDescriptors.getDescriptor(event
						.getType());

				if (descriptor == null) {
					if (log.isLoggable(Level.WARNING))
						log.warning("RedeemerPickedUpListener.notify(): There is no ItemDescriptor for the item type "
								+ event.getType() + "!");
					return;
				}

				// UT2004 BUG !!! ... desc.getAmount()/priAmmo()/secAmmo() is 0
				// !!! ... it should be 1 !!!
				if (descriptor.getItemCategory() == Category.AMMO) {
					ammo.weaponUpdate(event.getType(), 1);
				} else if (descriptor.getItemCategory() == Category.WEAPON) {
					WeaponDescriptor desc = (WeaponDescriptor) descriptor;
					ammo.weaponUpdate(desc.getPriAmmoItemType(), 1);
				}

				return;
			}
		}
	}

	private RedeemerPickedUpListener redeemerPickedUpListener;

	/**
	 * Constructor. Setups the memory module for a given bot.
	 * 
	 * @param bot
	 *            owner of the module that is using it
	 * @param agentInfo
	 *            AgentInfo memory module.
	 * @param itemDescriptors
	 *            ItemDescriptors memory module.
	 * @param moduleLog
	 */
	public UT2004Weaponry(UT2004Bot bot, ItemDescriptors descriptors,
			LogCategory moduleLog) {
		super(bot);

		redeemerPickedUpListener = new RedeemerPickedUpListener(worldView);
	}
	
	
	  /**
     * Constructor. Setups the memory module for a given bot.
     * @param bot owner of the module that is using it
     * @param agentInfo AgentInfo memory module.
     * @param itemDescriptors ItemDescriptors memory module.
     */
    public UT2004Weaponry(UT2004Bot bot, ItemDescriptors itemDescriptors) {
        this(bot, itemDescriptors, null);
    }
    

}
