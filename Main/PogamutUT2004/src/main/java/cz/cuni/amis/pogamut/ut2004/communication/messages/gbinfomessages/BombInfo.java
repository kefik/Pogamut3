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
            				Abstract definition of the GameBots2004 message BOM.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Synchronous message. BombInfo contains all info about the bomb
		in the BotBombingRun game mode. Is not sent in other game types.
	
         */
 	public abstract class BombInfo   
  				extends 
  				InfoMessage
  						implements IWorldEvent, IWorldChangeEvent, ICompositeWorldObject
  						
	    		,IGBViewable
	    {
 	
    	/** Example how the message looks like - used during parser tests. */
    	public static final String PROTOTYPE =
    		"BOM {Id unreal_id}  {Velocity 0,0,0}  {Location 0,0,0}  {Holder unreal_id}  {HolderTeam 0}  {Visible False}  {State text} ";
    
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public BombInfo()
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
			An unique Id for this bomb, assigned by the game.
		 
         */
        public abstract UnrealId getId()
 	;
		    			
 		/**
         * 
   		Current velocity of the bomb. TODO not sure if this actually does smthing
   	 
         */
        public abstract Velocity getVelocity()
 	;
		    			
 		/**
         * 
			An absolute location of the bomb (Sent if we can actually
			see the flag).
		 
         */
        public abstract Location getLocation()
 	;
		    			
 		/**
         * 
			Id of player/bot holding the bomb. (Sent if we can actually
			see the bomb and the bomb is being carried, or if the bomb
			is being carried by us).
		 
         */
        public abstract UnrealId getHolder()
 	;
		    			
 		/**
         * 
			The team of the current holder (if any).
		 
         */
        public abstract Integer getHolderTeam()
 	;
		    			
 		/**
         * True if the bot can see the bomb. 
         */
        public abstract boolean isVisible()
 	;
		    			
 		/**
         * 
			Represents the state the bomb is in. Can be "Held",
			"Dropped" or "Home".
		 
         */
        public abstract String getState()
 	;
		    			
		 	@Override
			public IWorldObjectUpdatedEvent createDisappearEvent() {
				return new ObjectDisappeared(this, getSimTime());
			}
			
			public static class ObjectDisappeared implements IWorldObjectUpdatedEvent
			{
				
				public ObjectDisappeared(BombInfo obj, long time) {
					this.obj = obj;
					this.time = time;
				}
				
				private BombInfo obj;
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
					if (!(obj instanceof BombInfoMessage)) {
						throw new PogamutException("Can't update different class than BombInfoMessage, got class " + obj.getClass().getSimpleName() + "!", this);
					}
					BombInfoMessage toUpdate = (BombInfoMessage)obj;
					if (toUpdate.Visible) {
						toUpdate.Visible = false;
						return new IWorldObjectUpdateResult.WorldObjectUpdateResult(IWorldObjectUpdateResult.Result.UPDATED, obj);
					} else {
						return new IWorldObjectUpdateResult.WorldObjectUpdateResult(IWorldObjectUpdateResult.Result.SAME, obj);
					}
				}
		
			}
	
		
    	
    	public static class BombInfoUpdate
     extends GBObjectUpdate implements ICompositeWorldObjectUpdatedEvent, IGBWorldObjectEvent {	
			private BombInfo object;
			private long time;
			private ITeamId teamId;
			
			public BombInfoUpdate
    (BombInfo source, long eventTime, ITeamId teamId) {
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
				return new BombInfoLocalImpl.BombInfoLocalUpdate
    ((BombInfoLocal)object.getLocal(), time);
			}
	
			@Override
			public ISharedWorldObjectUpdatedEvent getSharedEvent() {
				return new BombInfoSharedImpl.BombInfoSharedUpdate
    ((BombInfoShared)object.getShared(), time, teamId);
			}
	
			@Override
			public IStaticWorldObjectUpdatedEvent getStaticEvent() {
				return new BombInfoStaticImpl.BombInfoStaticUpdate
    ((BombInfoStatic)object.getStatic(), time);
			}
			
		}
    
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
		              			"Id = " + String.valueOf(getId()
 	) + " | " + 
		              		
		              			"Velocity = " + String.valueOf(getVelocity()
 	) + " | " + 
		              		
		              			"Location = " + String.valueOf(getLocation()
 	) + " | " + 
		              		
		              			"Holder = " + String.valueOf(getHolder()
 	) + " | " + 
		              		
		              			"HolderTeam = " + String.valueOf(getHolderTeam()
 	) + " | " + 
		              		
		              			"Visible = " + String.valueOf(isVisible()
 	) + " | " + 
		              		
		              			"State = " + String.valueOf(getState()
 	) + " | " + 
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>Id</b> = " + String.valueOf(getId()
 	) + " <br/> " + 
		              		
		              			"<b>Velocity</b> = " + String.valueOf(getVelocity()
 	) + " <br/> " + 
		              		
		              			"<b>Location</b> = " + String.valueOf(getLocation()
 	) + " <br/> " + 
		              		
		              			"<b>Holder</b> = " + String.valueOf(getHolder()
 	) + " <br/> " + 
		              		
		              			"<b>HolderTeam</b> = " + String.valueOf(getHolderTeam()
 	) + " <br/> " + 
		              		
		              			"<b>Visible</b> = " + String.valueOf(isVisible()
 	) + " <br/> " + 
		              		
		              			"<b>State</b> = " + String.valueOf(getState()
 	) + " <br/> " + 
		              		
				"<br/>]";     
		}
 	
 	    public String toJsonLiteral() {
            return "bombinfo( "
            		+
									(getId()
 	 == null ? "null" :
										"\"" + getId()
 	.getStringId() + "\"" 
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
								    (getLocation()
 	 == null ? "null" :
										"[" + getLocation()
 	.getX() + ", " + getLocation()
 	.getY() + ", " + getLocation()
 	.getZ() + "]" 
									)
								+ ", " + 
									(getHolder()
 	 == null ? "null" :
										"\"" + getHolder()
 	.getStringId() + "\"" 
									)
								+ ", " + 
								    String.valueOf(getHolderTeam()
 	)									
								+ ", " + 
								    String.valueOf(isVisible()
 	)									
								+ ", " + 
									(getState()
 	 == null ? "null" :
										"\"" + getState()
 	 + "\"" 
									)
								
                   + ")";
        }
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=base+classtype[@name=abstract]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=base+classtype[@name=abstract]) ---        	            	
 	
		}
 	