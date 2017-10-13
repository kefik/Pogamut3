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
         			Definition of the event HIT.
         		
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Asynchronous message. Bot hurt another player. Hit them with a
		shot.
	
         */
 	public class PlayerDamaged 
  				extends InfoMessage
    			implements IWorldEvent, IWorldChangeEvent
    			
	    {
 	
    	/** Example how the message looks like - used during parser tests. */
    	public static final String PROTOTYPE =
    		"HIT {Id unreal_id}  {Damage 0}  {DamageType text}  {WeaponName text}  {Flaming False}  {DirectDamage False}  {BulletHit False}  {VehicleHit False} ";
    
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public PlayerDamaged()
		{
		}
	
    	
    	
    	
    	/**
		 * Creates new instance of the message PlayerDamaged.
		 * 
		Asynchronous message. Bot hurt another player. Hit them with a
		shot.
	
		 * Corresponding GameBots message
		 *   
		 *   is
		 *   HIT.
		 * 
 	  	 * 
		 *   
		 *     @param Id Unique Id of the player hit.
		 *   
		 * 
		 *   
		 *     @param Damage Amount of damage done.
		 *   
		 * 
		 *   
		 *     @param DamageType 
			A string describing what kind of damage.
		
		 *   
		 * 
		 *   
		 *     @param WeaponName 
			Name of the weapon that caused this damage.
		
		 *   
		 * 
		 *   
		 *     @param Flaming 
			If this damage is causing our bot to burn. TODO
		
		 *   
		 * 
		 *   
		 *     @param DirectDamage 
			If the damage is direct. TODO
		
		 *   
		 * 
		 *   
		 *     @param BulletHit 
			If this damage was caused by bullet.
		
		 *   
		 * 
		 *   
		 *     @param VehicleHit 
			If this damage was caused by vehicle running over.
		
		 *   
		 * 
		 */
		public PlayerDamaged(
			UnrealId Id,  int Damage,  String DamageType,  String WeaponName,  boolean Flaming,  boolean DirectDamage,  boolean BulletHit,  boolean VehicleHit
		) {
			
					this.Id = Id;
				
					this.Damage = Damage;
				
					this.DamageType = DamageType;
				
					this.WeaponName = WeaponName;
				
					this.Flaming = Flaming;
				
					this.DirectDamage = DirectDamage;
				
					this.BulletHit = BulletHit;
				
					this.VehicleHit = VehicleHit;
				
		}
    
    	/**
		 * Cloning constructor from the full message.
		 *
		 * @param original
		 */
		public PlayerDamaged(PlayerDamaged original) {		
			
					this.Id = original.getId()
 	;
				
					this.Damage = original.getDamage()
 	;
				
					this.DamageType = original.getDamageType()
 	;
				
					this.WeaponName = original.getWeaponName()
 	;
				
					this.Flaming = original.isFlaming()
 	;
				
					this.DirectDamage = original.isDirectDamage()
 	;
				
					this.BulletHit = original.isBulletHit()
 	;
				
					this.VehicleHit = original.isVehicleHit()
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
         * Unique Id of the player hit. 
         */
        protected
         UnrealId Id =
       	null;
	
 		/**
         * Unique Id of the player hit. 
         */
        public  UnrealId getId()
 	 {
    					return Id;
    				}
    			
    	
	    /**
         * Amount of damage done. 
         */
        protected
         int Damage =
       	0;
	
 		/**
         * Amount of damage done. 
         */
        public  int getDamage()
 	 {
    					return Damage;
    				}
    			
    	
	    /**
         * 
			A string describing what kind of damage.
		 
         */
        protected
         String DamageType =
       	null;
	
 		/**
         * 
			A string describing what kind of damage.
		 
         */
        public  String getDamageType()
 	 {
    					return DamageType;
    				}
    			
    	
	    /**
         * 
			Name of the weapon that caused this damage.
		 
         */
        protected
         String WeaponName =
       	null;
	
 		/**
         * 
			Name of the weapon that caused this damage.
		 
         */
        public  String getWeaponName()
 	 {
    					return WeaponName;
    				}
    			
    	
	    /**
         * 
			If this damage is causing our bot to burn. TODO
		 
         */
        protected
         boolean Flaming =
       	false;
	
 		/**
         * 
			If this damage is causing our bot to burn. TODO
		 
         */
        public  boolean isFlaming()
 	 {
    					return Flaming;
    				}
    			
    	
	    /**
         * 
			If the damage is direct. TODO
		 
         */
        protected
         boolean DirectDamage =
       	false;
	
 		/**
         * 
			If the damage is direct. TODO
		 
         */
        public  boolean isDirectDamage()
 	 {
    					return DirectDamage;
    				}
    			
    	
	    /**
         * 
			If this damage was caused by bullet.
		 
         */
        protected
         boolean BulletHit =
       	false;
	
 		/**
         * 
			If this damage was caused by bullet.
		 
         */
        public  boolean isBulletHit()
 	 {
    					return BulletHit;
    				}
    			
    	
	    /**
         * 
			If this damage was caused by vehicle running over.
		 
         */
        protected
         boolean VehicleHit =
       	false;
	
 		/**
         * 
			If this damage was caused by vehicle running over.
		 
         */
        public  boolean isVehicleHit()
 	 {
    					return VehicleHit;
    				}
    			
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
		              			"Id = " + String.valueOf(getId()
 	) + " | " + 
		              		
		              			"Damage = " + String.valueOf(getDamage()
 	) + " | " + 
		              		
		              			"DamageType = " + String.valueOf(getDamageType()
 	) + " | " + 
		              		
		              			"WeaponName = " + String.valueOf(getWeaponName()
 	) + " | " + 
		              		
		              			"Flaming = " + String.valueOf(isFlaming()
 	) + " | " + 
		              		
		              			"DirectDamage = " + String.valueOf(isDirectDamage()
 	) + " | " + 
		              		
		              			"BulletHit = " + String.valueOf(isBulletHit()
 	) + " | " + 
		              		
		              			"VehicleHit = " + String.valueOf(isVehicleHit()
 	) + " | " + 
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>Id</b> = " + String.valueOf(getId()
 	) + " <br/> " + 
		              		
		              			"<b>Damage</b> = " + String.valueOf(getDamage()
 	) + " <br/> " + 
		              		
		              			"<b>DamageType</b> = " + String.valueOf(getDamageType()
 	) + " <br/> " + 
		              		
		              			"<b>WeaponName</b> = " + String.valueOf(getWeaponName()
 	) + " <br/> " + 
		              		
		              			"<b>Flaming</b> = " + String.valueOf(isFlaming()
 	) + " <br/> " + 
		              		
		              			"<b>DirectDamage</b> = " + String.valueOf(isDirectDamage()
 	) + " <br/> " + 
		              		
		              			"<b>BulletHit</b> = " + String.valueOf(isBulletHit()
 	) + " <br/> " + 
		              		
		              			"<b>VehicleHit</b> = " + String.valueOf(isVehicleHit()
 	) + " <br/> " + 
		              		
				"<br/>]";     
		}
 	
 	    public String toJsonLiteral() {
            return "playerdamaged( "
            		+
									(getId()
 	 == null ? "null" :
										"\"" + getId()
 	.getStringId() + "\"" 
									)
								+ ", " + 
								    String.valueOf(getDamage()
 	)									
								+ ", " + 
									(getDamageType()
 	 == null ? "null" :
										"\"" + getDamageType()
 	 + "\"" 
									)
								+ ", " + 
									(getWeaponName()
 	 == null ? "null" :
										"\"" + getWeaponName()
 	 + "\"" 
									)
								+ ", " + 
								    String.valueOf(isFlaming()
 	)									
								+ ", " + 
								    String.valueOf(isDirectDamage()
 	)									
								+ ", " + 
								    String.valueOf(isBulletHit()
 	)									
								+ ", " + 
								    String.valueOf(isVehicleHit()
 	)									
								
                   + ")";
        }
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=event+classtype[@name=impl]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=event+classtype[@name=impl]) ---        	            	
 	
		}
 	