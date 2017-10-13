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
import cz.cuni.amis.pogamut.base.component.bus.exception.ComponentNotRunningException;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencies;
import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.pogamut.base3d.ILockableVisionWorldView;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BeginMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.EndMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;

import java.util.logging.Level;

/**
 * Lockable word view.
 * <p><p>
 * Contains GameBots2004 correct locking of the worldview.
 * <p><p>
 * All messages are processed always in batches (all messages between EndMessages are one batch) meaning that the world view is always 
 * correct!
 * <p><p>
 * When worldview is lock()ed it postpones the events until unlock()ed, which is triggering
 * raising all events that came from the lock().
 * <p><p>
 * The world view is unlocked from the beginning.
 * <p><p>
 * All those locking mechanisms start working when the first BEGIN message comes. 
 * 
 * @author Jimmy
 */
@AgentScoped
public class UT2004LockableWorldView extends UT2004WorldView implements ILockableVisionWorldView {
	
	public static final String WORLDVIEW_DEPENDENCY = "UT2004LockableWorldViewDependency";
	
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
	
    @Inject
    public UT2004LockableWorldView(@Named(WORLDVIEW_DEPENDENCY) ComponentDependencies dependencies, IMediator mediator, IComponentBus bus, IAgentLogger log) {
        super(dependencies, mediator, bus, log);
    }
	
	/**
	 * When the world view is locked - no batches are processes until unlocked.
	 */
	public void lock() throws ComponentNotRunningException {
		if (!isRunning()) throw new ComponentNotRunningException(controller.getState().getFlag(), this);
		synchronized(objectMutex) {
			if (isLocked()) return;
			locked = true;
			if (log.isLoggable(Level.FINER)) log.finer("World view locked.");
		}
	}
	
	/**
	 * Unlocks the world view - triggers processing of all events till the last EndMessage that
	 * came between lock() / unlock() calls.
	 */
	public void unlock() throws ComponentNotRunningException {
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
	
	public boolean hasBatchesToProcess() {
		return !batches.isEmpty();
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
    	
    	
    	if (!isRunning()) throw new ComponentNotRunningException(controller.getState().getFlag(), this);
		synchronized(objectMutex) {
			if (!beginCame) {
				if (event instanceof BeginMessage) {
					beginCame = true;
				} else {
					super.notify(event);
					return;
				}
			}
			if (isLocked()) {
				if (event instanceof EndMessage) {
					currentBatch.add(event);
					batches.add(currentBatch);
					currentBatch = new ArrayList<IWorldChangeEvent>(currentBatch.size()+20);
				} else {
					currentBatch.add(event);				
				}
			} else {
				if (event instanceof EndMessage) {
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
