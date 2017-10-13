package cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor;

/**
 * Ammo desciptor describes the characteristics of an ammunition.
 * 
 * @author Ondrej, knight
 */
public class AmmoDescriptor extends ItemDescriptor {

	// This ammunition attributes
	@ItemDescriptorField
	private String priAmmoType;
	@ItemDescriptorField
	private int priInitialAmount = 0;
	@ItemDescriptorField
	private int priMaxAmount = 0;
	@ItemDescriptorField
	private double priMaxRange = 0;

	// This ammo damage type
	@ItemDescriptorField
	private String priDamageType;
	@ItemDescriptorField
	private boolean priArmorStops = true;
	@ItemDescriptorField
	private boolean priAlwaysGibs = false;
	@ItemDescriptorField
	private boolean priSpecial = false;
	@ItemDescriptorField
	private boolean priDetonatesGoop = false;
	@ItemDescriptorField
	private boolean priSuperWeapon = false;
	@ItemDescriptorField
	private boolean priExtraMomZ = false;

	// This ammo projectile
	@ItemDescriptorField
	private String priProjType;
	@ItemDescriptorField
	private double priDamage = 0;
	@ItemDescriptorField
	private double priSpeed = 0;
	@ItemDescriptorField
	private double priMaxSpeed = 0;
	@ItemDescriptorField
	private double priLifeSpan = 0;
	@ItemDescriptorField
	private double priDamageRadius = 0;
	@ItemDescriptorField
	private double priTossZ = 0;
	@ItemDescriptorField
	private double priMaxEffectDistance = 0;

	@Override
	public String toString() {
		return "AmmoDescriptor[pickupType=" + getPickupType() + ", inventoryType = " + getInventoryType() + ", amount=" + getAmount() + "]";
	}

	/**
	 * The class of this ammunition type.
	 * 
	 * @return priAmmoType
	 */
	public String getPriAmmoType() {
		return priAmmoType;
	}

	/**
	 * Initial amount of ammunition. We get this if we pick up the item for the
	 * first time.
	 * 
	 * @return priInitialAmount
	 */
	public int getPriInitialAmount() {
		return priInitialAmount;
	}

	/**
	 * Maximum amount of this ammunition we can hold in our inventory.
	 * 
	 * @return priMaxAmount
	 */
	public int getPriMaxAmount() {
		return priMaxAmount;
	}

	/**
	 * Maximum firing range. 0 if not limited - probably.
	 * 
	 * @return priMaxRange
	 * @todo Find out how this works.
	 */
	public double getPriMaxRange() {
		return priMaxRange;
	}

	/**
	 * Class of this ammunitions damage type. If ammo is not none, then this
	 * shouldn't be none either.
	 * 
	 * @return priDamageType
	 */
	public String getPriDamageType() {
		return priDamageType;
	}

	/**
	 * If this damage can be stopped by an armor.
	 * 
	 * @return priArmorStops
	 */
	public boolean isPriArmorStops() {
		return priArmorStops;
	}

	/**
	 * If this damage will kill us instantly.
	 * 
	 * @return priAlwaysGibs
	 */
	public boolean isPriAlwaysGibs() {
		return priAlwaysGibs;
	}

	/**
	 * If this damage is special.
	 * 
	 * @return priSpecial
	 * @todo find out what it is.
	 */
	public boolean isPriSpecial() {
		return priSpecial;
	}

	/**
	 * If this damage can detonate goop created by bio rifle (not sure).
	 * 
	 * @return priDetonatesGoop
	 * @todo Find out correct info.
	 */
	public boolean isPriDetonatesGoop() {
		return priDetonatesGoop;
	}

	/**
	 * If this damage is caused by super weapon and will damage also team mates
	 * even if friendly fire is off.
	 * 
	 * @return priSuperWeapon
	 */
	public boolean isPriSuperWeapon() {
		return priSuperWeapon;
	}

	/**
	 * If the hit by this damage will add some speed to the target (will "push"
	 * the target a bit).
	 * 
	 * @return priExtraMomZ
	 */
	public boolean isPriExtraMomZ() {
		return priExtraMomZ;
	}

	/**
	 * Holds the class of the projectile spawn by this ammo type. If none, then
	 * the ammo does not spawn projectiles. all the info below is then not
	 * relevant and will have default values on.
	 * 
	 * @return priProjType
	 */
	public String getPriProjType() {
		return priProjType;
	}

	/**
	 * Damage of the projectile.
	 * 
	 * @return priDamage
	 */
	public double getPriDamage() {
		return priDamage;
	}

	/**
	 * Default speed of the projectile - probably the projectile has this speed
	 * when fired.
	 * 
	 * @return priSpeed
	 */
	public double getPriSpeed() {
		return priSpeed;
	}

	/**
	 * Maximum possible speed of this projectile.
	 * 
	 * @return priMaxSpeed
	 */
	public double getPriMaxSpeed() {
		return priMaxSpeed;
	}

	/**
	 * Life span of this projectile. How long the projectile lasts in the
	 * environment. If 0 than probably unlimited.
	 * 
	 * @return priLifeSpan
	 */
	public double getPriLifeSpan() {
		return priLifeSpan;
	}

	/**
	 * If the projectile does splash damage, the value here won't be zero and
	 * will specify the radius of the splash damage in ut units.
	 * 
	 * @return priDamageRadius
	 */
	public double getPriDamageRadius() {
		return priDamageRadius;
	}

	/**
	 * Probably the amount of speed added to Z velocity vector when this
	 * projectile is fired. In UT units.
	 * 
	 * @return priTossZ
	 * @todo Find out correct info.
	 */
	public double getPriTossZ() {
		return priTossZ;
	}

	/**
	 * Maximum effective distance of the projectile. Probably 0 if not limited.
	 * 
	 * @return priMaxEffectDistance
	 * @todo Find out correct info.
	 */
	public double getPriMaxEffectDistance() {
		return priMaxEffectDistance;
	}

}
