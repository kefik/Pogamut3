package cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages;
 		
 		// --- IMPORTS FROM /messages/settings/javasettings/javaimport BEGIN
			import java.util.*;import javax.vecmath.*;import cz.cuni.amis.pogamut.base.communication.messages.*;import cz.cuni.amis.pogamut.base.communication.worldview.*;import cz.cuni.amis.pogamut.base.communication.worldview.event.*;import cz.cuni.amis.pogamut.base.communication.worldview.object.*;import cz.cuni.amis.pogamut.multi.communication.worldview.object.*;import cz.cuni.amis.pogamut.base.communication.translator.event.*;import cz.cuni.amis.pogamut.multi.communication.translator.event.*;import cz.cuni.amis.pogamut.base3d.worldview.object.*;import cz.cuni.amis.pogamut.base3d.worldview.object.event.*;import cz.cuni.amis.pogamut.ut2004.communication.messages.*;import cz.cuni.amis.pogamut.ut2004.communication.worldview.objects.*;import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.objects.*;import cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor.*;import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType.Category;import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;import cz.cuni.amis.utils.exception.*;import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdateResult.Result;import cz.cuni.amis.utils.SafeEquals;import cz.cuni.amis.pogamut.base.agent.*;import cz.cuni.amis.pogamut.multi.agent.*;import cz.cuni.amis.pogamut.multi.communication.worldview.property.*;import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.property.*;import cz.cuni.amis.utils.token.*;import cz.cuni.amis.utils.*;
		// --- IMPORTS FROM /messages/settings/javasettings/javaimport END
		
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name='all'] BEGIN
				
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name='all'] END
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name=base]+classtype[@name=abstract] BEGIN
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name=base]+classtype[@name=abstract] END
    
 		/**
         *  
            				Abstract definition of the GameBots2004 message VEH.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Synchronous message. Holds vehicles we see. Id for vehicles doesn't work, so 
		this message is without Id. We can join vehicles by ENTER command if we are 
		close enough - ussually 100 ut units or less. Note: Vehicle support is 
		in aplha testing right now.
	
         */
 	public abstract class Vehicle   
  				extends 
  				InfoMessage
  						implements IWorldEvent, IWorldChangeEvent, ICompositeWorldObject
  						
	    		,ILocated
	    		,ILocomotive
	    		,IRotable
	    		,IGBViewable
	    {
 	
    	/** Example how the message looks like - used during parser tests. */
    	public static final String PROTOTYPE =
    		"VEH {Id unreal_id}  {Rotation 0,0,0}  {Location 0,0,0}  {Velocity 0,0,0}  {Visible False}  {Team 0}  {Health 0}  {Armor 0}  {Driver unreal_id}  {TeamLocked False}  {Type text} ";
    
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public Vehicle()
		{
		}
	
				// abstract message, it does not have any more constructors				
			
	   		
			protected long SimTime;
				
			/**
			 * Simulation time in MILLI SECONDS !!!
			 */	
			@Override
			public long getSimTime() {
				return SimTime;
			}
						
			/**
			 * Used by Yylex to slip correct time of the object or programmatically.
			 */
			protected void setSimTime(long SimTime) {
				this.SimTime = SimTime;
			}
	   	
 		/**
         * Unique Id of the vehicle or vehicle part. 
         */
        public abstract UnrealId getId()
 	;
		    			
 		/**
         * 
			Which direction the vehicle is facing in absolute terms.
		 
         */
        public abstract Rotation getRotation()
 	;
		    			
 		/**
         * 
			An absolute location of the vehicle within the map.
		 
         */
        public abstract Location getLocation()
 	;
		    			
 		/**
         * 
			Absolute velocity of the vehicle as a vector of movement per one
			game second.
		 
         */
        public abstract Velocity getVelocity()
 	;
		    			
 		/**
         * 
            If the vehicle is in the field of view of the bot.
         
         */
        public abstract boolean isVisible()
 	;
		    			
 		/**
         * 
			What team the vehicle is on. 255 is no team. 0-3 are red,
			blue, green, gold in that order.
		 
         */
        public abstract Integer getTeam()
 	;
		    			
 		/**
         * 
			How much health the vehicle has left. Ranges from 0 to x, depending on the vehicle type.
		 
         */
        public abstract Integer getHealth()
 	;
		    			
 		/**
         * 
			How much the vehicle has left. Note: This may be 0 all the time. Maybe the vehicles are not supporting armor.
		 
         */
        public abstract Integer getArmor()
 	;
		    			
 		/**
         * Unique Id of the driver - if any. 
         */
        public abstract UnrealId getDriver()
 	;
		    			
 		/**
         * 
            If the vehicle is locked just for its current team.
         
         */
        public abstract boolean isTeamLocked()
 	;
		    			
 		/**
         * 
			Class of the vehicle. If it is a car, turret etc.
		 
         */
        public abstract String getType()
 	;
		    			
		 	@Override
			public IWorldObjectUpdatedEvent createDisappearEvent() {
				return new ObjectDisappeared(this, getSimTime());
			}
			
			public static class ObjectDisappeared implements IWorldObjectUpdatedEvent
			{
				
				public ObjectDisappeared(Vehicle obj, long time) {
					this.obj = obj;
					this.time = time;
				}
				
				private Vehicle obj;
				private long time;
		
				@Override
				public WorldObjectId getId() {
					return obj.getId();
				}
		
		        /**
		         * Simulation time in MILLI SECONDS !!!
		         */
				@Override
				public long getSimTime() {
					return time;
				}
		
				@Override
				public IWorldObjectUpdateResult<IWorldObject> update(IWorldObject obj) {
					if (obj == null) {
						throw new PogamutException("Can't 'disappear' null!", this);
					}
					if (!(obj instanceof VehicleMessage)) {
						throw new PogamutException("Can't update different class than VehicleMessage, got class " + obj.getClass().getSimpleName() + "!", this);
					}
					VehicleMessage toUpdate = (VehicleMessage)obj;
					if (toUpdate.Visible) {
						toUpdate.Visible = false;
						return new IWorldObjectUpdateResult.WorldObjectUpdateResult(IWorldObjectUpdateResult.Result.UPDATED, obj);
					} else {
						return new IWorldObjectUpdateResult.WorldObjectUpdateResult(IWorldObjectUpdateResult.Result.SAME, obj);
					}
				}
		
			}
	
		
    	
    	public static class VehicleUpdate
     extends GBObjectUpdate implements ICompositeWorldObjectUpdatedEvent, IGBWorldObjectEvent {	
			private Vehicle object;
			private long time;
			private ITeamId teamId;
			
			public VehicleUpdate
    (Vehicle source, long eventTime, ITeamId teamId) {
				this.object = source;
				this.time = eventTime;
				this.teamId = teamId;
			}
			
			/**
			 * Simulation time in MILLI SECONDS !!!
			 */ 
			@Override
			public long getSimTime() {
				return time;
			}
	
			@Override
			public IWorldObject getObject() {
				return object;
			}
	
			@Override
			public WorldObjectId getId() {
				return object.getId();
			}
	
			@Override
			public ILocalWorldObjectUpdatedEvent getLocalEvent() {
				return new VehicleLocalImpl.VehicleLocalUpdate
    ((VehicleLocal)object.getLocal(), time);
			}
	
			@Override
			public ISharedWorldObjectUpdatedEvent getSharedEvent() {
				return new VehicleSharedImpl.VehicleSharedUpdate
    ((VehicleShared)object.getShared(), time, teamId);
			}
	
			@Override
			public IStaticWorldObjectUpdatedEvent getStaticEvent() {
				return new VehicleStaticImpl.VehicleStaticUpdate
    ((VehicleStatic)object.getStatic(), time);
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
 	
 	    public String toJsonLiteral() {
            return "vehicle( "
            		+
									(getId()
 	 == null ? "null" :
										"\"" + getId()
 	.getStringId() + "\"" 
									)
								+ ", " + 
									(getRotation()
 	 == null ? "null" :
										"[" + getRotation()
 	.getPitch() + ", " + getRotation()
 	.getYaw() + ", " + getRotation()
 	.getRoll() + "]" 
									)								    
								+ ", " + 
								    (getLocation()
 	 == null ? "null" :
										"[" + getLocation()
 	.getX() + ", " + getLocation()
 	.getY() + ", " + getLocation()
 	.getZ() + "]" 
									)
								+ ", " + 
								    (getVelocity()
 	 == null ? "null" :
										"[" + getVelocity()
 	.getX() + ", " + getVelocity()
 	.getY() + ", " + getVelocity()
 	.getZ() + "]" 
									)
								+ ", " + 
								    String.valueOf(isVisible()
 	)									
								+ ", " + 
								    String.valueOf(getTeam()
 	)									
								+ ", " + 
								    String.valueOf(getHealth()
 	)									
								+ ", " + 
								    String.valueOf(getArmor()
 	)									
								+ ", " + 
									(getDriver()
 	 == null ? "null" :
										"\"" + getDriver()
 	.getStringId() + "\"" 
									)
								+ ", " + 
								    String.valueOf(isTeamLocked()
 	)									
								+ ", " + 
									(getType()
 	 == null ? "null" :
										"\"" + getType()
 	 + "\"" 
									)
								
                   + ")";
        }
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=base+classtype[@name=abstract]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=base+classtype[@name=abstract]) ---        	            	
 	
		}
 	