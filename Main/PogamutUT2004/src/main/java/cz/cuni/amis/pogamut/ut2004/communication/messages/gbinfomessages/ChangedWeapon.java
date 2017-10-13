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
         			Definition of the event CWP.
         		
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Asynchronous message. Bot changed weapon. Possibly as a result
		of a command sent by you. Here we will get the new weapon - the weapon the
        bot has changed to.
	
         */
 	public class ChangedWeapon 
  				extends InfoMessage
    			implements IWorldEvent, IWorldChangeEvent
    			
	    {
 	
    	/** Example how the message looks like - used during parser tests. */
    	public static final String PROTOTYPE =
    		"CWP {Id text}  {PrimaryAmmo 0}  {SecondaryAmmo 0}  {Type text} ";
    
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public ChangedWeapon()
		{
		}
	
    	
    	
    	
    	/**
		 * Creates new instance of the message ChangedWeapon.
		 * 
		Asynchronous message. Bot changed weapon. Possibly as a result
		of a command sent by you. Here we will get the new weapon - the weapon the
        bot has changed to.
	
		 * Corresponding GameBots message
		 *   
		 *   is
		 *   CWP.
		 * 
 	  	 * 
		 *   
		 *     @param Id 
			Unique Id of new weapon, based on the inventory weapon's
			name (this is different from the Id of the weapon that can
			be picked up in the map).
		
		 *   
		 * 
		 *   
		 *     @param PrimaryAmmo 
			Holding current primary ammo of the new weapon.
		
		 *   
		 * 
		 *   
		 *     @param SecondaryAmmo 
			Holding current secondary ammo of the new weapon.
		
		 *   
		 * 
		 *   
		 *     @param Type 
			A string representing the type of the weapon.
		
		 *   
		 * 
		 */
		public ChangedWeapon(
			String Id,  int PrimaryAmmo,  int SecondaryAmmo,  String Type
		) {
			
					this.Id = Id;
				
					this.PrimaryAmmo = PrimaryAmmo;
				
					this.SecondaryAmmo = SecondaryAmmo;
				
					this.Type = Type;
				
		}
    
    	/**
		 * Cloning constructor from the full message.
		 *
		 * @param original
		 */
		public ChangedWeapon(ChangedWeapon original) {		
			
					this.Id = original.getId()
 	;
				
					this.PrimaryAmmo = original.getPrimaryAmmo()
 	;
				
					this.SecondaryAmmo = original.getSecondaryAmmo()
 	;
				
					this.Type = original.getType()
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
			Unique Id of new weapon, based on the inventory weapon's
			name (this is different from the Id of the weapon that can
			be picked up in the map).
		 
         */
        protected
         String Id =
       	null;
	
 		/**
         * 
			Unique Id of new weapon, based on the inventory weapon's
			name (this is different from the Id of the weapon that can
			be picked up in the map).
		 
         */
        public  String getId()
 	 {
    					return Id;
    				}
    			
    	
	    /**
         * 
			Holding current primary ammo of the new weapon.
		 
         */
        protected
         int PrimaryAmmo =
       	0;
	
 		/**
         * 
			Holding current primary ammo of the new weapon.
		 
         */
        public  int getPrimaryAmmo()
 	 {
    					return PrimaryAmmo;
    				}
    			
    	
	    /**
         * 
			Holding current secondary ammo of the new weapon.
		 
         */
        protected
         int SecondaryAmmo =
       	0;
	
 		/**
         * 
			Holding current secondary ammo of the new weapon.
		 
         */
        public  int getSecondaryAmmo()
 	 {
    					return SecondaryAmmo;
    				}
    			
    	
	    /**
         * 
			A string representing the type of the weapon.
		 
         */
        protected
         String Type =
       	null;
	
 		/**
         * 
			A string representing the type of the weapon.
		 
         */
        public  String getType()
 	 {
    					return Type;
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
		              		
		              			"Type = " + String.valueOf(getType()
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
		              		
		              			"<b>Type</b> = " + String.valueOf(getType()
 	) + " <br/> " + 
		              		
				"<br/>]";     
		}
 	
 	    public String toJsonLiteral() {
            return "changedweapon( "
            		+
									(getId()
 	 == null ? "null" :
										"\"" + getId()
 	 + "\"" 
									)
								+ ", " + 
								    String.valueOf(getPrimaryAmmo()
 	)									
								+ ", " + 
								    String.valueOf(getSecondaryAmmo()
 	)									
								+ ", " + 
									(getType()
 	 == null ? "null" :
										"\"" + getType()
 	 + "\"" 
									)
								
                   + ")";
        }
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=event+classtype[@name=impl]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=event+classtype[@name=impl]) ---        	            	
 	
		}
 	