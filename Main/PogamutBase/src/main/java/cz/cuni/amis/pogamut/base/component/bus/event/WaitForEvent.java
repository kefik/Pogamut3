package cz.cuni.amis.pogamut.base.component.bus.event;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.component.bus.IComponentEvent;
import cz.cuni.amis.pogamut.base.component.bus.IComponentEventListener;
import cz.cuni.amis.utils.NullCheck;
import cz.cuni.amis.utils.exception.PogamutInterruptedException;
import cz.cuni.amis.utils.token.Token;

/**
 * Allows you to wait for some event on the bus.
 * <p><p>
 * WARNING: if you want to stop using this object (throw away the pointer) call DESTROY(), you will usually use try{}finally{} for this.
 */
public class WaitForEvent {

	/**
	 * Note that only events that implements {@link IComponentEvent} may be really substitued as T.
	 * @author Jimmy
	 *
	 * @param <T>
	 */
	public interface IEventFilter<T> {
		
		/**
		 * Must return class of the event that the object may accept.
		 * <p><p>
		 * Must not return null!
		 * 
		 * @return
		 */
		public Class<T> getEventClass(); 
		
		/**
		 * If it does not return null - then only events that happened on this class of component may be accepted (this class or descendants).
		 * @return
		 */
		public Class getComponentClass();
		
		/**
		 * If it does not return null - then only events from the component of this id may be accepted.
		 * @return
		 */
		public Token getComponentId();
		
		/**
		 * Whether the event may be accepted.
		 * @param event
		 * @return
		 */
		public boolean accept(T event);
		
	}
		
	private final CountDownLatch eventLatch = new CountDownLatch(1);
	private final IComponentBus componentBus;
	private final IEventFilter acceptEvent;
	private final IComponentEventListener listener;
	private final IComponentEventListener fatalErrorListener;
	private boolean fatalError = false;
	
	public WaitForEvent(IComponentBus bus, IEventFilter accept) {
		this.componentBus = bus;
		NullCheck.check(this.componentBus, "bus");
		this.acceptEvent = accept;
		NullCheck.check(accept.getEventClass(), "accept.getEventClass()");
		this.listener = new IComponentEventListener<IComponentEvent>() {

			@Override
			public void notify(IComponentEvent event) {
				if (acceptEvent.accept(acceptEvent.getEventClass().cast(event))) {
					try {
						componentBus.removeEventListener(acceptEvent.getEventClass(), this);
						componentBus.removeEventListener(IFatalErrorEvent.class, fatalErrorListener);
					} finally {
						eventLatch.countDown();
					}
				}
			}
			
		};
		
		this.fatalErrorListener = new IComponentEventListener<IComponentEvent>() {

			@Override
			public void notify(IComponentEvent event) {
				fatalError = true;
				try {
					componentBus.removeEventListener(acceptEvent.getEventClass(), this);
					componentBus.removeEventListener(IFatalErrorEvent.class, fatalErrorListener);
				} finally {
					eventLatch.countDown();
				}
			}
			
		};
		
		if (!componentBus.isRunning()) {
			fatalError = true;
			eventLatch.countDown();
		} else {		
			componentBus.addEventListener(IFatalErrorEvent.class, fatalErrorListener);
			componentBus.addEventListener(acceptEvent.getEventClass(), listener);
			if (!componentBus.isRunning()) {
				fatalError = true;
				eventLatch.countDown();
				try {
					componentBus.removeEventListener(IFatalErrorEvent.class, fatalErrorListener);
				} catch (Exception e) {
				} finally {
					try {
						componentBus.removeEventListener(acceptEvent.getEventClass(), listener);
					} catch (Exception e) {
					}
				}
			}
		}
	}
	
	/**
	 * Awaits the event (forever) ... do not use! Always use version with timeout!
	 * 
	 * @return whether the event has been received (false == fatal error happened)
	 */
	public boolean await() throws PogamutInterruptedException {
		try {
			eventLatch.await();
			return !fatalError;
		} catch (InterruptedException e) {
			throw new PogamutInterruptedException(e, this);
		}
	}
	
	/**
	 * Awaits the event for a specific amount of time.
	 * <p><p>
	 * WARNING: If the event is not received and you do not plan to use the object further - call destroy()!
	 * 
	 * @param timeoutMillis
	 * @return whether the event has been received (false == timeout or fatal error has happened)
	 * @throws InterruptedException
	 */
	public boolean await(long timeoutMillis) throws PogamutInterruptedException {
		try {
			eventLatch.await(timeoutMillis, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			throw new PogamutInterruptedException(e, this);
		}
		return !fatalError && eventLatch.getCount() == 0;
	}

	/**
	 * Destroys the object ... any await() call will just go through after this call.
	 */
	public void destroy() {
		componentBus.removeEventListener(acceptEvent.getEventClass(), listener);
		eventLatch.countDown();
	}
	
}