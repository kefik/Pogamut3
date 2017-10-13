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
            				Abstract definition of the GameBots2004 message NFO.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Asynchronous message. Sent as response to READY command.
		Information about the game. What type of game is it going to be,
		number of teams, maximum size of teams etc.
	
         */
 	public abstract class GameInfo   
  				extends 
  				InfoMessage
  						implements IWorldEvent, IWorldChangeEvent, ICompositeWorldObject
  						
	    {
 	
    	/** Example how the message looks like - used during parser tests. */
    	public static final String PROTOTYPE =
    		"NFO {Gametype text}  {Level text}  {WeaponStay False}  {TimeLimit 0}  {FragLimit 0}  {GoalTeamScore null}  {MaxTeams 0}  {MaxTeamSize 0}  {RedBaseLocation 0,0,0}  {BlueBaseLocation 0,0,0}  {FirstDomPointLocation 0,0,0}  {SecondDomPointLocation 0,0,0}  {GamePaused False}  {BotsPaused False}  {FactoryLocation 0,0,0}  {FactoryRadius 0}  {DisperserLocation 0,0,0}  {DisperserRadius 0}  {FactoryAdrenalineCount 0}  {FactorySpawnType text} ";
    
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public GameInfo()
		{
		}
	
				// abstract message, it does not have any more constructors				
			
						
						public static final UnrealId GameInfoId = UnrealId.get("GameInfoId");
					
						
						public UnrealId getId() {						
							return cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo.GameInfoId;
						}
					
	   		
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
			What you are playing (BotDeathMatch, BotTeamGame,
			BotCTFGame,BotDoubleDomination).
		 
         */
        public abstract String getGametype()
 	;
		    			
 		/**
         * Name of map in game. 
         */
        public abstract String getLevel()
 	;
		    			
 		/**
         * 
			If true respawned weapons will stay on the ground after picked up (but bot cannot pickup same weapon twice).
		 
         */
        public abstract boolean isWeaponStay()
 	;
		    			
 		/**
         * 
			Maximum time game will last (if tied at end may goe to
			"sudden death overtime" - depends on the game type).
		 
         */
        public abstract double getTimeLimit()
 	;
		    			
 		/**
         * 
			Number of kills needed to win game (BotDeathMatch only).
		 
         */
        public abstract int getFragLimit()
 	;
		    			
 		/**
         * 
			Number of points a team needs to win the game (BotTeamGame,
			BotCTFGame, BotDoubleDomination).
		 
         */
        public abstract long getGoalTeamScore()
 	;
		    			
 		/**
         * 
			Max number of teams. Valid team range will be 0 to (MaxTeams
			- 1) (BotTeamGame, BotCTFGame, BotDoubleDomination). Usually
			there will be two teams - 0 and 1.
		 
         */
        public abstract int getMaxTeams()
 	;
		    			
 		/**
         * 
			Max number of players per side (BotTeamGame, BotCTFGame,
			BotDoubleDomination).
		 
         */
        public abstract int getMaxTeamSize()
 	;
		    			
 		/**
         * 
			Location of the base spawning the red flag (team 0) (BotCTFGame).
		 
         */
        public abstract Location getRedBaseLocation()
 	;
		    			
 		/**
         * 
			Location of the base spawning the blue flag (team 1) (BotCTFGame).
		 
         */
        public abstract Location getBlueBaseLocation()
 	;
		    			
 		/**
         * 
			Location of the first dom point (BotDoubleDomination).
		 
         */
        public abstract Location getFirstDomPointLocation()
 	;
		    			
 		/**
         * 
			Location of the second dom point (BotDoubleDomination).
		 
         */
        public abstract Location getSecondDomPointLocation()
 	;
		    			
 		/**
         * 
			If the game is paused - nobody can move.
		 
         */
        public abstract boolean isGamePaused()
 	;
		    			
 		/**
         * 
			If the game is paused just for bots - human controlled
			players can normally move.
		 
         */
        public abstract boolean isBotsPaused()
 	;
		    			
 		/**
         * 
			Sent only in BotScenario game type. Location of the factory.
		 
         */
        public abstract Location getFactoryLocation()
 	;
		    			
 		/**
         * 
			Sent only in BotScenario game type. Radius of the factory.
		 
         */
        public abstract double getFactoryRadius()
 	;
		    			
 		/**
         * 
			Sent only in BotScenario game type. Location of the goal point where UDamagePack should be taken.
		 
         */
        public abstract Location getDisperserLocation()
 	;
		    			
 		/**
         * 
			Sent only in BotScenario game type. Radius of the disperser point.
		 
         */
        public abstract double getDisperserRadius()
 	;
		    			
 		/**
         * 
			Sent only in BotScenario game type. How much adrenaline we need to activate the factory.
		 
         */
        public abstract double getFactoryAdrenalineCount()
 	;
		    			
 		/**
         * 
			Sent only in BotScenario game type. Pickup class our factory spawns when used properly. Item will be spawned at the bot's location.
		 
         */
        public abstract String getFactorySpawnType()
 	;
		    			
    	
    	public static class GameInfoUpdate
     extends GBObjectUpdate implements ICompositeWorldObjectUpdatedEvent, IGBWorldObjectEvent {	
			private GameInfo object;
			private long time;
			private ITeamId teamId;
			
			public GameInfoUpdate
    (GameInfo source, long eventTime, ITeamId teamId) {
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
				return new GameInfoLocalImpl.GameInfoLocalUpdate
    ((GameInfoLocal)object.getLocal(), time);
			}
	
			@Override
			public ISharedWorldObjectUpdatedEvent getSharedEvent() {
				return new GameInfoSharedImpl.GameInfoSharedUpdate
    ((GameInfoShared)object.getShared(), time, teamId);
			}
	
			@Override
			public IStaticWorldObjectUpdatedEvent getStaticEvent() {
				return new GameInfoStaticImpl.GameInfoStaticUpdate
    ((GameInfoStatic)object.getStatic(), time);
			}
			
		}
    
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
		              			"Gametype = " + String.valueOf(getGametype()
 	) + " | " + 
		              		
		              			"Level = " + String.valueOf(getLevel()
 	) + " | " + 
		              		
		              			"WeaponStay = " + String.valueOf(isWeaponStay()
 	) + " | " + 
		              		
		              			"TimeLimit = " + String.valueOf(getTimeLimit()
 	) + " | " + 
		              		
		              			"FragLimit = " + String.valueOf(getFragLimit()
 	) + " | " + 
		              		
		              			"GoalTeamScore = " + String.valueOf(getGoalTeamScore()
 	) + " | " + 
		              		
		              			"MaxTeams = " + String.valueOf(getMaxTeams()
 	) + " | " + 
		              		
		              			"MaxTeamSize = " + String.valueOf(getMaxTeamSize()
 	) + " | " + 
		              		
		              			"RedBaseLocation = " + String.valueOf(getRedBaseLocation()
 	) + " | " + 
		              		
		              			"BlueBaseLocation = " + String.valueOf(getBlueBaseLocation()
 	) + " | " + 
		              		
		              			"FirstDomPointLocation = " + String.valueOf(getFirstDomPointLocation()
 	) + " | " + 
		              		
		              			"SecondDomPointLocation = " + String.valueOf(getSecondDomPointLocation()
 	) + " | " + 
		              		
		              			"GamePaused = " + String.valueOf(isGamePaused()
 	) + " | " + 
		              		
		              			"BotsPaused = " + String.valueOf(isBotsPaused()
 	) + " | " + 
		              		
		              			"FactoryLocation = " + String.valueOf(getFactoryLocation()
 	) + " | " + 
		              		
		              			"FactoryRadius = " + String.valueOf(getFactoryRadius()
 	) + " | " + 
		              		
		              			"DisperserLocation = " + String.valueOf(getDisperserLocation()
 	) + " | " + 
		              		
		              			"DisperserRadius = " + String.valueOf(getDisperserRadius()
 	) + " | " + 
		              		
		              			"FactoryAdrenalineCount = " + String.valueOf(getFactoryAdrenalineCount()
 	) + " | " + 
		              		
		              			"FactorySpawnType = " + String.valueOf(getFactorySpawnType()
 	) + " | " + 
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>Gametype</b> = " + String.valueOf(getGametype()
 	) + " <br/> " + 
		              		
		              			"<b>Level</b> = " + String.valueOf(getLevel()
 	) + " <br/> " + 
		              		
		              			"<b>WeaponStay</b> = " + String.valueOf(isWeaponStay()
 	) + " <br/> " + 
		              		
		              			"<b>TimeLimit</b> = " + String.valueOf(getTimeLimit()
 	) + " <br/> " + 
		              		
		              			"<b>FragLimit</b> = " + String.valueOf(getFragLimit()
 	) + " <br/> " + 
		              		
		              			"<b>GoalTeamScore</b> = " + String.valueOf(getGoalTeamScore()
 	) + " <br/> " + 
		              		
		              			"<b>MaxTeams</b> = " + String.valueOf(getMaxTeams()
 	) + " <br/> " + 
		              		
		              			"<b>MaxTeamSize</b> = " + String.valueOf(getMaxTeamSize()
 	) + " <br/> " + 
		              		
		              			"<b>RedBaseLocation</b> = " + String.valueOf(getRedBaseLocation()
 	) + " <br/> " + 
		              		
		              			"<b>BlueBaseLocation</b> = " + String.valueOf(getBlueBaseLocation()
 	) + " <br/> " + 
		              		
		              			"<b>FirstDomPointLocation</b> = " + String.valueOf(getFirstDomPointLocation()
 	) + " <br/> " + 
		              		
		              			"<b>SecondDomPointLocation</b> = " + String.valueOf(getSecondDomPointLocation()
 	) + " <br/> " + 
		              		
		              			"<b>GamePaused</b> = " + String.valueOf(isGamePaused()
 	) + " <br/> " + 
		              		
		              			"<b>BotsPaused</b> = " + String.valueOf(isBotsPaused()
 	) + " <br/> " + 
		              		
		              			"<b>FactoryLocation</b> = " + String.valueOf(getFactoryLocation()
 	) + " <br/> " + 
		              		
		              			"<b>FactoryRadius</b> = " + String.valueOf(getFactoryRadius()
 	) + " <br/> " + 
		              		
		              			"<b>DisperserLocation</b> = " + String.valueOf(getDisperserLocation()
 	) + " <br/> " + 
		              		
		              			"<b>DisperserRadius</b> = " + String.valueOf(getDisperserRadius()
 	) + " <br/> " + 
		              		
		              			"<b>FactoryAdrenalineCount</b> = " + String.valueOf(getFactoryAdrenalineCount()
 	) + " <br/> " + 
		              		
		              			"<b>FactorySpawnType</b> = " + String.valueOf(getFactorySpawnType()
 	) + " <br/> " + 
		              		
				"<br/>]";     
		}
 	
 	    public String toJsonLiteral() {
            return "gameinfo( "
            		+
									(getGametype()
 	 == null ? "null" :
										"\"" + getGametype()
 	 + "\"" 
									)
								+ ", " + 
									(getLevel()
 	 == null ? "null" :
										"\"" + getLevel()
 	 + "\"" 
									)
								+ ", " + 
								    String.valueOf(isWeaponStay()
 	)									
								+ ", " + 
								    String.valueOf(getTimeLimit()
 	)									
								+ ", " + 
								    String.valueOf(getFragLimit()
 	)									
								+ ", " + 
								    String.valueOf(getGoalTeamScore()
 	)									
								+ ", " + 
								    String.valueOf(getMaxTeams()
 	)									
								+ ", " + 
								    String.valueOf(getMaxTeamSize()
 	)									
								+ ", " + 
								    (getRedBaseLocation()
 	 == null ? "null" :
										"[" + getRedBaseLocation()
 	.getX() + ", " + getRedBaseLocation()
 	.getY() + ", " + getRedBaseLocation()
 	.getZ() + "]" 
									)
								+ ", " + 
								    (getBlueBaseLocation()
 	 == null ? "null" :
										"[" + getBlueBaseLocation()
 	.getX() + ", " + getBlueBaseLocation()
 	.getY() + ", " + getBlueBaseLocation()
 	.getZ() + "]" 
									)
								+ ", " + 
								    (getFirstDomPointLocation()
 	 == null ? "null" :
										"[" + getFirstDomPointLocation()
 	.getX() + ", " + getFirstDomPointLocation()
 	.getY() + ", " + getFirstDomPointLocation()
 	.getZ() + "]" 
									)
								+ ", " + 
								    (getSecondDomPointLocation()
 	 == null ? "null" :
										"[" + getSecondDomPointLocation()
 	.getX() + ", " + getSecondDomPointLocation()
 	.getY() + ", " + getSecondDomPointLocation()
 	.getZ() + "]" 
									)
								+ ", " + 
								    String.valueOf(isGamePaused()
 	)									
								+ ", " + 
								    String.valueOf(isBotsPaused()
 	)									
								+ ", " + 
								    (getFactoryLocation()
 	 == null ? "null" :
										"[" + getFactoryLocation()
 	.getX() + ", " + getFactoryLocation()
 	.getY() + ", " + getFactoryLocation()
 	.getZ() + "]" 
									)
								+ ", " + 
								    String.valueOf(getFactoryRadius()
 	)									
								+ ", " + 
								    (getDisperserLocation()
 	 == null ? "null" :
										"[" + getDisperserLocation()
 	.getX() + ", " + getDisperserLocation()
 	.getY() + ", " + getDisperserLocation()
 	.getZ() + "]" 
									)
								+ ", " + 
								    String.valueOf(getDisperserRadius()
 	)									
								+ ", " + 
								    String.valueOf(getFactoryAdrenalineCount()
 	)									
								+ ", " + 
									(getFactorySpawnType()
 	 == null ? "null" :
										"\"" + getFactorySpawnType()
 	 + "\"" 
									)
								
                   + ")";
        }
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=base+classtype[@name=abstract]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=base+classtype[@name=abstract]) ---        	            	
 	
		}
 	