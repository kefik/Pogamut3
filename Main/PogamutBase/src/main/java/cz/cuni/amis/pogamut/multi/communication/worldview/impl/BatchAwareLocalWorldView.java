package cz.cuni.amis.pogamut.multi.communication.worldview.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;

import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectDestroyedEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectFirstEncounteredEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.base.component.bus.exception.ComponentNotRunningException;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencies;
import cz.cuni.amis.pogamut.base.component.lifecyclebus.ILifecycleBus;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.pogamut.base3d.worldview.object.IViewable;
import cz.cuni.amis.pogamut.base3d.worldview.object.event.WorldObjectAppearedEvent;
import cz.cuni.amis.pogamut.multi.agent.ITeamedAgentId;
import cz.cuni.amis.pogamut.multi.communication.messages.SharedBatchBeginEvent;
import cz.cuni.amis.pogamut.multi.communication.messages.SharedBatchFinishedEvent;
import cz.cuni.amis.pogamut.multi.communication.translator.event.ICompositeWorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.multi.communication.translator.event.ILocalWorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.multi.communication.translator.event.ISharedWorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.multi.communication.translator.event.IStaticWorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.multi.communication.worldview.ISharedWorldView;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ICompositeWorldObject;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ILocalViewable;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ILocalWorldObject;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.event.DummyObjectEvent;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.event.DummyObjectEvent.EventType;
import cz.cuni.amis.pogamut.multi.utils.timekey.TimeKey;
import cz.cuni.amis.utils.NullCheck;
import cz.cuni.amis.utils.exception.PogamutInterruptedException;

/**
 * Implements the batch logic into the worldView.
 * @author srlok
 *
 */
public abstract class BatchAwareLocalWorldView extends VisionLocalWorldView {

	public BatchAwareLocalWorldView(ComponentDependencies dependencies,
			ILifecycleBus bus, IAgentLogger logger,
			ISharedWorldView parentWorldView, ITeamedAgentId agentId) {
		super(dependencies, bus, logger, parentWorldView, agentId);
		objectMutex = new Object();
	}

	/**
	 * Queue of all incoming batches ready to be processed.
	 */
	private Queue<List<IWorldChangeEvent>> batches = new LinkedBlockingQueue<List<IWorldChangeEvent>>();
	
	/**
	 * The current(incomplete) batch of events waiting to be processed.
	 */
	private List<IWorldChangeEvent> currentBatch = new LinkedList<IWorldChangeEvent>();
	
	/**
	 * Used to identify events marking beginning of batches. Override to provide correct behavior.
	 * @param event
	 * @return
	 */
	protected abstract boolean isBatchBeginEvent( IWorldChangeEvent event );
	
	/**
	 * Used to detect batch end events, needs to be overriden to detect the events properly.
	 * @param event
	 * @return
	 */
	protected abstract boolean isBatchEndEvent( IWorldChangeEvent event );
	
	/**
	 * This means, that lock was requested by a thread and it is waiting for finishing the batch.
	 */
	private boolean lockRequested = false;
	
	private boolean lockFinished = false;
	
	
	/**
	 * This means, that we are waiting for shared worldView to process all events from current batch (between current begin event and current end event)
	 */
	private boolean waitingForSharedBatch = false; 
	
	/**
	 * The lock for a time is set when the sharedWorldView has been sent a BatchBeginMessage, but it has not yet confirmed that
	 * all events for the specified time have been processed.
	 */
	private Set<Long> sharedWVLocks = Collections.synchronizedSet( new HashSet<Long>(4));
	
	private boolean timeKeyIncreased = false;
	
	private Object objectMutex = new Object();	
	private CountDownLatch latch = new CountDownLatch(1);
	
	/**
	 * These keys are currently locked (shadowCopies are held)
	 */
	private List<Long> lockedTimes = new LinkedList<Long>();
	
	/**
	 * Notifies sharedWorldView that a beginEvent has been recieved with with the specified time and the sharedWorldView should notify
	 * this worldView back, when all events for the time have been processed.
	 * @param time
	 */
	protected synchronized void notifySharedBegin( long time )
	{
		log.finer("Notifying sharedWorldView with SharedBegin event of time : " + time);
		sharedWorldView.notify( new SharedBatchBeginEvent(time, this.agentId) );
	}
	
	protected Map<WorldObjectId, Set<EventType>> bufferedEvents = new HashMap<WorldObjectId, Set<EventType>>();
	protected List<DummyObjectEvent> eventBuffer = new LinkedList<DummyObjectEvent>();
	
	// OVERRIDING OF
	//created, updated, disappeared, appeared, destroyed
	// implements buffering and later flushing of events
	
	@Override
	protected void objectCreated( ILocalWorldObject obj, long time )
	{
		bufferObjectEvent( obj.getId(), EventType.FIRST_ENCOUNTERED, time );
		bufferObjectEvent( obj.getId(), EventType.UPDATED, time);
		super.objectCreated(obj, time);
	}
	
	@Override
	protected void objectUpdated( ILocalWorldObject obj, long time )
	{
		bufferObjectEvent( obj.getId(), EventType.UPDATED, time );
		super.objectUpdated(obj, time);
	}
	
	@Override
	protected void objectDestroyed( ILocalWorldObject obj, long time)
	{
		//Raise now, raising later would try to get the deleted object
		raiseEvent( new WorldObjectDestroyedEvent( get(obj.getId(), TimeKey.get(time)), time));
		super.objectDestroyed(obj, time); //and delete
	}
	
	@Override
	protected void objectAppeared( ILocalViewable obj, long time )
	{
		super.objectAppeared(obj, time);
		bufferObjectEvent( obj.getId(), EventType.APPEARED, time);		
	}
	
	@Override
	protected void objectDisappeared( ILocalViewable obj, long time )
	{
		super.objectDisappeared(obj, time);
		bufferObjectEvent( obj.getId(), EventType.DISAPPEARED, time );		
	}
	
	/**
	 * Sets the visible property on the object to false by creating a disappeared event
	 * also raises correct events
	 * @param id
	 * @param time
	 */
	protected abstract void disappearObject( WorldObjectId id, long time);
	
	/**
	 * This is used for raising object events safely
	 * by buffering the object events, we make sure that when the events are raised and listeners notified,
	 * the update event has been fully processed and the object contains correct and consistent data.
	 * If you need to update objects manually and then want to raise events for whatever reason, always use this method.
	 * @param id
	 * @param eventType
	 * @param time
	 */
	protected void bufferObjectEvent(WorldObjectId id, EventType eventType, long time)
	{
		if ( log.isLoggable(Level.FINEST ))
		{
			log.finest("Buffering event for : " + id.toString() + " ; Type :" + eventType.toString() + "; T : " + time);
		}
		
		//lets check if we are not adding the event for the second time (happens when there is an update from shared part as well);
		//needs to synchronize because of shared worldViews
		synchronized(eventBuffer)
		{
			Set<EventType> buffered = bufferedEvents.get(id);
			if ( buffered == null)
			{
				eventBuffer.add( new DummyObjectEvent(id, eventType, time) );
				buffered = new HashSet<EventType>();
				buffered.add(eventType);
				bufferedEvents.put( id, buffered);
			}
			else if ( !buffered.contains(eventType) )
			{
				buffered.add(eventType);
				eventBuffer.add( new DummyObjectEvent(id, eventType, time) );
			}
		}
	
	}
	
	/**
	 * Raises all events from this batch
	 */
	protected void flushEvents()
	{
		List<DummyObjectEvent> toBuffer = new LinkedList<DummyObjectEvent>();
		if (log.isLoggable(Level.FINE) )
		{
			if ( eventBuffer.isEmpty() )
			{
				log.fine("No events to flush.");
			}
			else
			{
				log.fine("Flushing events for time : " + eventBuffer.iterator().next().getTime() + "; Buffer size : " + eventBuffer.size() );
			}
		}
		synchronized (eventBuffer)
		{
			List<DummyObjectEvent> toProcess = eventBuffer;
			eventBuffer = new LinkedList<DummyObjectEvent>(); 
			for (DummyObjectEvent dummy : toProcess)
			{
				WorldObjectEvent e = null;
				long eventTime = dummy.getTime();
				try	
				{					
					switch ( dummy.getType() )
					{
					case APPEARED :
						e = new WorldObjectAppearedEvent<IViewable>((IViewable)this.get(dummy.getObjectId(), TimeKey.get(eventTime)), eventTime );
						break;
					case DESTROYED :
						e = new WorldObjectDestroyedEvent<ICompositeWorldObject>(this.get(dummy.getObjectId(), TimeKey.get(eventTime)), eventTime );
						break;
					case DISAPPEARED :
						//disappear the object
						disappearObject( dummy.getObjectId(), eventTime );
							
						//e = new WorldObjectDisappearedEvent<IViewable>((IViewable)this.get(dummy.getObjectId(), TimeKey.get(eventTime)), eventTime);
						
						break;
					case FIRST_ENCOUNTERED :
						e = new WorldObjectFirstEncounteredEvent<ICompositeWorldObject>(this.get(dummy.getObjectId(), TimeKey.get(eventTime)), eventTime);
						break;
					case UPDATED :
						if ( getLocal(dummy.getObjectId() ) != null)
						{
							e = new WorldObjectUpdatedEvent<ICompositeWorldObject>(this.get(dummy.getObjectId(), TimeKey.get(eventTime)), eventTime);
						}			
						break;
					}
					if ( e != null )
					{
						raiseEvent(e);
					}
				}
				catch (Exception exc) //sometimes updating msgClass can get little slow, lets just postpone the event for now
				{					
					log.warning("["+dummy.getTime()+"]Exception in raising event |" + dummy.getObjectId() + "| postponing " + exc);
					dummy.incTime();
					toBuffer.add(dummy);					
				}		
			}
			eventBuffer.addAll(toBuffer);//clear event buffer
			bufferedEvents = new HashMap<WorldObjectId, Set<EventType>>();
		}// END SYNCHRONIZED
	}
	
	IWorldChangeEvent bufferedEndMessage = null;
	
	/**
	 * This method is called when the SharedBatchFinishedEvent is recieved from the sharedWorldView, notifying us that
	 * all sharedEvents for the specified time have been processed and it is safe to run logic on the time.
	 * @param time
	 */
	protected void sharedBatchFinished( long time )
	{
		//GUICE ERROR
		if ( lockedTimes == null )
		{
			lockedTimes = new LinkedList<Long>();
		}
		
		synchronized( lockedTimes )
		{
			//flush events
			this.flushEvents();
			NullCheck.check(bufferedEndMessage, "Buffered End message");
			super.notify( bufferedEndMessage );
			bufferedEndMessage = null;
			if ( log.isLoggable( Level.FINER ) )
			{
				log.finer("SharedBatchFinishedEvent recieved from the SharedWorldView for time " + time );
			}
			if ( !lockFinished )
			{
				log.fine("Setting current timeKey : " + time );
				setCurrentTime( TimeKey.get(time) );
				timeKeyIncreased = true;
				List<Long> newLocks = new LinkedList<Long>();
				for ( Long t : lockedTimes )
				{
					if ( t < time)
					{
						unlockTime(t);
					}
					else
					{
						newLocks.add(t);
					}
				}
				lockedTimes = newLocks;
				
				//GUICE ERROR
				if ( latch == null )
				{
					latch = new CountDownLatch(1);
				}
				latch.countDown();
				
			}		
		}
	}
	
	public boolean isLocked()
	{
		return lockFinished;
	}
	
	/**
	 * Must be called before starting logic.
	 */
	public void lock()
	{
		log.fine("Locking BatchAwareLocalWorldView");
		
		synchronized ( objectMutex )
		{
			if (!isRunning()) throw new ComponentNotRunningException("Can't lock() world view is not running!", log, this);
			if ( !lockFinished  )
			{
				lockRequested = true;
			}
			else
			{
				return;
			}			
		}
		
		try {
			latch.await();
		} catch (InterruptedException e) {
			throw new PogamutInterruptedException("Interrupted while waiting to acquire lock()!", e, this);
		}
		
		lockFinished = true;
		
		log.fine("BatchAwareLocalWorldView locked.");
	}

	/**
	 * Called after the logic has finished.
	 */
	public void unlock()
	{
		synchronized( objectMutex )
		{
			if (!isRunning()) throw new ComponentNotRunningException("Can't unlock() world view is not running!", log, this);
			LinkedList<Long> newLocks = new LinkedList<Long>();
			for ( Long t : lockedTimes )
			{
				if ( t < currentTimeKey.getTime() )
				{
					this.unlockTime( t );
				}
				else
				{
					newLocks.add(t);
				}
			}
			lockedTimes = newLocks;
			lockFinished = false;
			lockRequested = false;
			latch = new CountDownLatch(1);
			log.fine("BatchAwareLocalWorldView unlocked");
		}
	}
	
	private boolean timeKeySet = false;
	
	boolean endMessageCame = false;
	boolean sharedFinished = false;
	
	@Override
	public synchronized void notify(IWorldChangeEvent event)
	{
		log.finest( "BatchAwareLocalWorldView notify : " + event);
		
		if (!timeKeySet)
		{
			this.currentTimeKey = TimeKey.get( event.getSimTime() );
			timeKeySet = true;
		}
		
		//if the event updates a shared part of a WorldObject, notify sharedWorldView
    	if (!( event instanceof ILocalWorldObjectUpdatedEvent))
        {
    		if ( event instanceof ICompositeWorldObjectUpdatedEvent)
    		{
    			IWorldChangeEvent partEvent = ((ICompositeWorldObjectUpdatedEvent)event).getSharedEvent();
    			if (partEvent != null) //shared part
    			{
    				if ( log.isLoggable( Level.FINEST ))
    				{
    					log.finest("Notyfying sharedWV " + partEvent.toString() + ")");
    				}
    				sharedWorldView.notify(partEvent);	
    			}
    			partEvent = ((ICompositeWorldObjectUpdatedEvent)event).getStaticEvent();
				if ( partEvent != null) //static part
				{
					if ( log.isLoggable( Level.FINEST )) log.finest("Notyfying sharedWV " + partEvent.toString() + ")");
					sharedWorldView.notify(partEvent);
				}
    		}
        	//shared or static event will not modify LocalObjects, no need to process it beyond notifying sharedWorldView
    		else if ( (event instanceof ISharedWorldObjectUpdatedEvent) || (event instanceof IStaticWorldObjectUpdatedEvent) )
        	{
    			if ( log.isLoggable( Level.FINEST )) log.finest("Notyfying sharedWV " + event.toString() + ")");    			
    			sharedWorldView.notify(event);
        		return;
        	}
        }
    	
    	//FIXME some guice weird business that objectMutex isnt initialized
    	//GUICE ERROR
    	if (objectMutex == null )
    	{
    		objectMutex = new Object();
    	}
    	
    	synchronized(objectMutex)
    	{
	    	if ( isBatchBeginEvent(event) )
	    	{
	    		if ( currentTimeKey == null )
	    		{
	    			log.info("Setting new currentTimeKey to : " + event.getSimTime());
	    			currentTimeKey = TimeKey.get( event.getSimTime() );
	    		}
		    	lockTime( event.getSimTime());
		    	this.lockedTimes.add( event.getSimTime() );
		    	notifySharedBegin( event.getSimTime() );
		    	super.notify(event);
		    	
	    	}
	    	else if ( isBatchEndEvent(event) )
	    	{
	    		if ( log.isLoggable(Level.FINER ))
	    		{log.finer("Notifying sharedWorldView with EndEvent of time " + event.getSimTime() + " : " + event); };
	    		sharedWorldView.notify(event);
	    		bufferedEndMessage = event;
	    		endMessageCame = true;
	    		if ( endMessageCame && sharedFinished)
	    		{
	    			sharedBatchFinished(event.getSimTime());
	    			endMessageCame = false;
	    			sharedFinished = false;
	    		}
	    		//super.notify(event);
	    	}    	
	    	else if ( event instanceof SharedBatchFinishedEvent )
	    	{	    		
	    		sharedFinished = true;
	    		if ( endMessageCame && sharedFinished)
	    		{
	    			sharedBatchFinished(event.getSimTime());
	    			endMessageCame = false;
	    			sharedFinished = false;
	    		}
	    	}
	    	else
	    	{
	    		super.notify( event );
	    	}
    	}
	}
	
	@Override
	protected void stop() {
		super.stop();
		synchronized(objectMutex) {
			while (latch != null && latch.getCount() > 0) latch.countDown();
			while (lockedTimes != null && lockedTimes.size() > 0) {
				long time = lockedTimes.get(0);
				unlockTime(lockedTimes.get(0));
				if (lockedTimes.get(0) == time) lockedTimes.remove(0);
			}
		}
	}
	
	@Override
	protected void kill() {
		super.kill();
		synchronized(objectMutex) {
			while (latch != null && latch.getCount() > 0) latch.countDown();
			while (lockedTimes != null && lockedTimes.size() > 0) {
				try {
					long time = lockedTimes.get(0);
					unlockTime(lockedTimes.get(0));
					if (lockedTimes.get(0) == time) lockedTimes.remove(0);
				} catch (Exception e) {					
				}
			}
		}
	}
	
}
