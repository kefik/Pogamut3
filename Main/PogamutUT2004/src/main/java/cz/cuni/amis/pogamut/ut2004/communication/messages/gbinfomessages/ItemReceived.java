package cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages;
 		
 		// --- IMPORTS FROM /messages/settings/javasettings/javaimport BEGIN
			import java.util.*;import javax.vecmath.*;import cz.cuni.amis.pogamut.base.communication.messages.*;import cz.cuni.amis.pogamut.base.communication.worldview.*;import cz.cuni.amis.pogamut.base.communication.worldview.event.*;import cz.cuni.amis.pogamut.base.communication.worldview.object.*;import cz.cuni.amis.pogamut.multi.communication.worldview.object.*;import cz.cuni.amis.pogamut.base.communication.translator.event.*;import cz.cuni.amis.pogamut.multi.communication.translator.event.*;import cz.cuni.amis.pogamut.base3d.worldview.object.*;import cz.cuni.amis.pogamut.base3d.worldview.object.event.*;import cz.cuni.amis.pogamut.ut2004.communication.messages.*;import cz.cuni.amis.pogamut.ut2004.communication.worldview.objects.*;import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.objects.*;import cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor.*;import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType.Category;import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;import cz.cuni.amis.utils.exception.*;import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdateResult.Result;import cz.cuni.amis.utils.SafeEquals;import cz.cuni.amis.pogamut.base.agent.*;import cz.cuni.amis.pogamut.multi.agent.*;import cz.cuni.amis.pogamut.multi.communication.worldview.property.*;import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.property.*;import cz.cuni.amis.utils.token.*;import cz.cuni.amis.utils.*;
		// --- IMPORTS FROM /messages/settings/javasettings/javaimport END
		
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name='all'] BEGIN
				
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name='all'] END
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name=event]+classtype[@name=impl] BEGIN
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name=event]+classtype[@name=impl] END
    
 		/**
         *  
         			Definition of the event IRC.
         		
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Asynchronous message. Other bot gave us an item. We receive normal IPK message and this message with
		information about the giver and the item.
	
         */
 	public class ItemReceived 
  				extends InfoMessage
    			implements IWorldEvent, IWorldChangeEvent
    			
	    {
 	
    	/** Example how the message looks like - used during parser tests. */
    	public static final String PROTOTYPE =
    		"IRC {Id unreal_id}  {InventoryId unreal_id}  {GiverId unreal_id}  {Location 0,0,0}  {Amount 0}  {Type text}  {Dropped False} ";
    
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public ItemReceived()
		{
		}
	
    	
    	
    	
    	/**
		 * Creates new instance of the message ItemReceived.
		 * 
		Asynchronous message. Other bot gave us an item. We receive normal IPK message and this message with
		information about the giver and the item.
	
		 * Corresponding GameBots message
		 *   
		 *   is
		 *   IRC.
		 * 
 	  	 * 
		 *   
		 *     @param Id 
			Unique Id of the item. This Id represents just item on the
			map, not in our inventory.
		
		 *   
		 * 
		 *   
		 *     @param InventoryId 
			Unique Id of the item in our inventory - use this Id to do changeweapons etc.
			Unreal has different Ids for items in map and actual item in bot's inventory.
			If the item does not go into our inventory nothing will be here (null).
		
		 *   
		 * 
		 *   
		 *     @param GiverId 
			Unique Id of the bot that gave us this item.
		
		 *   
		 * 
		 *   
		 *     @param Location Location of the item.
		 *   
		 * 
		 *   
		 *     @param Amount 
			If this item is some ammo or health pack, here we can find
			out how much of the attribute this item will add. Works also for weapons - will hold primary ammo amount.
		
		 *   
		 * 
		 *   
		 *     @param Type 
			Class of the item.
		
		 *   
		 * 
		 *   
		 *     @param Dropped 
			Whether it is a regular item or dropped by player or bot.
		
		 *   
		 * 
		 */
		public ItemReceived(
			UnrealId Id,  UnrealId InventoryId,  UnrealId GiverId,  Location Location,  int Amount,  String Type,  Boolean Dropped
		) {
			
					this.Id = Id;
				
					this.InventoryId = InventoryId;
				
					this.GiverId = GiverId;
				
					this.Location = Location;
				
					this.Amount = Amount;
				
					this.Type = Type;
				
					this.Dropped = Dropped;
				
		}
    
    	/**
		 * Cloning constructor from the full message.
		 *
		 * @param original
		 */
		public ItemReceived(ItemReceived original) {		
			
					this.Id = original.getId()
 	;
				
					this.InventoryId = original.getInventoryId()
 	;
				
					this.GiverId = original.getGiverId()
 	;
				
					this.Location = original.getLocation()
 	;
				
					this.Amount = original.getAmount()
 	;
				
					this.Type = original.getType()
 	;
				
					this.Dropped = original.isDropped()
 	;
				
			this.SimTime = original.getSimTime();			
		}
		
	   		
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
			Unique Id of the item in our inventory - use this Id to do changeweapons etc.
			Unreal has different Ids for items in map and actual item in bot's inventory.
			If the item does not go into our inventory nothing will be here (null).
		 
         */
        protected
         UnrealId InventoryId =
       	null;
	
 		/**
         * 
			Unique Id of the item in our inventory - use this Id to do changeweapons etc.
			Unreal has different Ids for items in map and actual item in bot's inventory.
			If the item does not go into our inventory nothing will be here (null).
		 
         */
        public  UnrealId getInventoryId()
 	 {
    					return InventoryId;
    				}
    			
    	
	    /**
         * 
			Unique Id of the bot that gave us this item.
		 
         */
        protected
         UnrealId GiverId =
       	null;
	
 		/**
         * 
			Unique Id of the bot that gave us this item.
		 
         */
        public  UnrealId getGiverId()
 	 {
    					return GiverId;
    				}
    			
    	
	    /**
         * Location of the item. 
         */
        protected
         Location Location =
       	null;
	
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
			out how much of the attribute this item will add. Works also for weapons - will hold primary ammo amount.
		 
         */
        protected
         int Amount =
       	0;
	
 		/**
         * 
			If this item is some ammo or health pack, here we can find
			out how much of the attribute this item will add. Works also for weapons - will hold primary ammo amount.
		 
         */
        public  int getAmount()
 	 {
    					return Amount;
    				}
    			
    	
	    /**
         * 
			Class of the item.
		 
         */
        protected
         String Type =
       	null;
	
 		/**
         * 
			Class of the item.
		 
         */
        public  String getType()
 	 {
    					return Type;
    				}
    			
    	
	    /**
         * 
			Whether it is a regular item or dropped by player or bot.
		 
         */
        protected
         Boolean Dropped =
       	null;
	
 		/**
         * 
			Whether it is a regular item or dropped by player or bot.
		 
         */
        public  Boolean isDropped()
 	 {
    					return Dropped;
    				}
    			
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	
		              			"Id = " + String.valueOf(getId()
 	) + " | " + 
		              		
		              			"InventoryId = " + String.valueOf(getInventoryId()
 	) + " | " + 
		              		
		              			"GiverId = " + String.valueOf(getGiverId()
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
		              		
		              			"<b>InventoryId</b> = " + String.valueOf(getInventoryId()
 	) + " <br/> " + 
		              		
		              			"<b>GiverId</b> = " + String.valueOf(getGiverId()
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
 	
 	    public String toJsonLiteral() {
            return "itemreceived( "
            		+
									(getId()
 	 == null ? "null" :
										"\"" + getId()
 	.getStringId() + "\"" 
									)
								+ ", " + 
									(getInventoryId()
 	 == null ? "null" :
										"\"" + getInventoryId()
 	.getStringId() + "\"" 
									)
								+ ", " + 
									(getGiverId()
 	 == null ? "null" :
										"\"" + getGiverId()
 	.getStringId() + "\"" 
									)
								+ ", " + 
								    (getLocation()
 	 == null ? "null" :
										"[" + getLocation()
 	.getX() + ", " + getLocation()
 	.getY() + ", " + getLocation()
 	.getZ() + "]" 
									)
								+ ", " + 
								    String.valueOf(getAmount()
 	)									
								+ ", " + 
									(getType()
 	 == null ? "null" :
										"\"" + getType()
 	 + "\"" 
									)
								+ ", " + 
								    String.valueOf(isDropped()
 	)									
								
                   + ")";
        }
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=event+classtype[@name=impl]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=event+classtype[@name=impl]) ---        	            	
 	
		}
 	