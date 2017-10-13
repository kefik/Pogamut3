package cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages;
 		
 		// --- IMPORTS FROM /messages/settings/javasettings/javaimport BEGIN
			import java.util.*;import javax.vecmath.*;import cz.cuni.amis.pogamut.base.communication.messages.*;import cz.cuni.amis.pogamut.base.communication.worldview.*;import cz.cuni.amis.pogamut.base.communication.worldview.event.*;import cz.cuni.amis.pogamut.base.communication.worldview.object.*;import cz.cuni.amis.pogamut.multi.communication.worldview.object.*;import cz.cuni.amis.pogamut.base.communication.translator.event.*;import cz.cuni.amis.pogamut.multi.communication.translator.event.*;import cz.cuni.amis.pogamut.base3d.worldview.object.*;import cz.cuni.amis.pogamut.base3d.worldview.object.event.*;import cz.cuni.amis.pogamut.ut2004.communication.messages.*;import cz.cuni.amis.pogamut.ut2004.communication.worldview.objects.*;import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.objects.*;import cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor.*;import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType.Category;import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;import cz.cuni.amis.utils.exception.*;import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdateResult.Result;import cz.cuni.amis.utils.SafeEquals;import cz.cuni.amis.pogamut.base.agent.*;import cz.cuni.amis.pogamut.multi.agent.*;import cz.cuni.amis.pogamut.multi.communication.worldview.property.*;import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.property.*;import cz.cuni.amis.utils.token.*;import cz.cuni.amis.utils.*;
		// --- IMPORTS FROM /messages/settings/javasettings/javaimport END
		
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name='all'] BEGIN
				
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name='all'] END
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name=static]+classtype[@name=impl] BEGIN
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name=static]+classtype[@name=impl] END
    
 		/**
         *  
            				Implementation of the static part of the GameBots2004 message NFO.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Asynchronous message. Sent as response to READY command.
		Information about the game. What type of game is it going to be,
		number of teams, maximum size of teams etc.
	
         */
 	public class GameInfoStaticImpl 
  						extends
  						GameInfoStatic
	    {
 	
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public GameInfoStaticImpl()
		{
		}
	
    	/**
		 * Cloning constructor from the full message.
		 *
		 * @param original
		 */
		public GameInfoStaticImpl(GameInfo original) {		
			
					this.Gametype = original.getGametype()
 	;
				
					this.Level = original.getLevel()
 	;
				
					this.MaxTeams = original.getMaxTeams()
 	;
				
					this.RedBaseLocation = original.getRedBaseLocation()
 	;
				
					this.BlueBaseLocation = original.getBlueBaseLocation()
 	;
				
					this.FirstDomPointLocation = original.getFirstDomPointLocation()
 	;
				
					this.SecondDomPointLocation = original.getSecondDomPointLocation()
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
				
			this.SimTime = original.getSimTime();			
		}
		
		/**
		 * Cloning constructor from the full message.
		 *
		 * @param original
		 */
		public GameInfoStaticImpl(GameInfoStaticImpl original) {		
			
					this.Gametype = original.getGametype()
 	;
				
					this.Level = original.getLevel()
 	;
				
					this.MaxTeams = original.getMaxTeams()
 	;
				
					this.RedBaseLocation = original.getRedBaseLocation()
 	;
				
					this.BlueBaseLocation = original.getBlueBaseLocation()
 	;
				
					this.FirstDomPointLocation = original.getFirstDomPointLocation()
 	;
				
					this.SecondDomPointLocation = original.getSecondDomPointLocation()
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
				
			this.SimTime = original.getSimTime();
		}
		
			/**
			 * Cloning constructor from the message part.
			 *
			 * @param original
			 */
			public GameInfoStaticImpl(GameInfoStatic original) {
				
						this.Gametype = original.getGametype()
 	;
					
						this.Level = original.getLevel()
 	;
					
						this.MaxTeams = original.getMaxTeams()
 	;
					
						this.RedBaseLocation = original.getRedBaseLocation()
 	;
					
						this.BlueBaseLocation = original.getBlueBaseLocation()
 	;
					
						this.FirstDomPointLocation = original.getFirstDomPointLocation()
 	;
					
						this.SecondDomPointLocation = original.getSecondDomPointLocation()
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
					
			}
		
						
						public UnrealId getId() {						
							return cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo.GameInfoId;
						}
					
   				
   				@Override
   				public void setSimTime(long SimTime) {
					super.setSimTime(SimTime);
				}
   			
	    				@Override
	    				public 
	    				GameInfoStaticImpl clone() {
	    					return new 
	    					GameInfoStaticImpl(this);
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
        protected
         int MaxTeams =
       	0;
	
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
        protected
         Location RedBaseLocation =
       	null;
	
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
        protected
         Location FactoryLocation =
       	null;
	
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
         * 
			Sent only in BotScenario game type. Pickup class our factory spawns when used properly. Item will be spawned at the bot's location.
		 
         */
        public  String getFactorySpawnType()
 	 {
				    					return FactorySpawnType;
				    				}
				    			
    	
    	
    	public static class GameInfoStaticUpdate
     implements IStaticWorldObjectUpdatedEvent
		{
			
			private GameInfoStatic data;
			private long time;
			
			public GameInfoStaticUpdate
    (GameInfoStatic source, long time)
			{
				this.data = source;
				this.time = time;
			}
			
			/**
			 * Simulation time in MILLI SECONDS !!!
			 */
			@Override
			public long getSimTime() {
				return time;
			}
	
			@Override
			public WorldObjectId getId() {
				return data.getId();
			}
			
			@Override
			public IWorldObjectUpdateResult<IStaticWorldObject> update(
					IStaticWorldObject object) {
				if ( object == null)
				{
					data = new GameInfoStaticImpl(data);
					return new IWorldObjectUpdateResult.WorldObjectUpdateResult<IStaticWorldObject>(IWorldObjectUpdateResult.Result.CREATED, data);
				}
				//since static objects can't be updated, we'll just check that the object stays the same
				if ( object instanceof GameInfoStaticImpl)
				{
					GameInfoStaticImpl orig = (GameInfoStaticImpl)object;
					//since these errors usually mean error in gamebots, we will just print an error message
					if ( data.isDifferentFrom(orig) )
					{
						//data.isDifferentFrom(orig);
						//throw new PogamutException("Trying to modify static object " + this.data.getId().toString() , this);
						System.out.println("!!!!!ERROR!!!!!! in static object modification. Object class : GameInfoStaticImpl to see which property was different see !!!!PROPERTY UPDATE ERROR!!!!");
					}
					return new IWorldObjectUpdateResult.WorldObjectUpdateResult<IStaticWorldObject>(IWorldObjectUpdateResult.Result.SAME, data);
				}				
				throw new PogamutException("Unexpected object type for update, GameInfoStaticImpl expected not class " + object.getClass().getSimpleName() + ".", this);
			}
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
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=static+classtype[@name=impl]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=static+classtype[@name=impl]) ---        	            	
 	
		}
 	