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
         			Definition of the event INGP.
         		
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Info batch message. Start with SNGP, ends with ENGP. Sent for
		each INAV message at the beginning of the communication. Holds
		information about NavPoint neighbours. This way the reachability
		graph can be exported from UT2004. You should interpret this as
		a DIRECTED EDGE of the navpoint GRAPH that describes the the edge
		from FromNavPoint to ToNavPoint.
	
         */
 	public class NavPointNeighbourLink 
  				extends InfoMessage
    			implements IWorldEvent, IWorldChangeEvent
    			
	    {
 	
    	/** Example how the message looks like - used during parser tests. */
    	public static final String PROTOTYPE =
    		"INGP {Id unreal_id}  {Flags 0}  {CollisionR 0}  {CollisionH 0}  {TranslocZOffset 0}  {TranslocTargetTag text}  {OnlyTranslocator False}  {ForceDoubleJump False}  {NeededJump 0,0,0}  {NeverImpactJump False}  {NoLowGrav False}  {CalculatedGravityZ 0} ";
    
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public NavPointNeighbourLink()
		{
		}
	
    	
    	
    	
    	/**
		 * Creates new instance of the message NavPointNeighbourLink.
		 * 
		Info batch message. Start with SNGP, ends with ENGP. Sent for
		each INAV message at the beginning of the communication. Holds
		information about NavPoint neighbours. This way the reachability
		graph can be exported from UT2004. You should interpret this as
		a DIRECTED EDGE of the navpoint GRAPH that describes the the edge
		from FromNavPoint to ToNavPoint.
	
		 * Corresponding GameBots message
		 *   
		 *   is
		 *   INGP.
		 * 
 	  	 * 
		 *   
		 *     @param Id 
			Unique Id of the NavPoint the link is leading to (end of the link). Always identical to ToNavPoint.getStringId().
		
		 *   
		 * 
		 *   
		 *     @param Flags 
			Holds information about the path from the NavPoint to its
			neighbour that is represented by this message. TODO: see
			reachspecs on UnrealWiki.
		
		 *   
		 * 
		 *   
		 *     @param CollisionR 
			Maximum collision radius of the path between navigation
			points. Bot bigger then this cannot use this path.
		
		 *   
		 * 
		 *   
		 *     @param CollisionH 
			Maximum collision height of the path between navigation
			points. Bot bigger then this cannot use this path.
		
		 *   
		 * 
		 *   
		 *     @param TranslocZOffset 
	       TODO: mystery - we haven't figure it out so far.
	       Can be:
	       a) z-coord of the translocator target
	       b) translocator z-force for the translocator shot
	   
		 *   
		 * 
		 *   
		 *     @param TranslocTargetTag 
           TODO: mystery - we haven't figure it out so far.
           Can be: where you should appear when successfully translocated or where to aim at
       
		 *   
		 * 
		 *   
		 *     @param OnlyTranslocator 
           Whether the translocator is the only way how to traverse this navigation edge.
       
		 *   
		 * 
		 *   
		 *     @param ForceDoubleJump 
           Whether you need to double jump to get to the neighbour navpoint.
       
		 *   
		 * 
		 *   
		 *     @param NeededJump 
           TODO: mystery how to interpret
       
		 *   
		 * 
		 *   
		 *     @param NeverImpactJump 
           TODO: mystery how to interpret
       
		 *   
		 * 
		 *   
		 *     @param NoLowGrav 
           TODO: mystery how to interpret
       
		 *   
		 * 
		 *   
		 *     @param CalculatedGravityZ 
           TODO: mystery how to interpret
       
		 *   
		 * 
		 *   
		 *     @param FromNavPoint 
			Start of the link (edge), where the link originates.
		
		 *   
		 * 
		 *   
		 *     @param ToNavPoint 
            End of the link (edge), where the link ends.
        
		 *   
		 * 
		 */
		public NavPointNeighbourLink(
			UnrealId Id,  int Flags,  int CollisionR,  int CollisionH,  double TranslocZOffset,  String TranslocTargetTag,  boolean OnlyTranslocator,  boolean ForceDoubleJump,  Vector3d NeededJump,  boolean NeverImpactJump,  boolean NoLowGrav,  double CalculatedGravityZ,  NavPoint FromNavPoint,  NavPoint ToNavPoint
		) {
			
					this.Id = Id;
				
					this.Flags = Flags;
				
					this.CollisionR = CollisionR;
				
					this.CollisionH = CollisionH;
				
					this.TranslocZOffset = TranslocZOffset;
				
					this.TranslocTargetTag = TranslocTargetTag;
				
					this.OnlyTranslocator = OnlyTranslocator;
				
					this.ForceDoubleJump = ForceDoubleJump;
				
					this.NeededJump = NeededJump;
				
					this.NeverImpactJump = NeverImpactJump;
				
					this.NoLowGrav = NoLowGrav;
				
					this.CalculatedGravityZ = CalculatedGravityZ;
				
					this.FromNavPoint = FromNavPoint;
				
					this.ToNavPoint = ToNavPoint;
				
		}
    
    	/**
		 * Cloning constructor from the full message.
		 *
		 * @param original
		 */
		public NavPointNeighbourLink(NavPointNeighbourLink original) {		
			
					this.Id = original.getId()
 	;
				
					this.Flags = original.getFlags()
 	;
				
					this.CollisionR = original.getCollisionR()
 	;
				
					this.CollisionH = original.getCollisionH()
 	;
				
					this.TranslocZOffset = original.getTranslocZOffset()
 	;
				
					this.TranslocTargetTag = original.getTranslocTargetTag()
 	;
				
					this.OnlyTranslocator = original.isOnlyTranslocator()
 	;
				
					this.ForceDoubleJump = original.isForceDoubleJump()
 	;
				
					this.NeededJump = original.getNeededJump()
 	;
				
					this.NeverImpactJump = original.isNeverImpactJump()
 	;
				
					this.NoLowGrav = original.isNoLowGrav()
 	;
				
					this.CalculatedGravityZ = original.getCalculatedGravityZ()
 	;
				
					this.FromNavPoint = original.getFromNavPoint()
 	;
				
					this.ToNavPoint = original.getToNavPoint()
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
			Unique Id of the NavPoint the link is leading to (end of the link). Always identical to ToNavPoint.getStringId().
		 
         */
        protected
         UnrealId Id =
       	null;
	
 		/**
         * 
			Unique Id of the NavPoint the link is leading to (end of the link). Always identical to ToNavPoint.getStringId().
		 
         */
        public  UnrealId getId()
 	 {
    					return Id;
    				}
    			
    	
	    /**
         * 
			Holds information about the path from the NavPoint to its
			neighbour that is represented by this message. TODO: see
			reachspecs on UnrealWiki.
		 
         */
        protected
         int Flags =
       	0;
	
 		/**
         * 
			Holds information about the path from the NavPoint to its
			neighbour that is represented by this message. TODO: see
			reachspecs on UnrealWiki.
		 
         */
        public  int getFlags()
 	 {
    					return Flags;
    				}
    			
    	
	    /**
         * 
			Maximum collision radius of the path between navigation
			points. Bot bigger then this cannot use this path.
		 
         */
        protected
         int CollisionR =
       	0;
	
 		/**
         * 
			Maximum collision radius of the path between navigation
			points. Bot bigger then this cannot use this path.
		 
         */
        public  int getCollisionR()
 	 {
    					return CollisionR;
    				}
    			
    	
	    /**
         * 
			Maximum collision height of the path between navigation
			points. Bot bigger then this cannot use this path.
		 
         */
        protected
         int CollisionH =
       	0;
	
 		/**
         * 
			Maximum collision height of the path between navigation
			points. Bot bigger then this cannot use this path.
		 
         */
        public  int getCollisionH()
 	 {
    					return CollisionH;
    				}
    			
    	
	    /**
         * 
	       TODO: mystery - we haven't figure it out so far.
	       Can be:
	       a) z-coord of the translocator target
	       b) translocator z-force for the translocator shot
	    
         */
        protected
         double TranslocZOffset =
       	0;
	
 		/**
         * 
	       TODO: mystery - we haven't figure it out so far.
	       Can be:
	       a) z-coord of the translocator target
	       b) translocator z-force for the translocator shot
	    
         */
        public  double getTranslocZOffset()
 	 {
    					return TranslocZOffset;
    				}
    			
    	
	    /**
         * 
           TODO: mystery - we haven't figure it out so far.
           Can be: where you should appear when successfully translocated or where to aim at
        
         */
        protected
         String TranslocTargetTag =
       	null;
	
 		/**
         * 
           TODO: mystery - we haven't figure it out so far.
           Can be: where you should appear when successfully translocated or where to aim at
        
         */
        public  String getTranslocTargetTag()
 	 {
    					return TranslocTargetTag;
    				}
    			
    	
	    /**
         * 
           Whether the translocator is the only way how to traverse this navigation edge.
        
         */
        protected
         boolean OnlyTranslocator =
       	false;
	
 		/**
         * 
           Whether the translocator is the only way how to traverse this navigation edge.
        
         */
        public  boolean isOnlyTranslocator()
 	 {
    					return OnlyTranslocator;
    				}
    			
    	
	    /**
         * 
           Whether you need to double jump to get to the neighbour navpoint.
        
         */
        protected
         boolean ForceDoubleJump =
       	false;
	
 		/**
         * 
           Whether you need to double jump to get to the neighbour navpoint.
        
         */
        public  boolean isForceDoubleJump()
 	 {
    					return ForceDoubleJump;
    				}
    			
    	
	    /**
         * 
           TODO: mystery how to interpret
        
         */
        protected
         Vector3d NeededJump =
       	null;
	
 		/**
         * 
           TODO: mystery how to interpret
        
         */
        public  Vector3d getNeededJump()
 	 {
    					return NeededJump;
    				}
    			
    	
	    /**
         * 
           TODO: mystery how to interpret
        
         */
        protected
         boolean NeverImpactJump =
       	false;
	
 		/**
         * 
           TODO: mystery how to interpret
        
         */
        public  boolean isNeverImpactJump()
 	 {
    					return NeverImpactJump;
    				}
    			
    	
	    /**
         * 
           TODO: mystery how to interpret
        
         */
        protected
         boolean NoLowGrav =
       	false;
	
 		/**
         * 
           TODO: mystery how to interpret
        
         */
        public  boolean isNoLowGrav()
 	 {
    					return NoLowGrav;
    				}
    			
    	
	    /**
         * 
           TODO: mystery how to interpret
        
         */
        protected
         double CalculatedGravityZ =
       	0;
	
 		/**
         * 
           TODO: mystery how to interpret
        
         */
        public  double getCalculatedGravityZ()
 	 {
    					return CalculatedGravityZ;
    				}
    			
    	
	    /**
         * 
			Start of the link (edge), where the link originates.
		 
         */
        protected
         NavPoint FromNavPoint =
       	null;
	
 		/**
         * 
			Start of the link (edge), where the link originates.
		 
         */
        public  NavPoint getFromNavPoint()
 	 {
    					return FromNavPoint;
    				}
    			
    	
	    /**
         * 
            End of the link (edge), where the link ends.
         
         */
        protected
         NavPoint ToNavPoint =
       	null;
	
 		/**
         * 
            End of the link (edge), where the link ends.
         
         */
        public  NavPoint getToNavPoint()
 	 {
    					return ToNavPoint;
    				}
    			
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
		              			"Id = " + String.valueOf(getId()
 	) + " | " + 
		              		
		              			"Flags = " + String.valueOf(getFlags()
 	) + " | " + 
		              		
		              			"CollisionR = " + String.valueOf(getCollisionR()
 	) + " | " + 
		              		
		              			"CollisionH = " + String.valueOf(getCollisionH()
 	) + " | " + 
		              		
		              			"TranslocZOffset = " + String.valueOf(getTranslocZOffset()
 	) + " | " + 
		              		
		              			"TranslocTargetTag = " + String.valueOf(getTranslocTargetTag()
 	) + " | " + 
		              		
		              			"OnlyTranslocator = " + String.valueOf(isOnlyTranslocator()
 	) + " | " + 
		              		
		              			"ForceDoubleJump = " + String.valueOf(isForceDoubleJump()
 	) + " | " + 
		              		
		              			"NeededJump = " + String.valueOf(getNeededJump()
 	) + " | " + 
		              		
		              			"NeverImpactJump = " + String.valueOf(isNeverImpactJump()
 	) + " | " + 
		              		
		              			"NoLowGrav = " + String.valueOf(isNoLowGrav()
 	) + " | " + 
		              		
		              			"CalculatedGravityZ = " + String.valueOf(getCalculatedGravityZ()
 	) + " | " + 
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>Id</b> = " + String.valueOf(getId()
 	) + " <br/> " + 
		              		
		              			"<b>Flags</b> = " + String.valueOf(getFlags()
 	) + " <br/> " + 
		              		
		              			"<b>CollisionR</b> = " + String.valueOf(getCollisionR()
 	) + " <br/> " + 
		              		
		              			"<b>CollisionH</b> = " + String.valueOf(getCollisionH()
 	) + " <br/> " + 
		              		
		              			"<b>TranslocZOffset</b> = " + String.valueOf(getTranslocZOffset()
 	) + " <br/> " + 
		              		
		              			"<b>TranslocTargetTag</b> = " + String.valueOf(getTranslocTargetTag()
 	) + " <br/> " + 
		              		
		              			"<b>OnlyTranslocator</b> = " + String.valueOf(isOnlyTranslocator()
 	) + " <br/> " + 
		              		
		              			"<b>ForceDoubleJump</b> = " + String.valueOf(isForceDoubleJump()
 	) + " <br/> " + 
		              		
		              			"<b>NeededJump</b> = " + String.valueOf(getNeededJump()
 	) + " <br/> " + 
		              		
		              			"<b>NeverImpactJump</b> = " + String.valueOf(isNeverImpactJump()
 	) + " <br/> " + 
		              		
		              			"<b>NoLowGrav</b> = " + String.valueOf(isNoLowGrav()
 	) + " <br/> " + 
		              		
		              			"<b>CalculatedGravityZ</b> = " + String.valueOf(getCalculatedGravityZ()
 	) + " <br/> " + 
		              		
				"<br/>]";     
		}
 	
 	    public String toJsonLiteral() {
            return "navpointneighbourlink( "
            		+
									(getId()
 	 == null ? "null" :
										"\"" + getId()
 	.getStringId() + "\"" 
									)
								+ ", " + 
								    String.valueOf(getFlags()
 	)									
								+ ", " + 
								    String.valueOf(getCollisionR()
 	)									
								+ ", " + 
								    String.valueOf(getCollisionH()
 	)									
								+ ", " + 
								    String.valueOf(getTranslocZOffset()
 	)									
								+ ", " + 
									(getTranslocTargetTag()
 	 == null ? "null" :
										"\"" + getTranslocTargetTag()
 	 + "\"" 
									)
								+ ", " + 
								    String.valueOf(isOnlyTranslocator()
 	)									
								+ ", " + 
								    String.valueOf(isForceDoubleJump()
 	)									
								+ ", " + 
									(getNeededJump()
 	 == null ? "null" :
										"[" + getNeededJump()
 	.getX() + ", " + getNeededJump()
 	.getY() + ", " + getNeededJump()
 	.getZ() + "]" 
									)
								+ ", " + 
								    String.valueOf(isNeverImpactJump()
 	)									
								+ ", " + 
								    String.valueOf(isNoLowGrav()
 	)									
								+ ", " + 
								    String.valueOf(getCalculatedGravityZ()
 	)									
								
                   + ")";
        }
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
         	  
         		    
         	  
            /**
             * Use to fill missing fields of the Item when creating MapObtained event before
             * INIT command is requested.
             */ 
            public NavPointNeighbourLink(
              NavPointNeighbourLink orig, 
              NavPoint from,
              NavPoint to
            ) {
                this(orig);
                this.FromNavPoint = from;
                this.ToNavPoint = to;
            }   
		
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=event+classtype[@name=impl]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=event+classtype[@name=impl]) ---        	            	
 	
		}
 	