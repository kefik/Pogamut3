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
         			Definition of the event WAL.
         		
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Asynchronous message. We have collided with a wall. Note it is
		common to get a bunch of these when you try to run through a
		wall or are pushed into one by gunfire or something.
	
         */
 	public class WallCollision 
  				extends InfoMessage
    			implements IWorldEvent, IWorldChangeEvent
    			
	    {
 	
    	/** Example how the message looks like - used during parser tests. */
    	public static final String PROTOTYPE =
    		"WAL {Id text}  {Normal 0,0,0}  {Location 0,0,0} ";
    
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public WallCollision()
		{
		}
	
    	
    	
    	
    	/**
		 * Creates new instance of the message WallCollision.
		 * 
		Asynchronous message. We have collided with a wall. Note it is
		common to get a bunch of these when you try to run through a
		wall or are pushed into one by gunfire or something.
	
		 * Corresponding GameBots message
		 *   
		 *   is
		 *   WAL.
		 * 
 	  	 * 
		 *   
		 *     @param Id 
			Id of the wall we have hit (level geometry).
		
		 *   
		 * 
		 *   
		 *     @param Normal 
			Normal of the wall bot collided with.
		
		 *   
		 * 
		 *   
		 *     @param Location 
			Absolute location of bot at the time of impact.
		
		 *   
		 * 
		 */
		public WallCollision(
			String Id,  Vector3d Normal,  Location Location
		) {
			
					this.Id = Id;
				
					this.Normal = Normal;
				
					this.Location = Location;
				
		}
    
    	/**
		 * Cloning constructor from the full message.
		 *
		 * @param original
		 */
		public WallCollision(WallCollision original) {		
			
					this.Id = original.getId()
 	;
				
					this.Normal = original.getNormal()
 	;
				
					this.Location = original.getLocation()
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
			Id of the wall we have hit (level geometry).
		 
         */
        protected
         String Id =
       	null;
	
 		/**
         * 
			Id of the wall we have hit (level geometry).
		 
         */
        public  String getId()
 	 {
    					return Id;
    				}
    			
    	
	    /**
         * 
			Normal of the wall bot collided with.
		 
         */
        protected
         Vector3d Normal =
       	null;
	
 		/**
         * 
			Normal of the wall bot collided with.
		 
         */
        public  Vector3d getNormal()
 	 {
    					return Normal;
    				}
    			
    	
	    /**
         * 
			Absolute location of bot at the time of impact.
		 
         */
        protected
         Location Location =
       	null;
	
 		/**
         * 
			Absolute location of bot at the time of impact.
		 
         */
        public  Location getLocation()
 	 {
    					return Location;
    				}
    			
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
		              			"Id = " + String.valueOf(getId()
 	) + " | " + 
		              		
		              			"Normal = " + String.valueOf(getNormal()
 	) + " | " + 
		              		
		              			"Location = " + String.valueOf(getLocation()
 	) + " | " + 
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>Id</b> = " + String.valueOf(getId()
 	) + " <br/> " + 
		              		
		              			"<b>Normal</b> = " + String.valueOf(getNormal()
 	) + " <br/> " + 
		              		
		              			"<b>Location</b> = " + String.valueOf(getLocation()
 	) + " <br/> " + 
		              		
				"<br/>]";     
		}
 	
 	    public String toJsonLiteral() {
            return "wallcollision( "
            		+
									(getId()
 	 == null ? "null" :
										"\"" + getId()
 	 + "\"" 
									)
								+ ", " + 
									(getNormal()
 	 == null ? "null" :
										"[" + getNormal()
 	.getX() + ", " + getNormal()
 	.getY() + ", " + getNormal()
 	.getZ() + "]" 
									)
								+ ", " + 
								    (getLocation()
 	 == null ? "null" :
										"[" + getLocation()
 	.getX() + ", " + getLocation()
 	.getY() + ", " + getLocation()
 	.getZ() + "]" 
									)
								
                   + ")";
        }
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=event+classtype[@name=impl]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=event+classtype[@name=impl]) ---        	            	
 	
		}
 	