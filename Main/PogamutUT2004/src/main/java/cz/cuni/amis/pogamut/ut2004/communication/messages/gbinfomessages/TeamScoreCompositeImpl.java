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
            		Composite implementation of the TES abstract message. It wraps Local/Shared/Static parts in single object
            		allowing to presenting a nice facade for users.
            	
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Synchronous message. Contains the info about team score.
	
         */
 	public class TeamScoreCompositeImpl 
  				extends TeamScore
	    {
 	
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public TeamScoreCompositeImpl()
		{
		}
	
		
		/**
		 * Composite-impl constructor. It assembles the message from its three fragments - local/shared/static.
		 *
		 * @param partLocal local-part of the message
		 * @param partShared shared-part of the message
		 * @param partStatic static-part of the message
		 */
		public TeamScoreCompositeImpl(
			TeamScoreLocalImpl partLocal,
			TeamScoreSharedImpl partShared,
			TeamScoreStaticImpl partStatic
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
		public TeamScoreCompositeImpl(TeamScoreCompositeImpl original) {
			this.partLocal = partLocal;
			this.partShared = partShared;
			this.partStatic = partStatic;
		}
    
   				
   				@Override
   				public void setSimTime(long SimTime) {
					super.setSimTime(SimTime);
				}
   			
    			
    			protected 
    			TeamScoreStaticImpl
    			partStatic;
    			
    			@Override
				public TeamScoreStatic getStatic() {
					return partStatic;
				}
    			
    			protected
    			TeamScoreLocalImpl
    			partLocal;
    	
    			@Override
				public TeamScoreLocal getLocal() {
					return partLocal;
				}
			
    			TeamScoreSharedImpl
    			partShared;
    			
				@Override
				public TeamScoreShared getShared() {
					return partShared;
				}
			
				
  				
  					@Override
    				
 		/**
         * Message identifier. 
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
         * Team identifier. 
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
			The score of the team (can be some special measurement that
			differs from game type to game type - number of stolen flags
			in CTF game, number of team frags in TeamGame, etc.)
		 
         */
        public  Integer getScore()
 	 {
    					return 
    						
    								partShared.
    							getScore()
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
 	