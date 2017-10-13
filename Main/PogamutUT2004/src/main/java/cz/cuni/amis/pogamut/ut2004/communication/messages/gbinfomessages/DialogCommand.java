package cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages;
 		
 		// --- IMPORTS FROM /messages/settings/javasettings/javaimport BEGIN
			import java.util.*;import javax.vecmath.*;import cz.cuni.amis.pogamut.base.communication.messages.*;import cz.cuni.amis.pogamut.base.communication.worldview.*;import cz.cuni.amis.pogamut.base.communication.worldview.event.*;import cz.cuni.amis.pogamut.base.communication.worldview.object.*;import cz.cuni.amis.pogamut.multi.communication.worldview.object.*;import cz.cuni.amis.pogamut.base.communication.translator.event.*;import cz.cuni.amis.pogamut.multi.communication.translator.event.*;import cz.cuni.amis.pogamut.base3d.worldview.object.*;import cz.cuni.amis.pogamut.base3d.worldview.object.event.*;import cz.cuni.amis.pogamut.ut2004.communication.messages.*;import cz.cuni.amis.pogamut.ut2004.communication.worldview.objects.*;import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.objects.*;import cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor.*;import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType.Category;import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;import cz.cuni.amis.utils.exception.*;import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdateResult.Result;import cz.cuni.amis.utils.SafeEquals;import cz.cuni.amis.pogamut.base.agent.*;import cz.cuni.amis.pogamut.multi.agent.*;import cz.cuni.amis.pogamut.multi.communication.worldview.property.*;import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.property.*;import cz.cuni.amis.utils.token.*;import cz.cuni.amis.utils.*;
		// --- IMPORTS FROM /messages/settings/javasettings/javaimport END
		
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name='all'] BEGIN
				
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name='all'] END
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name=event]+classtype[@name=impl] BEGIN
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name=event]+classtype[@name=impl] END
    
 		/**
         *  
         			Definition of the event DLGCMD.
         		
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Asynchronous message. Message with data from dialog.
	
         */
 	public class DialogCommand 
  				extends InfoMessage
    			implements IWorldEvent, IWorldChangeEvent
    			
	    {
 	
    	/** Example how the message looks like - used during parser tests. */
    	public static final String PROTOTYPE =
    		"DLGCMD {Id text}  {SourceId text}  {Command text}  {Data text} ";
    
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public DialogCommand()
		{
		}
	
    	
    	
    	
    	/**
		 * Creates new instance of the message DialogCommand.
		 * 
		Asynchronous message. Message with data from dialog.
	
		 * Corresponding GameBots message
		 *   
		 *   is
		 *   DLGCMD.
		 * 
 	  	 * 
		 *   
		 *     @param Id Id of the dialog for with which is this command related to.
		 *   
		 * 
		 *   
		 *     @param SourceId Id of the component from where the command originates.
		 *   
		 * 
		 *   
		 *     @param Command Type of the command. SUBMIT and CANCEL carry the data.
		 *   
		 * 
		 *   
		 *     @param Data Data carried in the message, usually in form "param1=value1&param2=value2&...&paramN=valueN", equals, ampersands and backslashes are escaped by backslash.
		 *   
		 * 
		 */
		public DialogCommand(
			String Id,  String SourceId,  String Command,  String Data
		) {
			
					this.Id = Id;
				
					this.SourceId = SourceId;
				
					this.Command = Command;
				
					this.Data = Data;
				
		}
    
    	/**
		 * Cloning constructor from the full message.
		 *
		 * @param original
		 */
		public DialogCommand(DialogCommand original) {		
			
					this.Id = original.getId()
 	;
				
					this.SourceId = original.getSourceId()
 	;
				
					this.Command = original.getCommand()
 	;
				
					this.Data = original.getData()
 	;
				
			this.SimTime = original.getSimTime();			
		}
		
	   		
			protected long SimTime;
				
			/**
			 * Simulation time in MILLI SECONDS !!!
			 */	
			@Override
			public long getSimTime() {
				return SimTime;
			}
						
			/**
			 * Used by Yylex to slip correct time of the object or programmatically.
			 */
			protected void setSimTime(long SimTime) {
				this.SimTime = SimTime;
			}
	   	
    	
	    /**
         * Id of the dialog for with which is this command related to. 
         */
        protected
         String Id =
       	null;
	
 		/**
         * Id of the dialog for with which is this command related to. 
         */
        public  String getId()
 	 {
    					return Id;
    				}
    			
    	
	    /**
         * Id of the component from where the command originates. 
         */
        protected
         String SourceId =
       	null;
	
 		/**
         * Id of the component from where the command originates. 
         */
        public  String getSourceId()
 	 {
    					return SourceId;
    				}
    			
    	
	    /**
         * Type of the command. SUBMIT and CANCEL carry the data. 
         */
        protected
         String Command =
       	null;
	
 		/**
         * Type of the command. SUBMIT and CANCEL carry the data. 
         */
        public  String getCommand()
 	 {
    					return Command;
    				}
    			
    	
	    /**
         * Data carried in the message, usually in form "param1=value1&param2=value2&...&paramN=valueN", equals, ampersands and backslashes are escaped by backslash. 
         */
        protected
         String Data =
       	null;
	
 		/**
         * Data carried in the message, usually in form "param1=value1&param2=value2&...&paramN=valueN", equals, ampersands and backslashes are escaped by backslash. 
         */
        public  String getData()
 	 {
    					return Data;
    				}
    			
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
		              			"Id = " + String.valueOf(getId()
 	) + " | " + 
		              		
		              			"SourceId = " + String.valueOf(getSourceId()
 	) + " | " + 
		              		
		              			"Command = " + String.valueOf(getCommand()
 	) + " | " + 
		              		
		              			"Data = " + String.valueOf(getData()
 	) + " | " + 
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>Id</b> = " + String.valueOf(getId()
 	) + " <br/> " + 
		              		
		              			"<b>SourceId</b> = " + String.valueOf(getSourceId()
 	) + " <br/> " + 
		              		
		              			"<b>Command</b> = " + String.valueOf(getCommand()
 	) + " <br/> " + 
		              		
		              			"<b>Data</b> = " + String.valueOf(getData()
 	) + " <br/> " + 
		              		
				"<br/>]";     
		}
 	
 	    public String toJsonLiteral() {
            return "dialogcommand( "
            		+
									(getId()
 	 == null ? "null" :
										"\"" + getId()
 	 + "\"" 
									)
								+ ", " + 
									(getSourceId()
 	 == null ? "null" :
										"\"" + getSourceId()
 	 + "\"" 
									)
								+ ", " + 
									(getCommand()
 	 == null ? "null" :
										"\"" + getCommand()
 	 + "\"" 
									)
								+ ", " + 
									(getData()
 	 == null ? "null" :
										"\"" + getData()
 	 + "\"" 
									)
								
                   + ")";
        }
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=event+classtype[@name=impl]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=event+classtype[@name=impl]) ---        	            	
 	
		}
 	