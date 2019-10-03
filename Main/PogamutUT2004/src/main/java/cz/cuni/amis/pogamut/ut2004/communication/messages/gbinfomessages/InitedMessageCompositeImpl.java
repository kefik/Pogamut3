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
            		Composite implementation of the INITED abstract message. It wraps Local/Shared/Static parts in single object
            		allowing to presenting a nice facade for users.
            	
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Asynchronous message. Sent after succesfull init command (so usually just once). 
		Holds many attributes of the bots like speed, id starting and max health, etc.
		Some attributes are not used due to GameBots mechanics.
	
         */
 	public class InitedMessageCompositeImpl 
  				extends InitedMessage
	    {
 	
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public InitedMessageCompositeImpl()
		{
		}
	
		
		/**
		 * Composite-impl constructor. It assembles the message from its three fragments - local/shared/static.
		 *
		 * @param partLocal local-part of the message
		 * @param partShared shared-part of the message
		 * @param partStatic static-part of the message
		 */
		public InitedMessageCompositeImpl(
			InitedMessageLocalImpl partLocal,
			InitedMessageSharedImpl partShared,
			InitedMessageStaticImpl partStatic
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
		public InitedMessageCompositeImpl(InitedMessageCompositeImpl original) {
			this.partLocal = partLocal;
			this.partShared = partShared;
			this.partStatic = partStatic;
		}
    
						
						public UnrealId getId() {						
							return cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage.InitedMessageId;
						}
					
   				
   				@Override
   				public void setSimTime(long SimTime) {
					super.setSimTime(SimTime);
				}
   			
    			
    			protected 
    			InitedMessageStaticImpl
    			partStatic;
    			
    			@Override
				public InitedMessageStatic getStatic() {
					return partStatic;
				}
    			
    			protected
    			InitedMessageLocalImpl
    			partLocal;
    	
    			@Override
				public InitedMessageLocal getLocal() {
					return partLocal;
				}
			
    			InitedMessageSharedImpl
    			partShared;
    			
				@Override
				public InitedMessageShared getShared() {
					return partShared;
				}
			
				
  				
  					@Override
    				
 		/**
         * 
			A unique unreal Id of the new bot.
		 
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
			Bot will always start with this health amount (usually 100). 
		 
         */
        public  int getHealthStart()
 	 {
    					return 
    						
    								partLocal.
    							getHealthStart()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			Full health of the bot (usually 100).
		 
         */
        public  int getHealthFull()
 	 {
    					return 
    						
    								partLocal.
    							getHealthFull()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			Maximum health of the bot (default 199).
		 
         */
        public  int getHealthMax()
 	 {
    					return 
    						
    								partLocal.
    							getHealthMax()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			Amount of adrenaline at the start. Usually 0.
		 
         */
        public  double getAdrenalineStart()
 	 {
    					return 
    						
    								partLocal.
    							getAdrenalineStart()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			Maxium amount of the adrenaline. Usually 100 (this can trigger the combos).
		 
         */
        public  double getAdrenalineMax()
 	 {
    					return 
    						
    								partLocal.
    							getAdrenalineMax()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			Starting strength of the bot armor (usually 0).
		 
         */
        public  int getShieldStrengthStart()
 	 {
    					return 
    						
    								partLocal.
    							getShieldStrengthStart()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			Maximum strength of the bot armor (usually 150).
		 
         */
        public  int getShieldStrengthMax()
 	 {
    					return 
    						
    								partLocal.
    							getShieldStrengthMax()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			Maximum amount of succesing jumps. Currently limited to double jump in GB.
		 
         */
        public  int getMaxMultiJump()
 	 {
    					return 
    						
    								partLocal.
    							getMaxMultiJump()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			Damage scaling for this bot. (he will deal reduced damage depending on the setting).
		 
         */
        public  double getDamageScaling()
 	 {
    					return 
    						
    								partLocal.
    							getDamageScaling()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			Groundspeed of the bot (on the ground). Default 440.
		 
         */
        public  double getGroundSpeed()
 	 {
    					return 
    						
    								partLocal.
    							getGroundSpeed()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			Waterspeed of the bot (in the water).
		 
         */
        public  double getWaterSpeed()
 	 {
    					return 
    						
    								partLocal.
    							getWaterSpeed()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			AirSpeed of the bot (in the air).
		 
         */
        public  double getAirSpeed()
 	 {
    					return 
    						
    								partLocal.
    							getAirSpeed()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			Ladderspeed of the bot (on the ladder).
		 
         */
        public  double getLadderSpeed()
 	 {
    					return 
    						
    								partLocal.
    							getLadderSpeed()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			Accelartion rate of this bot. How fast he accelerates.
		 
         */
        public  double getAccelRate()
 	 {
    					return 
    						
    								partLocal.
    							getAccelRate()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			 Bot Jump's Z boost.
		 
         */
        public  double getJumpZ()
 	 {
    					return 
    						
    								partLocal.
    							getJumpZ()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			Not used in GB.
		 
         */
        public  double getMultiJumpBoost()
 	 {
    					return 
    						
    								partLocal.
    							getMultiJumpBoost()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			 Max fall speed of the bot.
		 
         */
        public  double getMaxFallSpeed()
 	 {
    					return 
    						
    								partLocal.
    							getMaxFallSpeed()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			Dodge speed factor.
		 
         */
        public  double getDodgeSpeedFactor()
 	 {
    					return 
    						
    								partLocal.
    							getDodgeSpeedFactor()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			Dodge jump Z boost of the bot. 
		 
         */
        public  double getDodgeSpeedZ()
 	 {
    					return 
    						
    								partLocal.
    							getDodgeSpeedZ()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			How well can be the bot controlled in the air (ranges from 0 to 1).
		 
         */
        public  double getAirControl()
 	 {
    					return 
    						
    								partLocal.
    							getAirControl()
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
 	