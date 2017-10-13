package cz.cuni.amis.pogamut.multi.communication.worldview.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldEventWrapper;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdateResult;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.impl.EventDrivenWorldView;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObject;
import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectDestroyedEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectFirstEncounteredEvent;
import cz.cuni.amis.pogamut.base.component.bus.exception.ComponentNotRunningException;
import cz.cuni.amis.pogamut.base.component.bus.exception.ComponentPausedException;
import cz.cuni.amis.pogamut.base.component.controller.ComponentController;
import cz.cuni.amis.pogamut.base.utils.guice.AgentTeamScoped;
import cz.cuni.amis.pogamut.multi.agent.ITeamId;
import cz.cuni.amis.pogamut.multi.agent.impl.TeamedAgentId;
import cz.cuni.amis.pogamut.multi.communication.translator.event.ISharedPropertyUpdateResult;
import cz.cuni.amis.pogamut.multi.communication.translator.event.ISharedPropertyUpdatedEvent;
import cz.cuni.amis.pogamut.multi.communication.translator.event.ISharedWorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.multi.communication.translator.event.IStaticWorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.multi.communication.worldview.ILocalWorldView;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ISharedProperty;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ISharedWorldObject;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.IStaticWorldObject;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.event.DummyObjectEvent.EventType;
import cz.cuni.amis.pogamut.multi.utils.timekey.TimeKey;
import cz.cuni.amis.utils.NullCheck;
import cz.cuni.amis.utils.exception.PogamutException;

/**
 * SharedWorldView implementing basic event management (notyfying listeners --- not yet fully functional)
 * and updating shared objects using the events. While some synchronization is done here, this worlview does not consider
 * the events to come in batches and thus may create incosistent data structures. (this is addressed by {@link BatchAwareSharedWorldView})
 * @author srlok
 *
 */
@AgentTeamScoped
public abstract class EventDrivenSharedWorldView extends AbstractSharedWorldView {

	public EventDrivenSharedWorldView(Logger logger) {
		super(logger);
	}
	
	private ISharedProperty copyProperty(ISharedProperty original)
	{
		return original.clone();
	}

	public static final String WORLDVIEW_DEPENDENCY = "EventDrivenSharedWorldViewDependency";

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
    protected PriorityBlockingQueue<IWorldChangeEvent> notifyEventsList =
    	new PriorityBlockingQueue<IWorldChangeEvent>(
    		64, 
    		new Comparator<IWorldChangeEvent>() {
				@Override
				public int compare(IWorldChangeEvent arg0, IWorldChangeEvent arg1) {
					return (int) Math.signum( arg0.getSimTime() - arg1.getSimTime() );
				}
			}
    	);
    
    protected Collection<IWorldChangeEvent> syncEventList = Collections.synchronizedCollection( notifyEventsList );
    
    @Override
    public void notify(IWorldChangeEvent event) throws ComponentNotRunningException, ComponentPausedException {
    	log.finest("SharedWorldView notify : [" + event.getSimTime() + " ; " + event);
    	if (isPaused()) {
    		throw new ComponentPausedException(controller.getState().getFlag(), this);
    	}
    	if (!isRunning()) {
    		throw new ComponentNotRunningException(controller.getState().getFlag(), this);
    	}

    	synchronized(syncEventList) {
    		// ADD EVENT AS A SUBJECT FOR FUTURE PROCESSING
    		syncEventList.add(event);
	        // is this method recursively called?
	        if (receiveEventProcessing) {
	            // yes it is -> that means the previous event has not been
	            // processed! ... store this event and allows the previous one
	            // to be fully processed (e.g. postpone raising this event)
	        	log.finest("Added event; events :" + notifyEventsList.size());
	            return;
	        } else {
	            // no it is not ... so raise the flag that we're inside the method
	            receiveEventProcessing = true;
	        }
    	}
    	
    	// SINGLE THREAD ONLY AT THIS POINT!
    	
    	// check the events list size, do we have more events to process?
        while (true) {
        	IWorldChangeEvent ev = null;
        	synchronized(syncEventList) {
        		if (notifyEventsList.size() == 0) {
        			// NO MORE EVENTS TO BE PROCESSED
        			receiveEventProcessing = false;
        			return;
        		}
        		ev = notifyEventsList.poll();
        	}        	  
            if (ev != null) {
            	boolean exception = false;
            	try {
            		innerNotify(ev);
            	} catch (PogamutException e1) {
            		exception = true;
            		throw e1;
            	} catch (Exception e2) {
            		exception = true;
            		throw new PogamutException("Failed to process: " + ev, e2, this);
            	} finally {
            		if (exception) {
            			// we're going to jump out of the method!
            			receiveEventProcessing = false;
            			// NOTE THAT PROCESSING OF EVENTS AFTER THIS POINT IS A BIT NON-DETERMINISTIC!
            		}
            	}
            }
        }
    }
        
    /**
     * Used to process IWorldChangeEvent - it has to be either IWorldChangeEvent or IWorldObjectUpdateEvent. Forbids recursion.
     * <p>
     * DO NOT CALL SEPARATELY - should be called only from notifyEvent().
     * <p><p>
     * You may override it to provide event-specific processing behavior.
     * <p><p>
     * MUST NOT BE CALLED CONCURRENTLY - SINGLE THREAD AT THIS POINT ONLY! MUST BE ENFORCED FROM THE OUTSIDE!
     *
     * @param event
     */
    protected void innerNotify(IWorldChangeEvent event) {    	
    	NullCheck.check(event, "event");
    	if (log.isLoggable(Level.FINEST)) log.finest("SharedWorldView processing " + event);
    	
    	//update shared part
        if (event instanceof ISharedWorldObjectUpdatedEvent) {
        	sharedObjectUpdatedEvent((ISharedWorldObjectUpdatedEvent)event);
        }
        else 
        if ( event instanceof ISharedPropertyUpdatedEvent) {
        	propertyUpdatedEvent((ISharedPropertyUpdatedEvent)event);
        }
        else 
        if ( event instanceof IStaticWorldObjectUpdatedEvent ) {
        	staticObjectUpdatedEvent((IStaticWorldObjectUpdatedEvent)event);
        }
        else
        if (event instanceof IWorldEventWrapper) {
            raiseEvent(((IWorldEventWrapper) event).getWorldEvent());
        } else
        if (event instanceof IWorldEvent) {
          	raiseEvent((IWorldEvent)event);
        } else {
            throw new PogamutException("Unsupported event type received (" + event.getClass() + "): " + event, this);
        }
    }
    
    /**
     * Catches exceptions. If exception is caught, it calls {@link ComponentController}.fatalError() and this.kill(). 
     */
    @Override
    protected void raiseEvent(IWorldEvent event) {
    	try {
    		super.raiseEvent(event);
    	} catch (Exception e) {
    		this.controller.fatalError("Exception raising event " + event, e);
    		this.kill();
    	}
    }
    
    Object objectMutex = new Object();
    
    public void addMsgClass(WorldObjectId id, Class msgClass)
    {
    	synchronized(objectMutex)
    	{
    		this.idClassMap.put(id, msgClass);
    	}
    }
    
    /**
     * Called from {@link EventDrivenWorldView#innerNotify(IWorldChangeEvent)} if the event is {@link IWorldObjectUpdatedEvent}
     * to process it.
     * <p><p>
     * MUST NOT BE CALLED CONCURRENTLY - SINGLE THREAD AT THIS POINT ONLY! MUST BE ENFORCED FROM THE OUTSIDE!
     * 
     * @param updateEvent
     */
    protected void sharedObjectUpdatedEvent(ISharedWorldObjectUpdatedEvent updateEvent) {
        //update results
        boolean created = false;
        boolean updated = false;
        boolean destroyed = false;
        
       if ( !syncIdClassMap.containsKey(updateEvent.getId() ))
       {
    	   NullCheck.check(updateEvent.getCompositeObjectClass(), "CompositeClass");
    	   syncIdClassMap.put(updateEvent.getId(), updateEvent.getCompositeObjectClass());
       }
        
        for ( ISharedPropertyUpdatedEvent propertyEvent : updateEvent.getPropertyEvents() )
        {
        	ISharedProperty property = currentSharedProperties.get(updateEvent.getTeamId(), updateEvent.getId(), propertyEvent.getPropertyId());
        	ISharedProperty copy = null;
        	
        	if (property != null)
        	{
        		copy = copyProperty(property);
        	};
        	
        	ISharedPropertyUpdateResult updateResult = propertyEvent.update(copy);
        	
        	switch (updateResult.getResult())
        	{
        	case CREATED:
        		created = true;
        		propertyCreated(updateResult.getProperty(), updateEvent.getTeamId());
        		break;
        	case UPDATED:
        		if ( updateResult.getProperty() != copy)
        		{
               		throw new PogamutException("Update event " + updateEvent + " did not return the same instance of the object (result UPDATED).", this);
        		}
        		//add old property to maps
        		updated = true;
        		addOldSharedProperty( property, updateEvent.getTeamId(), propertyEvent.getSimTime());
        		//update the value
        		propertyUpdated(copy, updateEvent.getTeamId());
        		break;
        	case DESTROYED:
        		//add value to old object maps
        		addOldSharedProperty(property, updateEvent.getTeamId(), propertyEvent.getSimTime());
        		//remove from current maps
        		removeSharedProperty(property, updateEvent.getTeamId());        		
        	case SAME:
        		break;
        	default:
        		throw new PogamutException("Unhandled object update result " + updateResult.getResult() + " for the object " + updateEvent.getId() + "." + "Property : " + property, this);
        	}
        }
        //now all properties are updated, let's raise the correct object events
       if ( created )
       {
    	   objectCreated( getShared(updateEvent.getTeamId(), updateEvent.getId(), TimeKey.get(updateEvent.getSimTime()) ), updateEvent.getSimTime());
    	   objectUpdated(updateEvent.getTeamId(), updateEvent.getId(), updateEvent.getSimTime());
       }
       else if ( updated )
       {
    	   objectUpdated(updateEvent.getTeamId(), updateEvent.getId(), updateEvent.getSimTime());
       }
       else if ( destroyed )
       {
    	   //remove cached object
    	   objectDestroyed( getShared(updateEvent.getTeamId(), updateEvent.getId(), TimeKey.get(updateEvent.getSimTime())), updateEvent.getSimTime());
       }
    }
    
    /**
     * MUST NOT BE CALLED CONCURRENTLY - SINGLE THREAD AT THIS POINT ONLY! MUST BE ENFORCED FROM THE OUTSIDE!
     * 
     * @param event
     */
    protected void propertyUpdatedEvent( ISharedPropertyUpdatedEvent event)
    {
    	ISharedProperty property = null;
    	ISharedProperty copy = null;
    	
    	property = getSharedProperty(event.getPropertyId(), event.getTeamId(), TimeKey.get( event.getSimTime() )); //??
    	
    	if ( property != null)
    	{
    		copy = property.clone();
    	}
    	ISharedPropertyUpdateResult result = event.update(copy);
    	switch ( result.getResult() )
    	{
    	case CREATED:
    		propertyCreated( result.getProperty(), event.getTeamId() );
    		break;
    	case UPDATED:
    		addOldSharedProperty(property, event.getTeamId(), event.getSimTime());
    		propertyUpdated(copy, event.getTeamId() );
    		break;
    	case DESTROYED:
    		addOldSharedProperty(property, event.getTeamId(), event.getSimTime() );
    		removeSharedProperty(property, event.getTeamId() );
    		break;
    	case SAME:
    		break;
    	default:
    		throw new PogamutException("Unexpected update result " + result.getResult() + " for property " + property.toString() + " .", this);    	
    	}
    }
    
    /**
     * Manages updating static objects,
     * only possible event types are CREATED and DESTROYED, any other event type raises an exception.
     * <p><p>
     * MUST NOT BE CALLED CONCURRENTLY - SINGLE THREAD AT THIS POINT ONLY! MUST BE ENFORCED FROM THE OUTSIDE!
     * 
     * @param event
     */
    protected void staticObjectUpdatedEvent( IStaticWorldObjectUpdatedEvent event)
    {
    	IStaticWorldObject current = super.getStatic( event.getId());
    	IWorldObjectUpdateResult<IStaticWorldObject> result = event.update(current);
    	switch ( result.getResult() )
    	{
    	case CREATED:
    		super.addStaticWorldObject( result.getObject() );
    		break;
    	case DESTROYED:
    		super.removeStaticWorldObject( result.getObject() );
    		break;
    	case SAME:
    		return;
    	default:
    		throw new PogamutException("Wrong static object update result " + result.getResult() + " for the object " + result.getObject().toString() + " . ", this);
    	}
    }
    
    /**
     * If team is null, the property will be created for all teams.
     * <p><p>
     * MUST NOT BE CALLED CONCURRENTLY - SINGLE THREAD AT THIS POINT ONLY! MUST BE ENFORCED FROM THE OUTSIDE!
     * 
     * @param property
     * @param team
     */
    protected void propertyCreated(ISharedProperty property, ITeamId team)
    {
    	if (team == null)
    	{
    		addSharedProperty(property);
    		return;
    	}
    	addSharedProperty(property, team );
    	//TODO event raise
    }
    
    /**
     * Updates the property.
     * This method is not responsible for adding old versions of the object.
     * <p><p>
     * MUST NOT BE CALLED CONCURRENTLY - SINGLE THREAD AT THIS POINT ONLY! MUST BE ENFORCED FROM THE OUTSIDE!
     * 
     * @param property
     * @param team
     */
    protected void propertyUpdated(ISharedProperty property, ITeamId team)
    {
    	currentSharedProperties.put(team,property.getObjectId(),property.getPropertyId(),property);
    	//TODO raise events
    }

    /**
     * Must be called whenever an object was created, raises correct events.
     * <p><p>
     * Might be overridden to provide different behavior.
     * <p><p>
     * MUST NOT BE CALLED CONCURRENTLY - SINGLE THREAD AT THIS POINT ONLY! MUST BE ENFORCED FROM THE OUTSIDE!
     * 
     * @param obj
     */
    protected void objectCreated(ISharedWorldObject obj, long time) {
    	//no event raise here as of now...
    	//so far we only notify local worldview with update events
    }
    
    /**
     * Must be called whenever an object was updated - raises correct event.
     * <p><p>
     * Might be overridden to provide a mechanism that will forbid
     * update of certain objects (like items that can't move).
     * <p><p>
     * MUST NOT BE CALLED CONCURRENTLY - SINGLE THREAD AT THIS POINT ONLY! MUST BE ENFORCED FROM THE OUTSIDE!
     *
     * @param obj
     */
    protected void objectUpdated(ITeamId teamId, WorldObjectId objectId, long time)
    {
       //notify local worldView listeners
    	for ( TeamedAgentId agentId : this.localWorldViews.keySet() )
    	{
    		if ( agentId.getTeamId().equals(teamId))
    		{
    			ILocalWorldView wv = localWorldViews.get(agentId);
    			if ( wv instanceof BatchAwareLocalWorldView)
    			{
    				//buffer the objectEvent
    				((BatchAwareLocalWorldView)wv).bufferObjectEvent(objectId, EventType.UPDATED, time);
    			}
    		}
    	}
    }
    
    /**
     * Must be called whenever an object was destroyed - raises correct events.
     * <p><p>
     * Might be overriden to provide different behavior.
     * <p><p>
     * MUST NOT BE CALLED CONCURRENTLY - SINGLE THREAD AT THIS POINT ONLY! MUST BE ENFORCED FROM THE OUTSIDE!
     * 
     * @param obj
     */
    protected void objectDestroyed(IWorldObject obj, long time) {
        //raiseEvent(new WorldObjectDestroyedEvent<IWorldObject>(obj, time));        
    }

	
}
