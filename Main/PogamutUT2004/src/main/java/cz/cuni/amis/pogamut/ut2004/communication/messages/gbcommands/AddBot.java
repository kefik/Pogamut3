
	 	/**
         IMPORTANT !!!

         DO NOT EDIT THIS FILE. IT IS GENERATED FROM approriate xml file in xmlresources/gbcommands BY
         THE JavaClassesGenerator.xslt. MODIFY THAT FILE INSTEAD OF THIS ONE.
         
         Use Ant task process-gb-messages after that to generate .java files again.
         
         IMPORTANT END !!!
        */
 	package cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands;import java.util.*;import javax.vecmath.*;import cz.cuni.amis.pogamut.base.communication.messages.*;import cz.cuni.amis.pogamut.base.communication.worldview.*;import cz.cuni.amis.pogamut.base.communication.worldview.event.*;import cz.cuni.amis.pogamut.base.communication.worldview.object.*;import cz.cuni.amis.pogamut.multi.communication.worldview.object.*;import cz.cuni.amis.pogamut.base.communication.translator.event.*;import cz.cuni.amis.pogamut.multi.communication.translator.event.*;import cz.cuni.amis.pogamut.base3d.worldview.object.*;import cz.cuni.amis.pogamut.base3d.worldview.object.event.*;import cz.cuni.amis.pogamut.ut2004.communication.messages.*;import cz.cuni.amis.pogamut.ut2004.communication.worldview.objects.*;import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.objects.*;import cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor.*;import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType.Category;import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;import cz.cuni.amis.utils.exception.*;import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdateResult.Result;import cz.cuni.amis.utils.SafeEquals;import cz.cuni.amis.pogamut.base.agent.*;import cz.cuni.amis.pogamut.multi.agent.*;import cz.cuni.amis.pogamut.multi.communication.worldview.property.*;import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.property.*;import cz.cuni.amis.utils.token.*;import cz.cuni.amis.utils.*;
 		/**
 		 * Representation of the GameBots2004 command ADDBOT.
 		 *
 		 * 
		Will add original epic bot to a game. May have issues with team
		balancing.
	
         */
 	public class AddBot 
		extends CommandMessage
	        {
	        	
		        
    	/** Example how the message looks like - used during parser tests. */
    	public static final String PROTOTYPE =
    		" {Name text}  {StartLocation 0,0,0}  {StartRotation 0,0,0}  {Skill 0}  {Team 0}  {Type text} ";
    
		/**
		 * Creates new instance of command AddBot.
		 * 
		Will add original epic bot to a game. May have issues with team
		balancing.
	
		 * Corresponding GameBots message for this command is
		 * ADDBOT.
		 *
		 * 
		 *    @param Name Optional name of the bot.
		 *    @param StartLocation 
			Optional start location of the bot.
		
		 *    @param StartRotation 
			Optional start rotation of the bot.
		
		 *    @param Skill 
			Skill of the bot - from 1 to 7 (best).
		
		 *    @param Team 
	Desired team of the of the bot (0 red, 1 blue).
      
		 *    @param Type 
			The class of the added bot - optional.
		
		 */
		public AddBot(
			String Name,  Location StartLocation,  Rotation StartRotation,  Integer Skill,  Integer Team,  String Type
		) {
			
				this.Name = Name;
            
				this.StartLocation = StartLocation;
            
				this.StartRotation = StartRotation;
            
				this.Skill = Skill;
            
				this.Team = Team;
            
				this.Type = Type;
            
		}

		
			/**
			 * Creates new instance of command AddBot.
			 * 
		Will add original epic bot to a game. May have issues with team
		balancing.
	
			 * Corresponding GameBots message for this command is
			 * ADDBOT.
			 * <p></p>
			 * WARNING: this is empty-command constructor, you have to use setters to fill it up with data that should be sent to GameBots2004!
		     */
		    public AddBot() {
		    }
			
		
		/**
		 * Cloning constructor.
		 *
		 * @param original
		 */
		public AddBot(AddBot original) {
		   
		        this.Name = original.Name;
		   
		        this.StartLocation = original.StartLocation;
		   
		        this.StartRotation = original.StartRotation;
		   
		        this.Skill = original.Skill;
		   
		        this.Team = original.Team;
		   
		        this.Type = original.Type;
		   
		}
    
	        /**
	        Optional name of the bot. 
	        */
	        protected
	         String Name =
	       	
	        		null
	        	;
	
	        
	        
 		/**
         * Optional name of the bot. 
         */
        public String getName()
 	
	        {
	            return
	        	 Name;
	        }
	        
	        
	        
 		
 		/**
         * Optional name of the bot. 
         */
        public AddBot 
        setName(String Name)
 	
			{
				this.Name = Name;
				return this;
			}
		
	        /**
	        
			Optional start location of the bot.
		 
	        */
	        protected
	         Location StartLocation =
	       	
	        		null
	        	;
	
	        
	        
 		/**
         * 
			Optional start location of the bot.
		 
         */
        public Location getStartLocation()
 	
	        {
	            return
	        	 StartLocation;
	        }
	        
	        
	        
 		
 		/**
         * 
			Optional start location of the bot.
		 
         */
        public AddBot 
        setStartLocation(Location StartLocation)
 	
			{
				this.StartLocation = StartLocation;
				return this;
			}
		
	        /**
	        
			Optional start rotation of the bot.
		 
	        */
	        protected
	         Rotation StartRotation =
	       	
	        		null
	        	;
	
	        
	        
 		/**
         * 
			Optional start rotation of the bot.
		 
         */
        public Rotation getStartRotation()
 	
	        {
	            return
	        	 StartRotation;
	        }
	        
	        
	        
 		
 		/**
         * 
			Optional start rotation of the bot.
		 
         */
        public AddBot 
        setStartRotation(Rotation StartRotation)
 	
			{
				this.StartRotation = StartRotation;
				return this;
			}
		
	        /**
	        
			Skill of the bot - from 1 to 7 (best).
		 
	        */
	        protected
	         Integer Skill =
	       	3;
	
	        
	        
 		/**
         * 
			Skill of the bot - from 1 to 7 (best).
		 
         */
        public Integer getSkill()
 	
	        {
	            return
	        	 Skill;
	        }
	        
	        
	        
 		
 		/**
         * 
			Skill of the bot - from 1 to 7 (best).
		 
         */
        public AddBot 
        setSkill(Integer Skill)
 	
			{
				this.Skill = Skill;
				return this;
			}
		
	        /**
	        
	Desired team of the of the bot (0 red, 1 blue).
       
	        */
	        protected
	         Integer Team =
	       	
	        		null
	        	;
	
	        
	        
 		/**
         * 
	Desired team of the of the bot (0 red, 1 blue).
       
         */
        public Integer getTeam()
 	
	        {
	            return
	        	 Team;
	        }
	        
	        
	        
 		
 		/**
         * 
	Desired team of the of the bot (0 red, 1 blue).
       
         */
        public AddBot 
        setTeam(Integer Team)
 	
			{
				this.Team = Team;
				return this;
			}
		
	        /**
	        
			The class of the added bot - optional.
		 
	        */
	        protected
	         String Type =
	       	
	        		null
	        	;
	
	        
	        
 		/**
         * 
			The class of the added bot - optional.
		 
         */
        public String getType()
 	
	        {
	            return
	        	 Type;
	        }
	        
	        
	        
 		
 		/**
         * 
			The class of the added bot - optional.
		 
         */
        public AddBot 
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
            	
            	"<b>Name</b> = " +
            	String.valueOf(getName()
 	) +
            	" <br/> " +
            	
            	"<b>StartLocation</b> = " +
            	String.valueOf(getStartLocation()
 	) +
            	" <br/> " +
            	
            	"<b>StartRotation</b> = " +
            	String.valueOf(getStartRotation()
 	) +
            	" <br/> " +
            	
            	"<b>Skill</b> = " +
            	String.valueOf(getSkill()
 	) +
            	" <br/> " +
            	
            	"<b>Team</b> = " +
            	String.valueOf(getTeam()
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
     		buf.append("ADDBOT");
     		
						if (Name != null) {
							buf.append(" {Name " + Name + "}");
						}
					
					    if (StartLocation != null) {
					        buf.append(" {StartLocation " +
					            StartLocation.getX() + "," +
					            StartLocation.getY() + "," +
					            StartLocation.getZ() + "}");
					    }
					
					    if (StartRotation != null) {
					        buf.append(" {StartRotation " +
					            StartRotation.getPitch() + "," +
					            StartRotation.getYaw() + "," +
					            StartRotation.getRoll() + "}");
					    }
					
						if (Skill != null) {
							buf.append(" {Skill " + Skill + "}");
						}
					
						if (Team != null) {
							buf.append(" {Team " + Team + "}");
						}
					
						if (Type != null) {
							buf.append(" {Type " + Type + "}");
						}
					
   			return buf.toString();
   		}
 	
 		// --- Extra Java from XML BEGIN (extra/code/java)
        	
		// --- Extra Java from XML END (extra/code/java)
 	
	        }
    	