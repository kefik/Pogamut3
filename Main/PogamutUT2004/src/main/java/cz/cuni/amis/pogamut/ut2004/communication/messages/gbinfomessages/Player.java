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
            				Abstract definition of the GameBots2004 message PLR.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Synchronous message. Contains information about other players in
		the game, like their current velocity, position, weapon and
		reachability. Only reports those players that are visible.
		(within field of view and not occluded).
	
         */
 	public abstract class Player   
  				extends 
  				InfoMessage
  						implements IWorldEvent, IWorldChangeEvent, ICompositeWorldObject
  						
	    		,IPerson
	    		,cz.cuni.amis.pogamut.unreal.communication.messages.gbinfomessages.IPlayer
	    		,IGBViewable
	    {
 	
    	/** Example how the message looks like - used during parser tests. */
    	public static final String PROTOTYPE =
    		"PLR {Id unreal_id}  {Jmx text}  {Name text}  {Spectator False}  {Action text}  {Visible False}  {Rotation 0,0,0}  {Location 0,0,0}  {Velocity 0,0,0}  {Team 0}  {Weapon text}  {Crouched False}  {Firing 0}  {EmotLeft text}  {EmotCenter text}  {EmotRight text}  {Bubble text}  {Anim text} ";
    
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public Player()
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
         * Unique Id of the player. 
         */
        public abstract UnrealId getId()
 	;
		    			
 		/**
         * 
			Exported just for control server. Holds jmx address we need to connect to
            when we want to debug our bot.
		 
         */
        public abstract String getJmx()
 	;
		    			
 		/**
         * 
			Human readable name of the player.
		 
         */
        public abstract String getName()
 	;
		    			
 		/**
         * 
			Whether this player is in SPECTATE mode. Humans can change the mode during runtime!
		 
         */
        public abstract Boolean isSpectator()
 	;
		    			
 		/**
         * 
			Atomic action this bot is doing (BDI).
		 
         */
        public abstract String getAction()
 	;
		    			
 		/**
         * 
            If the player is in the field of view of the bot.
         
         */
        public abstract boolean isVisible()
 	;
		    			
 		/**
         * 
			Which direction the player is facing in absolute terms.
		 
         */
        public abstract Rotation getRotation()
 	;
		    			
 		/**
         * 
			An absolute location of the player within the map.
		 
         */
        public abstract Location getLocation()
 	;
		    			
 		/**
         * 
			Absolute velocity of the player as a vector of movement per one
			game second.
		 
         */
        public abstract Velocity getVelocity()
 	;
		    			
 		/**
         * 
			What team the player is on. 255 is no team. 0-3 are red,
			blue, green, gold in that order.
		 
         */
        public abstract int getTeam()
 	;
		    			
 		/**
         * 
			Class of the weapon the player is holding. Weapon strings to
			look for include: "AssaultRifle", "ShieldGun", "FlakCannon",
			"BioRifle", "ShockRifle", "LinkGun", "SniperRifle",
			"RocketLauncher", "Minigun", "LightingGun", "Translocator".
			TODO: Look if this is all.
		 
         */
        public abstract String getWeapon()
 	;
		    			
 		/**
         * 
			True if the bot is crouched.
		 
         */
        public abstract boolean isCrouched()
 	;
		    			
 		/**
         * 
			0 means is not firing, 1 - firing in primary mode, 2 -
			firing in secondary mode (alt firing).
		 
         */
        public abstract int getFiring()
 	;
		    			
 		/**
         * 
			For UE2. Holds left emoticon of the bot, "None" means none set.
		 
         */
        public abstract String getEmotLeft()
 	;
		    			
 		/**
         * 
			For UE2. Holds center emoticon of the bot, "None" means none set.
		 
         */
        public abstract String getEmotCenter()
 	;
		    			
 		/**
         * 
			For UE2. Holds right emoticon of the bot, "None" means none set.
		 
         */
        public abstract String getEmotRight()
 	;
		    			
 		/**
         * 
			For UE2. Holds the bubble of the emoticon of the bot, "None" means none set.
		 
         */
        public abstract String getBubble()
 	;
		    			
 		/**
         * 
			For UE2. Current played animation of the bot.
		 
         */
        public abstract String getAnim()
 	;
		    			
		 	@Override
			public IWorldObjectUpdatedEvent createDisappearEvent() {
				return new ObjectDisappeared(this, getSimTime());
			}
			
			public static class ObjectDisappeared implements IWorldObjectUpdatedEvent
			{
				
				public ObjectDisappeared(Player obj, long time) {
					this.obj = obj;
					this.time = time;
				}
				
				private Player obj;
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
					if (!(obj instanceof PlayerMessage)) {
						throw new PogamutException("Can't update different class than PlayerMessage, got class " + obj.getClass().getSimpleName() + "!", this);
					}
					PlayerMessage toUpdate = (PlayerMessage)obj;
					if (toUpdate.Visible) {
						toUpdate.Visible = false;
						return new IWorldObjectUpdateResult.WorldObjectUpdateResult(IWorldObjectUpdateResult.Result.UPDATED, obj);
					} else {
						return new IWorldObjectUpdateResult.WorldObjectUpdateResult(IWorldObjectUpdateResult.Result.SAME, obj);
					}
				}
		
			}
	
		
    	
    	public static class PlayerUpdate
     extends GBObjectUpdate implements ICompositeWorldObjectUpdatedEvent, IGBWorldObjectEvent {	
			private Player object;
			private long time;
			private ITeamId teamId;
			
			public PlayerUpdate
    (Player source, long eventTime, ITeamId teamId) {
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
				return new PlayerLocalImpl.PlayerLocalUpdate
    ((PlayerLocal)object.getLocal(), time);
			}
	
			@Override
			public ISharedWorldObjectUpdatedEvent getSharedEvent() {
				return new PlayerSharedImpl.PlayerSharedUpdate
    ((PlayerShared)object.getShared(), time, teamId);
			}
	
			@Override
			public IStaticWorldObjectUpdatedEvent getStaticEvent() {
				return new PlayerStaticImpl.PlayerStaticUpdate
    ((PlayerStatic)object.getStatic(), time);
			}
			
		}
    
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
		              			"Id = " + String.valueOf(getId()
 	) + " | " + 
		              		
		              			"Jmx = " + String.valueOf(getJmx()
 	) + " | " + 
		              		
		              			"Name = " + String.valueOf(getName()
 	) + " | " + 
		              		
		              			"Spectator = " + String.valueOf(isSpectator()
 	) + " | " + 
		              		
		              			"Action = " + String.valueOf(getAction()
 	) + " | " + 
		              		
		              			"Visible = " + String.valueOf(isVisible()
 	) + " | " + 
		              		
		              			"Rotation = " + String.valueOf(getRotation()
 	) + " | " + 
		              		
		              			"Location = " + String.valueOf(getLocation()
 	) + " | " + 
		              		
		              			"Velocity = " + String.valueOf(getVelocity()
 	) + " | " + 
		              		
		              			"Team = " + String.valueOf(getTeam()
 	) + " | " + 
		              		
		              			"Weapon = " + String.valueOf(getWeapon()
 	) + " | " + 
		              		
		              			"Crouched = " + String.valueOf(isCrouched()
 	) + " | " + 
		              		
		              			"Firing = " + String.valueOf(getFiring()
 	) + " | " + 
		              		
		              			"EmotLeft = " + String.valueOf(getEmotLeft()
 	) + " | " + 
		              		
		              			"EmotCenter = " + String.valueOf(getEmotCenter()
 	) + " | " + 
		              		
		              			"EmotRight = " + String.valueOf(getEmotRight()
 	) + " | " + 
		              		
		              			"Bubble = " + String.valueOf(getBubble()
 	) + " | " + 
		              		
		              			"Anim = " + String.valueOf(getAnim()
 	) + " | " + 
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>Id</b> = " + String.valueOf(getId()
 	) + " <br/> " + 
		              		
		              			"<b>Jmx</b> = " + String.valueOf(getJmx()
 	) + " <br/> " + 
		              		
		              			"<b>Name</b> = " + String.valueOf(getName()
 	) + " <br/> " + 
		              		
		              			"<b>Spectator</b> = " + String.valueOf(isSpectator()
 	) + " <br/> " + 
		              		
		              			"<b>Action</b> = " + String.valueOf(getAction()
 	) + " <br/> " + 
		              		
		              			"<b>Visible</b> = " + String.valueOf(isVisible()
 	) + " <br/> " + 
		              		
		              			"<b>Rotation</b> = " + String.valueOf(getRotation()
 	) + " <br/> " + 
		              		
		              			"<b>Location</b> = " + String.valueOf(getLocation()
 	) + " <br/> " + 
		              		
		              			"<b>Velocity</b> = " + String.valueOf(getVelocity()
 	) + " <br/> " + 
		              		
		              			"<b>Team</b> = " + String.valueOf(getTeam()
 	) + " <br/> " + 
		              		
		              			"<b>Weapon</b> = " + String.valueOf(getWeapon()
 	) + " <br/> " + 
		              		
		              			"<b>Crouched</b> = " + String.valueOf(isCrouched()
 	) + " <br/> " + 
		              		
		              			"<b>Firing</b> = " + String.valueOf(getFiring()
 	) + " <br/> " + 
		              		
		              			"<b>EmotLeft</b> = " + String.valueOf(getEmotLeft()
 	) + " <br/> " + 
		              		
		              			"<b>EmotCenter</b> = " + String.valueOf(getEmotCenter()
 	) + " <br/> " + 
		              		
		              			"<b>EmotRight</b> = " + String.valueOf(getEmotRight()
 	) + " <br/> " + 
		              		
		              			"<b>Bubble</b> = " + String.valueOf(getBubble()
 	) + " <br/> " + 
		              		
		              			"<b>Anim</b> = " + String.valueOf(getAnim()
 	) + " <br/> " + 
		              		
				"<br/>]";     
		}
 	
 	    public String toJsonLiteral() {
            return "player( "
            		+
									(getId()
 	 == null ? "null" :
										"\"" + getId()
 	.getStringId() + "\"" 
									)
								+ ", " + 
									(getJmx()
 	 == null ? "null" :
										"\"" + getJmx()
 	 + "\"" 
									)
								+ ", " + 
									(getName()
 	 == null ? "null" :
										"\"" + getName()
 	 + "\"" 
									)
								+ ", " + 
								    String.valueOf(isSpectator()
 	)									
								+ ", " + 
									(getAction()
 	 == null ? "null" :
										"\"" + getAction()
 	 + "\"" 
									)
								+ ", " + 
								    String.valueOf(isVisible()
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
								    String.valueOf(getTeam()
 	)									
								+ ", " + 
									(getWeapon()
 	 == null ? "null" :
										"\"" + getWeapon()
 	 + "\"" 
									)
								+ ", " + 
								    String.valueOf(isCrouched()
 	)									
								+ ", " + 
								    String.valueOf(getFiring()
 	)									
								+ ", " + 
									(getEmotLeft()
 	 == null ? "null" :
										"\"" + getEmotLeft()
 	 + "\"" 
									)
								+ ", " + 
									(getEmotCenter()
 	 == null ? "null" :
										"\"" + getEmotCenter()
 	 + "\"" 
									)
								+ ", " + 
									(getEmotRight()
 	 == null ? "null" :
										"\"" + getEmotRight()
 	 + "\"" 
									)
								+ ", " + 
									(getBubble()
 	 == null ? "null" :
										"\"" + getBubble()
 	 + "\"" 
									)
								+ ", " + 
									(getAnim()
 	 == null ? "null" :
										"\"" + getAnim()
 	 + "\"" 
									)
								
                   + ")";
        }
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=base+classtype[@name=abstract]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=base+classtype[@name=abstract]) ---        	            	
 	
		}
 	