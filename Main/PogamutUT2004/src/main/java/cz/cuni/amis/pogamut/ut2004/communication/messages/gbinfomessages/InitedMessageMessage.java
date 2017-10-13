package cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages;
 		
 		// --- IMPORTS FROM /messages/settings/javasettings/javaimport BEGIN
			import java.util.*;import javax.vecmath.*;import cz.cuni.amis.pogamut.base.communication.messages.*;import cz.cuni.amis.pogamut.base.communication.worldview.*;import cz.cuni.amis.pogamut.base.communication.worldview.event.*;import cz.cuni.amis.pogamut.base.communication.worldview.object.*;import cz.cuni.amis.pogamut.multi.communication.worldview.object.*;import cz.cuni.amis.pogamut.base.communication.translator.event.*;import cz.cuni.amis.pogamut.multi.communication.translator.event.*;import cz.cuni.amis.pogamut.base3d.worldview.object.*;import cz.cuni.amis.pogamut.base3d.worldview.object.event.*;import cz.cuni.amis.pogamut.ut2004.communication.messages.*;import cz.cuni.amis.pogamut.ut2004.communication.worldview.objects.*;import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.objects.*;import cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor.*;import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType.Category;import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;import cz.cuni.amis.utils.exception.*;import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdateResult.Result;import cz.cuni.amis.utils.SafeEquals;import cz.cuni.amis.pogamut.base.agent.*;import cz.cuni.amis.pogamut.multi.agent.*;import cz.cuni.amis.pogamut.multi.communication.worldview.property.*;import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.property.*;import cz.cuni.amis.utils.token.*;import cz.cuni.amis.utils.*;
		// --- IMPORTS FROM /messages/settings/javasettings/javaimport END
		
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name='all'] BEGIN
				
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name='all'] END
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name=base]+classtype[@name=message] BEGIN
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name=base]+classtype[@name=message] END
    
 		/**
         *  
             				Implementation of the GameBots2004 message INITED contains also its Local/Shared/Static subpart class definitions..  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Asynchronous message. Sent after succesfull init command (so usually just once). 
		Holds many attributes of the bots like speed, id starting and max health, etc.
		Some attributes are not used due to GameBots mechanics.
	
         */
 	public class InitedMessageMessage   
  				extends 
  				InitedMessage
  						implements IWorldObjectUpdatedEvent, ICompositeWorldObjectUpdatedEvent
  						
	    {
 	
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public InitedMessageMessage()
		{
		}
	
    	
    	
    	
    	/**
		 * Creates new instance of the message InitedMessage.
		 * 
		Asynchronous message. Sent after succesfull init command (so usually just once). 
		Holds many attributes of the bots like speed, id starting and max health, etc.
		Some attributes are not used due to GameBots mechanics.
	
		 * Corresponding GameBots message
		 *   
		 *   is
		 *   INITED.
		 * 
 	  	 * 
		 *   
		 *     @param BotId 
			A unique unreal Id of the new bot.
		
		 *   
		 * 
		 *   
		 *     @param HealthStart 
			Bot will always start with this health amount (usually 100). 
		
		 *   
		 * 
		 *   
		 *     @param HealthFull 
			Full health of the bot (usually 100).
		
		 *   
		 * 
		 *   
		 *     @param HealthMax 
			Maximum health of the bot (default 199).
		
		 *   
		 * 
		 *   
		 *     @param AdrenalineStart 
			Amount of adrenaline at the start. Usually 0.
		
		 *   
		 * 
		 *   
		 *     @param AdrenalineMax 
			Maxium amount of the adrenaline. Usually 100 (this can trigger the combos).
		
		 *   
		 * 
		 *   
		 *     @param ShieldStrengthStart 
			Starting strength of the bot armor (usually 0).
		
		 *   
		 * 
		 *   
		 *     @param ShieldStrengthMax 
			Maximum strength of the bot armor (usually 150).
		
		 *   
		 * 
		 *   
		 *     @param MaxMultiJump 
			Maximum amount of succesing jumps. Currently limited to double jump in GB.
		
		 *   
		 * 
		 *   
		 *     @param DamageScaling 
			Damage scaling for this bot. (he will deal reduced damage depending on the setting).
		
		 *   
		 * 
		 *   
		 *     @param GroundSpeed 
			Groundspeed of the bot (on the ground). Default 440.
		
		 *   
		 * 
		 *   
		 *     @param WaterSpeed 
			Waterspeed of the bot (in the water).
		
		 *   
		 * 
		 *   
		 *     @param AirSpeed 
			AirSpeed of the bot (in the air).
		
		 *   
		 * 
		 *   
		 *     @param LadderSpeed 
			Ladderspeed of the bot (on the ladder).
		
		 *   
		 * 
		 *   
		 *     @param AccelRate 
			Accelartion rate of this bot. How fast he accelerates.
		
		 *   
		 * 
		 *   
		 *     @param JumpZ 
			 Bot Jump's Z boost.
		
		 *   
		 * 
		 *   
		 *     @param MultiJumpBoost 
			Not used in GB.
		
		 *   
		 * 
		 *   
		 *     @param MaxFallSpeed 
			 Max fall speed of the bot.
		
		 *   
		 * 
		 *   
		 *     @param DodgeSpeedFactor 
			Dodge speed factor.
		
		 *   
		 * 
		 *   
		 *     @param DodgeSpeedZ 
			Dodge jump Z boost of the bot. 
		
		 *   
		 * 
		 *   
		 *     @param AirControl 
			How well can be the bot controlled in the air (ranges from 0 to 1).
		
		 *   
		 * 
		 */
		public InitedMessageMessage(
			UnrealId BotId,  int HealthStart,  int HealthFull,  int HealthMax,  double AdrenalineStart,  double AdrenalineMax,  int ShieldStrengthStart,  int ShieldStrengthMax,  int MaxMultiJump,  double DamageScaling,  double GroundSpeed,  double WaterSpeed,  double AirSpeed,  double LadderSpeed,  double AccelRate,  double JumpZ,  double MultiJumpBoost,  double MaxFallSpeed,  double DodgeSpeedFactor,  double DodgeSpeedZ,  double AirControl
		) {
			
					this.BotId = BotId;
				
					this.HealthStart = HealthStart;
				
					this.HealthFull = HealthFull;
				
					this.HealthMax = HealthMax;
				
					this.AdrenalineStart = AdrenalineStart;
				
					this.AdrenalineMax = AdrenalineMax;
				
					this.ShieldStrengthStart = ShieldStrengthStart;
				
					this.ShieldStrengthMax = ShieldStrengthMax;
				
					this.MaxMultiJump = MaxMultiJump;
				
					this.DamageScaling = DamageScaling;
				
					this.GroundSpeed = GroundSpeed;
				
					this.WaterSpeed = WaterSpeed;
				
					this.AirSpeed = AirSpeed;
				
					this.LadderSpeed = LadderSpeed;
				
					this.AccelRate = AccelRate;
				
					this.JumpZ = JumpZ;
				
					this.MultiJumpBoost = MultiJumpBoost;
				
					this.MaxFallSpeed = MaxFallSpeed;
				
					this.DodgeSpeedFactor = DodgeSpeedFactor;
				
					this.DodgeSpeedZ = DodgeSpeedZ;
				
					this.AirControl = AirControl;
				
		}
    
		/**
		 * Cloning constructor from the full message.
		 *
		 * @param original
		 */
		public InitedMessageMessage(InitedMessageMessage original) {		
			
					this.BotId = original.getBotId()
 	;
				
					this.HealthStart = original.getHealthStart()
 	;
				
					this.HealthFull = original.getHealthFull()
 	;
				
					this.HealthMax = original.getHealthMax()
 	;
				
					this.AdrenalineStart = original.getAdrenalineStart()
 	;
				
					this.AdrenalineMax = original.getAdrenalineMax()
 	;
				
					this.ShieldStrengthStart = original.getShieldStrengthStart()
 	;
				
					this.ShieldStrengthMax = original.getShieldStrengthMax()
 	;
				
					this.MaxMultiJump = original.getMaxMultiJump()
 	;
				
					this.DamageScaling = original.getDamageScaling()
 	;
				
					this.GroundSpeed = original.getGroundSpeed()
 	;
				
					this.WaterSpeed = original.getWaterSpeed()
 	;
				
					this.AirSpeed = original.getAirSpeed()
 	;
				
					this.LadderSpeed = original.getLadderSpeed()
 	;
				
					this.AccelRate = original.getAccelRate()
 	;
				
					this.JumpZ = original.getJumpZ()
 	;
				
					this.MultiJumpBoost = original.getMultiJumpBoost()
 	;
				
					this.MaxFallSpeed = original.getMaxFallSpeed()
 	;
				
					this.DodgeSpeedFactor = original.getDodgeSpeedFactor()
 	;
				
					this.DodgeSpeedZ = original.getDodgeSpeedZ()
 	;
				
					this.AirControl = original.getAirControl()
 	;
				
				this.TeamId = original.getTeamId();
			
			this.SimTime = original.getSimTime();
		}
		
						
						public UnrealId getId() {						
							return cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage.InitedMessageId;
						}
					
    				
    					protected ITeamId TeamId;
    					
    					/**
    					 * Used by Yylex to slip corretn TeamId.
    					 */
    					protected void setTeamId(ITeamId TeamId) {
    					    this.TeamId = TeamId;
    					}
    				
    					public ITeamId getTeamId() {
							return TeamId;
						}
    	
    					
    					
    	
	    /**
         * 
			A unique unreal Id of the new bot.
		 
         */
        protected
         UnrealId BotId =
       	null;
	
    						
    						/**
		 					 * Whether property 'BotId' was received from GB2004.
		 					 */
							protected boolean BotId_Set = false;
							
    						@Override
		    				
 		/**
         * 
			A unique unreal Id of the new bot.
		 
         */
        public  UnrealId getBotId()
 	 {
		    					return BotId;
		    				}
		    			
    	
	    /**
         * 
			Bot will always start with this health amount (usually 100). 
		 
         */
        protected
         int HealthStart =
       	0;
	
    						
    						/**
		 					 * Whether property 'HealthStart' was received from GB2004.
		 					 */
							protected boolean HealthStart_Set = false;
							
    						@Override
		    				
 		/**
         * 
			Bot will always start with this health amount (usually 100). 
		 
         */
        public  int getHealthStart()
 	 {
		    					return HealthStart;
		    				}
		    			
    	
	    /**
         * 
			Full health of the bot (usually 100).
		 
         */
        protected
         int HealthFull =
       	0;
	
    						
    						/**
		 					 * Whether property 'HealthFull' was received from GB2004.
		 					 */
							protected boolean HealthFull_Set = false;
							
    						@Override
		    				
 		/**
         * 
			Full health of the bot (usually 100).
		 
         */
        public  int getHealthFull()
 	 {
		    					return HealthFull;
		    				}
		    			
    	
	    /**
         * 
			Maximum health of the bot (default 199).
		 
         */
        protected
         int HealthMax =
       	0;
	
    						
    						/**
		 					 * Whether property 'HealthMax' was received from GB2004.
		 					 */
							protected boolean HealthMax_Set = false;
							
    						@Override
		    				
 		/**
         * 
			Maximum health of the bot (default 199).
		 
         */
        public  int getHealthMax()
 	 {
		    					return HealthMax;
		    				}
		    			
    	
	    /**
         * 
			Amount of adrenaline at the start. Usually 0.
		 
         */
        protected
         double AdrenalineStart =
       	0;
	
    						
    						/**
		 					 * Whether property 'AdrenalineStart' was received from GB2004.
		 					 */
							protected boolean AdrenalineStart_Set = false;
							
    						@Override
		    				
 		/**
         * 
			Amount of adrenaline at the start. Usually 0.
		 
         */
        public  double getAdrenalineStart()
 	 {
		    					return AdrenalineStart;
		    				}
		    			
    	
	    /**
         * 
			Maxium amount of the adrenaline. Usually 100 (this can trigger the combos).
		 
         */
        protected
         double AdrenalineMax =
       	0;
	
    						
    						/**
		 					 * Whether property 'AdrenalineMax' was received from GB2004.
		 					 */
							protected boolean AdrenalineMax_Set = false;
							
    						@Override
		    				
 		/**
         * 
			Maxium amount of the adrenaline. Usually 100 (this can trigger the combos).
		 
         */
        public  double getAdrenalineMax()
 	 {
		    					return AdrenalineMax;
		    				}
		    			
    	
	    /**
         * 
			Starting strength of the bot armor (usually 0).
		 
         */
        protected
         int ShieldStrengthStart =
       	0;
	
    						
    						/**
		 					 * Whether property 'ShieldStrengthStart' was received from GB2004.
		 					 */
							protected boolean ShieldStrengthStart_Set = false;
							
    						@Override
		    				
 		/**
         * 
			Starting strength of the bot armor (usually 0).
		 
         */
        public  int getShieldStrengthStart()
 	 {
		    					return ShieldStrengthStart;
		    				}
		    			
    	
	    /**
         * 
			Maximum strength of the bot armor (usually 150).
		 
         */
        protected
         int ShieldStrengthMax =
       	0;
	
    						
    						/**
		 					 * Whether property 'ShieldStrengthMax' was received from GB2004.
		 					 */
							protected boolean ShieldStrengthMax_Set = false;
							
    						@Override
		    				
 		/**
         * 
			Maximum strength of the bot armor (usually 150).
		 
         */
        public  int getShieldStrengthMax()
 	 {
		    					return ShieldStrengthMax;
		    				}
		    			
    	
	    /**
         * 
			Maximum amount of succesing jumps. Currently limited to double jump in GB.
		 
         */
        protected
         int MaxMultiJump =
       	0;
	
    						
    						/**
		 					 * Whether property 'MaxMultiJump' was received from GB2004.
		 					 */
							protected boolean MaxMultiJump_Set = false;
							
    						@Override
		    				
 		/**
         * 
			Maximum amount of succesing jumps. Currently limited to double jump in GB.
		 
         */
        public  int getMaxMultiJump()
 	 {
		    					return MaxMultiJump;
		    				}
		    			
    	
	    /**
         * 
			Damage scaling for this bot. (he will deal reduced damage depending on the setting).
		 
         */
        protected
         double DamageScaling =
       	0;
	
    						
    						/**
		 					 * Whether property 'DamageScaling' was received from GB2004.
		 					 */
							protected boolean DamageScaling_Set = false;
							
    						@Override
		    				
 		/**
         * 
			Damage scaling for this bot. (he will deal reduced damage depending on the setting).
		 
         */
        public  double getDamageScaling()
 	 {
		    					return DamageScaling;
		    				}
		    			
    	
	    /**
         * 
			Groundspeed of the bot (on the ground). Default 440.
		 
         */
        protected
         double GroundSpeed =
       	0;
	
    						
    						/**
		 					 * Whether property 'GroundSpeed' was received from GB2004.
		 					 */
							protected boolean GroundSpeed_Set = false;
							
    						@Override
		    				
 		/**
         * 
			Groundspeed of the bot (on the ground). Default 440.
		 
         */
        public  double getGroundSpeed()
 	 {
		    					return GroundSpeed;
		    				}
		    			
    	
	    /**
         * 
			Waterspeed of the bot (in the water).
		 
         */
        protected
         double WaterSpeed =
       	0;
	
    						
    						/**
		 					 * Whether property 'WaterSpeed' was received from GB2004.
		 					 */
							protected boolean WaterSpeed_Set = false;
							
    						@Override
		    				
 		/**
         * 
			Waterspeed of the bot (in the water).
		 
         */
        public  double getWaterSpeed()
 	 {
		    					return WaterSpeed;
		    				}
		    			
    	
	    /**
         * 
			AirSpeed of the bot (in the air).
		 
         */
        protected
         double AirSpeed =
       	0;
	
    						
    						/**
		 					 * Whether property 'AirSpeed' was received from GB2004.
		 					 */
							protected boolean AirSpeed_Set = false;
							
    						@Override
		    				
 		/**
         * 
			AirSpeed of the bot (in the air).
		 
         */
        public  double getAirSpeed()
 	 {
		    					return AirSpeed;
		    				}
		    			
    	
	    /**
         * 
			Ladderspeed of the bot (on the ladder).
		 
         */
        protected
         double LadderSpeed =
       	0;
	
    						
    						/**
		 					 * Whether property 'LadderSpeed' was received from GB2004.
		 					 */
							protected boolean LadderSpeed_Set = false;
							
    						@Override
		    				
 		/**
         * 
			Ladderspeed of the bot (on the ladder).
		 
         */
        public  double getLadderSpeed()
 	 {
		    					return LadderSpeed;
		    				}
		    			
    	
	    /**
         * 
			Accelartion rate of this bot. How fast he accelerates.
		 
         */
        protected
         double AccelRate =
       	0;
	
    						
    						/**
		 					 * Whether property 'AccelRate' was received from GB2004.
		 					 */
							protected boolean AccelRate_Set = false;
							
    						@Override
		    				
 		/**
         * 
			Accelartion rate of this bot. How fast he accelerates.
		 
         */
        public  double getAccelRate()
 	 {
		    					return AccelRate;
		    				}
		    			
    	
	    /**
         * 
			 Bot Jump's Z boost.
		 
         */
        protected
         double JumpZ =
       	0;
	
    						
    						/**
		 					 * Whether property 'JumpZ' was received from GB2004.
		 					 */
							protected boolean JumpZ_Set = false;
							
    						@Override
		    				
 		/**
         * 
			 Bot Jump's Z boost.
		 
         */
        public  double getJumpZ()
 	 {
		    					return JumpZ;
		    				}
		    			
    	
	    /**
         * 
			Not used in GB.
		 
         */
        protected
         double MultiJumpBoost =
       	0;
	
    						
    						/**
		 					 * Whether property 'MultiJumpBoost' was received from GB2004.
		 					 */
							protected boolean MultiJumpBoost_Set = false;
							
    						@Override
		    				
 		/**
         * 
			Not used in GB.
		 
         */
        public  double getMultiJumpBoost()
 	 {
		    					return MultiJumpBoost;
		    				}
		    			
    	
	    /**
         * 
			 Max fall speed of the bot.
		 
         */
        protected
         double MaxFallSpeed =
       	0;
	
    						
    						/**
		 					 * Whether property 'MaxFallSpeed' was received from GB2004.
		 					 */
							protected boolean MaxFallSpeed_Set = false;
							
    						@Override
		    				
 		/**
         * 
			 Max fall speed of the bot.
		 
         */
        public  double getMaxFallSpeed()
 	 {
		    					return MaxFallSpeed;
		    				}
		    			
    	
	    /**
         * 
			Dodge speed factor.
		 
         */
        protected
         double DodgeSpeedFactor =
       	0;
	
    						
    						/**
		 					 * Whether property 'DodgeSpeedFactor' was received from GB2004.
		 					 */
							protected boolean DodgeSpeedFactor_Set = false;
							
    						@Override
		    				
 		/**
         * 
			Dodge speed factor.
		 
         */
        public  double getDodgeSpeedFactor()
 	 {
		    					return DodgeSpeedFactor;
		    				}
		    			
    	
	    /**
         * 
			Dodge jump Z boost of the bot. 
		 
         */
        protected
         double DodgeSpeedZ =
       	0;
	
    						
    						/**
		 					 * Whether property 'DodgeSpeedZ' was received from GB2004.
		 					 */
							protected boolean DodgeSpeedZ_Set = false;
							
    						@Override
		    				
 		/**
         * 
			Dodge jump Z boost of the bot. 
		 
         */
        public  double getDodgeSpeedZ()
 	 {
		    					return DodgeSpeedZ;
		    				}
		    			
    	
	    /**
         * 
			How well can be the bot controlled in the air (ranges from 0 to 1).
		 
         */
        protected
         double AirControl =
       	0;
	
    						
    						/**
		 					 * Whether property 'AirControl' was received from GB2004.
		 					 */
							protected boolean AirControl_Set = false;
							
    						@Override
		    				
 		/**
         * 
			How well can be the bot controlled in the air (ranges from 0 to 1).
		 
         */
        public  double getAirControl()
 	 {
		    					return AirControl;
		    				}
		    			
		    			
		    			private InitedMessageLocal localPart = null;
		    			
		    			@Override
						public InitedMessageLocal 
						getLocal() {
							if (localPart != null) return localPart;
							return localPart = new 
								InitedMessageLocalMessage();
						}
					
						private InitedMessageShared sharedPart = null;
					
						@Override
						public InitedMessageShared 
						getShared() {
							if (sharedPart != null) return sharedPart;							
							return sharedPart = new 
								InitedMessageSharedMessage();
						}
					
						private InitedMessageStatic staticPart = null; 
					
						@Override
						public InitedMessageStatic 
						getStatic() {
							if (staticPart != null) return staticPart;
							return staticPart = new 
								InitedMessageStaticMessage();
						}
    				
 		/**
         *  
            				Implementation of the local part of the GameBots2004 message INITED, used
            				to facade INITEDMessage.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Asynchronous message. Sent after succesfull init command (so usually just once). 
		Holds many attributes of the bots like speed, id starting and max health, etc.
		Some attributes are not used due to GameBots mechanics.
	
         */
 	public class InitedMessageLocalMessage 
	  					extends
  						InitedMessageLocal
	    {
 	
						
						public UnrealId getId() {						
							return cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage.InitedMessageId;
						}
					
		    			@Override
		    			public 
		    			InitedMessageLocalMessage clone() {
		    				return this;
		    			}
		    			
		    				public InitedMessageLocalMessage getLocal() {
								return this;
					    	}
							public ISharedWorldObject getShared() {
							 	throw new UnsupportedOperationException("Could not return LOCAL as SHARED");
							}
							public IStaticWorldObject getStatic() {
							    throw new UnsupportedOperationException("Could not return LOCAL as STATIC");
							}
		    			
 		/**
         * 
			A unique unreal Id of the new bot.
		 
         */
        public  UnrealId getBotId()
 	 {
				    					return BotId;
				    				}
				    			
 		/**
         * 
			Bot will always start with this health amount (usually 100). 
		 
         */
        public  int getHealthStart()
 	 {
				    					return HealthStart;
				    				}
				    			
 		/**
         * 
			Full health of the bot (usually 100).
		 
         */
        public  int getHealthFull()
 	 {
				    					return HealthFull;
				    				}
				    			
 		/**
         * 
			Maximum health of the bot (default 199).
		 
         */
        public  int getHealthMax()
 	 {
				    					return HealthMax;
				    				}
				    			
 		/**
         * 
			Amount of adrenaline at the start. Usually 0.
		 
         */
        public  double getAdrenalineStart()
 	 {
				    					return AdrenalineStart;
				    				}
				    			
 		/**
         * 
			Maxium amount of the adrenaline. Usually 100 (this can trigger the combos).
		 
         */
        public  double getAdrenalineMax()
 	 {
				    					return AdrenalineMax;
				    				}
				    			
 		/**
         * 
			Starting strength of the bot armor (usually 0).
		 
         */
        public  int getShieldStrengthStart()
 	 {
				    					return ShieldStrengthStart;
				    				}
				    			
 		/**
         * 
			Maximum strength of the bot armor (usually 150).
		 
         */
        public  int getShieldStrengthMax()
 	 {
				    					return ShieldStrengthMax;
				    				}
				    			
 		/**
         * 
			Maximum amount of succesing jumps. Currently limited to double jump in GB.
		 
         */
        public  int getMaxMultiJump()
 	 {
				    					return MaxMultiJump;
				    				}
				    			
 		/**
         * 
			Damage scaling for this bot. (he will deal reduced damage depending on the setting).
		 
         */
        public  double getDamageScaling()
 	 {
				    					return DamageScaling;
				    				}
				    			
 		/**
         * 
			Groundspeed of the bot (on the ground). Default 440.
		 
         */
        public  double getGroundSpeed()
 	 {
				    					return GroundSpeed;
				    				}
				    			
 		/**
         * 
			Waterspeed of the bot (in the water).
		 
         */
        public  double getWaterSpeed()
 	 {
				    					return WaterSpeed;
				    				}
				    			
 		/**
         * 
			AirSpeed of the bot (in the air).
		 
         */
        public  double getAirSpeed()
 	 {
				    					return AirSpeed;
				    				}
				    			
 		/**
         * 
			Ladderspeed of the bot (on the ladder).
		 
         */
        public  double getLadderSpeed()
 	 {
				    					return LadderSpeed;
				    				}
				    			
 		/**
         * 
			Accelartion rate of this bot. How fast he accelerates.
		 
         */
        public  double getAccelRate()
 	 {
				    					return AccelRate;
				    				}
				    			
 		/**
         * 
			 Bot Jump's Z boost.
		 
         */
        public  double getJumpZ()
 	 {
				    					return JumpZ;
				    				}
				    			
 		/**
         * 
			Not used in GB.
		 
         */
        public  double getMultiJumpBoost()
 	 {
				    					return MultiJumpBoost;
				    				}
				    			
 		/**
         * 
			 Max fall speed of the bot.
		 
         */
        public  double getMaxFallSpeed()
 	 {
				    					return MaxFallSpeed;
				    				}
				    			
 		/**
         * 
			Dodge speed factor.
		 
         */
        public  double getDodgeSpeedFactor()
 	 {
				    					return DodgeSpeedFactor;
				    				}
				    			
 		/**
         * 
			Dodge jump Z boost of the bot. 
		 
         */
        public  double getDodgeSpeedZ()
 	 {
				    					return DodgeSpeedZ;
				    				}
				    			
 		/**
         * 
			How well can be the bot controlled in the air (ranges from 0 to 1).
		 
         */
        public  double getAirControl()
 	 {
				    					return AirControl;
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
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=local+classtype[@name=message]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=local+classtype[@name=message]) ---        	            	
 	
		}
 	
 		/**
         *  
            				Implementation of the static part of the GameBots2004 message INITED, used
            				to facade INITEDMessage.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Asynchronous message. Sent after succesfull init command (so usually just once). 
		Holds many attributes of the bots like speed, id starting and max health, etc.
		Some attributes are not used due to GameBots mechanics.
	
         */
 	public class InitedMessageStaticMessage 
	  					extends
  						InitedMessageStatic
	    {
 	
						
						public UnrealId getId() {						
							return cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage.InitedMessageId;
						}
					
		    			@Override
		    			public 
		    			InitedMessageStaticMessage clone() {
		    				return this;
		    			}
		    			
 		
 		@Override
 		public boolean isDifferentFrom(IStaticWorldObject other)
 		{
 			if (other == null) //early fail
 			{
 				return true;
 			}
 			else if (other == this) //early out
 			{
 				return false;
 			}
 			else
 			{
 				InitedMessageStatic obj = (InitedMessageStatic) other;

 				
 			}
 			return false;
 		}
 	 
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
				"<br/>]";     
		}
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=static+classtype[@name=message]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=static+classtype[@name=message]) ---        	            	
 	
		}
 	
 		/**
         *  
            				Implementation of the shared part of the GameBots2004 message INITED, used
            				to facade INITEDMessage.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Asynchronous message. Sent after succesfull init command (so usually just once). 
		Holds many attributes of the bots like speed, id starting and max health, etc.
		Some attributes are not used due to GameBots mechanics.
	
         */
 	public class InitedMessageSharedMessage 
	  					extends
  						InitedMessageShared
	    {
 	
    	
    	
		public InitedMessageSharedMessage()
		{
			
		}		
    
						
						public UnrealId getId() {						
							return cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage.InitedMessageId;
						}
					
		    			@Override
		    			public 
		    			InitedMessageSharedMessage clone() {
		    				return this;
		    			}
		    			
		
		
		
		protected HashMap<PropertyId, ISharedProperty> propertyMap = new HashMap<PropertyId, ISharedProperty>(
			0
		);
		
		@Override
		public ISharedProperty getProperty(PropertyId id) {
			return propertyMap.get(id);
		}

		@Override
		public Map<PropertyId, ISharedProperty> getProperties() {
			return propertyMap;
		}
	
		
		
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
				"<br/>]";     
		}
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=shared+classtype[@name=message]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=shared+classtype[@name=message]) ---        	            	
 	
		}
 	
    	
    	
 	
		@Override
		public IWorldObjectUpdateResult<IWorldObject> update(IWorldObject object) {
			if (object == null)
			{
				return new IWorldObjectUpdateResult.WorldObjectUpdateResult(IWorldObjectUpdateResult.Result.CREATED, this);
			}
			if (!( object instanceof InitedMessageMessage) ) {
				throw new PogamutException("Can't update different class than InitedMessageMessage, got class " + object.getClass().getSimpleName() + "!", this);		
			}
			InitedMessageMessage toUpdate = (InitedMessageMessage)object;
			
			boolean updated = false;
			
			// UPDATING LOCAL PROPERTIES
			
				if (!SafeEquals.equals(toUpdate.BotId, getBotId()
 	)) {
					toUpdate.BotId=getBotId()
 	;
					updated = true;
				}
			
				if (toUpdate.HealthStart != getHealthStart()
 	) {
				    toUpdate.HealthStart=getHealthStart()
 	;
					updated = true;
				}
			
				if (toUpdate.HealthFull != getHealthFull()
 	) {
				    toUpdate.HealthFull=getHealthFull()
 	;
					updated = true;
				}
			
				if (toUpdate.HealthMax != getHealthMax()
 	) {
				    toUpdate.HealthMax=getHealthMax()
 	;
					updated = true;
				}
			
				if (toUpdate.AdrenalineStart != getAdrenalineStart()
 	) {
				    toUpdate.AdrenalineStart=getAdrenalineStart()
 	;
					updated = true;
				}
			
				if (toUpdate.AdrenalineMax != getAdrenalineMax()
 	) {
				    toUpdate.AdrenalineMax=getAdrenalineMax()
 	;
					updated = true;
				}
			
				if (toUpdate.ShieldStrengthStart != getShieldStrengthStart()
 	) {
				    toUpdate.ShieldStrengthStart=getShieldStrengthStart()
 	;
					updated = true;
				}
			
				if (toUpdate.ShieldStrengthMax != getShieldStrengthMax()
 	) {
				    toUpdate.ShieldStrengthMax=getShieldStrengthMax()
 	;
					updated = true;
				}
			
				if (toUpdate.MaxMultiJump != getMaxMultiJump()
 	) {
				    toUpdate.MaxMultiJump=getMaxMultiJump()
 	;
					updated = true;
				}
			
				if (toUpdate.DamageScaling != getDamageScaling()
 	) {
				    toUpdate.DamageScaling=getDamageScaling()
 	;
					updated = true;
				}
			
				if (toUpdate.GroundSpeed != getGroundSpeed()
 	) {
				    toUpdate.GroundSpeed=getGroundSpeed()
 	;
					updated = true;
				}
			
				if (toUpdate.WaterSpeed != getWaterSpeed()
 	) {
				    toUpdate.WaterSpeed=getWaterSpeed()
 	;
					updated = true;
				}
			
				if (toUpdate.AirSpeed != getAirSpeed()
 	) {
				    toUpdate.AirSpeed=getAirSpeed()
 	;
					updated = true;
				}
			
				if (toUpdate.LadderSpeed != getLadderSpeed()
 	) {
				    toUpdate.LadderSpeed=getLadderSpeed()
 	;
					updated = true;
				}
			
				if (toUpdate.AccelRate != getAccelRate()
 	) {
				    toUpdate.AccelRate=getAccelRate()
 	;
					updated = true;
				}
			
				if (toUpdate.JumpZ != getJumpZ()
 	) {
				    toUpdate.JumpZ=getJumpZ()
 	;
					updated = true;
				}
			
				if (toUpdate.MultiJumpBoost != getMultiJumpBoost()
 	) {
				    toUpdate.MultiJumpBoost=getMultiJumpBoost()
 	;
					updated = true;
				}
			
				if (toUpdate.MaxFallSpeed != getMaxFallSpeed()
 	) {
				    toUpdate.MaxFallSpeed=getMaxFallSpeed()
 	;
					updated = true;
				}
			
				if (toUpdate.DodgeSpeedFactor != getDodgeSpeedFactor()
 	) {
				    toUpdate.DodgeSpeedFactor=getDodgeSpeedFactor()
 	;
					updated = true;
				}
			
				if (toUpdate.DodgeSpeedZ != getDodgeSpeedZ()
 	) {
				    toUpdate.DodgeSpeedZ=getDodgeSpeedZ()
 	;
					updated = true;
				}
			
				if (toUpdate.AirControl != getAirControl()
 	) {
				    toUpdate.AirControl=getAirControl()
 	;
					updated = true;
				}
			
         	
         	// UPDATING SHARED PROPERTIES
         	
         	
         	// UPDATE TIME
         	toUpdate.SimTime = SimTime;
			
			if (updated) {
				return new IWorldObjectUpdateResult.WorldObjectUpdateResult<IWorldObject>(IWorldObjectUpdateResult.Result.UPDATED, toUpdate);
			} else {
				return new IWorldObjectUpdateResult.WorldObjectUpdateResult<IWorldObject>(IWorldObjectUpdateResult.Result.SAME, toUpdate);
			}
		}
		
		@Override
		public ILocalWorldObjectUpdatedEvent getLocalEvent() {
			return new InitedMessageLocalImpl.InitedMessageLocalUpdate
    (this.getLocal(), SimTime);
		}

		@Override
		public ISharedWorldObjectUpdatedEvent getSharedEvent() {
			return new InitedMessageSharedImpl.InitedMessageSharedUpdate
    (this.getShared(), SimTime, this.getTeamId());
		}

		@Override
		public IStaticWorldObjectUpdatedEvent getStaticEvent() {
			return new InitedMessageStaticImpl.InitedMessageStaticUpdate
    (this.getStatic(), SimTime);
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
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=base+classtype[@name=message]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=base+classtype[@name=message]) ---        	            	
 	
		}
 	