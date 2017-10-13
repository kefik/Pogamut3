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
            				Abstract definition of the GameBots2004 message NAV.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Synchronous message - however only NavPoints marking item pickup locations are exported synchronously. Other NavPoints are not exported synchronously at all, even if the bot can actually see them (but note that ALL NavPoints are exported in the handshake between bot and the server). Exporting NavPoints synchronously took a lot of UT server resources with limited information gain (in Pogamut there is now available visibility matrix holding static information which points can be seen from other points). NavPoint carries information about UT navigation point - location, paths and some additional information are stored there (if it is an ambush point, or sniper point, etc.).
   
         */
 	public abstract class NavPoint   
  				extends 
  				InfoMessage
  						implements IWorldEvent, IWorldChangeEvent, ICompositeWorldObject
  						
	    		,ILocated
	    		,ILocomotive
	    		,IGBViewable
	    {
 	
    	/** Example how the message looks like - used during parser tests. */
    	public static final String PROTOTYPE =
    		"NAV {Id unreal_id}  {Location 0,0,0}  {Velocity 0,0,0}  {Visible False}  {Item unreal_id}  {ItemClass xWeapons.FlakCannonPickup}  {ItemSpawned False}  {DoorOpened False}  {Mover unreal_id}  {LiftOffset 0,0,0}  {LiftJumpExit False}  {NoDoubleJump False}  {InvSpot False}  {PlayerStart False}  {TeamNumber 0}  {DomPoint False}  {DomPointController 0}  {Door False}  {LiftCenter False}  {LiftExit False}  {AIMarker False}  {JumpSpot False}  {JumpPad False}  {JumpDest False}  {Teleporter False}  {Rotation 0,0,0}  {RoamingSpot False}  {SnipingSpot False}  {PreferedWeapon text} ";
    
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public NavPoint()
		{
		}
	
				// abstract message, it does not have any more constructors				
			
	   		
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
			A unique Id of this navigation point assigned by the game.
		 
         */
        public abstract UnrealId getId()
 	;
		    			
 		/**
         * Location of navigation point. 
         */
        public abstract Location getLocation()
 	;
		    			
 		/**
         * 
			Velocity of the navigation point (if the navigation point is
			currently moving). Not sent at the moment.
		 
         */
        public abstract Velocity getVelocity()
 	;
		    			
 		/**
         * 
			If the point is in the field of view of the bot.
		 
         */
        public abstract boolean isVisible()
 	;
		    			
 		/**
         * 
			Unique Id of the respawned item (the item respawns at this
			point). Not sent if point is not an inventory spot. Sent only in HandShake.
		 
         */
        public abstract UnrealId getItem()
 	;
		    			
 		/**
         * 
            Class of the item (e.g. xWeapons.FlakCannonPickup). Not sent if point is not an inventory spot. Sent only in HandShake.
         
         */
        public abstract ItemType getItemClass()
 	;
		    			
 		/**
         * 
            True if the item is spawned at the point. Not sent if point is not an inventory spot. 
         
         */
        public abstract boolean isItemSpawned()
 	;
		    			
 		/**
         * 
            True if this NavPoint is a Door and door is opened. Not sent if point is not a door.
         
         */
        public abstract boolean isDoorOpened()
 	;
		    			
 		/**
         * 
            If this NavPoint is marking some mover, the mover id will be here. Not sent if point is not a Door, a LiftCenter or a LiftExit. Sent only in HandShake.
         
         */
        public abstract UnrealId getMover()
 	;
		    			
 		/**
         * 
            Starting vector between MyLift location and LiftCenter location. Not sent if point is not a LiftCenter. Sent only in HandShake.
         
         */
        public abstract Vector3d getLiftOffset()
 	;
		    			
 		/**
         * 
            Boolean. If we can/should exit the lift by a jump when near the destination place. Not sent if point is not a LiftExit. Sent only in HandShake.
         
         */
        public abstract boolean isLiftJumpExit()
 	;
		    			
 		/**
         * 
            Boolean. If we should or not use double jump when exiting lift with a jump. Not sent if point is not a LiftExit. Sent only in HandShake.
         
         */
        public abstract boolean isNoDoubleJump()
 	;
		    			
 		/**
         * 
			If this is an inventory spot (item is respawned at this point).
		 
         */
        public abstract boolean isInvSpot()
 	;
		    			
 		/**
         * 
			If this is a player start (players and/or bots are respawned at this point).
		 
         */
        public abstract boolean isPlayerStart()
 	;
		    			
 		/**
         * 
			Will be sent if this is a player start. In Team games (team deathmatch, capture the flag, domination) holds information about which team respawns at this player start spot. In non-team games will return 0!
		 
         */
        public abstract int getTeamNumber()
 	;
		    			
 		/**
         * If this point marks a DominationPoint (for BotDoubleDomination game). 
         */
        public abstract boolean isDomPoint()
 	;
		    			
 		/**
         * Exported if this NavPoint is a DominationPoint (for BotDoubleDomination game) - which team controls this point. 
         */
        public abstract int getDomPointController()
 	;
		    			
 		/**
         * 
			If this point marks a door mover.
		 
         */
        public abstract boolean isDoor()
 	;
		    			
 		/**
         * 
			If this point marks a lift center (used to mark center of a lift mover, note that this point will be always moved with the lift).
		 
         */
        public abstract boolean isLiftCenter()
 	;
		    			
 		/**
         * 
			If this point marks a lift exit (used to mark exit point of a lift mover).
		 
         */
        public abstract boolean isLiftExit()
 	;
		    			
 		/**
         * 
			If this point is an AI marker - marks an interesting spot in the environment. May be ambush point or sniping spot, etc.
		 
         */
        public abstract boolean isAIMarker()
 	;
		    			
 		/**
         * 
			If this point marks a jump spot (a special device that causes the bot to jump high or far).
		 
         */
        public abstract boolean isJumpSpot()
 	;
		    			
 		/**
         * 
			If this point marks a jump pad (a special device that causes the bot to jump high or far).
		 
         */
        public abstract boolean isJumpPad()
 	;
		    			
 		/**
         * 
			If this point marks a jump destination - some place that can be reached by some special jump.
		 
         */
        public abstract boolean isJumpDest()
 	;
		    			
 		/**
         * 
			If this point marks a teleport. 
		 
         */
        public abstract boolean isTeleporter()
 	;
		    			
 		/**
         * 
			If the type is AIMarker. The rotation the bot should be
			facing, when doing the action specified by AIMarker. Sent only in HandShake.
		 
         */
        public abstract Rotation getRotation()
 	;
		    			
 		/**
         * 
			Some ambush point, where is good chance to intercept
			approaching opponents. Sent only in HandShake.
		 
         */
        public abstract boolean isRoamingSpot()
 	;
		    			
 		/**
         * Point good for sniping. Sent only in HandShake. 
         */
        public abstract boolean isSnipingSpot()
 	;
		    			
 		/**
         * If item should be present at this navpoint it's instance will be here. 
         */
        public abstract Item getItemInstance()
 	;
		    			
 		/**
         * Maps edge-LEADING_TO-navpoint-UnrealId to neighbour link, those are outgoing edges (those edges that originates in this navpoint going to another one, those you may usually travel). 
         */
        public abstract Map<UnrealId, NavPointNeighbourLink> getOutgoingEdges()
 	;
		    			
 		/**
         * Maps edge-ORIGINATES_FROM-navpoint-UnrealId to neighbour link, those are incoming edges (those edges that originates in different navpoint and ends here, do not use this to ask whether you can get to navpoint of specific unreal id, use OutgoingEdges instead). 
         */
        public abstract Map<UnrealId, NavPointNeighbourLink> getIncomingEdges()
 	;
		    			
 		/**
         * 
			Class of the weapon that should be prefered when using this
			point for AIMarker specified action. Sent only in HandShake.
		 
         */
        public abstract String getPreferedWeapon()
 	;
		    			
		 	@Override
			public IWorldObjectUpdatedEvent createDisappearEvent() {
				return new ObjectDisappeared(this, getSimTime());
			}
			
			public static class ObjectDisappeared implements IWorldObjectUpdatedEvent
			{
				
				public ObjectDisappeared(NavPoint obj, long time) {
					this.obj = obj;
					this.time = time;
				}
				
				private NavPoint obj;
				private long time;
		
				@Override
				public WorldObjectId getId() {
					return obj.getId();
				}
		
		        /**
		         * Simulation time in MILLI SECONDS !!!
		         */
				@Override
				public long getSimTime() {
					return time;
				}
		
				@Override
				public IWorldObjectUpdateResult<IWorldObject> update(IWorldObject obj) {
					if (obj == null) {
						throw new PogamutException("Can't 'disappear' null!", this);
					}
					if (!(obj instanceof NavPointMessage)) {
						throw new PogamutException("Can't update different class than NavPointMessage, got class " + obj.getClass().getSimpleName() + "!", this);
					}
					NavPointMessage toUpdate = (NavPointMessage)obj;
					if (toUpdate.Visible) {
						toUpdate.Visible = false;
						return new IWorldObjectUpdateResult.WorldObjectUpdateResult(IWorldObjectUpdateResult.Result.UPDATED, obj);
					} else {
						return new IWorldObjectUpdateResult.WorldObjectUpdateResult(IWorldObjectUpdateResult.Result.SAME, obj);
					}
				}
		
			}
	
		
    	
    	public static class NavPointUpdate
     extends GBObjectUpdate implements ICompositeWorldObjectUpdatedEvent, IGBWorldObjectEvent {	
			private NavPoint object;
			private long time;
			private ITeamId teamId;
			
			public NavPointUpdate
    (NavPoint source, long eventTime, ITeamId teamId) {
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
				return new NavPointLocalImpl.NavPointLocalUpdate
    ((NavPointLocal)object.getLocal(), time);
			}
	
			@Override
			public ISharedWorldObjectUpdatedEvent getSharedEvent() {
				return new NavPointSharedImpl.NavPointSharedUpdate
    ((NavPointShared)object.getShared(), time, teamId);
			}
	
			@Override
			public IStaticWorldObjectUpdatedEvent getStaticEvent() {
				return new NavPointStaticImpl.NavPointStaticUpdate
    ((NavPointStatic)object.getStatic(), time);
			}
			
		}
    
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
		              			"Id = " + String.valueOf(getId()
 	) + " | " + 
		              		
		              			"Location = " + String.valueOf(getLocation()
 	) + " | " + 
		              		
		              			"Velocity = " + String.valueOf(getVelocity()
 	) + " | " + 
		              		
		              			"Visible = " + String.valueOf(isVisible()
 	) + " | " + 
		              		
		              			"Item = " + String.valueOf(getItem()
 	) + " | " + 
		              		
		              			"ItemClass = " + String.valueOf(getItemClass()
 	) + " | " + 
		              		
		              			"ItemSpawned = " + String.valueOf(isItemSpawned()
 	) + " | " + 
		              		
		              			"DoorOpened = " + String.valueOf(isDoorOpened()
 	) + " | " + 
		              		
		              			"Mover = " + String.valueOf(getMover()
 	) + " | " + 
		              		
		              			"LiftOffset = " + String.valueOf(getLiftOffset()
 	) + " | " + 
		              		
		              			"LiftJumpExit = " + String.valueOf(isLiftJumpExit()
 	) + " | " + 
		              		
		              			"NoDoubleJump = " + String.valueOf(isNoDoubleJump()
 	) + " | " + 
		              		
		              			"InvSpot = " + String.valueOf(isInvSpot()
 	) + " | " + 
		              		
		              			"PlayerStart = " + String.valueOf(isPlayerStart()
 	) + " | " + 
		              		
		              			"TeamNumber = " + String.valueOf(getTeamNumber()
 	) + " | " + 
		              		
		              			"DomPoint = " + String.valueOf(isDomPoint()
 	) + " | " + 
		              		
		              			"DomPointController = " + String.valueOf(getDomPointController()
 	) + " | " + 
		              		
		              			"Door = " + String.valueOf(isDoor()
 	) + " | " + 
		              		
		              			"LiftCenter = " + String.valueOf(isLiftCenter()
 	) + " | " + 
		              		
		              			"LiftExit = " + String.valueOf(isLiftExit()
 	) + " | " + 
		              		
		              			"AIMarker = " + String.valueOf(isAIMarker()
 	) + " | " + 
		              		
		              			"JumpSpot = " + String.valueOf(isJumpSpot()
 	) + " | " + 
		              		
		              			"JumpPad = " + String.valueOf(isJumpPad()
 	) + " | " + 
		              		
		              			"JumpDest = " + String.valueOf(isJumpDest()
 	) + " | " + 
		              		
		              			"Teleporter = " + String.valueOf(isTeleporter()
 	) + " | " + 
		              		
		              			"Rotation = " + String.valueOf(getRotation()
 	) + " | " + 
		              		
		              			"RoamingSpot = " + String.valueOf(isRoamingSpot()
 	) + " | " + 
		              		
		              			"SnipingSpot = " + String.valueOf(isSnipingSpot()
 	) + " | " + 
		              		
		              			"PreferedWeapon = " + String.valueOf(getPreferedWeapon()
 	) + " | " + 
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>Id</b> = " + String.valueOf(getId()
 	) + " <br/> " + 
		              		
		              			"<b>Location</b> = " + String.valueOf(getLocation()
 	) + " <br/> " + 
		              		
		              			"<b>Velocity</b> = " + String.valueOf(getVelocity()
 	) + " <br/> " + 
		              		
		              			"<b>Visible</b> = " + String.valueOf(isVisible()
 	) + " <br/> " + 
		              		
		              			"<b>Item</b> = " + String.valueOf(getItem()
 	) + " <br/> " + 
		              		
		              			"<b>ItemClass</b> = " + String.valueOf(getItemClass()
 	) + " <br/> " + 
		              		
		              			"<b>ItemSpawned</b> = " + String.valueOf(isItemSpawned()
 	) + " <br/> " + 
		              		
		              			"<b>DoorOpened</b> = " + String.valueOf(isDoorOpened()
 	) + " <br/> " + 
		              		
		              			"<b>Mover</b> = " + String.valueOf(getMover()
 	) + " <br/> " + 
		              		
		              			"<b>LiftOffset</b> = " + String.valueOf(getLiftOffset()
 	) + " <br/> " + 
		              		
		              			"<b>LiftJumpExit</b> = " + String.valueOf(isLiftJumpExit()
 	) + " <br/> " + 
		              		
		              			"<b>NoDoubleJump</b> = " + String.valueOf(isNoDoubleJump()
 	) + " <br/> " + 
		              		
		              			"<b>InvSpot</b> = " + String.valueOf(isInvSpot()
 	) + " <br/> " + 
		              		
		              			"<b>PlayerStart</b> = " + String.valueOf(isPlayerStart()
 	) + " <br/> " + 
		              		
		              			"<b>TeamNumber</b> = " + String.valueOf(getTeamNumber()
 	) + " <br/> " + 
		              		
		              			"<b>DomPoint</b> = " + String.valueOf(isDomPoint()
 	) + " <br/> " + 
		              		
		              			"<b>DomPointController</b> = " + String.valueOf(getDomPointController()
 	) + " <br/> " + 
		              		
		              			"<b>Door</b> = " + String.valueOf(isDoor()
 	) + " <br/> " + 
		              		
		              			"<b>LiftCenter</b> = " + String.valueOf(isLiftCenter()
 	) + " <br/> " + 
		              		
		              			"<b>LiftExit</b> = " + String.valueOf(isLiftExit()
 	) + " <br/> " + 
		              		
		              			"<b>AIMarker</b> = " + String.valueOf(isAIMarker()
 	) + " <br/> " + 
		              		
		              			"<b>JumpSpot</b> = " + String.valueOf(isJumpSpot()
 	) + " <br/> " + 
		              		
		              			"<b>JumpPad</b> = " + String.valueOf(isJumpPad()
 	) + " <br/> " + 
		              		
		              			"<b>JumpDest</b> = " + String.valueOf(isJumpDest()
 	) + " <br/> " + 
		              		
		              			"<b>Teleporter</b> = " + String.valueOf(isTeleporter()
 	) + " <br/> " + 
		              		
		              			"<b>Rotation</b> = " + String.valueOf(getRotation()
 	) + " <br/> " + 
		              		
		              			"<b>RoamingSpot</b> = " + String.valueOf(isRoamingSpot()
 	) + " <br/> " + 
		              		
		              			"<b>SnipingSpot</b> = " + String.valueOf(isSnipingSpot()
 	) + " <br/> " + 
		              		
		              			"<b>PreferedWeapon</b> = " + String.valueOf(getPreferedWeapon()
 	) + " <br/> " + 
		              		
				"<br/>]";     
		}
 	
 	    public String toJsonLiteral() {
            return "navpoint( "
            		+
									(getId()
 	 == null ? "null" :
										"\"" + getId()
 	.getStringId() + "\"" 
									)
								+ ", " + 
								    (getLocation()
 	 == null ? "null" :
										"[" + getLocation()
 	.getX() + ", " + getLocation()
 	.getY() + ", " + getLocation()
 	.getZ() + "]" 
									)
								+ ", " + 
								    (getVelocity()
 	 == null ? "null" :
										"[" + getVelocity()
 	.getX() + ", " + getVelocity()
 	.getY() + ", " + getVelocity()
 	.getZ() + "]" 
									)
								+ ", " + 
								    String.valueOf(isVisible()
 	)									
								+ ", " + 
									(getItem()
 	 == null ? "null" :
										"\"" + getItem()
 	.getStringId() + "\"" 
									)
								+ ", " + 
									(getItemClass()
 	 == null ? "null" :
										"\"" + getItemClass()
 	.getName() + "\"" 
									)
								+ ", " + 
								    String.valueOf(isItemSpawned()
 	)									
								+ ", " + 
								    String.valueOf(isDoorOpened()
 	)									
								+ ", " + 
									(getMover()
 	 == null ? "null" :
										"\"" + getMover()
 	.getStringId() + "\"" 
									)
								+ ", " + 
									(getLiftOffset()
 	 == null ? "null" :
										"[" + getLiftOffset()
 	.getX() + ", " + getLiftOffset()
 	.getY() + ", " + getLiftOffset()
 	.getZ() + "]" 
									)
								+ ", " + 
								    String.valueOf(isLiftJumpExit()
 	)									
								+ ", " + 
								    String.valueOf(isNoDoubleJump()
 	)									
								+ ", " + 
								    String.valueOf(isInvSpot()
 	)									
								+ ", " + 
								    String.valueOf(isPlayerStart()
 	)									
								+ ", " + 
								    String.valueOf(getTeamNumber()
 	)									
								+ ", " + 
								    String.valueOf(isDomPoint()
 	)									
								+ ", " + 
								    String.valueOf(getDomPointController()
 	)									
								+ ", " + 
								    String.valueOf(isDoor()
 	)									
								+ ", " + 
								    String.valueOf(isLiftCenter()
 	)									
								+ ", " + 
								    String.valueOf(isLiftExit()
 	)									
								+ ", " + 
								    String.valueOf(isAIMarker()
 	)									
								+ ", " + 
								    String.valueOf(isJumpSpot()
 	)									
								+ ", " + 
								    String.valueOf(isJumpPad()
 	)									
								+ ", " + 
								    String.valueOf(isJumpDest()
 	)									
								+ ", " + 
								    String.valueOf(isTeleporter()
 	)									
								+ ", " + 
									(getRotation()
 	 == null ? "null" :
										"[" + getRotation()
 	.getPitch() + ", " + getRotation()
 	.getYaw() + ", " + getRotation()
 	.getRoll() + "]" 
									)								    
								+ ", " + 
								    String.valueOf(isRoamingSpot()
 	)									
								+ ", " + 
								    String.valueOf(isSnipingSpot()
 	)									
								+ ", " + 
									(getPreferedWeapon()
 	 == null ? "null" :
										"\"" + getPreferedWeapon()
 	 + "\"" 
									)
								
                   + ")";
        }
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=base+classtype[@name=abstract]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=base+classtype[@name=abstract]) ---        	            	
 	
		}
 	