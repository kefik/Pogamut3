package cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages;
 		
 		// --- IMPORTS FROM /messages/settings/javasettings/javaimport BEGIN
			import java.util.*;import javax.vecmath.*;import cz.cuni.amis.pogamut.base.communication.messages.*;import cz.cuni.amis.pogamut.base.communication.worldview.*;import cz.cuni.amis.pogamut.base.communication.worldview.event.*;import cz.cuni.amis.pogamut.base.communication.worldview.object.*;import cz.cuni.amis.pogamut.multi.communication.worldview.object.*;import cz.cuni.amis.pogamut.base.communication.translator.event.*;import cz.cuni.amis.pogamut.multi.communication.translator.event.*;import cz.cuni.amis.pogamut.base3d.worldview.object.*;import cz.cuni.amis.pogamut.base3d.worldview.object.event.*;import cz.cuni.amis.pogamut.ut2004.communication.messages.*;import cz.cuni.amis.pogamut.ut2004.communication.worldview.objects.*;import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.objects.*;import cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor.*;import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType.Category;import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;import cz.cuni.amis.utils.exception.*;import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdateResult.Result;import cz.cuni.amis.utils.SafeEquals;import cz.cuni.amis.pogamut.base.agent.*;import cz.cuni.amis.pogamut.multi.agent.*;import cz.cuni.amis.pogamut.multi.communication.worldview.property.*;import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.property.*;import cz.cuni.amis.utils.token.*;import cz.cuni.amis.utils.*;
		// --- IMPORTS FROM /messages/settings/javasettings/javaimport END
		
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name='all'] BEGIN
				
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name='all'] END
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name=local]+classtype[@name=impl] BEGIN
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name=local]+classtype[@name=impl] END
    
 		/**
         *  
            				Implementation of the local part of the GameBots2004 message INITED.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Asynchronous message. Sent after succesfull init command (so usually just once). 
		Holds many attributes of the bots like speed, id starting and max health, etc.
		Some attributes are not used due to GameBots mechanics.
	
         */
 	public class InitedMessageLocalImpl 
  						extends
  						InitedMessageLocal
	    {
 	
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public InitedMessageLocalImpl()
		{
		}
	
    	/**
		 * Cloning constructor from the full message.
		 *
		 * @param original
		 */
		public InitedMessageLocalImpl(InitedMessage original) {		
			
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
				
			this.SimTime = original.getSimTime();			
		}
		
		/**
		 * Cloning constructor from the full message.
		 *
		 * @param original
		 */
		public InitedMessageLocalImpl(InitedMessageLocalImpl original) {		
			
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
				
			this.SimTime = original.getSimTime();
		}
		
			/**
			 * Cloning constructor from the message part.
			 *
			 * @param original
			 */
			public InitedMessageLocalImpl(InitedMessageLocal original) {
				
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
					
			}
		
						
						public UnrealId getId() {						
							return cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage.InitedMessageId;
						}
					
   				
   				@Override
   				public void setSimTime(long SimTime) {
					super.setSimTime(SimTime);
				}
   			
	    				@Override
	    				public 
	    				InitedMessageLocalImpl clone() {
	    					return new 
	    					InitedMessageLocalImpl(this);
	    				}
	    				
	    				
    	
	    /**
         * 
			A unique unreal Id of the new bot.
		 
         */
        protected
         UnrealId BotId =
       	null;
	
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
         * 
			How well can be the bot controlled in the air (ranges from 0 to 1).
		 
         */
        public  double getAirControl()
 	 {
				    					return AirControl;
				    				}
				    			
    	
    	
    	
    	
    	public InitedMessageLocalImpl getLocal() {
			return this;
    	}
		public ISharedWorldObject getShared() {
		 	throw new UnsupportedOperationException("Could not return LOCAL as SHARED");
		}
		public IStaticWorldObject getStatic() {
		    throw new UnsupportedOperationException("Could not return LOCAL as STATIC");
		}
 	
		public static class InitedMessageLocalUpdate
     implements ILocalWorldObjectUpdatedEvent, IGBWorldObjectEvent
		{
			protected long time;
			
			protected InitedMessageLocal data = null; //contains object data for this update
			
			public InitedMessageLocalUpdate
    (InitedMessageLocal moverLocal, long time)
			{
				this.data = moverLocal;
				this.time = time;
			}
			
			@Override
			public IWorldObjectUpdateResult<ILocalWorldObject> update(
					ILocalWorldObject object) 
			{
				if ( object == null)
				{
					data = new InitedMessageLocalImpl(data); //we always return Impl object
					return new IWorldObjectUpdateResult.WorldObjectUpdateResult<ILocalWorldObject>(IWorldObjectUpdateResult.Result.CREATED, data);
				}
				if ( object instanceof InitedMessageLocalImpl )
				{
					InitedMessageLocalImpl toUpdate = (InitedMessageLocalImpl)object;
					
					boolean updated = false;
					
					// UPDATING LOCAL PROPERTIES
					
				if (!SafeEquals.equals(toUpdate.BotId, data.getBotId()
 	)) {
					toUpdate.BotId=data.getBotId()
 	;
					updated = true;
				}
			
				if (toUpdate.HealthStart != data.getHealthStart()
 	) {
				    toUpdate.HealthStart=data.getHealthStart()
 	;
					updated = true;
				}
			
				if (toUpdate.HealthFull != data.getHealthFull()
 	) {
				    toUpdate.HealthFull=data.getHealthFull()
 	;
					updated = true;
				}
			
				if (toUpdate.HealthMax != data.getHealthMax()
 	) {
				    toUpdate.HealthMax=data.getHealthMax()
 	;
					updated = true;
				}
			
				if (toUpdate.AdrenalineStart != data.getAdrenalineStart()
 	) {
				    toUpdate.AdrenalineStart=data.getAdrenalineStart()
 	;
					updated = true;
				}
			
				if (toUpdate.AdrenalineMax != data.getAdrenalineMax()
 	) {
				    toUpdate.AdrenalineMax=data.getAdrenalineMax()
 	;
					updated = true;
				}
			
				if (toUpdate.ShieldStrengthStart != data.getShieldStrengthStart()
 	) {
				    toUpdate.ShieldStrengthStart=data.getShieldStrengthStart()
 	;
					updated = true;
				}
			
				if (toUpdate.ShieldStrengthMax != data.getShieldStrengthMax()
 	) {
				    toUpdate.ShieldStrengthMax=data.getShieldStrengthMax()
 	;
					updated = true;
				}
			
				if (toUpdate.MaxMultiJump != data.getMaxMultiJump()
 	) {
				    toUpdate.MaxMultiJump=data.getMaxMultiJump()
 	;
					updated = true;
				}
			
				if (toUpdate.DamageScaling != data.getDamageScaling()
 	) {
				    toUpdate.DamageScaling=data.getDamageScaling()
 	;
					updated = true;
				}
			
				if (toUpdate.GroundSpeed != data.getGroundSpeed()
 	) {
				    toUpdate.GroundSpeed=data.getGroundSpeed()
 	;
					updated = true;
				}
			
				if (toUpdate.WaterSpeed != data.getWaterSpeed()
 	) {
				    toUpdate.WaterSpeed=data.getWaterSpeed()
 	;
					updated = true;
				}
			
				if (toUpdate.AirSpeed != data.getAirSpeed()
 	) {
				    toUpdate.AirSpeed=data.getAirSpeed()
 	;
					updated = true;
				}
			
				if (toUpdate.LadderSpeed != data.getLadderSpeed()
 	) {
				    toUpdate.LadderSpeed=data.getLadderSpeed()
 	;
					updated = true;
				}
			
				if (toUpdate.AccelRate != data.getAccelRate()
 	) {
				    toUpdate.AccelRate=data.getAccelRate()
 	;
					updated = true;
				}
			
				if (toUpdate.JumpZ != data.getJumpZ()
 	) {
				    toUpdate.JumpZ=data.getJumpZ()
 	;
					updated = true;
				}
			
				if (toUpdate.MultiJumpBoost != data.getMultiJumpBoost()
 	) {
				    toUpdate.MultiJumpBoost=data.getMultiJumpBoost()
 	;
					updated = true;
				}
			
				if (toUpdate.MaxFallSpeed != data.getMaxFallSpeed()
 	) {
				    toUpdate.MaxFallSpeed=data.getMaxFallSpeed()
 	;
					updated = true;
				}
			
				if (toUpdate.DodgeSpeedFactor != data.getDodgeSpeedFactor()
 	) {
				    toUpdate.DodgeSpeedFactor=data.getDodgeSpeedFactor()
 	;
					updated = true;
				}
			
				if (toUpdate.DodgeSpeedZ != data.getDodgeSpeedZ()
 	) {
				    toUpdate.DodgeSpeedZ=data.getDodgeSpeedZ()
 	;
					updated = true;
				}
			
				if (toUpdate.AirControl != data.getAirControl()
 	) {
				    toUpdate.AirControl=data.getAirControl()
 	;
					updated = true;
				}
			
					
					data = toUpdate; //the updating has finished
					
					if ( updated )
					{
						toUpdate.SimTime = this.time;
						return new IWorldObjectUpdateResult.WorldObjectUpdateResult<ILocalWorldObject>(IWorldObjectUpdateResult.Result.UPDATED, data);
					}
					
					return new IWorldObjectUpdateResult.WorldObjectUpdateResult<ILocalWorldObject>(IWorldObjectUpdateResult.Result.SAME, data);
				}
				throw new PogamutException("Unsupported object type for update. Expected InitedMessageLocalImpl for object " + object.getId() +", not object of class " + object.getClass().getSimpleName() + ".", this);
			}
	
			/**
			 * Simulation time in MILLI SECONDS !!!
			 */
			@Override
			public long getSimTime() {
				return this.time;
			}
	
			@Override
			public IWorldObject getObject() {
				return data;
			}
	
			@Override
			public WorldObjectId getId() {
				return data.getId();
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
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=local+classtype[@name=impl]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=local+classtype[@name=impl]) ---        	            	
 	
		}
 	