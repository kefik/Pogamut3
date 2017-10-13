package cz.cuni.amis.pogamut.ut2004.bot.impl;

import cz.cuni.amis.pogamut.base.agent.module.LogicModule;
import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;
import cz.cuni.amis.pogamut.ut2004.agent.module.logic.SyncUT2004BotLogic;
import cz.cuni.amis.pogamut.ut2004.bot.IUT2004BotLogicController;

@AgentScoped
public class UT2004BotLogicController<BOT extends UT2004Bot> extends UT2004BotController<BOT> implements IUT2004BotLogicController<BOT, LogicModule>{

	protected SyncUT2004BotLogic logicModule;
	
	@Override
	public void initializeController(BOT bot) {
		super.initializeController(bot);
		initializeLogic(bot);	
	}
	
	protected void initializeLogic(BOT bot) {
		logicModule = new SyncUT2004BotLogic(bot, this);
	}
	
	@Override
	public long getLogicInitializeTime() {
		return 120000;
	}

	@Override
	public long getLogicShutdownTime() {
		return 120000;
	}
	
	@Override
	public void beforeFirstLogic() {
	}

	@Override
	public void logic() {
	}

	@Override
	public void logicInitialize(LogicModule logicModule) {		
	}

	@Override
	public void logicShutdown() {
	}
	
}
