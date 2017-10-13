package cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages;
 		
 		// --- IMPORTS FROM /messages/settings/javasettings/javaimport BEGIN
			import java.util.*;import javax.vecmath.*;import cz.cuni.amis.pogamut.base.communication.messages.*;import cz.cuni.amis.pogamut.base.communication.worldview.*;import cz.cuni.amis.pogamut.base.communication.worldview.event.*;import cz.cuni.amis.pogamut.base.communication.worldview.object.*;import cz.cuni.amis.pogamut.multi.communication.worldview.object.*;import cz.cuni.amis.pogamut.base.communication.translator.event.*;import cz.cuni.amis.pogamut.multi.communication.translator.event.*;import cz.cuni.amis.pogamut.base3d.worldview.object.*;import cz.cuni.amis.pogamut.base3d.worldview.object.event.*;import cz.cuni.amis.pogamut.ut2004.communication.messages.*;import cz.cuni.amis.pogamut.ut2004.communication.worldview.objects.*;import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.objects.*;import cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor.*;import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType.Category;import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;import cz.cuni.amis.utils.exception.*;import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdateResult.Result;import cz.cuni.amis.utils.SafeEquals;import cz.cuni.amis.pogamut.base.agent.*;import cz.cuni.amis.pogamut.multi.agent.*;import cz.cuni.amis.pogamut.multi.communication.worldview.property.*;import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.property.*;import cz.cuni.amis.utils.token.*;import cz.cuni.amis.utils.*;
		// --- IMPORTS FROM /messages/settings/javasettings/javaimport END
		
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name='all'] BEGIN
				
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name='all'] END
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name=composite]+classtype[@name=impl] BEGIN
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name=composite]+classtype[@name=impl] END
    
 		/**
         *  
            		Composite implementation of the CONFCH abstract message. It wraps Local/Shared/Static parts in single object
            		allowing to presenting a nice facade for users.
            	
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Asynchronous message. Message sent when the bot configuration
		changed - each agent has a lot of parameters affecting his state
		in the environment. See each property for the details.
	
         */
 	public class ConfigChangeCompositeImpl 
  				extends ConfigChange
	    {
 	
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public ConfigChangeCompositeImpl()
		{
		}
	
		
		/**
		 * Composite-impl constructor. It assembles the message from its three fragments - local/shared/static.
		 *
		 * @param partLocal local-part of the message
		 * @param partShared shared-part of the message
		 * @param partStatic static-part of the message
		 */
		public ConfigChangeCompositeImpl(
			ConfigChangeLocalImpl partLocal,
			ConfigChangeSharedImpl partShared,
			ConfigChangeStaticImpl partStatic
		) {
			this.partLocal  = partLocal;
			this.partShared = partShared;
			this.partStatic = partStatic;
		}
		
		/**
		 * Cloning constructor.
		 *
		 * @param original		 
		 */
		public ConfigChangeCompositeImpl(ConfigChangeCompositeImpl original) {
			this.partLocal = partLocal;
			this.partShared = partShared;
			this.partStatic = partStatic;
		}
    
   				
   				@Override
   				public void setSimTime(long SimTime) {
					super.setSimTime(SimTime);
				}
   			
    			
    			protected 
    			ConfigChangeStaticImpl
    			partStatic;
    			
    			@Override
				public ConfigChangeStatic getStatic() {
					return partStatic;
				}
    			
    			protected
    			ConfigChangeLocalImpl
    			partLocal;
    	
    			@Override
				public ConfigChangeLocal getLocal() {
					return partLocal;
				}
			
    			ConfigChangeSharedImpl
    			partShared;
    			
				@Override
				public ConfigChangeShared getShared() {
					return partShared;
				}
			
				
  				
  					@Override
    				
 		/**
         * Id of this config message. This Id is generated from BotId, string "_CONFCH" is added at the end. 
         */
        public  UnrealId getId()
 	 {
    					return 
    						
    								partStatic.
    							getId()
 	;
    				}
    			
  					@Override
    				
 		/**
         * Unique Id of the bot. 
         */
        public  UnrealId getBotId()
 	 {
    					return 
    						
    								partLocal.
    							getBotId()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			True if we have to spawn the bot manually after each death
		 
         */
        public  boolean isManualSpawn()
 	 {
    					return 
    						
    								partLocal.
    							isManualSpawn()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			True if the bot is using auto ray tracing (is provided with
			synchronous ATR messages). See ATR messages for more
			details.
		 
         */
        public  boolean isAutoTrace()
 	 {
    					return 
    						
    								partLocal.
    							isAutoTrace()
 	;
    				}
    			
  					@Override
    				
 		/**
         * The bot's name. 
         */
        public  String getName()
 	 {
    					return 
    						
    								partLocal.
    							getName()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			Bots default speed will be multiplied by this number. Ranges from 0.1 to 2 (default, can be set in ini in [GameBots2004.RemoteBot] MaxSpeed).
		 
         */
        public  double getSpeedMultiplier()
 	 {
    					return 
    						
    								partLocal.
    							getSpeedMultiplier()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			Bot rotation rate. Default rotation rate is: (Pitch=3072,Yaw=60000,Roll=2048) and may be configured in ini file in [GameBots2004.RemoteBot] DefaultRotationRate. (pitch - up/down, yaw - left/right, roll - equivalent of doing a cartwheel)
		 
         */
        public  Rotation getRotationRate()
 	 {
    					return 
    						
    								partLocal.
    							getRotationRate()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			If bot is invulnerable (cannot die) or not.
		 
         */
        public  boolean isInvulnerable()
 	 {
    					return 
    						
    								partLocal.
    							isInvulnerable()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			The delay between two self message synchronous batches 
			(can range from 0.01 to 2 seconds). Will be used only if NewSelfBatchProtocol
			attribute is set to true in INIT message.
		 
         */
        public  double getSelfUpdateTime()
 	 {
    					return 
    						
    								partLocal.
    							getSelfUpdateTime()
 	;
    				}
    			
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
    					return 
    						
    								partLocal.
    							getVisionTime()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			Holds information how many times faster is exported location update message (UPD) compared to sync. batch, e.g. when this multiplier is set to 5 and vision time is 250 ms, UPD message will arrive every 50 ms.
		 
         */
        public  int getLocUpdateMultiplier()
 	 {
    					return 
    						
    								partLocal.
    							getLocUpdateMultiplier()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			If some additional debug information will be shown in the
			UT2004 server console window.
		 
         */
        public  boolean isShowDebug()
 	 {
    					return 
    						
    								partLocal.
    							isShowDebug()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			If true an actor visualizing the location the bot is
			actually looking at will appear in the game.
		 
         */
        public  boolean isShowFocalPoint()
 	 {
    					return 
    						
    								partLocal.
    							isShowFocalPoint()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			if the GB should draw lines representing the auto ray traces
			of the bot (for more information see ATR message).
		 
         */
        public  boolean isDrawTraceLines()
 	 {
    					return 
    						
    								partLocal.
    							isDrawTraceLines()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			It informs if sending of all GB synchronous messages is
			enabled or disabled.
		 
         */
        public  boolean isSynchronousOff()
 	 {
    					return 
    						
    								partLocal.
    							isSynchronousOff()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			It enables/disables automatic pickup of the bot. If true the items can be picked up through PICK command.
		 
         */
        public  boolean isAutoPickupOff()
 	 {
    					return 
    						
    								partLocal.
    							isAutoPickupOff()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			Name of current BDI action.
		 
         */
        public  String getAction()
 	 {
    					return 
    						
    								partLocal.
    							getAction()
 	;
    				}
    			
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
            			"Static = " + String.valueOf(partStatic) + " | Local = " + String.valueOf(partLocal) + " | Shared = " + String.valueOf(partShared) + " ]" +
            		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
            			"Static = " + String.valueOf(partStatic) + " <br/> Local = " + String.valueOf(partLocal) + " <br/> Shared = " + String.valueOf(partShared) + " ]" +
            		
				"<br/>]";     
		}
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=composite+classtype[@name=impl]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=composite+classtype[@name=impl]) ---        	            	
 	
		}
 	