/*
 * Copyright (C) 2013 AMIS research group, Faculty of Mathematics and Physics, Charles University in Prague, Czech Republic
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cz.cuni.amis.pogamut.ut2004.communication.messages;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Self;
import cz.cuni.amis.utils.maps.HashMapSet;

import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author Evers
 */
public class UT2004ItemType extends ItemType {
	
	/**
     * Map of all registered ItemType prototypes.
     */
    protected static HashMap<String, ItemType> protos = new HashMap<String, ItemType>();
    
    /**
     * Contains item types that belongs to their groups.
     */
    public static final HashMapSet<UT2004Group, ItemType> GROUPS = new HashMapSet<UT2004Group, ItemType>();
    
    /**
     * Category of this item.
     */
    private Category category;
    
    /**
     * Group of this item.
     */
    private UT2004Group group;

    @Override
    public String toString() {
        return "ItemType[name = " + name + ", category = " + category + ", group = " + group + "]";
    }

    @Override
    public Category getCategory() {
        return category;
    }

    @Override
    public UT2004Group getGroup() {
        return group;
    }

    @Override
    public boolean equals(Object obj) {
        // the same object?
        if (this == obj)
            return true;

        // the same type?
        if (obj instanceof UT2004ItemType) {
            // same value
            if ((category == ((UT2004ItemType) obj).getCategory()) && (group == ((UT2004ItemType) obj).getGroup())
                    && (name == ((UT2004ItemType) obj).getName()))
                return true;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    @Override
    public int compareTo(ItemType o) {
        if (o == null) return 1;
        
        if (getName() == null) {
            if (o.getName() == null)
                return 0;
            return 1;
        } else {
            if (o.getName() == null) 
                return -1;
            return getName().compareTo(o.getName());
        }
    }
        
    /**
     * List of all item groups. Groups fine down the categories into specific
     * groups, based on what the item belongs to. Also, groups join items from
     * different categories together, if they belong together (e.g. weapon with
     * its ammo).
     */
    public enum UT2004Group implements Group {
        /** Translocating weapon and accessory. */
        TRANSLOCATOR("Translocator"),
        /** ShieldGun weapon and accessory. */
        SHIELD_GUN("ShieldGun"),
        /** AssaultRifle weapon and accessory. */
        ASSAULT_RIFLE("AssaultRifle"),
        /** BioRifle weapon and accessory. */
        BIO_RIFLE("BioRifle"),
        /** ShockRifle weapon and accessory. */
        SHOCK_RIFLE("ShockRifle"),
        /** LinkGun weapon and accessory. */
        LINK_GUN("LinkGun"),
        /** Minigun weapon and accessory. */
        MINIGUN("Minigun"),
        /** FlakCannon weapon and accessory. */
        FLAK_CANNON("FlakCannon"),
        /** RocketLauncher weapon and accessory. */
        ROCKET_LAUNCHER("RocketLauncher"),
        /** LightningGun weapon and accessory. */
        LIGHTNING_GUN("LightningGun"),
        /** SniperRifle weapon and accessory. */
        SNIPER_RIFLE("SniperRifle"),
        /** IonPainter weapon and accessory. */
        ION_PAINTER("IonPainter"),
        /** Redeemer weapon and accessory. */
        REDEEMER("Redeemer"),
        /** SuperShockRifle weapon and accessory. */
        SUPER_SHOCK_RIFLE("SuperShockRifle"),
        /** OnsMineLayer weapon and accessory. */
        ONS_MINE_LAYER("ONS MineLayer"),
        /** OnsGrenadeLauncher weapon and accessory. */
        ONS_GRENADE_LAUNCHER("ONS GrenadeLauncher"),
        /** OnsAvril weapon and accessory. */
        ONS_AVRIL("ONS AVRiL"),
        /** TargetPainter weapon and accessory. */
        ONS_TARGET_PAINTER("TargetPainter"),

        /** Classic health pack. */
        HEALTH("HealthKit"),
        /** Mini health vial. */
        MINI_HEALTH("HealthVial"),
        /** Big health recharger. */
        SUPER_HEALTH("SuperHealth"),

        /** Shield pack. */
        SMALL_ARMOR("SmallShield"),
        /** Shield pack. */
        SUPER_ARMOR("SuperShield"),

        /** Adrenaline packs and adrenaline restorers. */
        ADRENALINE("Adrenaline"),
        /** UDamage bonus items. */
        UDAMAGE("UDamage"),
        /** Keys. */
        KEY("Key"),
        /** Other items with user-defined group. */
        OTHER("Unknown"),
        /** No group, used for the prototype None */
        NONE("None");

        /* =================================================================== */
        
        /** Human-readable name of the group. */
        public String name;

        /* =================================================================== */

        /**
         * Constructor.
         * 
         * @param name
         *            Human-readable name of the group.
         */
        UT2004Group(String name) {
                this.name = name;
        }

        @Override
        public Set<ItemType> getTypes() {
                return GROUPS.get(this);
        }
        
        @Override
        public String getName() {
            return this.name;
        }
    }
    
    /* ======================================================================== */

    /** Translocator. */
    public static final UT2004ItemType TRANSLOCATOR = MakePrototype(Category.WEAPON, UT2004Group.TRANSLOCATOR, new String[] {
                    "XWeapons.TransPickup", "XWeapons.Transpickup", "XWeapons.Translauncher" });

    /** Translocator Beacon. */
    public static final UT2004ItemType TRANSLOCATOR_PROJECTILE = MakePrototype(Category.PROJECTILE, UT2004Group.TRANSLOCATOR,
                    new String[] { "XWeapons.BlueBeacon", "XWeapons.RedBeacon" });

    /** ShieldGun weapon. */
    public static final UT2004ItemType SHIELD_GUN = MakePrototype(Category.WEAPON, UT2004Group.SHIELD_GUN, new String[] {
                    "XWeapons.ShieldGunPickup", "XWeapons.ShieldGun" });

    /** ShieldGun ammo - sent when the bot is spawned. */
    public static final UT2004ItemType SHIELD_GUN_AMMO = MakePrototype(Category.AMMO, UT2004Group.SHIELD_GUN, new String[] {
                    "XWeapons.ShieldAmmoPickup", "XWeapons.ShieldAmmo" });

    /** AssaultRifle weapon. */
    public static final UT2004ItemType ASSAULT_RIFLE = MakePrototype(Category.WEAPON, UT2004Group.ASSAULT_RIFLE, new String[] {
                    "XWeapons.AssaultRiflePickup", "XWeapons.AssaultRifle" });
    /** AssaultRifle ammo. */
    public static final UT2004ItemType ASSAULT_RIFLE_AMMO = MakePrototype(Category.AMMO, UT2004Group.ASSAULT_RIFLE, new String[] {
                    "XWeapons.AssaultAmmoPickup", "XWeapons.AssaultAmmo" });
    /** AssaultRifle secondary ammo. */
    public static final UT2004ItemType ASSAULT_RIFLE_GRENADE = MakePrototype(Category.AMMO, UT2004Group.ASSAULT_RIFLE,
                    new String[] { "XWeapons.GrenadeAmmoPickup", "XWeapons.GrenadeAmmo" });
    /** AssaultRifle projectile. */
    public static final UT2004ItemType ASSAULT_RIFLE_PROJECTILE = MakePrototype(Category.PROJECTILE, UT2004Group.ASSAULT_RIFLE,
                    new String[] { "XWeapons.Grenade" });

    /** BioRifle weapon. */
    public static final UT2004ItemType BIO_RIFLE = MakePrototype(Category.WEAPON, UT2004Group.BIO_RIFLE, new String[] {
                    "XWeapons.BioRiflePickup", "UTClassic.ClassicBioRiflePickup", "XWeapons.BioRifle" });
    /** BioRifle ammo. */
    public static final UT2004ItemType BIO_RIFLE_AMMO = MakePrototype(Category.AMMO, UT2004Group.BIO_RIFLE, new String[] {
                    "XWeapons.BioAmmoPickup", "XWeapons.BioAmmo" });

    /** BioRifle projectile. */
    public static final UT2004ItemType BIO_RIFLE_PROJECTILE = MakePrototype(Category.PROJECTILE, UT2004Group.BIO_RIFLE,
                    new String[] { "XWeapons.BioGlob" });

    /** ShockRifle weapon. */
    public static final UT2004ItemType SHOCK_RIFLE = MakePrototype(Category.WEAPON, UT2004Group.SHOCK_RIFLE, new String[] {
                    "XWeapons.ShockRiflePickup", "UTClassic.ClassicShockRiflePickup", "XWeapons.ShockRifle" });
    /** ShockRifle ammo. */
    public static final UT2004ItemType SHOCK_RIFLE_AMMO = MakePrototype(Category.AMMO, UT2004Group.SHOCK_RIFLE, new String[] {
                    "XWeapons.ShockAmmoPickup", "XWeapons.ShockAmmo" });

    /** ShockRifle projectile. */
    public static final UT2004ItemType SHOCK_RIFLE_PROJECTILE = MakePrototype(Category.PROJECTILE, UT2004Group.SHOCK_RIFLE,
                    new String[] { "XWeapons.ShockProjectile" });

    /** LinkGun weapon. */
    public static final UT2004ItemType LINK_GUN = MakePrototype(Category.WEAPON, UT2004Group.LINK_GUN, new String[] {
                    "XWeapons.LinkGunPickup", "XWeapons.LinkGun" });
    /** LinkGun ammo. */
    public static final UT2004ItemType LINK_GUN_AMMO = MakePrototype(Category.AMMO, UT2004Group.LINK_GUN, new String[] {
                    "XWeapons.LinkAmmoPickup", "XWeapons.LinkAmmo" });

    /** LinkGun projectile. */
    public static final UT2004ItemType LINK_GUN_PROJECTILE = MakePrototype(Category.PROJECTILE, UT2004Group.LINK_GUN,
                    new String[] { "XWeapons.LinkProjectile" });

    /** Minigun weapon. */
    public static final UT2004ItemType MINIGUN = MakePrototype(Category.WEAPON, UT2004Group.MINIGUN, new String[] {
                    "XWeapons.MinigunPickup", "UTClassic.ClassicMinigunPickup", "XWeapons.Minigun" });
    /** Minigun ammo. */
    public static final UT2004ItemType MINIGUN_AMMO = MakePrototype(Category.AMMO, UT2004Group.MINIGUN, new String[] {
                    "XWeapons.MinigunAmmoPickup", "XWeapons.MinigunAmmo" });

    /** FlakCannon weapon. */
    public static final UT2004ItemType FLAK_CANNON = MakePrototype(Category.WEAPON, UT2004Group.FLAK_CANNON, new String[] {
                    "XWeapons.FlakCannonPickup", "UTClassic.ClassicFlakCannonPickup", "XWeapons.FlakCannon" });
    /** FlakCannon ammo. */
    public static final UT2004ItemType FLAK_CANNON_AMMO = MakePrototype(Category.AMMO, UT2004Group.FLAK_CANNON, new String[] {
                    "XWeapons.FlakAmmoPickup", "XWeapons.FlakAmmo" });

    /** FlakCannon chunk projectile. */
    public static final UT2004ItemType FLAK_CANNON_CHUNK = MakePrototype(Category.PROJECTILE, UT2004Group.FLAK_CANNON,
                    new String[] { "XWeapons.FlakChunk" });

    /** FlakCannon shell projectile. */
    public static final UT2004ItemType FLAK_CANNON_SHELL = MakePrototype(Category.PROJECTILE, UT2004Group.FLAK_CANNON,
                    new String[] { "XWeapons.FlakShell" });

    /** RocketLauncher weapon. */
    public static final UT2004ItemType ROCKET_LAUNCHER = MakePrototype(Category.WEAPON, UT2004Group.ROCKET_LAUNCHER, new String[] {
                    "XWeapons.RocketLauncherPickup", "UTClassic.ClassicRocketLauncherPickup", "XWeapons.RocketLauncher" });
    /** RocketLauncher ammo. */
    public static final UT2004ItemType ROCKET_LAUNCHER_AMMO = MakePrototype(Category.AMMO, UT2004Group.ROCKET_LAUNCHER,
                    new String[] { "XWeapons.RocketAmmoPickup", "XWeapons.RocketAmmo" });

    /** RocketLauncher projectile. */
    public static final UT2004ItemType ROCKET_LAUNCHER_PROJECTILE = MakePrototype(Category.PROJECTILE, UT2004Group.ROCKET_LAUNCHER,
                    new String[] { "XWeapons.RocketProj" /* Proj is correct */});

    /** LightningGun weapon (modern sniper weapon). */
    public static final UT2004ItemType LIGHTNING_GUN = MakePrototype(Category.WEAPON, UT2004Group.LIGHTNING_GUN, new String[] {
                    "XWeapons.SniperRiflePickup", "XWeapons.SniperRifle" });

    /** LightningGun ammo. */
    public static final UT2004ItemType LIGHTNING_GUN_AMMO = MakePrototype(Category.AMMO, UT2004Group.LIGHTNING_GUN, new String[] {
                    "XWeapons.SniperAmmoPickup", "XWeapons.SniperAmmo" });

    /** SniperRifle weapon (classic sniper weapon). */
    public static final UT2004ItemType SNIPER_RIFLE = MakePrototype(Category.WEAPON, UT2004Group.SNIPER_RIFLE,
                    new String[] { "UTClassic.ClassicSniperRiflePickup" });
    /** SniperRifle ammo. */
    public static final UT2004ItemType SNIPER_RIFLE_AMMO = MakePrototype(Category.AMMO, UT2004Group.SNIPER_RIFLE, new String[] {
                    "UTClassic.ClassicSniperAmmoPickup", "UTClassic.ClassicSniperAmmo" });

    /** Redeemer weapon. */
    public static final UT2004ItemType REDEEMER = MakePrototype(Category.WEAPON, UT2004Group.REDEEMER, new String[] {
                    "XWeapons.RedeemerPickup", "XWeapons.Redeemer" });

    /** Redeemer ammo. Does not actually exist.*/
    public static final UT2004ItemType REDEEMER_AMMO = MakePrototype(Category.AMMO, UT2004Group.REDEEMER, new String[] {
                    "XWeapons.RedeemerAmmo" });

    /** Redeemer weapon. */
    public static final UT2004ItemType REDEEMER_PROJECTILE = MakePrototype(Category.PROJECTILE, UT2004Group.REDEEMER,
                    new String[] { "XWeapons.RedeemerProjectile" });

    /** SuperShockRifle weapon (instagib weapon). */
    public static final UT2004ItemType SUPER_SHOCK_RIFLE = MakePrototype(Category.WEAPON, UT2004Group.SUPER_SHOCK_RIFLE,
                    new String[] { "XWeapons.SuperShockRiflePickup", "XWeapons.SuperShockRifle" });

    /** IonPainter weapon. */
    public static final UT2004ItemType ION_PAINTER = MakePrototype(Category.WEAPON, UT2004Group.ION_PAINTER, new String[] {
                    "XWeapons.PainterPickup", "XWeapons.Painter" });
    /** IonPainter ammo. Uses BallAmmo odly enough. */
    public static final UT2004ItemType ION_PAINTER_AMMO = MakePrototype(Category.AMMO, UT2004Group.ION_PAINTER, new String[] {"XWeapons.BallAmmo" });

    /** MineLayer Onslaught weapon. */
    public static final UT2004ItemType ONS_MINE_LAYER = MakePrototype(Category.WEAPON, UT2004Group.ONS_MINE_LAYER, new String[] {
                    "Onslaught.ONSMineLayerPickup", "Onslaught.ONSMineLayer" });
    /** MineLayer ammo. */
    public static final UT2004ItemType ONS_MINE_LAYER_AMMO = MakePrototype(Category.AMMO, UT2004Group.ONS_MINE_LAYER, new String[] {
                    "Onslaught.ONSMineAmmoPickup", "Onslaught.ONSMineAmmo" });

    /** MineLayer projectile. */
    public static final UT2004ItemType ONS_MINE_LAYER_PROJECTILE = MakePrototype(Category.PROJECTILE, UT2004Group.ONS_MINE_LAYER,
                    new String[] { "Onslaught.ONSMineProjectileRED", "Onslaught.ONSMineProjectileBLUE" });

    /** GrenadeLauncher Onslaught weapon. */
    public static final UT2004ItemType ONS_GRENADE_LAUNCHER = MakePrototype(Category.WEAPON, UT2004Group.ONS_GRENADE_LAUNCHER,
                    new String[] { "Onslaught.ONSGrenadePickup", "Onslaught.ONSGrenade" });

    /** GrenadeLauncher ammo. */
    public static final UT2004ItemType ONS_GRENADE_LAUNCHER_AMMO = MakePrototype(Category.AMMO, UT2004Group.ONS_GRENADE_LAUNCHER,
                    new String[] { "Onslaught.ONSGrenadeAmmoPickup" });

    /** GrenadeLauncher ammo. */
    public static final UT2004ItemType ONS_GRENADE_LAUNCHER_PROJECTILE = MakePrototype(Category.PROJECTILE, UT2004Group.ONS_GRENADE_LAUNCHER,
                    new String[] { "Onslaught.ONSGrenadeProjectile" });

    /** AVRiL Onslaught weapon. */
    public static final UT2004ItemType ONS_AVRIL = MakePrototype(Category.WEAPON, UT2004Group.ONS_AVRIL, new String[] {
                    "Onslaught.ONSAVRiLPickup", "Onslaught.ONSAVRiL" });
    /** AVRiL ammo. */
    public static final UT2004ItemType ONS_AVRIL_AMMO = MakePrototype(Category.AMMO, UT2004Group.ONS_AVRIL,
                    new String[] { "Onslaught.ONSAVRiLAmmoPickup" });

    /** AVRiL projectile.	*/
    public static final UT2004ItemType ONS_AVRIL_PROJECTILE = MakePrototype(Category.PROJECTILE, UT2004Group.ONS_AVRIL,
                    new String[] { "Onslaught.ONSAVRiLRocket"});

    /** TargetPainter Onslaught weapon. */
    public static final UT2004ItemType ONS_TARGET_PAINTER = MakePrototype(Category.WEAPON, UT2004Group.ONS_TARGET_PAINTER,
                    new String[] { "OnslaughtFull.ONSPainterPickup", "OnslaughtFull.ONSPainter" });

    /** Health kit. */
    public static final UT2004ItemType HEALTH_PACK = MakePrototype(Category.HEALTH, UT2004Group.HEALTH, new String[] {
                    "XPickups.HealthPack", "XPickups.TournamentHealth" });
    /** Health vial. */
    public static final UT2004ItemType MINI_HEALTH_PACK = MakePrototype(Category.HEALTH,UT2004Group.MINI_HEALTH,
                    new String[] { "XPickups.MiniHealthPack" });
    /** SuperHealth charger. */
    public static final UT2004ItemType SUPER_HEALTH_PACK = MakePrototype(Category.HEALTH, UT2004Group.SUPER_HEALTH,
                    new String[] { "XPickups.SuperHealthPack" });

    /** SmallShield. */
    public static final UT2004ItemType SHIELD_PACK = MakePrototype(Category.ARMOR, UT2004Group.SMALL_ARMOR, new String[] {
                    "XPickups.ShieldPack", "XPickups.ShieldPickup" });
    /** SuperShield. */
    public static final UT2004ItemType SUPER_SHIELD_PACK = MakePrototype(Category.ARMOR, UT2004Group.SUPER_ARMOR,
                    new String[] { "XPickups.SuperShieldPack" });

    /** UDamage bonus (damage multiplier). */
    public static final UT2004ItemType U_DAMAGE_PACK = MakePrototype(Category.OTHER, UT2004Group.UDAMAGE, new String[] {
                    "XPickups.UDamagePack", "XGame.UDamageReward" });

    /** Adrenaline capsule. */
    public static final UT2004ItemType ADRENALINE_PACK = MakePrototype(Category.ADRENALINE, UT2004Group.ADRENALINE,
                    new String[] { "XPickups.AdrenalinePickup" });

    /** Key. */
    public static final UT2004ItemType KEY = MakePrototype(Category.OTHER, UT2004Group.KEY, new String[] { "UnrealGame.KeyPickup" });

    /** No ItemType */
    public static final UT2004ItemType NONE = MakePrototype(Category.OTHER, UT2004Group.NONE,
                    new String[] { "None", "NONE", "none" });   
    
    /**
     * Public constructor - creates ItemType of the EXTRA category and Group
     * OTHER.
     * 
     * @param name
     *            Type name from GB engine.
     */
    public UT2004ItemType(String name) {
        this.name = name;
        this.category = Category.OTHER;
        this.group = UT2004Group.OTHER;
    }

    /**
     * Prototypes constructor.
     */
    private UT2004ItemType(String name, Category category, UT2004Group group) {
            this.name = name;
            this.category = category;
            this.group = group;
    }
    
    /**
     * Attempts to recognize the weapon you are currently holding...
     * <p>
     * <p>
     * See {@link Self#getWeapon()}.
     * <p>
     * <p>
     * May return null == weapon was not recognized. ALWAYS CHECK!
     * 
     * @return
     */
    public static ItemType getWeapon(UnrealId id) {
        if (id == null)
                return null;
        String str = id.getStringId();
        if (str.contains("."))
                str = str.substring(str.lastIndexOf(".") + 1);
        str = str.toLowerCase();
        if (str.equals("assaultrifle"))
                return UT2004ItemType.ASSAULT_RIFLE;
        if (str.equals("shieldgun"))
                return UT2004ItemType.SHIELD_GUN;
        if (str.equals("flakcannon"))
                return UT2004ItemType.FLAK_CANNON;
        if (str.equals("biorifle"))
                return UT2004ItemType.BIO_RIFLE;
        if (str.equals("shockrifle"))
                return UT2004ItemType.SHOCK_RIFLE;
        if (str.equals("linkgun"))
                return UT2004ItemType.LINK_GUN;
        if (str.equals("sniperrifle"))
                return UT2004ItemType.SNIPER_RIFLE;
        if (str.equals("rocketlauncher"))
                return UT2004ItemType.ROCKET_LAUNCHER;
        if (str.equals("minigun"))
                return UT2004ItemType.MINIGUN;
        if (str.equals("lightinggun"))
                return UT2004ItemType.LIGHTNING_GUN;
        if (str.equals("translocator"))
                return UT2004ItemType.TRANSLOCATOR;
        if (str.equals("translauncher"))
                return UT2004ItemType.TRANSLOCATOR;
        if (str.equals("redeemer"))
                return UT2004ItemType.REDEEMER;
        if (str.equals("painter"))
                return UT2004ItemType.ION_PAINTER;
        if (str.equals("classicsniperrifle"))
            return UT2004ItemType.SNIPER_RIFLE;
        return null;
    }
    
    /**
     * Proto-constructor.
     * 
     * @param category
     *            Category of the item.
     * @param group
     *            Group of the item.
     * @param utNames
     *            Names of the item in UT engine.
     * @return Prototype of known ItemType.
     */
    public static UT2004ItemType MakePrototype(Category category, UT2004Group group, String[] utNames) {
            UT2004ItemType type;
            synchronized (protos) {
                    // create new itemtype prototype
                    type = new UT2004ItemType(utNames[0], category, group);
                    // register the itemtype prototype
                    for (String utName : utNames)
                            protos.put(utName, type);
                    // C'est la vie..
                    if (category != null) {
                            CATEGORIES.get(category).add(type);
                    }
                    if (group != null) {
                            GROUPS.get(group).add(type);
                    }
            }
            return type;
    }
    
  
    /**
     * Name of the item in UT engine.
     * 
     * <p>
     * Note: Items of the same type might have different names in UT engine. Use
     * {@link #equals(Object)} to safely compare two ItemTypes. This name is
     * informative only.
     */
    protected String name;
    
    /**
     * Retrieves an ItemType for the specified item type name.
     * 
     * @param utName
     *            e.g. Item.getType()
     * @return
     */
    public static ItemType getItemType(String utName) {
        ItemType type;
                       
        synchronized (protos) {                        
                type = protos.get(utName);
                if (type != null)
                        return type;

                type = new UT2004ItemType(utName);
                protos.put(utName, type);
        }
        return type;
    }
    

    public String getName() {
        return name;
    }
        
}
