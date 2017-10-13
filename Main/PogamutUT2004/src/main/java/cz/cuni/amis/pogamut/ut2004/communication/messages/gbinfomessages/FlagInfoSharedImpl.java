package cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages;
 		
 		// --- IMPORTS FROM /messages/settings/javasettings/javaimport BEGIN
			import java.util.*;import javax.vecmath.*;import cz.cuni.amis.pogamut.base.communication.messages.*;import cz.cuni.amis.pogamut.base.communication.worldview.*;import cz.cuni.amis.pogamut.base.communication.worldview.event.*;import cz.cuni.amis.pogamut.base.communication.worldview.object.*;import cz.cuni.amis.pogamut.multi.communication.worldview.object.*;import cz.cuni.amis.pogamut.base.communication.translator.event.*;import cz.cuni.amis.pogamut.multi.communication.translator.event.*;import cz.cuni.amis.pogamut.base3d.worldview.object.*;import cz.cuni.amis.pogamut.base3d.worldview.object.event.*;import cz.cuni.amis.pogamut.ut2004.communication.messages.*;import cz.cuni.amis.pogamut.ut2004.communication.worldview.objects.*;import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.objects.*;import cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor.*;import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType.Category;import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;import cz.cuni.amis.utils.exception.*;import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdateResult.Result;import cz.cuni.amis.utils.SafeEquals;import cz.cuni.amis.pogamut.base.agent.*;import cz.cuni.amis.pogamut.multi.agent.*;import cz.cuni.amis.pogamut.multi.communication.worldview.property.*;import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.property.*;import cz.cuni.amis.utils.token.*;import cz.cuni.amis.utils.*;
		// --- IMPORTS FROM /messages/settings/javasettings/javaimport END
		
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name='all'] BEGIN
				
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name='all'] END
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name=shared]+classtype[@name=impl] BEGIN
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name=shared]+classtype[@name=impl] END
    
 		/**
         *  
            				Implementation of the shared part of the GameBots2004 message FLG.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Synchronous message. FlagInfo contains all info about the flag
		in the CTF game mode. Is not sent in other game types.
	
         */
 	public class FlagInfoSharedImpl 
  						extends
  						FlagInfoShared
	    {
 	
    
    	
    	public FlagInfoSharedImpl(FlagInfoSharedImpl source) {
			
				this.Id = source.
					getId()
 	;
			
				this.myLocation = source.myLocation;
			
				this.myHolder = source.myHolder;
			
				this.myTeam = source.myTeam;
			
				this.myState = source.myState;
			
		}
		
		public FlagInfoSharedImpl(WorldObjectId objectId, Collection<ISharedProperty> properties) {
			this.Id = (UnrealId)objectId;
			NullCheck.check(this.Id, "objectId");
		
			if (properties.size() != 4) {
				throw new PogamutException("Not enough properties passed to the constructor.", FlagInfoSharedImpl.class);
			}
		
			//we have to do some checking in this one to know that we get all properties required
			for ( ISharedProperty property : properties ) {
				PropertyId pId = property.getPropertyId();
				if ( !objectId.equals( property.getObjectId() )) {
					//properties for different objects
					throw new PogamutException("Trying to create a FlagInfoSharedImpl with different WorldObjectId properties : " + 
											    this.Id.getStringId() + " / " + property.getObjectId().getStringId() , this);
				}
				if (!FlagInfoShared.SharedPropertyTokens.contains(pId.getPropertyToken())) {
				// property that does not belong here
				throw new PogamutException("Trying to create a FlagInfoSharedImpl with invalid property (invalid property token): " + 
					this.Id.getStringId() + " / " + property.getPropertyId().getPropertyToken().getToken() , this);
				}
				propertyMap.put(property.getPropertyId(), property);
				
				
					if (pId.getPropertyToken().getToken().equals("Location"))
					{
						this.myLocation = (LocationProperty)property;
					}
				
					if (pId.getPropertyToken().getToken().equals("Holder"))
					{
						this.myHolder = (UnrealIdProperty)property;
					}
				
					if (pId.getPropertyToken().getToken().equals("Team"))
					{
						this.myTeam = (IntegerProperty)property;
					}
				
					if (pId.getPropertyToken().getToken().equals("State"))
					{
						this.myState = (StringProperty)property;
					}
				
			}
		}
    
   				
   				@Override
   				public void setSimTime(long SimTime) {
					super.setSimTime(SimTime);
				}
   			
	    				@Override
	    				public 
	    				FlagInfoSharedImpl clone() {
	    					return new 
	    					FlagInfoSharedImpl(this);
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
        protected
         UnrealId Id =
       	null;
	
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
					= null;
					
					
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
					= null;
					
					
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
					= null;
					
					
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
					= null;
					
					
 		/**
         * 
			Represents the state the flag is in. Can be "Held",
			"Dropped" or "Home" (note that the first letter does not have to be in upper case!).
		 
         */
        public  String getState()
 	 {
			  			return myState.getValue();
			  		}
				
    	
    	
    	public static class FlagInfoSharedUpdate
     implements ISharedWorldObjectUpdatedEvent
		{
	
			private FlagInfoShared object;
			private long time;
			private ITeamId teamId;
			
			public FlagInfoSharedUpdate
    (FlagInfoShared data, long time, ITeamId teamId)
			{
				this.object = data;
				this.time = time;
				this.teamId = teamId;
			}
			
			/**
			 * Simulation time in MILLI SECONDS !!!
			 */
			@Override
			public long getSimTime() {
				return this.time;
			}
	
			@Override
			public WorldObjectId getId() {
				return object.getId();
			}
	
			@Override
			public ITeamId getTeamId() {
				return teamId;
			}
			
			@Override
			public Class getCompositeObjectClass()
			{
				return object.getCompositeClass();
			}
	
			@Override
			public Collection<ISharedPropertyUpdatedEvent> getPropertyEvents() {
				LinkedList<ISharedPropertyUpdatedEvent> events = new LinkedList<ISharedPropertyUpdatedEvent>();
				
				for ( ISharedProperty property : object.getProperties().values() )
				{
					if ( property != null)
					{
						events.push( property.createUpdateEvent(time, teamId) );
					}
				}
				return events;
			}
			
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
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=shared+classtype[@name=impl]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=shared+classtype[@name=impl]) ---        	            	
 	
		}
 	