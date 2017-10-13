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
         			Definition of the event USED.
         		
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Asynchronous message. Response to USE (FactoryUse) command.
	
         */
 	public class FactoryUsed 
  				extends InfoMessage
    			implements IWorldEvent, IWorldChangeEvent
    			
	    {
 	
    	/** Example how the message looks like - used during parser tests. */
    	public static final String PROTOTYPE =
    		"USED {Success False}  {Reason text} ";
    
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public FactoryUsed()
		{
		}
	
    	
    	
    	
    	/**
		 * Creates new instance of the message FactoryUsed.
		 * 
		Asynchronous message. Response to USE (FactoryUse) command.
	
		 * Corresponding GameBots message
		 *   
		 *   is
		 *   USED.
		 * 
 	  	 * 
		 *   
		 *     @param Success 
			If we have successfully used the factory.
		
		 *   
		 * 
		 *   
		 *     @param Reason 
			If success is false, the reason why we couldn't use a factory will be here.
		
		 *   
		 * 
		 */
		public FactoryUsed(
			boolean Success,  String Reason
		) {
			
					this.Success = Success;
				
					this.Reason = Reason;
				
		}
    
    	/**
		 * Cloning constructor from the full message.
		 *
		 * @param original
		 */
		public FactoryUsed(FactoryUsed original) {		
			
					this.Success = original.isSuccess()
 	;
				
					this.Reason = original.getReason()
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
			If we have successfully used the factory.
		 
         */
        protected
         boolean Success =
       	false;
	
 		/**
         * 
			If we have successfully used the factory.
		 
         */
        public  boolean isSuccess()
 	 {
    					return Success;
    				}
    			
    	
	    /**
         * 
			If success is false, the reason why we couldn't use a factory will be here.
		 
         */
        protected
         String Reason =
       	null;
	
 		/**
         * 
			If success is false, the reason why we couldn't use a factory will be here.
		 
         */
        public  String getReason()
 	 {
    					return Reason;
    				}
    			
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
		              			"Success = " + String.valueOf(isSuccess()
 	) + " | " + 
		              		
		              			"Reason = " + String.valueOf(getReason()
 	) + " | " + 
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>Success</b> = " + String.valueOf(isSuccess()
 	) + " <br/> " + 
		              		
		              			"<b>Reason</b> = " + String.valueOf(getReason()
 	) + " <br/> " + 
		              		
				"<br/>]";     
		}
 	
 	    public String toJsonLiteral() {
            return "factoryused( "
            		+
								    String.valueOf(isSuccess()
 	)									
								+ ", " + 
									(getReason()
 	 == null ? "null" :
										"\"" + getReason()
 	 + "\"" 
									)
								
                   + ")";
        }
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=event+classtype[@name=impl]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=event+classtype[@name=impl]) ---        	            	
 	
		}
 	