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
         			Definition of the event VCH.
         		
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Asynchronous message. Some part of the bot body changed the
		zone.
	
         */
 	public class VolumeChanged 
  				extends InfoMessage
    			implements IWorldEvent, IWorldChangeEvent
    			
	    {
 	
    	/** Example how the message looks like - used during parser tests. */
    	public static final String PROTOTYPE =
    		"VCH {Id text}  {ZoneVelocity 0,0,0}  {ZoneGravity 0,0,0}  {GroundFriction 0}  {FluidFriction 0}  {TerminalVelocity 0}  {WaterVolume False}  {PainCausing False}  {Destructive False}  {DamagePerSec 0}  {DamageType text}  {NoInventory False}  {MoveProjectiles False}  {NeutralZone False} ";
    
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public VolumeChanged()
		{
		}
	
    	
    	
    	
    	/**
		 * Creates new instance of the message VolumeChanged.
		 * 
		Asynchronous message. Some part of the bot body changed the
		zone.
	
		 * Corresponding GameBots message
		 *   
		 *   is
		 *   VCH.
		 * 
 	  	 * 
		 *   
		 *     @param Id Id of the zone entered.
		 *   
		 * 
		 *   
		 *     @param ZoneVelocity 
			Zone velocity (?).
		
		 *   
		 * 
		 *   
		 *     @param ZoneGravity 
			Gravity in this zone.
		
		 *   
		 * 
		 *   
		 *     @param GroundFriction 
			Friction of the floor.
		
		 *   
		 * 
		 *   
		 *     @param FluidFriction 
			Friction of the fluid.
		
		 *   
		 * 
		 *   
		 *     @param TerminalVelocity 
			Terminal velocity (?).
		
		 *   
		 * 
		 *   
		 *     @param WaterVolume 
			If this zone is a water.
		
		 *   
		 * 
		 *   
		 *     @param PainCausing 
			True or false if we get some damage when we stay in this
			zone.
		
		 *   
		 * 
		 *   
		 *     @param Destructive 
			If this zone kills most of the actors instantly.
		
		 *   
		 * 
		 *   
		 *     @param DamagePerSec 
			Amount of damage we will suffer per second if we stay in this zone.
		
		 *   
		 * 
		 *   
		 *     @param DamageType 
			Type of the damage in this zone.
		
		 *   
		 * 
		 *   
		 *     @param NoInventory 
			If the inventory is allowed here.
		
		 *   
		 * 
		 *   
		 *     @param MoveProjectiles 
			If this velocity zone should impart velocity to projectiles and effects.
		
		 *   
		 * 
		 *   
		 *     @param NeutralZone 
			Players can't take damage in this zone.
		
		 *   
		 * 
		 */
		public VolumeChanged(
			String Id,  Velocity ZoneVelocity,  Velocity ZoneGravity,  double GroundFriction,  double FluidFriction,  double TerminalVelocity,  boolean WaterVolume,  boolean PainCausing,  boolean Destructive,  double DamagePerSec,  String DamageType,  boolean NoInventory,  boolean MoveProjectiles,  boolean NeutralZone
		) {
			
					this.Id = Id;
				
					this.ZoneVelocity = ZoneVelocity;
				
					this.ZoneGravity = ZoneGravity;
				
					this.GroundFriction = GroundFriction;
				
					this.FluidFriction = FluidFriction;
				
					this.TerminalVelocity = TerminalVelocity;
				
					this.WaterVolume = WaterVolume;
				
					this.PainCausing = PainCausing;
				
					this.Destructive = Destructive;
				
					this.DamagePerSec = DamagePerSec;
				
					this.DamageType = DamageType;
				
					this.NoInventory = NoInventory;
				
					this.MoveProjectiles = MoveProjectiles;
				
					this.NeutralZone = NeutralZone;
				
		}
    
    	/**
		 * Cloning constructor from the full message.
		 *
		 * @param original
		 */
		public VolumeChanged(VolumeChanged original) {		
			
					this.Id = original.getId()
 	;
				
					this.ZoneVelocity = original.getZoneVelocity()
 	;
				
					this.ZoneGravity = original.getZoneGravity()
 	;
				
					this.GroundFriction = original.getGroundFriction()
 	;
				
					this.FluidFriction = original.getFluidFriction()
 	;
				
					this.TerminalVelocity = original.getTerminalVelocity()
 	;
				
					this.WaterVolume = original.isWaterVolume()
 	;
				
					this.PainCausing = original.isPainCausing()
 	;
				
					this.Destructive = original.isDestructive()
 	;
				
					this.DamagePerSec = original.getDamagePerSec()
 	;
				
					this.DamageType = original.getDamageType()
 	;
				
					this.NoInventory = original.isNoInventory()
 	;
				
					this.MoveProjectiles = original.isMoveProjectiles()
 	;
				
					this.NeutralZone = original.isNeutralZone()
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
         * Id of the zone entered. 
         */
        protected
         String Id =
       	null;
	
 		/**
         * Id of the zone entered. 
         */
        public  String getId()
 	 {
    					return Id;
    				}
    			
    	
	    /**
         * 
			Zone velocity (?).
		 
         */
        protected
         Velocity ZoneVelocity =
       	null;
	
 		/**
         * 
			Zone velocity (?).
		 
         */
        public  Velocity getZoneVelocity()
 	 {
    					return ZoneVelocity;
    				}
    			
    	
	    /**
         * 
			Gravity in this zone.
		 
         */
        protected
         Velocity ZoneGravity =
       	null;
	
 		/**
         * 
			Gravity in this zone.
		 
         */
        public  Velocity getZoneGravity()
 	 {
    					return ZoneGravity;
    				}
    			
    	
	    /**
         * 
			Friction of the floor.
		 
         */
        protected
         double GroundFriction =
       	0;
	
 		/**
         * 
			Friction of the floor.
		 
         */
        public  double getGroundFriction()
 	 {
    					return GroundFriction;
    				}
    			
    	
	    /**
         * 
			Friction of the fluid.
		 
         */
        protected
         double FluidFriction =
       	0;
	
 		/**
         * 
			Friction of the fluid.
		 
         */
        public  double getFluidFriction()
 	 {
    					return FluidFriction;
    				}
    			
    	
	    /**
         * 
			Terminal velocity (?).
		 
         */
        protected
         double TerminalVelocity =
       	0;
	
 		/**
         * 
			Terminal velocity (?).
		 
         */
        public  double getTerminalVelocity()
 	 {
    					return TerminalVelocity;
    				}
    			
    	
	    /**
         * 
			If this zone is a water.
		 
         */
        protected
         boolean WaterVolume =
       	false;
	
 		/**
         * 
			If this zone is a water.
		 
         */
        public  boolean isWaterVolume()
 	 {
    					return WaterVolume;
    				}
    			
    	
	    /**
         * 
			True or false if we get some damage when we stay in this
			zone.
		 
         */
        protected
         boolean PainCausing =
       	false;
	
 		/**
         * 
			True or false if we get some damage when we stay in this
			zone.
		 
         */
        public  boolean isPainCausing()
 	 {
    					return PainCausing;
    				}
    			
    	
	    /**
         * 
			If this zone kills most of the actors instantly.
		 
         */
        protected
         boolean Destructive =
       	false;
	
 		/**
         * 
			If this zone kills most of the actors instantly.
		 
         */
        public  boolean isDestructive()
 	 {
    					return Destructive;
    				}
    			
    	
	    /**
         * 
			Amount of damage we will suffer per second if we stay in this zone.
		 
         */
        protected
         double DamagePerSec =
       	0;
	
 		/**
         * 
			Amount of damage we will suffer per second if we stay in this zone.
		 
         */
        public  double getDamagePerSec()
 	 {
    					return DamagePerSec;
    				}
    			
    	
	    /**
         * 
			Type of the damage in this zone.
		 
         */
        protected
         String DamageType =
       	null;
	
 		/**
         * 
			Type of the damage in this zone.
		 
         */
        public  String getDamageType()
 	 {
    					return DamageType;
    				}
    			
    	
	    /**
         * 
			If the inventory is allowed here.
		 
         */
        protected
         boolean NoInventory =
       	false;
	
 		/**
         * 
			If the inventory is allowed here.
		 
         */
        public  boolean isNoInventory()
 	 {
    					return NoInventory;
    				}
    			
    	
	    /**
         * 
			If this velocity zone should impart velocity to projectiles and effects.
		 
         */
        protected
         boolean MoveProjectiles =
       	false;
	
 		/**
         * 
			If this velocity zone should impart velocity to projectiles and effects.
		 
         */
        public  boolean isMoveProjectiles()
 	 {
    					return MoveProjectiles;
    				}
    			
    	
	    /**
         * 
			Players can't take damage in this zone.
		 
         */
        protected
         boolean NeutralZone =
       	false;
	
 		/**
         * 
			Players can't take damage in this zone.
		 
         */
        public  boolean isNeutralZone()
 	 {
    					return NeutralZone;
    				}
    			
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
		              			"Id = " + String.valueOf(getId()
 	) + " | " + 
		              		
		              			"ZoneVelocity = " + String.valueOf(getZoneVelocity()
 	) + " | " + 
		              		
		              			"ZoneGravity = " + String.valueOf(getZoneGravity()
 	) + " | " + 
		              		
		              			"GroundFriction = " + String.valueOf(getGroundFriction()
 	) + " | " + 
		              		
		              			"FluidFriction = " + String.valueOf(getFluidFriction()
 	) + " | " + 
		              		
		              			"TerminalVelocity = " + String.valueOf(getTerminalVelocity()
 	) + " | " + 
		              		
		              			"WaterVolume = " + String.valueOf(isWaterVolume()
 	) + " | " + 
		              		
		              			"PainCausing = " + String.valueOf(isPainCausing()
 	) + " | " + 
		              		
		              			"Destructive = " + String.valueOf(isDestructive()
 	) + " | " + 
		              		
		              			"DamagePerSec = " + String.valueOf(getDamagePerSec()
 	) + " | " + 
		              		
		              			"DamageType = " + String.valueOf(getDamageType()
 	) + " | " + 
		              		
		              			"NoInventory = " + String.valueOf(isNoInventory()
 	) + " | " + 
		              		
		              			"MoveProjectiles = " + String.valueOf(isMoveProjectiles()
 	) + " | " + 
		              		
		              			"NeutralZone = " + String.valueOf(isNeutralZone()
 	) + " | " + 
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>Id</b> = " + String.valueOf(getId()
 	) + " <br/> " + 
		              		
		              			"<b>ZoneVelocity</b> = " + String.valueOf(getZoneVelocity()
 	) + " <br/> " + 
		              		
		              			"<b>ZoneGravity</b> = " + String.valueOf(getZoneGravity()
 	) + " <br/> " + 
		              		
		              			"<b>GroundFriction</b> = " + String.valueOf(getGroundFriction()
 	) + " <br/> " + 
		              		
		              			"<b>FluidFriction</b> = " + String.valueOf(getFluidFriction()
 	) + " <br/> " + 
		              		
		              			"<b>TerminalVelocity</b> = " + String.valueOf(getTerminalVelocity()
 	) + " <br/> " + 
		              		
		              			"<b>WaterVolume</b> = " + String.valueOf(isWaterVolume()
 	) + " <br/> " + 
		              		
		              			"<b>PainCausing</b> = " + String.valueOf(isPainCausing()
 	) + " <br/> " + 
		              		
		              			"<b>Destructive</b> = " + String.valueOf(isDestructive()
 	) + " <br/> " + 
		              		
		              			"<b>DamagePerSec</b> = " + String.valueOf(getDamagePerSec()
 	) + " <br/> " + 
		              		
		              			"<b>DamageType</b> = " + String.valueOf(getDamageType()
 	) + " <br/> " + 
		              		
		              			"<b>NoInventory</b> = " + String.valueOf(isNoInventory()
 	) + " <br/> " + 
		              		
		              			"<b>MoveProjectiles</b> = " + String.valueOf(isMoveProjectiles()
 	) + " <br/> " + 
		              		
		              			"<b>NeutralZone</b> = " + String.valueOf(isNeutralZone()
 	) + " <br/> " + 
		              		
				"<br/>]";     
		}
 	
 	    public String toJsonLiteral() {
            return "volumechanged( "
            		+
									(getId()
 	 == null ? "null" :
										"\"" + getId()
 	 + "\"" 
									)
								+ ", " + 
								    (getZoneVelocity()
 	 == null ? "null" :
										"[" + getZoneVelocity()
 	.getX() + ", " + getZoneVelocity()
 	.getY() + ", " + getZoneVelocity()
 	.getZ() + "]" 
									)
								+ ", " + 
								    (getZoneGravity()
 	 == null ? "null" :
										"[" + getZoneGravity()
 	.getX() + ", " + getZoneGravity()
 	.getY() + ", " + getZoneGravity()
 	.getZ() + "]" 
									)
								+ ", " + 
								    String.valueOf(getGroundFriction()
 	)									
								+ ", " + 
								    String.valueOf(getFluidFriction()
 	)									
								+ ", " + 
								    String.valueOf(getTerminalVelocity()
 	)									
								+ ", " + 
								    String.valueOf(isWaterVolume()
 	)									
								+ ", " + 
								    String.valueOf(isPainCausing()
 	)									
								+ ", " + 
								    String.valueOf(isDestructive()
 	)									
								+ ", " + 
								    String.valueOf(getDamagePerSec()
 	)									
								+ ", " + 
									(getDamageType()
 	 == null ? "null" :
										"\"" + getDamageType()
 	 + "\"" 
									)
								+ ", " + 
								    String.valueOf(isNoInventory()
 	)									
								+ ", " + 
								    String.valueOf(isMoveProjectiles()
 	)									
								+ ", " + 
								    String.valueOf(isNeutralZone()
 	)									
								
                   + ")";
        }
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=event+classtype[@name=impl]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=event+classtype[@name=impl]) ---        	            	
 	
		}
 	