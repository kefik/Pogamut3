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
            				Abstract definition of the GameBots2004 message SLF.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Synchronous message. Information about your bot's state.
	
         */
 	public abstract class Self   
  				extends 
  				InfoMessage
  						implements IWorldEvent, IWorldChangeEvent, ICompositeWorldObject
  						
	    		,IPerson
	    {
 	
    	/** Example how the message looks like - used during parser tests. */
    	public static final String PROTOTYPE =
    		"SLF {Id unreal_id}  {BotId unreal_id}  {Name text}  {Vehicle False}  {Location 0,0,0}  {Velocity 0,0,0}  {Rotation 0,0,0}  {Team 0}  {Weapon text}  {Shooting False}  {Health 0}  {PrimaryAmmo 0}  {SecondaryAmmo 0}  {Adrenaline 0}  {Armor 0}  {SmallArmor 0}  {AltFiring False}  {Crouched False}  {Walking False}  {FloorLocation 0,0,0}  {FloorNormal 0,0,0}  {Combo text}  {UDamageTime 0}  {Action text}  {EmotLeft text}  {EmotCenter text}  {EmotRight text}  {Bubble text}  {Anim text} ";
    
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public Self()
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
         * Unique Id of this self message instance. 
         */
        public abstract UnrealId getId()
 	;
		    			
 		/**
         * Unique Id of this bot. 
         */
        public abstract UnrealId getBotId()
 	;
		    			
 		/**
         * Human readable bot name. 
         */
        public abstract String getName()
 	;
		    			
 		/**
         * If we are vehicle just these attr. are sent in SLF: "Id","Vehicle""Rotation", "Location","Velocity ","Name ","Team" ,"Health" 
	"Armor","Adrenaline", "FloorLocation", "FloorNormal". 
         */
        public abstract boolean isVehicle()
 	;
		    			
 		/**
         * 
			An absolute location of the bot.
		 
         */
        public abstract Location getLocation()
 	;
		    			
 		/**
         * 
			Absolute velocity of the bot as a vector of movement per one
			game second.
		 
         */
        public abstract Velocity getVelocity()
 	;
		    			
 		/**
         * 
			Which direction the bot is facing in absolute terms.
		 
         */
        public abstract Rotation getRotation()
 	;
		    			
 		/**
         * 
			What team the bot is on. 255 is no team. 0-3 are red,
			blue, green, gold in that order.
		 
         */
        public abstract int getTeam()
 	;
		    			
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
        public abstract String getWeapon()
 	;
		    			
 		/**
         * If the bot is shooting or not. 
         */
        public abstract boolean isShooting()
 	;
		    			
 		/**
         * 
			How much health the bot has left. Starts at 100, ranges from
			0 to 200.
		 
         */
        public abstract int getHealth()
 	;
		    			
 		/**
         * 
			How much ammo the bot has left for current weapon primary
			mode.
		 
         */
        public abstract int getPrimaryAmmo()
 	;
		    			
 		/**
         * 
			How much ammo the bot has left for current weapon secondary
			mode. Weapon does not have to support sec. fire mode.
		 
         */
        public abstract int getSecondaryAmmo()
 	;
		    			
 		/**
         * How much adrenaline the bot has. 
         */
        public abstract int getAdrenaline()
 	;
		    			
 		/**
         * 
			Combined size of high armor and low armor (or small armor). The high and low armor are tracked
                        separately. Low armor is limited to 50 points, while the
                        high armor can have up to 150 points. Both stacks can have a combined size of 150 points as well,
                        so if low armor is already at 50 points, high armor can have
                        100 points at max.
		 
         */
        public abstract int getArmor()
 	;
		    			
 		/**
         * 
			Also refered to as a "low armor". Ranges from 0 to 50 points.
		 
         */
        public abstract int getSmallArmor()
 	;
		    			
 		/**
         * 
			If we are firing in secondary firing mode. 
		 
         */
        public abstract boolean isAltFiring()
 	;
		    			
 		/**
         * 
			If we are currently crouched.
		 
         */
        public abstract boolean isCrouched()
 	;
		    			
 		/**
         * 
			If we are currently in walking mode.
		 
         */
        public abstract boolean isWalking()
 	;
		    			
 		/**
         * 
			Holds current floor location under the bot.
		 
         */
        public abstract Location getFloorLocation()
 	;
		    			
 		/**
         * 
			Holds current floor normal under the bot.
		 
         */
        public abstract Location getFloorNormal()
 	;
		    			
 		/**
         * 
			Name of the current combo (None if no combo active).
			Can be xGame.ComboBerserk, xGame.ComboDefensive, xGame.ComboInvis or xGame.ComboSpeed.
			To trigger combo adrenaline needs to be at 100 (maximum) and no other combo can be active.
		 
         */
        public abstract String getCombo()
 	;
		    			
 		/**
         * 
			Time when the UDamage effect expires. If the number is higher then the current
			time, it means the bot has UDamage effect active right now.
		 
         */
        public abstract double getUDamageTime()
 	;
		    			
 		/**
         * 
			Name of the current BDI action.
		 
         */
        public abstract String getAction()
 	;
		    			
 		/**
         * 
			For UE2. Holds left emoticon of the bot, "None" means none set.
		 
         */
        public abstract String getEmotLeft()
 	;
		    			
 		/**
         * 
			For UE2. Holds center emoticon of the bot, "None" means none set.
		 
         */
        public abstract String getEmotCenter()
 	;
		    			
 		/**
         * 
			For UE2. Holds right emoticon of the bot, "None" means none set.
		 
         */
        public abstract String getEmotRight()
 	;
		    			
 		/**
         * 
			For UE2. Holds the bubble of the emoticon of the bot, "None" means none set.
		 
         */
        public abstract String getBubble()
 	;
		    			
 		/**
         * 
			For UE2. Current played animation of the bot.
		 
         */
        public abstract String getAnim()
 	;
		    			
    	
    	public static class SelfUpdate
     extends GBObjectUpdate implements ICompositeWorldObjectUpdatedEvent, IGBWorldObjectEvent {	
			private Self object;
			private long time;
			private ITeamId teamId;
			
			public SelfUpdate
    (Self source, long eventTime, ITeamId teamId) {
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
				return new SelfLocalImpl.SelfLocalUpdate
    ((SelfLocal)object.getLocal(), time);
			}
	
			@Override
			public ISharedWorldObjectUpdatedEvent getSharedEvent() {
				return new SelfSharedImpl.SelfSharedUpdate
    ((SelfShared)object.getShared(), time, teamId);
			}
	
			@Override
			public IStaticWorldObjectUpdatedEvent getStaticEvent() {
				return new SelfStaticImpl.SelfStaticUpdate
    ((SelfStatic)object.getStatic(), time);
			}
			
		}
    
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
		              			"Id = " + String.valueOf(getId()
 	) + " | " + 
		              		
		              			"BotId = " + String.valueOf(getBotId()
 	) + " | " + 
		              		
		              			"Name = " + String.valueOf(getName()
 	) + " | " + 
		              		
		              			"Vehicle = " + String.valueOf(isVehicle()
 	) + " | " + 
		              		
		              			"Location = " + String.valueOf(getLocation()
 	) + " | " + 
		              		
		              			"Velocity = " + String.valueOf(getVelocity()
 	) + " | " + 
		              		
		              			"Rotation = " + String.valueOf(getRotation()
 	) + " | " + 
		              		
		              			"Team = " + String.valueOf(getTeam()
 	) + " | " + 
		              		
		              			"Weapon = " + String.valueOf(getWeapon()
 	) + " | " + 
		              		
		              			"Shooting = " + String.valueOf(isShooting()
 	) + " | " + 
		              		
		              			"Health = " + String.valueOf(getHealth()
 	) + " | " + 
		              		
		              			"PrimaryAmmo = " + String.valueOf(getPrimaryAmmo()
 	) + " | " + 
		              		
		              			"SecondaryAmmo = " + String.valueOf(getSecondaryAmmo()
 	) + " | " + 
		              		
		              			"Adrenaline = " + String.valueOf(getAdrenaline()
 	) + " | " + 
		              		
		              			"Armor = " + String.valueOf(getArmor()
 	) + " | " + 
		              		
		              			"SmallArmor = " + String.valueOf(getSmallArmor()
 	) + " | " + 
		              		
		              			"AltFiring = " + String.valueOf(isAltFiring()
 	) + " | " + 
		              		
		              			"Crouched = " + String.valueOf(isCrouched()
 	) + " | " + 
		              		
		              			"Walking = " + String.valueOf(isWalking()
 	) + " | " + 
		              		
		              			"FloorLocation = " + String.valueOf(getFloorLocation()
 	) + " | " + 
		              		
		              			"FloorNormal = " + String.valueOf(getFloorNormal()
 	) + " | " + 
		              		
		              			"Combo = " + String.valueOf(getCombo()
 	) + " | " + 
		              		
		              			"UDamageTime = " + String.valueOf(getUDamageTime()
 	) + " | " + 
		              		
		              			"Action = " + String.valueOf(getAction()
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
		              		
		              			"<b>BotId</b> = " + String.valueOf(getBotId()
 	) + " <br/> " + 
		              		
		              			"<b>Name</b> = " + String.valueOf(getName()
 	) + " <br/> " + 
		              		
		              			"<b>Vehicle</b> = " + String.valueOf(isVehicle()
 	) + " <br/> " + 
		              		
		              			"<b>Location</b> = " + String.valueOf(getLocation()
 	) + " <br/> " + 
		              		
		              			"<b>Velocity</b> = " + String.valueOf(getVelocity()
 	) + " <br/> " + 
		              		
		              			"<b>Rotation</b> = " + String.valueOf(getRotation()
 	) + " <br/> " + 
		              		
		              			"<b>Team</b> = " + String.valueOf(getTeam()
 	) + " <br/> " + 
		              		
		              			"<b>Weapon</b> = " + String.valueOf(getWeapon()
 	) + " <br/> " + 
		              		
		              			"<b>Shooting</b> = " + String.valueOf(isShooting()
 	) + " <br/> " + 
		              		
		              			"<b>Health</b> = " + String.valueOf(getHealth()
 	) + " <br/> " + 
		              		
		              			"<b>PrimaryAmmo</b> = " + String.valueOf(getPrimaryAmmo()
 	) + " <br/> " + 
		              		
		              			"<b>SecondaryAmmo</b> = " + String.valueOf(getSecondaryAmmo()
 	) + " <br/> " + 
		              		
		              			"<b>Adrenaline</b> = " + String.valueOf(getAdrenaline()
 	) + " <br/> " + 
		              		
		              			"<b>Armor</b> = " + String.valueOf(getArmor()
 	) + " <br/> " + 
		              		
		              			"<b>SmallArmor</b> = " + String.valueOf(getSmallArmor()
 	) + " <br/> " + 
		              		
		              			"<b>AltFiring</b> = " + String.valueOf(isAltFiring()
 	) + " <br/> " + 
		              		
		              			"<b>Crouched</b> = " + String.valueOf(isCrouched()
 	) + " <br/> " + 
		              		
		              			"<b>Walking</b> = " + String.valueOf(isWalking()
 	) + " <br/> " + 
		              		
		              			"<b>FloorLocation</b> = " + String.valueOf(getFloorLocation()
 	) + " <br/> " + 
		              		
		              			"<b>FloorNormal</b> = " + String.valueOf(getFloorNormal()
 	) + " <br/> " + 
		              		
		              			"<b>Combo</b> = " + String.valueOf(getCombo()
 	) + " <br/> " + 
		              		
		              			"<b>UDamageTime</b> = " + String.valueOf(getUDamageTime()
 	) + " <br/> " + 
		              		
		              			"<b>Action</b> = " + String.valueOf(getAction()
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
 	
 	    public String toJsonLiteral() {
            return "self( "
            		+
									(getId()
 	 == null ? "null" :
										"\"" + getId()
 	.getStringId() + "\"" 
									)
								+ ", " + 
									(getBotId()
 	 == null ? "null" :
										"\"" + getBotId()
 	.getStringId() + "\"" 
									)
								+ ", " + 
									(getName()
 	 == null ? "null" :
										"\"" + getName()
 	 + "\"" 
									)
								+ ", " + 
								    String.valueOf(isVehicle()
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
									(getRotation()
 	 == null ? "null" :
										"[" + getRotation()
 	.getPitch() + ", " + getRotation()
 	.getYaw() + ", " + getRotation()
 	.getRoll() + "]" 
									)								    
								+ ", " + 
								    String.valueOf(getTeam()
 	)									
								+ ", " + 
									(getWeapon()
 	 == null ? "null" :
										"\"" + getWeapon()
 	 + "\"" 
									)
								+ ", " + 
								    String.valueOf(isShooting()
 	)									
								+ ", " + 
								    String.valueOf(getHealth()
 	)									
								+ ", " + 
								    String.valueOf(getPrimaryAmmo()
 	)									
								+ ", " + 
								    String.valueOf(getSecondaryAmmo()
 	)									
								+ ", " + 
								    String.valueOf(getAdrenaline()
 	)									
								+ ", " + 
								    String.valueOf(getArmor()
 	)									
								+ ", " + 
								    String.valueOf(getSmallArmor()
 	)									
								+ ", " + 
								    String.valueOf(isAltFiring()
 	)									
								+ ", " + 
								    String.valueOf(isCrouched()
 	)									
								+ ", " + 
								    String.valueOf(isWalking()
 	)									
								+ ", " + 
								    (getFloorLocation()
 	 == null ? "null" :
										"[" + getFloorLocation()
 	.getX() + ", " + getFloorLocation()
 	.getY() + ", " + getFloorLocation()
 	.getZ() + "]" 
									)
								+ ", " + 
								    (getFloorNormal()
 	 == null ? "null" :
										"[" + getFloorNormal()
 	.getX() + ", " + getFloorNormal()
 	.getY() + ", " + getFloorNormal()
 	.getZ() + "]" 
									)
								+ ", " + 
									(getCombo()
 	 == null ? "null" :
										"\"" + getCombo()
 	 + "\"" 
									)
								+ ", " + 
								    String.valueOf(getUDamageTime()
 	)									
								+ ", " + 
									(getAction()
 	 == null ? "null" :
										"\"" + getAction()
 	 + "\"" 
									)
								+ ", " + 
									(getEmotLeft()
 	 == null ? "null" :
										"\"" + getEmotLeft()
 	 + "\"" 
									)
								+ ", " + 
									(getEmotCenter()
 	 == null ? "null" :
										"\"" + getEmotCenter()
 	 + "\"" 
									)
								+ ", " + 
									(getEmotRight()
 	 == null ? "null" :
										"\"" + getEmotRight()
 	 + "\"" 
									)
								+ ", " + 
									(getBubble()
 	 == null ? "null" :
										"\"" + getBubble()
 	 + "\"" 
									)
								+ ", " + 
									(getAnim()
 	 == null ? "null" :
										"\"" + getAnim()
 	 + "\"" 
									)
								
                   + ")";
        }
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=base+classtype[@name=abstract]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=base+classtype[@name=abstract]) ---        	            	
 	
		}
 	