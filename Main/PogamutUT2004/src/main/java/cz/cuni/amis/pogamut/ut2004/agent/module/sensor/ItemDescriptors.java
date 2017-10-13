package cz.cuni.amis.pogamut.ut2004.agent.module.sensor;

import java.util.HashMap;
import java.util.Map;

import cz.cuni.amis.pogamut.base.agent.module.SensorModule;
import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.pogamut.ut2004.bot.IUT2004BotController;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor.ItemDescriptor;
import cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor.WeaponDescriptor;
import cz.cuni.amis.pogamut.ut2004.communication.translator.shared.events.ItemDescriptorObtained;

/**
 * Sensory module that provides mapping between {@link ItemType} and {@link ItemDescriptor} providing
 * an easy way to obtain item descriptors for various items in UT2004.
 * <p><p>
 * Additionally it provides ammo-&gt;weapon mapping via {@link ItemDescriptors#getWeaponForAmmo(ItemType)}.
 * <p><p>
 * It is designed to be initialized inside {@link IUT2004BotController#prepareBot(UT2004Bot)} method call
 * and may be used since {@link IUT2004BotController#botInitialized(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage)}
 * is called.
 * 
 * @author Jimmy
 */
public class ItemDescriptors extends SensorModule<UT2004Bot> {

	private Map<String, ItemDescriptor> inventoryTypeDescs = new HashMap<String, ItemDescriptor>();
	
	private Map<ItemType, ItemDescriptor> descs = new HashMap<ItemType, ItemDescriptor>();
	
	private HashMap<ItemType, ItemType> ammoToWeapon = new HashMap<ItemType, ItemType>();
	
	/**
	 * Returns a weapon type for the given 'ammoType'.
	 * @param ammoType
	 * @return
	 */
	public ItemType getWeaponForAmmo(ItemType ammoType) {
		if (ammoType == null) return null;
		return ammoToWeapon.get(ammoType);
	}
	
	/**
	 * Tells whether the descriptor for given 'itemType' exists.
	 * @param itemType
	 * @return whether the descriptor for given 'itemType' exists
	 */
	public boolean hasDescriptor(ItemType itemType) {
		if (itemType == null) return false;
		return descs.containsKey(itemType);
	}
	
	/**
	 * Returns the descriptor for the given 'itemType'.
	 * @param itemType
	 * @return descriptor for given 'itemType' exists
	 */
	public ItemDescriptor getDescriptor(ItemType itemType) {
		if (itemType == null) return null;
		return descs.get(itemType);
	}
	
	/**
	 * Tells whether the descriptor for given 'inventoryType' exists.
	 * @param itemType
	 * @return whether the descriptor for given 'inventoryType' exists
	 */
	public boolean hasDescriptor(String inventoryType) {
		if (inventoryType == null) return false;
		return inventoryTypeDescs.containsKey(inventoryType);
	}
	
	/**
	 * Returns the descriptor for the given 'inventoryType'.
	 * @param itemType
	 * @return descriptor for given 'inventoryType' exists
	 */
	public ItemDescriptor getDescriptor(String inventoryType) {
		if (inventoryType == null) return null;
		return inventoryTypeDescs.get(inventoryType);
	}
	
	/*========================================================================*/
	
	/**
	 * {@link ItemDescriptorObtained} listener.
	 */
	private class ItemDescriptorObtainedListener implements IWorldEventListener<ItemDescriptorObtained> {
		private IWorldView worldView;

		/**
		 * Constructor. Registers itself on the given WorldView object.
		 * @param worldView WorldView object to listent to.
		 */
		public ItemDescriptorObtainedListener(IWorldView worldView)
		{
			worldView.addEventListener(ItemDescriptorObtained.class, this);
			this.worldView = worldView;
		}

		@Override
		public void notify(ItemDescriptorObtained event) {
			log.info("Processing: " + event);
			if (event.getItemDescriptor() == null) {
				return;
			}
			inventoryTypeDescs.put(event.getItemDescriptor().getInventoryType(), event.getItemDescriptor());
			descs.put(event.getItemDescriptor().getPickupType(), event.getItemDescriptor());
			if (event.getItemDescriptor() instanceof WeaponDescriptor) {
				WeaponDescriptor desc = (WeaponDescriptor)event.getItemDescriptor();
				if (desc.getPriAmmoItemType() != null) {
					ammoToWeapon.put(desc.getPriAmmoItemType(), desc.getPickupType());
				}
				if (desc.getSecAmmoItemType() != null) {
					ammoToWeapon.put(desc.getSecAmmoItemType(), desc.getPickupType());
				}
			}
		}
	}

	/** {@link ItemDescriptorObtained} listener */
	private ItemDescriptorObtainedListener itemDescObtainedListener;
	
	/**
	 * Constructor. Setups the memory module based on bot's world view.
	 * @param bot owner of the module
	 */
	public ItemDescriptors(UT2004Bot bot) {
		this(bot, null);
		
	}

	public ItemDescriptors(UT2004Bot bot, LogCategory moduleLog) {
		super(bot, moduleLog);
		itemDescObtainedListener = new ItemDescriptorObtainedListener(bot.getWorldView());
		
		cleanUp();
	}
	
	@Override
	protected void cleanUp() {
		super.cleanUp();
		inventoryTypeDescs.clear();
		descs.clear();
	}
	
}
