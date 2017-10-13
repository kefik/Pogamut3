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
         			Definition of the event CTRLMSG.
         		
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Asynchronous message passing some control message.
	
         */
 	public class ControlMessage 
  				extends InfoMessage
    			implements IWorldEvent, IWorldChangeEvent
    			
	    {
 	
    	/** Example how the message looks like - used during parser tests. */
    	public static final String PROTOTYPE =
    		"CTRLMSG {Type text}  {PS1 text}  {PS2 text}  {PS3 text}  {PI1 0}  {PI2 0}  {PI3 0}  {PF1 0}  {PF2 0}  {PF3 0}  {PB1 False}  {PB2 False}  {PB3 False} ";
    
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public ControlMessage()
		{
		}
	
    	
    	
    	
    	/**
		 * Creates new instance of the message ControlMessage.
		 * 
		Asynchronous message passing some control message.
	
		 * Corresponding GameBots message
		 *   
		 *   is
		 *   CTRLMSG.
		 * 
 	  	 * 
		 *   
		 *     @param Type Custom type of the message.
		 *   
		 * 
		 *   
		 *     @param PS1 Param String 1
		 *   
		 * 
		 *   
		 *     @param PS2 Param String 2
		 *   
		 * 
		 *   
		 *     @param PS3 Param String 3
		 *   
		 * 
		 *   
		 *     @param PI1 Param Integer 1
		 *   
		 * 
		 *   
		 *     @param PI2 Param Integer 2
		 *   
		 * 
		 *   
		 *     @param PI3 Param Integer 3
		 *   
		 * 
		 *   
		 *     @param PF1 Param Double 1
		 *   
		 * 
		 *   
		 *     @param PF2 Param Double 2
		 *   
		 * 
		 *   
		 *     @param PF3 Param Double 3
		 *   
		 * 
		 *   
		 *     @param PB1 Param Boolean 1
		 *   
		 * 
		 *   
		 *     @param PB2 Param Boolean 2
		 *   
		 * 
		 *   
		 *     @param PB3 Param Boolean 3
		 *   
		 * 
		 */
		public ControlMessage(
			String Type,  String PS1,  String PS2,  String PS3,  Integer PI1,  Integer PI2,  Integer PI3,  Double PF1,  Double PF2,  Double PF3,  Boolean PB1,  Boolean PB2,  Boolean PB3
		) {
			
					this.Type = Type;
				
					this.PS1 = PS1;
				
					this.PS2 = PS2;
				
					this.PS3 = PS3;
				
					this.PI1 = PI1;
				
					this.PI2 = PI2;
				
					this.PI3 = PI3;
				
					this.PF1 = PF1;
				
					this.PF2 = PF2;
				
					this.PF3 = PF3;
				
					this.PB1 = PB1;
				
					this.PB2 = PB2;
				
					this.PB3 = PB3;
				
		}
    
    	/**
		 * Cloning constructor from the full message.
		 *
		 * @param original
		 */
		public ControlMessage(ControlMessage original) {		
			
					this.Type = original.getType()
 	;
				
					this.PS1 = original.getPS1()
 	;
				
					this.PS2 = original.getPS2()
 	;
				
					this.PS3 = original.getPS3()
 	;
				
					this.PI1 = original.getPI1()
 	;
				
					this.PI2 = original.getPI2()
 	;
				
					this.PI3 = original.getPI3()
 	;
				
					this.PF1 = original.getPF1()
 	;
				
					this.PF2 = original.getPF2()
 	;
				
					this.PF3 = original.getPF3()
 	;
				
					this.PB1 = original.isPB1()
 	;
				
					this.PB2 = original.isPB2()
 	;
				
					this.PB3 = original.isPB3()
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
         * Custom type of the message. 
         */
        protected
         String Type =
       	null;
	
 		/**
         * Custom type of the message. 
         */
        public  String getType()
 	 {
    					return Type;
    				}
    			
    	
	    /**
         * Param String 1 
         */
        protected
         String PS1 =
       	null;
	
 		/**
         * Param String 1 
         */
        public  String getPS1()
 	 {
    					return PS1;
    				}
    			
    	
	    /**
         * Param String 2 
         */
        protected
         String PS2 =
       	null;
	
 		/**
         * Param String 2 
         */
        public  String getPS2()
 	 {
    					return PS2;
    				}
    			
    	
	    /**
         * Param String 3 
         */
        protected
         String PS3 =
       	null;
	
 		/**
         * Param String 3 
         */
        public  String getPS3()
 	 {
    					return PS3;
    				}
    			
    	
	    /**
         * Param Integer 1 
         */
        protected
         Integer PI1 =
       	null;
	
 		/**
         * Param Integer 1 
         */
        public  Integer getPI1()
 	 {
    					return PI1;
    				}
    			
    	
	    /**
         * Param Integer 2 
         */
        protected
         Integer PI2 =
       	null;
	
 		/**
         * Param Integer 2 
         */
        public  Integer getPI2()
 	 {
    					return PI2;
    				}
    			
    	
	    /**
         * Param Integer 3 
         */
        protected
         Integer PI3 =
       	null;
	
 		/**
         * Param Integer 3 
         */
        public  Integer getPI3()
 	 {
    					return PI3;
    				}
    			
    	
	    /**
         * Param Double 1 
         */
        protected
         Double PF1 =
       	null;
	
 		/**
         * Param Double 1 
         */
        public  Double getPF1()
 	 {
    					return PF1;
    				}
    			
    	
	    /**
         * Param Double 2 
         */
        protected
         Double PF2 =
       	null;
	
 		/**
         * Param Double 2 
         */
        public  Double getPF2()
 	 {
    					return PF2;
    				}
    			
    	
	    /**
         * Param Double 3 
         */
        protected
         Double PF3 =
       	null;
	
 		/**
         * Param Double 3 
         */
        public  Double getPF3()
 	 {
    					return PF3;
    				}
    			
    	
	    /**
         * Param Boolean 1 
         */
        protected
         Boolean PB1 =
       	null;
	
 		/**
         * Param Boolean 1 
         */
        public  Boolean isPB1()
 	 {
    					return PB1;
    				}
    			
    	
	    /**
         * Param Boolean 2 
         */
        protected
         Boolean PB2 =
       	null;
	
 		/**
         * Param Boolean 2 
         */
        public  Boolean isPB2()
 	 {
    					return PB2;
    				}
    			
    	
	    /**
         * Param Boolean 3 
         */
        protected
         Boolean PB3 =
       	null;
	
 		/**
         * Param Boolean 3 
         */
        public  Boolean isPB3()
 	 {
    					return PB3;
    				}
    			
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
		              			"Type = " + String.valueOf(getType()
 	) + " | " + 
		              		
		              			"PS1 = " + String.valueOf(getPS1()
 	) + " | " + 
		              		
		              			"PS2 = " + String.valueOf(getPS2()
 	) + " | " + 
		              		
		              			"PS3 = " + String.valueOf(getPS3()
 	) + " | " + 
		              		
		              			"PI1 = " + String.valueOf(getPI1()
 	) + " | " + 
		              		
		              			"PI2 = " + String.valueOf(getPI2()
 	) + " | " + 
		              		
		              			"PI3 = " + String.valueOf(getPI3()
 	) + " | " + 
		              		
		              			"PF1 = " + String.valueOf(getPF1()
 	) + " | " + 
		              		
		              			"PF2 = " + String.valueOf(getPF2()
 	) + " | " + 
		              		
		              			"PF3 = " + String.valueOf(getPF3()
 	) + " | " + 
		              		
		              			"PB1 = " + String.valueOf(isPB1()
 	) + " | " + 
		              		
		              			"PB2 = " + String.valueOf(isPB2()
 	) + " | " + 
		              		
		              			"PB3 = " + String.valueOf(isPB3()
 	) + " | " + 
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>Type</b> = " + String.valueOf(getType()
 	) + " <br/> " + 
		              		
		              			"<b>PS1</b> = " + String.valueOf(getPS1()
 	) + " <br/> " + 
		              		
		              			"<b>PS2</b> = " + String.valueOf(getPS2()
 	) + " <br/> " + 
		              		
		              			"<b>PS3</b> = " + String.valueOf(getPS3()
 	) + " <br/> " + 
		              		
		              			"<b>PI1</b> = " + String.valueOf(getPI1()
 	) + " <br/> " + 
		              		
		              			"<b>PI2</b> = " + String.valueOf(getPI2()
 	) + " <br/> " + 
		              		
		              			"<b>PI3</b> = " + String.valueOf(getPI3()
 	) + " <br/> " + 
		              		
		              			"<b>PF1</b> = " + String.valueOf(getPF1()
 	) + " <br/> " + 
		              		
		              			"<b>PF2</b> = " + String.valueOf(getPF2()
 	) + " <br/> " + 
		              		
		              			"<b>PF3</b> = " + String.valueOf(getPF3()
 	) + " <br/> " + 
		              		
		              			"<b>PB1</b> = " + String.valueOf(isPB1()
 	) + " <br/> " + 
		              		
		              			"<b>PB2</b> = " + String.valueOf(isPB2()
 	) + " <br/> " + 
		              		
		              			"<b>PB3</b> = " + String.valueOf(isPB3()
 	) + " <br/> " + 
		              		
				"<br/>]";     
		}
 	
 	    public String toJsonLiteral() {
            return "controlmessage( "
            		+
									(getType()
 	 == null ? "null" :
										"\"" + getType()
 	 + "\"" 
									)
								+ ", " + 
									(getPS1()
 	 == null ? "null" :
										"\"" + getPS1()
 	 + "\"" 
									)
								+ ", " + 
									(getPS2()
 	 == null ? "null" :
										"\"" + getPS2()
 	 + "\"" 
									)
								+ ", " + 
									(getPS3()
 	 == null ? "null" :
										"\"" + getPS3()
 	 + "\"" 
									)
								+ ", " + 
								    String.valueOf(getPI1()
 	)									
								+ ", " + 
								    String.valueOf(getPI2()
 	)									
								+ ", " + 
								    String.valueOf(getPI3()
 	)									
								+ ", " + 
								    String.valueOf(getPF1()
 	)									
								+ ", " + 
								    String.valueOf(getPF2()
 	)									
								+ ", " + 
								    String.valueOf(getPF3()
 	)									
								+ ", " + 
								    String.valueOf(isPB1()
 	)									
								+ ", " + 
								    String.valueOf(isPB2()
 	)									
								+ ", " + 
								    String.valueOf(isPB3()
 	)									
								
                   + ")";
        }
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=event+classtype[@name=impl]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=event+classtype[@name=impl]) ---        	            	
 	
		}
 	