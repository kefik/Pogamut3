package cz.cuni.amis.utils.future;

import java.util.concurrent.TimeUnit;

import cz.cuni.amis.utils.exception.PogamutCancellationException;
import cz.cuni.amis.utils.exception.PogamutInterruptedException;
import cz.cuni.amis.utils.exception.PogamutTimeoutException;
import cz.cuni.amis.utils.flag.Flag;
import cz.cuni.amis.utils.flag.WaitForFlagChange;

public abstract class RunnableFuture<RESULT> implements Runnable, IFuture<RESULT> {
	
	private Object mutex = new Object();
	
	private boolean cancelled = false;
	
	private boolean running = false;
	
	private Flag<Boolean> done = new Flag<Boolean>(false);

	private RuntimeException exception = null;

	private RESULT result = null;
	
	/**
	 * Provide the implementation of your work that returns some RESULT or throws an exception if something happens.
	 * @return
	 */
	protected abstract RESULT process() throws Exception;
	
	@Override
	public final void run() {
		synchronized(mutex) {
			if (cancelled) return;
			running = true;
		}
		try {
			result = process();
		} catch (RuntimeException e) {
			exception = e;
		} catch (Exception e) {
			exception = new RuntimeException(e);
		}
		synchronized(mutex) {
			running = false;
			done.setFlag(true);
		}
	}
	
	@Override
	public final boolean cancel(boolean mayInterruptIfRunning) {
		synchronized(mutex) {
			if (done.getFlag()) return false;
			if (running) return false;
			cancelled = true;
		}
		return true;
	}
	
	private RESULT getResult() {
		if (exception != null) throw exception;
		if (cancelled) throw new PogamutCancellationException("request has been cancelled", this);
		return result;
	}

	@Override
	public final RESULT get() {
		synchronized(mutex) {
			if (done.getFlag()) return getResult();
		}
		new WaitForFlagChange<Boolean>(done, true).await();
		return getResult();
	}

	@Override
	public final RESULT get(long timeout, TimeUnit unit) throws PogamutInterruptedException, PogamutTimeoutException {
		synchronized(mutex) {
			if (done.getFlag()) return getResult();
		}
		new WaitForFlagChange<Boolean>(done, true).await(timeout, unit);
		synchronized(mutex) {
			if (!done.getFlag()) return getResult();
			return getResult();
		}
	}

	@Override
	public final boolean isCancelled() {
		return cancelled;
	}

	@Override
	public final boolean isDone() {
		return done.getFlag();
	}
	
}
