package cz.cuni.amis.pogamut.ut2004.tag.bot;

import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;

public class UT2004BotTagController<BOT extends UT2004Bot> extends UT2004BotModuleController<BOT> {

	protected BotTagModule tag;
	
	protected void initializeModules(BOT bot) {
		super.initializeModules(bot);
		tag = new BotTagModule(bot, info, players);
	}

	public BotTagModule getTag() {
		return tag;
	};
	
}
