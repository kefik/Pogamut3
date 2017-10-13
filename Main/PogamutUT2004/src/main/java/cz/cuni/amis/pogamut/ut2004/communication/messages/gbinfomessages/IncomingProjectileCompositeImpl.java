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
            		Composite implementation of the PRJ abstract message. It wraps Local/Shared/Static parts in single object
            		allowing to presenting a nice facade for users.
            	
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Both asynchronous and synchronous message. Incoming projectile that we can see.
	
         */
 	public class IncomingProjectileCompositeImpl 
  				extends IncomingProjectile
	    {
 	
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public IncomingProjectileCompositeImpl()
		{
		}
	
		
		/**
		 * Composite-impl constructor. It assembles the message from its three fragments - local/shared/static.
		 *
		 * @param partLocal local-part of the message
		 * @param partShared shared-part of the message
		 * @param partStatic static-part of the message
		 */
		public IncomingProjectileCompositeImpl(
			IncomingProjectileLocalImpl partLocal,
			IncomingProjectileSharedImpl partShared,
			IncomingProjectileStaticImpl partStatic
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
		public IncomingProjectileCompositeImpl(IncomingProjectileCompositeImpl original) {
			this.partLocal = partLocal;
			this.partShared = partShared;
			this.partStatic = partStatic;
		}
    
   				
   				@Override
   				public void setSimTime(long SimTime) {
					super.setSimTime(SimTime);
				}
   			
    			
    			protected 
    			IncomingProjectileStaticImpl
    			partStatic;
    			
    			@Override
				public IncomingProjectileStatic getStatic() {
					return partStatic;
				}
    			
    			protected
    			IncomingProjectileLocalImpl
    			partLocal;
    	
    			@Override
				public IncomingProjectileLocal getLocal() {
					return partLocal;
				}
			
    			IncomingProjectileSharedImpl
    			partShared;
    			
				@Override
				public IncomingProjectileShared getShared() {
					return partShared;
				}
			
				
  				
  					@Override
    				
 		/**
         * Unique Id of the projectile. 
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
         * Estimated time till impact. 
         */
        public  double getImpactTime()
 	 {
    					return 
    						
    								partLocal.
    							getImpactTime()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			Which direction projectile is heading to -> orientation
			vector.
		 
         */
        public  Vector3d getDirection()
 	 {
    					return 
    						
    								partShared.
    							getDirection()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			Current location of the projectile.
		 
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
			Current velocity vector of the projectile.
		 
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
			Current speed of the projectile.
		 
         */
        public  double getSpeed()
 	 {
    					return 
    						
    								partShared.
    							getSpeed()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			Possition of the origin, when combined with direction can
			define the line of fire.
		 
         */
        public  Location getOrigin()
 	 {
    					return 
    						
    								partShared.
    							getOrigin()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			If the projectile has splash damage, how big it is – in ut
			units.
		 
         */
        public  double getDamageRadius()
 	 {
    					return 
    						
    								partShared.
    							getDamageRadius()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			The class of the projectile (so you know what is flying
			against you).
		 
         */
        public  String getType()
 	 {
    					return 
    						
    								partShared.
    							getType()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			The class of the projectile (so you know what is flying
			against you).
		 
         */
        public  boolean isVisible()
 	 {
    					return 
    						
    								partLocal.
    							isVisible()
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
 	