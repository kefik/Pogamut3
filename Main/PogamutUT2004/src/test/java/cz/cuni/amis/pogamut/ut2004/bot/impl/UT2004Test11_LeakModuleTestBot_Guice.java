package cz.cuni.amis.pogamut.ut2004.bot.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Test;

import cz.cuni.amis.pogamut.base.communication.connection.impl.socket.SocketConnectionAddress;
import cz.cuni.amis.pogamut.base.factory.IAgentFactory;
import cz.cuni.amis.pogamut.ut2004.bot.impl.test.BotTestContext;
import cz.cuni.amis.pogamut.ut2004.bot.impl.test.ConcurrentBot;
import cz.cuni.amis.pogamut.ut2004.bot.impl.test.LeakModuleTestBot;
import cz.cuni.amis.pogamut.ut2004.bot.impl.test.LeakModuleTestBotController;
import cz.cuni.amis.pogamut.ut2004.bot.impl.test.LeakModuleTestBotModule;
import cz.cuni.amis.pogamut.ut2004.bot.impl.test.PauseResumeBotTest;
import cz.cuni.amis.pogamut.ut2004.factory.guice.remoteagent.UT2004BotFactory;
import cz.cuni.amis.pogamut.ut2004.test.UT2004Test;
import cz.cuni.amis.utils.exception.PogamutInterruptedException;
import cz.cuni.amis.utils.test.Repeater;

public class UT2004Test11_LeakModuleTestBot_Guice extends UT2004Test {
	
	private static class MasterTest implements Runnable {

		private Logger log;
		private SocketConnectionAddress address;

		public MasterTest(Logger log, SocketConnectionAddress address) {
			this.log = log;
			this.address = address;
		}
		
		@Override
		public void run() {
			IAgentFactory factory = new UT2004BotFactory(new LeakModuleTestBotModule(LeakModuleTestBotController.class));
	    	BotTestContext testCtx = new BotTestContext(log, factory, address);
	    	
	    	//bot.stop()
	    	new Repeater<BotTestContext>(10, new ConcurrentBot(3, new PauseResumeBotTest(0))).run(testCtx);
	    	
	    	//bot.kill()
	    	new Repeater<BotTestContext>(10, new ConcurrentBot(3, new PauseResumeBotTest(1))).run(testCtx);
	    	
	    	// bot.getAct().act(new DisconnectBot())
	    	new Repeater<BotTestContext>(10, new ConcurrentBot(3, new PauseResumeBotTest(2))).run(testCtx);
	    	
	    	factory = null;
	    	testCtx = null;
		}
		
	}
	
//	/**
//     * Initialize UCC server.
//     * @throws UCCStartException
//     */
//    @Before
//    public void beforeTest() throws UCCStartException {
//    	startUCC(new UCCWrapperConf().setMapName("CTF-Face3"));    	
//    }
	
	@Test
    public void test() {
		SocketConnectionAddress botAddress;
		if (ucc == null) {
			botAddress = new SocketConnectionAddress("localhost", 3000);
		} else {
			botAddress = ucc.getBotAddress();
		}
		Thread t = new Thread(new MasterTest(log, botAddress), "LeakModuleTestBot");
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			throw new PogamutInterruptedException(e, this);
		}
		
		t = null;
		
		Integer ctrlValue = null;
		Integer botValue = null;
    	for (int i = 0; i < 120; ++i) {
    		System.gc();

    		ctrlValue = LeakModuleTestBotController.getInstances().waitFor((long)1000, 0);
    		botValue = LeakModuleTestBot.getInstances().waitFor((long)1000, 0);
    		if (botValue != null && botValue == 0) {
    			System.out.println("All instances of LeakModuleTestBot has been gc()ed.");    			
    		} else {
    			if (log.isLoggable(Level.INFO)) log.info("/" + (i+1) + " sec" + (i != 0 ? "s" : "") + "/ LeakModuleTestBot.instances = " + LeakModuleTestBot.getInstances().getFlag());
    		}
    		if (ctrlValue != null && ctrlValue == 0) {
    			System.out.println("All instances of LeakModuleTestBotController has been gc()ed.");    			
    		} else {
    			if (log.isLoggable(Level.INFO)) log.info("/" + (i+1) + " sec" + (i != 0 ? "s" : "") + "/ LeakModuleTestBotController.instances = " + LeakModuleTestBotController.getInstances().getFlag());
    		}
    		if (botValue != null && botValue == 0 && ctrlValue != null && ctrlValue == 0) {
    			break;
    		}
    	}
    	
    	if (botValue != null && botValue == 0 && ctrlValue != null && ctrlValue == 0) {
			System.out.println("---/// TEST OK ///---");
			return;
		}
		
		if (botValue == null || botValue > 0) {
			System.out.println("NOT ALL LeakModuleTestBot INSTANCES WERE gc()ed IN 120 SECS!");
		}
		if (ctrlValue == null || ctrlValue > 0) {
			System.out.println("NOT ALL LeakModuleTestBotController INSTANCES WERE gc()ed IN 120 SECS!");
		}
		
    	
    	String str = "Some instances were not gc()ed!";
    	if (log.isLoggable(Level.SEVERE)) log.severe(str);
    	Assert.fail(str);
    }
	
}
