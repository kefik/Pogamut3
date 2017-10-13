package cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric;

import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType.Category;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.ChangeWeapon;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.AddInventoryMsg;
import cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor.WeaponDescriptor;
import cz.cuni.amis.utils.exception.PogamutException;

/**
 * Class that holds information about the weapon the bot has in its inventory.
 * <p><p>
 * It provides information about number of primary and secondary ammo the bot has for the weapon
 * as well as weapon's {@link WeaponDescriptor}, its {@link ItemType} and inventory {@link UnrealId}.
 * 
 * @author Jimmy
 */
public class Weapon {

	protected ItemType weaponType = null;
	protected int primaryAmmo = 0;
	protected int secondaryAmmo = 0;
	protected UnrealId inventoryId;
	protected WeaponDescriptor descriptor = null;
	
	protected Weapon(AddInventoryMsg weaponGained, int primaryAmmo, int secondaryAmmo) {
		if (weaponGained.getPickupType().getCategory() != Category.WEAPON) {
			throw new PogamutException("Could not create Weapon class out of inventory item that is not a weapon.", this);
		}
		this.weaponType = weaponGained.getPickupType();
		this.inventoryId = weaponGained.getId();
		this.descriptor = (WeaponDescriptor) weaponGained.getDescriptor();
		this.primaryAmmo = primaryAmmo;
		this.secondaryAmmo = secondaryAmmo;
	}
	
	/**
	 * Returns type of the weapon.
	 * @return
	 */
	public ItemType getType() {
		return weaponType;
	}
	
	/**
	 * Returns group of the weapon.
	 * @return
	 */
	public ItemType.Group getGroup() {
		return weaponType.getGroup();
	}

	/**
	 * Returns how many primary ammo the bot is wielding for this weapon.
	 * @return
	 */
	public int getPrimaryAmmo() {
		return primaryAmmo;
	}

	/**
	 * Returns how many secondary ammo the bot is wielding for this weapon.
	 * @return
	 */
	public int getSecondaryAmmo() {
		return secondaryAmmo;
	}

	/**
	 * Returns inventory ID of the weapon.
	 * <p><p>
	 * This id is sought to be used with {@link ChangeWeapon} command, use {@link UnrealId#getStringId()} to obtain
	 * the string representation of the weapon's inventory ID.
	 * 
	 * @return
	 */
	public UnrealId getInventoryId() {
		return inventoryId;
	}

	/**
	 * Returns complete descriptor of the weapon containing various information about the weapon behavior in game.
	 */
	public WeaponDescriptor getDescriptor() {
		return descriptor;
	}
	
//	/**
//	 * Returns maximal effective distance of the weapon's primary firing mode.
//	 * @return
//	 */
// getPriMaxEffectDistance is almost always 0
//	public double getEffectiveDistance() {
//		return descriptor.getPriMaxEffectDistance();
//	}
	
	/**
     * Whether the weapon has secondary ammo different from the primary.
     * @return
     */
    public boolean hasSecondaryAmmoType() {
    	return getDescriptor().getSecAmmoItemType() != null && getDescriptor().getPriAmmoItemType() != getDescriptor().getSecAmmoItemType();    		
    }

	/**
	 * Returns total amount of ammo the bot has for the weapon (both primary and secondary).
	 * @return
	 */
	public int getAmmo() {
		return getPrimaryAmmo() + getSecondaryAmmo();
	}
	
	@Override
	public String toString() {
		if (getType() == null) {
			return "Weapon[type=UNKNOWN, primary ammo=" + getPrimaryAmmo() + ", secondary ammo=" + getSecondaryAmmo() + "]";
		} else
		if (hasSecondaryAmmoType()) {
			return "Weapon[type=" + getType().getName() + ", primary ammo=" + getPrimaryAmmo() + ", secondary ammo=" + getSecondaryAmmo() + "]";
		} else {
			return "Weapon[type=" + getType().getName() + ", ammo=" + getPrimaryAmmo() + "]";
		}
	}

}
