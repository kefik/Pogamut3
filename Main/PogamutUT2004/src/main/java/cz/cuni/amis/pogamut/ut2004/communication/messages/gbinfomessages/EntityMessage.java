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
             				Implementation of the GameBots2004 message ENT contains also its Local/Shared/Static subpart class definitions..  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Generic physical entity that may be present in the world. Works as a hook for custom objects, such as new items from EmohawkRPG extension.
	
         */
 	public class EntityMessage   
  				extends 
  				Entity
  						implements IWorldObjectUpdatedEvent, ICompositeWorldObjectUpdatedEvent
  						
	    {
 	
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public EntityMessage()
		{
		}
	
    	
    	
    	
    	/**
		 * Creates new instance of the message Entity.
		 * 
		Generic physical entity that may be present in the world. Works as a hook for custom objects, such as new items from EmohawkRPG extension.
	
		 * Corresponding GameBots message
		 *   
		 *   is
		 *   ENT.
		 * 
 	  	 * 
		 *   
		 *     @param Id 
			Unique Id of the item. This Id represents some entity in the world.
		
		 *   
		 * 
		 *   
		 *     @param Visible 
			If the item is in the field of view of the bot.
		
		 *   
		 * 
		 *   
		 *     @param Location Location of the item.
		 *   
		 * 
		 *   
		 *     @param Rotation Rotation of the item.
		 *   
		 * 
		 *   
		 *     @param Velocity Velocity of the item.
		 *   
		 * 
		 *   
		 *     @param Type Type (category) of the entity.
		 *   
		 * 
		 *   
		 *     @param EntityClass Type (category) of the entity.
		 *   
		 * 
		 */
		public EntityMessage(
			UnrealId Id,  boolean Visible,  Location Location,  Rotation Rotation,  Velocity Velocity,  String Type,  String EntityClass
		) {
			
					this.Id = Id;
				
					this.Visible = Visible;
				
					this.Location = Location;
				
					this.Rotation = Rotation;
				
					this.Velocity = Velocity;
				
					this.Type = Type;
				
					this.EntityClass = EntityClass;
				
		}
    
		/**
		 * Cloning constructor from the full message.
		 *
		 * @param original
		 */
		public EntityMessage(EntityMessage original) {		
			
					this.Id = original.getId()
 	;
				
					this.Visible = original.isVisible()
 	;
				
					this.Location = original.getLocation()
 	;
				
					this.Rotation = original.getRotation()
 	;
				
					this.Velocity = original.getVelocity()
 	;
				
					this.Type = original.getType()
 	;
				
					this.EntityClass = original.getEntityClass()
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
			Unique Id of the item. This Id represents some entity in the world.
		 
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
			Unique Id of the item. This Id represents some entity in the world.
		 
         */
        public  UnrealId getId()
 	 {
		    					return Id;
		    				}
		    			
    	
	    /**
         * 
			If the item is in the field of view of the bot.
		 
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
         * 
			If the item is in the field of view of the bot.
		 
         */
        public  boolean isVisible()
 	 {
		    					return Visible;
		    				}
		    			
    	
	    /**
         * Location of the item. 
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
         * Location of the item. 
         */
        public  Location getLocation()
 	 {
		    					return Location;
		    				}
		    			
    	
	    /**
         * Rotation of the item. 
         */
        protected
         Rotation Rotation =
       	null;
	
    						
    						/**
		 					 * Whether property 'Rotation' was received from GB2004.
		 					 */
							protected boolean Rotation_Set = false;
							
    						@Override
		    				
 		/**
         * Rotation of the item. 
         */
        public  Rotation getRotation()
 	 {
		    					return Rotation;
		    				}
		    			
    	
	    /**
         * Velocity of the item. 
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
         * Velocity of the item. 
         */
        public  Velocity getVelocity()
 	 {
		    					return Velocity;
		    				}
		    			
    	
	    /**
         * Type (category) of the entity. 
         */
        protected
         String Type =
       	null;
	
    						
    						/**
		 					 * Whether property 'Type' was received from GB2004.
		 					 */
							protected boolean Type_Set = false;
							
    						@Override
		    				
 		/**
         * Type (category) of the entity. 
         */
        public  String getType()
 	 {
		    					return Type;
		    				}
		    			
    	
	    /**
         * Type (category) of the entity. 
         */
        protected
         String EntityClass =
       	null;
	
    						
    						/**
		 					 * Whether property 'EntityClass' was received from GB2004.
		 					 */
							protected boolean EntityClass_Set = false;
							
    						@Override
		    				
 		/**
         * Type (category) of the entity. 
         */
        public  String getEntityClass()
 	 {
		    					return EntityClass;
		    				}
		    			
		    			
		    			private EntityLocal localPart = null;
		    			
		    			@Override
						public EntityLocal 
						getLocal() {
							if (localPart != null) return localPart;
							return localPart = new 
								EntityLocalMessage();
						}
					
						private EntityShared sharedPart = null;
					
						@Override
						public EntityShared 
						getShared() {
							if (sharedPart != null) return sharedPart;							
							return sharedPart = new 
								EntitySharedMessage();
						}
					
						private EntityStatic staticPart = null; 
					
						@Override
						public EntityStatic 
						getStatic() {
							if (staticPart != null) return staticPart;
							return staticPart = new 
								EntityStaticMessage();
						}
    				
 		/**
         *  
            				Implementation of the local part of the GameBots2004 message ENT, used
            				to facade ENTMessage.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Generic physical entity that may be present in the world. Works as a hook for custom objects, such as new items from EmohawkRPG extension.
	
         */
 	public class EntityLocalMessage 
	  					extends
  						EntityLocal
	    {
 	
		    			@Override
		    			public 
		    			EntityLocalMessage clone() {
		    				return this;
		    			}
		    			
		    				public EntityLocalMessage getLocal() {
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
			Unique Id of the item. This Id represents some entity in the world.
		 
         */
        public  UnrealId getId()
 	 {
				    					return Id;
				    				}
				    			
 		/**
         * 
			If the item is in the field of view of the bot.
		 
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
            				Implementation of the static part of the GameBots2004 message ENT, used
            				to facade ENTMessage.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Generic physical entity that may be present in the world. Works as a hook for custom objects, such as new items from EmohawkRPG extension.
	
         */
 	public class EntityStaticMessage 
	  					extends
  						EntityStatic
	    {
 	
		    			@Override
		    			public 
		    			EntityStaticMessage clone() {
		    				return this;
		    			}
		    			
 		/**
         * 
			Unique Id of the item. This Id represents some entity in the world.
		 
         */
        public  UnrealId getId()
 	 {
				    					return Id;
				    				}
				    			
 		/**
         * Type (category) of the entity. 
         */
        public  String getType()
 	 {
				    					return Type;
				    				}
				    			
 		/**
         * Type (category) of the entity. 
         */
        public  String getEntityClass()
 	 {
				    					return EntityClass;
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
 				EntityStatic obj = (EntityStatic) other;

 				
 						if ( !(
 	 			AdvancedEquals.equalsOrNull(this.getId()
 	, obj.getId()
 	)
 	 		) )
						{
							System.out.println("!!!!!PROPERTY UPDATE ERROR!!!! on property Id on object class EntityStatic");
							return true;
						}
 					
 						if ( !(
 	 			AdvancedEquals.equalsOrNull(this.getType()
 	, obj.getType()
 	)
 	 		) )
						{
							System.out.println("!!!!!PROPERTY UPDATE ERROR!!!! on property Type on object class EntityStatic");
							return true;
						}
 					
 						if ( !(
 	 			AdvancedEquals.equalsOrNull(this.getEntityClass()
 	, obj.getEntityClass()
 	)
 	 		) )
						{
							System.out.println("!!!!!PROPERTY UPDATE ERROR!!!! on property EntityClass on object class EntityStatic");
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
		              		
		              			"Type = " + String.valueOf(getType()
 	) + " | " + 
		              		
		              			"EntityClass = " + String.valueOf(getEntityClass()
 	) + " | " + 
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>Id</b> = " + String.valueOf(getId()
 	) + " <br/> " + 
		              		
		              			"<b>Type</b> = " + String.valueOf(getType()
 	) + " <br/> " + 
		              		
		              			"<b>EntityClass</b> = " + String.valueOf(getEntityClass()
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
            				Implementation of the shared part of the GameBots2004 message ENT, used
            				to facade ENTMessage.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Generic physical entity that may be present in the world. Works as a hook for custom objects, such as new items from EmohawkRPG extension.
	
         */
 	public class EntitySharedMessage 
	  					extends
  						EntityShared
	    {
 	
    	
    	
		public EntitySharedMessage()
		{
			
				propertyMap.put(myLocation.getPropertyId(), myLocation);
			
				propertyMap.put(myRotation.getPropertyId(), myRotation);
			
				propertyMap.put(myVelocity.getPropertyId(), myVelocity);
			
		}		
    
		    			@Override
		    			public 
		    			EntitySharedMessage clone() {
		    				return this;
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
			Unique Id of the item. This Id represents some entity in the world.
		 
         */
        public  UnrealId getId()
 	 {
  			return Id;
  		}
  		
    	
	    /**
         * Location of the item. 
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
						Entity.class
					);
					
 		/**
         * Location of the item. 
         */
        public  Location getLocation()
 	 {
			  			return myLocation.getValue();
			  		}
				
    	
	    /**
         * Rotation of the item. 
         */
        protected
         RotationProperty 
        myRotation
					= new
					RotationProperty
					(
						getId(), 
						"Rotation", 
						Rotation, 
						Entity.class
					);
					
 		/**
         * Rotation of the item. 
         */
        public  Rotation getRotation()
 	 {
			  			return myRotation.getValue();
			  		}
				
    	
	    /**
         * Velocity of the item. 
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
						Entity.class
					);
					
 		/**
         * Velocity of the item. 
         */
        public  Velocity getVelocity()
 	 {
			  			return myVelocity.getValue();
			  		}
				
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
		              			"Id = " + String.valueOf(getId()
 	) + " | " + 
		              		
		              			"Location = " + String.valueOf(getLocation()
 	) + " | " + 
		              		
		              			"Rotation = " + String.valueOf(getRotation()
 	) + " | " + 
		              		
		              			"Velocity = " + String.valueOf(getVelocity()
 	) + " | " + 
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>Id</b> = " + String.valueOf(getId()
 	) + " <br/> " + 
		              		
		              			"<b>Location</b> = " + String.valueOf(getLocation()
 	) + " <br/> " + 
		              		
		              			"<b>Rotation</b> = " + String.valueOf(getRotation()
 	) + " <br/> " + 
		              		
		              			"<b>Velocity</b> = " + String.valueOf(getVelocity()
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
			if (!( object instanceof EntityMessage) ) {
				throw new PogamutException("Can't update different class than EntityMessage, got class " + object.getClass().getSimpleName() + "!", this);		
			}
			EntityMessage toUpdate = (EntityMessage)object;
			
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
     	
     		if (getRotation()
 	 != null) {
     	
				if (!SafeEquals.equals(toUpdate.Rotation, getRotation()
 	)) {
					toUpdate.Rotation=getRotation()
 	;
					updated = true;
				}
			
     		}
     	
     		if (getVelocity()
 	 != null) {
     	
				if (!SafeEquals.equals(toUpdate.Velocity, getVelocity()
 	)) {
					toUpdate.Velocity=getVelocity()
 	;
					updated = true;
				}
			
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
			return new EntityLocalImpl.EntityLocalUpdate
    (this.getLocal(), SimTime);
		}

		@Override
		public ISharedWorldObjectUpdatedEvent getSharedEvent() {
			return new EntitySharedImpl.EntitySharedUpdate
    (this.getShared(), SimTime, this.getTeamId());
		}

		@Override
		public IStaticWorldObjectUpdatedEvent getStaticEvent() {
			return new EntityStaticImpl.EntityStaticUpdate
    (this.getStatic(), SimTime);
		}
 	
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
		              			"Id = " + String.valueOf(getId()
 	) + " | " + 
		              		
		              			"Visible = " + String.valueOf(isVisible()
 	) + " | " + 
		              		
		              			"Location = " + String.valueOf(getLocation()
 	) + " | " + 
		              		
		              			"Rotation = " + String.valueOf(getRotation()
 	) + " | " + 
		              		
		              			"Velocity = " + String.valueOf(getVelocity()
 	) + " | " + 
		              		
		              			"Type = " + String.valueOf(getType()
 	) + " | " + 
		              		
		              			"EntityClass = " + String.valueOf(getEntityClass()
 	) + " | " + 
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>Id</b> = " + String.valueOf(getId()
 	) + " <br/> " + 
		              		
		              			"<b>Visible</b> = " + String.valueOf(isVisible()
 	) + " <br/> " + 
		              		
		              			"<b>Location</b> = " + String.valueOf(getLocation()
 	) + " <br/> " + 
		              		
		              			"<b>Rotation</b> = " + String.valueOf(getRotation()
 	) + " <br/> " + 
		              		
		              			"<b>Velocity</b> = " + String.valueOf(getVelocity()
 	) + " <br/> " + 
		              		
		              			"<b>Type</b> = " + String.valueOf(getType()
 	) + " <br/> " + 
		              		
		              			"<b>EntityClass</b> = " + String.valueOf(getEntityClass()
 	) + " <br/> " + 
		              		
				"<br/>]";     
		}
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=base+classtype[@name=message]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=base+classtype[@name=message]) ---        	            	
 	
		}
 	