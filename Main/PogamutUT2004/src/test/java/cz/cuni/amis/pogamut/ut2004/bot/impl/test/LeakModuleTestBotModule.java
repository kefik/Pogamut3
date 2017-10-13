package cz.cuni.amis.pogamut.ut2004.bot.impl.test;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;

import cz.cuni.amis.pogamut.base.communication.command.IAct;
import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.pogamut.ut2004.agent.params.UT2004AgentParameters;
import cz.cuni.amis.pogamut.ut2004.bot.IUT2004Bot;
import cz.cuni.amis.pogamut.ut2004.bot.IUT2004BotController;
import cz.cuni.amis.pogamut.ut2004.factory.guice.remoteagent.UT2004BotModule;
import cz.cuni.amis.utils.flag.FlagInteger;
import cz.cuni.amis.utils.flag.ImmutableFlag;

public class LeakModuleTestBotModule extends UT2004BotModule {

	protected LeakModuleTestBotModule() {
	}
	
	public LeakModuleTestBotModule(Class<? extends IUT2004BotController> botControllerClass) {
		super(botControllerClass);
	}
		
	@Override
	protected void configureModules() {
		super.configureModules();
		addModule(new AbstractModule() {

			@Override
			protected void configure() {
				bind(IUT2004Bot.class).to(LeakModuleTestBot.class);
			}
			
		});
	}
	
}
