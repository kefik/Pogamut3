package cz.cuni.amis.pogamut.ut2004.bot.impl.test;

import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.utils.NullCheck;
import cz.cuni.amis.utils.test.TestContext;

public class BotContext extends TestContext {

	private final UT2004Bot bot;
	
	public BotContext(UT2004Bot bot) {
		super(bot.getLogger().getCategory("Test"));
		this.bot = bot;
		NullCheck.check(this.bot, "bot");
	}

	public UT2004Bot getBot() {
		return bot;
	}	
	
}
