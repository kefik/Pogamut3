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
             				Implementation of the GameBots2004 message FLG contains also its Local/Shared/Static subpart class definitions..  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Synchronous message. FlagInfo contains all info about the flag
		in the CTF game mode. Is not sent in other game types.
	
         */
 	public class FlagInfoMessage   
  				extends 
  				FlagInfo
  						implements IWorldObjectUpdatedEvent, ICompositeWorldObjectUpdatedEvent
  						
	    {
 	
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public FlagInfoMessage()
		{
		}
	
    	
    	
    	
    	/**
		 * Creates new instance of the message FlagInfo.
		 * 
		Synchronous message. FlagInfo contains all info about the flag
		in the CTF game mode. Is not sent in other game types.
	
		 * Corresponding GameBots message
		 *   
		 *   is
		 *   FLG.
		 * 
 	  	 * 
		 *   
		 *     @param Id 
			An unique Id for this flag, assigned by the game.
		
		 *   
		 * 
		 *   
		 *     @param Location 
			An absolute location of the flag (Sent if we can actually
			see the flag).
		
		 *   
		 * 
		 *   
		 *     @param Holder 
			Id of player/bot holding the flag. (Sent if we can actually
			see the flag and the flag is being carried, or if the flag
			is being carried by us).
		
		 *   
		 * 
		 *   
		 *     @param Team The owner team of this flag.
		 *   
		 * 
		 *   
		 *     @param Visible True if the bot can see the flag.
		 *   
		 * 
		 *   
		 *     @param State 
			Represents the state the flag is in. Can be "Held",
			"Dropped" or "Home" (note that the first letter does not have to be in upper case!).
		
		 *   
		 * 
		 */
		public FlagInfoMessage(
			UnrealId Id,  Location Location,  UnrealId Holder,  Integer Team,  boolean Visible,  String State
		) {
			
					this.Id = Id;
				
					this.Location = Location;
				
					this.Holder = Holder;
				
					this.Team = Team;
				
					this.Visible = Visible;
				
					this.State = State;
				
		}
    
		/**
		 * Cloning constructor from the full message.
		 *
		 * @param original
		 */
		public FlagInfoMessage(FlagInfoMessage original) {		
			
					this.Id = original.getId()
 	;
				
					this.Location = original.getLocation()
 	;
				
					this.Holder = original.getHolder()
 	;
				
					this.Team = original.getTeam()
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
			An unique Id for this flag, assigned by the game.
		 
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
			An unique Id for this flag, assigned by the game.
		 
         */
        public  UnrealId getId()
 	 {
		    					return Id;
		    				}
		    			
    	
	    /**
         * 
			An absolute location of the flag (Sent if we can actually
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
			An absolute location of the flag (Sent if we can actually
			see the flag).
		 
         */
        public  Location getLocation()
 	 {
		    					return Location;
		    				}
		    			
    	
	    /**
         * 
			Id of player/bot holding the flag. (Sent if we can actually
			see the flag and the flag is being carried, or if the flag
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
			Id of player/bot holding the flag. (Sent if we can actually
			see the flag and the flag is being carried, or if the flag
			is being carried by us).
		 
         */
        public  UnrealId getHolder()
 	 {
		    					return Holder;
		    				}
		    			
    	
	    /**
         * The owner team of this flag. 
         */
        protected
         Integer Team =
       	null;
	
    						
    						/**
		 					 * Whether property 'Team' was received from GB2004.
		 					 */
							protected boolean Team_Set = false;
							
    						@Override
		    				
 		/**
         * The owner team of this flag. 
         */
        public  Integer getTeam()
 	 {
		    					return Team;
		    				}
		    			
    	
	    /**
         * True if the bot can see the flag. 
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
         * True if the bot can see the flag. 
         */
        public  boolean isVisible()
 	 {
		    					return Visible;
		    				}
		    			
    	
	    /**
         * 
			Represents the state the flag is in. Can be "Held",
			"Dropped" or "Home" (note that the first letter does not have to be in upper case!).
		 
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
			Represents the state the flag is in. Can be "Held",
			"Dropped" or "Home" (note that the first letter does not have to be in upper case!).
		 
         */
        public  String getState()
 	 {
		    					return State;
		    				}
		    			
		    			
		    			private FlagInfoLocal localPart = null;
		    			
		    			@Override
						public FlagInfoLocal 
						getLocal() {
							if (localPart != null) return localPart;
							return localPart = new 
								FlagInfoLocalMessage();
						}
					
						private FlagInfoShared sharedPart = null;
					
						@Override
						public FlagInfoShared 
						getShared() {
							if (sharedPart != null) return sharedPart;							
							return sharedPart = new 
								FlagInfoSharedMessage();
						}
					
						private FlagInfoStatic staticPart = null; 
					
						@Override
						public FlagInfoStatic 
						getStatic() {
							if (staticPart != null) return staticPart;
							return staticPart = new 
								FlagInfoStaticMessage();
						}
    				
 		/**
         *  
            				Implementation of the local part of the GameBots2004 message FLG, used
            				to facade FLGMessage.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Synchronous message. FlagInfo contains all info about the flag
		in the CTF game mode. Is not sent in other game types.
	
         */
 	public class FlagInfoLocalMessage 
	  					extends
  						FlagInfoLocal
	    {
 	
		    			@Override
		    			public 
		    			FlagInfoLocalMessage clone() {
		    				return this;
		    			}
		    			
		    				public FlagInfoLocalMessage getLocal() {
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
			An unique Id for this flag, assigned by the game.
		 
         */
        public  UnrealId getId()
 	 {
				    					return Id;
				    				}
				    			
 		/**
         * True if the bot can see the flag. 
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
            				Implementation of the static part of the GameBots2004 message FLG, used
            				to facade FLGMessage.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Synchronous message. FlagInfo contains all info about the flag
		in the CTF game mode. Is not sent in other game types.
	
         */
 	public class FlagInfoStaticMessage 
	  					extends
  						FlagInfoStatic
	    {
 	
		    			@Override
		    			public 
		    			FlagInfoStaticMessage clone() {
		    				return this;
		    			}
		    			
 		/**
         * 
			An unique Id for this flag, assigned by the game.
		 
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
 				FlagInfoStatic obj = (FlagInfoStatic) other;

 				
 						if ( !(
 	 			AdvancedEquals.equalsOrNull(this.getId()
 	, obj.getId()
 	)
 	 		) )
						{
							System.out.println("!!!!!PROPERTY UPDATE ERROR!!!! on property Id on object class FlagInfoStatic");
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
            				Implementation of the shared part of the GameBots2004 message FLG, used
            				to facade FLGMessage.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Synchronous message. FlagInfo contains all info about the flag
		in the CTF game mode. Is not sent in other game types.
	
         */
 	public class FlagInfoSharedMessage 
	  					extends
  						FlagInfoShared
	    {
 	
    	
    	
		public FlagInfoSharedMessage()
		{
			
				propertyMap.put(myLocation.getPropertyId(), myLocation);
			
				propertyMap.put(myHolder.getPropertyId(), myHolder);
			
				propertyMap.put(myTeam.getPropertyId(), myTeam);
			
				propertyMap.put(myState.getPropertyId(), myState);
			
		}		
    
		    			@Override
		    			public 
		    			FlagInfoSharedMessage clone() {
		    				return this;
		    			}
		    			
		
		
		
		protected HashMap<PropertyId, ISharedProperty> propertyMap = new HashMap<PropertyId, ISharedProperty>(
			4
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
			An unique Id for this flag, assigned by the game.
		 
         */
        public  UnrealId getId()
 	 {
  			return Id;
  		}
  		
    	
	    /**
         * 
			An absolute location of the flag (Sent if we can actually
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
						FlagInfo.class
					);
					
 		/**
         * 
			An absolute location of the flag (Sent if we can actually
			see the flag).
		 
         */
        public  Location getLocation()
 	 {
			  			return myLocation.getValue();
			  		}
				
    	
	    /**
         * 
			Id of player/bot holding the flag. (Sent if we can actually
			see the flag and the flag is being carried, or if the flag
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
						FlagInfo.class
					);
					
 		/**
         * 
			Id of player/bot holding the flag. (Sent if we can actually
			see the flag and the flag is being carried, or if the flag
			is being carried by us).
		 
         */
        public  UnrealId getHolder()
 	 {
			  			return myHolder.getValue();
			  		}
				
    	
	    /**
         * The owner team of this flag. 
         */
        protected
         IntegerProperty 
        myTeam
					= new
					IntegerProperty
					(
						getId(), 
						"Team", 
						Team, 
						FlagInfo.class
					);
					
 		/**
         * The owner team of this flag. 
         */
        public  Integer getTeam()
 	 {
			  			return myTeam.getValue();
			  		}
				
    	
	    /**
         * 
			Represents the state the flag is in. Can be "Held",
			"Dropped" or "Home" (note that the first letter does not have to be in upper case!).
		 
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
						FlagInfo.class
					);
					
 		/**
         * 
			Represents the state the flag is in. Can be "Held",
			"Dropped" or "Home" (note that the first letter does not have to be in upper case!).
		 
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
		              		
		              			"Location = " + String.valueOf(getLocation()
 	) + " | " + 
		              		
		              			"Holder = " + String.valueOf(getHolder()
 	) + " | " + 
		              		
		              			"Team = " + String.valueOf(getTeam()
 	) + " | " + 
		              		
		              			"State = " + String.valueOf(getState()
 	) + " | " + 
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>Id</b> = " + String.valueOf(getId()
 	) + " <br/> " + 
		              		
		              			"<b>Location</b> = " + String.valueOf(getLocation()
 	) + " <br/> " + 
		              		
		              			"<b>Holder</b> = " + String.valueOf(getHolder()
 	) + " <br/> " + 
		              		
		              			"<b>Team</b> = " + String.valueOf(getTeam()
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
			if (!( object instanceof FlagInfoMessage) ) {
				throw new PogamutException("Can't update different class than FlagInfoMessage, got class " + object.getClass().getSimpleName() + "!", this);		
			}
			FlagInfoMessage toUpdate = (FlagInfoMessage)object;
			
			boolean updated = false;
			
			// UPDATING LOCAL PROPERTIES
			
				if (toUpdate.Visible != isVisible()
 	) {
				    toUpdate.Visible=isVisible()
 	;
					updated = true;
				}
			
         	
         	// UPDATING SHARED PROPERTIES
         	
     		if (getLocation()
 	 != null) {
     	
	            if (!SafeEquals.equals(toUpdate.Location, getLocation()
 	)) {
					toUpdate.Location=getLocation()
 	;
					updated = true;
				}
			
     		}
     	
				if (!SafeEquals.equals(toUpdate.Holder, getHolder()
 	)) {
					toUpdate.Holder=getHolder()
 	;
					updated = true;
				}
			
				if (!SafeEquals.equals(toUpdate.Team, getTeam()
 	)) {
					toUpdate.Team=getTeam()
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
			return new FlagInfoLocalImpl.FlagInfoLocalUpdate
    (this.getLocal(), SimTime);
		}

		@Override
		public ISharedWorldObjectUpdatedEvent getSharedEvent() {
			return new FlagInfoSharedImpl.FlagInfoSharedUpdate
    (this.getShared(), SimTime, this.getTeamId());
		}

		@Override
		public IStaticWorldObjectUpdatedEvent getStaticEvent() {
			return new FlagInfoStaticImpl.FlagInfoStaticUpdate
    (this.getStatic(), SimTime);
		}
 	
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
		              			"Id = " + String.valueOf(getId()
 	) + " | " + 
		              		
		              			"Location = " + String.valueOf(getLocation()
 	) + " | " + 
		              		
		              			"Holder = " + String.valueOf(getHolder()
 	) + " | " + 
		              		
		              			"Team = " + String.valueOf(getTeam()
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
		              		
		              			"<b>Location</b> = " + String.valueOf(getLocation()
 	) + " <br/> " + 
		              		
		              			"<b>Holder</b> = " + String.valueOf(getHolder()
 	) + " <br/> " + 
		              		
		              			"<b>Team</b> = " + String.valueOf(getTeam()
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
 	