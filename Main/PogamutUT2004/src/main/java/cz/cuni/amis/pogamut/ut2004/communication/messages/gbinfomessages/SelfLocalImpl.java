package cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages;
 		
 		// --- IMPORTS FROM /messages/settings/javasettings/javaimport BEGIN
			import java.util.*;import javax.vecmath.*;import cz.cuni.amis.pogamut.base.communication.messages.*;import cz.cuni.amis.pogamut.base.communication.worldview.*;import cz.cuni.amis.pogamut.base.communication.worldview.event.*;import cz.cuni.amis.pogamut.base.communication.worldview.object.*;import cz.cuni.amis.pogamut.multi.communication.worldview.object.*;import cz.cuni.amis.pogamut.base.communication.translator.event.*;import cz.cuni.amis.pogamut.multi.communication.translator.event.*;import cz.cuni.amis.pogamut.base3d.worldview.object.*;import cz.cuni.amis.pogamut.base3d.worldview.object.event.*;import cz.cuni.amis.pogamut.ut2004.communication.messages.*;import cz.cuni.amis.pogamut.ut2004.communication.worldview.objects.*;import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.objects.*;import cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor.*;import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType.Category;import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;import cz.cuni.amis.utils.exception.*;import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdateResult.Result;import cz.cuni.amis.utils.SafeEquals;import cz.cuni.amis.pogamut.base.agent.*;import cz.cuni.amis.pogamut.multi.agent.*;import cz.cuni.amis.pogamut.multi.communication.worldview.property.*;import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.property.*;import cz.cuni.amis.utils.token.*;import cz.cuni.amis.utils.*;
		// --- IMPORTS FROM /messages/settings/javasettings/javaimport END
		
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name='all'] BEGIN
				
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name='all'] END
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name=local]+classtype[@name=impl] BEGIN
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name=local]+classtype[@name=impl] END
    
 		/**
         *  
            				Implementation of the local part of the GameBots2004 message SLF.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Synchronous message. Information about your bot's state.
	
         */
 	public class SelfLocalImpl 
  						extends
  						SelfLocal
	    {
 	
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public SelfLocalImpl()
		{
		}
	
    	
    	
    	
    	/**
		 * Creates new instance of the message Self.
		 * 
		Synchronous message. Information about your bot's state.
	
		 * Corresponding GameBots message
		 *   (local part)
		 *   is
		 *   SLF.
		 * 
 	  	 * 
		 *   
		 *     @param Id Unique Id of this self message instance.
		 *   
		 * 
		 *   
		 *     @param BotId Unique Id of this bot.
		 *   
		 * 
		 *   
		 *     @param Name Human readable bot name.
		 *   
		 * 
		 *   
		 *     @param Vehicle If we are vehicle just these attr. are sent in SLF: "Id","Vehicle""Rotation", "Location","Velocity ","Name ","Team" ,"Health" 
	"Armor","Adrenaline", "FloorLocation", "FloorNormal".
		 *   
		 * 
		 *   
		 *     @param Location 
			An absolute location of the bot.
		
		 *   
		 * 
		 *   
		 *     @param Velocity 
			Absolute velocity of the bot as a vector of movement per one
			game second.
		
		 *   
		 * 
		 *   
		 *     @param Rotation 
			Which direction the bot is facing in absolute terms.
		
		 *   
		 * 
		 *   
		 *     @param Team 
			What team the bot is on. 255 is no team. 0-3 are red,
			blue, green, gold in that order.
		
		 *   
		 * 
		 *   
		 *     @param Weapon 
			Id of the weapon we are holding. This is unique Id of an
			item in our inventory and is different from the Id of the
			item we pick up from the ground! We can parse this string to
			look which weapon we hold. Weapon strings to look for
			include: "AssaultRifle", "ShieldGun", "FlakCannon",
			"BioRifle", "ShockRifle", "LinkGun", "SniperRifle",
			"RocketLauncher", "Minigun", "LightingGun", "Translocator".
			TODO: Look if this is all.
		
		 *   
		 * 
		 *   
		 *     @param Shooting If the bot is shooting or not.
		 *   
		 * 
		 *   
		 *     @param Health 
			How much health the bot has left. Starts at 100, ranges from
			0 to 200.
		
		 *   
		 * 
		 *   
		 *     @param PrimaryAmmo 
			How much ammo the bot has left for current weapon primary
			mode.
		
		 *   
		 * 
		 *   
		 *     @param SecondaryAmmo 
			How much ammo the bot has left for current weapon secondary
			mode. Weapon does not have to support sec. fire mode.
		
		 *   
		 * 
		 *   
		 *     @param Adrenaline How much adrenaline the bot has.
		 *   
		 * 
		 *   
		 *     @param Armor 
			Combined size of high armor and low armor (or small armor). The high and low armor are tracked
                        separately. Low armor is limited to 50 points, while the
                        high armor can have up to 150 points. Both stacks can have a combined size of 150 points as well,
                        so if low armor is already at 50 points, high armor can have
                        100 points at max.
		
		 *   
		 * 
		 *   
		 *     @param SmallArmor 
			Also refered to as a "low armor". Ranges from 0 to 50 points.
		
		 *   
		 * 
		 *   
		 *     @param AltFiring 
			If we are firing in secondary firing mode. 
		
		 *   
		 * 
		 *   
		 *     @param Crouched 
			If we are currently crouched.
		
		 *   
		 * 
		 *   
		 *     @param Walking 
			If we are currently in walking mode.
		
		 *   
		 * 
		 *   
		 *     @param FloorLocation 
			Holds current floor location under the bot.
		
		 *   
		 * 
		 *   
		 *     @param FloorNormal 
			Holds current floor normal under the bot.
		
		 *   
		 * 
		 *   
		 *     @param Combo 
			Name of the current combo (None if no combo active).
			Can be xGame.ComboBerserk, xGame.ComboDefensive, xGame.ComboInvis or xGame.ComboSpeed.
			To trigger combo adrenaline needs to be at 100 (maximum) and no other combo can be active.
		
		 *   
		 * 
		 *   
		 *     @param UDamageTime 
			Time when the UDamage effect expires. If the number is higher then the current
			time, it means the bot has UDamage effect active right now.
		
		 *   
		 * 
		 *   
		 *     @param Action 
			Name of the current BDI action.
		
		 *   
		 * 
		 *   
		 *     @param EmotLeft 
			For UE2. Holds left emoticon of the bot, "None" means none set.
		
		 *   
		 * 
		 *   
		 *     @param EmotCenter 
			For UE2. Holds center emoticon of the bot, "None" means none set.
		
		 *   
		 * 
		 *   
		 *     @param EmotRight 
			For UE2. Holds right emoticon of the bot, "None" means none set.
		
		 *   
		 * 
		 *   
		 *     @param Bubble 
			For UE2. Holds the bubble of the emoticon of the bot, "None" means none set.
		
		 *   
		 * 
		 *   
		 *     @param Anim 
			For UE2. Current played animation of the bot.
		
		 *   
		 * 
		 */
		public SelfLocalImpl(
			UnrealId Id,  UnrealId BotId,  String Name,  boolean Vehicle,  Location Location,  Velocity Velocity,  Rotation Rotation,  int Team,  String Weapon,  boolean Shooting,  int Health,  int PrimaryAmmo,  int SecondaryAmmo,  int Adrenaline,  int Armor,  int SmallArmor,  boolean AltFiring,  boolean Crouched,  boolean Walking,  Location FloorLocation,  Location FloorNormal,  String Combo,  double UDamageTime,  String Action,  String EmotLeft,  String EmotCenter,  String EmotRight,  String Bubble,  String Anim
		) {
			
					this.Id = Id;
				
					this.BotId = BotId;
				
					this.Name = Name;
				
					this.Vehicle = Vehicle;
				
					this.Location = Location;
				
					this.Velocity = Velocity;
				
					this.Rotation = Rotation;
				
					this.Team = Team;
				
					this.Weapon = Weapon;
				
					this.Shooting = Shooting;
				
					this.Health = Health;
				
					this.PrimaryAmmo = PrimaryAmmo;
				
					this.SecondaryAmmo = SecondaryAmmo;
				
					this.Adrenaline = Adrenaline;
				
					this.Armor = Armor;
				
					this.SmallArmor = SmallArmor;
				
					this.AltFiring = AltFiring;
				
					this.Crouched = Crouched;
				
					this.Walking = Walking;
				
					this.FloorLocation = FloorLocation;
				
					this.FloorNormal = FloorNormal;
				
					this.Combo = Combo;
				
					this.UDamageTime = UDamageTime;
				
					this.Action = Action;
				
					this.EmotLeft = EmotLeft;
				
					this.EmotCenter = EmotCenter;
				
					this.EmotRight = EmotRight;
				
					this.Bubble = Bubble;
				
					this.Anim = Anim;
				
		}
    
    	/**
		 * Cloning constructor from the full message.
		 *
		 * @param original
		 */
		public SelfLocalImpl(Self original) {		
			
					this.Id = original.getId()
 	;
				
					this.BotId = original.getBotId()
 	;
				
					this.Name = original.getName()
 	;
				
					this.Vehicle = original.isVehicle()
 	;
				
					this.Location = original.getLocation()
 	;
				
					this.Velocity = original.getVelocity()
 	;
				
					this.Rotation = original.getRotation()
 	;
				
					this.Team = original.getTeam()
 	;
				
					this.Weapon = original.getWeapon()
 	;
				
					this.Shooting = original.isShooting()
 	;
				
					this.Health = original.getHealth()
 	;
				
					this.PrimaryAmmo = original.getPrimaryAmmo()
 	;
				
					this.SecondaryAmmo = original.getSecondaryAmmo()
 	;
				
					this.Adrenaline = original.getAdrenaline()
 	;
				
					this.Armor = original.getArmor()
 	;
				
					this.SmallArmor = original.getSmallArmor()
 	;
				
					this.AltFiring = original.isAltFiring()
 	;
				
					this.Crouched = original.isCrouched()
 	;
				
					this.Walking = original.isWalking()
 	;
				
					this.FloorLocation = original.getFloorLocation()
 	;
				
					this.FloorNormal = original.getFloorNormal()
 	;
				
					this.Combo = original.getCombo()
 	;
				
					this.UDamageTime = original.getUDamageTime()
 	;
				
					this.Action = original.getAction()
 	;
				
					this.EmotLeft = original.getEmotLeft()
 	;
				
					this.EmotCenter = original.getEmotCenter()
 	;
				
					this.EmotRight = original.getEmotRight()
 	;
				
					this.Bubble = original.getBubble()
 	;
				
					this.Anim = original.getAnim()
 	;
				
			this.SimTime = original.getSimTime();			
		}
		
		/**
		 * Cloning constructor from the full message.
		 *
		 * @param original
		 */
		public SelfLocalImpl(SelfLocalImpl original) {		
			
					this.Id = original.getId()
 	;
				
					this.BotId = original.getBotId()
 	;
				
					this.Name = original.getName()
 	;
				
					this.Vehicle = original.isVehicle()
 	;
				
					this.Location = original.getLocation()
 	;
				
					this.Velocity = original.getVelocity()
 	;
				
					this.Rotation = original.getRotation()
 	;
				
					this.Team = original.getTeam()
 	;
				
					this.Weapon = original.getWeapon()
 	;
				
					this.Shooting = original.isShooting()
 	;
				
					this.Health = original.getHealth()
 	;
				
					this.PrimaryAmmo = original.getPrimaryAmmo()
 	;
				
					this.SecondaryAmmo = original.getSecondaryAmmo()
 	;
				
					this.Adrenaline = original.getAdrenaline()
 	;
				
					this.Armor = original.getArmor()
 	;
				
					this.SmallArmor = original.getSmallArmor()
 	;
				
					this.AltFiring = original.isAltFiring()
 	;
				
					this.Crouched = original.isCrouched()
 	;
				
					this.Walking = original.isWalking()
 	;
				
					this.FloorLocation = original.getFloorLocation()
 	;
				
					this.FloorNormal = original.getFloorNormal()
 	;
				
					this.Combo = original.getCombo()
 	;
				
					this.UDamageTime = original.getUDamageTime()
 	;
				
					this.Action = original.getAction()
 	;
				
					this.EmotLeft = original.getEmotLeft()
 	;
				
					this.EmotCenter = original.getEmotCenter()
 	;
				
					this.EmotRight = original.getEmotRight()
 	;
				
					this.Bubble = original.getBubble()
 	;
				
					this.Anim = original.getAnim()
 	;
				
			this.SimTime = original.getSimTime();
		}
		
			/**
			 * Cloning constructor from the message part.
			 *
			 * @param original
			 */
			public SelfLocalImpl(SelfLocal original) {
				
						this.Id = original.getId()
 	;
					
						this.BotId = original.getBotId()
 	;
					
						this.Name = original.getName()
 	;
					
						this.Vehicle = original.isVehicle()
 	;
					
						this.Location = original.getLocation()
 	;
					
						this.Velocity = original.getVelocity()
 	;
					
						this.Rotation = original.getRotation()
 	;
					
						this.Team = original.getTeam()
 	;
					
						this.Weapon = original.getWeapon()
 	;
					
						this.Shooting = original.isShooting()
 	;
					
						this.Health = original.getHealth()
 	;
					
						this.PrimaryAmmo = original.getPrimaryAmmo()
 	;
					
						this.SecondaryAmmo = original.getSecondaryAmmo()
 	;
					
						this.Adrenaline = original.getAdrenaline()
 	;
					
						this.Armor = original.getArmor()
 	;
					
						this.SmallArmor = original.getSmallArmor()
 	;
					
						this.AltFiring = original.isAltFiring()
 	;
					
						this.Crouched = original.isCrouched()
 	;
					
						this.Walking = original.isWalking()
 	;
					
						this.FloorLocation = original.getFloorLocation()
 	;
					
						this.FloorNormal = original.getFloorNormal()
 	;
					
						this.Combo = original.getCombo()
 	;
					
						this.UDamageTime = original.getUDamageTime()
 	;
					
						this.Action = original.getAction()
 	;
					
						this.EmotLeft = original.getEmotLeft()
 	;
					
						this.EmotCenter = original.getEmotCenter()
 	;
					
						this.EmotRight = original.getEmotRight()
 	;
					
						this.Bubble = original.getBubble()
 	;
					
						this.Anim = original.getAnim()
 	;
					
			}
		
   				
   				@Override
   				public void setSimTime(long SimTime) {
					super.setSimTime(SimTime);
				}
   			
	    				@Override
	    				public 
	    				SelfLocalImpl clone() {
	    					return new 
	    					SelfLocalImpl(this);
	    				}
	    				
	    				
    	
	    /**
         * Unique Id of this self message instance. 
         */
        protected
         UnrealId Id =
       	null;
	
 		/**
         * Unique Id of this self message instance. 
         */
        public  UnrealId getId()
 	 {
				    					return Id;
				    				}
				    			
    	
	    /**
         * Unique Id of this bot. 
         */
        protected
         UnrealId BotId =
       	null;
	
 		/**
         * Unique Id of this bot. 
         */
        public  UnrealId getBotId()
 	 {
				    					return BotId;
				    				}
				    			
    	
	    /**
         * Human readable bot name. 
         */
        protected
         String Name =
       	null;
	
 		/**
         * Human readable bot name. 
         */
        public  String getName()
 	 {
				    					return Name;
				    				}
				    			
    	
	    /**
         * If we are vehicle just these attr. are sent in SLF: "Id","Vehicle""Rotation", "Location","Velocity ","Name ","Team" ,"Health" 
	"Armor","Adrenaline", "FloorLocation", "FloorNormal". 
         */
        protected
         boolean Vehicle =
       	false;
	
 		/**
         * If we are vehicle just these attr. are sent in SLF: "Id","Vehicle""Rotation", "Location","Velocity ","Name ","Team" ,"Health" 
	"Armor","Adrenaline", "FloorLocation", "FloorNormal". 
         */
        public  boolean isVehicle()
 	 {
				    					return Vehicle;
				    				}
				    			
    	
	    /**
         * 
			An absolute location of the bot.
		 
         */
        protected
         Location Location =
       	null;
	
 		/**
         * 
			An absolute location of the bot.
		 
         */
        public  Location getLocation()
 	 {
				    					return Location;
				    				}
				    			
    	
	    /**
         * 
			Absolute velocity of the bot as a vector of movement per one
			game second.
		 
         */
        protected
         Velocity Velocity =
       	null;
	
 		/**
         * 
			Absolute velocity of the bot as a vector of movement per one
			game second.
		 
         */
        public  Velocity getVelocity()
 	 {
				    					return Velocity;
				    				}
				    			
    	
	    /**
         * 
			Which direction the bot is facing in absolute terms.
		 
         */
        protected
         Rotation Rotation =
       	null;
	
 		/**
         * 
			Which direction the bot is facing in absolute terms.
		 
         */
        public  Rotation getRotation()
 	 {
				    					return Rotation;
				    				}
				    			
    	
	    /**
         * 
			What team the bot is on. 255 is no team. 0-3 are red,
			blue, green, gold in that order.
		 
         */
        protected
         int Team =
       	0;
	
 		/**
         * 
			What team the bot is on. 255 is no team. 0-3 are red,
			blue, green, gold in that order.
		 
         */
        public  int getTeam()
 	 {
				    					return Team;
				    				}
				    			
    	
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
        protected
         String Weapon =
       	null;
	
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
				    					return Weapon;
				    				}
				    			
    	
	    /**
         * If the bot is shooting or not. 
         */
        protected
         boolean Shooting =
       	false;
	
 		/**
         * If the bot is shooting or not. 
         */
        public  boolean isShooting()
 	 {
				    					return Shooting;
				    				}
				    			
    	
	    /**
         * 
			How much health the bot has left. Starts at 100, ranges from
			0 to 200.
		 
         */
        protected
         int Health =
       	0;
	
 		/**
         * 
			How much health the bot has left. Starts at 100, ranges from
			0 to 200.
		 
         */
        public  int getHealth()
 	 {
				    					return Health;
				    				}
				    			
    	
	    /**
         * 
			How much ammo the bot has left for current weapon primary
			mode.
		 
         */
        protected
         int PrimaryAmmo =
       	0;
	
 		/**
         * 
			How much ammo the bot has left for current weapon primary
			mode.
		 
         */
        public  int getPrimaryAmmo()
 	 {
				    					return PrimaryAmmo;
				    				}
				    			
    	
	    /**
         * 
			How much ammo the bot has left for current weapon secondary
			mode. Weapon does not have to support sec. fire mode.
		 
         */
        protected
         int SecondaryAmmo =
       	0;
	
 		/**
         * 
			How much ammo the bot has left for current weapon secondary
			mode. Weapon does not have to support sec. fire mode.
		 
         */
        public  int getSecondaryAmmo()
 	 {
				    					return SecondaryAmmo;
				    				}
				    			
    	
	    /**
         * How much adrenaline the bot has. 
         */
        protected
         int Adrenaline =
       	0;
	
 		/**
         * How much adrenaline the bot has. 
         */
        public  int getAdrenaline()
 	 {
				    					return Adrenaline;
				    				}
				    			
    	
	    /**
         * 
			Combined size of high armor and low armor (or small armor). The high and low armor are tracked
                        separately. Low armor is limited to 50 points, while the
                        high armor can have up to 150 points. Both stacks can have a combined size of 150 points as well,
                        so if low armor is already at 50 points, high armor can have
                        100 points at max.
		 
         */
        protected
         int Armor =
       	0;
	
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
				    					return Armor;
				    				}
				    			
    	
	    /**
         * 
			Also refered to as a "low armor". Ranges from 0 to 50 points.
		 
         */
        protected
         int SmallArmor =
       	0;
	
 		/**
         * 
			Also refered to as a "low armor". Ranges from 0 to 50 points.
		 
         */
        public  int getSmallArmor()
 	 {
				    					return SmallArmor;
				    				}
				    			
    	
	    /**
         * 
			If we are firing in secondary firing mode. 
		 
         */
        protected
         boolean AltFiring =
       	false;
	
 		/**
         * 
			If we are firing in secondary firing mode. 
		 
         */
        public  boolean isAltFiring()
 	 {
				    					return AltFiring;
				    				}
				    			
    	
	    /**
         * 
			If we are currently crouched.
		 
         */
        protected
         boolean Crouched =
       	false;
	
 		/**
         * 
			If we are currently crouched.
		 
         */
        public  boolean isCrouched()
 	 {
				    					return Crouched;
				    				}
				    			
    	
	    /**
         * 
			If we are currently in walking mode.
		 
         */
        protected
         boolean Walking =
       	false;
	
 		/**
         * 
			If we are currently in walking mode.
		 
         */
        public  boolean isWalking()
 	 {
				    					return Walking;
				    				}
				    			
    	
	    /**
         * 
			Holds current floor location under the bot.
		 
         */
        protected
         Location FloorLocation =
       	null;
	
 		/**
         * 
			Holds current floor location under the bot.
		 
         */
        public  Location getFloorLocation()
 	 {
				    					return FloorLocation;
				    				}
				    			
    	
	    /**
         * 
			Holds current floor normal under the bot.
		 
         */
        protected
         Location FloorNormal =
       	null;
	
 		/**
         * 
			Holds current floor normal under the bot.
		 
         */
        public  Location getFloorNormal()
 	 {
				    					return FloorNormal;
				    				}
				    			
    	
	    /**
         * 
			Name of the current combo (None if no combo active).
			Can be xGame.ComboBerserk, xGame.ComboDefensive, xGame.ComboInvis or xGame.ComboSpeed.
			To trigger combo adrenaline needs to be at 100 (maximum) and no other combo can be active.
		 
         */
        protected
         String Combo =
       	null;
	
 		/**
         * 
			Name of the current combo (None if no combo active).
			Can be xGame.ComboBerserk, xGame.ComboDefensive, xGame.ComboInvis or xGame.ComboSpeed.
			To trigger combo adrenaline needs to be at 100 (maximum) and no other combo can be active.
		 
         */
        public  String getCombo()
 	 {
				    					return Combo;
				    				}
				    			
    	
	    /**
         * 
			Time when the UDamage effect expires. If the number is higher then the current
			time, it means the bot has UDamage effect active right now.
		 
         */
        protected
         double UDamageTime =
       	0;
	
 		/**
         * 
			Time when the UDamage effect expires. If the number is higher then the current
			time, it means the bot has UDamage effect active right now.
		 
         */
        public  double getUDamageTime()
 	 {
				    					return UDamageTime;
				    				}
				    			
    	
	    /**
         * 
			Name of the current BDI action.
		 
         */
        protected
         String Action =
       	null;
	
 		/**
         * 
			Name of the current BDI action.
		 
         */
        public  String getAction()
 	 {
				    					return Action;
				    				}
				    			
    	
	    /**
         * 
			For UE2. Holds left emoticon of the bot, "None" means none set.
		 
         */
        protected
         String EmotLeft =
       	null;
	
 		/**
         * 
			For UE2. Holds left emoticon of the bot, "None" means none set.
		 
         */
        public  String getEmotLeft()
 	 {
				    					return EmotLeft;
				    				}
				    			
    	
	    /**
         * 
			For UE2. Holds center emoticon of the bot, "None" means none set.
		 
         */
        protected
         String EmotCenter =
       	null;
	
 		/**
         * 
			For UE2. Holds center emoticon of the bot, "None" means none set.
		 
         */
        public  String getEmotCenter()
 	 {
				    					return EmotCenter;
				    				}
				    			
    	
	    /**
         * 
			For UE2. Holds right emoticon of the bot, "None" means none set.
		 
         */
        protected
         String EmotRight =
       	null;
	
 		/**
         * 
			For UE2. Holds right emoticon of the bot, "None" means none set.
		 
         */
        public  String getEmotRight()
 	 {
				    					return EmotRight;
				    				}
				    			
    	
	    /**
         * 
			For UE2. Holds the bubble of the emoticon of the bot, "None" means none set.
		 
         */
        protected
         String Bubble =
       	null;
	
 		/**
         * 
			For UE2. Holds the bubble of the emoticon of the bot, "None" means none set.
		 
         */
        public  String getBubble()
 	 {
				    					return Bubble;
				    				}
				    			
    	
	    /**
         * 
			For UE2. Current played animation of the bot.
		 
         */
        protected
         String Anim =
       	null;
	
 		/**
         * 
			For UE2. Current played animation of the bot.
		 
         */
        public  String getAnim()
 	 {
				    					return Anim;
				    				}
				    			
    	
    	
    	
    	
    	public SelfLocalImpl getLocal() {
			return this;
    	}
		public ISharedWorldObject getShared() {
		 	throw new UnsupportedOperationException("Could not return LOCAL as SHARED");
		}
		public IStaticWorldObject getStatic() {
		    throw new UnsupportedOperationException("Could not return LOCAL as STATIC");
		}
 	
		public static class SelfLocalUpdate
     implements ILocalWorldObjectUpdatedEvent, IGBWorldObjectEvent
		{
			protected long time;
			
			protected SelfLocal data = null; //contains object data for this update
			
			public SelfLocalUpdate
    (SelfLocal moverLocal, long time)
			{
				this.data = moverLocal;
				this.time = time;
			}
			
			@Override
			public IWorldObjectUpdateResult<ILocalWorldObject> update(
					ILocalWorldObject object) 
			{
				if ( object == null)
				{
					data = new SelfLocalImpl(data); //we always return Impl object
					return new IWorldObjectUpdateResult.WorldObjectUpdateResult<ILocalWorldObject>(IWorldObjectUpdateResult.Result.CREATED, data);
				}
				if ( object instanceof SelfLocalImpl )
				{
					SelfLocalImpl toUpdate = (SelfLocalImpl)object;
					
					boolean updated = false;
					
					// UPDATING LOCAL PROPERTIES
					
				if (!SafeEquals.equals(toUpdate.BotId, data.getBotId()
 	)) {
					toUpdate.BotId=data.getBotId()
 	;
					updated = true;
				}
			
				if (!SafeEquals.equals(toUpdate.Name, data.getName()
 	)) {
					toUpdate.Name=data.getName()
 	;
					updated = true;
				}
			
				if (toUpdate.Vehicle != data.isVehicle()
 	) {
				    toUpdate.Vehicle=data.isVehicle()
 	;
					updated = true;
				}
			
	            if (!SafeEquals.equals(toUpdate.Location, data.getLocation()
 	)) {
					toUpdate.Location=data.getLocation()
 	;
					updated = true;
				}
			
				if (!SafeEquals.equals(toUpdate.Velocity, data.getVelocity()
 	)) {
					toUpdate.Velocity=data.getVelocity()
 	;
					updated = true;
				}
			
				if (!SafeEquals.equals(toUpdate.Rotation, data.getRotation()
 	)) {
					toUpdate.Rotation=data.getRotation()
 	;
					updated = true;
				}
			
				if (toUpdate.Team != data.getTeam()
 	) {
				    toUpdate.Team=data.getTeam()
 	;
					updated = true;
				}
			
				if (!SafeEquals.equals(toUpdate.Weapon, data.getWeapon()
 	)) {
					toUpdate.Weapon=data.getWeapon()
 	;
					updated = true;
				}
			
				if (toUpdate.Shooting != data.isShooting()
 	) {
				    toUpdate.Shooting=data.isShooting()
 	;
					updated = true;
				}
			
				if (toUpdate.Health != data.getHealth()
 	) {
				    toUpdate.Health=data.getHealth()
 	;
					updated = true;
				}
			
				if (toUpdate.PrimaryAmmo != data.getPrimaryAmmo()
 	) {
				    toUpdate.PrimaryAmmo=data.getPrimaryAmmo()
 	;
					updated = true;
				}
			
				if (toUpdate.SecondaryAmmo != data.getSecondaryAmmo()
 	) {
				    toUpdate.SecondaryAmmo=data.getSecondaryAmmo()
 	;
					updated = true;
				}
			
				if (toUpdate.Adrenaline != data.getAdrenaline()
 	) {
				    toUpdate.Adrenaline=data.getAdrenaline()
 	;
					updated = true;
				}
			
				if (toUpdate.Armor != data.getArmor()
 	) {
				    toUpdate.Armor=data.getArmor()
 	;
					updated = true;
				}
			
				if (toUpdate.SmallArmor != data.getSmallArmor()
 	) {
				    toUpdate.SmallArmor=data.getSmallArmor()
 	;
					updated = true;
				}
			
				if (toUpdate.AltFiring != data.isAltFiring()
 	) {
				    toUpdate.AltFiring=data.isAltFiring()
 	;
					updated = true;
				}
			
				if (toUpdate.Crouched != data.isCrouched()
 	) {
				    toUpdate.Crouched=data.isCrouched()
 	;
					updated = true;
				}
			
				if (toUpdate.Walking != data.isWalking()
 	) {
				    toUpdate.Walking=data.isWalking()
 	;
					updated = true;
				}
			
	            if (!SafeEquals.equals(toUpdate.FloorLocation, data.getFloorLocation()
 	)) {
					toUpdate.FloorLocation=data.getFloorLocation()
 	;
					updated = true;
				}
			
	            if (!SafeEquals.equals(toUpdate.FloorNormal, data.getFloorNormal()
 	)) {
					toUpdate.FloorNormal=data.getFloorNormal()
 	;
					updated = true;
				}
			
				if (!SafeEquals.equals(toUpdate.Combo, data.getCombo()
 	)) {
					toUpdate.Combo=data.getCombo()
 	;
					updated = true;
				}
			
				if (toUpdate.UDamageTime != data.getUDamageTime()
 	) {
				    toUpdate.UDamageTime=data.getUDamageTime()
 	;
					updated = true;
				}
			
				if (!SafeEquals.equals(toUpdate.Action, data.getAction()
 	)) {
					toUpdate.Action=data.getAction()
 	;
					updated = true;
				}
			
				if (!SafeEquals.equals(toUpdate.EmotLeft, data.getEmotLeft()
 	)) {
					toUpdate.EmotLeft=data.getEmotLeft()
 	;
					updated = true;
				}
			
				if (!SafeEquals.equals(toUpdate.EmotCenter, data.getEmotCenter()
 	)) {
					toUpdate.EmotCenter=data.getEmotCenter()
 	;
					updated = true;
				}
			
				if (!SafeEquals.equals(toUpdate.EmotRight, data.getEmotRight()
 	)) {
					toUpdate.EmotRight=data.getEmotRight()
 	;
					updated = true;
				}
			
				if (!SafeEquals.equals(toUpdate.Bubble, data.getBubble()
 	)) {
					toUpdate.Bubble=data.getBubble()
 	;
					updated = true;
				}
			
				if (!SafeEquals.equals(toUpdate.Anim, data.getAnim()
 	)) {
					toUpdate.Anim=data.getAnim()
 	;
					updated = true;
				}
			
					
					data = toUpdate; //the updating has finished
					
					if ( updated )
					{
						toUpdate.SimTime = this.time;
						return new IWorldObjectUpdateResult.WorldObjectUpdateResult<ILocalWorldObject>(IWorldObjectUpdateResult.Result.UPDATED, data);
					}
					
					return new IWorldObjectUpdateResult.WorldObjectUpdateResult<ILocalWorldObject>(IWorldObjectUpdateResult.Result.SAME, data);
				}
				throw new PogamutException("Unsupported object type for update. Expected SelfLocalImpl for object " + object.getId() +", not object of class " + object.getClass().getSimpleName() + ".", this);
			}
	
			/**
			 * Simulation time in MILLI SECONDS !!!
			 */
			@Override
			public long getSimTime() {
				return this.time;
			}
	
			@Override
			public IWorldObject getObject() {
				return data;
			}
	
			@Override
			public WorldObjectId getId() {
				return data.getId();
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
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=local+classtype[@name=impl]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=local+classtype[@name=impl]) ---        	            	
 	
		}
 	