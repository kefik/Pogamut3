package cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages;
 		
 		// --- IMPORTS FROM /messages/settings/javasettings/javaimport BEGIN
			import java.util.*;import javax.vecmath.*;import cz.cuni.amis.pogamut.base.communication.messages.*;import cz.cuni.amis.pogamut.base.communication.worldview.*;import cz.cuni.amis.pogamut.base.communication.worldview.event.*;import cz.cuni.amis.pogamut.base.communication.worldview.object.*;import cz.cuni.amis.pogamut.multi.communication.worldview.object.*;import cz.cuni.amis.pogamut.base.communication.translator.event.*;import cz.cuni.amis.pogamut.multi.communication.translator.event.*;import cz.cuni.amis.pogamut.base3d.worldview.object.*;import cz.cuni.amis.pogamut.base3d.worldview.object.event.*;import cz.cuni.amis.pogamut.ut2004.communication.messages.*;import cz.cuni.amis.pogamut.ut2004.communication.worldview.objects.*;import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.objects.*;import cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor.*;import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType.Category;import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;import cz.cuni.amis.utils.exception.*;import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdateResult.Result;import cz.cuni.amis.utils.SafeEquals;import cz.cuni.amis.pogamut.base.agent.*;import cz.cuni.amis.pogamut.multi.agent.*;import cz.cuni.amis.pogamut.multi.communication.worldview.property.*;import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.property.*;import cz.cuni.amis.utils.token.*;import cz.cuni.amis.utils.*;
		// --- IMPORTS FROM /messages/settings/javasettings/javaimport END
		
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name='all'] BEGIN
				
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name='all'] END
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name=static]+classtype[@name=abstract] BEGIN
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name=static]+classtype[@name=abstract] END
    
 		/**
         *  
            				Abstract definition of the static part of the GameBots2004 message MOV.  
            			
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
 	public abstract class MoverStatic 
  						extends InfoMessage
  						implements IStaticWorldObject
  						
	    {
 	
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public MoverStatic()
		{
		}
		
				// abstract definition of the static-part of the message, no more constructors is needed
			
	   		
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
	   	
	    			
	    				@Override
		    			public abstract 
		    			MoverStatic clone();
		    			
						@Override
						public Class getCompositeClass() {
							return Mover.class;
						}
	
						
		    			
 		/**
         * 
			A unique Id of this mover assigned by the game.
		 
         */
        public abstract UnrealId getId()
 	;
		    			
 		/**
         * 
			True if the mover needs to be shot to be activated.
		 
         */
        public abstract boolean isDamageTrig()
 	;
		    			
 		/**
         * String class of the mover. 
         */
        public abstract String getType()
 	;
		    			
 		/**
         * Does the mover move right now? 
         */
        public abstract boolean isIsMoving()
 	;
		    			
 		/**
         * How long the mover moves, when it becomes triggered, before it stops. 
         */
        public abstract double getMoveTime()
 	;
		    			
 		/**
         * How long the mover stands still when it reaches its destination position. After
      this time, the mover returns back to its initial position. 
         */
        public abstract double getOpenTime()
 	;
		    			
 		/**
         * Base position of the mover. 
         */
        public abstract Location getBasePos()
 	;
		    			
 		/**
         * Base rotation of the mover. 
         */
        public abstract Location getBaseRot()
 	;
		    			
 		/**
         * Delay before starting to open (or before lift starts to move). 
         */
        public abstract double getDelayTime()
 	;
		    			
 		/**
         * Navigation point marking this mover. We can parse this Id to get information about type of the mover. Marker Id contains "lift" string if it is a lift, "door" string if it is a door, etc.  
         */
        public abstract UnrealId getNavPointMarker()
 	;
		    			
 		
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
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=static+classtype[@name=abstract]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=static+classtype[@name=abstract]) ---        	            	
 	
		}
 	