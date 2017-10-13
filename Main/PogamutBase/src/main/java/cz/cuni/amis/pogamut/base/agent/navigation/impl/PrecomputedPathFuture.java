package cz.cuni.amis.pogamut.base.agent.navigation.impl;

import java.util.List;
import java.util.concurrent.TimeUnit;

import cz.cuni.amis.pogamut.base.agent.navigation.IPathFuture;
import cz.cuni.amis.utils.SyncLazy;
import cz.cuni.amis.utils.future.FutureStatus;
import cz.cuni.amis.utils.future.IFutureListener;
import cz.cuni.amis.utils.listener.Listeners;

/**
 * Serves as a {@link IPathFuture} that contains pre-set result, i.e., you do not need the future, you just want to pass down
 * some value...
 * 
 * @author Jimmy
 *
 * @param <PATH_ELEMENT>
 */
public class PrecomputedPathFuture<PATH_ELEMENT> implements IPathFuture<PATH_ELEMENT> {

	private PATH_ELEMENT pathFrom;
	private PATH_ELEMENT pathTo;
	private List<PATH_ELEMENT> result;
	private SyncLazy<Listeners<IFutureListener>> listeners = new SyncLazy<Listeners<IFutureListener>>() {
		@Override
		protected Listeners<IFutureListener> create() {
			return new Listeners<IFutureListener>();
		}
	};

	public PrecomputedPathFuture(PATH_ELEMENT from, PATH_ELEMENT to, List<PATH_ELEMENT> path) {
		this.pathFrom = from;
		this.pathTo = to;
		this.result = path;
	}

	@Override
	public PATH_ELEMENT getPathFrom() {
		return pathFrom;
	}

	@Override
	public PATH_ELEMENT getPathTo() {
		return pathTo;
	}
	
	@Override
	public void addFutureListener(IFutureListener<List<PATH_ELEMENT>> listener) {
		listeners.get().addWeakListener(listener);
	}
	
	@Override
	public void removeFutureListener(IFutureListener<List<PATH_ELEMENT>> listener) {
		listeners.get().removeListener(listener);
	}

	@Override
	public List<PATH_ELEMENT> get() {
		return result;
	}

	@Override
	public List<PATH_ELEMENT> get(long timeout, TimeUnit unit) {
		return result;
	}

	@Override
	public FutureStatus getStatus() {
		return FutureStatus.FUTURE_IS_READY;
	}

	@Override
	public boolean isListening(IFutureListener<List<PATH_ELEMENT>> listener) {
		return listeners.get().isListening(listener);
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return false;
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
