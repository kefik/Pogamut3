package cz.cuni.amis.pogamut.multi.factory.guice;

import cz.cuni.amis.pogamut.base.communication.connection.IWorldConnection;
import cz.cuni.amis.pogamut.base.factory.guice.GuiceAgentModule;
import cz.cuni.amis.pogamut.base.factory.guice.GuiceRemoteAgentModule;
import cz.cuni.amis.pogamut.base.utils.guice.AdaptableProvider;
import cz.cuni.amis.pogamut.multi.communication.worldview.ISharedWorldView;
import cz.cuni.amis.pogamut.multi.params.ITeamRemoteAgentParameters;
import cz.cuni.amis.utils.NullCheck;

/**
 * Module extending {@link GuiceRemoteAgentModule} for the purpose of remote agents (those communicating with the world using 
 * {@link IWorldConnection}) that is using {@link ISharedWorldView} to synchronize information within the agent's team.
 * <p><p>
 * See {@link GuiceRemoteAgentModule} for more information.
 * <p><p>
 * This module introduces {@link GuiceTeamRemoteAgentModule#getSharedWorldViewProvider()} that is correctly filled during {@link GuiceTeamRemoteAgentModule#prepareNewAgent(ITeamRemoteAgentParameters)}
 * and so it can be used during agent construction.
 * 
 * @see GuiceAgentModule
 * @author Jimmy
 */
public abstract class GuiceTeamRemoteAgentModule<PARAMS extends ITeamRemoteAgentParameters> extends GuiceRemoteAgentModule<PARAMS> {
	
	private AdaptableProvider<ISharedWorldView> sharedWorldViewProvider = new AdaptableProvider<ISharedWorldView>(null);
		
	public AdaptableProvider<ISharedWorldView> getSharedWorldViewProvider() {
		return sharedWorldViewProvider;
	}

	@Override
	public void prepareNewAgent(PARAMS agentParameters) {
		super.prepareNewAgent(agentParameters);
		NullCheck.check(agentParameters.getSharedWorldView(), "agentParameters.getSharedWorldView()");
		sharedWorldViewProvider.set(agentParameters.getSharedWorldView());
	};

}