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
         			Definition of the event END.
         		
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Synchronous message. This message signalizes end of
		synchronous
		batch.
	
         */
 	public class EndMessage 
  				extends InfoMessage
    			implements IWorldEvent, IWorldChangeEvent
    			
	    {
 	
    	/** Example how the message looks like - used during parser tests. */
    	public static final String PROTOTYPE =
    		"END {Time 0}  {VisUpdate False} ";
    
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public EndMessage()
		{
		}
	
    	
    	
    	
    	/**
		 * Creates new instance of the message EndMessage.
		 * 
		Synchronous message. This message signalizes end of
		synchronous
		batch.
	
		 * Corresponding GameBots message
		 *   
		 *   is
		 *   END.
		 * 
 	  	 * 
		 *   
		 *     @param Time 
			Time, when the message was sent - intern UT time.
		
		 *   
		 * 
		 *   
		 *     @param VisUpdate Sent only if new synchronous batch procotol is used (NewSelfBatchProtocol attribute in INIT message). If true it means this sync. batches contains also visiblity information - PLR, INV, PRJ messages... If false, it means that only SELF message is in this synchronous batch.
		 *   
		 * 
		 */
		public EndMessage(
			double Time,  boolean VisUpdate
		) {
			
					this.Time = Time;
				
					this.VisUpdate = VisUpdate;
				
		}
    
    	/**
		 * Cloning constructor from the full message.
		 *
		 * @param original
		 */
		public EndMessage(EndMessage original) {		
			
					this.Time = original.getTime()
 	;
				
					this.VisUpdate = original.isVisUpdate()
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
			Time, when the message was sent - intern UT time.
		 
         */
        protected
         double Time =
       	0;
	
 		/**
         * 
			Time, when the message was sent - intern UT time.
		 
         */
        public  double getTime()
 	 {
    					return Time;
    				}
    			
    	
	    /**
         * Sent only if new synchronous batch procotol is used (NewSelfBatchProtocol attribute in INIT message). If true it means this sync. batches contains also visiblity information - PLR, INV, PRJ messages... If false, it means that only SELF message is in this synchronous batch. 
         */
        protected
         boolean VisUpdate =
       	false;
	
 		/**
         * Sent only if new synchronous batch procotol is used (NewSelfBatchProtocol attribute in INIT message). If true it means this sync. batches contains also visiblity information - PLR, INV, PRJ messages... If false, it means that only SELF message is in this synchronous batch. 
         */
        public  boolean isVisUpdate()
 	 {
    					return VisUpdate;
    				}
    			
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
		              			"Time = " + String.valueOf(getTime()
 	) + " | " + 
		              		
		              			"VisUpdate = " + String.valueOf(isVisUpdate()
 	) + " | " + 
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>Time</b> = " + String.valueOf(getTime()
 	) + " <br/> " + 
		              		
		              			"<b>VisUpdate</b> = " + String.valueOf(isVisUpdate()
 	) + " <br/> " + 
		              		
				"<br/>]";     
		}
 	
 	    public String toJsonLiteral() {
            return "endmessage( "
            		+
								    String.valueOf(getTime()
 	)									
								+ ", " + 
								    String.valueOf(isVisUpdate()
 	)									
								
                   + ")";
        }
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=event+classtype[@name=impl]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=event+classtype[@name=impl]) ---        	            	
 	
		}
 	