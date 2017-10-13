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
         			Definition of the event UPD.
         		
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		"Synchronous" message. Arrives outside sync. batch. The frequency of this message is configured through locUpdateMultiplier variable in GameBots2004.ini file. It gets exported N times faster than regular sync. batch where N equals locUpdateMultiplier. Holds information about Location, Velocity and Rotation of the bot and should help us to create more robust navigation.
	
         */
 	public class LocationUpdate 
  				extends InfoMessage
    			implements IWorldEvent, IWorldChangeEvent
    			
	    {
 	
    	/** Example how the message looks like - used during parser tests. */
    	public static final String PROTOTYPE =
    		"UPD {Loc 0,0,0}  {Vel 0,0,0}  {Rot 0,0,0} ";
    
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public LocationUpdate()
		{
		}
	
    	
    	
    	
    	/**
		 * Creates new instance of the message LocationUpdate.
		 * 
		"Synchronous" message. Arrives outside sync. batch. The frequency of this message is configured through locUpdateMultiplier variable in GameBots2004.ini file. It gets exported N times faster than regular sync. batch where N equals locUpdateMultiplier. Holds information about Location, Velocity and Rotation of the bot and should help us to create more robust navigation.
	
		 * Corresponding GameBots message
		 *   
		 *   is
		 *   UPD.
		 * 
 	  	 * 
		 *   
		 *     @param Loc 
			An absolute location of the bot.
		
		 *   
		 * 
		 *   
		 *     @param Vel 
			Absolute velocity of the bot as a vector of movement per one
			game second.
		
		 *   
		 * 
		 *   
		 *     @param Rot 
			Which direction the bot is facing in absolute terms.
		
		 *   
		 * 
		 */
		public LocationUpdate(
			Location Loc,  Velocity Vel,  Rotation Rot
		) {
			
					this.Loc = Loc;
				
					this.Vel = Vel;
				
					this.Rot = Rot;
				
		}
    
    	/**
		 * Cloning constructor from the full message.
		 *
		 * @param original
		 */
		public LocationUpdate(LocationUpdate original) {		
			
					this.Loc = original.getLoc()
 	;
				
					this.Vel = original.getVel()
 	;
				
					this.Rot = original.getRot()
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
			An absolute location of the bot.
		 
         */
        protected
         Location Loc =
       	null;
	
 		/**
         * 
			An absolute location of the bot.
		 
         */
        public  Location getLoc()
 	 {
    					return Loc;
    				}
    			
    	
	    /**
         * 
			Absolute velocity of the bot as a vector of movement per one
			game second.
		 
         */
        protected
         Velocity Vel =
       	null;
	
 		/**
         * 
			Absolute velocity of the bot as a vector of movement per one
			game second.
		 
         */
        public  Velocity getVel()
 	 {
    					return Vel;
    				}
    			
    	
	    /**
         * 
			Which direction the bot is facing in absolute terms.
		 
         */
        protected
         Rotation Rot =
       	null;
	
 		/**
         * 
			Which direction the bot is facing in absolute terms.
		 
         */
        public  Rotation getRot()
 	 {
    					return Rot;
    				}
    			
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
		              			"Loc = " + String.valueOf(getLoc()
 	) + " | " + 
		              		
		              			"Vel = " + String.valueOf(getVel()
 	) + " | " + 
		              		
		              			"Rot = " + String.valueOf(getRot()
 	) + " | " + 
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>Loc</b> = " + String.valueOf(getLoc()
 	) + " <br/> " + 
		              		
		              			"<b>Vel</b> = " + String.valueOf(getVel()
 	) + " <br/> " + 
		              		
		              			"<b>Rot</b> = " + String.valueOf(getRot()
 	) + " <br/> " + 
		              		
				"<br/>]";     
		}
 	
 	    public String toJsonLiteral() {
            return "locationupdate( "
            		+
								    (getLoc()
 	 == null ? "null" :
										"[" + getLoc()
 	.getX() + ", " + getLoc()
 	.getY() + ", " + getLoc()
 	.getZ() + "]" 
									)
								+ ", " + 
								    (getVel()
 	 == null ? "null" :
										"[" + getVel()
 	.getX() + ", " + getVel()
 	.getY() + ", " + getVel()
 	.getZ() + "]" 
									)
								+ ", " + 
									(getRot()
 	 == null ? "null" :
										"[" + getRot()
 	.getPitch() + ", " + getRot()
 	.getYaw() + ", " + getRot()
 	.getRoll() + "]" 
									)								    
								
                   + ")";
        }
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=event+classtype[@name=impl]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=event+classtype[@name=impl]) ---        	            	
 	
		}
 	