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
         			Definition of the event GAMERESTART.
         		
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Asynchronous message. Sent when the game is being restarted by the
                control server. Two messages arrive. One notifying the restart has
                been started and second notifying the restart has ended.
	
         */
 	public class GameRestarted 
  				extends InfoMessage
    			implements IWorldEvent, IWorldChangeEvent
    			
	    {
 	
    	/** Example how the message looks like - used during parser tests. */
    	public static final String PROTOTYPE =
    		"GAMERESTART {Started False}  {Finished False} ";
    
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public GameRestarted()
		{
		}
	
    	
    	
    	
    	/**
		 * Creates new instance of the message GameRestarted.
		 * 
		Asynchronous message. Sent when the game is being restarted by the
                control server. Two messages arrive. One notifying the restart has
                been started and second notifying the restart has ended.
	
		 * Corresponding GameBots message
		 *   
		 *   is
		 *   GAMERESTART.
		 * 
 	  	 * 
		 *   
		 *     @param Started Game restart sequence has been started.
		 *   
		 * 
		 *   
		 *     @param Finished Game restart has been finished.
		 *   
		 * 
		 */
		public GameRestarted(
			boolean Started,  boolean Finished
		) {
			
					this.Started = Started;
				
					this.Finished = Finished;
				
		}
    
    	/**
		 * Cloning constructor from the full message.
		 *
		 * @param original
		 */
		public GameRestarted(GameRestarted original) {		
			
					this.Started = original.isStarted()
 	;
				
					this.Finished = original.isFinished()
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
         * Game restart sequence has been started. 
         */
        protected
         boolean Started =
       	false;
	
 		/**
         * Game restart sequence has been started. 
         */
        public  boolean isStarted()
 	 {
    					return Started;
    				}
    			
    	
	    /**
         * Game restart has been finished. 
         */
        protected
         boolean Finished =
       	false;
	
 		/**
         * Game restart has been finished. 
         */
        public  boolean isFinished()
 	 {
    					return Finished;
    				}
    			
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
		              			"Started = " + String.valueOf(isStarted()
 	) + " | " + 
		              		
		              			"Finished = " + String.valueOf(isFinished()
 	) + " | " + 
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>Started</b> = " + String.valueOf(isStarted()
 	) + " <br/> " + 
		              		
		              			"<b>Finished</b> = " + String.valueOf(isFinished()
 	) + " <br/> " + 
		              		
				"<br/>]";     
		}
 	
 	    public String toJsonLiteral() {
            return "gamerestarted( "
            		+
								    String.valueOf(isStarted()
 	)									
								+ ", " + 
								    String.valueOf(isFinished()
 	)									
								
                   + ")";
        }
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=event+classtype[@name=impl]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=event+classtype[@name=impl]) ---        	            	
 	
		}
 	