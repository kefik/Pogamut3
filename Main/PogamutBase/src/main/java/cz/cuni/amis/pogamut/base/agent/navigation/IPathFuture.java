package cz.cuni.amis.pogamut.base.agent.navigation;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import cz.cuni.amis.utils.future.FutureStatus;
import cz.cuni.amis.utils.future.IFutureListener;

/**
 * Returns a path as the future result of {@link IPathPlanner} computation.
 * <p><p>
 * You should read the javadoc for {@link Future} first to know the concept first.
 * 
 * @author Jimmy
 *
 * @param <PATH_ELEMENT>
 */
public interface IPathFuture<PATH_ELEMENT> extends Future<List<PATH_ELEMENT>>{

	/**
	 * First, see {@link Future#get()}.
	 * <p><p>
	 * May return null if no such path exist.
	 * <p><p>
	 * Throws some runtime exception if the path could not be computed (exact type of exception
	 * depends on the implementor of the interface).
	 * 
	 * @return computed path
	 */
	@Override
	public List<PATH_ELEMENT> get();
	
	/**
	 * Returns a path from {@link IPathFuture#getPathFrom()} to {@link IPathFuture#getPathTo()}. "From" is the first
	 * element of the path, "To" is the last element of the path.
	 * <p><p>
	 * First, see {@link Future#get(long, TimeUnit)}.
	 * <p><p>
	 * May return null if no such path exist.
	 * <p><p>
	 * Throws some runtime exception if the path could not be computed (exact type of exception
	 * depends on the implementor of the interface).
	 */
	@Override
	public List<PATH_ELEMENT> get(long timeout, TimeUnit unit);
	
	/**
	 * Where does the path start. Note that this point might not be the first item of the path element list, this element
	 * marks the start location from which the planner has begun.
	 * 
	 * @return
	 */
	public PATH_ELEMENT getPathFrom();
	
	/**
	 * Where does the path end. Note that this point might not be the last item of the path element list, this
	 * element marks the end location to which the planner should plan the path.
	 * 
	 * @return
	 */
	public PATH_ELEMENT getPathTo();
	
	/**
	 * Current status of the path computation.
	 * @return
	 */
	public FutureStatus getStatus();
	
	/**
	 * Adds a listener on a future status (using strong reference). Listeners are automatically
	 * removed whenever the future gets its result (or is cancelled or an exception happens).
	 * @param listener
	 */
	public void addFutureListener(IFutureListener<List<PATH_ELEMENT>> listener);
	
	/**
	 * Removes a listener from the future.
	 * @param listener
	 */
	public void removeFutureListener(IFutureListener<List<PATH_ELEMENT>> listener);
	
	/**
	 * Whether some listener is listening on the future.
	 * @param listener
	 * @return
	 */
	public boolean isListening(IFutureListener<List<PATH_ELEMENT>> listener);
	
}
