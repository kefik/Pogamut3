package cz.cuni.amis.pogamut.ut2004.agent.navigation;

import cz.cuni.amis.pogamut.base.agent.navigation.IStuckDetector;
import cz.cuni.amis.pogamut.base.agent.navigation.PathExecutorState;
import cz.cuni.amis.pogamut.base.agent.navigation.impl.BasePathExecutorState;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPointNeighbourLink;

/**
 * This class is broadcast by {@link UT2004PathExecutor} whenever it gets stuck. It contains detail information about whereabouts of the stuck.
 * 
 * @author Jimmy
 *
 */
public class UT2004PathExecutorStuckState extends BasePathExecutorState {

	private boolean globalTimeout = false;
	private IStuckDetector stuckDetector = null;
	private NavPointNeighbourLink link = null;
	
	public UT2004PathExecutorStuckState() {
		super(PathExecutorState.STUCK);
	}

	/**
	 * Whether it was GLOBAL timeout (imposed by {@link UT2004PathExecutor}) == true, or it was detected by {@link #getStuckDetector()} == false.
	 * @return
	 */
	public boolean isGlobalTimeout() {
		return globalTimeout;
	}

	public void setGlobalTimeout(boolean globalTimeout) {
		this.globalTimeout = globalTimeout;
	}

	/**
	 * If not NULL, contains {@link IStuckDetector} that has reported the stuck ~ why we have stopped the execution.
	 * @return
	 */
	public IStuckDetector getStuckDetector() {
		return stuckDetector;
	}

	public void setStuckDetector(IStuckDetector stuckDetector) {
		this.stuckDetector = stuckDetector;
	}

	/**
	 * If available contains {@link NavPointNeighbourLink} we have failed to traverse.
	 * @return
	 */
	public NavPointNeighbourLink getLink() {
		return link;
	}

	public void setLink(NavPointNeighbourLink link) {
		this.link = link;
	}

	
	
}
