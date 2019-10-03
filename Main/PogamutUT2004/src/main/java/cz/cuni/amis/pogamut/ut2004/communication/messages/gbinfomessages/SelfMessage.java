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
             				Implementation of the GameBots2004 message SLF contains also its Local/Shared/Static subpart class definitions..  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Synchronous message. Information about your bot's state.
	
         */
 	public class SelfMessage   
  				extends 
  				Self
  						implements IWorldObjectUpdatedEvent, ICompositeWorldObjectUpdatedEvent
  						
	    {
 	
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public SelfMessage()
		{
		}
	
    	
    	
    	
    	/**
		 * Creates new instance of the message Self.
		 * 
		Synchronous message. Information about your bot's state.
	
		 * Corresponding GameBots message
		 *   
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
		public SelfMessage(
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
		public SelfMessage(SelfMessage original) {		
			
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
         * Unique Id of this self message instance. 
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
		 					 * Whether property 'BotId' was received from GB2004.
		 					 */
							protected boolean BotId_Set = false;
							
    						@Override
		    				
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
		 					 * Whether property 'Name' was received from GB2004.
		 					 */
							protected boolean Name_Set = false;
							
    						@Override
		    				
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
		 					 * Whether property 'Vehicle' was received from GB2004.
		 					 */
							protected boolean Vehicle_Set = false;
							
    						@Override
		    				
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
		 					 * Whether property 'Location' was received from GB2004.
		 					 */
							protected boolean Location_Set = false;
							
    						@Override
		    				
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
		 					 * Whether property 'Velocity' was received from GB2004.
		 					 */
							protected boolean Velocity_Set = false;
							
    						@Override
		    				
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
		 					 * Whether property 'Rotation' was received from GB2004.
		 					 */
							protected boolean Rotation_Set = false;
							
    						@Override
		    				
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
		 					 * Whether property 'Team' was received from GB2004.
		 					 */
							protected boolean Team_Set = false;
							
    						@Override
		    				
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
		 					 * Whether property 'Weapon' was received from GB2004.
		 					 */
							protected boolean Weapon_Set = false;
							
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
		    					return Weapon;
		    				}
		    			
    	
	    /**
         * If the bot is shooting or not. 
         */
        protected
         boolean Shooting =
       	false;
	
    						
    						/**
		 					 * Whether property 'Shooting' was received from GB2004.
		 					 */
							protected boolean Shooting_Set = false;
							
    						@Override
		    				
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
		 					 * Whether property 'Health' was received from GB2004.
		 					 */
							protected boolean Health_Set = false;
							
    						@Override
		    				
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
		 					 * Whether property 'PrimaryAmmo' was received from GB2004.
		 					 */
							protected boolean PrimaryAmmo_Set = false;
							
    						@Override
		    				
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
		 					 * Whether property 'SecondaryAmmo' was received from GB2004.
		 					 */
							protected boolean SecondaryAmmo_Set = false;
							
    						@Override
		    				
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
		 					 * Whether property 'Adrenaline' was received from GB2004.
		 					 */
							protected boolean Adrenaline_Set = false;
							
    						@Override
		    				
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
		 					 * Whether property 'Armor' was received from GB2004.
		 					 */
							protected boolean Armor_Set = false;
							
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
		 					 * Whether property 'SmallArmor' was received from GB2004.
		 					 */
							protected boolean SmallArmor_Set = false;
							
    						@Override
		    				
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
		 					 * Whether property 'AltFiring' was received from GB2004.
		 					 */
							protected boolean AltFiring_Set = false;
							
    						@Override
		    				
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
		 					 * Whether property 'Crouched' was received from GB2004.
		 					 */
							protected boolean Crouched_Set = false;
							
    						@Override
		    				
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
		 					 * Whether property 'Walking' was received from GB2004.
		 					 */
							protected boolean Walking_Set = false;
							
    						@Override
		    				
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
		 					 * Whether property 'FloorLocation' was received from GB2004.
		 					 */
							protected boolean FloorLocation_Set = false;
							
    						@Override
		    				
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
		 					 * Whether property 'FloorNormal' was received from GB2004.
		 					 */
							protected boolean FloorNormal_Set = false;
							
    						@Override
		    				
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
		 					 * Whether property 'Combo' was received from GB2004.
		 					 */
							protected boolean Combo_Set = false;
							
    						@Override
		    				
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
		 					 * Whether property 'UDamageTime' was received from GB2004.
		 					 */
							protected boolean UDamageTime_Set = false;
							
    						@Override
		    				
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
		 					 * Whether property 'Action' was received from GB2004.
		 					 */
							protected boolean Action_Set = false;
							
    						@Override
		    				
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
		 					 * Whether property 'EmotLeft' was received from GB2004.
		 					 */
							protected boolean EmotLeft_Set = false;
							
    						@Override
		    				
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
		 					 * Whether property 'EmotCenter' was received from GB2004.
		 					 */
							protected boolean EmotCenter_Set = false;
							
    						@Override
		    				
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
		 					 * Whether property 'EmotRight' was received from GB2004.
		 					 */
							protected boolean EmotRight_Set = false;
							
    						@Override
		    				
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
		 					 * Whether property 'Bubble' was received from GB2004.
		 					 */
							protected boolean Bubble_Set = false;
							
    						@Override
		    				
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
		 					 * Whether property 'Anim' was received from GB2004.
		 					 */
							protected boolean Anim_Set = false;
							
    						@Override
		    				
 		/**
         * 
			For UE2. Current played animation of the bot.
		 
         */
        public  String getAnim()
 	 {
		    					return Anim;
		    				}
		    			
		    			
		    			private SelfLocal localPart = null;
		    			
		    			@Override
						public SelfLocal 
						getLocal() {
							if (localPart != null) return localPart;
							return localPart = new 
								SelfLocalMessage();
						}
					
						private SelfShared sharedPart = null;
					
						@Override
						public SelfShared 
						getShared() {
							if (sharedPart != null) return sharedPart;							
							return sharedPart = new 
								SelfSharedMessage();
						}
					
						private SelfStatic staticPart = null; 
					
						@Override
						public SelfStatic 
						getStatic() {
							if (staticPart != null) return staticPart;
							return staticPart = new 
								SelfStaticMessage();
						}
    				
 		/**
         *  
            				Implementation of the local part of the GameBots2004 message SLF, used
            				to facade SLFMessage.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Synchronous message. Information about your bot's state.
	
         */
 	public class SelfLocalMessage 
	  					extends
  						SelfLocal
	    {
 	
		    			@Override
		    			public 
		    			SelfLocalMessage clone() {
		    				return this;
		    			}
		    			
		    				public SelfLocalMessage getLocal() {
								return this;
					    	}
							public ISharedWorldObject getShared() {
							 	throw new UnsupportedOperationException("Could not return LOCAL as SHARED");
							}
							public IStaticWorldObject getStatic() {
							    throw new UnsupportedOperationException("Could not return LOCAL as STATIC");
							}
		    			
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
        public  UnrealId getBotId()
 	 {
				    					return BotId;
				    				}
				    			
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
        public  boolean isVehicle()
 	 {
				    					return Vehicle;
				    				}
				    			
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
        public  Velocity getVelocity()
 	 {
				    					return Velocity;
				    				}
				    			
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
        public  String getWeapon()
 	 {
				    					return Weapon;
				    				}
				    			
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
        public  int getHealth()
 	 {
				    					return Health;
				    				}
				    			
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
        public  int getSecondaryAmmo()
 	 {
				    					return SecondaryAmmo;
				    				}
				    			
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
        public  int getArmor()
 	 {
				    					return Armor;
				    				}
				    			
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
        public  boolean isAltFiring()
 	 {
				    					return AltFiring;
				    				}
				    			
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
        public  boolean isWalking()
 	 {
				    					return Walking;
				    				}
				    			
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
        public  String getCombo()
 	 {
				    					return Combo;
				    				}
				    			
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
        public  String getAction()
 	 {
				    					return Action;
				    				}
				    			
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
        public  String getEmotCenter()
 	 {
				    					return EmotCenter;
				    				}
				    			
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
        public  String getBubble()
 	 {
				    					return Bubble;
				    				}
				    			
 		/**
         * 
			For UE2. Current played animation of the bot.
		 
         */
        public  String getAnim()
 	 {
				    					return Anim;
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
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=local+classtype[@name=message]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=local+classtype[@name=message]) ---        	            	
 	
		}
 	
 		/**
         *  
            				Implementation of the static part of the GameBots2004 message SLF, used
            				to facade SLFMessage.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Synchronous message. Information about your bot's state.
	
         */
 	public class SelfStaticMessage 
	  					extends
  						SelfStatic
	    {
 	
		    			@Override
		    			public 
		    			SelfStaticMessage clone() {
		    				return this;
		    			}
		    			
 		/**
         * Unique Id of this self message instance. 
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
 				SelfStatic obj = (SelfStatic) other;

 				
 						if ( !(
 	 			AdvancedEquals.equalsOrNull(this.getId()
 	, obj.getId()
 	)
 	 		) )
						{
							System.out.println("!!!!!PROPERTY UPDATE ERROR!!!! on property Id on object class SelfStatic");
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
            				Implementation of the shared part of the GameBots2004 message SLF, used
            				to facade SLFMessage.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Synchronous message. Information about your bot's state.
	
         */
 	public class SelfSharedMessage 
	  					extends
  						SelfShared
	    {
 	
    	
    	
		public SelfSharedMessage()
		{
			
		}		
    
		    			@Override
		    			public 
		    			SelfSharedMessage clone() {
		    				return this;
		    			}
		    			
		
		
		
		protected HashMap<PropertyId, ISharedProperty> propertyMap = new HashMap<PropertyId, ISharedProperty>(
			0
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
         * Unique Id of this self message instance. 
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
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=shared+classtype[@name=message]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=shared+classtype[@name=message]) ---        	            	
 	
		}
 	
    	
    	
 	
		@Override
		public IWorldObjectUpdateResult<IWorldObject> update(IWorldObject object) {
			if (object == null)
			{
				return new IWorldObjectUpdateResult.WorldObjectUpdateResult(IWorldObjectUpdateResult.Result.CREATED, this);
			}
			if (!( object instanceof SelfMessage) ) {
				throw new PogamutException("Can't update different class than SelfMessage, got class " + object.getClass().getSimpleName() + "!", this);		
			}
			SelfMessage toUpdate = (SelfMessage)object;
			
			boolean updated = false;
			
			// UPDATING LOCAL PROPERTIES
			
				if (!SafeEquals.equals(toUpdate.BotId, getBotId()
 	)) {
					toUpdate.BotId=getBotId()
 	;
					updated = true;
				}
			
				if (!SafeEquals.equals(toUpdate.Name, getName()
 	)) {
					toUpdate.Name=getName()
 	;
					updated = true;
				}
			
				if (toUpdate.Vehicle != isVehicle()
 	) {
				    toUpdate.Vehicle=isVehicle()
 	;
					updated = true;
				}
			
	            if (!SafeEquals.equals(toUpdate.Location, getLocation()
 	)) {
					toUpdate.Location=getLocation()
 	;
					updated = true;
				}
			
				if (!SafeEquals.equals(toUpdate.Velocity, getVelocity()
 	)) {
					toUpdate.Velocity=getVelocity()
 	;
					updated = true;
				}
			
				if (!SafeEquals.equals(toUpdate.Rotation, getRotation()
 	)) {
					toUpdate.Rotation=getRotation()
 	;
					updated = true;
				}
			
				if (toUpdate.Team != getTeam()
 	) {
				    toUpdate.Team=getTeam()
 	;
					updated = true;
				}
			
				if (!SafeEquals.equals(toUpdate.Weapon, getWeapon()
 	)) {
					toUpdate.Weapon=getWeapon()
 	;
					updated = true;
				}
			
				if (toUpdate.Shooting != isShooting()
 	) {
				    toUpdate.Shooting=isShooting()
 	;
					updated = true;
				}
			
				if (toUpdate.Health != getHealth()
 	) {
				    toUpdate.Health=getHealth()
 	;
					updated = true;
				}
			
				if (toUpdate.PrimaryAmmo != getPrimaryAmmo()
 	) {
				    toUpdate.PrimaryAmmo=getPrimaryAmmo()
 	;
					updated = true;
				}
			
				if (toUpdate.SecondaryAmmo != getSecondaryAmmo()
 	) {
				    toUpdate.SecondaryAmmo=getSecondaryAmmo()
 	;
					updated = true;
				}
			
				if (toUpdate.Adrenaline != getAdrenaline()
 	) {
				    toUpdate.Adrenaline=getAdrenaline()
 	;
					updated = true;
				}
			
				if (toUpdate.Armor != getArmor()
 	) {
				    toUpdate.Armor=getArmor()
 	;
					updated = true;
				}
			
				if (toUpdate.SmallArmor != getSmallArmor()
 	) {
				    toUpdate.SmallArmor=getSmallArmor()
 	;
					updated = true;
				}
			
				if (toUpdate.AltFiring != isAltFiring()
 	) {
				    toUpdate.AltFiring=isAltFiring()
 	;
					updated = true;
				}
			
				if (toUpdate.Crouched != isCrouched()
 	) {
				    toUpdate.Crouched=isCrouched()
 	;
					updated = true;
				}
			
				if (toUpdate.Walking != isWalking()
 	) {
				    toUpdate.Walking=isWalking()
 	;
					updated = true;
				}
			
	            if (!SafeEquals.equals(toUpdate.FloorLocation, getFloorLocation()
 	)) {
					toUpdate.FloorLocation=getFloorLocation()
 	;
					updated = true;
				}
			
	            if (!SafeEquals.equals(toUpdate.FloorNormal, getFloorNormal()
 	)) {
					toUpdate.FloorNormal=getFloorNormal()
 	;
					updated = true;
				}
			
				if (!SafeEquals.equals(toUpdate.Combo, getCombo()
 	)) {
					toUpdate.Combo=getCombo()
 	;
					updated = true;
				}
			
				if (toUpdate.UDamageTime != getUDamageTime()
 	) {
				    toUpdate.UDamageTime=getUDamageTime()
 	;
					updated = true;
				}
			
				if (!SafeEquals.equals(toUpdate.Action, getAction()
 	)) {
					toUpdate.Action=getAction()
 	;
					updated = true;
				}
			
				if (!SafeEquals.equals(toUpdate.EmotLeft, getEmotLeft()
 	)) {
					toUpdate.EmotLeft=getEmotLeft()
 	;
					updated = true;
				}
			
				if (!SafeEquals.equals(toUpdate.EmotCenter, getEmotCenter()
 	)) {
					toUpdate.EmotCenter=getEmotCenter()
 	;
					updated = true;
				}
			
				if (!SafeEquals.equals(toUpdate.EmotRight, getEmotRight()
 	)) {
					toUpdate.EmotRight=getEmotRight()
 	;
					updated = true;
				}
			
				if (!SafeEquals.equals(toUpdate.Bubble, getBubble()
 	)) {
					toUpdate.Bubble=getBubble()
 	;
					updated = true;
				}
			
				if (!SafeEquals.equals(toUpdate.Anim, getAnim()
 	)) {
					toUpdate.Anim=getAnim()
 	;
					updated = true;
				}
			
         	
         	// UPDATING SHARED PROPERTIES
         	
         	
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
			return new SelfLocalImpl.SelfLocalUpdate
    (this.getLocal(), SimTime);
		}

		@Override
		public ISharedWorldObjectUpdatedEvent getSharedEvent() {
			return new SelfSharedImpl.SelfSharedUpdate
    (this.getShared(), SimTime, this.getTeamId());
		}

		@Override
		public IStaticWorldObjectUpdatedEvent getStaticEvent() {
			return new SelfStaticImpl.SelfStaticUpdate
    (this.getStatic(), SimTime);
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
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=base+classtype[@name=message]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=base+classtype[@name=message]) ---        	            	
 	
		}
 	