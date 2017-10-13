package cz.cuni.amis.pogamut.multi.communication.worldview.impl;

import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;

import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldEventWrapper;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdateResult;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.impl.EventDrivenWorldView;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectDestroyedEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectFirstEncounteredEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.base.component.bus.exception.ComponentNotRunningException;
import cz.cuni.amis.pogamut.base.component.bus.exception.ComponentPausedException;
import cz.cuni.amis.pogamut.base.component.controller.ComponentController;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencies;
import cz.cuni.amis.pogamut.base.component.lifecyclebus.ILifecycleBus;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.pogamut.multi.agent.ITeamedAgentId;
import cz.cuni.amis.pogamut.multi.communication.translator.event.ICompositeWorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.multi.communication.translator.event.ILocalWorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.multi.communication.translator.event.ISharedWorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.multi.communication.translator.event.IStaticWorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.multi.communication.worldview.ISharedWorldView;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ICompositeWorldObject;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ILocalWorldObject;
import cz.cuni.amis.utils.NullCheck;
import cz.cuni.amis.utils.exception.PogamutException;

public abstract class EventDrivenLocalWorldView extends AbstractLocalWorldView {

	public EventDrivenLocalWorldView(ComponentDependencies dependencies, ILifecycleBus bus, IAgentLogger logger, ISharedWorldView sharedWV, ITeamedAgentId agentId)
	{
		super(dependencies, bus, logger, sharedWV, agentId);
	}

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
    protected LinkedList<IWorldChangeEvent> notifyEventsList =
    	new LinkedList<IWorldChangeEvent>();
    
    
    @Override
    public synchronized void notify(IWorldChangeEvent event) throws ComponentNotRunningException, ComponentPausedException {
    	
    	if (log.isLoggable(Level.FINE)){log.fine("LocalWorldView notify( " + event.toString() + ")");};
    	
    	if (isPaused()) {
    		throw new ComponentPausedException(controller.getState().getFlag(), this);
    	}
    	if (!isRunning()) {
    		throw new ComponentNotRunningException(controller.getState().getFlag(), this);
    	}
    	
    	if (!( event instanceof ILocalWorldObjectUpdatedEvent))
        {
    		if ( event instanceof ICompositeWorldObjectUpdatedEvent)
    		{
    			IWorldChangeEvent partEvent = ((ICompositeWorldObjectUpdatedEvent)event).getSharedEvent();
    			if (partEvent != null) //shared part
    			{
    				log.finest("Notyfying sharedWV " + event.toString() + ")");
    				sharedWorldView.notify(partEvent);	
    			}
    			partEvent = ((ICompositeWorldObjectUpdatedEvent)event).getStaticEvent();
				if ( partEvent != null) //static part
				{
					log.finest("Notyfying sharedWV " + event.toString() + ")");
					sharedWorldView.notify(partEvent);
					
				}
    		}
        	//shared or static event will not modify LocalObjects, no need to process it beyond notifying sharedWorldView
    		else if ( (event instanceof ISharedWorldObjectUpdatedEvent) || (event instanceof IStaticWorldObjectUpdatedEvent) )
        	{
    			log.finest("Notyfying sharedWV " + event.toString() + ")");
    			sharedWorldView.notify(event);
        		return;
        	}
        }    	
    	
        // process event
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
    	
    	log.finest("LocalWorldView notify( " + event.toString() + ")");
    	
    	if (isPaused()) {
    		throw new ComponentPausedException(controller.getState().getFlag(), this);
    	}
    	if (!isRunning()) {
    		throw new ComponentNotRunningException(controller.getState().getFlag(), this);
    	}
    	
    	if (!( event instanceof ILocalWorldObjectUpdatedEvent))
        {
    		if ( event instanceof ICompositeWorldObjectUpdatedEvent)
    		{
    			IWorldChangeEvent partEvent = ((ICompositeWorldObjectUpdatedEvent)event).getSharedEvent();
    			if (partEvent != null) //shared part
    			{
    				log.finest("Notyfying sharedWV " + event.toString() + ")");
    				sharedWorldView.notify(partEvent);	
    			}
    			partEvent = ((ICompositeWorldObjectUpdatedEvent)event).getStaticEvent();
				if ( partEvent != null) //static part
				{
					log.finest("Notyfying sharedWV " + event.toString() + ")");
					sharedWorldView.notify(partEvent);
					
				}
    		}
        	//shared or static event will not modify LocalObjects, no need to process it beyond notifying sharedWorldView
    		else if ( (event instanceof ISharedWorldObjectUpdatedEvent) || (event instanceof IStaticWorldObjectUpdatedEvent) )
        	{
    			log.finest("Notyfying sharedWV " + event.toString() + ")");
    			sharedWorldView.notify(event);
        		return;
        	}
        }    	
    	
        // process event
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
    
        
    
    /**
     * Catches exceptions. If exception is caught, it calls {@link ComponentController}.fatalError() and this.kill(). 
     */
    @Override
    protected void raiseEvent(IWorldEvent event) {
    	try {
    		//log.info("Raise event : " + event);
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
    	if (log.isLoggable(Level.FINEST)) log.finest("processing " + event);
    	
        if (event instanceof ILocalWorldObjectUpdatedEvent)
        {
            objectUpdatedEvent((ILocalWorldObjectUpdatedEvent)event);
        }
        else if ( event instanceof ICompositeWorldObjectUpdatedEvent )
        {
        	//use the local part only.
        	ILocalWorldObjectUpdatedEvent locEvent = ((ICompositeWorldObjectUpdatedEvent)event).getLocalEvent();
        	if ( locEvent != null )
        	{
        		objectUpdatedEvent(locEvent);
        	}
        }
        else
        {
            if (event instanceof IWorldEventWrapper)
            {
                raiseEvent(((IWorldEventWrapper) event).getWorldEvent());
            }
            else if (event instanceof IWorldEvent)
            {
            	raiseEvent((IWorldEvent)event);
            }
            else 
            {
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
    protected void objectUpdatedEvent(ILocalWorldObjectUpdatedEvent updateEvent) {
    	
        ILocalWorldObject obj = getMostRecentLocalWorldObject( updateEvent.getId() ); //getting the most recent object available
             
        
        ILocalWorldObject copy = null;
        //if old timeKeys are held, store the original value, create a new copy, which will be updated
        if ( obj != null)
        {
        	//log.finer("Obj : " + obj.toString() );
        	copy = obj.clone();
        }
        else //may be created event
        {
        	copy = null;
        }
       
        IWorldObjectUpdateResult<ILocalWorldObject> updateResult = updateEvent.update(copy);
        switch (updateResult.getResult()) {
        case CREATED:            	
        	//log.finest("CreatedEvent [t:"+updateEvent.getSimTime()+"]");
        	((EventDrivenSharedWorldView)sharedWorldView).addMsgClass(updateResult.getObject().getId(), updateResult.getObject().getCompositeClass());
            objectCreated(updateResult.getObject(), updateEvent.getSimTime());
            return;
        case UPDATED:
        	//log.finest("UpdatedEvent [t:"+updateEvent.getSimTime()+"]");
        	if (updateResult.getObject() != copy) {
        		throw new PogamutException("Update event " + updateEvent + " did not return the same instance of the object (result UPDATED).", this);
        	}
        	
        	//log.finer("Updated : copy : " + copy.toString() );
        	
        	super.addOldLocalWorldObject( obj, updateEvent.getSimTime() );        	

        	actLocalWorldObjects.put(copy.getId(), copy);
        	objectUpdated(copy, updateEvent.getSimTime());
        	
        	return;
        case SAME:
        	return;
        case DESTROYED:
        	
        	super.addOldLocalWorldObject(obj, updateEvent.getSimTime() );        	
        	objectDestroyed(copy, updateEvent.getSimTime());
            return;
        default:
        	throw new PogamutException("Unhandled object update result " + updateResult.getResult() + " for the object " + obj + ".", this);
        }
    }

    //Since event raising is more complicated in local/shared worldviews, this will be implemented on batchWV level
    
    /**
     * Must be called whenever an object was created, raises correct events.
     * <p><p>
     * Might be overridden to provide different behavior.
     * @param obj
     */
    protected void objectCreated(ILocalWorldObject obj, long time) {
    	addLocalWorldObject(obj);
       }
    
    /**
     * Must be called whenever an object was updated - raises correct event.
     * <p><p>
     * Might be overridden to provide a mechanism that will forbid
     * update of certain objects (like items that can't move).
     *
     * @param obj
     */
    protected void objectUpdated(ILocalWorldObject obj, long time) {
    }
    
    /**
     * Must be called whenever an object was destroyed - raises correct events.
     * <p><p>
     * Might be overriden to provide different behavior.
     * 
     * @param obj
     */
    protected void objectDestroyed(ILocalWorldObject obj, long time) {
    	removeLocalWorldObject(obj);
    }

   
	
}
