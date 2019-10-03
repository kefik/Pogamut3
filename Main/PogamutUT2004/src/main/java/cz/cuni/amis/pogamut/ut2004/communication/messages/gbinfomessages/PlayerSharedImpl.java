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
            				Implementation of the shared part of the GameBots2004 message PLR.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Synchronous message. Contains information about other players in
		the game, like their current velocity, position, weapon and
		reachability. Only reports those players that are visible.
		(within field of view and not occluded).
	
         */
 	public class PlayerSharedImpl 
  						extends
  						PlayerShared
	    {
 	
    
    	
    	public PlayerSharedImpl(PlayerSharedImpl source) {
			
				this.Id = source.
					getId()
 	;
			
				this.myName = source.myName;
			
				this.mySpectator = source.mySpectator;
			
				this.myAction = source.myAction;
			
				this.myRotation = source.myRotation;
			
				this.myLocation = source.myLocation;
			
				this.myVelocity = source.myVelocity;
			
				this.myTeam = source.myTeam;
			
				this.myWeapon = source.myWeapon;
			
				this.myCrouched = source.myCrouched;
			
				this.myFiring = source.myFiring;
			
				this.myEmotLeft = source.myEmotLeft;
			
				this.myEmotCenter = source.myEmotCenter;
			
				this.myEmotRight = source.myEmotRight;
			
				this.myBubble = source.myBubble;
			
				this.myAnim = source.myAnim;
			
		}
		
		public PlayerSharedImpl(WorldObjectId objectId, Collection<ISharedProperty> properties) {
			this.Id = (UnrealId)objectId;
			NullCheck.check(this.Id, "objectId");
		
			if (properties.size() != 15) {
				throw new PogamutException("Not enough properties passed to the constructor.", PlayerSharedImpl.class);
			}
		
			//we have to do some checking in this one to know that we get all properties required
			for ( ISharedProperty property : properties ) {
				PropertyId pId = property.getPropertyId();
				if ( !objectId.equals( property.getObjectId() )) {
					//properties for different objects
					throw new PogamutException("Trying to create a PlayerSharedImpl with different WorldObjectId properties : " + 
											    this.Id.getStringId() + " / " + property.getObjectId().getStringId() , this);
				}
				if (!PlayerShared.SharedPropertyTokens.contains(pId.getPropertyToken())) {
				// property that does not belong here
				throw new PogamutException("Trying to create a PlayerSharedImpl with invalid property (invalid property token): " + 
					this.Id.getStringId() + " / " + property.getPropertyId().getPropertyToken().getToken() , this);
				}
				propertyMap.put(property.getPropertyId(), property);
				
				
					if (pId.getPropertyToken().getToken().equals("Name"))
					{
						this.myName = (StringProperty)property;
					}
				
					if (pId.getPropertyToken().getToken().equals("Spectator"))
					{
						this.mySpectator = (BooleanProperty)property;
					}
				
					if (pId.getPropertyToken().getToken().equals("Action"))
					{
						this.myAction = (StringProperty)property;
					}
				
					if (pId.getPropertyToken().getToken().equals("Rotation"))
					{
						this.myRotation = (RotationProperty)property;
					}
				
					if (pId.getPropertyToken().getToken().equals("Location"))
					{
						this.myLocation = (LocationProperty)property;
					}
				
					if (pId.getPropertyToken().getToken().equals("Velocity"))
					{
						this.myVelocity = (VelocityProperty)property;
					}
				
					if (pId.getPropertyToken().getToken().equals("Team"))
					{
						this.myTeam = (IntProperty)property;
					}
				
					if (pId.getPropertyToken().getToken().equals("Weapon"))
					{
						this.myWeapon = (StringProperty)property;
					}
				
					if (pId.getPropertyToken().getToken().equals("Crouched"))
					{
						this.myCrouched = (BooleanProperty)property;
					}
				
					if (pId.getPropertyToken().getToken().equals("Firing"))
					{
						this.myFiring = (IntProperty)property;
					}
				
					if (pId.getPropertyToken().getToken().equals("EmotLeft"))
					{
						this.myEmotLeft = (StringProperty)property;
					}
				
					if (pId.getPropertyToken().getToken().equals("EmotCenter"))
					{
						this.myEmotCenter = (StringProperty)property;
					}
				
					if (pId.getPropertyToken().getToken().equals("EmotRight"))
					{
						this.myEmotRight = (StringProperty)property;
					}
				
					if (pId.getPropertyToken().getToken().equals("Bubble"))
					{
						this.myBubble = (StringProperty)property;
					}
				
					if (pId.getPropertyToken().getToken().equals("Anim"))
					{
						this.myAnim = (StringProperty)property;
					}
				
			}
		}
    
   				
   				@Override
   				public void setSimTime(long SimTime) {
					super.setSimTime(SimTime);
				}
   			
	    				@Override
	    				public 
	    				PlayerSharedImpl clone() {
	    					return new 
	    					PlayerSharedImpl(this);
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
        protected
         UnrealId Id =
       	null;
	
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
					= null;
					
					
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
					= null;
					
					
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
					= null;
					
					
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
					= null;
					
					
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
					= null;
					
					
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
					= null;
					
					
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
					= null;
					
					
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
					= null;
					
					
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
					= null;
					
					
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
					= null;
					
					
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
					= null;
					
					
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
					= null;
					
					
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
					= null;
					
					
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
					= null;
					
					
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
					= null;
					
					
 		/**
         * 
			For UE2. Current played animation of the bot.
		 
         */
        public  String getAnim()
 	 {
			  			return myAnim.getValue();
			  		}
				
    	
    	
    	public static class PlayerSharedUpdate
     implements ISharedWorldObjectUpdatedEvent
		{
	
			private PlayerShared object;
			private long time;
			private ITeamId teamId;
			
			public PlayerSharedUpdate
    (PlayerShared data, long time, ITeamId teamId)
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
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=shared+classtype[@name=impl]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=shared+classtype[@name=impl]) ---        	            	
 	
		}
 	