package cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages;
 		
 		// --- IMPORTS FROM /messages/settings/javasettings/javaimport BEGIN
			import java.util.*;import javax.vecmath.*;import cz.cuni.amis.pogamut.base.communication.messages.*;import cz.cuni.amis.pogamut.base.communication.worldview.*;import cz.cuni.amis.pogamut.base.communication.worldview.event.*;import cz.cuni.amis.pogamut.base.communication.worldview.object.*;import cz.cuni.amis.pogamut.multi.communication.worldview.object.*;import cz.cuni.amis.pogamut.base.communication.translator.event.*;import cz.cuni.amis.pogamut.multi.communication.translator.event.*;import cz.cuni.amis.pogamut.base3d.worldview.object.*;import cz.cuni.amis.pogamut.base3d.worldview.object.event.*;import cz.cuni.amis.pogamut.ut2004.communication.messages.*;import cz.cuni.amis.pogamut.ut2004.communication.worldview.objects.*;import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.objects.*;import cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor.*;import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType.Category;import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;import cz.cuni.amis.utils.exception.*;import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdateResult.Result;import cz.cuni.amis.utils.SafeEquals;import cz.cuni.amis.pogamut.base.agent.*;import cz.cuni.amis.pogamut.multi.agent.*;import cz.cuni.amis.pogamut.multi.communication.worldview.property.*;import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.property.*;import cz.cuni.amis.utils.token.*;import cz.cuni.amis.utils.*;
		// --- IMPORTS FROM /messages/settings/javasettings/javaimport END
		
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name='all'] BEGIN
				
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name='all'] END
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name=composite]+classtype[@name=impl] BEGIN
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name=composite]+classtype[@name=impl] END
    
 		/**
         *  
            		Composite implementation of the NFO abstract message. It wraps Local/Shared/Static parts in single object
            		allowing to presenting a nice facade for users.
            	
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Asynchronous message. Sent as response to READY command.
		Information about the game. What type of game is it going to be,
		number of teams, maximum size of teams etc.
	
         */
 	public class GameInfoCompositeImpl 
  				extends GameInfo
	    {
 	
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public GameInfoCompositeImpl()
		{
		}
	
		
		/**
		 * Composite-impl constructor. It assembles the message from its three fragments - local/shared/static.
		 *
		 * @param partLocal local-part of the message
		 * @param partShared shared-part of the message
		 * @param partStatic static-part of the message
		 */
		public GameInfoCompositeImpl(
			GameInfoLocalImpl partLocal,
			GameInfoSharedImpl partShared,
			GameInfoStaticImpl partStatic
		) {
			this.partLocal  = partLocal;
			this.partShared = partShared;
			this.partStatic = partStatic;
		}
		
		/**
		 * Cloning constructor.
		 *
		 * @param original		 
		 */
		public GameInfoCompositeImpl(GameInfoCompositeImpl original) {
			this.partLocal = partLocal;
			this.partShared = partShared;
			this.partStatic = partStatic;
		}
    
						
						public UnrealId getId() {						
							return cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo.GameInfoId;
						}
					
   				
   				@Override
   				public void setSimTime(long SimTime) {
					super.setSimTime(SimTime);
				}
   			
    			
    			protected 
    			GameInfoStaticImpl
    			partStatic;
    			
    			@Override
				public GameInfoStatic getStatic() {
					return partStatic;
				}
    			
    			protected
    			GameInfoLocalImpl
    			partLocal;
    	
    			@Override
				public GameInfoLocal getLocal() {
					return partLocal;
				}
			
    			GameInfoSharedImpl
    			partShared;
    			
				@Override
				public GameInfoShared getShared() {
					return partShared;
				}
			
				
  				
  					@Override
    				
 		/**
         * 
			What you are playing (BotDeathMatch, BotTeamGame,
			BotCTFGame,BotDoubleDomination).
		 
         */
        public  String getGametype()
 	 {
    					return 
    						
    								partStatic.
    							getGametype()
 	;
    				}
    			
  					@Override
    				
 		/**
         * Name of map in game. 
         */
        public  String getLevel()
 	 {
    					return 
    						
    								partStatic.
    							getLevel()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			If true respawned weapons will stay on the ground after picked up (but bot cannot pickup same weapon twice).
		 
         */
        public  boolean isWeaponStay()
 	 {
    					return 
    						
    								partShared.
    							isWeaponStay()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			Maximum time game will last (if tied at end may goe to
			"sudden death overtime" - depends on the game type).
		 
         */
        public  double getTimeLimit()
 	 {
    					return 
    						
    								partShared.
    							getTimeLimit()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			Number of kills needed to win game (BotDeathMatch only).
		 
         */
        public  int getFragLimit()
 	 {
    					return 
    						
    								partShared.
    							getFragLimit()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			Number of points a team needs to win the game (BotTeamGame,
			BotCTFGame, BotDoubleDomination).
		 
         */
        public  long getGoalTeamScore()
 	 {
    					return 
    						
    								partShared.
    							getGoalTeamScore()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			Max number of teams. Valid team range will be 0 to (MaxTeams
			- 1) (BotTeamGame, BotCTFGame, BotDoubleDomination). Usually
			there will be two teams - 0 and 1.
		 
         */
        public  int getMaxTeams()
 	 {
    					return 
    						
    								partStatic.
    							getMaxTeams()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			Max number of players per side (BotTeamGame, BotCTFGame,
			BotDoubleDomination).
		 
         */
        public  int getMaxTeamSize()
 	 {
    					return 
    						
    								partShared.
    							getMaxTeamSize()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			Location of the base spawning the red flag (team 0) (BotCTFGame).
		 
         */
        public  Location getRedBaseLocation()
 	 {
    					return 
    						
    								partStatic.
    							getRedBaseLocation()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			Location of the base spawning the blue flag (team 1) (BotCTFGame).
		 
         */
        public  Location getBlueBaseLocation()
 	 {
    					return 
    						
    								partStatic.
    							getBlueBaseLocation()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			Location of the first dom point (BotDoubleDomination).
		 
         */
        public  Location getFirstDomPointLocation()
 	 {
    					return 
    						
    								partStatic.
    							getFirstDomPointLocation()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			Location of the second dom point (BotDoubleDomination).
		 
         */
        public  Location getSecondDomPointLocation()
 	 {
    					return 
    						
    								partStatic.
    							getSecondDomPointLocation()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			If the game is paused - nobody can move.
		 
         */
        public  boolean isGamePaused()
 	 {
    					return 
    						
    								partShared.
    							isGamePaused()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			If the game is paused just for bots - human controlled
			players can normally move.
		 
         */
        public  boolean isBotsPaused()
 	 {
    					return 
    						
    								partShared.
    							isBotsPaused()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			Sent only in BotScenario game type. Location of the factory.
		 
         */
        public  Location getFactoryLocation()
 	 {
    					return 
    						
    								partStatic.
    							getFactoryLocation()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			Sent only in BotScenario game type. Radius of the factory.
		 
         */
        public  double getFactoryRadius()
 	 {
    					return 
    						
    								partStatic.
    							getFactoryRadius()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			Sent only in BotScenario game type. Location of the goal point where UDamagePack should be taken.
		 
         */
        public  Location getDisperserLocation()
 	 {
    					return 
    						
    								partStatic.
    							getDisperserLocation()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			Sent only in BotScenario game type. Radius of the disperser point.
		 
         */
        public  double getDisperserRadius()
 	 {
    					return 
    						
    								partStatic.
    							getDisperserRadius()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			Sent only in BotScenario game type. How much adrenaline we need to activate the factory.
		 
         */
        public  double getFactoryAdrenalineCount()
 	 {
    					return 
    						
    								partStatic.
    							getFactoryAdrenalineCount()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			Sent only in BotScenario game type. Pickup class our factory spawns when used properly. Item will be spawned at the bot's location.
		 
         */
        public  String getFactorySpawnType()
 	 {
    					return 
    						
    								partStatic.
    							getFactorySpawnType()
 	;
    				}
    			
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
            			"Static = " + String.valueOf(partStatic) + " | Local = " + String.valueOf(partLocal) + " | Shared = " + String.valueOf(partShared) + " ]" +
            		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
            			"Static = " + String.valueOf(partStatic) + " <br/> Local = " + String.valueOf(partLocal) + " <br/> Shared = " + String.valueOf(partShared) + " ]" +
            		
				"<br/>]";     
		}
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=composite+classtype[@name=impl]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=composite+classtype[@name=impl]) ---        	            	
 	
		}
 	