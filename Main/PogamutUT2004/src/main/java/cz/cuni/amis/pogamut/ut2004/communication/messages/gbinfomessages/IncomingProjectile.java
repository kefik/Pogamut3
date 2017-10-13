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
            				Abstract definition of the GameBots2004 message PRJ.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Both asynchronous and synchronous message. Incoming projectile that we can see.
	
         */
 	public abstract class IncomingProjectile   
  				extends 
  				InfoMessage
  						implements IWorldEvent, IWorldChangeEvent, ICompositeWorldObject
  						
	    		,IGBViewable
	    		,ILocated
	    {
 	
    	/** Example how the message looks like - used during parser tests. */
    	public static final String PROTOTYPE =
    		"PRJ {Id unreal_id}  {ImpactTime 0}  {Direction 0,0,0}  {Location 0,0,0}  {Velocity 0,0,0}  {Speed 0}  {Origin 0,0,0}  {DamageRadius 0}  {Type text}  {Visible False} ";
    
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public IncomingProjectile()
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
         * Unique Id of the projectile. 
         */
        public abstract UnrealId getId()
 	;
		    			
 		/**
         * Estimated time till impact. 
         */
        public abstract double getImpactTime()
 	;
		    			
 		/**
         * 
			Which direction projectile is heading to -> orientation
			vector.
		 
         */
        public abstract Vector3d getDirection()
 	;
		    			
 		/**
         * 
			Current location of the projectile.
		 
         */
        public abstract Location getLocation()
 	;
		    			
 		/**
         * 
			Current velocity vector of the projectile.
		 
         */
        public abstract Velocity getVelocity()
 	;
		    			
 		/**
         * 
			Current speed of the projectile.
		 
         */
        public abstract double getSpeed()
 	;
		    			
 		/**
         * 
			Possition of the origin, when combined with direction can
			define the line of fire.
		 
         */
        public abstract Location getOrigin()
 	;
		    			
 		/**
         * 
			If the projectile has splash damage, how big it is – in ut
			units.
		 
         */
        public abstract double getDamageRadius()
 	;
		    			
 		/**
         * 
			The class of the projectile (so you know what is flying
			against you).
		 
         */
        public abstract String getType()
 	;
		    			
 		/**
         * 
			The class of the projectile (so you know what is flying
			against you).
		 
         */
        public abstract boolean isVisible()
 	;
		    			
		 	@Override
			public IWorldObjectUpdatedEvent createDisappearEvent() {
				return new ObjectDisappeared(this, getSimTime());
			}
			
			public static class ObjectDisappeared implements IWorldObjectUpdatedEvent
			{
				
				public ObjectDisappeared(IncomingProjectile obj, long time) {
					this.obj = obj;
					this.time = time;
				}
				
				private IncomingProjectile obj;
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
					if (!(obj instanceof IncomingProjectileMessage)) {
						throw new PogamutException("Can't update different class than IncomingProjectileMessage, got class " + obj.getClass().getSimpleName() + "!", this);
					}
					IncomingProjectileMessage toUpdate = (IncomingProjectileMessage)obj;
					if (toUpdate.Visible) {
						toUpdate.Visible = false;
						return new IWorldObjectUpdateResult.WorldObjectUpdateResult(IWorldObjectUpdateResult.Result.UPDATED, obj);
					} else {
						return new IWorldObjectUpdateResult.WorldObjectUpdateResult(IWorldObjectUpdateResult.Result.SAME, obj);
					}
				}
		
			}
	
		
    	
    	public static class IncomingProjectileUpdate
     extends GBObjectUpdate implements ICompositeWorldObjectUpdatedEvent, IGBWorldObjectEvent {	
			private IncomingProjectile object;
			private long time;
			private ITeamId teamId;
			
			public IncomingProjectileUpdate
    (IncomingProjectile source, long eventTime, ITeamId teamId) {
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
				return new IncomingProjectileLocalImpl.IncomingProjectileLocalUpdate
    ((IncomingProjectileLocal)object.getLocal(), time);
			}
	
			@Override
			public ISharedWorldObjectUpdatedEvent getSharedEvent() {
				return new IncomingProjectileSharedImpl.IncomingProjectileSharedUpdate
    ((IncomingProjectileShared)object.getShared(), time, teamId);
			}
	
			@Override
			public IStaticWorldObjectUpdatedEvent getStaticEvent() {
				return new IncomingProjectileStaticImpl.IncomingProjectileStaticUpdate
    ((IncomingProjectileStatic)object.getStatic(), time);
			}
			
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
 	
 	    public String toJsonLiteral() {
            return "incomingprojectile( "
            		+
									(getId()
 	 == null ? "null" :
										"\"" + getId()
 	.getStringId() + "\"" 
									)
								+ ", " + 
								    String.valueOf(getImpactTime()
 	)									
								+ ", " + 
									(getDirection()
 	 == null ? "null" :
										"[" + getDirection()
 	.getX() + ", " + getDirection()
 	.getY() + ", " + getDirection()
 	.getZ() + "]" 
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
								    String.valueOf(getSpeed()
 	)									
								+ ", " + 
								    (getOrigin()
 	 == null ? "null" :
										"[" + getOrigin()
 	.getX() + ", " + getOrigin()
 	.getY() + ", " + getOrigin()
 	.getZ() + "]" 
									)
								+ ", " + 
								    String.valueOf(getDamageRadius()
 	)									
								+ ", " + 
									(getType()
 	 == null ? "null" :
										"\"" + getType()
 	 + "\"" 
									)
								+ ", " + 
								    String.valueOf(isVisible()
 	)									
								
                   + ")";
        }
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=base+classtype[@name=abstract]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=base+classtype[@name=abstract]) ---        	            	
 	
		}
 	