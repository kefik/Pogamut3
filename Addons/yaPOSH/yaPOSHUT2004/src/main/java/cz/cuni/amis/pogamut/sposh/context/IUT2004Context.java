package cz.cuni.amis.pogamut.sposh.context;

import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.sposh.context.IContext;

public interface IUT2004Context<BOT extends UT2004Bot> extends IContext<BOT> {

	public void finishInitialization();
	
}
