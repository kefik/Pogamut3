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
            		Composite implementation of the NAV abstract message. It wraps Local/Shared/Static parts in single object
            		allowing to presenting a nice facade for users.
            	
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Synchronous message - however only NavPoints marking item pickup locations are exported synchronously. Other NavPoints are not exported synchronously at all, even if the bot can actually see them (but note that ALL NavPoints are exported in the handshake between bot and the server). Exporting NavPoints synchronously took a lot of UT server resources with limited information gain (in Pogamut there is now available visibility matrix holding static information which points can be seen from other points). NavPoint carries information about UT navigation point - location, paths and some additional information are stored there (if it is an ambush point, or sniper point, etc.).
   
         */
 	public class NavPointCompositeImpl 
  				extends NavPoint
	    {
 	
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public NavPointCompositeImpl()
		{
		}
	
		
		/**
		 * Composite-impl constructor. It assembles the message from its three fragments - local/shared/static.
		 *
		 * @param partLocal local-part of the message
		 * @param partShared shared-part of the message
		 * @param partStatic static-part of the message
		 */
		public NavPointCompositeImpl(
			NavPointLocalImpl partLocal,
			NavPointSharedImpl partShared,
			NavPointStaticImpl partStatic
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
		public NavPointCompositeImpl(NavPointCompositeImpl original) {
			this.partLocal = partLocal;
			this.partShared = partShared;
			this.partStatic = partStatic;
		}
    
   				
   				@Override
   				public void setSimTime(long SimTime) {
					super.setSimTime(SimTime);
				}
   			
    			
    			protected 
    			NavPointStaticImpl
    			partStatic;
    			
    			@Override
				public NavPointStatic getStatic() {
					return partStatic;
				}
    			
    			protected
    			NavPointLocalImpl
    			partLocal;
    	
    			@Override
				public NavPointLocal getLocal() {
					return partLocal;
				}
			
    			NavPointSharedImpl
    			partShared;
    			
				@Override
				public NavPointShared getShared() {
					return partShared;
				}
			
				
  				
  					@Override
    				
 		/**
         * 
			A unique Id of this navigation point assigned by the game.
		 
         */
        public  UnrealId getId()
 	 {
    					return 
    						
    								partStatic.
    							getId()
 	;
    				}
    			
  					@Override
    				
 		/**
         * Location of navigation point. 
         */
        public  Location getLocation()
 	 {
    					return 
    						
    								partShared.
    							getLocation()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			Velocity of the navigation point (if the navigation point is
			currently moving). Not sent at the moment.
		 
         */
        public  Velocity getVelocity()
 	 {
    					return 
    						
    								partShared.
    							getVelocity()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			If the point is in the field of view of the bot.
		 
         */
        public  boolean isVisible()
 	 {
    					return 
    						
    								partLocal.
    							isVisible()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			Unique Id of the respawned item (the item respawns at this
			point). Not sent if point is not an inventory spot. Sent only in HandShake.
		 
         */
        public  UnrealId getItem()
 	 {
    					return 
    						
    								partStatic.
    							getItem()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
            Class of the item (e.g. xWeapons.FlakCannonPickup). Not sent if point is not an inventory spot. Sent only in HandShake.
         
         */
        public  ItemType getItemClass()
 	 {
    					return 
    						
    								partStatic.
    							getItemClass()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
            True if the item is spawned at the point. Not sent if point is not an inventory spot. 
         
         */
        public  boolean isItemSpawned()
 	 {
    					return 
    						
    								partShared.
    							isItemSpawned()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
            True if this NavPoint is a Door and door is opened. Not sent if point is not a door.
         
         */
        public  boolean isDoorOpened()
 	 {
    					return 
    						
    								partShared.
    							isDoorOpened()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
            If this NavPoint is marking some mover, the mover id will be here. Not sent if point is not a Door, a LiftCenter or a LiftExit. Sent only in HandShake.
         
         */
        public  UnrealId getMover()
 	 {
    					return 
    						
    								partStatic.
    							getMover()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
            Starting vector between MyLift location and LiftCenter location. Not sent if point is not a LiftCenter. Sent only in HandShake.
         
         */
        public  Vector3d getLiftOffset()
 	 {
    					return 
    						
    								partStatic.
    							getLiftOffset()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
            Boolean. If we can/should exit the lift by a jump when near the destination place. Not sent if point is not a LiftExit. Sent only in HandShake.
         
         */
        public  boolean isLiftJumpExit()
 	 {
    					return 
    						
    								partStatic.
    							isLiftJumpExit()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
            Boolean. If we should or not use double jump when exiting lift with a jump. Not sent if point is not a LiftExit. Sent only in HandShake.
         
         */
        public  boolean isNoDoubleJump()
 	 {
    					return 
    						
    								partStatic.
    							isNoDoubleJump()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			If this is an inventory spot (item is respawned at this point).
		 
         */
        public  boolean isInvSpot()
 	 {
    					return 
    						
    								partStatic.
    							isInvSpot()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			If this is a player start (players and/or bots are respawned at this point).
		 
         */
        public  boolean isPlayerStart()
 	 {
    					return 
    						
    								partStatic.
    							isPlayerStart()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			Will be sent if this is a player start. In Team games (team deathmatch, capture the flag, domination) holds information about which team respawns at this player start spot. In non-team games will return 0!
		 
         */
        public  int getTeamNumber()
 	 {
    					return 
    						
    								partStatic.
    							getTeamNumber()
 	;
    				}
    			
  					@Override
    				
 		/**
         * If this point marks a DominationPoint (for BotDoubleDomination game). 
         */
        public  boolean isDomPoint()
 	 {
    					return 
    						
    								partStatic.
    							isDomPoint()
 	;
    				}
    			
  					@Override
    				
 		/**
         * Exported if this NavPoint is a DominationPoint (for BotDoubleDomination game) - which team controls this point. 
         */
        public  int getDomPointController()
 	 {
    					return 
    						
    								partShared.
    							getDomPointController()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			If this point marks a door mover.
		 
         */
        public  boolean isDoor()
 	 {
    					return 
    						
    								partStatic.
    							isDoor()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			If this point marks a lift center (used to mark center of a lift mover, note that this point will be always moved with the lift).
		 
         */
        public  boolean isLiftCenter()
 	 {
    					return 
    						
    								partStatic.
    							isLiftCenter()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			If this point marks a lift exit (used to mark exit point of a lift mover).
		 
         */
        public  boolean isLiftExit()
 	 {
    					return 
    						
    								partStatic.
    							isLiftExit()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			If this point is an AI marker - marks an interesting spot in the environment. May be ambush point or sniping spot, etc.
		 
         */
        public  boolean isAIMarker()
 	 {
    					return 
    						
    								partStatic.
    							isAIMarker()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			If this point marks a jump spot (a special device that causes the bot to jump high or far).
		 
         */
        public  boolean isJumpSpot()
 	 {
    					return 
    						
    								partStatic.
    							isJumpSpot()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			If this point marks a jump pad (a special device that causes the bot to jump high or far).
		 
         */
        public  boolean isJumpPad()
 	 {
    					return 
    						
    								partStatic.
    							isJumpPad()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			If this point marks a jump destination - some place that can be reached by some special jump.
		 
         */
        public  boolean isJumpDest()
 	 {
    					return 
    						
    								partStatic.
    							isJumpDest()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			If this point marks a teleport. 
		 
         */
        public  boolean isTeleporter()
 	 {
    					return 
    						
    								partStatic.
    							isTeleporter()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			If the type is AIMarker. The rotation the bot should be
			facing, when doing the action specified by AIMarker. Sent only in HandShake.
		 
         */
        public  Rotation getRotation()
 	 {
    					return 
    						
    								partStatic.
    							getRotation()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			Some ambush point, where is good chance to intercept
			approaching opponents. Sent only in HandShake.
		 
         */
        public  boolean isRoamingSpot()
 	 {
    					return 
    						
    								partStatic.
    							isRoamingSpot()
 	;
    				}
    			
  					@Override
    				
 		/**
         * Point good for sniping. Sent only in HandShake. 
         */
        public  boolean isSnipingSpot()
 	 {
    					return 
    						
    								partStatic.
    							isSnipingSpot()
 	;
    				}
    			
  					@Override
    				
 		/**
         * If item should be present at this navpoint it's instance will be here. 
         */
        public  Item getItemInstance()
 	 {
    					return 
    						
    								partStatic.
    							getItemInstance()
 	;
    				}
    			
  					@Override
    				
 		/**
         * Maps edge-LEADING_TO-navpoint-UnrealId to neighbour link, those are outgoing edges (those edges that originates in this navpoint going to another one, those you may usually travel). 
         */
        public  Map<UnrealId, NavPointNeighbourLink> getOutgoingEdges()
 	 {
    					return 
    						
    								partStatic.
    							getOutgoingEdges()
 	;
    				}
    			
  					@Override
    				
 		/**
         * Maps edge-ORIGINATES_FROM-navpoint-UnrealId to neighbour link, those are incoming edges (those edges that originates in different navpoint and ends here, do not use this to ask whether you can get to navpoint of specific unreal id, use OutgoingEdges instead). 
         */
        public  Map<UnrealId, NavPointNeighbourLink> getIncomingEdges()
 	 {
    					return 
    						
    								partStatic.
    							getIncomingEdges()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			Class of the weapon that should be prefered when using this
			point for AIMarker specified action. Sent only in HandShake.
		 
         */
        public  String getPreferedWeapon()
 	 {
    					return 
    						
    								partStatic.
    							getPreferedWeapon()
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
 	