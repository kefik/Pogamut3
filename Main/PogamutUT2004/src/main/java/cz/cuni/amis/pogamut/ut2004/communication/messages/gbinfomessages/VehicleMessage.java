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
             				Implementation of the GameBots2004 message VEH contains also its Local/Shared/Static subpart class definitions..  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Synchronous message. Holds vehicles we see. Id for vehicles doesn't work, so 
		this message is without Id. We can join vehicles by ENTER command if we are 
		close enough - ussually 100 ut units or less. Note: Vehicle support is 
		in aplha testing right now.
	
         */
 	public class VehicleMessage   
  				extends 
  				Vehicle
  						implements IWorldObjectUpdatedEvent, ICompositeWorldObjectUpdatedEvent
  						
	    {
 	
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public VehicleMessage()
		{
		}
	
    	
    	
    	
    	/**
		 * Creates new instance of the message Vehicle.
		 * 
		Synchronous message. Holds vehicles we see. Id for vehicles doesn't work, so 
		this message is without Id. We can join vehicles by ENTER command if we are 
		close enough - ussually 100 ut units or less. Note: Vehicle support is 
		in aplha testing right now.
	
		 * Corresponding GameBots message
		 *   
		 *   is
		 *   VEH.
		 * 
 	  	 * 
		 *   
		 *     @param Id Unique Id of the vehicle or vehicle part.
		 *   
		 * 
		 *   
		 *     @param Rotation 
			Which direction the vehicle is facing in absolute terms.
		
		 *   
		 * 
		 *   
		 *     @param Location 
			An absolute location of the vehicle within the map.
		
		 *   
		 * 
		 *   
		 *     @param Velocity 
			Absolute velocity of the vehicle as a vector of movement per one
			game second.
		
		 *   
		 * 
		 *   
		 *     @param Visible 
            If the vehicle is in the field of view of the bot.
        
		 *   
		 * 
		 *   
		 *     @param Team 
			What team the vehicle is on. 255 is no team. 0-3 are red,
			blue, green, gold in that order.
		
		 *   
		 * 
		 *   
		 *     @param Health 
			How much health the vehicle has left. Ranges from 0 to x, depending on the vehicle type.
		
		 *   
		 * 
		 *   
		 *     @param Armor 
			How much the vehicle has left. Note: This may be 0 all the time. Maybe the vehicles are not supporting armor.
		
		 *   
		 * 
		 *   
		 *     @param Driver Unique Id of the driver - if any.
		 *   
		 * 
		 *   
		 *     @param TeamLocked 
            If the vehicle is locked just for its current team.
        
		 *   
		 * 
		 *   
		 *     @param Type 
			Class of the vehicle. If it is a car, turret etc.
		
		 *   
		 * 
		 */
		public VehicleMessage(
			UnrealId Id,  Rotation Rotation,  Location Location,  Velocity Velocity,  boolean Visible,  Integer Team,  Integer Health,  Integer Armor,  UnrealId Driver,  boolean TeamLocked,  String Type
		) {
			
					this.Id = Id;
				
					this.Rotation = Rotation;
				
					this.Location = Location;
				
					this.Velocity = Velocity;
				
					this.Visible = Visible;
				
					this.Team = Team;
				
					this.Health = Health;
				
					this.Armor = Armor;
				
					this.Driver = Driver;
				
					this.TeamLocked = TeamLocked;
				
					this.Type = Type;
				
		}
    
		/**
		 * Cloning constructor from the full message.
		 *
		 * @param original
		 */
		public VehicleMessage(VehicleMessage original) {		
			
					this.Id = original.getId()
 	;
				
					this.Rotation = original.getRotation()
 	;
				
					this.Location = original.getLocation()
 	;
				
					this.Velocity = original.getVelocity()
 	;
				
					this.Visible = original.isVisible()
 	;
				
					this.Team = original.getTeam()
 	;
				
					this.Health = original.getHealth()
 	;
				
					this.Armor = original.getArmor()
 	;
				
					this.Driver = original.getDriver()
 	;
				
					this.TeamLocked = original.isTeamLocked()
 	;
				
					this.Type = original.getType()
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
         * Unique Id of the vehicle or vehicle part. 
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
         Rotation Rotation =
       	null;
	
    						
    						/**
		 					 * Whether property 'Rotation' was received from GB2004.
		 					 */
							protected boolean Rotation_Set = false;
							
    						@Override
		    				
 		/**
         * 
			Which direction the vehicle is facing in absolute terms.
		 
         */
        public  Rotation getRotation()
 	 {
		    					return Rotation;
		    				}
		    			
    	
	    /**
         * 
			An absolute location of the vehicle within the map.
		 
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
			An absolute location of the vehicle within the map.
		 
         */
        public  Location getLocation()
 	 {
		    					return Location;
		    				}
		    			
    	
	    /**
         * 
			Absolute velocity of the vehicle as a vector of movement per one
			game second.
		 
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
			Absolute velocity of the vehicle as a vector of movement per one
			game second.
		 
         */
        public  Velocity getVelocity()
 	 {
		    					return Velocity;
		    				}
		    			
    	
	    /**
         * 
            If the vehicle is in the field of view of the bot.
         
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
            If the vehicle is in the field of view of the bot.
         
         */
        public  boolean isVisible()
 	 {
		    					return Visible;
		    				}
		    			
    	
	    /**
         * 
			What team the vehicle is on. 255 is no team. 0-3 are red,
			blue, green, gold in that order.
		 
         */
        protected
         Integer Team =
       	255;
	
    						
    						/**
		 					 * Whether property 'Team' was received from GB2004.
		 					 */
							protected boolean Team_Set = false;
							
    						@Override
		    				
 		/**
         * 
			What team the vehicle is on. 255 is no team. 0-3 are red,
			blue, green, gold in that order.
		 
         */
        public  Integer getTeam()
 	 {
		    					return Team;
		    				}
		    			
    	
	    /**
         * 
			How much health the vehicle has left. Ranges from 0 to x, depending on the vehicle type.
		 
         */
        protected
         Integer Health =
       	0;
	
    						
    						/**
		 					 * Whether property 'Health' was received from GB2004.
		 					 */
							protected boolean Health_Set = false;
							
    						@Override
		    				
 		/**
         * 
			How much health the vehicle has left. Ranges from 0 to x, depending on the vehicle type.
		 
         */
        public  Integer getHealth()
 	 {
		    					return Health;
		    				}
		    			
    	
	    /**
         * 
			How much the vehicle has left. Note: This may be 0 all the time. Maybe the vehicles are not supporting armor.
		 
         */
        protected
         Integer Armor =
       	0;
	
    						
    						/**
		 					 * Whether property 'Armor' was received from GB2004.
		 					 */
							protected boolean Armor_Set = false;
							
    						@Override
		    				
 		/**
         * 
			How much the vehicle has left. Note: This may be 0 all the time. Maybe the vehicles are not supporting armor.
		 
         */
        public  Integer getArmor()
 	 {
		    					return Armor;
		    				}
		    			
    	
	    /**
         * Unique Id of the driver - if any. 
         */
        protected
         UnrealId Driver =
       	null;
	
    						
    						/**
		 					 * Whether property 'Driver' was received from GB2004.
		 					 */
							protected boolean Driver_Set = false;
							
    						@Override
		    				
 		/**
         * Unique Id of the driver - if any. 
         */
        public  UnrealId getDriver()
 	 {
		    					return Driver;
		    				}
		    			
    	
	    /**
         * 
            If the vehicle is locked just for its current team.
         
         */
        protected
         boolean TeamLocked =
       	false;
	
    						
    						/**
		 					 * Whether property 'TeamLocked' was received from GB2004.
		 					 */
							protected boolean TeamLocked_Set = false;
							
    						@Override
		    				
 		/**
         * 
            If the vehicle is locked just for its current team.
         
         */
        public  boolean isTeamLocked()
 	 {
		    					return TeamLocked;
		    				}
		    			
    	
	    /**
         * 
			Class of the vehicle. If it is a car, turret etc.
		 
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
			Class of the vehicle. If it is a car, turret etc.
		 
         */
        public  String getType()
 	 {
		    					return Type;
		    				}
		    			
		    			
		    			private VehicleLocal localPart = null;
		    			
		    			@Override
						public VehicleLocal 
						getLocal() {
							if (localPart != null) return localPart;
							return localPart = new 
								VehicleLocalMessage();
						}
					
						private VehicleShared sharedPart = null;
					
						@Override
						public VehicleShared 
						getShared() {
							if (sharedPart != null) return sharedPart;							
							return sharedPart = new 
								VehicleSharedMessage();
						}
					
						private VehicleStatic staticPart = null; 
					
						@Override
						public VehicleStatic 
						getStatic() {
							if (staticPart != null) return staticPart;
							return staticPart = new 
								VehicleStaticMessage();
						}
    				
 		/**
         *  
            				Implementation of the local part of the GameBots2004 message VEH, used
            				to facade VEHMessage.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Synchronous message. Holds vehicles we see. Id for vehicles doesn't work, so 
		this message is without Id. We can join vehicles by ENTER command if we are 
		close enough - ussually 100 ut units or less. Note: Vehicle support is 
		in aplha testing right now.
	
         */
 	public class VehicleLocalMessage 
	  					extends
  						VehicleLocal
	    {
 	
		    			@Override
		    			public 
		    			VehicleLocalMessage clone() {
		    				return this;
		    			}
		    			
		    				public VehicleLocalMessage getLocal() {
								return this;
					    	}
							public ISharedWorldObject getShared() {
							 	throw new UnsupportedOperationException("Could not return LOCAL as SHARED");
							}
							public IStaticWorldObject getStatic() {
							    throw new UnsupportedOperationException("Could not return LOCAL as STATIC");
							}
		    			
 		/**
         * Unique Id of the vehicle or vehicle part. 
         */
        public  UnrealId getId()
 	 {
				    					return Id;
				    				}
				    			
 		/**
         * 
            If the vehicle is in the field of view of the bot.
         
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
            				Implementation of the static part of the GameBots2004 message VEH, used
            				to facade VEHMessage.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Synchronous message. Holds vehicles we see. Id for vehicles doesn't work, so 
		this message is without Id. We can join vehicles by ENTER command if we are 
		close enough - ussually 100 ut units or less. Note: Vehicle support is 
		in aplha testing right now.
	
         */
 	public class VehicleStaticMessage 
	  					extends
  						VehicleStatic
	    {
 	
		    			@Override
		    			public 
		    			VehicleStaticMessage clone() {
		    				return this;
		    			}
		    			
 		/**
         * Unique Id of the vehicle or vehicle part. 
         */
        public  UnrealId getId()
 	 {
				    					return Id;
				    				}
				    			
 		/**
         * 
			Class of the vehicle. If it is a car, turret etc.
		 
         */
        public  String getType()
 	 {
				    					return Type;
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
 				VehicleStatic obj = (VehicleStatic) other;

 				
 						if ( !(
 	 			AdvancedEquals.equalsOrNull(this.getId()
 	, obj.getId()
 	)
 	 		) )
						{
							System.out.println("!!!!!PROPERTY UPDATE ERROR!!!! on property Id on object class VehicleStatic");
							return true;
						}
 					
 						if ( !(
 	 			AdvancedEquals.equalsOrNull(this.getType()
 	, obj.getType()
 	)
 	 		) )
						{
							System.out.println("!!!!!PROPERTY UPDATE ERROR!!!! on property Type on object class VehicleStatic");
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
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>Id</b> = " + String.valueOf(getId()
 	) + " <br/> " + 
		              		
		              			"<b>Type</b> = " + String.valueOf(getType()
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
            				Implementation of the shared part of the GameBots2004 message VEH, used
            				to facade VEHMessage.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Synchronous message. Holds vehicles we see. Id for vehicles doesn't work, so 
		this message is without Id. We can join vehicles by ENTER command if we are 
		close enough - ussually 100 ut units or less. Note: Vehicle support is 
		in aplha testing right now.
	
         */
 	public class VehicleSharedMessage 
	  					extends
  						VehicleShared
	    {
 	
    	
    	
		public VehicleSharedMessage()
		{
			
				propertyMap.put(myRotation.getPropertyId(), myRotation);
			
				propertyMap.put(myLocation.getPropertyId(), myLocation);
			
				propertyMap.put(myVelocity.getPropertyId(), myVelocity);
			
				propertyMap.put(myTeam.getPropertyId(), myTeam);
			
				propertyMap.put(myHealth.getPropertyId(), myHealth);
			
				propertyMap.put(myArmor.getPropertyId(), myArmor);
			
				propertyMap.put(myDriver.getPropertyId(), myDriver);
			
				propertyMap.put(myTeamLocked.getPropertyId(), myTeamLocked);
			
		}		
    
		    			@Override
		    			public 
		    			VehicleSharedMessage clone() {
		    				return this;
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
					= new
					RotationProperty
					(
						getId(), 
						"Rotation", 
						Rotation, 
						Vehicle.class
					);
					
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
					= new
					LocationProperty
					(
						getId(), 
						"Location", 
						Location, 
						Vehicle.class
					);
					
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
					= new
					VelocityProperty
					(
						getId(), 
						"Velocity", 
						Velocity, 
						Vehicle.class
					);
					
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
					= new
					IntegerProperty
					(
						getId(), 
						"Team", 
						Team, 
						Vehicle.class
					);
					
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
					= new
					IntegerProperty
					(
						getId(), 
						"Health", 
						Health, 
						Vehicle.class
					);
					
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
					= new
					IntegerProperty
					(
						getId(), 
						"Armor", 
						Armor, 
						Vehicle.class
					);
					
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
					= new
					UnrealIdProperty
					(
						getId(), 
						"Driver", 
						Driver, 
						Vehicle.class
					);
					
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
					= new
					BooleanProperty
					(
						getId(), 
						"TeamLocked", 
						TeamLocked, 
						Vehicle.class
					);
					
 		/**
         * 
            If the vehicle is locked just for its current team.
         
         */
        public  boolean isTeamLocked()
 	 {
			  			return myTeamLocked.getValue();
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
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=shared+classtype[@name=message]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=shared+classtype[@name=message]) ---        	            	
 	
		}
 	
    	
    	
 	
		@Override
		public IWorldObjectUpdateResult<IWorldObject> update(IWorldObject object) {
			if (object == null)
			{
				return new IWorldObjectUpdateResult.WorldObjectUpdateResult(IWorldObjectUpdateResult.Result.CREATED, this);
			}
			if (!( object instanceof VehicleMessage) ) {
				throw new PogamutException("Can't update different class than VehicleMessage, got class " + object.getClass().getSimpleName() + "!", this);		
			}
			VehicleMessage toUpdate = (VehicleMessage)object;
			
			boolean updated = false;
			
			// UPDATING LOCAL PROPERTIES
			
				if (toUpdate.Visible != isVisible()
 	) {
				    toUpdate.Visible=isVisible()
 	;
					updated = true;
				}
			
         	
         	// UPDATING SHARED PROPERTIES
         	
				if (!SafeEquals.equals(toUpdate.Rotation, getRotation()
 	)) {
					toUpdate.Rotation=getRotation()
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
			
				if (!SafeEquals.equals(toUpdate.Team, getTeam()
 	)) {
					toUpdate.Team=getTeam()
 	;
					updated = true;
				}
			
				if (!SafeEquals.equals(toUpdate.Health, getHealth()
 	)) {
					toUpdate.Health=getHealth()
 	;
					updated = true;
				}
			
				if (!SafeEquals.equals(toUpdate.Armor, getArmor()
 	)) {
					toUpdate.Armor=getArmor()
 	;
					updated = true;
				}
			
				if (!SafeEquals.equals(toUpdate.Driver, getDriver()
 	)) {
					toUpdate.Driver=getDriver()
 	;
					updated = true;
				}
			
				if (toUpdate.TeamLocked != isTeamLocked()
 	) {
				    toUpdate.TeamLocked=isTeamLocked()
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
			return new VehicleLocalImpl.VehicleLocalUpdate
    (this.getLocal(), SimTime);
		}

		@Override
		public ISharedWorldObjectUpdatedEvent getSharedEvent() {
			return new VehicleSharedImpl.VehicleSharedUpdate
    (this.getShared(), SimTime, this.getTeamId());
		}

		@Override
		public IStaticWorldObjectUpdatedEvent getStaticEvent() {
			return new VehicleStaticImpl.VehicleStaticUpdate
    (this.getStatic(), SimTime);
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
		              		
		              			"Visible = " + String.valueOf(isVisible()
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
		              		
		              			"Type = " + String.valueOf(getType()
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
		              		
		              			"<b>Visible</b> = " + String.valueOf(isVisible()
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
		              		
		              			"<b>Type</b> = " + String.valueOf(getType()
 	) + " <br/> " + 
		              		
				"<br/>]";     
		}
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=base+classtype[@name=message]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=base+classtype[@name=message]) ---        	            	
 	
		}
 	