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
         			Definition of the event HELLO_OBSERVER.
         		
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Asynchronous message. A message sent at the beginning of
		establishing the connection.
	
         */
 	public class HelloObserverHandshake 
  				extends InfoMessage
    			implements IWorldEvent, IWorldChangeEvent
    			
	    {
 	
    	/** Example how the message looks like - used during parser tests. */
    	public static final String PROTOTYPE =
    		"HELLO_OBSERVER {Game text}  {Version text} ";
    
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public HelloObserverHandshake()
		{
		}
	
    	
    	
    	
    	/**
		 * Creates new instance of the message HelloObserverHandshake.
		 * 
		Asynchronous message. A message sent at the beginning of
		establishing the connection.
	
		 * Corresponding GameBots message
		 *   
		 *   is
		 *   HELLO_OBSERVER.
		 * 
 	  	 * 
		 *   
		 *     @param Game 
			Name of the Unreal Tournament version these GameBots are running on (UT2004, UE2, UDK, UT3...).
		
		 *   
		 * 
		 *   
		 *     @param Version 
			Version number of GameBots.
		
		 *   
		 * 
		 */
		public HelloObserverHandshake(
			String Game,  String Version
		) {
			
					this.Game = Game;
				
					this.Version = Version;
				
		}
    
    	/**
		 * Cloning constructor from the full message.
		 *
		 * @param original
		 */
		public HelloObserverHandshake(HelloObserverHandshake original) {		
			
					this.Game = original.getGame()
 	;
				
					this.Version = original.getVersion()
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
			Name of the Unreal Tournament version these GameBots are running on (UT2004, UE2, UDK, UT3...).
		 
         */
        protected
         String Game =
       	null;
	
 		/**
         * 
			Name of the Unreal Tournament version these GameBots are running on (UT2004, UE2, UDK, UT3...).
		 
         */
        public  String getGame()
 	 {
    					return Game;
    				}
    			
    	
	    /**
         * 
			Version number of GameBots.
		 
         */
        protected
         String Version =
       	null;
	
 		/**
         * 
			Version number of GameBots.
		 
         */
        public  String getVersion()
 	 {
    					return Version;
    				}
    			
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
		              			"Game = " + String.valueOf(getGame()
 	) + " | " + 
		              		
		              			"Version = " + String.valueOf(getVersion()
 	) + " | " + 
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>Game</b> = " + String.valueOf(getGame()
 	) + " <br/> " + 
		              		
		              			"<b>Version</b> = " + String.valueOf(getVersion()
 	) + " <br/> " + 
		              		
				"<br/>]";     
		}
 	
 	    public String toJsonLiteral() {
            return "helloobserverhandshake( "
            		+
									(getGame()
 	 == null ? "null" :
										"\"" + getGame()
 	 + "\"" 
									)
								+ ", " + 
									(getVersion()
 	 == null ? "null" :
										"\"" + getVersion()
 	 + "\"" 
									)
								
                   + ")";
        }
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=event+classtype[@name=impl]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=event+classtype[@name=impl]) ---        	            	
 	
		}
 	