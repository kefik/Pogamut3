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
             				Implementation of the GameBots2004 message TES contains also its Local/Shared/Static subpart class definitions..  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Synchronous message. Contains the info about team score.
	
         */
 	public class TeamScoreMessage   
  				extends 
  				TeamScore
  						implements IWorldObjectUpdatedEvent, ICompositeWorldObjectUpdatedEvent
  						
	    {
 	
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public TeamScoreMessage()
		{
		}
	
    	
    	
    	
    	/**
		 * Creates new instance of the message TeamScore.
		 * 
		Synchronous message. Contains the info about team score.
	
		 * Corresponding GameBots message
		 *   
		 *   is
		 *   TES.
		 * 
 	  	 * 
		 *   
		 *     @param Id Message identifier.
		 *   
		 * 
		 *   
		 *     @param Team Team identifier.
		 *   
		 * 
		 *   
		 *     @param Score 
			The score of the team (can be some special measurement that
			differs from game type to game type - number of stolen flags
			in CTF game, number of team frags in TeamGame, etc.)
		
		 *   
		 * 
		 */
		public TeamScoreMessage(
			UnrealId Id,  Integer Team,  Integer Score
		) {
			
					this.Id = Id;
				
					this.Team = Team;
				
					this.Score = Score;
				
		}
    
		/**
		 * Cloning constructor from the full message.
		 *
		 * @param original
		 */
		public TeamScoreMessage(TeamScoreMessage original) {		
			
					this.Id = original.getId()
 	;
				
					this.Team = original.getTeam()
 	;
				
					this.Score = original.getScore()
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
         * Message identifier. 
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
         * Message identifier. 
         */
        public  UnrealId getId()
 	 {
		    					return Id;
		    				}
		    			
    	
	    /**
         * Team identifier. 
         */
        protected
         Integer Team =
       	255;
	
    						
    						/**
		 					 * Whether property 'Team' was received from GB2004.
		 					 */
							protected boolean Team_Set = false;
							
    						@Override
		    				
 		/**
         * Team identifier. 
         */
        public  Integer getTeam()
 	 {
		    					return Team;
		    				}
		    			
    	
	    /**
         * 
			The score of the team (can be some special measurement that
			differs from game type to game type - number of stolen flags
			in CTF game, number of team frags in TeamGame, etc.)
		 
         */
        protected
         Integer Score =
       	0;
	
    						
    						/**
		 					 * Whether property 'Score' was received from GB2004.
		 					 */
							protected boolean Score_Set = false;
							
    						@Override
		    				
 		/**
         * 
			The score of the team (can be some special measurement that
			differs from game type to game type - number of stolen flags
			in CTF game, number of team frags in TeamGame, etc.)
		 
         */
        public  Integer getScore()
 	 {
		    					return Score;
		    				}
		    			
		    			
		    			private TeamScoreLocal localPart = null;
		    			
		    			@Override
						public TeamScoreLocal 
						getLocal() {
							if (localPart != null) return localPart;
							return localPart = new 
								TeamScoreLocalMessage();
						}
					
						private TeamScoreShared sharedPart = null;
					
						@Override
						public TeamScoreShared 
						getShared() {
							if (sharedPart != null) return sharedPart;							
							return sharedPart = new 
								TeamScoreSharedMessage();
						}
					
						private TeamScoreStatic staticPart = null; 
					
						@Override
						public TeamScoreStatic 
						getStatic() {
							if (staticPart != null) return staticPart;
							return staticPart = new 
								TeamScoreStaticMessage();
						}
    				
 		/**
         *  
            				Implementation of the local part of the GameBots2004 message TES, used
            				to facade TESMessage.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Synchronous message. Contains the info about team score.
	
         */
 	public class TeamScoreLocalMessage 
	  					extends
  						TeamScoreLocal
	    {
 	
		    			@Override
		    			public 
		    			TeamScoreLocalMessage clone() {
		    				return this;
		    			}
		    			
		    				public TeamScoreLocalMessage getLocal() {
								return this;
					    	}
							public ISharedWorldObject getShared() {
							 	throw new UnsupportedOperationException("Could not return LOCAL as SHARED");
							}
							public IStaticWorldObject getStatic() {
							    throw new UnsupportedOperationException("Could not return LOCAL as STATIC");
							}
		    			
 		/**
         * Message identifier. 
         */
        public  UnrealId getId()
 	 {
				    					return Id;
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
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=local+classtype[@name=message]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=local+classtype[@name=message]) ---        	            	
 	
		}
 	
 		/**
         *  
            				Implementation of the static part of the GameBots2004 message TES, used
            				to facade TESMessage.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Synchronous message. Contains the info about team score.
	
         */
 	public class TeamScoreStaticMessage 
	  					extends
  						TeamScoreStatic
	    {
 	
		    			@Override
		    			public 
		    			TeamScoreStaticMessage clone() {
		    				return this;
		    			}
		    			
 		/**
         * Message identifier. 
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
 				TeamScoreStatic obj = (TeamScoreStatic) other;

 				
 						if ( !(
 	 			AdvancedEquals.equalsOrNull(this.getId()
 	, obj.getId()
 	)
 	 		) )
						{
							System.out.println("!!!!!PROPERTY UPDATE ERROR!!!! on property Id on object class TeamScoreStatic");
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
            				Implementation of the shared part of the GameBots2004 message TES, used
            				to facade TESMessage.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Synchronous message. Contains the info about team score.
	
         */
 	public class TeamScoreSharedMessage 
	  					extends
  						TeamScoreShared
	    {
 	
    	
    	
		public TeamScoreSharedMessage()
		{
			
				propertyMap.put(myTeam.getPropertyId(), myTeam);
			
				propertyMap.put(myScore.getPropertyId(), myScore);
			
		}		
    
		    			@Override
		    			public 
		    			TeamScoreSharedMessage clone() {
		    				return this;
		    			}
		    			
		
		
		
		protected HashMap<PropertyId, ISharedProperty> propertyMap = new HashMap<PropertyId, ISharedProperty>(
			2
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
         * Message identifier. 
         */
        public  UnrealId getId()
 	 {
  			return Id;
  		}
  		
    	
	    /**
         * Team identifier. 
         */
        protected
         IntegerProperty 
        myTeam
					= new
					IntegerProperty
					(
						getId(), 
						"Team", 
						Team, 
						TeamScore.class
					);
					
 		/**
         * Team identifier. 
         */
        public  Integer getTeam()
 	 {
			  			return myTeam.getValue();
			  		}
				
    	
	    /**
         * 
			The score of the team (can be some special measurement that
			differs from game type to game type - number of stolen flags
			in CTF game, number of team frags in TeamGame, etc.)
		 
         */
        protected
         IntegerProperty 
        myScore
					= new
					IntegerProperty
					(
						getId(), 
						"Score", 
						Score, 
						TeamScore.class
					);
					
 		/**
         * 
			The score of the team (can be some special measurement that
			differs from game type to game type - number of stolen flags
			in CTF game, number of team frags in TeamGame, etc.)
		 
         */
        public  Integer getScore()
 	 {
			  			return myScore.getValue();
			  		}
				
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
		              			"Id = " + String.valueOf(getId()
 	) + " | " + 
		              		
		              			"Team = " + String.valueOf(getTeam()
 	) + " | " + 
		              		
		              			"Score = " + String.valueOf(getScore()
 	) + " | " + 
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>Id</b> = " + String.valueOf(getId()
 	) + " <br/> " + 
		              		
		              			"<b>Team</b> = " + String.valueOf(getTeam()
 	) + " <br/> " + 
		              		
		              			"<b>Score</b> = " + String.valueOf(getScore()
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
			if (!( object instanceof TeamScoreMessage) ) {
				throw new PogamutException("Can't update different class than TeamScoreMessage, got class " + object.getClass().getSimpleName() + "!", this);		
			}
			TeamScoreMessage toUpdate = (TeamScoreMessage)object;
			
			boolean updated = false;
			
			// UPDATING LOCAL PROPERTIES
			
         	
         	// UPDATING SHARED PROPERTIES
         	
				if (!SafeEquals.equals(toUpdate.Team, getTeam()
 	)) {
					toUpdate.Team=getTeam()
 	;
					updated = true;
				}
			
				if (!SafeEquals.equals(toUpdate.Score, getScore()
 	)) {
					toUpdate.Score=getScore()
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
			return new TeamScoreLocalImpl.TeamScoreLocalUpdate
    (this.getLocal(), SimTime);
		}

		@Override
		public ISharedWorldObjectUpdatedEvent getSharedEvent() {
			return new TeamScoreSharedImpl.TeamScoreSharedUpdate
    (this.getShared(), SimTime, this.getTeamId());
		}

		@Override
		public IStaticWorldObjectUpdatedEvent getStaticEvent() {
			return new TeamScoreStaticImpl.TeamScoreStaticUpdate
    (this.getStatic(), SimTime);
		}
 	
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
		              			"Id = " + String.valueOf(getId()
 	) + " | " + 
		              		
		              			"Team = " + String.valueOf(getTeam()
 	) + " | " + 
		              		
		              			"Score = " + String.valueOf(getScore()
 	) + " | " + 
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>Id</b> = " + String.valueOf(getId()
 	) + " <br/> " + 
		              		
		              			"<b>Team</b> = " + String.valueOf(getTeam()
 	) + " <br/> " + 
		              		
		              			"<b>Score</b> = " + String.valueOf(getScore()
 	) + " <br/> " + 
		              		
				"<br/>]";     
		}
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=base+classtype[@name=message]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=base+classtype[@name=message]) ---        	            	
 	
		}
 	