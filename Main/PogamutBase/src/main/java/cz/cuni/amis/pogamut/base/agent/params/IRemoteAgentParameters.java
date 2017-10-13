package cz.cuni.amis.pogamut.base.agent.params;

import cz.cuni.amis.pogamut.base.communication.connection.IWorldConnectionAddress;

/**
 * Remote agent parameters are additionally providing an address of the remote environment which the 
 * agent has to connect into.
 * 
 * @author Jimmy
 *
 * @param <ADDRESS>
 */
public interface IRemoteAgentParameters extends IAgentParameters {
	
	/**
	 * Address of the environment the newly created agent has to connect into.
	 * 
	 * @return environment address
	 */
	public IWorldConnectionAddress getWorldAddress();

}
