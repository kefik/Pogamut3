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
         			Definition of the event KEYEVENT.
         		
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Asynchronous message. Response to key event (key press, key release...) when previous SETSENDKEYS command turned this sending on. 
	
         */
 	public class KeyEvent 
  				extends InfoMessage
    			implements IWorldEvent, IWorldChangeEvent
    			
	    {
 	
    	/** Example how the message looks like - used during parser tests. */
    	public static final String PROTOTYPE =
    		"KEYEVENT {Player text}  {PlayerId unreal_id}  {PlayerName text}  {ViewTarget unreal_id}  {Action text}  {Key text} ";
    
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public KeyEvent()
		{
		}
	
    	
    	
    	
    	/**
		 * Creates new instance of the message KeyEvent.
		 * 
		Asynchronous message. Response to key event (key press, key release...) when previous SETSENDKEYS command turned this sending on. 
	
		 * Corresponding GameBots message
		 *   
		 *   is
		 *   KEYEVENT.
		 * 
 	  	 * 
		 *   
		 *     @param Player 
			Name of player who has triggered the key event.
		
		 *   
		 * 
		 *   
		 *     @param PlayerId 
			Id of player who has triggered the key event.
	    
		 *   
		 * 
		 *   
		 *     @param PlayerName 
			Name of player who has triggered the key event.
		
		 *   
		 * 
		 *   
		 *     @param ViewTarget 
			If the player that triggered the key event is observing another player in the game, then the id of that observed player will be exported here.
	
		 *   
		 * 
		 *   
		 *     @param Action 
			Action that happened with the key. Possible are: PRESS, HOLD, RELEASE.
		
		 *   
		 * 
		 *   
		 *     @param Key 
			Key that has been pressed, released or held. See Interactions.EInputKey for choices - these are without the "IK_" prefix with original case.
		
		 *   
		 * 
		 */
		public KeyEvent(
			String Player,  UnrealId PlayerId,  String PlayerName,  UnrealId ViewTarget,  String Action,  String Key
		) {
			
					this.Player = Player;
				
					this.PlayerId = PlayerId;
				
					this.PlayerName = PlayerName;
				
					this.ViewTarget = ViewTarget;
				
					this.Action = Action;
				
					this.Key = Key;
				
		}
    
    	/**
		 * Cloning constructor from the full message.
		 *
		 * @param original
		 */
		public KeyEvent(KeyEvent original) {		
			
					this.Player = original.getPlayer()
 	;
				
					this.PlayerId = original.getPlayerId()
 	;
				
					this.PlayerName = original.getPlayerName()
 	;
				
					this.ViewTarget = original.getViewTarget()
 	;
				
					this.Action = original.getAction()
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
			Name of player who has triggered the key event.
		 
         */
        protected
         String Player =
       	null;
	
 		/**
         * 
			Name of player who has triggered the key event.
		 
         */
        public  String getPlayer()
 	 {
    					return Player;
    				}
    			
    	
	    /**
         * 
			Id of player who has triggered the key event.
	     
         */
        protected
         UnrealId PlayerId =
       	null;
	
 		/**
         * 
			Id of player who has triggered the key event.
	     
         */
        public  UnrealId getPlayerId()
 	 {
    					return PlayerId;
    				}
    			
    	
	    /**
         * 
			Name of player who has triggered the key event.
		 
         */
        protected
         String PlayerName =
       	null;
	
 		/**
         * 
			Name of player who has triggered the key event.
		 
         */
        public  String getPlayerName()
 	 {
    					return PlayerName;
    				}
    			
    	
	    /**
         * 
			If the player that triggered the key event is observing another player in the game, then the id of that observed player will be exported here.
	 
         */
        protected
         UnrealId ViewTarget =
       	null;
	
 		/**
         * 
			If the player that triggered the key event is observing another player in the game, then the id of that observed player will be exported here.
	 
         */
        public  UnrealId getViewTarget()
 	 {
    					return ViewTarget;
    				}
    			
    	
	    /**
         * 
			Action that happened with the key. Possible are: PRESS, HOLD, RELEASE.
		 
         */
        protected
         String Action =
       	null;
	
 		/**
         * 
			Action that happened with the key. Possible are: PRESS, HOLD, RELEASE.
		 
         */
        public  String getAction()
 	 {
    					return Action;
    				}
    			
    	
	    /**
         * 
			Key that has been pressed, released or held. See Interactions.EInputKey for choices - these are without the "IK_" prefix with original case.
		 
         */
        protected
         String Key =
       	null;
	
 		/**
         * 
			Key that has been pressed, released or held. See Interactions.EInputKey for choices - these are without the "IK_" prefix with original case.
		 
         */
        public  String getKey()
 	 {
    					return Key;
    				}
    			
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
		              			"Player = " + String.valueOf(getPlayer()
 	) + " | " + 
		              		
		              			"PlayerId = " + String.valueOf(getPlayerId()
 	) + " | " + 
		              		
		              			"PlayerName = " + String.valueOf(getPlayerName()
 	) + " | " + 
		              		
		              			"ViewTarget = " + String.valueOf(getViewTarget()
 	) + " | " + 
		              		
		              			"Action = " + String.valueOf(getAction()
 	) + " | " + 
		              		
		              			"Key = " + String.valueOf(getKey()
 	) + " | " + 
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>Player</b> = " + String.valueOf(getPlayer()
 	) + " <br/> " + 
		              		
		              			"<b>PlayerId</b> = " + String.valueOf(getPlayerId()
 	) + " <br/> " + 
		              		
		              			"<b>PlayerName</b> = " + String.valueOf(getPlayerName()
 	) + " <br/> " + 
		              		
		              			"<b>ViewTarget</b> = " + String.valueOf(getViewTarget()
 	) + " <br/> " + 
		              		
		              			"<b>Action</b> = " + String.valueOf(getAction()
 	) + " <br/> " + 
		              		
		              			"<b>Key</b> = " + String.valueOf(getKey()
 	) + " <br/> " + 
		              		
				"<br/>]";     
		}
 	
 	    public String toJsonLiteral() {
            return "keyevent( "
            		+
									(getPlayer()
 	 == null ? "null" :
										"\"" + getPlayer()
 	 + "\"" 
									)
								+ ", " + 
									(getPlayerId()
 	 == null ? "null" :
										"\"" + getPlayerId()
 	.getStringId() + "\"" 
									)
								+ ", " + 
									(getPlayerName()
 	 == null ? "null" :
										"\"" + getPlayerName()
 	 + "\"" 
									)
								+ ", " + 
									(getViewTarget()
 	 == null ? "null" :
										"\"" + getViewTarget()
 	.getStringId() + "\"" 
									)
								+ ", " + 
									(getAction()
 	 == null ? "null" :
										"\"" + getAction()
 	 + "\"" 
									)
								+ ", " + 
									(getKey()
 	 == null ? "null" :
										"\"" + getKey()
 	 + "\"" 
									)
								
                   + ")";
        }
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=event+classtype[@name=impl]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=event+classtype[@name=impl]) ---        	            	
 	
		}
 	