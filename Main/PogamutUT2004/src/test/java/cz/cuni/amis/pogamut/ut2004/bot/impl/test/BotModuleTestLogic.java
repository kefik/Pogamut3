package cz.cuni.amis.pogamut.ut2004.bot.impl.test;

import java.util.logging.Level;

import cz.cuni.amis.pogamut.base.agent.module.LogicModule;
import cz.cuni.amis.pogamut.base.component.bus.event.BusAwareCountDownLatch;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Self;

public class BotModuleTestLogic extends UT2004BotModuleController<UT2004Bot> {
	
	private BusAwareCountDownLatch latch;
	
	public BusAwareCountDownLatch getLatch() {
		return latch;
	}
	
	@Override
	public void logicInitialize(LogicModule logicModule) {
		super.logicInitialize(logicModule);
		this.latch = new BusAwareCountDownLatch(1, bot.getEventBus(), bot.getWorldView());
	}

	@Override
	public void logic() {
		if (log.isLoggable(Level.WARNING)) log.warning("Logic!");
		latch.countDown();
		if (bot.getWorldView().getSingle(Self.class) == null) {
			throw new RuntimeException("Self not present in the logic!");
		} else {
			if (log.isLoggable(Level.WARNING)) log.warning("Self present.");
		}
	}
	
	@Override
	public void logicShutdown() {
		super.logicShutdown();
		latch = null;
	}

}
