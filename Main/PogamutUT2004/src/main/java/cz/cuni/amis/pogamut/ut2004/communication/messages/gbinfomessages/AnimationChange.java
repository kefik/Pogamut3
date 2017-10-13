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
         			Definition of the event CHANGEANIM.
         		
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Asynchronous message. Bot starts or finishes custom played animation. If a list of animation was supported, this
		message will come each time an animation in the list starts or stops playing.
	
         */
 	public class AnimationChange 
  				extends InfoMessage
    			implements IWorldEvent, IWorldChangeEvent
    			
	    {
 	
    	/** Example how the message looks like - used during parser tests. */
    	public static final String PROTOTYPE =
    		"CHANGEANIM {Name text}  {AnimStart False}  {AnimEnd False}  {Time 0} ";
    
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public AnimationChange()
		{
		}
	
    	
    	
    	
    	/**
		 * Creates new instance of the message AnimationChange.
		 * 
		Asynchronous message. Bot starts or finishes custom played animation. If a list of animation was supported, this
		message will come each time an animation in the list starts or stops playing.
	
		 * Corresponding GameBots message
		 *   
		 *   is
		 *   CHANGEANIM.
		 * 
 	  	 * 
		 *   
		 *     @param Name 
			Name of the animation.
		
		 *   
		 * 
		 *   
		 *     @param AnimStart 
			True if this animation has now started.
		
		 *   
		 * 
		 *   
		 *     @param AnimEnd 
			True if this animation has ended.
		
		 *   
		 * 
		 *   
		 *     @param Time 
			Time of the animation change.
		
		 *   
		 * 
		 */
		public AnimationChange(
			String Name,  boolean AnimStart,  boolean AnimEnd,  double Time
		) {
			
					this.Name = Name;
				
					this.AnimStart = AnimStart;
				
					this.AnimEnd = AnimEnd;
				
					this.Time = Time;
				
		}
    
    	/**
		 * Cloning constructor from the full message.
		 *
		 * @param original
		 */
		public AnimationChange(AnimationChange original) {		
			
					this.Name = original.getName()
 	;
				
					this.AnimStart = original.isAnimStart()
 	;
				
					this.AnimEnd = original.isAnimEnd()
 	;
				
					this.Time = original.getTime()
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
         * 
			Name of the animation.
		 
         */
        protected
         String Name =
       	null;
	
 		/**
         * 
			Name of the animation.
		 
         */
        public  String getName()
 	 {
    					return Name;
    				}
    			
    	
	    /**
         * 
			True if this animation has now started.
		 
         */
        protected
         boolean AnimStart =
       	false;
	
 		/**
         * 
			True if this animation has now started.
		 
         */
        public  boolean isAnimStart()
 	 {
    					return AnimStart;
    				}
    			
    	
	    /**
         * 
			True if this animation has ended.
		 
         */
        protected
         boolean AnimEnd =
       	false;
	
 		/**
         * 
			True if this animation has ended.
		 
         */
        public  boolean isAnimEnd()
 	 {
    					return AnimEnd;
    				}
    			
    	
	    /**
         * 
			Time of the animation change.
		 
         */
        protected
         double Time =
       	0;
	
 		/**
         * 
			Time of the animation change.
		 
         */
        public  double getTime()
 	 {
    					return Time;
    				}
    			
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
		              			"Name = " + String.valueOf(getName()
 	) + " | " + 
		              		
		              			"AnimStart = " + String.valueOf(isAnimStart()
 	) + " | " + 
		              		
		              			"AnimEnd = " + String.valueOf(isAnimEnd()
 	) + " | " + 
		              		
		              			"Time = " + String.valueOf(getTime()
 	) + " | " + 
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>Name</b> = " + String.valueOf(getName()
 	) + " <br/> " + 
		              		
		              			"<b>AnimStart</b> = " + String.valueOf(isAnimStart()
 	) + " <br/> " + 
		              		
		              			"<b>AnimEnd</b> = " + String.valueOf(isAnimEnd()
 	) + " <br/> " + 
		              		
		              			"<b>Time</b> = " + String.valueOf(getTime()
 	) + " <br/> " + 
		              		
				"<br/>]";     
		}
 	
 	    public String toJsonLiteral() {
            return "animationchange( "
            		+
									(getName()
 	 == null ? "null" :
										"\"" + getName()
 	 + "\"" 
									)
								+ ", " + 
								    String.valueOf(isAnimStart()
 	)									
								+ ", " + 
								    String.valueOf(isAnimEnd()
 	)									
								+ ", " + 
								    String.valueOf(getTime()
 	)									
								
                   + ")";
        }
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=event+classtype[@name=impl]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=event+classtype[@name=impl]) ---        	            	
 	
		}
 	