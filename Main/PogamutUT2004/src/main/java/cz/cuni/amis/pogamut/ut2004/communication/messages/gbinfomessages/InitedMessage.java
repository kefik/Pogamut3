package cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages;
 		
 		// --- IMPORTS FROM /messages/settings/javasettings/javaimport BEGIN
			import java.util.*;import javax.vecmath.*;import cz.cuni.amis.pogamut.base.communication.messages.*;import cz.cuni.amis.pogamut.base.communication.worldview.*;import cz.cuni.amis.pogamut.base.communication.worldview.event.*;import cz.cuni.amis.pogamut.base.communication.worldview.object.*;import cz.cuni.amis.pogamut.multi.communication.worldview.object.*;import cz.cuni.amis.pogamut.base.communication.translator.event.*;import cz.cuni.amis.pogamut.multi.communication.translator.event.*;import cz.cuni.amis.pogamut.base3d.worldview.object.*;import cz.cuni.amis.pogamut.base3d.worldview.object.event.*;import cz.cuni.amis.pogamut.ut2004.communication.messages.*;import cz.cuni.amis.pogamut.ut2004.communication.worldview.objects.*;import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.objects.*;import cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor.*;import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType.Category;import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;import cz.cuni.amis.utils.exception.*;import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdateResult.Result;import cz.cuni.amis.utils.SafeEquals;import cz.cuni.amis.pogamut.base.agent.*;import cz.cuni.amis.pogamut.multi.agent.*;import cz.cuni.amis.pogamut.multi.communication.worldview.property.*;import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.property.*;import cz.cuni.amis.utils.token.*;import cz.cuni.amis.utils.*;
		// --- IMPORTS FROM /messages/settings/javasettings/javaimport END
		
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name='all'] BEGIN
				
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name='all'] END
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name=base]+classtype[@name=abstract] BEGIN
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name=base]+classtype[@name=abstract] END
    
 		/**
         *  
            				Abstract definition of the GameBots2004 message INITED.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Asynchronous message. Sent after succesfull init command (so usually just once). 
		Holds many attributes of the bots like speed, id starting and max health, etc.
		Some attributes are not used due to GameBots mechanics.
	
         */
 	public abstract class InitedMessage   
  				extends 
  				InfoMessage
  						implements IWorldEvent, IWorldChangeEvent, ICompositeWorldObject
  						
	    {
 	
    	/** Example how the message looks like - used during parser tests. */
    	public static final String PROTOTYPE =
    		"INITED {BotId unreal_id}  {HealthStart 0}  {HealthFull 0}  {HealthMax 0}  {AdrenalineStart 0}  {AdrenalineMax 0}  {ShieldStrengthStart 0}  {ShieldStrengthMax 0}  {MaxMultiJump 0}  {DamageScaling 0}  {GroundSpeed 0}  {WaterSpeed 0}  {AirSpeed 0}  {LadderSpeed 0}  {AccelRate 0}  {JumpZ 0}  {MultiJumpBoost 0}  {MaxFallSpeed 0}  {DodgeSpeedFactor 0}  {DodgeSpeedZ 0}  {AirControl 0} ";
    
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public InitedMessage()
		{
		}
	
				// abstract message, it does not have any more constructors				
			
						
						public static final UnrealId InitedMessageId = UnrealId.get("InitedMessageId");
					
						
						public UnrealId getId() {						
							return cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage.InitedMessageId;
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
			A unique unreal Id of the new bot.
		 
         */
        public abstract UnrealId getBotId()
 	;
		    			
 		/**
         * 
			Bot will always start with this health amount (usually 100). 
		 
         */
        public abstract int getHealthStart()
 	;
		    			
 		/**
         * 
			Full health of the bot (usually 100).
		 
         */
        public abstract int getHealthFull()
 	;
		    			
 		/**
         * 
			Maximum health of the bot (default 199).
		 
         */
        public abstract int getHealthMax()
 	;
		    			
 		/**
         * 
			Amount of adrenaline at the start. Usually 0.
		 
         */
        public abstract double getAdrenalineStart()
 	;
		    			
 		/**
         * 
			Maxium amount of the adrenaline. Usually 100 (this can trigger the combos).
		 
         */
        public abstract double getAdrenalineMax()
 	;
		    			
 		/**
         * 
			Starting strength of the bot armor (usually 0).
		 
         */
        public abstract int getShieldStrengthStart()
 	;
		    			
 		/**
         * 
			Maximum strength of the bot armor (usually 150).
		 
         */
        public abstract int getShieldStrengthMax()
 	;
		    			
 		/**
         * 
			Maximum amount of succesing jumps. Currently limited to double jump in GB.
		 
         */
        public abstract int getMaxMultiJump()
 	;
		    			
 		/**
         * 
			Damage scaling for this bot. (he will deal reduced damage depending on the setting).
		 
         */
        public abstract double getDamageScaling()
 	;
		    			
 		/**
         * 
			Groundspeed of the bot (on the ground). Default 440.
		 
         */
        public abstract double getGroundSpeed()
 	;
		    			
 		/**
         * 
			Waterspeed of the bot (in the water).
		 
         */
        public abstract double getWaterSpeed()
 	;
		    			
 		/**
         * 
			AirSpeed of the bot (in the air).
		 
         */
        public abstract double getAirSpeed()
 	;
		    			
 		/**
         * 
			Ladderspeed of the bot (on the ladder).
		 
         */
        public abstract double getLadderSpeed()
 	;
		    			
 		/**
         * 
			Accelartion rate of this bot. How fast he accelerates.
		 
         */
        public abstract double getAccelRate()
 	;
		    			
 		/**
         * 
			 Bot Jump's Z boost.
		 
         */
        public abstract double getJumpZ()
 	;
		    			
 		/**
         * 
			Not used in GB.
		 
         */
        public abstract double getMultiJumpBoost()
 	;
		    			
 		/**
         * 
			 Max fall speed of the bot.
		 
         */
        public abstract double getMaxFallSpeed()
 	;
		    			
 		/**
         * 
			Dodge speed factor.
		 
         */
        public abstract double getDodgeSpeedFactor()
 	;
		    			
 		/**
         * 
			Dodge jump Z boost of the bot. 
		 
         */
        public abstract double getDodgeSpeedZ()
 	;
		    			
 		/**
         * 
			How well can be the bot controlled in the air (ranges from 0 to 1).
		 
         */
        public abstract double getAirControl()
 	;
		    			
    	
    	public static class InitedMessageUpdate
     extends GBObjectUpdate implements ICompositeWorldObjectUpdatedEvent, IGBWorldObjectEvent {	
			private InitedMessage object;
			private long time;
			private ITeamId teamId;
			
			public InitedMessageUpdate
    (InitedMessage source, long eventTime, ITeamId teamId) {
				this.object = source;
				this.time = eventTime;
				this.teamId = teamId;
			}
			
			/**
			 * Simulation time in MILLI SECONDS !!!
			 */ 
			@Override
			public long getSimTime() {
				return time;
			}
	
			@Override
			public IWorldObject getObject() {
				return object;
			}
	
			@Override
			public WorldObjectId getId() {
				return object.getId();
			}
	
			@Override
			public ILocalWorldObjectUpdatedEvent getLocalEvent() {
				return new InitedMessageLocalImpl.InitedMessageLocalUpdate
    ((InitedMessageLocal)object.getLocal(), time);
			}
	
			@Override
			public ISharedWorldObjectUpdatedEvent getSharedEvent() {
				return new InitedMessageSharedImpl.InitedMessageSharedUpdate
    ((InitedMessageShared)object.getShared(), time, teamId);
			}
	
			@Override
			public IStaticWorldObjectUpdatedEvent getStaticEvent() {
				return new InitedMessageStaticImpl.InitedMessageStaticUpdate
    ((InitedMessageStatic)object.getStatic(), time);
			}
			
		}
    
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
		              			"BotId = " + String.valueOf(getBotId()
 	) + " | " + 
		              		
		              			"HealthStart = " + String.valueOf(getHealthStart()
 	) + " | " + 
		              		
		              			"HealthFull = " + String.valueOf(getHealthFull()
 	) + " | " + 
		              		
		              			"HealthMax = " + String.valueOf(getHealthMax()
 	) + " | " + 
		              		
		              			"AdrenalineStart = " + String.valueOf(getAdrenalineStart()
 	) + " | " + 
		              		
		              			"AdrenalineMax = " + String.valueOf(getAdrenalineMax()
 	) + " | " + 
		              		
		              			"ShieldStrengthStart = " + String.valueOf(getShieldStrengthStart()
 	) + " | " + 
		              		
		              			"ShieldStrengthMax = " + String.valueOf(getShieldStrengthMax()
 	) + " | " + 
		              		
		              			"MaxMultiJump = " + String.valueOf(getMaxMultiJump()
 	) + " | " + 
		              		
		              			"DamageScaling = " + String.valueOf(getDamageScaling()
 	) + " | " + 
		              		
		              			"GroundSpeed = " + String.valueOf(getGroundSpeed()
 	) + " | " + 
		              		
		              			"WaterSpeed = " + String.valueOf(getWaterSpeed()
 	) + " | " + 
		              		
		              			"AirSpeed = " + String.valueOf(getAirSpeed()
 	) + " | " + 
		              		
		              			"LadderSpeed = " + String.valueOf(getLadderSpeed()
 	) + " | " + 
		              		
		              			"AccelRate = " + String.valueOf(getAccelRate()
 	) + " | " + 
		              		
		              			"JumpZ = " + String.valueOf(getJumpZ()
 	) + " | " + 
		              		
		              			"MultiJumpBoost = " + String.valueOf(getMultiJumpBoost()
 	) + " | " + 
		              		
		              			"MaxFallSpeed = " + String.valueOf(getMaxFallSpeed()
 	) + " | " + 
		              		
		              			"DodgeSpeedFactor = " + String.valueOf(getDodgeSpeedFactor()
 	) + " | " + 
		              		
		              			"DodgeSpeedZ = " + String.valueOf(getDodgeSpeedZ()
 	) + " | " + 
		              		
		              			"AirControl = " + String.valueOf(getAirControl()
 	) + " | " + 
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>BotId</b> = " + String.valueOf(getBotId()
 	) + " <br/> " + 
		              		
		              			"<b>HealthStart</b> = " + String.valueOf(getHealthStart()
 	) + " <br/> " + 
		              		
		              			"<b>HealthFull</b> = " + String.valueOf(getHealthFull()
 	) + " <br/> " + 
		              		
		              			"<b>HealthMax</b> = " + String.valueOf(getHealthMax()
 	) + " <br/> " + 
		              		
		              			"<b>AdrenalineStart</b> = " + String.valueOf(getAdrenalineStart()
 	) + " <br/> " + 
		              		
		              			"<b>AdrenalineMax</b> = " + String.valueOf(getAdrenalineMax()
 	) + " <br/> " + 
		              		
		              			"<b>ShieldStrengthStart</b> = " + String.valueOf(getShieldStrengthStart()
 	) + " <br/> " + 
		              		
		              			"<b>ShieldStrengthMax</b> = " + String.valueOf(getShieldStrengthMax()
 	) + " <br/> " + 
		              		
		              			"<b>MaxMultiJump</b> = " + String.valueOf(getMaxMultiJump()
 	) + " <br/> " + 
		              		
		              			"<b>DamageScaling</b> = " + String.valueOf(getDamageScaling()
 	) + " <br/> " + 
		              		
		              			"<b>GroundSpeed</b> = " + String.valueOf(getGroundSpeed()
 	) + " <br/> " + 
		              		
		              			"<b>WaterSpeed</b> = " + String.valueOf(getWaterSpeed()
 	) + " <br/> " + 
		              		
		              			"<b>AirSpeed</b> = " + String.valueOf(getAirSpeed()
 	) + " <br/> " + 
		              		
		              			"<b>LadderSpeed</b> = " + String.valueOf(getLadderSpeed()
 	) + " <br/> " + 
		              		
		              			"<b>AccelRate</b> = " + String.valueOf(getAccelRate()
 	) + " <br/> " + 
		              		
		              			"<b>JumpZ</b> = " + String.valueOf(getJumpZ()
 	) + " <br/> " + 
		              		
		              			"<b>MultiJumpBoost</b> = " + String.valueOf(getMultiJumpBoost()
 	) + " <br/> " + 
		              		
		              			"<b>MaxFallSpeed</b> = " + String.valueOf(getMaxFallSpeed()
 	) + " <br/> " + 
		              		
		              			"<b>DodgeSpeedFactor</b> = " + String.valueOf(getDodgeSpeedFactor()
 	) + " <br/> " + 
		              		
		              			"<b>DodgeSpeedZ</b> = " + String.valueOf(getDodgeSpeedZ()
 	) + " <br/> " + 
		              		
		              			"<b>AirControl</b> = " + String.valueOf(getAirControl()
 	) + " <br/> " + 
		              		
				"<br/>]";     
		}
 	
 	    public String toJsonLiteral() {
            return "initedmessage( "
            		+
									(getBotId()
 	 == null ? "null" :
										"\"" + getBotId()
 	.getStringId() + "\"" 
									)
								+ ", " + 
								    String.valueOf(getHealthStart()
 	)									
								+ ", " + 
								    String.valueOf(getHealthFull()
 	)									
								+ ", " + 
								    String.valueOf(getHealthMax()
 	)									
								+ ", " + 
								    String.valueOf(getAdrenalineStart()
 	)									
								+ ", " + 
								    String.valueOf(getAdrenalineMax()
 	)									
								+ ", " + 
								    String.valueOf(getShieldStrengthStart()
 	)									
								+ ", " + 
								    String.valueOf(getShieldStrengthMax()
 	)									
								+ ", " + 
								    String.valueOf(getMaxMultiJump()
 	)									
								+ ", " + 
								    String.valueOf(getDamageScaling()
 	)									
								+ ", " + 
								    String.valueOf(getGroundSpeed()
 	)									
								+ ", " + 
								    String.valueOf(getWaterSpeed()
 	)									
								+ ", " + 
								    String.valueOf(getAirSpeed()
 	)									
								+ ", " + 
								    String.valueOf(getLadderSpeed()
 	)									
								+ ", " + 
								    String.valueOf(getAccelRate()
 	)									
								+ ", " + 
								    String.valueOf(getJumpZ()
 	)									
								+ ", " + 
								    String.valueOf(getMultiJumpBoost()
 	)									
								+ ", " + 
								    String.valueOf(getMaxFallSpeed()
 	)									
								+ ", " + 
								    String.valueOf(getDodgeSpeedFactor()
 	)									
								+ ", " + 
								    String.valueOf(getDodgeSpeedZ()
 	)									
								+ ", " + 
								    String.valueOf(getAirControl()
 	)									
								
                   + ")";
        }
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=base+classtype[@name=abstract]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=base+classtype[@name=abstract]) ---        	            	
 	
		}
 	