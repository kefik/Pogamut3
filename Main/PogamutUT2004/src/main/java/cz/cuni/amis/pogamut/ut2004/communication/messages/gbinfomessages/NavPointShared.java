package cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages;
 		
 		// --- IMPORTS FROM /messages/settings/javasettings/javaimport BEGIN
			import java.util.*;import javax.vecmath.*;import cz.cuni.amis.pogamut.base.communication.messages.*;import cz.cuni.amis.pogamut.base.communication.worldview.*;import cz.cuni.amis.pogamut.base.communication.worldview.event.*;import cz.cuni.amis.pogamut.base.communication.worldview.object.*;import cz.cuni.amis.pogamut.multi.communication.worldview.object.*;import cz.cuni.amis.pogamut.base.communication.translator.event.*;import cz.cuni.amis.pogamut.multi.communication.translator.event.*;import cz.cuni.amis.pogamut.base3d.worldview.object.*;import cz.cuni.amis.pogamut.base3d.worldview.object.event.*;import cz.cuni.amis.pogamut.ut2004.communication.messages.*;import cz.cuni.amis.pogamut.ut2004.communication.worldview.objects.*;import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.objects.*;import cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor.*;import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType.Category;import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;import cz.cuni.amis.utils.exception.*;import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdateResult.Result;import cz.cuni.amis.utils.SafeEquals;import cz.cuni.amis.pogamut.base.agent.*;import cz.cuni.amis.pogamut.multi.agent.*;import cz.cuni.amis.pogamut.multi.communication.worldview.property.*;import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.property.*;import cz.cuni.amis.utils.token.*;import cz.cuni.amis.utils.*;
		// --- IMPORTS FROM /messages/settings/javasettings/javaimport END
		
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name='all'] BEGIN
				
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name='all'] END
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name=shared]+classtype[@name=abstract] BEGIN
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name=shared]+classtype[@name=abstract] END
    
 		/**
         *  
            				Abstract definition of the shared part of the GameBots2004 message NAV.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Synchronous message - however only NavPoints marking item pickup locations are exported synchronously. Other NavPoints are not exported synchronously at all, even if the bot can actually see them (but note that ALL NavPoints are exported in the handshake between bot and the server). Exporting NavPoints synchronously took a lot of UT server resources with limited information gain (in Pogamut there is now available visibility matrix holding static information which points can be seen from other points). NavPoint carries information about UT navigation point - location, paths and some additional information are stored there (if it is an ambush point, or sniper point, etc.).
   
         */
 	public abstract class NavPointShared 
  						extends InfoMessage
  						implements ISharedWorldObject
  						
	    		,ILocated
	    		,ILocomotive
	    {
 	
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public NavPointShared()
		{
		}
		
				// abstract definition of the shared-part of the message, no more constructors is needed
			
	   		
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
	   	
	    						public static final Token LocationPropertyToken = Tokens.get("Location");
	    					
	    						public static final Token VelocityPropertyToken = Tokens.get("Velocity");
	    					
	    						public static final Token ItemSpawnedPropertyToken = Tokens.get("ItemSpawned");
	    					
	    						public static final Token DoorOpenedPropertyToken = Tokens.get("DoorOpened");
	    					
	    						public static final Token DomPointControllerPropertyToken = Tokens.get("DomPointController");
	    						
							
							public static final Set<Token> SharedPropertyTokens;
	
							static {
								Set<Token> tokens = new HashSet<Token>();
								
									tokens.add(LocationPropertyToken);
								
									tokens.add(VelocityPropertyToken);
								
									tokens.add(ItemSpawnedPropertyToken);
								
									tokens.add(DoorOpenedPropertyToken);
								
									tokens.add(DomPointControllerPropertyToken);
								
								SharedPropertyTokens = Collections.unmodifiableSet(tokens);
							}
	    				
	    			
	    				@Override
		    			public abstract 
		    			NavPointShared clone();
		    			
						@Override
						public Class getCompositeClass() {
							return NavPoint.class;
						}
	
						
		    			
 		/**
         * 
			A unique Id of this navigation point assigned by the game.
		 
         */
        public abstract UnrealId getId()
 	;
		    			
 		/**
         * Location of navigation point. 
         */
        public abstract Location getLocation()
 	;
		    			
 		/**
         * 
			Velocity of the navigation point (if the navigation point is
			currently moving). Not sent at the moment.
		 
         */
        public abstract Velocity getVelocity()
 	;
		    			
 		/**
         * 
            True if the item is spawned at the point. Not sent if point is not an inventory spot. 
         
         */
        public abstract boolean isItemSpawned()
 	;
		    			
 		/**
         * 
            True if this NavPoint is a Door and door is opened. Not sent if point is not a door.
         
         */
        public abstract boolean isDoorOpened()
 	;
		    			
 		/**
         * Exported if this NavPoint is a DominationPoint (for BotDoubleDomination game) - which team controls this point. 
         */
        public abstract int getDomPointController()
 	;
		    			
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
		              			"Id = " + String.valueOf(getId()
 	) + " | " + 
		              		
		              			"Location = " + String.valueOf(getLocation()
 	) + " | " + 
		              		
		              			"Velocity = " + String.valueOf(getVelocity()
 	) + " | " + 
		              		
		              			"ItemSpawned = " + String.valueOf(isItemSpawned()
 	) + " | " + 
		              		
		              			"DoorOpened = " + String.valueOf(isDoorOpened()
 	) + " | " + 
		              		
		              			"DomPointController = " + String.valueOf(getDomPointController()
 	) + " | " + 
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>Id</b> = " + String.valueOf(getId()
 	) + " <br/> " + 
		              		
		              			"<b>Location</b> = " + String.valueOf(getLocation()
 	) + " <br/> " + 
		              		
		              			"<b>Velocity</b> = " + String.valueOf(getVelocity()
 	) + " <br/> " + 
		              		
		              			"<b>ItemSpawned</b> = " + String.valueOf(isItemSpawned()
 	) + " <br/> " + 
		              		
		              			"<b>DoorOpened</b> = " + String.valueOf(isDoorOpened()
 	) + " <br/> " + 
		              		
		              			"<b>DomPointController</b> = " + String.valueOf(getDomPointController()
 	) + " <br/> " + 
		              		
				"<br/>]";     
		}
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=shared+classtype[@name=abstract]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=shared+classtype[@name=abstract]) ---        	            	
 	
		}
 	