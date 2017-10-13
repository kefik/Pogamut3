package cz.cuni.amis.utils.future;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import cz.cuni.amis.utils.NullCheck;
import cz.cuni.amis.utils.TimeUnitToMillis;
import cz.cuni.amis.utils.exception.PogamutTimeoutException;

/**
 * Used to combine multiple Future&lt;Boolean> together into one Future&lt;Boolean>.
 * <p><p>
 * During construction of the instance of this class you have to specify an array of
 * booleans you want to combine - then all methods will wait for all futures to end
 * (e.g. get(), etc.).
 * <p><p>
 * Note that you will probably want to use getAll() method to get respective Future results.
 * 
 * @author Jimmy
 *
 */
public class CombinedBooleanFuture implements IFuture<Boolean> {
	
	private IFuture<Boolean>[] futures;
	
	public CombinedBooleanFuture(IFuture<Boolean>[] futures) {
		this.futures = futures;
		NullCheck.check(this.futures, "futures");
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		boolean canceled = true;
		for (Future<Boolean> future : futures) {
			canceled = canceled && future.cancel(mayInterruptIfRunning);
		}
		return canceled;
	}

	@Override
	public Boolean get() {
		boolean result = true;
		for (IFuture<Boolean> future : futures) {
			Boolean futureResult = future.get(); 
			result = result && (futureResult != null ? futureResult : false);
		}
		return result;
	}

	@Override
	public Boolean get(long timeout, TimeUnit unit) {
		long timeoutMillis = TimeUnitToMillis.toMillis(timeout, unit);
		long start = System.currentTimeMillis();
		boolean result = true;
		for (IFuture<Boolean> future : futures) {
			long futureStart = System.currentTimeMillis();
			if (futureStart - start > timeoutMillis) throw new PogamutTimeoutException("timeouted after " + timeout + " " + unit, this);
			Boolean futureResult = future.get(timeoutMillis - (futureStart - start), TimeUnit.MILLISECONDS);
			result = result && (futureResult != null ? futureResult : false);
		}
		return result;
	}
	
	public Boolean[] getAll(long timeout, TimeUnit unit) {
		long timeoutMillis = TimeUnitToMillis.toMillis(timeout, unit);
		long start = System.currentTimeMillis();
		Boolean[] results = new Boolean[futures.length];
		int i = 0;
		for (IFuture<Boolean> future : futures) {
			long futureStart = System.currentTimeMillis();
			if (futureStart - start > timeoutMillis) throw new PogamutTimeoutException("timeouted after " + timeout + " " + unit, this);
			results[i++] = future.get(timeoutMillis - (futureStart - start), TimeUnit.MILLISECONDS);
		}
		return results;
	} 

	@Override
	public boolean isCancelled() {
		boolean result = false;
		for (Future<Boolean> future : futures) {
			Boolean futureResult = future.isCancelled(); 
			result = result || (futureResult != null ? futureResult : false);			
		}
		return result;
	}

	@Override
	public boolean isDone() {
		boolean result = true;
		for (Future<Boolean> future : futures) {			
			result = result && future.isDone();
		}
		return result;
	}
	
	public Future<Boolean>[] getFutures() {
		return futures;
	}

}
