package cz.cuni.amis.pogamut.base3d.worldview.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.ILockableWorldView;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencies;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.utils.exception.PogamutInterruptedException;
import java.util.logging.Level;

public abstract class SyncLockableBatchAwareWorldView extends BatchAwareWorldView implements ILockableWorldView {
	
	public static final String WORLDVIEW_DEPENDENCY = "SyncLockableBatchAwareWorldView";

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
	 * First the world view will become locked, when the next END message is
	 * received, it will raise this flag meaning the lock() succeeded (the
	 * lockLatch has been raised) and we have to buffer all new messages.
	 */
	private boolean inLock = false;

	/**
	 * First BEG message
	 */
	private boolean beginCame = false;

	/**
	 * Synchronization mutex for this class.
	 */
	private final Object objectMutex = new Object();

	/**
	 * lock() waits on this latch to continue...
	 */
	private CountDownLatch lockLatch = new CountDownLatch(1);

	/**
	 * Whether the stop() method has been called.
	 */
	private boolean stopRequested = false;

	@Inject
	public SyncLockableBatchAwareWorldView(@Named(WORLDVIEW_DEPENDENCY) ComponentDependencies dependencies, IComponentBus bus, IAgentLogger log) {
        super(dependencies, bus, log);
	}

	/**
	 * When the world view is locked - no batches are processes until unlocked.
	 * 
	 * @throws InterruptedException
	 */
	public void lock() throws PogamutInterruptedException {
		synchronized (objectMutex) {
			if (isLocked())
				return;
			locked = true;
			if (log.isLoggable(Level.FINER)) log.finer("World view locked.");
		}
		try {
			lockLatch.await();
		} catch (InterruptedException e) {
			throw new PogamutInterruptedException(e.getMessage(), e, log, this);
		}
		if (stopRequested) {
			throw new PogamutInterruptedException(
					"lock() interrupted with the request to stop the work", log, this);
		}
	}

	/**
	 * Unlocks the world view - triggers processing of all events till the last
	 * EndMessage that came between lock() / unlock() calls.
	 */
	public void unlock() {
		synchronized (objectMutex) {
			if (!isLocked())
				return;
			if (log.isLoggable(Level.FINER)) log.finer("World view is being unlocked.");
			locked = false;
			inLock = false;
			processBatches();
			if (log.isLoggable(Level.FINER)) log.finer("World view unlocked.");
			// reinitialize the lock latch so the next lock() blocks as well
			lockLatch = new CountDownLatch(1);
		}
	}

	public boolean isLocked() {
		return locked;
	}

	public boolean isInLock() {
		return inLock;
	}

	/**
	 * Process all messages that are stored inside the batches and cleares them.
	 * <p>
	 * <p>
	 * <b>Unsync!</b>
	 */
	private void processBatches() {
		// process old batches
		for (List<IWorldChangeEvent> batch : batches) {
			processBatch(batch);
		}
		batches.clear();
		// process current opened batch
		processBatch(currentBatch);
	}

	/**
	 * Does super.notifyEvent(event) for each event in the batch.
	 * <p>
	 * <p>
	 * <b>Unsync!</b>
	 * 
	 * @param batch
	 */
	private void processBatch(List<IWorldChangeEvent> batch) {
		for (IWorldChangeEvent event : batch) {
			super.notify(event);
		}
		batch.clear();
	}

	/**
	 * Implements locking logic.
	 */
	@Override
	public void notify(IWorldChangeEvent event) {
		synchronized (objectMutex) {
			if (!beginCame) {
				if (isBatchBeginEvent(event)) {
					beginCame = true;
				} else {
					super.notify(event);
					return;
				}
			}
			if (isLocked()) {
				if (isInLock()) {
					// we're IN LOCK - logic is running, do not process any new
					// message
					if (isBatchEndEvent(event)) {
						currentBatch.add(event);
						batches.add(currentBatch);
						currentBatch = new ArrayList<IWorldChangeEvent>(
							currentBatch.size() + 10
						);
					} else {
						currentBatch.add(event);
					}
				} else {
					// we're waiting for the next EndMessage
					if (isBatchEndEvent(event)) {
						// EndMessage came! Notify...
						super.notify(event);
						// ... raise the latch and let the logic continue!
						if (log.isLoggable(Level.FINER)) log.finer("World view in-locked state, raising the lock() latch.");
						lockLatch.countDown();
						inLock = true;
					} else {
						// not an EndMessage, process as usual
						super.notify(event);
					}
				}
			} else {
				super.notify(event);
			}
		}
	}

	@Override
	public void stop() {
		super.stop();
		synchronized (objectMutex) {
			stopRequested = true;
			lockLatch.countDown();
		}
	}

}
