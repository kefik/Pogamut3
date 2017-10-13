package cz.cuni.amis.pogamut.ut2004multi.factory.guice.remoteagent;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.name.Names;

import cz.cuni.amis.pogamut.base.agent.IAgent;
import cz.cuni.amis.pogamut.base.communication.translator.IWorldMessageTranslator;
import cz.cuni.amis.pogamut.base.communication.worldview.ILockableWorldView;
import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencies;
import cz.cuni.amis.pogamut.base.component.lifecyclebus.ILifecycleBus;
import cz.cuni.amis.pogamut.base.component.lifecyclebus.LifecycleBus;
import cz.cuni.amis.pogamut.base.utils.guice.AdaptableProvider;
import cz.cuni.amis.pogamut.base3d.ILockableVisionWorldView;
import cz.cuni.amis.pogamut.base3d.agent.IAgent3D;
import cz.cuni.amis.pogamut.base3d.worldview.IVisionWorldView;
import cz.cuni.amis.pogamut.multi.agent.ITeamedAgentId;
import cz.cuni.amis.pogamut.multi.agent.impl.TeamedAgentId;
import cz.cuni.amis.pogamut.multi.communication.worldview.ISharedWorldView;
import cz.cuni.amis.pogamut.multi.communication.worldview.impl.BatchAwareLocalWorldView;
import cz.cuni.amis.pogamut.multi.communication.worldview.impl.LocalWorldViewAdapter;
import cz.cuni.amis.pogamut.ut2004.bot.IUT2004Bot;
import cz.cuni.amis.pogamut.ut2004.bot.IUT2004BotController;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.bot.params.UT2004BotParameters;
import cz.cuni.amis.pogamut.ut2004.communication.translator.bot.BotFSM;
import cz.cuni.amis.pogamut.ut2004.factory.guice.remoteagent.UT2004BotModule;
import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.UT2004BatchAwareSharedWorldView;
import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.UT2004LockableLocalWorldView;

/**
 * Implements a bot module using shared WorldView
 * @author srlok
 *
 */
public class UT2004MultiBotModule<PARAMS extends UT2004BotParameters> extends UT2004BotModule<PARAMS>
{
	
	private Class<? extends IUT2004BotController> botControllerClass;
	
	protected UT2004MultiBotModule() 
	{
	}
	
	public UT2004MultiBotModule(Class<? extends IUT2004BotController> botControllerClass) {
		this.botControllerClass = botControllerClass;
	}
	
	@Override
	public void prepareNewAgent(PARAMS agentParameters)
	{
		super.prepareNewAgent(agentParameters);
		teamedIdProvider.set( (TeamedAgentId)agentParameters.getAgentId());
	}
	
	protected AdaptableProvider<TeamedAgentId> teamedIdProvider = new AdaptableProvider<TeamedAgentId>();
	
	protected Provider<TeamedAgentId> getTeamedAgentIdProvider()
	{
		return this.teamedIdProvider;
	}
	
	@Override
	protected void configureModules() {
		super.configureModules();
		addModule(new AbstractModule() {

			@Override
			public void configure() {	
				
				bind(IWorldMessageTranslator.class).to(BotFSM.class);
				bind(IWorldView.class).to(IVisionWorldView.class);
				bind(IVisionWorldView.class).to(ILockableVisionWorldView.class);
				bind(ILockableWorldView.class).to(ILockableVisionWorldView.class);
				bind(ILockableVisionWorldView.class).to(LocalWorldViewAdapter.class);
				bind(BatchAwareLocalWorldView.class).to(UT2004LockableLocalWorldView.class);
				bind(ISharedWorldView.class).to(UT2004BatchAwareSharedWorldView.class);
				
				bind(IComponentBus.class).to(ILifecycleBus.class);
				bind(ILifecycleBus.class).to(LifecycleBus.class);
				
				bind(ITeamedAgentId.class).to(TeamedAgentId.class);
				bind(TeamedAgentId.class).toProvider( getTeamedAgentIdProvider());
				
				bind(ComponentDependencies.class).annotatedWith(Names.named(UT2004LockableLocalWorldView.WORLDVIEW_DEPENDENCY)).toProvider(worldViewDependenciesProvider);
				bind(IAgent.class).to(IAgent3D.class);
				bind(IAgent3D.class).to(IUT2004Bot.class);
				bind(IUT2004Bot.class).to(UT2004Bot.class);
				
				if (botControllerClass != null) {
					bind(IUT2004BotController.class).to(botControllerClass);
				}
				
			}
			
		});
	}

}
