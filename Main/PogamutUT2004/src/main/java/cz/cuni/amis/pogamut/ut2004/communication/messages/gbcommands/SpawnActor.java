
	 	/**
         IMPORTANT !!!

         DO NOT EDIT THIS FILE. IT IS GENERATED FROM approriate xml file in xmlresources/gbcommands BY
         THE JavaClassesGenerator.xslt. MODIFY THAT FILE INSTEAD OF THIS ONE.
         
         Use Ant task process-gb-messages after that to generate .java files again.
         
         IMPORTANT END !!!
        */
 	package cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands;import java.util.*;import javax.vecmath.*;import cz.cuni.amis.pogamut.base.communication.messages.*;import cz.cuni.amis.pogamut.base.communication.worldview.*;import cz.cuni.amis.pogamut.base.communication.worldview.event.*;import cz.cuni.amis.pogamut.base.communication.worldview.object.*;import cz.cuni.amis.pogamut.multi.communication.worldview.object.*;import cz.cuni.amis.pogamut.base.communication.translator.event.*;import cz.cuni.amis.pogamut.multi.communication.translator.event.*;import cz.cuni.amis.pogamut.base3d.worldview.object.*;import cz.cuni.amis.pogamut.base3d.worldview.object.event.*;import cz.cuni.amis.pogamut.ut2004.communication.messages.*;import cz.cuni.amis.pogamut.ut2004.communication.worldview.objects.*;import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.objects.*;import cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor.*;import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType.Category;import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;import cz.cuni.amis.utils.exception.*;import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdateResult.Result;import cz.cuni.amis.utils.SafeEquals;import cz.cuni.amis.pogamut.base.agent.*;import cz.cuni.amis.pogamut.multi.agent.*;import cz.cuni.amis.pogamut.multi.communication.worldview.property.*;import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.property.*;import cz.cuni.amis.utils.token.*;import cz.cuni.amis.utils.*;
 		/**
 		 * Representation of the GameBots2004 command SPAWNACTOR.
 		 *
 		 * 
		Will spawn an actor in the game specified by Type (holds the
		class of the actor). Be carefull with what you spawn and where
		you spawn it. Possible use - for some additional inventory
		spawns.
	
         */
 	public class SpawnActor 
		extends CommandMessage
	        {
	        	
		        
    	/** Example how the message looks like - used during parser tests. */
    	public static final String PROTOTYPE =
    		" {Location 0,0,0}  {Rotation 0,0,0}  {Type text} ";
    
		/**
		 * Creates new instance of command SpawnActor.
		 * 
		Will spawn an actor in the game specified by Type (holds the
		class of the actor). Be carefull with what you spawn and where
		you spawn it. Possible use - for some additional inventory
		spawns.
	
		 * Corresponding GameBots message for this command is
		 * SPAWNACTOR.
		 *
		 * 
		 *    @param Location 
			Location where the actor will be spawned.
		
		 *    @param Rotation Initial rotation of the actor.
		 *    @param Type 
			Holds the desired actor class (e.g.
			xWeapons.FlakCannonPickup).
		
		 */
		public SpawnActor(
			Location Location,  Rotation Rotation,  String Type
		) {
			
				this.Location = Location;
            
				this.Rotation = Rotation;
            
				this.Type = Type;
            
		}

		
			/**
			 * Creates new instance of command SpawnActor.
			 * 
		Will spawn an actor in the game specified by Type (holds the
		class of the actor). Be carefull with what you spawn and where
		you spawn it. Possible use - for some additional inventory
		spawns.
	
			 * Corresponding GameBots message for this command is
			 * SPAWNACTOR.
			 * <p></p>
			 * WARNING: this is empty-command constructor, you have to use setters to fill it up with data that should be sent to GameBots2004!
		     */
		    public SpawnActor() {
		    }
			
		
		/**
		 * Cloning constructor.
		 *
		 * @param original
		 */
		public SpawnActor(SpawnActor original) {
		   
		        this.Location = original.Location;
		   
		        this.Rotation = original.Rotation;
		   
		        this.Type = original.Type;
		   
		}
    
	        /**
	        
			Location where the actor will be spawned.
		 
	        */
	        protected
	         Location Location =
	       	
	        		null
	        	;
	
	        
	        
 		/**
         * 
			Location where the actor will be spawned.
		 
         */
        public Location getLocation()
 	
	        {
	            return
	        	 Location;
	        }
	        
	        
	        
 		
 		/**
         * 
			Location where the actor will be spawned.
		 
         */
        public SpawnActor 
        setLocation(Location Location)
 	
			{
				this.Location = Location;
				return this;
			}
		
	        /**
	        Initial rotation of the actor. 
	        */
	        protected
	         Rotation Rotation =
	       	
	        		null
	        	;
	
	        
	        
 		/**
         * Initial rotation of the actor. 
         */
        public Rotation getRotation()
 	
	        {
	            return
	        	 Rotation;
	        }
	        
	        
	        
 		
 		/**
         * Initial rotation of the actor. 
         */
        public SpawnActor 
        setRotation(Rotation Rotation)
 	
			{
				this.Rotation = Rotation;
				return this;
			}
		
	        /**
	        
			Holds the desired actor class (e.g.
			xWeapons.FlakCannonPickup).
		 
	        */
	        protected
	         String Type =
	       	
	        		null
	        	;
	
	        
	        
 		/**
         * 
			Holds the desired actor class (e.g.
			xWeapons.FlakCannonPickup).
		 
         */
        public String getType()
 	
	        {
	            return
	        	 Type;
	        }
	        
	        
	        
 		
 		/**
         * 
			Holds the desired actor class (e.g.
			xWeapons.FlakCannonPickup).
		 
         */
        public SpawnActor 
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
            	
            	"<b>Location</b> = " +
            	String.valueOf(getLocation()
 	) +
            	" <br/> " +
            	
            	"<b>Rotation</b> = " +
            	String.valueOf(getRotation()
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
     		buf.append("SPAWNACTOR");
     		
					    if (Location != null) {
					        buf.append(" {Location " +
					            Location.getX() + "," +
					            Location.getY() + "," +
					            Location.getZ() + "}");
					    }
					
					    if (Rotation != null) {
					        buf.append(" {Rotation " +
					            Rotation.getPitch() + "," +
					            Rotation.getYaw() + "," +
					            Rotation.getRoll() + "}");
					    }
					
						if (Type != null) {
							buf.append(" {Type " + Type + "}");
						}
					
   			return buf.toString();
   		}
 	
 		// --- Extra Java from XML BEGIN (extra/code/java)
        	
		// --- Extra Java from XML END (extra/code/java)
 	
	        }
    	