package cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages;
 		
 		// --- IMPORTS FROM /messages/settings/javasettings/javaimport BEGIN
			import java.util.*;import javax.vecmath.*;import cz.cuni.amis.pogamut.base.communication.messages.*;import cz.cuni.amis.pogamut.base.communication.worldview.*;import cz.cuni.amis.pogamut.base.communication.worldview.event.*;import cz.cuni.amis.pogamut.base.communication.worldview.object.*;import cz.cuni.amis.pogamut.multi.communication.worldview.object.*;import cz.cuni.amis.pogamut.base.communication.translator.event.*;import cz.cuni.amis.pogamut.multi.communication.translator.event.*;import cz.cuni.amis.pogamut.base3d.worldview.object.*;import cz.cuni.amis.pogamut.base3d.worldview.object.event.*;import cz.cuni.amis.pogamut.ut2004.communication.messages.*;import cz.cuni.amis.pogamut.ut2004.communication.worldview.objects.*;import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.objects.*;import cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor.*;import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType.Category;import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;import cz.cuni.amis.utils.exception.*;import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdateResult.Result;import cz.cuni.amis.utils.SafeEquals;import cz.cuni.amis.pogamut.base.agent.*;import cz.cuni.amis.pogamut.multi.agent.*;import cz.cuni.amis.pogamut.multi.communication.worldview.property.*;import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.property.*;import cz.cuni.amis.utils.token.*;import cz.cuni.amis.utils.*;
		// --- IMPORTS FROM /messages/settings/javasettings/javaimport END
		
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name='all'] BEGIN
				
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name='all'] END
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name=base]+classtype[@name=message] BEGIN
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name=base]+classtype[@name=message] END
    
 		/**
         *  
             				Implementation of the GameBots2004 message ATR contains also its Local/Shared/Static subpart class definitions..  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Synchronous message. Contains the results of automatically
		casted rays. One ATR message is for one casted ray. New
		automatically casted rays can be defined by ADDRAY command and
		removed by REMOVERAY command.
	
         */
 	public class AutoTraceRayMessage   
  				extends 
  				AutoTraceRay
  						implements IWorldObjectUpdatedEvent, ICompositeWorldObjectUpdatedEvent
  						
	    {
 	
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public AutoTraceRayMessage()
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
		 *   
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
		public AutoTraceRayMessage(
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
		public AutoTraceRayMessage(AutoTraceRayMessage original) {		
			
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
				
				this.TeamId = original.getTeamId();
			
			this.SimTime = original.getSimTime();
		}
		
    				
    					protected ITeamId TeamId;
    					
    					/**
    					 * Used by Yylex to slip corretn TeamId.
    					 */
    					protected void setTeamId(ITeamId TeamId) {
    					    this.TeamId = TeamId;
    					}
    				
    					public ITeamId getTeamId() {
							return TeamId;
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
		 					 * Whether property 'Id' was received from GB2004.
		 					 */
							protected boolean Id_Set = false;
							
    						@Override
		    				
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
		 					 * Whether property 'From' was received from GB2004.
		 					 */
							protected boolean From_Set = false;
							
    						@Override
		    				
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
		 					 * Whether property 'To' was received from GB2004.
		 					 */
							protected boolean To_Set = false;
							
    						@Override
		    				
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
		 					 * Whether property 'FastTrace' was received from GB2004.
		 					 */
							protected boolean FastTrace_Set = false;
							
    						@Override
		    				
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
		 					 * Whether property 'FloorCorrection' was received from GB2004.
		 					 */
							protected boolean FloorCorrection_Set = false;
							
    						@Override
		    				
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
		 					 * Whether property 'Result' was received from GB2004.
		 					 */
							protected boolean Result_Set = false;
							
    						@Override
		    				
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
		 					 * Whether property 'HitNormal' was received from GB2004.
		 					 */
							protected boolean HitNormal_Set = false;
							
    						@Override
		    				
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
		 					 * Whether property 'HitLocation' was received from GB2004.
		 					 */
							protected boolean HitLocation_Set = false;
							
    						@Override
		    				
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
		 					 * Whether property 'TraceActors' was received from GB2004.
		 					 */
							protected boolean TraceActors_Set = false;
							
    						@Override
		    				
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
		 					 * Whether property 'HitId' was received from GB2004.
		 					 */
							protected boolean HitId_Set = false;
							
    						@Override
		    				
 		/**
         * 
			Id of the actor we have hit. (Sent if FastTrace is False and
			TraceActors is True).
		 
         */
        public  UnrealId getHitId()
 	 {
		    					return HitId;
		    				}
		    			
		    			
		    			private AutoTraceRayLocal localPart = null;
		    			
		    			@Override
						public AutoTraceRayLocal 
						getLocal() {
							if (localPart != null) return localPart;
							return localPart = new 
								AutoTraceRayLocalMessage();
						}
					
						private AutoTraceRayShared sharedPart = null;
					
						@Override
						public AutoTraceRayShared 
						getShared() {
							if (sharedPart != null) return sharedPart;							
							return sharedPart = new 
								AutoTraceRaySharedMessage();
						}
					
						private AutoTraceRayStatic staticPart = null; 
					
						@Override
						public AutoTraceRayStatic 
						getStatic() {
							if (staticPart != null) return staticPart;
							return staticPart = new 
								AutoTraceRayStaticMessage();
						}
    				
 		/**
         *  
            				Implementation of the local part of the GameBots2004 message ATR, used
            				to facade ATRMessage.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Synchronous message. Contains the results of automatically
		casted rays. One ATR message is for one casted ray. New
		automatically casted rays can be defined by ADDRAY command and
		removed by REMOVERAY command.
	
         */
 	public class AutoTraceRayLocalMessage 
	  					extends
  						AutoTraceRayLocal
	    {
 	
		    			@Override
		    			public 
		    			AutoTraceRayLocalMessage clone() {
		    				return this;
		    			}
		    			
		    				public AutoTraceRayLocalMessage getLocal() {
								return this;
					    	}
							public ISharedWorldObject getShared() {
							 	throw new UnsupportedOperationException("Could not return LOCAL as SHARED");
							}
							public IStaticWorldObject getStatic() {
							    throw new UnsupportedOperationException("Could not return LOCAL as STATIC");
							}
		    			
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
        public  Location getFrom()
 	 {
				    					return From;
				    				}
				    			
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
        public  boolean isFastTrace()
 	 {
				    					return FastTrace;
				    				}
				    			
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
        public  boolean isResult()
 	 {
				    					return Result;
				    				}
				    			
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
        public  boolean isTraceActors()
 	 {
				    					return TraceActors;
				    				}
				    			
 		/**
         * 
			Id of the actor we have hit. (Sent if FastTrace is False and
			TraceActors is True).
		 
         */
        public  UnrealId getHitId()
 	 {
				    					return HitId;
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
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=local+classtype[@name=message]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=local+classtype[@name=message]) ---        	            	
 	
		}
 	
 		/**
         *  
            				Implementation of the static part of the GameBots2004 message ATR, used
            				to facade ATRMessage.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Synchronous message. Contains the results of automatically
		casted rays. One ATR message is for one casted ray. New
		automatically casted rays can be defined by ADDRAY command and
		removed by REMOVERAY command.
	
         */
 	public class AutoTraceRayStaticMessage 
	  					extends
  						AutoTraceRayStatic
	    {
 	
		    			@Override
		    			public 
		    			AutoTraceRayStaticMessage clone() {
		    				return this;
		    			}
		    			
 		/**
         * 
			An Id for this ray (should be unique), assigned by the user
			when adding ray.
		 
         */
        public  UnrealId getId()
 	 {
				    					return Id;
				    				}
				    			
 		
 		@Override
 		public boolean isDifferentFrom(IStaticWorldObject other)
 		{
 			if (other == null) //early fail
 			{
 				return true;
 			}
 			else if (other == this) //early out
 			{
 				return false;
 			}
 			else
 			{
 				AutoTraceRayStatic obj = (AutoTraceRayStatic) other;

 				
 						if ( !(
 	 			AdvancedEquals.equalsOrNull(this.getId()
 	, obj.getId()
 	)
 	 		) )
						{
							System.out.println("!!!!!PROPERTY UPDATE ERROR!!!! on property Id on object class AutoTraceRayStatic");
							return true;
						}
 					
 			}
 			return false;
 		}
 	 
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
		              			"Id = " + String.valueOf(getId()
 	) + " | " + 
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>Id</b> = " + String.valueOf(getId()
 	) + " <br/> " + 
		              		
				"<br/>]";     
		}
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=static+classtype[@name=message]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=static+classtype[@name=message]) ---        	            	
 	
		}
 	
 		/**
         *  
            				Implementation of the shared part of the GameBots2004 message ATR, used
            				to facade ATRMessage.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Synchronous message. Contains the results of automatically
		casted rays. One ATR message is for one casted ray. New
		automatically casted rays can be defined by ADDRAY command and
		removed by REMOVERAY command.
	
         */
 	public class AutoTraceRaySharedMessage 
	  					extends
  						AutoTraceRayShared
	    {
 	
    	
    	
		public AutoTraceRaySharedMessage()
		{
			
		}		
    
		    			@Override
		    			public 
		    			AutoTraceRaySharedMessage clone() {
		    				return this;
		    			}
		    			
		
		
		
		protected HashMap<PropertyId, ISharedProperty> propertyMap = new HashMap<PropertyId, ISharedProperty>(
			0
		);
		
		@Override
		public ISharedProperty getProperty(PropertyId id) {
			return propertyMap.get(id);
		}

		@Override
		public Map<PropertyId, ISharedProperty> getProperties() {
			return propertyMap;
		}
	
		
		
 		/**
         * 
			An Id for this ray (should be unique), assigned by the user
			when adding ray.
		 
         */
        public  UnrealId getId()
 	 {
  			return Id;
  		}
  		
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
		              			"Id = " + String.valueOf(getId()
 	) + " | " + 
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>Id</b> = " + String.valueOf(getId()
 	) + " <br/> " + 
		              		
				"<br/>]";     
		}
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=shared+classtype[@name=message]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=shared+classtype[@name=message]) ---        	            	
 	
		}
 	
    	
    	
 	
		@Override
		public IWorldObjectUpdateResult<IWorldObject> update(IWorldObject object) {
			if (object == null)
			{
				return new IWorldObjectUpdateResult.WorldObjectUpdateResult(IWorldObjectUpdateResult.Result.CREATED, this);
			}
			if (!( object instanceof AutoTraceRayMessage) ) {
				throw new PogamutException("Can't update different class than AutoTraceRayMessage, got class " + object.getClass().getSimpleName() + "!", this);		
			}
			AutoTraceRayMessage toUpdate = (AutoTraceRayMessage)object;
			
			boolean updated = false;
			
			// UPDATING LOCAL PROPERTIES
			
	            if (!SafeEquals.equals(toUpdate.From, getFrom()
 	)) {
					toUpdate.From=getFrom()
 	;
					updated = true;
				}
			
	            if (!SafeEquals.equals(toUpdate.To, getTo()
 	)) {
					toUpdate.To=getTo()
 	;
					updated = true;
				}
			
				if (toUpdate.FastTrace != isFastTrace()
 	) {
				    toUpdate.FastTrace=isFastTrace()
 	;
					updated = true;
				}
			
				if (toUpdate.FloorCorrection != isFloorCorrection()
 	) {
				    toUpdate.FloorCorrection=isFloorCorrection()
 	;
					updated = true;
				}
			
				if (toUpdate.Result != isResult()
 	) {
				    toUpdate.Result=isResult()
 	;
					updated = true;
				}
			
				if (!SafeEquals.equals(toUpdate.HitNormal, getHitNormal()
 	)) {
					toUpdate.HitNormal=getHitNormal()
 	;
					updated = true;
				}
			
	            if (!SafeEquals.equals(toUpdate.HitLocation, getHitLocation()
 	)) {
					toUpdate.HitLocation=getHitLocation()
 	;
					updated = true;
				}
			
				if (toUpdate.TraceActors != isTraceActors()
 	) {
				    toUpdate.TraceActors=isTraceActors()
 	;
					updated = true;
				}
			
				if (!SafeEquals.equals(toUpdate.HitId, getHitId()
 	)) {
					toUpdate.HitId=getHitId()
 	;
					updated = true;
				}
			
         	
         	// UPDATING SHARED PROPERTIES
         	
         	
         	// UPDATE TIME
         	toUpdate.SimTime = SimTime;
			
			if (updated) {
				return new IWorldObjectUpdateResult.WorldObjectUpdateResult<IWorldObject>(IWorldObjectUpdateResult.Result.UPDATED, toUpdate);
			} else {
				return new IWorldObjectUpdateResult.WorldObjectUpdateResult<IWorldObject>(IWorldObjectUpdateResult.Result.SAME, toUpdate);
			}
		}
		
		@Override
		public ILocalWorldObjectUpdatedEvent getLocalEvent() {
			return new AutoTraceRayLocalImpl.AutoTraceRayLocalUpdate
    (this.getLocal(), SimTime);
		}

		@Override
		public ISharedWorldObjectUpdatedEvent getSharedEvent() {
			return new AutoTraceRaySharedImpl.AutoTraceRaySharedUpdate
    (this.getShared(), SimTime, this.getTeamId());
		}

		@Override
		public IStaticWorldObjectUpdatedEvent getStaticEvent() {
			return new AutoTraceRayStaticImpl.AutoTraceRayStaticUpdate
    (this.getStatic(), SimTime);
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
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=base+classtype[@name=message]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=base+classtype[@name=message]) ---        	            	
 	
		}
 	