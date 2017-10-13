package cz.cuni.amis.pogamut.ut2004.agent.navigation;

import java.util.List;

import cz.cuni.amis.pogamut.base.agent.navigation.IPathExecutorHelper;
import cz.cuni.amis.pogamut.base.agent.navigation.IStuckDetector;

public interface IUT2004PathExecutorHelper<PATH_ELEMENT> extends IPathExecutorHelper<PATH_ELEMENT> {

	/**
	 * Returns list of all stuck detectors registered inside the executor.
	 * @return
	 */
	public List<IStuckDetector> getStuckDetectors();
	
}
