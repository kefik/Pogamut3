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
            				Implementation of the shared part of the GameBots2004 message NAV.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Synchronous message - however only NavPoints marking item pickup locations are exported synchronously. Other NavPoints are not exported synchronously at all, even if the bot can actually see them (but note that ALL NavPoints are exported in the handshake between bot and the server). Exporting NavPoints synchronously took a lot of UT server resources with limited information gain (in Pogamut there is now available visibility matrix holding static information which points can be seen from other points). NavPoint carries information about UT navigation point - location, paths and some additional information are stored there (if it is an ambush point, or sniper point, etc.).
   
         */
 	public class NavPointSharedImpl 
  						extends
  						NavPointShared
	    {
 	
    
    	
    	public NavPointSharedImpl(NavPointSharedImpl source) {
			
				this.Id = source.
					getId()
 	;
			
				this.myLocation = source.myLocation;
			
				this.myVelocity = source.myVelocity;
			
				this.myItemSpawned = source.myItemSpawned;
			
				this.myDoorOpened = source.myDoorOpened;
			
				this.myDomPointController = source.myDomPointController;
			
		}
		
		public NavPointSharedImpl(WorldObjectId objectId, Collection<ISharedProperty> properties) {
			this.Id = (UnrealId)objectId;
			NullCheck.check(this.Id, "objectId");
		
			if (properties.size() != 5) {
				throw new PogamutException("Not enough properties passed to the constructor.", NavPointSharedImpl.class);
			}
		
			//we have to do some checking in this one to know that we get all properties required
			for ( ISharedProperty property : properties ) {
				PropertyId pId = property.getPropertyId();
				if ( !objectId.equals( property.getObjectId() )) {
					//properties for different objects
					throw new PogamutException("Trying to create a NavPointSharedImpl with different WorldObjectId properties : " + 
											    this.Id.getStringId() + " / " + property.getObjectId().getStringId() , this);
				}
				if (!NavPointShared.SharedPropertyTokens.contains(pId.getPropertyToken())) {
				// property that does not belong here
				throw new PogamutException("Trying to create a NavPointSharedImpl with invalid property (invalid property token): " + 
					this.Id.getStringId() + " / " + property.getPropertyId().getPropertyToken().getToken() , this);
				}
				propertyMap.put(property.getPropertyId(), property);
				
				
					if (pId.getPropertyToken().getToken().equals("Location"))
					{
						this.myLocation = (LocationProperty)property;
					}
				
					if (pId.getPropertyToken().getToken().equals("Velocity"))
					{
						this.myVelocity = (VelocityProperty)property;
					}
				
					if (pId.getPropertyToken().getToken().equals("ItemSpawned"))
					{
						this.myItemSpawned = (BooleanProperty)property;
					}
				
					if (pId.getPropertyToken().getToken().equals("DoorOpened"))
					{
						this.myDoorOpened = (BooleanProperty)property;
					}
				
					if (pId.getPropertyToken().getToken().equals("DomPointController"))
					{
						this.myDomPointController = (IntProperty)property;
					}
				
			}
		}
    
   				
   				@Override
   				public void setSimTime(long SimTime) {
					super.setSimTime(SimTime);
				}
   			
	    				@Override
	    				public 
	    				NavPointSharedImpl clone() {
	    					return new 
	    					NavPointSharedImpl(this);
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
			A unique Id of this navigation point assigned by the game.
		 
         */
        protected
         UnrealId Id =
       	null;
	
 		/**
         * 
			A unique Id of this navigation point assigned by the game.
		 
         */
        public  UnrealId getId()
 	 {
  			return Id;
  		}
  		
    	
	    /**
         * Location of navigation point. 
         */
        protected
         LocationProperty 
        myLocation
					= null;
					
					
 		/**
         * Location of navigation point. 
         */
        public  Location getLocation()
 	 {
			  			return myLocation.getValue();
			  		}
				
    	
	    /**
         * 
			Velocity of the navigation point (if the navigation point is
			currently moving). Not sent at the moment.
		 
         */
        protected
         VelocityProperty 
        myVelocity
					= null;
					
					
 		/**
         * 
			Velocity of the navigation point (if the navigation point is
			currently moving). Not sent at the moment.
		 
         */
        public  Velocity getVelocity()
 	 {
			  			return myVelocity.getValue();
			  		}
				
    	
	    /**
         * 
            True if the item is spawned at the point. Not sent if point is not an inventory spot. 
         
         */
        protected
         BooleanProperty 
        myItemSpawned
					= null;
					
					
 		/**
         * 
            True if the item is spawned at the point. Not sent if point is not an inventory spot. 
         
         */
        public  boolean isItemSpawned()
 	 {
			  			return myItemSpawned.getValue();
			  		}
				
    	
	    /**
         * 
            True if this NavPoint is a Door and door is opened. Not sent if point is not a door.
         
         */
        protected
         BooleanProperty 
        myDoorOpened
					= null;
					
					
 		/**
         * 
            True if this NavPoint is a Door and door is opened. Not sent if point is not a door.
         
         */
        public  boolean isDoorOpened()
 	 {
			  			return myDoorOpened.getValue();
			  		}
				
    	
	    /**
         * Exported if this NavPoint is a DominationPoint (for BotDoubleDomination game) - which team controls this point. 
         */
        protected
         IntProperty 
        myDomPointController
					= null;
					
					
 		/**
         * Exported if this NavPoint is a DominationPoint (for BotDoubleDomination game) - which team controls this point. 
         */
        public  int getDomPointController()
 	 {
			  			return myDomPointController.getValue();
			  		}
				
    	
    	
    	public static class NavPointSharedUpdate
     implements ISharedWorldObjectUpdatedEvent
		{
	
			private NavPointShared object;
			private long time;
			private ITeamId teamId;
			
			public NavPointSharedUpdate
    (NavPointShared data, long time, ITeamId teamId)
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
		              		
		              			"Velocity = " + String.valueOf(getVelocity()
 	) + " | " + 
		              		
		              			"ItemSpawned = " + String.valueOf(isItemSpawned()
 	) + " | " + 
		              		
		              			"DoorOpened = " + String.valueOf(isDoorOpened()
 	) + " | " + 
		              		
		              			"DomPointController = " + String.valueOf(getDomPointController()
 	) + " | " + 
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>Id</b> = " + String.valueOf(getId()
 	) + " <br/> " + 
		              		
		              			"<b>Location</b> = " + String.valueOf(getLocation()
 	) + " <br/> " + 
		              		
		              			"<b>Velocity</b> = " + String.valueOf(getVelocity()
 	) + " <br/> " + 
		              		
		              			"<b>ItemSpawned</b> = " + String.valueOf(isItemSpawned()
 	) + " <br/> " + 
		              		
		              			"<b>DoorOpened</b> = " + String.valueOf(isDoorOpened()
 	) + " <br/> " + 
		              		
		              			"<b>DomPointController</b> = " + String.valueOf(getDomPointController()
 	) + " <br/> " + 
		              		
				"<br/>]";     
		}
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=shared+classtype[@name=impl]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=shared+classtype[@name=impl]) ---        	            	
 	
		}
 	