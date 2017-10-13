package cz.cuni.amis.pogamut.base.utils.future;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import cz.cuni.amis.pogamut.base.component.IComponent;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.component.bus.event.BusAwareCountDownLatch;
import cz.cuni.amis.pogamut.base.component.bus.exception.ComponentNotRunningException;
import cz.cuni.amis.utils.future.FutureWithListeners;

/**
 * Future that depends on the running state of the {@link IComponent}. This future guarantees 
 * that it unblock all threads waiting for the result if the world view (or other component(s) specified
 * via {@link ComponentFuture#ComponentFuture(IComponentBus, IComponent...)}) dies.
 * 
 * @author Jimmy
 *
 * @param <RESULT>
 */
public class ComponentFuture<RESULT> extends FutureWithListeners<RESULT> {

	private IComponent[] dependants;
	private IComponentBus bus;

	/**
	 * Constructor where you have to specify components on which the result depends + its component bus. Note
	 * that all components must be registered at 'bus'.
	 * @param bus may be null - in this case a simple {@link CountDownLatch} is created instead of {@link BusAwareCountDownLatch}
	 * @param dependants may be null or zero-length - in this case a simple {@link CountDownLatch} is created instead of {@link BusAwareCountDownLatch}
	 */
	public ComponentFuture(IComponentBus bus, IComponent... dependants) {
		this.bus = bus;
		this.dependants = dependants;
	}
	
	@Override
	protected CountDownLatch createLatch() {
		if (bus != null && dependants != null && dependants.length > 0) {
			return new BusAwareCountDownLatch(1, bus, dependants);
		} else {
			return new CountDownLatch(1);
		}
	}
	
	/**
	 * Blocks until the future is computed and then returns the result of the computation.
	 * <p><p>
	 * If the result can't be computed (computation is cancelled, exception happens or some component working on the future
	 * result stops), throws an {@link ComponentFutureException}.
	 * <p><p>
	 * For additional info, see {@link FutureWithListeners#get()}.
	 * 
	 * @return
	 */
	@Override
	public RESULT get() throws ComponentFutureException {
		super.get();
		synchronized(mutex) {
			switch (getStatus()) {
			case FUTURE_IS_READY: return super.get();
			case CANCELED: throw new ComponentFutureException("The computation has been canceled.", this);
			case COMPUTATION_EXCEPTION: throw new ComponentFutureException("Computation exception.", getException());
			case FUTURE_IS_BEING_COMPUTED: 
				computationException(new ComponentNotRunningException("One of the component dealing with the future computation has stopped.", this));
				throw new ComponentFutureException("One of the component has stopped, future can't be computer.", getException());
			}
		}
		return null;
	}
	
	/**
	 * Blocks until the future is computed (or timeout) and then returns the result of the computation. If the result
	 * is not computed until timeout, null is returned (check status of the future, whether the 'null' is truly the result
	 * of the computation).
	 * <p><p>
	 * If the result can't be computed (computation is cancelled, exception happens or some component working on the future
	 * result stops), throws an {@link ComponentFutureException}.
	 * <p><p>
	 * For additional info, see {@link FutureWithListeners#get(long, TimeUnit)}.
	 * 
	 * @param timeout
	 * @param unit
	 */
	@Override
	public RESULT get(long timeout, TimeUnit unit) throws ComponentFutureException {
		super.get(timeout, unit);
		synchronized(mutex) {
			switch (getStatus()) {
			case FUTURE_IS_READY: return super.get();
			case CANCELED: throw new ComponentFutureException("The computation has been canceled.", this);
			case COMPUTATION_EXCEPTION: throw new ComponentFutureException("Computation exception.", getException());
			case FUTURE_IS_BEING_COMPUTED: 
				if (latch.getCount() == 0) {
					// latch has been risen without the setting the result
					computationException(new ComponentNotRunningException("One of the component dealing with the future computation has stopped.", this));				
					throw new ComponentFutureException("One of the component has stopped, future can't be computer.", getException());
				}
			}
		}
		return null;
	}
	
}
