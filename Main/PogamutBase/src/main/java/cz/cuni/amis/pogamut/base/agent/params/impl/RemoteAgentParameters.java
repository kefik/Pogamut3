package cz.cuni.amis.pogamut.base.agent.params.impl;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.agent.params.IAgentParameters;
import cz.cuni.amis.pogamut.base.agent.params.IRemoteAgentParameters;
import cz.cuni.amis.pogamut.base.agent.utils.runner.IAgentRunner;
import cz.cuni.amis.pogamut.base.agent.utils.runner.IMultipleAgentRunner;
import cz.cuni.amis.pogamut.base.communication.connection.IWorldConnectionAddress;

/**
 * Remote agent parameters are meant to provide run-time parameters needed by various agents that works on client/server
 * paradigm.
 * <p><p>
 * If you need to populate the parameters after instantiation, use setters available in this
 * class: {@link RemoteAgentParameters#setAgentId(IAgentId)}, {@link RemoteAgentParameters#setWorldAddress(IWorldConnectionAddress)}.
 * <p><p>
 * NOTE: all {@link IAgentParameters} implementors are usually used together with {@link IAgentRunner} or {@link IMultipleAgentRunner}
 * which usually contains sensible default params, therefore there is no need to set all parameters
 * into newly created ones as runners will supply them via {@link IAgentParameters#assignDefaults(IAgentParameters)}.
 * 
 * @see AgentParameters
 * @author Jimmy
 */
public class RemoteAgentParameters extends AgentParameters implements IRemoteAgentParameters {

	private IWorldConnectionAddress address;

	public RemoteAgentParameters() {
		super();
		this.address = null;
	}
	
	@Override
	public IWorldConnectionAddress getWorldAddress() {
		return address;
	}
	
	@Override
	public RemoteAgentParameters setAgentId(IAgentId agentId) {
		super.setAgentId(agentId);
		return this;
	}
	
	/**
	 * Sets connection address into the parameters.
	 * <p><p>
	 * WARNING: Note that you should not mess with 'setters' in different threads as they
	 * are non-thread-safe and may interrupt horrible agent instantiations with such behavior.
	 * @param address
	 * @return this instance
	 */
	public RemoteAgentParameters setWorldAddress(IWorldConnectionAddress address) {
		this.address = address;
		return this;
	}
	
	@Override
	public void assignDefaults(IAgentParameters defaults) {
		super.assignDefaults(defaults);
		if (defaults instanceof IRemoteAgentParameters) {
			if (address == null) {
				address = ((IRemoteAgentParameters)defaults).getWorldAddress();
			}
		}
	}

}
