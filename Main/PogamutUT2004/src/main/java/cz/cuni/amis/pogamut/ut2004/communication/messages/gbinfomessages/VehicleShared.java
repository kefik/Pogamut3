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
            				Abstract definition of the shared part of the GameBots2004 message VEH.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Synchronous message. Holds vehicles we see. Id for vehicles doesn't work, so 
		this message is without Id. We can join vehicles by ENTER command if we are 
		close enough - ussually 100 ut units or less. Note: Vehicle support is 
		in aplha testing right now.
	
         */
 	public abstract class VehicleShared 
  						extends InfoMessage
  						implements ISharedWorldObject
  						
	    		,ILocated
	    		,ILocomotive
	    		,IRotable
	    {
 	
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public VehicleShared()
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
	   	
	    						public static final Token RotationPropertyToken = Tokens.get("Rotation");
	    					
	    						public static final Token LocationPropertyToken = Tokens.get("Location");
	    					
	    						public static final Token VelocityPropertyToken = Tokens.get("Velocity");
	    					
	    						public static final Token TeamPropertyToken = Tokens.get("Team");
	    					
	    						public static final Token HealthPropertyToken = Tokens.get("Health");
	    					
	    						public static final Token ArmorPropertyToken = Tokens.get("Armor");
	    					
	    						public static final Token DriverPropertyToken = Tokens.get("Driver");
	    					
	    						public static final Token TeamLockedPropertyToken = Tokens.get("TeamLocked");
	    						
							
							public static final Set<Token> SharedPropertyTokens;
	
							static {
								Set<Token> tokens = new HashSet<Token>();
								
									tokens.add(RotationPropertyToken);
								
									tokens.add(LocationPropertyToken);
								
									tokens.add(VelocityPropertyToken);
								
									tokens.add(TeamPropertyToken);
								
									tokens.add(HealthPropertyToken);
								
									tokens.add(ArmorPropertyToken);
								
									tokens.add(DriverPropertyToken);
								
									tokens.add(TeamLockedPropertyToken);
								
								SharedPropertyTokens = Collections.unmodifiableSet(tokens);
							}
	    				
	    			
	    				@Override
		    			public abstract 
		    			VehicleShared clone();
		    			
						@Override
						public Class getCompositeClass() {
							return Vehicle.class;
						}
	
						
		    			
 		/**
         * Unique Id of the vehicle or vehicle part. 
         */
        public abstract UnrealId getId()
 	;
		    			
 		/**
         * 
			Which direction the vehicle is facing in absolute terms.
		 
         */
        public abstract Rotation getRotation()
 	;
		    			
 		/**
         * 
			An absolute location of the vehicle within the map.
		 
         */
        public abstract Location getLocation()
 	;
		    			
 		/**
         * 
			Absolute velocity of the vehicle as a vector of movement per one
			game second.
		 
         */
        public abstract Velocity getVelocity()
 	;
		    			
 		/**
         * 
			What team the vehicle is on. 255 is no team. 0-3 are red,
			blue, green, gold in that order.
		 
         */
        public abstract Integer getTeam()
 	;
		    			
 		/**
         * 
			How much health the vehicle has left. Ranges from 0 to x, depending on the vehicle type.
		 
         */
        public abstract Integer getHealth()
 	;
		    			
 		/**
         * 
			How much the vehicle has left. Note: This may be 0 all the time. Maybe the vehicles are not supporting armor.
		 
         */
        public abstract Integer getArmor()
 	;
		    			
 		/**
         * Unique Id of the driver - if any. 
         */
        public abstract UnrealId getDriver()
 	;
		    			
 		/**
         * 
            If the vehicle is locked just for its current team.
         
         */
        public abstract boolean isTeamLocked()
 	;
		    			
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
		              			"Id = " + String.valueOf(getId()
 	) + " | " + 
		              		
		              			"Rotation = " + String.valueOf(getRotation()
 	) + " | " + 
		              		
		              			"Location = " + String.valueOf(getLocation()
 	) + " | " + 
		              		
		              			"Velocity = " + String.valueOf(getVelocity()
 	) + " | " + 
		              		
		              			"Team = " + String.valueOf(getTeam()
 	) + " | " + 
		              		
		              			"Health = " + String.valueOf(getHealth()
 	) + " | " + 
		              		
		              			"Armor = " + String.valueOf(getArmor()
 	) + " | " + 
		              		
		              			"Driver = " + String.valueOf(getDriver()
 	) + " | " + 
		              		
		              			"TeamLocked = " + String.valueOf(isTeamLocked()
 	) + " | " + 
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>Id</b> = " + String.valueOf(getId()
 	) + " <br/> " + 
		              		
		              			"<b>Rotation</b> = " + String.valueOf(getRotation()
 	) + " <br/> " + 
		              		
		              			"<b>Location</b> = " + String.valueOf(getLocation()
 	) + " <br/> " + 
		              		
		              			"<b>Velocity</b> = " + String.valueOf(getVelocity()
 	) + " <br/> " + 
		              		
		              			"<b>Team</b> = " + String.valueOf(getTeam()
 	) + " <br/> " + 
		              		
		              			"<b>Health</b> = " + String.valueOf(getHealth()
 	) + " <br/> " + 
		              		
		              			"<b>Armor</b> = " + String.valueOf(getArmor()
 	) + " <br/> " + 
		              		
		              			"<b>Driver</b> = " + String.valueOf(getDriver()
 	) + " <br/> " + 
		              		
		              			"<b>TeamLocked</b> = " + String.valueOf(isTeamLocked()
 	) + " <br/> " + 
		              		
				"<br/>]";     
		}
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=shared+classtype[@name=abstract]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=shared+classtype[@name=abstract]) ---        	            	
 	
		}
 	