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
             				Implementation of the GameBots2004 message PLR contains also its Local/Shared/Static subpart class definitions..  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Synchronous message. Contains information about other players in
		the game, like their current velocity, position, weapon and
		reachability. Only reports those players that are visible.
		(within field of view and not occluded).
	
         */
 	public class PlayerMessage   
  				extends 
  				Player
  						implements IWorldObjectUpdatedEvent, ICompositeWorldObjectUpdatedEvent
  						
	    {
 	
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public PlayerMessage()
		{
		}
	
    	
    	
    	
    	/**
		 * Creates new instance of the message Player.
		 * 
		Synchronous message. Contains information about other players in
		the game, like their current velocity, position, weapon and
		reachability. Only reports those players that are visible.
		(within field of view and not occluded).
	
		 * Corresponding GameBots message
		 *   
		 *   is
		 *   PLR.
		 * 
 	  	 * 
		 *   
		 *     @param Id Unique Id of the player.
		 *   
		 * 
		 *   
		 *     @param Jmx 
			Exported just for control server. Holds jmx address we need to connect to
            when we want to debug our bot.
		
		 *   
		 * 
		 *   
		 *     @param Name 
			Human readable name of the player.
		
		 *   
		 * 
		 *   
		 *     @param Spectator 
			Whether this player is in SPECTATE mode. Humans can change the mode during runtime!
		
		 *   
		 * 
		 *   
		 *     @param Action 
			Atomic action this bot is doing (BDI).
		
		 *   
		 * 
		 *   
		 *     @param Visible 
            If the player is in the field of view of the bot.
        
		 *   
		 * 
		 *   
		 *     @param Rotation 
			Which direction the player is facing in absolute terms.
		
		 *   
		 * 
		 *   
		 *     @param Location 
			An absolute location of the player within the map.
		
		 *   
		 * 
		 *   
		 *     @param Velocity 
			Absolute velocity of the player as a vector of movement per one
			game second.
		
		 *   
		 * 
		 *   
		 *     @param Team 
			What team the player is on. 255 is no team. 0-3 are red,
			blue, green, gold in that order.
		
		 *   
		 * 
		 *   
		 *     @param Weapon 
			Class of the weapon the player is holding. Weapon strings to
			look for include: "AssaultRifle", "ShieldGun", "FlakCannon",
			"BioRifle", "ShockRifle", "LinkGun", "SniperRifle",
			"RocketLauncher", "Minigun", "LightingGun", "Translocator".
			TODO: Look if this is all.
		
		 *   
		 * 
		 *   
		 *     @param Crouched 
			True if the bot is crouched.
		
		 *   
		 * 
		 *   
		 *     @param Firing 
			0 means is not firing, 1 - firing in primary mode, 2 -
			firing in secondary mode (alt firing).
		
		 *   
		 * 
		 *   
		 *     @param EmotLeft 
			For UE2. Holds left emoticon of the bot, "None" means none set.
		
		 *   
		 * 
		 *   
		 *     @param EmotCenter 
			For UE2. Holds center emoticon of the bot, "None" means none set.
		
		 *   
		 * 
		 *   
		 *     @param EmotRight 
			For UE2. Holds right emoticon of the bot, "None" means none set.
		
		 *   
		 * 
		 *   
		 *     @param Bubble 
			For UE2. Holds the bubble of the emoticon of the bot, "None" means none set.
		
		 *   
		 * 
		 *   
		 *     @param Anim 
			For UE2. Current played animation of the bot.
		
		 *   
		 * 
		 */
		public PlayerMessage(
			UnrealId Id,  String Jmx,  String Name,  Boolean Spectator,  String Action,  boolean Visible,  Rotation Rotation,  Location Location,  Velocity Velocity,  int Team,  String Weapon,  boolean Crouched,  int Firing,  String EmotLeft,  String EmotCenter,  String EmotRight,  String Bubble,  String Anim
		) {
			
					this.Id = Id;
				
					this.Jmx = Jmx;
				
					this.Name = Name;
				
					this.Spectator = Spectator;
				
					this.Action = Action;
				
					this.Visible = Visible;
				
					this.Rotation = Rotation;
				
					this.Location = Location;
				
					this.Velocity = Velocity;
				
					this.Team = Team;
				
					this.Weapon = Weapon;
				
					this.Crouched = Crouched;
				
					this.Firing = Firing;
				
					this.EmotLeft = EmotLeft;
				
					this.EmotCenter = EmotCenter;
				
					this.EmotRight = EmotRight;
				
					this.Bubble = Bubble;
				
					this.Anim = Anim;
				
		}
    
		/**
		 * Cloning constructor from the full message.
		 *
		 * @param original
		 */
		public PlayerMessage(PlayerMessage original) {		
			
					this.Id = original.getId()
 	;
				
					this.Jmx = original.getJmx()
 	;
				
					this.Name = original.getName()
 	;
				
					this.Spectator = original.isSpectator()
 	;
				
					this.Action = original.getAction()
 	;
				
					this.Visible = original.isVisible()
 	;
				
					this.Rotation = original.getRotation()
 	;
				
					this.Location = original.getLocation()
 	;
				
					this.Velocity = original.getVelocity()
 	;
				
					this.Team = original.getTeam()
 	;
				
					this.Weapon = original.getWeapon()
 	;
				
					this.Crouched = original.isCrouched()
 	;
				
					this.Firing = original.getFiring()
 	;
				
					this.EmotLeft = original.getEmotLeft()
 	;
				
					this.EmotCenter = original.getEmotCenter()
 	;
				
					this.EmotRight = original.getEmotRight()
 	;
				
					this.Bubble = original.getBubble()
 	;
				
					this.Anim = original.getAnim()
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
         * Unique Id of the player. 
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
         * Unique Id of the player. 
         */
        public  UnrealId getId()
 	 {
		    					return Id;
		    				}
		    			
    	
	    /**
         * 
			Exported just for control server. Holds jmx address we need to connect to
            when we want to debug our bot.
		 
         */
        protected
         String Jmx =
       	null;
	
    						
    						/**
		 					 * Whether property 'Jmx' was received from GB2004.
		 					 */
							protected boolean Jmx_Set = false;
							
    						@Override
		    				
 		/**
         * 
			Exported just for control server. Holds jmx address we need to connect to
            when we want to debug our bot.
		 
         */
        public  String getJmx()
 	 {
		    					return Jmx;
		    				}
		    			
    	
	    /**
         * 
			Human readable name of the player.
		 
         */
        protected
         String Name =
       	null;
	
    						
    						/**
		 					 * Whether property 'Name' was received from GB2004.
		 					 */
							protected boolean Name_Set = false;
							
    						@Override
		    				
 		/**
         * 
			Human readable name of the player.
		 
         */
        public  String getName()
 	 {
		    					return Name;
		    				}
		    			
    	
	    /**
         * 
			Whether this player is in SPECTATE mode. Humans can change the mode during runtime!
		 
         */
        protected
         Boolean Spectator =
       	false;
	
    						
    						/**
		 					 * Whether property 'Spectator' was received from GB2004.
		 					 */
							protected boolean Spectator_Set = false;
							
    						@Override
		    				
 		/**
         * 
			Whether this player is in SPECTATE mode. Humans can change the mode during runtime!
		 
         */
        public  Boolean isSpectator()
 	 {
		    					return Spectator;
		    				}
		    			
    	
	    /**
         * 
			Atomic action this bot is doing (BDI).
		 
         */
        protected
         String Action =
       	null;
	
    						
    						/**
		 					 * Whether property 'Action' was received from GB2004.
		 					 */
							protected boolean Action_Set = false;
							
    						@Override
		    				
 		/**
         * 
			Atomic action this bot is doing (BDI).
		 
         */
        public  String getAction()
 	 {
		    					return Action;
		    				}
		    			
    	
	    /**
         * 
            If the player is in the field of view of the bot.
         
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
            If the player is in the field of view of the bot.
         
         */
        public  boolean isVisible()
 	 {
		    					return Visible;
		    				}
		    			
    	
	    /**
         * 
			Which direction the player is facing in absolute terms.
		 
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
			Which direction the player is facing in absolute terms.
		 
         */
        public  Rotation getRotation()
 	 {
		    					return Rotation;
		    				}
		    			
    	
	    /**
         * 
			An absolute location of the player within the map.
		 
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
			An absolute location of the player within the map.
		 
         */
        public  Location getLocation()
 	 {
		    					return Location;
		    				}
		    			
    	
	    /**
         * 
			Absolute velocity of the player as a vector of movement per one
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
			Absolute velocity of the player as a vector of movement per one
			game second.
		 
         */
        public  Velocity getVelocity()
 	 {
		    					return Velocity;
		    				}
		    			
    	
	    /**
         * 
			What team the player is on. 255 is no team. 0-3 are red,
			blue, green, gold in that order.
		 
         */
        protected
         int Team =
       	0;
	
    						
    						/**
		 					 * Whether property 'Team' was received from GB2004.
		 					 */
							protected boolean Team_Set = false;
							
    						@Override
		    				
 		/**
         * 
			What team the player is on. 255 is no team. 0-3 are red,
			blue, green, gold in that order.
		 
         */
        public  int getTeam()
 	 {
		    					return Team;
		    				}
		    			
    	
	    /**
         * 
			Class of the weapon the player is holding. Weapon strings to
			look for include: "AssaultRifle", "ShieldGun", "FlakCannon",
			"BioRifle", "ShockRifle", "LinkGun", "SniperRifle",
			"RocketLauncher", "Minigun", "LightingGun", "Translocator".
			TODO: Look if this is all.
		 
         */
        protected
         String Weapon =
       	null;
	
    						
    						/**
		 					 * Whether property 'Weapon' was received from GB2004.
		 					 */
							protected boolean Weapon_Set = false;
							
    						@Override
		    				
 		/**
         * 
			Class of the weapon the player is holding. Weapon strings to
			look for include: "AssaultRifle", "ShieldGun", "FlakCannon",
			"BioRifle", "ShockRifle", "LinkGun", "SniperRifle",
			"RocketLauncher", "Minigun", "LightingGun", "Translocator".
			TODO: Look if this is all.
		 
         */
        public  String getWeapon()
 	 {
		    					return Weapon;
		    				}
		    			
    	
	    /**
         * 
			True if the bot is crouched.
		 
         */
        protected
         boolean Crouched =
       	false;
	
    						
    						/**
		 					 * Whether property 'Crouched' was received from GB2004.
		 					 */
							protected boolean Crouched_Set = false;
							
    						@Override
		    				
 		/**
         * 
			True if the bot is crouched.
		 
         */
        public  boolean isCrouched()
 	 {
		    					return Crouched;
		    				}
		    			
    	
	    /**
         * 
			0 means is not firing, 1 - firing in primary mode, 2 -
			firing in secondary mode (alt firing).
		 
         */
        protected
         int Firing =
       	0;
	
    						
    						/**
		 					 * Whether property 'Firing' was received from GB2004.
		 					 */
							protected boolean Firing_Set = false;
							
    						@Override
		    				
 		/**
         * 
			0 means is not firing, 1 - firing in primary mode, 2 -
			firing in secondary mode (alt firing).
		 
         */
        public  int getFiring()
 	 {
		    					return Firing;
		    				}
		    			
    	
	    /**
         * 
			For UE2. Holds left emoticon of the bot, "None" means none set.
		 
         */
        protected
         String EmotLeft =
       	null;
	
    						
    						/**
		 					 * Whether property 'EmotLeft' was received from GB2004.
		 					 */
							protected boolean EmotLeft_Set = false;
							
    						@Override
		    				
 		/**
         * 
			For UE2. Holds left emoticon of the bot, "None" means none set.
		 
         */
        public  String getEmotLeft()
 	 {
		    					return EmotLeft;
		    				}
		    			
    	
	    /**
         * 
			For UE2. Holds center emoticon of the bot, "None" means none set.
		 
         */
        protected
         String EmotCenter =
       	null;
	
    						
    						/**
		 					 * Whether property 'EmotCenter' was received from GB2004.
		 					 */
							protected boolean EmotCenter_Set = false;
							
    						@Override
		    				
 		/**
         * 
			For UE2. Holds center emoticon of the bot, "None" means none set.
		 
         */
        public  String getEmotCenter()
 	 {
		    					return EmotCenter;
		    				}
		    			
    	
	    /**
         * 
			For UE2. Holds right emoticon of the bot, "None" means none set.
		 
         */
        protected
         String EmotRight =
       	null;
	
    						
    						/**
		 					 * Whether property 'EmotRight' was received from GB2004.
		 					 */
							protected boolean EmotRight_Set = false;
							
    						@Override
		    				
 		/**
         * 
			For UE2. Holds right emoticon of the bot, "None" means none set.
		 
         */
        public  String getEmotRight()
 	 {
		    					return EmotRight;
		    				}
		    			
    	
	    /**
         * 
			For UE2. Holds the bubble of the emoticon of the bot, "None" means none set.
		 
         */
        protected
         String Bubble =
       	null;
	
    						
    						/**
		 					 * Whether property 'Bubble' was received from GB2004.
		 					 */
							protected boolean Bubble_Set = false;
							
    						@Override
		    				
 		/**
         * 
			For UE2. Holds the bubble of the emoticon of the bot, "None" means none set.
		 
         */
        public  String getBubble()
 	 {
		    					return Bubble;
		    				}
		    			
    	
	    /**
         * 
			For UE2. Current played animation of the bot.
		 
         */
        protected
         String Anim =
       	null;
	
    						
    						/**
		 					 * Whether property 'Anim' was received from GB2004.
		 					 */
							protected boolean Anim_Set = false;
							
    						@Override
		    				
 		/**
         * 
			For UE2. Current played animation of the bot.
		 
         */
        public  String getAnim()
 	 {
		    					return Anim;
		    				}
		    			
		    			
		    			private PlayerLocal localPart = null;
		    			
		    			@Override
						public PlayerLocal 
						getLocal() {
							if (localPart != null) return localPart;
							return localPart = new 
								PlayerLocalMessage();
						}
					
						private PlayerShared sharedPart = null;
					
						@Override
						public PlayerShared 
						getShared() {
							if (sharedPart != null) return sharedPart;							
							return sharedPart = new 
								PlayerSharedMessage();
						}
					
						private PlayerStatic staticPart = null; 
					
						@Override
						public PlayerStatic 
						getStatic() {
							if (staticPart != null) return staticPart;
							return staticPart = new 
								PlayerStaticMessage();
						}
    				
 		/**
         *  
            				Implementation of the local part of the GameBots2004 message PLR, used
            				to facade PLRMessage.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Synchronous message. Contains information about other players in
		the game, like their current velocity, position, weapon and
		reachability. Only reports those players that are visible.
		(within field of view and not occluded).
	
         */
 	public class PlayerLocalMessage 
	  					extends
  						PlayerLocal
	    {
 	
		    			@Override
		    			public 
		    			PlayerLocalMessage clone() {
		    				return this;
		    			}
		    			
		    				public PlayerLocalMessage getLocal() {
								return this;
					    	}
							public ISharedWorldObject getShared() {
							 	throw new UnsupportedOperationException("Could not return LOCAL as SHARED");
							}
							public IStaticWorldObject getStatic() {
							    throw new UnsupportedOperationException("Could not return LOCAL as STATIC");
							}
		    			
 		/**
         * Unique Id of the player. 
         */
        public  UnrealId getId()
 	 {
				    					return Id;
				    				}
				    			
 		/**
         * 
			Exported just for control server. Holds jmx address we need to connect to
            when we want to debug our bot.
		 
         */
        public  String getJmx()
 	 {
				    					return Jmx;
				    				}
				    			
 		/**
         * 
            If the player is in the field of view of the bot.
         
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
		              		
		              			"Jmx = " + String.valueOf(getJmx()
 	) + " | " + 
		              		
		              			"Visible = " + String.valueOf(isVisible()
 	) + " | " + 
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>Id</b> = " + String.valueOf(getId()
 	) + " <br/> " + 
		              		
		              			"<b>Jmx</b> = " + String.valueOf(getJmx()
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
            				Implementation of the static part of the GameBots2004 message PLR, used
            				to facade PLRMessage.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Synchronous message. Contains information about other players in
		the game, like their current velocity, position, weapon and
		reachability. Only reports those players that are visible.
		(within field of view and not occluded).
	
         */
 	public class PlayerStaticMessage 
	  					extends
  						PlayerStatic
	    {
 	
		    			@Override
		    			public 
		    			PlayerStaticMessage clone() {
		    				return this;
		    			}
		    			
 		/**
         * Unique Id of the player. 
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
 				PlayerStatic obj = (PlayerStatic) other;

 				
 						if ( !(
 	 			AdvancedEquals.equalsOrNull(this.getId()
 	, obj.getId()
 	)
 	 		) )
						{
							System.out.println("!!!!!PROPERTY UPDATE ERROR!!!! on property Id on object class PlayerStatic");
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
            				Implementation of the shared part of the GameBots2004 message PLR, used
            				to facade PLRMessage.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Synchronous message. Contains information about other players in
		the game, like their current velocity, position, weapon and
		reachability. Only reports those players that are visible.
		(within field of view and not occluded).
	
         */
 	public class PlayerSharedMessage 
	  					extends
  						PlayerShared
	    {
 	
    	
    	
		public PlayerSharedMessage()
		{
			
				propertyMap.put(myName.getPropertyId(), myName);
			
				propertyMap.put(mySpectator.getPropertyId(), mySpectator);
			
				propertyMap.put(myAction.getPropertyId(), myAction);
			
				propertyMap.put(myRotation.getPropertyId(), myRotation);
			
				propertyMap.put(myLocation.getPropertyId(), myLocation);
			
				propertyMap.put(myVelocity.getPropertyId(), myVelocity);
			
				propertyMap.put(myTeam.getPropertyId(), myTeam);
			
				propertyMap.put(myWeapon.getPropertyId(), myWeapon);
			
				propertyMap.put(myCrouched.getPropertyId(), myCrouched);
			
				propertyMap.put(myFiring.getPropertyId(), myFiring);
			
				propertyMap.put(myEmotLeft.getPropertyId(), myEmotLeft);
			
				propertyMap.put(myEmotCenter.getPropertyId(), myEmotCenter);
			
				propertyMap.put(myEmotRight.getPropertyId(), myEmotRight);
			
				propertyMap.put(myBubble.getPropertyId(), myBubble);
			
				propertyMap.put(myAnim.getPropertyId(), myAnim);
			
		}		
    
		    			@Override
		    			public 
		    			PlayerSharedMessage clone() {
		    				return this;
		    			}
		    			
		
		
		
		protected HashMap<PropertyId, ISharedProperty> propertyMap = new HashMap<PropertyId, ISharedProperty>(
			15
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
         * Unique Id of the player. 
         */
        public  UnrealId getId()
 	 {
  			return Id;
  		}
  		
    	
	    /**
         * 
			Human readable name of the player.
		 
         */
        protected
         StringProperty 
        myName
					= new
					StringProperty
					(
						getId(), 
						"Name", 
						Name, 
						Player.class
					);
					
 		/**
         * 
			Human readable name of the player.
		 
         */
        public  String getName()
 	 {
			  			return myName.getValue();
			  		}
				
    	
	    /**
         * 
			Whether this player is in SPECTATE mode. Humans can change the mode during runtime!
		 
         */
        protected
         BooleanProperty 
        mySpectator
					= new
					BooleanProperty
					(
						getId(), 
						"Spectator", 
						Spectator, 
						Player.class
					);
					
 		/**
         * 
			Whether this player is in SPECTATE mode. Humans can change the mode during runtime!
		 
         */
        public  Boolean isSpectator()
 	 {
			  			return mySpectator.getValue();
			  		}
				
    	
	    /**
         * 
			Atomic action this bot is doing (BDI).
		 
         */
        protected
         StringProperty 
        myAction
					= new
					StringProperty
					(
						getId(), 
						"Action", 
						Action, 
						Player.class
					);
					
 		/**
         * 
			Atomic action this bot is doing (BDI).
		 
         */
        public  String getAction()
 	 {
			  			return myAction.getValue();
			  		}
				
    	
	    /**
         * 
			Which direction the player is facing in absolute terms.
		 
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
						Player.class
					);
					
 		/**
         * 
			Which direction the player is facing in absolute terms.
		 
         */
        public  Rotation getRotation()
 	 {
			  			return myRotation.getValue();
			  		}
				
    	
	    /**
         * 
			An absolute location of the player within the map.
		 
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
						Player.class
					);
					
 		/**
         * 
			An absolute location of the player within the map.
		 
         */
        public  Location getLocation()
 	 {
			  			return myLocation.getValue();
			  		}
				
    	
	    /**
         * 
			Absolute velocity of the player as a vector of movement per one
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
						Player.class
					);
					
 		/**
         * 
			Absolute velocity of the player as a vector of movement per one
			game second.
		 
         */
        public  Velocity getVelocity()
 	 {
			  			return myVelocity.getValue();
			  		}
				
    	
	    /**
         * 
			What team the player is on. 255 is no team. 0-3 are red,
			blue, green, gold in that order.
		 
         */
        protected
         IntProperty 
        myTeam
					= new
					IntProperty
					(
						getId(), 
						"Team", 
						Team, 
						Player.class
					);
					
 		/**
         * 
			What team the player is on. 255 is no team. 0-3 are red,
			blue, green, gold in that order.
		 
         */
        public  int getTeam()
 	 {
			  			return myTeam.getValue();
			  		}
				
    	
	    /**
         * 
			Class of the weapon the player is holding. Weapon strings to
			look for include: "AssaultRifle", "ShieldGun", "FlakCannon",
			"BioRifle", "ShockRifle", "LinkGun", "SniperRifle",
			"RocketLauncher", "Minigun", "LightingGun", "Translocator".
			TODO: Look if this is all.
		 
         */
        protected
         StringProperty 
        myWeapon
					= new
					StringProperty
					(
						getId(), 
						"Weapon", 
						Weapon, 
						Player.class
					);
					
 		/**
         * 
			Class of the weapon the player is holding. Weapon strings to
			look for include: "AssaultRifle", "ShieldGun", "FlakCannon",
			"BioRifle", "ShockRifle", "LinkGun", "SniperRifle",
			"RocketLauncher", "Minigun", "LightingGun", "Translocator".
			TODO: Look if this is all.
		 
         */
        public  String getWeapon()
 	 {
			  			return myWeapon.getValue();
			  		}
				
    	
	    /**
         * 
			True if the bot is crouched.
		 
         */
        protected
         BooleanProperty 
        myCrouched
					= new
					BooleanProperty
					(
						getId(), 
						"Crouched", 
						Crouched, 
						Player.class
					);
					
 		/**
         * 
			True if the bot is crouched.
		 
         */
        public  boolean isCrouched()
 	 {
			  			return myCrouched.getValue();
			  		}
				
    	
	    /**
         * 
			0 means is not firing, 1 - firing in primary mode, 2 -
			firing in secondary mode (alt firing).
		 
         */
        protected
         IntProperty 
        myFiring
					= new
					IntProperty
					(
						getId(), 
						"Firing", 
						Firing, 
						Player.class
					);
					
 		/**
         * 
			0 means is not firing, 1 - firing in primary mode, 2 -
			firing in secondary mode (alt firing).
		 
         */
        public  int getFiring()
 	 {
			  			return myFiring.getValue();
			  		}
				
    	
	    /**
         * 
			For UE2. Holds left emoticon of the bot, "None" means none set.
		 
         */
        protected
         StringProperty 
        myEmotLeft
					= new
					StringProperty
					(
						getId(), 
						"EmotLeft", 
						EmotLeft, 
						Player.class
					);
					
 		/**
         * 
			For UE2. Holds left emoticon of the bot, "None" means none set.
		 
         */
        public  String getEmotLeft()
 	 {
			  			return myEmotLeft.getValue();
			  		}
				
    	
	    /**
         * 
			For UE2. Holds center emoticon of the bot, "None" means none set.
		 
         */
        protected
         StringProperty 
        myEmotCenter
					= new
					StringProperty
					(
						getId(), 
						"EmotCenter", 
						EmotCenter, 
						Player.class
					);
					
 		/**
         * 
			For UE2. Holds center emoticon of the bot, "None" means none set.
		 
         */
        public  String getEmotCenter()
 	 {
			  			return myEmotCenter.getValue();
			  		}
				
    	
	    /**
         * 
			For UE2. Holds right emoticon of the bot, "None" means none set.
		 
         */
        protected
         StringProperty 
        myEmotRight
					= new
					StringProperty
					(
						getId(), 
						"EmotRight", 
						EmotRight, 
						Player.class
					);
					
 		/**
         * 
			For UE2. Holds right emoticon of the bot, "None" means none set.
		 
         */
        public  String getEmotRight()
 	 {
			  			return myEmotRight.getValue();
			  		}
				
    	
	    /**
         * 
			For UE2. Holds the bubble of the emoticon of the bot, "None" means none set.
		 
         */
        protected
         StringProperty 
        myBubble
					= new
					StringProperty
					(
						getId(), 
						"Bubble", 
						Bubble, 
						Player.class
					);
					
 		/**
         * 
			For UE2. Holds the bubble of the emoticon of the bot, "None" means none set.
		 
         */
        public  String getBubble()
 	 {
			  			return myBubble.getValue();
			  		}
				
    	
	    /**
         * 
			For UE2. Current played animation of the bot.
		 
         */
        protected
         StringProperty 
        myAnim
					= new
					StringProperty
					(
						getId(), 
						"Anim", 
						Anim, 
						Player.class
					);
					
 		/**
         * 
			For UE2. Current played animation of the bot.
		 
         */
        public  String getAnim()
 	 {
			  			return myAnim.getValue();
			  		}
				
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
		              			"Id = " + String.valueOf(getId()
 	) + " | " + 
		              		
		              			"Name = " + String.valueOf(getName()
 	) + " | " + 
		              		
		              			"Spectator = " + String.valueOf(isSpectator()
 	) + " | " + 
		              		
		              			"Action = " + String.valueOf(getAction()
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
		              		
		              			"<b>Name</b> = " + String.valueOf(getName()
 	) + " <br/> " + 
		              		
		              			"<b>Spectator</b> = " + String.valueOf(isSpectator()
 	) + " <br/> " + 
		              		
		              			"<b>Action</b> = " + String.valueOf(getAction()
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
			if (!( object instanceof PlayerMessage) ) {
				throw new PogamutException("Can't update different class than PlayerMessage, got class " + object.getClass().getSimpleName() + "!", this);		
			}
			PlayerMessage toUpdate = (PlayerMessage)object;
			
			boolean updated = false;
			
			// UPDATING LOCAL PROPERTIES
			
				if (!SafeEquals.equals(toUpdate.Jmx, getJmx()
 	)) {
					toUpdate.Jmx=getJmx()
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
         	
				if (!SafeEquals.equals(toUpdate.Name, getName()
 	)) {
					toUpdate.Name=getName()
 	;
					updated = true;
				}
			
				if (!SafeEquals.equals(toUpdate.Spectator, isSpectator()
 	)) {
					toUpdate.Spectator=isSpectator()
 	;
					updated = true;
				}
			
				if (!SafeEquals.equals(toUpdate.Action, getAction()
 	)) {
					toUpdate.Action=getAction()
 	;
					updated = true;
				}
			
				if (!SafeEquals.equals(toUpdate.Rotation, getRotation()
 	)) {
					toUpdate.Rotation=getRotation()
 	;
					updated = true;
				}
			
     		if (getLocation()
 	 != null) {
     	
	            if (!SafeEquals.equals(toUpdate.Location, getLocation()
 	)) {
					toUpdate.Location=getLocation()
 	;
					updated = true;
				}
			
     		}
     	
				if (!SafeEquals.equals(toUpdate.Velocity, getVelocity()
 	)) {
					toUpdate.Velocity=getVelocity()
 	;
					updated = true;
				}
			
				if (toUpdate.Team != getTeam()
 	) {
				    toUpdate.Team=getTeam()
 	;
					updated = true;
				}
			
				if (!SafeEquals.equals(toUpdate.Weapon, getWeapon()
 	)) {
					toUpdate.Weapon=getWeapon()
 	;
					updated = true;
				}
			
				if (toUpdate.Crouched != isCrouched()
 	) {
				    toUpdate.Crouched=isCrouched()
 	;
					updated = true;
				}
			
				if (toUpdate.Firing != getFiring()
 	) {
				    toUpdate.Firing=getFiring()
 	;
					updated = true;
				}
			
				if (!SafeEquals.equals(toUpdate.EmotLeft, getEmotLeft()
 	)) {
					toUpdate.EmotLeft=getEmotLeft()
 	;
					updated = true;
				}
			
				if (!SafeEquals.equals(toUpdate.EmotCenter, getEmotCenter()
 	)) {
					toUpdate.EmotCenter=getEmotCenter()
 	;
					updated = true;
				}
			
				if (!SafeEquals.equals(toUpdate.EmotRight, getEmotRight()
 	)) {
					toUpdate.EmotRight=getEmotRight()
 	;
					updated = true;
				}
			
				if (!SafeEquals.equals(toUpdate.Bubble, getBubble()
 	)) {
					toUpdate.Bubble=getBubble()
 	;
					updated = true;
				}
			
				if (!SafeEquals.equals(toUpdate.Anim, getAnim()
 	)) {
					toUpdate.Anim=getAnim()
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
			return new PlayerLocalImpl.PlayerLocalUpdate
    (this.getLocal(), SimTime);
		}

		@Override
		public ISharedWorldObjectUpdatedEvent getSharedEvent() {
			return new PlayerSharedImpl.PlayerSharedUpdate
    (this.getShared(), SimTime, this.getTeamId());
		}

		@Override
		public IStaticWorldObjectUpdatedEvent getStaticEvent() {
			return new PlayerStaticImpl.PlayerStaticUpdate
    (this.getStatic(), SimTime);
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
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=base+classtype[@name=message]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=base+classtype[@name=message]) ---        	            	
 	
		}
 	