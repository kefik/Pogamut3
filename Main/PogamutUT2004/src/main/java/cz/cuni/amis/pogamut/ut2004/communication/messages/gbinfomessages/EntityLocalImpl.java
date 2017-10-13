package cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages;
 		
 		// --- IMPORTS FROM /messages/settings/javasettings/javaimport BEGIN
			import java.util.*;import javax.vecmath.*;import cz.cuni.amis.pogamut.base.communication.messages.*;import cz.cuni.amis.pogamut.base.communication.worldview.*;import cz.cuni.amis.pogamut.base.communication.worldview.event.*;import cz.cuni.amis.pogamut.base.communication.worldview.object.*;import cz.cuni.amis.pogamut.multi.communication.worldview.object.*;import cz.cuni.amis.pogamut.base.communication.translator.event.*;import cz.cuni.amis.pogamut.multi.communication.translator.event.*;import cz.cuni.amis.pogamut.base3d.worldview.object.*;import cz.cuni.amis.pogamut.base3d.worldview.object.event.*;import cz.cuni.amis.pogamut.ut2004.communication.messages.*;import cz.cuni.amis.pogamut.ut2004.communication.worldview.objects.*;import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.objects.*;import cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor.*;import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType.Category;import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;import cz.cuni.amis.utils.exception.*;import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdateResult.Result;import cz.cuni.amis.utils.SafeEquals;import cz.cuni.amis.pogamut.base.agent.*;import cz.cuni.amis.pogamut.multi.agent.*;import cz.cuni.amis.pogamut.multi.communication.worldview.property.*;import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.property.*;import cz.cuni.amis.utils.token.*;import cz.cuni.amis.utils.*;
		// --- IMPORTS FROM /messages/settings/javasettings/javaimport END
		
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name='all'] BEGIN
				
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name='all'] END
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name=local]+classtype[@name=impl] BEGIN
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name=local]+classtype[@name=impl] END
    
 		/**
         *  
            				Implementation of the local part of the GameBots2004 message ENT.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Generic physical entity that may be present in the world. Works as a hook for custom objects, such as new items from EmohawkRPG extension.
	
         */
 	public class EntityLocalImpl 
  						extends
  						EntityLocal
	    {
 	
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public EntityLocalImpl()
		{
		}
	
    	
    	
    	
    	/**
		 * Creates new instance of the message Entity.
		 * 
		Generic physical entity that may be present in the world. Works as a hook for custom objects, such as new items from EmohawkRPG extension.
	
		 * Corresponding GameBots message
		 *   (local part)
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
		 *     @param Visible 
			If the item is in the field of view of the bot.
		
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
		 * 
		 */
		public EntityLocalImpl(
			UnrealId Id,  boolean Visible
		) {
			
					this.Id = Id;
				
					this.Visible = Visible;
				
		}
    
    	/**
		 * Cloning constructor from the full message.
		 *
		 * @param original
		 */
		public EntityLocalImpl(Entity original) {		
			
					this.Id = original.getId()
 	;
				
					this.Visible = original.isVisible()
 	;
				
			this.SimTime = original.getSimTime();			
		}
		
		/**
		 * Cloning constructor from the full message.
		 *
		 * @param original
		 */
		public EntityLocalImpl(EntityLocalImpl original) {		
			
					this.Id = original.getId()
 	;
				
					this.Visible = original.isVisible()
 	;
				
			this.SimTime = original.getSimTime();
		}
		
			/**
			 * Cloning constructor from the message part.
			 *
			 * @param original
			 */
			public EntityLocalImpl(EntityLocal original) {
				
						this.Id = original.getId()
 	;
					
						this.Visible = original.isVisible()
 	;
					
			}
		
   				
   				@Override
   				public void setSimTime(long SimTime) {
					super.setSimTime(SimTime);
				}
   			
	    				@Override
	    				public 
	    				EntityLocalImpl clone() {
	    					return new 
	    					EntityLocalImpl(this);
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
         * 
			If the item is in the field of view of the bot.
		 
         */
        protected
         boolean Visible =
       	false;
	
 		/**
         * 
			If the item is in the field of view of the bot.
		 
         */
        public  boolean isVisible()
 	 {
				    					return Visible;
				    				}
				    			
    	
    	
    	
    	
    	public EntityLocalImpl getLocal() {
			return this;
    	}
		public ISharedWorldObject getShared() {
		 	throw new UnsupportedOperationException("Could not return LOCAL as SHARED");
		}
		public IStaticWorldObject getStatic() {
		    throw new UnsupportedOperationException("Could not return LOCAL as STATIC");
		}
 	
		public static class EntityLocalUpdate
     implements ILocalWorldObjectUpdatedEvent, IGBWorldObjectEvent
		{
			protected long time;
			
			protected EntityLocal data = null; //contains object data for this update
			
			public EntityLocalUpdate
    (EntityLocal moverLocal, long time)
			{
				this.data = moverLocal;
				this.time = time;
			}
			
			@Override
			public IWorldObjectUpdateResult<ILocalWorldObject> update(
					ILocalWorldObject object) 
			{
				if ( object == null)
				{
					data = new EntityLocalImpl(data); //we always return Impl object
					return new IWorldObjectUpdateResult.WorldObjectUpdateResult<ILocalWorldObject>(IWorldObjectUpdateResult.Result.CREATED, data);
				}
				if ( object instanceof EntityLocalImpl )
				{
					EntityLocalImpl toUpdate = (EntityLocalImpl)object;
					
					boolean updated = false;
					
					// UPDATING LOCAL PROPERTIES
					
				if (toUpdate.Visible != data.isVisible()
 	) {
				    toUpdate.Visible=data.isVisible()
 	;
					updated = true;
				}
			
					
					data = toUpdate; //the updating has finished
					
					if ( updated )
					{
						toUpdate.SimTime = this.time;
						return new IWorldObjectUpdateResult.WorldObjectUpdateResult<ILocalWorldObject>(IWorldObjectUpdateResult.Result.UPDATED, data);
					}
					
					return new IWorldObjectUpdateResult.WorldObjectUpdateResult<ILocalWorldObject>(IWorldObjectUpdateResult.Result.SAME, data);
				}
				throw new PogamutException("Unsupported object type for update. Expected EntityLocalImpl for object " + object.getId() +", not object of class " + object.getClass().getSimpleName() + ".", this);
			}
	
			/**
			 * Simulation time in MILLI SECONDS !!!
			 */
			@Override
			public long getSimTime() {
				return this.time;
			}
	
			@Override
			public IWorldObject getObject() {
				return data;
			}
	
			@Override
			public WorldObjectId getId() {
				return data.getId();
			}
			
		}	
 	
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
		              			"Id = " + String.valueOf(getId()
 	) + " | " + 
		              		
		              			"Visible = " + String.valueOf(isVisible()
 	) + " | " + 
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>Id</b> = " + String.valueOf(getId()
 	) + " <br/> " + 
		              		
		              			"<b>Visible</b> = " + String.valueOf(isVisible()
 	) + " <br/> " + 
		              		
				"<br/>]";     
		}
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=local+classtype[@name=impl]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=local+classtype[@name=impl]) ---        	            	
 	
		}
 	