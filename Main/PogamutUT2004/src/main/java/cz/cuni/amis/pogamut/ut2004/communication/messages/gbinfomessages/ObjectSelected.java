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
         			Definition of the event SEL.
         		
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Asynchronous message. Player selected an object in the environment in PlayerMousing state (by pressing ALT + SHIFT to switch to this state).
	
         */
 	public class ObjectSelected 
  				extends InfoMessage
    			implements IWorldEvent, IWorldChangeEvent
    			
	    {
 	
    	/** Example how the message looks like - used during parser tests. */
    	public static final String PROTOTYPE =
    		"SEL {PlayerId unreal_id}  {PlayerName text}  {ObjectId unreal_id}  {ObjectLocation 0,0,0}  {ObjectHitLocation 0,0,0} ";
    
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public ObjectSelected()
		{
		}
	
    	
    	
    	
    	/**
		 * Creates new instance of the message ObjectSelected.
		 * 
		Asynchronous message. Player selected an object in the environment in PlayerMousing state (by pressing ALT + SHIFT to switch to this state).
	
		 * Corresponding GameBots message
		 *   
		 *   is
		 *   SEL.
		 * 
 	  	 * 
		 *   
		 *     @param PlayerId 
			Unique Id of the player that this event is for.
		
		 *   
		 * 
		 *   
		 *     @param PlayerName 
			Name of the player that this event is for.
		
		 *   
		 * 
		 *   
		 *     @param ObjectId 
			Id of the actor the player selected (actors include
			other players or bots and other physical objects that can
			block your path and even level geometry actors). Will be "None" if nothing was selected (or actor was deselected).
		
		 *   
		 * 
		 *   
		 *     @param ObjectLocation 
			Location of the actor the player selected. Sent only if some object selected.
		
		 *   
		 * 
		 *   
		 *     @param ObjectHitLocation 
			Location of the hit point that we have selected this actor through. Sent only if some object selected.
		
		 *   
		 * 
		 */
		public ObjectSelected(
			UnrealId PlayerId,  String PlayerName,  UnrealId ObjectId,  Location ObjectLocation,  Location ObjectHitLocation
		) {
			
					this.PlayerId = PlayerId;
				
					this.PlayerName = PlayerName;
				
					this.ObjectId = ObjectId;
				
					this.ObjectLocation = ObjectLocation;
				
					this.ObjectHitLocation = ObjectHitLocation;
				
		}
    
    	/**
		 * Cloning constructor from the full message.
		 *
		 * @param original
		 */
		public ObjectSelected(ObjectSelected original) {		
			
					this.PlayerId = original.getPlayerId()
 	;
				
					this.PlayerName = original.getPlayerName()
 	;
				
					this.ObjectId = original.getObjectId()
 	;
				
					this.ObjectLocation = original.getObjectLocation()
 	;
				
					this.ObjectHitLocation = original.getObjectHitLocation()
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
			Unique Id of the player that this event is for.
		 
         */
        protected
         UnrealId PlayerId =
       	null;
	
 		/**
         * 
			Unique Id of the player that this event is for.
		 
         */
        public  UnrealId getPlayerId()
 	 {
    					return PlayerId;
    				}
    			
    	
	    /**
         * 
			Name of the player that this event is for.
		 
         */
        protected
         String PlayerName =
       	null;
	
 		/**
         * 
			Name of the player that this event is for.
		 
         */
        public  String getPlayerName()
 	 {
    					return PlayerName;
    				}
    			
    	
	    /**
         * 
			Id of the actor the player selected (actors include
			other players or bots and other physical objects that can
			block your path and even level geometry actors). Will be "None" if nothing was selected (or actor was deselected).
		 
         */
        protected
         UnrealId ObjectId =
       	null;
	
 		/**
         * 
			Id of the actor the player selected (actors include
			other players or bots and other physical objects that can
			block your path and even level geometry actors). Will be "None" if nothing was selected (or actor was deselected).
		 
         */
        public  UnrealId getObjectId()
 	 {
    					return ObjectId;
    				}
    			
    	
	    /**
         * 
			Location of the actor the player selected. Sent only if some object selected.
		 
         */
        protected
         Location ObjectLocation =
       	null;
	
 		/**
         * 
			Location of the actor the player selected. Sent only if some object selected.
		 
         */
        public  Location getObjectLocation()
 	 {
    					return ObjectLocation;
    				}
    			
    	
	    /**
         * 
			Location of the hit point that we have selected this actor through. Sent only if some object selected.
		 
         */
        protected
         Location ObjectHitLocation =
       	null;
	
 		/**
         * 
			Location of the hit point that we have selected this actor through. Sent only if some object selected.
		 
         */
        public  Location getObjectHitLocation()
 	 {
    					return ObjectHitLocation;
    				}
    			
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
		              			"PlayerId = " + String.valueOf(getPlayerId()
 	) + " | " + 
		              		
		              			"PlayerName = " + String.valueOf(getPlayerName()
 	) + " | " + 
		              		
		              			"ObjectId = " + String.valueOf(getObjectId()
 	) + " | " + 
		              		
		              			"ObjectLocation = " + String.valueOf(getObjectLocation()
 	) + " | " + 
		              		
		              			"ObjectHitLocation = " + String.valueOf(getObjectHitLocation()
 	) + " | " + 
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>PlayerId</b> = " + String.valueOf(getPlayerId()
 	) + " <br/> " + 
		              		
		              			"<b>PlayerName</b> = " + String.valueOf(getPlayerName()
 	) + " <br/> " + 
		              		
		              			"<b>ObjectId</b> = " + String.valueOf(getObjectId()
 	) + " <br/> " + 
		              		
		              			"<b>ObjectLocation</b> = " + String.valueOf(getObjectLocation()
 	) + " <br/> " + 
		              		
		              			"<b>ObjectHitLocation</b> = " + String.valueOf(getObjectHitLocation()
 	) + " <br/> " + 
		              		
				"<br/>]";     
		}
 	
 	    public String toJsonLiteral() {
            return "objectselected( "
            		+
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
									(getObjectId()
 	 == null ? "null" :
										"\"" + getObjectId()
 	.getStringId() + "\"" 
									)
								+ ", " + 
								    (getObjectLocation()
 	 == null ? "null" :
										"[" + getObjectLocation()
 	.getX() + ", " + getObjectLocation()
 	.getY() + ", " + getObjectLocation()
 	.getZ() + "]" 
									)
								+ ", " + 
								    (getObjectHitLocation()
 	 == null ? "null" :
										"[" + getObjectHitLocation()
 	.getX() + ", " + getObjectHitLocation()
 	.getY() + ", " + getObjectHitLocation()
 	.getZ() + "]" 
									)
								
                   + ")";
        }
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=event+classtype[@name=impl]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=event+classtype[@name=impl]) ---        	            	
 	
		}
 	