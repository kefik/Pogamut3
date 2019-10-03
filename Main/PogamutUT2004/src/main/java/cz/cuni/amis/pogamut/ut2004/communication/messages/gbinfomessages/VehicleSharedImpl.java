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
            				Implementation of the shared part of the GameBots2004 message VEH.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Synchronous message. Holds vehicles we see. Id for vehicles doesn't work, so 
		this message is without Id. We can join vehicles by ENTER command if we are 
		close enough - ussually 100 ut units or less. Note: Vehicle support is 
		in aplha testing right now.
	
         */
 	public class VehicleSharedImpl 
  						extends
  						VehicleShared
	    {
 	
    
    	
    	public VehicleSharedImpl(VehicleSharedImpl source) {
			
				this.Id = source.
					getId()
 	;
			
				this.myRotation = source.myRotation;
			
				this.myLocation = source.myLocation;
			
				this.myVelocity = source.myVelocity;
			
				this.myTeam = source.myTeam;
			
				this.myHealth = source.myHealth;
			
				this.myArmor = source.myArmor;
			
				this.myDriver = source.myDriver;
			
				this.myTeamLocked = source.myTeamLocked;
			
		}
		
		public VehicleSharedImpl(WorldObjectId objectId, Collection<ISharedProperty> properties) {
			this.Id = (UnrealId)objectId;
			NullCheck.check(this.Id, "objectId");
		
			if (properties.size() != 8) {
				throw new PogamutException("Not enough properties passed to the constructor.", VehicleSharedImpl.class);
			}
		
			//we have to do some checking in this one to know that we get all properties required
			for ( ISharedProperty property : properties ) {
				PropertyId pId = property.getPropertyId();
				if ( !objectId.equals( property.getObjectId() )) {
					//properties for different objects
					throw new PogamutException("Trying to create a VehicleSharedImpl with different WorldObjectId properties : " + 
											    this.Id.getStringId() + " / " + property.getObjectId().getStringId() , this);
				}
				if (!VehicleShared.SharedPropertyTokens.contains(pId.getPropertyToken())) {
				// property that does not belong here
				throw new PogamutException("Trying to create a VehicleSharedImpl with invalid property (invalid property token): " + 
					this.Id.getStringId() + " / " + property.getPropertyId().getPropertyToken().getToken() , this);
				}
				propertyMap.put(property.getPropertyId(), property);
				
				
					if (pId.getPropertyToken().getToken().equals("Rotation"))
					{
						this.myRotation = (RotationProperty)property;
					}
				
					if (pId.getPropertyToken().getToken().equals("Location"))
					{
						this.myLocation = (LocationProperty)property;
					}
				
					if (pId.getPropertyToken().getToken().equals("Velocity"))
					{
						this.myVelocity = (VelocityProperty)property;
					}
				
					if (pId.getPropertyToken().getToken().equals("Team"))
					{
						this.myTeam = (IntegerProperty)property;
					}
				
					if (pId.getPropertyToken().getToken().equals("Health"))
					{
						this.myHealth = (IntegerProperty)property;
					}
				
					if (pId.getPropertyToken().getToken().equals("Armor"))
					{
						this.myArmor = (IntegerProperty)property;
					}
				
					if (pId.getPropertyToken().getToken().equals("Driver"))
					{
						this.myDriver = (UnrealIdProperty)property;
					}
				
					if (pId.getPropertyToken().getToken().equals("TeamLocked"))
					{
						this.myTeamLocked = (BooleanProperty)property;
					}
				
			}
		}
    
   				
   				@Override
   				public void setSimTime(long SimTime) {
					super.setSimTime(SimTime);
				}
   			
	    				@Override
	    				public 
	    				VehicleSharedImpl clone() {
	    					return new 
	    					VehicleSharedImpl(this);
	    				}
	    				
	    				
		
		
		
		protected HashMap<PropertyId, ISharedProperty> propertyMap = new HashMap<PropertyId, ISharedProperty>(
			8
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
         * Unique Id of the vehicle or vehicle part. 
         */
        protected
         UnrealId Id =
       	null;
	
 		/**
         * Unique Id of the vehicle or vehicle part. 
         */
        public  UnrealId getId()
 	 {
  			return Id;
  		}
  		
    	
	    /**
         * 
			Which direction the vehicle is facing in absolute terms.
		 
         */
        protected
         RotationProperty 
        myRotation
					= null;
					
					
 		/**
         * 
			Which direction the vehicle is facing in absolute terms.
		 
         */
        public  Rotation getRotation()
 	 {
			  			return myRotation.getValue();
			  		}
				
    	
	    /**
         * 
			An absolute location of the vehicle within the map.
		 
         */
        protected
         LocationProperty 
        myLocation
					= null;
					
					
 		/**
         * 
			An absolute location of the vehicle within the map.
		 
         */
        public  Location getLocation()
 	 {
			  			return myLocation.getValue();
			  		}
				
    	
	    /**
         * 
			Absolute velocity of the vehicle as a vector of movement per one
			game second.
		 
         */
        protected
         VelocityProperty 
        myVelocity
					= null;
					
					
 		/**
         * 
			Absolute velocity of the vehicle as a vector of movement per one
			game second.
		 
         */
        public  Velocity getVelocity()
 	 {
			  			return myVelocity.getValue();
			  		}
				
    	
	    /**
         * 
			What team the vehicle is on. 255 is no team. 0-3 are red,
			blue, green, gold in that order.
		 
         */
        protected
         IntegerProperty 
        myTeam
					= null;
					
					
 		/**
         * 
			What team the vehicle is on. 255 is no team. 0-3 are red,
			blue, green, gold in that order.
		 
         */
        public  Integer getTeam()
 	 {
			  			return myTeam.getValue();
			  		}
				
    	
	    /**
         * 
			How much health the vehicle has left. Ranges from 0 to x, depending on the vehicle type.
		 
         */
        protected
         IntegerProperty 
        myHealth
					= null;
					
					
 		/**
         * 
			How much health the vehicle has left. Ranges from 0 to x, depending on the vehicle type.
		 
         */
        public  Integer getHealth()
 	 {
			  			return myHealth.getValue();
			  		}
				
    	
	    /**
         * 
			How much the vehicle has left. Note: This may be 0 all the time. Maybe the vehicles are not supporting armor.
		 
         */
        protected
         IntegerProperty 
        myArmor
					= null;
					
					
 		/**
         * 
			How much the vehicle has left. Note: This may be 0 all the time. Maybe the vehicles are not supporting armor.
		 
         */
        public  Integer getArmor()
 	 {
			  			return myArmor.getValue();
			  		}
				
    	
	    /**
         * Unique Id of the driver - if any. 
         */
        protected
         UnrealIdProperty 
        myDriver
					= null;
					
					
 		/**
         * Unique Id of the driver - if any. 
         */
        public  UnrealId getDriver()
 	 {
			  			return myDriver.getValue();
			  		}
				
    	
	    /**
         * 
            If the vehicle is locked just for its current team.
         
         */
        protected
         BooleanProperty 
        myTeamLocked
					= null;
					
					
 		/**
         * 
            If the vehicle is locked just for its current team.
         
         */
        public  boolean isTeamLocked()
 	 {
			  			return myTeamLocked.getValue();
			  		}
				
    	
    	
    	public static class VehicleSharedUpdate
     implements ISharedWorldObjectUpdatedEvent
		{
	
			private VehicleShared object;
			private long time;
			private ITeamId teamId;
			
			public VehicleSharedUpdate
    (VehicleShared data, long time, ITeamId teamId)
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
		              		
		              			"Rotation = " + String.valueOf(getRotation()
 	) + " | " + 
		              		
		              			"Location = " + String.valueOf(getLocation()
 	) + " | " + 
		              		
		              			"Velocity = " + String.valueOf(getVelocity()
 	) + " | " + 
		              		
		              			"Team = " + String.valueOf(getTeam()
 	) + " | " + 
		              		
		              			"Health = " + String.valueOf(getHealth()
 	) + " | " + 
		              		
		              			"Armor = " + String.valueOf(getArmor()
 	) + " | " + 
		              		
		              			"Driver = " + String.valueOf(getDriver()
 	) + " | " + 
		              		
		              			"TeamLocked = " + String.valueOf(isTeamLocked()
 	) + " | " + 
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>Id</b> = " + String.valueOf(getId()
 	) + " <br/> " + 
		              		
		              			"<b>Rotation</b> = " + String.valueOf(getRotation()
 	) + " <br/> " + 
		              		
		              			"<b>Location</b> = " + String.valueOf(getLocation()
 	) + " <br/> " + 
		              		
		              			"<b>Velocity</b> = " + String.valueOf(getVelocity()
 	) + " <br/> " + 
		              		
		              			"<b>Team</b> = " + String.valueOf(getTeam()
 	) + " <br/> " + 
		              		
		              			"<b>Health</b> = " + String.valueOf(getHealth()
 	) + " <br/> " + 
		              		
		              			"<b>Armor</b> = " + String.valueOf(getArmor()
 	) + " <br/> " + 
		              		
		              			"<b>Driver</b> = " + String.valueOf(getDriver()
 	) + " <br/> " + 
		              		
		              			"<b>TeamLocked</b> = " + String.valueOf(isTeamLocked()
 	) + " <br/> " + 
		              		
				"<br/>]";     
		}
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=shared+classtype[@name=impl]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=shared+classtype[@name=impl]) ---        	            	
 	
		}
 	