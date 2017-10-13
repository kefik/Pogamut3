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
             				Implementation of the GameBots2004 message BOM contains also its Local/Shared/Static subpart class definitions..  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Synchronous message. BombInfo contains all info about the bomb
		in the BotBombingRun game mode. Is not sent in other game types.
	
         */
 	public class BombInfoMessage   
  				extends 
  				BombInfo
  						implements IWorldObjectUpdatedEvent, ICompositeWorldObjectUpdatedEvent
  						
	    {
 	
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public BombInfoMessage()
		{
		}
	
    	
    	
    	
    	/**
		 * Creates new instance of the message BombInfo.
		 * 
		Synchronous message. BombInfo contains all info about the bomb
		in the BotBombingRun game mode. Is not sent in other game types.
	
		 * Corresponding GameBots message
		 *   
		 *   is
		 *   BOM.
		 * 
 	  	 * 
		 *   
		 *     @param Id 
			An unique Id for this bomb, assigned by the game.
		
		 *   
		 * 
		 *   
		 *     @param Velocity 
   		Current velocity of the bomb. TODO not sure if this actually does smthing
   	
		 *   
		 * 
		 *   
		 *     @param Location 
			An absolute location of the bomb (Sent if we can actually
			see the flag).
		
		 *   
		 * 
		 *   
		 *     @param Holder 
			Id of player/bot holding the bomb. (Sent if we can actually
			see the bomb and the bomb is being carried, or if the bomb
			is being carried by us).
		
		 *   
		 * 
		 *   
		 *     @param HolderTeam 
			The team of the current holder (if any).
		
		 *   
		 * 
		 *   
		 *     @param Visible True if the bot can see the bomb.
		 *   
		 * 
		 *   
		 *     @param State 
			Represents the state the bomb is in. Can be "Held",
			"Dropped" or "Home".
		
		 *   
		 * 
		 */
		public BombInfoMessage(
			UnrealId Id,  Velocity Velocity,  Location Location,  UnrealId Holder,  Integer HolderTeam,  boolean Visible,  String State
		) {
			
					this.Id = Id;
				
					this.Velocity = Velocity;
				
					this.Location = Location;
				
					this.Holder = Holder;
				
					this.HolderTeam = HolderTeam;
				
					this.Visible = Visible;
				
					this.State = State;
				
		}
    
		/**
		 * Cloning constructor from the full message.
		 *
		 * @param original
		 */
		public BombInfoMessage(BombInfoMessage original) {		
			
					this.Id = original.getId()
 	;
				
					this.Velocity = original.getVelocity()
 	;
				
					this.Location = original.getLocation()
 	;
				
					this.Holder = original.getHolder()
 	;
				
					this.HolderTeam = original.getHolderTeam()
 	;
				
					this.Visible = original.isVisible()
 	;
				
					this.State = original.getState()
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
			An unique Id for this bomb, assigned by the game.
		 
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
			An unique Id for this bomb, assigned by the game.
		 
         */
        public  UnrealId getId()
 	 {
		    					return Id;
		    				}
		    			
    	
	    /**
         * 
   		Current velocity of the bomb. TODO not sure if this actually does smthing
   	 
         */
        protected
         Velocity Velocity =
       	null;
	
    						
    						/**
		 					 * Whether property 'Velocity' was received from GB2004.
		 					 */
							protected boolean Velocity_Set = false;
							
    						@Override
		    				
 		/**
         * 
   		Current velocity of the bomb. TODO not sure if this actually does smthing
   	 
         */
        public  Velocity getVelocity()
 	 {
		    					return Velocity;
		    				}
		    			
    	
	    /**
         * 
			An absolute location of the bomb (Sent if we can actually
			see the flag).
		 
         */
        protected
         Location Location =
       	null;
	
    						
    						/**
		 					 * Whether property 'Location' was received from GB2004.
		 					 */
							protected boolean Location_Set = false;
							
    						@Override
		    				
 		/**
         * 
			An absolute location of the bomb (Sent if we can actually
			see the flag).
		 
         */
        public  Location getLocation()
 	 {
		    					return Location;
		    				}
		    			
    	
	    /**
         * 
			Id of player/bot holding the bomb. (Sent if we can actually
			see the bomb and the bomb is being carried, or if the bomb
			is being carried by us).
		 
         */
        protected
         UnrealId Holder =
       	null;
	
    						
    						/**
		 					 * Whether property 'Holder' was received from GB2004.
		 					 */
							protected boolean Holder_Set = false;
							
    						@Override
		    				
 		/**
         * 
			Id of player/bot holding the bomb. (Sent if we can actually
			see the bomb and the bomb is being carried, or if the bomb
			is being carried by us).
		 
         */
        public  UnrealId getHolder()
 	 {
		    					return Holder;
		    				}
		    			
    	
	    /**
         * 
			The team of the current holder (if any).
		 
         */
        protected
         Integer HolderTeam =
       	255;
	
    						
    						/**
		 					 * Whether property 'HolderTeam' was received from GB2004.
		 					 */
							protected boolean HolderTeam_Set = false;
							
    						@Override
		    				
 		/**
         * 
			The team of the current holder (if any).
		 
         */
        public  Integer getHolderTeam()
 	 {
		    					return HolderTeam;
		    				}
		    			
    	
	    /**
         * True if the bot can see the bomb. 
         */
        protected
         boolean Visible =
       	false;
	
    						
    						/**
		 					 * Whether property 'Visible' was received from GB2004.
		 					 */
							protected boolean Visible_Set = false;
							
    						@Override
		    				
 		/**
         * True if the bot can see the bomb. 
         */
        public  boolean isVisible()
 	 {
		    					return Visible;
		    				}
		    			
    	
	    /**
         * 
			Represents the state the bomb is in. Can be "Held",
			"Dropped" or "Home".
		 
         */
        protected
         String State =
       	null;
	
    						
    						/**
		 					 * Whether property 'State' was received from GB2004.
		 					 */
							protected boolean State_Set = false;
							
    						@Override
		    				
 		/**
         * 
			Represents the state the bomb is in. Can be "Held",
			"Dropped" or "Home".
		 
         */
        public  String getState()
 	 {
		    					return State;
		    				}
		    			
		    			
		    			private BombInfoLocal localPart = null;
		    			
		    			@Override
						public BombInfoLocal 
						getLocal() {
							if (localPart != null) return localPart;
							return localPart = new 
								BombInfoLocalMessage();
						}
					
						private BombInfoShared sharedPart = null;
					
						@Override
						public BombInfoShared 
						getShared() {
							if (sharedPart != null) return sharedPart;							
							return sharedPart = new 
								BombInfoSharedMessage();
						}
					
						private BombInfoStatic staticPart = null; 
					
						@Override
						public BombInfoStatic 
						getStatic() {
							if (staticPart != null) return staticPart;
							return staticPart = new 
								BombInfoStaticMessage();
						}
    				
 		/**
         *  
            				Implementation of the local part of the GameBots2004 message BOM, used
            				to facade BOMMessage.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Synchronous message. BombInfo contains all info about the bomb
		in the BotBombingRun game mode. Is not sent in other game types.
	
         */
 	public class BombInfoLocalMessage 
	  					extends
  						BombInfoLocal
	    {
 	
		    			@Override
		    			public 
		    			BombInfoLocalMessage clone() {
		    				return this;
		    			}
		    			
		    				public BombInfoLocalMessage getLocal() {
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
			An unique Id for this bomb, assigned by the game.
		 
         */
        public  UnrealId getId()
 	 {
				    					return Id;
				    				}
				    			
 		/**
         * True if the bot can see the bomb. 
         */
        public  boolean isVisible()
 	 {
				    					return Visible;
				    				}
				    			
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
		              			"Id = " + String.valueOf(getId()
 	) + " | " + 
		              		
		              			"Visible = " + String.valueOf(isVisible()
 	) + " | " + 
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>Id</b> = " + String.valueOf(getId()
 	) + " <br/> " + 
		              		
		              			"<b>Visible</b> = " + String.valueOf(isVisible()
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
            				Implementation of the static part of the GameBots2004 message BOM, used
            				to facade BOMMessage.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Synchronous message. BombInfo contains all info about the bomb
		in the BotBombingRun game mode. Is not sent in other game types.
	
         */
 	public class BombInfoStaticMessage 
	  					extends
  						BombInfoStatic
	    {
 	
		    			@Override
		    			public 
		    			BombInfoStaticMessage clone() {
		    				return this;
		    			}
		    			
 		/**
         * 
			An unique Id for this bomb, assigned by the game.
		 
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
 				BombInfoStatic obj = (BombInfoStatic) other;

 				
 						if ( !(
 	 			AdvancedEquals.equalsOrNull(this.getId()
 	, obj.getId()
 	)
 	 		) )
						{
							System.out.println("!!!!!PROPERTY UPDATE ERROR!!!! on property Id on object class BombInfoStatic");
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
            				Implementation of the shared part of the GameBots2004 message BOM, used
            				to facade BOMMessage.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Synchronous message. BombInfo contains all info about the bomb
		in the BotBombingRun game mode. Is not sent in other game types.
	
         */
 	public class BombInfoSharedMessage 
	  					extends
  						BombInfoShared
	    {
 	
    	
    	
		public BombInfoSharedMessage()
		{
			
				propertyMap.put(myVelocity.getPropertyId(), myVelocity);
			
				propertyMap.put(myLocation.getPropertyId(), myLocation);
			
				propertyMap.put(myHolder.getPropertyId(), myHolder);
			
				propertyMap.put(myHolderTeam.getPropertyId(), myHolderTeam);
			
				propertyMap.put(myState.getPropertyId(), myState);
			
		}		
    
		    			@Override
		    			public 
		    			BombInfoSharedMessage clone() {
		    				return this;
		    			}
		    			
		
		
		
		protected HashMap<PropertyId, ISharedProperty> propertyMap = new HashMap<PropertyId, ISharedProperty>(
			5
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
			An unique Id for this bomb, assigned by the game.
		 
         */
        public  UnrealId getId()
 	 {
  			return Id;
  		}
  		
    	
	    /**
         * 
   		Current velocity of the bomb. TODO not sure if this actually does smthing
   	 
         */
        protected
         VelocityProperty 
        myVelocity
					= new
					VelocityProperty
					(
						getId(), 
						"Velocity", 
						Velocity, 
						BombInfo.class
					);
					
 		/**
         * 
   		Current velocity of the bomb. TODO not sure if this actually does smthing
   	 
         */
        public  Velocity getVelocity()
 	 {
			  			return myVelocity.getValue();
			  		}
				
    	
	    /**
         * 
			An absolute location of the bomb (Sent if we can actually
			see the flag).
		 
         */
        protected
         LocationProperty 
        myLocation
					= new
					LocationProperty
					(
						getId(), 
						"Location", 
						Location, 
						BombInfo.class
					);
					
 		/**
         * 
			An absolute location of the bomb (Sent if we can actually
			see the flag).
		 
         */
        public  Location getLocation()
 	 {
			  			return myLocation.getValue();
			  		}
				
    	
	    /**
         * 
			Id of player/bot holding the bomb. (Sent if we can actually
			see the bomb and the bomb is being carried, or if the bomb
			is being carried by us).
		 
         */
        protected
         UnrealIdProperty 
        myHolder
					= new
					UnrealIdProperty
					(
						getId(), 
						"Holder", 
						Holder, 
						BombInfo.class
					);
					
 		/**
         * 
			Id of player/bot holding the bomb. (Sent if we can actually
			see the bomb and the bomb is being carried, or if the bomb
			is being carried by us).
		 
         */
        public  UnrealId getHolder()
 	 {
			  			return myHolder.getValue();
			  		}
				
    	
	    /**
         * 
			The team of the current holder (if any).
		 
         */
        protected
         IntegerProperty 
        myHolderTeam
					= new
					IntegerProperty
					(
						getId(), 
						"HolderTeam", 
						HolderTeam, 
						BombInfo.class
					);
					
 		/**
         * 
			The team of the current holder (if any).
		 
         */
        public  Integer getHolderTeam()
 	 {
			  			return myHolderTeam.getValue();
			  		}
				
    	
	    /**
         * 
			Represents the state the bomb is in. Can be "Held",
			"Dropped" or "Home".
		 
         */
        protected
         StringProperty 
        myState
					= new
					StringProperty
					(
						getId(), 
						"State", 
						State, 
						BombInfo.class
					);
					
 		/**
         * 
			Represents the state the bomb is in. Can be "Held",
			"Dropped" or "Home".
		 
         */
        public  String getState()
 	 {
			  			return myState.getValue();
			  		}
				
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
		              			"Id = " + String.valueOf(getId()
 	) + " | " + 
		              		
		              			"Velocity = " + String.valueOf(getVelocity()
 	) + " | " + 
		              		
		              			"Location = " + String.valueOf(getLocation()
 	) + " | " + 
		              		
		              			"Holder = " + String.valueOf(getHolder()
 	) + " | " + 
		              		
		              			"HolderTeam = " + String.valueOf(getHolderTeam()
 	) + " | " + 
		              		
		              			"State = " + String.valueOf(getState()
 	) + " | " + 
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>Id</b> = " + String.valueOf(getId()
 	) + " <br/> " + 
		              		
		              			"<b>Velocity</b> = " + String.valueOf(getVelocity()
 	) + " <br/> " + 
		              		
		              			"<b>Location</b> = " + String.valueOf(getLocation()
 	) + " <br/> " + 
		              		
		              			"<b>Holder</b> = " + String.valueOf(getHolder()
 	) + " <br/> " + 
		              		
		              			"<b>HolderTeam</b> = " + String.valueOf(getHolderTeam()
 	) + " <br/> " + 
		              		
		              			"<b>State</b> = " + String.valueOf(getState()
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
			if (!( object instanceof BombInfoMessage) ) {
				throw new PogamutException("Can't update different class than BombInfoMessage, got class " + object.getClass().getSimpleName() + "!", this);		
			}
			BombInfoMessage toUpdate = (BombInfoMessage)object;
			
			boolean updated = false;
			
			// UPDATING LOCAL PROPERTIES
			
				if (toUpdate.Visible != isVisible()
 	) {
				    toUpdate.Visible=isVisible()
 	;
					updated = true;
				}
			
         	
         	// UPDATING SHARED PROPERTIES
         	
				if (!SafeEquals.equals(toUpdate.Velocity, getVelocity()
 	)) {
					toUpdate.Velocity=getVelocity()
 	;
					updated = true;
				}
			
	            if (!SafeEquals.equals(toUpdate.Location, getLocation()
 	)) {
					toUpdate.Location=getLocation()
 	;
					updated = true;
				}
			
				if (!SafeEquals.equals(toUpdate.Holder, getHolder()
 	)) {
					toUpdate.Holder=getHolder()
 	;
					updated = true;
				}
			
				if (!SafeEquals.equals(toUpdate.HolderTeam, getHolderTeam()
 	)) {
					toUpdate.HolderTeam=getHolderTeam()
 	;
					updated = true;
				}
			
				if (!SafeEquals.equals(toUpdate.State, getState()
 	)) {
					toUpdate.State=getState()
 	;
					updated = true;
				}
			
         	
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
			return new BombInfoLocalImpl.BombInfoLocalUpdate
    (this.getLocal(), SimTime);
		}

		@Override
		public ISharedWorldObjectUpdatedEvent getSharedEvent() {
			return new BombInfoSharedImpl.BombInfoSharedUpdate
    (this.getShared(), SimTime, this.getTeamId());
		}

		@Override
		public IStaticWorldObjectUpdatedEvent getStaticEvent() {
			return new BombInfoStaticImpl.BombInfoStaticUpdate
    (this.getStatic(), SimTime);
		}
 	
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
		              			"Id = " + String.valueOf(getId()
 	) + " | " + 
		              		
		              			"Velocity = " + String.valueOf(getVelocity()
 	) + " | " + 
		              		
		              			"Location = " + String.valueOf(getLocation()
 	) + " | " + 
		              		
		              			"Holder = " + String.valueOf(getHolder()
 	) + " | " + 
		              		
		              			"HolderTeam = " + String.valueOf(getHolderTeam()
 	) + " | " + 
		              		
		              			"Visible = " + String.valueOf(isVisible()
 	) + " | " + 
		              		
		              			"State = " + String.valueOf(getState()
 	) + " | " + 
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>Id</b> = " + String.valueOf(getId()
 	) + " <br/> " + 
		              		
		              			"<b>Velocity</b> = " + String.valueOf(getVelocity()
 	) + " <br/> " + 
		              		
		              			"<b>Location</b> = " + String.valueOf(getLocation()
 	) + " <br/> " + 
		              		
		              			"<b>Holder</b> = " + String.valueOf(getHolder()
 	) + " <br/> " + 
		              		
		              			"<b>HolderTeam</b> = " + String.valueOf(getHolderTeam()
 	) + " <br/> " + 
		              		
		              			"<b>Visible</b> = " + String.valueOf(isVisible()
 	) + " <br/> " + 
		              		
		              			"<b>State</b> = " + String.valueOf(getState()
 	) + " <br/> " + 
		              		
				"<br/>]";     
		}
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=base+classtype[@name=message]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=base+classtype[@name=message]) ---        	            	
 	
		}
 	