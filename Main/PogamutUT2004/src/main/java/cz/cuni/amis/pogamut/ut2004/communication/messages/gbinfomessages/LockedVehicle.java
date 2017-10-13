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
         			Definition of the event LOCKED.
         		
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Asynchronous message. Sent as a response to ENTER command. Sent if
		the ENTER command was unsuccessfull because the vehicle is locked for
		our bot.
	
         */
 	public class LockedVehicle 
  				extends InfoMessage
    			implements IWorldEvent, IWorldChangeEvent
    			
	    {
 	
    	/** Example how the message looks like - used during parser tests. */
    	public static final String PROTOTYPE =
    		"LOCKED {Id unreal_id}  {Type text}  {Location 0,0,0} ";
    
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public LockedVehicle()
		{
		}
	
    	
    	
    	
    	/**
		 * Creates new instance of the message LockedVehicle.
		 * 
		Asynchronous message. Sent as a response to ENTER command. Sent if
		the ENTER command was unsuccessfull because the vehicle is locked for
		our bot.
	
		 * Corresponding GameBots message
		 *   
		 *   is
		 *   LOCKED.
		 * 
 	  	 * 
		 *   
		 *     @param Id Id of the vehicle, which we cannot enter, because it is locked. 
		 *   
		 * 
		 *   
		 *     @param Type Type of the vehicle we wanted to enter. What kind of turret or car. 
		 *   
		 * 
		 *   
		 *     @param Location Location of the vehicle.
		 *   
		 * 
		 */
		public LockedVehicle(
			UnrealId Id,  String Type,  Location Location
		) {
			
					this.Id = Id;
				
					this.Type = Type;
				
					this.Location = Location;
				
		}
    
    	/**
		 * Cloning constructor from the full message.
		 *
		 * @param original
		 */
		public LockedVehicle(LockedVehicle original) {		
			
					this.Id = original.getId()
 	;
				
					this.Type = original.getType()
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
         * Id of the vehicle, which we cannot enter, because it is locked.  
         */
        protected
         UnrealId Id =
       	null;
	
 		/**
         * Id of the vehicle, which we cannot enter, because it is locked.  
         */
        public  UnrealId getId()
 	 {
    					return Id;
    				}
    			
    	
	    /**
         * Type of the vehicle we wanted to enter. What kind of turret or car.  
         */
        protected
         String Type =
       	null;
	
 		/**
         * Type of the vehicle we wanted to enter. What kind of turret or car.  
         */
        public  String getType()
 	 {
    					return Type;
    				}
    			
    	
	    /**
         * Location of the vehicle. 
         */
        protected
         Location Location =
       	null;
	
 		/**
         * Location of the vehicle. 
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
		              		
		              			"Type = " + String.valueOf(getType()
 	) + " | " + 
		              		
		              			"Location = " + String.valueOf(getLocation()
 	) + " | " + 
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>Id</b> = " + String.valueOf(getId()
 	) + " <br/> " + 
		              		
		              			"<b>Type</b> = " + String.valueOf(getType()
 	) + " <br/> " + 
		              		
		              			"<b>Location</b> = " + String.valueOf(getLocation()
 	) + " <br/> " + 
		              		
				"<br/>]";     
		}
 	
 	    public String toJsonLiteral() {
            return "lockedvehicle( "
            		+
									(getId()
 	 == null ? "null" :
										"\"" + getId()
 	.getStringId() + "\"" 
									)
								+ ", " + 
									(getType()
 	 == null ? "null" :
										"\"" + getType()
 	 + "\"" 
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
 	