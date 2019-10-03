package cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages;
 		
 		// --- IMPORTS FROM /messages/settings/javasettings/javaimport BEGIN
			import java.util.*;import javax.vecmath.*;import cz.cuni.amis.pogamut.base.communication.messages.*;import cz.cuni.amis.pogamut.base.communication.worldview.*;import cz.cuni.amis.pogamut.base.communication.worldview.event.*;import cz.cuni.amis.pogamut.base.communication.worldview.object.*;import cz.cuni.amis.pogamut.multi.communication.worldview.object.*;import cz.cuni.amis.pogamut.base.communication.translator.event.*;import cz.cuni.amis.pogamut.multi.communication.translator.event.*;import cz.cuni.amis.pogamut.base3d.worldview.object.*;import cz.cuni.amis.pogamut.base3d.worldview.object.event.*;import cz.cuni.amis.pogamut.ut2004.communication.messages.*;import cz.cuni.amis.pogamut.ut2004.communication.worldview.objects.*;import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.objects.*;import cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor.*;import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType.Category;import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;import cz.cuni.amis.utils.exception.*;import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdateResult.Result;import cz.cuni.amis.utils.SafeEquals;import cz.cuni.amis.pogamut.base.agent.*;import cz.cuni.amis.pogamut.multi.agent.*;import cz.cuni.amis.pogamut.multi.communication.worldview.property.*;import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.property.*;import cz.cuni.amis.utils.token.*;import cz.cuni.amis.utils.*;
		// --- IMPORTS FROM /messages/settings/javasettings/javaimport END
		
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name='all'] BEGIN
				
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name='all'] END
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name=shared]+classtype[@name=abstract] BEGIN
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name=shared]+classtype[@name=abstract] END
    
 		/**
         *  
            				Abstract definition of the shared part of the GameBots2004 message PRJ.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Both asynchronous and synchronous message. Incoming projectile that we can see.
	
         */
 	public abstract class IncomingProjectileShared 
  						extends InfoMessage
  						implements ISharedWorldObject
  						
	    		,ILocated
	    {
 	
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public IncomingProjectileShared()
		{
		}
		
				// abstract definition of the shared-part of the message, no more constructors is needed
			
	   		
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
	   	
	    						public static final Token DirectionPropertyToken = Tokens.get("Direction");
	    					
	    						public static final Token LocationPropertyToken = Tokens.get("Location");
	    					
	    						public static final Token VelocityPropertyToken = Tokens.get("Velocity");
	    					
	    						public static final Token SpeedPropertyToken = Tokens.get("Speed");
	    					
	    						public static final Token OriginPropertyToken = Tokens.get("Origin");
	    					
	    						public static final Token DamageRadiusPropertyToken = Tokens.get("DamageRadius");
	    					
	    						public static final Token TypePropertyToken = Tokens.get("Type");
	    						
							
							public static final Set<Token> SharedPropertyTokens;
	
							static {
								Set<Token> tokens = new HashSet<Token>();
								
									tokens.add(DirectionPropertyToken);
								
									tokens.add(LocationPropertyToken);
								
									tokens.add(VelocityPropertyToken);
								
									tokens.add(SpeedPropertyToken);
								
									tokens.add(OriginPropertyToken);
								
									tokens.add(DamageRadiusPropertyToken);
								
									tokens.add(TypePropertyToken);
								
								SharedPropertyTokens = Collections.unmodifiableSet(tokens);
							}
	    				
	    			
	    				@Override
		    			public abstract 
		    			IncomingProjectileShared clone();
		    			
						@Override
						public Class getCompositeClass() {
							return IncomingProjectile.class;
						}
	
						
		    			
 		/**
         * Unique Id of the projectile. 
         */
        public abstract UnrealId getId()
 	;
		    			
 		/**
         * 
			Which direction projectile is heading to -> orientation
			vector.
		 
         */
        public abstract Vector3d getDirection()
 	;
		    			
 		/**
         * 
			Current location of the projectile.
		 
         */
        public abstract Location getLocation()
 	;
		    			
 		/**
         * 
			Current velocity vector of the projectile.
		 
         */
        public abstract Velocity getVelocity()
 	;
		    			
 		/**
         * 
			Current speed of the projectile.
		 
         */
        public abstract double getSpeed()
 	;
		    			
 		/**
         * 
			Possition of the origin, when combined with direction can
			define the line of fire.
		 
         */
        public abstract Location getOrigin()
 	;
		    			
 		/**
         * 
			If the projectile has splash damage, how big it is – in ut
			units.
		 
         */
        public abstract double getDamageRadius()
 	;
		    			
 		/**
         * 
			The class of the projectile (so you know what is flying
			against you).
		 
         */
        public abstract String getType()
 	;
		    			
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
		              			"Id = " + String.valueOf(getId()
 	) + " | " + 
		              		
		              			"Direction = " + String.valueOf(getDirection()
 	) + " | " + 
		              		
		              			"Location = " + String.valueOf(getLocation()
 	) + " | " + 
		              		
		              			"Velocity = " + String.valueOf(getVelocity()
 	) + " | " + 
		              		
		              			"Speed = " + String.valueOf(getSpeed()
 	) + " | " + 
		              		
		              			"Origin = " + String.valueOf(getOrigin()
 	) + " | " + 
		              		
		              			"DamageRadius = " + String.valueOf(getDamageRadius()
 	) + " | " + 
		              		
		              			"Type = " + String.valueOf(getType()
 	) + " | " + 
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>Id</b> = " + String.valueOf(getId()
 	) + " <br/> " + 
		              		
		              			"<b>Direction</b> = " + String.valueOf(getDirection()
 	) + " <br/> " + 
		              		
		              			"<b>Location</b> = " + String.valueOf(getLocation()
 	) + " <br/> " + 
		              		
		              			"<b>Velocity</b> = " + String.valueOf(getVelocity()
 	) + " <br/> " + 
		              		
		              			"<b>Speed</b> = " + String.valueOf(getSpeed()
 	) + " <br/> " + 
		              		
		              			"<b>Origin</b> = " + String.valueOf(getOrigin()
 	) + " <br/> " + 
		              		
		              			"<b>DamageRadius</b> = " + String.valueOf(getDamageRadius()
 	) + " <br/> " + 
		              		
		              			"<b>Type</b> = " + String.valueOf(getType()
 	) + " <br/> " + 
		              		
				"<br/>]";     
		}
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=shared+classtype[@name=abstract]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=shared+classtype[@name=abstract]) ---        	            	
 	
		}
 	