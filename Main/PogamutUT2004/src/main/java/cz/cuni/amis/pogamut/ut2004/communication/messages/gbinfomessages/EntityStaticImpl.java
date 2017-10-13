package cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages;
 		
 		// --- IMPORTS FROM /messages/settings/javasettings/javaimport BEGIN
			import java.util.*;import javax.vecmath.*;import cz.cuni.amis.pogamut.base.communication.messages.*;import cz.cuni.amis.pogamut.base.communication.worldview.*;import cz.cuni.amis.pogamut.base.communication.worldview.event.*;import cz.cuni.amis.pogamut.base.communication.worldview.object.*;import cz.cuni.amis.pogamut.multi.communication.worldview.object.*;import cz.cuni.amis.pogamut.base.communication.translator.event.*;import cz.cuni.amis.pogamut.multi.communication.translator.event.*;import cz.cuni.amis.pogamut.base3d.worldview.object.*;import cz.cuni.amis.pogamut.base3d.worldview.object.event.*;import cz.cuni.amis.pogamut.ut2004.communication.messages.*;import cz.cuni.amis.pogamut.ut2004.communication.worldview.objects.*;import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.objects.*;import cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor.*;import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType.Category;import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;import cz.cuni.amis.utils.exception.*;import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdateResult.Result;import cz.cuni.amis.utils.SafeEquals;import cz.cuni.amis.pogamut.base.agent.*;import cz.cuni.amis.pogamut.multi.agent.*;import cz.cuni.amis.pogamut.multi.communication.worldview.property.*;import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.property.*;import cz.cuni.amis.utils.token.*;import cz.cuni.amis.utils.*;
		// --- IMPORTS FROM /messages/settings/javasettings/javaimport END
		
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name='all'] BEGIN
				
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name='all'] END
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name=static]+classtype[@name=impl] BEGIN
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name=static]+classtype[@name=impl] END
    
 		/**
         *  
            				Implementation of the static part of the GameBots2004 message ENT.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Generic physical entity that may be present in the world. Works as a hook for custom objects, such as new items from EmohawkRPG extension.
	
         */
 	public class EntityStaticImpl 
  						extends
  						EntityStatic
	    {
 	
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public EntityStaticImpl()
		{
		}
	
    	
    	
    	
    	/**
		 * Creates new instance of the message Entity.
		 * 
		Generic physical entity that may be present in the world. Works as a hook for custom objects, such as new items from EmohawkRPG extension.
	
		 * Corresponding GameBots message
		 *   (static part)
		 *   is
		 *   ENT.
		 * 
 	  	 * 
		 *   
		 *     @param Id 
			Unique Id of the item. This Id represents some entity in the world.
		
		 *   
		 * 
		 *   
		 * 
		 *   
		 * 
		 *   
		 * 
		 *   
		 * 
		 *   
		 *     @param Type Type (category) of the entity.
		 *   
		 * 
		 *   
		 *     @param EntityClass Type (category) of the entity.
		 *   
		 * 
		 */
		public EntityStaticImpl(
			UnrealId Id,  String Type,  String EntityClass
		) {
			
					this.Id = Id;
				
					this.Type = Type;
				
					this.EntityClass = EntityClass;
				
		}
    
    	/**
		 * Cloning constructor from the full message.
		 *
		 * @param original
		 */
		public EntityStaticImpl(Entity original) {		
			
					this.Id = original.getId()
 	;
				
					this.Type = original.getType()
 	;
				
					this.EntityClass = original.getEntityClass()
 	;
				
			this.SimTime = original.getSimTime();			
		}
		
		/**
		 * Cloning constructor from the full message.
		 *
		 * @param original
		 */
		public EntityStaticImpl(EntityStaticImpl original) {		
			
					this.Id = original.getId()
 	;
				
					this.Type = original.getType()
 	;
				
					this.EntityClass = original.getEntityClass()
 	;
				
			this.SimTime = original.getSimTime();
		}
		
			/**
			 * Cloning constructor from the message part.
			 *
			 * @param original
			 */
			public EntityStaticImpl(EntityStatic original) {
				
						this.Id = original.getId()
 	;
					
						this.Type = original.getType()
 	;
					
						this.EntityClass = original.getEntityClass()
 	;
					
			}
		
   				
   				@Override
   				public void setSimTime(long SimTime) {
					super.setSimTime(SimTime);
				}
   			
	    				@Override
	    				public 
	    				EntityStaticImpl clone() {
	    					return new 
	    					EntityStaticImpl(this);
	    				}
	    				
	    				
    	
	    /**
         * 
			Unique Id of the item. This Id represents some entity in the world.
		 
         */
        protected
         UnrealId Id =
       	null;
	
 		/**
         * 
			Unique Id of the item. This Id represents some entity in the world.
		 
         */
        public  UnrealId getId()
 	 {
				    					return Id;
				    				}
				    			
    	
	    /**
         * Type (category) of the entity. 
         */
        protected
         String Type =
       	null;
	
 		/**
         * Type (category) of the entity. 
         */
        public  String getType()
 	 {
				    					return Type;
				    				}
				    			
    	
	    /**
         * Type (category) of the entity. 
         */
        protected
         String EntityClass =
       	null;
	
 		/**
         * Type (category) of the entity. 
         */
        public  String getEntityClass()
 	 {
				    					return EntityClass;
				    				}
				    			
    	
    	
    	public static class EntityStaticUpdate
     implements IStaticWorldObjectUpdatedEvent
		{
			
			private EntityStatic data;
			private long time;
			
			public EntityStaticUpdate
    (EntityStatic source, long time)
			{
				this.data = source;
				this.time = time;
			}
			
			/**
			 * Simulation time in MILLI SECONDS !!!
			 */
			@Override
			public long getSimTime() {
				return time;
			}
	
			@Override
			public WorldObjectId getId() {
				return data.getId();
			}
			
			@Override
			public IWorldObjectUpdateResult<IStaticWorldObject> update(
					IStaticWorldObject object) {
				if ( object == null)
				{
					data = new EntityStaticImpl(data);
					return new IWorldObjectUpdateResult.WorldObjectUpdateResult<IStaticWorldObject>(IWorldObjectUpdateResult.Result.CREATED, data);
				}
				//since static objects can't be updated, we'll just check that the object stays the same
				if ( object instanceof EntityStaticImpl)
				{
					EntityStaticImpl orig = (EntityStaticImpl)object;
					//since these errors usually mean error in gamebots, we will just print an error message
					if ( data.isDifferentFrom(orig) )
					{
						//data.isDifferentFrom(orig);
						//throw new PogamutException("Trying to modify static object " + this.data.getId().toString() , this);
						System.out.println("!!!!!ERROR!!!!!! in static object modification. Object class : EntityStaticImpl to see which property was different see !!!!PROPERTY UPDATE ERROR!!!!");
					}
					return new IWorldObjectUpdateResult.WorldObjectUpdateResult<IStaticWorldObject>(IWorldObjectUpdateResult.Result.SAME, data);
				}				
				throw new PogamutException("Unexpected object type for update, EntityStaticImpl expected not class " + object.getClass().getSimpleName() + ".", this);
			}
		}
	
    
 		
 		@Override
 		public boolean isDifferentFrom(IStaticWorldObject other)
 		{
 			if (other == null) //early fail
 			{
 				return true;
 			}
 			else if (other == this) //early out
 			{
 				return false;
 			}
 			else
 			{
 				EntityStatic obj = (EntityStatic) other;

 				
 						if ( !(
 	 			AdvancedEquals.equalsOrNull(this.getId()
 	, obj.getId()
 	)
 	 		) )
						{
							System.out.println("!!!!!PROPERTY UPDATE ERROR!!!! on property Id on object class EntityStatic");
							return true;
						}
 					
 						if ( !(
 	 			AdvancedEquals.equalsOrNull(this.getType()
 	, obj.getType()
 	)
 	 		) )
						{
							System.out.println("!!!!!PROPERTY UPDATE ERROR!!!! on property Type on object class EntityStatic");
							return true;
						}
 					
 						if ( !(
 	 			AdvancedEquals.equalsOrNull(this.getEntityClass()
 	, obj.getEntityClass()
 	)
 	 		) )
						{
							System.out.println("!!!!!PROPERTY UPDATE ERROR!!!! on property EntityClass on object class EntityStatic");
							return true;
						}
 					
 			}
 			return false;
 		}
 	 
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
		              			"Id = " + String.valueOf(getId()
 	) + " | " + 
		              		
		              			"Type = " + String.valueOf(getType()
 	) + " | " + 
		              		
		              			"EntityClass = " + String.valueOf(getEntityClass()
 	) + " | " + 
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>Id</b> = " + String.valueOf(getId()
 	) + " <br/> " + 
		              		
		              			"<b>Type</b> = " + String.valueOf(getType()
 	) + " <br/> " + 
		              		
		              			"<b>EntityClass</b> = " + String.valueOf(getEntityClass()
 	) + " <br/> " + 
		              		
				"<br/>]";     
		}
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=static+classtype[@name=impl]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=static+classtype[@name=impl]) ---        	            	
 	
		}
 	