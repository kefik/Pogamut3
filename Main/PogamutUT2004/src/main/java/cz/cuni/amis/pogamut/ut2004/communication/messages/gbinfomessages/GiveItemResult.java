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
         			Definition of the event GIVERES.
         		
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Aynchronous message. Response to GIVE command. Here we get the information about the result of our GIVE command. 
	
         */
 	public class GiveItemResult 
  				extends InfoMessage
    			implements IWorldEvent, IWorldChangeEvent
    			
	    {
 	
    	/** Example how the message looks like - used during parser tests. */
    	public static final String PROTOTYPE =
    		"GIVERES {TargetId unreal_id}  {ItemId unreal_id}  {ItemType text}  {Result False}  {Reason text} ";
    
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public GiveItemResult()
		{
		}
	
    	
    	
    	
    	/**
		 * Creates new instance of the message GiveItemResult.
		 * 
		Aynchronous message. Response to GIVE command. Here we get the information about the result of our GIVE command. 
	
		 * Corresponding GameBots message
		 *   
		 *   is
		 *   GIVERES.
		 * 
 	  	 * 
		 *   
		 *     @param TargetId 
			An Id of the bot we tried to send item to.
		
		 *   
		 * 
		 *   
		 *     @param ItemId 
			If the command was a success then here we receive Id of the item that was given to bot.
		
		 *   
		 * 
		 *   
		 *     @param ItemType 
			Class of the item we wanted to give to the bot.
		
		 *   
		 * 
		 *   
		 *     @param Result 
		Boolean result containing whether the give command was successful.			
		
		 *   
		 * 
		 *   
		 *     @param Reason 
			String reason why the give command was or wasn't successful. Can be BOT_NOT_FOUND_OR_BOT_PAWN_NONE, MAX_DISTANCE_EXCEEDED, WRONG_ITEM_TYPE and SUCCESS.
		
		 *   
		 * 
		 */
		public GiveItemResult(
			UnrealId TargetId,  UnrealId ItemId,  String ItemType,  Boolean Result,  String Reason
		) {
			
					this.TargetId = TargetId;
				
					this.ItemId = ItemId;
				
					this.ItemType = ItemType;
				
					this.Result = Result;
				
					this.Reason = Reason;
				
		}
    
    	/**
		 * Cloning constructor from the full message.
		 *
		 * @param original
		 */
		public GiveItemResult(GiveItemResult original) {		
			
					this.TargetId = original.getTargetId()
 	;
				
					this.ItemId = original.getItemId()
 	;
				
					this.ItemType = original.getItemType()
 	;
				
					this.Result = original.isResult()
 	;
				
					this.Reason = original.getReason()
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
			An Id of the bot we tried to send item to.
		 
         */
        protected
         UnrealId TargetId =
       	null;
	
 		/**
         * 
			An Id of the bot we tried to send item to.
		 
         */
        public  UnrealId getTargetId()
 	 {
    					return TargetId;
    				}
    			
    	
	    /**
         * 
			If the command was a success then here we receive Id of the item that was given to bot.
		 
         */
        protected
         UnrealId ItemId =
       	null;
	
 		/**
         * 
			If the command was a success then here we receive Id of the item that was given to bot.
		 
         */
        public  UnrealId getItemId()
 	 {
    					return ItemId;
    				}
    			
    	
	    /**
         * 
			Class of the item we wanted to give to the bot.
		 
         */
        protected
         String ItemType =
       	null;
	
 		/**
         * 
			Class of the item we wanted to give to the bot.
		 
         */
        public  String getItemType()
 	 {
    					return ItemType;
    				}
    			
    	
	    /**
         * 
		Boolean result containing whether the give command was successful.			
		 
         */
        protected
         Boolean Result =
       	null;
	
 		/**
         * 
		Boolean result containing whether the give command was successful.			
		 
         */
        public  Boolean isResult()
 	 {
    					return Result;
    				}
    			
    	
	    /**
         * 
			String reason why the give command was or wasn't successful. Can be BOT_NOT_FOUND_OR_BOT_PAWN_NONE, MAX_DISTANCE_EXCEEDED, WRONG_ITEM_TYPE and SUCCESS.
		 
         */
        protected
         String Reason =
       	null;
	
 		/**
         * 
			String reason why the give command was or wasn't successful. Can be BOT_NOT_FOUND_OR_BOT_PAWN_NONE, MAX_DISTANCE_EXCEEDED, WRONG_ITEM_TYPE and SUCCESS.
		 
         */
        public  String getReason()
 	 {
    					return Reason;
    				}
    			
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
		              			"TargetId = " + String.valueOf(getTargetId()
 	) + " | " + 
		              		
		              			"ItemId = " + String.valueOf(getItemId()
 	) + " | " + 
		              		
		              			"ItemType = " + String.valueOf(getItemType()
 	) + " | " + 
		              		
		              			"Result = " + String.valueOf(isResult()
 	) + " | " + 
		              		
		              			"Reason = " + String.valueOf(getReason()
 	) + " | " + 
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>TargetId</b> = " + String.valueOf(getTargetId()
 	) + " <br/> " + 
		              		
		              			"<b>ItemId</b> = " + String.valueOf(getItemId()
 	) + " <br/> " + 
		              		
		              			"<b>ItemType</b> = " + String.valueOf(getItemType()
 	) + " <br/> " + 
		              		
		              			"<b>Result</b> = " + String.valueOf(isResult()
 	) + " <br/> " + 
		              		
		              			"<b>Reason</b> = " + String.valueOf(getReason()
 	) + " <br/> " + 
		              		
				"<br/>]";     
		}
 	
 	    public String toJsonLiteral() {
            return "giveitemresult( "
            		+
									(getTargetId()
 	 == null ? "null" :
										"\"" + getTargetId()
 	.getStringId() + "\"" 
									)
								+ ", " + 
									(getItemId()
 	 == null ? "null" :
										"\"" + getItemId()
 	.getStringId() + "\"" 
									)
								+ ", " + 
									(getItemType()
 	 == null ? "null" :
										"\"" + getItemType()
 	 + "\"" 
									)
								+ ", " + 
								    String.valueOf(isResult()
 	)									
								+ ", " + 
									(getReason()
 	 == null ? "null" :
										"\"" + getReason()
 	 + "\"" 
									)
								
                   + ")";
        }
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=event+classtype[@name=impl]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=event+classtype[@name=impl]) ---        	            	
 	
		}
 	