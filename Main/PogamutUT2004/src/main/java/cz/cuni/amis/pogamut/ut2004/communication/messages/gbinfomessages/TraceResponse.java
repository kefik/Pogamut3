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
         			Definition of the event TRC.
         		
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Aynchronous message. Response to TRACE command.
	
         */
 	public class TraceResponse 
  				extends InfoMessage
    			implements IWorldEvent, IWorldChangeEvent
    			
	    {
 	
    	/** Example how the message looks like - used during parser tests. */
    	public static final String PROTOTYPE =
    		"TRC {Id text}  {From 0,0,0}  {To 0,0,0}  {Result False}  {HitNormal 0,0,0}  {HitLocation 0,0,0}  {HitID unreal_id}  {TraceActors False} ";
    
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public TraceResponse()
		{
		}
	
    	
    	
    	
    	/**
		 * Creates new instance of the message TraceResponse.
		 * 
		Aynchronous message. Response to TRACE command.
	
		 * Corresponding GameBots message
		 *   
		 *   is
		 *   TRC.
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
		 *   
		 *     @param HitNormal 
			Normal vector to the trace ray in the point of hit.
		
		 *   
		 * 
		 *   
		 *     @param HitLocation Point of the hit.
		 *   
		 * 
		 *   
		 *     @param HitID 
			Id of the thing we have hit. May be other player or some
			item or level geometry.
		
		 *   
		 * 
		 *   
		 *     @param TraceActors 
			True if we are tracing also actors in the game (players,
			items). False if we are tracing just level geometry.
		
		 *   
		 * 
		 */
		public TraceResponse(
			String Id,  Location From,  Location To,  boolean Result,  Vector3d HitNormal,  Vector3d HitLocation,  UnrealId HitID,  boolean TraceActors
		) {
			
					this.Id = Id;
				
					this.From = From;
				
					this.To = To;
				
					this.Result = Result;
				
					this.HitNormal = HitNormal;
				
					this.HitLocation = HitLocation;
				
					this.HitID = HitID;
				
					this.TraceActors = TraceActors;
				
		}
    
    	/**
		 * Cloning constructor from the full message.
		 *
		 * @param original
		 */
		public TraceResponse(TraceResponse original) {		
			
					this.Id = original.getId()
 	;
				
					this.From = original.getFrom()
 	;
				
					this.To = original.getTo()
 	;
				
					this.Result = original.isResult()
 	;
				
					this.HitNormal = original.getHitNormal()
 	;
				
					this.HitLocation = original.getHitLocation()
 	;
				
					this.HitID = original.getHitID()
 	;
				
					this.TraceActors = original.isTraceActors()
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
    			
    	
	    /**
         * 
			Normal vector to the trace ray in the point of hit.
		 
         */
        protected
         Vector3d HitNormal =
       	null;
	
 		/**
         * 
			Normal vector to the trace ray in the point of hit.
		 
         */
        public  Vector3d getHitNormal()
 	 {
    					return HitNormal;
    				}
    			
    	
	    /**
         * Point of the hit. 
         */
        protected
         Vector3d HitLocation =
       	null;
	
 		/**
         * Point of the hit. 
         */
        public  Vector3d getHitLocation()
 	 {
    					return HitLocation;
    				}
    			
    	
	    /**
         * 
			Id of the thing we have hit. May be other player or some
			item or level geometry.
		 
         */
        protected
         UnrealId HitID =
       	null;
	
 		/**
         * 
			Id of the thing we have hit. May be other player or some
			item or level geometry.
		 
         */
        public  UnrealId getHitID()
 	 {
    					return HitID;
    				}
    			
    	
	    /**
         * 
			True if we are tracing also actors in the game (players,
			items). False if we are tracing just level geometry.
		 
         */
        protected
         boolean TraceActors =
       	false;
	
 		/**
         * 
			True if we are tracing also actors in the game (players,
			items). False if we are tracing just level geometry.
		 
         */
        public  boolean isTraceActors()
 	 {
    					return TraceActors;
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
		              		
		              			"HitNormal = " + String.valueOf(getHitNormal()
 	) + " | " + 
		              		
		              			"HitLocation = " + String.valueOf(getHitLocation()
 	) + " | " + 
		              		
		              			"HitID = " + String.valueOf(getHitID()
 	) + " | " + 
		              		
		              			"TraceActors = " + String.valueOf(isTraceActors()
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
		              		
		              			"<b>HitNormal</b> = " + String.valueOf(getHitNormal()
 	) + " <br/> " + 
		              		
		              			"<b>HitLocation</b> = " + String.valueOf(getHitLocation()
 	) + " <br/> " + 
		              		
		              			"<b>HitID</b> = " + String.valueOf(getHitID()
 	) + " <br/> " + 
		              		
		              			"<b>TraceActors</b> = " + String.valueOf(isTraceActors()
 	) + " <br/> " + 
		              		
				"<br/>]";     
		}
 	
 	    public String toJsonLiteral() {
            return "traceresponse( "
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
								+ ", " + 
									(getHitNormal()
 	 == null ? "null" :
										"[" + getHitNormal()
 	.getX() + ", " + getHitNormal()
 	.getY() + ", " + getHitNormal()
 	.getZ() + "]" 
									)
								+ ", " + 
									(getHitLocation()
 	 == null ? "null" :
										"[" + getHitLocation()
 	.getX() + ", " + getHitLocation()
 	.getY() + ", " + getHitLocation()
 	.getZ() + "]" 
									)
								+ ", " + 
									(getHitID()
 	 == null ? "null" :
										"\"" + getHitID()
 	.getStringId() + "\"" 
									)
								+ ", " + 
								    String.valueOf(isTraceActors()
 	)									
								
                   + ")";
        }
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=event+classtype[@name=impl]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=event+classtype[@name=impl]) ---        	            	
 	
		}
 	