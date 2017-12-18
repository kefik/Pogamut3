package cz.cuni.amis.pogamut.ut2004.examples.hunterbotfsm;

import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;

public abstract class State<TContext extends UT2004BotModuleController<UT2004Bot>> {
    public abstract State<TContext> execute(TContext context);
}
