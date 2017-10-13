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
         			Definition of the event IPTH.
         		
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Info batch message. A series of pathnodes in response to a
		GETPATH command from the client. Starts with SPTH, ends with
		EPTH. Represents some path in the map (sequence of traversable
		navigation points).
	
         */
 	public class PathList 
  				extends InfoMessage
    			implements IWorldEvent, IWorldChangeEvent
    			
	    {
 	
    	/** Example how the message looks like - used during parser tests. */
    	public static final String PROTOTYPE =
    		"IPTH {RouteId unreal_id}  {Location 0,0,0} ";
    
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public PathList()
		{
		}
	
    	
    	
    	
    	/**
		 * Creates new instance of the message PathList.
		 * 
		Info batch message. A series of pathnodes in response to a
		GETPATH command from the client. Starts with SPTH, ends with
		EPTH. Represents some path in the map (sequence of traversable
		navigation points).
	
		 * Corresponding GameBots message
		 *   
		 *   is
		 *   IPTH.
		 * 
 	  	 * 
		 *   
		 *     @param RouteId 
			Unique Id of one navigation point in the path (well it
			should be navigation point - safer is to use location).
		
		 *   
		 * 
		 *   
		 *     @param Location 
			Location of one navigation point in the path.
		
		 *   
		 * 
		 */
		public PathList(
			UnrealId RouteId,  Location Location
		) {
			
					this.RouteId = RouteId;
				
					this.Location = Location;
				
		}
    
    	/**
		 * Cloning constructor from the full message.
		 *
		 * @param original
		 */
		public PathList(PathList original) {		
			
					this.RouteId = original.getRouteId()
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
			Unique Id of one navigation point in the path (well it
			should be navigation point - safer is to use location).
		 
         */
        protected
         UnrealId RouteId =
       	null;
	
 		/**
         * 
			Unique Id of one navigation point in the path (well it
			should be navigation point - safer is to use location).
		 
         */
        public  UnrealId getRouteId()
 	 {
    					return RouteId;
    				}
    			
    	
	    /**
         * 
			Location of one navigation point in the path.
		 
         */
        protected
         Location Location =
       	null;
	
 		/**
         * 
			Location of one navigation point in the path.
		 
         */
        public  Location getLocation()
 	 {
    					return Location;
    				}
    			
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
		              			"RouteId = " + String.valueOf(getRouteId()
 	) + " | " + 
		              		
		              			"Location = " + String.valueOf(getLocation()
 	) + " | " + 
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>RouteId</b> = " + String.valueOf(getRouteId()
 	) + " <br/> " + 
		              		
		              			"<b>Location</b> = " + String.valueOf(getLocation()
 	) + " <br/> " + 
		              		
				"<br/>]";     
		}
 	
 	    public String toJsonLiteral() {
            return "pathlist( "
            		+
									(getRouteId()
 	 == null ? "null" :
										"\"" + getRouteId()
 	.getStringId() + "\"" 
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
 	