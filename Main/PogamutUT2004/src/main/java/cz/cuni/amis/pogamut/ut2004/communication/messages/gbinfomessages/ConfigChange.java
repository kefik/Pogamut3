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
            				Abstract definition of the GameBots2004 message CONFCH.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Asynchronous message. Message sent when the bot configuration
		changed - each agent has a lot of parameters affecting his state
		in the environment. See each property for the details.
	
         */
 	public abstract class ConfigChange   
  				extends 
  				InfoMessage
  						implements IWorldEvent, IWorldChangeEvent, ICompositeWorldObject
  						
	    {
 	
    	/** Example how the message looks like - used during parser tests. */
    	public static final String PROTOTYPE =
    		"CONFCH {Id unreal_id}  {BotId unreal_id}  {ManualSpawn False}  {AutoTrace False}  {Name text}  {SpeedMultiplier 0}  {RotationRate 0,0,0}  {Invulnerable False}  {SelfUpdateTime 0}  {VisionTime 0}  {LocUpdateMultiplier 0}  {ShowDebug False}  {ShowFocalPoint False}  {DrawTraceLines False}  {SynchronousOff False}  {AutoPickupOff False}  {Action text} ";
    
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public ConfigChange()
		{
		}
	
				// abstract message, it does not have any more constructors				
			
	   		
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
         * Id of this config message. This Id is generated from BotId, string "_CONFCH" is added at the end. 
         */
        public abstract UnrealId getId()
 	;
		    			
 		/**
         * Unique Id of the bot. 
         */
        public abstract UnrealId getBotId()
 	;
		    			
 		/**
         * 
			True if we have to spawn the bot manually after each death
		 
         */
        public abstract boolean isManualSpawn()
 	;
		    			
 		/**
         * 
			True if the bot is using auto ray tracing (is provided with
			synchronous ATR messages). See ATR messages for more
			details.
		 
         */
        public abstract boolean isAutoTrace()
 	;
		    			
 		/**
         * The bot's name. 
         */
        public abstract String getName()
 	;
		    			
 		/**
         * 
			Bots default speed will be multiplied by this number. Ranges from 0.1 to 2 (default, can be set in ini in [GameBots2004.RemoteBot] MaxSpeed).
		 
         */
        public abstract double getSpeedMultiplier()
 	;
		    			
 		/**
         * 
			Bot rotation rate. Default rotation rate is: (Pitch=3072,Yaw=60000,Roll=2048) and may be configured in ini file in [GameBots2004.RemoteBot] DefaultRotationRate. (pitch - up/down, yaw - left/right, roll - equivalent of doing a cartwheel)
		 
         */
        public abstract Rotation getRotationRate()
 	;
		    			
 		/**
         * 
			If bot is invulnerable (cannot die) or not.
		 
         */
        public abstract boolean isInvulnerable()
 	;
		    			
 		/**
         * 
			The delay between two self message synchronous batches 
			(can range from 0.01 to 2 seconds). Will be used only if NewSelfBatchProtocol
			attribute is set to true in INIT message.
		 
         */
        public abstract double getSelfUpdateTime()
 	;
		    			
 		/**
         * 
			The delay between two synchronous batches containing vision updates
			(can range from 0.1 to 2 seconds). If NewSelfBatchProtocol
			attribute is set to true in INIT message, more batch messages containing only
			SELF message will arrive between two vision update batches (containing PLR,PRJ,INV.. messages).
		 
         */
        public abstract double getVisionTime()
 	;
		    			
 		/**
         * 
			Holds information how many times faster is exported location update message (UPD) compared to sync. batch, e.g. when this multiplier is set to 5 and vision time is 250 ms, UPD message will arrive every 50 ms.
		 
         */
        public abstract int getLocUpdateMultiplier()
 	;
		    			
 		/**
         * 
			If some additional debug information will be shown in the
			UT2004 server console window.
		 
         */
        public abstract boolean isShowDebug()
 	;
		    			
 		/**
         * 
			If true an actor visualizing the location the bot is
			actually looking at will appear in the game.
		 
         */
        public abstract boolean isShowFocalPoint()
 	;
		    			
 		/**
         * 
			if the GB should draw lines representing the auto ray traces
			of the bot (for more information see ATR message).
		 
         */
        public abstract boolean isDrawTraceLines()
 	;
		    			
 		/**
         * 
			It informs if sending of all GB synchronous messages is
			enabled or disabled.
		 
         */
        public abstract boolean isSynchronousOff()
 	;
		    			
 		/**
         * 
			It enables/disables automatic pickup of the bot. If true the items can be picked up through PICK command.
		 
         */
        public abstract boolean isAutoPickupOff()
 	;
		    			
 		/**
         * 
			Name of current BDI action.
		 
         */
        public abstract String getAction()
 	;
		    			
    	
    	public static class ConfigChangeUpdate
     extends GBObjectUpdate implements ICompositeWorldObjectUpdatedEvent, IGBWorldObjectEvent {	
			private ConfigChange object;
			private long time;
			private ITeamId teamId;
			
			public ConfigChangeUpdate
    (ConfigChange source, long eventTime, ITeamId teamId) {
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
				return new ConfigChangeLocalImpl.ConfigChangeLocalUpdate
    ((ConfigChangeLocal)object.getLocal(), time);
			}
	
			@Override
			public ISharedWorldObjectUpdatedEvent getSharedEvent() {
				return new ConfigChangeSharedImpl.ConfigChangeSharedUpdate
    ((ConfigChangeShared)object.getShared(), time, teamId);
			}
	
			@Override
			public IStaticWorldObjectUpdatedEvent getStaticEvent() {
				return new ConfigChangeStaticImpl.ConfigChangeStaticUpdate
    ((ConfigChangeStatic)object.getStatic(), time);
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
 	
 	    public String toJsonLiteral() {
            return "configchange( "
            		+
									(getId()
 	 == null ? "null" :
										"\"" + getId()
 	.getStringId() + "\"" 
									)
								+ ", " + 
									(getBotId()
 	 == null ? "null" :
										"\"" + getBotId()
 	.getStringId() + "\"" 
									)
								+ ", " + 
								    String.valueOf(isManualSpawn()
 	)									
								+ ", " + 
								    String.valueOf(isAutoTrace()
 	)									
								+ ", " + 
									(getName()
 	 == null ? "null" :
										"\"" + getName()
 	 + "\"" 
									)
								+ ", " + 
								    String.valueOf(getSpeedMultiplier()
 	)									
								+ ", " + 
									(getRotationRate()
 	 == null ? "null" :
										"[" + getRotationRate()
 	.getPitch() + ", " + getRotationRate()
 	.getYaw() + ", " + getRotationRate()
 	.getRoll() + "]" 
									)								    
								+ ", " + 
								    String.valueOf(isInvulnerable()
 	)									
								+ ", " + 
								    String.valueOf(getSelfUpdateTime()
 	)									
								+ ", " + 
								    String.valueOf(getVisionTime()
 	)									
								+ ", " + 
								    String.valueOf(getLocUpdateMultiplier()
 	)									
								+ ", " + 
								    String.valueOf(isShowDebug()
 	)									
								+ ", " + 
								    String.valueOf(isShowFocalPoint()
 	)									
								+ ", " + 
								    String.valueOf(isDrawTraceLines()
 	)									
								+ ", " + 
								    String.valueOf(isSynchronousOff()
 	)									
								+ ", " + 
								    String.valueOf(isAutoPickupOff()
 	)									
								+ ", " + 
									(getAction()
 	 == null ? "null" :
										"\"" + getAction()
 	 + "\"" 
									)
								
                   + ")";
        }
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=base+classtype[@name=abstract]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=base+classtype[@name=abstract]) ---        	            	
 	
		}
 	