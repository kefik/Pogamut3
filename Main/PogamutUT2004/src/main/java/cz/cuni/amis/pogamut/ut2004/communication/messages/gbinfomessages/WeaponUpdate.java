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
         			Definition of the event WUP.
         		
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Asynchronous message. Send when the bot changed weapon. Here we will export
        the status of the old weapon - of the weapon that was changed. So we can have
        correct info about weapons in our inventory. This could be a problem without
        this message because of synchronous batch delay.
	
         */
 	public class WeaponUpdate 
  				extends InfoMessage
    			implements IWorldEvent, IWorldChangeEvent
    			
	    {
 	
    	/** Example how the message looks like - used during parser tests. */
    	public static final String PROTOTYPE =
    		"WUP {Id unreal_id}  {PrimaryAmmo 0}  {SecondaryAmmo 0}  {InventoryType text} ";
    
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public WeaponUpdate()
		{
		}
	
    	
    	
    	
    	/**
		 * Creates new instance of the message WeaponUpdate.
		 * 
		Asynchronous message. Send when the bot changed weapon. Here we will export
        the status of the old weapon - of the weapon that was changed. So we can have
        correct info about weapons in our inventory. This could be a problem without
        this message because of synchronous batch delay.
	
		 * Corresponding GameBots message
		 *   
		 *   is
		 *   WUP.
		 * 
 	  	 * 
		 *   
		 *     @param Id 
			Unique Id of the weapon, based on the inventory weapon's
			name (this is different from the Id of the weapon that can
			be picked up in the map).
		
		 *   
		 * 
		 *   
		 *     @param PrimaryAmmo 
			Holding primary ammo of the old weapon (that was changed).
		
		 *   
		 * 
		 *   
		 *     @param SecondaryAmmo 
			Holding secondary ammo of the old weapon (that was changed)
		
		 *   
		 * 
		 *   
		 *     @param InventoryType 
			Class of the weapon in the inventory. Matches InventoryType in the item class (ITC) message.
		
		 *   
		 * 
		 */
		public WeaponUpdate(
			UnrealId Id,  int PrimaryAmmo,  int SecondaryAmmo,  String InventoryType
		) {
			
					this.Id = Id;
				
					this.PrimaryAmmo = PrimaryAmmo;
				
					this.SecondaryAmmo = SecondaryAmmo;
				
					this.InventoryType = InventoryType;
				
		}
    
    	/**
		 * Cloning constructor from the full message.
		 *
		 * @param original
		 */
		public WeaponUpdate(WeaponUpdate original) {		
			
					this.Id = original.getId()
 	;
				
					this.PrimaryAmmo = original.getPrimaryAmmo()
 	;
				
					this.SecondaryAmmo = original.getSecondaryAmmo()
 	;
				
					this.InventoryType = original.getInventoryType()
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
			Unique Id of the weapon, based on the inventory weapon's
			name (this is different from the Id of the weapon that can
			be picked up in the map).
		 
         */
        protected
         UnrealId Id =
       	null;
	
 		/**
         * 
			Unique Id of the weapon, based on the inventory weapon's
			name (this is different from the Id of the weapon that can
			be picked up in the map).
		 
         */
        public  UnrealId getId()
 	 {
    					return Id;
    				}
    			
    	
	    /**
         * 
			Holding primary ammo of the old weapon (that was changed).
		 
         */
        protected
         int PrimaryAmmo =
       	0;
	
 		/**
         * 
			Holding primary ammo of the old weapon (that was changed).
		 
         */
        public  int getPrimaryAmmo()
 	 {
    					return PrimaryAmmo;
    				}
    			
    	
	    /**
         * 
			Holding secondary ammo of the old weapon (that was changed)
		 
         */
        protected
         int SecondaryAmmo =
       	0;
	
 		/**
         * 
			Holding secondary ammo of the old weapon (that was changed)
		 
         */
        public  int getSecondaryAmmo()
 	 {
    					return SecondaryAmmo;
    				}
    			
    	
	    /**
         * 
			Class of the weapon in the inventory. Matches InventoryType in the item class (ITC) message.
		 
         */
        protected
         String InventoryType =
       	null;
	
 		/**
         * 
			Class of the weapon in the inventory. Matches InventoryType in the item class (ITC) message.
		 
         */
        public  String getInventoryType()
 	 {
    					return InventoryType;
    				}
    			
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
		              			"Id = " + String.valueOf(getId()
 	) + " | " + 
		              		
		              			"PrimaryAmmo = " + String.valueOf(getPrimaryAmmo()
 	) + " | " + 
		              		
		              			"SecondaryAmmo = " + String.valueOf(getSecondaryAmmo()
 	) + " | " + 
		              		
		              			"InventoryType = " + String.valueOf(getInventoryType()
 	) + " | " + 
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>Id</b> = " + String.valueOf(getId()
 	) + " <br/> " + 
		              		
		              			"<b>PrimaryAmmo</b> = " + String.valueOf(getPrimaryAmmo()
 	) + " <br/> " + 
		              		
		              			"<b>SecondaryAmmo</b> = " + String.valueOf(getSecondaryAmmo()
 	) + " <br/> " + 
		              		
		              			"<b>InventoryType</b> = " + String.valueOf(getInventoryType()
 	) + " <br/> " + 
		              		
				"<br/>]";     
		}
 	
 	    public String toJsonLiteral() {
            return "weaponupdate( "
            		+
									(getId()
 	 == null ? "null" :
										"\"" + getId()
 	.getStringId() + "\"" 
									)
								+ ", " + 
								    String.valueOf(getPrimaryAmmo()
 	)									
								+ ", " + 
								    String.valueOf(getSecondaryAmmo()
 	)									
								+ ", " + 
									(getInventoryType()
 	 == null ? "null" :
										"\"" + getInventoryType()
 	 + "\"" 
									)
								
                   + ")";
        }
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=event+classtype[@name=impl]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=event+classtype[@name=impl]) ---        	            	
 	
		}
 	