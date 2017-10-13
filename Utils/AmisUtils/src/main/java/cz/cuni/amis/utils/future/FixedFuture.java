package cz.cuni.amis.utils.future;

import java.util.concurrent.TimeUnit;

/**
 * Future implementation that holds fixed result.
 */
public class FixedFuture<Result> implements IFuture<Result> {
	
	private Result result;
	
	public FixedFuture(Result result) {
		this.result = result;
	}
	
	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return false;
	}

	@Override
	public Result get() {
		return result;
	}

	@Override
	public Result get(long timeout, TimeUnit unit) {
		return result;
	}

	@Override
	public boolean isCancelled() {
		return false;
	}

	@Override
	public boolean isDone() {
		return true;
	}	

}
