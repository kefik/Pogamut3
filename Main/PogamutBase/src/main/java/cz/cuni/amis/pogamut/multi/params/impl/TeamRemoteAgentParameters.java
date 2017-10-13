package cz.cuni.amis.pogamut.multi.params.impl;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.agent.params.IAgentParameters;
import cz.cuni.amis.pogamut.base.agent.params.impl.RemoteAgentParameters;
import cz.cuni.amis.pogamut.base.communication.connection.IWorldConnectionAddress;
import cz.cuni.amis.pogamut.multi.communication.worldview.ISharedWorldView;
import cz.cuni.amis.pogamut.multi.params.ITeamAgentParameters;
import cz.cuni.amis.pogamut.multi.params.ITeamRemoteAgentParameters;

public class TeamRemoteAgentParameters<SHARED_WORLDVIEW extends ISharedWorldView> extends RemoteAgentParameters implements ITeamRemoteAgentParameters<SHARED_WORLDVIEW> {

	protected SHARED_WORLDVIEW sharedWorldView;
	
	public TeamRemoteAgentParameters() {
		super();
		this.sharedWorldView = null;
	}

	
	@Override
	public SHARED_WORLDVIEW getSharedWorldView() {
		return sharedWorldView;
	}
	
	/**
	 * Sets instance of {@link ISharedWorldView} that should be used by this agent.
	 * <p><p>
	 * WARNING: Note that you should not mess with 'setters' in different threads as they
	 * are non-thread-safe and may interrupt horribly agent instantiations with such behavior.
	 * 
	 * @param sharedWorldView
	 * @return this instance
	 */
	public TeamRemoteAgentParameters<SHARED_WORLDVIEW> setSharedWorldView(SHARED_WORLDVIEW sharedWorldView) {
		this.sharedWorldView = sharedWorldView;
		return this;
	}
	
	@Override
	public TeamRemoteAgentParameters<SHARED_WORLDVIEW> setAgentId(IAgentId agentId) {
		super.setAgentId(agentId);
		return this;
	}
	
	@Override
	public TeamRemoteAgentParameters<SHARED_WORLDVIEW> setWorldAddress(IWorldConnectionAddress address) {
		super.setWorldAddress(address);
		return this;
	}
	
	@Override
	public void assignDefaults(IAgentParameters defaults) {
		super.assignDefaults(defaults);
		if (defaults instanceof ITeamAgentParameters) {
			if (sharedWorldView == null) {
				sharedWorldView = (SHARED_WORLDVIEW) ((ITeamAgentParameters)defaults).getSharedWorldView();
			}
		}
	}

}
