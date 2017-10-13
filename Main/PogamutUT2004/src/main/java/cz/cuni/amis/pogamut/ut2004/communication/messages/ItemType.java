package cz.cuni.amis.pogamut.ut2004.communication.messages;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;

import static cz.cuni.amis.pogamut.ut2004.communication.messages.UT2004ItemType.CATEGORIES;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType.Group;
import cz.cuni.amis.utils.maps.HashMapSet;

/**
 * Abastract Type of the item.
 * 
 * <p>
 * Note: Items of the same type might have different names in UT engine.
 * <b>Always use {@link #equals(Object)} to safely compare two ItemTypes.</b>
 * 
 * <p>
 * Use {@link #getCategory()} to obtain basic categorization of items.
 * 
 * <p>
 * Use {@link #getGroup()} to obtain detailed group info of items.
 * 
 * <p>
 * {@link Comparable} according to {@link ItemType#getName()}.
 * 
 * @author Juraj 'Loque' Simlovic
 * @author Jimmy
 */
public abstract class ItemType implements Serializable, Comparable<ItemType> {       
    public abstract interface Group {
        public Set<ItemType> getTypes();
        /**
         * @return human readable representation of name.
         */
        public String getName();
        
        /**
         * @return enum representation of name
         */
        public String name();
    }
    
    /**
     * Contains item types that belongs to their categories.
     */
    public static final HashMapSet<Category, ItemType> CATEGORIES = new HashMapSet<Category, ItemType>();
    
    public enum Category {
                /** Weapons of all sorts. */
        WEAPON("Weapon"),
        /** Ammunition for weapons of all sorts. */
        AMMO("Ammo"),
        /** Projectiles for weapons of all sorts */
        PROJECTILE("Projectile"),
        /** Health packs and other health restorers. */
        HEALTH("Health"),
        /** Armor packs and other armor restorers. */
        ARMOR("Armor"),
        /** Shield packs and other shield restorers. */
        SHIELD("Shield"),
        /** Adrenaline */
        ADRENALINE("Adrenaline"),
        /** Deployable weapons like slowfields, minefields, etc. */
        DEPLOYABLE("Deployable"),
        /** UDamage, Keys + user defined items */
        OTHER("Other"),
        /** No category */
        NONE("No category");

        /* =================================================================== */

        /** Human-readable name of the category. */
        public final String name;

        /* =================================================================== */

        /**
         * Constructor.
         * 
         * @param name
         *            Human-readable name of the category.
         */
        Category(String name) {
                this.name = name;
        }

        /**
         * Return all item types of a certain category.
         * 
         * @return
         */
        public Set<ItemType> getTypes() {
                return CATEGORIES.get(this);
        }
    }
    
  
      
    public abstract String toString();
    
    /**
     * Retrieves category of the item type.
     * 
     * @return String representing the category of the item.
     */
    public abstract Category getCategory();

    /**
     * Retrieves group of the item type.
     * 
     * @return String representing the group of the item.
     */
    public abstract Group getGroup();

    /**
     * Indicates whether some other ItemType is "equal to" this one.
     * 
     * @param obj
     *            Object to be compared with.
     * @return True, if the objects are equal.
     */
    public abstract boolean equals(Object obj);
   
    /**
     * Returns a hash code value for the object.
     * 
     * @return A hash code value for this object.
     */
    public abstract int hashCode();

	public abstract String getName();

   

}