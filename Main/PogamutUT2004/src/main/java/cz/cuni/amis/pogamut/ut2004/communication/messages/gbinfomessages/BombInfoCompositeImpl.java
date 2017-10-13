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
            		Composite implementation of the BOM abstract message. It wraps Local/Shared/Static parts in single object
            		allowing to presenting a nice facade for users.
            	
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Synchronous message. BombInfo contains all info about the bomb
		in the BotBombingRun game mode. Is not sent in other game types.
	
         */
 	public class BombInfoCompositeImpl 
  				extends BombInfo
	    {
 	
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public BombInfoCompositeImpl()
		{
		}
	
		
		/**
		 * Composite-impl constructor. It assembles the message from its three fragments - local/shared/static.
		 *
		 * @param partLocal local-part of the message
		 * @param partShared shared-part of the message
		 * @param partStatic static-part of the message
		 */
		public BombInfoCompositeImpl(
			BombInfoLocalImpl partLocal,
			BombInfoSharedImpl partShared,
			BombInfoStaticImpl partStatic
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
		public BombInfoCompositeImpl(BombInfoCompositeImpl original) {
			this.partLocal = partLocal;
			this.partShared = partShared;
			this.partStatic = partStatic;
		}
    
   				
   				@Override
   				public void setSimTime(long SimTime) {
					super.setSimTime(SimTime);
				}
   			
    			
    			protected 
    			BombInfoStaticImpl
    			partStatic;
    			
    			@Override
				public BombInfoStatic getStatic() {
					return partStatic;
				}
    			
    			protected
    			BombInfoLocalImpl
    			partLocal;
    	
    			@Override
				public BombInfoLocal getLocal() {
					return partLocal;
				}
			
    			BombInfoSharedImpl
    			partShared;
    			
				@Override
				public BombInfoShared getShared() {
					return partShared;
				}
			
				
  				
  					@Override
    				
 		/**
         * 
			An unique Id for this bomb, assigned by the game.
		 
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
   		Current velocity of the bomb. TODO not sure if this actually does smthing
   	 
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
			An absolute location of the bomb (Sent if we can actually
			see the flag).
		 
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
			Id of player/bot holding the bomb. (Sent if we can actually
			see the bomb and the bomb is being carried, or if the bomb
			is being carried by us).
		 
         */
        public  UnrealId getHolder()
 	 {
    					return 
    						
    								partShared.
    							getHolder()
 	;
    				}
    			
  					@Override
    				
 		/**
         * 
			The team of the current holder (if any).
		 
         */
        public  Integer getHolderTeam()
 	 {
    					return 
    						
    								partShared.
    							getHolderTeam()
 	;
    				}
    			
  					@Override
    				
 		/**
         * True if the bot can see the bomb. 
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
			Represents the state the bomb is in. Can be "Held",
			"Dropped" or "Home".
		 
         */
        public  String getState()
 	 {
    					return 
    						
    								partShared.
    							getState()
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
 	