package cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages;
 		
 		// --- IMPORTS FROM /messages/settings/javasettings/javaimport BEGIN
			import java.util.*;import javax.vecmath.*;import cz.cuni.amis.pogamut.base.communication.messages.*;import cz.cuni.amis.pogamut.base.communication.worldview.*;import cz.cuni.amis.pogamut.base.communication.worldview.event.*;import cz.cuni.amis.pogamut.base.communication.worldview.object.*;import cz.cuni.amis.pogamut.multi.communication.worldview.object.*;import cz.cuni.amis.pogamut.base.communication.translator.event.*;import cz.cuni.amis.pogamut.multi.communication.translator.event.*;import cz.cuni.amis.pogamut.base3d.worldview.object.*;import cz.cuni.amis.pogamut.base3d.worldview.object.event.*;import cz.cuni.amis.pogamut.ut2004.communication.messages.*;import cz.cuni.amis.pogamut.ut2004.communication.worldview.objects.*;import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.objects.*;import cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor.*;import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType.Category;import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;import cz.cuni.amis.utils.exception.*;import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdateResult.Result;import cz.cuni.amis.utils.SafeEquals;import cz.cuni.amis.pogamut.base.agent.*;import cz.cuni.amis.pogamut.multi.agent.*;import cz.cuni.amis.pogamut.multi.communication.worldview.property.*;import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.property.*;import cz.cuni.amis.utils.token.*;import cz.cuni.amis.utils.*;
		// --- IMPORTS FROM /messages/settings/javasettings/javaimport END
		
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name='all'] BEGIN
				
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name='all'] END
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name=base]+classtype[@name=message] BEGIN
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name=base]+classtype[@name=message] END
    
 		/**
         *  
             				Implementation of the GameBots2004 message INV contains also its Local/Shared/Static subpart class definitions..  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Synchronous/asynchronous message. An object on the ground that
		can be picked up.
	
         */
 	public class ItemMessage   
  				extends 
  				Item
  						implements IWorldObjectUpdatedEvent, ICompositeWorldObjectUpdatedEvent
  						
	    {
 	
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public ItemMessage()
		{
		}
	
    	
    	
    	
    	/**
		 * Creates new instance of the message Item.
		 * 
		Synchronous/asynchronous message. An object on the ground that
		can be picked up.
	
		 * Corresponding GameBots message
		 *   
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
		 *     @param Visible 
			If the item is in the field of view of the bot.
		
		 *   
		 * 
		 *   
		 *     @param Location Location of the item.
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
		 *     @param Dropped 
            Whether it is regular item or one dropped by some bot (usually during dying). Items that are not dropped
            usually respawns itself (depends on the game settings) while those that are dropped may be taken only once.
        
		 *   
		 * 
		 */
		public ItemMessage(
			UnrealId Id,  UnrealId NavPointId,  NavPoint NavPoint,  boolean Visible,  Location Location,  int Amount,  ItemType Type,  ItemDescriptor Descriptor,  boolean Dropped
		) {
			
					this.Id = Id;
				
					this.NavPointId = NavPointId;
				
					this.NavPoint = NavPoint;
				
					this.Visible = Visible;
				
					this.Location = Location;
				
					this.Amount = Amount;
				
					this.Type = Type;
				
					this.Descriptor = Descriptor;
				
					this.Dropped = Dropped;
				
		}
    
		/**
		 * Cloning constructor from the full message.
		 *
		 * @param original
		 */
		public ItemMessage(ItemMessage original) {		
			
					this.Id = original.getId()
 	;
				
					this.NavPointId = original.getNavPointId()
 	;
				
					this.NavPoint = original.getNavPoint()
 	;
				
					this.Visible = original.isVisible()
 	;
				
					this.Location = original.getLocation()
 	;
				
					this.Amount = original.getAmount()
 	;
				
					this.Type = original.getType()
 	;
				
					this.Descriptor = original.getDescriptor()
 	;
				
					this.Dropped = original.isDropped()
 	;
				
				this.TeamId = original.getTeamId();
			
			this.SimTime = original.getSimTime();
		}
		
    				
    					protected ITeamId TeamId;
    					
    					/**
    					 * Used by Yylex to slip corretn TeamId.
    					 */
    					protected void setTeamId(ITeamId TeamId) {
    					    this.TeamId = TeamId;
    					}
    				
    					public ITeamId getTeamId() {
							return TeamId;
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
		 					 * Whether property 'Id' was received from GB2004.
		 					 */
							protected boolean Id_Set = false;
							
    						@Override
		    				
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
		 					 * Whether property 'NavPointId' was received from GB2004.
		 					 */
							protected boolean NavPointId_Set = false;
							
    						@Override
		    				
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
		 					 * Whether property 'NavPoint' was received from GB2004.
		 					 */
							protected boolean NavPoint_Set = false;
							
    						@Override
		    				
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
			If the item is in the field of view of the bot.
		 
         */
        protected
         boolean Visible =
       	false;
	
    						
    						/**
		 					 * Whether property 'Visible' was received from GB2004.
		 					 */
							protected boolean Visible_Set = false;
							
    						@Override
		    				
 		/**
         * 
			If the item is in the field of view of the bot.
		 
         */
        public  boolean isVisible()
 	 {
		    					return Visible;
		    				}
		    			
    	
	    /**
         * Location of the item. 
         */
        protected
         Location Location =
       	null;
	
    						
    						/**
		 					 * Whether property 'Location' was received from GB2004.
		 					 */
							protected boolean Location_Set = false;
							
    						@Override
		    				
 		/**
         * Location of the item. 
         */
        public  Location getLocation()
 	 {
		    					return Location;
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
		 					 * Whether property 'Amount' was received from GB2004.
		 					 */
							protected boolean Amount_Set = false;
							
    						@Override
		    				
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
		 					 * Whether property 'Type' was received from GB2004.
		 					 */
							protected boolean Type_Set = false;
							
    						@Override
		    				
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
		 					 * Whether property 'Descriptor' was received from GB2004.
		 					 */
							protected boolean Descriptor_Set = false;
							
    						@Override
		    				
 		/**
         * 
			Descriptor of the item - according the ItemType you may cast this to various XYZDescriptor. 
		 
         */
        public  ItemDescriptor getDescriptor()
 	 {
		    					return Descriptor;
		    				}
		    			
    	
	    /**
         * 
            Whether it is regular item or one dropped by some bot (usually during dying). Items that are not dropped
            usually respawns itself (depends on the game settings) while those that are dropped may be taken only once.
         
         */
        protected
         boolean Dropped =
       	false;
	
    						
    						/**
		 					 * Whether property 'Dropped' was received from GB2004.
		 					 */
							protected boolean Dropped_Set = false;
							
    						@Override
		    				
 		/**
         * 
            Whether it is regular item or one dropped by some bot (usually during dying). Items that are not dropped
            usually respawns itself (depends on the game settings) while those that are dropped may be taken only once.
         
         */
        public  boolean isDropped()
 	 {
		    					return Dropped;
		    				}
		    			
		    			
		    			private ItemLocal localPart = null;
		    			
		    			@Override
						public ItemLocal 
						getLocal() {
							if (localPart != null) return localPart;
							return localPart = new 
								ItemLocalMessage();
						}
					
						private ItemShared sharedPart = null;
					
						@Override
						public ItemShared 
						getShared() {
							if (sharedPart != null) return sharedPart;							
							return sharedPart = new 
								ItemSharedMessage();
						}
					
						private ItemStatic staticPart = null; 
					
						@Override
						public ItemStatic 
						getStatic() {
							if (staticPart != null) return staticPart;
							return staticPart = new 
								ItemStaticMessage();
						}
    				
 		/**
         *  
            				Implementation of the local part of the GameBots2004 message INV, used
            				to facade INVMessage.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Synchronous/asynchronous message. An object on the ground that
		can be picked up.
	
         */
 	public class ItemLocalMessage 
	  					extends
  						ItemLocal
	    {
 	
		    			@Override
		    			public 
		    			ItemLocalMessage clone() {
		    				return this;
		    			}
		    			
		    				public ItemLocalMessage getLocal() {
								return this;
					    	}
							public ISharedWorldObject getShared() {
							 	throw new UnsupportedOperationException("Could not return LOCAL as SHARED");
							}
							public IStaticWorldObject getStatic() {
							    throw new UnsupportedOperationException("Could not return LOCAL as STATIC");
							}
		    			
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
			If the item is in the field of view of the bot.
		 
         */
        public  boolean isVisible()
 	 {
				    					return Visible;
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
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=local+classtype[@name=message]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=local+classtype[@name=message]) ---        	            	
 	
		}
 	
 		/**
         *  
            				Implementation of the static part of the GameBots2004 message INV, used
            				to facade INVMessage.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Synchronous/asynchronous message. An object on the ground that
		can be picked up.
	
         */
 	public class ItemStaticMessage 
	  					extends
  						ItemStatic
	    {
 	
		    			@Override
		    			public 
		    			ItemStaticMessage clone() {
		    				return this;
		    			}
		    			
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
        public  UnrealId getNavPointId()
 	 {
				    					return NavPointId;
				    				}
				    			
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
        public  int getAmount()
 	 {
				    					return Amount;
				    				}
				    			
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
        public  ItemDescriptor getDescriptor()
 	 {
				    					return Descriptor;
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
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=static+classtype[@name=message]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=static+classtype[@name=message]) ---        	            	
 	
		}
 	
 		/**
         *  
            				Implementation of the shared part of the GameBots2004 message INV, used
            				to facade INVMessage.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Synchronous/asynchronous message. An object on the ground that
		can be picked up.
	
         */
 	public class ItemSharedMessage 
	  					extends
  						ItemShared
	    {
 	
    	
    	
		public ItemSharedMessage()
		{
			
				propertyMap.put(myLocation.getPropertyId(), myLocation);
			
				propertyMap.put(myDropped.getPropertyId(), myDropped);
			
		}		
    
		    			@Override
		    			public 
		    			ItemSharedMessage clone() {
		    				return this;
		    			}
		    			
		
		
		
		protected HashMap<PropertyId, ISharedProperty> propertyMap = new HashMap<PropertyId, ISharedProperty>(
			2
		);
		
		@Override
		public ISharedProperty getProperty(PropertyId id) {
			return propertyMap.get(id);
		}

		@Override
		public Map<PropertyId, ISharedProperty> getProperties() {
			return propertyMap;
		}
	
		
		
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
         * Location of the item. 
         */
        protected
         LocationProperty 
        myLocation
					= new
					LocationProperty
					(
						getId(), 
						"Location", 
						Location, 
						Item.class
					);
					
 		/**
         * Location of the item. 
         */
        public  Location getLocation()
 	 {
			  			return myLocation.getValue();
			  		}
				
    	
	    /**
         * 
            Whether it is regular item or one dropped by some bot (usually during dying). Items that are not dropped
            usually respawns itself (depends on the game settings) while those that are dropped may be taken only once.
         
         */
        protected
         BooleanProperty 
        myDropped
					= new
					BooleanProperty
					(
						getId(), 
						"Dropped", 
						Dropped, 
						Item.class
					);
					
 		/**
         * 
            Whether it is regular item or one dropped by some bot (usually during dying). Items that are not dropped
            usually respawns itself (depends on the game settings) while those that are dropped may be taken only once.
         
         */
        public  boolean isDropped()
 	 {
			  			return myDropped.getValue();
			  		}
				
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
		              			"Id = " + String.valueOf(getId()
 	) + " | " + 
		              		
		              			"Location = " + String.valueOf(getLocation()
 	) + " | " + 
		              		
		              			"Dropped = " + String.valueOf(isDropped()
 	) + " | " + 
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>Id</b> = " + String.valueOf(getId()
 	) + " <br/> " + 
		              		
		              			"<b>Location</b> = " + String.valueOf(getLocation()
 	) + " <br/> " + 
		              		
		              			"<b>Dropped</b> = " + String.valueOf(isDropped()
 	) + " <br/> " + 
		              		
				"<br/>]";     
		}
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=shared+classtype[@name=message]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=shared+classtype[@name=message]) ---        	            	
 	
		}
 	
    	
    	
 	
		@Override
		public IWorldObjectUpdateResult<IWorldObject> update(IWorldObject object) {
			if (object == null)
			{
				return new IWorldObjectUpdateResult.WorldObjectUpdateResult(IWorldObjectUpdateResult.Result.CREATED, this);
			}
			if (!( object instanceof ItemMessage) ) {
				throw new PogamutException("Can't update different class than ItemMessage, got class " + object.getClass().getSimpleName() + "!", this);		
			}
			ItemMessage toUpdate = (ItemMessage)object;
			
			boolean updated = false;
			
			// UPDATING LOCAL PROPERTIES
			
				if (toUpdate.Visible != isVisible()
 	) {
				    toUpdate.Visible=isVisible()
 	;
					updated = true;
				}
			
         	
         	// UPDATING SHARED PROPERTIES
         	
     		if (getLocation()
 	 != null) {
     	
	            if (!SafeEquals.equals(toUpdate.Location, getLocation()
 	)) {
					toUpdate.Location=getLocation()
 	;
					updated = true;
				}
			
     		}
     	
				if (toUpdate.Dropped != isDropped()
 	) {
				    toUpdate.Dropped=isDropped()
 	;
					updated = true;
				}
			
         	
         	// UPDATE TIME
         	toUpdate.SimTime = SimTime;
			
			if (updated) {
				return new IWorldObjectUpdateResult.WorldObjectUpdateResult<IWorldObject>(IWorldObjectUpdateResult.Result.UPDATED, toUpdate);
			} else {
				return new IWorldObjectUpdateResult.WorldObjectUpdateResult<IWorldObject>(IWorldObjectUpdateResult.Result.SAME, toUpdate);
			}
		}
		
		@Override
		public ILocalWorldObjectUpdatedEvent getLocalEvent() {
			return new ItemLocalImpl.ItemLocalUpdate
    (this.getLocal(), SimTime);
		}

		@Override
		public ISharedWorldObjectUpdatedEvent getSharedEvent() {
			return new ItemSharedImpl.ItemSharedUpdate
    (this.getShared(), SimTime, this.getTeamId());
		}

		@Override
		public IStaticWorldObjectUpdatedEvent getStaticEvent() {
			return new ItemStaticImpl.ItemStaticUpdate
    (this.getStatic(), SimTime);
		}
 	
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
		              			"Id = " + String.valueOf(getId()
 	) + " | " + 
		              		
		              			"NavPointId = " + String.valueOf(getNavPointId()
 	) + " | " + 
		              		
		              			"Visible = " + String.valueOf(isVisible()
 	) + " | " + 
		              		
		              			"Location = " + String.valueOf(getLocation()
 	) + " | " + 
		              		
		              			"Amount = " + String.valueOf(getAmount()
 	) + " | " + 
		              		
		              			"Type = " + String.valueOf(getType()
 	) + " | " + 
		              		
		              			"Dropped = " + String.valueOf(isDropped()
 	) + " | " + 
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>Id</b> = " + String.valueOf(getId()
 	) + " <br/> " + 
		              		
		              			"<b>NavPointId</b> = " + String.valueOf(getNavPointId()
 	) + " <br/> " + 
		              		
		              			"<b>Visible</b> = " + String.valueOf(isVisible()
 	) + " <br/> " + 
		              		
		              			"<b>Location</b> = " + String.valueOf(getLocation()
 	) + " <br/> " + 
		              		
		              			"<b>Amount</b> = " + String.valueOf(getAmount()
 	) + " <br/> " + 
		              		
		              			"<b>Type</b> = " + String.valueOf(getType()
 	) + " <br/> " + 
		              		
		              			"<b>Dropped</b> = " + String.valueOf(isDropped()
 	) + " <br/> " + 
		              		
				"<br/>]";     
		}
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=base+classtype[@name=message]) ---
	        
         	  	
         		    
         		    
			         
			         	
         		    
         		    
         	  
			/**
			 * DO NOT USE THIS METHOD! Reserved for GaviaLib (Pogamut core)! It's used
			 * to set correct navpoint instance into the item.
			 */  	
			public void setNavPoint(NavPoint navPoint) {
			    NavPoint = navPoint;
			} 		
		
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=base+classtype[@name=message]) ---        	            	
 	
		}
 	