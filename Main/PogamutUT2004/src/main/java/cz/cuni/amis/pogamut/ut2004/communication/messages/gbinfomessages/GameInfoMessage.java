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
             				Implementation of the GameBots2004 message NFO contains also its Local/Shared/Static subpart class definitions..  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Asynchronous message. Sent as response to READY command.
		Information about the game. What type of game is it going to be,
		number of teams, maximum size of teams etc.
	
         */
 	public class GameInfoMessage   
  				extends 
  				GameInfo
  						implements IWorldObjectUpdatedEvent, ICompositeWorldObjectUpdatedEvent
  						
	    {
 	
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public GameInfoMessage()
		{
		}
	
    	
    	
    	
    	/**
		 * Creates new instance of the message GameInfo.
		 * 
		Asynchronous message. Sent as response to READY command.
		Information about the game. What type of game is it going to be,
		number of teams, maximum size of teams etc.
	
		 * Corresponding GameBots message
		 *   
		 *   is
		 *   NFO.
		 * 
 	  	 * 
		 *   
		 *     @param Gametype 
			What you are playing (BotDeathMatch, BotTeamGame,
			BotCTFGame,BotDoubleDomination).
		
		 *   
		 * 
		 *   
		 *     @param Level Name of map in game.
		 *   
		 * 
		 *   
		 *     @param WeaponStay 
			If true respawned weapons will stay on the ground after picked up (but bot cannot pickup same weapon twice).
		
		 *   
		 * 
		 *   
		 *     @param TimeLimit 
			Maximum time game will last (if tied at end may goe to
			"sudden death overtime" - depends on the game type).
		
		 *   
		 * 
		 *   
		 *     @param FragLimit 
			Number of kills needed to win game (BotDeathMatch only).
		
		 *   
		 * 
		 *   
		 *     @param GoalTeamScore 
			Number of points a team needs to win the game (BotTeamGame,
			BotCTFGame, BotDoubleDomination).
		
		 *   
		 * 
		 *   
		 *     @param MaxTeams 
			Max number of teams. Valid team range will be 0 to (MaxTeams
			- 1) (BotTeamGame, BotCTFGame, BotDoubleDomination). Usually
			there will be two teams - 0 and 1.
		
		 *   
		 * 
		 *   
		 *     @param MaxTeamSize 
			Max number of players per side (BotTeamGame, BotCTFGame,
			BotDoubleDomination).
		
		 *   
		 * 
		 *   
		 *     @param RedBaseLocation 
			Location of the base spawning the red flag (team 0) (BotCTFGame).
		
		 *   
		 * 
		 *   
		 *     @param BlueBaseLocation 
			Location of the base spawning the blue flag (team 1) (BotCTFGame).
		
		 *   
		 * 
		 *   
		 *     @param FirstDomPointLocation 
			Location of the first dom point (BotDoubleDomination).
		
		 *   
		 * 
		 *   
		 *     @param SecondDomPointLocation 
			Location of the second dom point (BotDoubleDomination).
		
		 *   
		 * 
		 *   
		 *     @param GamePaused 
			If the game is paused - nobody can move.
		
		 *   
		 * 
		 *   
		 *     @param BotsPaused 
			If the game is paused just for bots - human controlled
			players can normally move.
		
		 *   
		 * 
		 *   
		 *     @param FactoryLocation 
			Sent only in BotScenario game type. Location of the factory.
		
		 *   
		 * 
		 *   
		 *     @param FactoryRadius 
			Sent only in BotScenario game type. Radius of the factory.
		
		 *   
		 * 
		 *   
		 *     @param DisperserLocation 
			Sent only in BotScenario game type. Location of the goal point where UDamagePack should be taken.
		
		 *   
		 * 
		 *   
		 *     @param DisperserRadius 
			Sent only in BotScenario game type. Radius of the disperser point.
		
		 *   
		 * 
		 *   
		 *     @param FactoryAdrenalineCount 
			Sent only in BotScenario game type. How much adrenaline we need to activate the factory.
		
		 *   
		 * 
		 *   
		 *     @param FactorySpawnType 
			Sent only in BotScenario game type. Pickup class our factory spawns when used properly. Item will be spawned at the bot's location.
		
		 *   
		 * 
		 */
		public GameInfoMessage(
			String Gametype,  String Level,  boolean WeaponStay,  double TimeLimit,  int FragLimit,  long GoalTeamScore,  int MaxTeams,  int MaxTeamSize,  Location RedBaseLocation,  Location BlueBaseLocation,  Location FirstDomPointLocation,  Location SecondDomPointLocation,  boolean GamePaused,  boolean BotsPaused,  Location FactoryLocation,  double FactoryRadius,  Location DisperserLocation,  double DisperserRadius,  double FactoryAdrenalineCount,  String FactorySpawnType
		) {
			
					this.Gametype = Gametype;
				
					this.Level = Level;
				
					this.WeaponStay = WeaponStay;
				
					this.TimeLimit = TimeLimit;
				
					this.FragLimit = FragLimit;
				
					this.GoalTeamScore = GoalTeamScore;
				
					this.MaxTeams = MaxTeams;
				
					this.MaxTeamSize = MaxTeamSize;
				
					this.RedBaseLocation = RedBaseLocation;
				
					this.BlueBaseLocation = BlueBaseLocation;
				
					this.FirstDomPointLocation = FirstDomPointLocation;
				
					this.SecondDomPointLocation = SecondDomPointLocation;
				
					this.GamePaused = GamePaused;
				
					this.BotsPaused = BotsPaused;
				
					this.FactoryLocation = FactoryLocation;
				
					this.FactoryRadius = FactoryRadius;
				
					this.DisperserLocation = DisperserLocation;
				
					this.DisperserRadius = DisperserRadius;
				
					this.FactoryAdrenalineCount = FactoryAdrenalineCount;
				
					this.FactorySpawnType = FactorySpawnType;
				
		}
    
		/**
		 * Cloning constructor from the full message.
		 *
		 * @param original
		 */
		public GameInfoMessage(GameInfoMessage original) {		
			
					this.Gametype = original.getGametype()
 	;
				
					this.Level = original.getLevel()
 	;
				
					this.WeaponStay = original.isWeaponStay()
 	;
				
					this.TimeLimit = original.getTimeLimit()
 	;
				
					this.FragLimit = original.getFragLimit()
 	;
				
					this.GoalTeamScore = original.getGoalTeamScore()
 	;
				
					this.MaxTeams = original.getMaxTeams()
 	;
				
					this.MaxTeamSize = original.getMaxTeamSize()
 	;
				
					this.RedBaseLocation = original.getRedBaseLocation()
 	;
				
					this.BlueBaseLocation = original.getBlueBaseLocation()
 	;
				
					this.FirstDomPointLocation = original.getFirstDomPointLocation()
 	;
				
					this.SecondDomPointLocation = original.getSecondDomPointLocation()
 	;
				
					this.GamePaused = original.isGamePaused()
 	;
				
					this.BotsPaused = original.isBotsPaused()
 	;
				
					this.FactoryLocation = original.getFactoryLocation()
 	;
				
					this.FactoryRadius = original.getFactoryRadius()
 	;
				
					this.DisperserLocation = original.getDisperserLocation()
 	;
				
					this.DisperserRadius = original.getDisperserRadius()
 	;
				
					this.FactoryAdrenalineCount = original.getFactoryAdrenalineCount()
 	;
				
					this.FactorySpawnType = original.getFactorySpawnType()
 	;
				
				this.TeamId = original.getTeamId();
			
			this.SimTime = original.getSimTime();
		}
		
						
						public UnrealId getId() {						
							return cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo.GameInfoId;
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
         * 
			What you are playing (BotDeathMatch, BotTeamGame,
			BotCTFGame,BotDoubleDomination).
		 
         */
        protected
         String Gametype =
       	null;
	
    						
    						/**
		 					 * Whether property 'Gametype' was received from GB2004.
		 					 */
							protected boolean Gametype_Set = false;
							
    						@Override
		    				
 		/**
         * 
			What you are playing (BotDeathMatch, BotTeamGame,
			BotCTFGame,BotDoubleDomination).
		 
         */
        public  String getGametype()
 	 {
		    					return Gametype;
		    				}
		    			
    	
	    /**
         * Name of map in game. 
         */
        protected
         String Level =
       	null;
	
    						
    						/**
		 					 * Whether property 'Level' was received from GB2004.
		 					 */
							protected boolean Level_Set = false;
							
    						@Override
		    				
 		/**
         * Name of map in game. 
         */
        public  String getLevel()
 	 {
		    					return Level;
		    				}
		    			
    	
	    /**
         * 
			If true respawned weapons will stay on the ground after picked up (but bot cannot pickup same weapon twice).
		 
         */
        protected
         boolean WeaponStay =
       	false;
	
    						
    						/**
		 					 * Whether property 'WeaponStay' was received from GB2004.
		 					 */
							protected boolean WeaponStay_Set = false;
							
    						@Override
		    				
 		/**
         * 
			If true respawned weapons will stay on the ground after picked up (but bot cannot pickup same weapon twice).
		 
         */
        public  boolean isWeaponStay()
 	 {
		    					return WeaponStay;
		    				}
		    			
    	
	    /**
         * 
			Maximum time game will last (if tied at end may goe to
			"sudden death overtime" - depends on the game type).
		 
         */
        protected
         double TimeLimit =
       	0;
	
    						
    						/**
		 					 * Whether property 'TimeLimit' was received from GB2004.
		 					 */
							protected boolean TimeLimit_Set = false;
							
    						@Override
		    				
 		/**
         * 
			Maximum time game will last (if tied at end may goe to
			"sudden death overtime" - depends on the game type).
		 
         */
        public  double getTimeLimit()
 	 {
		    					return TimeLimit;
		    				}
		    			
    	
	    /**
         * 
			Number of kills needed to win game (BotDeathMatch only).
		 
         */
        protected
         int FragLimit =
       	0;
	
    						
    						/**
		 					 * Whether property 'FragLimit' was received from GB2004.
		 					 */
							protected boolean FragLimit_Set = false;
							
    						@Override
		    				
 		/**
         * 
			Number of kills needed to win game (BotDeathMatch only).
		 
         */
        public  int getFragLimit()
 	 {
		    					return FragLimit;
		    				}
		    			
    	
	    /**
         * 
			Number of points a team needs to win the game (BotTeamGame,
			BotCTFGame, BotDoubleDomination).
		 
         */
        protected
         long GoalTeamScore =
       	0;
	
    						
    						/**
		 					 * Whether property 'GoalTeamScore' was received from GB2004.
		 					 */
							protected boolean GoalTeamScore_Set = false;
							
    						@Override
		    				
 		/**
         * 
			Number of points a team needs to win the game (BotTeamGame,
			BotCTFGame, BotDoubleDomination).
		 
         */
        public  long getGoalTeamScore()
 	 {
		    					return GoalTeamScore;
		    				}
		    			
    	
	    /**
         * 
			Max number of teams. Valid team range will be 0 to (MaxTeams
			- 1) (BotTeamGame, BotCTFGame, BotDoubleDomination). Usually
			there will be two teams - 0 and 1.
		 
         */
        protected
         int MaxTeams =
       	0;
	
    						
    						/**
		 					 * Whether property 'MaxTeams' was received from GB2004.
		 					 */
							protected boolean MaxTeams_Set = false;
							
    						@Override
		    				
 		/**
         * 
			Max number of teams. Valid team range will be 0 to (MaxTeams
			- 1) (BotTeamGame, BotCTFGame, BotDoubleDomination). Usually
			there will be two teams - 0 and 1.
		 
         */
        public  int getMaxTeams()
 	 {
		    					return MaxTeams;
		    				}
		    			
    	
	    /**
         * 
			Max number of players per side (BotTeamGame, BotCTFGame,
			BotDoubleDomination).
		 
         */
        protected
         int MaxTeamSize =
       	0;
	
    						
    						/**
		 					 * Whether property 'MaxTeamSize' was received from GB2004.
		 					 */
							protected boolean MaxTeamSize_Set = false;
							
    						@Override
		    				
 		/**
         * 
			Max number of players per side (BotTeamGame, BotCTFGame,
			BotDoubleDomination).
		 
         */
        public  int getMaxTeamSize()
 	 {
		    					return MaxTeamSize;
		    				}
		    			
    	
	    /**
         * 
			Location of the base spawning the red flag (team 0) (BotCTFGame).
		 
         */
        protected
         Location RedBaseLocation =
       	null;
	
    						
    						/**
		 					 * Whether property 'RedBaseLocation' was received from GB2004.
		 					 */
							protected boolean RedBaseLocation_Set = false;
							
    						@Override
		    				
 		/**
         * 
			Location of the base spawning the red flag (team 0) (BotCTFGame).
		 
         */
        public  Location getRedBaseLocation()
 	 {
		    					return RedBaseLocation;
		    				}
		    			
    	
	    /**
         * 
			Location of the base spawning the blue flag (team 1) (BotCTFGame).
		 
         */
        protected
         Location BlueBaseLocation =
       	null;
	
    						
    						/**
		 					 * Whether property 'BlueBaseLocation' was received from GB2004.
		 					 */
							protected boolean BlueBaseLocation_Set = false;
							
    						@Override
		    				
 		/**
         * 
			Location of the base spawning the blue flag (team 1) (BotCTFGame).
		 
         */
        public  Location getBlueBaseLocation()
 	 {
		    					return BlueBaseLocation;
		    				}
		    			
    	
	    /**
         * 
			Location of the first dom point (BotDoubleDomination).
		 
         */
        protected
         Location FirstDomPointLocation =
       	null;
	
    						
    						/**
		 					 * Whether property 'FirstDomPointLocation' was received from GB2004.
		 					 */
							protected boolean FirstDomPointLocation_Set = false;
							
    						@Override
		    				
 		/**
         * 
			Location of the first dom point (BotDoubleDomination).
		 
         */
        public  Location getFirstDomPointLocation()
 	 {
		    					return FirstDomPointLocation;
		    				}
		    			
    	
	    /**
         * 
			Location of the second dom point (BotDoubleDomination).
		 
         */
        protected
         Location SecondDomPointLocation =
       	null;
	
    						
    						/**
		 					 * Whether property 'SecondDomPointLocation' was received from GB2004.
		 					 */
							protected boolean SecondDomPointLocation_Set = false;
							
    						@Override
		    				
 		/**
         * 
			Location of the second dom point (BotDoubleDomination).
		 
         */
        public  Location getSecondDomPointLocation()
 	 {
		    					return SecondDomPointLocation;
		    				}
		    			
    	
	    /**
         * 
			If the game is paused - nobody can move.
		 
         */
        protected
         boolean GamePaused =
       	false;
	
    						
    						/**
		 					 * Whether property 'GamePaused' was received from GB2004.
		 					 */
							protected boolean GamePaused_Set = false;
							
    						@Override
		    				
 		/**
         * 
			If the game is paused - nobody can move.
		 
         */
        public  boolean isGamePaused()
 	 {
		    					return GamePaused;
		    				}
		    			
    	
	    /**
         * 
			If the game is paused just for bots - human controlled
			players can normally move.
		 
         */
        protected
         boolean BotsPaused =
       	false;
	
    						
    						/**
		 					 * Whether property 'BotsPaused' was received from GB2004.
		 					 */
							protected boolean BotsPaused_Set = false;
							
    						@Override
		    				
 		/**
         * 
			If the game is paused just for bots - human controlled
			players can normally move.
		 
         */
        public  boolean isBotsPaused()
 	 {
		    					return BotsPaused;
		    				}
		    			
    	
	    /**
         * 
			Sent only in BotScenario game type. Location of the factory.
		 
         */
        protected
         Location FactoryLocation =
       	null;
	
    						
    						/**
		 					 * Whether property 'FactoryLocation' was received from GB2004.
		 					 */
							protected boolean FactoryLocation_Set = false;
							
    						@Override
		    				
 		/**
         * 
			Sent only in BotScenario game type. Location of the factory.
		 
         */
        public  Location getFactoryLocation()
 	 {
		    					return FactoryLocation;
		    				}
		    			
    	
	    /**
         * 
			Sent only in BotScenario game type. Radius of the factory.
		 
         */
        protected
         double FactoryRadius =
       	0;
	
    						
    						/**
		 					 * Whether property 'FactoryRadius' was received from GB2004.
		 					 */
							protected boolean FactoryRadius_Set = false;
							
    						@Override
		    				
 		/**
         * 
			Sent only in BotScenario game type. Radius of the factory.
		 
         */
        public  double getFactoryRadius()
 	 {
		    					return FactoryRadius;
		    				}
		    			
    	
	    /**
         * 
			Sent only in BotScenario game type. Location of the goal point where UDamagePack should be taken.
		 
         */
        protected
         Location DisperserLocation =
       	null;
	
    						
    						/**
		 					 * Whether property 'DisperserLocation' was received from GB2004.
		 					 */
							protected boolean DisperserLocation_Set = false;
							
    						@Override
		    				
 		/**
         * 
			Sent only in BotScenario game type. Location of the goal point where UDamagePack should be taken.
		 
         */
        public  Location getDisperserLocation()
 	 {
		    					return DisperserLocation;
		    				}
		    			
    	
	    /**
         * 
			Sent only in BotScenario game type. Radius of the disperser point.
		 
         */
        protected
         double DisperserRadius =
       	0;
	
    						
    						/**
		 					 * Whether property 'DisperserRadius' was received from GB2004.
		 					 */
							protected boolean DisperserRadius_Set = false;
							
    						@Override
		    				
 		/**
         * 
			Sent only in BotScenario game type. Radius of the disperser point.
		 
         */
        public  double getDisperserRadius()
 	 {
		    					return DisperserRadius;
		    				}
		    			
    	
	    /**
         * 
			Sent only in BotScenario game type. How much adrenaline we need to activate the factory.
		 
         */
        protected
         double FactoryAdrenalineCount =
       	0;
	
    						
    						/**
		 					 * Whether property 'FactoryAdrenalineCount' was received from GB2004.
		 					 */
							protected boolean FactoryAdrenalineCount_Set = false;
							
    						@Override
		    				
 		/**
         * 
			Sent only in BotScenario game type. How much adrenaline we need to activate the factory.
		 
         */
        public  double getFactoryAdrenalineCount()
 	 {
		    					return FactoryAdrenalineCount;
		    				}
		    			
    	
	    /**
         * 
			Sent only in BotScenario game type. Pickup class our factory spawns when used properly. Item will be spawned at the bot's location.
		 
         */
        protected
         String FactorySpawnType =
       	null;
	
    						
    						/**
		 					 * Whether property 'FactorySpawnType' was received from GB2004.
		 					 */
							protected boolean FactorySpawnType_Set = false;
							
    						@Override
		    				
 		/**
         * 
			Sent only in BotScenario game type. Pickup class our factory spawns when used properly. Item will be spawned at the bot's location.
		 
         */
        public  String getFactorySpawnType()
 	 {
		    					return FactorySpawnType;
		    				}
		    			
		    			
		    			private GameInfoLocal localPart = null;
		    			
		    			@Override
						public GameInfoLocal 
						getLocal() {
							if (localPart != null) return localPart;
							return localPart = new 
								GameInfoLocalMessage();
						}
					
						private GameInfoShared sharedPart = null;
					
						@Override
						public GameInfoShared 
						getShared() {
							if (sharedPart != null) return sharedPart;							
							return sharedPart = new 
								GameInfoSharedMessage();
						}
					
						private GameInfoStatic staticPart = null; 
					
						@Override
						public GameInfoStatic 
						getStatic() {
							if (staticPart != null) return staticPart;
							return staticPart = new 
								GameInfoStaticMessage();
						}
    				
 		/**
         *  
            				Implementation of the local part of the GameBots2004 message NFO, used
            				to facade NFOMessage.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Asynchronous message. Sent as response to READY command.
		Information about the game. What type of game is it going to be,
		number of teams, maximum size of teams etc.
	
         */
 	public class GameInfoLocalMessage 
	  					extends
  						GameInfoLocal
	    {
 	
						
						public UnrealId getId() {						
							return cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo.GameInfoId;
						}
					
		    			@Override
		    			public 
		    			GameInfoLocalMessage clone() {
		    				return this;
		    			}
		    			
		    				public GameInfoLocalMessage getLocal() {
								return this;
					    	}
							public ISharedWorldObject getShared() {
							 	throw new UnsupportedOperationException("Could not return LOCAL as SHARED");
							}
							public IStaticWorldObject getStatic() {
							    throw new UnsupportedOperationException("Could not return LOCAL as STATIC");
							}
		    			
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
				"<br/>]";     
		}
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=local+classtype[@name=message]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=local+classtype[@name=message]) ---        	            	
 	
		}
 	
 		/**
         *  
            				Implementation of the static part of the GameBots2004 message NFO, used
            				to facade NFOMessage.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Asynchronous message. Sent as response to READY command.
		Information about the game. What type of game is it going to be,
		number of teams, maximum size of teams etc.
	
         */
 	public class GameInfoStaticMessage 
	  					extends
  						GameInfoStatic
	    {
 	
						
						public UnrealId getId() {						
							return cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo.GameInfoId;
						}
					
		    			@Override
		    			public 
		    			GameInfoStaticMessage clone() {
		    				return this;
		    			}
		    			
 		/**
         * 
			What you are playing (BotDeathMatch, BotTeamGame,
			BotCTFGame,BotDoubleDomination).
		 
         */
        public  String getGametype()
 	 {
				    					return Gametype;
				    				}
				    			
 		/**
         * Name of map in game. 
         */
        public  String getLevel()
 	 {
				    					return Level;
				    				}
				    			
 		/**
         * 
			Max number of teams. Valid team range will be 0 to (MaxTeams
			- 1) (BotTeamGame, BotCTFGame, BotDoubleDomination). Usually
			there will be two teams - 0 and 1.
		 
         */
        public  int getMaxTeams()
 	 {
				    					return MaxTeams;
				    				}
				    			
 		/**
         * 
			Location of the base spawning the red flag (team 0) (BotCTFGame).
		 
         */
        public  Location getRedBaseLocation()
 	 {
				    					return RedBaseLocation;
				    				}
				    			
 		/**
         * 
			Location of the base spawning the blue flag (team 1) (BotCTFGame).
		 
         */
        public  Location getBlueBaseLocation()
 	 {
				    					return BlueBaseLocation;
				    				}
				    			
 		/**
         * 
			Location of the first dom point (BotDoubleDomination).
		 
         */
        public  Location getFirstDomPointLocation()
 	 {
				    					return FirstDomPointLocation;
				    				}
				    			
 		/**
         * 
			Location of the second dom point (BotDoubleDomination).
		 
         */
        public  Location getSecondDomPointLocation()
 	 {
				    					return SecondDomPointLocation;
				    				}
				    			
 		/**
         * 
			Sent only in BotScenario game type. Location of the factory.
		 
         */
        public  Location getFactoryLocation()
 	 {
				    					return FactoryLocation;
				    				}
				    			
 		/**
         * 
			Sent only in BotScenario game type. Radius of the factory.
		 
         */
        public  double getFactoryRadius()
 	 {
				    					return FactoryRadius;
				    				}
				    			
 		/**
         * 
			Sent only in BotScenario game type. Location of the goal point where UDamagePack should be taken.
		 
         */
        public  Location getDisperserLocation()
 	 {
				    					return DisperserLocation;
				    				}
				    			
 		/**
         * 
			Sent only in BotScenario game type. Radius of the disperser point.
		 
         */
        public  double getDisperserRadius()
 	 {
				    					return DisperserRadius;
				    				}
				    			
 		/**
         * 
			Sent only in BotScenario game type. How much adrenaline we need to activate the factory.
		 
         */
        public  double getFactoryAdrenalineCount()
 	 {
				    					return FactoryAdrenalineCount;
				    				}
				    			
 		/**
         * 
			Sent only in BotScenario game type. Pickup class our factory spawns when used properly. Item will be spawned at the bot's location.
		 
         */
        public  String getFactorySpawnType()
 	 {
				    					return FactorySpawnType;
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
 				GameInfoStatic obj = (GameInfoStatic) other;

 				
 						if ( !(
 	 			AdvancedEquals.equalsOrNull(this.getGametype()
 	, obj.getGametype()
 	)
 	 		) )
						{
							System.out.println("!!!!!PROPERTY UPDATE ERROR!!!! on property Gametype on object class GameInfoStatic");
							return true;
						}
 					
 						if ( !(
 	 			AdvancedEquals.equalsOrNull(this.getLevel()
 	, obj.getLevel()
 	)
 	 		) )
						{
							System.out.println("!!!!!PROPERTY UPDATE ERROR!!!! on property Level on object class GameInfoStatic");
							return true;
						}
 					
 						if ( !(this.getMaxTeams()
 	
 	 			== obj.getMaxTeams()
 	) )
						{
							System.out.println("!!!!!PROPERTY UPDATE ERROR!!!! on property MaxTeams on object class GameInfoStatic");
							return true;
						}
 					
 						if ( !(
 	 			AdvancedEquals.equalsOrNull(this.getRedBaseLocation()
 	, obj.getRedBaseLocation()
 	)
 	 		) )
						{
							System.out.println("!!!!!PROPERTY UPDATE ERROR!!!! on property RedBaseLocation on object class GameInfoStatic");
							return true;
						}
 					
 						if ( !(
 	 			AdvancedEquals.equalsOrNull(this.getBlueBaseLocation()
 	, obj.getBlueBaseLocation()
 	)
 	 		) )
						{
							System.out.println("!!!!!PROPERTY UPDATE ERROR!!!! on property BlueBaseLocation on object class GameInfoStatic");
							return true;
						}
 					
 						if ( !(
 	 			AdvancedEquals.equalsOrNull(this.getFirstDomPointLocation()
 	, obj.getFirstDomPointLocation()
 	)
 	 		) )
						{
							System.out.println("!!!!!PROPERTY UPDATE ERROR!!!! on property FirstDomPointLocation on object class GameInfoStatic");
							return true;
						}
 					
 						if ( !(
 	 			AdvancedEquals.equalsOrNull(this.getSecondDomPointLocation()
 	, obj.getSecondDomPointLocation()
 	)
 	 		) )
						{
							System.out.println("!!!!!PROPERTY UPDATE ERROR!!!! on property SecondDomPointLocation on object class GameInfoStatic");
							return true;
						}
 					
 						if ( !(
 	 			AdvancedEquals.equalsOrNull(this.getFactoryLocation()
 	, obj.getFactoryLocation()
 	)
 	 		) )
						{
							System.out.println("!!!!!PROPERTY UPDATE ERROR!!!! on property FactoryLocation on object class GameInfoStatic");
							return true;
						}
 					
 						if ( !(this.getFactoryRadius()
 	
 	 			== obj.getFactoryRadius()
 	) )
						{
							System.out.println("!!!!!PROPERTY UPDATE ERROR!!!! on property FactoryRadius on object class GameInfoStatic");
							return true;
						}
 					
 						if ( !(
 	 			AdvancedEquals.equalsOrNull(this.getDisperserLocation()
 	, obj.getDisperserLocation()
 	)
 	 		) )
						{
							System.out.println("!!!!!PROPERTY UPDATE ERROR!!!! on property DisperserLocation on object class GameInfoStatic");
							return true;
						}
 					
 						if ( !(this.getDisperserRadius()
 	
 	 			== obj.getDisperserRadius()
 	) )
						{
							System.out.println("!!!!!PROPERTY UPDATE ERROR!!!! on property DisperserRadius on object class GameInfoStatic");
							return true;
						}
 					
 						if ( !(this.getFactoryAdrenalineCount()
 	
 	 			== obj.getFactoryAdrenalineCount()
 	) )
						{
							System.out.println("!!!!!PROPERTY UPDATE ERROR!!!! on property FactoryAdrenalineCount on object class GameInfoStatic");
							return true;
						}
 					
 						if ( !(
 	 			AdvancedEquals.equalsOrNull(this.getFactorySpawnType()
 	, obj.getFactorySpawnType()
 	)
 	 		) )
						{
							System.out.println("!!!!!PROPERTY UPDATE ERROR!!!! on property FactorySpawnType on object class GameInfoStatic");
							return true;
						}
 					
 			}
 			return false;
 		}
 	 
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
		              			"Gametype = " + String.valueOf(getGametype()
 	) + " | " + 
		              		
		              			"Level = " + String.valueOf(getLevel()
 	) + " | " + 
		              		
		              			"MaxTeams = " + String.valueOf(getMaxTeams()
 	) + " | " + 
		              		
		              			"RedBaseLocation = " + String.valueOf(getRedBaseLocation()
 	) + " | " + 
		              		
		              			"BlueBaseLocation = " + String.valueOf(getBlueBaseLocation()
 	) + " | " + 
		              		
		              			"FirstDomPointLocation = " + String.valueOf(getFirstDomPointLocation()
 	) + " | " + 
		              		
		              			"SecondDomPointLocation = " + String.valueOf(getSecondDomPointLocation()
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
		              		
		              			"<b>MaxTeams</b> = " + String.valueOf(getMaxTeams()
 	) + " <br/> " + 
		              		
		              			"<b>RedBaseLocation</b> = " + String.valueOf(getRedBaseLocation()
 	) + " <br/> " + 
		              		
		              			"<b>BlueBaseLocation</b> = " + String.valueOf(getBlueBaseLocation()
 	) + " <br/> " + 
		              		
		              			"<b>FirstDomPointLocation</b> = " + String.valueOf(getFirstDomPointLocation()
 	) + " <br/> " + 
		              		
		              			"<b>SecondDomPointLocation</b> = " + String.valueOf(getSecondDomPointLocation()
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
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=static+classtype[@name=message]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=static+classtype[@name=message]) ---        	            	
 	
		}
 	
 		/**
         *  
            				Implementation of the shared part of the GameBots2004 message NFO, used
            				to facade NFOMessage.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Asynchronous message. Sent as response to READY command.
		Information about the game. What type of game is it going to be,
		number of teams, maximum size of teams etc.
	
         */
 	public class GameInfoSharedMessage 
	  					extends
  						GameInfoShared
	    {
 	
    	
    	
		public GameInfoSharedMessage()
		{
			
				propertyMap.put(myWeaponStay.getPropertyId(), myWeaponStay);
			
				propertyMap.put(myTimeLimit.getPropertyId(), myTimeLimit);
			
				propertyMap.put(myFragLimit.getPropertyId(), myFragLimit);
			
				propertyMap.put(myGoalTeamScore.getPropertyId(), myGoalTeamScore);
			
				propertyMap.put(myMaxTeamSize.getPropertyId(), myMaxTeamSize);
			
				propertyMap.put(myGamePaused.getPropertyId(), myGamePaused);
			
				propertyMap.put(myBotsPaused.getPropertyId(), myBotsPaused);
			
		}		
    
						
						public UnrealId getId() {						
							return cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo.GameInfoId;
						}
					
		    			@Override
		    			public 
		    			GameInfoSharedMessage clone() {
		    				return this;
		    			}
		    			
		
		
		
		protected HashMap<PropertyId, ISharedProperty> propertyMap = new HashMap<PropertyId, ISharedProperty>(
			7
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
         * 
			If true respawned weapons will stay on the ground after picked up (but bot cannot pickup same weapon twice).
		 
         */
        protected
         BooleanProperty 
        myWeaponStay
					= new
					BooleanProperty
					(
						getId(), 
						"WeaponStay", 
						WeaponStay, 
						GameInfo.class
					);
					
 		/**
         * 
			If true respawned weapons will stay on the ground after picked up (but bot cannot pickup same weapon twice).
		 
         */
        public  boolean isWeaponStay()
 	 {
			  			return myWeaponStay.getValue();
			  		}
				
    	
	    /**
         * 
			Maximum time game will last (if tied at end may goe to
			"sudden death overtime" - depends on the game type).
		 
         */
        protected
         DoubleProperty 
        myTimeLimit
					= new
					DoubleProperty
					(
						getId(), 
						"TimeLimit", 
						TimeLimit, 
						GameInfo.class
					);
					
 		/**
         * 
			Maximum time game will last (if tied at end may goe to
			"sudden death overtime" - depends on the game type).
		 
         */
        public  double getTimeLimit()
 	 {
			  			return myTimeLimit.getValue();
			  		}
				
    	
	    /**
         * 
			Number of kills needed to win game (BotDeathMatch only).
		 
         */
        protected
         IntProperty 
        myFragLimit
					= new
					IntProperty
					(
						getId(), 
						"FragLimit", 
						FragLimit, 
						GameInfo.class
					);
					
 		/**
         * 
			Number of kills needed to win game (BotDeathMatch only).
		 
         */
        public  int getFragLimit()
 	 {
			  			return myFragLimit.getValue();
			  		}
				
    	
	    /**
         * 
			Number of points a team needs to win the game (BotTeamGame,
			BotCTFGame, BotDoubleDomination).
		 
         */
        protected
         LongProperty 
        myGoalTeamScore
					= new
					LongProperty
					(
						getId(), 
						"GoalTeamScore", 
						GoalTeamScore, 
						GameInfo.class
					);
					
 		/**
         * 
			Number of points a team needs to win the game (BotTeamGame,
			BotCTFGame, BotDoubleDomination).
		 
         */
        public  long getGoalTeamScore()
 	 {
			  			return myGoalTeamScore.getValue();
			  		}
				
    	
	    /**
         * 
			Max number of players per side (BotTeamGame, BotCTFGame,
			BotDoubleDomination).
		 
         */
        protected
         IntProperty 
        myMaxTeamSize
					= new
					IntProperty
					(
						getId(), 
						"MaxTeamSize", 
						MaxTeamSize, 
						GameInfo.class
					);
					
 		/**
         * 
			Max number of players per side (BotTeamGame, BotCTFGame,
			BotDoubleDomination).
		 
         */
        public  int getMaxTeamSize()
 	 {
			  			return myMaxTeamSize.getValue();
			  		}
				
    	
	    /**
         * 
			If the game is paused - nobody can move.
		 
         */
        protected
         BooleanProperty 
        myGamePaused
					= new
					BooleanProperty
					(
						getId(), 
						"GamePaused", 
						GamePaused, 
						GameInfo.class
					);
					
 		/**
         * 
			If the game is paused - nobody can move.
		 
         */
        public  boolean isGamePaused()
 	 {
			  			return myGamePaused.getValue();
			  		}
				
    	
	    /**
         * 
			If the game is paused just for bots - human controlled
			players can normally move.
		 
         */
        protected
         BooleanProperty 
        myBotsPaused
					= new
					BooleanProperty
					(
						getId(), 
						"BotsPaused", 
						BotsPaused, 
						GameInfo.class
					);
					
 		/**
         * 
			If the game is paused just for bots - human controlled
			players can normally move.
		 
         */
        public  boolean isBotsPaused()
 	 {
			  			return myBotsPaused.getValue();
			  		}
				
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
		              			"WeaponStay = " + String.valueOf(isWeaponStay()
 	) + " | " + 
		              		
		              			"TimeLimit = " + String.valueOf(getTimeLimit()
 	) + " | " + 
		              		
		              			"FragLimit = " + String.valueOf(getFragLimit()
 	) + " | " + 
		              		
		              			"GoalTeamScore = " + String.valueOf(getGoalTeamScore()
 	) + " | " + 
		              		
		              			"MaxTeamSize = " + String.valueOf(getMaxTeamSize()
 	) + " | " + 
		              		
		              			"GamePaused = " + String.valueOf(isGamePaused()
 	) + " | " + 
		              		
		              			"BotsPaused = " + String.valueOf(isBotsPaused()
 	) + " | " + 
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>WeaponStay</b> = " + String.valueOf(isWeaponStay()
 	) + " <br/> " + 
		              		
		              			"<b>TimeLimit</b> = " + String.valueOf(getTimeLimit()
 	) + " <br/> " + 
		              		
		              			"<b>FragLimit</b> = " + String.valueOf(getFragLimit()
 	) + " <br/> " + 
		              		
		              			"<b>GoalTeamScore</b> = " + String.valueOf(getGoalTeamScore()
 	) + " <br/> " + 
		              		
		              			"<b>MaxTeamSize</b> = " + String.valueOf(getMaxTeamSize()
 	) + " <br/> " + 
		              		
		              			"<b>GamePaused</b> = " + String.valueOf(isGamePaused()
 	) + " <br/> " + 
		              		
		              			"<b>BotsPaused</b> = " + String.valueOf(isBotsPaused()
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
			if (!( object instanceof GameInfoMessage) ) {
				throw new PogamutException("Can't update different class than GameInfoMessage, got class " + object.getClass().getSimpleName() + "!", this);		
			}
			GameInfoMessage toUpdate = (GameInfoMessage)object;
			
			boolean updated = false;
			
			// UPDATING LOCAL PROPERTIES
			
         	
         	// UPDATING SHARED PROPERTIES
         	
				if (toUpdate.WeaponStay != isWeaponStay()
 	) {
				    toUpdate.WeaponStay=isWeaponStay()
 	;
					updated = true;
				}
			
				if (toUpdate.TimeLimit != getTimeLimit()
 	) {
				    toUpdate.TimeLimit=getTimeLimit()
 	;
					updated = true;
				}
			
				if (toUpdate.FragLimit != getFragLimit()
 	) {
				    toUpdate.FragLimit=getFragLimit()
 	;
					updated = true;
				}
			
				if (toUpdate.GoalTeamScore != getGoalTeamScore()
 	) {
				    toUpdate.GoalTeamScore=getGoalTeamScore()
 	;
					updated = true;
				}
			
				if (toUpdate.MaxTeamSize != getMaxTeamSize()
 	) {
				    toUpdate.MaxTeamSize=getMaxTeamSize()
 	;
					updated = true;
				}
			
				if (toUpdate.GamePaused != isGamePaused()
 	) {
				    toUpdate.GamePaused=isGamePaused()
 	;
					updated = true;
				}
			
				if (toUpdate.BotsPaused != isBotsPaused()
 	) {
				    toUpdate.BotsPaused=isBotsPaused()
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
			return new GameInfoLocalImpl.GameInfoLocalUpdate
    (this.getLocal(), SimTime);
		}

		@Override
		public ISharedWorldObjectUpdatedEvent getSharedEvent() {
			return new GameInfoSharedImpl.GameInfoSharedUpdate
    (this.getShared(), SimTime, this.getTeamId());
		}

		@Override
		public IStaticWorldObjectUpdatedEvent getStaticEvent() {
			return new GameInfoStaticImpl.GameInfoStaticUpdate
    (this.getStatic(), SimTime);
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
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=base+classtype[@name=message]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=base+classtype[@name=message]) ---        	            	
 	
		}
 	