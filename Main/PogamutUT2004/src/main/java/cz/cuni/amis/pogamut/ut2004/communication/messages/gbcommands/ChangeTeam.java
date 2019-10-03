
	 	/**
         IMPORTANT !!!

         DO NOT EDIT THIS FILE. IT IS GENERATED FROM approriate xml file in xmlresources/gbcommands BY
         THE JavaClassesGenerator.xslt. MODIFY THAT FILE INSTEAD OF THIS ONE.
         
         Use Ant task process-gb-messages after that to generate .java files again.
         
         IMPORTANT END !!!
        */
 	package cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands;import java.util.*;import javax.vecmath.*;import cz.cuni.amis.pogamut.base.communication.messages.*;import cz.cuni.amis.pogamut.base.communication.worldview.*;import cz.cuni.amis.pogamut.base.communication.worldview.event.*;import cz.cuni.amis.pogamut.base.communication.worldview.object.*;import cz.cuni.amis.pogamut.multi.communication.worldview.object.*;import cz.cuni.amis.pogamut.base.communication.translator.event.*;import cz.cuni.amis.pogamut.multi.communication.translator.event.*;import cz.cuni.amis.pogamut.base3d.worldview.object.*;import cz.cuni.amis.pogamut.base3d.worldview.object.event.*;import cz.cuni.amis.pogamut.ut2004.communication.messages.*;import cz.cuni.amis.pogamut.ut2004.communication.worldview.objects.*;import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.objects.*;import cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor.*;import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType.Category;import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;import cz.cuni.amis.utils.exception.*;import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdateResult.Result;import cz.cuni.amis.utils.SafeEquals;import cz.cuni.amis.pogamut.base.agent.*;import cz.cuni.amis.pogamut.multi.agent.*;import cz.cuni.amis.pogamut.multi.communication.worldview.property.*;import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.property.*;import cz.cuni.amis.utils.token.*;import cz.cuni.amis.utils.*;
 		/**
 		 * Representation of the GameBots2004 command CHANGETEAM.
 		 *
 		 * 
		Command for changing the bot team. Responds with TEAMCHANGE
		message. This command can be issued also by bot on the bot
		itself (in this case Id attribute is not parsed).
	
         */
 	public class ChangeTeam 
		extends CommandMessage
	        {
	        	
		        
    	/** Example how the message looks like - used during parser tests. */
    	public static final String PROTOTYPE =
    		" {Id unreal_id}  {Team 0} ";
    
		/**
		 * Creates new instance of command ChangeTeam.
		 * 
		Command for changing the bot team. Responds with TEAMCHANGE
		message. This command can be issued also by bot on the bot
		itself (in this case Id attribute is not parsed).
	
		 * Corresponding GameBots message for this command is
		 * CHANGETEAM.
		 *
		 * 
		 *    @param Id 
			Id of the target bot (won't be parsed if sent to bot
			connection).
		
		 *    @param Team 
			This is the team we want to change to (0 for red, 1 for
			blue, etc.).
		
		 */
		public ChangeTeam(
			UnrealId Id,  Integer Team
		) {
			
				this.Id = Id;
            
				this.Team = Team;
            
		}

		
			/**
			 * Creates new instance of command ChangeTeam.
			 * 
		Command for changing the bot team. Responds with TEAMCHANGE
		message. This command can be issued also by bot on the bot
		itself (in this case Id attribute is not parsed).
	
			 * Corresponding GameBots message for this command is
			 * CHANGETEAM.
			 * <p></p>
			 * WARNING: this is empty-command constructor, you have to use setters to fill it up with data that should be sent to GameBots2004!
		     */
		    public ChangeTeam() {
		    }
			
		
		/**
		 * Cloning constructor.
		 *
		 * @param original
		 */
		public ChangeTeam(ChangeTeam original) {
		   
		        this.Id = original.Id;
		   
		        this.Team = original.Team;
		   
		}
    
	        /**
	        
			Id of the target bot (won't be parsed if sent to bot
			connection).
		 
	        */
	        protected
	         UnrealId Id =
	       	
	        		null
	        	;
	
	        
	        
 		/**
         * 
			Id of the target bot (won't be parsed if sent to bot
			connection).
		 
         */
        public UnrealId getId()
 	
	        {
	            return
	        	 Id;
	        }
	        
	        
	        
 		
 		/**
         * 
			Id of the target bot (won't be parsed if sent to bot
			connection).
		 
         */
        public ChangeTeam 
        setId(UnrealId Id)
 	
			{
				this.Id = Id;
				return this;
			}
		
	        /**
	        
			This is the team we want to change to (0 for red, 1 for
			blue, etc.).
		 
	        */
	        protected
	         Integer Team =
	       	
	        		null
	        	;
	
	        
	        
 		/**
         * 
			This is the team we want to change to (0 for red, 1 for
			blue, etc.).
		 
         */
        public Integer getTeam()
 	
	        {
	            return
	        	 Team;
	        }
	        
	        
	        
 		
 		/**
         * 
			This is the team we want to change to (0 for red, 1 for
			blue, etc.).
		 
         */
        public ChangeTeam 
        setTeam(Integer Team)
 	
			{
				this.Team = Team;
				return this;
			}
		
 	    public String toString() {
            return toMessage();
        }
 	
 		public String toHtmlString() {
			return super.toString() + "[<br/>" +
            	
            	"<b>Id</b> = " +
            	String.valueOf(getId()
 	) +
            	" <br/> " +
            	
            	"<b>Team</b> = " +
            	String.valueOf(getTeam()
 	) +
            	" <br/> " +
            	 
            	"<br/>]"
            ;
		}
 	
		public String toMessage() {
     		StringBuffer buf = new StringBuffer();
     		buf.append("CHANGETEAM");
     		
						if (Id != null) {
							buf.append(" {Id " + Id.getStringId() + "}");
						}
					
						if (Team != null) {
							buf.append(" {Team " + Team + "}");
						}
					
   			return buf.toString();
   		}
 	
 		// --- Extra Java from XML BEGIN (extra/code/java)
        	
		// --- Extra Java from XML END (extra/code/java)
 	
	        }
    	