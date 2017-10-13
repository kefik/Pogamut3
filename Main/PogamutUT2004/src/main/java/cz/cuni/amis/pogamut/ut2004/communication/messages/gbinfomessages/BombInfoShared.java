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
            				Abstract definition of the shared part of the GameBots2004 message BOM.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Synchronous message. BombInfo contains all info about the bomb
		in the BotBombingRun game mode. Is not sent in other game types.
	
         */
 	public abstract class BombInfoShared 
  						extends InfoMessage
  						implements ISharedWorldObject
  						
	    {
 	
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public BombInfoShared()
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
	   	
	    						public static final Token VelocityPropertyToken = Tokens.get("Velocity");
	    					
	    						public static final Token LocationPropertyToken = Tokens.get("Location");
	    					
	    						public static final Token HolderPropertyToken = Tokens.get("Holder");
	    					
	    						public static final Token HolderTeamPropertyToken = Tokens.get("HolderTeam");
	    					
	    						public static final Token StatePropertyToken = Tokens.get("State");
	    						
							
							public static final Set<Token> SharedPropertyTokens;
	
							static {
								Set<Token> tokens = new HashSet<Token>();
								
									tokens.add(VelocityPropertyToken);
								
									tokens.add(LocationPropertyToken);
								
									tokens.add(HolderPropertyToken);
								
									tokens.add(HolderTeamPropertyToken);
								
									tokens.add(StatePropertyToken);
								
								SharedPropertyTokens = Collections.unmodifiableSet(tokens);
							}
	    				
	    			
	    				@Override
		    			public abstract 
		    			BombInfoShared clone();
		    			
						@Override
						public Class getCompositeClass() {
							return BombInfo.class;
						}
	
						
		    			
 		/**
         * 
			An unique Id for this bomb, assigned by the game.
		 
         */
        public abstract UnrealId getId()
 	;
		    			
 		/**
         * 
   		Current velocity of the bomb. TODO not sure if this actually does smthing
   	 
         */
        public abstract Velocity getVelocity()
 	;
		    			
 		/**
         * 
			An absolute location of the bomb (Sent if we can actually
			see the flag).
		 
         */
        public abstract Location getLocation()
 	;
		    			
 		/**
         * 
			Id of player/bot holding the bomb. (Sent if we can actually
			see the bomb and the bomb is being carried, or if the bomb
			is being carried by us).
		 
         */
        public abstract UnrealId getHolder()
 	;
		    			
 		/**
         * 
			The team of the current holder (if any).
		 
         */
        public abstract Integer getHolderTeam()
 	;
		    			
 		/**
         * 
			Represents the state the bomb is in. Can be "Held",
			"Dropped" or "Home".
		 
         */
        public abstract String getState()
 	;
		    			
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
		              			"Id = " + String.valueOf(getId()
 	) + " | " + 
		              		
		              			"Velocity = " + String.valueOf(getVelocity()
 	) + " | " + 
		              		
		              			"Location = " + String.valueOf(getLocation()
 	) + " | " + 
		              		
		              			"Holder = " + String.valueOf(getHolder()
 	) + " | " + 
		              		
		              			"HolderTeam = " + String.valueOf(getHolderTeam()
 	) + " | " + 
		              		
		              			"State = " + String.valueOf(getState()
 	) + " | " + 
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>Id</b> = " + String.valueOf(getId()
 	) + " <br/> " + 
		              		
		              			"<b>Velocity</b> = " + String.valueOf(getVelocity()
 	) + " <br/> " + 
		              		
		              			"<b>Location</b> = " + String.valueOf(getLocation()
 	) + " <br/> " + 
		              		
		              			"<b>Holder</b> = " + String.valueOf(getHolder()
 	) + " <br/> " + 
		              		
		              			"<b>HolderTeam</b> = " + String.valueOf(getHolderTeam()
 	) + " <br/> " + 
		              		
		              			"<b>State</b> = " + String.valueOf(getState()
 	) + " <br/> " + 
		              		
				"<br/>]";     
		}
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=shared+classtype[@name=abstract]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=shared+classtype[@name=abstract]) ---        	            	
 	
		}
 	