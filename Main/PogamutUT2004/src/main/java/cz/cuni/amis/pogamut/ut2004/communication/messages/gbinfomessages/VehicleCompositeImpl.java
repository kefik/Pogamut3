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
            		Composite implementation of the VEH abstract message. It wraps Local/Shared/Static parts in single object
            		allowing to presenting a nice facade for users.
            	
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Synchronous message. Holds vehicles we see. Id for vehicles doesn't work, so 
		this message is without Id. We can join vehicles by ENTER command if we are 
		close enough - ussually 100 ut units or less. Note: Vehicle support is 
		in aplha testing right now.
	
         */
 	public class VehicleCompositeImpl 
  				extends Vehicle
	    {
 	
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public VehicleCompositeImpl()
		{
		}
	
		
		/**
		 * Composite-impl constructor. It assembles the message from its three fragments - local/shared/static.
		 *
		 * @param partLocal local-part of the message
		 * @param partShared shared-part of the message
		 * @param partStatic static-part of the message
		 */
		public VehicleCompositeImpl(
			VehicleLocalImpl partLocal,
			VehicleSharedImpl partShared,
			VehicleStaticImpl partStatic
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
		public VehicleCompositeImpl(VehicleCompositeImpl original) {
			this.partLocal = partLocal;
			this.partShared = partShared;
			this.partStatic = partStatic;
		}
    
   				
   				@Override
   				public void setSimTime(long SimTime) {
					super.setSimTime(SimTime);
				}
   			
    			
    			protected 
    			VehicleStaticImpl
    			partStatic;
    			
    			@Override
				public VehicleStatic getStatic() {
					return partStatic;
				}
    			
    			protected
    			VehicleLocalImpl
    			partLocal;
    	
    			@Override
				public VehicleLocal getLocal() {
					return partLocal;
				}
			
    			VehicleSharedImpl
    			partShared;
    			
				@Override
				public VehicleShared getShared() {
					return partShared;
				}
			
				
  				
  					@Override
    				
 		/**
         * Unique Id of the vehicle or vehicle part. 
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
			Which direction the vehicle is facing in absolute terms.
		 
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
			An absolute location of the vehicle within the map.
		 
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
			Absolute velocity of the vehicle as a vector of movement per one
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
            If the vehicle is in the field of view of the bot.
         
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
			What team the vehicle is on. 255 is no team. 0-3 are red,
			blue, green, gold in that order.
		 
         */
        public  Integer getTeam()
 	 {
    					return 
    						
    								partShared.
    							getTeam()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			How much health the vehicle has left. Ranges from 0 to x, depending on the vehicle type.
		 
         */
        public  Integer getHealth()
 	 {
    					return 
    						
    								partShared.
    							getHealth()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			How much the vehicle has left. Note: This may be 0 all the time. Maybe the vehicles are not supporting armor.
		 
         */
        public  Integer getArmor()
 	 {
    					return 
    						
    								partShared.
    							getArmor()
 	;
    				}
    			
  					@Override
    				
 		/**
         * Unique Id of the driver - if any. 
         */
        public  UnrealId getDriver()
 	 {
    					return 
    						
    								partShared.
    							getDriver()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
            If the vehicle is locked just for its current team.
         
         */
        public  boolean isTeamLocked()
 	 {
    					return 
    						
    								partShared.
    							isTeamLocked()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			Class of the vehicle. If it is a car, turret etc.
		 
         */
        public  String getType()
 	 {
    					return 
    						
    								partStatic.
    							getType()
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
 	