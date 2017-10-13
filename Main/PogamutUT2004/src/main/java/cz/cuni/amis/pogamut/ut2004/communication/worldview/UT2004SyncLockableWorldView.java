package cz.cuni.amis.pogamut.ut2004.communication.worldview;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import cz.cuni.amis.pogamut.base.communication.mediator.IMediator;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.component.bus.event.BusAwareCountDownLatch;
import cz.cuni.amis.pogamut.base.component.bus.exception.ComponentNotRunningException;
import cz.cuni.amis.pogamut.base.component.bus.exception.ComponentPausedException;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencies;
import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.pogamut.base3d.ILockableVisionWorldView;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BeginMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.EndMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.cuni.amis.utils.exception.PogamutInterruptedException;
import java.util.logging.Level;

/**
 * Lockable word view.
 * <p>
 * <p>
 * Contains GameBots2004 correct locking of the worldview.
 * <p>
 * <p>
 * All messages are processed always in batches (all messages between
 * EndMessages are one batch) meaning that the world view is always correct!
 * <p>
 * <p>
 * When worldview is lock()ed it postpones the events until unlock()ed, which is
 * triggering raising all events that came from the lock().
 * <p>
 * <p>
 * <b>lock() method here blocks</b> until the END message of the batch is hit,
 * then the world view is considered to be fully locked and let the lock()
 * continue. You may use it to create correct sync bot. (just lock() the world
 * view before your logic and unlock() the world view after the logic finishes)
 * <p>
 * <p>
 * The world view is unlocked from the beginning.
 * <p>
 * <p>
 * The locking mechanism starts to work with the first BeginMessage. (To let all
 * other events to be processed automatically during the handshake.)
 * 
 * @author Jimmy
 *         <p>
 *         <p>
 * 
 * 
 * @author Jimmy
 * @see UT2004LockableWorldView
 */
@AgentScoped
public class UT2004SyncLockableWorldView extends UT2004WorldView
		implements ILockableVisionWorldView {

	public static final String WORLDVIEW_DEPENDENCY = "UT2004SyncLockableWorldViewDependency";

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
	private BusAwareCountDownLatch lockLatch;

	/**
	 * Whether the stop() method has been called.
	 */
	private boolean stopRequested = false;
	
	/**
	 * Whether the pause() method has been called.
	 */
	private boolean pauseRequested = false;

	@Inject
	public UT2004SyncLockableWorldView(
			@Named(WORLDVIEW_DEPENDENCY) ComponentDependencies dependencies,
			IMediator mediator,
			IComponentBus bus, IAgentLogger log) {
		super(dependencies, mediator, bus, log);		
	}

	/**
	 * When the world view is locked - no batches are processes until unlocked.
	 * 
	 * @throws PogamutInterruptedException
	 */
	public void lock() throws PogamutInterruptedException, ComponentNotRunningException {
		if (isPaused()) throw new ComponentPausedException(controller.getState().getFlag(), this);
		if (!isRunning()) throw new ComponentNotRunningException(controller.getState().getFlag(), this);
		synchronized (objectMutex) {
			if (isLocked())
				return;
			locked = true;
			if (log.isLoggable(Level.FINER)) log.finer("World view is being locked.");
		}
		if (isPaused()) {
			if (log.isLoggable(Level.FINER)) log.finer("World view paused, unlocking.");
			locked = false;
			throw new ComponentPausedException(controller.getState().getFlag(), this);
		}
		if (!isRunning()) {
			if (log.isLoggable(Level.FINER)) log.finer("World view not running, unlocking.");
			locked = false;
			throw new ComponentNotRunningException(controller.getState().getFlag(), this);
		}
		lockLatch.await();
		if (log.isLoggable(Level.FINER)) log.finer("World view locked.");
		if (pauseRequested) { 
			throw new ComponentPausedException("Component pause requested.", this);
		}
		if (stopRequested) {
			throw new ComponentNotRunningException("Component stop requested.", this);
		}
	}

	/**
	 * Unlocks the world view - triggers processing of all events till the last
	 * EndMessage that came between lock() / unlock() calls.
	 */
	public void unlock() throws ComponentNotRunningException {
		synchronized (objectMutex) {
			if (!isLocked())
				return;
			locked = false;
			if (log.isLoggable(Level.FINER)) log.finer("World view is being unlocked.");
			inLock = false;
			processBatches();
			if (log.isLoggable(Level.FINER)) log.finer("World view unlocked.");
			// reinitialize the lock latch so the next lock() blocks as well
			lockLatch = new BusAwareCountDownLatch(1, eventBus, this);
		}
	}

	public boolean isLocked() {
		return locked;
	}

	public boolean isInLock() {
		return inLock;
	}

	/**
	 * Process all messages that are stored inside the batches and clears them.
	 * <p><p>
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
				if (event instanceof BeginMessage) {
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
					if (event instanceof EndMessage) {
						currentBatch.add(event);
						batches.add(currentBatch);
						currentBatch = new ArrayList<IWorldChangeEvent>(
								currentBatch.size() + 10);
					} else {
						currentBatch.add(event);
					}
				} else {
					// we're waiting for the next EndMessage
					if (event instanceof EndMessage) {
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
	protected void start(boolean startPaused) {
		super.start(startPaused);
		lockLatch = new BusAwareCountDownLatch(1, eventBus, this);
		stopRequested = false;
		pauseRequested = false;
	}

	@Override
	protected void preStop() {
		super.preStop();
		synchronized (objectMutex) {
			stopRequested = true;
			lockLatch.countDown();
		}
	}
	
	@Override
	protected void prePause() {
		super.preStop();
		synchronized (objectMutex) {
			pauseRequested = true;
			lockLatch.countDown();
		}
	}
	
	@Override
	protected void resume() {
		super.resume();
		synchronized(objectMutex) {
			lockLatch.countDown();
			lockLatch = new BusAwareCountDownLatch(1, eventBus, this);
			pauseRequested = false;
		}
	}
	
	@Override
	protected void stop() {
		super.stop();
		synchronized (objectMutex) {
			stopRequested = true;
			lockLatch.countDown();
		}
	}

}
