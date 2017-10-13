package cz.cuni.amis.pogamut.multi.communication.worldview.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdateResult;
import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencies;
import cz.cuni.amis.pogamut.base.component.lifecyclebus.ILifecycleBus;
import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.pogamut.base3d.worldview.object.IViewable;
import cz.cuni.amis.pogamut.base3d.worldview.object.event.WorldObjectDisappearedEvent;
import cz.cuni.amis.pogamut.multi.agent.ITeamedAgentId;
import cz.cuni.amis.pogamut.multi.communication.translator.event.ICompositeWorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.multi.communication.translator.event.ILocalWorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.multi.communication.worldview.ISharedWorldView;
import cz.cuni.amis.pogamut.multi.communication.worldview.IVisionLocalWorldView;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ILocalViewable;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ILocalWorldObject;
import cz.cuni.amis.pogamut.multi.utils.timekey.TimeKey;
import cz.cuni.amis.utils.ClassUtils;
import cz.cuni.amis.utils.exception.PogamutException;
import cz.cuni.amis.utils.maps.WeakHashMapMap;

/**
 * VisionLocalWorldView manages information about all objects currently in the bot's FOV (field-of-view)
 * by implementing methods from {@link IVisionLocalWorldView} interface.
 * 
 * @author srlok
 *
 */
@AgentScoped
public abstract class VisionLocalWorldView extends EventDrivenLocalWorldView implements IVisionLocalWorldView {

	public VisionLocalWorldView(
			ComponentDependencies dependencies,
			ILifecycleBus bus, IAgentLogger logger,
			ISharedWorldView parentWorldView, ITeamedAgentId agentId
	) {
		super(dependencies, bus, logger, parentWorldView, agentId);
	}

	@Override
	public void notify(IWorldChangeEvent event)
	{
		if ( event instanceof ILocalWorldObjectUpdatedEvent)
		{
			objectUpdatedEvent( (ILocalWorldObjectUpdatedEvent)event );
		}
		else if ( event instanceof ICompositeWorldObjectUpdatedEvent)
		{
			objectUpdatedEvent( ((ICompositeWorldObjectUpdatedEvent)event).getLocalEvent() );
		}
		else
		{
			super.notify(event);
		}
	}
	
	/**
	 * Map of all currently visible objects.
	 */
	protected Map<TimeKey, Map<WorldObjectId, IViewable>> visibleMap = 
		new WeakHashMap<TimeKey, Map<WorldObjectId, IViewable>>();
	
	/**
	 * Synchronized version of visible objects.
	 */
	protected Map< TimeKey, Map<WorldObjectId, IViewable>> syncVisibleMap = 
		Collections.synchronizedMap(visibleMap);
	
	/**
	 * Map of all currently visible objects, sorted according to their classes.
	 */
	@SuppressWarnings("rawtypes")
	protected Map<TimeKey, Map<Class, Map<WorldObjectId, IViewable>>> visibleClassMap =
		new WeakHashMapMap<TimeKey, Class, Map<WorldObjectId, IViewable>>();

	/**
	 * Synchronized version of visible objects sorted according to class.
	 */
	@SuppressWarnings("rawtypes")
	protected Map<TimeKey, Map< Class, Map<WorldObjectId,IViewable>>> syncVisibleClassMap =
		Collections.synchronizedMap(visibleClassMap);
	
	@Override
	protected void objectUpdatedEvent(ILocalWorldObjectUpdatedEvent updateEvent) 
	{
		//log.fine("VisionLocalWorldView notify : " + updateEvent.getId() )
		
        ILocalWorldObject obj = getMostRecentLocalWorldObject( updateEvent.getId() );
        
        boolean oldVisible = false;
        boolean isViewable = false;
        
        ILocalWorldObject copy = null;
        //if old timeKeys are held, store the original value, create a new copy, which will be updated
        if ( obj != null)
        {
        	if ( obj instanceof ILocalViewable) 
        	{
        		oldVisible = ((ILocalViewable)obj).isVisible();
        		isViewable = true;
        	}
        	copy = obj.clone();
        }
        else //may be created event
        {
        	copy = null;
        }
       
        IWorldObjectUpdateResult<ILocalWorldObject> updateResult = updateEvent.update(copy);
        //log.fine("Update result for id :" + updateEvent.getId() + " ; : " + updateResult.getResult() );
        switch (updateResult.getResult()) {
        case CREATED:            	
            objectCreated(updateResult.getObject(), updateEvent.getSimTime());
            return;
        case UPDATED:
        	if (updateResult.getObject() != copy) {
        		throw new PogamutException("Update event " + updateEvent + " does not returned the same instance of the object (result UPDATED).", this);
        	}
        	
        	super.addOldLocalWorldObject(obj, updateEvent.getSimTime());        	
        	
        	boolean appearedDisappeared = false; //if the object will appear or disappear we won't need to call the objectUpdatedMethod
        	
        	if (isViewable)
        	{
        		boolean visible = ((ILocalViewable)copy).isVisible();
        		if ( visible != oldVisible)
        		{
        			appearedDisappeared = true;
        			if ( visible )
        			{
        				objectAppeared((ILocalViewable)copy, updateEvent.getSimTime());
        			}
        			else
        			{
        				objectDisappeared((ILocalViewable)copy, updateEvent.getSimTime());
        			}
        		}
        		else
        		{
        			//this is here because we have to add the visible objects to a map with greater timeKey
        			//this is more effective then copying and then disappearing later
        			if ( visible )
        			{
        				addVisible((ILocalViewable)obj, updateEvent.getSimTime());
        			}
        		}
        	}
        	
        	if ( !appearedDisappeared )
        	objectUpdated(copy, updateEvent.getSimTime());	
        	
        	actLocalWorldObjects.put(copy.getId(), copy);    	
        	
        	return;
        case SAME:
        	if (isViewable && oldVisible)
        	{
        		addVisible((ILocalViewable)obj, updateEvent.getSimTime());
        	}
        	return;
        case DESTROYED:
        	
        	super.addOldLocalWorldObject(obj, updateEvent.getSimTime());
        	objectDestroyed(copy, updateEvent.getSimTime());
        	
            return;
        default:
        	throw new PogamutException("Unhandled object update result " + updateResult.getResult() + " for the object " + obj + ".", this);
        }
    }
	
	
	protected void objectCreated( ILocalWorldObject obj , long time )
	{
		if ( obj instanceof ILocalViewable)
		{
			if ( ((ILocalViewable)obj).isVisible() )
			{
				objectAppeared( (ILocalViewable)obj, time );
			}
		}
		
		super.objectCreated(obj, time);
	}
	
	
	protected void objectDestroyed( ILocalWorldObject obj , long time)
	{
		/*if ( obj instanceof ILocalViewable)
		{
			removeVisible((ILocalViewable)obj , time);
		}*/
		super.objectDestroyed(obj, time);
	}
	
	/**
	 * Handles events for making the object visible.
	 * @param obj
	 */
	protected void objectAppeared( ILocalViewable obj, long time )
	{
		//log.info("Object appeared : " + obj);
		addVisible(obj, time);
	}
	
	/**
	 * Handles events for making the object not visible.
	 * @param obj
	 */
	protected void objectDisappeared( ILocalViewable obj, long time )
	{
		
	}

	
	/**
	 * Adds the provided object as visible into all visibleMaps int the worldView.
	 * Note that since the cached visible objects are composite and the parameter for this method is a local object,
	 * only the id and the getCompositeClass of the object are actually used.
	 * @param obj
	 */
	protected synchronized void addVisible( ILocalViewable obj , long time )
	{
		//log.info("["+time+"]AddingVisible of id : " + obj.getId() + " ; class : " + obj.getCompositeClass() );
		synchronized(visibleMap)
		{
			Map map = visibleMap.get(TimeKey.get(time));
			if ( map == null )
			{
				map = new LazyCompositeObjectMap<IViewable>( time );
				visibleMap.put(TimeKey.get(time), map );
			}
			((LazyCompositeObjectMap<IViewable>)visibleMap.get(TimeKey.get(time))).addKey(obj.getId());
		}
		synchronized( visibleClassMap )
		{
			Map clsMap = visibleClassMap.get(time);
			if ( clsMap == null )
			{
				clsMap = new HashMap<Class,Map>();
				visibleClassMap.put(TimeKey.get(time),clsMap);
			}
			for ( Class cls : ClassUtils.getSubclasses(obj.getCompositeClass()) )
			{
				LazyCompositeObjectMap<IViewable> map = ((LazyCompositeObjectMap<IViewable>)visibleClassMap.get(TimeKey.get(time)).get(cls));
				if ( map == null )
				{
					map = new LazyCompositeObjectMap<IViewable>( getCurrentTimeKey().getTime() );
					visibleClassMap.get(TimeKey.get(time)).put(cls, map);
				}
				map.addKey(obj.getId());
			}
		}
	}
	
	/**
	 * Removes object of the same objectId as the provided localObject from visible maps.
	 * Note that the provided ILocalViewable object has to implement the getCompositeClass() method to return the correct composite object class.
	 * @param obj
	 */
	protected synchronized void removeVisible( ILocalViewable obj, long time )
	{
		synchronized(visibleMap)
		{
			((LazyCompositeObjectMap<IViewable>)visibleMap.get(TimeKey.get(time))).remove(obj.getId());
		}
		synchronized( visibleClassMap )
		{
			for ( Class cls : ClassUtils.getSubclasses(obj.getCompositeClass()) )
			{
				LazyCompositeObjectMap<IViewable> map = ((LazyCompositeObjectMap<IViewable>)visibleClassMap.get(TimeKey.get(time)).get(cls));
				if ( map == null )
				{
					map = new LazyCompositeObjectMap<IViewable>( getCurrentTimeKey().getTime() );
					visibleClassMap.get(TimeKey.get(time)).put(cls, map);
				}
				map.remove(obj.getId());
			}
		}
	}
		
	@Override
	public Map<Class, Map<WorldObjectId, IViewable>> getAllVisible() {
		
		return syncVisibleClassMap.get(currentTimeKey);
	}

	@Override
	public <T extends IViewable> Map<WorldObjectId, T> getAllVisible(
			Class<T> type) 
	{
		
		Map<WorldObjectId,T> map = (Map<WorldObjectId, T>) syncVisibleClassMap.get(currentTimeKey).get(type);
		if ( map == null )
		{
			map = new LazyCompositeObjectMap<T>( currentTimeKey.getTime() );
		}
		return map;
	}

	@Override
	public Map<WorldObjectId, IViewable> getVisible() {
		return syncVisibleMap.get(currentTimeKey);
	}

	@Override
	public IViewable getVisible(WorldObjectId id) {
		return syncVisibleMap.get(currentTimeKey).get(id);
	}
	
}
