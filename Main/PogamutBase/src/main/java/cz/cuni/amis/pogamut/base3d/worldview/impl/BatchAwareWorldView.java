package cz.cuni.amis.pogamut.base3d.worldview.impl;

import java.util.HashSet;
import java.util.Set;

import com.google.inject.name.Named;

import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.component.bus.exception.ComponentNotRunningException;
import cz.cuni.amis.pogamut.base.component.bus.exception.ComponentPausedException;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencies;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.pogamut.base3d.worldview.object.IViewable;

/**
 * World view that is updated by protocol utilizing concept of batches. Each batch
 * is separated by some message. After receiving this message additional events
 * may be raised (eg. visibility update etc.).
 * @author ik
 */
public abstract class BatchAwareWorldView extends VisionWorldView {

	public static final String WORLDVIEW_DEPENDENCY = "BatchAwareWorldViewDependency";
	
    protected Set<IViewable> lastObjectBatch = new HashSet<IViewable>();
    protected Set<IViewable> currentObjectBatch = new HashSet<IViewable>();

    public BatchAwareWorldView(@Named(WORLDVIEW_DEPENDENCY) ComponentDependencies dependencies, IComponentBus bus, IAgentLogger log) {
        super(dependencies, bus, log);
    }

    /**
     * Is this event a batch end event? If so some extra events may be generated
     * in processing this message.
     * @param evt
     * @return true if this is a batch ending event
     */
    protected abstract boolean isBatchEndEvent(IWorldChangeEvent evt);
    
	/**
	 * Is this event a batch begin event? It is needed for the locking to be
	 * working correctly.
	 * 
	 * @param evt
	 * @return true if this is a batch ending event
	 */
	protected abstract boolean isBatchBeginEvent(IWorldChangeEvent evt);    

    /**
     * Sets the visible flag to true on {@link IViewable} objects.
     * @param obj Object that disappeared
     */
    protected abstract void setDisappearedFlag(IViewable obj);
    
//    long lastBatch = 0;

    protected void batchAwareWorldViewNotify(IWorldChangeEvent event) {
    	if (isBatchEndEvent(event)) {
    		
//    		if (lastBatch == 0) {
//    			lastBatch = System.currentTimeMillis();
//    		} else {
//    			long currTime = System.currentTimeMillis();
//    			log.warning("LAST BATCH: " + (currTime - lastBatch) + " ms | event.getSimTime() == " + event.getSimTime());
//    			lastBatch = currTime;    			
//    		}
    		
            // handle disappeared objects
            lastObjectBatch.removeAll(currentObjectBatch);
            for (IViewable obj : lastObjectBatch) {
                // set the visibility flag to false
                setDisappearedFlag(obj);
                // first generate update event
                objectUpdated(obj);                
                // IMPORTANT:               
                // then generate disappear event ... usually appeared/disappeared events will 
                // wrap the update event processing (e.g. like html tags they must open/...updates.../close)
                super.objectDisappeared(obj);                
            }            
            // exchange the two sets and clear current batch
            Set<IViewable> swp = lastObjectBatch;
            lastObjectBatch = currentObjectBatch;
            currentObjectBatch = swp;
            currentObjectBatch.clear();
        } else {
            if (event instanceof IWorldObjectUpdatedEvent && event instanceof IViewable) {
                IViewable viewable = (IViewable)get(((IWorldObjectUpdatedEvent)event).getId());
                if (viewable != null && viewable.isVisible()) {
                    currentObjectBatch.add(viewable);
                }
            }
        }
    }
    
    @Override
    public synchronized void notify(IWorldChangeEvent event) {
    	batchAwareWorldViewNotify(event);
    	super.notify(event);
    }
    
    @Override
    public synchronized void notifyImmediately(IWorldChangeEvent event)
    		throws ComponentNotRunningException, ComponentPausedException {
    	batchAwareWorldViewNotify(event);
    	super.notifyImmediately(event);
    }
    
    @Override
    protected void objectAppeared(IViewable obj) {
    	super.objectAppeared(obj);
    	currentObjectBatch.add(obj);
    }
    
    @Override
    protected void objectDisappeared(IViewable obj) {
    	super.objectDisappeared(obj);
    	lastObjectBatch.remove(obj);
    	currentObjectBatch.remove(obj);
    }
    
    /**
     * Any objects waiting in {@link BatchAwareWorldView#currentObjectBatch} for processing?
     * @return true if there is at least one object to process
     */
    public boolean hasObjectsToProcess() {
    	return !currentObjectBatch.isEmpty();
    }
    
}
