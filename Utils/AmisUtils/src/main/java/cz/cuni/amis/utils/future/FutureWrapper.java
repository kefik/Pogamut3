package cz.cuni.amis.utils.future;

import java.util.concurrent.TimeUnit;

public class FutureWrapper<RESULT> implements IFuture<RESULT> {

	private IFuture<RESULT> impl;

	public FutureWrapper(IFuture<RESULT> impl) {
		this.impl = impl;
	}
	
	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return impl.cancel(mayInterruptIfRunning);
	}

	@Override
	public RESULT get() {
		return impl.get();
	}

	@Override
	public RESULT get(long timeout, TimeUnit unit) {
		return impl.get(timeout, unit);
	}

	@Override
	public boolean isCancelled() {
		return impl.isCancelled();
	}

	@Override
	public boolean isDone() {
		return impl.isDone();
	}

}