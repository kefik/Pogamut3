package cz.cuni.amis.pogamut.ut2004.bot;

import cz.cuni.amis.pogamut.base.agent.module.IAgentLogic;
import cz.cuni.amis.pogamut.base.agent.module.LogicModule;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;

public interface IUT2004BotLogicController<BOT extends UT2004Bot, LOGIC_MODULE extends LogicModule> extends IUT2004BotController<BOT>, IAgentLogic<LOGIC_MODULE> {
}
