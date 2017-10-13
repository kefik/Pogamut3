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
            				Implementation of the shared part of the GameBots2004 message MOV.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Synchronous message. Movers can be doors, elevators, or any
		other chunk of architecture that can move. They generally need
		to be either run into, or activated by shooting or pressing a
		button. We are working on ways to provide bots with more of the
		information they need to deal with movers appropriately.
	
         */
 	public class MoverSharedImpl 
  						extends
  						MoverShared
	    {
 	
    
    	
    	public MoverSharedImpl(MoverSharedImpl source) {
			
				this.Id = source.
					getId()
 	;
			
				this.myLocation = source.myLocation;
			
				this.myVelocity = source.myVelocity;
			
				this.myState = source.myState;
			
		}
		
		public MoverSharedImpl(WorldObjectId objectId, Collection<ISharedProperty> properties) {
			this.Id = (UnrealId)objectId;
			NullCheck.check(this.Id, "objectId");
		
			if (properties.size() != 3) {
				throw new PogamutException("Not enough properties passed to the constructor.", MoverSharedImpl.class);
			}
		
			//we have to do some checking in this one to know that we get all properties required
			for ( ISharedProperty property : properties ) {
				PropertyId pId = property.getPropertyId();
				if ( !objectId.equals( property.getObjectId() )) {
					//properties for different objects
					throw new PogamutException("Trying to create a MoverSharedImpl with different WorldObjectId properties : " + 
											    this.Id.getStringId() + " / " + property.getObjectId().getStringId() , this);
				}
				if (!MoverShared.SharedPropertyTokens.contains(pId.getPropertyToken())) {
				// property that does not belong here
				throw new PogamutException("Trying to create a MoverSharedImpl with invalid property (invalid property token): " + 
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
	    				MoverSharedImpl clone() {
	    					return new 
	    					MoverSharedImpl(this);
	    				}
	    				
	    				
		
		
		
		protected HashMap<PropertyId, ISharedProperty> propertyMap = new HashMap<PropertyId, ISharedProperty>(
			3
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
			A unique Id of this mover assigned by the game.
		 
         */
        protected
         UnrealId Id =
       	null;
	
 		/**
         * 
			A unique Id of this mover assigned by the game.
		 
         */
        public  UnrealId getId()
 	 {
  			return Id;
  		}
  		
    	
	    /**
         * Location of the mover. 
         */
        protected
         LocationProperty 
        myLocation
					= null;
					
					
 		/**
         * Location of the mover. 
         */
        public  Location getLocation()
 	 {
			  			return myLocation.getValue();
			  		}
				
    	
	    /**
         * Velocity vector. 
         */
        protected
         VelocityProperty 
        myVelocity
					= null;
					
					
 		/**
         * Velocity vector. 
         */
        public  Velocity getVelocity()
 	 {
			  			return myVelocity.getValue();
			  		}
				
    	
	    /**
         * Name of the state Mover is currently in. Can be used to determine the type of the mover.  
         */
        protected
         StringProperty 
        myState
					= null;
					
					
 		/**
         * Name of the state Mover is currently in. Can be used to determine the type of the mover.  
         */
        public  String getState()
 	 {
			  			return myState.getValue();
			  		}
				
    	
    	
    	public static class MoverSharedUpdate
     implements ISharedWorldObjectUpdatedEvent
		{
	
			private MoverShared object;
			private long time;
			private ITeamId teamId;
			
			public MoverSharedUpdate
    (MoverShared data, long time, ITeamId teamId)
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
		              		
		              			"<b>Velocity</b> = " + String.valueOf(getVelocity()
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
 	