
	 	/**
         IMPORTANT !!!

         DO NOT EDIT THIS FILE. IT IS GENERATED FROM approriate xml file in xmlresources/gbcommands BY
         THE JavaClassesGenerator.xslt. MODIFY THAT FILE INSTEAD OF THIS ONE.
         
         Use Ant task process-gb-messages after that to generate .java files again.
         
         IMPORTANT END !!!
        */
 	package cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands;import java.util.*;import javax.vecmath.*;import cz.cuni.amis.pogamut.base.communication.messages.*;import cz.cuni.amis.pogamut.base.communication.worldview.*;import cz.cuni.amis.pogamut.base.communication.worldview.event.*;import cz.cuni.amis.pogamut.base.communication.worldview.object.*;import cz.cuni.amis.pogamut.multi.communication.worldview.object.*;import cz.cuni.amis.pogamut.base.communication.translator.event.*;import cz.cuni.amis.pogamut.multi.communication.translator.event.*;import cz.cuni.amis.pogamut.base3d.worldview.object.*;import cz.cuni.amis.pogamut.base3d.worldview.object.event.*;import cz.cuni.amis.pogamut.ut2004.communication.messages.*;import cz.cuni.amis.pogamut.ut2004.communication.worldview.objects.*;import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.objects.*;import cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor.*;import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType.Category;import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;import cz.cuni.amis.utils.exception.*;import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdateResult.Result;import cz.cuni.amis.utils.SafeEquals;import cz.cuni.amis.pogamut.base.agent.*;import cz.cuni.amis.pogamut.multi.agent.*;import cz.cuni.amis.pogamut.multi.communication.worldview.property.*;import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.property.*;import cz.cuni.amis.utils.token.*;import cz.cuni.amis.utils.*;
 		/**
 		 * Representation of the GameBots2004 command DIALOG.
 		 *
 		 * 
		Sets the dialog for specified player. Dialog will be set just if the player has our bot selected by cursor (ALT+SHIFT and the LeftMouse button). Note that not all of the Option variables have to be set. But they need to be set succesively starting from 0,1,2 etc. They are parsed in this order and first emty Option will terminate parsing.
	
         */
 	public class SetDialog 
		extends CommandMessage
	        {
	        	
		        
    	/** Example how the message looks like - used during parser tests. */
    	public static final String PROTOTYPE =
    		" {Id unreal_id}  {DialogId text}  {Text text}  {Option0 text}  {Option1 text}  {Option2 text}  {Option3 text}  {Option4 text}  {Option5 text}  {Option6 text}  {Option7 text}  {Option8 text}  {Option9 text} ";
    
		/**
		 * Creates new instance of command SetDialog.
		 * 
		Sets the dialog for specified player. Dialog will be set just if the player has our bot selected by cursor (ALT+SHIFT and the LeftMouse button). Note that not all of the Option variables have to be set. But they need to be set succesively starting from 0,1,2 etc. They are parsed in this order and first emty Option will terminate parsing.
	
		 * Corresponding GameBots message for this command is
		 * DIALOG.
		 *
		 * 
		 *    @param Id Id of the player we want to set dialog for.
		 *    @param DialogId Our Id of the dialog - we will match this Id in response messages and in PLI messages. We choose this value.
		 *    @param Text Text of the dialog.
		 *    @param Option0 
			One of the options (possible answers) for the dialog.
		
		 *    @param Option1 
			One of the options (possible answers) for the dialog.
		
		 *    @param Option2 
			One of the options (possible answers) for the dialog.
		
		 *    @param Option3 
			One of the options (possible answers) for the dialog.
		
		 *    @param Option4 
			One of the options (possible answers) for the dialog.
		
		 *    @param Option5 
			One of the options (possible answers) for the dialog.
		
		 *    @param Option6 
			One of the options (possible answers) for the dialog.
		
		 *    @param Option7 
			One of the options (possible answers) for the dialog.
		
		 *    @param Option8 
			One of the options (possible answers) for the dialog.
		
		 *    @param Option9 
			One of the options (possible answers) for the dialog.
		
		 */
		public SetDialog(
			UnrealId Id,  String DialogId,  String Text,  String Option0,  String Option1,  String Option2,  String Option3,  String Option4,  String Option5,  String Option6,  String Option7,  String Option8,  String Option9
		) {
			
				this.Id = Id;
            
				this.DialogId = DialogId;
            
				this.Text = Text;
            
				this.Option0 = Option0;
            
				this.Option1 = Option1;
            
				this.Option2 = Option2;
            
				this.Option3 = Option3;
            
				this.Option4 = Option4;
            
				this.Option5 = Option5;
            
				this.Option6 = Option6;
            
				this.Option7 = Option7;
            
				this.Option8 = Option8;
            
				this.Option9 = Option9;
            
		}

		
			/**
			 * Creates new instance of command SetDialog.
			 * 
		Sets the dialog for specified player. Dialog will be set just if the player has our bot selected by cursor (ALT+SHIFT and the LeftMouse button). Note that not all of the Option variables have to be set. But they need to be set succesively starting from 0,1,2 etc. They are parsed in this order and first emty Option will terminate parsing.
	
			 * Corresponding GameBots message for this command is
			 * DIALOG.
			 * <p></p>
			 * WARNING: this is empty-command constructor, you have to use setters to fill it up with data that should be sent to GameBots2004!
		     */
		    public SetDialog() {
		    }
			
		
		/**
		 * Cloning constructor.
		 *
		 * @param original
		 */
		public SetDialog(SetDialog original) {
		   
		        this.Id = original.Id;
		   
		        this.DialogId = original.DialogId;
		   
		        this.Text = original.Text;
		   
		        this.Option0 = original.Option0;
		   
		        this.Option1 = original.Option1;
		   
		        this.Option2 = original.Option2;
		   
		        this.Option3 = original.Option3;
		   
		        this.Option4 = original.Option4;
		   
		        this.Option5 = original.Option5;
		   
		        this.Option6 = original.Option6;
		   
		        this.Option7 = original.Option7;
		   
		        this.Option8 = original.Option8;
		   
		        this.Option9 = original.Option9;
		   
		}
    
	        /**
	        Id of the player we want to set dialog for. 
	        */
	        protected
	         UnrealId Id =
	       	
	        		null
	        	;
	
	        
	        
 		/**
         * Id of the player we want to set dialog for. 
         */
        public UnrealId getId()
 	
	        {
	            return
	        	 Id;
	        }
	        
	        
	        
 		
 		/**
         * Id of the player we want to set dialog for. 
         */
        public SetDialog 
        setId(UnrealId Id)
 	
			{
				this.Id = Id;
				return this;
			}
		
	        /**
	        Our Id of the dialog - we will match this Id in response messages and in PLI messages. We choose this value. 
	        */
	        protected
	         String DialogId =
	       	
	        		null
	        	;
	
	        
	        
 		/**
         * Our Id of the dialog - we will match this Id in response messages and in PLI messages. We choose this value. 
         */
        public String getDialogId()
 	
	        {
	            return
	        	 DialogId;
	        }
	        
	        
	        
 		
 		/**
         * Our Id of the dialog - we will match this Id in response messages and in PLI messages. We choose this value. 
         */
        public SetDialog 
        setDialogId(String DialogId)
 	
			{
				this.DialogId = DialogId;
				return this;
			}
		
	        /**
	        Text of the dialog. 
	        */
	        protected
	         String Text =
	       	
	        		null
	        	;
	
	        
	        
 		/**
         * Text of the dialog. 
         */
        public String getText()
 	
	        {
	            return
	        	 Text;
	        }
	        
	        
	        
 		
 		/**
         * Text of the dialog. 
         */
        public SetDialog 
        setText(String Text)
 	
			{
				this.Text = Text;
				return this;
			}
		
	        /**
	        
			One of the options (possible answers) for the dialog.
		 
	        */
	        protected
	         String Option0 =
	       	
	        		null
	        	;
	
	        
	        
 		/**
         * 
			One of the options (possible answers) for the dialog.
		 
         */
        public String getOption0()
 	
	        {
	            return
	        	 Option0;
	        }
	        
	        
	        
 		
 		/**
         * 
			One of the options (possible answers) for the dialog.
		 
         */
        public SetDialog 
        setOption0(String Option0)
 	
			{
				this.Option0 = Option0;
				return this;
			}
		
	        /**
	        
			One of the options (possible answers) for the dialog.
		 
	        */
	        protected
	         String Option1 =
	       	
	        		null
	        	;
	
	        
	        
 		/**
         * 
			One of the options (possible answers) for the dialog.
		 
         */
        public String getOption1()
 	
	        {
	            return
	        	 Option1;
	        }
	        
	        
	        
 		
 		/**
         * 
			One of the options (possible answers) for the dialog.
		 
         */
        public SetDialog 
        setOption1(String Option1)
 	
			{
				this.Option1 = Option1;
				return this;
			}
		
	        /**
	        
			One of the options (possible answers) for the dialog.
		 
	        */
	        protected
	         String Option2 =
	       	
	        		null
	        	;
	
	        
	        
 		/**
         * 
			One of the options (possible answers) for the dialog.
		 
         */
        public String getOption2()
 	
	        {
	            return
	        	 Option2;
	        }
	        
	        
	        
 		
 		/**
         * 
			One of the options (possible answers) for the dialog.
		 
         */
        public SetDialog 
        setOption2(String Option2)
 	
			{
				this.Option2 = Option2;
				return this;
			}
		
	        /**
	        
			One of the options (possible answers) for the dialog.
		 
	        */
	        protected
	         String Option3 =
	       	
	        		null
	        	;
	
	        
	        
 		/**
         * 
			One of the options (possible answers) for the dialog.
		 
         */
        public String getOption3()
 	
	        {
	            return
	        	 Option3;
	        }
	        
	        
	        
 		
 		/**
         * 
			One of the options (possible answers) for the dialog.
		 
         */
        public SetDialog 
        setOption3(String Option3)
 	
			{
				this.Option3 = Option3;
				return this;
			}
		
	        /**
	        
			One of the options (possible answers) for the dialog.
		 
	        */
	        protected
	         String Option4 =
	       	
	        		null
	        	;
	
	        
	        
 		/**
         * 
			One of the options (possible answers) for the dialog.
		 
         */
        public String getOption4()
 	
	        {
	            return
	        	 Option4;
	        }
	        
	        
	        
 		
 		/**
         * 
			One of the options (possible answers) for the dialog.
		 
         */
        public SetDialog 
        setOption4(String Option4)
 	
			{
				this.Option4 = Option4;
				return this;
			}
		
	        /**
	        
			One of the options (possible answers) for the dialog.
		 
	        */
	        protected
	         String Option5 =
	       	
	        		null
	        	;
	
	        
	        
 		/**
         * 
			One of the options (possible answers) for the dialog.
		 
         */
        public String getOption5()
 	
	        {
	            return
	        	 Option5;
	        }
	        
	        
	        
 		
 		/**
         * 
			One of the options (possible answers) for the dialog.
		 
         */
        public SetDialog 
        setOption5(String Option5)
 	
			{
				this.Option5 = Option5;
				return this;
			}
		
	        /**
	        
			One of the options (possible answers) for the dialog.
		 
	        */
	        protected
	         String Option6 =
	       	
	        		null
	        	;
	
	        
	        
 		/**
         * 
			One of the options (possible answers) for the dialog.
		 
         */
        public String getOption6()
 	
	        {
	            return
	        	 Option6;
	        }
	        
	        
	        
 		
 		/**
         * 
			One of the options (possible answers) for the dialog.
		 
         */
        public SetDialog 
        setOption6(String Option6)
 	
			{
				this.Option6 = Option6;
				return this;
			}
		
	        /**
	        
			One of the options (possible answers) for the dialog.
		 
	        */
	        protected
	         String Option7 =
	       	
	        		null
	        	;
	
	        
	        
 		/**
         * 
			One of the options (possible answers) for the dialog.
		 
         */
        public String getOption7()
 	
	        {
	            return
	        	 Option7;
	        }
	        
	        
	        
 		
 		/**
         * 
			One of the options (possible answers) for the dialog.
		 
         */
        public SetDialog 
        setOption7(String Option7)
 	
			{
				this.Option7 = Option7;
				return this;
			}
		
	        /**
	        
			One of the options (possible answers) for the dialog.
		 
	        */
	        protected
	         String Option8 =
	       	
	        		null
	        	;
	
	        
	        
 		/**
         * 
			One of the options (possible answers) for the dialog.
		 
         */
        public String getOption8()
 	
	        {
	            return
	        	 Option8;
	        }
	        
	        
	        
 		
 		/**
         * 
			One of the options (possible answers) for the dialog.
		 
         */
        public SetDialog 
        setOption8(String Option8)
 	
			{
				this.Option8 = Option8;
				return this;
			}
		
	        /**
	        
			One of the options (possible answers) for the dialog.
		 
	        */
	        protected
	         String Option9 =
	       	
	        		null
	        	;
	
	        
	        
 		/**
         * 
			One of the options (possible answers) for the dialog.
		 
         */
        public String getOption9()
 	
	        {
	            return
	        	 Option9;
	        }
	        
	        
	        
 		
 		/**
         * 
			One of the options (possible answers) for the dialog.
		 
         */
        public SetDialog 
        setOption9(String Option9)
 	
			{
				this.Option9 = Option9;
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
            	
            	"<b>DialogId</b> = " +
            	String.valueOf(getDialogId()
 	) +
            	" <br/> " +
            	
            	"<b>Text</b> = " +
            	String.valueOf(getText()
 	) +
            	" <br/> " +
            	
            	"<b>Option0</b> = " +
            	String.valueOf(getOption0()
 	) +
            	" <br/> " +
            	
            	"<b>Option1</b> = " +
            	String.valueOf(getOption1()
 	) +
            	" <br/> " +
            	
            	"<b>Option2</b> = " +
            	String.valueOf(getOption2()
 	) +
            	" <br/> " +
            	
            	"<b>Option3</b> = " +
            	String.valueOf(getOption3()
 	) +
            	" <br/> " +
            	
            	"<b>Option4</b> = " +
            	String.valueOf(getOption4()
 	) +
            	" <br/> " +
            	
            	"<b>Option5</b> = " +
            	String.valueOf(getOption5()
 	) +
            	" <br/> " +
            	
            	"<b>Option6</b> = " +
            	String.valueOf(getOption6()
 	) +
            	" <br/> " +
            	
            	"<b>Option7</b> = " +
            	String.valueOf(getOption7()
 	) +
            	" <br/> " +
            	
            	"<b>Option8</b> = " +
            	String.valueOf(getOption8()
 	) +
            	" <br/> " +
            	
            	"<b>Option9</b> = " +
            	String.valueOf(getOption9()
 	) +
            	" <br/> " +
            	 
            	"<br/>]"
            ;
		}
 	
		public String toMessage() {
     		StringBuffer buf = new StringBuffer();
     		buf.append("DIALOG");
     		
						if (Id != null) {
							buf.append(" {Id " + Id.getStringId() + "}");
						}
					
						if (DialogId != null) {
							buf.append(" {DialogId " + DialogId + "}");
						}
					
						if (Text != null) {
							buf.append(" {Text " + Text + "}");
						}
					
						if (Option0 != null) {
							buf.append(" {Option0 " + Option0 + "}");
						}
					
						if (Option1 != null) {
							buf.append(" {Option1 " + Option1 + "}");
						}
					
						if (Option2 != null) {
							buf.append(" {Option2 " + Option2 + "}");
						}
					
						if (Option3 != null) {
							buf.append(" {Option3 " + Option3 + "}");
						}
					
						if (Option4 != null) {
							buf.append(" {Option4 " + Option4 + "}");
						}
					
						if (Option5 != null) {
							buf.append(" {Option5 " + Option5 + "}");
						}
					
						if (Option6 != null) {
							buf.append(" {Option6 " + Option6 + "}");
						}
					
						if (Option7 != null) {
							buf.append(" {Option7 " + Option7 + "}");
						}
					
						if (Option8 != null) {
							buf.append(" {Option8 " + Option8 + "}");
						}
					
						if (Option9 != null) {
							buf.append(" {Option9 " + Option9 + "}");
						}
					
   			return buf.toString();
   		}
 	
 		// --- Extra Java from XML BEGIN (extra/code/java)
        	
		// --- Extra Java from XML END (extra/code/java)
 	
	        }
    	