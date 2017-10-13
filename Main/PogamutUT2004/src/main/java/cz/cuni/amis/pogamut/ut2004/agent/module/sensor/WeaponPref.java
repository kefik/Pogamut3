package cz.cuni.amis.pogamut.ut2004.agent.module.sensor;

import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.Weapon;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType.Category;
import cz.cuni.amis.utils.HashCode;
import cz.cuni.amis.utils.NullCheck;

/**
 * Class that contains a weapon preference. I.e., stating:
 * <ol>
 * <li>weapon to be used</li>
 * <li>primary/secondary mode</li>
 * </ol>
 * 
 * @author Jimmy
 */
public class WeaponPref {

	private ItemType weapon;
	private boolean primary;
	private int hashCode;

	/**
	 * Use 'weapon' and the concrete mode.
	 * @param weapon
	 * @param primary true == use primary firing mode, false == use secondary firing mode
	 */
	public WeaponPref(ItemType weapon, boolean primary) {
		this.weapon = weapon;
		this.primary = primary;
		NullCheck.check(this.weapon, "weapon");
		if (weapon.getCategory() != Category.WEAPON) {
			throw new IllegalArgumentException("passed 'weapon' is not of ItemType.Category.WEAPON but " + weapon.getCategory().toString());
		}
		hashCode = new HashCode().add(weapon).add(primary).getHash();
	}
	
	/**
	 * Use 'weapon' with primary-firing-mode.
	 * @param weapon
	 * @param primary true == use primary firing mode, false == use secondary firing mode
	 */
	public WeaponPref(ItemType weapon) {
		this.weapon = weapon;
		this.primary = true;
		NullCheck.check(this.weapon, "weapon");
		if (weapon.getCategory() != Category.WEAPON) {
			throw new IllegalArgumentException("passed 'weapon' is not of ItemType.Category.WEAPON but " + weapon.getCategory().toString());
		}
		hashCode = new HashCode().add(weapon).add(primary).getHash();
	}
	
	@Override
	public int hashCode() {
		return hashCode;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null) return false;
		if (!(obj instanceof WeaponPref)) return false;
		WeaponPref pref = (WeaponPref)obj;
		return pref.weapon == weapon && pref.primary == primary;
	}
	
	/**
	 * Use 'weapon' and the concrete mode.
	 * @param weapon
	 * @param primary true == use primary firing mode, false == use secondary firing mode
	 */
	public WeaponPref(Weapon weapon, boolean primary) {
		this.weapon = weapon.getType();
		this.primary = primary;
		NullCheck.check(this.weapon, "weapon");
	}

	/**
	 * Which weapon to choose.
	 * @return
	 */
	public ItemType getWeapon() {
		return weapon;
	}

	/**
	 * Whether to use primary firing mode?
	 * @return
	 */
	public boolean isPrimary() {
		return primary;
	}
	
	/**
	 * Whether to use secondary firing mode?
	 * @return
	 */
	public boolean isSecondary() {
		return !primary;
	}
	
	@Override
	public String toString() {
		return "WeaponPref[type=" + weapon.getName() + ", " + (primary ? "primary mode" : "secondary mode") + "]";
	}
	
}
