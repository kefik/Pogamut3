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
            		Composite implementation of the MOV abstract message. It wraps Local/Shared/Static parts in single object
            		allowing to presenting a nice facade for users.
            	
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
 	public class MoverCompositeImpl 
  				extends Mover
	    {
 	
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public MoverCompositeImpl()
		{
		}
	
		
		/**
		 * Composite-impl constructor. It assembles the message from its three fragments - local/shared/static.
		 *
		 * @param partLocal local-part of the message
		 * @param partShared shared-part of the message
		 * @param partStatic static-part of the message
		 */
		public MoverCompositeImpl(
			MoverLocalImpl partLocal,
			MoverSharedImpl partShared,
			MoverStaticImpl partStatic
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
		public MoverCompositeImpl(MoverCompositeImpl original) {
			this.partLocal = partLocal;
			this.partShared = partShared;
			this.partStatic = partStatic;
		}
    
   				
   				@Override
   				public void setSimTime(long SimTime) {
					super.setSimTime(SimTime);
				}
   			
    			
    			protected 
    			MoverStaticImpl
    			partStatic;
    			
    			@Override
				public MoverStatic getStatic() {
					return partStatic;
				}
    			
    			protected
    			MoverLocalImpl
    			partLocal;
    	
    			@Override
				public MoverLocal getLocal() {
					return partLocal;
				}
			
    			MoverSharedImpl
    			partShared;
    			
				@Override
				public MoverShared getShared() {
					return partShared;
				}
			
				
  				
  					@Override
    				
 		/**
         * 
			A unique Id of this mover assigned by the game.
		 
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
         * Location of the mover. 
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
         * If the mover is in the field of view of the bot. 
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
			True if the mover needs to be shot to be activated.
		 
         */
        public  boolean isDamageTrig()
 	 {
    					return 
    						
    								partStatic.
    							isDamageTrig()
 	;
    				}
    			
  					@Override
    				
 		/**
         * String class of the mover. 
         */
        public  String getType()
 	 {
    					return 
    						
    								partStatic.
    							getType()
 	;
    				}
    			
  					@Override
    				
 		/**
         * Does the mover move right now? 
         */
        public  boolean isIsMoving()
 	 {
    					return 
    						
    								partStatic.
    							isIsMoving()
 	;
    				}
    			
  					@Override
    				
 		/**
         * Velocity vector. 
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
         * How long the mover moves, when it becomes triggered, before it stops. 
         */
        public  double getMoveTime()
 	 {
    					return 
    						
    								partStatic.
    							getMoveTime()
 	;
    				}
    			
  					@Override
    				
 		/**
         * How long the mover stands still when it reaches its destination position. After
      this time, the mover returns back to its initial position. 
         */
        public  double getOpenTime()
 	 {
    					return 
    						
    								partStatic.
    							getOpenTime()
 	;
    				}
    			
  					@Override
    				
 		/**
         * Base position of the mover. 
         */
        public  Location getBasePos()
 	 {
    					return 
    						
    								partStatic.
    							getBasePos()
 	;
    				}
    			
  					@Override
    				
 		/**
         * Base rotation of the mover. 
         */
        public  Location getBaseRot()
 	 {
    					return 
    						
    								partStatic.
    							getBaseRot()
 	;
    				}
    			
  					@Override
    				
 		/**
         * Delay before starting to open (or before lift starts to move). 
         */
        public  double getDelayTime()
 	 {
    					return 
    						
    								partStatic.
    							getDelayTime()
 	;
    				}
    			
  					@Override
    				
 		/**
         * Name of the state Mover is currently in. Can be used to determine the type of the mover.  
         */
        public  String getState()
 	 {
    					return 
    						
    								partShared.
    							getState()
 	;
    				}
    			
  					@Override
    				
 		/**
         * Navigation point marking this mover. We can parse this Id to get information about type of the mover. Marker Id contains "lift" string if it is a lift, "door" string if it is a door, etc.  
         */
        public  UnrealId getNavPointMarker()
 	 {
    					return 
    						
    								partStatic.
    							getNavPointMarker()
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
 	