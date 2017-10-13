package cz.cuni.amis.utils.future;

/**
 * Status of the {@link FutureWithListeners} object. It can tell you in a single enum what state the future is in.
 * 
 * @author Jimmy
 */
public enum FutureStatus {

	/**
	 * The future has not been computed yet, methods {@link FutureWithListeners#get()} and {@link FutureWithListeners#get(long, java.util.concurrent.TimeUnit)} will block.
	 */
	FUTURE_IS_BEING_COMPUTED,
	
	/**
	 * Future is ready, methods {@link FutureWithListeners#get()} and {@link FutureWithListeners#get(long, java.util.concurrent.TimeUnit)} won't block
	 * and will immediately return a result.
	 */
	FUTURE_IS_READY,
	
	/**
	 * The computation of the future has been canceled. The future result is (and will remain) unavailable.
	 */
	CANCELED,
	
	/**
	 * An exception has happenned during the future computation. The future result is (and will remain) unavailable.
	 */
	COMPUTATION_EXCEPTION;

}
