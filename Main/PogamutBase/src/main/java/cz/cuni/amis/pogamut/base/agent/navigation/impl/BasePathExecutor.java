
package cz.cuni.amis.pogamut.base.agent.navigation.impl;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.agent.navigation.IPathExecutor;
import cz.cuni.amis.pogamut.base.agent.navigation.IPathExecutorHelper;
import cz.cuni.amis.pogamut.base.agent.navigation.IPathExecutorState;
import cz.cuni.amis.pogamut.base.agent.navigation.IPathFuture;
import cz.cuni.amis.pogamut.base.agent.navigation.IStuckDetector;
import cz.cuni.amis.pogamut.base.agent.navigation.PathExecutorState;
import cz.cuni.amis.utils.exception.PogamutException;
import cz.cuni.amis.utils.future.FutureStatus;
import cz.cuni.amis.utils.future.FutureWithListeners;
import cz.cuni.amis.utils.future.IFutureListener;

/**
 * BasePathExecutor provides a stub implementation of abstract methods of the {@link AbstractPathExecutor} 
 * which correctly sets the path executor states along the way and provide methods for reporting failures.
 * <p><p>
 * Note that it is somewhat hard to use this stub-implementation as the base for implementing 
 * your own {@link IPathExecutor} implementation - the {@link AbstractPathExecutor} might be more suitable.
 * That's because this implementation is defining a way how its fields are used, how methods should be synchronized,
 * how they are called (method protocol), etc.
 * <p><p>
 * Implementation notes:
 * <ul>
 *   <li>
 *     As the {@link IPathExecutorState} may have different implementation providing different kind of the information
 *     about the path - {@link BasePathExecutor} defines {@link BasePathExecutor#createState(PathExecutorState)} as a hook
 *     for custom {@link IPathExecutorState} implementation (currently it uses simple {@link BasePathExecutorState}). 
 *   </li>
 *   <li>
 *     Many methods here are made final - this might seem to block your inventions, but it is actually for your own good.
 *     If you find yourself in need to override some method that is made final, it is advised that you switch to {@link AbstractPathExecutor}
 *     or copy-paste the {@link BasePathExecutor} code and do your changes in copied code.
 *   </li>
 *   <li>
 *     There are several important final methods that drive the {@link BasePathExecutor}.
 *     <ul>
 *       <li>{@link BasePathExecutor#followPath(IPathFuture)} - interface public method / meant to be called from the outside - begin following the path / handles path future listener</li>
 *       <li>{@link BasePathExecutor#pathComputed()} - protected method / it is automatically called by the {@link BasePathExecutor} whenever the path future returns the path</li>
 *       <li>{@link BasePathExecutor#pathComputationFailed()} - protected method / it is automatically called by the {@link BasePathExecutor} whenever path future reports {@link FutureStatus#CANCELED} or {@link FutureStatus#COMPUTATION_EXCEPTION}.
 *       <li>{@link BasePathExecutor#switchToAnotherPathElement(int)} - protected method / meant to be called from within the path executor (descendant) whenever the executor should start to navigate towards another path element</li>
 *       <li>{@link BasePathExecutor#stuck(IStuckDetector)} - protected method / automatically called by {@link BasePathExecutor#executePath()} whenever one of the {@link AbstractPathExecutor#stuckDetectors} reports stuck</li>
 *       <li>{@link BasePathExecutor#stop()} - interface public method / meant to be called both from the outside or from the inside of path executor / stops the path executor</li>
 *     </ul>	
 *   </li>
 *   <li>
 *   	Important final methods from the previous paragraph are usually split into three parts
 *   	<ol>
 *   	  <li>their actual implementation that can't be overridden, it (usually) changes the state of the path executor and recalls pre/post methods</li>
 *   	  <li>pre-phase method that is called (from within the method's implementation) just before the state of the executor is changed</li>
 *   	  <li>post-phase method that is called (from within the method's implementation) just after the state of the executor is changed</li> 
 *   	</ol>
 *   	Pre/Post-phase methods are usually abstract (if not, they contain meaningful implementation but that can be overriden if wished for) and are meant to be implemented in descendants. 
 *      They allow you to fine control the state of the executor before and after the change of its state without the need to program boring part of the code that changes the state in a correct way. 
 *      Think of implementing the descendant of this class as implementing reactions to the changes of executor's state.
 *   </li>
 *   <li>
 *   	Additionally, there are methods that are not called from anywhere of {@link BasePathExecutor}. These are:
 *      <ul>
 *        <li>{@link BasePathExecutor#switchToAnotherPathElement(int)} - this method should be called either from {@link BasePathExecutor#prePathComputedImpl()} or {@link BasePathExecutor#pathComputed()} to mark
 *            the index of the path element that is going to be pursued first (this index will likely be '0').</li>
 *        <li>{@link BasePathExecutor#stuck(IStuckDetector)} - use this method to report that the stuck has been detected</li>
 *        <li>{@link BasePathExecutor#checkStuckDetectors()}    
 *      </ul>
 *   </li>
 *   <li>
 *   	It is up to the descendant how the path execution will be actually implemented (either by some event-driven model, i.e.,
 *      reacting on the events that will come from the environment, or spawning some thread that will regularly perform some operation, etc.).
 *   </li>
 * </ul>
 * 
 * @author Jimmy
 */
public abstract class BasePathExecutor<PATH_ELEMENT> extends AbstractPathExecutor<PATH_ELEMENT> implements IPathExecutorHelper<PATH_ELEMENT> {

	/**
	 * Mutex object synchronizing access to {@link BasePathExecutor#followPath(IPathFuture)} and
	 * {@link BasePathExecutor#stop()} methods.
	 */
	protected Object mutex = new Object();
	
	/**
	 * Current path future of the path executor. Path future is set in {@link BasePathExecutor#followPath(IPathFuture)}
	 * and removed (set to 'null') in {@link BasePathExecutor#stop()}.
	 */
	protected IPathFuture<PATH_ELEMENT> pathFuture = null;
	
	/**
	 * Marks the index of the previous path element (path element that has been previously pursued) 
	 * from the path element list of {@link BasePathExecutor#getPath()}.
	 * <p><p>
	 * Setting value to this field manually must be done only inside {@link BasePathExecutor#preSwitchToAnotherPathElementImpl(int)}
	 * and {@link BasePathExecutor#switchToAnotherPathElementImpl()}.
	 */
	protected int previousPathElementIndex = -1;
	
	/**
	 * Marks the index of the current path element (path element that is currently pursued) 
	 * from the path element list of {@link BasePathExecutor#getPath()}.
	 * <p><p>
	 * Setting value to this field manually must be done only inside {@link BasePathExecutor#preSwitchToAnotherPathElementImpl(int)}
	 * and {@link BasePathExecutor#switchToAnotherPathElementImpl()}.
	 */
	protected int pathElementIndex = -1;
	
	/**
	 * {@link BasePathExecutor#pathFuture} listener that recalls methods {@link BasePathExecutor#pathComputed()}
	 * or {@link BasePathExecutor#pathComputationFailed()} based upon the change of the future status. Note that
	 * the listener recalls these methods only if the event comes from the {@link BasePathExecutor#pathFuture} to prevent
	 * wrong invocations. 
	 * <p><p>
	 * If you wish to use different implementation of the {@link IFutureListener}, reinstantiate this field
	 * in the constructor of your own descendant.
	 */
	IFutureListener<List<PATH_ELEMENT>> pathFutureListener = new IFutureListener<List<PATH_ELEMENT>>() {
		
		@Override
		public void futureEvent(FutureWithListeners<List<PATH_ELEMENT>> source, FutureStatus oldStatus, FutureStatus newStatus) {
			synchronized(mutex) {
				source.removeFutureListener(this);
				if (pathFuture != source) {
					// we've been called from the future that is not being used by the executor
					return;
				}
				switch(newStatus) {
				case FUTURE_IS_READY:
					pathComputed();
					return;
				case COMPUTATION_EXCEPTION:
				case CANCELED:
					pathComputationFailed();
					return;
				case FUTURE_IS_BEING_COMPUTED:
					throw new RuntimeException("FutureWithListeners can't change its state to FUTURE_IS_BEING_COMPUTED.");
				}
			}
		}	
		
	};
	
	public BasePathExecutor() {
		this(null);
	}
	
	public BasePathExecutor(Logger log) {
		this.log = log;
	}

//	@Override
	@Override
	public int getPathElementIndex() {
		return pathElementIndex;
	}
	
	@Override
	public IPathFuture<PATH_ELEMENT> getPathFuture() {
		return pathFuture;
	}
	
	@Override
	public PATH_ELEMENT getPathFrom() {
		return getPathFuture().getPathFrom();
	}
	
	@Override
	public PATH_ELEMENT getPathTo() {
		return getPathFuture().getPathTo();
	}

	
	/**
	 * Utility method that is responsible for creating new state for the path executor. You may override this method
	 * to fine-control which implementations {@link IPathExecutorState} are instantiated.
	 * <p><p>
	 * This method allows you to provide own {@link IPathExecutorState} implementation that may carry additional
	 * information about the executor's state.
	 *  
	 * @param state
	 * @return
	 */
	protected IPathExecutorState createState(PathExecutorState state) {
		switch(state) {
		case SWITCHED_TO_ANOTHER_PATH_ELEMENT: return new BasePathExecutorState(PathExecutorState.SWITCHED_TO_ANOTHER_PATH_ELEMENT);
		default: return BasePathExecutorState.getState(state);
		}
	}
	
	// ---
	// =====
	// followPath()
	// =====
	// ---
	
	/**
	 * Tell the executor to start navigating the agent along the 'path'.
	 * <p><p>
	 * If called and the {@link BasePathExecutor#isExecuting()}, it first calls {@link BasePathExecutor#stop()}.
	 * <p><p>
	 * For more info see {@link AbstractPathExecutor#followPath(IPathFuture)}
	 * 
	 * @param path path to navigate along
	 */
	@Override
	public final void followPath(IPathFuture<? extends PATH_ELEMENT> path) {
		synchronized(mutex) {
			if (isExecuting()) {
				stop();			
			}
			if (log != null && log.isLoggable(Level.INFO)) log.info("followPath called, destination " + path.getPathTo());			
			pathFuture = (IPathFuture<PATH_ELEMENT>) path;
			preFollowPathImpl();
			switchState(createState(PathExecutorState.FOLLOW_PATH_CALLED));
			followPathImpl();
			if (path == null) {
				pathComputationFailed();
				return;
			}
			switch(path.getStatus()) {
			case COMPUTATION_EXCEPTION:
			case CANCELED:
				pathComputationFailed();
				return;
			case FUTURE_IS_READY:
				if (getPath() == null) {
					pathComputationFailed();
				} else {
					pathComputed();
				}
				return;
			case FUTURE_IS_BEING_COMPUTED:
				pathFuture.addFutureListener(pathFutureListener);
				break;
			default:
				throw new RuntimeException("Unhandled path future status '" + path.getStatus() + "'.");
			}
			// if we get here, we've add a future listener to 'pathFuture'
			// but what if the path future status has changed before we have attached the listener?
			// ... check it again :-)
			switch(path.getStatus()) {
			case COMPUTATION_EXCEPTION:
			case CANCELED:
				pathComputationFailed();
				return;
			case FUTURE_IS_READY:
				pathComputed();
				return;
			case FUTURE_IS_BEING_COMPUTED:
				return;
			default:
				throw new RuntimeException("Unhandled path future status '" + path.getStatus() + "'.");
			}
		}
	}
	
	/**
	 * Method that is called just before the executor's state is switched to {@link PathExecutorState#FOLLOW_PATH_CALLED} from within
	 * the {@link BasePathExecutor#followPath(IPathFuture)} method.
	 * <p><p>
	 * You may utilize this methods this way:
	 * <ul>
	 *   <li>Initialize inner/public fields that can be used by executor state listeners.</li>
	 *   <li>Store values that should be passed into {@link BasePathExecutor#createState(PathExecutorState)} to create more-informed state object.</li>
	 * </ul>
	 * <p>
	 * Current implementation ensures that the state of the path executor is cleared.
	 */
	protected void preFollowPathImpl() {	
		// ensure that the previous state is cleared
		previousPathElementIndex = -1;
		pathElementIndex = -1;
	}

	/**
	 * Method that is called just after the executor's state is switched to {@link PathExecutorState#FOLLOW_PATH_CALLED} from
	 * within the {@link BasePathExecutor#followPath(IPathFuture)} method.
	 * <p><p>
	 * You may utilize this method this way:
	 * <ul>
	 *   <li>Examine the state of the executor / values produced by executor state listeners.</li>
	 * </ul> 
	 */
	protected abstract void followPathImpl();
	
	// ---
	// =====
	// pathComputed()
	// =====
	// ---
	
	/**
	 * Path has been computed and is available in {@link BasePathExecutor#pathFuture}. This method is automatically called
	 * from the {@link BasePathExecutor#followPath(IPathFuture)} or by {@link BasePathExecutor#pathFutureListener} whenever
	 * the path is ready. 
	 * <p><p>
	 * Effective only if the state is: {@link PathExecutorState#FOLLOW_PATH_CALLED}
	 */
	protected final void pathComputed() {
		synchronized(mutex) {
			if (notInState(PathExecutorState.FOLLOW_PATH_CALLED)) return;
			if (log != null && log.isLoggable(Level.FINE)) log.fine("path computed, size == " + getPath().size());
			pathFuture.removeFutureListener(pathFutureListener);			
			prePathComputedImpl();
			switchState(createState(PathExecutorState.PATH_COMPUTED));
			if (inState(PathExecutorState.PATH_COMPUTED)) {
				pathComputedImpl();
			}
		}
	}
	
	/**
	 * Method that is called just before the executor's state is switched to {@link PathExecutorState#PATH_COMPUTED} from within
	 * the {@link BasePathExecutor#pathComputed()} method. Note that since this method is called, the path can be simply 
	 * obtained by calling {@link BasePathExecutor#getPath()}.
	 * <p><p>
	 * You may utilize this method (for instance) to:
	 * <ul>
	 *   <li>Initialize inner/public fields that can be used by executor state listeners.</li>
	 *   <li>Store values that should be passed into {@link BasePathExecutor#createState(PathExecutorState)} to create more-informed state object.</li>
	 * </ul>
	 * <p>
	 * Note that you should call {@link BasePathExecutor#switchToAnotherPathElement(int)} (to mark the index of the first path element)
	 * in this method or in {@link BasePathExecutor#pathComputedImpl()}. The first path element index will likely be '0'.
	 * <p><p>
	 * Resets and enables all {@link AbstractPathExecutor#stuckDetectors}.
	 */
	protected void prePathComputedImpl() {
		for (IStuckDetector detector : stuckDetectors) {
			detector.reset();
			detector.setEnabled(true);
		}
	}
	
	/**
	 * Method that is called just after the executor's state is switched to {@link PathExecutorState#PATH_COMPUTED} from
	 * within the {@link BasePathExecutor#pathComputed()} method. Note that the path can be simply obtained
	 * by calling {@link BasePathExecutor#getPath()}.
	 * <p><p>
	 * You may utilize this method (for instance) to:
	 * <ul>
	 *   <li>Examine the state of the executor / values produced by executor state listeners.</li>
	 * </ul> 
	 * <p>
	 * Note that you should call {@link BasePathExecutor#switchToAnotherPathElement(int)} (to mark the index of the first path element)
	 * in this method or in {@link BasePathExecutor#prePathComputedImpl()}. The first path element index will likely be '0'.
	 */
	protected abstract void pathComputedImpl();
	
	// ---
	// =====
	// pathComputationFailed()
	// =====
	// ---
	
	/**
	 * Path computation has failed, path is unavailable and the executor can't start navigate the agent through the environment.
	 * This method is automatically called
	 * from the {@link BasePathExecutor#followPath(IPathFuture)} or by {@link BasePathExecutor#pathFutureListener} whenever
	 * it is found out that the path can never be obtained from the {@link BasePathExecutor#pathFuture}.
	 * <p><p>
	 * Effective only if the state is: {@link PathExecutorState#FOLLOW_PATH_CALLED}
	 */
	protected final void pathComputationFailed() {
		synchronized(mutex) {
			if (notInState(PathExecutorState.FOLLOW_PATH_CALLED)) return;
			if (log != null && log.isLoggable(Level.WARNING)) log.warning("path computation failed");
			if (pathFuture != null) {
				pathFuture.removeFutureListener(pathFutureListener);
			}
			prePathComputationFailed();
			switchState(createState(PathExecutorState.PATH_COMPUTATION_FAILED));
			if (inState(PathExecutorState.PATH_COMPUTATION_FAILED)) {
				pathComputationFailedImpl();
			}
		}		
	}
	
	/**
	 * Method that is called just before the executor's state is switched to {@link PathExecutorState#PATH_COMPUTATION_FAILED} from within
	 * the {@link BasePathExecutor#pathComputationFailed()} method.
	 * <p><p>
	 * You may utilize this methods (for instance) to:
	 * <ul>
	 *   <li>Initialize inner/public fields that can be used by executor state listeners.</li>
	 *   <li>Store values that should be passed into {@link BasePathExecutor#createState(PathExecutorState)} to create more-informed state object.</li>
	 *   <li>Clean up some executor's fields.</li>
	 * </ul>
	 * <p>
	 * Empty implementation, does not doing anything.
	 */
	protected void prePathComputationFailed() {
	}
	
	/**
	 * Method that is called just after the executor's state is switched to {@link PathExecutorState#PATH_COMPUTATION_FAILED} from
	 * within the {@link BasePathExecutor#pathComputationFailed()} method. Note that the path can be simply obtained
	 * by calling {@link BasePathExecutor#getPath()}.
	 * <p><p>
	 * You may utilize this method (for instance) to:
	 * <ul>
	 *   <li>Examine the state of the executor / values produced by executor state listeners.</li>
	 *   <li>Clean up some executor's fields.</li>
	 * </ul> 
	 */
	protected abstract void pathComputationFailedImpl();
	
	// ---
	// =====
	// switchToAnotherPathElement()
	// =====
	// ---
	
	/**
	 * Switches from current path element index into the new one. You have to call this method
	 * from your implementation whenever you want to change {@link BasePathExecutor#pathElementIndex}.
	 * <p><p>
	 * This method should be also called as a reaction to {@link BasePathExecutor#pathComputed()} method call, i.e.,
	 * from {@link BasePathExecutor#prePathComputedImpl()} or {@link BasePathExecutor#pathComputedImpl()} to change
	 * the index into '0' (or other index as you see fit).
	 * <p><p>
	 * Effective only if {@link AbstractPathExecutor#isExecuting()}.
	 */
	@Override
	public final void switchToAnotherPathElement(int index) {
		synchronized(mutex) {
			if (!isExecuting()) return;			
			List<PATH_ELEMENT> path = getPath();
			if (path == null) throw new PogamutException("Can't switch to element of index '" + index + "' as the current path executor's path is null.", this);
			if (index < 0 || index >= path.size()) throw new PogamutException("Can't switch to element of index '" + index + "' as it is out of path range (path.size() = " + path.size() + ").", this);
			if (log != null && log.isLoggable(Level.FINER)) log.finer("switching to path element " + (index+1) + "/" + getPath().size() + " -> " + path.get(index));
			preSwitchToAnotherPathElementImpl(index);		
			switchState(createState(PathExecutorState.SWITCHED_TO_ANOTHER_PATH_ELEMENT));
			if (inState(PathExecutorState.SWITCHED_TO_ANOTHER_PATH_ELEMENT)) {
				switchToAnotherPathElementImpl();
			}
		}
	}
	
	/**
	 * Method that is called just before the executor's state is switched to {@link PathExecutorState#SWITCHED_TO_ANOTHER_PATH_ELEMENT} from within
	 * the {@link BasePathExecutor#switchToAnotherPathElement(int)} method. Note that this method is called to alter
	 * values inside {@link BasePathExecutor#previousPathElementIndex} and {@link BasePathExecutor#pathElementIndex}
	 * by using 'newIndex'.
	 * <p><p>
	 * You may additionally utilize this method (for instance) to:
	 * <ul>
	 *   <li>Initialize inner/public fields that can be used by executor state listeners.</li>
	 *   <li>Store values that should be passed into {@link BasePathExecutor#createState(PathExecutorState)} to create more-informed state object.</li>
	 * </ul>
	 * <p>
	 * Current implementation does (and nothing else):
	 * <ul>
	 *   <li>previousPathElementIndex = pathElementIndex;</li>
	 *   <li>pathElementIndex = newIndex;</li>
	 * </ul>
	 * 
	 * @param newIndex index of the path element that should be pursued now 
	 */
	protected void preSwitchToAnotherPathElementImpl(int newIndex) {
		previousPathElementIndex = pathElementIndex;
		pathElementIndex = newIndex;
	}
	
	/**
	 * Method that is called just after the executor's state is switched to {@link PathExecutorState#SWITCHED_TO_ANOTHER_PATH_ELEMENT} from
	 * within the {@link BasePathExecutor#switchToAnotherPathElement(int)} method. Note that this method
	 * is called after the values inside {@link BasePathExecutor#previousPathElementIndex} and {@link BasePathExecutor#pathElementIndex}
	 * are overwritten with new ones. 
	 * <p><p>
	 * You may utilize this method (for instance) to:
	 * <ul>
	 *   <li>Examine the state of the executor / values produced by executor state listeners.</li>
	 * </ul> 
	 */
	protected abstract void switchToAnotherPathElementImpl();
	
	// ---
	// =====
	// stop()
	// =====
	// ---
	
	/**
	 * Used to stop the path executor, for more info see {@link AbstractPathExecutor#stop()}.
	 * <p><p>
	 * Effective only if the state is <b>NOT</b>: {@link PathExecutorState#STOPPED}
	 */
	@Override
	public final void stop() {
		synchronized(mutex) {			
			if (inState(PathExecutorState.STOPPED)) return;
			if (log != null && log.isLoggable(Level.INFO)) log.info("stop");
			stopImpl();
			switchState(createState(PathExecutorState.STOPPED));	
			if (inState(PathExecutorState.STOPPED)) {
				stopped();
			}			
		}
	}
	
	/**
	 * Method that is called just before the executor's state is switched to {@link PathExecutorState#STOPPED} from within the 
	 * {@link BasePathExecutor#stop()} method. Note that this method is called to clean up internal data structures
	 * before we switch itself into {@link PathExecutorState#STOPPED} state.
	 * <p><p>
	 * You may additionally utilize this method (for instance) to:
	 * <ul>
	 *   <li>Initialize inner/public fields that can be used by executor state listeners.</li>
	 *   <li>Store values that should be passed into {@link BasePathExecutor#createState(PathExecutorState)} to create more-informed state object.</li>
	 * </ul>
	 * <p>
	 * Current implementation does (and nothing else):
	 * <ul>
	 *   <li>sets {@link BasePathExecutor#previousPathElementIndex} and {@link BasePathExecutor#pathElementIndex} to -1</li>
	 *   <li>removes {@link BasePathExecutor#pathFutureListener} from the {@link BasePathExecutor#pathFuture}</li>
	 *   <li>sets {@link BasePathExecutor#pathFuture} to null</li>
	 *   <li>disables all {@link AbstractPathExecutor#stuckDetectors}</li>   	 
	 * </ul>
	 * 
	 * @param newIndex index of the path element that should be pursued now 
	 */
	protected void stopImpl() {
		previousPathElementIndex = -1;
		pathElementIndex = -1;			
		if (pathFuture != null) {
			pathFuture.removeFutureListener(pathFutureListener);
			pathFuture = null;
		}
		for (IStuckDetector stuckDetector : stuckDetectors) {
			stuckDetector.setEnabled(false);
		}
	}
	
	/**
	 * Method that is called just after the executor's state is switched to {@link PathExecutorState#STOPPED} from
	 * within the {@link BasePathExecutor#stop()} method.
	 * <p><p>
	 * You may utilize this method (for instance) to:
	 * <ul>
	 *   <li>Finish the internal data structures clean up.</li>
	 * </ul> 
	 */
	protected abstract void stopped();
	
	// ---
	// =====
	// checkStuckDetectors()
	// =====
	// ---
	
	/**
	 * This method checks (one-by-one) stuck detectors whether some of them is reporting that the agent has stuck.
	 * If the stuck is detected, particular {@link IStuckDetector} is returned. If the stuck is not detected,
	 * null is returned.
	 * 
	 * @return first detector to report that agent has stuck or null
	 */
	@Override
	public IStuckDetector checkStuckDetectors() {
		for (IStuckDetector detector : stuckDetectors) {
			if (detector.isStuck()) {
				return detector;
			}
		}
		return null;
	}
	
	// ---
	// =====
	// stuck()
	// =====
	// ---
	
	/**
	 * Method that changes the state to {@link PathExecutorState#STUCK} that should be called whenever some 
	 * stuck detector detects that the agent is stuck.
	 * <p><p>
	 * It is currently called only from {@link BasePathExecutor#checkStuckDetectors()} which must be called from
	 * the descendant.
	 * <p><p>
	 * Note that you may actually pass 'null' as 'detector' into the method.
	 * <p><p>
	 * Effective only if the state is: {@link PathExecutorState#FOLLOW_PATH_CALLED} or {@link PathExecutorState#PATH_COMPUTED} or {@link PathExecutorState#SWITCHED_TO_ANOTHER_PATH_ELEMENT}
	 */
	@Override
	public final void stuck() {
		synchronized(mutex) {
			if (notInState(PathExecutorState.FOLLOW_PATH_CALLED, PathExecutorState.PATH_COMPUTED, PathExecutorState.SWITCHED_TO_ANOTHER_PATH_ELEMENT)) return;
			preStuckImpl();
			switchState(createState(PathExecutorState.STUCK));
			if (inState(PathExecutorState.STUCK)) {
				stuckImpl();
			}
		}
	}
	
	/**
	 * Method that is called just before the executor's state is switched to {@link PathExecutorState#STUCKD} from within the 
	 * {@link BasePathExecutor#stuck(IStuckDetector)} method. Note that this method is called to clean up internal data structures
	 * before we switch itself into {@link PathExecutorState#STUCK} state.
	 * <p><p>
	 * BasePathExecutor's implementation disables all {@link BasePathExecutor#stuckDetectors}.
	 * <p><p>
	 * You may utilize this method (for instance) to:
	 * <ul>
	 *   <li>Initialize inner/public fields that can be used by executor state listeners.</li>
	 *   <li>Store values that should be passed into {@link BasePathExecutor#createState(PathExecutorState)} to create more-informed state object.</li>
	 * </ul>
	 * <p><p>
	 * Current implementation does (and nothing else):
	 * <ul>
	 *   <li>disables all {@link AbstractPathExecutor#stuckDetectors}</li>
	 * </ul>
	 */
	protected void preStuckImpl() {	
		for (IStuckDetector stuckDetector : stuckDetectors) {
			stuckDetector.setEnabled(false);
		}
	}
	
	/**
	 * Method that is called just after the executor's state is switched to {@link PathExecutorState#STUCK} from
	 * within the {@link BasePathExecutor#stuck(IStuckDetector)} method.
	 * <p><p>
	 * You may utilize this method (for instance) to:
	 * <ul>
	 *   <li>Finish the internal data structures clean up.</li>
	 * </ul>
	 * <p><p>
	 * <b>WARNING:</b> 'null' may be passed as 'detector' if the stuck has been detected by different component 
	 */
	protected abstract void stuckImpl();
	
	// ---
	// =====
	// targetReached()
	// =====
	// ---
	
	/**
	 * Method that should be called whenever the path executor reaches the end of the path. Currently this method
	 * is not called from anywhere of {@link BasePathExecutor}.
	 * <p><p>
	 * Effective only if the state is: {@link PathExecutorState#FOLLOW_PATH_CALLED} or {@link PathExecutorState#PATH_COMPUTED} or {@link PathExecutorState#SWITCHED_TO_ANOTHER_PATH_ELEMENT}
	 */
	public final void targetReached() {
		synchronized(mutex) {
			if (notInState(PathExecutorState.FOLLOW_PATH_CALLED, PathExecutorState.PATH_COMPUTED, PathExecutorState.SWITCHED_TO_ANOTHER_PATH_ELEMENT)) return;
			preTargetReachedImpl();
			switchState(createState(PathExecutorState.TARGET_REACHED));
			if (inState(PathExecutorState.TARGET_REACHED)) {
				targetReachedImpl();
			}
		}
	}
	
	/**
	 * Method that is called just before the executor's state is switched to {@link PathExecutorState#TARGET_REACHED} from within the 
	 * {@link BasePathExecutor#targetReached()} method.
	 * <p><p>
	 * You may utilize this method (for instance) to:
	 * <ul>
	 *   <li>Initialize inner/public fields that can be used by executor state listeners.</li>
	 *   <li>Store values that should be passed into {@link BasePathExecutor#createState(PathExecutorState)} to create more-informed state object.</li>
	 *   <li>Clean up some internal data structures as the target has been reached.</li>
	 * </ul> 
	 * <p>
	 * Empty implementation, does not doing anything.
	 */
	protected void preTargetReachedImpl() {		
	}
	
	/**
	 * Method that is called just after the executor's state is switched to {@link PathExecutorState#TARGET_REACHED} from
	 * within the {@link BasePathExecutor#targetReached()} method.
	 * <p><p>
	 * You may utilize this method (for instance) to:
	 * <ul>
	 *   <li>Examine the state of the executor / values produced by executor state listeners.</li>
	 *   <li>Clean up some internal data structures as the target has been reached.</li>
	 * </ul> 
	 */
	protected abstract void targetReachedImpl();
	
}
