package cz.cuni.amis.pogamut.base.communication.worldview.impl;

import java.util.LinkedList;
import java.util.Queue;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldEventWrapper;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdateResult;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.IWorldChangeEventInput;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObject;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectDestroyedEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectFirstEncounteredEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.component.bus.exception.ComponentNotRunningException;
import cz.cuni.amis.pogamut.base.component.bus.exception.ComponentPausedException;
import cz.cuni.amis.pogamut.base.component.controller.ComponentController;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencies;
import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.utils.NullCheck;
import cz.cuni.amis.utils.exception.PogamutException;
import java.util.logging.Level;

/**
 * Schema: "real" world | ... some communication ... | IWorldViewEventInput - EventDrivenWorldView | Agent (most probably listening for events)
 * <p><p>
 * {@link EventDrivenWorldView} assumes that everything is driven by the events that are received
 * through {@link IWorldChangeEventInput} - those events surely must contains "new object appears event",
 * "object update event", "object disappear event". Those three events are wrapped in one
 * interface IWorldObjectUpdateEvent that has three types of behavior (see its javadoc).
 * <p><p>
 * The EDWV uses {@link IWorldChangeEventFilter}s to process incoming events. During the construction of the EDWV the filters
 * should be registered into it (see method initFilters). Subclass this EDWV to provide a different set of filters.
 * <p><p>
 * Used filters:
 * 
 * TODO! finish the documentation
 * 
 * Handling of events (that is incoming):
 * <ul>
 * <li>event <b>is NOT</b> IWorldObjectUpdateEvent - the event is raised (if is instance of IWorldViewEvent, otherwise an exception is thrown)
 * all listeners at this object for that type of event are informed sequentially as they were added</li>
 * <li>event <b>IS</b> IWorldObjectUpdateEvent - the event is consumed (processed) by the EventDrivenWorldView
 * the IWorldObjectUpdateEvent may have three already mentioned outcomes:
 * 		<ol>
 * 			<li>new object appears - an event WorldObjectAppearedEvent is raised</li>
 * 			<li>object was updated - the EventDrivenWorldView will process this event silently, not raising any other events</li>
 * 			<li>object disappeared - an even WorldObjectDisappearedEvent is raised</li>
 *      </ol>
 * </li>
 * </ul>
 * <p><p>
 * <b>
 * Note that the implementation of raising / receiving / notifying about event is
 * strictly time-ordered. Every event is fully processed before another is raised/received.
 * There is a possibility that raising one event may produce another one - in that case
 * processing of this new one is postponed until the previous event has been fully processed
 * (e.g. all listeners has been notified about it). That means that recursion of events is forbidden.
 * <p><p>
 * Note that we rely on method update() of the IWorldObjectUpdateEvent to be called
 * as the first before the event is propagated further. An original object must be passed
 * to the update() method and that method should save it as the original so the
 * event can return correct object via getObject() method.
 *
 * @author Jimmy
 */
@AgentScoped
public class EventDrivenWorldView extends AbstractWorldView {
	
	public static final String WORLDVIEW_DEPENDENCY = "EventDrivenWorldViewDependency";

    /**
     * Flag that is telling us whether there is an event being processed or not.
     * <p><p>
     * It is managed only by notify() method - DO NOT MODIFY OUTSIDE IT!
     */
    protected boolean receiveEventProcessing = false;
    
    /**
     * List of events we have to process.
     * <p><p>
     * It is managed only by notify() method - DO NOT MODIFY OUTSIDE IT!
     */
    protected LinkedList<IWorldChangeEvent> notifyEventsList = new LinkedList<IWorldChangeEvent>();

    @Inject
    public EventDrivenWorldView(@Named(WORLDVIEW_DEPENDENCY) ComponentDependencies dependencies, IComponentBus bus, IAgentLogger log) {
        super(dependencies, bus, log);
    }
    
    /**
     * Catches exceptions. If exception is caught, it calls {@link ComponentController}.fatalError() and this.kill(). 
     */
    @Override
    protected void raiseEvent(IWorldEvent event) {
    	try {
    		if (log != null && log.isLoggable(Level.FINER)) log.finer("raising event " + event);
    		super.raiseEvent(event);
    	} catch (Exception e) {
    		this.controller.fatalError("Exception raising event " + event, e);
    		this.kill();
    	}
    }

    /**
     * Used to process IWorldChangeEvent - it has to be either IWorldChangeEvent or IWorldObjectUpdateEvent. Forbids recursion.
     * <p>
     * DO NOT CALL SEPARATELY - should be called only from notifyEvent().
     * <p><p>
     * You may override it to provide event-specific processing behavior.
     *
     * @param event
     */
    protected void innerNotify(IWorldChangeEvent event) {    	
    	NullCheck.check(event, "event");
    	if (log.isLoggable(Level.FINEST)) {
    		log.finest("processing " + event);
    	}
    	
        if (event instanceof IWorldObjectUpdatedEvent) {
            objectUpdatedEvent((IWorldObjectUpdatedEvent)event);
        } else {
            if (event instanceof IWorldEventWrapper) {
                raiseEvent(((IWorldEventWrapper) event).getWorldEvent());
            } else
            if (event instanceof IWorldEvent) {
            	raiseEvent((IWorldEvent)event);
            } else {
                throw new PogamutException("Unsupported event type received (" + event.getClass() + ").", this);
            }
        }
    }
    
    /**
     * Called from {@link EventDrivenWorldView#innerNotify(IWorldChangeEvent)} if the event is {@link IWorldObjectUpdatedEvent}
     * to process it.
     * 
     * @param updateEvent
     */
    protected void objectUpdatedEvent(IWorldObjectUpdatedEvent updateEvent) {
        IWorldObject obj = get(updateEvent.getId());
        IWorldObjectUpdateResult updateResult = updateEvent.update(obj);
        switch (updateResult.getResult()) {
        case CREATED:            	
            objectCreated(updateResult.getObject());
            return;
        case UPDATED:
        	if (updateResult.getObject() != obj) {
        		throw new PogamutException("Update event " + updateEvent + " does not returned the same instance of the object (result UPDATED).", this);
        	}
        	objectUpdated(obj);
        	return;
        case SAME:
        	if (log.isLoggable(Level.FINEST)) {
        		log.finest("no update for " + updateEvent);
        	}
        	return;
        case DESTROYED:
        	objectDestroyed(obj);
            return;
        default:
        	throw new PogamutException("Unhandled object update result " + updateResult.getResult() + " for the object " + obj + ".", this);
        }
    }

    /**
     * Must be called whenever an object was created, raises correct events.
     * <p><p>
     * Might be overridden to provide different behavior.
     * @param obj
     */
    protected void objectCreated(IWorldObject obj) {
    	addWorldObject(obj);
    	raiseEvent(new WorldObjectFirstEncounteredEvent<IWorldObject>(obj, obj.getSimTime()));
        objectUpdated(obj);
    }
    
    /**
     * Must be called whenever an object was updated - raises correct event.
     * <p><p>
     * Might be overridden to provide a mechanism that will forbid
     * update of certain objects (like items that can't move).
     *
     * @param obj
     */
    protected void objectUpdated(IWorldObject obj) {
        raiseEvent(new WorldObjectUpdatedEvent<IWorldObject>(obj, obj.getSimTime()));
    }
    
    /**
     * Must be called whenever an object was destroyed - raises correct events.
     * <p><p>
     * Might be overriden to provide different behavior.
     * 
     * @param obj
     */
    protected void objectDestroyed(IWorldObject obj) {
    	removeWorldObject(obj);
        raiseEvent(new WorldObjectDestroyedEvent<IWorldObject>(obj, obj.getSimTime()));        
    }

    @Override
    public synchronized void notify(IWorldChangeEvent event) throws ComponentNotRunningException, ComponentPausedException {
    	if (isPaused()) {
    		throw new ComponentPausedException(controller.getState().getFlag(), this);
    	}
    	if (!isRunning()) {
    		throw new ComponentNotRunningException(controller.getState().getFlag(), this);
    	}

        // is this method recursively called?
        if (receiveEventProcessing) {
            // yes it is -> that means the previous event has not been
            // processed! ... store this event and allows the previous one
            // to be fully processed (e.g. postpone raising this event)
            notifyEventsList.add(event);
            return;
        } else {
            // no it is not ... so raise the flag that we're inside the method
            receiveEventProcessing = true;
        }
        // process event
        try {
	        innerNotify(event);
	        // check the events list size, do we have more events to process?
	        while (notifyEventsList.size() != 0) {
	            // yes -> do it!
	            innerNotify(notifyEventsList.poll());
	        }
        } finally {
        // all events has been processed, drop the flag that we're inside the method
        	receiveEventProcessing = false;
        }
    }
    
    @Override
    public synchronized void notifyAfterPropagation(IWorldChangeEvent event) throws ComponentNotRunningException, ComponentPausedException {
    	notifyEventsList.addFirst(event);
    }
    
    @Override
    public synchronized void notifyImmediately(IWorldChangeEvent event) throws ComponentNotRunningException, ComponentPausedException {
    	innerNotify(event);	    
    }

}
