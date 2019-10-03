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
            				Implementation of the static part of the GameBots2004 message INV.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Synchronous/asynchronous message. An object on the ground that
		can be picked up.
	
         */
 	public class ItemStaticImpl 
  						extends
  						ItemStatic
	    {
 	
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public ItemStaticImpl()
		{
		}
	
    	
    	
    	
    	/**
		 * Creates new instance of the message Item.
		 * 
		Synchronous/asynchronous message. An object on the ground that
		can be picked up.
	
		 * Corresponding GameBots message
		 *   (static part)
		 *   is
		 *   INV.
		 * 
 	  	 * 
		 *   
		 *     @param Id 
			Unique Id of the item. This Id represents just item on the
			map, not in our inventory.
		
		 *   
		 * 
		 *   
		 *     @param NavPointId 
            Id of the navpoint where the item is laying. If null - the item was dropped by the bot or another player.
        
		 *   
		 * 
		 *   
		 *     @param NavPoint 
            If the Item is not dropped then it's item that is laying at some navpoint and
            this is that NavPoint instance.
        
		 *   
		 * 
		 *   
		 * 
		 *   
		 * 
		 *   
		 *     @param Amount 
			If this item is some ammo or health pack, here we can find
			out how much of the attribute this item will add.
		
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
		 *   
		 * 
		 */
		public ItemStaticImpl(
			UnrealId Id,  UnrealId NavPointId,  NavPoint NavPoint,  int Amount,  ItemType Type,  ItemDescriptor Descriptor
		) {
			
					this.Id = Id;
				
					this.NavPointId = NavPointId;
				
					this.NavPoint = NavPoint;
				
					this.Amount = Amount;
				
					this.Type = Type;
				
					this.Descriptor = Descriptor;
				
		}
    
    	/**
		 * Cloning constructor from the full message.
		 *
		 * @param original
		 */
		public ItemStaticImpl(Item original) {		
			
					this.Id = original.getId()
 	;
				
					this.NavPointId = original.getNavPointId()
 	;
				
					this.NavPoint = original.getNavPoint()
 	;
				
					this.Amount = original.getAmount()
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
		public ItemStaticImpl(ItemStaticImpl original) {		
			
					this.Id = original.getId()
 	;
				
					this.NavPointId = original.getNavPointId()
 	;
				
					this.NavPoint = original.getNavPoint()
 	;
				
					this.Amount = original.getAmount()
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
			public ItemStaticImpl(ItemStatic original) {
				
						this.Id = original.getId()
 	;
					
						this.NavPointId = original.getNavPointId()
 	;
					
						this.NavPoint = original.getNavPoint()
 	;
					
						this.Amount = original.getAmount()
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
	    				ItemStaticImpl clone() {
	    					return new 
	    					ItemStaticImpl(this);
	    				}
	    				
	    				
    	
	    /**
         * 
			Unique Id of the item. This Id represents just item on the
			map, not in our inventory.
		 
         */
        protected
         UnrealId Id =
       	null;
	
 		/**
         * 
			Unique Id of the item. This Id represents just item on the
			map, not in our inventory.
		 
         */
        public  UnrealId getId()
 	 {
				    					return Id;
				    				}
				    			
    	
	    /**
         * 
            Id of the navpoint where the item is laying. If null - the item was dropped by the bot or another player.
         
         */
        protected
         UnrealId NavPointId =
       	null;
	
 		/**
         * 
            Id of the navpoint where the item is laying. If null - the item was dropped by the bot or another player.
         
         */
        public  UnrealId getNavPointId()
 	 {
				    					return NavPointId;
				    				}
				    			
    	
	    /**
         * 
            If the Item is not dropped then it's item that is laying at some navpoint and
            this is that NavPoint instance.
         
         */
        protected
         NavPoint NavPoint =
       	null;
	
 		/**
         * 
            If the Item is not dropped then it's item that is laying at some navpoint and
            this is that NavPoint instance.
         
         */
        public  NavPoint getNavPoint()
 	 {
				    					return NavPoint;
				    				}
				    			
    	
	    /**
         * 
			If this item is some ammo or health pack, here we can find
			out how much of the attribute this item will add.
		 
         */
        protected
         int Amount =
       	0;
	
 		/**
         * 
			If this item is some ammo or health pack, here we can find
			out how much of the attribute this item will add.
		 
         */
        public  int getAmount()
 	 {
				    					return Amount;
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
				    			
    	
    	
    	public static class ItemStaticUpdate
     implements IStaticWorldObjectUpdatedEvent
		{
			
			private ItemStatic data;
			private long time;
			
			public ItemStaticUpdate
    (ItemStatic source, long time)
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
					data = new ItemStaticImpl(data);
					return new IWorldObjectUpdateResult.WorldObjectUpdateResult<IStaticWorldObject>(IWorldObjectUpdateResult.Result.CREATED, data);
				}
				//since static objects can't be updated, we'll just check that the object stays the same
				if ( object instanceof ItemStaticImpl)
				{
					ItemStaticImpl orig = (ItemStaticImpl)object;
					//since these errors usually mean error in gamebots, we will just print an error message
					if ( data.isDifferentFrom(orig) )
					{
						//data.isDifferentFrom(orig);
						//throw new PogamutException("Trying to modify static object " + this.data.getId().toString() , this);
						System.out.println("!!!!!ERROR!!!!!! in static object modification. Object class : ItemStaticImpl to see which property was different see !!!!PROPERTY UPDATE ERROR!!!!");
					}
					return new IWorldObjectUpdateResult.WorldObjectUpdateResult<IStaticWorldObject>(IWorldObjectUpdateResult.Result.SAME, data);
				}				
				throw new PogamutException("Unexpected object type for update, ItemStaticImpl expected not class " + object.getClass().getSimpleName() + ".", this);
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
 				ItemStatic obj = (ItemStatic) other;

 				
 						if ( !(
 	 			AdvancedEquals.equalsOrNull(this.getId()
 	, obj.getId()
 	)
 	 		) )
						{
							System.out.println("!!!!!PROPERTY UPDATE ERROR!!!! on property Id on object class ItemStatic");
							return true;
						}
 					
 						if ( !(
 	 			AdvancedEquals.equalsOrNull(this.getNavPointId()
 	, obj.getNavPointId()
 	)
 	 		) )
						{
							System.out.println("!!!!!PROPERTY UPDATE ERROR!!!! on property NavPointId on object class ItemStatic");
							return true;
						}
 					
 						if ( !(
 	 			AdvancedEquals.equalsOrNull(this.getNavPoint()
 	, obj.getNavPoint()
 	)
 	 		) )
						{
							System.out.println("!!!!!PROPERTY UPDATE ERROR!!!! on property NavPoint on object class ItemStatic");
							return true;
						}
 					
 						if ( !(this.getAmount()
 	
 	 			== obj.getAmount()
 	) )
						{
							System.out.println("!!!!!PROPERTY UPDATE ERROR!!!! on property Amount on object class ItemStatic");
							return true;
						}
 					
 						if ( !(
 	 			AdvancedEquals.equalsOrNull(this.getType()
 	, obj.getType()
 	)
 	 		) )
						{
							System.out.println("!!!!!PROPERTY UPDATE ERROR!!!! on property Type on object class ItemStatic");
							return true;
						}
 					
 						if ( !(
 	 			AdvancedEquals.equalsOrNull(this.getDescriptor()
 	, obj.getDescriptor()
 	)
 	 		) )
						{
							System.out.println("!!!!!PROPERTY UPDATE ERROR!!!! on property Descriptor on object class ItemStatic");
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
		              		
		              			"NavPointId = " + String.valueOf(getNavPointId()
 	) + " | " + 
		              		
		              			"Amount = " + String.valueOf(getAmount()
 	) + " | " + 
		              		
		              			"Type = " + String.valueOf(getType()
 	) + " | " + 
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>Id</b> = " + String.valueOf(getId()
 	) + " <br/> " + 
		              		
		              			"<b>NavPointId</b> = " + String.valueOf(getNavPointId()
 	) + " <br/> " + 
		              		
		              			"<b>Amount</b> = " + String.valueOf(getAmount()
 	) + " <br/> " + 
		              		
		              			"<b>Type</b> = " + String.valueOf(getType()
 	) + " <br/> " + 
		              		
				"<br/>]";     
		}
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=static+classtype[@name=impl]) ---
	        
         	  	
         		    
         		    
			         
			         	
         		    
         		    
         	  
			/**
			 * DO NOT USE THIS METHOD! Reserved for GaviaLib (Pogamut core)! It's used
			 * to set correct navpoint instance into the item.
			 */  	
			public void setNavPoint(NavPoint navPoint) {
			    NavPoint = navPoint;
			} 		
		
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=static+classtype[@name=impl]) ---        	            	
 	
		}
 	