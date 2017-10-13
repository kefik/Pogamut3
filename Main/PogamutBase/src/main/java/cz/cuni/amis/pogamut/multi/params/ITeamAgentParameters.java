package cz.cuni.amis.pogamut.multi.params;

import cz.cuni.amis.pogamut.multi.communication.worldview.ISharedWorldView;

/**
 * These parameters declares getter for {@link ISharedWorldView} instance that should be used by the particular agent.
 * <p><p>
 * Such shared world view instance has to be picked by the agent during its instantiation preferable via Guice IOC mechanis,.
 * 
 * @author Jimmy
 *
 * @param <SHARED_WORLDVIEW>
 */
public interface ITeamAgentParameters<SHARED_WORLDVIEW extends ISharedWorldView> {

	public SHARED_WORLDVIEW getSharedWorldView();
	
}
