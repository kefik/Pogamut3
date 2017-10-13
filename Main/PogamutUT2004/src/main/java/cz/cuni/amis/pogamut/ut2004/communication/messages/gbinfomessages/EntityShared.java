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
            				Abstract definition of the shared part of the GameBots2004 message ENT.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Generic physical entity that may be present in the world. Works as a hook for custom objects, such as new items from EmohawkRPG extension.
	
         */
 	public abstract class EntityShared 
  						extends InfoMessage
  						implements ISharedWorldObject
  						
	    		,ILocated
	    {
 	
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public EntityShared()
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
	    					
	    						public static final Token RotationPropertyToken = Tokens.get("Rotation");
	    					
	    						public static final Token VelocityPropertyToken = Tokens.get("Velocity");
	    						
							
							public static final Set<Token> SharedPropertyTokens;
	
							static {
								Set<Token> tokens = new HashSet<Token>();
								
									tokens.add(LocationPropertyToken);
								
									tokens.add(RotationPropertyToken);
								
									tokens.add(VelocityPropertyToken);
								
								SharedPropertyTokens = Collections.unmodifiableSet(tokens);
							}
	    				
	    			
	    				@Override
		    			public abstract 
		    			EntityShared clone();
		    			
						@Override
						public Class getCompositeClass() {
							return Entity.class;
						}
	
						
		    			
 		/**
         * 
			Unique Id of the item. This Id represents some entity in the world.
		 
         */
        public abstract UnrealId getId()
 	;
		    			
 		/**
         * Location of the item. 
         */
        public abstract Location getLocation()
 	;
		    			
 		/**
         * Rotation of the item. 
         */
        public abstract Rotation getRotation()
 	;
		    			
 		/**
         * Velocity of the item. 
         */
        public abstract Velocity getVelocity()
 	;
		    			
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
		              			"Id = " + String.valueOf(getId()
 	) + " | " + 
		              		
		              			"Location = " + String.valueOf(getLocation()
 	) + " | " + 
		              		
		              			"Rotation = " + String.valueOf(getRotation()
 	) + " | " + 
		              		
		              			"Velocity = " + String.valueOf(getVelocity()
 	) + " | " + 
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>Id</b> = " + String.valueOf(getId()
 	) + " <br/> " + 
		              		
		              			"<b>Location</b> = " + String.valueOf(getLocation()
 	) + " <br/> " + 
		              		
		              			"<b>Rotation</b> = " + String.valueOf(getRotation()
 	) + " <br/> " + 
		              		
		              			"<b>Velocity</b> = " + String.valueOf(getVelocity()
 	) + " <br/> " + 
		              		
				"<br/>]";     
		}
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=shared+classtype[@name=abstract]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=shared+classtype[@name=abstract]) ---        	            	
 	
		}
 	