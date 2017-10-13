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
             				Implementation of the GameBots2004 message MYINV contains also its Local/Shared/Static subpart class definitions..  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		An object in the observed player's inventory.
    
         */
 	public class MyInventoryMessage   
  				extends 
  				MyInventory
  						implements IWorldObjectUpdatedEvent, ICompositeWorldObjectUpdatedEvent
  						
	    {
 	
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public MyInventoryMessage()
		{
		}
	
    	
    	
    	
    	/**
		 * Creates new instance of the message MyInventory.
		 * 
		An object in the observed player's inventory.
    
		 * Corresponding GameBots message
		 *   
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
		 *     @param CurrentAmmo 
			If this item is a weapon, this holds the amount of primary ammo.
		
		 *   
		 * 
		 *   
		 *     @param CurrentAltAmmo 
			If this item is a weapon, this holds the amount of secondary ammo.
		
		 *   
		 * 
		 *   
		 *     @param Amount 
			If this item is ammo or armor, this holds the amount of the item the player has.
		
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
		public MyInventoryMessage(
			UnrealId Id,  int CurrentAmmo,  int CurrentAltAmmo,  int Amount,  ItemType Type,  ItemDescriptor Descriptor
		) {
			
					this.Id = Id;
				
					this.CurrentAmmo = CurrentAmmo;
				
					this.CurrentAltAmmo = CurrentAltAmmo;
				
					this.Amount = Amount;
				
					this.Type = Type;
				
					this.Descriptor = Descriptor;
				
		}
    
		/**
		 * Cloning constructor from the full message.
		 *
		 * @param original
		 */
		public MyInventoryMessage(MyInventoryMessage original) {		
			
					this.Id = original.getId()
 	;
				
					this.CurrentAmmo = original.getCurrentAmmo()
 	;
				
					this.CurrentAltAmmo = original.getCurrentAltAmmo()
 	;
				
					this.Amount = original.getAmount()
 	;
				
					this.Type = original.getType()
 	;
				
					this.Descriptor = original.getDescriptor()
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
			Unique Id of the item. This Id represents the item in the inventory.
		 
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
			Unique Id of the item. This Id represents the item in the inventory.
		 
         */
        public  UnrealId getId()
 	 {
		    					return Id;
		    				}
		    			
    	
	    /**
         * 
			If this item is a weapon, this holds the amount of primary ammo.
		 
         */
        protected
         int CurrentAmmo =
       	0;
	
    						
    						/**
		 					 * Whether property 'CurrentAmmo' was received from GB2004.
		 					 */
							protected boolean CurrentAmmo_Set = false;
							
    						@Override
		    				
 		/**
         * 
			If this item is a weapon, this holds the amount of primary ammo.
		 
         */
        public  int getCurrentAmmo()
 	 {
		    					return CurrentAmmo;
		    				}
		    			
    	
	    /**
         * 
			If this item is a weapon, this holds the amount of secondary ammo.
		 
         */
        protected
         int CurrentAltAmmo =
       	0;
	
    						
    						/**
		 					 * Whether property 'CurrentAltAmmo' was received from GB2004.
		 					 */
							protected boolean CurrentAltAmmo_Set = false;
							
    						@Override
		    				
 		/**
         * 
			If this item is a weapon, this holds the amount of secondary ammo.
		 
         */
        public  int getCurrentAltAmmo()
 	 {
		    					return CurrentAltAmmo;
		    				}
		    			
    	
	    /**
         * 
			If this item is ammo or armor, this holds the amount of the item the player has.
		 
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
			If this item is ammo or armor, this holds the amount of the item the player has.
		 
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
		    			
		    			
		    			private MyInventoryLocal localPart = null;
		    			
		    			@Override
						public MyInventoryLocal 
						getLocal() {
							if (localPart != null) return localPart;
							return localPart = new 
								MyInventoryLocalMessage();
						}
					
						private MyInventoryShared sharedPart = null;
					
						@Override
						public MyInventoryShared 
						getShared() {
							if (sharedPart != null) return sharedPart;							
							return sharedPart = new 
								MyInventorySharedMessage();
						}
					
						private MyInventoryStatic staticPart = null; 
					
						@Override
						public MyInventoryStatic 
						getStatic() {
							if (staticPart != null) return staticPart;
							return staticPart = new 
								MyInventoryStaticMessage();
						}
    				
 		/**
         *  
            				Implementation of the local part of the GameBots2004 message MYINV, used
            				to facade MYINVMessage.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		An object in the observed player's inventory.
    
         */
 	public class MyInventoryLocalMessage 
	  					extends
  						MyInventoryLocal
	    {
 	
		    			@Override
		    			public 
		    			MyInventoryLocalMessage clone() {
		    				return this;
		    			}
		    			
		    				public MyInventoryLocalMessage getLocal() {
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
			Unique Id of the item. This Id represents the item in the inventory.
		 
         */
        public  UnrealId getId()
 	 {
				    					return Id;
				    				}
				    			
 		/**
         * 
			If this item is a weapon, this holds the amount of primary ammo.
		 
         */
        public  int getCurrentAmmo()
 	 {
				    					return CurrentAmmo;
				    				}
				    			
 		/**
         * 
			If this item is a weapon, this holds the amount of secondary ammo.
		 
         */
        public  int getCurrentAltAmmo()
 	 {
				    					return CurrentAltAmmo;
				    				}
				    			
 		/**
         * 
			If this item is ammo or armor, this holds the amount of the item the player has.
		 
         */
        public  int getAmount()
 	 {
				    					return Amount;
				    				}
				    			
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
		              			"Id = " + String.valueOf(getId()
 	) + " | " + 
		              		
		              			"CurrentAmmo = " + String.valueOf(getCurrentAmmo()
 	) + " | " + 
		              		
		              			"CurrentAltAmmo = " + String.valueOf(getCurrentAltAmmo()
 	) + " | " + 
		              		
		              			"Amount = " + String.valueOf(getAmount()
 	) + " | " + 
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>Id</b> = " + String.valueOf(getId()
 	) + " <br/> " + 
		              		
		              			"<b>CurrentAmmo</b> = " + String.valueOf(getCurrentAmmo()
 	) + " <br/> " + 
		              		
		              			"<b>CurrentAltAmmo</b> = " + String.valueOf(getCurrentAltAmmo()
 	) + " <br/> " + 
		              		
		              			"<b>Amount</b> = " + String.valueOf(getAmount()
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
            				Implementation of the static part of the GameBots2004 message MYINV, used
            				to facade MYINVMessage.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		An object in the observed player's inventory.
    
         */
 	public class MyInventoryStaticMessage 
	  					extends
  						MyInventoryStatic
	    {
 	
		    			@Override
		    			public 
		    			MyInventoryStaticMessage clone() {
		    				return this;
		    			}
		    			
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
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=static+classtype[@name=message]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=static+classtype[@name=message]) ---        	            	
 	
		}
 	
 		/**
         *  
            				Implementation of the shared part of the GameBots2004 message MYINV, used
            				to facade MYINVMessage.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		An object in the observed player's inventory.
    
         */
 	public class MyInventorySharedMessage 
	  					extends
  						MyInventoryShared
	    {
 	
    	
    	
		public MyInventorySharedMessage()
		{
			
		}		
    
		    			@Override
		    			public 
		    			MyInventorySharedMessage clone() {
		    				return this;
		    			}
		    			
		
		
		
		protected HashMap<PropertyId, ISharedProperty> propertyMap = new HashMap<PropertyId, ISharedProperty>(
			0
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
			Unique Id of the item. This Id represents the item in the inventory.
		 
         */
        public  UnrealId getId()
 	 {
  			return Id;
  		}
  		
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
		              			"Id = " + String.valueOf(getId()
 	) + " | " + 
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>Id</b> = " + String.valueOf(getId()
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
			if (!( object instanceof MyInventoryMessage) ) {
				throw new PogamutException("Can't update different class than MyInventoryMessage, got class " + object.getClass().getSimpleName() + "!", this);		
			}
			MyInventoryMessage toUpdate = (MyInventoryMessage)object;
			
			boolean updated = false;
			
			// UPDATING LOCAL PROPERTIES
			
				if (toUpdate.CurrentAmmo != getCurrentAmmo()
 	) {
				    toUpdate.CurrentAmmo=getCurrentAmmo()
 	;
					updated = true;
				}
			
				if (toUpdate.CurrentAltAmmo != getCurrentAltAmmo()
 	) {
				    toUpdate.CurrentAltAmmo=getCurrentAltAmmo()
 	;
					updated = true;
				}
			
				if (toUpdate.Amount != getAmount()
 	) {
				    toUpdate.Amount=getAmount()
 	;
					updated = true;
				}
			
         	
         	// UPDATING SHARED PROPERTIES
         	
         	
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
			return new MyInventoryLocalImpl.MyInventoryLocalUpdate
    (this.getLocal(), SimTime);
		}

		@Override
		public ISharedWorldObjectUpdatedEvent getSharedEvent() {
			return new MyInventorySharedImpl.MyInventorySharedUpdate
    (this.getShared(), SimTime, this.getTeamId());
		}

		@Override
		public IStaticWorldObjectUpdatedEvent getStaticEvent() {
			return new MyInventoryStaticImpl.MyInventoryStaticUpdate
    (this.getStatic(), SimTime);
		}
 	
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
		              			"Id = " + String.valueOf(getId()
 	) + " | " + 
		              		
		              			"CurrentAmmo = " + String.valueOf(getCurrentAmmo()
 	) + " | " + 
		              		
		              			"CurrentAltAmmo = " + String.valueOf(getCurrentAltAmmo()
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
		              		
		              			"<b>CurrentAmmo</b> = " + String.valueOf(getCurrentAmmo()
 	) + " <br/> " + 
		              		
		              			"<b>CurrentAltAmmo</b> = " + String.valueOf(getCurrentAltAmmo()
 	) + " <br/> " + 
		              		
		              			"<b>Amount</b> = " + String.valueOf(getAmount()
 	) + " <br/> " + 
		              		
		              			"<b>Type</b> = " + String.valueOf(getType()
 	) + " <br/> " + 
		              		
				"<br/>]";     
		}
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=base+classtype[@name=message]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=base+classtype[@name=message]) ---        	            	
 	
		}
 	