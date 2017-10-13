package cz.cuni.amis.pogamut.ut2004.bot.impl.test;

import cz.cuni.amis.pogamut.base.agent.module.LogicModule;
import cz.cuni.amis.pogamut.base.component.bus.event.BusAwareCountDownLatch;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotLogicController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Self;
import java.util.logging.Level;

public class BotTestLogic extends UT2004BotLogicController<UT2004Bot> {
	
	private BusAwareCountDownLatch testLogicLatch;
	
	public BusAwareCountDownLatch getLatch() {
		return this.testLogicLatch;
	}
	
	@Override
	public void logicInitialize(LogicModule logicModule) {
		super.logicInitialize(logicModule);
		this.testLogicLatch = new BusAwareCountDownLatch(1, bot.getEventBus(), bot.getWorldView());
	}

	@Override
	public void logic() {
		if (log.isLoggable(Level.WARNING)) log.warning("Logic!");
		testLogicLatch.countDown();
		if (bot.getWorldView().getSingle(Self.class) == null) {
			throw new RuntimeException("Self not present in the logic!");
		} else {
			if (log.isLoggable(Level.WARNING)) log.warning("Self present.");
		}
	}
	
	@Override
	public void logicShutdown() {
		super.logicShutdown();
		testLogicLatch = null;
	}

}
