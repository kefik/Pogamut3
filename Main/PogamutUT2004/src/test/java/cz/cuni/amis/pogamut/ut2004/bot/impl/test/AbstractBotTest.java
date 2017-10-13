package cz.cuni.amis.pogamut.ut2004.bot.impl.test;

import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.agent.state.level1.IAgentStateDown;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.utils.StopWatch;
import cz.cuni.amis.utils.test.ContextRunnable;
import java.util.logging.Level;

public abstract class AbstractBotTest implements ContextRunnable<BotContext> {

	private long stopTimeout;
	
	public AbstractBotTest() {
		this.stopTimeout = 5000;
	}
	
	public AbstractBotTest(long stopTimeoutMillis) {
		this.stopTimeout = stopTimeoutMillis;
	}
	
	protected abstract void doTest(UT2004Bot bot, Logger test);
	
	@Override
	public void run(BotContext ctx) {
		UT2004Bot bot = ctx.getBot();
		Logger log = ctx.getLog();
		Throwable ex = null;
		try {
			StopWatch watch = new StopWatch();
			if (log.isLoggable(Level.INFO)) log.info("Starting test.");
			doTest(bot, ctx.getLog());
			log.log(log.getLevel(), "Test finished in " + watch.stopStr() + ".");
		} catch (Exception e) {
			ex = e;
		} finally {
			try {
				bot.awaitState(IAgentStateDown.class, stopTimeout);
			} finally {
				if (bot.notInState(IAgentStateDown.class)) {
					bot.kill();
					if (ex != null) {
						throw new RuntimeException("Bot did not stopped in " + stopTimeout + " ms.", ex);
					} else {
						throw new RuntimeException("Bot did not stopped in " + stopTimeout + " ms.");
					}
				}
			}
		}
	}



}
