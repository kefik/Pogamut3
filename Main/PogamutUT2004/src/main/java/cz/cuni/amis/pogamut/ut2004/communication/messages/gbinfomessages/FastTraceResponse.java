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
         			Definition of the event FTR.
         		
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Asynchronous message. Response of the FASTTRACE command. Note
		that trace commands are computationally expensive.
	
         */
 	public class FastTraceResponse 
  				extends InfoMessage
    			implements IWorldEvent, IWorldChangeEvent
    			
	    {
 	
    	/** Example how the message looks like - used during parser tests. */
    	public static final String PROTOTYPE =
    		"FTR {Id text}  {From 0,0,0}  {To 0,0,0}  {Result False} ";
    
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public FastTraceResponse()
		{
		}
	
    	
    	
    	
    	/**
		 * Creates new instance of the message FastTraceResponse.
		 * 
		Asynchronous message. Response of the FASTTRACE command. Note
		that trace commands are computationally expensive.
	
		 * Corresponding GameBots message
		 *   
		 *   is
		 *   FTR.
		 * 
 	  	 * 
		 *   
		 *     @param Id 
			An Id matching the one sent by client. Allows bot to match
			answer with right query.
		
		 *   
		 * 
		 *   
		 *     @param From 
			Location from which the ray is emitted.
		
		 *   
		 * 
		 *   
		 *     @param To 
			Location to which the ray is sent.
		
		 *   
		 * 
		 *   
		 *     @param Result 
			True if it hit something, false if not.
		
		 *   
		 * 
		 */
		public FastTraceResponse(
			String Id,  Location From,  Location To,  boolean Result
		) {
			
					this.Id = Id;
				
					this.From = From;
				
					this.To = To;
				
					this.Result = Result;
				
		}
    
    	/**
		 * Cloning constructor from the full message.
		 *
		 * @param original
		 */
		public FastTraceResponse(FastTraceResponse original) {		
			
					this.Id = original.getId()
 	;
				
					this.From = original.getFrom()
 	;
				
					this.To = original.getTo()
 	;
				
					this.Result = original.isResult()
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
			answer with right query.
		 
         */
        protected
         String Id =
       	null;
	
 		/**
         * 
			An Id matching the one sent by client. Allows bot to match
			answer with right query.
		 
         */
        public  String getId()
 	 {
    					return Id;
    				}
    			
    	
	    /**
         * 
			Location from which the ray is emitted.
		 
         */
        protected
         Location From =
       	null;
	
 		/**
         * 
			Location from which the ray is emitted.
		 
         */
        public  Location getFrom()
 	 {
    					return From;
    				}
    			
    	
	    /**
         * 
			Location to which the ray is sent.
		 
         */
        protected
         Location To =
       	null;
	
 		/**
         * 
			Location to which the ray is sent.
		 
         */
        public  Location getTo()
 	 {
    					return To;
    				}
    			
    	
	    /**
         * 
			True if it hit something, false if not.
		 
         */
        protected
         boolean Result =
       	false;
	
 		/**
         * 
			True if it hit something, false if not.
		 
         */
        public  boolean isResult()
 	 {
    					return Result;
    				}
    			
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
		              			"Id = " + String.valueOf(getId()
 	) + " | " + 
		              		
		              			"From = " + String.valueOf(getFrom()
 	) + " | " + 
		              		
		              			"To = " + String.valueOf(getTo()
 	) + " | " + 
		              		
		              			"Result = " + String.valueOf(isResult()
 	) + " | " + 
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>Id</b> = " + String.valueOf(getId()
 	) + " <br/> " + 
		              		
		              			"<b>From</b> = " + String.valueOf(getFrom()
 	) + " <br/> " + 
		              		
		              			"<b>To</b> = " + String.valueOf(getTo()
 	) + " <br/> " + 
		              		
		              			"<b>Result</b> = " + String.valueOf(isResult()
 	) + " <br/> " + 
		              		
				"<br/>]";     
		}
 	
 	    public String toJsonLiteral() {
            return "fasttraceresponse( "
            		+
									(getId()
 	 == null ? "null" :
										"\"" + getId()
 	 + "\"" 
									)
								+ ", " + 
								    (getFrom()
 	 == null ? "null" :
										"[" + getFrom()
 	.getX() + ", " + getFrom()
 	.getY() + ", " + getFrom()
 	.getZ() + "]" 
									)
								+ ", " + 
								    (getTo()
 	 == null ? "null" :
										"[" + getTo()
 	.getX() + ", " + getTo()
 	.getY() + ", " + getTo()
 	.getZ() + "]" 
									)
								+ ", " + 
								    String.valueOf(isResult()
 	)									
								
                   + ")";
        }
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=event+classtype[@name=impl]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=event+classtype[@name=impl]) ---        	            	
 	
		}
 	