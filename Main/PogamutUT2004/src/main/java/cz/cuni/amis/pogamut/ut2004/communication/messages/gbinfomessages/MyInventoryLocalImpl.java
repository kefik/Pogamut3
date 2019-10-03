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
            				Implementation of the local part of the GameBots2004 message MYINV.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		An object in the observed player's inventory.
    
         */
 	public class MyInventoryLocalImpl 
  						extends
  						MyInventoryLocal
	    {
 	
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public MyInventoryLocalImpl()
		{
		}
	
    	
    	
    	
    	/**
		 * Creates new instance of the message MyInventory.
		 * 
		An object in the observed player's inventory.
    
		 * Corresponding GameBots message
		 *   (local part)
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
		 * 
		 *   
		 * 
		 */
		public MyInventoryLocalImpl(
			UnrealId Id,  int CurrentAmmo,  int CurrentAltAmmo,  int Amount
		) {
			
					this.Id = Id;
				
					this.CurrentAmmo = CurrentAmmo;
				
					this.CurrentAltAmmo = CurrentAltAmmo;
				
					this.Amount = Amount;
				
		}
    
    	/**
		 * Cloning constructor from the full message.
		 *
		 * @param original
		 */
		public MyInventoryLocalImpl(MyInventory original) {		
			
					this.Id = original.getId()
 	;
				
					this.CurrentAmmo = original.getCurrentAmmo()
 	;
				
					this.CurrentAltAmmo = original.getCurrentAltAmmo()
 	;
				
					this.Amount = original.getAmount()
 	;
				
			this.SimTime = original.getSimTime();			
		}
		
		/**
		 * Cloning constructor from the full message.
		 *
		 * @param original
		 */
		public MyInventoryLocalImpl(MyInventoryLocalImpl original) {		
			
					this.Id = original.getId()
 	;
				
					this.CurrentAmmo = original.getCurrentAmmo()
 	;
				
					this.CurrentAltAmmo = original.getCurrentAltAmmo()
 	;
				
					this.Amount = original.getAmount()
 	;
				
			this.SimTime = original.getSimTime();
		}
		
			/**
			 * Cloning constructor from the message part.
			 *
			 * @param original
			 */
			public MyInventoryLocalImpl(MyInventoryLocal original) {
				
						this.Id = original.getId()
 	;
					
						this.CurrentAmmo = original.getCurrentAmmo()
 	;
					
						this.CurrentAltAmmo = original.getCurrentAltAmmo()
 	;
					
						this.Amount = original.getAmount()
 	;
					
			}
		
   				
   				@Override
   				public void setSimTime(long SimTime) {
					super.setSimTime(SimTime);
				}
   			
	    				@Override
	    				public 
	    				MyInventoryLocalImpl clone() {
	    					return new 
	    					MyInventoryLocalImpl(this);
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
			If this item is a weapon, this holds the amount of primary ammo.
		 
         */
        protected
         int CurrentAmmo =
       	0;
	
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
         * 
			If this item is ammo or armor, this holds the amount of the item the player has.
		 
         */
        public  int getAmount()
 	 {
				    					return Amount;
				    				}
				    			
    	
    	
    	
    	
    	public MyInventoryLocalImpl getLocal() {
			return this;
    	}
		public ISharedWorldObject getShared() {
		 	throw new UnsupportedOperationException("Could not return LOCAL as SHARED");
		}
		public IStaticWorldObject getStatic() {
		    throw new UnsupportedOperationException("Could not return LOCAL as STATIC");
		}
 	
		public static class MyInventoryLocalUpdate
     implements ILocalWorldObjectUpdatedEvent, IGBWorldObjectEvent
		{
			protected long time;
			
			protected MyInventoryLocal data = null; //contains object data for this update
			
			public MyInventoryLocalUpdate
    (MyInventoryLocal moverLocal, long time)
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
					data = new MyInventoryLocalImpl(data); //we always return Impl object
					return new IWorldObjectUpdateResult.WorldObjectUpdateResult<ILocalWorldObject>(IWorldObjectUpdateResult.Result.CREATED, data);
				}
				if ( object instanceof MyInventoryLocalImpl )
				{
					MyInventoryLocalImpl toUpdate = (MyInventoryLocalImpl)object;
					
					boolean updated = false;
					
					// UPDATING LOCAL PROPERTIES
					
				if (toUpdate.CurrentAmmo != data.getCurrentAmmo()
 	) {
				    toUpdate.CurrentAmmo=data.getCurrentAmmo()
 	;
					updated = true;
				}
			
				if (toUpdate.CurrentAltAmmo != data.getCurrentAltAmmo()
 	) {
				    toUpdate.CurrentAltAmmo=data.getCurrentAltAmmo()
 	;
					updated = true;
				}
			
				if (toUpdate.Amount != data.getAmount()
 	) {
				    toUpdate.Amount=data.getAmount()
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
				throw new PogamutException("Unsupported object type for update. Expected MyInventoryLocalImpl for object " + object.getId() +", not object of class " + object.getClass().getSimpleName() + ".", this);
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
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=local+classtype[@name=impl]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=local+classtype[@name=impl]) ---        	            	
 	
		}
 	