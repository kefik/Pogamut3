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
         			Definition of the event DIE.
         		
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Asynchronous message. This bot has died.
	
         */
 	public class BotKilled 
  				extends InfoMessage
    			implements IWorldEvent, IWorldChangeEvent
    			
	    {
 	
    	/** Example how the message looks like - used during parser tests. */
    	public static final String PROTOTYPE =
    		"DIE {Killer unreal_id}  {DamageType text}  {DeathString text}  {WeaponName text}  {Flaming False}  {CausedByWorld False}  {DirectDamage False}  {BulletHit False}  {VehicleHit False} ";
    
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public BotKilled()
		{
		}
	
    	
    	
    	
    	/**
		 * Creates new instance of the message BotKilled.
		 * 
		Asynchronous message. This bot has died.
	
		 * Corresponding GameBots message
		 *   
		 *   is
		 *   DIE.
		 * 
 	  	 * 
		 *   
		 *     @param Killer 
			Unique Id of player that killed our bot if any (may have
			walked off a ledge).
		
		 *   
		 * 
		 *   
		 *     @param DamageType 
			A string describing what kind of damage killed the bot.
		
		 *   
		 * 
		 *   
		 *     @param DeathString 
			String describing this type of death.
		
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
		 *     @param CausedByWorld 
			If this damage was caused by world - falling into lava, or falling down.
		
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
			If this damage was caused by vehicle running over us.
		
		 *   
		 * 
		 */
		public BotKilled(
			UnrealId Killer,  String DamageType,  String DeathString,  String WeaponName,  boolean Flaming,  boolean CausedByWorld,  boolean DirectDamage,  boolean BulletHit,  boolean VehicleHit
		) {
			
					this.Killer = Killer;
				
					this.DamageType = DamageType;
				
					this.DeathString = DeathString;
				
					this.WeaponName = WeaponName;
				
					this.Flaming = Flaming;
				
					this.CausedByWorld = CausedByWorld;
				
					this.DirectDamage = DirectDamage;
				
					this.BulletHit = BulletHit;
				
					this.VehicleHit = VehicleHit;
				
		}
    
    	/**
		 * Cloning constructor from the full message.
		 *
		 * @param original
		 */
		public BotKilled(BotKilled original) {		
			
					this.Killer = original.getKiller()
 	;
				
					this.DamageType = original.getDamageType()
 	;
				
					this.DeathString = original.getDeathString()
 	;
				
					this.WeaponName = original.getWeaponName()
 	;
				
					this.Flaming = original.isFlaming()
 	;
				
					this.CausedByWorld = original.isCausedByWorld()
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
         * 
			Unique Id of player that killed our bot if any (may have
			walked off a ledge).
		 
         */
        protected
         UnrealId Killer =
       	null;
	
 		/**
         * 
			Unique Id of player that killed our bot if any (may have
			walked off a ledge).
		 
         */
        public  UnrealId getKiller()
 	 {
    					return Killer;
    				}
    			
    	
	    /**
         * 
			A string describing what kind of damage killed the bot.
		 
         */
        protected
         String DamageType =
       	null;
	
 		/**
         * 
			A string describing what kind of damage killed the bot.
		 
         */
        public  String getDamageType()
 	 {
    					return DamageType;
    				}
    			
    	
	    /**
         * 
			String describing this type of death.
		 
         */
        protected
         String DeathString =
       	null;
	
 		/**
         * 
			String describing this type of death.
		 
         */
        public  String getDeathString()
 	 {
    					return DeathString;
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
			If this damage was caused by world - falling into lava, or falling down.
		 
         */
        protected
         boolean CausedByWorld =
       	false;
	
 		/**
         * 
			If this damage was caused by world - falling into lava, or falling down.
		 
         */
        public  boolean isCausedByWorld()
 	 {
    					return CausedByWorld;
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
			If this damage was caused by vehicle running over us.
		 
         */
        protected
         boolean VehicleHit =
       	false;
	
 		/**
         * 
			If this damage was caused by vehicle running over us.
		 
         */
        public  boolean isVehicleHit()
 	 {
    					return VehicleHit;
    				}
    			
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
		              			"Killer = " + String.valueOf(getKiller()
 	) + " | " + 
		              		
		              			"DamageType = " + String.valueOf(getDamageType()
 	) + " | " + 
		              		
		              			"DeathString = " + String.valueOf(getDeathString()
 	) + " | " + 
		              		
		              			"WeaponName = " + String.valueOf(getWeaponName()
 	) + " | " + 
		              		
		              			"Flaming = " + String.valueOf(isFlaming()
 	) + " | " + 
		              		
		              			"CausedByWorld = " + String.valueOf(isCausedByWorld()
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
            	
		              			"<b>Killer</b> = " + String.valueOf(getKiller()
 	) + " <br/> " + 
		              		
		              			"<b>DamageType</b> = " + String.valueOf(getDamageType()
 	) + " <br/> " + 
		              		
		              			"<b>DeathString</b> = " + String.valueOf(getDeathString()
 	) + " <br/> " + 
		              		
		              			"<b>WeaponName</b> = " + String.valueOf(getWeaponName()
 	) + " <br/> " + 
		              		
		              			"<b>Flaming</b> = " + String.valueOf(isFlaming()
 	) + " <br/> " + 
		              		
		              			"<b>CausedByWorld</b> = " + String.valueOf(isCausedByWorld()
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
            return "botkilled( "
            		+
									(getKiller()
 	 == null ? "null" :
										"\"" + getKiller()
 	.getStringId() + "\"" 
									)
								+ ", " + 
									(getDamageType()
 	 == null ? "null" :
										"\"" + getDamageType()
 	 + "\"" 
									)
								+ ", " + 
									(getDeathString()
 	 == null ? "null" :
										"\"" + getDeathString()
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
								    String.valueOf(isCausedByWorld()
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
 	