package cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages;
 		
 		// --- IMPORTS FROM /messages/settings/javasettings/javaimport BEGIN
			import java.util.*;import javax.vecmath.*;import cz.cuni.amis.pogamut.base.communication.messages.*;import cz.cuni.amis.pogamut.base.communication.worldview.*;import cz.cuni.amis.pogamut.base.communication.worldview.event.*;import cz.cuni.amis.pogamut.base.communication.worldview.object.*;import cz.cuni.amis.pogamut.multi.communication.worldview.object.*;import cz.cuni.amis.pogamut.base.communication.translator.event.*;import cz.cuni.amis.pogamut.multi.communication.translator.event.*;import cz.cuni.amis.pogamut.base3d.worldview.object.*;import cz.cuni.amis.pogamut.base3d.worldview.object.event.*;import cz.cuni.amis.pogamut.ut2004.communication.messages.*;import cz.cuni.amis.pogamut.ut2004.communication.worldview.objects.*;import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.objects.*;import cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor.*;import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType.Category;import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;import cz.cuni.amis.utils.exception.*;import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdateResult.Result;import cz.cuni.amis.utils.SafeEquals;import cz.cuni.amis.pogamut.base.agent.*;import cz.cuni.amis.pogamut.multi.agent.*;import cz.cuni.amis.pogamut.multi.communication.worldview.property.*;import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.property.*;import cz.cuni.amis.utils.token.*;import cz.cuni.amis.utils.*;
		// --- IMPORTS FROM /messages/settings/javasettings/javaimport END
		
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name='all'] BEGIN
				
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name='all'] END
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name=static]+classtype[@name=abstract] BEGIN
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name=static]+classtype[@name=abstract] END
    
 		/**
         *  
            				Abstract definition of the static part of the GameBots2004 message NFO.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Asynchronous message. Sent as response to READY command.
		Information about the game. What type of game is it going to be,
		number of teams, maximum size of teams etc.
	
         */
 	public abstract class GameInfoStatic 
  						extends InfoMessage
  						implements IStaticWorldObject
  						
	    {
 	
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public GameInfoStatic()
		{
		}
		
				// abstract definition of the static-part of the message, no more constructors is needed
			
						
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
	   	
	    			
	    				@Override
		    			public abstract 
		    			GameInfoStatic clone();
		    			
						@Override
						public Class getCompositeClass() {
							return GameInfo.class;
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
			Max number of teams. Valid team range will be 0 to (MaxTeams
			- 1) (BotTeamGame, BotCTFGame, BotDoubleDomination). Usually
			there will be two teams - 0 and 1.
		 
         */
        public abstract int getMaxTeams()
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
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=static+classtype[@name=abstract]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=static+classtype[@name=abstract]) ---        	            	
 	
		}
 	