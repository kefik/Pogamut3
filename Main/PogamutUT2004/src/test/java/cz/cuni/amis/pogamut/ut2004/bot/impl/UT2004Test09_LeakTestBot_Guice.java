package cz.cuni.amis.pogamut.ut2004.bot.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Test;

import cz.cuni.amis.pogamut.base.communication.connection.impl.socket.SocketConnectionAddress;
import cz.cuni.amis.pogamut.base.factory.IAgentFactory;
import cz.cuni.amis.pogamut.ut2004.bot.impl.test.BotTestContext;
import cz.cuni.amis.pogamut.ut2004.bot.impl.test.ConcurrentBot;
import cz.cuni.amis.pogamut.ut2004.bot.impl.test.LeakTestBot;
import cz.cuni.amis.pogamut.ut2004.bot.impl.test.LeakTestBotModule;
import cz.cuni.amis.pogamut.ut2004.bot.impl.test.PauseResumeBotTest;
import cz.cuni.amis.pogamut.ut2004.factory.guice.remoteagent.UT2004BotFactory;
import cz.cuni.amis.pogamut.ut2004.test.UT2004Test;
import cz.cuni.amis.utils.exception.PogamutInterruptedException;
import cz.cuni.amis.utils.test.Repeater;

public class UT2004Test09_LeakTestBot_Guice extends UT2004Test {
	
	private static class MasterTest implements Runnable {

		private Logger log;
		private SocketConnectionAddress address;

		public MasterTest(Logger log, SocketConnectionAddress address) {
			this.log = log;
			this.address = address;
		}
		
		@Override
		public void run() {
			IAgentFactory factory = new UT2004BotFactory(new LeakTestBotModule(UT2004BotLogicController.class));
	    	BotTestContext testCtx = new BotTestContext(log, factory, address);
	    	new Repeater<BotTestContext>(1, new ConcurrentBot(4, new PauseResumeBotTest(0))).run(testCtx);
	    	
	    	factory = null;
	    	testCtx = null;
		}
		
	}
	
	
	@Test
    public void test() {
		Thread t = new Thread(new MasterTest(log, ucc.getBotAddress()), "LeakTestBot_Guice");
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			throw new PogamutInterruptedException(e, this);
		}
		
		t = null;
		
    	for (int i = 0; i < 120; ++i) {
    		System.gc();
    		Integer value = LeakTestBot.getInstances().waitFor((long)1000, 0);
    		if (value != null && value == 0) {
    			System.out.println("All instances of LeakTestBot has been gc()ed.");
    			System.out.println("---/// TEST OK ///---");
    			return;
    		} else {
    			if (log.isLoggable(Level.INFO)) log.info("/" + (i+1) + " sec" + (i != 0 ? "s" : "") + "/ LeakTestBot.instances = " + LeakTestBot.getInstances().getFlag());
    		}
    	}
    	
    	String str = "Not all LeakTestBot instances were gc()ed in 120secs after the test ended, LeakTestBot.instances = " + LeakTestBot.getInstances().getFlag() + ".";
    	if (log.isLoggable(Level.SEVERE)) log.severe(str);
    	Assert.fail(str);
    }
	
}
