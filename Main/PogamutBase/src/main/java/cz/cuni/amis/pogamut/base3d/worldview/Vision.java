package cz.cuni.amis.pogamut.base3d.worldview;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.google.inject.Inject;

import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObject;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectListener;
import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.pogamut.base3d.worldview.object.IViewable;
import cz.cuni.amis.utils.ClassUtils;

/**
 * Vision is taking care about the objects the bot might see (of all implementing IViewable interface).
 * 
 * @author Jimmy
 *
 */
@AgentScoped
public class Vision implements IWorldObjectListener<IViewable> {
	
	// TODO: (Jakub) hide WorldViewEventListener<IWorldObjectEvent> into
	//               inner private class
	
	/**
	 * Synchronized map that holds all the objects we may currently see according to their type
	 * in the maps.
	 * <p><p>
	 * Due to nature of generics we can't typed this field, it holds maps of objects
	 * according to their classes. 
	 * <p>
	 * Map &lt; Class, Map &lt; IWorldViewObjectId, IWorldObject of Class &gt; &gt;
	 */
	private Map seeObjects =
		Collections.synchronizedMap(
			new HashMap()
		);
	
	/**
	 * E.g. worldObjects but contains immutable (unmodifiable) version of map.
	 */
	private Map immutableSeeObjects =
		Collections.unmodifiableMap(
			seeObjects
		);
	
	/**
	 * Synchronized map of all the world objects that the agent can currently see.
	 */
	private Map<WorldObjectId, IWorldObject> seeObjectsId =
		Collections.synchronizedMap(
			new HashMap<WorldObjectId, IWorldObject>()
		);
	
	@Inject
	public Vision(IWorldView worldView, IAgentLogger logger) {
		// hook itself to listen on all events that are raised on viewable objects
		worldView.addObjectListener(IViewable.class, IWorldObjectEvent.class, this);		
	}
	
	@Override
	public void notify(IWorldObjectEvent<IViewable> event) {				
		IWorldObject obj = seeObjectsId.get(event.getId());
		if (obj != null) {
			if (((IViewable)obj).isVisible()) {
				if (seeObjects.get(event.getId()) == null) {
					addSeeObject(obj);
				}
			} else {
				if (seeObjects.get(event.getId()) != null) {
					removeSeeObject(obj);
				}
			}
		} else { 
			IViewable object = event.getObject();
			if (object.isVisible()) {
				if (seeObjects.get(event.getId()) == null) {
					addSeeObject(obj);
				}
			} else {
				if (seeObjects.get(event.getId()) != null) {
					removeSeeObject(obj);
				}
			}
		}
	}
	
	/**
	 * Used to introduce new object category into worldObjects and immutableWorldObjects.
	 * <p><p>
	 * It will create new synchronized Map&lt;IWorldViewObjectId, T&gt; in the worldObjects and it's immutable
	 * counterpart in immutableWorldObjects under key of 'cls'.
	 * <p><p>
	 * Returns modifiable version of created map.
	 * 
	 * @param <T>
	 * @param cls
	 * @return
	 */
	protected synchronized <T> Map<WorldObjectId, T> addNewObjectCategory(Class<T> cls) {
		Map<WorldObjectId, T> objects = Collections.synchronizedMap(new HashMap<WorldObjectId, T>());			
		seeObjects.put(cls, objects);
		immutableSeeObjects.put(cls, Collections.unmodifiableMap(objects));
		return objects;
	}
	
	/**
	 * Method that adds a new world object to the object maps. It will be called from
	 * the descendant whenever new object appears in the world view.
	 * @param worldObject
	 */
	protected void addSeeObject(IWorldObject seeObject) {
		seeObjectsId.put(seeObject.getId(), seeObject);
		for (Class cls : ClassUtils.getSubclasses(seeObject.getClass())) {
			Map objects;
			synchronized(seeObjects) {
				objects = (Map) seeObjects.get(cls);
				if (objects == null) objects = addNewObjectCategory(cls);
			}
			objects.put(seeObject.getId(), seeObject);
		}		
	}
	
	/**
	 * Returns world object of the given id or null if the object is not yet in the world view.
	 * @param objectId
	 * @return
	 */
	protected IWorldObject getSeeObject(WorldObjectId objectId) {
		return seeObjectsId.get(objectId);
	}
	
	/**
	 * Removes world object from the world view - this will be called from the descendants
	 * of the AbstractWorldView whenever world object should disappear from the world view.
	 * @param worldObject
	 */
	protected void removeSeeObject(IWorldObject worldObject) {
		seeObjectsId.remove(worldObject.getId());
		for (Class cls : ClassUtils.getSubclasses(worldObject.getClass())) {
			Map objects = (Map) seeObjects.get(cls);
			if (objects != null) {
				objects.remove(worldObject.getId());
			}		
		}			
	}

	/**
	 * Returns map of all objects the agent can currently see. 
	 * <p><p>
	 * WARNING: returns immutable map!
	 * @return
	 */
	public Map<Class, Map<WorldObjectId, IWorldObject>> getSee() {
		return immutableSeeObjects;
	}

	/**
	 * Returns map map of all objects of a certain type the agent can currently see.
	 * <p><p>
	 * WARNING: returns immutable map!
	 *  
	 * @param type
	 * @return
	 */
	public <T> Map<WorldObjectId, T> getSee(Class<T> type) {
		// WE HAVE TO SYNCHRONIZE on seeObjects NOT immutableSeeObjects,
		// because we're adding new category the world objects depends on that!
		// see addSeeObject()
		synchronized(seeObjects) {
			Map<WorldObjectId, T> objects = (Map<WorldObjectId, T>) immutableSeeObjects.get(type);
			if (objects == null) {
				addNewObjectCategory(type);
				return (Map<WorldObjectId, T>) immutableSeeObjects.get(type);
			} else {
				return objects;
			}
		}
	}
	
	/**
	 * If agents sees item of 'id' it returns it instances, otherwise it returns null.
	 * @param id
	 * @return
	 */
	public IWorldObject getSee(WorldObjectId id) {
		return seeObjectsId.get(id);
	}
	
}
