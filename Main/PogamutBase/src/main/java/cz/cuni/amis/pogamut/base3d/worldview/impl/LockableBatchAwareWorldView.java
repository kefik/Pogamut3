package cz.cuni.amis.pogamut.base3d.worldview.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.google.inject.name.Named;

import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.ILockableWorldView;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencies;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import java.util.logging.Level;

public abstract class LockableBatchAwareWorldView extends BatchAwareWorldView implements ILockableWorldView {

	public static final String WORLDVIEW_DEPENDENCY = "LockableBatchAwareWorldView";
	
	/**
	 * Here we store batches that are complete (ends with the EndMessage).
	 */
	private Queue<List<IWorldChangeEvent>> batches = new LinkedList<List<IWorldChangeEvent>>();
	
	/**
	 * Here we store new events that are coming from the Mediator.
	 */
	private List<IWorldChangeEvent> currentBatch = new ArrayList<IWorldChangeEvent>();
	
	/**
	 * Whether the world view is locked.
	 */
	private boolean locked = false;
	
	/**
	 * First BEG message 
	 */
	private boolean beginCame = false;
	
	/**
	 * Synchronization mutex for this class.
	 */
	private final Object objectMutex = new Object();

	public LockableBatchAwareWorldView(@Named(WORLDVIEW_DEPENDENCY) ComponentDependencies dependencies, IComponentBus bus, IAgentLogger log) {
        super(dependencies, bus, log);
	}
	
	/**
     * Is this event a batch begin event? It is needed for the locking to be working correctly.
     * @param evt
     * @return true if this is a batch ending event
     */
    protected abstract boolean isBatchBeginEvent(IWorldChangeEvent evt);

	/**
	 * When the world view is locked - no batches are processes until unlocked.
	 */
	public void lock() {
		synchronized(objectMutex ) {
			if (isLocked()) return;
			locked = true;
			if (log.isLoggable(Level.FINER)) log.finer("World view locked.");
		}
	}
	
	/**
	 * Unlocks the world view - triggers processing of all events till the last EndMessage that
	 * came between lock() / unlock() calls.
	 */
	public void unlock() {
		synchronized(objectMutex) {
			if (!isLocked()) return;
			if (log.isLoggable(Level.FINER)) log.finer("World view is being unlocked.");
			locked = false;
			for (List<IWorldChangeEvent> batch : batches) {
				processBatch(batch);
			}
			batches.clear();			
			if (log.isLoggable(Level.FINER)) log.finer("World view unlocked.");
		}
	}
	
	public boolean isLocked() {
		return locked;
	}
	
	/**
	 * Does super.notifyEvent(event) for each event in the batch. 
	 * <p><p>
	 * <b>Unsync!</b>
	 * @param batch
	 */
	private void processBatch(List<IWorldChangeEvent> batch) {
		for (IWorldChangeEvent event : batch) {
			super.notify(event);
		}		
	}
	
	/**
	 * Implements locking logic.
	 */
	@Override
	public void notify(IWorldChangeEvent event) {
		synchronized(objectMutex) {
			if (!beginCame ) {
				if (isBatchBeginEvent(event)) {
					beginCame = true;
				} else {
					super.notify(event);
					return;
				}
			}
			if (isLocked()) {
				if (isBatchEndEvent(event)) {
					currentBatch .add(event);
					batches.add(currentBatch);
					currentBatch = new ArrayList<IWorldChangeEvent>(currentBatch.size()+20);
				} else {
					currentBatch.add(event);				
				}
			} else {
				if (isBatchEndEvent(event)) {
					currentBatch.add(event);
					processBatch(currentBatch);
					currentBatch.clear();
				} else {
					currentBatch.add(event);
				}
			}
		}
	}
	
}
