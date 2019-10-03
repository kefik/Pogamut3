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
            				Implementation of the static part of the GameBots2004 message MYINV.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		An object in the observed player's inventory.
    
         */
 	public class MyInventoryStaticImpl 
  						extends
  						MyInventoryStatic
	    {
 	
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public MyInventoryStaticImpl()
		{
		}
	
    	
    	
    	
    	/**
		 * Creates new instance of the message MyInventory.
		 * 
		An object in the observed player's inventory.
    
		 * Corresponding GameBots message
		 *   (static part)
		 *   is
		 *   MYINV.
		 * 
 	  	 * 
		 *   
		 *     @param Id 
			Unique Id of the item. This Id represents the item in the inventory.
		
		 *   
		 * 
		 *   
		 * 
		 *   
		 * 
		 *   
		 * 
		 *   
		 *     @param Type 
			Class of the item (e.g. xWeapons.FlakCannonPickup).
		
		 *   
		 * 
		 *   
		 *     @param Descriptor 
			Descriptor of the item - according the ItemType you may cast this to various XYZDescriptor.
		
		 *   
		 * 
		 */
		public MyInventoryStaticImpl(
			UnrealId Id,  ItemType Type,  ItemDescriptor Descriptor
		) {
			
					this.Id = Id;
				
					this.Type = Type;
				
					this.Descriptor = Descriptor;
				
		}
    
    	/**
		 * Cloning constructor from the full message.
		 *
		 * @param original
		 */
		public MyInventoryStaticImpl(MyInventory original) {		
			
					this.Id = original.getId()
 	;
				
					this.Type = original.getType()
 	;
				
					this.Descriptor = original.getDescriptor()
 	;
				
			this.SimTime = original.getSimTime();			
		}
		
		/**
		 * Cloning constructor from the full message.
		 *
		 * @param original
		 */
		public MyInventoryStaticImpl(MyInventoryStaticImpl original) {		
			
					this.Id = original.getId()
 	;
				
					this.Type = original.getType()
 	;
				
					this.Descriptor = original.getDescriptor()
 	;
				
			this.SimTime = original.getSimTime();
		}
		
			/**
			 * Cloning constructor from the message part.
			 *
			 * @param original
			 */
			public MyInventoryStaticImpl(MyInventoryStatic original) {
				
						this.Id = original.getId()
 	;
					
						this.Type = original.getType()
 	;
					
						this.Descriptor = original.getDescriptor()
 	;
					
			}
		
   				
   				@Override
   				public void setSimTime(long SimTime) {
					super.setSimTime(SimTime);
				}
   			
	    				@Override
	    				public 
	    				MyInventoryStaticImpl clone() {
	    					return new 
	    					MyInventoryStaticImpl(this);
	    				}
	    				
	    				
    	
	    /**
         * 
			Unique Id of the item. This Id represents the item in the inventory.
		 
         */
        protected
         UnrealId Id =
       	null;
	
 		/**
         * 
			Unique Id of the item. This Id represents the item in the inventory.
		 
         */
        public  UnrealId getId()
 	 {
				    					return Id;
				    				}
				    			
    	
	    /**
         * 
			Class of the item (e.g. xWeapons.FlakCannonPickup).
		 
         */
        protected
         ItemType Type =
       	null;
	
 		/**
         * 
			Class of the item (e.g. xWeapons.FlakCannonPickup).
		 
         */
        public  ItemType getType()
 	 {
				    					return Type;
				    				}
				    			
    	
	    /**
         * 
			Descriptor of the item - according the ItemType you may cast this to various XYZDescriptor.
		 
         */
        protected
         ItemDescriptor Descriptor =
       	null;
	
 		/**
         * 
			Descriptor of the item - according the ItemType you may cast this to various XYZDescriptor.
		 
         */
        public  ItemDescriptor getDescriptor()
 	 {
				    					return Descriptor;
				    				}
				    			
    	
    	
    	public static class MyInventoryStaticUpdate
     implements IStaticWorldObjectUpdatedEvent
		{
			
			private MyInventoryStatic data;
			private long time;
			
			public MyInventoryStaticUpdate
    (MyInventoryStatic source, long time)
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
					data = new MyInventoryStaticImpl(data);
					return new IWorldObjectUpdateResult.WorldObjectUpdateResult<IStaticWorldObject>(IWorldObjectUpdateResult.Result.CREATED, data);
				}
				//since static objects can't be updated, we'll just check that the object stays the same
				if ( object instanceof MyInventoryStaticImpl)
				{
					MyInventoryStaticImpl orig = (MyInventoryStaticImpl)object;
					//since these errors usually mean error in gamebots, we will just print an error message
					if ( data.isDifferentFrom(orig) )
					{
						//data.isDifferentFrom(orig);
						//throw new PogamutException("Trying to modify static object " + this.data.getId().toString() , this);
						System.out.println("!!!!!ERROR!!!!!! in static object modification. Object class : MyInventoryStaticImpl to see which property was different see !!!!PROPERTY UPDATE ERROR!!!!");
					}
					return new IWorldObjectUpdateResult.WorldObjectUpdateResult<IStaticWorldObject>(IWorldObjectUpdateResult.Result.SAME, data);
				}				
				throw new PogamutException("Unexpected object type for update, MyInventoryStaticImpl expected not class " + object.getClass().getSimpleName() + ".", this);
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
 				MyInventoryStatic obj = (MyInventoryStatic) other;

 				
 						if ( !(
 	 			AdvancedEquals.equalsOrNull(this.getId()
 	, obj.getId()
 	)
 	 		) )
						{
							System.out.println("!!!!!PROPERTY UPDATE ERROR!!!! on property Id on object class MyInventoryStatic");
							return true;
						}
 					
 						if ( !(
 	 			AdvancedEquals.equalsOrNull(this.getType()
 	, obj.getType()
 	)
 	 		) )
						{
							System.out.println("!!!!!PROPERTY UPDATE ERROR!!!! on property Type on object class MyInventoryStatic");
							return true;
						}
 					
 						if ( !(
 	 			AdvancedEquals.equalsOrNull(this.getDescriptor()
 	, obj.getDescriptor()
 	)
 	 		) )
						{
							System.out.println("!!!!!PROPERTY UPDATE ERROR!!!! on property Descriptor on object class MyInventoryStatic");
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
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>Id</b> = " + String.valueOf(getId()
 	) + " <br/> " + 
		              		
		              			"<b>Type</b> = " + String.valueOf(getType()
 	) + " <br/> " + 
		              		
				"<br/>]";     
		}
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=static+classtype[@name=impl]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=static+classtype[@name=impl]) ---        	            	
 	
		}
 	