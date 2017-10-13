package cz.cuni.amis.pogamut.base3d.worldview.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.google.inject.name.Named;

import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdateResult;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.impl.EventDrivenWorldView;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObject;
import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectDestroyedEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectFirstEncounteredEvent;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencies;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.pogamut.base3d.worldview.IVisionWorldView;
import cz.cuni.amis.pogamut.base3d.worldview.object.IViewable;
import cz.cuni.amis.pogamut.base3d.worldview.object.event.WorldObjectAppearedEvent;
import cz.cuni.amis.pogamut.base3d.worldview.object.event.WorldObjectDisappearedEvent;
import cz.cuni.amis.utils.ClassUtils;
import cz.cuni.amis.utils.exception.PogamutException;
import cz.cuni.amis.utils.maps.HashMapMap;

public class VisionWorldView extends EventDrivenWorldView implements IVisionWorldView {
	
	public static final String WORLDVIEW_DEPENDENCY = "VisionWorldViewDependency";
	
	/**
	 * Unsynchronized map that holds all visible world objects according to their type
	 * in the maps.
	 * <p><p>
	 * Due to nature of generics we can't typed this field, it holds maps of objects
	 * according to their classes. 
	 * <p>
	 * Map &lt; Class, Map &lt; IWorldObjectId, IWorldObject of Class &gt; &gt;
	 */
	private HashMapMap<Class, WorldObjectId, IViewable> worldVisibleObjects = 
		new HashMapMap<Class, WorldObjectId, IViewable>();
	
	/**
	 * Synchronized version of world visible objects.
	 */
	private Map<Class, Map<WorldObjectId, IViewable>> syncWorldVisibleObjects = 
		Collections.synchronizedMap(worldVisibleObjects);
			
	/**
	 * Synchronized map of all visible world objects that are present in the worldview.
	 */
	private Map<WorldObjectId, IViewable> visibleObjects =
		Collections.synchronizedMap(new HashMap<WorldObjectId, IViewable>());

	public VisionWorldView(@Named(WORLDVIEW_DEPENDENCY) ComponentDependencies dependencies, IComponentBus bus, IAgentLogger log) {
		super(dependencies, bus, log);
	}
	
	@Override
	protected void cleanUp() {
		super.cleanUp();
		synchronized(worldVisibleObjects) {
			worldVisibleObjects.clear();
		}
		synchronized(visibleObjects) {
			visibleObjects.clear();
		}
		synchronized(notifyEventsList) {
			notifyEventsList.clear();
		}
	}
	
	@Override
	public Map<Class, Map<WorldObjectId, IViewable>> getAllVisible() {
		return syncWorldVisibleObjects;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends IViewable> Map<WorldObjectId, T> getAllVisible(Class<T> type) {
		return (Map<WorldObjectId, T>) syncWorldVisibleObjects.get(type);
	}

	@Override
	public Map<WorldObjectId, IViewable> getVisible() {
		return visibleObjects;
	}

	@Override
	public IViewable getVisible(WorldObjectId id) {
		return visibleObjects.get(id);
	}
	
	//
	//
	// PROTECTED METHODS
	//
	//
	
	/**
	 * Method that adds a world object to the visible object maps.
	 * <p><p>
	 * Synchronized!
	 * 
	 * @param worldObject
	 */
	protected synchronized void addVisibleObject(IViewable worldObject) {
		visibleObjects.put(worldObject.getId(), worldObject);
		for (Class cls : ClassUtils.getSubclasses(worldObject.getClass())) {
			syncWorldVisibleObjects.get(cls).put(worldObject.getId(), worldObject);
		}		
	}
	
	/**
	 * Removes world object from the visible world maps.
	 * <p><p>
	 * Synchronized!
	 * 
	 * @param worldObject
	 */
	protected synchronized void removeVisibleObject(IViewable worldObject) {
		visibleObjects.remove(worldObject.getId());
		for (Class<?> cls : ClassUtils.getSubclasses(worldObject.getClass())) {
			syncWorldVisibleObjects.get(cls).remove(worldObject.getId());
		}			
	}	
	
	//
	//
	// EVENT PROCESSING
	//
	//
	
	/**
	 * It additionally handles {@link IViewable} objects automatically raising {@link WorldObjectAppearedEvent} and
	 * {@link WorldObjectDisappearedEvent}.
	 */
	@Override
	protected void objectUpdatedEvent(IWorldObjectUpdatedEvent updateEvent) {
		if (updateEvent == null) {
			log.warning("Could not process object update, as its ID is null: " + updateEvent);
			return;
		}
		
        IWorldObject obj = get(updateEvent.getId());
        
        boolean wasVisible = obj != null && obj instanceof IViewable && ((IViewable)obj).isVisible();        
        
        IWorldObjectUpdateResult updateResult = updateEvent.update(obj);
        
        if (updateResult == null) {
        	throw new PogamutException("Update result is null (updateEvent.update(obj) == null)! Cannot update object of ID " + updateEvent.getId() + "!", this);
        }
        
        switch (updateResult.getResult()) {
        case CREATED:            	
            objectCreated(updateResult.getObject());
            return;
        case UPDATED:
        	if (updateResult.getObject() != obj) {
        		throw new PogamutException("Update event " + updateEvent + " does not returned the same instance of the object (result UPDATED).", this);
        	}
        	if (obj instanceof IViewable) {
        		boolean isVisible = ((IViewable)obj).isVisible();
        		if (wasVisible) {
        			objectUpdated(obj);
        			if (!isVisible) {
        				objectDisappeared((IViewable) obj);
        			}
        		} else {
        			if (isVisible) {
        				objectAppeared((IViewable) obj);	
        			}
        			objectUpdated(obj);
        		}        	
        	} else {
                    objectUpdated(obj);
                }

        	return;
        case SAME:
        	return;
        case DESTROYED:
        	objectDestroyed(obj);
            return;
        default:
        	throw new PogamutException("Unhandled object update result " + updateResult.getResult() + " for the object " + obj + ".", this);
        }
    }

	/**
     * Additionally, it provides handling of {@link IViewable} objects raising {@link WorldObjectAppearedEvent} automatically (if object is visible).
     */
	@Override
    protected void objectCreated(IWorldObject obj) {
    	addWorldObject(obj);
    	raiseEvent(new WorldObjectFirstEncounteredEvent<IWorldObject>(obj, obj.getSimTime()));
    	if (obj instanceof IViewable) {
    		IViewable viewable = (IViewable)obj;
    		if (viewable.isVisible()) {
    			objectAppeared(viewable);
    		}
    	}
        objectUpdated(obj);
    }
	
	/**
	 * Called whenever the object appears in the agent's FOV.
	 * @param obj
	 */
	protected void objectAppeared(IViewable obj) {
		addVisibleObject(obj);
		raiseEvent(new WorldObjectAppearedEvent<IViewable>(obj, obj.getSimTime()));
	}
	
	/**
	 * Called whenever the object disappears from the agent's FOV.
	 * @param obj
	 */
	protected void objectDisappeared(IViewable obj) {
		removeVisibleObject(obj);
		raiseEvent(new WorldObjectDisappearedEvent<IViewable>(obj, obj.getSimTime()));
	}
       
    /**
     * Additionally it handles {@link IViewable} objects automatically raising {@link WorldObjectDisappearedEvent} if object was visible
     * before it was destroyed.
     * 
     * @param obj
     */
    protected void objectDestroyed(IWorldObject obj) {
    	if (obj instanceof IViewable) {
    		IViewable viewable = (IViewable)obj;
    		if (viewable.isVisible()) {
    			objectDisappeared(viewable);
    		}
    	}
    	removeWorldObject(obj);
        raiseEvent(new WorldObjectDestroyedEvent<IWorldObject>(obj, obj.getSimTime()));        
    }

	
}
