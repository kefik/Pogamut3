package cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages;
 		
 		// --- IMPORTS FROM /messages/settings/javasettings/javaimport BEGIN
			import java.util.*;import javax.vecmath.*;import cz.cuni.amis.pogamut.base.communication.messages.*;import cz.cuni.amis.pogamut.base.communication.worldview.*;import cz.cuni.amis.pogamut.base.communication.worldview.event.*;import cz.cuni.amis.pogamut.base.communication.worldview.object.*;import cz.cuni.amis.pogamut.multi.communication.worldview.object.*;import cz.cuni.amis.pogamut.base.communication.translator.event.*;import cz.cuni.amis.pogamut.multi.communication.translator.event.*;import cz.cuni.amis.pogamut.base3d.worldview.object.*;import cz.cuni.amis.pogamut.base3d.worldview.object.event.*;import cz.cuni.amis.pogamut.ut2004.communication.messages.*;import cz.cuni.amis.pogamut.ut2004.communication.worldview.objects.*;import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.objects.*;import cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor.*;import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType.Category;import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;import cz.cuni.amis.utils.exception.*;import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdateResult.Result;import cz.cuni.amis.utils.SafeEquals;import cz.cuni.amis.pogamut.base.agent.*;import cz.cuni.amis.pogamut.multi.agent.*;import cz.cuni.amis.pogamut.multi.communication.worldview.property.*;import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.property.*;import cz.cuni.amis.utils.token.*;import cz.cuni.amis.utils.*;
		// --- IMPORTS FROM /messages/settings/javasettings/javaimport END
		
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name='all'] BEGIN
				
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name='all'] END
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name=local]+classtype[@name=impl] BEGIN
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name=local]+classtype[@name=impl] END
    
 		/**
         *  
            				Implementation of the local part of the GameBots2004 message ATR.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Synchronous message. Contains the results of automatically
		casted rays. One ATR message is for one casted ray. New
		automatically casted rays can be defined by ADDRAY command and
		removed by REMOVERAY command.
	
         */
 	public class AutoTraceRayLocalImpl 
  						extends
  						AutoTraceRayLocal
	    {
 	
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public AutoTraceRayLocalImpl()
		{
		}
	
    	
    	
    	
    	/**
		 * Creates new instance of the message AutoTraceRay.
		 * 
		Synchronous message. Contains the results of automatically
		casted rays. One ATR message is for one casted ray. New
		automatically casted rays can be defined by ADDRAY command and
		removed by REMOVERAY command.
	
		 * Corresponding GameBots message
		 *   (local part)
		 *   is
		 *   ATR.
		 * 
 	  	 * 
		 *   
		 *     @param Id 
			An Id for this ray (should be unique), assigned by the user
			when adding ray.
		
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
		 *     @param FastTrace 
			True if it is a fast trace, false if not (fast trace is a
			bit faster version of UT2004 ray trace - but provides us
			with less information - just true/false if we hit something
			on the way or not).
		
		 *   
		 * 
		 *   
		 *     @param FloorCorrection 
      If we should correct ray directions accoring floor normal. Note: Has issue - we can't set set rays up or down when correction is active.
		
		 *   
		 * 
		 *   
		 *     @param Result 
			True if it hit something, false if not.
		
		 *   
		 * 
		 *   
		 *     @param HitNormal 
			Vector with normal of the plane we have hit (not sent if
			FastTrace is True).
		
		 *   
		 * 
		 *   
		 *     @param HitLocation 
			Vector with location of the collision (not sent if FastTrace
			is True).
		
		 *   
		 * 
		 *   
		 *     @param TraceActors 
			If we traced also actors with this ray (actors – moving
			things in a game – bots, players, monsters, pickup …) (only
			if NOT using FastTrace)
		
		 *   
		 * 
		 *   
		 *     @param HitId 
			Id of the actor we have hit. (Sent if FastTrace is False and
			TraceActors is True).
		
		 *   
		 * 
		 */
		public AutoTraceRayLocalImpl(
			UnrealId Id,  Location From,  Location To,  boolean FastTrace,  boolean FloorCorrection,  boolean Result,  Vector3d HitNormal,  Location HitLocation,  boolean TraceActors,  UnrealId HitId
		) {
			
					this.Id = Id;
				
					this.From = From;
				
					this.To = To;
				
					this.FastTrace = FastTrace;
				
					this.FloorCorrection = FloorCorrection;
				
					this.Result = Result;
				
					this.HitNormal = HitNormal;
				
					this.HitLocation = HitLocation;
				
					this.TraceActors = TraceActors;
				
					this.HitId = HitId;
				
		}
    
    	/**
		 * Cloning constructor from the full message.
		 *
		 * @param original
		 */
		public AutoTraceRayLocalImpl(AutoTraceRay original) {		
			
					this.Id = original.getId()
 	;
				
					this.From = original.getFrom()
 	;
				
					this.To = original.getTo()
 	;
				
					this.FastTrace = original.isFastTrace()
 	;
				
					this.FloorCorrection = original.isFloorCorrection()
 	;
				
					this.Result = original.isResult()
 	;
				
					this.HitNormal = original.getHitNormal()
 	;
				
					this.HitLocation = original.getHitLocation()
 	;
				
					this.TraceActors = original.isTraceActors()
 	;
				
					this.HitId = original.getHitId()
 	;
				
			this.SimTime = original.getSimTime();			
		}
		
		/**
		 * Cloning constructor from the full message.
		 *
		 * @param original
		 */
		public AutoTraceRayLocalImpl(AutoTraceRayLocalImpl original) {		
			
					this.Id = original.getId()
 	;
				
					this.From = original.getFrom()
 	;
				
					this.To = original.getTo()
 	;
				
					this.FastTrace = original.isFastTrace()
 	;
				
					this.FloorCorrection = original.isFloorCorrection()
 	;
				
					this.Result = original.isResult()
 	;
				
					this.HitNormal = original.getHitNormal()
 	;
				
					this.HitLocation = original.getHitLocation()
 	;
				
					this.TraceActors = original.isTraceActors()
 	;
				
					this.HitId = original.getHitId()
 	;
				
			this.SimTime = original.getSimTime();
		}
		
			/**
			 * Cloning constructor from the message part.
			 *
			 * @param original
			 */
			public AutoTraceRayLocalImpl(AutoTraceRayLocal original) {
				
						this.Id = original.getId()
 	;
					
						this.From = original.getFrom()
 	;
					
						this.To = original.getTo()
 	;
					
						this.FastTrace = original.isFastTrace()
 	;
					
						this.FloorCorrection = original.isFloorCorrection()
 	;
					
						this.Result = original.isResult()
 	;
					
						this.HitNormal = original.getHitNormal()
 	;
					
						this.HitLocation = original.getHitLocation()
 	;
					
						this.TraceActors = original.isTraceActors()
 	;
					
						this.HitId = original.getHitId()
 	;
					
			}
		
   				
   				@Override
   				public void setSimTime(long SimTime) {
					super.setSimTime(SimTime);
				}
   			
	    				@Override
	    				public 
	    				AutoTraceRayLocalImpl clone() {
	    					return new 
	    					AutoTraceRayLocalImpl(this);
	    				}
	    				
	    				
    	
	    /**
         * 
			An Id for this ray (should be unique), assigned by the user
			when adding ray.
		 
         */
        protected
         UnrealId Id =
       	null;
	
 		/**
         * 
			An Id for this ray (should be unique), assigned by the user
			when adding ray.
		 
         */
        public  UnrealId getId()
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
			True if it is a fast trace, false if not (fast trace is a
			bit faster version of UT2004 ray trace - but provides us
			with less information - just true/false if we hit something
			on the way or not).
		 
         */
        protected
         boolean FastTrace =
       	false;
	
 		/**
         * 
			True if it is a fast trace, false if not (fast trace is a
			bit faster version of UT2004 ray trace - but provides us
			with less information - just true/false if we hit something
			on the way or not).
		 
         */
        public  boolean isFastTrace()
 	 {
				    					return FastTrace;
				    				}
				    			
    	
	    /**
         * 
      If we should correct ray directions accoring floor normal. Note: Has issue - we can't set set rays up or down when correction is active.
		 
         */
        protected
         boolean FloorCorrection =
       	false;
	
 		/**
         * 
      If we should correct ray directions accoring floor normal. Note: Has issue - we can't set set rays up or down when correction is active.
		 
         */
        public  boolean isFloorCorrection()
 	 {
				    					return FloorCorrection;
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
			Vector with normal of the plane we have hit (not sent if
			FastTrace is True).
		 
         */
        protected
         Vector3d HitNormal =
       	null;
	
 		/**
         * 
			Vector with normal of the plane we have hit (not sent if
			FastTrace is True).
		 
         */
        public  Vector3d getHitNormal()
 	 {
				    					return HitNormal;
				    				}
				    			
    	
	    /**
         * 
			Vector with location of the collision (not sent if FastTrace
			is True).
		 
         */
        protected
         Location HitLocation =
       	null;
	
 		/**
         * 
			Vector with location of the collision (not sent if FastTrace
			is True).
		 
         */
        public  Location getHitLocation()
 	 {
				    					return HitLocation;
				    				}
				    			
    	
	    /**
         * 
			If we traced also actors with this ray (actors – moving
			things in a game – bots, players, monsters, pickup …) (only
			if NOT using FastTrace)
		 
         */
        protected
         boolean TraceActors =
       	false;
	
 		/**
         * 
			If we traced also actors with this ray (actors – moving
			things in a game – bots, players, monsters, pickup …) (only
			if NOT using FastTrace)
		 
         */
        public  boolean isTraceActors()
 	 {
				    					return TraceActors;
				    				}
				    			
    	
	    /**
         * 
			Id of the actor we have hit. (Sent if FastTrace is False and
			TraceActors is True).
		 
         */
        protected
         UnrealId HitId =
       	null;
	
 		/**
         * 
			Id of the actor we have hit. (Sent if FastTrace is False and
			TraceActors is True).
		 
         */
        public  UnrealId getHitId()
 	 {
				    					return HitId;
				    				}
				    			
    	
    	
    	
    	
    	public AutoTraceRayLocalImpl getLocal() {
			return this;
    	}
		public ISharedWorldObject getShared() {
		 	throw new UnsupportedOperationException("Could not return LOCAL as SHARED");
		}
		public IStaticWorldObject getStatic() {
		    throw new UnsupportedOperationException("Could not return LOCAL as STATIC");
		}
 	
		public static class AutoTraceRayLocalUpdate
     implements ILocalWorldObjectUpdatedEvent, IGBWorldObjectEvent
		{
			protected long time;
			
			protected AutoTraceRayLocal data = null; //contains object data for this update
			
			public AutoTraceRayLocalUpdate
    (AutoTraceRayLocal moverLocal, long time)
			{
				this.data = moverLocal;
				this.time = time;
			}
			
			@Override
			public IWorldObjectUpdateResult<ILocalWorldObject> update(
					ILocalWorldObject object) 
			{
				if ( object == null)
				{
					data = new AutoTraceRayLocalImpl(data); //we always return Impl object
					return new IWorldObjectUpdateResult.WorldObjectUpdateResult<ILocalWorldObject>(IWorldObjectUpdateResult.Result.CREATED, data);
				}
				if ( object instanceof AutoTraceRayLocalImpl )
				{
					AutoTraceRayLocalImpl toUpdate = (AutoTraceRayLocalImpl)object;
					
					boolean updated = false;
					
					// UPDATING LOCAL PROPERTIES
					
	            if (!SafeEquals.equals(toUpdate.From, data.getFrom()
 	)) {
					toUpdate.From=data.getFrom()
 	;
					updated = true;
				}
			
	            if (!SafeEquals.equals(toUpdate.To, data.getTo()
 	)) {
					toUpdate.To=data.getTo()
 	;
					updated = true;
				}
			
				if (toUpdate.FastTrace != data.isFastTrace()
 	) {
				    toUpdate.FastTrace=data.isFastTrace()
 	;
					updated = true;
				}
			
				if (toUpdate.FloorCorrection != data.isFloorCorrection()
 	) {
				    toUpdate.FloorCorrection=data.isFloorCorrection()
 	;
					updated = true;
				}
			
				if (toUpdate.Result != data.isResult()
 	) {
				    toUpdate.Result=data.isResult()
 	;
					updated = true;
				}
			
				if (!SafeEquals.equals(toUpdate.HitNormal, data.getHitNormal()
 	)) {
					toUpdate.HitNormal=data.getHitNormal()
 	;
					updated = true;
				}
			
	            if (!SafeEquals.equals(toUpdate.HitLocation, data.getHitLocation()
 	)) {
					toUpdate.HitLocation=data.getHitLocation()
 	;
					updated = true;
				}
			
				if (toUpdate.TraceActors != data.isTraceActors()
 	) {
				    toUpdate.TraceActors=data.isTraceActors()
 	;
					updated = true;
				}
			
				if (!SafeEquals.equals(toUpdate.HitId, data.getHitId()
 	)) {
					toUpdate.HitId=data.getHitId()
 	;
					updated = true;
				}
			
					
					data = toUpdate; //the updating has finished
					
					if ( updated )
					{
						toUpdate.SimTime = this.time;
						return new IWorldObjectUpdateResult.WorldObjectUpdateResult<ILocalWorldObject>(IWorldObjectUpdateResult.Result.UPDATED, data);
					}
					
					return new IWorldObjectUpdateResult.WorldObjectUpdateResult<ILocalWorldObject>(IWorldObjectUpdateResult.Result.SAME, data);
				}
				throw new PogamutException("Unsupported object type for update. Expected AutoTraceRayLocalImpl for object " + object.getId() +", not object of class " + object.getClass().getSimpleName() + ".", this);
			}
	
			/**
			 * Simulation time in MILLI SECONDS !!!
			 */
			@Override
			public long getSimTime() {
				return this.time;
			}
	
			@Override
			public IWorldObject getObject() {
				return data;
			}
	
			@Override
			public WorldObjectId getId() {
				return data.getId();
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
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=local+classtype[@name=impl]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=local+classtype[@name=impl]) ---        	            	
 	
		}
 	