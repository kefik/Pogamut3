package cz.cuni.amis.pogamut.ut2004.bot.command;

import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.communication.command.ICommandListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectListener;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.Weapon;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.Weaponry;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.WeaponPref;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.WeaponPrefs;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.ChangeWeapon;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Shoot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.StopShooting;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Self;
import cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor.WeaponDescriptor;
import cz.cuni.amis.utils.NullCheck;

/**
 * This shooting will allow you to define with which {@link Weapon} / {@ItemType} / {@link WeaponPrefs} you want to shoot.
 * It will also automatically handles rearming to the desired weapon if needed and watches over which weapon/firing mode
 * you've used in previous logic iteration (if any).
 * <p><p>
 * Further more it allows you to define cool down that specify how often the bot may change its weapon via {@link ImprovedShooting#setChangeWeaponCooldown(long)}.
 * But rest assured that this can be overriden as there are two types of method here:
 * <ol>
 * <li>methods that DOES NOT OBEY the cooldown - they have suffix 'Now', e.g., {@link ImprovedShooting#shootNow(WeaponPrefs, Player, ItemType...)}</li>
 * <li>methods that OBEY the cooldown - they DO NOT HAVE suffix 'Now', e.g., {@link ImprovedShooting#shoot(WeaponPrefs, Player, ItemType...)}</li>
 * </ol>
 * 
 * @author Jimmy
 */
public class ImprovedShooting extends AdvancedShooting {

	private Weaponry weaponry;
	
	private ItemType currentWeapon = null;
	
	private Self self;
	
	private IWorldObjectListener<Self> selfListener = new IWorldObjectListener<Self>() {

		@Override
		public void notify(IWorldObjectEvent<Self> event) {
			self = event.getObject();
			if (weaponry.getCurrentWeapon() == null) {
				currentWeapon = null;
			} else {
				currentWeapon = weaponry.getCurrentWeapon().getType();
			}
		}
		
	};
	
	/**
	 * {@link System#currentTimeMillis()} when the bot changed the weapon for the last time.
	 */
	private long lastChangeWeapon = 0;
	
	/**
	 * Minimum time (in millis) between {@link ChangeWeapon} commands. Default: 1000 (1 second).
	 */
	private long changeWeaponCooldown = 1000;
	
	private ICommandListener<ChangeWeapon> changeWeaponListener = new ICommandListener<ChangeWeapon>() {

		@Override
		public void notify(ChangeWeapon event) {
			WeaponDescriptor weapon = weaponry.getDescriptorForId(UnrealId.get(event.getId()));
			if (weapon == null) {
				log.warning("Unknown weapon descriptor for item of inventory ID: " + event.getId());
			}
			currentWeapon = weapon.getPickupType();
			lastChangeWeapon = System.currentTimeMillis();
		}
		
	};

	private WeaponPref lastShooting = null;
	
	private ICommandListener<Shoot> shootListener = new ICommandListener<Shoot>() {

		@Override
		public void notify(Shoot event) {
			if (currentWeapon == null) {
				lastShooting = null;
			} else {
				lastShooting = new WeaponPref(currentWeapon, event.isAlt() == null || !event.isAlt());
			}
		}
		
	};
	
	private ICommandListener<StopShooting> stopShootingListener = new ICommandListener<StopShooting>() {

		@Override
		public void notify(StopShooting event) {
			lastShooting = null;
		}
		
	};

	public ImprovedShooting(Weaponry weaponry, UT2004Bot agent, Logger log) {
		super(agent, log);
		NullCheck.check(weaponry, "weaponry");
		this.weaponry = weaponry;
		
		agent.getWorldView().addObjectListener(Self.class, selfListener);
		agent.getAct().addCommandListener(Shoot.class, shootListener);
		agent.getAct().addCommandListener(StopShooting.class, stopShootingListener);
		agent.getAct().addCommandListener(ChangeWeapon.class, changeWeaponListener);
	}
	
	/**
	 * Whether bot may change weapon (change weapon cooled down as defined by {@link ImprovedShooting#setChangeWeaponCooldown(long)}). 
	 * @return
	 */
	public boolean mayChangeWeapon() {
		return System.currentTimeMillis() - lastChangeWeapon > changeWeaponCooldown;
	}
	
	/**
	 * Get {@link WeaponPref} for actually used weapon.
	 * @return
	 */
	public WeaponPref getActualWeaponPref() {
		if (lastShooting != null && lastShooting.getWeapon() == currentWeapon) return lastShooting;
		if (currentWeapon != null) return new WeaponPref(currentWeapon, true);
		return null;
	}
	
	/**
	 * Get {@link WeaponPref} for actually used weapon.
	 * @return
	 */
	protected ItemType getActualWeaponType() {
		if (currentWeapon != null) return currentWeapon;
		return null;
	}
	
	//
	//
	// CHANGE WEAPON SECTION
	//
	//
	
	/**
	 * Changes the weapon the bot is currently holding (if the bot has the weapon and its ammo > 0).
	 * <p><p>
	 * Obeys {@link ImprovedShooting#getChangeWeaponCooldown()}.
	 * 
	 * @param weaponType
	 * @return which weapon we have chosen, null == no suitable weapon found
	 */
	public boolean changeWeapon(ItemType weaponType) {
		if (weaponType == null) return false;
		if (currentWeapon == weaponType) return true;
		if (!mayChangeWeapon()) return false;
		return changeWeaponNow(weaponType);
	}

	/**
	 * Changes the weapon the bot is currently holding (if the bot has the weapon and its ammo > 0).
	 * <p><p>
	 * Obeys {@link ImprovedShooting#getChangeWeaponCooldown()}.
	 * 
	 * @param weapon
	 * @return which weapon we have chosen, null == no suitable weapon found
	 */
	public boolean changeWeapon(Weapon weapon) {
		if (weapon == null) return false;
		if (currentWeapon == weapon.getType()) return true;
		if (!mayChangeWeapon()) return false;
		return changeWeaponNow(weapon);
	}
	
	/**
	 * Changes the weapon the bot is currently holding (if the bot has the weapon and its primary/secondary (according to 'pref') ammo > 0).
	 * <p><p>
	 * Obeys {@link ImprovedShooting#getChangeWeaponCooldown()}.
	 * 
	 * @param pref
	 * @return which weapon we have chosen, null == no suitable weapon found
	 */
	public boolean changeWeapon(WeaponPref pref) {
		if (pref == null) return false;
		if (currentWeapon == pref.getWeapon()) return true;
		if (!mayChangeWeapon()) return false;
		return changeWeaponNow(pref);
	}
	
	/**
	 * Arms the best weapon according to general preferences in 'weaponPrefs'.
	 * <p><p>
	 * Obeys {@link ImprovedShooting#getChangeWeaponCooldown()}.
	 * 
	 * @param weaponPrefs
	 * @param forbiddenWeapons optionally, you may define weapons which bot should not choose (i.e. {@link ItemType#ROCKET_LAUNCHER})
	 * @return which weapon we have chosen, null == no suitable weapon found
	 */
	public WeaponPref changeWeapon(WeaponPrefs weaponPrefs, ItemType... forbiddenWeapons) {
		WeaponPref chosen = weaponPrefs.getWeaponPreference(forbiddenWeapons);
		if (changeWeapon(chosen)) return chosen;
		return getActualWeaponPref();
	}
	
	/**
	 * Arms the best weapon according to distance from 'weaponPrefs'.
	 * <p><p>
	 * Obeys {@link ImprovedShooting#getChangeWeaponCooldown()}.
	 * 
	 * @param weaponPrefs
	 * @param distance choose weapon according to distance
	 * @param forbiddenWeapons optionally, you may define weapons which bot should not choose (i.e. {@link ItemType#ROCKET_LAUNCHER})
	 * @return which weapon we have chosen, null == no suitable weapon found
	 */
	public WeaponPref changeWeapon(WeaponPrefs weaponPrefs, double distance, ItemType... forbiddenWeapons) {
		WeaponPref chosen = weaponPrefs.getWeaponPreference(distance, forbiddenWeapons);
		if (changeWeapon(chosen)) return chosen;
		return getActualWeaponPref();
	}
	
	/**
	 * Arms the best weapon according to distance from 'weaponPrefs'.
	 * <p><p>
	 * Obeys {@link ImprovedShooting#getChangeWeaponCooldown()}.
	 * 
	 * @param weaponPrefs
	 * @param target choose weapon according to distance to the target (from the bot)
	 * @param forbiddenWeapons optionally, you may define weapons which bot should not choose (i.e. {@link ItemType#ROCKET_LAUNCHER})
	 * @return which weapon we have chosen, null == no suitable weapon found
	 */
	public WeaponPref changeWeapon(WeaponPrefs weaponPrefs, ILocated target, ItemType... forbiddenWeapons) {
		WeaponPref chosen = weaponPrefs.getWeaponPreference(target, forbiddenWeapons);
		if (changeWeapon(chosen)) return chosen;
		return getActualWeaponPref();
	}
	
	//
	//
	// CHANGE WEAPON NOW SECTION
	//
	//
	
	/**
	 * Changes the weapon the bot is currently holding (if the bot has the weapon and its ammo > 0).
	 * <p><p>
	 * DOES NOT OBEY {@link ImprovedShooting#getChangeWeaponCooldown()}.
	 * 
	 * @param weaponType
	 * @return whether we have changed the weapon (i.e., we have some ammo for it), or we already have it prepared (i.e., is our current weapon)
	 */
	public boolean changeWeaponNow(ItemType weaponType) {
		if (weaponType == null) return false;
		return weaponry.changeWeapon(weaponType);
	}

	/**
	 * Changes the weapon the bot is currently holding (if the bot has the weapon and its ammo > 0).
	 * <p><p>
	 * DOES NOT OBEY {@link ImprovedShooting#getChangeWeaponCooldown()}.
	 * 
	 * @param weapon
	 * @return whether we have changed the weapon (i.e., we have some ammo for it), or we already have it prepared (i.e., is our current weapon)
	 */
	public boolean changeWeaponNow(Weapon weapon) {
		if (weapon == null) return false;
		return weaponry.changeWeapon(weapon);
	}
	
	/**
	 * Changes the weapon the bot is currently holding (if the bot has the weapon and its primary/secondary (according to 'pref') ammo > 0).
	 * <p><p>
	 * DOES NOT OBEY {@link ImprovedShooting#getChangeWeaponCooldown()}.
	 * 
	 * @param pref
	 * @return whether we have changed the weapon (i.e., we have some ammo for required firing mode), or we already have it prepared (i.e., is our current weapon and we have enough ammo)
	 */
	public boolean changeWeaponNow(WeaponPref pref) {
		if (pref == null) return false;
		if (pref.isPrimary()) {
			if (weaponry.hasPrimaryLoadedWeapon(pref.getWeapon())) {
				return changeWeaponNow(pref.getWeapon());
			} else {
				log.info("Can't change to weapon-primary " + pref.getWeapon() + " as we do not have the weapon/or ammo for doing so.");
				return false;
			}
		} else {
			if (weaponry.hasSecondaryLoadedWeapon(pref.getWeapon())) {
				return changeWeaponNow(pref.getWeapon());
			} else {
				log.info("Can't change to weapon-secondary " + pref.getWeapon() + " as we do not have the weapon/or ammo for doing so.");
				return false;
			}				
		}
	}

	/**
	 * Arms the best weapon according to general preferences in 'weaponPrefs'.
	 * <p><p>
	 * DOES NOT OBEY {@link ImprovedShooting#getChangeWeaponCooldown()}.
	 * 
	 * @param weaponPrefs
	 */
	public WeaponPref changeWeaponNow(WeaponPrefs weaponPrefs, ItemType... forbiddenWeapons) {
		WeaponPref chosen = weaponPrefs.getWeaponPreference(forbiddenWeapons);
		if (changeWeaponNow(chosen)) return chosen;
		return getActualWeaponPref();
	}
	
	/**
	 * Arms the best weapon according to distance from 'weaponPrefs'.
	 * <p><p>
	 * DOES NOT OBEY {@link ImprovedShooting#getChangeWeaponCooldown()}.
	 * 
	 * @param weaponPrefs
	 * @param distance choose weapon according to distance
	 * @param forbiddenWeapons optionally, you may define weapons which bot should not choose (i.e. {@link ItemType#ROCKET_LAUNCHER})
	 * @return which weapon we have chosen, null == no suitable weapon found
	 */
	public WeaponPref changeWeaponNow(WeaponPrefs weaponPrefs, double distance, ItemType... forbiddenWeapons) {		
		WeaponPref chosen = weaponPrefs.getWeaponPreference(distance, forbiddenWeapons);
		if (changeWeaponNow(chosen)) return chosen;
		return getActualWeaponPref();
	}
	
	/**
	 * Arms the best weapon according to distance from 'weaponPrefs'.
	 * <p><p>
	 * DOES NOT OBEY {@link ImprovedShooting#getChangeWeaponCooldown()}.
	 * 
	 * @param weaponPrefs
	 * @param target choose weapon according to distance to the target (from the bot)
	 * @param forbiddenWeapons optionally, you may define weapons which bot should not choose (i.e. {@link ItemType#ROCKET_LAUNCHER})
	 * @return which weapon we have chosen, null == no suitable weapon found
	 */
	public WeaponPref changeWeaponNow(WeaponPrefs weaponPrefs, ILocated target, ItemType... forbiddenWeapons) {
		WeaponPref chosen = weaponPrefs.getWeaponPreference(target, forbiddenWeapons);
		if (changeWeaponNow(chosen)) return chosen;
		return getActualWeaponPref();
	}
	
	//
	//
	// SHOOT SECTION
	//
	//
	
	/**
	 * Will start shooting with {@link WeaponPref#getWeapon()} primary/secondary (as specified) at 'target'.
	 * <p><p>
	 * Obeys {@link ImprovedShooting#getChangeWeaponCooldown()}. If cooldown has not been reached yet, it will keep firing with
	 * current weapon.
	 * 
	 * @param pref
	 * @param target
	 * @return whether the bot is shooting (i.e., has enough ammo to do so)
	 */
	public boolean shoot(WeaponPref pref, UnrealId target) {
		if (pref == null) return false;
		if (currentWeapon == pref.getWeapon()) {
			if (pref.isPrimary()) shoot(target);
			else shootSecondary(target);
			return true;
		}
		if (!mayChangeWeapon()) {
			if (lastShooting != null && currentWeapon == lastShooting.getWeapon()) {
				if (lastShooting.isPrimary()) shoot(target);
				else shootSecondary(target);
			} else {
				shoot(target);
			}
			return true;
		}
		return shootNow(pref, target);
	}
	
	/**
	 * Will start shooting with {@link WeaponPref#getWeapon()} primary/secondary (as specified) at 'target'.
	 * <p><p>
	 * Obeys {@link ImprovedShooting#getChangeWeaponCooldown()}. If cooldown has not been reached yet, it will keep firing with
	 * current weapon.
	 * 
	 * @param pref
	 * @param target
	 * @return whether the bot is shooting (i.e., has enough ammo to do so)
	 */
	public boolean shoot(WeaponPref pref, ILocated target) {
		if (pref == null) return false;
		if (currentWeapon == pref.getWeapon()) {
			if (pref.isPrimary()) shoot(target);
			else shootSecondary(target);
			return true;
		}
		if (!mayChangeWeapon()) {
			if (lastShooting != null && currentWeapon == lastShooting.getWeapon()) {
				if (lastShooting.isPrimary()) shoot(target);
				else shootSecondary(target);
			} else {
				shoot(target);
			}
			return true;
		}
		return shootNow(pref, target);
	}
	
	/**
	 * Will start shooting with {@link Weapon#getType()} primary/secondary (as specified) at 'target'.
	 * <p><p>
	 * Obeys {@link ImprovedShooting#getChangeWeaponCooldown()}.
	 * 
	 * @param weapon
	 * @param usePrimaryMode
	 * @param target
	 * @return whether the bot is shooting (i.e., has enough ammo to do so)
	 */
	public boolean shoot(Weapon weapon, boolean usePrimaryMode, UnrealId target) {
		if (weapon == null) return false;
		if (target == null) return false;
		return shoot(new WeaponPref(weapon.getType(), usePrimaryMode), target);
	}
	
	/**
	 * Will start shooting with {@link Weapon#getType()} primary/secondary (as specified) at 'target'.
	 * <p><p>
	 * Obeys {@link ImprovedShooting#getChangeWeaponCooldown()}.
	 * 
	 * @param weapon
	 * @param usePrimaryMode
	 * @param target
	 * @return whether the bot is shooting (i.e., has enough ammo to do so)
	 */
	public boolean shoot(Weapon weapon, boolean usePrimaryMode, ILocated target) {
		if (weapon == null) return false;
		if (target == null) return false;
		return shoot(new WeaponPref(weapon.getType(), usePrimaryMode), target);
	}
	
	/**
	 * Will start shooting with the best weapon (according to 'prefs') at target.
	 * <p><p>
	 * Obeys {@link ImprovedShooting#getChangeWeaponCooldown()}.
	 * 
	 * @param prefs
	 * @param target
	 * @param forbiddenWeapons optionally, you may define weapons which bot should not choose (i.e. {@link ItemType#ROCKET_LAUNCHER}).
	 * @return what the bot is shooting with
	 */
	public WeaponPref shoot(WeaponPrefs prefs, ILocated target, ItemType... forbiddenWeapon) {
		NullCheck.check(prefs, "prefs");
		if (target == null) return null;
		WeaponPref chosen = prefs.getWeaponPreference(target, forbiddenWeapon);
		if (shoot(chosen, target)) return chosen;
		return getActualWeaponPref();
	}
	
	//
	//
	// SHOOT NOW SECTION
	//
	//
		
	/**
	 * Will start shooting with {@link WeaponPref#getWeapon()} primary/secondary (as specified) at 'target'.
	 * <p><p>
	 * DOES NOT OBEY {@link ImprovedShooting#getChangeWeaponCooldown()}.
	 * 
	 * @param pref
	 * @param target
	 * @return whether the bot is shooting (i.e., has enough ammo to do so)
	 */
	public boolean shootNow(WeaponPref pref, UnrealId target) {
		if (pref == null) return false;
		if (target == null) return false;
		if (currentWeapon == pref.getWeapon() || changeWeaponNow(pref)) {
			if (pref.isPrimary()) shoot(target);
			else shootSecondary(target);
		} else
		if (lastShooting != null && currentWeapon == lastShooting.getWeapon()) {
			if (lastShooting.isPrimary()) shoot(target);
			else shootSecondary(target);
		} else {
			shoot(target);
		}
		return true;
	}
	
	/**
	 * Will start shooting with {@link WeaponPref#getWeapon()} primary/secondary (as specified) at 'target'.
	 * <p><p>
	 * DOES NOT OBEY {@link ImprovedShooting#getChangeWeaponCooldown()}.
	 * 
	 * @param pref
	 * @param target
	 * @return whether the bot is shooting (i.e., has enough ammo to do so)
	 */
	public boolean shootNow(WeaponPref pref, ILocated target) {
		if (pref == null) return false;
		if (target == null) return false;
		if (currentWeapon == pref.getWeapon() || changeWeaponNow(pref)) {
			if (pref.isPrimary()) shoot(target);
			else shootSecondary(target);
			return true;
		}
		if (lastShooting != null && currentWeapon == lastShooting.getWeapon()) {
			if (lastShooting.isPrimary()) shoot(target);
			else shootSecondary(target);
			return true;
		}
		shoot(target);
		return true;
	}
	
	/**
	 * Will start shooting with {@link Weapon#getType()} primary/secondary (as specified) at 'target'.
	 * <p><p>
	 * DOES NOT OBEY {@link ImprovedShooting#getChangeWeaponCooldown()}.
	 * 
	 * @param weapon
	 * @param usePrimaryMode
	 * @param target
	 * @return whether the bot is shooting (i.e., has enough ammo to do so)
	 */
	public boolean shootNow(Weapon weapon, boolean usePrimaryMode, UnrealId target) {
		if (weapon == null) return false;
		if (target == null) return false;
		return shootNow(new WeaponPref(weapon.getType(), usePrimaryMode), target);
	}
	
	/**
	 * Will start shooting with {@link Weapon#getType()} primary/secondary (as specified) at 'target'.
	 * <p><p>
	 * DOES NOT OBEY {@link ImprovedShooting#getChangeWeaponCooldown()}.
	 * 
	 * @param weapon
	 * @param usePrimaryMode
	 * @param target
	 * @return whether the bot is shooting (i.e., has enough ammo to do so)
	 */
	public boolean shootNow(Weapon weapon, boolean usePrimaryMode, ILocated target) {
		if (weapon == null) return false;
		if (target == null) return false;
		return shootNow(new WeaponPref(weapon.getType(), usePrimaryMode), target);
	}
	
	/**
	 * Will start shooting with the best weapon (according to 'prefs') at target.
	 * <p><p>
	 * DOES NOT OBEY {@link ImprovedShooting#getChangeWeaponCooldown()}.
	 * 
	 * @param prefs
	 * @param target
	 * @param forbiddenWeapons optionally, you may define weapons which bot should not choose (i.e. {@link ItemType#ROCKET_LAUNCHER}).
	 * @return what weapon the bot is shooting with
	 */
	public WeaponPref shootNow(WeaponPrefs prefs, ILocated target, ItemType... forbiddenWeapon) {
		NullCheck.check(prefs, "prefs");
		if (target == null) return null;
		WeaponPref chosen = prefs.getWeaponPreference(target, forbiddenWeapon);
		if (shootNow(chosen, target)) return chosen;
		return getActualWeaponPref();
	}
	
	//
	//
	// UTILITY METHODS
	//
	//
	
	/**
	 * Return last type of weapon / mode of firing you've used (or null, if you have issued {@link StopShooting} command.
	 * <p><p>
	 * May be null!
	 * 
	 * @return
	 */
	public WeaponPref getLastShooting() {
		return lastShooting;
	}

	/**
	 * {@link System#currentTimeMillis()} when the bot changed the weapon for the last time.
	 * @return
	 */
	public long getLastChangeWeapon() {
		return lastChangeWeapon;
	}

	/**
	 * Returns how often the bot may change weapon. Default: 1000 (1 second).
	 * <p><p>
	 * DOES NOT APPLY GENERALLY TO ALL {@link ChangeWeapon} COMMANDS! Just for methods that are NOT SUFFIXED with 'Now' in this class!
	 * 
	 * @return
	 */
	public long getChangeWeaponCooldown() {
		return changeWeaponCooldown;
	}

	/**
	 * Sets how often the bot may change weapon. Default: 1000 (1 second).
	 * <p><p>
	 * DOES NOT APPLY GENERALLY TO ALL {@link ChangeWeapon} COMMANDS! Just for {@link ImprovedShooting#changeWeapon(ItemType)} methods
	 * defined here!
	 * 
	 * @return
	 */
	public void setChangeWeaponCooldown(long changeWeaponCooldownMillis) {
		this.changeWeaponCooldown = changeWeaponCooldownMillis;
	}
	
}
