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
            		Composite implementation of the SLF abstract message. It wraps Local/Shared/Static parts in single object
            		allowing to presenting a nice facade for users.
            	
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Synchronous message. Information about your bot's state.
	
         */
 	public class SelfCompositeImpl 
  				extends Self
	    {
 	
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public SelfCompositeImpl()
		{
		}
	
		
		/**
		 * Composite-impl constructor. It assembles the message from its three fragments - local/shared/static.
		 *
		 * @param partLocal local-part of the message
		 * @param partShared shared-part of the message
		 * @param partStatic static-part of the message
		 */
		public SelfCompositeImpl(
			SelfLocalImpl partLocal,
			SelfSharedImpl partShared,
			SelfStaticImpl partStatic
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
		public SelfCompositeImpl(SelfCompositeImpl original) {
			this.partLocal = partLocal;
			this.partShared = partShared;
			this.partStatic = partStatic;
		}
    
   				
   				@Override
   				public void setSimTime(long SimTime) {
					super.setSimTime(SimTime);
				}
   			
    			
    			protected 
    			SelfStaticImpl
    			partStatic;
    			
    			@Override
				public SelfStatic getStatic() {
					return partStatic;
				}
    			
    			protected
    			SelfLocalImpl
    			partLocal;
    	
    			@Override
				public SelfLocal getLocal() {
					return partLocal;
				}
			
    			SelfSharedImpl
    			partShared;
    			
				@Override
				public SelfShared getShared() {
					return partShared;
				}
			
				
  				
  					@Override
    				
 		/**
         * Unique Id of this self message instance. 
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
         * Unique Id of this bot. 
         */
        public  UnrealId getBotId()
 	 {
    					return 
    						
    								partLocal.
    							getBotId()
 	;
    				}
    			
  					@Override
    				
 		/**
         * Human readable bot name. 
         */
        public  String getName()
 	 {
    					return 
    						
    								partLocal.
    							getName()
 	;
    				}
    			
  					@Override
    				
 		/**
         * If we are vehicle just these attr. are sent in SLF: "Id","Vehicle""Rotation", "Location","Velocity ","Name ","Team" ,"Health" 
	"Armor","Adrenaline", "FloorLocation", "FloorNormal". 
         */
        public  boolean isVehicle()
 	 {
    					return 
    						
    								partLocal.
    							isVehicle()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			An absolute location of the bot.
		 
         */
        public  Location getLocation()
 	 {
    					return 
    						
    								partLocal.
    							getLocation()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			Absolute velocity of the bot as a vector of movement per one
			game second.
		 
         */
        public  Velocity getVelocity()
 	 {
    					return 
    						
    								partLocal.
    							getVelocity()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			Which direction the bot is facing in absolute terms.
		 
         */
        public  Rotation getRotation()
 	 {
    					return 
    						
    								partLocal.
    							getRotation()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			What team the bot is on. 255 is no team. 0-3 are red,
			blue, green, gold in that order.
		 
         */
        public  int getTeam()
 	 {
    					return 
    						
    								partLocal.
    							getTeam()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			Id of the weapon we are holding. This is unique Id of an
			item in our inventory and is different from the Id of the
			item we pick up from the ground! We can parse this string to
			look which weapon we hold. Weapon strings to look for
			include: "AssaultRifle", "ShieldGun", "FlakCannon",
			"BioRifle", "ShockRifle", "LinkGun", "SniperRifle",
			"RocketLauncher", "Minigun", "LightingGun", "Translocator".
			TODO: Look if this is all.
		 
         */
        public  String getWeapon()
 	 {
    					return 
    						
    								partLocal.
    							getWeapon()
 	;
    				}
    			
  					@Override
    				
 		/**
         * If the bot is shooting or not. 
         */
        public  boolean isShooting()
 	 {
    					return 
    						
    								partLocal.
    							isShooting()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			How much health the bot has left. Starts at 100, ranges from
			0 to 200.
		 
         */
        public  int getHealth()
 	 {
    					return 
    						
    								partLocal.
    							getHealth()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			How much ammo the bot has left for current weapon primary
			mode.
		 
         */
        public  int getPrimaryAmmo()
 	 {
    					return 
    						
    								partLocal.
    							getPrimaryAmmo()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			How much ammo the bot has left for current weapon secondary
			mode. Weapon does not have to support sec. fire mode.
		 
         */
        public  int getSecondaryAmmo()
 	 {
    					return 
    						
    								partLocal.
    							getSecondaryAmmo()
 	;
    				}
    			
  					@Override
    				
 		/**
         * How much adrenaline the bot has. 
         */
        public  int getAdrenaline()
 	 {
    					return 
    						
    								partLocal.
    							getAdrenaline()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			Combined size of high armor and low armor (or small armor). The high and low armor are tracked
                        separately. Low armor is limited to 50 points, while the
                        high armor can have up to 150 points. Both stacks can have a combined size of 150 points as well,
                        so if low armor is already at 50 points, high armor can have
                        100 points at max.
		 
         */
        public  int getArmor()
 	 {
    					return 
    						
    								partLocal.
    							getArmor()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			Also refered to as a "low armor". Ranges from 0 to 50 points.
		 
         */
        public  int getSmallArmor()
 	 {
    					return 
    						
    								partLocal.
    							getSmallArmor()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			If we are firing in secondary firing mode. 
		 
         */
        public  boolean isAltFiring()
 	 {
    					return 
    						
    								partLocal.
    							isAltFiring()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			If we are currently crouched.
		 
         */
        public  boolean isCrouched()
 	 {
    					return 
    						
    								partLocal.
    							isCrouched()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			If we are currently in walking mode.
		 
         */
        public  boolean isWalking()
 	 {
    					return 
    						
    								partLocal.
    							isWalking()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			Holds current floor location under the bot.
		 
         */
        public  Location getFloorLocation()
 	 {
    					return 
    						
    								partLocal.
    							getFloorLocation()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			Holds current floor normal under the bot.
		 
         */
        public  Location getFloorNormal()
 	 {
    					return 
    						
    								partLocal.
    							getFloorNormal()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			Name of the current combo (None if no combo active).
			Can be xGame.ComboBerserk, xGame.ComboDefensive, xGame.ComboInvis or xGame.ComboSpeed.
			To trigger combo adrenaline needs to be at 100 (maximum) and no other combo can be active.
		 
         */
        public  String getCombo()
 	 {
    					return 
    						
    								partLocal.
    							getCombo()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			Time when the UDamage effect expires. If the number is higher then the current
			time, it means the bot has UDamage effect active right now.
		 
         */
        public  double getUDamageTime()
 	 {
    					return 
    						
    								partLocal.
    							getUDamageTime()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			Name of the current BDI action.
		 
         */
        public  String getAction()
 	 {
    					return 
    						
    								partLocal.
    							getAction()
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
    						
    								partLocal.
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
    						
    								partLocal.
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
    						
    								partLocal.
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
    						
    								partLocal.
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
    						
    								partLocal.
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
 	