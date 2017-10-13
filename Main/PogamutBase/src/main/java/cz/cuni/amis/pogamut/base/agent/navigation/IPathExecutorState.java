package cz.cuni.amis.pogamut.base.agent.navigation;

/**
 * Represents a state of the {@link IPathExecutor} providing a high-level description of the state via 
 * {@link IPathExecutorState#getState()}.
 * <p><p>
 * Note that executor states can't change on a whim - please read javadoc for {@link PathExecutorState} that contains
 * description of how the state can change.
 * <p><p>
 * Every {@link IPathExecutor} implementor may provide own implementation of this interface so it is able to pass
 * arbitrary information within its state.
 *  
 * @author Jimmy
 */
public interface IPathExecutorState {

	/**
	 * Returns current high-level state of the {@link IPathExecutor}.
	 * @return
	 */
	public PathExecutorState getState();
	
}
