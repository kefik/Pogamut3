package cz.cuni.amis.pogamut.base.communication.worldview.object;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectDestroyedEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectFirstEncounteredEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.BusAwareCountDownLatch;
import cz.cuni.amis.utils.exception.PogamutException;

/**
 * Use this if you want to wait for first appearance of some IWorldObject with known string ID.
 * 
 * @author ik
 */
public class WorldObjectFuture<T extends IWorldObject> implements Future<T> {
	
	public static class WorldObjectFutureException extends PogamutException {

		public WorldObjectFutureException(String message, Object origin) {
			super(message, origin);
		}
		
	}

    private final IWorldView worldView;
    private final Class<T> objectClass;
    
    private T worldObject = null;
    private BusAwareCountDownLatch latch;
    private IWorldObjectListener<T> listener = null;
	private boolean cancelled = false;
	
    /**
     * Creates new instance of future that waits for first appearance of WorldObject
     * of given class.
     * @param worldView WorldView where the object will appear
     * @param objectClass class of the object that will appear
     */
    @SuppressWarnings("unchecked")
    public WorldObjectFuture(final IWorldView worldView, final Class<T> objectClass) {
    	this.worldView = worldView;
    	this.objectClass = objectClass;
    	Map<WorldObjectId, T> objects = worldView.getAll(objectClass);
    	switch (objects.size()) {
    	case 0:
    		latch = new BusAwareCountDownLatch(1, worldView.getEventBus(), worldView);
    		worldView.addObjectListener(
                objectClass,
                IWorldObjectEvent.class,
                listener = new IWorldObjectListener<T>() {
	                @Override
	                public void notify(IWorldObjectEvent<T> event) {
	                	if (event instanceof WorldObjectDestroyedEvent) return;
	                    worldObject = event.getObject();
	                    worldView.removeObjectListener(objectClass, IWorldObjectEvent.class, this);
	                    customObjectEncounteredHook(worldObject);
	                    latch.countDown();
	                }
                }
            );
    		objects = worldView.getAll(objectClass);
    		switch (objects.size()) {
        		case 0:
        			break;
        		case 1:        			
        			worldView.removeObjectListener(objectClass, IWorldObjectEvent.class, listener);
        			worldObject = objects.values().iterator().next();
        			latch.countDown();
        			break;
        		case 2:       
        			if (worldObject != null) return;
        			worldView.removeObjectListener(objectClass, IWorldObjectEvent.class, listener);
        			latch.countDown();
            		throw new WorldObjectFutureException("There are already " + objects.size() + " objects in world view of class " + objectClass.getSimpleName() + ".", this);
    		}
    		break;
    	case 1:
    		latch = new BusAwareCountDownLatch(0, worldView.getEventBus(), worldView);
    		worldObject = objects.values().iterator().next();
    		customObjectEncounteredHook(worldObject);
    		break;
    	default:
    		throw new WorldObjectFutureException("There are already " + objects.size() + " objects in world view of class " + objectClass.getSimpleName() + ".", this);
    	}    	
    }

    /**
     * Creates new instance of future that waits for first appearance of WorldObject
     * of given id.
     * @param worldView WorldView where the object will appear
     * @param id string id of the object that will appear
     * @param objectClass class of the object that will appear
     */
    @SuppressWarnings("unchecked")
    public WorldObjectFuture(final IWorldView worldView, final String id, final Class<T> objectClass) {
    	this.worldView = worldView;
    	this.objectClass = objectClass;
    	
    	IWorldObject o = worldView.get(WorldObjectId.get(id));
    	if (o != null) {
    		this.worldObject = (T) o;
    		latch = new BusAwareCountDownLatch(0, worldView.getEventBus(), worldView);
    	} else {    	
    		latch = new BusAwareCountDownLatch(1, worldView.getEventBus(), worldView);
	        worldView.addObjectListener(
	            objectClass,
	            IWorldObjectEvent.class,
	            listener = new IWorldObjectListener<T>() {
	
		            @Override
		            public void notify(IWorldObjectEvent<T> event) {
		            	if (event instanceof WorldObjectDestroyedEvent) return;
		                if (event.getObject().getId().getStringId().equals(id)) {		                	
		                    worldObject = event.getObject();
		                    worldView.removeObjectListener(objectClass, IWorldObjectEvent.class, this);
		                    latch.countDown();
                                    customObjectEncounteredHook(worldObject);
		                }
		            }
	            }
	        );
	        
	        o = worldView.get(WorldObjectId.get(id));
	        if (o != null) {
	        	worldView.removeObjectListener(objectClass, WorldObjectFirstEncounteredEvent.class, listener);
    			latch.countDown();
	        }
    	}
    }

    /**
     * Utility method for custom handling of the first-encounter event.
     * @param obj
     */
    protected void customObjectEncounteredHook(T obj) {
    }

    @Override
    public synchronized boolean cancel(boolean mayInterruptIfRunning) {
        if (latch != null) latch.countDown();
        cancelled  = true;
        worldView.removeObjectListener(objectClass, IWorldObjectEvent.class, listener);
        return true;
    }

    @Override
    public synchronized boolean isCancelled() {
        return cancelled;
    }

    @Override
    public boolean isDone() {
        return worldObject != null;
    }

    /**
     * @return the object that was awaited
     * @throws InterruptedException
     * @throws ExecutionException
     */
    @Override
    public T get() {
        latch.await();
        return worldObject;
    }

    @Override
    public T get(long timeout, TimeUnit unit) {
        latch.await(timeout, unit);
        return worldObject;
    }
}
