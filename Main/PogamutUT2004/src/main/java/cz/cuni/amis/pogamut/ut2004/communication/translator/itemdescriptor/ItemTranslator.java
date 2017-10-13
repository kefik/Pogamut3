package cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor;

import java.util.HashMap;

import com.google.inject.Inject;

import cz.cuni.amis.pogamut.base.communication.exception.CommunicationException;
import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemTypeTranslator;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ItemCategory;

/**
 * Main class responsible for the item decoration.
 * <p>
 * <p>
 * Items in UT2004 has a lots of characteristics which don't change over the
 * time (at least during one game). As it is pointless to send all those
 * information every time an item is perceived by a bot, those information are
 * sent through the ITCMsgs (ItemCategory). This message is used in
 * ItemTranslator as a configuration message for an ItemDescriptor.
 * <p>
 * <p>
 * ItemDescriptor contains all characteristics available for the corresponding
 * UTClass of items and is returned by the ItemTranslator. This description is
 * then attached to the item (in fact to all item events like AIN, INV, IPK).
 * <p>
 * <p>
 * Now how does it work insight? ItemTranslator uses a set of
 * DescriptorFactories (one for each type of an item). ITCMsg messages are
 * usually sent at the beginning of the game (classes for all items in the map).
 * But they can arrive in the middle of the game for a new category of an item.
 * 
 * TODO: maybe it is rather ItemDecorator.
 * 
 * @author Ondrej
 */
@AgentScoped
public class ItemTranslator {

	private HashMap<ItemType, ItemDescriptor> descriptors = new HashMap<ItemType, ItemDescriptor>();

	private HashMap<ItemType, GeneralDescriptor> userDescriptors = new HashMap<ItemType, GeneralDescriptor>();

	// TODO: These work for UT3 too? If not we may have to use dependency
	// injection and make the descriptors described what they can decorate.
	private final IDescriptorFactory<WeaponDescriptor> weaponDescriptorFactory;
	private final IDescriptorFactory<AdrenalineDescriptor> adrenalineDescriptorFactory = new AdrenalineDescriptorFactory();
	private final IDescriptorFactory<ShieldDescriptor> shieldDescriptorFactory = new ShieldDescriptorFactory();
	private final IDescriptorFactory<HealthDescriptor> healthDescriptorFactory = new HealthDescriptorFactory();
	private final IDescriptorFactory<OtherDescriptor> otherDescriptorFactory = new OtherDescriptorFactory();
	private final IDescriptorFactory<ArmorDescriptor> armorDescriptorFactory = new ArmorDescriptorFactory();
	private final IDescriptorFactory<AmmoDescriptor> ammoDescriptorFactory = new AmmoDescriptorFactory();

	private final ItemTypeTranslator itemTypeTranslator;

	@Inject
	public ItemTranslator(ItemTypeTranslator translator) {
		this.itemTypeTranslator = translator;
		this.weaponDescriptorFactory = new WeaponDescriptorFactory(
				itemTypeTranslator);
	}

	public ItemType[] getItemTypes() {
		return descriptors.values().toArray(new ItemType[0]);
	}

	public ItemDescriptor getDescriptor(ItemTyped msg) {
		return getDescriptor(msg.getType());
	}

	/**
	 * Gets descriptor for this item.
	 * 
	 * NOTE: User descriptors will override default Pogamut descriptors.
	 * 
	 * @param type
	 * @return
	 */
	public ItemDescriptor getDescriptor(ItemType type) {
		// user may override our default descriptors
		if (userDescriptors.containsKey(type))
			return userDescriptors.get(type);
		return descriptors.get(type);
	}

	/**
	 * Gets default Pogamut descriptor for this item.
	 * 
	 * @param type
	 * @return
	 */
	public ItemDescriptor getDefaultDescriptor(ItemType type) {
		return descriptors.get(type);
	}

	/**
	 * Adds custom user descriptor.
	 * 
	 * @param userDescriptor
	 */
	public void addCustomUserDescriptor(GeneralDescriptor userDescriptor) {
		userDescriptors.put(userDescriptor.getPickupType(), userDescriptor);
	}

	/**
	 * Default Pogamut descriptors will be created for all UT2004 items.
	 * 
	 * @param message
	 */
	public ItemDescriptor createDescriptor(ItemCategory message) {
		ItemDescriptor result = null;
		switch (message.getType().getCategory()) {
		case AMMO:
			descriptors.put(message.getType(),
					result = ammoDescriptorFactory.getNewDescriptor(message));
			break;
		case ARMOR:
			descriptors.put(message.getType(),
					result = armorDescriptorFactory.getNewDescriptor(message));
			break;
		case OTHER:
			descriptors.put(message.getType(),
					result = otherDescriptorFactory.getNewDescriptor(message));
			break;
		case HEALTH:
			descriptors.put(message.getType(),
					result = healthDescriptorFactory.getNewDescriptor(message));
			break;
		case SHIELD:
			descriptors.put(message.getType(),
					result = shieldDescriptorFactory.getNewDescriptor(message));
			break;
		case ADRENALINE:
			descriptors.put(
					message.getType(),
					result = adrenalineDescriptorFactory
							.getNewDescriptor(message));
			break;
		case WEAPON:
			descriptors.put(message.getType(),
					result = weaponDescriptorFactory.getNewDescriptor(message));
			break;
		default:
			throw new CommunicationException(
					"should not reach here - new ItemType.Category has been added and not handled inside the ItemTranslator, item type = "
							+ message.getType(), this);
		}
		return result;
	}
}
