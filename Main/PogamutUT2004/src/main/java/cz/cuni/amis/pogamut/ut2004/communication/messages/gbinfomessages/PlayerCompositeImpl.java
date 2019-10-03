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
            		Composite implementation of the PLR abstract message. It wraps Local/Shared/Static parts in single object
            		allowing to presenting a nice facade for users.
            	
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Synchronous message. Contains information about other players in
		the game, like their current velocity, position, weapon and
		reachability. Only reports those players that are visible.
		(within field of view and not occluded).
	
         */
 	public class PlayerCompositeImpl 
  				extends Player
	    {
 	
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public PlayerCompositeImpl()
		{
		}
	
		
		/**
		 * Composite-impl constructor. It assembles the message from its three fragments - local/shared/static.
		 *
		 * @param partLocal local-part of the message
		 * @param partShared shared-part of the message
		 * @param partStatic static-part of the message
		 */
		public PlayerCompositeImpl(
			PlayerLocalImpl partLocal,
			PlayerSharedImpl partShared,
			PlayerStaticImpl partStatic
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
		public PlayerCompositeImpl(PlayerCompositeImpl original) {
			this.partLocal = partLocal;
			this.partShared = partShared;
			this.partStatic = partStatic;
		}
    
   				
   				@Override
   				public void setSimTime(long SimTime) {
					super.setSimTime(SimTime);
				}
   			
    			
    			protected 
    			PlayerStaticImpl
    			partStatic;
    			
    			@Override
				public PlayerStatic getStatic() {
					return partStatic;
				}
    			
    			protected
    			PlayerLocalImpl
    			partLocal;
    	
    			@Override
				public PlayerLocal getLocal() {
					return partLocal;
				}
			
    			PlayerSharedImpl
    			partShared;
    			
				@Override
				public PlayerShared getShared() {
					return partShared;
				}
			
				
  				
  					@Override
    				
 		/**
         * Unique Id of the player. 
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
         * 
			Exported just for control server. Holds jmx address we need to connect to
            when we want to debug our bot.
		 
         */
        public  String getJmx()
 	 {
    					return 
    						
    								partLocal.
    							getJmx()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			Human readable name of the player.
		 
         */
        public  String getName()
 	 {
    					return 
    						
    								partShared.
    							getName()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			Whether this player is in SPECTATE mode. Humans can change the mode during runtime!
		 
         */
        public  Boolean isSpectator()
 	 {
    					return 
    						
    								partShared.
    							isSpectator()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			Atomic action this bot is doing (BDI).
		 
         */
        public  String getAction()
 	 {
    					return 
    						
    								partShared.
    							getAction()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
            If the player is in the field of view of the bot.
         
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
			Which direction the player is facing in absolute terms.
		 
         */
        public  Rotation getRotation()
 	 {
    					return 
    						
    								partShared.
    							getRotation()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			An absolute location of the player within the map.
		 
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
			Absolute velocity of the player as a vector of movement per one
			game second.
		 
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
			What team the player is on. 255 is no team. 0-3 are red,
			blue, green, gold in that order.
		 
         */
        public  int getTeam()
 	 {
    					return 
    						
    								partShared.
    							getTeam()
 	;
    				}
    			
  					@Override
    				
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
    					return 
    						
    								partShared.
    							getWeapon()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			True if the bot is crouched.
		 
         */
        public  boolean isCrouched()
 	 {
    					return 
    						
    								partShared.
    							isCrouched()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			0 means is not firing, 1 - firing in primary mode, 2 -
			firing in secondary mode (alt firing).
		 
         */
        public  int getFiring()
 	 {
    					return 
    						
    								partShared.
    							getFiring()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			For UE2. Holds left emoticon of the bot, "None" means none set.
		 
         */
        public  String getEmotLeft()
 	 {
    					return 
    						
    								partShared.
    							getEmotLeft()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			For UE2. Holds center emoticon of the bot, "None" means none set.
		 
         */
        public  String getEmotCenter()
 	 {
    					return 
    						
    								partShared.
    							getEmotCenter()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			For UE2. Holds right emoticon of the bot, "None" means none set.
		 
         */
        public  String getEmotRight()
 	 {
    					return 
    						
    								partShared.
    							getEmotRight()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			For UE2. Holds the bubble of the emoticon of the bot, "None" means none set.
		 
         */
        public  String getBubble()
 	 {
    					return 
    						
    								partShared.
    							getBubble()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			For UE2. Current played animation of the bot.
		 
         */
        public  String getAnim()
 	 {
    					return 
    						
    								partShared.
    							getAnim()
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
 	