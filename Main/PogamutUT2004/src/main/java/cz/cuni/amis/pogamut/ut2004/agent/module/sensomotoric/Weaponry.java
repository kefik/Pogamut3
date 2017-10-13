package cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import cz.cuni.amis.pogamut.base.agent.module.SensomotoricModule;
import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.AgentInfo;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.ItemDescriptors;
import cz.cuni.amis.pogamut.ut2004.bot.IUT2004BotController;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType.Category;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.ChangeWeapon;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.AddInventoryMsg;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BotKilled;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ItemPickedUp;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Self;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Thrown;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.WeaponUpdate;
import cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor.AmmoDescriptor;
import cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor.ItemDescriptor;
import cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor.WeaponDescriptor;
import cz.cuni.amis.utils.maps.LazyMap;

/**
 * Memory module specialized on info about the bot's weapon inventory.
 * <p><p>
 * It listens to various events that provides information about weapons the bot picks up as well as weapon's ammo
 * grouping it together and providing {@link Weapon} abstraction of bot's weaponry.
 * <p><p>
 * It also provides a way for easy weapon changing via {@link Weaponry#changeWeapon(ItemType)} and {@link Weaponry#changeWeapon(Weapon)}.
 * <p><p>
 * It is designed to be initialized inside {@link IUT2004BotController#prepareBot(UT2004Bot)} method call
 * and may be used since {@link IUT2004BotController#botInitialized(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage)}
 * is called.
 * <p><p>
 * Hardened version - immune to NPEs (hopefully).
 *
 * @author Jimmy
 */
public class Weaponry extends SensomotoricModule<UT2004Bot> {
	
	/**
	 * Returns {@link WeaponDescriptor} for a given inventory {@link UnrealId} of the weapon.
	 * <p><p>
	 * Note that there exists two types of {@link UnrealId} for every weapon:
	 * <ol>
	 * <li>item unreal id (i.e. from {@link Item})</li>
	 * <li>inventory unreal id (i.e. id of the weapon inside bot's inventory from {@link AddInventoryMsg})</li>
	 * </ol>
	 * 
	 * @param inventoryWeaponId
	 * @return weapon descriptor
	 */
	public WeaponDescriptor getDescriptorForId(UnrealId inventoryWeaponId) {
		WeaponDescriptor desc = inventoryUnrealIdToWeaponDescriptor.get(inventoryWeaponId);
		if (desc == null) {
			if (log.isLoggable(Level.WARNING)) log.warning("getDescriptorForId(): There is no WeaponDescriptor for the inventory weapon id '" + inventoryWeaponId.getStringId() + "'.");
		}
		return desc;
	}
	
	/**
	 * Returns an item type for a given inventory {@link UnrealId} of the weapon.
	 * <p><p>
	 * Note that there exists two types of {@link UnrealId} for every weapon:
	 * <ol>
	 * <li>item unreal id (i.e. from {@link Item})</li>
	 * <li>inventory unreal id (i.e. id of the weapon inside bot's inventory from {@link AddInventoryMsg})</li>
	 * </ol>
	 * 
	 * @param inventoryWeaponId
	 * @return
	 */
	public ItemType getItemTypeForId(UnrealId inventoryWeaponId) {
		WeaponDescriptor desc = inventoryUnrealIdToWeaponDescriptor.get(inventoryWeaponId);
		if (desc == null) {
			if (log.isLoggable(Level.WARNING)) log.warning("getItemTypeForId(): There is no WeaponDescriptor for the inventory weapon id '" + inventoryWeaponId.getStringId() + "'.");
			return null;
		}
		return desc.getPickupType();		
	}
	
	/**
	 * Returns inventory {@link UnrealId} of the weapon the bot has inside its inventory (if the bot does not have
	 * it, returns null).
	 * <p><p>
	 * Note that there exists two types of {@link UnrealId} for every weapon:
	 * <ol>
	 * <li>item unreal id (i.e. from {@link Item})</li>
	 * <li>inventory unreal id (i.e. id of the weapon inside bot's inventory from {@link AddInventoryMsg})</li>
	 * </ol>
	 * This (inventory) unreal id is the one that must be used together with {@link ChangeWeapon} command.
	 * 
	 * @param weaponType
	 * @return inventory unreal id (or null if the bot does not have the weapon)
	 */
	public UnrealId getWeaponInventoryId(ItemType weaponType) {
		return weaponTypeToInventoryUnrealId.get(weaponType);
	}
	
	/**
	 * Returns inventory {@link UnrealId} of the weapon the bot has inside its inventory (if the bot does not have
	 * it, returns null).
	 * <p><p>
	 * Note that there exists two types of {@link UnrealId} for every weapon:
	 * <ol>
	 * <li>item unreal id (i.e. from {@link Item})</li>
	 * <li>inventory unreal id (i.e. id of the weapon inside bot's inventory from {@link AddInventoryMsg})</li>
	 * </ol>
	 * This (inventory) unreal id is the one that must be used together with {@link ChangeWeapon} command.
	 * 
	 * @param weaponType
	 * @return inventory unreal id (or null if the bot does not have the weapon)
	 */
	public UnrealId getWeaponInventoryId(WeaponDescriptor weaponDescriptor) {
		if (weaponDescriptor == null) return null;
		if (weaponDescriptor.getPickupType() == null) {
			if (log.isLoggable(Level.WARNING)) log.warning("getWeaponInventoryId(): WeaponDescriptor does not have PickupType assigned!");
			return null;
		}
		return weaponTypeToInventoryUnrealId.get(weaponDescriptor.getPickupType());
	}
	
	/**
	 * Changes the weapon the bot is currently holding (if the bot has the weapon and its ammo > 0).
	 * 
	 * @param weaponType
	 * @return whether we have changed the weapon (i.e., we have some ammo for it), or we already have it prepared (i.e., is our current weapon)
	 */
	public boolean changeWeapon(ItemType weaponType) {
		if (weaponType == null) return false;
		if (weaponType.getCategory() != ItemType.Category.WEAPON) return false;
		Weapon weapon = getWeapon(weaponType);
		return changeWeapon(getWeapon(weaponType));
	}
	
	/**
	 * Changes the weapon the bot is currently holding (if the weapon's ammo is > 0).
	 * 
	 * @param weapon
	 * @return whether we have changed the weapon (i.e., we have some ammo for it), or we already have it prepared (i.e., is our current weapon).
	 */
	public boolean changeWeapon(Weapon weapon) {
		if (weapon == null) return false;
		if (weapon == getCurrentWeapon()) return true;
		if (weapon.getAmmo() <= 0) return false;
		if (weaponsByItemType.all.get(weapon.getType()) == null) return false;
		act.act(new ChangeWeapon().setId(weapon.getInventoryId().getStringId()));
		return true;
	}
	
	/**
	 * Returns an amount of ammo of 'ammoOrWeaponType' the bot currently has. 
	 * <p><p>
	 * If an ammo type is passed - exact number is returned.
	 * <p><p>
	 * If an weapon type is passed - its primary + secondary ammo amount is returned.
	 * <p><p>
	 * If an invalid 'ammoType' is passed, 0 is returned.
	 *  
	 * @param ammoType
	 * @return amount of ammo of 'ammoType'
	 */
	public int getAmmo(ItemType ammoOrWeaponType) {
		if (ammoOrWeaponType == null) return 0;
		if (ammoOrWeaponType.getCategory() == ItemType.Category.WEAPON) {
			if (hasSecondaryAmmoType(ammoOrWeaponType)) {
    			return getPrimaryWeaponAmmo(ammoOrWeaponType) + getSecondaryWeaponAmmo(ammoOrWeaponType);
    		} else {
    			return getPrimaryWeaponAmmo(ammoOrWeaponType);
    		}			
		}
		if (ammoOrWeaponType.getCategory() == ItemType.Category.AMMO) {
			return ammo.getAmmo(ammoOrWeaponType);
		}
		return 0;
	}
	
	/**
	 * Returns an amount of primary ammo of a given 'weaponType' the bot currently has.
	 * <p><p>
	 * If an invalid 'weaponType' is passed, 0 is returned.
	 * 
	 * @param weaponType
	 * @return amount of primary ammo of the given 'weaponType'
	 */
	public int getPrimaryWeaponAmmo(ItemType weaponType) {
		if (weaponType == null) return 0;
		if (weaponType.getCategory() != ItemType.Category.WEAPON) return 0;
		return ammo.getPriAmmoForWeapon(weaponType);
	}
	
	/**
	 * Returns an amount of secondary ammo of a given 'weaponType' the bot currently has.
	 * <p><p>
	 * If an invalid 'weaponType' is passed, 0 is returned.
	 * 
	 * @param weaponType
	 * @return amount of secondary ammo of the given 'weaponType'
	 */
	public int getSecondaryWeaponAmmo(ItemType weaponType) {
		if (weaponType == null) return 0;
		if (weaponType.getCategory() != ItemType.Category.WEAPON) return 0;
		return ammo.getSecAmmoForWeapon(weaponType);
	}
	
	/**
	 * Returns an amount of primary+secondary ammo of a given 'weaponType' the bot currently has.
	 * <p><p>
	 * If an invalid 'weaponType' is passed, 0 is returned.
	 * 
	 * @param weaponType
	 * @return amount of primary+secondary ammo of the given 'weaponType'
	 */
	public int getWeaponAmmo(ItemType weaponType) {
		if (weaponType == null) return 0;
		if (weaponType.getCategory() != ItemType.Category.WEAPON) return 0;
		return ammo.getAmmoForWeapon(weaponType);
	}
	
	/**
	 * Tells whether the bot has low-ammo for "weaponType" (either primary/secondary).
	 * <p><p>
	 * Low ammo is: currAmmmo / maxAmmo &lt; lowRatio
	 * 
	 * @param weaponType
	 * @param lowRatio
	 * @return
	 */
	public boolean hasLowAmmoForWeapon(ItemType weaponType, double lowRatio) {
		return hasPrimaryLowAmmoForWeapon(weaponType, lowRatio) || hasSecondaryLowAmmoForWeapon(weaponType, lowRatio);		
	}

	/**
	 * Tells whether the bot has secondary low-ammo for "weaponType"
	 * <p><p>
	 * Low ammo is: currAmmmo / maxAmmo &lt; lowRatio
	 * 
	 * @param weaponType
	 * @param lowRatio
	 * @return
	 */
	public boolean hasSecondaryLowAmmoForWeapon(ItemType weaponType, double lowRatio) {
		int ammo = getSecondaryWeaponAmmo(weaponType);
		WeaponDescriptor desc = getWeaponDescriptor(weaponType);
		return ammo / desc.getPriMaxAmount() < lowRatio;	
	}

	/**
	 * Tells whether the bot has primary low-ammo for "weaponType"
	 * <p><p>
	 * Low ammo is: currAmmmo / maxAmmo &lt; lowRatio
	 * 
	 * @param weaponType
	 * @param lowRatio
	 * @return
	 */
	public boolean hasPrimaryLowAmmoForWeapon(ItemType weaponType, double lowRatio) {
		if (!hasSecondaryAmmoType(weaponType)) return false;
		int ammo = getSecondaryWeaponAmmo(weaponType);
		WeaponDescriptor desc = getWeaponDescriptor(weaponType);
		return ammo / desc.getSecMaxAmount() < lowRatio;		
	}

	/**
	 * Tells whether the bot has an ammo of 'ammoType'.
	 * <p><p>
	 * If an invalid 'ammoType' is passed, false is returned.
	 * 
	 * @param ammoType
	 * @return True, if the bot has at least one ammo of 'ammoType'.
	 */
	public boolean hasAmmo(ItemType ammoType) {
		if (ammoType == null) return false;
		if (ammoType.getCategory() != ItemType.Category.AMMO) return false;
		return ammo.getAmmo(ammoType) > 0;
	}
	
	/**
	 * Alias for {@link Weaponry#hasWeaponAmmo(ItemType)}.
	 * 
	 * @see hasWeaponAmmo(ItemType)
	 * 
	 * @param weaponType
	 * @return True, if the bot has any ammo for a 'weaponType'.
	 */
	public boolean hasAmmoForWeapon(ItemType weaponType) {
		return hasWeaponAmmo(weaponType);
	}
	
	
	/**
	 * Tells whether the bot has an ammo (either primary or secondary) for a given 'weaponType'.
	 * <p><p>
	 * If an invalid 'weaponType' is passed, false is returned.
	 * 
	 * @param weaponType
	 * @return True, if the bot has any ammo for a 'weaponType'.
	 */
	public boolean hasWeaponAmmo(ItemType weaponType) {
		if (weaponType == null) return false;
		if (weaponType.getCategory() != ItemType.Category.WEAPON) return false;
		return ammo.getAmmoForWeapon(weaponType) > 0;
	}
	
	/**
	 * Tells whether the bot has a primary ammo for a given 'weaponType'.
	 * <p><p>
	 * If an invalid 'weaponType' is passed, false is returned.
	 * 
	 * @param weaponType
	 * @return True, if the bot has primary ammo for a 'weaponType'.
	 */
	public boolean hasPrimaryWeaponAmmo(ItemType weaponType) {
		if (weaponType == null) return false;
		if (weaponType.getCategory() != ItemType.Category.WEAPON) return false;
		return ammo.getPriAmmoForWeapon(weaponType) > 0;
	}
	
	/**
	 * Tells whether the bot has a secondary ammo for a given 'weaponType'.
	 * <p><p>
	 * If an invalid 'weaponType' is passed, false is returned.
	 * 
	 * @param weaponType
	 * @return True, if the bot has secondary ammo for a 'weaponType'.
	 */
	public boolean hasSecondaryWeaponAmmo(ItemType weaponType) {
		if (weaponType == null) return false;
		if (weaponType.getCategory() != ItemType.Category.WEAPON) return false;
		return ammo.getSecAmmoForWeapon(weaponType) > 0;
	}
		
	/**
     * Tells, whether specific weapon is in the agent's inventory.
     * <p><p>
	 * If an invalid 'weaponType' is passed, false is returned.
     *
     * @param weaponType type of the weapon
     * @return True, if the requested weapon is present; false otherwise.
     */
    public boolean hasWeapon(ItemType weaponType) {
    	if (weaponType == null) return false;
    	if (weaponType.getCategory() != ItemType.Category.WEAPON) return false;
    	return weaponsByItemType.all.containsKey(weaponType);
    }
    
    /**
     * Tells, whether a weapon from the specific group is in the agent's inventory.
     * <p><p>
     * If an invalid 'weaponGroup' is passed, false is returned.
     *
     * @param weaponGroup group of the weapon
     * @return True, if the requested weapon is present; false otherwise.
     */
    public boolean hasWeapon(ItemType.Group weaponGroup) {
    	if (weaponGroup == null) return false;
    	return weaponsByGroup.all.containsKey(weaponGroup);
    }    
    
    /**
     * Tells, whether specific weapon is in the agent's inventory && is loaded (has at least 1 primary or secondary ammo).
     * <p><p>
     * If an invalid 'weaponType' is passed, false is returned.
     *
     * @param weaponType type of the weapon
     * @return True, if the requested weapon is present && is loaded; false otherwise.
     */
    public boolean isLoaded(ItemType weaponType) {
    	if (weaponType == null) return false;
    	if (weaponType.getCategory() != ItemType.Category.WEAPON) return false;
    	return weaponsByItemType.allLoaded.containsKey(weaponType);
    }
    
    /**
     * Tells, whether a weapon from a specific group is in the agent's inventory && is loaded (has at least 1 primary or secondary ammo).
     * <p><p>
     * If an invalid 'weaponGroup' is passed, false is returned.
     *
     * @param weaponType type of the weapon
     * @return True, if the requested weapon is present && is loaded; false otherwise.
     */
    public boolean isLoaded(ItemType.Group weaponGroup) {
    	if (weaponGroup == null) return false;
    	return weaponsByGroup.allLoaded.containsKey(weaponGroup);
    }
    
    /**
     * Whether the weapon has secondary ammo different from the primary.
     * <p><p>
     * If an invalid 'weaponType' is passed, false is returned.
     * 
     * @param weaponType
     * @return
     */
    public boolean hasSecondaryAmmoType(ItemType weaponType) {
    	if (weaponType == null) return false;
    	if (weaponType.getCategory() != ItemType.Category.WEAPON) return false;
    	WeaponDescriptor desc = (WeaponDescriptor) itemDescriptors.getDescriptor(weaponType);
    	if (desc == null) {
    		if (log.isLoggable(Level.WARNING)) log.warning("hasSecondaryAmmoType(): There is no weapon descriptor for the item type " + weaponType + "!");
    		return false;
    	}
    	return desc.getSecAmmoItemType() != null && desc.getPriAmmoItemType() != desc.getSecAmmoItemType();    		
    }
    
    /**
     * Returns a {@link WeaponDescriptor} for a given 'weaponType' (if it is not a weapon, returns null).
     * <p><p>
     * The descriptor can be used to reason about the weapon suitability for actual combat.
     * 
     * @param weaponType
     * @return weapon descriptor
     */
    public WeaponDescriptor getWeaponDescriptor(ItemType weaponType) {
    	if (weaponType == null) return null;
    	if (weaponType.getCategory() != ItemType.Category.WEAPON) return null;
    	WeaponDescriptor desc = (WeaponDescriptor) itemDescriptors.getDescriptor(weaponType);
    	if (desc == null) {
    		if (log.isLoggable(Level.WARNING)) log.warning("getWeaponDescriptor(): There is no weapon descriptor for the item type " + weaponType + "!");
    	}
    	return desc;
    }
    
    /**
     * Retrieves current weapon from the agent's inventory.
     *
     * @return Current weapon from inventory; or null upon no current weapon.
     *
     * @see getCurrentPrimaryAmmo()
     * @see getCurrentAlternateAmmo()
     * @see AgentInfo#getCurrentWeapon()
     */
    public Weapon getCurrentWeapon() {
        if (self == null) {
            return null;
        }
        if (self.getWeapon() == null) {
        	return null;
        }
    	WeaponDescriptor desc = inventoryUnrealIdToWeaponDescriptor.get(UnrealId.get(self.getWeapon()));
    	if (desc == null) {
    		if (self.getWeapon().equalsIgnoreCase(AgentInfo.NONE_WEAPON_ID)) return null;
    		if (log.isLoggable(Level.WARNING)) log.warning("getCurrentWeapon(): There is no weapon descriptor for current bot's weapon of id: '" + self.getWeapon() + "'");
    		return null;
    	}
    	return weaponsByItemType.all.get(desc.getPickupType());
    }
    
    /**
     * Tells, how much ammo is in the agent's inventory for current weapon - primary + (if has different secondary ammo type) alternate (secondary).
     *
     * @return Amount of ammo in the inventory.
     * 
     * @see getCurrentPrimaryAmmo()
     * @see getCurrentAlternateAmmo()
     * @see AgentInfo#getCurrentAmmo()
     */
    public int getCurrentAmmo() {
    	if (getCurrentWeapon() == null) return 0;
    	if (getCurrentWeapon().hasSecondaryAmmoType()) {
    		return getCurrentPrimaryAmmo() + getCurrentAlternateAmmo();
    	} else {
    		return getCurrentPrimaryAmmo();
    	}
    }
   
    /**
     * Tells, how much ammo is in the agent's inventory for current weapon.
     *
     * @return Amount of primary ammo for the current weapon.
     *
     * @see getCurrentAlternateAmmo()
     * @see AgentInfo#getCurrentAmmo()
     */
    public int getCurrentPrimaryAmmo() {
        // retreive from self
        if (self == null) {
            return 0;
        }
        return self.getPrimaryAmmo();
    }
    
    /**
     * Tells, how much ammo is in the agent's inventory for current weapon for
     * alternate firing mode. (If the weapon consumes primary ammo for alternate
     * firing mode it returns the same number as {@link Weaponry#getCurrentPrimaryAmmo()}.
     * @return Amount of ammo in the inventory for alternate fire mode.
     *
     * @see getCurrentPrimaryAmmo()
     * @see AgentInfo#getCurrentSecondaryAmmo()
     */
    public int getCurrentAlternateAmmo() {
        if (self == null) {
            return 0;
        }
        if (self.getWeapon() == null) {
        	return 0;
        }
        WeaponDescriptor weaponDesc = inventoryUnrealIdToWeaponDescriptor.get(UnrealId.get(self.getWeapon()));
        if (weaponDesc == null) {
        	if (self.getWeapon().equals(AgentInfo.NONE_WEAPON_ID)) return 0;
        	if (log.isLoggable(Level.WARNING)) log.warning("getCurrentAlternateAmmo(): There is no weapon descriptor for current bot's weapon of id: '" + self.getWeapon() + "'");
        	return 0;
        }
        if (weaponDesc.getSecAmmoItemType() != null) {
        	return self.getSecondaryAmmo();
        } else {
        	return self.getPrimaryAmmo();
        }
    }

    /**
     * Retrieves all weapons from the agent's inventory.
     * <p><p>
     * NOTE: Returned map can't be modified.
     * <p><p>
     * NOTE: The {@link Weapon} instance is invalidated by {@link BotKilled} event, discard the instance upon bot's death.
     *
     * @return List of all available weapons from inventory.
     *
     * @see hasLoadedWeapon()
     * @see getLoadedMeleeWeapons()
     * @see getLoadedRangedWeapons()
     */
    public Map<ItemType, Weapon> getWeapons() {
    	return Collections.unmodifiableMap(weaponsByItemType.all);
    }
    
    /**
     * Returns {@link Weapon} instance for given 'weaponType' if the bot posses it.
     * 
     * @see getWeapons()
     * @param weaponType
     * @return {@link Weapon}
     */
    public Weapon getWeapon(ItemType weaponType) {
    	if (weaponType == null) return null;
    	if (weaponType.getCategory() != ItemType.Category.WEAPON) return null;
    	return weaponsByItemType.all.get(weaponType);    	
    }
    
   
    /**
     * Retrieves all loaded weapons from the agent's inventory.
     *
     * <p>Note: <b>Shield guns</b> are never treated as loaded weapons, though
     * they are usually <i>loaded</i>, i.e. ready to be fired.</p>
     * <p><p>
     * NOTE: Returned map can't be modified.
     * <p><p>
     * NOTE: The {@link Weapon} instance is invalidated by {@link BotKilled} event, discard the instance upon bot's death.
     *
     * @return List of all available weapons from inventory.
     *
     * @see hasLoadedWeapon()
     * @see getLoadedMeleeWeapons()
     * @see getLoadedRangedWeapons()
     */
    public Map<ItemType, Weapon> getLoadedWeapons() {
        return Collections.unmodifiableMap(weaponsByItemType.allLoaded);
    }

    /**
     * Retrieves melee weapons from the agent's inventory.
     *<p><p>
     * NOTE: Returned map can't be modified.
     * <p><p>
     * NOTE: The {@link Weapon} instance is invalidated by {@link BotKilled} event, discard the instance upon bot's death.
     *
     * @return List of all available melee weapons from inventory.
     *
     * @see getLoadedMeleeWeapons()
     */
    public Map<ItemType, Weapon> getMeleeWeapons() {
        return Collections.unmodifiableMap(weaponsByItemType.allMelee);
    }

    /**
     * Retrieves ranged weapons from the agent's inventory.
     * <p><p>
     * NOTE: Returned map can't be modified.
     * <p><p>
     * NOTE: The {@link Weapon} instance is invalidated by {@link BotKilled} event, discard the instance upon bot's death.
     *
     * @return List of all available ranged weapons from inventory.
     *
     * @see getLoadedRangedWeapons()
     */
    public Map<ItemType, Weapon> getRangedWeapons() {
        return Collections.unmodifiableMap(weaponsByItemType.allRanged);
    }

    /**
     * Retrieves loaded melee weapons from the agent's inventory.
     * <p><p>
     * NOTE: Returned map can't be modified.
     * <p><p>
     * NOTE: The {@link Weapon} instance is invalidated by {@link BotKilled} event, discard the instance upon bot's death.
     *
     * @return List of all available melee weapons from inventory.
     *
     * @see getMeleeWeapons()
     */
    public Map<ItemType, Weapon> getLoadedMeleeWeapons() {
        return Collections.unmodifiableMap(weaponsByItemType.allLoadedMelee);
    }

    /**
     * Retrieves loaded ranged weapons from the agent's inventory.
     * <p><p>
     * NOTE: Returned map can't be modified.
     * <p><p>
     * NOTE: The {@link Weapon} instance is invalidated by {@link BotKilled} event, discard the instance upon bot's death.
     *
     * @return List of all available loaded ranged weapons from inventory.
     *
     * @see getRangedWeapons()
     */
    public Map<ItemType, Weapon> getLoadedRangedWeapons() {
        return Collections.unmodifiableMap(weaponsByItemType.allLoadedRanged);
    }    
    
    /**
     * Returns a map with current state of ammo (ammo for owned weapons as well
     * as ammo for weapons that the bot do not have yet).
     * <p><p>
     * NOTE: Returned map can't be modified.
     * 
     * @return
     */
    public Map<ItemType, Integer> getAmmos() {
    	return Collections.unmodifiableMap(ammo.ammo);
    }

    /**
     * Tells, whether the agent has any loaded weapon in the inventory.
     *
     * <p>Note: <b>Shield guns</b> are never treated as loaded weapons, though
     * they are usually <i>loaded</i>, i.e. ready to be fired.</p>
     *
     * @return True, if there is a loaded weapon in the inventory.
     *
     * @see getLoadedWeapons()
     */
    public boolean hasLoadedWeapon() {
        // are there any in the list of loaded weapons?
        return !getLoadedWeapons().isEmpty();
    }
    
    /**
     * Tells, whether the agent has any loaded ranged weapon in the inventory.
     *
     *
     * @return True, if there is a loaded ranged weapon in the inventory.
     *
     * @see getLoadedRangedWeapons()
     */
    public boolean hasLoadedRangedWeapon() {
        // are there any in the list of loaded weapons?
        return !getLoadedRangedWeapons().isEmpty();
    }
    
    /**
     * Tells, whether the agent has any loaded melee weapon in the inventory.
     *
     * <p>Note: <b>Shield guns</b> are never treated as loaded weapons, though
     * they are usually <i>loaded</i>, i.e. ready to be fired.</p>
     *
     * @return True, if there is a loaded melee weapon in the inventory.
     *
     * @see getLoadedMeleeWeapons()
     */
    public boolean hasLoadedMeleeWeapon() {
        // are there any in the list of loaded weapons?
        return !getLoadedMeleeWeapons().isEmpty();
    }
    
    /**
     * Whether the bot possess 'weapon' that has primary or secondary ammo.
     * @param weapon
     * @return
     */
	public boolean hasLoadedWeapon(ItemType weapon) {
		return hasPrimaryLoadedWeapon(weapon) || hasSecondaryLoadedWeapon(weapon);
	}
    
    /**
     * Whether the bot possess 'weapon' that has primary ammo.
     * @param weapon
     * @return
     */
	public boolean hasPrimaryLoadedWeapon(ItemType weapon) {
		Weapon w = getWeapon(weapon);
		if (w == null) return false;
		return w.getPrimaryAmmo() > 0;
	}
	
	/**
     * Whether the bot possess 'weapon' that has secondary ammo.
     * @param weapon
     * @return
     */
	public boolean hasSecondaryLoadedWeapon(ItemType weapon) {
		Weapon w = getWeapon(weapon);
		if (w == null) return false;
		return w.getSecondaryAmmo() > 0;
	}

	/**
	 * Returns weaponType for a given ammoType (regardles whether it is primary or secondary ammo type).
	 * 
	 * @param priOrSecAmmoType
	 * @return
	 */
	public ItemType getWeaponForAmmo(ItemType priOrSecAmmoType) {
		for (ItemType weaponType : Category.WEAPON.getTypes()) {
			WeaponDescriptor desc = (WeaponDescriptor)itemDescriptors.getDescriptor(weaponType);
			if (desc == null) continue;
			if (desc.getPriAmmoItemType() == priOrSecAmmoType) return weaponType;
			if (desc.getSecAmmoItemType() == priOrSecAmmoType) return weaponType;
		}
		return null;
	}

	/**
	 * Return primary-ammo {@link ItemType} for a weapon.
	 * @param weaponType
	 * @return
	 */
	public ItemType getPrimaryWeaponAmmoType(ItemType weaponType) {
		WeaponDescriptor desc = (WeaponDescriptor)itemDescriptors.getDescriptor(weaponType);
		if (desc == null) return null;
		return desc.getPriAmmoItemType();
	}
    
	/**
	 * Return secondary-ammo {@link ItemType} for a weapon.
	 * @param weaponType
	 * @return
	 */
	public ItemType getSecondaryWeaponAmmoType(ItemType weaponType) {
		WeaponDescriptor desc = (WeaponDescriptor)itemDescriptors.getDescriptor(weaponType);
		if (desc == null) return null;
		return desc.getSecAmmoItemType();
	}
		

    /*========================================================================*/
    
    /**
     * Storage class for ammo - taking inputs from {@link WeaponUpdateListener} and {@link ItemPickedUpListener}
     * storing how many ammo the bot has.
     * <p><p>
     * Additionally it contains weapon-ammo-update method {@link Ammunition#updateWeaponAmmo(Weapon)} that
     * notifies {@link WeaponsByKey} about ammo amount changes via {@link WeaponsByKey#ammoChanged(Object)} as well. 
     */
    class Ammunition {
    	
    	/**
    	 * Contains amount of ammos the bot has.
    	 */
    	private LazyMap<ItemType, Integer> ammo = new LazyMap<ItemType, Integer>() {
			@Override
			protected Integer create(ItemType key) {
				return 0;
			}    		
    	};
    
    	/**
    	 * Returns amount of ammo for a given type.
    	 * @param ammoType must be from the AMMO category otherwise returns 0
    	 * @return amount of ammo of the given type
    	 */
     	public int getAmmo(ItemType ammoType) {
    		if (ammoType == null) return 0;
    		if (ammoType.getCategory() != ItemType.Category.AMMO) return 0;
    		return ammo.get(ammoType);
    	}
    	
     	/**
     	 * Returns amount of primary ammo for a given weapon.
     	 * @param weapon must be from the WEAPON category otherwise returns 0
     	 * @return amount of primary ammo for a weapon
     	 */
    	public int getPriAmmoForWeapon(ItemType weapon) {
    		if (weapon == null) return 0;
    		if (weapon.getCategory() != ItemType.Category.WEAPON) return 0;
    		WeaponDescriptor desc = (WeaponDescriptor)itemDescriptors.getDescriptor(weapon);
    		if (desc == null) {
    			if (log.isLoggable(Level.WARNING)) log.warning("Ammunition.getPriAmmoForWeapon(): There is no WeaponDescriptor for the item type " + weapon + "!");
    			return 0;
    		}
    		return getAmmo(desc.getPriAmmoItemType());
    	}
    	
    	/**
     	 * Returns amount of secondary ammo for a given weapon. If secondary ammo does not exist for the weapon
     	 * returns the same value as {@link Ammunition#getPriAmmoForWeapon(ItemType)}. 
     	 * @param weaponType must be from the WEAPON category otherwise returns 0
     	 * @return amount of secondary ammo for a weapon
     	 */
    	public int getSecAmmoForWeapon(ItemType weaponType) {
    		if (weaponType == null) return 0;
    		if (weaponType.getCategory() != ItemType.Category.WEAPON) return 0;
    		WeaponDescriptor desc = (WeaponDescriptor)itemDescriptors.getDescriptor(weaponType);
    		if (desc == null) {
    			if (log.isLoggable(Level.WARNING)) log.warning("Ammunition.getSecAmmoForWeapon(): There is no WeaponDescriptor for the item type " + weaponType + "!");
    			return 0;
    		}
    		if (desc.getSecAmmoItemType() == null) return getPriAmmoForWeapon(weaponType);
    		return getAmmo(desc.getSecAmmoItemType());
    	}
    	
    	/**
     	 * Returns amount of primary+secondary (if exists) ammo for a given weapon.
     	 * @param weapon must be from the WEAPON category otherwise returns 0
     	 * @return amount of primary+secondary (if exists) ammo for a weapon
     	 */
    	public int getAmmoForWeapon(ItemType weaponType) {
    		if (weaponType == null) return 0;
    		if (weaponType.getCategory() != ItemType.Category.WEAPON) return 0;
    		WeaponDescriptor desc = (WeaponDescriptor) itemDescriptors.getDescriptor(weaponType);
    		if (desc == null) {
    			if (log.isLoggable(Level.WARNING)) log.warning("Ammunition.getAmmoForWeapon(): There is no WeaponDescriptor for the item type " + weaponType + "!");
    			return 0;
    		}
    		if (desc.getSecAmmoItemType() != null && desc.getPriAmmoItemType()!= desc.getSecAmmoItemType()) {
    			return getPriAmmoForWeapon(weaponType) + getSecAmmoForWeapon(weaponType);
    		} else {
    			return getPriAmmoForWeapon(weaponType);
    		}
    	}
    	
    	/**
    	 * Called by {@link ItemPickedUpListener} to process ammo/weapon pickups (increase state of ammunition in the inventory).
    	 * @param pickedUp
    	 */
    	public void itemPickedUp(ItemPickedUp pickedUp) {
    		if (pickedUp == null) return;
    		ItemDescriptor descriptor = itemDescriptors.getDescriptor(pickedUp.getType());
    		if (descriptor == null) {
    			if (log.isLoggable(Level.WARNING)) log.warning("Ammunition.itemPickedUp(): There is no ItemDescriptor for the item type " + pickedUp.getType() + "!");
    			return;
    		}
    		
    		
    		if (descriptor.getItemCategory() == Category.AMMO) {
    			AmmoDescriptor desc = (AmmoDescriptor)descriptor;
    			int current = getAmmo(pickedUp.getType());
    			if (current + pickedUp.getAmount() > desc.getPriMaxAmount()) {
    				ammo.put(pickedUp.getType(), desc.getPriMaxAmount());
    			} else {
    				ammo.put(pickedUp.getType(), current + pickedUp.getAmount());
    			}
    		} else 
    		if (descriptor.getItemCategory() == Category.WEAPON) {
    			WeaponDescriptor desc = (WeaponDescriptor)descriptor;
    			
				if (desc.getPriAmmoItemType() != null) {
	    			int priAmmo = ammo.get(desc.getPriAmmoItemType());

	    			int priWeaponAmmoPlus = pickedUp.getAmount();
	    			if (priAmmo + priWeaponAmmoPlus <= desc.getPriMaxAmount()) {
	    				ammo.put(desc.getPriAmmoItemType(), priAmmo + priWeaponAmmoPlus);
	    			} else {
	    				ammo.put(desc.getPriAmmoItemType(), desc.getPriMaxAmount());
	    			}
    			}
    			
    			if (desc.getSecAmmoItemType() != null && desc.getSecAmmoItemType() != desc.getPriAmmoItemType()) {
    				int secAmmo = ammo.get(desc.getSecAmmoItemType());
    				int secWeaponAmmoPlus = pickedUp.getAmountSec();
        			if (secAmmo + secWeaponAmmoPlus <= desc.getSecMaxAmount()) {
        				ammo.put(desc.getSecAmmoItemType(), secAmmo + secWeaponAmmoPlus);
        			} else {
        				ammo.put(desc.getSecAmmoItemType(), desc.getSecMaxAmount());
        			}        			
    			}	    			
    		}
    	}
    	
    	/**
    	 * Caused by 1. {@link WeaponUpdate} event, called by the {@link WeaponUpdateListener}
         * and by 2. {@link Self} object updated event, called by the {@link SelfUpdateListener}.
    	 * @param ammoType
    	 * @param amount
    	 */
    	public void weaponUpdate(ItemType ammoType, int amount) {
    		//log.severe("WEAPON UPDATE: " + ammoType + ", amount " + amount);
    		
    		if (ammoType == null) return;
    		if (ammoType.getCategory() != ItemType.Category.AMMO) {
    			if (log.isLoggable(Level.SEVERE)) log.severe("Ammunition.weaponUpdate: Can't update weapon ammo, unknown ammo type=" + ammoType.getName() + ", category=" + ammoType.getCategory() + ", group=" + ammoType.getGroup());
    			return;
    		}
    		ammo.put(ammoType, amount);
		}
            	
    	/**
    	 * Called by {@link BotKilledListener} to clear the storage.
    	 */
    	public void botKilled() {
    		ammo.clear();
    	}
    	
    	/**
    	 * Updates weapon ammo based on current values inside 'ammo' storage.
    	 */
    	public void updateWeaponAmmo(Weapon weapon) {
    		if (weapon == null) return;
    		weapon.primaryAmmo = getAmmo(weapon.getDescriptor().getPriAmmoItemType());
        	if (weapon.getDescriptor().getSecAmmoItemType() != null) {
        		weapon.secondaryAmmo = getAmmo(weapon.getDescriptor().getSecAmmoItemType());
        	}
        	weaponsByGroup.ammoChanged(weapon.getGroup());
        	weaponsByItemType.ammoChanged(weapon.getType());
        	weaponsById.ammoChanged(weapon.getInventoryId());
    	}    	
    }
    
    Ammunition ammo = new Ammunition();
        
    /*========================================================================*/
    
    /**
     * Storage class for weapons according to some KEY.
     */
    private class WeaponsByKey<KEY> {
    	/** All items picked up according by their KEY. */
    	private HashMap<KEY, Weapon> all = new HashMap<KEY, Weapon>();
    	/** All loaded weapons mapped by their KEY. */
        private HashMap<KEY, Weapon> allLoaded = new HashMap<KEY, Weapon>();
        /** All loaded weapons mapped by their KEY. */
        private HashMap<KEY, Weapon> allMelee = new HashMap<KEY, Weapon>();
        /** All loaded weapons mapped by their KEY. */
        private HashMap<KEY, Weapon> allRanged = new HashMap<KEY, Weapon>();
        /** All loaded weapons mapped by their KEY. */
        private HashMap<KEY, Weapon> allLoadedMelee = new HashMap<KEY, Weapon>();
        /** All loaded weapons mapped by their KEY. */
        private HashMap<KEY, Weapon> allLoadedRanged = new HashMap<KEY, Weapon>();
        
        /**
         * Adds weapon under the key into the storage. Called by {@link AddInventoryMsgListener} to introduce
         * new weapon into the storage.
         * @param key
         * @param inv
         */
        public void add(KEY key, Weapon inv) {
        	if (key == null || inv == null) return;
        	if (inv.getDescriptor() == null) {
        		if (log.isLoggable(Level.WARNING)) log.warning("WeaponsByKey.add(): Can't add weapon " + inv.getType() + " that has associated weapon descriptor == null!");
        		return;
        	}
        	if (inv.getDescriptor() instanceof WeaponDescriptor) {
        		WeaponDescriptor desc = inv.getDescriptor();
        		all.put(key, inv);
        		if (desc.isMelee()) {
        			allMelee.put(key, inv);
        			if (inv.getAmmo() > 0) {
        				allLoadedMelee.put(key, inv);
        				allLoaded.put(key, inv);
        			}
        		} else {
        			allRanged.put(key, inv);
        			if (inv.getAmmo() > 0) {
        				allLoadedRanged.put(key, inv);
        				allLoaded.put(key, inv);
        			}
        		}
        	}	
        }
        
        /**
         * Removes a weapon under the KEY from the storage.
         */
        public void remove(KEY key) {
        	all.remove(key);
        	allLoaded.remove(key);
        	allMelee.remove(key);
        	allRanged.remove(key);
        	allLoadedMelee.remove(key);
        	allLoadedRanged.remove(key);
        }
        
        /**
         * Inform the storage that the amount of ammo for the weapon under the 'KEY' has changed.
         * Called from {@link Ammunition#weaponUpdate(ItemType, int)}.
         */
        public void ammoChanged(KEY key) {
        	if (key == null) return;
        	Weapon weapon = all.get(key);
        	if (weapon == null) {
        		// we currently do not have such weapon
        		return;        		
        	}
        	if (weapon.getAmmo() > 0) {
        		if (!allLoaded.containsKey(key)) {
        			// we have ammo for the weapon that is not marked as LOADED!
        			WeaponDescriptor desc = (weapon.getDescriptor());
        			allLoaded.put(key, weapon);
        			if (desc.isMelee()) {
        				allLoadedMelee.put(key, weapon);
        			} else {
        				allLoadedRanged.put(key, weapon);
        			}
        		}
        	} else {
        		// ammo == 0
        		if (allLoaded.containsKey(key)) {
        			// we do not have ammo for the weapon that is marked as LOADED!
        			allLoaded.remove(key);
        			allLoadedMelee.remove(key);
        			allLoadedRanged.remove(key);
        		}
        	}
        }
               
        /**
         * Called by {@link BotKilledListener} to clear the storage.
         */
        public void botKilled() {
        	all.clear();
        	allLoaded.clear();
        	allLoadedMelee.clear();
        	allLoadedRanged.clear();
        	allMelee.clear();
        	allRanged.clear();
        }
        
    }
    
    WeaponsByKey<ItemType.Group> weaponsByGroup = new WeaponsByKey<ItemType.Group>();

    WeaponsByKey<ItemType> weaponsByItemType    = new WeaponsByKey<ItemType>();
    
    WeaponsByKey<UnrealId> weaponsById          = new WeaponsByKey<UnrealId>();
    
    /*===================================================================================*/
    
    private Map<ItemType, UnrealId> weaponTypeToInventoryUnrealId = new HashMap<ItemType, UnrealId>();
    private Map<UnrealId, WeaponDescriptor> inventoryUnrealIdToWeaponDescriptor = new HashMap<UnrealId, WeaponDescriptor>();
    
    /*===================================================================================*/
    
    /**
     * AddInventoryMsg listener.
     */
    private class AddInventoryMsgListener implements IWorldEventListener<AddInventoryMsg> {

        @Override
        public void notify(AddInventoryMsg event) {
        	if (event == null) return;
        	if (event.getPickupType() == null) return;
        	if (event.getPickupType().getCategory() != ItemType.Category.WEAPON) return;
        	
        	// DO NOT NOTIFY THE AMMO STORAGE - Another ItemPickedUp event will came.
        	
//        	if (event.getPickupType() == ItemType.REDEEMER) {
//        		log.info("REDEEMER!");
//        	}
        	
        	// create a weapon
        	Weapon weapon = new Weapon(event, ammo.getPriAmmoForWeapon(event.getPickupType()), ammo.getSecAmmoForWeapon(event.getPickupType()));
        	
        	if (weapon.getDescriptor() == null) {
        		if (log.isLoggable(Level.SEVERE)) log.severe("AddInventoryMsgListener.notify(): There is no weapon descriptor for " + weapon.getType() + "!!! The newly gained weapon is not added to the Weaponry!");
        		return;
        	}
        	
        	// then weapon storage
        	weaponsByGroup.add(event.getPickupType().getGroup(), weapon);
        	weaponsByItemType.add(event.getPickupType(), weapon);
        	weaponsById.add(event.getId(), weapon);
        	// finally add unreal id mapping
        	weaponTypeToInventoryUnrealId.put(event.getPickupType(), event.getId());
        	inventoryUnrealIdToWeaponDescriptor.put(event.getId(), (WeaponDescriptor) event.getDescriptor());
        }

        /**
         * Constructor. Registers itself on the given WorldView object.
         * @param worldView WorldView object to listent to.
         */
        public AddInventoryMsgListener(IWorldView worldView) {
            worldView.addEventListener(AddInventoryMsg.class, this);
        }
    }
    /** ItemPickedUp listener */
    AddInventoryMsgListener addInventoryMsgListener;

    /*========================================================================*/
    
    /**
     * {@link ItemPickedUp} listener.
     * Here we will count the ammo properly.
     */
    private class ItemPickedUpListener implements IWorldEventListener<ItemPickedUp> {

        @Override
        public void notify(ItemPickedUp event) {
        	if (event == null) return;
        	if (event.getType() == null) return;
        	if (event.getType().getCategory() == Category.AMMO || event.getType().getCategory() == Category.WEAPON) {
        		ammo.itemPickedUp(event);
        		Weapon weapon;
        		if (event.getType().getCategory() == Category.AMMO) {
        			ItemType weaponType = itemDescriptors.getWeaponForAmmo(event.getType());
        			if (weaponType == null) {
        				if (log.isLoggable(Level.WARNING)) log.warning("ItemPickedUpListener.notify(): There is no weapon for the ammo " + event.getType() + ", the weapon probably can not be found in this map.");
        				return;
        			}
        			weapon = weaponsByItemType.all.get(weaponType);        			        		
        		} else {
        			// Category.WEAPON
        			ItemType weaponType = event.getType();
        			weapon = weaponsByItemType.all.get(weaponType);        			
        		}
        		if (weapon != null) {
        			ammo.updateWeaponAmmo(weapon);            		
        		}
        	}
        }        

		/**
         * Constructor. Registers itself on the given WorldView object.
         * @param worldView WorldView object to listent to.
         */
        public ItemPickedUpListener(IWorldView worldView) {
            worldView.addEventListener(ItemPickedUp.class, this);
        }
    }
    /** ItemPickedUp listener */
    ItemPickedUpListener itemPickedUpListener;

    /*========================================================================*/
    
    /**
     * WeaponUpdate listener.
     * When we change weapon, we need to update ammo of the old weapon - because of
     * the delay in synchronous batches.
     */
    private class WeaponUpdateListener implements IWorldEventListener<WeaponUpdate> {

        @Override
        public void notify(WeaponUpdate event) {
        	if (event == null) return;
        	if (event.getInventoryType() == null) return;
        	WeaponDescriptor weaponDesc = (WeaponDescriptor)itemDescriptors.getDescriptor(event.getInventoryType());
        	if (weaponDesc == null) {
        		Weapon weapon = weaponsById.all.get(event.getId());
        		if (weapon != null) {
        			weaponDesc = weapon.getDescriptor();
        		}
        	}
        	if (weaponDesc == null) {    
        		if (log.isLoggable(Level.WARNING)) log.warning("WeaponUpdateListener.notify(): There is no weapon descriptor for the weapon for the event: " + event);
        		return;
        	}
        	if (weaponDesc.getPriAmmoItemType() != null) {
        		ammo.weaponUpdate(weaponDesc.getPriAmmoItemType(), event.getPrimaryAmmo());
        	}
        	if (weaponDesc.getSecAmmoItemType() != null && weaponDesc.getSecAmmoItemType() != weaponDesc.getPriAmmoItemType()) {
        		ammo.weaponUpdate(weaponDesc.getSecAmmoItemType(), event.getSecondaryAmmo());
        	}
        	
        	Weapon weapon = weaponsByItemType.all.get(weaponDesc.getPickupType());
        	if (weapon != null) {
        		ammo.updateWeaponAmmo(weapon);
        	}
        }

        /**
         * Constructor. Registers itself on the given WorldView object.
         * @param worldView WorldView object to listent to.
         */
        public WeaponUpdateListener(IWorldView worldView) {
            worldView.addEventListener(WeaponUpdate.class, this);
        }
    }
    /** WeaponUpdate listener */
    WeaponUpdateListener weaponUpdateListener;


    /*========================================================================*/

    /**
     * SelfUpdate listener.
     * When the bot shoot, information about current ammo in Self message is changing.
     * We will update ammo according to this information.
     */
    private class SelfUpdateListener implements IWorldObjectEventListener<Self, WorldObjectUpdatedEvent<Self>> {

        public void notify(WorldObjectUpdatedEvent<Self> event) {
        	if (event == null) return;
        	
            //set up self object for the first time
            if (self == null) {
                self = event.getObject();
            }   
            
            Weapon weaponToUpdate = getCurrentWeapon();
            if (weaponToUpdate != null) {
	        	WeaponDescriptor weaponDesc = weaponToUpdate.getDescriptor();
	        	if (weaponDesc == null) {
	        		if (log.isLoggable(Level.WARNING)) log.warning("SelfUpdateListener.notify(): There is no weapon descriptor for the weapon " + weaponToUpdate);
	        		return;
	        	}
	        	if (weaponDesc.getPriAmmoItemType() != null) {
	        		ammo.weaponUpdate(weaponDesc.getPriAmmoItemType(), self.getPrimaryAmmo());
	        	}
	        	if (weaponDesc.getSecAmmoItemType() != null && weaponDesc.getSecAmmoItemType() != weaponDesc.getPriAmmoItemType()) {
	        		ammo.weaponUpdate(weaponDesc.getSecAmmoItemType(), self.getSecondaryAmmo());
	        	}  
	        	ammo.updateWeaponAmmo(weaponToUpdate);
            }               
        }

        /**
         * Constructor. Registers itself on the given WorldView object.
         * @param worldView WorldView object to listent to.
         */
        public SelfUpdateListener(IWorldView worldView) {
            worldView.addObjectListener(Self.class, WorldObjectUpdatedEvent.class, this);
        }
    }
    
    /** SelfUpdate listener */
    SelfUpdateListener selfUpdateListener;

    /*========================================================================*/
    
    /**
     * Thrown listener.
     * When we loose some weapon from the inventory we want to know about it.
     */
    private class ThrownListener implements IWorldEventListener<Thrown> {

        @Override
        public void notify(Thrown event) {
        	WeaponDescriptor desc = inventoryUnrealIdToWeaponDescriptor.get(event.getId());
        	if (desc == null) {
        		if (log.isLoggable(Level.WARNING)) log.warning("ThrownListener.notify(): There is no known weapon descriptor for id " + event.getId() + " inside Weaponary.");
        		return;
        	}
        	ItemType weaponType = desc.getPickupType();
        	weaponsByGroup.remove(weaponType.getGroup());
        	weaponsByItemType.remove(weaponType);        	
        }

        /**
         * Constructor. Registers itself on the given WorldView object.
         * @param worldView WorldView object to listen to.
         */
        public ThrownListener(IWorldView worldView) {
            worldView.addEventListener(Thrown.class, this);
        }
    }
    /** Thrown listener */
    ThrownListener thrownListener;

    /*========================================================================*/
    
    /**
     * Thrown listener.
     * When we loose some weapon from the inventory we want to know about it.
     */
    private class BotKilledListener implements IWorldEventListener<BotKilled> {

        @Override
        public void notify(BotKilled event) {
        	ammo.botKilled();
        	weaponsByGroup.botKilled();
        	weaponsByItemType.botKilled();
        	weaponsById.botKilled();
        	weaponTypeToInventoryUnrealId.clear();
        	inventoryUnrealIdToWeaponDescriptor.clear();
        }

        /**
         * Constructor. Registers itself on the given WorldView object.
         * @param worldView WorldView object to listent to.
         */
        public BotKilledListener(IWorldView worldView) {
            worldView.addEventListener(BotKilled.class, this);
        }
        
    }
    /** BotKilled listener */
    BotKilledListener botKilledListener;
    
    /*========================================================================*/
    
	ItemDescriptors itemDescriptors;

        /** Self object holding information about agent current state.
         Is set up in SelfUpdateListener */
        private Self self = null;

        /**
     * Constructor. Setups the memory module for a given bot.
     * @param bot owner of the module that is using it
     */
    public Weaponry(UT2004Bot bot) {
        this(bot, new ItemDescriptors(bot));
    }
	
    /**
     * Constructor. Setups the memory module for a given bot.
     * @param bot owner of the module that is using it
     * @param agentInfo AgentInfo memory module.
     * @param itemDescriptors ItemDescriptors memory module.
     */
    public Weaponry(UT2004Bot bot, ItemDescriptors itemDescriptors) {
        this(bot, itemDescriptors, null);
    }
    
    /**
     * Constructor. Setups the memory module for a given bot.
     * @param bot owner of the module that is using it
     * @param agentInfo AgentInfo memory module.
     * @param itemDescriptors ItemDescriptors memory module.
     * @param moduleLog
     */
    public Weaponry(UT2004Bot bot, ItemDescriptors descriptors, LogCategory moduleLog) {
    	super(bot);
        
        this.itemDescriptors = descriptors;
        
        if (this.itemDescriptors == null) {
        	this.itemDescriptors = new ItemDescriptors(bot, moduleLog);
        }

        // create listeners
        addInventoryMsgListener = new AddInventoryMsgListener(worldView);
        itemPickedUpListener    = new ItemPickedUpListener(worldView);
        weaponUpdateListener    = new WeaponUpdateListener(worldView);
        selfUpdateListener      = new SelfUpdateListener(worldView);
        thrownListener          = new ThrownListener(worldView);
        botKilledListener       = new BotKilledListener(worldView);
        
        cleanUp();
	}

    @Override
    protected void cleanUp() {
    	super.cleanUp();
    	// reset (clear) the weapon inventory
    	ammo.botKilled();
    	weaponsByGroup.botKilled();
    	weaponsByItemType.botKilled();
    	weaponsById.botKilled();
    	weaponTypeToInventoryUnrealId.clear();
    	inventoryUnrealIdToWeaponDescriptor.clear();
    }

    /**
     * Returns max ammo that the bot may have for a specified ammo type.
     * <p><p>
     * Contributed by: David Holan
	 * 
     * @param ammoType
     * @return
     */
	public int getMaxAmmo(ItemType ammoType) {
		if (ammoType == null) return 0;
		WeaponDescriptor weapon = getWeaponDescriptor( getWeaponForAmmo(ammoType) );
		if (weapon == null) {
			if (log.isLoggable(Level.WARNING)) log.warning("There is no known weapon descriptor for item type " + ammoType + " inside Weaponary.");
			return 0;
		}
    	if ( weapon.getPriAmmoItemType() == ammoType ) {
    		return weapon.getPriMaxAmount();
    	} else if ( weapon.getSecAmmoItemType() == ammoType ) {
    		return weapon.getSecMaxAmount();
    	} else {
    		return 0;
    	}
	}
	
}
