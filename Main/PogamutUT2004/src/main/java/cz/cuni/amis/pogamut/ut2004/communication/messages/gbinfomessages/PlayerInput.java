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
         			Definition of the event PLI.
         		
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Aynchronous message. When the player has our bot selected by mouse cursor, he can provide the bot with key input (keys 0 - 9 represented by int 0-9 and key T represented by 10). If the player has some dialog on the HUD, Id of the dialog will be sent (if any).
	
         */
 	public class PlayerInput 
  				extends InfoMessage
    			implements IWorldEvent, IWorldChangeEvent
    			
	    {
 	
    	/** Example how the message looks like - used during parser tests. */
    	public static final String PROTOTYPE =
    		"PLI {Id unreal_id}  {DialogId text}  {Key 0} ";
    
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public PlayerInput()
		{
		}
	
    	
    	
    	
    	/**
		 * Creates new instance of the message PlayerInput.
		 * 
		Aynchronous message. When the player has our bot selected by mouse cursor, he can provide the bot with key input (keys 0 - 9 represented by int 0-9 and key T represented by 10). If the player has some dialog on the HUD, Id of the dialog will be sent (if any).
	
		 * Corresponding GameBots message
		 *   
		 *   is
		 *   PLI.
		 * 
 	  	 * 
		 *   
		 *     @param Id 
			Id of the player that sent the input.
		
		 *   
		 * 
		 *   
		 *     @param DialogId 
			An Id of the dialog (if set any) that the player has visible on the HUD. This attribute won't be sent at all if DialogId is "" or if no dialog on player HUD.
		
		 *   
		 * 
		 *   
		 *     @param Key 
			Which key was pressed. Supported keys: 0 - 9 represented by int 0-9 and key T represented by 10/
		
		 *   
		 * 
		 */
		public PlayerInput(
			UnrealId Id,  String DialogId,  int Key
		) {
			
					this.Id = Id;
				
					this.DialogId = DialogId;
				
					this.Key = Key;
				
		}
    
    	/**
		 * Cloning constructor from the full message.
		 *
		 * @param original
		 */
		public PlayerInput(PlayerInput original) {		
			
					this.Id = original.getId()
 	;
				
					this.DialogId = original.getDialogId()
 	;
				
					this.Key = original.getKey()
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
			Id of the player that sent the input.
		 
         */
        protected
         UnrealId Id =
       	null;
	
 		/**
         * 
			Id of the player that sent the input.
		 
         */
        public  UnrealId getId()
 	 {
    					return Id;
    				}
    			
    	
	    /**
         * 
			An Id of the dialog (if set any) that the player has visible on the HUD. This attribute won't be sent at all if DialogId is "" or if no dialog on player HUD.
		 
         */
        protected
         String DialogId =
       	null;
	
 		/**
         * 
			An Id of the dialog (if set any) that the player has visible on the HUD. This attribute won't be sent at all if DialogId is "" or if no dialog on player HUD.
		 
         */
        public  String getDialogId()
 	 {
    					return DialogId;
    				}
    			
    	
	    /**
         * 
			Which key was pressed. Supported keys: 0 - 9 represented by int 0-9 and key T represented by 10/
		 
         */
        protected
         int Key =
       	0;
	
 		/**
         * 
			Which key was pressed. Supported keys: 0 - 9 represented by int 0-9 and key T represented by 10/
		 
         */
        public  int getKey()
 	 {
    					return Key;
    				}
    			
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
		              			"Id = " + String.valueOf(getId()
 	) + " | " + 
		              		
		              			"DialogId = " + String.valueOf(getDialogId()
 	) + " | " + 
		              		
		              			"Key = " + String.valueOf(getKey()
 	) + " | " + 
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>Id</b> = " + String.valueOf(getId()
 	) + " <br/> " + 
		              		
		              			"<b>DialogId</b> = " + String.valueOf(getDialogId()
 	) + " <br/> " + 
		              		
		              			"<b>Key</b> = " + String.valueOf(getKey()
 	) + " <br/> " + 
		              		
				"<br/>]";     
		}
 	
 	    public String toJsonLiteral() {
            return "playerinput( "
            		+
									(getId()
 	 == null ? "null" :
										"\"" + getId()
 	.getStringId() + "\"" 
									)
								+ ", " + 
									(getDialogId()
 	 == null ? "null" :
										"\"" + getDialogId()
 	 + "\"" 
									)
								+ ", " + 
								    String.valueOf(getKey()
 	)									
								
                   + ")";
        }
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=event+classtype[@name=impl]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=event+classtype[@name=impl]) ---        	            	
 	
		}
 	