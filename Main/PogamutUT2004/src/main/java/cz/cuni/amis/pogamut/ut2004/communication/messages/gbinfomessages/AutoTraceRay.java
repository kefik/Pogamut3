package cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages;
 		
 		// --- IMPORTS FROM /messages/settings/javasettings/javaimport BEGIN
			import java.util.*;import javax.vecmath.*;import cz.cuni.amis.pogamut.base.communication.messages.*;import cz.cuni.amis.pogamut.base.communication.worldview.*;import cz.cuni.amis.pogamut.base.communication.worldview.event.*;import cz.cuni.amis.pogamut.base.communication.worldview.object.*;import cz.cuni.amis.pogamut.multi.communication.worldview.object.*;import cz.cuni.amis.pogamut.base.communication.translator.event.*;import cz.cuni.amis.pogamut.multi.communication.translator.event.*;import cz.cuni.amis.pogamut.base3d.worldview.object.*;import cz.cuni.amis.pogamut.base3d.worldview.object.event.*;import cz.cuni.amis.pogamut.ut2004.communication.messages.*;import cz.cuni.amis.pogamut.ut2004.communication.worldview.objects.*;import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.objects.*;import cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor.*;import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType.Category;import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;import cz.cuni.amis.utils.exception.*;import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdateResult.Result;import cz.cuni.amis.utils.SafeEquals;import cz.cuni.amis.pogamut.base.agent.*;import cz.cuni.amis.pogamut.multi.agent.*;import cz.cuni.amis.pogamut.multi.communication.worldview.property.*;import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.property.*;import cz.cuni.amis.utils.token.*;import cz.cuni.amis.utils.*;
		// --- IMPORTS FROM /messages/settings/javasettings/javaimport END
		
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name='all'] BEGIN
				
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name='all'] END
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name=base]+classtype[@name=abstract] BEGIN
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name=base]+classtype[@name=abstract] END
    
 		/**
         *  
            				Abstract definition of the GameBots2004 message ATR.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Synchronous message. Contains the results of automatically
		casted rays. One ATR message is for one casted ray. New
		automatically casted rays can be defined by ADDRAY command and
		removed by REMOVERAY command.
	
         */
 	public abstract class AutoTraceRay   
  				extends 
  				InfoMessage
  						implements IWorldEvent, IWorldChangeEvent, ICompositeWorldObject
  						
	    {
 	
    	/** Example how the message looks like - used during parser tests. */
    	public static final String PROTOTYPE =
    		"ATR {Id unreal_id}  {From 0,0,0}  {To 0,0,0}  {FastTrace False}  {FloorCorrection False}  {Result False}  {HitNormal 0,0,0}  {HitLocation 0,0,0}  {TraceActors False}  {HitId unreal_id} ";
    
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public AutoTraceRay()
		{
		}
	
				// abstract message, it does not have any more constructors				
			
	   		
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
			An Id for this ray (should be unique), assigned by the user
			when adding ray.
		 
         */
        public abstract UnrealId getId()
 	;
		    			
 		/**
         * 
			Location from which the ray is emitted.
		 
         */
        public abstract Location getFrom()
 	;
		    			
 		/**
         * 
			Location to which the ray is sent.
		 
         */
        public abstract Location getTo()
 	;
		    			
 		/**
         * 
			True if it is a fast trace, false if not (fast trace is a
			bit faster version of UT2004 ray trace - but provides us
			with less information - just true/false if we hit something
			on the way or not).
		 
         */
        public abstract boolean isFastTrace()
 	;
		    			
 		/**
         * 
      If we should correct ray directions accoring floor normal. Note: Has issue - we can't set set rays up or down when correction is active.
		 
         */
        public abstract boolean isFloorCorrection()
 	;
		    			
 		/**
         * 
			True if it hit something, false if not.
		 
         */
        public abstract boolean isResult()
 	;
		    			
 		/**
         * 
			Vector with normal of the plane we have hit (not sent if
			FastTrace is True).
		 
         */
        public abstract Vector3d getHitNormal()
 	;
		    			
 		/**
         * 
			Vector with location of the collision (not sent if FastTrace
			is True).
		 
         */
        public abstract Location getHitLocation()
 	;
		    			
 		/**
         * 
			If we traced also actors with this ray (actors – moving
			things in a game – bots, players, monsters, pickup …) (only
			if NOT using FastTrace)
		 
         */
        public abstract boolean isTraceActors()
 	;
		    			
 		/**
         * 
			Id of the actor we have hit. (Sent if FastTrace is False and
			TraceActors is True).
		 
         */
        public abstract UnrealId getHitId()
 	;
		    			
    	
    	public static class AutoTraceRayUpdate
     extends GBObjectUpdate implements ICompositeWorldObjectUpdatedEvent, IGBWorldObjectEvent {	
			private AutoTraceRay object;
			private long time;
			private ITeamId teamId;
			
			public AutoTraceRayUpdate
    (AutoTraceRay source, long eventTime, ITeamId teamId) {
				this.object = source;
				this.time = eventTime;
				this.teamId = teamId;
			}
			
			/**
			 * Simulation time in MILLI SECONDS !!!
			 */ 
			@Override
			public long getSimTime() {
				return time;
			}
	
			@Override
			public IWorldObject getObject() {
				return object;
			}
	
			@Override
			public WorldObjectId getId() {
				return object.getId();
			}
	
			@Override
			public ILocalWorldObjectUpdatedEvent getLocalEvent() {
				return new AutoTraceRayLocalImpl.AutoTraceRayLocalUpdate
    ((AutoTraceRayLocal)object.getLocal(), time);
			}
	
			@Override
			public ISharedWorldObjectUpdatedEvent getSharedEvent() {
				return new AutoTraceRaySharedImpl.AutoTraceRaySharedUpdate
    ((AutoTraceRayShared)object.getShared(), time, teamId);
			}
	
			@Override
			public IStaticWorldObjectUpdatedEvent getStaticEvent() {
				return new AutoTraceRayStaticImpl.AutoTraceRayStaticUpdate
    ((AutoTraceRayStatic)object.getStatic(), time);
			}
			
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
		              		
		              			"FastTrace = " + String.valueOf(isFastTrace()
 	) + " | " + 
		              		
		              			"FloorCorrection = " + String.valueOf(isFloorCorrection()
 	) + " | " + 
		              		
		              			"Result = " + String.valueOf(isResult()
 	) + " | " + 
		              		
		              			"HitNormal = " + String.valueOf(getHitNormal()
 	) + " | " + 
		              		
		              			"HitLocation = " + String.valueOf(getHitLocation()
 	) + " | " + 
		              		
		              			"TraceActors = " + String.valueOf(isTraceActors()
 	) + " | " + 
		              		
		              			"HitId = " + String.valueOf(getHitId()
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
		              		
		              			"<b>FastTrace</b> = " + String.valueOf(isFastTrace()
 	) + " <br/> " + 
		              		
		              			"<b>FloorCorrection</b> = " + String.valueOf(isFloorCorrection()
 	) + " <br/> " + 
		              		
		              			"<b>Result</b> = " + String.valueOf(isResult()
 	) + " <br/> " + 
		              		
		              			"<b>HitNormal</b> = " + String.valueOf(getHitNormal()
 	) + " <br/> " + 
		              		
		              			"<b>HitLocation</b> = " + String.valueOf(getHitLocation()
 	) + " <br/> " + 
		              		
		              			"<b>TraceActors</b> = " + String.valueOf(isTraceActors()
 	) + " <br/> " + 
		              		
		              			"<b>HitId</b> = " + String.valueOf(getHitId()
 	) + " <br/> " + 
		              		
				"<br/>]";     
		}
 	
 	    public String toJsonLiteral() {
            return "autotraceray( "
            		+
									(getId()
 	 == null ? "null" :
										"\"" + getId()
 	.getStringId() + "\"" 
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
								    String.valueOf(isFastTrace()
 	)									
								+ ", " + 
								    String.valueOf(isFloorCorrection()
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
								    String.valueOf(isTraceActors()
 	)									
								+ ", " + 
									(getHitId()
 	 == null ? "null" :
										"\"" + getHitId()
 	.getStringId() + "\"" 
									)
								
                   + ")";
        }
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=base+classtype[@name=abstract]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=base+classtype[@name=abstract]) ---        	            	
 	
		}
 	