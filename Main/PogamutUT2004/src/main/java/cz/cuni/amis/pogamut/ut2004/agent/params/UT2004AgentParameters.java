package cz.cuni.amis.pogamut.ut2004.agent.params;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.agent.impl.AgentId;
import cz.cuni.amis.pogamut.base.agent.params.IAgentParameters;
import cz.cuni.amis.pogamut.base.agent.params.impl.RemoteAgentParameters;
import cz.cuni.amis.pogamut.base.agent.utils.runner.IAgentRunner;
import cz.cuni.amis.pogamut.base.agent.utils.runner.IMultipleAgentRunner;
import cz.cuni.amis.pogamut.base.communication.connection.IWorldConnectionAddress;
import cz.cuni.amis.pogamut.base.communication.connection.impl.socket.ISocketConnectionAddress;

/**
 * Agent parameters are meant to provide run-time parameters needed by various UT2004 agents for 
 * running such as {@link IAgentId} and {@link ISocketConnectionAddress}.
 * <p><p>
 * If you need to populate the parameters after instantiation, use setters available in this
 * class: {@link UT2004AgentParameters#setAgentId(IAgentId)}, {@link UT2004AgentParameters#setWorldAddress(IWorldConnectionAddress)}.
 * <p><p>
 * NOTE: all {@link IAgentParameters} implementors are usually used together with {@link IAgentRunner} or {@link IMultipleAgentRunner}
 * which usually contains sensible default params, therefore there is no need to set all parameters
 * into newly created ones as runners will supply them via {@link IAgentParameters#assignDefaults(IAgentParameters)}.
 * 
 * @see RemoteAgentParameters
 * @author Jimmy
 */
public class UT2004AgentParameters extends RemoteAgentParameters {

	/**
	 * If you need to populate the parameters after instantiation, use setters available in this
	 * class: {@link UT2004AgentParameters#setAgentId(IAgentId)}, {@link UT2004AgentParameters#setWorldAddress(IWorldConnectionAddress)}.
	 */
	public UT2004AgentParameters() {
		super();
	}
	
	@Override
	public UT2004AgentParameters setAgentId(IAgentId agentId) {
		super.setAgentId(agentId);
		return this;
	}
	
	@Override
	public UT2004AgentParameters setWorldAddress(IWorldConnectionAddress address) {
		super.setWorldAddress(address);
		return this;
	}

}
