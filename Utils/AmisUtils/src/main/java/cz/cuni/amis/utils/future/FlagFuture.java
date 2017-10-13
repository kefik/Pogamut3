package cz.cuni.amis.utils.future;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import cz.cuni.amis.utils.exception.PogamutInterruptedException;
import cz.cuni.amis.utils.flag.Flag;
import cz.cuni.amis.utils.flag.FlagListener;


/**
 * Future implementation that is listening on a flag and when it's terminal state
 * is set on the flag the future completes itself with result specified in the map
 * (under the key of the flag value).
 * <p><p>
 * Thread-safe implementation, not as easy as it seems ;-)
 * 
 * @author Jimmy
 *
 * @param <Result>
 * @param <FlagType>
 */
public class FlagFuture<Result, FlagType> implements IFuture<Result>, FlagListener<FlagType>{
	
	/**
	 * Mapping flag value -&gt; results, if a value of the flag is not listed here then the
	 * object ignores that flag value.
	 */
	private Map<FlagType, Result> terminalMap;
	
	/**
	 * Where to listen for values.
	 */
	private Flag<FlagType> waitFlag;
	
	/**
	 * Latch where the users of this object is waiting for the future to complete.
	 */
	private final CountDownLatch latch = new CountDownLatch(1);
	
	/**
	 * Whether the future is done - the future is done when the flag reaches one of
	 * the terminal states from the map terminalMap.
	 */
	private boolean done = false;
	
	/**
	 * Result of the future - when the flag reaches one of the terminal value (as defined
	 * in terminalMap) according result (from the map) is written here and 'done' is set to true.
	 */
	private Result result = null;
	
	/**
	 * In constructor you have to specify a flag where the future should listen at + terminal
	 * states for the future (terminalMap).
	 * <p><p>
	 * Possible flag values and the behavior of the future:
	 * <ol>
	 * <li>flag value is in the keys of terminalMap - the future completes with the result under the key from the flag</li>
	 * <li>flag value <b>is not</b> in the keys of terminalMap - the future ignores that value and waits for next flag change</li>
	 * </ol>
	 * <p>
	 * Note that the flag value is examined during the construction of the object and if the
	 * flag has value that is in the terminalMap the future is completes itself.
	 *  
	 * @param waitFlag
	 * @param terminalMap
	 */
	public FlagFuture(Flag<FlagType> waitFlag, Map<FlagType, Result> terminalMap) {
		this.terminalMap = new HashMap<FlagType, Result>(terminalMap);
		this.waitFlag = waitFlag;
		init();
	}
	
	/**
	 * Initializing future to wait for 'terminalFlagValue' at 'waitFlag', when that happens complete
	 * itself with result 'resultValue'.
	 * @param waitFlag
	 * @param terminalFlagValue
	 * @param resultValue
	 */
	public FlagFuture(Flag<FlagType> waitFlag, FlagType terminalFlagValue, Result resultValue) {
		terminalMap = new HashMap<FlagType, Result>();
		terminalMap.put(terminalFlagValue, resultValue);
		this.waitFlag = waitFlag;
		init();
	}
	
	/**
	 * Initialize the listener to a flag + checking the current value of the flag.
	 * <p><p>
	 * Called from constructor.
	 */
	private void init() {
		waitFlag.addListener(this);
		synchronized(latch) {
			if (!done) {
				FlagType value = waitFlag.getFlag();
				if (terminalMap.containsKey(value)) {
					result = terminalMap.get(value);
					done = true;
					latch.countDown();
				}				
			}			
		}
		if (done) waitFlag.removeListener(this);
	}
	
	/**
	 * Stops the future (not the task it represents!). It raise the latch
	 * unblocking all threads waiting for the future to complete. Sets the result
	 * to desired value.
	 * <p><p>
	 * Note that this happened IFF !isDone(), e.g. if the future is already done this
	 * won't do anything (as latch is already raised and the result value has been
	 * determined).
	 * <p><p>
	 * Note that this is different behavior that cancel() should implement, therefore
	 * it's a different method.
	 */
	public void stop(Result result) {
		synchronized(latch) {
			if (done) return;
			done = true;
			this.result = result;			
		}
		waitFlag.removeListener(this);
		latch.countDown();
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		// can't cancel
		return false;
	}

	@Override
	public Result get() {
		try {
			latch.await();
		} catch (InterruptedException e) {
			throw new PogamutInterruptedException(e.getMessage(), e, this);
		}
		return result;
	}

	@Override
	public Result get(long timeout, TimeUnit unit) {
		try {
			latch.await(timeout, unit);
		} catch (InterruptedException e) {
			throw new PogamutInterruptedException(e.getMessage(), e, this);
		}
		return result;
	}

	@Override
	public boolean isCancelled() {
		return false;
	}

	@Override
	public boolean isDone() {	
		return done;
	}

	@Override
	public void flagChanged(FlagType changedValue) {
		synchronized(latch) {
			if (done) {
				waitFlag.removeListener(this);
				return;
			}
			if (terminalMap.containsKey(changedValue)) {			
				result = terminalMap.get(changedValue);
				done = true;
				waitFlag.removeListener(this);
				latch.countDown();				
			}			
		}		
	}
}
