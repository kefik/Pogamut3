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
             				Implementation of the GameBots2004 message MOV contains also its Local/Shared/Static subpart class definitions..  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Synchronous message. Movers can be doors, elevators, or any
		other chunk of architecture that can move. They generally need
		to be either run into, or activated by shooting or pressing a
		button. We are working on ways to provide bots with more of the
		information they need to deal with movers appropriately.
	
         */
 	public class MoverMessage   
  				extends 
  				Mover
  						implements IWorldObjectUpdatedEvent, ICompositeWorldObjectUpdatedEvent
  						
	    {
 	
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public MoverMessage()
		{
		}
	
    	
    	
    	
    	/**
		 * Creates new instance of the message Mover.
		 * 
		Synchronous message. Movers can be doors, elevators, or any
		other chunk of architecture that can move. They generally need
		to be either run into, or activated by shooting or pressing a
		button. We are working on ways to provide bots with more of the
		information they need to deal with movers appropriately.
	
		 * Corresponding GameBots message
		 *   
		 *   is
		 *   MOV.
		 * 
 	  	 * 
		 *   
		 *     @param Id 
			A unique Id of this mover assigned by the game.
		
		 *   
		 * 
		 *   
		 *     @param Location Location of the mover.
		 *   
		 * 
		 *   
		 *     @param Visible If the mover is in the field of view of the bot.
		 *   
		 * 
		 *   
		 *     @param DamageTrig 
			True if the mover needs to be shot to be activated.
		
		 *   
		 * 
		 *   
		 *     @param Type String class of the mover.
		 *   
		 * 
		 *   
		 *     @param IsMoving Does the mover move right now?
		 *   
		 * 
		 *   
		 *     @param Velocity Velocity vector.
		 *   
		 * 
		 *   
		 *     @param MoveTime How long the mover moves, when it becomes triggered, before it stops.
		 *   
		 * 
		 *   
		 *     @param OpenTime How long the mover stands still when it reaches its destination position. After
      this time, the mover returns back to its initial position.
		 *   
		 * 
		 *   
		 *     @param BasePos Base position of the mover.
		 *   
		 * 
		 *   
		 *     @param BaseRot Base rotation of the mover.
		 *   
		 * 
		 *   
		 *     @param DelayTime Delay before starting to open (or before lift starts to move).
		 *   
		 * 
		 *   
		 *     @param State Name of the state Mover is currently in. Can be used to determine the type of the mover. 
		 *   
		 * 
		 *   
		 *     @param NavPointMarker Navigation point marking this mover. We can parse this Id to get information about type of the mover. Marker Id contains "lift" string if it is a lift, "door" string if it is a door, etc. 
		 *   
		 * 
		 */
		public MoverMessage(
			UnrealId Id,  Location Location,  boolean Visible,  boolean DamageTrig,  String Type,  boolean IsMoving,  Velocity Velocity,  double MoveTime,  double OpenTime,  Location BasePos,  Location BaseRot,  double DelayTime,  String State,  UnrealId NavPointMarker
		) {
			
					this.Id = Id;
				
					this.Location = Location;
				
					this.Visible = Visible;
				
					this.DamageTrig = DamageTrig;
				
					this.Type = Type;
				
					this.IsMoving = IsMoving;
				
					this.Velocity = Velocity;
				
					this.MoveTime = MoveTime;
				
					this.OpenTime = OpenTime;
				
					this.BasePos = BasePos;
				
					this.BaseRot = BaseRot;
				
					this.DelayTime = DelayTime;
				
					this.State = State;
				
					this.NavPointMarker = NavPointMarker;
				
		}
    
		/**
		 * Cloning constructor from the full message.
		 *
		 * @param original
		 */
		public MoverMessage(MoverMessage original) {		
			
					this.Id = original.getId()
 	;
				
					this.Location = original.getLocation()
 	;
				
					this.Visible = original.isVisible()
 	;
				
					this.DamageTrig = original.isDamageTrig()
 	;
				
					this.Type = original.getType()
 	;
				
					this.IsMoving = original.isIsMoving()
 	;
				
					this.Velocity = original.getVelocity()
 	;
				
					this.MoveTime = original.getMoveTime()
 	;
				
					this.OpenTime = original.getOpenTime()
 	;
				
					this.BasePos = original.getBasePos()
 	;
				
					this.BaseRot = original.getBaseRot()
 	;
				
					this.DelayTime = original.getDelayTime()
 	;
				
					this.State = original.getState()
 	;
				
					this.NavPointMarker = original.getNavPointMarker()
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
			A unique Id of this mover assigned by the game.
		 
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
			A unique Id of this mover assigned by the game.
		 
         */
        public  UnrealId getId()
 	 {
		    					return Id;
		    				}
		    			
    	
	    /**
         * Location of the mover. 
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
         * Location of the mover. 
         */
        public  Location getLocation()
 	 {
		    					return Location;
		    				}
		    			
    	
	    /**
         * If the mover is in the field of view of the bot. 
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
         * If the mover is in the field of view of the bot. 
         */
        public  boolean isVisible()
 	 {
		    					return Visible;
		    				}
		    			
    	
	    /**
         * 
			True if the mover needs to be shot to be activated.
		 
         */
        protected
         boolean DamageTrig =
       	false;
	
    						
    						/**
		 					 * Whether property 'DamageTrig' was received from GB2004.
		 					 */
							protected boolean DamageTrig_Set = false;
							
    						@Override
		    				
 		/**
         * 
			True if the mover needs to be shot to be activated.
		 
         */
        public  boolean isDamageTrig()
 	 {
		    					return DamageTrig;
		    				}
		    			
    	
	    /**
         * String class of the mover. 
         */
        protected
         String Type =
       	null;
	
    						
    						/**
		 					 * Whether property 'Type' was received from GB2004.
		 					 */
							protected boolean Type_Set = false;
							
    						@Override
		    				
 		/**
         * String class of the mover. 
         */
        public  String getType()
 	 {
		    					return Type;
		    				}
		    			
    	
	    /**
         * Does the mover move right now? 
         */
        protected
         boolean IsMoving =
       	false;
	
    						
    						/**
		 					 * Whether property 'IsMoving' was received from GB2004.
		 					 */
							protected boolean IsMoving_Set = false;
							
    						@Override
		    				
 		/**
         * Does the mover move right now? 
         */
        public  boolean isIsMoving()
 	 {
		    					return IsMoving;
		    				}
		    			
    	
	    /**
         * Velocity vector. 
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
         * Velocity vector. 
         */
        public  Velocity getVelocity()
 	 {
		    					return Velocity;
		    				}
		    			
    	
	    /**
         * How long the mover moves, when it becomes triggered, before it stops. 
         */
        protected
         double MoveTime =
       	0;
	
    						
    						/**
		 					 * Whether property 'MoveTime' was received from GB2004.
		 					 */
							protected boolean MoveTime_Set = false;
							
    						@Override
		    				
 		/**
         * How long the mover moves, when it becomes triggered, before it stops. 
         */
        public  double getMoveTime()
 	 {
		    					return MoveTime;
		    				}
		    			
    	
	    /**
         * How long the mover stands still when it reaches its destination position. After
      this time, the mover returns back to its initial position. 
         */
        protected
         double OpenTime =
       	0;
	
    						
    						/**
		 					 * Whether property 'OpenTime' was received from GB2004.
		 					 */
							protected boolean OpenTime_Set = false;
							
    						@Override
		    				
 		/**
         * How long the mover stands still when it reaches its destination position. After
      this time, the mover returns back to its initial position. 
         */
        public  double getOpenTime()
 	 {
		    					return OpenTime;
		    				}
		    			
    	
	    /**
         * Base position of the mover. 
         */
        protected
         Location BasePos =
       	null;
	
    						
    						/**
		 					 * Whether property 'BasePos' was received from GB2004.
		 					 */
							protected boolean BasePos_Set = false;
							
    						@Override
		    				
 		/**
         * Base position of the mover. 
         */
        public  Location getBasePos()
 	 {
		    					return BasePos;
		    				}
		    			
    	
	    /**
         * Base rotation of the mover. 
         */
        protected
         Location BaseRot =
       	null;
	
    						
    						/**
		 					 * Whether property 'BaseRot' was received from GB2004.
		 					 */
							protected boolean BaseRot_Set = false;
							
    						@Override
		    				
 		/**
         * Base rotation of the mover. 
         */
        public  Location getBaseRot()
 	 {
		    					return BaseRot;
		    				}
		    			
    	
	    /**
         * Delay before starting to open (or before lift starts to move). 
         */
        protected
         double DelayTime =
       	0;
	
    						
    						/**
		 					 * Whether property 'DelayTime' was received from GB2004.
		 					 */
							protected boolean DelayTime_Set = false;
							
    						@Override
		    				
 		/**
         * Delay before starting to open (or before lift starts to move). 
         */
        public  double getDelayTime()
 	 {
		    					return DelayTime;
		    				}
		    			
    	
	    /**
         * Name of the state Mover is currently in. Can be used to determine the type of the mover.  
         */
        protected
         String State =
       	null;
	
    						
    						/**
		 					 * Whether property 'State' was received from GB2004.
		 					 */
							protected boolean State_Set = false;
							
    						@Override
		    				
 		/**
         * Name of the state Mover is currently in. Can be used to determine the type of the mover.  
         */
        public  String getState()
 	 {
		    					return State;
		    				}
		    			
    	
	    /**
         * Navigation point marking this mover. We can parse this Id to get information about type of the mover. Marker Id contains "lift" string if it is a lift, "door" string if it is a door, etc.  
         */
        protected
         UnrealId NavPointMarker =
       	null;
	
    						
    						/**
		 					 * Whether property 'NavPointMarker' was received from GB2004.
		 					 */
							protected boolean NavPointMarker_Set = false;
							
    						@Override
		    				
 		/**
         * Navigation point marking this mover. We can parse this Id to get information about type of the mover. Marker Id contains "lift" string if it is a lift, "door" string if it is a door, etc.  
         */
        public  UnrealId getNavPointMarker()
 	 {
		    					return NavPointMarker;
		    				}
		    			
		    			
		    			private MoverLocal localPart = null;
		    			
		    			@Override
						public MoverLocal 
						getLocal() {
							if (localPart != null) return localPart;
							return localPart = new 
								MoverLocalMessage();
						}
					
						private MoverShared sharedPart = null;
					
						@Override
						public MoverShared 
						getShared() {
							if (sharedPart != null) return sharedPart;							
							return sharedPart = new 
								MoverSharedMessage();
						}
					
						private MoverStatic staticPart = null; 
					
						@Override
						public MoverStatic 
						getStatic() {
							if (staticPart != null) return staticPart;
							return staticPart = new 
								MoverStaticMessage();
						}
    				
 		/**
         *  
            				Implementation of the local part of the GameBots2004 message MOV, used
            				to facade MOVMessage.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Synchronous message. Movers can be doors, elevators, or any
		other chunk of architecture that can move. They generally need
		to be either run into, or activated by shooting or pressing a
		button. We are working on ways to provide bots with more of the
		information they need to deal with movers appropriately.
	
         */
 	public class MoverLocalMessage 
	  					extends
  						MoverLocal
	    {
 	
		    			@Override
		    			public 
		    			MoverLocalMessage clone() {
		    				return this;
		    			}
		    			
		    				public MoverLocalMessage getLocal() {
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
			A unique Id of this mover assigned by the game.
		 
         */
        public  UnrealId getId()
 	 {
				    					return Id;
				    				}
				    			
 		/**
         * If the mover is in the field of view of the bot. 
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
            				Implementation of the static part of the GameBots2004 message MOV, used
            				to facade MOVMessage.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Synchronous message. Movers can be doors, elevators, or any
		other chunk of architecture that can move. They generally need
		to be either run into, or activated by shooting or pressing a
		button. We are working on ways to provide bots with more of the
		information they need to deal with movers appropriately.
	
         */
 	public class MoverStaticMessage 
	  					extends
  						MoverStatic
	    {
 	
		    			@Override
		    			public 
		    			MoverStaticMessage clone() {
		    				return this;
		    			}
		    			
 		/**
         * 
			A unique Id of this mover assigned by the game.
		 
         */
        public  UnrealId getId()
 	 {
				    					return Id;
				    				}
				    			
 		/**
         * 
			True if the mover needs to be shot to be activated.
		 
         */
        public  boolean isDamageTrig()
 	 {
				    					return DamageTrig;
				    				}
				    			
 		/**
         * String class of the mover. 
         */
        public  String getType()
 	 {
				    					return Type;
				    				}
				    			
 		/**
         * Does the mover move right now? 
         */
        public  boolean isIsMoving()
 	 {
				    					return IsMoving;
				    				}
				    			
 		/**
         * How long the mover moves, when it becomes triggered, before it stops. 
         */
        public  double getMoveTime()
 	 {
				    					return MoveTime;
				    				}
				    			
 		/**
         * How long the mover stands still when it reaches its destination position. After
      this time, the mover returns back to its initial position. 
         */
        public  double getOpenTime()
 	 {
				    					return OpenTime;
				    				}
				    			
 		/**
         * Base position of the mover. 
         */
        public  Location getBasePos()
 	 {
				    					return BasePos;
				    				}
				    			
 		/**
         * Base rotation of the mover. 
         */
        public  Location getBaseRot()
 	 {
				    					return BaseRot;
				    				}
				    			
 		/**
         * Delay before starting to open (or before lift starts to move). 
         */
        public  double getDelayTime()
 	 {
				    					return DelayTime;
				    				}
				    			
 		/**
         * Navigation point marking this mover. We can parse this Id to get information about type of the mover. Marker Id contains "lift" string if it is a lift, "door" string if it is a door, etc.  
         */
        public  UnrealId getNavPointMarker()
 	 {
				    					return NavPointMarker;
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
 				MoverStatic obj = (MoverStatic) other;

 				
 						if ( !(
 	 			AdvancedEquals.equalsOrNull(this.getId()
 	, obj.getId()
 	)
 	 		) )
						{
							System.out.println("!!!!!PROPERTY UPDATE ERROR!!!! on property Id on object class MoverStatic");
							return true;
						}
 					
 						if ( !(this.isDamageTrig()
 	
 	 			== obj.isDamageTrig()
 	) )
						{
							System.out.println("!!!!!PROPERTY UPDATE ERROR!!!! on property DamageTrig on object class MoverStatic");
							return true;
						}
 					
 						if ( !(
 	 			AdvancedEquals.equalsOrNull(this.getType()
 	, obj.getType()
 	)
 	 		) )
						{
							System.out.println("!!!!!PROPERTY UPDATE ERROR!!!! on property Type on object class MoverStatic");
							return true;
						}
 					
 						if ( !(this.isIsMoving()
 	
 	 			== obj.isIsMoving()
 	) )
						{
							System.out.println("!!!!!PROPERTY UPDATE ERROR!!!! on property IsMoving on object class MoverStatic");
							return true;
						}
 					
 						if ( !(this.getMoveTime()
 	
 	 			== obj.getMoveTime()
 	) )
						{
							System.out.println("!!!!!PROPERTY UPDATE ERROR!!!! on property MoveTime on object class MoverStatic");
							return true;
						}
 					
 						if ( !(this.getOpenTime()
 	
 	 			== obj.getOpenTime()
 	) )
						{
							System.out.println("!!!!!PROPERTY UPDATE ERROR!!!! on property OpenTime on object class MoverStatic");
							return true;
						}
 					
 						if ( !(
 	 			AdvancedEquals.equalsOrNull(this.getBasePos()
 	, obj.getBasePos()
 	)
 	 		) )
						{
							System.out.println("!!!!!PROPERTY UPDATE ERROR!!!! on property BasePos on object class MoverStatic");
							return true;
						}
 					
 						if ( !(
 	 			AdvancedEquals.equalsOrNull(this.getBaseRot()
 	, obj.getBaseRot()
 	)
 	 		) )
						{
							System.out.println("!!!!!PROPERTY UPDATE ERROR!!!! on property BaseRot on object class MoverStatic");
							return true;
						}
 					
 						if ( !(this.getDelayTime()
 	
 	 			== obj.getDelayTime()
 	) )
						{
							System.out.println("!!!!!PROPERTY UPDATE ERROR!!!! on property DelayTime on object class MoverStatic");
							return true;
						}
 					
 						if ( !(
 	 			AdvancedEquals.equalsOrNull(this.getNavPointMarker()
 	, obj.getNavPointMarker()
 	)
 	 		) )
						{
							System.out.println("!!!!!PROPERTY UPDATE ERROR!!!! on property NavPointMarker on object class MoverStatic");
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
		              		
		              			"DamageTrig = " + String.valueOf(isDamageTrig()
 	) + " | " + 
		              		
		              			"Type = " + String.valueOf(getType()
 	) + " | " + 
		              		
		              			"IsMoving = " + String.valueOf(isIsMoving()
 	) + " | " + 
		              		
		              			"MoveTime = " + String.valueOf(getMoveTime()
 	) + " | " + 
		              		
		              			"OpenTime = " + String.valueOf(getOpenTime()
 	) + " | " + 
		              		
		              			"BasePos = " + String.valueOf(getBasePos()
 	) + " | " + 
		              		
		              			"BaseRot = " + String.valueOf(getBaseRot()
 	) + " | " + 
		              		
		              			"DelayTime = " + String.valueOf(getDelayTime()
 	) + " | " + 
		              		
		              			"NavPointMarker = " + String.valueOf(getNavPointMarker()
 	) + " | " + 
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>Id</b> = " + String.valueOf(getId()
 	) + " <br/> " + 
		              		
		              			"<b>DamageTrig</b> = " + String.valueOf(isDamageTrig()
 	) + " <br/> " + 
		              		
		              			"<b>Type</b> = " + String.valueOf(getType()
 	) + " <br/> " + 
		              		
		              			"<b>IsMoving</b> = " + String.valueOf(isIsMoving()
 	) + " <br/> " + 
		              		
		              			"<b>MoveTime</b> = " + String.valueOf(getMoveTime()
 	) + " <br/> " + 
		              		
		              			"<b>OpenTime</b> = " + String.valueOf(getOpenTime()
 	) + " <br/> " + 
		              		
		              			"<b>BasePos</b> = " + String.valueOf(getBasePos()
 	) + " <br/> " + 
		              		
		              			"<b>BaseRot</b> = " + String.valueOf(getBaseRot()
 	) + " <br/> " + 
		              		
		              			"<b>DelayTime</b> = " + String.valueOf(getDelayTime()
 	) + " <br/> " + 
		              		
		              			"<b>NavPointMarker</b> = " + String.valueOf(getNavPointMarker()
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
            				Implementation of the shared part of the GameBots2004 message MOV, used
            				to facade MOVMessage.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Synchronous message. Movers can be doors, elevators, or any
		other chunk of architecture that can move. They generally need
		to be either run into, or activated by shooting or pressing a
		button. We are working on ways to provide bots with more of the
		information they need to deal with movers appropriately.
	
         */
 	public class MoverSharedMessage 
	  					extends
  						MoverShared
	    {
 	
    	
    	
		public MoverSharedMessage()
		{
			
				propertyMap.put(myLocation.getPropertyId(), myLocation);
			
				propertyMap.put(myVelocity.getPropertyId(), myVelocity);
			
				propertyMap.put(myState.getPropertyId(), myState);
			
		}		
    
		    			@Override
		    			public 
		    			MoverSharedMessage clone() {
		    				return this;
		    			}
		    			
		
		
		
		protected HashMap<PropertyId, ISharedProperty> propertyMap = new HashMap<PropertyId, ISharedProperty>(
			3
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
			A unique Id of this mover assigned by the game.
		 
         */
        public  UnrealId getId()
 	 {
  			return Id;
  		}
  		
    	
	    /**
         * Location of the mover. 
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
						Mover.class
					);
					
 		/**
         * Location of the mover. 
         */
        public  Location getLocation()
 	 {
			  			return myLocation.getValue();
			  		}
				
    	
	    /**
         * Velocity vector. 
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
						Mover.class
					);
					
 		/**
         * Velocity vector. 
         */
        public  Velocity getVelocity()
 	 {
			  			return myVelocity.getValue();
			  		}
				
    	
	    /**
         * Name of the state Mover is currently in. Can be used to determine the type of the mover.  
         */
        protected
         StringProperty 
        myState
					= new
					StringProperty
					(
						getId(), 
						"State", 
						State, 
						Mover.class
					);
					
 		/**
         * Name of the state Mover is currently in. Can be used to determine the type of the mover.  
         */
        public  String getState()
 	 {
			  			return myState.getValue();
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
		              		
		              			"State = " + String.valueOf(getState()
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
		              		
		              			"<b>State</b> = " + String.valueOf(getState()
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
			if (!( object instanceof MoverMessage) ) {
				throw new PogamutException("Can't update different class than MoverMessage, got class " + object.getClass().getSimpleName() + "!", this);		
			}
			MoverMessage toUpdate = (MoverMessage)object;
			
			boolean updated = false;
			
			// UPDATING LOCAL PROPERTIES
			
				if (toUpdate.Visible != isVisible()
 	) {
				    toUpdate.Visible=isVisible()
 	;
					updated = true;
				}
			
         	
         	// UPDATING SHARED PROPERTIES
         	
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
			
				if (!SafeEquals.equals(toUpdate.State, getState()
 	)) {
					toUpdate.State=getState()
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
			return new MoverLocalImpl.MoverLocalUpdate
    (this.getLocal(), SimTime);
		}

		@Override
		public ISharedWorldObjectUpdatedEvent getSharedEvent() {
			return new MoverSharedImpl.MoverSharedUpdate
    (this.getShared(), SimTime, this.getTeamId());
		}

		@Override
		public IStaticWorldObjectUpdatedEvent getStaticEvent() {
			return new MoverStaticImpl.MoverStaticUpdate
    (this.getStatic(), SimTime);
		}
 	
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
		              			"Id = " + String.valueOf(getId()
 	) + " | " + 
		              		
		              			"Location = " + String.valueOf(getLocation()
 	) + " | " + 
		              		
		              			"Visible = " + String.valueOf(isVisible()
 	) + " | " + 
		              		
		              			"DamageTrig = " + String.valueOf(isDamageTrig()
 	) + " | " + 
		              		
		              			"Type = " + String.valueOf(getType()
 	) + " | " + 
		              		
		              			"IsMoving = " + String.valueOf(isIsMoving()
 	) + " | " + 
		              		
		              			"Velocity = " + String.valueOf(getVelocity()
 	) + " | " + 
		              		
		              			"MoveTime = " + String.valueOf(getMoveTime()
 	) + " | " + 
		              		
		              			"OpenTime = " + String.valueOf(getOpenTime()
 	) + " | " + 
		              		
		              			"BasePos = " + String.valueOf(getBasePos()
 	) + " | " + 
		              		
		              			"BaseRot = " + String.valueOf(getBaseRot()
 	) + " | " + 
		              		
		              			"DelayTime = " + String.valueOf(getDelayTime()
 	) + " | " + 
		              		
		              			"State = " + String.valueOf(getState()
 	) + " | " + 
		              		
		              			"NavPointMarker = " + String.valueOf(getNavPointMarker()
 	) + " | " + 
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>Id</b> = " + String.valueOf(getId()
 	) + " <br/> " + 
		              		
		              			"<b>Location</b> = " + String.valueOf(getLocation()
 	) + " <br/> " + 
		              		
		              			"<b>Visible</b> = " + String.valueOf(isVisible()
 	) + " <br/> " + 
		              		
		              			"<b>DamageTrig</b> = " + String.valueOf(isDamageTrig()
 	) + " <br/> " + 
		              		
		              			"<b>Type</b> = " + String.valueOf(getType()
 	) + " <br/> " + 
		              		
		              			"<b>IsMoving</b> = " + String.valueOf(isIsMoving()
 	) + " <br/> " + 
		              		
		              			"<b>Velocity</b> = " + String.valueOf(getVelocity()
 	) + " <br/> " + 
		              		
		              			"<b>MoveTime</b> = " + String.valueOf(getMoveTime()
 	) + " <br/> " + 
		              		
		              			"<b>OpenTime</b> = " + String.valueOf(getOpenTime()
 	) + " <br/> " + 
		              		
		              			"<b>BasePos</b> = " + String.valueOf(getBasePos()
 	) + " <br/> " + 
		              		
		              			"<b>BaseRot</b> = " + String.valueOf(getBaseRot()
 	) + " <br/> " + 
		              		
		              			"<b>DelayTime</b> = " + String.valueOf(getDelayTime()
 	) + " <br/> " + 
		              		
		              			"<b>State</b> = " + String.valueOf(getState()
 	) + " <br/> " + 
		              		
		              			"<b>NavPointMarker</b> = " + String.valueOf(getNavPointMarker()
 	) + " <br/> " + 
		              		
				"<br/>]";     
		}
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=base+classtype[@name=message]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=base+classtype[@name=message]) ---        	            	
 	
		}
 	