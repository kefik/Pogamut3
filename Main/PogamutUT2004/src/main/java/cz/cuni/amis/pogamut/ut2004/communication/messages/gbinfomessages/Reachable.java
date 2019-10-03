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
         			Definition of the event RCH.
         		
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Asynchronous message. A boolean result of a checkreach call.
	
         */
 	public class Reachable 
  				extends InfoMessage
    			implements IWorldEvent, IWorldChangeEvent
    			
	    {
 	
    	/** Example how the message looks like - used during parser tests. */
    	public static final String PROTOTYPE =
    		"RCH {Id text}  {Reachable False}  {From 0,0,0} ";
    
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public Reachable()
		{
		}
	
    	
    	
    	
    	/**
		 * Creates new instance of the message Reachable.
		 * 
		Asynchronous message. A boolean result of a checkreach call.
	
		 * Corresponding GameBots message
		 *   
		 *   is
		 *   RCH.
		 * 
 	  	 * 
		 *   
		 *     @param Id 
			An Id matching the one sent by client. Allows bot to match
			answer with right querry.
		
		 *   
		 * 
		 *   
		 *     @param Reachable 
			True if the bot can run here directly, false otherwise.
		
		 *   
		 * 
		 *   
		 *     @param From 
			Exact location of bot at time of check.
		
		 *   
		 * 
		 */
		public Reachable(
			String Id,  boolean Reachable,  Location From
		) {
			
					this.Id = Id;
				
					this.Reachable = Reachable;
				
					this.From = From;
				
		}
    
    	/**
		 * Cloning constructor from the full message.
		 *
		 * @param original
		 */
		public Reachable(Reachable original) {		
			
					this.Id = original.getId()
 	;
				
					this.Reachable = original.isReachable()
 	;
				
					this.From = original.getFrom()
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
			An Id matching the one sent by client. Allows bot to match
			answer with right querry.
		 
         */
        protected
         String Id =
       	null;
	
 		/**
         * 
			An Id matching the one sent by client. Allows bot to match
			answer with right querry.
		 
         */
        public  String getId()
 	 {
    					return Id;
    				}
    			
    	
	    /**
         * 
			True if the bot can run here directly, false otherwise.
		 
         */
        protected
         boolean Reachable =
       	false;
	
 		/**
         * 
			True if the bot can run here directly, false otherwise.
		 
         */
        public  boolean isReachable()
 	 {
    					return Reachable;
    				}
    			
    	
	    /**
         * 
			Exact location of bot at time of check.
		 
         */
        protected
         Location From =
       	null;
	
 		/**
         * 
			Exact location of bot at time of check.
		 
         */
        public  Location getFrom()
 	 {
    					return From;
    				}
    			
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
		              			"Id = " + String.valueOf(getId()
 	) + " | " + 
		              		
		              			"Reachable = " + String.valueOf(isReachable()
 	) + " | " + 
		              		
		              			"From = " + String.valueOf(getFrom()
 	) + " | " + 
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>Id</b> = " + String.valueOf(getId()
 	) + " <br/> " + 
		              		
		              			"<b>Reachable</b> = " + String.valueOf(isReachable()
 	) + " <br/> " + 
		              		
		              			"<b>From</b> = " + String.valueOf(getFrom()
 	) + " <br/> " + 
		              		
				"<br/>]";     
		}
 	
 	    public String toJsonLiteral() {
            return "reachable( "
            		+
									(getId()
 	 == null ? "null" :
										"\"" + getId()
 	 + "\"" 
									)
								+ ", " + 
								    String.valueOf(isReachable()
 	)									
								+ ", " + 
								    (getFrom()
 	 == null ? "null" :
										"[" + getFrom()
 	.getX() + ", " + getFrom()
 	.getY() + ", " + getFrom()
 	.getZ() + "]" 
									)
								
                   + ")";
        }
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=event+classtype[@name=impl]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=event+classtype[@name=impl]) ---        	            	
 	
		}
 	