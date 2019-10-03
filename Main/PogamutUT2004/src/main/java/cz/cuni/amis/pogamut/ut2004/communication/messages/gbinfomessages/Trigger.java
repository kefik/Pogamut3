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
         			Definition of the event TRG.
         		
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Asynchronous message. When we trigger some trigger. TODO:
		Experiment message.
	
         */
 	public class Trigger 
  				extends InfoMessage
    			implements IWorldEvent, IWorldChangeEvent
    			
	    {
 	
    	/** Example how the message looks like - used during parser tests. */
    	public static final String PROTOTYPE =
    		"TRG {Actor text}  {EventInstigator unreal_id} ";
    
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public Trigger()
		{
		}
	
    	
    	
    	
    	/**
		 * Creates new instance of the message Trigger.
		 * 
		Asynchronous message. When we trigger some trigger. TODO:
		Experiment message.
	
		 * Corresponding GameBots message
		 *   
		 *   is
		 *   TRG.
		 * 
 	  	 * 
		 *   
		 *     @param Actor 
			Should be the trigger we have triggered.
		
		 *   
		 * 
		 *   
		 *     @param EventInstigator 
			Should be the Id of the player who triggered this
			(ourself?).
		
		 *   
		 * 
		 */
		public Trigger(
			String Actor,  UnrealId EventInstigator
		) {
			
					this.Actor = Actor;
				
					this.EventInstigator = EventInstigator;
				
		}
    
    	/**
		 * Cloning constructor from the full message.
		 *
		 * @param original
		 */
		public Trigger(Trigger original) {		
			
					this.Actor = original.getActor()
 	;
				
					this.EventInstigator = original.getEventInstigator()
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
			Should be the trigger we have triggered.
		 
         */
        protected
         String Actor =
       	null;
	
 		/**
         * 
			Should be the trigger we have triggered.
		 
         */
        public  String getActor()
 	 {
    					return Actor;
    				}
    			
    	
	    /**
         * 
			Should be the Id of the player who triggered this
			(ourself?).
		 
         */
        protected
         UnrealId EventInstigator =
       	null;
	
 		/**
         * 
			Should be the Id of the player who triggered this
			(ourself?).
		 
         */
        public  UnrealId getEventInstigator()
 	 {
    					return EventInstigator;
    				}
    			
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
		              			"Actor = " + String.valueOf(getActor()
 	) + " | " + 
		              		
		              			"EventInstigator = " + String.valueOf(getEventInstigator()
 	) + " | " + 
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>Actor</b> = " + String.valueOf(getActor()
 	) + " <br/> " + 
		              		
		              			"<b>EventInstigator</b> = " + String.valueOf(getEventInstigator()
 	) + " <br/> " + 
		              		
				"<br/>]";     
		}
 	
 	    public String toJsonLiteral() {
            return "trigger( "
            		+
									(getActor()
 	 == null ? "null" :
										"\"" + getActor()
 	 + "\"" 
									)
								+ ", " + 
									(getEventInstigator()
 	 == null ? "null" :
										"\"" + getEventInstigator()
 	.getStringId() + "\"" 
									)
								
                   + ")";
        }
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=event+classtype[@name=impl]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=event+classtype[@name=impl]) ---        	            	
 	
		}
 	