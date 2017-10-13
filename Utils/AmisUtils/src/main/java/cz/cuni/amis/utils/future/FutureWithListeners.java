package cz.cuni.amis.utils.future;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import cz.cuni.amis.utils.exception.PogamutException;
import cz.cuni.amis.utils.exception.PogamutInterruptedException;
import cz.cuni.amis.utils.listener.Listeners;
import cz.cuni.amis.utils.listener.Listeners.ListenerNotifier;

/**
 * Abstract class that represents future result of some computation that allows you to hook
 * listeners on the status of the future computation (see {@link FutureWithListeners#addFutureListener(IFutureListener)}).
 * Whenever the computation is completed (or cancelled / exception has happened / etc.) the listeners are informed.
 * 
 * @author Jimmy
 *
 * @param <RESULT>
 */
public abstract class FutureWithListeners<RESULT> implements IFutureWithListeners<RESULT> {

	/**
	 * Mutex synchronizing access to internal data structures of the future.
	 */
	protected Object mutex = new Object();
	
	/**
	 * Future listeners, here we store listeners registred in {@link FutureWithListeners#addFutureListener(IFutureListener)}.
	 */
	protected Listeners<IFutureListener<RESULT>> listeners = new Listeners<IFutureListener<RESULT>>();
	
	/**
	 * Misc - used by {@link FutureWithListeners#notifier} to know what the old status was.
	 */
	private FutureStatus oldStatus = null;
	
	/**
	 * Misc - used by {@link FutureWithListeners#notifier} to know what the new status is.
	 */
	private FutureStatus newStatus = null;
	
	/**
	 * Notifier that raises events on respective listeners.
	 */
	private ListenerNotifier<IFutureListener<RESULT>> notifier = new ListenerNotifier<IFutureListener<RESULT>>() {
		
		@Override
		public Object getEvent() {
			return newStatus;
		}

		@Override
		public void notify(IFutureListener<RESULT> listener) {
			listener.futureEvent(FutureWithListeners.this, oldStatus, newStatus);
		}
	};
	
	/**
	 * Current status of the future computation. The status is changed only via {@link FutureWithListeners#switchStatus(FutureStatus)} that
	 * also notifies all listeners about the change.
	 */
	private FutureStatus status = FutureStatus.FUTURE_IS_BEING_COMPUTED;
	
	/**
	 * Result of the future.
	 */
	private RESULT result = null;
	
	/**
	 * Latch where threads are waiting when using {@link FutureWithListeners#get()} or {@link FutureWithListeners#get(long, TimeUnit)}. This
	 * latch is instantiated whenever needed via method {@link FutureWithListeners#createLatch()}.
	 */
	protected CountDownLatch latch = null;

	/**
	 * If the computation results in an exception and the future is informed about such fact, the exception
	 * is stored here.
	 */
	private Exception exception;
	
	/**
	 * Current status of the future computation.
	 * @return
	 */
    @Override
	public FutureStatus getStatus() {
		return status;
	}
	
	/**
	 * Adds a listener on a future status (using strong reference). Listeners are automatically
	 * removed whenever the future gets its result (or is cancelled or an exception happens).
	 * @param listener
	 */
    @Override
	public void addFutureListener(IFutureListener<RESULT> listener) {
		listeners.addStrongListener(listener);
	}
	
	/**
	 * Removes a listener from the future.
	 * @param listener
	 */
    @Override
	public void removeFutureListener(IFutureListener<RESULT> listener) {
		listeners.removeListener(listener);
	}
	
	/**
	 * Whether some listener is listening on the future.
	 * @param listener
	 * @return
	 */
    @Override
	public boolean isListening(IFutureListener<RESULT> listener) {
		return listeners.isListening(listener);
	}
	
	/**
	 * Sets the result of the future computation.
	 * <p><p>
	 * Switches the status to FUTURE_IS_READY (notifying listeners along the way).
	 * <p><p>
	 * The result can be set only iff NOT {@link FutureWithListeners#isDone()}, i.e., status is {@link FutureStatus}:FUTURE_IS_BEING_COMPUTED.
	 * @param result
	 */
    @Override
	public void setResult(RESULT result) {
		synchronized(mutex) {
			if (status != FutureStatus.FUTURE_IS_BEING_COMPUTED) {
				throw new PogamutException("Future is not being computed anymore - can't set result.", this);
			}			
			this.result = result;
			switchStatus(FutureStatus.FUTURE_IS_READY);
			if (latch != null) {
				while (latch.getCount() > 0) latch.countDown();
			} else {
				latch = new CountDownLatch(0);
			}			
			listeners.clearListeners();
		}
	}
	
	/** 
	 * Informs the future that it can't be computed due to the exception. 
	 * <p><p>
	 * Switches the status to EXCEPTION (notifying listeners along the way).
	 * <p><p>
	 * The result can be set only iff NOT {@link FutureWithListeners#isDone()}, i.e., status is {@link FutureStatus}:FUTURE_IS_BEING_COMPUTED.
	 * @param e
	 */
    @Override
	public void computationException(Exception e) {
		synchronized(mutex) {
			if (status != FutureStatus.FUTURE_IS_BEING_COMPUTED) {
				throw new PogamutException("Future is not being computed anymore - can't process computation exception.", e);
			}
			this.exception = e;
			switchStatus(FutureStatus.COMPUTATION_EXCEPTION);
			this.result = null;
			if (latch != null) {
				while (latch.getCount() > 0) latch.countDown();
			} else {
				latch = new CountDownLatch(0);
			}
		}
	}
	
	/**
	 * Changes the status of the future (if it is different than current one) and notifies the listeners
	 * about this change.
	 * 
	 * @param newStatus
	 */
	protected void switchStatus(FutureStatus newStatus) {
        synchronized(mutex){
			if (newStatus == status) return;
			oldStatus = status;
			this.newStatus = newStatus;
			status = newStatus;
			listeners.notify(notifier);
        }
	}
	
	/**
	 * Factory method that should return {@link CountDownLatch} or its descendant initialized to 1.
	 * @return
	 */
	protected CountDownLatch createLatch() {
		return new CountDownLatch(1);
	}
	
	/**
	 * This should cancel the computation of the future. Current implementation returns always false. Override
	 * the method to provide correct behavior for particular future.
	 * @param mayInterruptIfRunning
	 * @return
	 */
	protected boolean cancelComputation(boolean mayInterruptIfRunning) {
		return false;
	}
	
	@Override
	public final boolean cancel(boolean mayInterruptIfRunning) {
		synchronized(mutex) {
			if (cancelComputation(mayInterruptIfRunning)) {
				switchStatus(FutureStatus.CANCELED);
				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public RESULT get() {
		if (status == FutureStatus.FUTURE_IS_READY) return result;
		if (status == FutureStatus.FUTURE_IS_BEING_COMPUTED) {
			synchronized(mutex) {
				if (status == FutureStatus.FUTURE_IS_READY) return result;
				if (status == FutureStatus.FUTURE_IS_BEING_COMPUTED) {
					latch = createLatch();
				}
			}
			try {
				latch.await();
			} catch (InterruptedException e) {
				throw new PogamutInterruptedException("Interrupted while awaiting furure result.", e, this);
			}
			if (status == FutureStatus.FUTURE_IS_READY) return result;
		}
		return null;
	}

	/**
	 * Returns a result or waits for the computation till timeout. 
	 * <p><p>
	 * Does not throw {@link TimeoutException}! It returns null instead - always examine status of the future
	 * via {@link FutureWithListeners#getStatus()} if the null is returned to tell whether the 'null' is the
	 * result of the computation (if the status is FUTURE_IS_READY than the 'null' is truly the result).
	 * @param timeout
	 * @param unit
	 * @return
	 */
	@Override
	public RESULT get(long timeout, TimeUnit unit) {
		if (status == FutureStatus.FUTURE_IS_READY) return result;
		if (status == FutureStatus.FUTURE_IS_BEING_COMPUTED) {
			synchronized(mutex) {
				if (status == FutureStatus.FUTURE_IS_READY) return result;
				if (status == FutureStatus.FUTURE_IS_BEING_COMPUTED) {
					latch = createLatch();
				}
			}
			try {
				latch.await(timeout, unit);
			} catch (InterruptedException e) {
				throw new PogamutInterruptedException("Interrupted while awaiting future result.", e, this);
			}
			if (status == FutureStatus.FUTURE_IS_READY) return result;
		}
		return null;
	}

	@Override
	public boolean isCancelled() {
		return status == FutureStatus.CANCELED;
	}

	@Override
	public boolean isDone() {
		return status != FutureStatus.FUTURE_IS_BEING_COMPUTED;
	}

	/**
	 * Contains an exception that has happened during the computation in the case of ({@link FutureWithListeners#getStatus()} == EXCEPTION).
	 * @return
	 */
    @Override
	public Exception getException() {
		return exception;
	}
	
}
