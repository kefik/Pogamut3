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
             				Implementation of the GameBots2004 message NAV contains also its Local/Shared/Static subpart class definitions..  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Synchronous message - however only NavPoints marking item pickup locations are exported synchronously. Other NavPoints are not exported synchronously at all, even if the bot can actually see them (but note that ALL NavPoints are exported in the handshake between bot and the server). Exporting NavPoints synchronously took a lot of UT server resources with limited information gain (in Pogamut there is now available visibility matrix holding static information which points can be seen from other points). NavPoint carries information about UT navigation point - location, paths and some additional information are stored there (if it is an ambush point, or sniper point, etc.).
   
         */
 	public class NavPointMessage   
  				extends 
  				NavPoint
  						implements IWorldObjectUpdatedEvent, ICompositeWorldObjectUpdatedEvent
  						
	    {
 	
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public NavPointMessage()
		{
		}
	
    	
    	
    	
    	/**
		 * Creates new instance of the message NavPoint.
		 * 
		Synchronous message - however only NavPoints marking item pickup locations are exported synchronously. Other NavPoints are not exported synchronously at all, even if the bot can actually see them (but note that ALL NavPoints are exported in the handshake between bot and the server). Exporting NavPoints synchronously took a lot of UT server resources with limited information gain (in Pogamut there is now available visibility matrix holding static information which points can be seen from other points). NavPoint carries information about UT navigation point - location, paths and some additional information are stored there (if it is an ambush point, or sniper point, etc.).
   
		 * Corresponding GameBots message
		 *   
		 *   is
		 *   NAV.
		 * 
 	  	 * 
		 *   
		 *     @param Id 
			A unique Id of this navigation point assigned by the game.
		
		 *   
		 * 
		 *   
		 *     @param Location Location of navigation point.
		 *   
		 * 
		 *   
		 *     @param Velocity 
			Velocity of the navigation point (if the navigation point is
			currently moving). Not sent at the moment.
		
		 *   
		 * 
		 *   
		 *     @param Visible 
			If the point is in the field of view of the bot.
		
		 *   
		 * 
		 *   
		 *     @param Item 
			Unique Id of the respawned item (the item respawns at this
			point). Not sent if point is not an inventory spot. Sent only in HandShake.
		
		 *   
		 * 
		 *   
		 *     @param ItemClass 
            Class of the item (e.g. xWeapons.FlakCannonPickup). Not sent if point is not an inventory spot. Sent only in HandShake.
        
		 *   
		 * 
		 *   
		 *     @param ItemSpawned 
            True if the item is spawned at the point. Not sent if point is not an inventory spot. 
        
		 *   
		 * 
		 *   
		 *     @param DoorOpened 
            True if this NavPoint is a Door and door is opened. Not sent if point is not a door.
        
		 *   
		 * 
		 *   
		 *     @param Mover 
            If this NavPoint is marking some mover, the mover id will be here. Not sent if point is not a Door, a LiftCenter or a LiftExit. Sent only in HandShake.
        
		 *   
		 * 
		 *   
		 *     @param LiftOffset 
            Starting vector between MyLift location and LiftCenter location. Not sent if point is not a LiftCenter. Sent only in HandShake.
        
		 *   
		 * 
		 *   
		 *     @param LiftJumpExit 
            Boolean. If we can/should exit the lift by a jump when near the destination place. Not sent if point is not a LiftExit. Sent only in HandShake.
        
		 *   
		 * 
		 *   
		 *     @param NoDoubleJump 
            Boolean. If we should or not use double jump when exiting lift with a jump. Not sent if point is not a LiftExit. Sent only in HandShake.
        
		 *   
		 * 
		 *   
		 *     @param InvSpot 
			If this is an inventory spot (item is respawned at this point).
		
		 *   
		 * 
		 *   
		 *     @param PlayerStart 
			If this is a player start (players and/or bots are respawned at this point).
		
		 *   
		 * 
		 *   
		 *     @param TeamNumber 
			Will be sent if this is a player start. In Team games (team deathmatch, capture the flag, domination) holds information about which team respawns at this player start spot. In non-team games will return 0!
		
		 *   
		 * 
		 *   
		 *     @param DomPoint If this point marks a DominationPoint (for BotDoubleDomination game).
		 *   
		 * 
		 *   
		 *     @param DomPointController Exported if this NavPoint is a DominationPoint (for BotDoubleDomination game) - which team controls this point.
		 *   
		 * 
		 *   
		 *     @param Door 
			If this point marks a door mover.
		
		 *   
		 * 
		 *   
		 *     @param LiftCenter 
			If this point marks a lift center (used to mark center of a lift mover, note that this point will be always moved with the lift).
		
		 *   
		 * 
		 *   
		 *     @param LiftExit 
			If this point marks a lift exit (used to mark exit point of a lift mover).
		
		 *   
		 * 
		 *   
		 *     @param AIMarker 
			If this point is an AI marker - marks an interesting spot in the environment. May be ambush point or sniping spot, etc.
		
		 *   
		 * 
		 *   
		 *     @param JumpSpot 
			If this point marks a jump spot (a special device that causes the bot to jump high or far).
		
		 *   
		 * 
		 *   
		 *     @param JumpPad 
			If this point marks a jump pad (a special device that causes the bot to jump high or far).
		
		 *   
		 * 
		 *   
		 *     @param JumpDest 
			If this point marks a jump destination - some place that can be reached by some special jump.
		
		 *   
		 * 
		 *   
		 *     @param Teleporter 
			If this point marks a teleport. 
		
		 *   
		 * 
		 *   
		 *     @param Rotation 
			If the type is AIMarker. The rotation the bot should be
			facing, when doing the action specified by AIMarker. Sent only in HandShake.
		
		 *   
		 * 
		 *   
		 *     @param RoamingSpot 
			Some ambush point, where is good chance to intercept
			approaching opponents. Sent only in HandShake.
		
		 *   
		 * 
		 *   
		 *     @param SnipingSpot Point good for sniping. Sent only in HandShake.
		 *   
		 * 
		 *   
		 *     @param ItemInstance If item should be present at this navpoint it's instance will be here.
		 *   
		 * 
		 *   
		 *     @param OutgoingEdges Maps edge-LEADING_TO-navpoint-UnrealId to neighbour link, those are outgoing edges (those edges that originates in this navpoint going to another one, those you may usually travel).
		 *   
		 * 
		 *   
		 *     @param IncomingEdges Maps edge-ORIGINATES_FROM-navpoint-UnrealId to neighbour link, those are incoming edges (those edges that originates in different navpoint and ends here, do not use this to ask whether you can get to navpoint of specific unreal id, use OutgoingEdges instead).
		 *   
		 * 
		 *   
		 *     @param PreferedWeapon 
			Class of the weapon that should be prefered when using this
			point for AIMarker specified action. Sent only in HandShake.
		
		 *   
		 * 
		 */
		public NavPointMessage(
			UnrealId Id,  Location Location,  Velocity Velocity,  boolean Visible,  UnrealId Item,  ItemType ItemClass,  boolean ItemSpawned,  boolean DoorOpened,  UnrealId Mover,  Vector3d LiftOffset,  boolean LiftJumpExit,  boolean NoDoubleJump,  boolean InvSpot,  boolean PlayerStart,  int TeamNumber,  boolean DomPoint,  int DomPointController,  boolean Door,  boolean LiftCenter,  boolean LiftExit,  boolean AIMarker,  boolean JumpSpot,  boolean JumpPad,  boolean JumpDest,  boolean Teleporter,  Rotation Rotation,  boolean RoamingSpot,  boolean SnipingSpot,  Item ItemInstance,  Map<UnrealId, NavPointNeighbourLink> OutgoingEdges,  Map<UnrealId, NavPointNeighbourLink> IncomingEdges,  String PreferedWeapon
		) {
			
					this.Id = Id;
				
					this.Location = Location;
				
					this.Velocity = Velocity;
				
					this.Visible = Visible;
				
					this.Item = Item;
				
					this.ItemClass = ItemClass;
				
					this.ItemSpawned = ItemSpawned;
				
					this.DoorOpened = DoorOpened;
				
					this.Mover = Mover;
				
					this.LiftOffset = LiftOffset;
				
					this.LiftJumpExit = LiftJumpExit;
				
					this.NoDoubleJump = NoDoubleJump;
				
					this.InvSpot = InvSpot;
				
					this.PlayerStart = PlayerStart;
				
					this.TeamNumber = TeamNumber;
				
					this.DomPoint = DomPoint;
				
					this.DomPointController = DomPointController;
				
					this.Door = Door;
				
					this.LiftCenter = LiftCenter;
				
					this.LiftExit = LiftExit;
				
					this.AIMarker = AIMarker;
				
					this.JumpSpot = JumpSpot;
				
					this.JumpPad = JumpPad;
				
					this.JumpDest = JumpDest;
				
					this.Teleporter = Teleporter;
				
					this.Rotation = Rotation;
				
					this.RoamingSpot = RoamingSpot;
				
					this.SnipingSpot = SnipingSpot;
				
					this.ItemInstance = ItemInstance;
				
					this.OutgoingEdges = OutgoingEdges;
				
					this.IncomingEdges = IncomingEdges;
				
					this.PreferedWeapon = PreferedWeapon;
				
		}
    
		/**
		 * Cloning constructor from the full message.
		 *
		 * @param original
		 */
		public NavPointMessage(NavPointMessage original) {		
			
					this.Id = original.getId()
 	;
				
					this.Location = original.getLocation()
 	;
				
					this.Velocity = original.getVelocity()
 	;
				
					this.Visible = original.isVisible()
 	;
				
					this.Item = original.getItem()
 	;
				
					this.ItemClass = original.getItemClass()
 	;
				
					this.ItemSpawned = original.isItemSpawned()
 	;
				
					this.DoorOpened = original.isDoorOpened()
 	;
				
					this.Mover = original.getMover()
 	;
				
					this.LiftOffset = original.getLiftOffset()
 	;
				
					this.LiftJumpExit = original.isLiftJumpExit()
 	;
				
					this.NoDoubleJump = original.isNoDoubleJump()
 	;
				
					this.InvSpot = original.isInvSpot()
 	;
				
					this.PlayerStart = original.isPlayerStart()
 	;
				
					this.TeamNumber = original.getTeamNumber()
 	;
				
					this.DomPoint = original.isDomPoint()
 	;
				
					this.DomPointController = original.getDomPointController()
 	;
				
					this.Door = original.isDoor()
 	;
				
					this.LiftCenter = original.isLiftCenter()
 	;
				
					this.LiftExit = original.isLiftExit()
 	;
				
					this.AIMarker = original.isAIMarker()
 	;
				
					this.JumpSpot = original.isJumpSpot()
 	;
				
					this.JumpPad = original.isJumpPad()
 	;
				
					this.JumpDest = original.isJumpDest()
 	;
				
					this.Teleporter = original.isTeleporter()
 	;
				
					this.Rotation = original.getRotation()
 	;
				
					this.RoamingSpot = original.isRoamingSpot()
 	;
				
					this.SnipingSpot = original.isSnipingSpot()
 	;
				
					this.ItemInstance = original.getItemInstance()
 	;
				
					this.OutgoingEdges = original.getOutgoingEdges()
 	;
				
					this.IncomingEdges = original.getIncomingEdges()
 	;
				
					this.PreferedWeapon = original.getPreferedWeapon()
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
         * 
			A unique Id of this navigation point assigned by the game.
		 
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
         * 
			A unique Id of this navigation point assigned by the game.
		 
         */
        public  UnrealId getId()
 	 {
		    					return Id;
		    				}
		    			
    	
	    /**
         * Location of navigation point. 
         */
        protected
         Location Location =
       	null;
	
    						
    						/**
		 					 * Whether property 'Location' was received from GB2004.
		 					 */
							protected boolean Location_Set = false;
							
    						@Override
		    				
 		/**
         * Location of navigation point. 
         */
        public  Location getLocation()
 	 {
		    					return Location;
		    				}
		    			
    	
	    /**
         * 
			Velocity of the navigation point (if the navigation point is
			currently moving). Not sent at the moment.
		 
         */
        protected
         Velocity Velocity =
       	null;
	
    						
    						/**
		 					 * Whether property 'Velocity' was received from GB2004.
		 					 */
							protected boolean Velocity_Set = false;
							
    						@Override
		    				
 		/**
         * 
			Velocity of the navigation point (if the navigation point is
			currently moving). Not sent at the moment.
		 
         */
        public  Velocity getVelocity()
 	 {
		    					return Velocity;
		    				}
		    			
    	
	    /**
         * 
			If the point is in the field of view of the bot.
		 
         */
        protected
         boolean Visible =
       	false;
	
    						
    						/**
		 					 * Whether property 'Visible' was received from GB2004.
		 					 */
							protected boolean Visible_Set = false;
							
    						@Override
		    				
 		/**
         * 
			If the point is in the field of view of the bot.
		 
         */
        public  boolean isVisible()
 	 {
		    					return Visible;
		    				}
		    			
    	
	    /**
         * 
			Unique Id of the respawned item (the item respawns at this
			point). Not sent if point is not an inventory spot. Sent only in HandShake.
		 
         */
        protected
         UnrealId Item =
       	null;
	
    						
    						/**
		 					 * Whether property 'Item' was received from GB2004.
		 					 */
							protected boolean Item_Set = false;
							
    						@Override
		    				
 		/**
         * 
			Unique Id of the respawned item (the item respawns at this
			point). Not sent if point is not an inventory spot. Sent only in HandShake.
		 
         */
        public  UnrealId getItem()
 	 {
		    					return Item;
		    				}
		    			
    	
	    /**
         * 
            Class of the item (e.g. xWeapons.FlakCannonPickup). Not sent if point is not an inventory spot. Sent only in HandShake.
         
         */
        protected
         ItemType ItemClass =
       	null;
	
    						
    						/**
		 					 * Whether property 'ItemClass' was received from GB2004.
		 					 */
							protected boolean ItemClass_Set = false;
							
    						@Override
		    				
 		/**
         * 
            Class of the item (e.g. xWeapons.FlakCannonPickup). Not sent if point is not an inventory spot. Sent only in HandShake.
         
         */
        public  ItemType getItemClass()
 	 {
		    					return ItemClass;
		    				}
		    			
    	
	    /**
         * 
            True if the item is spawned at the point. Not sent if point is not an inventory spot. 
         
         */
        protected
         boolean ItemSpawned =
       	false;
	
    						
    						/**
		 					 * Whether property 'ItemSpawned' was received from GB2004.
		 					 */
							protected boolean ItemSpawned_Set = false;
							
    						@Override
		    				
 		/**
         * 
            True if the item is spawned at the point. Not sent if point is not an inventory spot. 
         
         */
        public  boolean isItemSpawned()
 	 {
		    					return ItemSpawned;
		    				}
		    			
    	
	    /**
         * 
            True if this NavPoint is a Door and door is opened. Not sent if point is not a door.
         
         */
        protected
         boolean DoorOpened =
       	false;
	
    						
    						/**
		 					 * Whether property 'DoorOpened' was received from GB2004.
		 					 */
							protected boolean DoorOpened_Set = false;
							
    						@Override
		    				
 		/**
         * 
            True if this NavPoint is a Door and door is opened. Not sent if point is not a door.
         
         */
        public  boolean isDoorOpened()
 	 {
		    					return DoorOpened;
		    				}
		    			
    	
	    /**
         * 
            If this NavPoint is marking some mover, the mover id will be here. Not sent if point is not a Door, a LiftCenter or a LiftExit. Sent only in HandShake.
         
         */
        protected
         UnrealId Mover =
       	null;
	
    						
    						/**
		 					 * Whether property 'Mover' was received from GB2004.
		 					 */
							protected boolean Mover_Set = false;
							
    						@Override
		    				
 		/**
         * 
            If this NavPoint is marking some mover, the mover id will be here. Not sent if point is not a Door, a LiftCenter or a LiftExit. Sent only in HandShake.
         
         */
        public  UnrealId getMover()
 	 {
		    					return Mover;
		    				}
		    			
    	
	    /**
         * 
            Starting vector between MyLift location and LiftCenter location. Not sent if point is not a LiftCenter. Sent only in HandShake.
         
         */
        protected
         Vector3d LiftOffset =
       	null;
	
    						
    						/**
		 					 * Whether property 'LiftOffset' was received from GB2004.
		 					 */
							protected boolean LiftOffset_Set = false;
							
    						@Override
		    				
 		/**
         * 
            Starting vector between MyLift location and LiftCenter location. Not sent if point is not a LiftCenter. Sent only in HandShake.
         
         */
        public  Vector3d getLiftOffset()
 	 {
		    					return LiftOffset;
		    				}
		    			
    	
	    /**
         * 
            Boolean. If we can/should exit the lift by a jump when near the destination place. Not sent if point is not a LiftExit. Sent only in HandShake.
         
         */
        protected
         boolean LiftJumpExit =
       	false;
	
    						
    						/**
		 					 * Whether property 'LiftJumpExit' was received from GB2004.
		 					 */
							protected boolean LiftJumpExit_Set = false;
							
    						@Override
		    				
 		/**
         * 
            Boolean. If we can/should exit the lift by a jump when near the destination place. Not sent if point is not a LiftExit. Sent only in HandShake.
         
         */
        public  boolean isLiftJumpExit()
 	 {
		    					return LiftJumpExit;
		    				}
		    			
    	
	    /**
         * 
            Boolean. If we should or not use double jump when exiting lift with a jump. Not sent if point is not a LiftExit. Sent only in HandShake.
         
         */
        protected
         boolean NoDoubleJump =
       	false;
	
    						
    						/**
		 					 * Whether property 'NoDoubleJump' was received from GB2004.
		 					 */
							protected boolean NoDoubleJump_Set = false;
							
    						@Override
		    				
 		/**
         * 
            Boolean. If we should or not use double jump when exiting lift with a jump. Not sent if point is not a LiftExit. Sent only in HandShake.
         
         */
        public  boolean isNoDoubleJump()
 	 {
		    					return NoDoubleJump;
		    				}
		    			
    	
	    /**
         * 
			If this is an inventory spot (item is respawned at this point).
		 
         */
        protected
         boolean InvSpot =
       	false;
	
    						
    						/**
		 					 * Whether property 'InvSpot' was received from GB2004.
		 					 */
							protected boolean InvSpot_Set = false;
							
    						@Override
		    				
 		/**
         * 
			If this is an inventory spot (item is respawned at this point).
		 
         */
        public  boolean isInvSpot()
 	 {
		    					return InvSpot;
		    				}
		    			
    	
	    /**
         * 
			If this is a player start (players and/or bots are respawned at this point).
		 
         */
        protected
         boolean PlayerStart =
       	false;
	
    						
    						/**
		 					 * Whether property 'PlayerStart' was received from GB2004.
		 					 */
							protected boolean PlayerStart_Set = false;
							
    						@Override
		    				
 		/**
         * 
			If this is a player start (players and/or bots are respawned at this point).
		 
         */
        public  boolean isPlayerStart()
 	 {
		    					return PlayerStart;
		    				}
		    			
    	
	    /**
         * 
			Will be sent if this is a player start. In Team games (team deathmatch, capture the flag, domination) holds information about which team respawns at this player start spot. In non-team games will return 0!
		 
         */
        protected
         int TeamNumber =
       	0;
	
    						
    						/**
		 					 * Whether property 'TeamNumber' was received from GB2004.
		 					 */
							protected boolean TeamNumber_Set = false;
							
    						@Override
		    				
 		/**
         * 
			Will be sent if this is a player start. In Team games (team deathmatch, capture the flag, domination) holds information about which team respawns at this player start spot. In non-team games will return 0!
		 
         */
        public  int getTeamNumber()
 	 {
		    					return TeamNumber;
		    				}
		    			
    	
	    /**
         * If this point marks a DominationPoint (for BotDoubleDomination game). 
         */
        protected
         boolean DomPoint =
       	false;
	
    						
    						/**
		 					 * Whether property 'DomPoint' was received from GB2004.
		 					 */
							protected boolean DomPoint_Set = false;
							
    						@Override
		    				
 		/**
         * If this point marks a DominationPoint (for BotDoubleDomination game). 
         */
        public  boolean isDomPoint()
 	 {
		    					return DomPoint;
		    				}
		    			
    	
	    /**
         * Exported if this NavPoint is a DominationPoint (for BotDoubleDomination game) - which team controls this point. 
         */
        protected
         int DomPointController =
       	0;
	
    						
    						/**
		 					 * Whether property 'DomPointController' was received from GB2004.
		 					 */
							protected boolean DomPointController_Set = false;
							
    						@Override
		    				
 		/**
         * Exported if this NavPoint is a DominationPoint (for BotDoubleDomination game) - which team controls this point. 
         */
        public  int getDomPointController()
 	 {
		    					return DomPointController;
		    				}
		    			
    	
	    /**
         * 
			If this point marks a door mover.
		 
         */
        protected
         boolean Door =
       	false;
	
    						
    						/**
		 					 * Whether property 'Door' was received from GB2004.
		 					 */
							protected boolean Door_Set = false;
							
    						@Override
		    				
 		/**
         * 
			If this point marks a door mover.
		 
         */
        public  boolean isDoor()
 	 {
		    					return Door;
		    				}
		    			
    	
	    /**
         * 
			If this point marks a lift center (used to mark center of a lift mover, note that this point will be always moved with the lift).
		 
         */
        protected
         boolean LiftCenter =
       	false;
	
    						
    						/**
		 					 * Whether property 'LiftCenter' was received from GB2004.
		 					 */
							protected boolean LiftCenter_Set = false;
							
    						@Override
		    				
 		/**
         * 
			If this point marks a lift center (used to mark center of a lift mover, note that this point will be always moved with the lift).
		 
         */
        public  boolean isLiftCenter()
 	 {
		    					return LiftCenter;
		    				}
		    			
    	
	    /**
         * 
			If this point marks a lift exit (used to mark exit point of a lift mover).
		 
         */
        protected
         boolean LiftExit =
       	false;
	
    						
    						/**
		 					 * Whether property 'LiftExit' was received from GB2004.
		 					 */
							protected boolean LiftExit_Set = false;
							
    						@Override
		    				
 		/**
         * 
			If this point marks a lift exit (used to mark exit point of a lift mover).
		 
         */
        public  boolean isLiftExit()
 	 {
		    					return LiftExit;
		    				}
		    			
    	
	    /**
         * 
			If this point is an AI marker - marks an interesting spot in the environment. May be ambush point or sniping spot, etc.
		 
         */
        protected
         boolean AIMarker =
       	false;
	
    						
    						/**
		 					 * Whether property 'AIMarker' was received from GB2004.
		 					 */
							protected boolean AIMarker_Set = false;
							
    						@Override
		    				
 		/**
         * 
			If this point is an AI marker - marks an interesting spot in the environment. May be ambush point or sniping spot, etc.
		 
         */
        public  boolean isAIMarker()
 	 {
		    					return AIMarker;
		    				}
		    			
    	
	    /**
         * 
			If this point marks a jump spot (a special device that causes the bot to jump high or far).
		 
         */
        protected
         boolean JumpSpot =
       	false;
	
    						
    						/**
		 					 * Whether property 'JumpSpot' was received from GB2004.
		 					 */
							protected boolean JumpSpot_Set = false;
							
    						@Override
		    				
 		/**
         * 
			If this point marks a jump spot (a special device that causes the bot to jump high or far).
		 
         */
        public  boolean isJumpSpot()
 	 {
		    					return JumpSpot;
		    				}
		    			
    	
	    /**
         * 
			If this point marks a jump pad (a special device that causes the bot to jump high or far).
		 
         */
        protected
         boolean JumpPad =
       	false;
	
    						
    						/**
		 					 * Whether property 'JumpPad' was received from GB2004.
		 					 */
							protected boolean JumpPad_Set = false;
							
    						@Override
		    				
 		/**
         * 
			If this point marks a jump pad (a special device that causes the bot to jump high or far).
		 
         */
        public  boolean isJumpPad()
 	 {
		    					return JumpPad;
		    				}
		    			
    	
	    /**
         * 
			If this point marks a jump destination - some place that can be reached by some special jump.
		 
         */
        protected
         boolean JumpDest =
       	false;
	
    						
    						/**
		 					 * Whether property 'JumpDest' was received from GB2004.
		 					 */
							protected boolean JumpDest_Set = false;
							
    						@Override
		    				
 		/**
         * 
			If this point marks a jump destination - some place that can be reached by some special jump.
		 
         */
        public  boolean isJumpDest()
 	 {
		    					return JumpDest;
		    				}
		    			
    	
	    /**
         * 
			If this point marks a teleport. 
		 
         */
        protected
         boolean Teleporter =
       	false;
	
    						
    						/**
		 					 * Whether property 'Teleporter' was received from GB2004.
		 					 */
							protected boolean Teleporter_Set = false;
							
    						@Override
		    				
 		/**
         * 
			If this point marks a teleport. 
		 
         */
        public  boolean isTeleporter()
 	 {
		    					return Teleporter;
		    				}
		    			
    	
	    /**
         * 
			If the type is AIMarker. The rotation the bot should be
			facing, when doing the action specified by AIMarker. Sent only in HandShake.
		 
         */
        protected
         Rotation Rotation =
       	null;
	
    						
    						/**
		 					 * Whether property 'Rotation' was received from GB2004.
		 					 */
							protected boolean Rotation_Set = false;
							
    						@Override
		    				
 		/**
         * 
			If the type is AIMarker. The rotation the bot should be
			facing, when doing the action specified by AIMarker. Sent only in HandShake.
		 
         */
        public  Rotation getRotation()
 	 {
		    					return Rotation;
		    				}
		    			
    	
	    /**
         * 
			Some ambush point, where is good chance to intercept
			approaching opponents. Sent only in HandShake.
		 
         */
        protected
         boolean RoamingSpot =
       	false;
	
    						
    						/**
		 					 * Whether property 'RoamingSpot' was received from GB2004.
		 					 */
							protected boolean RoamingSpot_Set = false;
							
    						@Override
		    				
 		/**
         * 
			Some ambush point, where is good chance to intercept
			approaching opponents. Sent only in HandShake.
		 
         */
        public  boolean isRoamingSpot()
 	 {
		    					return RoamingSpot;
		    				}
		    			
    	
	    /**
         * Point good for sniping. Sent only in HandShake. 
         */
        protected
         boolean SnipingSpot =
       	false;
	
    						
    						/**
		 					 * Whether property 'SnipingSpot' was received from GB2004.
		 					 */
							protected boolean SnipingSpot_Set = false;
							
    						@Override
		    				
 		/**
         * Point good for sniping. Sent only in HandShake. 
         */
        public  boolean isSnipingSpot()
 	 {
		    					return SnipingSpot;
		    				}
		    			
    	
	    /**
         * If item should be present at this navpoint it's instance will be here. 
         */
        protected
         Item ItemInstance =
       	null;
	
    						
    						/**
		 					 * Whether property 'ItemInstance' was received from GB2004.
		 					 */
							protected boolean ItemInstance_Set = false;
							
    						@Override
		    				
 		/**
         * If item should be present at this navpoint it's instance will be here. 
         */
        public  Item getItemInstance()
 	 {
		    					return ItemInstance;
		    				}
		    			
    	
	    /**
         * Maps edge-LEADING_TO-navpoint-UnrealId to neighbour link, those are outgoing edges (those edges that originates in this navpoint going to another one, those you may usually travel). 
         */
        protected
         Map<UnrealId, NavPointNeighbourLink> OutgoingEdges =
       	new HashMap<UnrealId, NavPointNeighbourLink>();
	
    						
    						/**
		 					 * Whether property 'OutgoingEdges' was received from GB2004.
		 					 */
							protected boolean OutgoingEdges_Set = false;
							
    						@Override
		    				
 		/**
         * Maps edge-LEADING_TO-navpoint-UnrealId to neighbour link, those are outgoing edges (those edges that originates in this navpoint going to another one, those you may usually travel). 
         */
        public  Map<UnrealId, NavPointNeighbourLink> getOutgoingEdges()
 	 {
		    					return OutgoingEdges;
		    				}
		    			
    	
	    /**
         * Maps edge-ORIGINATES_FROM-navpoint-UnrealId to neighbour link, those are incoming edges (those edges that originates in different navpoint and ends here, do not use this to ask whether you can get to navpoint of specific unreal id, use OutgoingEdges instead). 
         */
        protected
         Map<UnrealId, NavPointNeighbourLink> IncomingEdges =
       	new HashMap<UnrealId, NavPointNeighbourLink>();
	
    						
    						/**
		 					 * Whether property 'IncomingEdges' was received from GB2004.
		 					 */
							protected boolean IncomingEdges_Set = false;
							
    						@Override
		    				
 		/**
         * Maps edge-ORIGINATES_FROM-navpoint-UnrealId to neighbour link, those are incoming edges (those edges that originates in different navpoint and ends here, do not use this to ask whether you can get to navpoint of specific unreal id, use OutgoingEdges instead). 
         */
        public  Map<UnrealId, NavPointNeighbourLink> getIncomingEdges()
 	 {
		    					return IncomingEdges;
		    				}
		    			
    	
	    /**
         * 
			Class of the weapon that should be prefered when using this
			point for AIMarker specified action. Sent only in HandShake.
		 
         */
        protected
         String PreferedWeapon =
       	null;
	
    						
    						/**
		 					 * Whether property 'PreferedWeapon' was received from GB2004.
		 					 */
							protected boolean PreferedWeapon_Set = false;
							
    						@Override
		    				
 		/**
         * 
			Class of the weapon that should be prefered when using this
			point for AIMarker specified action. Sent only in HandShake.
		 
         */
        public  String getPreferedWeapon()
 	 {
		    					return PreferedWeapon;
		    				}
		    			
		    			
		    			private NavPointLocal localPart = null;
		    			
		    			@Override
						public NavPointLocal 
						getLocal() {
							if (localPart != null) return localPart;
							return localPart = new 
								NavPointLocalMessage();
						}
					
						private NavPointShared sharedPart = null;
					
						@Override
						public NavPointShared 
						getShared() {
							if (sharedPart != null) return sharedPart;							
							return sharedPart = new 
								NavPointSharedMessage();
						}
					
						private NavPointStatic staticPart = null; 
					
						@Override
						public NavPointStatic 
						getStatic() {
							if (staticPart != null) return staticPart;
							return staticPart = new 
								NavPointStaticMessage();
						}
    				
 		/**
         *  
            				Implementation of the local part of the GameBots2004 message NAV, used
            				to facade NAVMessage.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Synchronous message - however only NavPoints marking item pickup locations are exported synchronously. Other NavPoints are not exported synchronously at all, even if the bot can actually see them (but note that ALL NavPoints are exported in the handshake between bot and the server). Exporting NavPoints synchronously took a lot of UT server resources with limited information gain (in Pogamut there is now available visibility matrix holding static information which points can be seen from other points). NavPoint carries information about UT navigation point - location, paths and some additional information are stored there (if it is an ambush point, or sniper point, etc.).
   
         */
 	public class NavPointLocalMessage 
	  					extends
  						NavPointLocal
	    {
 	
		    			@Override
		    			public 
		    			NavPointLocalMessage clone() {
		    				return this;
		    			}
		    			
		    				public NavPointLocalMessage getLocal() {
								return this;
					    	}
							public ISharedWorldObject getShared() {
							 	throw new UnsupportedOperationException("Could not return LOCAL as SHARED");
							}
							public IStaticWorldObject getStatic() {
							    throw new UnsupportedOperationException("Could not return LOCAL as STATIC");
							}
		    			
 		/**
         * 
			A unique Id of this navigation point assigned by the game.
		 
         */
        public  UnrealId getId()
 	 {
				    					return Id;
				    				}
				    			
 		/**
         * 
			If the point is in the field of view of the bot.
		 
         */
        public  boolean isVisible()
 	 {
				    					return Visible;
				    				}
				    			
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
		              			"Id = " + String.valueOf(getId()
 	) + " | " + 
		              		
		              			"Visible = " + String.valueOf(isVisible()
 	) + " | " + 
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>Id</b> = " + String.valueOf(getId()
 	) + " <br/> " + 
		              		
		              			"<b>Visible</b> = " + String.valueOf(isVisible()
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
            				Implementation of the static part of the GameBots2004 message NAV, used
            				to facade NAVMessage.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Synchronous message - however only NavPoints marking item pickup locations are exported synchronously. Other NavPoints are not exported synchronously at all, even if the bot can actually see them (but note that ALL NavPoints are exported in the handshake between bot and the server). Exporting NavPoints synchronously took a lot of UT server resources with limited information gain (in Pogamut there is now available visibility matrix holding static information which points can be seen from other points). NavPoint carries information about UT navigation point - location, paths and some additional information are stored there (if it is an ambush point, or sniper point, etc.).
   
         */
 	public class NavPointStaticMessage 
	  					extends
  						NavPointStatic
	    {
 	
		    			@Override
		    			public 
		    			NavPointStaticMessage clone() {
		    				return this;
		    			}
		    			
 		/**
         * 
			A unique Id of this navigation point assigned by the game.
		 
         */
        public  UnrealId getId()
 	 {
				    					return Id;
				    				}
				    			
 		/**
         * 
			Unique Id of the respawned item (the item respawns at this
			point). Not sent if point is not an inventory spot. Sent only in HandShake.
		 
         */
        public  UnrealId getItem()
 	 {
				    					return Item;
				    				}
				    			
 		/**
         * 
            Class of the item (e.g. xWeapons.FlakCannonPickup). Not sent if point is not an inventory spot. Sent only in HandShake.
         
         */
        public  ItemType getItemClass()
 	 {
				    					return ItemClass;
				    				}
				    			
 		/**
         * 
            If this NavPoint is marking some mover, the mover id will be here. Not sent if point is not a Door, a LiftCenter or a LiftExit. Sent only in HandShake.
         
         */
        public  UnrealId getMover()
 	 {
				    					return Mover;
				    				}
				    			
 		/**
         * 
            Starting vector between MyLift location and LiftCenter location. Not sent if point is not a LiftCenter. Sent only in HandShake.
         
         */
        public  Vector3d getLiftOffset()
 	 {
				    					return LiftOffset;
				    				}
				    			
 		/**
         * 
            Boolean. If we can/should exit the lift by a jump when near the destination place. Not sent if point is not a LiftExit. Sent only in HandShake.
         
         */
        public  boolean isLiftJumpExit()
 	 {
				    					return LiftJumpExit;
				    				}
				    			
 		/**
         * 
            Boolean. If we should or not use double jump when exiting lift with a jump. Not sent if point is not a LiftExit. Sent only in HandShake.
         
         */
        public  boolean isNoDoubleJump()
 	 {
				    					return NoDoubleJump;
				    				}
				    			
 		/**
         * 
			If this is an inventory spot (item is respawned at this point).
		 
         */
        public  boolean isInvSpot()
 	 {
				    					return InvSpot;
				    				}
				    			
 		/**
         * 
			If this is a player start (players and/or bots are respawned at this point).
		 
         */
        public  boolean isPlayerStart()
 	 {
				    					return PlayerStart;
				    				}
				    			
 		/**
         * 
			Will be sent if this is a player start. In Team games (team deathmatch, capture the flag, domination) holds information about which team respawns at this player start spot. In non-team games will return 0!
		 
         */
        public  int getTeamNumber()
 	 {
				    					return TeamNumber;
				    				}
				    			
 		/**
         * If this point marks a DominationPoint (for BotDoubleDomination game). 
         */
        public  boolean isDomPoint()
 	 {
				    					return DomPoint;
				    				}
				    			
 		/**
         * 
			If this point marks a door mover.
		 
         */
        public  boolean isDoor()
 	 {
				    					return Door;
				    				}
				    			
 		/**
         * 
			If this point marks a lift center (used to mark center of a lift mover, note that this point will be always moved with the lift).
		 
         */
        public  boolean isLiftCenter()
 	 {
				    					return LiftCenter;
				    				}
				    			
 		/**
         * 
			If this point marks a lift exit (used to mark exit point of a lift mover).
		 
         */
        public  boolean isLiftExit()
 	 {
				    					return LiftExit;
				    				}
				    			
 		/**
         * 
			If this point is an AI marker - marks an interesting spot in the environment. May be ambush point or sniping spot, etc.
		 
         */
        public  boolean isAIMarker()
 	 {
				    					return AIMarker;
				    				}
				    			
 		/**
         * 
			If this point marks a jump spot (a special device that causes the bot to jump high or far).
		 
         */
        public  boolean isJumpSpot()
 	 {
				    					return JumpSpot;
				    				}
				    			
 		/**
         * 
			If this point marks a jump pad (a special device that causes the bot to jump high or far).
		 
         */
        public  boolean isJumpPad()
 	 {
				    					return JumpPad;
				    				}
				    			
 		/**
         * 
			If this point marks a jump destination - some place that can be reached by some special jump.
		 
         */
        public  boolean isJumpDest()
 	 {
				    					return JumpDest;
				    				}
				    			
 		/**
         * 
			If this point marks a teleport. 
		 
         */
        public  boolean isTeleporter()
 	 {
				    					return Teleporter;
				    				}
				    			
 		/**
         * 
			If the type is AIMarker. The rotation the bot should be
			facing, when doing the action specified by AIMarker. Sent only in HandShake.
		 
         */
        public  Rotation getRotation()
 	 {
				    					return Rotation;
				    				}
				    			
 		/**
         * 
			Some ambush point, where is good chance to intercept
			approaching opponents. Sent only in HandShake.
		 
         */
        public  boolean isRoamingSpot()
 	 {
				    					return RoamingSpot;
				    				}
				    			
 		/**
         * Point good for sniping. Sent only in HandShake. 
         */
        public  boolean isSnipingSpot()
 	 {
				    					return SnipingSpot;
				    				}
				    			
 		/**
         * If item should be present at this navpoint it's instance will be here. 
         */
        public  Item getItemInstance()
 	 {
				    					return ItemInstance;
				    				}
				    			
 		/**
         * Maps edge-LEADING_TO-navpoint-UnrealId to neighbour link, those are outgoing edges (those edges that originates in this navpoint going to another one, those you may usually travel). 
         */
        public  Map<UnrealId, NavPointNeighbourLink> getOutgoingEdges()
 	 {
				    					return OutgoingEdges;
				    				}
				    			
 		/**
         * Maps edge-ORIGINATES_FROM-navpoint-UnrealId to neighbour link, those are incoming edges (those edges that originates in different navpoint and ends here, do not use this to ask whether you can get to navpoint of specific unreal id, use OutgoingEdges instead). 
         */
        public  Map<UnrealId, NavPointNeighbourLink> getIncomingEdges()
 	 {
				    					return IncomingEdges;
				    				}
				    			
 		/**
         * 
			Class of the weapon that should be prefered when using this
			point for AIMarker specified action. Sent only in HandShake.
		 
         */
        public  String getPreferedWeapon()
 	 {
				    					return PreferedWeapon;
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
 				NavPointStatic obj = (NavPointStatic) other;

 				
 						if ( !(
 	 			AdvancedEquals.equalsOrNull(this.getId()
 	, obj.getId()
 	)
 	 		) )
						{
							System.out.println("!!!!!PROPERTY UPDATE ERROR!!!! on property Id on object class NavPointStatic");
							return true;
						}
 					
 						if ( !(
 	 			AdvancedEquals.equalsOrNull(this.getItem()
 	, obj.getItem()
 	)
 	 		) )
						{
							System.out.println("!!!!!PROPERTY UPDATE ERROR!!!! on property Item on object class NavPointStatic");
							return true;
						}
 					
 						if ( !(
 	 			AdvancedEquals.equalsOrNull(this.getItemClass()
 	, obj.getItemClass()
 	)
 	 		) )
						{
							System.out.println("!!!!!PROPERTY UPDATE ERROR!!!! on property ItemClass on object class NavPointStatic");
							return true;
						}
 					
 						if ( !(
 	 			AdvancedEquals.equalsOrNull(this.getMover()
 	, obj.getMover()
 	)
 	 		) )
						{
							System.out.println("!!!!!PROPERTY UPDATE ERROR!!!! on property Mover on object class NavPointStatic");
							return true;
						}
 					
 						if ( !(
 	 			AdvancedEquals.equalsOrNull(this.getLiftOffset()
 	, obj.getLiftOffset()
 	)
 	 		) )
						{
							System.out.println("!!!!!PROPERTY UPDATE ERROR!!!! on property LiftOffset on object class NavPointStatic");
							return true;
						}
 					
 						if ( !(this.isLiftJumpExit()
 	
 	 			== obj.isLiftJumpExit()
 	) )
						{
							System.out.println("!!!!!PROPERTY UPDATE ERROR!!!! on property LiftJumpExit on object class NavPointStatic");
							return true;
						}
 					
 						if ( !(this.isNoDoubleJump()
 	
 	 			== obj.isNoDoubleJump()
 	) )
						{
							System.out.println("!!!!!PROPERTY UPDATE ERROR!!!! on property NoDoubleJump on object class NavPointStatic");
							return true;
						}
 					
 						if ( !(this.isInvSpot()
 	
 	 			== obj.isInvSpot()
 	) )
						{
							System.out.println("!!!!!PROPERTY UPDATE ERROR!!!! on property InvSpot on object class NavPointStatic");
							return true;
						}
 					
 						if ( !(this.isPlayerStart()
 	
 	 			== obj.isPlayerStart()
 	) )
						{
							System.out.println("!!!!!PROPERTY UPDATE ERROR!!!! on property PlayerStart on object class NavPointStatic");
							return true;
						}
 					
 						if ( !(this.getTeamNumber()
 	
 	 			== obj.getTeamNumber()
 	) )
						{
							System.out.println("!!!!!PROPERTY UPDATE ERROR!!!! on property TeamNumber on object class NavPointStatic");
							return true;
						}
 					
 						if ( !(this.isDomPoint()
 	
 	 			== obj.isDomPoint()
 	) )
						{
							System.out.println("!!!!!PROPERTY UPDATE ERROR!!!! on property DomPoint on object class NavPointStatic");
							return true;
						}
 					
 						if ( !(this.isDoor()
 	
 	 			== obj.isDoor()
 	) )
						{
							System.out.println("!!!!!PROPERTY UPDATE ERROR!!!! on property Door on object class NavPointStatic");
							return true;
						}
 					
 						if ( !(this.isLiftCenter()
 	
 	 			== obj.isLiftCenter()
 	) )
						{
							System.out.println("!!!!!PROPERTY UPDATE ERROR!!!! on property LiftCenter on object class NavPointStatic");
							return true;
						}
 					
 						if ( !(this.isLiftExit()
 	
 	 			== obj.isLiftExit()
 	) )
						{
							System.out.println("!!!!!PROPERTY UPDATE ERROR!!!! on property LiftExit on object class NavPointStatic");
							return true;
						}
 					
 						if ( !(this.isAIMarker()
 	
 	 			== obj.isAIMarker()
 	) )
						{
							System.out.println("!!!!!PROPERTY UPDATE ERROR!!!! on property AIMarker on object class NavPointStatic");
							return true;
						}
 					
 						if ( !(this.isJumpSpot()
 	
 	 			== obj.isJumpSpot()
 	) )
						{
							System.out.println("!!!!!PROPERTY UPDATE ERROR!!!! on property JumpSpot on object class NavPointStatic");
							return true;
						}
 					
 						if ( !(this.isJumpPad()
 	
 	 			== obj.isJumpPad()
 	) )
						{
							System.out.println("!!!!!PROPERTY UPDATE ERROR!!!! on property JumpPad on object class NavPointStatic");
							return true;
						}
 					
 						if ( !(this.isJumpDest()
 	
 	 			== obj.isJumpDest()
 	) )
						{
							System.out.println("!!!!!PROPERTY UPDATE ERROR!!!! on property JumpDest on object class NavPointStatic");
							return true;
						}
 					
 						if ( !(this.isTeleporter()
 	
 	 			== obj.isTeleporter()
 	) )
						{
							System.out.println("!!!!!PROPERTY UPDATE ERROR!!!! on property Teleporter on object class NavPointStatic");
							return true;
						}
 					
 						if ( !(
 	 			AdvancedEquals.equalsOrNull(this.getRotation()
 	, obj.getRotation()
 	)
 	 		) )
						{
							System.out.println("!!!!!PROPERTY UPDATE ERROR!!!! on property Rotation on object class NavPointStatic");
							return true;
						}
 					
 						if ( !(this.isRoamingSpot()
 	
 	 			== obj.isRoamingSpot()
 	) )
						{
							System.out.println("!!!!!PROPERTY UPDATE ERROR!!!! on property RoamingSpot on object class NavPointStatic");
							return true;
						}
 					
 						if ( !(this.isSnipingSpot()
 	
 	 			== obj.isSnipingSpot()
 	) )
						{
							System.out.println("!!!!!PROPERTY UPDATE ERROR!!!! on property SnipingSpot on object class NavPointStatic");
							return true;
						}
 					
 						if ( !(
 	 			AdvancedEquals.equalsOrNull(this.getItemInstance()
 	, obj.getItemInstance()
 	)
 	 		) )
						{
							System.out.println("!!!!!PROPERTY UPDATE ERROR!!!! on property ItemInstance on object class NavPointStatic");
							return true;
						}
 					
 						//Skipping outgoing and incoming edges tests because the navGraph is sent only once
 					
 						//Skipping outgoing and incoming edges tests because the navGraph is sent only once
 					
 						if ( !(
 	 			AdvancedEquals.equalsOrNull(this.getPreferedWeapon()
 	, obj.getPreferedWeapon()
 	)
 	 		) )
						{
							System.out.println("!!!!!PROPERTY UPDATE ERROR!!!! on property PreferedWeapon on object class NavPointStatic");
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
		              		
		              			"Item = " + String.valueOf(getItem()
 	) + " | " + 
		              		
		              			"ItemClass = " + String.valueOf(getItemClass()
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
		              		
		              			"<b>Item</b> = " + String.valueOf(getItem()
 	) + " <br/> " + 
		              		
		              			"<b>ItemClass</b> = " + String.valueOf(getItemClass()
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
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=static+classtype[@name=message]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=static+classtype[@name=message]) ---        	            	
 	
		}
 	
 		/**
         *  
            				Implementation of the shared part of the GameBots2004 message NAV, used
            				to facade NAVMessage.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Synchronous message - however only NavPoints marking item pickup locations are exported synchronously. Other NavPoints are not exported synchronously at all, even if the bot can actually see them (but note that ALL NavPoints are exported in the handshake between bot and the server). Exporting NavPoints synchronously took a lot of UT server resources with limited information gain (in Pogamut there is now available visibility matrix holding static information which points can be seen from other points). NavPoint carries information about UT navigation point - location, paths and some additional information are stored there (if it is an ambush point, or sniper point, etc.).
   
         */
 	public class NavPointSharedMessage 
	  					extends
  						NavPointShared
	    {
 	
    	
    	
		public NavPointSharedMessage()
		{
			
				propertyMap.put(myLocation.getPropertyId(), myLocation);
			
				propertyMap.put(myVelocity.getPropertyId(), myVelocity);
			
				propertyMap.put(myItemSpawned.getPropertyId(), myItemSpawned);
			
				propertyMap.put(myDoorOpened.getPropertyId(), myDoorOpened);
			
				propertyMap.put(myDomPointController.getPropertyId(), myDomPointController);
			
		}		
    
		    			@Override
		    			public 
		    			NavPointSharedMessage clone() {
		    				return this;
		    			}
		    			
		
		
		
		protected HashMap<PropertyId, ISharedProperty> propertyMap = new HashMap<PropertyId, ISharedProperty>(
			5
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
			A unique Id of this navigation point assigned by the game.
		 
         */
        public  UnrealId getId()
 	 {
  			return Id;
  		}
  		
    	
	    /**
         * Location of navigation point. 
         */
        protected
         LocationProperty 
        myLocation
					= new
					LocationProperty
					(
						getId(), 
						"Location", 
						Location, 
						NavPoint.class
					);
					
 		/**
         * Location of navigation point. 
         */
        public  Location getLocation()
 	 {
			  			return myLocation.getValue();
			  		}
				
    	
	    /**
         * 
			Velocity of the navigation point (if the navigation point is
			currently moving). Not sent at the moment.
		 
         */
        protected
         VelocityProperty 
        myVelocity
					= new
					VelocityProperty
					(
						getId(), 
						"Velocity", 
						Velocity, 
						NavPoint.class
					);
					
 		/**
         * 
			Velocity of the navigation point (if the navigation point is
			currently moving). Not sent at the moment.
		 
         */
        public  Velocity getVelocity()
 	 {
			  			return myVelocity.getValue();
			  		}
				
    	
	    /**
         * 
            True if the item is spawned at the point. Not sent if point is not an inventory spot. 
         
         */
        protected
         BooleanProperty 
        myItemSpawned
					= new
					BooleanProperty
					(
						getId(), 
						"ItemSpawned", 
						ItemSpawned, 
						NavPoint.class
					);
					
 		/**
         * 
            True if the item is spawned at the point. Not sent if point is not an inventory spot. 
         
         */
        public  boolean isItemSpawned()
 	 {
			  			return myItemSpawned.getValue();
			  		}
				
    	
	    /**
         * 
            True if this NavPoint is a Door and door is opened. Not sent if point is not a door.
         
         */
        protected
         BooleanProperty 
        myDoorOpened
					= new
					BooleanProperty
					(
						getId(), 
						"DoorOpened", 
						DoorOpened, 
						NavPoint.class
					);
					
 		/**
         * 
            True if this NavPoint is a Door and door is opened. Not sent if point is not a door.
         
         */
        public  boolean isDoorOpened()
 	 {
			  			return myDoorOpened.getValue();
			  		}
				
    	
	    /**
         * Exported if this NavPoint is a DominationPoint (for BotDoubleDomination game) - which team controls this point. 
         */
        protected
         IntProperty 
        myDomPointController
					= new
					IntProperty
					(
						getId(), 
						"DomPointController", 
						DomPointController, 
						NavPoint.class
					);
					
 		/**
         * Exported if this NavPoint is a DominationPoint (for BotDoubleDomination game) - which team controls this point. 
         */
        public  int getDomPointController()
 	 {
			  			return myDomPointController.getValue();
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
		              		
		              			"ItemSpawned = " + String.valueOf(isItemSpawned()
 	) + " | " + 
		              		
		              			"DoorOpened = " + String.valueOf(isDoorOpened()
 	) + " | " + 
		              		
		              			"DomPointController = " + String.valueOf(getDomPointController()
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
		              		
		              			"<b>ItemSpawned</b> = " + String.valueOf(isItemSpawned()
 	) + " <br/> " + 
		              		
		              			"<b>DoorOpened</b> = " + String.valueOf(isDoorOpened()
 	) + " <br/> " + 
		              		
		              			"<b>DomPointController</b> = " + String.valueOf(getDomPointController()
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
			if (!( object instanceof NavPointMessage) ) {
				throw new PogamutException("Can't update different class than NavPointMessage, got class " + object.getClass().getSimpleName() + "!", this);		
			}
			NavPointMessage toUpdate = (NavPointMessage)object;
			
			boolean updated = false;
			
			// UPDATING LOCAL PROPERTIES
			
				if (toUpdate.Visible != isVisible()
 	) {
				    toUpdate.Visible=isVisible()
 	;
					updated = true;
				}
			
         	
         	// UPDATING SHARED PROPERTIES
         	
     		if (getLocation()
 	 != null) {
     	
	            if (!SafeEquals.equals(toUpdate.Location, getLocation()
 	)) {
					toUpdate.Location=getLocation()
 	;
					updated = true;
				}
			
     		}
     	
				if (!SafeEquals.equals(toUpdate.Velocity, getVelocity()
 	)) {
					toUpdate.Velocity=getVelocity()
 	;
					updated = true;
				}
			
				if (toUpdate.ItemSpawned != isItemSpawned()
 	) {
				    toUpdate.ItemSpawned=isItemSpawned()
 	;
					updated = true;
				}
			
				if (toUpdate.DoorOpened != isDoorOpened()
 	) {
				    toUpdate.DoorOpened=isDoorOpened()
 	;
					updated = true;
				}
			
				if (toUpdate.DomPointController != getDomPointController()
 	) {
				    toUpdate.DomPointController=getDomPointController()
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
			return new NavPointLocalImpl.NavPointLocalUpdate
    (this.getLocal(), SimTime);
		}

		@Override
		public ISharedWorldObjectUpdatedEvent getSharedEvent() {
			return new NavPointSharedImpl.NavPointSharedUpdate
    (this.getShared(), SimTime, this.getTeamId());
		}

		@Override
		public IStaticWorldObjectUpdatedEvent getStaticEvent() {
			return new NavPointStaticImpl.NavPointStaticUpdate
    (this.getStatic(), SimTime);
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
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=base+classtype[@name=message]) ---
	                
         	  
         		    
         		    
         	            		   	
         	  
         		    
         		    
         	  
         	/**
		     * DO NOT USE THIS METHOD! Reserved for GaviaLib (Pogamut core)! It's used
		     * to set correct item instance into the NavPoint.
		     */  	
		    public void setItemInstance(Item item) {
		        this.ItemInstance = item;
		        if (item != null) {
		        	this.Item = item.getId();
		        }
		    } 	
		
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=base+classtype[@name=message]) ---        	            	
 	
		}
 	