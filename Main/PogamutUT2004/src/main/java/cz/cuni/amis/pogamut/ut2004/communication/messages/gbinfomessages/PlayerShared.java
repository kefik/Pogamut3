package cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages;
 		
 		// --- IMPORTS FROM /messages/settings/javasettings/javaimport BEGIN
			import java.util.*;import javax.vecmath.*;import cz.cuni.amis.pogamut.base.communication.messages.*;import cz.cuni.amis.pogamut.base.communication.worldview.*;import cz.cuni.amis.pogamut.base.communication.worldview.event.*;import cz.cuni.amis.pogamut.base.communication.worldview.object.*;import cz.cuni.amis.pogamut.multi.communication.worldview.object.*;import cz.cuni.amis.pogamut.base.communication.translator.event.*;import cz.cuni.amis.pogamut.multi.communication.translator.event.*;import cz.cuni.amis.pogamut.base3d.worldview.object.*;import cz.cuni.amis.pogamut.base3d.worldview.object.event.*;import cz.cuni.amis.pogamut.ut2004.communication.messages.*;import cz.cuni.amis.pogamut.ut2004.communication.worldview.objects.*;import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.objects.*;import cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor.*;import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType.Category;import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;import cz.cuni.amis.utils.exception.*;import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdateResult.Result;import cz.cuni.amis.utils.SafeEquals;import cz.cuni.amis.pogamut.base.agent.*;import cz.cuni.amis.pogamut.multi.agent.*;import cz.cuni.amis.pogamut.multi.communication.worldview.property.*;import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.property.*;import cz.cuni.amis.utils.token.*;import cz.cuni.amis.utils.*;
		// --- IMPORTS FROM /messages/settings/javasettings/javaimport END
		
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name='all'] BEGIN
				
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name='all'] END
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name=shared]+classtype[@name=abstract] BEGIN
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name=shared]+classtype[@name=abstract] END
    
 		/**
         *  
            				Abstract definition of the shared part of the GameBots2004 message PLR.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Synchronous message. Contains information about other players in
		the game, like their current velocity, position, weapon and
		reachability. Only reports those players that are visible.
		(within field of view and not occluded).
	
         */
 	public abstract class PlayerShared 
  						extends InfoMessage
  						implements ISharedWorldObject
  						
	    {
 	
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public PlayerShared()
		{
		}
		
				// abstract definition of the shared-part of the message, no more constructors is needed
			
	   		
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
	   	
	    						public static final Token NamePropertyToken = Tokens.get("Name");
	    					
	    						public static final Token SpectatorPropertyToken = Tokens.get("Spectator");
	    					
	    						public static final Token ActionPropertyToken = Tokens.get("Action");
	    					
	    						public static final Token RotationPropertyToken = Tokens.get("Rotation");
	    					
	    						public static final Token LocationPropertyToken = Tokens.get("Location");
	    					
	    						public static final Token VelocityPropertyToken = Tokens.get("Velocity");
	    					
	    						public static final Token TeamPropertyToken = Tokens.get("Team");
	    					
	    						public static final Token WeaponPropertyToken = Tokens.get("Weapon");
	    					
	    						public static final Token CrouchedPropertyToken = Tokens.get("Crouched");
	    					
	    						public static final Token FiringPropertyToken = Tokens.get("Firing");
	    					
	    						public static final Token EmotLeftPropertyToken = Tokens.get("EmotLeft");
	    					
	    						public static final Token EmotCenterPropertyToken = Tokens.get("EmotCenter");
	    					
	    						public static final Token EmotRightPropertyToken = Tokens.get("EmotRight");
	    					
	    						public static final Token BubblePropertyToken = Tokens.get("Bubble");
	    					
	    						public static final Token AnimPropertyToken = Tokens.get("Anim");
	    						
							
							public static final Set<Token> SharedPropertyTokens;
	
							static {
								Set<Token> tokens = new HashSet<Token>();
								
									tokens.add(NamePropertyToken);
								
									tokens.add(SpectatorPropertyToken);
								
									tokens.add(ActionPropertyToken);
								
									tokens.add(RotationPropertyToken);
								
									tokens.add(LocationPropertyToken);
								
									tokens.add(VelocityPropertyToken);
								
									tokens.add(TeamPropertyToken);
								
									tokens.add(WeaponPropertyToken);
								
									tokens.add(CrouchedPropertyToken);
								
									tokens.add(FiringPropertyToken);
								
									tokens.add(EmotLeftPropertyToken);
								
									tokens.add(EmotCenterPropertyToken);
								
									tokens.add(EmotRightPropertyToken);
								
									tokens.add(BubblePropertyToken);
								
									tokens.add(AnimPropertyToken);
								
								SharedPropertyTokens = Collections.unmodifiableSet(tokens);
							}
	    				
	    			
	    				@Override
		    			public abstract 
		    			PlayerShared clone();
		    			
						@Override
						public Class getCompositeClass() {
							return Player.class;
						}
	
						
		    			
 		/**
         * Unique Id of the player. 
         */
        public abstract UnrealId getId()
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
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=shared+classtype[@name=abstract]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=shared+classtype[@name=abstract]) ---        	            	
 	
		}
 	