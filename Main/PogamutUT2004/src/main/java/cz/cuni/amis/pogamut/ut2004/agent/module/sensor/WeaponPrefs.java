package cz.cuni.amis.pogamut.ut2004.agent.module.sensor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.Weapon;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.Weaponry;
import cz.cuni.amis.pogamut.ut2004.bot.IUT2004BotController;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;

/**
 * Class that allows you to easily define weapon preferences for your bot as well as time how often you may change your weapon.
 * <p><p>
 * Use this class in {@link IUT2004BotController#prepareBot(UT2004Bot)}, i.e., use methods such as {@link WeaponPrefs#addGeneralPref(ItemType, boolean)},
 * {@link WeaponPrefs#newPrefsRange(double)}, {@link WeaponPrefsRange#add(ItemType, boolean)}.
 * <p><p>
 * Preferences are never automatically wiped out!
 * 
 * @author Jimmy
 */
public class WeaponPrefs {

	protected List<WeaponPrefsRange> prefs = new ArrayList<WeaponPrefsRange>();
	protected Weaponry weaponry;
	protected UT2004Bot bot;
	protected WeaponPrefsRange generalPrefs;
	protected WeaponPrefs onlyGeneral;
	
	public WeaponPrefs(Weaponry weaponry, UT2004Bot bot) {
		this.weaponry = weaponry;
		this.bot = bot;
		this.generalPrefs = new WeaponPrefsRange(this, 0);
		this.onlyGeneral = new WeaponPrefs(weaponry, bot, new WeaponPrefsRange(this, 0)) {
			@Override
			public WeaponPrefsRange newPrefsRange(double minDistance) {
				throw new IllegalStateException("Can't invoke the method on 'generalOnly' preferences!");
			}
			@Override
			public WeaponPrefs addGeneralPref(ItemType weapon, boolean usePrimaryMode) {
				throw new IllegalStateException("Can't invoke the method on 'generalOnly' preferences!");
			}
			@Override
			public WeaponPrefs addGeneralPref(Weapon weapon, boolean usePrimaryMode) {
				throw new IllegalStateException("Can't invoke the method on 'generalOnly' preferences!");
			}
		};
	}
	
	protected WeaponPrefs(Weaponry weaponry, UT2004Bot bot, WeaponPrefsRange generalPrefs) {
		this.weaponry = weaponry;
		this.bot = bot;
		this.generalPrefs = new WeaponPrefsRange(this, generalPrefs);
		this.onlyGeneral = this;
	}
	
	/**
	 * Return weapon preferences that has only "general" weapon preferences, might come in handy.
	 * <p><p>
	 * WARNING: returned prefs are IMMUTABLE! You can't invoke method {@link WeaponPrefs#newPrefsRange(double)},  {@link WeaponPrefs#addGeneralPref(ItemType, boolean)} or {@link WeaponPrefs#addGeneralPref(Weapon, boolean)}.
	 * @return 
	 */
	public WeaponPrefs asGeneralOnly() {
		return this.onlyGeneral;
	}
	
	/**
	 * Removes all weapon preferences.
	 */
	public void clearAllPrefs() {
		prefs.clear();
		generalPrefs.clear();
		onlyGeneral.generalPrefs.clear();
	}
	
	/**
	 * Returns general weapon preferences.
	 * 
	 * @return
	 */
	public WeaponPrefsRange getGeneralPrefs() {
		return generalPrefs;
	}
	
	/**
	 * Adds another weapon as "the least preferable" one into general-preferences (used if no weapons are found for a given range)
	 * You may define weapons from the most preferred to the least preferred by sequentially calling this method.
	 * 
	 * @param weapon weapon to be used
	 * @param usePrimaryMode true == use primary firing mode, false == use secondary firing mode
	 */
	public WeaponPrefs addGeneralPref(ItemType weapon, boolean usePrimaryMode) {
		generalPrefs.add(weapon, usePrimaryMode);
		onlyGeneral.generalPrefs.add(weapon, usePrimaryMode);
		return this;
	}
	
	/**
	 * Adds another weapon as "the least preferable" one into general-preferences (used if no weapons are found for a given range)
	 * You may define weapons from the most preferred to the least preferred by sequentially calling this method.
	 * 
	 * @param weapon weapon to be used
	 * @param usePrimaryMode true == use primary firing mode, false == use secondary firing mode
	 */
	public WeaponPrefs addGeneralPref(Weapon weapon, boolean usePrimaryMode) {
		generalPrefs.add(weapon, usePrimaryMode);
		onlyGeneral.generalPrefs.add(weapon, usePrimaryMode);
		return this;
	}

	/**
	 * Creates new {@link WeaponPrefsRange}, these weapon will be used when the target is at "maxDistance" afar. Lower bound (minDistance)
	 * is then define by previous WeaponPrefsRange object (if such exist, otherwise it is 0).
	 * 
	 * @param maxDistance
	 * @return
	 */
	public WeaponPrefsRange newPrefsRange(double maxDistance) {
		WeaponPrefsRange newPrefs = new WeaponPrefsRange(this, maxDistance);
		this.prefs.add(newPrefs);
		Collections.sort(this.prefs, new Comparator<WeaponPrefsRange>() {
			@Override
			public int compare(WeaponPrefsRange o1, WeaponPrefsRange o2) {
				double diff = o1.getMaxDistance() - o2.getMaxDistance();
				if (diff > 0) return 1;
				if (diff < 0) return -1;
				return 0;
			}
		});
		return newPrefs;
	}
	
	/**
	 * Get preferences for a given distance. 
	 * <p><p>
	 * Distance may be negative == will choose only from the general preferences.
	 * <p><p>
	 * If no "ranges" are defined (no {@link WeaponPrefs#newPrefsRange(double)} has been used), it returns {@link WeaponPrefs#generalPrefs}.
	 * 
	 * @param distance
	 * @return
	 */
	public WeaponPrefsRange getWeaponPreferences(double distance) {
		if (distance < 0) {
			return generalPrefs;
		}
		if (prefs.size() == 0) return generalPrefs;
		int i = 0;
		for (WeaponPrefsRange pref : prefs) {
			double minDistance = pref.getMinDistance();
			if (minDistance > distance) {
				if (i == 0) return null;
				return prefs.get(i-1);
			}
			++i;
		}
		return prefs.get(prefs.size()-1);
	}

	/**
	 * Get range that is right after "weaponPrefsRange".
	 * @param weaponPrefsRange
	 * @return
	 */
	protected WeaponPrefsRange getPreviousRange(WeaponPrefsRange weaponPrefsRange) {
		if (weaponPrefsRange == generalPrefs) return null;
		int index = prefs.indexOf(weaponPrefsRange);
		if (index < 1) return null;
		return prefs.get(index-1);
	}
	
	/**
	 * Return the best weapon the bot has for a given distance (choosing right weapon preferances for a given distance).
	 * <p><p>
	 * May return null if "general preferences are not defined" or the bot does not have ammo to any of defined weapons.
	 * <p><p>
	 * Note that it may actually return "forbiddenWeapon" in the case that the bot does not have ammo for any other "more preferred" weapon
	 * but has ammo for some of forbiddenWeapon.
	 * <p><p>
	 * If distance < 0, only general preferences will be used.
	 * 
	 * @param distance
	 * @param forbiddenWeapons optionally, you may define weapons which bot should not choose (i.e. {@link ItemType#ROCKET_LAUNCHER})
	 * @return
	 */
	public WeaponPref getWeaponPreference(double distance, ItemType... forbiddenWeapons) {
		WeaponPref pref = null;
		if (distance >= 0 && prefs.size() != 0) {
			WeaponPrefsRange range = getWeaponPreferences(distance); 
			pref = range.getWeaponPreference(forbiddenWeapons);
			if (pref != null) {
				return pref;
			}
		}
		pref = generalPrefs.getWeaponPreference(forbiddenWeapons);
		if (pref != null) return pref;
		pref = generalPrefs.getWeaponPreference();
		if (pref != null) return pref;
		if (weaponry.getCurrentWeapon() != null) {
			return new WeaponPref(weaponry.getCurrentWeapon().getType(), true);
		}
		return null;
	}
	
	/**
	 * Return the best weapon the bot has to shoot at given location (choosing right weapon preferances for a given distance).
	 * <p><p>
	 * May return null if "general preferences are not defined" or the bot does not have ammo to any of defined weapons.
	 * <p><p>
	 * If target is null, only general preferences will be used.
	 * 
	 * @param target
	 * @param forbiddenWeapons optionally, you may define weapons which bot should not choose (i.e. {@link ItemType#ROCKET_LAUNCHER})
	 * @return
	 */
	public WeaponPref getWeaponPreference(ILocated target, ItemType... forbiddenWeapons) {
		if (target == null) {
			return getWeaponPreference(-1, forbiddenWeapons);
		} else {
			return getWeaponPreference(bot.getLocation().getDistance(target.getLocation()), forbiddenWeapons);
		}
	}
	
	/**
	 * Return the best weapon according ONLY general preferences.
	 * @param forbiddenWeapons optionally, you may define weapons which bot should not choose (i.e. {@link ItemType#ROCKET_LAUNCHER})
	 * @return
	 */
	public WeaponPref getWeaponPreference(ItemType... forbiddenWeapons) {
		return getWeaponPreference(-1, forbiddenWeapons);
	}
	
	/**
	 * Return the best weapon the bot has for a given distance (choosing right weapon preferances for a given distance).
	 * <p><p>
	 * May return null if "general preferences are not defined" or the bot does not have ammo to any of defined weapons.
	 * <p><p>
	 * Note that it may actually return "forbiddenWeapon" in the case that the bot does not have ammo for any other "more preferred" weapon
	 * but has ammo for some of forbiddenWeapon.
	 * <p><p>
	 * If distance < 0, only general preferences will be used.
	 * 
	 * @param distance
	 * @param forbiddenWeapons optionally, you may define weapons which bot should not choose (i.e. {@link WeaponPref#ROCKET_LAUNCHER})
	 * @return
	 */
	public WeaponPref getWeaponPreference(double distance, WeaponPref... forbiddenWeapons) {
		WeaponPref pref = null;
		if (distance >= 0 && prefs.size() != 0) {
			WeaponPrefsRange range = getWeaponPreferences(distance); 
			pref = range.getWeaponPreference(forbiddenWeapons);
			if (pref != null) {
				return pref;
			}
		}
		pref = generalPrefs.getWeaponPreference(forbiddenWeapons);
		if (pref != null) return pref;
		pref = generalPrefs.getWeaponPreference();
		if (pref != null) return pref;
		if (weaponry.getCurrentWeapon() != null) {
			return new WeaponPref(weaponry.getCurrentWeapon().getType(), true);
		}
		return null;
	}
	
	/**
	 * Return the best weapon the bot has to shoot at given location (choosing right weapon preferances for a given distance).
	 * <p><p>
	 * May return null if "general preferences are not defined" or the bot does not have ammo to any of defined weapons.
	 * <p><p>
	 * If target is null, only general preferences will be used.
	 * 
	 * @param target
	 * @param forbiddenWeapons optionally, you may define weapons which bot should not choose.
	 * @return
	 */
	public WeaponPref getWeaponPreference(ILocated target, WeaponPref... forbiddenWeapons) {
		if (target == null) {
			return getWeaponPreference(-1, forbiddenWeapons);
		} else {
			return getWeaponPreference(bot.getLocation().getDistance(target.getLocation()), forbiddenWeapons);
		}
	}
	
	/**
	 * Return the best weapon according ONLY general preferences.
	 * @param forbiddenWeapons optionally, you may define weapons which bot should not choose.
	 * @return
	 */
	public WeaponPref getWeaponPreference(WeaponPref... forbiddenWeapons) {
		return getWeaponPreference(-1, forbiddenWeapons);
	}
	
	/**
	 * Return the best weapon the bot has for a given distance (choosing right weapon preferances for a given distance).
	 * <p><p>
	 * May return null if "general preferences are not defined" or the bot does not have ammo to any of defined weapons.
	 * <p><p>
	 * If distance < 0, only general preferences will be used.
	 * 
	 * @param distance
	 * @return
	 */
	public WeaponPref getWeaponPreference(double distance) {
		return getWeaponPreference(distance, (ItemType[])null);
	}
	
	/**
	 * Return the best weapon the bot has to shoot at given location (choosing right weapon preferances for a given distance).
	 * <p><p>
	 * May return null if "general preferences are not defined" or the bot does not have ammo to any of defined weapons.
	 * <p><p>
	 * If target is null, only general preferences will be used.
	 * 
	 * @param target
	 * @return
	 */
	public WeaponPref getWeaponPreference(ILocated target) {
		return getWeaponPreference(target, (ItemType[])null);
	}
	
	/**
	 * Return the best weapon according ONLY general preferences.
	 * @return
	 */
	public WeaponPref getWeaponPreference() {
		return getWeaponPreference(-1, (ItemType[])null);
	}
	
	/**
	 * Returns all {@link WeaponPref} defined in general preferences + all range preferences, i.e.,
	 * all weapons your bot might be interested in. 
	 *
	 * @return
	 */
	public Set<WeaponPref> getPreferredWeapons() {
		Set<WeaponPref> result = new HashSet<WeaponPref>();
		
		result.addAll(generalPrefs.getPrefs());
		for (WeaponPrefsRange range : prefs) {
			result.addAll(range.getPrefs());
		}
		
		return result;
	}
	
}
