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
         			Definition of the event IUO.
         		
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Asynchronous message. Sent when the observed player interacts with usable object.
    
         */
 	public class InteractedUsableObject 
  				extends InfoMessage
    			implements IWorldEvent, IWorldChangeEvent
    			
	    {
 	
    	/** Example how the message looks like - used during parser tests. */
    	public static final String PROTOTYPE =
    		"IUO {Name text}  {Location 0,0,0}  {UOEvent 0}  {Action text} ";
    
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public InteractedUsableObject()
		{
		}
	
    	
    	
    	
    	/**
		 * Creates new instance of the message InteractedUsableObject.
		 * 
		Asynchronous message. Sent when the observed player interacts with usable object.
    
		 * Corresponding GameBots message
		 *   
		 *   is
		 *   IUO.
		 * 
 	  	 * 
		 *   
		 *     @param Name 
			Name of the usable object.
		
		 *   
		 * 
		 *   
		 *     @param Location 
			Location of the usable object.
		
		 *   
		 * 
		 *   
		 *     @param UOEvent 
			Code of the action performed.
		
		 *   
		 * 
		 *   
		 *     @param Action 
			Name of the action performed.
		
		 *   
		 * 
		 */
		public InteractedUsableObject(
			String Name,  Location Location,  int UOEvent,  String Action
		) {
			
					this.Name = Name;
				
					this.Location = Location;
				
					this.UOEvent = UOEvent;
				
					this.Action = Action;
				
		}
    
    	/**
		 * Cloning constructor from the full message.
		 *
		 * @param original
		 */
		public InteractedUsableObject(InteractedUsableObject original) {		
			
					this.Name = original.getName()
 	;
				
					this.Location = original.getLocation()
 	;
				
					this.UOEvent = original.getUOEvent()
 	;
				
					this.Action = original.getAction()
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
			Name of the usable object.
		 
         */
        protected
         String Name =
       	null;
	
 		/**
         * 
			Name of the usable object.
		 
         */
        public  String getName()
 	 {
    					return Name;
    				}
    			
    	
	    /**
         * 
			Location of the usable object.
		 
         */
        protected
         Location Location =
       	null;
	
 		/**
         * 
			Location of the usable object.
		 
         */
        public  Location getLocation()
 	 {
    					return Location;
    				}
    			
    	
	    /**
         * 
			Code of the action performed.
		 
         */
        protected
         int UOEvent =
       	0;
	
 		/**
         * 
			Code of the action performed.
		 
         */
        public  int getUOEvent()
 	 {
    					return UOEvent;
    				}
    			
    	
	    /**
         * 
			Name of the action performed.
		 
         */
        protected
         String Action =
       	null;
	
 		/**
         * 
			Name of the action performed.
		 
         */
        public  String getAction()
 	 {
    					return Action;
    				}
    			
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
		              			"Name = " + String.valueOf(getName()
 	) + " | " + 
		              		
		              			"Location = " + String.valueOf(getLocation()
 	) + " | " + 
		              		
		              			"UOEvent = " + String.valueOf(getUOEvent()
 	) + " | " + 
		              		
		              			"Action = " + String.valueOf(getAction()
 	) + " | " + 
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>Name</b> = " + String.valueOf(getName()
 	) + " <br/> " + 
		              		
		              			"<b>Location</b> = " + String.valueOf(getLocation()
 	) + " <br/> " + 
		              		
		              			"<b>UOEvent</b> = " + String.valueOf(getUOEvent()
 	) + " <br/> " + 
		              		
		              			"<b>Action</b> = " + String.valueOf(getAction()
 	) + " <br/> " + 
		              		
				"<br/>]";     
		}
 	
 	    public String toJsonLiteral() {
            return "interactedusableobject( "
            		+
									(getName()
 	 == null ? "null" :
										"\"" + getName()
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
								+ ", " + 
								    String.valueOf(getUOEvent()
 	)									
								+ ", " + 
									(getAction()
 	 == null ? "null" :
										"\"" + getAction()
 	 + "\"" 
									)
								
                   + ")";
        }
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=event+classtype[@name=impl]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=event+classtype[@name=impl]) ---        	            	
 	
		}
 	