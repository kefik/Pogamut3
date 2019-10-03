
	 	/**
         IMPORTANT !!!

         DO NOT EDIT THIS FILE. IT IS GENERATED FROM approriate xml file in xmlresources/gbcommands BY
         THE JavaClassesGenerator.xslt. MODIFY THAT FILE INSTEAD OF THIS ONE.
         
         Use Ant task process-gb-messages after that to generate .java files again.
         
         IMPORTANT END !!!
        */
 	package cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands;import java.util.*;import javax.vecmath.*;import cz.cuni.amis.pogamut.base.communication.messages.*;import cz.cuni.amis.pogamut.base.communication.worldview.*;import cz.cuni.amis.pogamut.base.communication.worldview.event.*;import cz.cuni.amis.pogamut.base.communication.worldview.object.*;import cz.cuni.amis.pogamut.multi.communication.worldview.object.*;import cz.cuni.amis.pogamut.base.communication.translator.event.*;import cz.cuni.amis.pogamut.multi.communication.translator.event.*;import cz.cuni.amis.pogamut.base3d.worldview.object.*;import cz.cuni.amis.pogamut.base3d.worldview.object.event.*;import cz.cuni.amis.pogamut.ut2004.communication.messages.*;import cz.cuni.amis.pogamut.ut2004.communication.worldview.objects.*;import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.objects.*;import cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor.*;import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType.Category;import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;import cz.cuni.amis.utils.exception.*;import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdateResult.Result;import cz.cuni.amis.utils.SafeEquals;import cz.cuni.amis.pogamut.base.agent.*;import cz.cuni.amis.pogamut.multi.agent.*;import cz.cuni.amis.pogamut.multi.communication.worldview.property.*;import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.property.*;import cz.cuni.amis.utils.token.*;import cz.cuni.amis.utils.*;
 		/**
 		 * Representation of the GameBots2004 command PLSND.
 		 *
 		 * 
		Plays some sound.		
	
         */
 	public class PlaySound 
		extends CommandMessage
	        {
	        	
		        
    	/** Example how the message looks like - used during parser tests. */
    	public static final String PROTOTYPE =
    		" {Player text}  {Sound text} ";
    
		/**
		 * Creates new instance of command PlaySound.
		 * 
		Plays some sound.		
	
		 * Corresponding GameBots message for this command is
		 * PLSND.
		 *
		 * 
		 *    @param Player Name of the affected player.
		 *    @param Sound Name of the sound that should be played.
		 */
		public PlaySound(
			String Player,  String Sound
		) {
			
				this.Player = Player;
            
				this.Sound = Sound;
            
		}

		
			/**
			 * Creates new instance of command PlaySound.
			 * 
		Plays some sound.		
	
			 * Corresponding GameBots message for this command is
			 * PLSND.
			 * <p></p>
			 * WARNING: this is empty-command constructor, you have to use setters to fill it up with data that should be sent to GameBots2004!
		     */
		    public PlaySound() {
		    }
			
		
		/**
		 * Cloning constructor.
		 *
		 * @param original
		 */
		public PlaySound(PlaySound original) {
		   
		        this.Player = original.Player;
		   
		        this.Sound = original.Sound;
		   
		}
    
	        /**
	        Name of the affected player. 
	        */
	        protected
	         String Player =
	       	
	        		null
	        	;
	
	        
	        
 		/**
         * Name of the affected player. 
         */
        public String getPlayer()
 	
	        {
	            return
	        	 Player;
	        }
	        
	        
	        
 		
 		/**
         * Name of the affected player. 
         */
        public PlaySound 
        setPlayer(String Player)
 	
			{
				this.Player = Player;
				return this;
			}
		
	        /**
	        Name of the sound that should be played. 
	        */
	        protected
	         String Sound =
	       	
	        		null
	        	;
	
	        
	        
 		/**
         * Name of the sound that should be played. 
         */
        public String getSound()
 	
	        {
	            return
	        	 Sound;
	        }
	        
	        
	        
 		
 		/**
         * Name of the sound that should be played. 
         */
        public PlaySound 
        setSound(String Sound)
 	
			{
				this.Sound = Sound;
				return this;
			}
		
 	    public String toString() {
            return toMessage();
        }
 	
 		public String toHtmlString() {
			return super.toString() + "[<br/>" +
            	
            	"<b>Player</b> = " +
            	String.valueOf(getPlayer()
 	) +
            	" <br/> " +
            	
            	"<b>Sound</b> = " +
            	String.valueOf(getSound()
 	) +
            	" <br/> " +
            	 
            	"<br/>]"
            ;
		}
 	
		public String toMessage() {
     		StringBuffer buf = new StringBuffer();
     		buf.append("PLSND");
     		
						if (Player != null) {
							buf.append(" {Player " + Player + "}");
						}
					
						if (Sound != null) {
							buf.append(" {Sound " + Sound + "}");
						}
					
   			return buf.toString();
   		}
 	
 		// --- Extra Java from XML BEGIN (extra/code/java)
        	
		// --- Extra Java from XML END (extra/code/java)
 	
	        }
    	