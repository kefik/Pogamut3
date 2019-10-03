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
         			Definition of the event TEAMCHANGE.
         		
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Aynchronous message. Response of the CHANGETEAM command.
	
         */
 	public class TeamChanged 
  				extends InfoMessage
    			implements IWorldEvent, IWorldChangeEvent
    			
	    {
 	
    	/** Example how the message looks like - used during parser tests. */
    	public static final String PROTOTYPE =
    		"TEAMCHANGE {Id unreal_id}  {Success False}  {DesiredTeam 0} ";
    
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public TeamChanged()
		{
		}
	
    	
    	
    	
    	/**
		 * Creates new instance of the message TeamChanged.
		 * 
		Aynchronous message. Response of the CHANGETEAM command.
	
		 * Corresponding GameBots message
		 *   
		 *   is
		 *   TEAMCHANGE.
		 * 
 	  	 * 
		 *   
		 *     @param Id 
			Sent only for ControlServer connections, so they know which bot changed the team.                        
        
		 *   
		 * 
		 *   
		 *     @param Success 
			If true team change was succesfull (it won't be succesfull if
			we are changing to a team we already are in).
		
		 *   
		 * 
		 *   
		 *     @param DesiredTeam 
			This is the team we wanted to change to (0 for red,1 for
			blue, etc..).
		
		 *   
		 * 
		 */
		public TeamChanged(
			UnrealId Id,  boolean Success,  int DesiredTeam
		) {
			
					this.Id = Id;
				
					this.Success = Success;
				
					this.DesiredTeam = DesiredTeam;
				
		}
    
    	/**
		 * Cloning constructor from the full message.
		 *
		 * @param original
		 */
		public TeamChanged(TeamChanged original) {		
			
					this.Id = original.getId()
 	;
				
					this.Success = original.isSuccess()
 	;
				
					this.DesiredTeam = original.getDesiredTeam()
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
			Sent only for ControlServer connections, so they know which bot changed the team.                        
         
         */
        protected
         UnrealId Id =
       	null;
	
 		/**
         * 
			Sent only for ControlServer connections, so they know which bot changed the team.                        
         
         */
        public  UnrealId getId()
 	 {
    					return Id;
    				}
    			
    	
	    /**
         * 
			If true team change was succesfull (it won't be succesfull if
			we are changing to a team we already are in).
		 
         */
        protected
         boolean Success =
       	false;
	
 		/**
         * 
			If true team change was succesfull (it won't be succesfull if
			we are changing to a team we already are in).
		 
         */
        public  boolean isSuccess()
 	 {
    					return Success;
    				}
    			
    	
	    /**
         * 
			This is the team we wanted to change to (0 for red,1 for
			blue, etc..).
		 
         */
        protected
         int DesiredTeam =
       	0;
	
 		/**
         * 
			This is the team we wanted to change to (0 for red,1 for
			blue, etc..).
		 
         */
        public  int getDesiredTeam()
 	 {
    					return DesiredTeam;
    				}
    			
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
		              			"Id = " + String.valueOf(getId()
 	) + " | " + 
		              		
		              			"Success = " + String.valueOf(isSuccess()
 	) + " | " + 
		              		
		              			"DesiredTeam = " + String.valueOf(getDesiredTeam()
 	) + " | " + 
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>Id</b> = " + String.valueOf(getId()
 	) + " <br/> " + 
		              		
		              			"<b>Success</b> = " + String.valueOf(isSuccess()
 	) + " <br/> " + 
		              		
		              			"<b>DesiredTeam</b> = " + String.valueOf(getDesiredTeam()
 	) + " <br/> " + 
		              		
				"<br/>]";     
		}
 	
 	    public String toJsonLiteral() {
            return "teamchanged( "
            		+
									(getId()
 	 == null ? "null" :
										"\"" + getId()
 	.getStringId() + "\"" 
									)
								+ ", " + 
								    String.valueOf(isSuccess()
 	)									
								+ ", " + 
								    String.valueOf(getDesiredTeam()
 	)									
								
                   + ")";
        }
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=event+classtype[@name=impl]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=event+classtype[@name=impl]) ---        	            	
 	
		}
 	