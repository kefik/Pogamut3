package cz.cuni.amis.pogamut.ut2004.bot.impl.test;

import com.google.inject.AbstractModule;

import cz.cuni.amis.pogamut.ut2004.bot.IUT2004Bot;
import cz.cuni.amis.pogamut.ut2004.bot.IUT2004BotController;
import cz.cuni.amis.pogamut.ut2004.factory.guice.remoteagent.UT2004BotModule;

public class LeakTestBotModule extends UT2004BotModule {
	
	protected LeakTestBotModule() {
	}
	
	public LeakTestBotModule(Class<? extends IUT2004BotController> botControllerClass) {
		super(botControllerClass);
	}
		
	@Override
	protected void configureModules() {
		super.configureModules();
		addModule(new AbstractModule() {

			@Override
			protected void configure() {
				bind(IUT2004Bot.class).to(LeakTestBot.class);
			}
			
		});
	}
	
}
