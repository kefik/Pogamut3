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
            				Implementation of the shared part of the GameBots2004 message PRJ.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Both asynchronous and synchronous message. Incoming projectile that we can see.
	
         */
 	public class IncomingProjectileSharedImpl 
  						extends
  						IncomingProjectileShared
	    {
 	
    
    	
    	public IncomingProjectileSharedImpl(IncomingProjectileSharedImpl source) {
			
				this.Id = source.
					getId()
 	;
			
				this.myDirection = source.myDirection;
			
				this.myLocation = source.myLocation;
			
				this.myVelocity = source.myVelocity;
			
				this.mySpeed = source.mySpeed;
			
				this.myOrigin = source.myOrigin;
			
				this.myDamageRadius = source.myDamageRadius;
			
				this.myType = source.myType;
			
		}
		
		public IncomingProjectileSharedImpl(WorldObjectId objectId, Collection<ISharedProperty> properties) {
			this.Id = (UnrealId)objectId;
			NullCheck.check(this.Id, "objectId");
		
			if (properties.size() != 7) {
				throw new PogamutException("Not enough properties passed to the constructor.", IncomingProjectileSharedImpl.class);
			}
		
			//we have to do some checking in this one to know that we get all properties required
			for ( ISharedProperty property : properties ) {
				PropertyId pId = property.getPropertyId();
				if ( !objectId.equals( property.getObjectId() )) {
					//properties for different objects
					throw new PogamutException("Trying to create a IncomingProjectileSharedImpl with different WorldObjectId properties : " + 
											    this.Id.getStringId() + " / " + property.getObjectId().getStringId() , this);
				}
				if (!IncomingProjectileShared.SharedPropertyTokens.contains(pId.getPropertyToken())) {
				// property that does not belong here
				throw new PogamutException("Trying to create a IncomingProjectileSharedImpl with invalid property (invalid property token): " + 
					this.Id.getStringId() + " / " + property.getPropertyId().getPropertyToken().getToken() , this);
				}
				propertyMap.put(property.getPropertyId(), property);
				
				
					if (pId.getPropertyToken().getToken().equals("Direction"))
					{
						this.myDirection = (Vector3dProperty)property;
					}
				
					if (pId.getPropertyToken().getToken().equals("Location"))
					{
						this.myLocation = (LocationProperty)property;
					}
				
					if (pId.getPropertyToken().getToken().equals("Velocity"))
					{
						this.myVelocity = (VelocityProperty)property;
					}
				
					if (pId.getPropertyToken().getToken().equals("Speed"))
					{
						this.mySpeed = (DoubleProperty)property;
					}
				
					if (pId.getPropertyToken().getToken().equals("Origin"))
					{
						this.myOrigin = (LocationProperty)property;
					}
				
					if (pId.getPropertyToken().getToken().equals("DamageRadius"))
					{
						this.myDamageRadius = (DoubleProperty)property;
					}
				
					if (pId.getPropertyToken().getToken().equals("Type"))
					{
						this.myType = (StringProperty)property;
					}
				
			}
		}
    
   				
   				@Override
   				public void setSimTime(long SimTime) {
					super.setSimTime(SimTime);
				}
   			
	    				@Override
	    				public 
	    				IncomingProjectileSharedImpl clone() {
	    					return new 
	    					IncomingProjectileSharedImpl(this);
	    				}
	    				
	    				
		
		
		
		protected HashMap<PropertyId, ISharedProperty> propertyMap = new HashMap<PropertyId, ISharedProperty>(
			7
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
         * Unique Id of the projectile. 
         */
        protected
         UnrealId Id =
       	null;
	
 		/**
         * Unique Id of the projectile. 
         */
        public  UnrealId getId()
 	 {
  			return Id;
  		}
  		
    	
	    /**
         * 
			Which direction projectile is heading to -> orientation
			vector.
		 
         */
        protected
         Vector3dProperty 
        myDirection
					= null;
					
					
 		/**
         * 
			Which direction projectile is heading to -> orientation
			vector.
		 
         */
        public  Vector3d getDirection()
 	 {
			  			return myDirection.getValue();
			  		}
				
    	
	    /**
         * 
			Current location of the projectile.
		 
         */
        protected
         LocationProperty 
        myLocation
					= null;
					
					
 		/**
         * 
			Current location of the projectile.
		 
         */
        public  Location getLocation()
 	 {
			  			return myLocation.getValue();
			  		}
				
    	
	    /**
         * 
			Current velocity vector of the projectile.
		 
         */
        protected
         VelocityProperty 
        myVelocity
					= null;
					
					
 		/**
         * 
			Current velocity vector of the projectile.
		 
         */
        public  Velocity getVelocity()
 	 {
			  			return myVelocity.getValue();
			  		}
				
    	
	    /**
         * 
			Current speed of the projectile.
		 
         */
        protected
         DoubleProperty 
        mySpeed
					= null;
					
					
 		/**
         * 
			Current speed of the projectile.
		 
         */
        public  double getSpeed()
 	 {
			  			return mySpeed.getValue();
			  		}
				
    	
	    /**
         * 
			Possition of the origin, when combined with direction can
			define the line of fire.
		 
         */
        protected
         LocationProperty 
        myOrigin
					= null;
					
					
 		/**
         * 
			Possition of the origin, when combined with direction can
			define the line of fire.
		 
         */
        public  Location getOrigin()
 	 {
			  			return myOrigin.getValue();
			  		}
				
    	
	    /**
         * 
			If the projectile has splash damage, how big it is – in ut
			units.
		 
         */
        protected
         DoubleProperty 
        myDamageRadius
					= null;
					
					
 		/**
         * 
			If the projectile has splash damage, how big it is – in ut
			units.
		 
         */
        public  double getDamageRadius()
 	 {
			  			return myDamageRadius.getValue();
			  		}
				
    	
	    /**
         * 
			The class of the projectile (so you know what is flying
			against you).
		 
         */
        protected
         StringProperty 
        myType
					= null;
					
					
 		/**
         * 
			The class of the projectile (so you know what is flying
			against you).
		 
         */
        public  String getType()
 	 {
			  			return myType.getValue();
			  		}
				
    	
    	
    	public static class IncomingProjectileSharedUpdate
     implements ISharedWorldObjectUpdatedEvent
		{
	
			private IncomingProjectileShared object;
			private long time;
			private ITeamId teamId;
			
			public IncomingProjectileSharedUpdate
    (IncomingProjectileShared data, long time, ITeamId teamId)
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
		              		
		              			"Direction = " + String.valueOf(getDirection()
 	) + " | " + 
		              		
		              			"Location = " + String.valueOf(getLocation()
 	) + " | " + 
		              		
		              			"Velocity = " + String.valueOf(getVelocity()
 	) + " | " + 
		              		
		              			"Speed = " + String.valueOf(getSpeed()
 	) + " | " + 
		              		
		              			"Origin = " + String.valueOf(getOrigin()
 	) + " | " + 
		              		
		              			"DamageRadius = " + String.valueOf(getDamageRadius()
 	) + " | " + 
		              		
		              			"Type = " + String.valueOf(getType()
 	) + " | " + 
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>Id</b> = " + String.valueOf(getId()
 	) + " <br/> " + 
		              		
		              			"<b>Direction</b> = " + String.valueOf(getDirection()
 	) + " <br/> " + 
		              		
		              			"<b>Location</b> = " + String.valueOf(getLocation()
 	) + " <br/> " + 
		              		
		              			"<b>Velocity</b> = " + String.valueOf(getVelocity()
 	) + " <br/> " + 
		              		
		              			"<b>Speed</b> = " + String.valueOf(getSpeed()
 	) + " <br/> " + 
		              		
		              			"<b>Origin</b> = " + String.valueOf(getOrigin()
 	) + " <br/> " + 
		              		
		              			"<b>DamageRadius</b> = " + String.valueOf(getDamageRadius()
 	) + " <br/> " + 
		              		
		              			"<b>Type</b> = " + String.valueOf(getType()
 	) + " <br/> " + 
		              		
				"<br/>]";     
		}
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=shared+classtype[@name=impl]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=shared+classtype[@name=impl]) ---        	            	
 	
		}
 	