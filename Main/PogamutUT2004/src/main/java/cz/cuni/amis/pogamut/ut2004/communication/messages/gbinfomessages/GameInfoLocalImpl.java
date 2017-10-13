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
            				Implementation of the local part of the GameBots2004 message NFO.  
            			
         *
         *  <p></p><p></p>
         *  Complete message documentation:               
         *  
		Asynchronous message. Sent as response to READY command.
		Information about the game. What type of game is it going to be,
		number of teams, maximum size of teams etc.
	
         */
 	public class GameInfoLocalImpl 
  						extends
  						GameInfoLocal
	    {
 	
    	
    	
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public GameInfoLocalImpl()
		{
		}
	
    	/**
		 * Cloning constructor from the full message.
		 *
		 * @param original
		 */
		public GameInfoLocalImpl(GameInfo original) {		
			
			this.SimTime = original.getSimTime();			
		}
		
		/**
		 * Cloning constructor from the full message.
		 *
		 * @param original
		 */
		public GameInfoLocalImpl(GameInfoLocalImpl original) {		
			
			this.SimTime = original.getSimTime();
		}
		
			/**
			 * Cloning constructor from the message part.
			 *
			 * @param original
			 */
			public GameInfoLocalImpl(GameInfoLocal original) {
				
			}
		
						
						public UnrealId getId() {						
							return cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo.GameInfoId;
						}
					
   				
   				@Override
   				public void setSimTime(long SimTime) {
					super.setSimTime(SimTime);
				}
   			
	    				@Override
	    				public 
	    				GameInfoLocalImpl clone() {
	    					return new 
	    					GameInfoLocalImpl(this);
	    				}
	    				
	    				
    	
    	
    	
    	
    	public GameInfoLocalImpl getLocal() {
			return this;
    	}
		public ISharedWorldObject getShared() {
		 	throw new UnsupportedOperationException("Could not return LOCAL as SHARED");
		}
		public IStaticWorldObject getStatic() {
		    throw new UnsupportedOperationException("Could not return LOCAL as STATIC");
		}
 	
		public static class GameInfoLocalUpdate
     implements ILocalWorldObjectUpdatedEvent, IGBWorldObjectEvent
		{
			protected long time;
			
			protected GameInfoLocal data = null; //contains object data for this update
			
			public GameInfoLocalUpdate
    (GameInfoLocal moverLocal, long time)
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
					data = new GameInfoLocalImpl(data); //we always return Impl object
					return new IWorldObjectUpdateResult.WorldObjectUpdateResult<ILocalWorldObject>(IWorldObjectUpdateResult.Result.CREATED, data);
				}
				if ( object instanceof GameInfoLocalImpl )
				{
					GameInfoLocalImpl toUpdate = (GameInfoLocalImpl)object;
					
					boolean updated = false;
					
					// UPDATING LOCAL PROPERTIES
					
					
					data = toUpdate; //the updating has finished
					
					if ( updated )
					{
						toUpdate.SimTime = this.time;
						return new IWorldObjectUpdateResult.WorldObjectUpdateResult<ILocalWorldObject>(IWorldObjectUpdateResult.Result.UPDATED, data);
					}
					
					return new IWorldObjectUpdateResult.WorldObjectUpdateResult<ILocalWorldObject>(IWorldObjectUpdateResult.Result.SAME, data);
				}
				throw new PogamutException("Unsupported object type for update. Expected GameInfoLocalImpl for object " + object.getId() +", not object of class " + object.getClass().getSimpleName() + ".", this);
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
            	
				"]";           		
        }
 	
 		
 		public String toHtmlString() {
 			return super.toString() + "[<br/>" +
            	
				"<br/>]";     
		}
 	
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=local+classtype[@name=impl]) ---
	        
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=local+classtype[@name=impl]) ---        	            	
 	
		}
 	