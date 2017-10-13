package cz.cuni.amis.pogamut.ut2004.bot.impl.test;

import cz.cuni.amis.utils.test.Concurrent;
import cz.cuni.amis.utils.test.ContextRunnable;

public class ConcurrentBot extends Concurrent<BotTestContext> {

	public ConcurrentBot(int threads, ContextRunnable<? extends BotContext> tester) {
		super(threads, tester);
	}
	
	@Override
	protected Runnable newTest(BotTestContext ctx) {
		final BotContext botCtx = ctx.newBotContext();
		return new Runnable() {

			@Override
			public void run() {
				tester.run(botCtx);
			}
			
		};
	}
	
}
