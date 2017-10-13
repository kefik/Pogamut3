package cz.cuni.amis.pogamut.multi.communication.worldview.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.pogamut.base.utils.guice.AgentTeamScoped;
import cz.cuni.amis.pogamut.multi.communication.messages.SharedBatchBeginEvent;
import cz.cuni.amis.pogamut.multi.communication.messages.SharedBatchFinishedEvent;
import cz.cuni.amis.utils.exception.PogamutException;
import cz.cuni.amis.utils.maps.HashMapSet;


/**
 * SharedWorldView with batch-driven implementation.
 * The worldView manages all its localWorldViews and will notify them after a batch has been fully processed from all worldViews
 * thus preventing inconsistency in shared data.
 * LocalWorldViews must notify this worldView with correct events ({@link SharedBatchBeginEvent} and batchEndEvents which are left for the user to override
 * -> the UT2004 version has its own implementation in descendant worldview )
 * the sharedWorldView will then notify back with {@link SharedBatchFinishedEvent} when all events have been processed.
 * @author srlok
 *
 */
public abstract class BatchAwareSharedWorldView extends EventDrivenSharedWorldView
{
	/**
	 * Construtor - all we need is logger. Shared world view gets all other information at runtime.
	 * @param logger
	 */
	public BatchAwareSharedWorldView(Logger logger) {
		super(logger);
	}
	
	/**
	 * This map counts the number of unfinished message batches for the respective TimeKey, once the count reaches zero,
	 * all worldViews waiting for the lock to release are notifies.
	 */
	private Map<Long, Integer> timeLocks = Collections.synchronizedMap( new HashMap<Long, Integer>() );
	
	/**
	 * Map containing time->agent for agents which are waiting for the time to be completely exported. 
	 */
	private HashMapSet<Long, IAgentId> waitingLocalWorldViews = new HashMapSet<Long, IAgentId>();
	
	private Object objectMutex = new Object();
		
	protected abstract boolean isBatchEndEvent( IWorldChangeEvent event );
	
	/**
	 * Notifies all waiting local world views, that batch belonging to 'time' has been exported by all local world views.
	 * I.e. {@link SharedBatchBeginEvent} occurs for 'time'.
	 *  
	 * @param waiting agent which local wvs should be notified
	 * @param time time for which the batch has finished
	 */
	protected void notifyLocalWorldViews(Set<IAgentId> waiting, long time) 
	{
		if (waiting != null) {
			for ( IAgentId id : waiting ) {
				localWorldViews.get(id).notify(new SharedBatchFinishedEvent(time) );
			}
		}
	}
	
	/**
	 * Adds a lock for the desired time.
	 * This method is called when a SharedBatchBeginEvent is recieved, it means that the localWorldViews should wait
	 * until the entire batch has been processed.
	 * @param time
	 */
	protected void processBeginEvent( SharedBatchBeginEvent event) {
		//log.info("Processing: " + event);
		
		synchronized(timeLocks) {
			// INCREASE TIME LOCKS FOR A GIVEN TIME
			Integer n = timeLocks.get(event.getSimTime());
			if ( n == null) {
				timeLocks.put(event.getSimTime(), 1);
			} else {
				timeLocks.put(event.getSimTime(), ++n);
			}
			// SUBSCRIBE LOCAL WORLD VIEW AS WAITING FOR THE SHARED-BATCH-END-EVENT
			waitingLocalWorldViews.add( event.getSimTime(), event.getAgentId() );			
		}
	}
	
	/**
	 * Processes batch-end event ... correctly synchronize access to timeLocks in lock-free manner.
	 * 
	 * @param time
	 */
	protected void processEndEvent( IWorldChangeEvent event) {
		//log.info("Processing:" + event + " ;" + event.getSimTime());
		Set<IAgentId> waiting = null;
		synchronized(timeLocks) {
			Integer locks = timeLocks.remove(event.getSimTime());
			if (locks == null) {
				throw new PogamutException("BatchEndEvent came for time that has no locks == no previous BatchBeginEvent came!", this);
			}
			if (locks <= 0) {
				throw new PogamutException("BatchEndEvent came for time that " + locks + " <= 0 locks! INVALID STATE!", this);
			}
			--locks;
			if (locks == 0)	{
				waiting = waitingLocalWorldViews.remove(event.getSimTime());				
			} else {
				timeLocks.put(event.getSimTime(), locks);
			}
		}
		notifyLocalWorldViews(waiting, event.getSimTime());
	}

	//Object objMutex = new Object();
	
	@Override
	public void notify(IWorldChangeEvent event) {
		log.finest("BatchAwareSharedWorldView notify : " + event);
		
			if ( event instanceof SharedBatchBeginEvent ) {
				log.fine("SharedWorldView : SharedBatchBeginEvent for time : " + event.getSimTime());
				processBeginEvent( (SharedBatchBeginEvent) event );
			}
			else 
			if ( isBatchEndEvent(event) ) {
				log.fine("SharedWorldView : SharedBatchEndEvent for time : " + event.getSimTime());
				processEndEvent(event);
			}
			else
			{
				super.notify(event);
			}
	}
}
