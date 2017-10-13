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
         			Definition of the event PLS.
         		
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Synchronous message. Contains the info about player score.
	
         */
 	public class PlayerScore 
  				extends InfoMessage
    			implements IWorldEvent, IWorldChangeEvent
    			
	    {
 	
    	/** Example how the message looks like - used during parser tests. */
    	public static final String PROTOTYPE =
    		"PLS {Id unreal_id}  {Score 0}  {Deaths 0} ";
    
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public PlayerScore()
		{
		}
	
    	
    	
    	
    	/**
		 * Creates new instance of the message PlayerScore.
		 * 
		Synchronous message. Contains the info about player score.
	
		 * Corresponding GameBots message
		 *   
		 *   is
		 *   PLS.
		 * 
 	  	 * 
		 *   
		 *     @param lastSeenTime 
			When was the last time we've received info about this
			object.
		
		 *   
		 * 
		 *   
		 *     @param Id Unique Id of the player.
		 *   
		 * 
		 *   
		 *     @param Score 
			Number of player frags (how many times the player killed
			other players) or number of victory points (player frags +
			some special measurement that can differ from game type to
			game type).
		
		 *   
		 * 
		 *   
		 *     @param Deaths Number of players deaths.
		 *   
		 * 
		 */
		public PlayerScore(
			double lastSeenTime,  UnrealId Id,  int Score,  int Deaths
		) {
			
					this.lastSeenTime = lastSeenTime;
				
					this.Id = Id;
				
					this.Score = Score;
				
					this.Deaths = Deaths;
				
		}
    
    	/**
		 * Cloning constructor from the full message.
		 *
		 * @param original
		 */
		public PlayerScore(PlayerScore original) {		
			
					this.lastSeenTime = original.getLastSeenTime()
 	;
				
					this.Id = original.getId()
 	;
				
					this.Score = original.getScore()
 	;
				
					this.Deaths = original.getDeaths()
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
			When was the last time we've received info about this
			object.
		 
         */
        protected
         double lastSeenTime =
       	0;
	
 		/**
         * 
			When was the last time we've received info about this
			object.
		 
         */
        public  double getLastSeenTime()
 	 {
    					return lastSeenTime;
    				}
    			
    	
	    /**
         * Unique Id of the player. 
         */
        protected
         UnrealId Id =
       	null;
	
 		/**
         * Unique Id of the player. 
         */
        public  UnrealId getId()
 	 {
    					return Id;
    				}
    			
    	
	    /**
         * 
			Number of player frags (how many times the player killed
			other players) or number of victory points (player frags +
			some special measurement that can differ from game type to
			game type).
		 
         */
        protected
         int Score =
       	0;
	
 		/**
         * 
			Number of player frags (how many times the player killed
			other players) or number of victory points (player frags +
			some special measurement that can differ from game type to
			game type).
		 
         */
        public  int getScore()
 	 {
    					return Score;
    				}
    			
    	
	    /**
         * Number of players deaths. 
         */
        protected
         int Deaths =
       	0;
	
 		/**
         * Number of players deaths. 
         */
        public  int getDeaths()
 	 {
    					return Deaths;
    				}
    			
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
		              			"Id = " + String.valueOf(getId()
 	) + " | " + 
		              		
		              			"Score = " + String.valueOf(getScore()
 	) + " | " + 
		              		
		              			"Deaths = " + String.valueOf(getDeaths()
 	) + " | " + 
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>Id</b> = " + String.valueOf(getId()
 	) + " <br/> " + 
		              		
		              			"<b>Score</b> = " + String.valueOf(getScore()
 	) + " <br/> " + 
		              		
		              			"<b>Deaths</b> = " + String.valueOf(getDeaths()
 	) + " <br/> " + 
		              		
				"<br/>]";     
		}
 	
 	    public String toJsonLiteral() {
            return "playerscore( "
            		+ ", " + 
									(getId()
 	 == null ? "null" :
										"\"" + getId()
 	.getStringId() + "\"" 
									)
								+ ", " + 
								    String.valueOf(getScore()
 	)									
								+ ", " + 
								    String.valueOf(getDeaths()
 	)									
								
                   + ")";
        }
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=event+classtype[@name=impl]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=event+classtype[@name=impl]) ---        	            	
 	
		}
 	