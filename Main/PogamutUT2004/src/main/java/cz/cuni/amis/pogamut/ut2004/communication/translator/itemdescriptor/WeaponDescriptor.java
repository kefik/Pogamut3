package cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor;

import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemTypeTranslator;

/**
 * Weapon descriptor describes all characteristics of a weapon.
 * 
 * @author Ondrej, knight
 */
public class WeaponDescriptor extends ItemDescriptor {

	private final ItemTypeTranslator itemTypeTranslator;
	
	public WeaponDescriptor(ItemTypeTranslator translator) {
		this.itemTypeTranslator = translator;
	}

	// Attributes
	@ItemDescriptorField
	private boolean melee = false;
	@ItemDescriptorField
	private boolean sniping = false;
	@ItemDescriptorField
	private boolean usesAltAmmo = false;

	// Primary firing mode
	@ItemDescriptorField
	private String priFireModeType;
	@ItemDescriptorField
	private boolean priSplashDamage = false;
	@ItemDescriptorField
	private boolean priSplashJump = false;
	@ItemDescriptorField
	private boolean priRecomSplashDamage = false;
	@ItemDescriptorField
	private boolean priTossed = false;
	@ItemDescriptorField
	private boolean priLeadTarget = false;
	@ItemDescriptorField
	private boolean priInstantHit = false;
	@ItemDescriptorField
	private boolean priFireOnRelease = false; // fire when released
	@ItemDescriptorField
	private boolean priWaitForRelease = false; // if we want to refire, we need
												// to stop pushing, push again
	@ItemDescriptorField
	private boolean priModeExclusive = false;
	@ItemDescriptorField
	private double priFireRate = 0; // how fast the weapon fire
	@ItemDescriptorField
	private double priBotRefireRate = 0; // when stoped shooting, how fast we
											// can resume
	@ItemDescriptorField
	private int priAmmoPerFire = 0;
	@ItemDescriptorField
	private int priAmmoClipSize = 0;
	@ItemDescriptorField
	private double priAimError = 0; // 0=none 1000=quite a bit
	@ItemDescriptorField
	private double priSpread = 0; // rotator units. no relation to AimError
	@ItemDescriptorField
	private int priSpreadStyle = 0; // is enum in fact
	@ItemDescriptorField
	private int priFireCount = 0;
	@ItemDescriptorField
	private double priDamageAtten = 0; // attenuate instant-hit/projectile
										// damage by this multiplier

	// Primary firing mode ammo
	@ItemDescriptorField
	private String priAmmoType;
	@ItemDescriptorField
	private int priInitialAmount = 0;
	@ItemDescriptorField
	private int priMaxAmount = 0;
	@ItemDescriptorField	
	private double priMaxRange = 0;

	// Primary firing mode ammo damage type
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

	// Primary firing mode projectile
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

	// Secondary firing mode
	@ItemDescriptorField
	private String secFireModeType;
	@ItemDescriptorField
	private boolean secSplashDamage = false;
	@ItemDescriptorField
	private boolean secSplashJump = false;
	@ItemDescriptorField
	private boolean secRecomSplashDamage = false;
	@ItemDescriptorField
	private boolean secTossed = false;
	@ItemDescriptorField
	private boolean secLeadTarget = false;
	@ItemDescriptorField
	private boolean secInstantHit = false;
	@ItemDescriptorField
	private boolean secFireOnRelease = false; // fire when released
	@ItemDescriptorField
	private boolean secWaitForRelease = false; // if we want to refire, we need
												// to stop pushing, push again
	@ItemDescriptorField
	private boolean secModeExclusive = false;
	@ItemDescriptorField
	private double secFireRate = 0; // how fast the weapon fire
	@ItemDescriptorField
	private double secBotRefireRate = 0; // when stoped shooting, how fast we
											// can resume
	@ItemDescriptorField
	private int secAmmoPerFire = 0;
	@ItemDescriptorField
	private int secAmmoClipSize = 0;
	@ItemDescriptorField
	private double secAimError = 0; // 0=none 1000=quite a bit
	@ItemDescriptorField
	private double secSpread = 0; // rotator units. no relation to AimError
	@ItemDescriptorField
	private int secSpreadStyle = 0; // is enum in fact
	@ItemDescriptorField
	private int secFireCount = 0;
	@ItemDescriptorField
	private double secDamageAtten = 0; // attenuate instant-hit/projectile
										// damage by this multiplier

	// Secondary firing mode ammo
	@ItemDescriptorField
	private String secAmmoType;
	@ItemDescriptorField
	private int secInitialAmount = 0;
	@ItemDescriptorField
	private int secMaxAmount = 0;
	@ItemDescriptorField
	private double secMaxRange = 0;

	// Secondary firing mode ammo damage type
	@ItemDescriptorField
	private String secDamageType;
	@ItemDescriptorField
	private boolean secArmorStops = true;
	@ItemDescriptorField
	private boolean secAlwaysGibs = false;
	@ItemDescriptorField
	private boolean secSpecial = false;
	@ItemDescriptorField
	private boolean secDetonatesGoop = false;
	@ItemDescriptorField
	private boolean secSuperWeapon = false;
	@ItemDescriptorField
	private boolean secExtraMomZ = false;

	// Secondary firing mode projectile
	@ItemDescriptorField
	private String secProjType;
	@ItemDescriptorField
	private double secDamage = 0;
	@ItemDescriptorField
	private double secSpeed = 0;
	@ItemDescriptorField
	private double secMaxSpeed = 0;
	@ItemDescriptorField
	private double secLifeSpan = 0;
	@ItemDescriptorField
	private double secDamageRadius = 0;
	@ItemDescriptorField
	private double secTossZ = 0;
	@ItemDescriptorField
	private double secMaxEffectDistance = 0;

	@Override
	public String toString() {
		return "WeaponDescriptor[pickupType="+ getPickupType() + ", inventoryType=" + getInventoryType() + ", itemCategory=" + getItemCategory() + "]";
	}

	/**
	 * UT engine informs us, if this weapon is known as melee (short range,
	 * close combat).
	 * 
	 * @return melee
	 */
	public boolean isMelee() {
		return melee;
	}

	/**
	 * UT engine infroms us, if this weapon is a sniper weapon (long range, high
	 * damage).
	 * 
	 * @return sniping
	 */
	public boolean isSniping() {
		return sniping;
	}

	/**
	 * Whether this weapon uses two separate ammo classes - first for primary
	 * firing mode, second for secondary.
	 * 
	 * @return usesAltAmmo
	 */
	public boolean isUsesAltAmmo() {
		return usesAltAmmo;
	}

	/**
	 * Returns the class of primary firing mode. If none, the weapon has NOT
	 * primary firing mode that means all information for primary firing mode
	 * are not relevant and will have default values.
	 * 
	 * @return priFireModeType
	 */
	public String getPriFireModeType() {
		return priFireModeType;
	}

	/**
	 * If our weapons primary firing mode does splash damage - area effect.
	 * Weapon will damage everything in certain raidus.
	 * 
	 * @return priSplashDamage
	 */
	public boolean isPriSplashDamage() {
		return priSplashDamage;
	}

	/**
	 * If this weapon can be used to boost jumping height.
	 * 
	 * @return priSplashJump
	 */
	public boolean isPriSplashJump() {
		return priSplashJump;
	}

	/**
	 * If the engine recomends us to use splash damage. For AI?
	 * 
	 * @return priRecomSplashDamage
	 * @todo Find correct information about this.
	 */
	public boolean isPriRecomSplashDamage() {
		return priRecomSplashDamage;
	}

	/**
	 * If the weapon in this firing mode is tossing projectiles. The projectiles
	 * will usually fall down to the ground slowly (they won't fly in a line).
	 * 
	 * @return priTossed
	 */
	public boolean isPriTossed() {
		return priTossed;
	}

	/**
	 * If this firing mode is capable of shooting projectiles that will lead the
	 * target.
	 * 
	 * @return priLeadTarget
	 * @todo Find correct info about this.
	 */
	public boolean isPriLeadTarget() {
		return priLeadTarget;
	}

	/**
	 * If this firing mode does instant hits - will hit the target at the moment
	 * it is fired. Usually true for weapon like machine gun. False for slow
	 * projectiles.
	 * 
	 * @return priInsantHit
	 */
	public boolean isPriInstantHit() {
		return priInstantHit;
	}

	/**
	 * If to fire this firing mode you need to press shooting button (start
	 * shooting) and then release it (stop shooting). Usually true for charged
	 * weapons. You'll first charge your weapon and when decided you've charged
	 * the weapon enough, you release the key and the weapon fires.
	 * 
	 * @return priFireOnRelease
	 */
	public boolean isPriFireOnRelease() {
		return priFireOnRelease;
	}

	/**
	 * If to fire this mode you need to stop pressing shooting button between
	 * two shots to shoot. You will fire once when pressing the button, then you
	 * need to press it again to fire again.
	 * 
	 * @return priWaitForRelease
	 */
	public boolean isPriWaitForRelease() {
		return priWaitForRelease;
	}

	/**
	 * If true you are unable to fire both firing modes of this weapon at the
	 * same time.
	 * 
	 * @return priModeExlusive
	 */
	public boolean isPriModeExclusive() {
		return priModeExclusive;
	}

	/**
	 * How fast this weapon fires. Delay between two shots, when shooting
	 * continuously.
	 * 
	 * @return priFireRate
	 */
	public double getPriFireRate() {
		return priFireRate;
	}

	/**
	 * How fast we can refire the weapon. Delay between when we stop shooting
	 * and when we can start shooting again.
	 * 
	 * @return priBotRefireRate
	 */
	public double getPriBotRefireRate() {
		return priBotRefireRate;
	}

	/**
	 * How much ammo this weapon consumes for one shot.
	 * 
	 * @return priAmmoPerFire
	 */
	public int getPriAmmoPerFire() {
		return priAmmoPerFire;
	}

	/**
	 * If this mode has clips how big they are.
	 * 
	 * @return priAmmoClipSize
	 * @todo Find correct info.
	 */
	public int getPriAmmoClipSize() {
		return priAmmoClipSize;
	}

	/**
	 * How big aiming error this firing mode has. 0 - none, 1000 quite a bit.
	 * 
	 * @return priAimError
	 */
	public double getPriAimError() {
		return priAimError;
	}

	/**
	 * When the weapon is spreading some projectiles, here is how big the spread
	 * is. In UT rotator units. No relation to AimError.
	 * 
	 * @return priSpread
	 */
	public double getPriSpread() {
		return priSpread;
	}

	/**
	 * The style of weapon spread. Is in fact enum. Definition below (starts
	 * from 0 and goes up).
	 * 
	 * var() enum ESpreadStyle { SS_None, SS_Random, // spread is max random
	 * angle deviation SS_Line, // spread is angle between each projectile
	 * SS_Ring } SpreadStyle;
	 * 
	 * @return priSpreadStyle
	 */
	public int getPriSpreadStyle() {
		return priSpreadStyle;
	}

	/**
	 * Firing mode fire count.
	 * 
	 * @return priFireCount
	 * @todo Find out what this is.
	 */
	public int getPriFireCount() {
		return priFireCount;
	}

	/**
	 * Attenuate instant-hit/projectile damage by this multiplier.
	 * 
	 * @return priDamageAtten
	 */
	public double getPriDamageAtten() {
		return priDamageAtten;
	}

	/**
	 * The class of primary firing mode ammunition type. If none, then the
	 * firing mode does not use any ammunition. That means all ammo specific
	 * information is not relevant and will have default values (ammo specific
	 * information starts from this attribute and ends at priProjType attribute -
	 * that is not ammo specific).
	 * 
	 * @return priAmmoType
	 */
	public String getPriAmmoType() {
		return priAmmoType;
	}
	
	private ItemType priAmmoItemType = null;

	
	/**
	 * @return ammo type as {@link ItemType}, null if not exist or uses primary ammo
	 */
	public ItemType getPriAmmoItemType() {
		if (getPriAmmoType() == null) return null;
		if (priAmmoItemType != null) return priAmmoItemType;
		priAmmoItemType = itemTypeTranslator.get(getPriAmmoType());
		return priAmmoItemType;
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
	 * Holds the class of the projectile of this firing mode. If none, then the
	 * mode does not spawn projectiles. all the info below is then not relevant
	 * and will have default values on.
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

	/**
	 * Returns the class of secondary firing mode. If none, the weapon has NOT
	 * secondary firing mode that means all information for secondary firing
	 * mode are not relevant and will have default values.
	 * 
	 * @return secFireModeType
	 */
	public String getSecFireModeType() {
		return secFireModeType;
	}

	/**
	 * If our weapons secondary firing mode does splash damage - area effect.
	 * Weapon will damage everything in certain raidus.
	 * 
	 * @return secSplashDamage
	 */
	public boolean isSecSplashDamage() {
		return secSplashDamage;
	}

	/**
	 * If this weapon can be used to boost jumping height.
	 * 
	 * @return secSplashJump
	 */
	public boolean isSecSplashJump() {
		return secSplashJump;
	}

	/**
	 * If the engine recomends us to use splash damage. For AI?
	 * 
	 * @return secRecomSplashDamage
	 * @todo Find correct information about this.
	 */
	public boolean isSecRecomSplashDamage() {
		return secRecomSplashDamage;
	}

	/**
	 * If the weapon in this firing mode is tossing projectiles. The projectiles
	 * will usually fall down to the ground slowly (they won't fly in a line).
	 * 
	 * @return secTossed
	 */
	public boolean isSecTossed() {
		return secTossed;
	}

	/**
	 * If this firing mode is capable of shooting projectiles that will lead the
	 * target.
	 * 
	 * @return secLeadTarget
	 * @todo Find correct info about this.
	 */
	public boolean isSecLeadTarget() {
		return secLeadTarget;
	}

	/**
	 * If this firing mode does instant hits - will hit the target at the moment
	 * it is fired. Usually true for weapon like machine gun. False for slow
	 * projectiles.
	 * 
	 * @return secInsantHit
	 */
	public boolean isSecInstantHit() {
		return secInstantHit;
	}

	/**
	 * If to fire this firing mode you need to press shooting button (start
	 * shooting) and then release it (stop shooting). Usually true for charged
	 * weapons. You'll first charge your weapon and when decided you've charged
	 * the weapon enough, you release the key and the weapon fires.
	 * 
	 * @return secFireOnRelease
	 */
	public boolean isSecFireOnRelease() {
		return secFireOnRelease;
	}

	/**
	 * If to fire this mode you need to stop pressing shooting button between
	 * two shots to shoot. You will fire once when pressing the button, then you
	 * need to press it again to fire again.
	 * 
	 * @return secWaitForRelease
	 */
	public boolean isSecWaitForRelease() {
		return secWaitForRelease;
	}

	/**
	 * If true you are unable to fire both firing modes of this weapon at the
	 * same time.
	 * 
	 * @return secModeExlusive
	 */
	public boolean isSecModeExclusive() {
		return secModeExclusive;
	}

	/**
	 * How fast this weapon fires. Delay between two shots, when shooting
	 * continuously.
	 * 
	 * @return secFireRate
	 */
	public double getSecFireRate() {
		return secFireRate;
	}

	/**
	 * How fast we can refire the weapon. Delay between when we stop shooting
	 * and when we can start shooting again.
	 * 
	 * @return secBotRefireRate
	 */
	public double getSecBotRefireRate() {
		return secBotRefireRate;
	}

	/**
	 * How much ammo this weapon consumes for one shot.
	 * 
	 * @return secAmmoPerFire
	 */
	public int getSecAmmoPerFire() {
		return secAmmoPerFire;
	}

	/**
	 * If this mode has clips how big they are.
	 * 
	 * @return secAmmoClipSize
	 * @todo Find correct info.
	 */
	public int getSecAmmoClipSize() {
		return secAmmoClipSize;
	}

	/**
	 * How big aiming error this firing mode has. 0 - none, 1000 quite a bit.
	 * 
	 * @return secAimError
	 */
	public double getSecAimError() {
		return secAimError;
	}

	/**
	 * When the weapon is spreading some projectiles, here is how big the spread
	 * is. In UT rotator units. No relation to AimError.
	 * 
	 * @return secSpread
	 */
	public double getSecSpread() {
		return secSpread;
	}

	/**
	 * The style of weapon spread. Is in fact enum. Definition below (starts
	 * from 0 and goes up).
	 * 
	 * var() enum ESpreadStyle { SS_None, SS_Random, // spread is max random
	 * angle deviation SS_Line, // spread is angle between each projectile
	 * SS_Ring } SpreadStyle;
	 * 
	 * @return secSpreadStyle
	 */
	public int getSecSpreadStyle() {
		return secSpreadStyle;
	}

	/**
	 * Firing mode fire count.
	 * 
	 * @return secFireCount
	 * @todo Find out what this is.
	 */
	public int getSecFireCount() {
		return secFireCount;
	}

	/**
	 * Attenuate instant-hit/projectile damage by this multiplier.
	 * 
	 * @return secDamageAtten
	 */
	public double getSecDamageAtten() {
		return secDamageAtten;
	}

	/**
	 * The class of secondary firing mode ammunition type. If none, then the
	 * firing mode does not use any ammunition. That means all ammo specific
	 * information is not relevant and will have default values (ammo specific
	 * informaton starts from this attribute and ends at secProjType attribute -
	 * that is not ammo specific).
	 * 
	 * @return secAmmoType
	 */
	public String getSecAmmoType() {
		return secAmmoType;
	}
	
	private ItemType secAmmoItemType = null;
	
	/**
	 * @return secondary ammo type as {@link ItemType}, null if not exist or uses primary ammo
	 */
	public ItemType getSecAmmoItemType() {
		if (getSecAmmoType() == null || getSecAmmoType().equals("None")) return null;
		if (secAmmoItemType != null) return secAmmoItemType;
		secAmmoItemType = itemTypeTranslator.get(getSecAmmoType());
		return secAmmoItemType;
	}


	/**
	 * Initial amount of ammunition. We get this if we pick up the item for the
	 * first time.
	 * 
	 * @return secInitialAmount
	 */
	public int getSecInitialAmount() {
		return secInitialAmount;
	}

	/**
	 * Maximum amount of this ammunition we can hold in our inventory.
	 * 
	 * @return secMaxAmount
	 */
	public int getSecMaxAmount() {
		return secMaxAmount;
	}

	/**
	 * Maximum firing range. 0 if not limited - probably.
	 * 
	 * @return secMaxRange
	 * @todo Find out how this works.
	 */
	public double getSecMaxRange() {
		return secMaxRange;
	}

	/**
	 * Class of this ammunitions damage type. If ammo is not none, then this
	 * shouldn't be none either.
	 * 
	 * @return secDamageType
	 */
	public String getSecDamageType() {
		return secDamageType;
	}

	/**
	 * If this damage can be stopped by an armor.
	 * 
	 * @return secArmorStops
	 */
	public boolean isSecArmorStops() {
		return secArmorStops;
	}

	/**
	 * If this damage will kill us instantly.
	 * 
	 * @return secAlwaysGibs
	 */
	public boolean isSecAlwaysGibs() {
		return secAlwaysGibs;
	}

	/**
	 * If this damage is special.
	 * 
	 * @return secSpecial
	 * @todo find out what it is.
	 */
	public boolean isSecSpecial() {
		return secSpecial;
	}

	/**
	 * If this damage can detonate goop created by bio rifle (not sure).
	 * 
	 * @return secDetonatesGoop
	 * @todo Find out correct info.
	 */
	public boolean isSecDetonatesGoop() {
		return secDetonatesGoop;
	}

	/**
	 * If this damage is caused by super weapon and will damage also team mates
	 * even if friendly fire is off.
	 * 
	 * @return secSuperWeapon
	 */
	public boolean isSecSuperWeapon() {
		return secSuperWeapon;
	}

	/**
	 * If the hit by this damage will add some speed to the target (will "push"
	 * the target a bit).
	 * 
	 * @return secExtraMomZ
	 */
	public boolean isSecExtraMomZ() {
		return secExtraMomZ;
	}

	/**
	 * Holds the class of the projectile of this firing mode. If none, then the
	 * mode does not spawn projectiles. all the info below is then not relevant
	 * and will have default values on.
	 * 
	 * @return secProjType
	 */
	public String getSecProjType() {
		return secProjType;
	}

	/**
	 * Damage of the projectile.
	 * 
	 * @return secDamage
	 */
	public double getSecDamage() {
		return secDamage;
	}

	/**
	 * Default speed of the projectile - probably the projectile has this speed
	 * when fired.
	 * 
	 * @return secSpeed
	 */
	public double getSecSpeed() {
		return secSpeed;
	}

	/**
	 * Maximum possible speed of this projectile.
	 * 
	 * @return secMaxSpeed
	 */
	public double getSecMaxSpeed() {
		return secMaxSpeed;
	}

	/**
	 * Life span of this projectile. How long the projectile lasts in the
	 * environment. If 0 than probably unlimited.
	 * 
	 * @return secLifeSpan
	 */
	public double getSecLifeSpan() {
		return secLifeSpan;
	}

	/**
	 * If the projectile does splash damage, the value here won't be zero and
	 * will specify the radius of the splash damage in ut units.
	 * 
	 * @return secDamageRadius
	 */
	public double getSecDamageRadius() {
		return secDamageRadius;
	}

	/**
	 * Probably the amount of speed added to Z velocity vector when this
	 * projectile is fired. In UT units.
	 * 
	 * @return secTossZ
	 * @todo Find out correct info.
	 */
	public double getSecTossZ() {
		return secTossZ;
	}

	/**
	 * Maximum effective distance of the projectile. Probably 0 if not limited.
	 * 
	 * @return secMaxEffectDistance
	 * @todo Find out correct info.
	 */
	public double getSecMaxEffectDistance() {
		return secMaxEffectDistance;
	}

}
