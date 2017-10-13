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
            				Abstract definition of the shared part of the GameBots2004 message NFO.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Asynchronous message. Sent as response to READY command.
		Information about the game. What type of game is it going to be,
		number of teams, maximum size of teams etc.
	
         */
 	public abstract class GameInfoShared 
  						extends InfoMessage
  						implements ISharedWorldObject
  						
	    {
 	
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public GameInfoShared()
		{
		}
		
				// abstract definition of the shared-part of the message, no more constructors is needed
			
						
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
	   	
	    						public static final Token WeaponStayPropertyToken = Tokens.get("WeaponStay");
	    					
	    						public static final Token TimeLimitPropertyToken = Tokens.get("TimeLimit");
	    					
	    						public static final Token FragLimitPropertyToken = Tokens.get("FragLimit");
	    					
	    						public static final Token GoalTeamScorePropertyToken = Tokens.get("GoalTeamScore");
	    					
	    						public static final Token MaxTeamSizePropertyToken = Tokens.get("MaxTeamSize");
	    					
	    						public static final Token GamePausedPropertyToken = Tokens.get("GamePaused");
	    					
	    						public static final Token BotsPausedPropertyToken = Tokens.get("BotsPaused");
	    						
							
							public static final Set<Token> SharedPropertyTokens;
	
							static {
								Set<Token> tokens = new HashSet<Token>();
								
									tokens.add(WeaponStayPropertyToken);
								
									tokens.add(TimeLimitPropertyToken);
								
									tokens.add(FragLimitPropertyToken);
								
									tokens.add(GoalTeamScorePropertyToken);
								
									tokens.add(MaxTeamSizePropertyToken);
								
									tokens.add(GamePausedPropertyToken);
								
									tokens.add(BotsPausedPropertyToken);
								
								SharedPropertyTokens = Collections.unmodifiableSet(tokens);
							}
	    				
	    			
	    				@Override
		    			public abstract 
		    			GameInfoShared clone();
		    			
						@Override
						public Class getCompositeClass() {
							return GameInfo.class;
						}
	
						
		    			
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
			Max number of players per side (BotTeamGame, BotCTFGame,
			BotDoubleDomination).
		 
         */
        public abstract int getMaxTeamSize()
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
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=shared+classtype[@name=abstract]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=shared+classtype[@name=abstract]) ---        	            	
 	
		}
 	