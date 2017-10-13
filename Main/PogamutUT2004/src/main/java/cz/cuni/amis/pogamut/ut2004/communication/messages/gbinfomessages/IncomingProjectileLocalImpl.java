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
            				Implementation of the local part of the GameBots2004 message PRJ.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Both asynchronous and synchronous message. Incoming projectile that we can see.
	
         */
 	public class IncomingProjectileLocalImpl 
  						extends
  						IncomingProjectileLocal
	    {
 	
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public IncomingProjectileLocalImpl()
		{
		}
	
    	
    	
    	
    	/**
		 * Creates new instance of the message IncomingProjectile.
		 * 
		Both asynchronous and synchronous message. Incoming projectile that we can see.
	
		 * Corresponding GameBots message
		 *   (local part)
		 *   is
		 *   PRJ.
		 * 
 	  	 * 
		 *   
		 *     @param Id Unique Id of the projectile.
		 *   
		 * 
		 *   
		 *     @param ImpactTime Estimated time till impact.
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
		 *   
		 * 
		 *   
		 * 
		 *   
		 *     @param Visible 
			The class of the projectile (so you know what is flying
			against you).
		
		 *   
		 * 
		 */
		public IncomingProjectileLocalImpl(
			UnrealId Id,  double ImpactTime,  boolean Visible
		) {
			
					this.Id = Id;
				
					this.ImpactTime = ImpactTime;
				
					this.Visible = Visible;
				
		}
    
    	/**
		 * Cloning constructor from the full message.
		 *
		 * @param original
		 */
		public IncomingProjectileLocalImpl(IncomingProjectile original) {		
			
					this.Id = original.getId()
 	;
				
					this.ImpactTime = original.getImpactTime()
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
		public IncomingProjectileLocalImpl(IncomingProjectileLocalImpl original) {		
			
					this.Id = original.getId()
 	;
				
					this.ImpactTime = original.getImpactTime()
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
			public IncomingProjectileLocalImpl(IncomingProjectileLocal original) {
				
						this.Id = original.getId()
 	;
					
						this.ImpactTime = original.getImpactTime()
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
	    				IncomingProjectileLocalImpl clone() {
	    					return new 
	    					IncomingProjectileLocalImpl(this);
	    				}
	    				
	    				
    	
	    /**
         * Unique Id of the projectile. 
         */
        protected
         UnrealId Id =
       	null;
	
 		/**
         * Unique Id of the projectile. 
         */
        public  UnrealId getId()
 	 {
				    					return Id;
				    				}
				    			
    	
	    /**
         * Estimated time till impact. 
         */
        protected
         double ImpactTime =
       	0;
	
 		/**
         * Estimated time till impact. 
         */
        public  double getImpactTime()
 	 {
				    					return ImpactTime;
				    				}
				    			
    	
	    /**
         * 
			The class of the projectile (so you know what is flying
			against you).
		 
         */
        protected
         boolean Visible =
       	true;
	
 		/**
         * 
			The class of the projectile (so you know what is flying
			against you).
		 
         */
        public  boolean isVisible()
 	 {
				    					return Visible;
				    				}
				    			
    	
    	
    	
    	
    	public IncomingProjectileLocalImpl getLocal() {
			return this;
    	}
		public ISharedWorldObject getShared() {
		 	throw new UnsupportedOperationException("Could not return LOCAL as SHARED");
		}
		public IStaticWorldObject getStatic() {
		    throw new UnsupportedOperationException("Could not return LOCAL as STATIC");
		}
 	
		public static class IncomingProjectileLocalUpdate
     implements ILocalWorldObjectUpdatedEvent, IGBWorldObjectEvent
		{
			protected long time;
			
			protected IncomingProjectileLocal data = null; //contains object data for this update
			
			public IncomingProjectileLocalUpdate
    (IncomingProjectileLocal moverLocal, long time)
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
					data = new IncomingProjectileLocalImpl(data); //we always return Impl object
					return new IWorldObjectUpdateResult.WorldObjectUpdateResult<ILocalWorldObject>(IWorldObjectUpdateResult.Result.CREATED, data);
				}
				if ( object instanceof IncomingProjectileLocalImpl )
				{
					IncomingProjectileLocalImpl toUpdate = (IncomingProjectileLocalImpl)object;
					
					boolean updated = false;
					
					// UPDATING LOCAL PROPERTIES
					
				if (toUpdate.ImpactTime != data.getImpactTime()
 	) {
				    toUpdate.ImpactTime=data.getImpactTime()
 	;
					updated = true;
				}
			
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
				throw new PogamutException("Unsupported object type for update. Expected IncomingProjectileLocalImpl for object " + object.getId() +", not object of class " + object.getClass().getSimpleName() + ".", this);
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
		              		
		              			"ImpactTime = " + String.valueOf(getImpactTime()
 	) + " | " + 
		              		
		              			"Visible = " + String.valueOf(isVisible()
 	) + " | " + 
		              		
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
		              			"<b>Id</b> = " + String.valueOf(getId()
 	) + " <br/> " + 
		              		
		              			"<b>ImpactTime</b> = " + String.valueOf(getImpactTime()
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
 	