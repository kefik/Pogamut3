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
             				Implementation of the GameBots2004 message PRJ contains also its Local/Shared/Static subpart class definitions..  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Both asynchronous and synchronous message. Incoming projectile that we can see.
	
         */
 	public class IncomingProjectileMessage   
  				extends 
  				IncomingProjectile
  						implements IWorldObjectUpdatedEvent, ICompositeWorldObjectUpdatedEvent
  						
	    {
 	
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public IncomingProjectileMessage()
		{
		}
	
    	
    	
    	
    	/**
		 * Creates new instance of the message IncomingProjectile.
		 * 
		Both asynchronous and synchronous message. Incoming projectile that we can see.
	
		 * Corresponding GameBots message
		 *   
		 *   is
		 *   PRJ.
		 * 
 	  	 * 
		 *   
		 *     @param Id Unique Id of the projectile.
		 *   
		 * 
		 *   
		 *     @param ImpactTime Estimated time till impact.
		 *   
		 * 
		 *   
		 *     @param Direction 
			Which direction projectile is heading to -> orientation
			vector.
		
		 *   
		 * 
		 *   
		 *     @param Location 
			Current location of the projectile.
		
		 *   
		 * 
		 *   
		 *     @param Velocity 
			Current velocity vector of the projectile.
		
		 *   
		 * 
		 *   
		 *     @param Speed 
			Current speed of the projectile.
		
		 *   
		 * 
		 *   
		 *     @param Origin 
			Possition of the origin, when combined with direction can
			define the line of fire.
		
		 *   
		 * 
		 *   
		 *     @param DamageRadius 
			If the projectile has splash damage, how big it is – in ut
			units.
		
		 *   
		 * 
		 *   
		 *     @param Type 
			The class of the projectile (so you know what is flying
			against you).
		
		 *   
		 * 
		 *   
		 *     @param Visible 
			The class of the projectile (so you know what is flying
			against you).
		
		 *   
		 * 
		 */
		public IncomingProjectileMessage(
			UnrealId Id,  double ImpactTime,  Vector3d Direction,  Location Location,  Velocity Velocity,  double Speed,  Location Origin,  double DamageRadius,  String Type,  boolean Visible
		) {
			
					this.Id = Id;
				
					this.ImpactTime = ImpactTime;
				
					this.Direction = Direction;
				
					this.Location = Location;
				
					this.Velocity = Velocity;
				
					this.Speed = Speed;
				
					this.Origin = Origin;
				
					this.DamageRadius = DamageRadius;
				
					this.Type = Type;
				
					this.Visible = Visible;
				
		}
    
		/**
		 * Cloning constructor from the full message.
		 *
		 * @param original
		 */
		public IncomingProjectileMessage(IncomingProjectileMessage original) {		
			
					this.Id = original.getId()
 	;
				
					this.ImpactTime = original.getImpactTime()
 	;
				
					this.Direction = original.getDirection()
 	;
				
					this.Location = original.getLocation()
 	;
				
					this.Velocity = original.getVelocity()
 	;
				
					this.Speed = original.getSpeed()
 	;
				
					this.Origin = original.getOrigin()
 	;
				
					this.DamageRadius = original.getDamageRadius()
 	;
				
					this.Type = original.getType()
 	;
				
					this.Visible = original.isVisible()
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
         * Unique Id of the projectile. 
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
         * Unique Id of the projectile. 
         */
        public  UnrealId getId()
 	 {
		    					return Id;
		    				}
		    			
    	
	    /**
         * Estimated time till impact. 
         */
        protected
         double ImpactTime =
       	0;
	
    						
    						/**
		 					 * Whether property 'ImpactTime' was received from GB2004.
		 					 */
							protected boolean ImpactTime_Set = false;
							
    						@Override
		    				
 		/**
         * Estimated time till impact. 
         */
        public  double getImpactTime()
 	 {
		    					return ImpactTime;
		    				}
		    			
    	
	    /**
         * 
			Which direction projectile is heading to -> orientation
			vector.
		 
         */
        protected
         Vector3d Direction =
       	null;
	
    						
    						/**
		 					 * Whether property 'Direction' was received from GB2004.
		 					 */
							protected boolean Direction_Set = false;
							
    						@Override
		    				
 		/**
         * 
			Which direction projectile is heading to -> orientation
			vector.
		 
         */
        public  Vector3d getDirection()
 	 {
		    					return Direction;
		    				}
		    			
    	
	    /**
         * 
			Current location of the projectile.
		 
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
			Current location of the projectile.
		 
         */
        public  Location getLocation()
 	 {
		    					return Location;
		    				}
		    			
    	
	    /**
         * 
			Current velocity vector of the projectile.
		 
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
			Current velocity vector of the projectile.
		 
         */
        public  Velocity getVelocity()
 	 {
		    					return Velocity;
		    				}
		    			
    	
	    /**
         * 
			Current speed of the projectile.
		 
         */
        protected
         double Speed =
       	0;
	
    						
    						/**
		 					 * Whether property 'Speed' was received from GB2004.
		 					 */
							protected boolean Speed_Set = false;
							
    						@Override
		    				
 		/**
         * 
			Current speed of the projectile.
		 
         */
        public  double getSpeed()
 	 {
		    					return Speed;
		    				}
		    			
    	
	    /**
         * 
			Possition of the origin, when combined with direction can
			define the line of fire.
		 
         */
        protected
         Location Origin =
       	null;
	
    						
    						/**
		 					 * Whether property 'Origin' was received from GB2004.
		 					 */
							protected boolean Origin_Set = false;
							
    						@Override
		    				
 		/**
         * 
			Possition of the origin, when combined with direction can
			define the line of fire.
		 
         */
        public  Location getOrigin()
 	 {
		    					return Origin;
		    				}
		    			
    	
	    /**
         * 
			If the projectile has splash damage, how big it is – in ut
			units.
		 
         */
        protected
         double DamageRadius =
       	0;
	
    						
    						/**
		 					 * Whether property 'DamageRadius' was received from GB2004.
		 					 */
							protected boolean DamageRadius_Set = false;
							
    						@Override
		    				
 		/**
         * 
			If the projectile has splash damage, how big it is – in ut
			units.
		 
         */
        public  double getDamageRadius()
 	 {
		    					return DamageRadius;
		    				}
		    			
    	
	    /**
         * 
			The class of the projectile (so you know what is flying
			against you).
		 
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
         * 
			The class of the projectile (so you know what is flying
			against you).
		 
         */
        public  String getType()
 	 {
		    					return Type;
		    				}
		    			
    	
	    /**
         * 
			The class of the projectile (so you know what is flying
			against you).
		 
         */
        protected
         boolean Visible =
       	true;
	
    						
    						/**
		 					 * Whether property 'Visible' was received from GB2004.
		 					 */
							protected boolean Visible_Set = false;
							
    						@Override
		    				
 		/**
         * 
			The class of the projectile (so you know what is flying
			against you).
		 
         */
        public  boolean isVisible()
 	 {
		    					return Visible;
		    				}
		    			
		    			
		    			private IncomingProjectileLocal localPart = null;
		    			
		    			@Override
						public IncomingProjectileLocal 
						getLocal() {
							if (localPart != null) return localPart;
							return localPart = new 
								IncomingProjectileLocalMessage();
						}
					
						private IncomingProjectileShared sharedPart = null;
					
						@Override
						public IncomingProjectileShared 
						getShared() {
							if (sharedPart != null) return sharedPart;							
							return sharedPart = new 
								IncomingProjectileSharedMessage();
						}
					
						private IncomingProjectileStatic staticPart = null; 
					
						@Override
						public IncomingProjectileStatic 
						getStatic() {
							if (staticPart != null) return staticPart;
							return staticPart = new 
								IncomingProjectileStaticMessage();
						}
    				
 		/**
         *  
            				Implementation of the local part of the GameBots2004 message PRJ, used
            				to facade PRJMessage.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Both asynchronous and synchronous message. Incoming projectile that we can see.
	
         */
 	public class IncomingProjectileLocalMessage 
	  					extends
  						IncomingProjectileLocal
	    {
 	
		    			@Override
		    			public 
		    			IncomingProjectileLocalMessage clone() {
		    				return this;
		    			}
		    			
		    				public IncomingProjectileLocalMessage getLocal() {
								return this;
					    	}
							public ISharedWorldObject getShared() {
							 	throw new UnsupportedOperationException("Could not return LOCAL as SHARED");
							}
							public IStaticWorldObject getStatic() {
							    throw new UnsupportedOperationException("Could not return LOCAL as STATIC");
							}
		    			
 		/**
         * Unique Id of the projectile. 
         */
        public  UnrealId getId()
 	 {
				    					return Id;
				    				}
				    			
 		/**
         * Estimated time till impact. 
         */
        public  double getImpactTime()
 	 {
				    					return ImpactTime;
				    				}
				    			
 		/**
         * 
			The class of the projectile (so you know what is flying
			against you).
		 
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
		              		
		              			"ImpactTime = " + String.valueOf(getImpactTime()
 	) + " | " + 
		              		
		              			"Visible = " + String.valueOf(isVisible()
 	) + " | " + 
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>Id</b> = " + String.valueOf(getId()
 	) + " <br/> " + 
		              		
		              			"<b>ImpactTime</b> = " + String.valueOf(getImpactTime()
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
            				Implementation of the static part of the GameBots2004 message PRJ, used
            				to facade PRJMessage.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Both asynchronous and synchronous message. Incoming projectile that we can see.
	
         */
 	public class IncomingProjectileStaticMessage 
	  					extends
  						IncomingProjectileStatic
	    {
 	
		    			@Override
		    			public 
		    			IncomingProjectileStaticMessage clone() {
		    				return this;
		    			}
		    			
 		/**
         * Unique Id of the projectile. 
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
 				IncomingProjectileStatic obj = (IncomingProjectileStatic) other;

 				
 						if ( !(
 	 			AdvancedEquals.equalsOrNull(this.getId()
 	, obj.getId()
 	)
 	 		) )
						{
							System.out.println("!!!!!PROPERTY UPDATE ERROR!!!! on property Id on object class IncomingProjectileStatic");
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
            				Implementation of the shared part of the GameBots2004 message PRJ, used
            				to facade PRJMessage.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Both asynchronous and synchronous message. Incoming projectile that we can see.
	
         */
 	public class IncomingProjectileSharedMessage 
	  					extends
  						IncomingProjectileShared
	    {
 	
    	
    	
		public IncomingProjectileSharedMessage()
		{
			
				propertyMap.put(myDirection.getPropertyId(), myDirection);
			
				propertyMap.put(myLocation.getPropertyId(), myLocation);
			
				propertyMap.put(myVelocity.getPropertyId(), myVelocity);
			
				propertyMap.put(mySpeed.getPropertyId(), mySpeed);
			
				propertyMap.put(myOrigin.getPropertyId(), myOrigin);
			
				propertyMap.put(myDamageRadius.getPropertyId(), myDamageRadius);
			
				propertyMap.put(myType.getPropertyId(), myType);
			
		}		
    
		    			@Override
		    			public 
		    			IncomingProjectileSharedMessage clone() {
		    				return this;
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
					= new
					Vector3dProperty
					(
						getId(), 
						"Direction", 
						Direction, 
						IncomingProjectile.class
					);
					
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
					= new
					LocationProperty
					(
						getId(), 
						"Location", 
						Location, 
						IncomingProjectile.class
					);
					
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
					= new
					VelocityProperty
					(
						getId(), 
						"Velocity", 
						Velocity, 
						IncomingProjectile.class
					);
					
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
					= new
					DoubleProperty
					(
						getId(), 
						"Speed", 
						Speed, 
						IncomingProjectile.class
					);
					
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
					= new
					LocationProperty
					(
						getId(), 
						"Origin", 
						Origin, 
						IncomingProjectile.class
					);
					
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
					= new
					DoubleProperty
					(
						getId(), 
						"DamageRadius", 
						DamageRadius, 
						IncomingProjectile.class
					);
					
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
					= new
					StringProperty
					(
						getId(), 
						"Type", 
						Type, 
						IncomingProjectile.class
					);
					
 		/**
         * 
			The class of the projectile (so you know what is flying
			against you).
		 
         */
        public  String getType()
 	 {
			  			return myType.getValue();
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
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=shared+classtype[@name=message]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=shared+classtype[@name=message]) ---        	            	
 	
		}
 	
    	
    	
 	
		@Override
		public IWorldObjectUpdateResult<IWorldObject> update(IWorldObject object) {
			if (object == null)
			{
				return new IWorldObjectUpdateResult.WorldObjectUpdateResult(IWorldObjectUpdateResult.Result.CREATED, this);
			}
			if (!( object instanceof IncomingProjectileMessage) ) {
				throw new PogamutException("Can't update different class than IncomingProjectileMessage, got class " + object.getClass().getSimpleName() + "!", this);		
			}
			IncomingProjectileMessage toUpdate = (IncomingProjectileMessage)object;
			
			boolean updated = false;
			
			// UPDATING LOCAL PROPERTIES
			
				if (toUpdate.ImpactTime != getImpactTime()
 	) {
				    toUpdate.ImpactTime=getImpactTime()
 	;
					updated = true;
				}
			
				if (toUpdate.Visible != isVisible()
 	) {
				    toUpdate.Visible=isVisible()
 	;
					updated = true;
				}
			
         	
         	// UPDATING SHARED PROPERTIES
         	
				if (!SafeEquals.equals(toUpdate.Direction, getDirection()
 	)) {
					toUpdate.Direction=getDirection()
 	;
					updated = true;
				}
			
	            if (!SafeEquals.equals(toUpdate.Location, getLocation()
 	)) {
					toUpdate.Location=getLocation()
 	;
					updated = true;
				}
			
				if (!SafeEquals.equals(toUpdate.Velocity, getVelocity()
 	)) {
					toUpdate.Velocity=getVelocity()
 	;
					updated = true;
				}
			
				if (toUpdate.Speed != getSpeed()
 	) {
				    toUpdate.Speed=getSpeed()
 	;
					updated = true;
				}
			
     		if (getOrigin()
 	 != null) {
     	
	            if (!SafeEquals.equals(toUpdate.Origin, getOrigin()
 	)) {
					toUpdate.Origin=getOrigin()
 	;
					updated = true;
				}
			
     		}
     	
				if (toUpdate.DamageRadius != getDamageRadius()
 	) {
				    toUpdate.DamageRadius=getDamageRadius()
 	;
					updated = true;
				}
			
				if (!SafeEquals.equals(toUpdate.Type, getType()
 	)) {
					toUpdate.Type=getType()
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
			return new IncomingProjectileLocalImpl.IncomingProjectileLocalUpdate
    (this.getLocal(), SimTime);
		}

		@Override
		public ISharedWorldObjectUpdatedEvent getSharedEvent() {
			return new IncomingProjectileSharedImpl.IncomingProjectileSharedUpdate
    (this.getShared(), SimTime, this.getTeamId());
		}

		@Override
		public IStaticWorldObjectUpdatedEvent getStaticEvent() {
			return new IncomingProjectileStaticImpl.IncomingProjectileStaticUpdate
    (this.getStatic(), SimTime);
		}
 	
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
		              			"Id = " + String.valueOf(getId()
 	) + " | " + 
		              		
		              			"ImpactTime = " + String.valueOf(getImpactTime()
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
		              		
		              			"Visible = " + String.valueOf(isVisible()
 	) + " | " + 
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>Id</b> = " + String.valueOf(getId()
 	) + " <br/> " + 
		              		
		              			"<b>ImpactTime</b> = " + String.valueOf(getImpactTime()
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
		              		
		              			"<b>Visible</b> = " + String.valueOf(isVisible()
 	) + " <br/> " + 
		              		
				"<br/>]";     
		}
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=base+classtype[@name=message]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=base+classtype[@name=message]) ---        	            	
 	
		}
 	