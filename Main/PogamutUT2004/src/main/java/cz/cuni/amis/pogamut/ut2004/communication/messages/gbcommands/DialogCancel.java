
	 	/**
         IMPORTANT !!!

         DO NOT EDIT THIS FILE. IT IS GENERATED FROM approriate xml file in xmlresources/gbcommands BY
         THE JavaClassesGenerator.xslt. MODIFY THAT FILE INSTEAD OF THIS ONE.
         
         Use Ant task process-gb-messages after that to generate .java files again.
         
         IMPORTANT END !!!
        */
 	package cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands;import java.util.*;import javax.vecmath.*;import cz.cuni.amis.pogamut.base.communication.messages.*;import cz.cuni.amis.pogamut.base.communication.worldview.*;import cz.cuni.amis.pogamut.base.communication.worldview.event.*;import cz.cuni.amis.pogamut.base.communication.worldview.object.*;import cz.cuni.amis.pogamut.multi.communication.worldview.object.*;import cz.cuni.amis.pogamut.base.communication.translator.event.*;import cz.cuni.amis.pogamut.multi.communication.translator.event.*;import cz.cuni.amis.pogamut.base3d.worldview.object.*;import cz.cuni.amis.pogamut.base3d.worldview.object.event.*;import cz.cuni.amis.pogamut.ut2004.communication.messages.*;import cz.cuni.amis.pogamut.ut2004.communication.worldview.objects.*;import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.objects.*;import cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor.*;import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType.Category;import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;import cz.cuni.amis.utils.exception.*;import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdateResult.Result;import cz.cuni.amis.utils.SafeEquals;import cz.cuni.amis.pogamut.base.agent.*;import cz.cuni.amis.pogamut.multi.agent.*;import cz.cuni.amis.pogamut.multi.communication.worldview.property.*;import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.property.*;import cz.cuni.amis.utils.token.*;import cz.cuni.amis.utils.*;
 		/**
 		 * Representation of the GameBots2004 command DLGCANCEL.
 		 *
 		 * 
		Cancels some dialog, hiding him from screen. The dialog sends DLGCMD with Command CANCEL and latest available data as confirmation.
	
         */
 	public class DialogCancel 
		extends CommandMessage
	        {
	        	
		        
    	/** Example how the message looks like - used during parser tests. */
    	public static final String PROTOTYPE =
    		" {Player text}  {Id text} ";
    
		/**
		 * Creates new instance of command DialogCancel.
		 * 
		Cancels some dialog, hiding him from screen. The dialog sends DLGCMD with Command CANCEL and latest available data as confirmation.
	
		 * Corresponding GameBots message for this command is
		 * DLGCANCEL.
		 *
		 * 
		 *    @param Player Name of the player on who's HUD is the dialog.
		 *    @param Id Id of the cancelled dialog.
		 */
		public DialogCancel(
			String Player,  String Id
		) {
			
				this.Player = Player;
            
				this.Id = Id;
            
		}

		
			/**
			 * Creates new instance of command DialogCancel.
			 * 
		Cancels some dialog, hiding him from screen. The dialog sends DLGCMD with Command CANCEL and latest available data as confirmation.
	
			 * Corresponding GameBots message for this command is
			 * DLGCANCEL.
			 * <p></p>
			 * WARNING: this is empty-command constructor, you have to use setters to fill it up with data that should be sent to GameBots2004!
		     */
		    public DialogCancel() {
		    }
			
		
		/**
		 * Cloning constructor.
		 *
		 * @param original
		 */
		public DialogCancel(DialogCancel original) {
		   
		        this.Player = original.Player;
		   
		        this.Id = original.Id;
		   
		}
    
	        /**
	        Name of the player on who's HUD is the dialog. 
	        */
	        protected
	         String Player =
	       	
	        		null
	        	;
	
	        
	        
 		/**
         * Name of the player on who's HUD is the dialog. 
         */
        public String getPlayer()
 	
	        {
	            return
	        	 Player;
	        }
	        
	        
	        
 		
 		/**
         * Name of the player on who's HUD is the dialog. 
         */
        public DialogCancel 
        setPlayer(String Player)
 	
			{
				this.Player = Player;
				return this;
			}
		
	        /**
	        Id of the cancelled dialog. 
	        */
	        protected
	         String Id =
	       	
	        		null
	        	;
	
	        
	        
 		/**
         * Id of the cancelled dialog. 
         */
        public String getId()
 	
	        {
	            return
	        	 Id;
	        }
	        
	        
	        
 		
 		/**
         * Id of the cancelled dialog. 
         */
        public DialogCancel 
        setId(String Id)
 	
			{
				this.Id = Id;
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
            	
            	"<b>Id</b> = " +
            	String.valueOf(getId()
 	) +
            	" <br/> " +
            	 
            	"<br/>]"
            ;
		}
 	
		public String toMessage() {
     		StringBuffer buf = new StringBuffer();
     		buf.append("DLGCANCEL");
     		
						if (Player != null) {
							buf.append(" {Player " + Player + "}");
						}
					
						if (Id != null) {
							buf.append(" {Id " + Id + "}");
						}
					
   			return buf.toString();
   		}
 	
 		// --- Extra Java from XML BEGIN (extra/code/java)
        	
		// --- Extra Java from XML END (extra/code/java)
 	
	        }
    	