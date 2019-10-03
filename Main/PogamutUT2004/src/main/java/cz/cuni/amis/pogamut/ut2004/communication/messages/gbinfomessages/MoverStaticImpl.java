package cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages;
 		
 		// --- IMPORTS FROM /messages/settings/javasettings/javaimport BEGIN
			import java.util.*;import javax.vecmath.*;import cz.cuni.amis.pogamut.base.communication.messages.*;import cz.cuni.amis.pogamut.base.communication.worldview.*;import cz.cuni.amis.pogamut.base.communication.worldview.event.*;import cz.cuni.amis.pogamut.base.communication.worldview.object.*;import cz.cuni.amis.pogamut.multi.communication.worldview.object.*;import cz.cuni.amis.pogamut.base.communication.translator.event.*;import cz.cuni.amis.pogamut.multi.communication.translator.event.*;import cz.cuni.amis.pogamut.base3d.worldview.object.*;import cz.cuni.amis.pogamut.base3d.worldview.object.event.*;import cz.cuni.amis.pogamut.ut2004.communication.messages.*;import cz.cuni.amis.pogamut.ut2004.communication.worldview.objects.*;import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.objects.*;import cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor.*;import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType.Category;import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;import cz.cuni.amis.utils.exception.*;import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdateResult.Result;import cz.cuni.amis.utils.SafeEquals;import cz.cuni.amis.pogamut.base.agent.*;import cz.cuni.amis.pogamut.multi.agent.*;import cz.cuni.amis.pogamut.multi.communication.worldview.property.*;import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.property.*;import cz.cuni.amis.utils.token.*;import cz.cuni.amis.utils.*;
		// --- IMPORTS FROM /messages/settings/javasettings/javaimport END
		
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name='all'] BEGIN
				
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name='all'] END
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name=static]+classtype[@name=impl] BEGIN
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name=static]+classtype[@name=impl] END
    
 		/**
         *  
            				Implementation of the static part of the GameBots2004 message MOV.  
            			
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
 	public class MoverStaticImpl 
  						extends
  						MoverStatic
	    {
 	
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public MoverStaticImpl()
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
		 *   (static part)
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
		 * 
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
		 * 
		 *   
		 *     @param NavPointMarker Navigation point marking this mover. We can parse this Id to get information about type of the mover. Marker Id contains "lift" string if it is a lift, "door" string if it is a door, etc. 
		 *   
		 * 
		 */
		public MoverStaticImpl(
			UnrealId Id,  boolean DamageTrig,  String Type,  boolean IsMoving,  double MoveTime,  double OpenTime,  Location BasePos,  Location BaseRot,  double DelayTime,  UnrealId NavPointMarker
		) {
			
					this.Id = Id;
				
					this.DamageTrig = DamageTrig;
				
					this.Type = Type;
				
					this.IsMoving = IsMoving;
				
					this.MoveTime = MoveTime;
				
					this.OpenTime = OpenTime;
				
					this.BasePos = BasePos;
				
					this.BaseRot = BaseRot;
				
					this.DelayTime = DelayTime;
				
					this.NavPointMarker = NavPointMarker;
				
		}
    
    	/**
		 * Cloning constructor from the full message.
		 *
		 * @param original
		 */
		public MoverStaticImpl(Mover original) {		
			
					this.Id = original.getId()
 	;
				
					this.DamageTrig = original.isDamageTrig()
 	;
				
					this.Type = original.getType()
 	;
				
					this.IsMoving = original.isIsMoving()
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
				
					this.NavPointMarker = original.getNavPointMarker()
 	;
				
			this.SimTime = original.getSimTime();			
		}
		
		/**
		 * Cloning constructor from the full message.
		 *
		 * @param original
		 */
		public MoverStaticImpl(MoverStaticImpl original) {		
			
					this.Id = original.getId()
 	;
				
					this.DamageTrig = original.isDamageTrig()
 	;
				
					this.Type = original.getType()
 	;
				
					this.IsMoving = original.isIsMoving()
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
				
					this.NavPointMarker = original.getNavPointMarker()
 	;
				
			this.SimTime = original.getSimTime();
		}
		
			/**
			 * Cloning constructor from the message part.
			 *
			 * @param original
			 */
			public MoverStaticImpl(MoverStatic original) {
				
						this.Id = original.getId()
 	;
					
						this.DamageTrig = original.isDamageTrig()
 	;
					
						this.Type = original.getType()
 	;
					
						this.IsMoving = original.isIsMoving()
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
					
						this.NavPointMarker = original.getNavPointMarker()
 	;
					
			}
		
   				
   				@Override
   				public void setSimTime(long SimTime) {
					super.setSimTime(SimTime);
				}
   			
	    				@Override
	    				public 
	    				MoverStaticImpl clone() {
	    					return new 
	    					MoverStaticImpl(this);
	    				}
	    				
	    				
    	
	    /**
         * 
			A unique Id of this mover assigned by the game.
		 
         */
        protected
         UnrealId Id =
       	null;
	
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
        protected
         boolean DamageTrig =
       	false;
	
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
         * Does the mover move right now? 
         */
        public  boolean isIsMoving()
 	 {
				    					return IsMoving;
				    				}
				    			
    	
	    /**
         * How long the mover moves, when it becomes triggered, before it stops. 
         */
        protected
         double MoveTime =
       	0;
	
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
         * Delay before starting to open (or before lift starts to move). 
         */
        public  double getDelayTime()
 	 {
				    					return DelayTime;
				    				}
				    			
    	
	    /**
         * Navigation point marking this mover. We can parse this Id to get information about type of the mover. Marker Id contains "lift" string if it is a lift, "door" string if it is a door, etc.  
         */
        protected
         UnrealId NavPointMarker =
       	null;
	
 		/**
         * Navigation point marking this mover. We can parse this Id to get information about type of the mover. Marker Id contains "lift" string if it is a lift, "door" string if it is a door, etc.  
         */
        public  UnrealId getNavPointMarker()
 	 {
				    					return NavPointMarker;
				    				}
				    			
    	
    	
    	public static class MoverStaticUpdate
     implements IStaticWorldObjectUpdatedEvent
		{
			
			private MoverStatic data;
			private long time;
			
			public MoverStaticUpdate
    (MoverStatic source, long time)
			{
				this.data = source;
				this.time = time;
			}
			
			/**
			 * Simulation time in MILLI SECONDS !!!
			 */
			@Override
			public long getSimTime() {
				return time;
			}
	
			@Override
			public WorldObjectId getId() {
				return data.getId();
			}
			
			@Override
			public IWorldObjectUpdateResult<IStaticWorldObject> update(
					IStaticWorldObject object) {
				if ( object == null)
				{
					data = new MoverStaticImpl(data);
					return new IWorldObjectUpdateResult.WorldObjectUpdateResult<IStaticWorldObject>(IWorldObjectUpdateResult.Result.CREATED, data);
				}
				//since static objects can't be updated, we'll just check that the object stays the same
				if ( object instanceof MoverStaticImpl)
				{
					MoverStaticImpl orig = (MoverStaticImpl)object;
					//since these errors usually mean error in gamebots, we will just print an error message
					if ( data.isDifferentFrom(orig) )
					{
						//data.isDifferentFrom(orig);
						//throw new PogamutException("Trying to modify static object " + this.data.getId().toString() , this);
						System.out.println("!!!!!ERROR!!!!!! in static object modification. Object class : MoverStaticImpl to see which property was different see !!!!PROPERTY UPDATE ERROR!!!!");
					}
					return new IWorldObjectUpdateResult.WorldObjectUpdateResult<IStaticWorldObject>(IWorldObjectUpdateResult.Result.SAME, data);
				}				
				throw new PogamutException("Unexpected object type for update, MoverStaticImpl expected not class " + object.getClass().getSimpleName() + ".", this);
			}
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
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=static+classtype[@name=impl]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=static+classtype[@name=impl]) ---        	            	
 	
		}
 	