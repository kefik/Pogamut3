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
             				Implementation of the GameBots2004 message CONFCH contains also its Local/Shared/Static subpart class definitions..  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Asynchronous message. Message sent when the bot configuration
		changed - each agent has a lot of parameters affecting his state
		in the environment. See each property for the details.
	
         */
 	public class ConfigChangeMessage   
  				extends 
  				ConfigChange
  						implements IWorldObjectUpdatedEvent, ICompositeWorldObjectUpdatedEvent
  						
	    {
 	
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public ConfigChangeMessage()
		{
		}
	
    	
    	
    	
    	/**
		 * Creates new instance of the message ConfigChange.
		 * 
		Asynchronous message. Message sent when the bot configuration
		changed - each agent has a lot of parameters affecting his state
		in the environment. See each property for the details.
	
		 * Corresponding GameBots message
		 *   
		 *   is
		 *   CONFCH.
		 * 
 	  	 * 
		 *   
		 *     @param Id Id of this config message. This Id is generated from BotId, string "_CONFCH" is added at the end.
		 *   
		 * 
		 *   
		 *     @param BotId Unique Id of the bot.
		 *   
		 * 
		 *   
		 *     @param ManualSpawn 
			True if we have to spawn the bot manually after each death
		
		 *   
		 * 
		 *   
		 *     @param AutoTrace 
			True if the bot is using auto ray tracing (is provided with
			synchronous ATR messages). See ATR messages for more
			details.
		
		 *   
		 * 
		 *   
		 *     @param Name The bot's name.
		 *   
		 * 
		 *   
		 *     @param SpeedMultiplier 
			Bots default speed will be multiplied by this number. Ranges from 0.1 to 2 (default, can be set in ini in [GameBots2004.RemoteBot] MaxSpeed).
		
		 *   
		 * 
		 *   
		 *     @param RotationRate 
			Bot rotation rate. Default rotation rate is: (Pitch=3072,Yaw=60000,Roll=2048) and may be configured in ini file in [GameBots2004.RemoteBot] DefaultRotationRate. (pitch - up/down, yaw - left/right, roll - equivalent of doing a cartwheel)
		
		 *   
		 * 
		 *   
		 *     @param Invulnerable 
			If bot is invulnerable (cannot die) or not.
		
		 *   
		 * 
		 *   
		 *     @param SelfUpdateTime 
			The delay between two self message synchronous batches 
			(can range from 0.01 to 2 seconds). Will be used only if NewSelfBatchProtocol
			attribute is set to true in INIT message.
		
		 *   
		 * 
		 *   
		 *     @param VisionTime 
			The delay between two synchronous batches containing vision updates
			(can range from 0.1 to 2 seconds). If NewSelfBatchProtocol
			attribute is set to true in INIT message, more batch messages containing only
			SELF message will arrive between two vision update batches (containing PLR,PRJ,INV.. messages).
		
		 *   
		 * 
		 *   
		 *     @param LocUpdateMultiplier 
			Holds information how many times faster is exported location update message (UPD) compared to sync. batch, e.g. when this multiplier is set to 5 and vision time is 250 ms, UPD message will arrive every 50 ms.
		
		 *   
		 * 
		 *   
		 *     @param ShowDebug 
			If some additional debug information will be shown in the
			UT2004 server console window.
		
		 *   
		 * 
		 *   
		 *     @param ShowFocalPoint 
			If true an actor visualizing the location the bot is
			actually looking at will appear in the game.
		
		 *   
		 * 
		 *   
		 *     @param DrawTraceLines 
			if the GB should draw lines representing the auto ray traces
			of the bot (for more information see ATR message).
		
		 *   
		 * 
		 *   
		 *     @param SynchronousOff 
			It informs if sending of all GB synchronous messages is
			enabled or disabled.
		
		 *   
		 * 
		 *   
		 *     @param AutoPickupOff 
			It enables/disables automatic pickup of the bot. If true the items can be picked up through PICK command.
		
		 *   
		 * 
		 *   
		 *     @param Action 
			Name of current BDI action.
		
		 *   
		 * 
		 */
		public ConfigChangeMessage(
			UnrealId Id,  UnrealId BotId,  boolean ManualSpawn,  boolean AutoTrace,  String Name,  double SpeedMultiplier,  Rotation RotationRate,  boolean Invulnerable,  double SelfUpdateTime,  double VisionTime,  int LocUpdateMultiplier,  boolean ShowDebug,  boolean ShowFocalPoint,  boolean DrawTraceLines,  boolean SynchronousOff,  boolean AutoPickupOff,  String Action
		) {
			
					this.Id = Id;
				
					this.BotId = BotId;
				
					this.ManualSpawn = ManualSpawn;
				
					this.AutoTrace = AutoTrace;
				
					this.Name = Name;
				
					this.SpeedMultiplier = SpeedMultiplier;
				
					this.RotationRate = RotationRate;
				
					this.Invulnerable = Invulnerable;
				
					this.SelfUpdateTime = SelfUpdateTime;
				
					this.VisionTime = VisionTime;
				
					this.LocUpdateMultiplier = LocUpdateMultiplier;
				
					this.ShowDebug = ShowDebug;
				
					this.ShowFocalPoint = ShowFocalPoint;
				
					this.DrawTraceLines = DrawTraceLines;
				
					this.SynchronousOff = SynchronousOff;
				
					this.AutoPickupOff = AutoPickupOff;
				
					this.Action = Action;
				
		}
    
		/**
		 * Cloning constructor from the full message.
		 *
		 * @param original
		 */
		public ConfigChangeMessage(ConfigChangeMessage original) {		
			
					this.Id = original.getId()
 	;
				
					this.BotId = original.getBotId()
 	;
				
					this.ManualSpawn = original.isManualSpawn()
 	;
				
					this.AutoTrace = original.isAutoTrace()
 	;
				
					this.Name = original.getName()
 	;
				
					this.SpeedMultiplier = original.getSpeedMultiplier()
 	;
				
					this.RotationRate = original.getRotationRate()
 	;
				
					this.Invulnerable = original.isInvulnerable()
 	;
				
					this.SelfUpdateTime = original.getSelfUpdateTime()
 	;
				
					this.VisionTime = original.getVisionTime()
 	;
				
					this.LocUpdateMultiplier = original.getLocUpdateMultiplier()
 	;
				
					this.ShowDebug = original.isShowDebug()
 	;
				
					this.ShowFocalPoint = original.isShowFocalPoint()
 	;
				
					this.DrawTraceLines = original.isDrawTraceLines()
 	;
				
					this.SynchronousOff = original.isSynchronousOff()
 	;
				
					this.AutoPickupOff = original.isAutoPickupOff()
 	;
				
					this.Action = original.getAction()
 	;
				
				this.TeamId = original.getTeamId();
			
			this.SimTime = original.getSimTime();
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
         * Id of this config message. This Id is generated from BotId, string "_CONFCH" is added at the end. 
         */
        protected
         UnrealId Id =
       	null;
	
    						
    						/**
		 					 * Whether property 'Id' was received from GB2004.
		 					 */
							protected boolean Id_Set = false;
							
    						@Override
		    				
 		/**
         * Id of this config message. This Id is generated from BotId, string "_CONFCH" is added at the end. 
         */
        public  UnrealId getId()
 	 {
		    					return Id;
		    				}
		    			
    	
	    /**
         * Unique Id of the bot. 
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
         * Unique Id of the bot. 
         */
        public  UnrealId getBotId()
 	 {
		    					return BotId;
		    				}
		    			
    	
	    /**
         * 
			True if we have to spawn the bot manually after each death
		 
         */
        protected
         boolean ManualSpawn =
       	false;
	
    						
    						/**
		 					 * Whether property 'ManualSpawn' was received from GB2004.
		 					 */
							protected boolean ManualSpawn_Set = false;
							
    						@Override
		    				
 		/**
         * 
			True if we have to spawn the bot manually after each death
		 
         */
        public  boolean isManualSpawn()
 	 {
		    					return ManualSpawn;
		    				}
		    			
    	
	    /**
         * 
			True if the bot is using auto ray tracing (is provided with
			synchronous ATR messages). See ATR messages for more
			details.
		 
         */
        protected
         boolean AutoTrace =
       	false;
	
    						
    						/**
		 					 * Whether property 'AutoTrace' was received from GB2004.
		 					 */
							protected boolean AutoTrace_Set = false;
							
    						@Override
		    				
 		/**
         * 
			True if the bot is using auto ray tracing (is provided with
			synchronous ATR messages). See ATR messages for more
			details.
		 
         */
        public  boolean isAutoTrace()
 	 {
		    					return AutoTrace;
		    				}
		    			
    	
	    /**
         * The bot's name. 
         */
        protected
         String Name =
       	null;
	
    						
    						/**
		 					 * Whether property 'Name' was received from GB2004.
		 					 */
							protected boolean Name_Set = false;
							
    						@Override
		    				
 		/**
         * The bot's name. 
         */
        public  String getName()
 	 {
		    					return Name;
		    				}
		    			
    	
	    /**
         * 
			Bots default speed will be multiplied by this number. Ranges from 0.1 to 2 (default, can be set in ini in [GameBots2004.RemoteBot] MaxSpeed).
		 
         */
        protected
         double SpeedMultiplier =
       	0;
	
    						
    						/**
		 					 * Whether property 'SpeedMultiplier' was received from GB2004.
		 					 */
							protected boolean SpeedMultiplier_Set = false;
							
    						@Override
		    				
 		/**
         * 
			Bots default speed will be multiplied by this number. Ranges from 0.1 to 2 (default, can be set in ini in [GameBots2004.RemoteBot] MaxSpeed).
		 
         */
        public  double getSpeedMultiplier()
 	 {
		    					return SpeedMultiplier;
		    				}
		    			
    	
	    /**
         * 
			Bot rotation rate. Default rotation rate is: (Pitch=3072,Yaw=60000,Roll=2048) and may be configured in ini file in [GameBots2004.RemoteBot] DefaultRotationRate. (pitch - up/down, yaw - left/right, roll - equivalent of doing a cartwheel)
		 
         */
        protected
         Rotation RotationRate =
       	null;
	
    						
    						/**
		 					 * Whether property 'RotationRate' was received from GB2004.
		 					 */
							protected boolean RotationRate_Set = false;
							
    						@Override
		    				
 		/**
         * 
			Bot rotation rate. Default rotation rate is: (Pitch=3072,Yaw=60000,Roll=2048) and may be configured in ini file in [GameBots2004.RemoteBot] DefaultRotationRate. (pitch - up/down, yaw - left/right, roll - equivalent of doing a cartwheel)
		 
         */
        public  Rotation getRotationRate()
 	 {
		    					return RotationRate;
		    				}
		    			
    	
	    /**
         * 
			If bot is invulnerable (cannot die) or not.
		 
         */
        protected
         boolean Invulnerable =
       	false;
	
    						
    						/**
		 					 * Whether property 'Invulnerable' was received from GB2004.
		 					 */
							protected boolean Invulnerable_Set = false;
							
    						@Override
		    				
 		/**
         * 
			If bot is invulnerable (cannot die) or not.
		 
         */
        public  boolean isInvulnerable()
 	 {
		    					return Invulnerable;
		    				}
		    			
    	
	    /**
         * 
			The delay between two self message synchronous batches 
			(can range from 0.01 to 2 seconds). Will be used only if NewSelfBatchProtocol
			attribute is set to true in INIT message.
		 
         */
        protected
         double SelfUpdateTime =
       	0;
	
    						
    						/**
		 					 * Whether property 'SelfUpdateTime' was received from GB2004.
		 					 */
							protected boolean SelfUpdateTime_Set = false;
							
    						@Override
		    				
 		/**
         * 
			The delay between two self message synchronous batches 
			(can range from 0.01 to 2 seconds). Will be used only if NewSelfBatchProtocol
			attribute is set to true in INIT message.
		 
         */
        public  double getSelfUpdateTime()
 	 {
		    					return SelfUpdateTime;
		    				}
		    			
    	
	    /**
         * 
			The delay between two synchronous batches containing vision updates
			(can range from 0.1 to 2 seconds). If NewSelfBatchProtocol
			attribute is set to true in INIT message, more batch messages containing only
			SELF message will arrive between two vision update batches (containing PLR,PRJ,INV.. messages).
		 
         */
        protected
         double VisionTime =
       	0;
	
    						
    						/**
		 					 * Whether property 'VisionTime' was received from GB2004.
		 					 */
							protected boolean VisionTime_Set = false;
							
    						@Override
		    				
 		/**
         * 
			The delay between two synchronous batches containing vision updates
			(can range from 0.1 to 2 seconds). If NewSelfBatchProtocol
			attribute is set to true in INIT message, more batch messages containing only
			SELF message will arrive between two vision update batches (containing PLR,PRJ,INV.. messages).
		 
         */
        public  double getVisionTime()
 	 {
		    					return VisionTime;
		    				}
		    			
    	
	    /**
         * 
			Holds information how many times faster is exported location update message (UPD) compared to sync. batch, e.g. when this multiplier is set to 5 and vision time is 250 ms, UPD message will arrive every 50 ms.
		 
         */
        protected
         int LocUpdateMultiplier =
       	0;
	
    						
    						/**
		 					 * Whether property 'LocUpdateMultiplier' was received from GB2004.
		 					 */
							protected boolean LocUpdateMultiplier_Set = false;
							
    						@Override
		    				
 		/**
         * 
			Holds information how many times faster is exported location update message (UPD) compared to sync. batch, e.g. when this multiplier is set to 5 and vision time is 250 ms, UPD message will arrive every 50 ms.
		 
         */
        public  int getLocUpdateMultiplier()
 	 {
		    					return LocUpdateMultiplier;
		    				}
		    			
    	
	    /**
         * 
			If some additional debug information will be shown in the
			UT2004 server console window.
		 
         */
        protected
         boolean ShowDebug =
       	false;
	
    						
    						/**
		 					 * Whether property 'ShowDebug' was received from GB2004.
		 					 */
							protected boolean ShowDebug_Set = false;
							
    						@Override
		    				
 		/**
         * 
			If some additional debug information will be shown in the
			UT2004 server console window.
		 
         */
        public  boolean isShowDebug()
 	 {
		    					return ShowDebug;
		    				}
		    			
    	
	    /**
         * 
			If true an actor visualizing the location the bot is
			actually looking at will appear in the game.
		 
         */
        protected
         boolean ShowFocalPoint =
       	false;
	
    						
    						/**
		 					 * Whether property 'ShowFocalPoint' was received from GB2004.
		 					 */
							protected boolean ShowFocalPoint_Set = false;
							
    						@Override
		    				
 		/**
         * 
			If true an actor visualizing the location the bot is
			actually looking at will appear in the game.
		 
         */
        public  boolean isShowFocalPoint()
 	 {
		    					return ShowFocalPoint;
		    				}
		    			
    	
	    /**
         * 
			if the GB should draw lines representing the auto ray traces
			of the bot (for more information see ATR message).
		 
         */
        protected
         boolean DrawTraceLines =
       	false;
	
    						
    						/**
		 					 * Whether property 'DrawTraceLines' was received from GB2004.
		 					 */
							protected boolean DrawTraceLines_Set = false;
							
    						@Override
		    				
 		/**
         * 
			if the GB should draw lines representing the auto ray traces
			of the bot (for more information see ATR message).
		 
         */
        public  boolean isDrawTraceLines()
 	 {
		    					return DrawTraceLines;
		    				}
		    			
    	
	    /**
         * 
			It informs if sending of all GB synchronous messages is
			enabled or disabled.
		 
         */
        protected
         boolean SynchronousOff =
       	false;
	
    						
    						/**
		 					 * Whether property 'SynchronousOff' was received from GB2004.
		 					 */
							protected boolean SynchronousOff_Set = false;
							
    						@Override
		    				
 		/**
         * 
			It informs if sending of all GB synchronous messages is
			enabled or disabled.
		 
         */
        public  boolean isSynchronousOff()
 	 {
		    					return SynchronousOff;
		    				}
		    			
    	
	    /**
         * 
			It enables/disables automatic pickup of the bot. If true the items can be picked up through PICK command.
		 
         */
        protected
         boolean AutoPickupOff =
       	false;
	
    						
    						/**
		 					 * Whether property 'AutoPickupOff' was received from GB2004.
		 					 */
							protected boolean AutoPickupOff_Set = false;
							
    						@Override
		    				
 		/**
         * 
			It enables/disables automatic pickup of the bot. If true the items can be picked up through PICK command.
		 
         */
        public  boolean isAutoPickupOff()
 	 {
		    					return AutoPickupOff;
		    				}
		    			
    	
	    /**
         * 
			Name of current BDI action.
		 
         */
        protected
         String Action =
       	null;
	
    						
    						/**
		 					 * Whether property 'Action' was received from GB2004.
		 					 */
							protected boolean Action_Set = false;
							
    						@Override
		    				
 		/**
         * 
			Name of current BDI action.
		 
         */
        public  String getAction()
 	 {
		    					return Action;
		    				}
		    			
		    			
		    			private ConfigChangeLocal localPart = null;
		    			
		    			@Override
						public ConfigChangeLocal 
						getLocal() {
							if (localPart != null) return localPart;
							return localPart = new 
								ConfigChangeLocalMessage();
						}
					
						private ConfigChangeShared sharedPart = null;
					
						@Override
						public ConfigChangeShared 
						getShared() {
							if (sharedPart != null) return sharedPart;							
							return sharedPart = new 
								ConfigChangeSharedMessage();
						}
					
						private ConfigChangeStatic staticPart = null; 
					
						@Override
						public ConfigChangeStatic 
						getStatic() {
							if (staticPart != null) return staticPart;
							return staticPart = new 
								ConfigChangeStaticMessage();
						}
    				
 		/**
         *  
            				Implementation of the local part of the GameBots2004 message CONFCH, used
            				to facade CONFCHMessage.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Asynchronous message. Message sent when the bot configuration
		changed - each agent has a lot of parameters affecting his state
		in the environment. See each property for the details.
	
         */
 	public class ConfigChangeLocalMessage 
	  					extends
  						ConfigChangeLocal
	    {
 	
		    			@Override
		    			public 
		    			ConfigChangeLocalMessage clone() {
		    				return this;
		    			}
		    			
		    				public ConfigChangeLocalMessage getLocal() {
								return this;
					    	}
							public ISharedWorldObject getShared() {
							 	throw new UnsupportedOperationException("Could not return LOCAL as SHARED");
							}
							public IStaticWorldObject getStatic() {
							    throw new UnsupportedOperationException("Could not return LOCAL as STATIC");
							}
		    			
 		/**
         * Id of this config message. This Id is generated from BotId, string "_CONFCH" is added at the end. 
         */
        public  UnrealId getId()
 	 {
				    					return Id;
				    				}
				    			
 		/**
         * Unique Id of the bot. 
         */
        public  UnrealId getBotId()
 	 {
				    					return BotId;
				    				}
				    			
 		/**
         * 
			True if we have to spawn the bot manually after each death
		 
         */
        public  boolean isManualSpawn()
 	 {
				    					return ManualSpawn;
				    				}
				    			
 		/**
         * 
			True if the bot is using auto ray tracing (is provided with
			synchronous ATR messages). See ATR messages for more
			details.
		 
         */
        public  boolean isAutoTrace()
 	 {
				    					return AutoTrace;
				    				}
				    			
 		/**
         * The bot's name. 
         */
        public  String getName()
 	 {
				    					return Name;
				    				}
				    			
 		/**
         * 
			Bots default speed will be multiplied by this number. Ranges from 0.1 to 2 (default, can be set in ini in [GameBots2004.RemoteBot] MaxSpeed).
		 
         */
        public  double getSpeedMultiplier()
 	 {
				    					return SpeedMultiplier;
				    				}
				    			
 		/**
         * 
			Bot rotation rate. Default rotation rate is: (Pitch=3072,Yaw=60000,Roll=2048) and may be configured in ini file in [GameBots2004.RemoteBot] DefaultRotationRate. (pitch - up/down, yaw - left/right, roll - equivalent of doing a cartwheel)
		 
         */
        public  Rotation getRotationRate()
 	 {
				    					return RotationRate;
				    				}
				    			
 		/**
         * 
			If bot is invulnerable (cannot die) or not.
		 
         */
        public  boolean isInvulnerable()
 	 {
				    					return Invulnerable;
				    				}
				    			
 		/**
         * 
			The delay between two self message synchronous batches 
			(can range from 0.01 to 2 seconds). Will be used only if NewSelfBatchProtocol
			attribute is set to true in INIT message.
		 
         */
        public  double getSelfUpdateTime()
 	 {
				    					return SelfUpdateTime;
				    				}
				    			
 		/**
         * 
			The delay between two synchronous batches containing vision updates
			(can range from 0.1 to 2 seconds). If NewSelfBatchProtocol
			attribute is set to true in INIT message, more batch messages containing only
			SELF message will arrive between two vision update batches (containing PLR,PRJ,INV.. messages).
		 
         */
        public  double getVisionTime()
 	 {
				    					return VisionTime;
				    				}
				    			
 		/**
         * 
			Holds information how many times faster is exported location update message (UPD) compared to sync. batch, e.g. when this multiplier is set to 5 and vision time is 250 ms, UPD message will arrive every 50 ms.
		 
         */
        public  int getLocUpdateMultiplier()
 	 {
				    					return LocUpdateMultiplier;
				    				}
				    			
 		/**
         * 
			If some additional debug information will be shown in the
			UT2004 server console window.
		 
         */
        public  boolean isShowDebug()
 	 {
				    					return ShowDebug;
				    				}
				    			
 		/**
         * 
			If true an actor visualizing the location the bot is
			actually looking at will appear in the game.
		 
         */
        public  boolean isShowFocalPoint()
 	 {
				    					return ShowFocalPoint;
				    				}
				    			
 		/**
         * 
			if the GB should draw lines representing the auto ray traces
			of the bot (for more information see ATR message).
		 
         */
        public  boolean isDrawTraceLines()
 	 {
				    					return DrawTraceLines;
				    				}
				    			
 		/**
         * 
			It informs if sending of all GB synchronous messages is
			enabled or disabled.
		 
         */
        public  boolean isSynchronousOff()
 	 {
				    					return SynchronousOff;
				    				}
				    			
 		/**
         * 
			It enables/disables automatic pickup of the bot. If true the items can be picked up through PICK command.
		 
         */
        public  boolean isAutoPickupOff()
 	 {
				    					return AutoPickupOff;
				    				}
				    			
 		/**
         * 
			Name of current BDI action.
		 
         */
        public  String getAction()
 	 {
				    					return Action;
				    				}
				    			
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
		              			"Id = " + String.valueOf(getId()
 	) + " | " + 
		              		
		              			"BotId = " + String.valueOf(getBotId()
 	) + " | " + 
		              		
		              			"ManualSpawn = " + String.valueOf(isManualSpawn()
 	) + " | " + 
		              		
		              			"AutoTrace = " + String.valueOf(isAutoTrace()
 	) + " | " + 
		              		
		              			"Name = " + String.valueOf(getName()
 	) + " | " + 
		              		
		              			"SpeedMultiplier = " + String.valueOf(getSpeedMultiplier()
 	) + " | " + 
		              		
		              			"RotationRate = " + String.valueOf(getRotationRate()
 	) + " | " + 
		              		
		              			"Invulnerable = " + String.valueOf(isInvulnerable()
 	) + " | " + 
		              		
		              			"SelfUpdateTime = " + String.valueOf(getSelfUpdateTime()
 	) + " | " + 
		              		
		              			"VisionTime = " + String.valueOf(getVisionTime()
 	) + " | " + 
		              		
		              			"LocUpdateMultiplier = " + String.valueOf(getLocUpdateMultiplier()
 	) + " | " + 
		              		
		              			"ShowDebug = " + String.valueOf(isShowDebug()
 	) + " | " + 
		              		
		              			"ShowFocalPoint = " + String.valueOf(isShowFocalPoint()
 	) + " | " + 
		              		
		              			"DrawTraceLines = " + String.valueOf(isDrawTraceLines()
 	) + " | " + 
		              		
		              			"SynchronousOff = " + String.valueOf(isSynchronousOff()
 	) + " | " + 
		              		
		              			"AutoPickupOff = " + String.valueOf(isAutoPickupOff()
 	) + " | " + 
		              		
		              			"Action = " + String.valueOf(getAction()
 	) + " | " + 
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>Id</b> = " + String.valueOf(getId()
 	) + " <br/> " + 
		              		
		              			"<b>BotId</b> = " + String.valueOf(getBotId()
 	) + " <br/> " + 
		              		
		              			"<b>ManualSpawn</b> = " + String.valueOf(isManualSpawn()
 	) + " <br/> " + 
		              		
		              			"<b>AutoTrace</b> = " + String.valueOf(isAutoTrace()
 	) + " <br/> " + 
		              		
		              			"<b>Name</b> = " + String.valueOf(getName()
 	) + " <br/> " + 
		              		
		              			"<b>SpeedMultiplier</b> = " + String.valueOf(getSpeedMultiplier()
 	) + " <br/> " + 
		              		
		              			"<b>RotationRate</b> = " + String.valueOf(getRotationRate()
 	) + " <br/> " + 
		              		
		              			"<b>Invulnerable</b> = " + String.valueOf(isInvulnerable()
 	) + " <br/> " + 
		              		
		              			"<b>SelfUpdateTime</b> = " + String.valueOf(getSelfUpdateTime()
 	) + " <br/> " + 
		              		
		              			"<b>VisionTime</b> = " + String.valueOf(getVisionTime()
 	) + " <br/> " + 
		              		
		              			"<b>LocUpdateMultiplier</b> = " + String.valueOf(getLocUpdateMultiplier()
 	) + " <br/> " + 
		              		
		              			"<b>ShowDebug</b> = " + String.valueOf(isShowDebug()
 	) + " <br/> " + 
		              		
		              			"<b>ShowFocalPoint</b> = " + String.valueOf(isShowFocalPoint()
 	) + " <br/> " + 
		              		
		              			"<b>DrawTraceLines</b> = " + String.valueOf(isDrawTraceLines()
 	) + " <br/> " + 
		              		
		              			"<b>SynchronousOff</b> = " + String.valueOf(isSynchronousOff()
 	) + " <br/> " + 
		              		
		              			"<b>AutoPickupOff</b> = " + String.valueOf(isAutoPickupOff()
 	) + " <br/> " + 
		              		
		              			"<b>Action</b> = " + String.valueOf(getAction()
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
            				Implementation of the static part of the GameBots2004 message CONFCH, used
            				to facade CONFCHMessage.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Asynchronous message. Message sent when the bot configuration
		changed - each agent has a lot of parameters affecting his state
		in the environment. See each property for the details.
	
         */
 	public class ConfigChangeStaticMessage 
	  					extends
  						ConfigChangeStatic
	    {
 	
		    			@Override
		    			public 
		    			ConfigChangeStaticMessage clone() {
		    				return this;
		    			}
		    			
 		/**
         * Id of this config message. This Id is generated from BotId, string "_CONFCH" is added at the end. 
         */
        public  UnrealId getId()
 	 {
				    					return Id;
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
 				ConfigChangeStatic obj = (ConfigChangeStatic) other;

 				
 						if ( !(
 	 			AdvancedEquals.equalsOrNull(this.getId()
 	, obj.getId()
 	)
 	 		) )
						{
							System.out.println("!!!!!PROPERTY UPDATE ERROR!!!! on property Id on object class ConfigChangeStatic");
							return true;
						}
 					
 			}
 			return false;
 		}
 	 
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
		              			"Id = " + String.valueOf(getId()
 	) + " | " + 
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>Id</b> = " + String.valueOf(getId()
 	) + " <br/> " + 
		              		
				"<br/>]";     
		}
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=static+classtype[@name=message]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=static+classtype[@name=message]) ---        	            	
 	
		}
 	
 		/**
         *  
            				Implementation of the shared part of the GameBots2004 message CONFCH, used
            				to facade CONFCHMessage.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Asynchronous message. Message sent when the bot configuration
		changed - each agent has a lot of parameters affecting his state
		in the environment. See each property for the details.
	
         */
 	public class ConfigChangeSharedMessage 
	  					extends
  						ConfigChangeShared
	    {
 	
    	
    	
		public ConfigChangeSharedMessage()
		{
			
		}		
    
		    			@Override
		    			public 
		    			ConfigChangeSharedMessage clone() {
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
	
		
		
 		/**
         * Id of this config message. This Id is generated from BotId, string "_CONFCH" is added at the end. 
         */
        public  UnrealId getId()
 	 {
  			return Id;
  		}
  		
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
		              			"Id = " + String.valueOf(getId()
 	) + " | " + 
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>Id</b> = " + String.valueOf(getId()
 	) + " <br/> " + 
		              		
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
			if (!( object instanceof ConfigChangeMessage) ) {
				throw new PogamutException("Can't update different class than ConfigChangeMessage, got class " + object.getClass().getSimpleName() + "!", this);		
			}
			ConfigChangeMessage toUpdate = (ConfigChangeMessage)object;
			
			boolean updated = false;
			
			// UPDATING LOCAL PROPERTIES
			
				if (!SafeEquals.equals(toUpdate.BotId, getBotId()
 	)) {
					toUpdate.BotId=getBotId()
 	;
					updated = true;
				}
			
				if (toUpdate.ManualSpawn != isManualSpawn()
 	) {
				    toUpdate.ManualSpawn=isManualSpawn()
 	;
					updated = true;
				}
			
				if (toUpdate.AutoTrace != isAutoTrace()
 	) {
				    toUpdate.AutoTrace=isAutoTrace()
 	;
					updated = true;
				}
			
				if (!SafeEquals.equals(toUpdate.Name, getName()
 	)) {
					toUpdate.Name=getName()
 	;
					updated = true;
				}
			
				if (toUpdate.SpeedMultiplier != getSpeedMultiplier()
 	) {
				    toUpdate.SpeedMultiplier=getSpeedMultiplier()
 	;
					updated = true;
				}
			
				if (!SafeEquals.equals(toUpdate.RotationRate, getRotationRate()
 	)) {
					toUpdate.RotationRate=getRotationRate()
 	;
					updated = true;
				}
			
				if (toUpdate.Invulnerable != isInvulnerable()
 	) {
				    toUpdate.Invulnerable=isInvulnerable()
 	;
					updated = true;
				}
			
				if (toUpdate.SelfUpdateTime != getSelfUpdateTime()
 	) {
				    toUpdate.SelfUpdateTime=getSelfUpdateTime()
 	;
					updated = true;
				}
			
				if (toUpdate.VisionTime != getVisionTime()
 	) {
				    toUpdate.VisionTime=getVisionTime()
 	;
					updated = true;
				}
			
				if (toUpdate.LocUpdateMultiplier != getLocUpdateMultiplier()
 	) {
				    toUpdate.LocUpdateMultiplier=getLocUpdateMultiplier()
 	;
					updated = true;
				}
			
				if (toUpdate.ShowDebug != isShowDebug()
 	) {
				    toUpdate.ShowDebug=isShowDebug()
 	;
					updated = true;
				}
			
				if (toUpdate.ShowFocalPoint != isShowFocalPoint()
 	) {
				    toUpdate.ShowFocalPoint=isShowFocalPoint()
 	;
					updated = true;
				}
			
				if (toUpdate.DrawTraceLines != isDrawTraceLines()
 	) {
				    toUpdate.DrawTraceLines=isDrawTraceLines()
 	;
					updated = true;
				}
			
				if (toUpdate.SynchronousOff != isSynchronousOff()
 	) {
				    toUpdate.SynchronousOff=isSynchronousOff()
 	;
					updated = true;
				}
			
				if (toUpdate.AutoPickupOff != isAutoPickupOff()
 	) {
				    toUpdate.AutoPickupOff=isAutoPickupOff()
 	;
					updated = true;
				}
			
				if (!SafeEquals.equals(toUpdate.Action, getAction()
 	)) {
					toUpdate.Action=getAction()
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
			return new ConfigChangeLocalImpl.ConfigChangeLocalUpdate
    (this.getLocal(), SimTime);
		}

		@Override
		public ISharedWorldObjectUpdatedEvent getSharedEvent() {
			return new ConfigChangeSharedImpl.ConfigChangeSharedUpdate
    (this.getShared(), SimTime, this.getTeamId());
		}

		@Override
		public IStaticWorldObjectUpdatedEvent getStaticEvent() {
			return new ConfigChangeStaticImpl.ConfigChangeStaticUpdate
    (this.getStatic(), SimTime);
		}
 	
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
		              			"Id = " + String.valueOf(getId()
 	) + " | " + 
		              		
		              			"BotId = " + String.valueOf(getBotId()
 	) + " | " + 
		              		
		              			"ManualSpawn = " + String.valueOf(isManualSpawn()
 	) + " | " + 
		              		
		              			"AutoTrace = " + String.valueOf(isAutoTrace()
 	) + " | " + 
		              		
		              			"Name = " + String.valueOf(getName()
 	) + " | " + 
		              		
		              			"SpeedMultiplier = " + String.valueOf(getSpeedMultiplier()
 	) + " | " + 
		              		
		              			"RotationRate = " + String.valueOf(getRotationRate()
 	) + " | " + 
		              		
		              			"Invulnerable = " + String.valueOf(isInvulnerable()
 	) + " | " + 
		              		
		              			"SelfUpdateTime = " + String.valueOf(getSelfUpdateTime()
 	) + " | " + 
		              		
		              			"VisionTime = " + String.valueOf(getVisionTime()
 	) + " | " + 
		              		
		              			"LocUpdateMultiplier = " + String.valueOf(getLocUpdateMultiplier()
 	) + " | " + 
		              		
		              			"ShowDebug = " + String.valueOf(isShowDebug()
 	) + " | " + 
		              		
		              			"ShowFocalPoint = " + String.valueOf(isShowFocalPoint()
 	) + " | " + 
		              		
		              			"DrawTraceLines = " + String.valueOf(isDrawTraceLines()
 	) + " | " + 
		              		
		              			"SynchronousOff = " + String.valueOf(isSynchronousOff()
 	) + " | " + 
		              		
		              			"AutoPickupOff = " + String.valueOf(isAutoPickupOff()
 	) + " | " + 
		              		
		              			"Action = " + String.valueOf(getAction()
 	) + " | " + 
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>Id</b> = " + String.valueOf(getId()
 	) + " <br/> " + 
		              		
		              			"<b>BotId</b> = " + String.valueOf(getBotId()
 	) + " <br/> " + 
		              		
		              			"<b>ManualSpawn</b> = " + String.valueOf(isManualSpawn()
 	) + " <br/> " + 
		              		
		              			"<b>AutoTrace</b> = " + String.valueOf(isAutoTrace()
 	) + " <br/> " + 
		              		
		              			"<b>Name</b> = " + String.valueOf(getName()
 	) + " <br/> " + 
		              		
		              			"<b>SpeedMultiplier</b> = " + String.valueOf(getSpeedMultiplier()
 	) + " <br/> " + 
		              		
		              			"<b>RotationRate</b> = " + String.valueOf(getRotationRate()
 	) + " <br/> " + 
		              		
		              			"<b>Invulnerable</b> = " + String.valueOf(isInvulnerable()
 	) + " <br/> " + 
		              		
		              			"<b>SelfUpdateTime</b> = " + String.valueOf(getSelfUpdateTime()
 	) + " <br/> " + 
		              		
		              			"<b>VisionTime</b> = " + String.valueOf(getVisionTime()
 	) + " <br/> " + 
		              		
		              			"<b>LocUpdateMultiplier</b> = " + String.valueOf(getLocUpdateMultiplier()
 	) + " <br/> " + 
		              		
		              			"<b>ShowDebug</b> = " + String.valueOf(isShowDebug()
 	) + " <br/> " + 
		              		
		              			"<b>ShowFocalPoint</b> = " + String.valueOf(isShowFocalPoint()
 	) + " <br/> " + 
		              		
		              			"<b>DrawTraceLines</b> = " + String.valueOf(isDrawTraceLines()
 	) + " <br/> " + 
		              		
		              			"<b>SynchronousOff</b> = " + String.valueOf(isSynchronousOff()
 	) + " <br/> " + 
		              		
		              			"<b>AutoPickupOff</b> = " + String.valueOf(isAutoPickupOff()
 	) + " <br/> " + 
		              		
		              			"<b>Action</b> = " + String.valueOf(getAction()
 	) + " <br/> " + 
		              		
				"<br/>]";     
		}
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=base+classtype[@name=message]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=base+classtype[@name=message]) ---        	            	
 	
		}
 	