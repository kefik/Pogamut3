package cz.cuni.amis.pogamut.ut2004.agent.module.sensor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.Weapon;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import cz.cuni.amis.utils.NullCheck;

public class WeaponPrefsRange {

	private double maxDistance;
	private WeaponPrefs owner;
	private List<WeaponPref> prefs = new ArrayList<WeaponPref>();

	protected WeaponPrefsRange(WeaponPrefs owner, double maxDistance) {
		NullCheck.check(owner, "owner");
		this.owner = owner;
		this.maxDistance = maxDistance;
	}
	
	public WeaponPrefsRange(WeaponPrefs owner, WeaponPrefsRange prefs) {
		NullCheck.check(prefs, "prefs");
		NullCheck.check(owner, "owner");
		this.prefs.addAll(prefs.prefs);
		this.owner = owner;
		this.maxDistance = prefs.maxDistance;
	}

	/**
	 * Adds another weapon as "the least preferable" one, i.e., you may define
	 * weapons from the most preferred to the least preferred by sequentially calling this method.
	 * 
	 * @param weapon weapon to be used
	 * @param usePrimaryMode true == use primary firing mode, false == use secondary firing mode
	 */
	public WeaponPrefsRange add(ItemType weapon, boolean usePrimaryMode) {
		NullCheck.check(weapon, "weapon");
		this.prefs.add(new WeaponPref(weapon, usePrimaryMode));
		return this;
	}
	
	/**
	 * Adds another weapon as "the least preferable" one, i.e., you may define
	 * weapons from the most preferred to the least preferred by sequentially calling this method.
	 * 
	 * @param weapon weapon to be used
	 * @param usePrimaryMode true == use primary firing mode, false == use secondary firing mode
	 * @return 
	 */
	public WeaponPrefsRange add(Weapon weapon, boolean usePrimaryMode) {
		NullCheck.check(weapon, "weapon");
		add(weapon.getType(), usePrimaryMode);
		return this;
	}

	/**
	 * Minimum distance for preferences.
	 * @return
	 */
	public double getMinDistance() {
		WeaponPrefsRange next = owner.getPreviousRange(this);
		if (next == null) return 0;
		return next.getMaxDistance();
	}
	
	/**
	 * Maximum distance for preferences.
	 * @return
	 */
	public double getMaxDistance() {
		return maxDistance;
	}
	
	/**
	 * Return the best weapon the bot has for a given distance.
	 * @return
	 */
	public WeaponPref getWeaponPreference() {
		return getWeaponPreference((ItemType[])null);
	}
	
	/**
	 * Return the best weapon the bot has for a given distance.
	 * @param forbiddenWeapons optionally, you may define weapons which bot should not choose (i.e. {@link ItemType#ROCKET_LAUNCHER})
	 * @return
	 */
	public WeaponPref getWeaponPreference(ItemType... forbiddenWeapons) {
		List<ItemType> forbidden = (forbiddenWeapons == null ? new ArrayList<ItemType>(0) : Arrays.asList(forbiddenWeapons));
		for (WeaponPref pref : prefs) {
			if (forbidden.contains(pref.getWeapon())) continue;
			if (pref.isPrimary()) {
				if (owner.weaponry.hasPrimaryLoadedWeapon(pref.getWeapon())) return pref;
			} else {
				if (owner.weaponry.hasSecondaryLoadedWeapon(pref.getWeapon())) return pref;				
			}
		}
		return null;
	}
	
	/**
	 * Return the best weapon the bot has for a given distance.
	 * @param forbiddenWeapons optionally, you may define weapons/mode-of-fire which bot should not choose.
	 * @return
	 */
	public WeaponPref getWeaponPreference(WeaponPref... forbiddenWeapons) {
		Set<WeaponPref> forbidden = new HashSet<WeaponPref>();
		if (forbiddenWeapons != null) {
			for (WeaponPref pref : forbiddenWeapons) {
				forbidden.add(pref);
			}
		}
		for (WeaponPref pref : prefs) {
			if (forbidden.contains(pref)) continue;
			if (pref.isPrimary()) {
				if (owner.weaponry.hasPrimaryLoadedWeapon(pref.getWeapon())) return pref;
			} else {
				if (owner.weaponry.hasSecondaryLoadedWeapon(pref.getWeapon())) return pref;				
			}
		}
		return null;
	}

	/**
	 * Clears all weapon preferences.
	 */
	public void clear() {
		prefs.clear();
	}

	/**
	 * Returns list of preferences (from the most preferred one to the least one). You may alter it as you wish the class
	 * be using it.
	 * @return
	 */
	public List<WeaponPref> getPrefs() {
		return prefs;
	}

	/**
	 * Set list of preferences to be used (from the most preferred one to the least one).
	 * @param prefs
	 */
	public void setPrefs(List<WeaponPref> prefs) {
		this.prefs = prefs;
	}

	/**
	 * Owner of this preferences.
	 * @return
	 */
	public WeaponPrefs getOwner() {
		return owner;
	}

}
