package cz.cuni.amis.pogamut.multi.communication.worldview.impl;

import java.util.Map;

import com.google.inject.Inject;

import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObject;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.component.bus.exception.ComponentNotRunningException;
import cz.cuni.amis.pogamut.base.component.bus.exception.ComponentPausedException;
import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;
import cz.cuni.amis.pogamut.base3d.ILockableVisionWorldView;
import cz.cuni.amis.pogamut.base3d.worldview.object.IViewable;
import cz.cuni.amis.pogamut.multi.communication.worldview.ILocalWorldView;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ICompositeWorldObject;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ILocalWorldObject;
import cz.cuni.amis.utils.exception.PogamutInterruptedException;
import cz.cuni.amis.utils.token.IToken;

/**
 * This class serves as an adapter for ILocalWorldView to satisfy the IWorldView interface,
 * all methods of this worldView only call the appropriate methods on the backing localWorldView
 * @author srlok
 *
 */
@AgentScoped
public class LocalWorldViewAdapter implements IWorldView, ILockableVisionWorldView{

	private BatchAwareLocalWorldView localWV;
	
	@Inject
	public LocalWorldViewAdapter( BatchAwareLocalWorldView localWV )
	{
		this.localWV = localWV;
	}
	
	public long getSimTime()
	{
		return localWV.getCurrentTimeKey().getTime();
	}
	
	@Override
	public void notify(IWorldChangeEvent event) throws ComponentNotRunningException, ComponentPausedException {
		localWV.notify(event);
	}
	
	@Override
	public void notifyAfterPropagation(IWorldChangeEvent event) throws ComponentNotRunningException, ComponentPausedException {
		localWV.notifyAfterPropagation(event);
	}

	@Override
	public void notifyImmediately(IWorldChangeEvent event)
			throws ComponentNotRunningException, ComponentPausedException {
		localWV.notifyImmediately(event);
	}

	@Override
	public IToken getComponentId() {
		return localWV.getComponentId();
	}

	@Override
	public IComponentBus getEventBus() {
		return localWV.getEventBus();
	}

	@Override
	public void addEventListener(Class<?> eventClass,
			IWorldEventListener<?> listener) {
		
		localWV.addEventListener(eventClass, listener);
		
	}

	@Override
	public void addObjectListener(Class<?> objectClass,
			IWorldObjectEventListener<?, ?> listener) {

		localWV.addObjectListener(objectClass,listener);
		
	}

	@Override
	public void addObjectListener(Class<?> objectClass, Class<?> eventClass,
			IWorldObjectEventListener<?, ?> listener) {
		localWV.addObjectListener(objectClass, eventClass, listener);
		
	}

	@Override
	public void addObjectListener(WorldObjectId objectId,
			IWorldObjectEventListener<?, ?> listener) {
		localWV.addObjectListener(objectId, listener);
		
	}

	@Override
	public void addObjectListener(WorldObjectId objectId, Class<?> eventClass,
			IWorldObjectEventListener<?, ?> listener) {
		localWV.addObjectListener(objectId, eventClass, listener);
		
	}

	@Override
	public void removeEventListener(Class<?> eventClass,
			IWorldEventListener<?> listener) {
		localWV.removeEventListener(eventClass, listener);
		
	}

	@Override
	public void removeObjectListener(Class<?> objectClass,
			IWorldObjectEventListener<?, ?> listener) {
		
		localWV.removeObjectListener(objectClass, listener);
		
	}

	@Override
	public void removeObjectListener(Class<?> objectClass, Class<?> eventClass,
			IWorldObjectEventListener<?, ?> listener) {
		
		localWV.removeObjectListener(objectClass, eventClass, listener);
		
	}

	@Override
	public void removeObjectListener(WorldObjectId objectId,
			IWorldObjectEventListener<?, ?> listener) {
		
		localWV.removeObjectListener(objectId, listener);
		
	}

	@Override
	public void removeObjectListener(WorldObjectId objectId,
			Class<?> eventClass, IWorldObjectEventListener<?, ?> listener) {
		localWV.removeObjectListener(objectId, eventClass, listener);
		
	}

	@Override
	public void removeListener(IWorldEventListener<?> listener) {
		localWV.removeListener(listener);
	}

	@Override
	public boolean isListening(Class<?> eventClass,
			IWorldEventListener<?> listener) {
		return localWV.isListening(eventClass, listener);
	}

	@Override
	public boolean isListening(Class<?> objectClass,
			IWorldObjectEventListener<?, ?> listener) {
		return localWV.isListening(objectClass, listener);
	}

	@Override
	public boolean isListening(Class<?> objectClass, Class<?> eventClass,
			IWorldObjectEventListener<?, ?> listener) {
		return localWV.isListening(objectClass, eventClass, listener);
	}

	@Override
	public boolean isListening(WorldObjectId objectId,
			IWorldObjectEventListener<?, ?> listener) {
		return localWV.isListening(objectId, listener);
	}

	@Override
	public boolean isListening(WorldObjectId objectId, Class<?> eventClass,
			IWorldObjectEventListener<?, ?> listener) {
		return localWV.isListening(objectId, eventClass, listener);
	}

	@Override
	public boolean isListening(IWorldEventListener<?> listener) {
		return localWV.isListening(listener);
	}

	@Override
	public Map<Class, Map<WorldObjectId, IWorldObject>> getAll() {
		return ( Map )localWV.getAll();
	}

	@Override
	public <T extends IWorldObject> Map<WorldObjectId, T> getAll(Class<T> type) {
		return ( Map ) localWV.getAll(type);
	}

	@Override
	public <T extends IWorldObject> T getSingle(Class<T> cls) {
		return localWV.getSingle(cls);
	}

	@Override
	public Map<WorldObjectId, IWorldObject> get() {
		return (Map) localWV.get();
	}

	@Override
	public IWorldObject get(WorldObjectId id) {
		return localWV.get(id);
	}

        @Override
        public <T extends IWorldObject> T get(WorldObjectId objectId, Class<T> clazz) {
            //cannot directly delegate to localWV.get(WorldObjectId, Class<T>), because the type constraints are not compatible
            IWorldObject obj = get(objectId);
            if(obj == null){
                return null;
            }
            else if(clazz.isAssignableFrom(obj.getClass())){
                return (T)obj;
            } else {
                throw new ClassCastException("Object with id " + objectId + " is not of class " + clazz);
            }
        }
                

	@Override
	public Map<Class, Map<WorldObjectId, IViewable>> getAllVisible() {
		return localWV.getAllVisible();
	}

	@Override
	public <T extends IViewable> Map<WorldObjectId, T> getAllVisible(
			Class<T> type) {
		return localWV.getAllVisible(type);
	}

	@Override
	public Map<WorldObjectId, IViewable> getVisible() {
		return localWV.getVisible();
	}

	@Override
	public IViewable getVisible(WorldObjectId id) {
		return localWV.getVisible(id);
	}

	@Override
	public void lock() throws PogamutInterruptedException,
			ComponentNotRunningException, ComponentPausedException {
		localWV.lock();
	}

	@Override
	public void unlock() throws ComponentNotRunningException,
			ComponentPausedException {
		localWV.unlock();
	}

	@Override
	public boolean isLocked() {
		return localWV.isLocked();
	}

}
