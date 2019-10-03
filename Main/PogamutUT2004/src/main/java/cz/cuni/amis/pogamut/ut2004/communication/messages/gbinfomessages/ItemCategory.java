package cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages;
 		
 		// --- IMPORTS FROM /messages/settings/javasettings/javaimport BEGIN
			import java.util.*;import javax.vecmath.*;import cz.cuni.amis.pogamut.base.communication.messages.*;import cz.cuni.amis.pogamut.base.communication.worldview.*;import cz.cuni.amis.pogamut.base.communication.worldview.event.*;import cz.cuni.amis.pogamut.base.communication.worldview.object.*;import cz.cuni.amis.pogamut.multi.communication.worldview.object.*;import cz.cuni.amis.pogamut.base.communication.translator.event.*;import cz.cuni.amis.pogamut.multi.communication.translator.event.*;import cz.cuni.amis.pogamut.base3d.worldview.object.*;import cz.cuni.amis.pogamut.base3d.worldview.object.event.*;import cz.cuni.amis.pogamut.ut2004.communication.messages.*;import cz.cuni.amis.pogamut.ut2004.communication.worldview.objects.*;import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.objects.*;import cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor.*;import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType.Category;import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;import cz.cuni.amis.utils.exception.*;import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdateResult.Result;import cz.cuni.amis.utils.SafeEquals;import cz.cuni.amis.pogamut.base.agent.*;import cz.cuni.amis.pogamut.multi.agent.*;import cz.cuni.amis.pogamut.multi.communication.worldview.property.*;import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.property.*;import cz.cuni.amis.utils.token.*;import cz.cuni.amis.utils.*;
		// --- IMPORTS FROM /messages/settings/javasettings/javaimport END
		
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name='all'] BEGIN
				
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name='all'] END
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name=event]+classtype[@name=impl] BEGIN
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name=event]+classtype[@name=impl] END
    
 		/**
         *  
         			Definition of the event ITC.
         		
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Synchronous/asynchronous message. Holds all attributes of item category.
		There are many item categories in UT2004. This class holds attributes for all of them.
		When some item category is exported just appropriate attributes are exported with it.
	
         */
 	public class ItemCategory 
  				extends InfoMessage
    			implements IWorldEvent, IWorldChangeEvent
    			
	    	,ItemTyped
	    {
 	
    	/** Example how the message looks like - used during parser tests. */
    	public static final String PROTOTYPE =
    		"ITC {InventoryType text}  {PickupType xWeapons.FlakCannonPickup}  {ItemCategory null}  {Melee False}  {Sniping False}  {UsesAltAmmo False}  {PriFireModeType text}  {PriSplashDamage False}  {PriSplashJump False}  {PriRecomSplashDamage False}  {PriTossed False}  {PriLeadTarget False}  {PriInstantHit False}  {PriFireOnRelease False}  {PriWaitForRelease False}  {PriModeExclusive False}  {PriFireRate 0}  {PriBotRefireRate 0}  {PriAmmoPerFire 0}  {PriAmmoClipSize 0}  {PriAimError 0}  {PriSpread 0}  {PriSpreadStyle 0}  {PriFireCount 0}  {PriDamageAtten 0}  {PriAmmoType text}  {PriInitialAmount 0}  {PriMaxAmount 0}  {PriMaxRange 0}  {PriDamageType text}  {PriArmorStops False}  {PriAlwaysGibs False}  {PriSpecial False}  {PriDetonatesGoop False}  {PriSuperWeapon False}  {PriExtraMomZ False}  {PriProjType text}  {PriDamage 0}  {PriDamageMax 0}  {PriDamageMin 0}  {PriSpeed 0}  {PriMaxSpeed 0}  {PriLifeSpan 0}  {PriDamageRadius 0}  {PriTossZ 0}  {PriMaxEffectDistance 0}  {SecFireModeType text}  {SecSplashDamage False}  {SecSplashJump False}  {SecRecomSplashDamage False}  {SecTossed False}  {SecLeadTarget False}  {SecInstantHit False}  {SecFireOnRelease False}  {SecWaitForRelease False}  {SecModeExclusive False}  {SecFireRate 0}  {SecBotRefireRate 0}  {SecAmmoPerFire 0}  {SecAmmoClipSize 0}  {SecAimError 0}  {SecSpread 0}  {SecSpreadStyle 0}  {SecFireCount 0}  {SecDamageAtten 0}  {SecAmmoType text}  {SecInitialAmount 0}  {SecMaxAmount 0}  {SecMaxRange 0}  {SecDamageType text}  {SecArmorStops False}  {SecAlwaysGibs False}  {SecSpecial False}  {SecDetonatesGoop False}  {SecSuperWeapon False}  {SecExtraMomZ False}  {SecProjType text}  {SecDamage 0}  {SecDamageMax 0}  {SecDamageMin 0}  {SecSpeed 0}  {SecMaxSpeed 0}  {SecLifeSpan 0}  {SecDamageRadius 0}  {SecTossZ 0}  {SecMaxEffectDistance 0}  {Amount 0}  {SuperHeal False} ";
    
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public ItemCategory()
		{
		}
	
    	
    	
    	
    	/**
		 * Creates new instance of the message ItemCategory.
		 * 
		Synchronous/asynchronous message. Holds all attributes of item category.
		There are many item categories in UT2004. This class holds attributes for all of them.
		When some item category is exported just appropriate attributes are exported with it.
	
		 * Corresponding GameBots message
		 *   
		 *   is
		 *   ITC.
		 * 
 	  	 * 
		 *   
		 *     @param InventoryType 
			By this class the item is represented in inventory. This is inventory type class.
		
		 *   
		 * 
		 *   
		 *     @param PickupType 
			By this class the item is represented in the map. This is pickup type class.
		
		 *   
		 * 
		 *   
		 *     @param ItemCategory 
			Category of the item. Can be "Weapon", "Adrenaline", "Ammo", "Armor", "Shield", "Health" or "Other".
		
		 *   
		 * 
		 *   
		 *     @param Melee 
		For Weapon. True if the weapon is melee weapon (close range).
		
		 *   
		 * 
		 *   
		 *     @param Sniping 
		For Weapon. True if the weapon is sniping weapon (long range).
		
		 *   
		 * 
		 *   
		 *     @param UsesAltAmmo 
		For Weapon. True if the weapon uses two separate ammos for primary and secondary firing mode.
		
		 *   
		 * 
		 *   
		 *     @param PriFireModeType 
			For Weapon, primary firing mode. Type of the firing mode. If none, the weapon does not have this fireing mode.
		
		 *   
		 * 
		 *   
		 *     @param PriSplashDamage 
			For Weapon, primary firing mode. If this mode does splash damage.
		
		 *   
		 * 
		 *   
		 *     @param PriSplashJump 
			For Weapon, primary firing mode. If the splash damage of this firing mode can be used for increasing jump height.
		
		 *   
		 * 
		 *   
		 *     @param PriRecomSplashDamage 
			For Weapon, primary firing mode. If the engine recommends to use this splash damage. TODO
		
		 *   
		 * 
		 *   
		 *     @param PriTossed 
			For Weapon, primary firing mode. If the this mode is tossing something (projectile) out.
		
		 *   
		 * 
		 *   
		 *     @param PriLeadTarget 
			For Weapon, primary firing mode. If this mode can lead the target. TODO
		
		 *   
		 * 
		 *   
		 *     @param PriInstantHit 
			For Weapon, primary firing mode. If this mode does instant hits - weapon hits instantly - no flying time for bullets.
		
		 *   
		 * 
		 *   
		 *     @param PriFireOnRelease 
			For Weapon, primary firing mode. If to fire this mode you need to press shooting button (start shooting) and then release it (stop shooting). Usually for charging weapons.
		
		 *   
		 * 
		 *   
		 *     @param PriWaitForRelease 
			For Weapon, primary firing mode. If to fire this mode you need to stop pressing shooting button between two shots to shoot. You will fire once when pressing the button, then you need to press it again to fire again.
		
		 *   
		 * 
		 *   
		 *     @param PriModeExclusive 
			For Weapon, primary firing mode. If this firing mode cannot be used at the same time with other firing mode of the weapon.
		
		 *   
		 * 
		 *   
		 *     @param PriFireRate 
			For Weapon, primary firing mode. Fire rate in seconds. How fast the weapon fires if we are firing continuosly.
		
		 *   
		 * 
		 *   
		 *     @param PriBotRefireRate 
			For Weapon, primary firing mode. Refire rate for bots in seconds. When we stop firing how long does it take to resume firing again.
		
		 *   
		 * 
		 *   
		 *     @param PriAmmoPerFire 
			For Weapon, primary firing mode. Needed amount of ammo to fire this weapon mode once.
		
		 *   
		 * 
		 *   
		 *     @param PriAmmoClipSize 
			For Weapon, primary firing mode. If the weapon mode has clips, their size. TODO
		
		 *   
		 * 
		 *   
		 *     @param PriAimError 
			For Weapon, primary firing mode. Aiming error of the weapon. 0 none, 1000 quite a bit.
		
		 *   
		 * 
		 *   
		 *     @param PriSpread 
			For Weapon, primary firing mode. Double, rotator units. No relation to aim error.
		
		 *   
		 * 
		 *   
		 *     @param PriSpreadStyle 
			For Weapon, primary firing mode. Type of spreading. TODO
		
		 *   
		 * 
		 *   
		 *     @param PriFireCount 
			For Weapon, primary firing mode. TODO
		
		 *   
		 * 
		 *   
		 *     @param PriDamageAtten 
			For Weapon, primary firing mode. Attenuate instant-hit/projectile damage by this multiplier.
		
		 *   
		 * 
		 *   
		 *     @param PriAmmoType 
			For Ammo or for Weapon, primary firing mode. Class of the ammo.
		
		 *   
		 * 
		 *   
		 *     @param PriInitialAmount 
			For Ammo or for Weapon, primary firing mode. Amount of ammo we get if we pick up the item (weapon or ammo) for the first time.
		
		 *   
		 * 
		 *   
		 *     @param PriMaxAmount 
			For Ammo or for Weapon, primary firing mode. Max amount of ammo of this type we can have in our inventory.
		
		 *   
		 * 
		 *   
		 *     @param PriMaxRange 
			For Ammo or for Weapon, primary firing mode. TODO
		
		 *   
		 * 
		 *   
		 *     @param PriDamageType 
			For Ammo or for Weapon, primary firing mode. Type of the damage. Maybe the same string for all damage.
		
		 *   
		 * 
		 *   
		 *     @param PriArmorStops 
			For Ammo or for Weapon, primary firing mode. If the armor is effective against this damage type.
		
		 *   
		 * 
		 *   
		 *     @param PriAlwaysGibs 
			For Ammo or for Weapon, primary firing mode. If this damage kills instantly.
		
		 *   
		 * 
		 *   
		 *     @param PriSpecial 
			For Ammo or for Weapon, primary firing mode. If this damage is special. TODO
		
		 *   
		 * 
		 *   
		 *     @param PriDetonatesGoop 
			For Ammo or for Weapon, primary firing mode. If this damage detonates goops. TODO
		
		 *   
		 * 
		 *   
		 *     @param PriSuperWeapon 
			For Ammo or for Weapon, primary firing mode. If this damage is super weapon damage. Kills everyone even teammates.
		
		 *   
		 * 
		 *   
		 *     @param PriExtraMomZ 
			For Ammo or for Weapon, primary firing mode. If this damage adds something to Panws momentum. TODO
		
		 *   
		 * 
		 *   
		 *     @param PriProjType 
			For Ammo or for Weapon, primary firing mode. Class of the projectile this ammo, weapon spawns. 
		
		 *   
		 * 
		 *   
		 *     @param PriDamage 
			For Ammo or for Weapon, primary firing mode. How much damage this projectile does.
		
		 *   
		 * 
		 *   
		 *     @param PriDamageMax 
			For Ammo or for Weapon, primary firing mode. How much maximum damage this projectile does.
		
		 *   
		 * 
		 *   
		 *     @param PriDamageMin 
			For Ammo or for Weapon, primary firing mode. How much minimum damage this projectile does.
		
		 *   
		 * 
		 *   
		 *     @param PriSpeed 
			For Ammo or for Weapon, primary firing mode. Default projectile speed.
		
		 *   
		 * 
		 *   
		 *     @param PriMaxSpeed 
			For Ammo or for Weapon, primary firing mode. Maximum projectile speed.
		
		 *   
		 * 
		 *   
		 *     @param PriLifeSpan 
			For Ammo or for Weapon, primary firing mode. Maximum amount of time in seconds this projectile can survive in the environment.
		
		 *   
		 * 
		 *   
		 *     @param PriDamageRadius 
			For Ammo or for Weapon, primary firing mode. If the projectile does splash damage, here is radius in ut units of the splash.
		
		 *   
		 * 
		 *   
		 *     @param PriTossZ 
			For Ammo or for Weapon, primary firing mode. If the projectile is tossed, here is velocity in Z direction of the toss. TODO
		
		 *   
		 * 
		 *   
		 *     @param PriMaxEffectDistance 
			For Ammo or for Weapon, primary firing mode. Maximum effective distance. TODO
		
		 *   
		 * 
		 *   
		 *     @param SecFireModeType 
			For Weapon, secondary firing mode. Type of the firing mode. If none, the weapon does not have this fireing mode.
		
		 *   
		 * 
		 *   
		 *     @param SecSplashDamage 
			For Weapon, secondary firing mode. If this mode does splash damage.
		
		 *   
		 * 
		 *   
		 *     @param SecSplashJump 
			For Weapon, secondary firing mode. If the splash damage of this firing mode can be used for increasing jump height.
		
		 *   
		 * 
		 *   
		 *     @param SecRecomSplashDamage 
			For Weapon, secondary firing mode. If the engine recommends to use this splash damage. TODO
		
		 *   
		 * 
		 *   
		 *     @param SecTossed 
			For Weapon, secondary firing mode. If the this mode is tossing something (projectile) out.
		
		 *   
		 * 
		 *   
		 *     @param SecLeadTarget 
			For Weapon, secondary firing mode. If this mode can lead the target. TODO
		
		 *   
		 * 
		 *   
		 *     @param SecInstantHit 
			For Weapon, secondary firing mode. If this mode does instant hits - weapon hits instantly - no flying time for bullets.
		
		 *   
		 * 
		 *   
		 *     @param SecFireOnRelease 
			For Weapon, secondary firing mode. If to fire this mode you need to press shooting button (start shooting) and then release it (stop shooting). Usually for charging weapons.
		
		 *   
		 * 
		 *   
		 *     @param SecWaitForRelease 
			For Weapon, secondary firing mode. If to fire this mode you need to stop pressing shooting button between two shots to shoot. You will fire once when pressing the button, then you need to press it again to fire again.
		
		 *   
		 * 
		 *   
		 *     @param SecModeExclusive 
			For Weapon, secondary firing mode. If this firing mode cannot be used at the same time with other firing mode of the weapon.
		
		 *   
		 * 
		 *   
		 *     @param SecFireRate 
			For Weapon, secondary firing mode. Fire rate in seconds.
		
		 *   
		 * 
		 *   
		 *     @param SecBotRefireRate 
			For Weapon, secondary firing mode. Refire rate for bots in seconds. TODO
		
		 *   
		 * 
		 *   
		 *     @param SecAmmoPerFire 
			For Weapon, secondary firing mode. Needed amount of ammo to fire this weapon mode once.
		
		 *   
		 * 
		 *   
		 *     @param SecAmmoClipSize 
			For Weapon, secondary firing mode. If the weapon mode has clips, their size. TODO
		
		 *   
		 * 
		 *   
		 *     @param SecAimError 
			For Weapon, secondary firing mode. Aiming error of the weapon. 0 none, 1000 quite a bit.
		
		 *   
		 * 
		 *   
		 *     @param SecSpread 
			For Weapon, secondary firing mode. Double, rotator units. No relation to aim error.
		
		 *   
		 * 
		 *   
		 *     @param SecSpreadStyle 
			For Weapon, secondary firing mode. Type of spreading. TODO
		
		 *   
		 * 
		 *   
		 *     @param SecFireCount 
			For Weapon, secondary firing mode. TODO
		
		 *   
		 * 
		 *   
		 *     @param SecDamageAtten 
			For Weapon, secondary firing mode. Attenuate instant-hit/projectile damage by this multiplier.
		
		 *   
		 * 
		 *   
		 *     @param SecAmmoType 
			For Ammo or for Weapon, secondary firing mode. Class of the ammo.
		
		 *   
		 * 
		 *   
		 *     @param SecInitialAmount 
			For Ammo or for Weapon, secondary firing mode. Amount of ammo we get if we pick up the item (weapon or ammo) for the first time.
		
		 *   
		 * 
		 *   
		 *     @param SecMaxAmount 
			For Ammo or for Weapon, secondary firing mode. Max amount of ammo of this type we can have in our inventory.
		
		 *   
		 * 
		 *   
		 *     @param SecMaxRange 
			For Ammo or for Weapon, secondary firing mode. TODO
		
		 *   
		 * 
		 *   
		 *     @param SecDamageType 
			For Ammo or for Weapon, secondary firing mode. Type of the damage. Maybe the same string for all damage.
		
		 *   
		 * 
		 *   
		 *     @param SecArmorStops 
			For Ammo or for Weapon, secondary firing mode. If the armor is effective against this damage type.
		
		 *   
		 * 
		 *   
		 *     @param SecAlwaysGibs 
			For Ammo or for Weapon, secondary firing mode. If this damage kills instantly.
		
		 *   
		 * 
		 *   
		 *     @param SecSpecial 
			For Ammo or for Weapon, secondary firing mode. If this damage is special. TODO
		
		 *   
		 * 
		 *   
		 *     @param SecDetonatesGoop 
			For Ammo or for Weapon, secondary firing mode. If this damage detonates goops. TODO
		
		 *   
		 * 
		 *   
		 *     @param SecSuperWeapon 
			For Ammo or for Weapon, secondary firing mode. If this damage is super weapon damage. Kills everyone even teammates.
		
		 *   
		 * 
		 *   
		 *     @param SecExtraMomZ 
			For Ammo or for Weapon, secondary firing mode. If this damage adds something to Pawns momentum. TODO
		
		 *   
		 * 
		 *   
		 *     @param SecProjType 
			For Ammo or for Weapon, secondary firing mode. Class of the projectile this ammo, weapon spawns. 
		
		 *   
		 * 
		 *   
		 *     @param SecDamage 
			For Ammo or for Weapon, secondary firing mode. How much damage this projectile does.
		
		 *   
		 * 
		 *   
		 *     @param SecDamageMax 
			For Ammo or for Weapon, secondary firing mode. How much maximum damage this projectile does.
		
		 *   
		 * 
		 *   
		 *     @param SecDamageMin 
			For Ammo or for Weapon, secondary firing mode. How much minimum damage this projectile does.
		
		 *   
		 * 
		 *   
		 *     @param SecSpeed 
			For Ammo or for Weapon, secondary firing mode. Default projectile speed.
		
		 *   
		 * 
		 *   
		 *     @param SecMaxSpeed 
			For Ammo or for Weapon, secondary firing mode. Maximum projectile speed.
		
		 *   
		 * 
		 *   
		 *     @param SecLifeSpan 
			For Ammo or for Weapon, secondary firing mode. Maximum amount of time in seconds this projectile can survive in the environment.
		
		 *   
		 * 
		 *   
		 *     @param SecDamageRadius 
			For Ammo or for Weapon, secondary firing mode. If the projectile does splash damage, here is radius in ut units of the splash.
		
		 *   
		 * 
		 *   
		 *     @param SecTossZ 
			For Ammo or for Weapon, secondary firing mode. If the projectile is tossed, here is velocity in Z direction of the toss. TODO
		
		 *   
		 * 
		 *   
		 *     @param SecMaxEffectDistance 
			For Ammo or for Weapon, secondary firing mode. Maximum effective distance. TODO
		
		 *   
		 * 
		 *   
		 *     @param Amount 
			If this item is some ammo or health pack, here we can find
			out how much of the attribute this item will add.
		
		 *   
		 * 
		 *   
		 *     @param SuperHeal 
			If this item is health. True if super health.
		
		 *   
		 * 
		 */
		public ItemCategory(
			String InventoryType,  ItemType PickupType,  Category ItemCategory,  boolean Melee,  boolean Sniping,  boolean UsesAltAmmo,  String PriFireModeType,  boolean PriSplashDamage,  boolean PriSplashJump,  boolean PriRecomSplashDamage,  boolean PriTossed,  boolean PriLeadTarget,  boolean PriInstantHit,  boolean PriFireOnRelease,  boolean PriWaitForRelease,  boolean PriModeExclusive,  double PriFireRate,  double PriBotRefireRate,  int PriAmmoPerFire,  int PriAmmoClipSize,  double PriAimError,  double PriSpread,  int PriSpreadStyle,  int PriFireCount,  double PriDamageAtten,  String PriAmmoType,  int PriInitialAmount,  int PriMaxAmount,  double PriMaxRange,  String PriDamageType,  boolean PriArmorStops,  boolean PriAlwaysGibs,  boolean PriSpecial,  boolean PriDetonatesGoop,  boolean PriSuperWeapon,  boolean PriExtraMomZ,  String PriProjType,  double PriDamage,  double PriDamageMax,  double PriDamageMin,  double PriSpeed,  double PriMaxSpeed,  double PriLifeSpan,  double PriDamageRadius,  double PriTossZ,  double PriMaxEffectDistance,  String SecFireModeType,  boolean SecSplashDamage,  boolean SecSplashJump,  boolean SecRecomSplashDamage,  boolean SecTossed,  boolean SecLeadTarget,  boolean SecInstantHit,  boolean SecFireOnRelease,  boolean SecWaitForRelease,  boolean SecModeExclusive,  double SecFireRate,  double SecBotRefireRate,  int SecAmmoPerFire,  int SecAmmoClipSize,  double SecAimError,  double SecSpread,  int SecSpreadStyle,  int SecFireCount,  double SecDamageAtten,  String SecAmmoType,  int SecInitialAmount,  int SecMaxAmount,  double SecMaxRange,  String SecDamageType,  boolean SecArmorStops,  boolean SecAlwaysGibs,  boolean SecSpecial,  boolean SecDetonatesGoop,  boolean SecSuperWeapon,  boolean SecExtraMomZ,  String SecProjType,  double SecDamage,  double SecDamageMax,  double SecDamageMin,  double SecSpeed,  double SecMaxSpeed,  double SecLifeSpan,  double SecDamageRadius,  double SecTossZ,  double SecMaxEffectDistance,  int Amount,  boolean SuperHeal
		) {
			
					this.InventoryType = InventoryType;
				
					this.PickupType = PickupType;
				
					this.ItemCategory = ItemCategory;
				
					this.Melee = Melee;
				
					this.Sniping = Sniping;
				
					this.UsesAltAmmo = UsesAltAmmo;
				
					this.PriFireModeType = PriFireModeType;
				
					this.PriSplashDamage = PriSplashDamage;
				
					this.PriSplashJump = PriSplashJump;
				
					this.PriRecomSplashDamage = PriRecomSplashDamage;
				
					this.PriTossed = PriTossed;
				
					this.PriLeadTarget = PriLeadTarget;
				
					this.PriInstantHit = PriInstantHit;
				
					this.PriFireOnRelease = PriFireOnRelease;
				
					this.PriWaitForRelease = PriWaitForRelease;
				
					this.PriModeExclusive = PriModeExclusive;
				
					this.PriFireRate = PriFireRate;
				
					this.PriBotRefireRate = PriBotRefireRate;
				
					this.PriAmmoPerFire = PriAmmoPerFire;
				
					this.PriAmmoClipSize = PriAmmoClipSize;
				
					this.PriAimError = PriAimError;
				
					this.PriSpread = PriSpread;
				
					this.PriSpreadStyle = PriSpreadStyle;
				
					this.PriFireCount = PriFireCount;
				
					this.PriDamageAtten = PriDamageAtten;
				
					this.PriAmmoType = PriAmmoType;
				
					this.PriInitialAmount = PriInitialAmount;
				
					this.PriMaxAmount = PriMaxAmount;
				
					this.PriMaxRange = PriMaxRange;
				
					this.PriDamageType = PriDamageType;
				
					this.PriArmorStops = PriArmorStops;
				
					this.PriAlwaysGibs = PriAlwaysGibs;
				
					this.PriSpecial = PriSpecial;
				
					this.PriDetonatesGoop = PriDetonatesGoop;
				
					this.PriSuperWeapon = PriSuperWeapon;
				
					this.PriExtraMomZ = PriExtraMomZ;
				
					this.PriProjType = PriProjType;
				
					this.PriDamage = PriDamage;
				
					this.PriDamageMax = PriDamageMax;
				
					this.PriDamageMin = PriDamageMin;
				
					this.PriSpeed = PriSpeed;
				
					this.PriMaxSpeed = PriMaxSpeed;
				
					this.PriLifeSpan = PriLifeSpan;
				
					this.PriDamageRadius = PriDamageRadius;
				
					this.PriTossZ = PriTossZ;
				
					this.PriMaxEffectDistance = PriMaxEffectDistance;
				
					this.SecFireModeType = SecFireModeType;
				
					this.SecSplashDamage = SecSplashDamage;
				
					this.SecSplashJump = SecSplashJump;
				
					this.SecRecomSplashDamage = SecRecomSplashDamage;
				
					this.SecTossed = SecTossed;
				
					this.SecLeadTarget = SecLeadTarget;
				
					this.SecInstantHit = SecInstantHit;
				
					this.SecFireOnRelease = SecFireOnRelease;
				
					this.SecWaitForRelease = SecWaitForRelease;
				
					this.SecModeExclusive = SecModeExclusive;
				
					this.SecFireRate = SecFireRate;
				
					this.SecBotRefireRate = SecBotRefireRate;
				
					this.SecAmmoPerFire = SecAmmoPerFire;
				
					this.SecAmmoClipSize = SecAmmoClipSize;
				
					this.SecAimError = SecAimError;
				
					this.SecSpread = SecSpread;
				
					this.SecSpreadStyle = SecSpreadStyle;
				
					this.SecFireCount = SecFireCount;
				
					this.SecDamageAtten = SecDamageAtten;
				
					this.SecAmmoType = SecAmmoType;
				
					this.SecInitialAmount = SecInitialAmount;
				
					this.SecMaxAmount = SecMaxAmount;
				
					this.SecMaxRange = SecMaxRange;
				
					this.SecDamageType = SecDamageType;
				
					this.SecArmorStops = SecArmorStops;
				
					this.SecAlwaysGibs = SecAlwaysGibs;
				
					this.SecSpecial = SecSpecial;
				
					this.SecDetonatesGoop = SecDetonatesGoop;
				
					this.SecSuperWeapon = SecSuperWeapon;
				
					this.SecExtraMomZ = SecExtraMomZ;
				
					this.SecProjType = SecProjType;
				
					this.SecDamage = SecDamage;
				
					this.SecDamageMax = SecDamageMax;
				
					this.SecDamageMin = SecDamageMin;
				
					this.SecSpeed = SecSpeed;
				
					this.SecMaxSpeed = SecMaxSpeed;
				
					this.SecLifeSpan = SecLifeSpan;
				
					this.SecDamageRadius = SecDamageRadius;
				
					this.SecTossZ = SecTossZ;
				
					this.SecMaxEffectDistance = SecMaxEffectDistance;
				
					this.Amount = Amount;
				
					this.SuperHeal = SuperHeal;
				
		}
    
    	/**
		 * Cloning constructor from the full message.
		 *
		 * @param original
		 */
		public ItemCategory(ItemCategory original) {		
			
					this.InventoryType = original.getInventoryType()
 	;
				
					this.PickupType = original.getPickupType()
 	;
				
					this.ItemCategory = original.getItemCategory()
 	;
				
					this.Melee = original.isMelee()
 	;
				
					this.Sniping = original.isSniping()
 	;
				
					this.UsesAltAmmo = original.isUsesAltAmmo()
 	;
				
					this.PriFireModeType = original.getPriFireModeType()
 	;
				
					this.PriSplashDamage = original.isPriSplashDamage()
 	;
				
					this.PriSplashJump = original.isPriSplashJump()
 	;
				
					this.PriRecomSplashDamage = original.isPriRecomSplashDamage()
 	;
				
					this.PriTossed = original.isPriTossed()
 	;
				
					this.PriLeadTarget = original.isPriLeadTarget()
 	;
				
					this.PriInstantHit = original.isPriInstantHit()
 	;
				
					this.PriFireOnRelease = original.isPriFireOnRelease()
 	;
				
					this.PriWaitForRelease = original.isPriWaitForRelease()
 	;
				
					this.PriModeExclusive = original.isPriModeExclusive()
 	;
				
					this.PriFireRate = original.getPriFireRate()
 	;
				
					this.PriBotRefireRate = original.getPriBotRefireRate()
 	;
				
					this.PriAmmoPerFire = original.getPriAmmoPerFire()
 	;
				
					this.PriAmmoClipSize = original.getPriAmmoClipSize()
 	;
				
					this.PriAimError = original.getPriAimError()
 	;
				
					this.PriSpread = original.getPriSpread()
 	;
				
					this.PriSpreadStyle = original.getPriSpreadStyle()
 	;
				
					this.PriFireCount = original.getPriFireCount()
 	;
				
					this.PriDamageAtten = original.getPriDamageAtten()
 	;
				
					this.PriAmmoType = original.getPriAmmoType()
 	;
				
					this.PriInitialAmount = original.getPriInitialAmount()
 	;
				
					this.PriMaxAmount = original.getPriMaxAmount()
 	;
				
					this.PriMaxRange = original.getPriMaxRange()
 	;
				
					this.PriDamageType = original.getPriDamageType()
 	;
				
					this.PriArmorStops = original.isPriArmorStops()
 	;
				
					this.PriAlwaysGibs = original.isPriAlwaysGibs()
 	;
				
					this.PriSpecial = original.isPriSpecial()
 	;
				
					this.PriDetonatesGoop = original.isPriDetonatesGoop()
 	;
				
					this.PriSuperWeapon = original.isPriSuperWeapon()
 	;
				
					this.PriExtraMomZ = original.isPriExtraMomZ()
 	;
				
					this.PriProjType = original.getPriProjType()
 	;
				
					this.PriDamage = original.getPriDamage()
 	;
				
					this.PriDamageMax = original.getPriDamageMax()
 	;
				
					this.PriDamageMin = original.getPriDamageMin()
 	;
				
					this.PriSpeed = original.getPriSpeed()
 	;
				
					this.PriMaxSpeed = original.getPriMaxSpeed()
 	;
				
					this.PriLifeSpan = original.getPriLifeSpan()
 	;
				
					this.PriDamageRadius = original.getPriDamageRadius()
 	;
				
					this.PriTossZ = original.getPriTossZ()
 	;
				
					this.PriMaxEffectDistance = original.getPriMaxEffectDistance()
 	;
				
					this.SecFireModeType = original.getSecFireModeType()
 	;
				
					this.SecSplashDamage = original.isSecSplashDamage()
 	;
				
					this.SecSplashJump = original.isSecSplashJump()
 	;
				
					this.SecRecomSplashDamage = original.isSecRecomSplashDamage()
 	;
				
					this.SecTossed = original.isSecTossed()
 	;
				
					this.SecLeadTarget = original.isSecLeadTarget()
 	;
				
					this.SecInstantHit = original.isSecInstantHit()
 	;
				
					this.SecFireOnRelease = original.isSecFireOnRelease()
 	;
				
					this.SecWaitForRelease = original.isSecWaitForRelease()
 	;
				
					this.SecModeExclusive = original.isSecModeExclusive()
 	;
				
					this.SecFireRate = original.getSecFireRate()
 	;
				
					this.SecBotRefireRate = original.getSecBotRefireRate()
 	;
				
					this.SecAmmoPerFire = original.getSecAmmoPerFire()
 	;
				
					this.SecAmmoClipSize = original.getSecAmmoClipSize()
 	;
				
					this.SecAimError = original.getSecAimError()
 	;
				
					this.SecSpread = original.getSecSpread()
 	;
				
					this.SecSpreadStyle = original.getSecSpreadStyle()
 	;
				
					this.SecFireCount = original.getSecFireCount()
 	;
				
					this.SecDamageAtten = original.getSecDamageAtten()
 	;
				
					this.SecAmmoType = original.getSecAmmoType()
 	;
				
					this.SecInitialAmount = original.getSecInitialAmount()
 	;
				
					this.SecMaxAmount = original.getSecMaxAmount()
 	;
				
					this.SecMaxRange = original.getSecMaxRange()
 	;
				
					this.SecDamageType = original.getSecDamageType()
 	;
				
					this.SecArmorStops = original.isSecArmorStops()
 	;
				
					this.SecAlwaysGibs = original.isSecAlwaysGibs()
 	;
				
					this.SecSpecial = original.isSecSpecial()
 	;
				
					this.SecDetonatesGoop = original.isSecDetonatesGoop()
 	;
				
					this.SecSuperWeapon = original.isSecSuperWeapon()
 	;
				
					this.SecExtraMomZ = original.isSecExtraMomZ()
 	;
				
					this.SecProjType = original.getSecProjType()
 	;
				
					this.SecDamage = original.getSecDamage()
 	;
				
					this.SecDamageMax = original.getSecDamageMax()
 	;
				
					this.SecDamageMin = original.getSecDamageMin()
 	;
				
					this.SecSpeed = original.getSecSpeed()
 	;
				
					this.SecMaxSpeed = original.getSecMaxSpeed()
 	;
				
					this.SecLifeSpan = original.getSecLifeSpan()
 	;
				
					this.SecDamageRadius = original.getSecDamageRadius()
 	;
				
					this.SecTossZ = original.getSecTossZ()
 	;
				
					this.SecMaxEffectDistance = original.getSecMaxEffectDistance()
 	;
				
					this.Amount = original.getAmount()
 	;
				
					this.SuperHeal = original.isSuperHeal()
 	;
				
			this.SimTime = original.getSimTime();			
		}
		
	   		
			protected long SimTime;
				
			/**
			 * Simulation time in MILLI SECONDS !!!
			 */	
			@Override
			public long getSimTime() {
				return SimTime;
			}
						
			/**
			 * Used by Yylex to slip correct time of the object or programmatically.
			 */
			protected void setSimTime(long SimTime) {
				this.SimTime = SimTime;
			}
	   	
    	
	    /**
         * 
			By this class the item is represented in inventory. This is inventory type class.
		 
         */
        protected
         String InventoryType =
       	null;
	
 		/**
         * 
			By this class the item is represented in inventory. This is inventory type class.
		 
         */
        public  String getInventoryType()
 	 {
    					return InventoryType;
    				}
    			
    	
	    /**
         * 
			By this class the item is represented in the map. This is pickup type class.
		 
         */
        protected
         ItemType PickupType =
       	null;
	
 		/**
         * 
			By this class the item is represented in the map. This is pickup type class.
		 
         */
        public  ItemType getPickupType()
 	 {
    					return PickupType;
    				}
    			
    	
	    /**
         * 
			Category of the item. Can be "Weapon", "Adrenaline", "Ammo", "Armor", "Shield", "Health" or "Other".
		 
         */
        protected
         Category ItemCategory =
       	null;
	
 		/**
         * 
			Category of the item. Can be "Weapon", "Adrenaline", "Ammo", "Armor", "Shield", "Health" or "Other".
		 
         */
        public  Category getItemCategory()
 	 {
    					return ItemCategory;
    				}
    			
    	
	    /**
         * 
		For Weapon. True if the weapon is melee weapon (close range).
		 
         */
        protected
         boolean Melee =
       	false;
	
 		/**
         * 
		For Weapon. True if the weapon is melee weapon (close range).
		 
         */
        public  boolean isMelee()
 	 {
    					return Melee;
    				}
    			
    	
	    /**
         * 
		For Weapon. True if the weapon is sniping weapon (long range).
		 
         */
        protected
         boolean Sniping =
       	false;
	
 		/**
         * 
		For Weapon. True if the weapon is sniping weapon (long range).
		 
         */
        public  boolean isSniping()
 	 {
    					return Sniping;
    				}
    			
    	
	    /**
         * 
		For Weapon. True if the weapon uses two separate ammos for primary and secondary firing mode.
		 
         */
        protected
         boolean UsesAltAmmo =
       	false;
	
 		/**
         * 
		For Weapon. True if the weapon uses two separate ammos for primary and secondary firing mode.
		 
         */
        public  boolean isUsesAltAmmo()
 	 {
    					return UsesAltAmmo;
    				}
    			
    	
	    /**
         * 
			For Weapon, primary firing mode. Type of the firing mode. If none, the weapon does not have this fireing mode.
		 
         */
        protected
         String PriFireModeType =
       	null;
	
 		/**
         * 
			For Weapon, primary firing mode. Type of the firing mode. If none, the weapon does not have this fireing mode.
		 
         */
        public  String getPriFireModeType()
 	 {
    					return PriFireModeType;
    				}
    			
    	
	    /**
         * 
			For Weapon, primary firing mode. If this mode does splash damage.
		 
         */
        protected
         boolean PriSplashDamage =
       	false;
	
 		/**
         * 
			For Weapon, primary firing mode. If this mode does splash damage.
		 
         */
        public  boolean isPriSplashDamage()
 	 {
    					return PriSplashDamage;
    				}
    			
    	
	    /**
         * 
			For Weapon, primary firing mode. If the splash damage of this firing mode can be used for increasing jump height.
		 
         */
        protected
         boolean PriSplashJump =
       	false;
	
 		/**
         * 
			For Weapon, primary firing mode. If the splash damage of this firing mode can be used for increasing jump height.
		 
         */
        public  boolean isPriSplashJump()
 	 {
    					return PriSplashJump;
    				}
    			
    	
	    /**
         * 
			For Weapon, primary firing mode. If the engine recommends to use this splash damage. TODO
		 
         */
        protected
         boolean PriRecomSplashDamage =
       	false;
	
 		/**
         * 
			For Weapon, primary firing mode. If the engine recommends to use this splash damage. TODO
		 
         */
        public  boolean isPriRecomSplashDamage()
 	 {
    					return PriRecomSplashDamage;
    				}
    			
    	
	    /**
         * 
			For Weapon, primary firing mode. If the this mode is tossing something (projectile) out.
		 
         */
        protected
         boolean PriTossed =
       	false;
	
 		/**
         * 
			For Weapon, primary firing mode. If the this mode is tossing something (projectile) out.
		 
         */
        public  boolean isPriTossed()
 	 {
    					return PriTossed;
    				}
    			
    	
	    /**
         * 
			For Weapon, primary firing mode. If this mode can lead the target. TODO
		 
         */
        protected
         boolean PriLeadTarget =
       	false;
	
 		/**
         * 
			For Weapon, primary firing mode. If this mode can lead the target. TODO
		 
         */
        public  boolean isPriLeadTarget()
 	 {
    					return PriLeadTarget;
    				}
    			
    	
	    /**
         * 
			For Weapon, primary firing mode. If this mode does instant hits - weapon hits instantly - no flying time for bullets.
		 
         */
        protected
         boolean PriInstantHit =
       	false;
	
 		/**
         * 
			For Weapon, primary firing mode. If this mode does instant hits - weapon hits instantly - no flying time for bullets.
		 
         */
        public  boolean isPriInstantHit()
 	 {
    					return PriInstantHit;
    				}
    			
    	
	    /**
         * 
			For Weapon, primary firing mode. If to fire this mode you need to press shooting button (start shooting) and then release it (stop shooting). Usually for charging weapons.
		 
         */
        protected
         boolean PriFireOnRelease =
       	false;
	
 		/**
         * 
			For Weapon, primary firing mode. If to fire this mode you need to press shooting button (start shooting) and then release it (stop shooting). Usually for charging weapons.
		 
         */
        public  boolean isPriFireOnRelease()
 	 {
    					return PriFireOnRelease;
    				}
    			
    	
	    /**
         * 
			For Weapon, primary firing mode. If to fire this mode you need to stop pressing shooting button between two shots to shoot. You will fire once when pressing the button, then you need to press it again to fire again.
		 
         */
        protected
         boolean PriWaitForRelease =
       	false;
	
 		/**
         * 
			For Weapon, primary firing mode. If to fire this mode you need to stop pressing shooting button between two shots to shoot. You will fire once when pressing the button, then you need to press it again to fire again.
		 
         */
        public  boolean isPriWaitForRelease()
 	 {
    					return PriWaitForRelease;
    				}
    			
    	
	    /**
         * 
			For Weapon, primary firing mode. If this firing mode cannot be used at the same time with other firing mode of the weapon.
		 
         */
        protected
         boolean PriModeExclusive =
       	false;
	
 		/**
         * 
			For Weapon, primary firing mode. If this firing mode cannot be used at the same time with other firing mode of the weapon.
		 
         */
        public  boolean isPriModeExclusive()
 	 {
    					return PriModeExclusive;
    				}
    			
    	
	    /**
         * 
			For Weapon, primary firing mode. Fire rate in seconds. How fast the weapon fires if we are firing continuosly.
		 
         */
        protected
         double PriFireRate =
       	0;
	
 		/**
         * 
			For Weapon, primary firing mode. Fire rate in seconds. How fast the weapon fires if we are firing continuosly.
		 
         */
        public  double getPriFireRate()
 	 {
    					return PriFireRate;
    				}
    			
    	
	    /**
         * 
			For Weapon, primary firing mode. Refire rate for bots in seconds. When we stop firing how long does it take to resume firing again.
		 
         */
        protected
         double PriBotRefireRate =
       	0;
	
 		/**
         * 
			For Weapon, primary firing mode. Refire rate for bots in seconds. When we stop firing how long does it take to resume firing again.
		 
         */
        public  double getPriBotRefireRate()
 	 {
    					return PriBotRefireRate;
    				}
    			
    	
	    /**
         * 
			For Weapon, primary firing mode. Needed amount of ammo to fire this weapon mode once.
		 
         */
        protected
         int PriAmmoPerFire =
       	0;
	
 		/**
         * 
			For Weapon, primary firing mode. Needed amount of ammo to fire this weapon mode once.
		 
         */
        public  int getPriAmmoPerFire()
 	 {
    					return PriAmmoPerFire;
    				}
    			
    	
	    /**
         * 
			For Weapon, primary firing mode. If the weapon mode has clips, their size. TODO
		 
         */
        protected
         int PriAmmoClipSize =
       	0;
	
 		/**
         * 
			For Weapon, primary firing mode. If the weapon mode has clips, their size. TODO
		 
         */
        public  int getPriAmmoClipSize()
 	 {
    					return PriAmmoClipSize;
    				}
    			
    	
	    /**
         * 
			For Weapon, primary firing mode. Aiming error of the weapon. 0 none, 1000 quite a bit.
		 
         */
        protected
         double PriAimError =
       	0;
	
 		/**
         * 
			For Weapon, primary firing mode. Aiming error of the weapon. 0 none, 1000 quite a bit.
		 
         */
        public  double getPriAimError()
 	 {
    					return PriAimError;
    				}
    			
    	
	    /**
         * 
			For Weapon, primary firing mode. Double, rotator units. No relation to aim error.
		 
         */
        protected
         double PriSpread =
       	0;
	
 		/**
         * 
			For Weapon, primary firing mode. Double, rotator units. No relation to aim error.
		 
         */
        public  double getPriSpread()
 	 {
    					return PriSpread;
    				}
    			
    	
	    /**
         * 
			For Weapon, primary firing mode. Type of spreading. TODO
		 
         */
        protected
         int PriSpreadStyle =
       	0;
	
 		/**
         * 
			For Weapon, primary firing mode. Type of spreading. TODO
		 
         */
        public  int getPriSpreadStyle()
 	 {
    					return PriSpreadStyle;
    				}
    			
    	
	    /**
         * 
			For Weapon, primary firing mode. TODO
		 
         */
        protected
         int PriFireCount =
       	0;
	
 		/**
         * 
			For Weapon, primary firing mode. TODO
		 
         */
        public  int getPriFireCount()
 	 {
    					return PriFireCount;
    				}
    			
    	
	    /**
         * 
			For Weapon, primary firing mode. Attenuate instant-hit/projectile damage by this multiplier.
		 
         */
        protected
         double PriDamageAtten =
       	0;
	
 		/**
         * 
			For Weapon, primary firing mode. Attenuate instant-hit/projectile damage by this multiplier.
		 
         */
        public  double getPriDamageAtten()
 	 {
    					return PriDamageAtten;
    				}
    			
    	
	    /**
         * 
			For Ammo or for Weapon, primary firing mode. Class of the ammo.
		 
         */
        protected
         String PriAmmoType =
       	null;
	
 		/**
         * 
			For Ammo or for Weapon, primary firing mode. Class of the ammo.
		 
         */
        public  String getPriAmmoType()
 	 {
    					return PriAmmoType;
    				}
    			
    	
	    /**
         * 
			For Ammo or for Weapon, primary firing mode. Amount of ammo we get if we pick up the item (weapon or ammo) for the first time.
		 
         */
        protected
         int PriInitialAmount =
       	0;
	
 		/**
         * 
			For Ammo or for Weapon, primary firing mode. Amount of ammo we get if we pick up the item (weapon or ammo) for the first time.
		 
         */
        public  int getPriInitialAmount()
 	 {
    					return PriInitialAmount;
    				}
    			
    	
	    /**
         * 
			For Ammo or for Weapon, primary firing mode. Max amount of ammo of this type we can have in our inventory.
		 
         */
        protected
         int PriMaxAmount =
       	0;
	
 		/**
         * 
			For Ammo or for Weapon, primary firing mode. Max amount of ammo of this type we can have in our inventory.
		 
         */
        public  int getPriMaxAmount()
 	 {
    					return PriMaxAmount;
    				}
    			
    	
	    /**
         * 
			For Ammo or for Weapon, primary firing mode. TODO
		 
         */
        protected
         double PriMaxRange =
       	0;
	
 		/**
         * 
			For Ammo or for Weapon, primary firing mode. TODO
		 
         */
        public  double getPriMaxRange()
 	 {
    					return PriMaxRange;
    				}
    			
    	
	    /**
         * 
			For Ammo or for Weapon, primary firing mode. Type of the damage. Maybe the same string for all damage.
		 
         */
        protected
         String PriDamageType =
       	null;
	
 		/**
         * 
			For Ammo or for Weapon, primary firing mode. Type of the damage. Maybe the same string for all damage.
		 
         */
        public  String getPriDamageType()
 	 {
    					return PriDamageType;
    				}
    			
    	
	    /**
         * 
			For Ammo or for Weapon, primary firing mode. If the armor is effective against this damage type.
		 
         */
        protected
         boolean PriArmorStops =
       	false;
	
 		/**
         * 
			For Ammo or for Weapon, primary firing mode. If the armor is effective against this damage type.
		 
         */
        public  boolean isPriArmorStops()
 	 {
    					return PriArmorStops;
    				}
    			
    	
	    /**
         * 
			For Ammo or for Weapon, primary firing mode. If this damage kills instantly.
		 
         */
        protected
         boolean PriAlwaysGibs =
       	false;
	
 		/**
         * 
			For Ammo or for Weapon, primary firing mode. If this damage kills instantly.
		 
         */
        public  boolean isPriAlwaysGibs()
 	 {
    					return PriAlwaysGibs;
    				}
    			
    	
	    /**
         * 
			For Ammo or for Weapon, primary firing mode. If this damage is special. TODO
		 
         */
        protected
         boolean PriSpecial =
       	false;
	
 		/**
         * 
			For Ammo or for Weapon, primary firing mode. If this damage is special. TODO
		 
         */
        public  boolean isPriSpecial()
 	 {
    					return PriSpecial;
    				}
    			
    	
	    /**
         * 
			For Ammo or for Weapon, primary firing mode. If this damage detonates goops. TODO
		 
         */
        protected
         boolean PriDetonatesGoop =
       	false;
	
 		/**
         * 
			For Ammo or for Weapon, primary firing mode. If this damage detonates goops. TODO
		 
         */
        public  boolean isPriDetonatesGoop()
 	 {
    					return PriDetonatesGoop;
    				}
    			
    	
	    /**
         * 
			For Ammo or for Weapon, primary firing mode. If this damage is super weapon damage. Kills everyone even teammates.
		 
         */
        protected
         boolean PriSuperWeapon =
       	false;
	
 		/**
         * 
			For Ammo or for Weapon, primary firing mode. If this damage is super weapon damage. Kills everyone even teammates.
		 
         */
        public  boolean isPriSuperWeapon()
 	 {
    					return PriSuperWeapon;
    				}
    			
    	
	    /**
         * 
			For Ammo or for Weapon, primary firing mode. If this damage adds something to Panws momentum. TODO
		 
         */
        protected
         boolean PriExtraMomZ =
       	false;
	
 		/**
         * 
			For Ammo or for Weapon, primary firing mode. If this damage adds something to Panws momentum. TODO
		 
         */
        public  boolean isPriExtraMomZ()
 	 {
    					return PriExtraMomZ;
    				}
    			
    	
	    /**
         * 
			For Ammo or for Weapon, primary firing mode. Class of the projectile this ammo, weapon spawns. 
		 
         */
        protected
         String PriProjType =
       	null;
	
 		/**
         * 
			For Ammo or for Weapon, primary firing mode. Class of the projectile this ammo, weapon spawns. 
		 
         */
        public  String getPriProjType()
 	 {
    					return PriProjType;
    				}
    			
    	
	    /**
         * 
			For Ammo or for Weapon, primary firing mode. How much damage this projectile does.
		 
         */
        protected
         double PriDamage =
       	0;
	
 		/**
         * 
			For Ammo or for Weapon, primary firing mode. How much damage this projectile does.
		 
         */
        public  double getPriDamage()
 	 {
    					return PriDamage;
    				}
    			
    	
	    /**
         * 
			For Ammo or for Weapon, primary firing mode. How much maximum damage this projectile does.
		 
         */
        protected
         double PriDamageMax =
       	0;
	
 		/**
         * 
			For Ammo or for Weapon, primary firing mode. How much maximum damage this projectile does.
		 
         */
        public  double getPriDamageMax()
 	 {
    					return PriDamageMax;
    				}
    			
    	
	    /**
         * 
			For Ammo or for Weapon, primary firing mode. How much minimum damage this projectile does.
		 
         */
        protected
         double PriDamageMin =
       	0;
	
 		/**
         * 
			For Ammo or for Weapon, primary firing mode. How much minimum damage this projectile does.
		 
         */
        public  double getPriDamageMin()
 	 {
    					return PriDamageMin;
    				}
    			
    	
	    /**
         * 
			For Ammo or for Weapon, primary firing mode. Default projectile speed.
		 
         */
        protected
         double PriSpeed =
       	0;
	
 		/**
         * 
			For Ammo or for Weapon, primary firing mode. Default projectile speed.
		 
         */
        public  double getPriSpeed()
 	 {
    					return PriSpeed;
    				}
    			
    	
	    /**
         * 
			For Ammo or for Weapon, primary firing mode. Maximum projectile speed.
		 
         */
        protected
         double PriMaxSpeed =
       	0;
	
 		/**
         * 
			For Ammo or for Weapon, primary firing mode. Maximum projectile speed.
		 
         */
        public  double getPriMaxSpeed()
 	 {
    					return PriMaxSpeed;
    				}
    			
    	
	    /**
         * 
			For Ammo or for Weapon, primary firing mode. Maximum amount of time in seconds this projectile can survive in the environment.
		 
         */
        protected
         double PriLifeSpan =
       	0;
	
 		/**
         * 
			For Ammo or for Weapon, primary firing mode. Maximum amount of time in seconds this projectile can survive in the environment.
		 
         */
        public  double getPriLifeSpan()
 	 {
    					return PriLifeSpan;
    				}
    			
    	
	    /**
         * 
			For Ammo or for Weapon, primary firing mode. If the projectile does splash damage, here is radius in ut units of the splash.
		 
         */
        protected
         double PriDamageRadius =
       	0;
	
 		/**
         * 
			For Ammo or for Weapon, primary firing mode. If the projectile does splash damage, here is radius in ut units of the splash.
		 
         */
        public  double getPriDamageRadius()
 	 {
    					return PriDamageRadius;
    				}
    			
    	
	    /**
         * 
			For Ammo or for Weapon, primary firing mode. If the projectile is tossed, here is velocity in Z direction of the toss. TODO
		 
         */
        protected
         double PriTossZ =
       	0;
	
 		/**
         * 
			For Ammo or for Weapon, primary firing mode. If the projectile is tossed, here is velocity in Z direction of the toss. TODO
		 
         */
        public  double getPriTossZ()
 	 {
    					return PriTossZ;
    				}
    			
    	
	    /**
         * 
			For Ammo or for Weapon, primary firing mode. Maximum effective distance. TODO
		 
         */
        protected
         double PriMaxEffectDistance =
       	0;
	
 		/**
         * 
			For Ammo or for Weapon, primary firing mode. Maximum effective distance. TODO
		 
         */
        public  double getPriMaxEffectDistance()
 	 {
    					return PriMaxEffectDistance;
    				}
    			
    	
	    /**
         * 
			For Weapon, secondary firing mode. Type of the firing mode. If none, the weapon does not have this fireing mode.
		 
         */
        protected
         String SecFireModeType =
       	null;
	
 		/**
         * 
			For Weapon, secondary firing mode. Type of the firing mode. If none, the weapon does not have this fireing mode.
		 
         */
        public  String getSecFireModeType()
 	 {
    					return SecFireModeType;
    				}
    			
    	
	    /**
         * 
			For Weapon, secondary firing mode. If this mode does splash damage.
		 
         */
        protected
         boolean SecSplashDamage =
       	false;
	
 		/**
         * 
			For Weapon, secondary firing mode. If this mode does splash damage.
		 
         */
        public  boolean isSecSplashDamage()
 	 {
    					return SecSplashDamage;
    				}
    			
    	
	    /**
         * 
			For Weapon, secondary firing mode. If the splash damage of this firing mode can be used for increasing jump height.
		 
         */
        protected
         boolean SecSplashJump =
       	false;
	
 		/**
         * 
			For Weapon, secondary firing mode. If the splash damage of this firing mode can be used for increasing jump height.
		 
         */
        public  boolean isSecSplashJump()
 	 {
    					return SecSplashJump;
    				}
    			
    	
	    /**
         * 
			For Weapon, secondary firing mode. If the engine recommends to use this splash damage. TODO
		 
         */
        protected
         boolean SecRecomSplashDamage =
       	false;
	
 		/**
         * 
			For Weapon, secondary firing mode. If the engine recommends to use this splash damage. TODO
		 
         */
        public  boolean isSecRecomSplashDamage()
 	 {
    					return SecRecomSplashDamage;
    				}
    			
    	
	    /**
         * 
			For Weapon, secondary firing mode. If the this mode is tossing something (projectile) out.
		 
         */
        protected
         boolean SecTossed =
       	false;
	
 		/**
         * 
			For Weapon, secondary firing mode. If the this mode is tossing something (projectile) out.
		 
         */
        public  boolean isSecTossed()
 	 {
    					return SecTossed;
    				}
    			
    	
	    /**
         * 
			For Weapon, secondary firing mode. If this mode can lead the target. TODO
		 
         */
        protected
         boolean SecLeadTarget =
       	false;
	
 		/**
         * 
			For Weapon, secondary firing mode. If this mode can lead the target. TODO
		 
         */
        public  boolean isSecLeadTarget()
 	 {
    					return SecLeadTarget;
    				}
    			
    	
	    /**
         * 
			For Weapon, secondary firing mode. If this mode does instant hits - weapon hits instantly - no flying time for bullets.
		 
         */
        protected
         boolean SecInstantHit =
       	false;
	
 		/**
         * 
			For Weapon, secondary firing mode. If this mode does instant hits - weapon hits instantly - no flying time for bullets.
		 
         */
        public  boolean isSecInstantHit()
 	 {
    					return SecInstantHit;
    				}
    			
    	
	    /**
         * 
			For Weapon, secondary firing mode. If to fire this mode you need to press shooting button (start shooting) and then release it (stop shooting). Usually for charging weapons.
		 
         */
        protected
         boolean SecFireOnRelease =
       	false;
	
 		/**
         * 
			For Weapon, secondary firing mode. If to fire this mode you need to press shooting button (start shooting) and then release it (stop shooting). Usually for charging weapons.
		 
         */
        public  boolean isSecFireOnRelease()
 	 {
    					return SecFireOnRelease;
    				}
    			
    	
	    /**
         * 
			For Weapon, secondary firing mode. If to fire this mode you need to stop pressing shooting button between two shots to shoot. You will fire once when pressing the button, then you need to press it again to fire again.
		 
         */
        protected
         boolean SecWaitForRelease =
       	false;
	
 		/**
         * 
			For Weapon, secondary firing mode. If to fire this mode you need to stop pressing shooting button between two shots to shoot. You will fire once when pressing the button, then you need to press it again to fire again.
		 
         */
        public  boolean isSecWaitForRelease()
 	 {
    					return SecWaitForRelease;
    				}
    			
    	
	    /**
         * 
			For Weapon, secondary firing mode. If this firing mode cannot be used at the same time with other firing mode of the weapon.
		 
         */
        protected
         boolean SecModeExclusive =
       	false;
	
 		/**
         * 
			For Weapon, secondary firing mode. If this firing mode cannot be used at the same time with other firing mode of the weapon.
		 
         */
        public  boolean isSecModeExclusive()
 	 {
    					return SecModeExclusive;
    				}
    			
    	
	    /**
         * 
			For Weapon, secondary firing mode. Fire rate in seconds.
		 
         */
        protected
         double SecFireRate =
       	0;
	
 		/**
         * 
			For Weapon, secondary firing mode. Fire rate in seconds.
		 
         */
        public  double getSecFireRate()
 	 {
    					return SecFireRate;
    				}
    			
    	
	    /**
         * 
			For Weapon, secondary firing mode. Refire rate for bots in seconds. TODO
		 
         */
        protected
         double SecBotRefireRate =
       	0;
	
 		/**
         * 
			For Weapon, secondary firing mode. Refire rate for bots in seconds. TODO
		 
         */
        public  double getSecBotRefireRate()
 	 {
    					return SecBotRefireRate;
    				}
    			
    	
	    /**
         * 
			For Weapon, secondary firing mode. Needed amount of ammo to fire this weapon mode once.
		 
         */
        protected
         int SecAmmoPerFire =
       	0;
	
 		/**
         * 
			For Weapon, secondary firing mode. Needed amount of ammo to fire this weapon mode once.
		 
         */
        public  int getSecAmmoPerFire()
 	 {
    					return SecAmmoPerFire;
    				}
    			
    	
	    /**
         * 
			For Weapon, secondary firing mode. If the weapon mode has clips, their size. TODO
		 
         */
        protected
         int SecAmmoClipSize =
       	0;
	
 		/**
         * 
			For Weapon, secondary firing mode. If the weapon mode has clips, their size. TODO
		 
         */
        public  int getSecAmmoClipSize()
 	 {
    					return SecAmmoClipSize;
    				}
    			
    	
	    /**
         * 
			For Weapon, secondary firing mode. Aiming error of the weapon. 0 none, 1000 quite a bit.
		 
         */
        protected
         double SecAimError =
       	0;
	
 		/**
         * 
			For Weapon, secondary firing mode. Aiming error of the weapon. 0 none, 1000 quite a bit.
		 
         */
        public  double getSecAimError()
 	 {
    					return SecAimError;
    				}
    			
    	
	    /**
         * 
			For Weapon, secondary firing mode. Double, rotator units. No relation to aim error.
		 
         */
        protected
         double SecSpread =
       	0;
	
 		/**
         * 
			For Weapon, secondary firing mode. Double, rotator units. No relation to aim error.
		 
         */
        public  double getSecSpread()
 	 {
    					return SecSpread;
    				}
    			
    	
	    /**
         * 
			For Weapon, secondary firing mode. Type of spreading. TODO
		 
         */
        protected
         int SecSpreadStyle =
       	0;
	
 		/**
         * 
			For Weapon, secondary firing mode. Type of spreading. TODO
		 
         */
        public  int getSecSpreadStyle()
 	 {
    					return SecSpreadStyle;
    				}
    			
    	
	    /**
         * 
			For Weapon, secondary firing mode. TODO
		 
         */
        protected
         int SecFireCount =
       	0;
	
 		/**
         * 
			For Weapon, secondary firing mode. TODO
		 
         */
        public  int getSecFireCount()
 	 {
    					return SecFireCount;
    				}
    			
    	
	    /**
         * 
			For Weapon, secondary firing mode. Attenuate instant-hit/projectile damage by this multiplier.
		 
         */
        protected
         double SecDamageAtten =
       	0;
	
 		/**
         * 
			For Weapon, secondary firing mode. Attenuate instant-hit/projectile damage by this multiplier.
		 
         */
        public  double getSecDamageAtten()
 	 {
    					return SecDamageAtten;
    				}
    			
    	
	    /**
         * 
			For Ammo or for Weapon, secondary firing mode. Class of the ammo.
		 
         */
        protected
         String SecAmmoType =
       	null;
	
 		/**
         * 
			For Ammo or for Weapon, secondary firing mode. Class of the ammo.
		 
         */
        public  String getSecAmmoType()
 	 {
    					return SecAmmoType;
    				}
    			
    	
	    /**
         * 
			For Ammo or for Weapon, secondary firing mode. Amount of ammo we get if we pick up the item (weapon or ammo) for the first time.
		 
         */
        protected
         int SecInitialAmount =
       	0;
	
 		/**
         * 
			For Ammo or for Weapon, secondary firing mode. Amount of ammo we get if we pick up the item (weapon or ammo) for the first time.
		 
         */
        public  int getSecInitialAmount()
 	 {
    					return SecInitialAmount;
    				}
    			
    	
	    /**
         * 
			For Ammo or for Weapon, secondary firing mode. Max amount of ammo of this type we can have in our inventory.
		 
         */
        protected
         int SecMaxAmount =
       	0;
	
 		/**
         * 
			For Ammo or for Weapon, secondary firing mode. Max amount of ammo of this type we can have in our inventory.
		 
         */
        public  int getSecMaxAmount()
 	 {
    					return SecMaxAmount;
    				}
    			
    	
	    /**
         * 
			For Ammo or for Weapon, secondary firing mode. TODO
		 
         */
        protected
         double SecMaxRange =
       	0;
	
 		/**
         * 
			For Ammo or for Weapon, secondary firing mode. TODO
		 
         */
        public  double getSecMaxRange()
 	 {
    					return SecMaxRange;
    				}
    			
    	
	    /**
         * 
			For Ammo or for Weapon, secondary firing mode. Type of the damage. Maybe the same string for all damage.
		 
         */
        protected
         String SecDamageType =
       	null;
	
 		/**
         * 
			For Ammo or for Weapon, secondary firing mode. Type of the damage. Maybe the same string for all damage.
		 
         */
        public  String getSecDamageType()
 	 {
    					return SecDamageType;
    				}
    			
    	
	    /**
         * 
			For Ammo or for Weapon, secondary firing mode. If the armor is effective against this damage type.
		 
         */
        protected
         boolean SecArmorStops =
       	false;
	
 		/**
         * 
			For Ammo or for Weapon, secondary firing mode. If the armor is effective against this damage type.
		 
         */
        public  boolean isSecArmorStops()
 	 {
    					return SecArmorStops;
    				}
    			
    	
	    /**
         * 
			For Ammo or for Weapon, secondary firing mode. If this damage kills instantly.
		 
         */
        protected
         boolean SecAlwaysGibs =
       	false;
	
 		/**
         * 
			For Ammo or for Weapon, secondary firing mode. If this damage kills instantly.
		 
         */
        public  boolean isSecAlwaysGibs()
 	 {
    					return SecAlwaysGibs;
    				}
    			
    	
	    /**
         * 
			For Ammo or for Weapon, secondary firing mode. If this damage is special. TODO
		 
         */
        protected
         boolean SecSpecial =
       	false;
	
 		/**
         * 
			For Ammo or for Weapon, secondary firing mode. If this damage is special. TODO
		 
         */
        public  boolean isSecSpecial()
 	 {
    					return SecSpecial;
    				}
    			
    	
	    /**
         * 
			For Ammo or for Weapon, secondary firing mode. If this damage detonates goops. TODO
		 
         */
        protected
         boolean SecDetonatesGoop =
       	false;
	
 		/**
         * 
			For Ammo or for Weapon, secondary firing mode. If this damage detonates goops. TODO
		 
         */
        public  boolean isSecDetonatesGoop()
 	 {
    					return SecDetonatesGoop;
    				}
    			
    	
	    /**
         * 
			For Ammo or for Weapon, secondary firing mode. If this damage is super weapon damage. Kills everyone even teammates.
		 
         */
        protected
         boolean SecSuperWeapon =
       	false;
	
 		/**
         * 
			For Ammo or for Weapon, secondary firing mode. If this damage is super weapon damage. Kills everyone even teammates.
		 
         */
        public  boolean isSecSuperWeapon()
 	 {
    					return SecSuperWeapon;
    				}
    			
    	
	    /**
         * 
			For Ammo or for Weapon, secondary firing mode. If this damage adds something to Pawns momentum. TODO
		 
         */
        protected
         boolean SecExtraMomZ =
       	false;
	
 		/**
         * 
			For Ammo or for Weapon, secondary firing mode. If this damage adds something to Pawns momentum. TODO
		 
         */
        public  boolean isSecExtraMomZ()
 	 {
    					return SecExtraMomZ;
    				}
    			
    	
	    /**
         * 
			For Ammo or for Weapon, secondary firing mode. Class of the projectile this ammo, weapon spawns. 
		 
         */
        protected
         String SecProjType =
       	null;
	
 		/**
         * 
			For Ammo or for Weapon, secondary firing mode. Class of the projectile this ammo, weapon spawns. 
		 
         */
        public  String getSecProjType()
 	 {
    					return SecProjType;
    				}
    			
    	
	    /**
         * 
			For Ammo or for Weapon, secondary firing mode. How much damage this projectile does.
		 
         */
        protected
         double SecDamage =
       	0;
	
 		/**
         * 
			For Ammo or for Weapon, secondary firing mode. How much damage this projectile does.
		 
         */
        public  double getSecDamage()
 	 {
    					return SecDamage;
    				}
    			
    	
	    /**
         * 
			For Ammo or for Weapon, secondary firing mode. How much maximum damage this projectile does.
		 
         */
        protected
         double SecDamageMax =
       	0;
	
 		/**
         * 
			For Ammo or for Weapon, secondary firing mode. How much maximum damage this projectile does.
		 
         */
        public  double getSecDamageMax()
 	 {
    					return SecDamageMax;
    				}
    			
    	
	    /**
         * 
			For Ammo or for Weapon, secondary firing mode. How much minimum damage this projectile does.
		 
         */
        protected
         double SecDamageMin =
       	0;
	
 		/**
         * 
			For Ammo or for Weapon, secondary firing mode. How much minimum damage this projectile does.
		 
         */
        public  double getSecDamageMin()
 	 {
    					return SecDamageMin;
    				}
    			
    	
	    /**
         * 
			For Ammo or for Weapon, secondary firing mode. Default projectile speed.
		 
         */
        protected
         double SecSpeed =
       	0;
	
 		/**
         * 
			For Ammo or for Weapon, secondary firing mode. Default projectile speed.
		 
         */
        public  double getSecSpeed()
 	 {
    					return SecSpeed;
    				}
    			
    	
	    /**
         * 
			For Ammo or for Weapon, secondary firing mode. Maximum projectile speed.
		 
         */
        protected
         double SecMaxSpeed =
       	0;
	
 		/**
         * 
			For Ammo or for Weapon, secondary firing mode. Maximum projectile speed.
		 
         */
        public  double getSecMaxSpeed()
 	 {
    					return SecMaxSpeed;
    				}
    			
    	
	    /**
         * 
			For Ammo or for Weapon, secondary firing mode. Maximum amount of time in seconds this projectile can survive in the environment.
		 
         */
        protected
         double SecLifeSpan =
       	0;
	
 		/**
         * 
			For Ammo or for Weapon, secondary firing mode. Maximum amount of time in seconds this projectile can survive in the environment.
		 
         */
        public  double getSecLifeSpan()
 	 {
    					return SecLifeSpan;
    				}
    			
    	
	    /**
         * 
			For Ammo or for Weapon, secondary firing mode. If the projectile does splash damage, here is radius in ut units of the splash.
		 
         */
        protected
         double SecDamageRadius =
       	0;
	
 		/**
         * 
			For Ammo or for Weapon, secondary firing mode. If the projectile does splash damage, here is radius in ut units of the splash.
		 
         */
        public  double getSecDamageRadius()
 	 {
    					return SecDamageRadius;
    				}
    			
    	
	    /**
         * 
			For Ammo or for Weapon, secondary firing mode. If the projectile is tossed, here is velocity in Z direction of the toss. TODO
		 
         */
        protected
         double SecTossZ =
       	0;
	
 		/**
         * 
			For Ammo or for Weapon, secondary firing mode. If the projectile is tossed, here is velocity in Z direction of the toss. TODO
		 
         */
        public  double getSecTossZ()
 	 {
    					return SecTossZ;
    				}
    			
    	
	    /**
         * 
			For Ammo or for Weapon, secondary firing mode. Maximum effective distance. TODO
		 
         */
        protected
         double SecMaxEffectDistance =
       	0;
	
 		/**
         * 
			For Ammo or for Weapon, secondary firing mode. Maximum effective distance. TODO
		 
         */
        public  double getSecMaxEffectDistance()
 	 {
    					return SecMaxEffectDistance;
    				}
    			
    	
	    /**
         * 
			If this item is some ammo or health pack, here we can find
			out how much of the attribute this item will add.
		 
         */
        protected
         int Amount =
       	0;
	
 		/**
         * 
			If this item is some ammo or health pack, here we can find
			out how much of the attribute this item will add.
		 
         */
        public  int getAmount()
 	 {
    					return Amount;
    				}
    			
    	
	    /**
         * 
			If this item is health. True if super health.
		 
         */
        protected
         boolean SuperHeal =
       	false;
	
 		/**
         * 
			If this item is health. True if super health.
		 
         */
        public  boolean isSuperHeal()
 	 {
    					return SuperHeal;
    				}
    			
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
		              			"InventoryType = " + String.valueOf(getInventoryType()
 	) + " | " + 
		              		
		              			"PickupType = " + String.valueOf(getPickupType()
 	) + " | " + 
		              		
		              			"ItemCategory = " + String.valueOf(getItemCategory()
 	) + " | " + 
		              		
		              			"Melee = " + String.valueOf(isMelee()
 	) + " | " + 
		              		
		              			"Sniping = " + String.valueOf(isSniping()
 	) + " | " + 
		              		
		              			"UsesAltAmmo = " + String.valueOf(isUsesAltAmmo()
 	) + " | " + 
		              		
		              			"PriFireModeType = " + String.valueOf(getPriFireModeType()
 	) + " | " + 
		              		
		              			"PriSplashDamage = " + String.valueOf(isPriSplashDamage()
 	) + " | " + 
		              		
		              			"PriSplashJump = " + String.valueOf(isPriSplashJump()
 	) + " | " + 
		              		
		              			"PriRecomSplashDamage = " + String.valueOf(isPriRecomSplashDamage()
 	) + " | " + 
		              		
		              			"PriTossed = " + String.valueOf(isPriTossed()
 	) + " | " + 
		              		
		              			"PriLeadTarget = " + String.valueOf(isPriLeadTarget()
 	) + " | " + 
		              		
		              			"PriInstantHit = " + String.valueOf(isPriInstantHit()
 	) + " | " + 
		              		
		              			"PriFireOnRelease = " + String.valueOf(isPriFireOnRelease()
 	) + " | " + 
		              		
		              			"PriWaitForRelease = " + String.valueOf(isPriWaitForRelease()
 	) + " | " + 
		              		
		              			"PriModeExclusive = " + String.valueOf(isPriModeExclusive()
 	) + " | " + 
		              		
		              			"PriFireRate = " + String.valueOf(getPriFireRate()
 	) + " | " + 
		              		
		              			"PriBotRefireRate = " + String.valueOf(getPriBotRefireRate()
 	) + " | " + 
		              		
		              			"PriAmmoPerFire = " + String.valueOf(getPriAmmoPerFire()
 	) + " | " + 
		              		
		              			"PriAmmoClipSize = " + String.valueOf(getPriAmmoClipSize()
 	) + " | " + 
		              		
		              			"PriAimError = " + String.valueOf(getPriAimError()
 	) + " | " + 
		              		
		              			"PriSpread = " + String.valueOf(getPriSpread()
 	) + " | " + 
		              		
		              			"PriSpreadStyle = " + String.valueOf(getPriSpreadStyle()
 	) + " | " + 
		              		
		              			"PriFireCount = " + String.valueOf(getPriFireCount()
 	) + " | " + 
		              		
		              			"PriDamageAtten = " + String.valueOf(getPriDamageAtten()
 	) + " | " + 
		              		
		              			"PriAmmoType = " + String.valueOf(getPriAmmoType()
 	) + " | " + 
		              		
		              			"PriInitialAmount = " + String.valueOf(getPriInitialAmount()
 	) + " | " + 
		              		
		              			"PriMaxAmount = " + String.valueOf(getPriMaxAmount()
 	) + " | " + 
		              		
		              			"PriMaxRange = " + String.valueOf(getPriMaxRange()
 	) + " | " + 
		              		
		              			"PriDamageType = " + String.valueOf(getPriDamageType()
 	) + " | " + 
		              		
		              			"PriArmorStops = " + String.valueOf(isPriArmorStops()
 	) + " | " + 
		              		
		              			"PriAlwaysGibs = " + String.valueOf(isPriAlwaysGibs()
 	) + " | " + 
		              		
		              			"PriSpecial = " + String.valueOf(isPriSpecial()
 	) + " | " + 
		              		
		              			"PriDetonatesGoop = " + String.valueOf(isPriDetonatesGoop()
 	) + " | " + 
		              		
		              			"PriSuperWeapon = " + String.valueOf(isPriSuperWeapon()
 	) + " | " + 
		              		
		              			"PriExtraMomZ = " + String.valueOf(isPriExtraMomZ()
 	) + " | " + 
		              		
		              			"PriProjType = " + String.valueOf(getPriProjType()
 	) + " | " + 
		              		
		              			"PriDamage = " + String.valueOf(getPriDamage()
 	) + " | " + 
		              		
		              			"PriDamageMax = " + String.valueOf(getPriDamageMax()
 	) + " | " + 
		              		
		              			"PriDamageMin = " + String.valueOf(getPriDamageMin()
 	) + " | " + 
		              		
		              			"PriSpeed = " + String.valueOf(getPriSpeed()
 	) + " | " + 
		              		
		              			"PriMaxSpeed = " + String.valueOf(getPriMaxSpeed()
 	) + " | " + 
		              		
		              			"PriLifeSpan = " + String.valueOf(getPriLifeSpan()
 	) + " | " + 
		              		
		              			"PriDamageRadius = " + String.valueOf(getPriDamageRadius()
 	) + " | " + 
		              		
		              			"PriTossZ = " + String.valueOf(getPriTossZ()
 	) + " | " + 
		              		
		              			"PriMaxEffectDistance = " + String.valueOf(getPriMaxEffectDistance()
 	) + " | " + 
		              		
		              			"SecFireModeType = " + String.valueOf(getSecFireModeType()
 	) + " | " + 
		              		
		              			"SecSplashDamage = " + String.valueOf(isSecSplashDamage()
 	) + " | " + 
		              		
		              			"SecSplashJump = " + String.valueOf(isSecSplashJump()
 	) + " | " + 
		              		
		              			"SecRecomSplashDamage = " + String.valueOf(isSecRecomSplashDamage()
 	) + " | " + 
		              		
		              			"SecTossed = " + String.valueOf(isSecTossed()
 	) + " | " + 
		              		
		              			"SecLeadTarget = " + String.valueOf(isSecLeadTarget()
 	) + " | " + 
		              		
		              			"SecInstantHit = " + String.valueOf(isSecInstantHit()
 	) + " | " + 
		              		
		              			"SecFireOnRelease = " + String.valueOf(isSecFireOnRelease()
 	) + " | " + 
		              		
		              			"SecWaitForRelease = " + String.valueOf(isSecWaitForRelease()
 	) + " | " + 
		              		
		              			"SecModeExclusive = " + String.valueOf(isSecModeExclusive()
 	) + " | " + 
		              		
		              			"SecFireRate = " + String.valueOf(getSecFireRate()
 	) + " | " + 
		              		
		              			"SecBotRefireRate = " + String.valueOf(getSecBotRefireRate()
 	) + " | " + 
		              		
		              			"SecAmmoPerFire = " + String.valueOf(getSecAmmoPerFire()
 	) + " | " + 
		              		
		              			"SecAmmoClipSize = " + String.valueOf(getSecAmmoClipSize()
 	) + " | " + 
		              		
		              			"SecAimError = " + String.valueOf(getSecAimError()
 	) + " | " + 
		              		
		              			"SecSpread = " + String.valueOf(getSecSpread()
 	) + " | " + 
		              		
		              			"SecSpreadStyle = " + String.valueOf(getSecSpreadStyle()
 	) + " | " + 
		              		
		              			"SecFireCount = " + String.valueOf(getSecFireCount()
 	) + " | " + 
		              		
		              			"SecDamageAtten = " + String.valueOf(getSecDamageAtten()
 	) + " | " + 
		              		
		              			"SecAmmoType = " + String.valueOf(getSecAmmoType()
 	) + " | " + 
		              		
		              			"SecInitialAmount = " + String.valueOf(getSecInitialAmount()
 	) + " | " + 
		              		
		              			"SecMaxAmount = " + String.valueOf(getSecMaxAmount()
 	) + " | " + 
		              		
		              			"SecMaxRange = " + String.valueOf(getSecMaxRange()
 	) + " | " + 
		              		
		              			"SecDamageType = " + String.valueOf(getSecDamageType()
 	) + " | " + 
		              		
		              			"SecArmorStops = " + String.valueOf(isSecArmorStops()
 	) + " | " + 
		              		
		              			"SecAlwaysGibs = " + String.valueOf(isSecAlwaysGibs()
 	) + " | " + 
		              		
		              			"SecSpecial = " + String.valueOf(isSecSpecial()
 	) + " | " + 
		              		
		              			"SecDetonatesGoop = " + String.valueOf(isSecDetonatesGoop()
 	) + " | " + 
		              		
		              			"SecSuperWeapon = " + String.valueOf(isSecSuperWeapon()
 	) + " | " + 
		              		
		              			"SecExtraMomZ = " + String.valueOf(isSecExtraMomZ()
 	) + " | " + 
		              		
		              			"SecProjType = " + String.valueOf(getSecProjType()
 	) + " | " + 
		              		
		              			"SecDamage = " + String.valueOf(getSecDamage()
 	) + " | " + 
		              		
		              			"SecDamageMax = " + String.valueOf(getSecDamageMax()
 	) + " | " + 
		              		
		              			"SecDamageMin = " + String.valueOf(getSecDamageMin()
 	) + " | " + 
		              		
		              			"SecSpeed = " + String.valueOf(getSecSpeed()
 	) + " | " + 
		              		
		              			"SecMaxSpeed = " + String.valueOf(getSecMaxSpeed()
 	) + " | " + 
		              		
		              			"SecLifeSpan = " + String.valueOf(getSecLifeSpan()
 	) + " | " + 
		              		
		              			"SecDamageRadius = " + String.valueOf(getSecDamageRadius()
 	) + " | " + 
		              		
		              			"SecTossZ = " + String.valueOf(getSecTossZ()
 	) + " | " + 
		              		
		              			"SecMaxEffectDistance = " + String.valueOf(getSecMaxEffectDistance()
 	) + " | " + 
		              		
		              			"Amount = " + String.valueOf(getAmount()
 	) + " | " + 
		              		
		              			"SuperHeal = " + String.valueOf(isSuperHeal()
 	) + " | " + 
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>InventoryType</b> = " + String.valueOf(getInventoryType()
 	) + " <br/> " + 
		              		
		              			"<b>PickupType</b> = " + String.valueOf(getPickupType()
 	) + " <br/> " + 
		              		
		              			"<b>ItemCategory</b> = " + String.valueOf(getItemCategory()
 	) + " <br/> " + 
		              		
		              			"<b>Melee</b> = " + String.valueOf(isMelee()
 	) + " <br/> " + 
		              		
		              			"<b>Sniping</b> = " + String.valueOf(isSniping()
 	) + " <br/> " + 
		              		
		              			"<b>UsesAltAmmo</b> = " + String.valueOf(isUsesAltAmmo()
 	) + " <br/> " + 
		              		
		              			"<b>PriFireModeType</b> = " + String.valueOf(getPriFireModeType()
 	) + " <br/> " + 
		              		
		              			"<b>PriSplashDamage</b> = " + String.valueOf(isPriSplashDamage()
 	) + " <br/> " + 
		              		
		              			"<b>PriSplashJump</b> = " + String.valueOf(isPriSplashJump()
 	) + " <br/> " + 
		              		
		              			"<b>PriRecomSplashDamage</b> = " + String.valueOf(isPriRecomSplashDamage()
 	) + " <br/> " + 
		              		
		              			"<b>PriTossed</b> = " + String.valueOf(isPriTossed()
 	) + " <br/> " + 
		              		
		              			"<b>PriLeadTarget</b> = " + String.valueOf(isPriLeadTarget()
 	) + " <br/> " + 
		              		
		              			"<b>PriInstantHit</b> = " + String.valueOf(isPriInstantHit()
 	) + " <br/> " + 
		              		
		              			"<b>PriFireOnRelease</b> = " + String.valueOf(isPriFireOnRelease()
 	) + " <br/> " + 
		              		
		              			"<b>PriWaitForRelease</b> = " + String.valueOf(isPriWaitForRelease()
 	) + " <br/> " + 
		              		
		              			"<b>PriModeExclusive</b> = " + String.valueOf(isPriModeExclusive()
 	) + " <br/> " + 
		              		
		              			"<b>PriFireRate</b> = " + String.valueOf(getPriFireRate()
 	) + " <br/> " + 
		              		
		              			"<b>PriBotRefireRate</b> = " + String.valueOf(getPriBotRefireRate()
 	) + " <br/> " + 
		              		
		              			"<b>PriAmmoPerFire</b> = " + String.valueOf(getPriAmmoPerFire()
 	) + " <br/> " + 
		              		
		              			"<b>PriAmmoClipSize</b> = " + String.valueOf(getPriAmmoClipSize()
 	) + " <br/> " + 
		              		
		              			"<b>PriAimError</b> = " + String.valueOf(getPriAimError()
 	) + " <br/> " + 
		              		
		              			"<b>PriSpread</b> = " + String.valueOf(getPriSpread()
 	) + " <br/> " + 
		              		
		              			"<b>PriSpreadStyle</b> = " + String.valueOf(getPriSpreadStyle()
 	) + " <br/> " + 
		              		
		              			"<b>PriFireCount</b> = " + String.valueOf(getPriFireCount()
 	) + " <br/> " + 
		              		
		              			"<b>PriDamageAtten</b> = " + String.valueOf(getPriDamageAtten()
 	) + " <br/> " + 
		              		
		              			"<b>PriAmmoType</b> = " + String.valueOf(getPriAmmoType()
 	) + " <br/> " + 
		              		
		              			"<b>PriInitialAmount</b> = " + String.valueOf(getPriInitialAmount()
 	) + " <br/> " + 
		              		
		              			"<b>PriMaxAmount</b> = " + String.valueOf(getPriMaxAmount()
 	) + " <br/> " + 
		              		
		              			"<b>PriMaxRange</b> = " + String.valueOf(getPriMaxRange()
 	) + " <br/> " + 
		              		
		              			"<b>PriDamageType</b> = " + String.valueOf(getPriDamageType()
 	) + " <br/> " + 
		              		
		              			"<b>PriArmorStops</b> = " + String.valueOf(isPriArmorStops()
 	) + " <br/> " + 
		              		
		              			"<b>PriAlwaysGibs</b> = " + String.valueOf(isPriAlwaysGibs()
 	) + " <br/> " + 
		              		
		              			"<b>PriSpecial</b> = " + String.valueOf(isPriSpecial()
 	) + " <br/> " + 
		              		
		              			"<b>PriDetonatesGoop</b> = " + String.valueOf(isPriDetonatesGoop()
 	) + " <br/> " + 
		              		
		              			"<b>PriSuperWeapon</b> = " + String.valueOf(isPriSuperWeapon()
 	) + " <br/> " + 
		              		
		              			"<b>PriExtraMomZ</b> = " + String.valueOf(isPriExtraMomZ()
 	) + " <br/> " + 
		              		
		              			"<b>PriProjType</b> = " + String.valueOf(getPriProjType()
 	) + " <br/> " + 
		              		
		              			"<b>PriDamage</b> = " + String.valueOf(getPriDamage()
 	) + " <br/> " + 
		              		
		              			"<b>PriDamageMax</b> = " + String.valueOf(getPriDamageMax()
 	) + " <br/> " + 
		              		
		              			"<b>PriDamageMin</b> = " + String.valueOf(getPriDamageMin()
 	) + " <br/> " + 
		              		
		              			"<b>PriSpeed</b> = " + String.valueOf(getPriSpeed()
 	) + " <br/> " + 
		              		
		              			"<b>PriMaxSpeed</b> = " + String.valueOf(getPriMaxSpeed()
 	) + " <br/> " + 
		              		
		              			"<b>PriLifeSpan</b> = " + String.valueOf(getPriLifeSpan()
 	) + " <br/> " + 
		              		
		              			"<b>PriDamageRadius</b> = " + String.valueOf(getPriDamageRadius()
 	) + " <br/> " + 
		              		
		              			"<b>PriTossZ</b> = " + String.valueOf(getPriTossZ()
 	) + " <br/> " + 
		              		
		              			"<b>PriMaxEffectDistance</b> = " + String.valueOf(getPriMaxEffectDistance()
 	) + " <br/> " + 
		              		
		              			"<b>SecFireModeType</b> = " + String.valueOf(getSecFireModeType()
 	) + " <br/> " + 
		              		
		              			"<b>SecSplashDamage</b> = " + String.valueOf(isSecSplashDamage()
 	) + " <br/> " + 
		              		
		              			"<b>SecSplashJump</b> = " + String.valueOf(isSecSplashJump()
 	) + " <br/> " + 
		              		
		              			"<b>SecRecomSplashDamage</b> = " + String.valueOf(isSecRecomSplashDamage()
 	) + " <br/> " + 
		              		
		              			"<b>SecTossed</b> = " + String.valueOf(isSecTossed()
 	) + " <br/> " + 
		              		
		              			"<b>SecLeadTarget</b> = " + String.valueOf(isSecLeadTarget()
 	) + " <br/> " + 
		              		
		              			"<b>SecInstantHit</b> = " + String.valueOf(isSecInstantHit()
 	) + " <br/> " + 
		              		
		              			"<b>SecFireOnRelease</b> = " + String.valueOf(isSecFireOnRelease()
 	) + " <br/> " + 
		              		
		              			"<b>SecWaitForRelease</b> = " + String.valueOf(isSecWaitForRelease()
 	) + " <br/> " + 
		              		
		              			"<b>SecModeExclusive</b> = " + String.valueOf(isSecModeExclusive()
 	) + " <br/> " + 
		              		
		              			"<b>SecFireRate</b> = " + String.valueOf(getSecFireRate()
 	) + " <br/> " + 
		              		
		              			"<b>SecBotRefireRate</b> = " + String.valueOf(getSecBotRefireRate()
 	) + " <br/> " + 
		              		
		              			"<b>SecAmmoPerFire</b> = " + String.valueOf(getSecAmmoPerFire()
 	) + " <br/> " + 
		              		
		              			"<b>SecAmmoClipSize</b> = " + String.valueOf(getSecAmmoClipSize()
 	) + " <br/> " + 
		              		
		              			"<b>SecAimError</b> = " + String.valueOf(getSecAimError()
 	) + " <br/> " + 
		              		
		              			"<b>SecSpread</b> = " + String.valueOf(getSecSpread()
 	) + " <br/> " + 
		              		
		              			"<b>SecSpreadStyle</b> = " + String.valueOf(getSecSpreadStyle()
 	) + " <br/> " + 
		              		
		              			"<b>SecFireCount</b> = " + String.valueOf(getSecFireCount()
 	) + " <br/> " + 
		              		
		              			"<b>SecDamageAtten</b> = " + String.valueOf(getSecDamageAtten()
 	) + " <br/> " + 
		              		
		              			"<b>SecAmmoType</b> = " + String.valueOf(getSecAmmoType()
 	) + " <br/> " + 
		              		
		              			"<b>SecInitialAmount</b> = " + String.valueOf(getSecInitialAmount()
 	) + " <br/> " + 
		              		
		              			"<b>SecMaxAmount</b> = " + String.valueOf(getSecMaxAmount()
 	) + " <br/> " + 
		              		
		              			"<b>SecMaxRange</b> = " + String.valueOf(getSecMaxRange()
 	) + " <br/> " + 
		              		
		              			"<b>SecDamageType</b> = " + String.valueOf(getSecDamageType()
 	) + " <br/> " + 
		              		
		              			"<b>SecArmorStops</b> = " + String.valueOf(isSecArmorStops()
 	) + " <br/> " + 
		              		
		              			"<b>SecAlwaysGibs</b> = " + String.valueOf(isSecAlwaysGibs()
 	) + " <br/> " + 
		              		
		              			"<b>SecSpecial</b> = " + String.valueOf(isSecSpecial()
 	) + " <br/> " + 
		              		
		              			"<b>SecDetonatesGoop</b> = " + String.valueOf(isSecDetonatesGoop()
 	) + " <br/> " + 
		              		
		              			"<b>SecSuperWeapon</b> = " + String.valueOf(isSecSuperWeapon()
 	) + " <br/> " + 
		              		
		              			"<b>SecExtraMomZ</b> = " + String.valueOf(isSecExtraMomZ()
 	) + " <br/> " + 
		              		
		              			"<b>SecProjType</b> = " + String.valueOf(getSecProjType()
 	) + " <br/> " + 
		              		
		              			"<b>SecDamage</b> = " + String.valueOf(getSecDamage()
 	) + " <br/> " + 
		              		
		              			"<b>SecDamageMax</b> = " + String.valueOf(getSecDamageMax()
 	) + " <br/> " + 
		              		
		              			"<b>SecDamageMin</b> = " + String.valueOf(getSecDamageMin()
 	) + " <br/> " + 
		              		
		              			"<b>SecSpeed</b> = " + String.valueOf(getSecSpeed()
 	) + " <br/> " + 
		              		
		              			"<b>SecMaxSpeed</b> = " + String.valueOf(getSecMaxSpeed()
 	) + " <br/> " + 
		              		
		              			"<b>SecLifeSpan</b> = " + String.valueOf(getSecLifeSpan()
 	) + " <br/> " + 
		              		
		              			"<b>SecDamageRadius</b> = " + String.valueOf(getSecDamageRadius()
 	) + " <br/> " + 
		              		
		              			"<b>SecTossZ</b> = " + String.valueOf(getSecTossZ()
 	) + " <br/> " + 
		              		
		              			"<b>SecMaxEffectDistance</b> = " + String.valueOf(getSecMaxEffectDistance()
 	) + " <br/> " + 
		              		
		              			"<b>Amount</b> = " + String.valueOf(getAmount()
 	) + " <br/> " + 
		              		
		              			"<b>SuperHeal</b> = " + String.valueOf(isSuperHeal()
 	) + " <br/> " + 
		              		
				"<br/>]";     
		}
 	
 	    public String toJsonLiteral() {
            return "itemcategory( "
            		+
									(getInventoryType()
 	 == null ? "null" :
										"\"" + getInventoryType()
 	 + "\"" 
									)
								+ ", " + 
									(getPickupType()
 	 == null ? "null" :
										"\"" + getPickupType()
 	.getName() + "\"" 
									)
								+ ", " + 
								    String.valueOf(getItemCategory()
 	)									
								+ ", " + 
								    String.valueOf(isMelee()
 	)									
								+ ", " + 
								    String.valueOf(isSniping()
 	)									
								+ ", " + 
								    String.valueOf(isUsesAltAmmo()
 	)									
								+ ", " + 
									(getPriFireModeType()
 	 == null ? "null" :
										"\"" + getPriFireModeType()
 	 + "\"" 
									)
								+ ", " + 
								    String.valueOf(isPriSplashDamage()
 	)									
								+ ", " + 
								    String.valueOf(isPriSplashJump()
 	)									
								+ ", " + 
								    String.valueOf(isPriRecomSplashDamage()
 	)									
								+ ", " + 
								    String.valueOf(isPriTossed()
 	)									
								+ ", " + 
								    String.valueOf(isPriLeadTarget()
 	)									
								+ ", " + 
								    String.valueOf(isPriInstantHit()
 	)									
								+ ", " + 
								    String.valueOf(isPriFireOnRelease()
 	)									
								+ ", " + 
								    String.valueOf(isPriWaitForRelease()
 	)									
								+ ", " + 
								    String.valueOf(isPriModeExclusive()
 	)									
								+ ", " + 
								    String.valueOf(getPriFireRate()
 	)									
								+ ", " + 
								    String.valueOf(getPriBotRefireRate()
 	)									
								+ ", " + 
								    String.valueOf(getPriAmmoPerFire()
 	)									
								+ ", " + 
								    String.valueOf(getPriAmmoClipSize()
 	)									
								+ ", " + 
								    String.valueOf(getPriAimError()
 	)									
								+ ", " + 
								    String.valueOf(getPriSpread()
 	)									
								+ ", " + 
								    String.valueOf(getPriSpreadStyle()
 	)									
								+ ", " + 
								    String.valueOf(getPriFireCount()
 	)									
								+ ", " + 
								    String.valueOf(getPriDamageAtten()
 	)									
								+ ", " + 
									(getPriAmmoType()
 	 == null ? "null" :
										"\"" + getPriAmmoType()
 	 + "\"" 
									)
								+ ", " + 
								    String.valueOf(getPriInitialAmount()
 	)									
								+ ", " + 
								    String.valueOf(getPriMaxAmount()
 	)									
								+ ", " + 
								    String.valueOf(getPriMaxRange()
 	)									
								+ ", " + 
									(getPriDamageType()
 	 == null ? "null" :
										"\"" + getPriDamageType()
 	 + "\"" 
									)
								+ ", " + 
								    String.valueOf(isPriArmorStops()
 	)									
								+ ", " + 
								    String.valueOf(isPriAlwaysGibs()
 	)									
								+ ", " + 
								    String.valueOf(isPriSpecial()
 	)									
								+ ", " + 
								    String.valueOf(isPriDetonatesGoop()
 	)									
								+ ", " + 
								    String.valueOf(isPriSuperWeapon()
 	)									
								+ ", " + 
								    String.valueOf(isPriExtraMomZ()
 	)									
								+ ", " + 
									(getPriProjType()
 	 == null ? "null" :
										"\"" + getPriProjType()
 	 + "\"" 
									)
								+ ", " + 
								    String.valueOf(getPriDamage()
 	)									
								+ ", " + 
								    String.valueOf(getPriDamageMax()
 	)									
								+ ", " + 
								    String.valueOf(getPriDamageMin()
 	)									
								+ ", " + 
								    String.valueOf(getPriSpeed()
 	)									
								+ ", " + 
								    String.valueOf(getPriMaxSpeed()
 	)									
								+ ", " + 
								    String.valueOf(getPriLifeSpan()
 	)									
								+ ", " + 
								    String.valueOf(getPriDamageRadius()
 	)									
								+ ", " + 
								    String.valueOf(getPriTossZ()
 	)									
								+ ", " + 
								    String.valueOf(getPriMaxEffectDistance()
 	)									
								+ ", " + 
									(getSecFireModeType()
 	 == null ? "null" :
										"\"" + getSecFireModeType()
 	 + "\"" 
									)
								+ ", " + 
								    String.valueOf(isSecSplashDamage()
 	)									
								+ ", " + 
								    String.valueOf(isSecSplashJump()
 	)									
								+ ", " + 
								    String.valueOf(isSecRecomSplashDamage()
 	)									
								+ ", " + 
								    String.valueOf(isSecTossed()
 	)									
								+ ", " + 
								    String.valueOf(isSecLeadTarget()
 	)									
								+ ", " + 
								    String.valueOf(isSecInstantHit()
 	)									
								+ ", " + 
								    String.valueOf(isSecFireOnRelease()
 	)									
								+ ", " + 
								    String.valueOf(isSecWaitForRelease()
 	)									
								+ ", " + 
								    String.valueOf(isSecModeExclusive()
 	)									
								+ ", " + 
								    String.valueOf(getSecFireRate()
 	)									
								+ ", " + 
								    String.valueOf(getSecBotRefireRate()
 	)									
								+ ", " + 
								    String.valueOf(getSecAmmoPerFire()
 	)									
								+ ", " + 
								    String.valueOf(getSecAmmoClipSize()
 	)									
								+ ", " + 
								    String.valueOf(getSecAimError()
 	)									
								+ ", " + 
								    String.valueOf(getSecSpread()
 	)									
								+ ", " + 
								    String.valueOf(getSecSpreadStyle()
 	)									
								+ ", " + 
								    String.valueOf(getSecFireCount()
 	)									
								+ ", " + 
								    String.valueOf(getSecDamageAtten()
 	)									
								+ ", " + 
									(getSecAmmoType()
 	 == null ? "null" :
										"\"" + getSecAmmoType()
 	 + "\"" 
									)
								+ ", " + 
								    String.valueOf(getSecInitialAmount()
 	)									
								+ ", " + 
								    String.valueOf(getSecMaxAmount()
 	)									
								+ ", " + 
								    String.valueOf(getSecMaxRange()
 	)									
								+ ", " + 
									(getSecDamageType()
 	 == null ? "null" :
										"\"" + getSecDamageType()
 	 + "\"" 
									)
								+ ", " + 
								    String.valueOf(isSecArmorStops()
 	)									
								+ ", " + 
								    String.valueOf(isSecAlwaysGibs()
 	)									
								+ ", " + 
								    String.valueOf(isSecSpecial()
 	)									
								+ ", " + 
								    String.valueOf(isSecDetonatesGoop()
 	)									
								+ ", " + 
								    String.valueOf(isSecSuperWeapon()
 	)									
								+ ", " + 
								    String.valueOf(isSecExtraMomZ()
 	)									
								+ ", " + 
									(getSecProjType()
 	 == null ? "null" :
										"\"" + getSecProjType()
 	 + "\"" 
									)
								+ ", " + 
								    String.valueOf(getSecDamage()
 	)									
								+ ", " + 
								    String.valueOf(getSecDamageMax()
 	)									
								+ ", " + 
								    String.valueOf(getSecDamageMin()
 	)									
								+ ", " + 
								    String.valueOf(getSecSpeed()
 	)									
								+ ", " + 
								    String.valueOf(getSecMaxSpeed()
 	)									
								+ ", " + 
								    String.valueOf(getSecLifeSpan()
 	)									
								+ ", " + 
								    String.valueOf(getSecDamageRadius()
 	)									
								+ ", " + 
								    String.valueOf(getSecTossZ()
 	)									
								+ ", " + 
								    String.valueOf(getSecMaxEffectDistance()
 	)									
								+ ", " + 
								    String.valueOf(getAmount()
 	)									
								+ ", " + 
								    String.valueOf(isSuperHeal()
 	)									
								
                   + ")";
        }
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
         	  
         		    
         	  
        	@Override
			public ItemType getType() {
				return getPickupType();
			}
		
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=event+classtype[@name=impl]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=event+classtype[@name=impl]) ---        	            	
 	
		}
 	