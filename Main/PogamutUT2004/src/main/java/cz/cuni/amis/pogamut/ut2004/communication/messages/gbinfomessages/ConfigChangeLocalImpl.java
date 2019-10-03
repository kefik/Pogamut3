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
            				Implementation of the local part of the GameBots2004 message CONFCH.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Asynchronous message. Message sent when the bot configuration
		changed - each agent has a lot of parameters affecting his state
		in the environment. See each property for the details.
	
         */
 	public class ConfigChangeLocalImpl 
  						extends
  						ConfigChangeLocal
	    {
 	
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public ConfigChangeLocalImpl()
		{
		}
	
    	
    	
    	
    	/**
		 * Creates new instance of the message ConfigChange.
		 * 
		Asynchronous message. Message sent when the bot configuration
		changed - each agent has a lot of parameters affecting his state
		in the environment. See each property for the details.
	
		 * Corresponding GameBots message
		 *   (local part)
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
		 *     @param SyncNavpoints 
			Whether Navpoint.visible information is being exported by GB2004.
  	  
		 *   
		 * 
		 *   
		 *     @param VisionFOV 
			Field of view of the bot, in degrees.
  	  
		 *   
		 * 
		 *   
		 *     @param Action 
			Name of current BDI action.
		
		 *   
		 * 
		 */
		public ConfigChangeLocalImpl(
			UnrealId Id,  UnrealId BotId,  boolean ManualSpawn,  boolean AutoTrace,  String Name,  double SpeedMultiplier,  Rotation RotationRate,  boolean Invulnerable,  double SelfUpdateTime,  double VisionTime,  int LocUpdateMultiplier,  boolean ShowDebug,  boolean ShowFocalPoint,  boolean DrawTraceLines,  boolean SynchronousOff,  boolean AutoPickupOff,  boolean SyncNavpoints,  double VisionFOV,  String Action
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
				
					this.SyncNavpoints = SyncNavpoints;
				
					this.VisionFOV = VisionFOV;
				
					this.Action = Action;
				
		}
    
    	/**
		 * Cloning constructor from the full message.
		 *
		 * @param original
		 */
		public ConfigChangeLocalImpl(ConfigChange original) {		
			
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
				
					this.SyncNavpoints = original.isSyncNavpoints()
 	;
				
					this.VisionFOV = original.getVisionFOV()
 	;
				
					this.Action = original.getAction()
 	;
				
			this.SimTime = original.getSimTime();			
		}
		
		/**
		 * Cloning constructor from the full message.
		 *
		 * @param original
		 */
		public ConfigChangeLocalImpl(ConfigChangeLocalImpl original) {		
			
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
				
					this.SyncNavpoints = original.isSyncNavpoints()
 	;
				
					this.VisionFOV = original.getVisionFOV()
 	;
				
					this.Action = original.getAction()
 	;
				
			this.SimTime = original.getSimTime();
		}
		
			/**
			 * Cloning constructor from the message part.
			 *
			 * @param original
			 */
			public ConfigChangeLocalImpl(ConfigChangeLocal original) {
				
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
					
						this.SyncNavpoints = original.isSyncNavpoints()
 	;
					
						this.VisionFOV = original.getVisionFOV()
 	;
					
						this.Action = original.getAction()
 	;
					
			}
		
   				
   				@Override
   				public void setSimTime(long SimTime) {
					super.setSimTime(SimTime);
				}
   			
	    				@Override
	    				public 
	    				ConfigChangeLocalImpl clone() {
	    					return new 
	    					ConfigChangeLocalImpl(this);
	    				}
	    				
	    				
    	
	    /**
         * Id of this config message. This Id is generated from BotId, string "_CONFCH" is added at the end. 
         */
        protected
         UnrealId Id =
       	null;
	
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
         * 
			It enables/disables automatic pickup of the bot. If true the items can be picked up through PICK command.
		 
         */
        public  boolean isAutoPickupOff()
 	 {
				    					return AutoPickupOff;
				    				}
				    			
    	
	    /**
         * 
			Whether Navpoint.visible information is being exported by GB2004.
  	   
         */
        protected
         boolean SyncNavpoints =
       	false;
	
 		/**
         * 
			Whether Navpoint.visible information is being exported by GB2004.
  	   
         */
        public  boolean isSyncNavpoints()
 	 {
				    					return SyncNavpoints;
				    				}
				    			
    	
	    /**
         * 
			Field of view of the bot, in degrees.
  	   
         */
        protected
         double VisionFOV =
       	0;
	
 		/**
         * 
			Field of view of the bot, in degrees.
  	   
         */
        public  double getVisionFOV()
 	 {
				    					return VisionFOV;
				    				}
				    			
    	
	    /**
         * 
			Name of current BDI action.
		 
         */
        protected
         String Action =
       	null;
	
 		/**
         * 
			Name of current BDI action.
		 
         */
        public  String getAction()
 	 {
				    					return Action;
				    				}
				    			
    	
    	
    	
    	
    	public ConfigChangeLocalImpl getLocal() {
			return this;
    	}
		public ISharedWorldObject getShared() {
		 	throw new UnsupportedOperationException("Could not return LOCAL as SHARED");
		}
		public IStaticWorldObject getStatic() {
		    throw new UnsupportedOperationException("Could not return LOCAL as STATIC");
		}
 	
		public static class ConfigChangeLocalUpdate
     implements ILocalWorldObjectUpdatedEvent, IGBWorldObjectEvent
		{
			protected long time;
			
			protected ConfigChangeLocal data = null; //contains object data for this update
			
			public ConfigChangeLocalUpdate
    (ConfigChangeLocal moverLocal, long time)
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
					data = new ConfigChangeLocalImpl(data); //we always return Impl object
					return new IWorldObjectUpdateResult.WorldObjectUpdateResult<ILocalWorldObject>(IWorldObjectUpdateResult.Result.CREATED, data);
				}
				if ( object instanceof ConfigChangeLocalImpl )
				{
					ConfigChangeLocalImpl toUpdate = (ConfigChangeLocalImpl)object;
					
					boolean updated = false;
					
					// UPDATING LOCAL PROPERTIES
					
				if (!SafeEquals.equals(toUpdate.BotId, data.getBotId()
 	)) {
					toUpdate.BotId=data.getBotId()
 	;
					updated = true;
				}
			
				if (toUpdate.ManualSpawn != data.isManualSpawn()
 	) {
				    toUpdate.ManualSpawn=data.isManualSpawn()
 	;
					updated = true;
				}
			
				if (toUpdate.AutoTrace != data.isAutoTrace()
 	) {
				    toUpdate.AutoTrace=data.isAutoTrace()
 	;
					updated = true;
				}
			
				if (!SafeEquals.equals(toUpdate.Name, data.getName()
 	)) {
					toUpdate.Name=data.getName()
 	;
					updated = true;
				}
			
				if (toUpdate.SpeedMultiplier != data.getSpeedMultiplier()
 	) {
				    toUpdate.SpeedMultiplier=data.getSpeedMultiplier()
 	;
					updated = true;
				}
			
				if (!SafeEquals.equals(toUpdate.RotationRate, data.getRotationRate()
 	)) {
					toUpdate.RotationRate=data.getRotationRate()
 	;
					updated = true;
				}
			
				if (toUpdate.Invulnerable != data.isInvulnerable()
 	) {
				    toUpdate.Invulnerable=data.isInvulnerable()
 	;
					updated = true;
				}
			
				if (toUpdate.SelfUpdateTime != data.getSelfUpdateTime()
 	) {
				    toUpdate.SelfUpdateTime=data.getSelfUpdateTime()
 	;
					updated = true;
				}
			
				if (toUpdate.VisionTime != data.getVisionTime()
 	) {
				    toUpdate.VisionTime=data.getVisionTime()
 	;
					updated = true;
				}
			
				if (toUpdate.LocUpdateMultiplier != data.getLocUpdateMultiplier()
 	) {
				    toUpdate.LocUpdateMultiplier=data.getLocUpdateMultiplier()
 	;
					updated = true;
				}
			
				if (toUpdate.ShowDebug != data.isShowDebug()
 	) {
				    toUpdate.ShowDebug=data.isShowDebug()
 	;
					updated = true;
				}
			
				if (toUpdate.ShowFocalPoint != data.isShowFocalPoint()
 	) {
				    toUpdate.ShowFocalPoint=data.isShowFocalPoint()
 	;
					updated = true;
				}
			
				if (toUpdate.DrawTraceLines != data.isDrawTraceLines()
 	) {
				    toUpdate.DrawTraceLines=data.isDrawTraceLines()
 	;
					updated = true;
				}
			
				if (toUpdate.SynchronousOff != data.isSynchronousOff()
 	) {
				    toUpdate.SynchronousOff=data.isSynchronousOff()
 	;
					updated = true;
				}
			
				if (toUpdate.AutoPickupOff != data.isAutoPickupOff()
 	) {
				    toUpdate.AutoPickupOff=data.isAutoPickupOff()
 	;
					updated = true;
				}
			
				if (toUpdate.SyncNavpoints != data.isSyncNavpoints()
 	) {
				    toUpdate.SyncNavpoints=data.isSyncNavpoints()
 	;
					updated = true;
				}
			
				if (toUpdate.VisionFOV != data.getVisionFOV()
 	) {
				    toUpdate.VisionFOV=data.getVisionFOV()
 	;
					updated = true;
				}
			
				if (!SafeEquals.equals(toUpdate.Action, data.getAction()
 	)) {
					toUpdate.Action=data.getAction()
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
				throw new PogamutException("Unsupported object type for update. Expected ConfigChangeLocalImpl for object " + object.getId() +", not object of class " + object.getClass().getSimpleName() + ".", this);
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
		              		
		              			"SyncNavpoints = " + String.valueOf(isSyncNavpoints()
 	) + " | " + 
		              		
		              			"VisionFOV = " + String.valueOf(getVisionFOV()
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
		              		
		              			"<b>SyncNavpoints</b> = " + String.valueOf(isSyncNavpoints()
 	) + " <br/> " + 
		              		
		              			"<b>VisionFOV</b> = " + String.valueOf(getVisionFOV()
 	) + " <br/> " + 
		              		
		              			"<b>Action</b> = " + String.valueOf(getAction()
 	) + " <br/> " + 
		              		
				"<br/>]";     
		}
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=local+classtype[@name=impl]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=local+classtype[@name=impl]) ---        	            	
 	
		}
 	