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
         			Definition of the event AIN.
         		
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Asynchronous message. Sent when we get new weapon or ammunition
		for weapon we do not have yet. Sent just once per weapon type or
		per new ammunition type (notify new object in our inventory, NOT
		pickup). 
		The Id of the object (Inventory Id) here is different from the object that is lying on the 
		ground and represents this item in the map (Pickup id).
		If you want to listen to every item pickup use ItemPickedUp message!
	
         */
 	public class AddInventoryMsg 
  				extends InfoMessage
    			implements IWorldEvent, IWorldChangeEvent
    			
	    {
 	
    	/** Example how the message looks like - used during parser tests. */
    	public static final String PROTOTYPE =
    		"AIN {Id unreal_id}  {Type text}  {PickupType xWeapons.FlakCannonPickup}  {Sniping False}  {Melee False}  {PrimaryInitialAmmo 0}  {MaxPrimaryAmmo 0}  {SecondaryInitialAmmo 0}  {MaxSecondaryAmmo 0} ";
    
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public AddInventoryMsg()
		{
		}
	
    	
    	
    	
    	/**
		 * Creates new instance of the message AddInventoryMsg.
		 * 
		Asynchronous message. Sent when we get new weapon or ammunition
		for weapon we do not have yet. Sent just once per weapon type or
		per new ammunition type (notify new object in our inventory, NOT
		pickup). 
		The Id of the object (Inventory Id) here is different from the object that is lying on the 
		ground and represents this item in the map (Pickup id).
		If you want to listen to every item pickup use ItemPickedUp message!
	
		 * Corresponding GameBots message
		 *   
		 *   is
		 *   AIN.
		 * 
 	  	 * 
		 *   
		 *     @param Id 
			A unique Id for this inventory item, assigned by the game.
			Unique, but based on a string describing the item type.
		
		 *   
		 * 
		 *   
		 *     @param Type 
			A string representing type (inventory type) of the object.
		
		 *   
		 * 
		 *   
		 *     @param Descriptor 
			Descriptor of the item - according the ItemType you may cast this to various XYZDescriptor. 
		
		 *   
		 * 
		 *   
		 *     @param PickupType 
			We get this item if we pick up this pickup class in the map.
		
		 *   
		 * 
		 *   
		 *     @param Sniping 
			If the item is a weapon, contains information whether
			this weapon is good for sniping.
		
		 *   
		 * 
		 *   
		 *     @param Melee 
			If the item is a weapon, contains information whether
			this weapon is a melee weapon.
		
		 *   
		 * 
		 *   
		 *     @param PrimaryInitialAmmo 
			If the item is a weapon, contains information how much
			primary ammo the weapon initial has.
		
		 *   
		 * 
		 *   
		 *     @param MaxPrimaryAmmo 
			If the item is a weapon, contains information how much
			primary ammo the weapon may have.
		
		 *   
		 * 
		 *   
		 *     @param SecondaryInitialAmmo 
			If the item is a weapon, contains information how much
			secondary ammo the weapon initial has.
		
		 *   
		 * 
		 *   
		 *     @param MaxSecondaryAmmo 
			If the item is a weapon, contains information how much
			secondary ammo the weapon may have.
		
		 *   
		 * 
		 */
		public AddInventoryMsg(
			UnrealId Id,  String Type,  ItemDescriptor Descriptor,  ItemType PickupType,  Boolean Sniping,  Boolean Melee,  int PrimaryInitialAmmo,  int MaxPrimaryAmmo,  int SecondaryInitialAmmo,  int MaxSecondaryAmmo
		) {
			
					this.Id = Id;
				
					this.Type = Type;
				
					this.Descriptor = Descriptor;
				
					this.PickupType = PickupType;
				
					this.Sniping = Sniping;
				
					this.Melee = Melee;
				
					this.PrimaryInitialAmmo = PrimaryInitialAmmo;
				
					this.MaxPrimaryAmmo = MaxPrimaryAmmo;
				
					this.SecondaryInitialAmmo = SecondaryInitialAmmo;
				
					this.MaxSecondaryAmmo = MaxSecondaryAmmo;
				
		}
    
    	/**
		 * Cloning constructor from the full message.
		 *
		 * @param original
		 */
		public AddInventoryMsg(AddInventoryMsg original) {		
			
					this.Id = original.getId()
 	;
				
					this.Type = original.getType()
 	;
				
					this.Descriptor = original.getDescriptor()
 	;
				
					this.PickupType = original.getPickupType()
 	;
				
					this.Sniping = original.isSniping()
 	;
				
					this.Melee = original.isMelee()
 	;
				
					this.PrimaryInitialAmmo = original.getPrimaryInitialAmmo()
 	;
				
					this.MaxPrimaryAmmo = original.getMaxPrimaryAmmo()
 	;
				
					this.SecondaryInitialAmmo = original.getSecondaryInitialAmmo()
 	;
				
					this.MaxSecondaryAmmo = original.getMaxSecondaryAmmo()
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
			A unique Id for this inventory item, assigned by the game.
			Unique, but based on a string describing the item type.
		 
         */
        protected
         UnrealId Id =
       	null;
	
 		/**
         * 
			A unique Id for this inventory item, assigned by the game.
			Unique, but based on a string describing the item type.
		 
         */
        public  UnrealId getId()
 	 {
    					return Id;
    				}
    			
    	
	    /**
         * 
			A string representing type (inventory type) of the object.
		 
         */
        protected
         String Type =
       	null;
	
 		/**
         * 
			A string representing type (inventory type) of the object.
		 
         */
        public  String getType()
 	 {
    					return Type;
    				}
    			
    	
	    /**
         * 
			Descriptor of the item - according the ItemType you may cast this to various XYZDescriptor. 
		 
         */
        protected
         ItemDescriptor Descriptor =
       	null;
	
 		/**
         * 
			Descriptor of the item - according the ItemType you may cast this to various XYZDescriptor. 
		 
         */
        public  ItemDescriptor getDescriptor()
 	 {
    					return Descriptor;
    				}
    			
    	
	    /**
         * 
			We get this item if we pick up this pickup class in the map.
		 
         */
        protected
         ItemType PickupType =
       	null;
	
 		/**
         * 
			We get this item if we pick up this pickup class in the map.
		 
         */
        public  ItemType getPickupType()
 	 {
    					return PickupType;
    				}
    			
    	
	    /**
         * 
			If the item is a weapon, contains information whether
			this weapon is good for sniping.
		 
         */
        protected
         Boolean Sniping =
       	null;
	
 		/**
         * 
			If the item is a weapon, contains information whether
			this weapon is good for sniping.
		 
         */
        public  Boolean isSniping()
 	 {
    					return Sniping;
    				}
    			
    	
	    /**
         * 
			If the item is a weapon, contains information whether
			this weapon is a melee weapon.
		 
         */
        protected
         Boolean Melee =
       	null;
	
 		/**
         * 
			If the item is a weapon, contains information whether
			this weapon is a melee weapon.
		 
         */
        public  Boolean isMelee()
 	 {
    					return Melee;
    				}
    			
    	
	    /**
         * 
			If the item is a weapon, contains information how much
			primary ammo the weapon initial has.
		 
         */
        protected
         int PrimaryInitialAmmo =
       	0;
	
 		/**
         * 
			If the item is a weapon, contains information how much
			primary ammo the weapon initial has.
		 
         */
        public  int getPrimaryInitialAmmo()
 	 {
    					return PrimaryInitialAmmo;
    				}
    			
    	
	    /**
         * 
			If the item is a weapon, contains information how much
			primary ammo the weapon may have.
		 
         */
        protected
         int MaxPrimaryAmmo =
       	0;
	
 		/**
         * 
			If the item is a weapon, contains information how much
			primary ammo the weapon may have.
		 
         */
        public  int getMaxPrimaryAmmo()
 	 {
    					return MaxPrimaryAmmo;
    				}
    			
    	
	    /**
         * 
			If the item is a weapon, contains information how much
			secondary ammo the weapon initial has.
		 
         */
        protected
         int SecondaryInitialAmmo =
       	0;
	
 		/**
         * 
			If the item is a weapon, contains information how much
			secondary ammo the weapon initial has.
		 
         */
        public  int getSecondaryInitialAmmo()
 	 {
    					return SecondaryInitialAmmo;
    				}
    			
    	
	    /**
         * 
			If the item is a weapon, contains information how much
			secondary ammo the weapon may have.
		 
         */
        protected
         int MaxSecondaryAmmo =
       	0;
	
 		/**
         * 
			If the item is a weapon, contains information how much
			secondary ammo the weapon may have.
		 
         */
        public  int getMaxSecondaryAmmo()
 	 {
    					return MaxSecondaryAmmo;
    				}
    			
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
		              			"Id = " + String.valueOf(getId()
 	) + " | " + 
		              		
		              			"Type = " + String.valueOf(getType()
 	) + " | " + 
		              		
		              			"PickupType = " + String.valueOf(getPickupType()
 	) + " | " + 
		              		
		              			"Sniping = " + String.valueOf(isSniping()
 	) + " | " + 
		              		
		              			"Melee = " + String.valueOf(isMelee()
 	) + " | " + 
		              		
		              			"PrimaryInitialAmmo = " + String.valueOf(getPrimaryInitialAmmo()
 	) + " | " + 
		              		
		              			"MaxPrimaryAmmo = " + String.valueOf(getMaxPrimaryAmmo()
 	) + " | " + 
		              		
		              			"SecondaryInitialAmmo = " + String.valueOf(getSecondaryInitialAmmo()
 	) + " | " + 
		              		
		              			"MaxSecondaryAmmo = " + String.valueOf(getMaxSecondaryAmmo()
 	) + " | " + 
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>Id</b> = " + String.valueOf(getId()
 	) + " <br/> " + 
		              		
		              			"<b>Type</b> = " + String.valueOf(getType()
 	) + " <br/> " + 
		              		
		              			"<b>PickupType</b> = " + String.valueOf(getPickupType()
 	) + " <br/> " + 
		              		
		              			"<b>Sniping</b> = " + String.valueOf(isSniping()
 	) + " <br/> " + 
		              		
		              			"<b>Melee</b> = " + String.valueOf(isMelee()
 	) + " <br/> " + 
		              		
		              			"<b>PrimaryInitialAmmo</b> = " + String.valueOf(getPrimaryInitialAmmo()
 	) + " <br/> " + 
		              		
		              			"<b>MaxPrimaryAmmo</b> = " + String.valueOf(getMaxPrimaryAmmo()
 	) + " <br/> " + 
		              		
		              			"<b>SecondaryInitialAmmo</b> = " + String.valueOf(getSecondaryInitialAmmo()
 	) + " <br/> " + 
		              		
		              			"<b>MaxSecondaryAmmo</b> = " + String.valueOf(getMaxSecondaryAmmo()
 	) + " <br/> " + 
		              		
				"<br/>]";     
		}
 	
 	    public String toJsonLiteral() {
            return "addinventorymsg( "
            		+
									(getId()
 	 == null ? "null" :
										"\"" + getId()
 	.getStringId() + "\"" 
									)
								+ ", " + 
									(getType()
 	 == null ? "null" :
										"\"" + getType()
 	 + "\"" 
									)
								+ ", " + 
									(getPickupType()
 	 == null ? "null" :
										"\"" + getPickupType()
 	.getName() + "\"" 
									)
								+ ", " + 
								    String.valueOf(isSniping()
 	)									
								+ ", " + 
								    String.valueOf(isMelee()
 	)									
								+ ", " + 
								    String.valueOf(getPrimaryInitialAmmo()
 	)									
								+ ", " + 
								    String.valueOf(getMaxPrimaryAmmo()
 	)									
								+ ", " + 
								    String.valueOf(getSecondaryInitialAmmo()
 	)									
								+ ", " + 
								    String.valueOf(getMaxSecondaryAmmo()
 	)									
								
                   + ")";
        }
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=event+classtype[@name=impl]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=event+classtype[@name=impl]) ---        	            	
 	
		}
 	