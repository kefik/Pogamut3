package cz.cuni.amis.pogamut.base.communication.worldview.event;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.component.bus.event.BusAwareCountDownLatch;
import cz.cuni.amis.utils.exception.PogamutException;

/**
 * Use this if you want to wait for first appearance of some IWorldObject with known string ID.
 * 
 * @author ik
 */
public class WorldEventFuture<T extends IWorldEvent> implements Future<T> {
	
	public static class WorldEventFutureException extends PogamutException {

		public WorldEventFutureException(String message, Object origin) {
			super(message, origin);
		}
		
	}

    private final IWorldView worldView;
    private final Class<T> eventClass;
    
    private T worldEvent = null;
    private BusAwareCountDownLatch latch;
    private IWorldEventListener<T> listener = null;
	private boolean cancelled = false;
	
    /**
     * Creates new instance of future that waits for the event of given class.
     * @param worldView WorldView where the object will appear
     * @param eventClass class of the event we're waiting for
     */
    @SuppressWarnings("unchecked")
    public WorldEventFuture(final IWorldView worldView, final Class<T> eventClass) {
    	this.worldView = worldView;
    	this.eventClass = eventClass;
    	latch = new BusAwareCountDownLatch(1, worldView.getEventBus(), worldView);
		worldView.addEventListener(
            eventClass,
            listener = new IWorldEventListener<T>() {
                @Override
                public void notify(T event) {
                	worldEvent = event;
                    worldView.removeEventListener(eventClass, this);
                    customEventEncounteredHook(worldEvent);
                    latch.countDown();
                }
            }
        );    		
    }

    /**
     * Utility method for custom handling of the first-encounter event.
     * @param obj
     */
    protected void customEventEncounteredHook(T obj) {
    }

    @Override
    public synchronized boolean cancel(boolean mayInterruptIfRunning) {
        if (latch != null) latch.countDown();
        cancelled  = true;
        worldView.removeEventListener(eventClass, listener);
        return true;
    }

    @Override
    public synchronized boolean isCancelled() {
        return cancelled;
    }

    @Override
    public boolean isDone() {
        return worldEvent != null;
    }

    /**
     * @return the object that was awaited
     * @throws InterruptedException
     * @throws ExecutionException
     */
    @Override
    public T get() {
        latch.await();
        return worldEvent;
    }

    @Override
    public T get(long timeout, TimeUnit unit) {
        latch.await(timeout, unit);
        return worldEvent;
    }
}
