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
            				Abstract definition of the GameBots2004 message MOV.  
            			
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
 	public abstract class Mover   
  				extends 
  				InfoMessage
  						implements IWorldEvent, IWorldChangeEvent, ICompositeWorldObject
  						
	    		,ILocated
	    		,ILocomotive
	    		,IGBViewable
	    {
 	
    	/** Example how the message looks like - used during parser tests. */
    	public static final String PROTOTYPE =
    		"MOV {Id unreal_id}  {Location 0,0,0}  {Visible False}  {DamageTrig False}  {Type text}  {IsMoving False}  {Velocity 0,0,0}  {MoveTime 0}  {OpenTime 0}  {BasePos 0,0,0}  {BaseRot 0,0,0}  {DelayTime 0}  {State text}  {NavPointMarker unreal_id} ";
    
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public Mover()
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
         * 
			A unique Id of this mover assigned by the game.
		 
         */
        public abstract UnrealId getId()
 	;
		    			
 		/**
         * Location of the mover. 
         */
        public abstract Location getLocation()
 	;
		    			
 		/**
         * If the mover is in the field of view of the bot. 
         */
        public abstract boolean isVisible()
 	;
		    			
 		/**
         * 
			True if the mover needs to be shot to be activated.
		 
         */
        public abstract boolean isDamageTrig()
 	;
		    			
 		/**
         * String class of the mover. 
         */
        public abstract String getType()
 	;
		    			
 		/**
         * Does the mover move right now? 
         */
        public abstract boolean isIsMoving()
 	;
		    			
 		/**
         * Velocity vector. 
         */
        public abstract Velocity getVelocity()
 	;
		    			
 		/**
         * How long the mover moves, when it becomes triggered, before it stops. 
         */
        public abstract double getMoveTime()
 	;
		    			
 		/**
         * How long the mover stands still when it reaches its destination position. After
      this time, the mover returns back to its initial position. 
         */
        public abstract double getOpenTime()
 	;
		    			
 		/**
         * Base position of the mover. 
         */
        public abstract Location getBasePos()
 	;
		    			
 		/**
         * Base rotation of the mover. 
         */
        public abstract Location getBaseRot()
 	;
		    			
 		/**
         * Delay before starting to open (or before lift starts to move). 
         */
        public abstract double getDelayTime()
 	;
		    			
 		/**
         * Name of the state Mover is currently in. Can be used to determine the type of the mover.  
         */
        public abstract String getState()
 	;
		    			
 		/**
         * Navigation point marking this mover. We can parse this Id to get information about type of the mover. Marker Id contains "lift" string if it is a lift, "door" string if it is a door, etc.  
         */
        public abstract UnrealId getNavPointMarker()
 	;
		    			
		 	@Override
			public IWorldObjectUpdatedEvent createDisappearEvent() {
				return new ObjectDisappeared(this, getSimTime());
			}
			
			public static class ObjectDisappeared implements IWorldObjectUpdatedEvent
			{
				
				public ObjectDisappeared(Mover obj, long time) {
					this.obj = obj;
					this.time = time;
				}
				
				private Mover obj;
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
					if (!(obj instanceof MoverMessage)) {
						throw new PogamutException("Can't update different class than MoverMessage, got class " + obj.getClass().getSimpleName() + "!", this);
					}
					MoverMessage toUpdate = (MoverMessage)obj;
					if (toUpdate.Visible) {
						toUpdate.Visible = false;
						return new IWorldObjectUpdateResult.WorldObjectUpdateResult(IWorldObjectUpdateResult.Result.UPDATED, obj);
					} else {
						return new IWorldObjectUpdateResult.WorldObjectUpdateResult(IWorldObjectUpdateResult.Result.SAME, obj);
					}
				}
		
			}
	
		
    	
    	public static class MoverUpdate
     extends GBObjectUpdate implements ICompositeWorldObjectUpdatedEvent, IGBWorldObjectEvent {	
			private Mover object;
			private long time;
			private ITeamId teamId;
			
			public MoverUpdate
    (Mover source, long eventTime, ITeamId teamId) {
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
				return new MoverLocalImpl.MoverLocalUpdate
    ((MoverLocal)object.getLocal(), time);
			}
	
			@Override
			public ISharedWorldObjectUpdatedEvent getSharedEvent() {
				return new MoverSharedImpl.MoverSharedUpdate
    ((MoverShared)object.getShared(), time, teamId);
			}
	
			@Override
			public IStaticWorldObjectUpdatedEvent getStaticEvent() {
				return new MoverStaticImpl.MoverStaticUpdate
    ((MoverStatic)object.getStatic(), time);
			}
			
		}
    
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
		              			"Id = " + String.valueOf(getId()
 	) + " | " + 
		              		
		              			"Location = " + String.valueOf(getLocation()
 	) + " | " + 
		              		
		              			"Visible = " + String.valueOf(isVisible()
 	) + " | " + 
		              		
		              			"DamageTrig = " + String.valueOf(isDamageTrig()
 	) + " | " + 
		              		
		              			"Type = " + String.valueOf(getType()
 	) + " | " + 
		              		
		              			"IsMoving = " + String.valueOf(isIsMoving()
 	) + " | " + 
		              		
		              			"Velocity = " + String.valueOf(getVelocity()
 	) + " | " + 
		              		
		              			"MoveTime = " + String.valueOf(getMoveTime()
 	) + " | " + 
		              		
		              			"OpenTime = " + String.valueOf(getOpenTime()
 	) + " | " + 
		              		
		              			"BasePos = " + String.valueOf(getBasePos()
 	) + " | " + 
		              		
		              			"BaseRot = " + String.valueOf(getBaseRot()
 	) + " | " + 
		              		
		              			"DelayTime = " + String.valueOf(getDelayTime()
 	) + " | " + 
		              		
		              			"State = " + String.valueOf(getState()
 	) + " | " + 
		              		
		              			"NavPointMarker = " + String.valueOf(getNavPointMarker()
 	) + " | " + 
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>Id</b> = " + String.valueOf(getId()
 	) + " <br/> " + 
		              		
		              			"<b>Location</b> = " + String.valueOf(getLocation()
 	) + " <br/> " + 
		              		
		              			"<b>Visible</b> = " + String.valueOf(isVisible()
 	) + " <br/> " + 
		              		
		              			"<b>DamageTrig</b> = " + String.valueOf(isDamageTrig()
 	) + " <br/> " + 
		              		
		              			"<b>Type</b> = " + String.valueOf(getType()
 	) + " <br/> " + 
		              		
		              			"<b>IsMoving</b> = " + String.valueOf(isIsMoving()
 	) + " <br/> " + 
		              		
		              			"<b>Velocity</b> = " + String.valueOf(getVelocity()
 	) + " <br/> " + 
		              		
		              			"<b>MoveTime</b> = " + String.valueOf(getMoveTime()
 	) + " <br/> " + 
		              		
		              			"<b>OpenTime</b> = " + String.valueOf(getOpenTime()
 	) + " <br/> " + 
		              		
		              			"<b>BasePos</b> = " + String.valueOf(getBasePos()
 	) + " <br/> " + 
		              		
		              			"<b>BaseRot</b> = " + String.valueOf(getBaseRot()
 	) + " <br/> " + 
		              		
		              			"<b>DelayTime</b> = " + String.valueOf(getDelayTime()
 	) + " <br/> " + 
		              		
		              			"<b>State</b> = " + String.valueOf(getState()
 	) + " <br/> " + 
		              		
		              			"<b>NavPointMarker</b> = " + String.valueOf(getNavPointMarker()
 	) + " <br/> " + 
		              		
				"<br/>]";     
		}
 	
 	    public String toJsonLiteral() {
            return "mover( "
            		+
									(getId()
 	 == null ? "null" :
										"\"" + getId()
 	.getStringId() + "\"" 
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
								    String.valueOf(isVisible()
 	)									
								+ ", " + 
								    String.valueOf(isDamageTrig()
 	)									
								+ ", " + 
									(getType()
 	 == null ? "null" :
										"\"" + getType()
 	 + "\"" 
									)
								+ ", " + 
								    String.valueOf(isIsMoving()
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
								    String.valueOf(getMoveTime()
 	)									
								+ ", " + 
								    String.valueOf(getOpenTime()
 	)									
								+ ", " + 
								    (getBasePos()
 	 == null ? "null" :
										"[" + getBasePos()
 	.getX() + ", " + getBasePos()
 	.getY() + ", " + getBasePos()
 	.getZ() + "]" 
									)
								+ ", " + 
								    (getBaseRot()
 	 == null ? "null" :
										"[" + getBaseRot()
 	.getX() + ", " + getBaseRot()
 	.getY() + ", " + getBaseRot()
 	.getZ() + "]" 
									)
								+ ", " + 
								    String.valueOf(getDelayTime()
 	)									
								+ ", " + 
									(getState()
 	 == null ? "null" :
										"\"" + getState()
 	 + "\"" 
									)
								+ ", " + 
									(getNavPointMarker()
 	 == null ? "null" :
										"\"" + getNavPointMarker()
 	.getStringId() + "\"" 
									)
								
                   + ")";
        }
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=base+classtype[@name=abstract]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=base+classtype[@name=abstract]) ---        	            	
 	
		}
 	