
	 	/**
         IMPORTANT !!!

         DO NOT EDIT THIS FILE. IT IS GENERATED FROM approriate xml file in xmlresources/gbcommands BY
         THE JavaClassesGenerator.xslt. MODIFY THAT FILE INSTEAD OF THIS ONE.
         
         Use Ant task process-gb-messages after that to generate .java files again.
         
         IMPORTANT END !!!
        */
 	package cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands;import java.util.*;import javax.vecmath.*;import cz.cuni.amis.pogamut.base.communication.messages.*;import cz.cuni.amis.pogamut.base.communication.worldview.*;import cz.cuni.amis.pogamut.base.communication.worldview.event.*;import cz.cuni.amis.pogamut.base.communication.worldview.object.*;import cz.cuni.amis.pogamut.multi.communication.worldview.object.*;import cz.cuni.amis.pogamut.base.communication.translator.event.*;import cz.cuni.amis.pogamut.multi.communication.translator.event.*;import cz.cuni.amis.pogamut.base3d.worldview.object.*;import cz.cuni.amis.pogamut.base3d.worldview.object.event.*;import cz.cuni.amis.pogamut.ut2004.communication.messages.*;import cz.cuni.amis.pogamut.ut2004.communication.worldview.objects.*;import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.objects.*;import cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor.*;import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType.Category;import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;import cz.cuni.amis.utils.exception.*;import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdateResult.Result;import cz.cuni.amis.utils.SafeEquals;import cz.cuni.amis.pogamut.base.agent.*;import cz.cuni.amis.pogamut.multi.agent.*;import cz.cuni.amis.pogamut.multi.communication.worldview.property.*;import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.property.*;import cz.cuni.amis.utils.token.*;import cz.cuni.amis.utils.*;
 		/**
 		 * Representation of the GameBots2004 command ADDINV.
 		 *
 		 * 
		We can add custom inventory for specified bot. This command can
		be issued also by bot on the bot itself (in this case Id
		attribute is not parsed). Issuing by bot is allowed just when
		the game has allowed cheating (bAllowCheats = True in GameBots2004.ini
		file).
	
         */
 	public class AddInventory 
		extends CommandMessage
	        {
	        	
		        
    	/** Example how the message looks like - used during parser tests. */
    	public static final String PROTOTYPE =
    		" {Id unreal_id}  {Type text} ";
    
		/**
		 * Creates new instance of command AddInventory.
		 * 
		We can add custom inventory for specified bot. This command can
		be issued also by bot on the bot itself (in this case Id
		attribute is not parsed). Issuing by bot is allowed just when
		the game has allowed cheating (bAllowCheats = True in GameBots2004.ini
		file).
	
		 * Corresponding GameBots message for this command is
		 * ADDINV.
		 *
		 * 
		 *    @param Id 
			Id of the target bot. Is used just when sending command to
			the server. If sending command to the bot Id is ignored and
			ADDINV command is executed on the bot (if bAllowCheats ==
			True).
		
		 *    @param Type 
			Class of the item we want to add. Must be pickup class (e.g.
			xWeapons.FlakCannonPickup).
		
		 */
		public AddInventory(
			UnrealId Id,  String Type
		) {
			
				this.Id = Id;
            
				this.Type = Type;
            
		}

		
			/**
			 * Creates new instance of command AddInventory.
			 * 
		We can add custom inventory for specified bot. This command can
		be issued also by bot on the bot itself (in this case Id
		attribute is not parsed). Issuing by bot is allowed just when
		the game has allowed cheating (bAllowCheats = True in GameBots2004.ini
		file).
	
			 * Corresponding GameBots message for this command is
			 * ADDINV.
			 * <p></p>
			 * WARNING: this is empty-command constructor, you have to use setters to fill it up with data that should be sent to GameBots2004!
		     */
		    public AddInventory() {
		    }
			
		
		/**
		 * Cloning constructor.
		 *
		 * @param original
		 */
		public AddInventory(AddInventory original) {
		   
		        this.Id = original.Id;
		   
		        this.Type = original.Type;
		   
		}
    
	        /**
	        
			Id of the target bot. Is used just when sending command to
			the server. If sending command to the bot Id is ignored and
			ADDINV command is executed on the bot (if bAllowCheats ==
			True).
		 
	        */
	        protected
	         UnrealId Id =
	       	
	        		null
	        	;
	
	        
	        
 		/**
         * 
			Id of the target bot. Is used just when sending command to
			the server. If sending command to the bot Id is ignored and
			ADDINV command is executed on the bot (if bAllowCheats ==
			True).
		 
         */
        public UnrealId getId()
 	
	        {
	            return
	        	 Id;
	        }
	        
	        
	        
 		
 		/**
         * 
			Id of the target bot. Is used just when sending command to
			the server. If sending command to the bot Id is ignored and
			ADDINV command is executed on the bot (if bAllowCheats ==
			True).
		 
         */
        public AddInventory 
        setId(UnrealId Id)
 	
			{
				this.Id = Id;
				return this;
			}
		
	        /**
	        
			Class of the item we want to add. Must be pickup class (e.g.
			xWeapons.FlakCannonPickup).
		 
	        */
	        protected
	         String Type =
	       	
	        		null
	        	;
	
	        
	        
 		/**
         * 
			Class of the item we want to add. Must be pickup class (e.g.
			xWeapons.FlakCannonPickup).
		 
         */
        public String getType()
 	
	        {
	            return
	        	 Type;
	        }
	        
	        
	        
 		
 		/**
         * 
			Class of the item we want to add. Must be pickup class (e.g.
			xWeapons.FlakCannonPickup).
		 
         */
        public AddInventory 
        setType(String Type)
 	
			{
				this.Type = Type;
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
            	
            	"<b>Type</b> = " +
            	String.valueOf(getType()
 	) +
            	" <br/> " +
            	 
            	"<br/>]"
            ;
		}
 	
		public String toMessage() {
     		StringBuffer buf = new StringBuffer();
     		buf.append("ADDINV");
     		
						if (Id != null) {
							buf.append(" {Id " + Id.getStringId() + "}");
						}
					
						if (Type != null) {
							buf.append(" {Type " + Type + "}");
						}
					
   			return buf.toString();
   		}
 	
 		// --- Extra Java from XML BEGIN (extra/code/java)
        	
		// --- Extra Java from XML END (extra/code/java)
 	
	        }
    	