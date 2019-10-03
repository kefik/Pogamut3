
	 	/**
         IMPORTANT !!!

         DO NOT EDIT THIS FILE. IT IS GENERATED FROM approriate xml file in xmlresources/gbcommands BY
         THE JavaClassesGenerator.xslt. MODIFY THAT FILE INSTEAD OF THIS ONE.
         
         Use Ant task process-gb-messages after that to generate .java files again.
         
         IMPORTANT END !!!
        */
 	package cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands;import java.util.*;import javax.vecmath.*;import cz.cuni.amis.pogamut.base.communication.messages.*;import cz.cuni.amis.pogamut.base.communication.worldview.*;import cz.cuni.amis.pogamut.base.communication.worldview.event.*;import cz.cuni.amis.pogamut.base.communication.worldview.object.*;import cz.cuni.amis.pogamut.multi.communication.worldview.object.*;import cz.cuni.amis.pogamut.base.communication.translator.event.*;import cz.cuni.amis.pogamut.multi.communication.translator.event.*;import cz.cuni.amis.pogamut.base3d.worldview.object.*;import cz.cuni.amis.pogamut.base3d.worldview.object.event.*;import cz.cuni.amis.pogamut.ut2004.communication.messages.*;import cz.cuni.amis.pogamut.ut2004.communication.worldview.objects.*;import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.objects.*;import cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor.*;import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType.Category;import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;import cz.cuni.amis.utils.exception.*;import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdateResult.Result;import cz.cuni.amis.utils.SafeEquals;import cz.cuni.amis.pogamut.base.agent.*;import cz.cuni.amis.pogamut.multi.agent.*;import cz.cuni.amis.pogamut.multi.communication.worldview.property.*;import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.property.*;import cz.cuni.amis.utils.token.*;import cz.cuni.amis.utils.*;
 		/**
 		 * Representation of the GameBots2004 command SETWALK.
 		 *
 		 * 
		Set whether you are walking or running (default is run).
	
         */
 	public class SetWalk 
		extends CommandMessage
	        {
	        	
		        
    	/** Example how the message looks like - used during parser tests. */
    	public static final String PROTOTYPE =
    		" {Walk False}  {WalkAnim text}  {RunAnim text} ";
    
		/**
		 * Creates new instance of command SetWalk.
		 * 
		Set whether you are walking or running (default is run).
	
		 * Corresponding GameBots message for this command is
		 * SETWALK.
		 *
		 * 
		 *    @param Walk 
			True or false to enable/disable bot walking.
		
		 *    @param WalkAnim 
			Change name of walking animation. Supported in UE2.
		
		 *    @param RunAnim 
			Change name of running animation. Supported in UE2
		
		 */
		public SetWalk(
			Boolean Walk,  String WalkAnim,  String RunAnim
		) {
			
				this.Walk = Walk;
            
				this.WalkAnim = WalkAnim;
            
				this.RunAnim = RunAnim;
            
		}

		
			/**
			 * Creates new instance of command SetWalk.
			 * 
		Set whether you are walking or running (default is run).
	
			 * Corresponding GameBots message for this command is
			 * SETWALK.
			 * <p></p>
			 * WARNING: this is empty-command constructor, you have to use setters to fill it up with data that should be sent to GameBots2004!
		     */
		    public SetWalk() {
		    }
			
		
		/**
		 * Cloning constructor.
		 *
		 * @param original
		 */
		public SetWalk(SetWalk original) {
		   
		        this.Walk = original.Walk;
		   
		        this.WalkAnim = original.WalkAnim;
		   
		        this.RunAnim = original.RunAnim;
		   
		}
    
	        /**
	        
			True or false to enable/disable bot walking.
		 
	        */
	        protected
	         Boolean Walk =
	       	
	        		null
	        	;
	
	        
	        
 		/**
         * 
			True or false to enable/disable bot walking.
		 
         */
        public Boolean isWalk()
 	
	        {
	            return
	        	 Walk;
	        }
	        
	        
	        
 		
 		/**
         * 
			True or false to enable/disable bot walking.
		 
         */
        public SetWalk 
        setWalk(Boolean Walk)
 	
			{
				this.Walk = Walk;
				return this;
			}
		
	        /**
	        
			Change name of walking animation. Supported in UE2.
		 
	        */
	        protected
	         String WalkAnim =
	       	
	        		null
	        	;
	
	        
	        
 		/**
         * 
			Change name of walking animation. Supported in UE2.
		 
         */
        public String getWalkAnim()
 	
	        {
	            return
	        	 WalkAnim;
	        }
	        
	        
	        
 		
 		/**
         * 
			Change name of walking animation. Supported in UE2.
		 
         */
        public SetWalk 
        setWalkAnim(String WalkAnim)
 	
			{
				this.WalkAnim = WalkAnim;
				return this;
			}
		
	        /**
	        
			Change name of running animation. Supported in UE2
		 
	        */
	        protected
	         String RunAnim =
	       	
	        		null
	        	;
	
	        
	        
 		/**
         * 
			Change name of running animation. Supported in UE2
		 
         */
        public String getRunAnim()
 	
	        {
	            return
	        	 RunAnim;
	        }
	        
	        
	        
 		
 		/**
         * 
			Change name of running animation. Supported in UE2
		 
         */
        public SetWalk 
        setRunAnim(String RunAnim)
 	
			{
				this.RunAnim = RunAnim;
				return this;
			}
		
 	    public String toString() {
            return toMessage();
        }
 	
 		public String toHtmlString() {
			return super.toString() + "[<br/>" +
            	
            	"<b>Walk</b> = " +
            	String.valueOf(isWalk()
 	) +
            	" <br/> " +
            	
            	"<b>WalkAnim</b> = " +
            	String.valueOf(getWalkAnim()
 	) +
            	" <br/> " +
            	
            	"<b>RunAnim</b> = " +
            	String.valueOf(getRunAnim()
 	) +
            	" <br/> " +
            	 
            	"<br/>]"
            ;
		}
 	
		public String toMessage() {
     		StringBuffer buf = new StringBuffer();
     		buf.append("SETWALK");
     		
						if (Walk != null) {
							buf.append(" {Walk " + Walk + "}");
						}
					
						if (WalkAnim != null) {
							buf.append(" {WalkAnim " + WalkAnim + "}");
						}
					
						if (RunAnim != null) {
							buf.append(" {RunAnim " + RunAnim + "}");
						}
					
   			return buf.toString();
   		}
 	
 		// --- Extra Java from XML BEGIN (extra/code/java)
        	
		// --- Extra Java from XML END (extra/code/java)
 	
	        }
    	