package cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages;
 		
 		// --- IMPORTS FROM /messages/settings/javasettings/javaimport BEGIN
			import java.util.*;import javax.vecmath.*;import cz.cuni.amis.pogamut.base.communication.messages.*;import cz.cuni.amis.pogamut.base.communication.worldview.*;import cz.cuni.amis.pogamut.base.communication.worldview.event.*;import cz.cuni.amis.pogamut.base.communication.worldview.object.*;import cz.cuni.amis.pogamut.multi.communication.worldview.object.*;import cz.cuni.amis.pogamut.base.communication.translator.event.*;import cz.cuni.amis.pogamut.multi.communication.translator.event.*;import cz.cuni.amis.pogamut.base3d.worldview.object.*;import cz.cuni.amis.pogamut.base3d.worldview.object.event.*;import cz.cuni.amis.pogamut.ut2004.communication.messages.*;import cz.cuni.amis.pogamut.ut2004.communication.worldview.objects.*;import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.objects.*;import cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor.*;import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType.Category;import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;import cz.cuni.amis.utils.exception.*;import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdateResult.Result;import cz.cuni.amis.utils.SafeEquals;import cz.cuni.amis.pogamut.base.agent.*;import cz.cuni.amis.pogamut.multi.agent.*;import cz.cuni.amis.pogamut.multi.communication.worldview.property.*;import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.property.*;import cz.cuni.amis.utils.token.*;import cz.cuni.amis.utils.*;
		// --- IMPORTS FROM /messages/settings/javasettings/javaimport END
		
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name='all'] BEGIN
				
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name='all'] END
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name=shared]+classtype[@name=impl] BEGIN
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name=shared]+classtype[@name=impl] END
    
 		/**
         *  
            				Implementation of the shared part of the GameBots2004 message NFO.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Asynchronous message. Sent as response to READY command.
		Information about the game. What type of game is it going to be,
		number of teams, maximum size of teams etc.
	
         */
 	public class GameInfoSharedImpl 
  						extends
  						GameInfoShared
	    {
 	
    
    	
    	public GameInfoSharedImpl(GameInfoSharedImpl source) {
			
				this.myWeaponStay = source.myWeaponStay;
			
				this.myTimeLimit = source.myTimeLimit;
			
				this.myFragLimit = source.myFragLimit;
			
				this.myGoalTeamScore = source.myGoalTeamScore;
			
				this.myMaxTeamSize = source.myMaxTeamSize;
			
				this.myGamePaused = source.myGamePaused;
			
				this.myBotsPaused = source.myBotsPaused;
			
		}
		
		public GameInfoSharedImpl(WorldObjectId objectId, Collection<ISharedProperty> properties) {
			this.Id = (UnrealId)objectId;
			NullCheck.check(this.Id, "objectId");
		
			if (properties.size() != 7) {
				throw new PogamutException("Not enough properties passed to the constructor.", GameInfoSharedImpl.class);
			}
		
			//we have to do some checking in this one to know that we get all properties required
			for ( ISharedProperty property : properties ) {
				PropertyId pId = property.getPropertyId();
				if ( !objectId.equals( property.getObjectId() )) {
					//properties for different objects
					throw new PogamutException("Trying to create a GameInfoSharedImpl with different WorldObjectId properties : " + 
											    this.Id.getStringId() + " / " + property.getObjectId().getStringId() , this);
				}
				if (!GameInfoShared.SharedPropertyTokens.contains(pId.getPropertyToken())) {
				// property that does not belong here
				throw new PogamutException("Trying to create a GameInfoSharedImpl with invalid property (invalid property token): " + 
					this.Id.getStringId() + " / " + property.getPropertyId().getPropertyToken().getToken() , this);
				}
				propertyMap.put(property.getPropertyId(), property);
				
				
					if (pId.getPropertyToken().getToken().equals("WeaponStay"))
					{
						this.myWeaponStay = (BooleanProperty)property;
					}
				
					if (pId.getPropertyToken().getToken().equals("TimeLimit"))
					{
						this.myTimeLimit = (DoubleProperty)property;
					}
				
					if (pId.getPropertyToken().getToken().equals("FragLimit"))
					{
						this.myFragLimit = (IntProperty)property;
					}
				
					if (pId.getPropertyToken().getToken().equals("GoalTeamScore"))
					{
						this.myGoalTeamScore = (LongProperty)property;
					}
				
					if (pId.getPropertyToken().getToken().equals("MaxTeamSize"))
					{
						this.myMaxTeamSize = (IntProperty)property;
					}
				
					if (pId.getPropertyToken().getToken().equals("GamePaused"))
					{
						this.myGamePaused = (BooleanProperty)property;
					}
				
					if (pId.getPropertyToken().getToken().equals("BotsPaused"))
					{
						this.myBotsPaused = (BooleanProperty)property;
					}
				
			}
		}
    
						
						private UnrealId Id = cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo.GameInfoId;
						
						public UnrealId getId() {
							return Id;
						}
					
   				
   				@Override
   				public void setSimTime(long SimTime) {
					super.setSimTime(SimTime);
				}
   			
	    				@Override
	    				public 
	    				GameInfoSharedImpl clone() {
	    					return new 
	    					GameInfoSharedImpl(this);
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
					= null;
					
					
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
					= null;
					
					
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
					= null;
					
					
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
					= null;
					
					
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
					= null;
					
					
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
					= null;
					
					
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
					= null;
					
					
 		/**
         * 
			If the game is paused just for bots - human controlled
			players can normally move.
		 
         */
        public  boolean isBotsPaused()
 	 {
			  			return myBotsPaused.getValue();
			  		}
				
    	
    	
    	public static class GameInfoSharedUpdate
     implements ISharedWorldObjectUpdatedEvent
		{
	
			private GameInfoShared object;
			private long time;
			private ITeamId teamId;
			
			public GameInfoSharedUpdate
    (GameInfoShared data, long time, ITeamId teamId)
			{
				this.object = data;
				this.time = time;
				this.teamId = teamId;
			}
			
			/**
			 * Simulation time in MILLI SECONDS !!!
			 */
			@Override
			public long getSimTime() {
				return this.time;
			}
	
			@Override
			public WorldObjectId getId() {
				return object.getId();
			}
	
			@Override
			public ITeamId getTeamId() {
				return teamId;
			}
			
			@Override
			public Class getCompositeObjectClass()
			{
				return object.getCompositeClass();
			}
	
			@Override
			public Collection<ISharedPropertyUpdatedEvent> getPropertyEvents() {
				LinkedList<ISharedPropertyUpdatedEvent> events = new LinkedList<ISharedPropertyUpdatedEvent>();
				
				for ( ISharedProperty property : object.getProperties().values() )
				{
					if ( property != null)
					{
						events.push( property.createUpdateEvent(time, teamId) );
					}
				}
				return events;
			}
			
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
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=shared+classtype[@name=impl]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=shared+classtype[@name=impl]) ---        	            	
 	
		}
 	