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
         			Definition of the event VMS.
         		
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		tAsynchronous message. Recieved message from global chat channel
	
         */
 	public class GlobalChat 
  				extends InfoMessage
    			implements IWorldEvent, IWorldChangeEvent
    			
	    {
 	
    	/** Example how the message looks like - used during parser tests. */
    	public static final String PROTOTYPE =
    		"VMS {Id unreal_id}  {Name text}  {ControlServer False}  {Text text} ";
    
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public GlobalChat()
		{
		}
	
    	
    	
    	
    	/**
		 * Creates new instance of the message GlobalChat.
		 * 
		tAsynchronous message. Recieved message from global chat channel
	
		 * Corresponding GameBots message
		 *   
		 *   is
		 *   VMS.
		 * 
 	  	 * 
		 *   
		 *     @param Id 
			Unique UnrealId of the sender.
		
		 *   
		 * 
		 *   
		 *     @param Name 
			Human readable name of the sender.
		
		 *   
		 * 
		 *   
		 *     @param ControlServer 
			True if this message was sent by control server - in that case the id will be id of control server (that has no physical appearance in the game).
		
		 *   
		 * 
		 *   
		 *     @param Text 
			A human readable message sent by another player in the game
			on the global channel.
		
		 *   
		 * 
		 */
		public GlobalChat(
			UnrealId Id,  String Name,  Boolean ControlServer,  String Text
		) {
			
					this.Id = Id;
				
					this.Name = Name;
				
					this.ControlServer = ControlServer;
				
					this.Text = Text;
				
		}
    
    	/**
		 * Cloning constructor from the full message.
		 *
		 * @param original
		 */
		public GlobalChat(GlobalChat original) {		
			
					this.Id = original.getId()
 	;
				
					this.Name = original.getName()
 	;
				
					this.ControlServer = original.isControlServer()
 	;
				
					this.Text = original.getText()
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
			Unique UnrealId of the sender.
		 
         */
        protected
         UnrealId Id =
       	null;
	
 		/**
         * 
			Unique UnrealId of the sender.
		 
         */
        public  UnrealId getId()
 	 {
    					return Id;
    				}
    			
    	
	    /**
         * 
			Human readable name of the sender.
		 
         */
        protected
         String Name =
       	null;
	
 		/**
         * 
			Human readable name of the sender.
		 
         */
        public  String getName()
 	 {
    					return Name;
    				}
    			
    	
	    /**
         * 
			True if this message was sent by control server - in that case the id will be id of control server (that has no physical appearance in the game).
		 
         */
        protected
         Boolean ControlServer =
       	null;
	
 		/**
         * 
			True if this message was sent by control server - in that case the id will be id of control server (that has no physical appearance in the game).
		 
         */
        public  Boolean isControlServer()
 	 {
    					return ControlServer;
    				}
    			
    	
	    /**
         * 
			A human readable message sent by another player in the game
			on the global channel.
		 
         */
        protected
         String Text =
       	null;
	
 		/**
         * 
			A human readable message sent by another player in the game
			on the global channel.
		 
         */
        public  String getText()
 	 {
    					return Text;
    				}
    			
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
		              			"Id = " + String.valueOf(getId()
 	) + " | " + 
		              		
		              			"Name = " + String.valueOf(getName()
 	) + " | " + 
		              		
		              			"ControlServer = " + String.valueOf(isControlServer()
 	) + " | " + 
		              		
		              			"Text = " + String.valueOf(getText()
 	) + " | " + 
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>Id</b> = " + String.valueOf(getId()
 	) + " <br/> " + 
		              		
		              			"<b>Name</b> = " + String.valueOf(getName()
 	) + " <br/> " + 
		              		
		              			"<b>ControlServer</b> = " + String.valueOf(isControlServer()
 	) + " <br/> " + 
		              		
		              			"<b>Text</b> = " + String.valueOf(getText()
 	) + " <br/> " + 
		              		
				"<br/>]";     
		}
 	
 	    public String toJsonLiteral() {
            return "globalchat( "
            		+
									(getId()
 	 == null ? "null" :
										"\"" + getId()
 	.getStringId() + "\"" 
									)
								+ ", " + 
									(getName()
 	 == null ? "null" :
										"\"" + getName()
 	 + "\"" 
									)
								+ ", " + 
								    String.valueOf(isControlServer()
 	)									
								+ ", " + 
									(getText()
 	 == null ? "null" :
										"\"" + getText()
 	 + "\"" 
									)
								
                   + ")";
        }
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=event+classtype[@name=impl]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=event+classtype[@name=impl]) ---        	            	
 	
		}
 	