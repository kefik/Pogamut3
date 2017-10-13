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
         			Definition of the event HELLO_BOT.
         		
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Asynchronous message. Sent for bot connection. First message
		from UT2004, part of handshake.
	
         */
 	public class HelloBotHandshake 
  				extends InfoMessage
    			implements IWorldEvent, IWorldChangeEvent
    			
	    {
 	
    	/** Example how the message looks like - used during parser tests. */
    	public static final String PROTOTYPE =
    		"HELLO_BOT {ServerFull False}  {Game text}  {Version text} ";
    
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public HelloBotHandshake()
		{
		}
	
    	
    	
    	
    	/**
		 * Creates new instance of the message HelloBotHandshake.
		 * 
		Asynchronous message. Sent for bot connection. First message
		from UT2004, part of handshake.
	
		 * Corresponding GameBots message
		 *   
		 *   is
		 *   HELLO_BOT.
		 * 
 	  	 * 
		 *   
		 *     @param ServerFull 
			If it is true, connection will be terminated afterwards.
		
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
		public HelloBotHandshake(
			boolean ServerFull,  String Game,  String Version
		) {
			
					this.ServerFull = ServerFull;
				
					this.Game = Game;
				
					this.Version = Version;
				
		}
    
    	/**
		 * Cloning constructor from the full message.
		 *
		 * @param original
		 */
		public HelloBotHandshake(HelloBotHandshake original) {		
			
					this.ServerFull = original.isServerFull()
 	;
				
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
			If it is true, connection will be terminated afterwards.
		 
         */
        protected
         boolean ServerFull =
       	false;
	
 		/**
         * 
			If it is true, connection will be terminated afterwards.
		 
         */
        public  boolean isServerFull()
 	 {
    					return ServerFull;
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
            	
		              			"ServerFull = " + String.valueOf(isServerFull()
 	) + " | " + 
		              		
		              			"Game = " + String.valueOf(getGame()
 	) + " | " + 
		              		
		              			"Version = " + String.valueOf(getVersion()
 	) + " | " + 
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>ServerFull</b> = " + String.valueOf(isServerFull()
 	) + " <br/> " + 
		              		
		              			"<b>Game</b> = " + String.valueOf(getGame()
 	) + " <br/> " + 
		              		
		              			"<b>Version</b> = " + String.valueOf(getVersion()
 	) + " <br/> " + 
		              		
				"<br/>]";     
		}
 	
 	    public String toJsonLiteral() {
            return "hellobothandshake( "
            		+
								    String.valueOf(isServerFull()
 	)									
								+ ", " + 
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
 	