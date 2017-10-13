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
            				Abstract definition of the GameBots2004 message FLG.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Synchronous message. FlagInfo contains all info about the flag
		in the CTF game mode. Is not sent in other game types.
	
         */
 	public abstract class FlagInfo   
  				extends 
  				InfoMessage
  						implements IWorldEvent, IWorldChangeEvent, ICompositeWorldObject
  						
	    		,IGBViewable
	    {
 	
    	/** Example how the message looks like - used during parser tests. */
    	public static final String PROTOTYPE =
    		"FLG {Id unreal_id}  {Location 0,0,0}  {Holder unreal_id}  {Team 0}  {Visible False}  {State text} ";
    
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public FlagInfo()
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
			An unique Id for this flag, assigned by the game.
		 
         */
        public abstract UnrealId getId()
 	;
		    			
 		/**
         * 
			An absolute location of the flag (Sent if we can actually
			see the flag).
		 
         */
        public abstract Location getLocation()
 	;
		    			
 		/**
         * 
			Id of player/bot holding the flag. (Sent if we can actually
			see the flag and the flag is being carried, or if the flag
			is being carried by us).
		 
         */
        public abstract UnrealId getHolder()
 	;
		    			
 		/**
         * The owner team of this flag. 
         */
        public abstract Integer getTeam()
 	;
		    			
 		/**
         * True if the bot can see the flag. 
         */
        public abstract boolean isVisible()
 	;
		    			
 		/**
         * 
			Represents the state the flag is in. Can be "Held",
			"Dropped" or "Home" (note that the first letter does not have to be in upper case!).
		 
         */
        public abstract String getState()
 	;
		    			
		 	@Override
			public IWorldObjectUpdatedEvent createDisappearEvent() {
				return new ObjectDisappeared(this, getSimTime());
			}
			
			public static class ObjectDisappeared implements IWorldObjectUpdatedEvent
			{
				
				public ObjectDisappeared(FlagInfo obj, long time) {
					this.obj = obj;
					this.time = time;
				}
				
				private FlagInfo obj;
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
					if (!(obj instanceof FlagInfoMessage)) {
						throw new PogamutException("Can't update different class than FlagInfoMessage, got class " + obj.getClass().getSimpleName() + "!", this);
					}
					FlagInfoMessage toUpdate = (FlagInfoMessage)obj;
					if (toUpdate.Visible) {
						toUpdate.Visible = false;
						return new IWorldObjectUpdateResult.WorldObjectUpdateResult(IWorldObjectUpdateResult.Result.UPDATED, obj);
					} else {
						return new IWorldObjectUpdateResult.WorldObjectUpdateResult(IWorldObjectUpdateResult.Result.SAME, obj);
					}
				}
		
			}
	
		
    	
    	public static class FlagInfoUpdate
     extends GBObjectUpdate implements ICompositeWorldObjectUpdatedEvent, IGBWorldObjectEvent {	
			private FlagInfo object;
			private long time;
			private ITeamId teamId;
			
			public FlagInfoUpdate
    (FlagInfo source, long eventTime, ITeamId teamId) {
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
				return new FlagInfoLocalImpl.FlagInfoLocalUpdate
    ((FlagInfoLocal)object.getLocal(), time);
			}
	
			@Override
			public ISharedWorldObjectUpdatedEvent getSharedEvent() {
				return new FlagInfoSharedImpl.FlagInfoSharedUpdate
    ((FlagInfoShared)object.getShared(), time, teamId);
			}
	
			@Override
			public IStaticWorldObjectUpdatedEvent getStaticEvent() {
				return new FlagInfoStaticImpl.FlagInfoStaticUpdate
    ((FlagInfoStatic)object.getStatic(), time);
			}
			
		}
    
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
		              			"Id = " + String.valueOf(getId()
 	) + " | " + 
		              		
		              			"Location = " + String.valueOf(getLocation()
 	) + " | " + 
		              		
		              			"Holder = " + String.valueOf(getHolder()
 	) + " | " + 
		              		
		              			"Team = " + String.valueOf(getTeam()
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
		              		
		              			"<b>Location</b> = " + String.valueOf(getLocation()
 	) + " <br/> " + 
		              		
		              			"<b>Holder</b> = " + String.valueOf(getHolder()
 	) + " <br/> " + 
		              		
		              			"<b>Team</b> = " + String.valueOf(getTeam()
 	) + " <br/> " + 
		              		
		              			"<b>Visible</b> = " + String.valueOf(isVisible()
 	) + " <br/> " + 
		              		
		              			"<b>State</b> = " + String.valueOf(getState()
 	) + " <br/> " + 
		              		
				"<br/>]";     
		}
 	
 	    public String toJsonLiteral() {
            return "flaginfo( "
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
									(getHolder()
 	 == null ? "null" :
										"\"" + getHolder()
 	.getStringId() + "\"" 
									)
								+ ", " + 
								    String.valueOf(getTeam()
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
 	