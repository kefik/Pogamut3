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
            				Abstract definition of the GameBots2004 message ENT.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Generic physical entity that may be present in the world. Works as a hook for custom objects, such as new items from EmohawkRPG extension.
	
         */
 	public abstract class Entity   
  				extends 
  				InfoMessage
  						implements IWorldEvent, IWorldChangeEvent, ICompositeWorldObject
  						
	    		,ILocated
	    		,IGBViewable
	    {
 	
    	/** Example how the message looks like - used during parser tests. */
    	public static final String PROTOTYPE =
    		"ENT {Id unreal_id}  {Visible False}  {Location 0,0,0}  {Rotation 0,0,0}  {Velocity 0,0,0}  {Type text}  {EntityClass text} ";
    
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public Entity()
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
			Unique Id of the item. This Id represents some entity in the world.
		 
         */
        public abstract UnrealId getId()
 	;
		    			
 		/**
         * 
			If the item is in the field of view of the bot.
		 
         */
        public abstract boolean isVisible()
 	;
		    			
 		/**
         * Location of the item. 
         */
        public abstract Location getLocation()
 	;
		    			
 		/**
         * Rotation of the item. 
         */
        public abstract Rotation getRotation()
 	;
		    			
 		/**
         * Velocity of the item. 
         */
        public abstract Velocity getVelocity()
 	;
		    			
 		/**
         * Type (category) of the entity. 
         */
        public abstract String getType()
 	;
		    			
 		/**
         * Type (category) of the entity. 
         */
        public abstract String getEntityClass()
 	;
		    			
		 	@Override
			public IWorldObjectUpdatedEvent createDisappearEvent() {
				return new ObjectDisappeared(this, getSimTime());
			}
			
			public static class ObjectDisappeared implements IWorldObjectUpdatedEvent
			{
				
				public ObjectDisappeared(Entity obj, long time) {
					this.obj = obj;
					this.time = time;
				}
				
				private Entity obj;
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
					if (!(obj instanceof EntityMessage)) {
						throw new PogamutException("Can't update different class than EntityMessage, got class " + obj.getClass().getSimpleName() + "!", this);
					}
					EntityMessage toUpdate = (EntityMessage)obj;
					if (toUpdate.Visible) {
						toUpdate.Visible = false;
						return new IWorldObjectUpdateResult.WorldObjectUpdateResult(IWorldObjectUpdateResult.Result.UPDATED, obj);
					} else {
						return new IWorldObjectUpdateResult.WorldObjectUpdateResult(IWorldObjectUpdateResult.Result.SAME, obj);
					}
				}
		
			}
	
		
    	
    	public static class EntityUpdate
     extends GBObjectUpdate implements ICompositeWorldObjectUpdatedEvent, IGBWorldObjectEvent {	
			private Entity object;
			private long time;
			private ITeamId teamId;
			
			public EntityUpdate
    (Entity source, long eventTime, ITeamId teamId) {
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
				return new EntityLocalImpl.EntityLocalUpdate
    ((EntityLocal)object.getLocal(), time);
			}
	
			@Override
			public ISharedWorldObjectUpdatedEvent getSharedEvent() {
				return new EntitySharedImpl.EntitySharedUpdate
    ((EntityShared)object.getShared(), time, teamId);
			}
	
			@Override
			public IStaticWorldObjectUpdatedEvent getStaticEvent() {
				return new EntityStaticImpl.EntityStaticUpdate
    ((EntityStatic)object.getStatic(), time);
			}
			
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
 	
 	    public String toJsonLiteral() {
            return "entity( "
            		+
									(getId()
 	 == null ? "null" :
										"\"" + getId()
 	.getStringId() + "\"" 
									)
								+ ", " + 
								    String.valueOf(isVisible()
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
									(getRotation()
 	 == null ? "null" :
										"[" + getRotation()
 	.getPitch() + ", " + getRotation()
 	.getYaw() + ", " + getRotation()
 	.getRoll() + "]" 
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
									(getType()
 	 == null ? "null" :
										"\"" + getType()
 	 + "\"" 
									)
								+ ", " + 
									(getEntityClass()
 	 == null ? "null" :
										"\"" + getEntityClass()
 	 + "\"" 
									)
								
                   + ")";
        }
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=base+classtype[@name=abstract]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=base+classtype[@name=abstract]) ---        	            	
 	
		}
 	